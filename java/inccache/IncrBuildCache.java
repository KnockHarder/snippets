package com.knockharder;

import java.util.Collection;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.annotation.Nullable;

/**
 * @param <T> 源数据的类型
 * @param <D> 缓存数据类型
 * @param <R> 目标数据类型
 */
public class IncrBuildCache<T, D, R> {
    private final Set<T> builtItems;
    private Set<T> toBuildItems;
    private final Function<Collection<T>, D> dataBuildFunc;
    private final MergeFunction<D> dataMergeFunc;
    private final BiFunction<T, D, R> valueExtractor;
    private D dataCache;

    IncrBuildCache(Function<Collection<T>, D> dataBuildFunc, MergeFunction<D> dataMergeFunc,
            BiFunction<T, D, R> valueExtractor) {
        this.builtItems = new HashSet<>();
        this.toBuildItems = new HashSet<>();
        this.dataBuildFunc = dataBuildFunc;
        this.dataMergeFunc = dataMergeFunc;
        this.valueExtractor = valueExtractor;
    }

    public void appendToBuildItems(Collection<T> toBuildItems) {
        this.toBuildItems.addAll(toBuildItems);
    }

    @Nullable
    public R getValue(T item) {
        if (!builtItems.contains(item) && toBuildItems.contains(item)) {
            incrementalBuild();
        }
        if (dataCache == null) {
            return null;
        }
        return valueExtractor.apply(item, dataCache);
    }

    public R getValueOrDefault(T item, R defaultValue) {
        return Optional.ofNullable(getValue(item))
                .orElse(defaultValue);
    }

    private void incrementalBuild() {
        D incrementalCache = dataBuildFunc.apply(toBuildItems);
        builtItems.addAll(toBuildItems);
        toBuildItems.clear();
        if (incrementalCache == null) {
            return;
        }
        if (dataCache == null) {
            dataCache = incrementalCache;
        } else {
            dataCache = dataMergeFunc.merge(dataCache, incrementalCache);
        }
    }

    public void resetToBuildItems(Collection<T> toBuildItems) {
        this.toBuildItems = toBuildItems.stream()
                .filter(x -> !builtItems.contains(x))
                .collect(Collectors.toSet());
    }

    public interface MergeFunction<T> {
        T merge(T one, T another);
    }
}

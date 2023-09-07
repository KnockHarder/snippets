package com.knockharder;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.annotation.Nonnull;

import com.sun.org.slf4j.internal.Logger;
import com.sun.org.slf4j.internal.LoggerFactory;

public class IncrBuildCacheFactory {
    private static final Logger log = LoggerFactory.getLogger(IncrBuildCacheFactory.class);

    public static <T, I> IncrBuildCache<T, Set<I>, Boolean> idInSetSafeBuildCache(
            Function<T, I> idExtractor, Function<Collection<I>, Set<I>> idBuildFunc) {
        return new IncrBuildCache<>(toDataBuildFunc(safeSetFunc(idBuildFunc), idExtractor),
                IncrBuildCacheFactory::mergeNonnullSets,
                (item, cache) -> cache.contains(idExtractor.apply(item)));
    }

    private static <T, R> Function<Collection<T>, Set<R>> safeSetFunc(Function<Collection<T>, Set<R>> function) {
        return safeFuncWithDefault(function, Collections.emptySet());
    }

    private static <T, R> Function<T, R> safeFuncWithDefault(Function<T, R> function, R defaultValue) {
        return x -> {
            R result = null;
            try {
                result = function.apply(x);
            } catch (Exception e) {
                log.error("[fail safe]", e);
            }
            return Optional.ofNullable(result).orElse(defaultValue);
        };
    }

    @Nonnull
    private static <T, I, D> Function<Collection<T>, D> toDataBuildFunc(
            Function<Collection<I>, D> idBuildFunc, Function<T, I> idExtractor) {
        return l -> {
            List<I> idList = l.stream()
                    .map(idExtractor)
                    .collect(Collectors.toList());
            return idBuildFunc.apply(idList);
        };
    }

    public static <T, I, R> IncrBuildCache<T, Map<I, R>, R> idMapSafeBuildCache(
            Function<T, I> idExtractor, Function<Collection<I>, Map<I, R>> idBuildFunc) {
        return new IncrBuildCache<>(toDataBuildFunc(safeMapFunc(idBuildFunc), idExtractor),
                IncrBuildCacheFactory::mergeNonnullMaps,
                (item, cacheData) -> cacheData.get(idExtractor.apply(item)));
    }

    public static <T, K, R> IncrBuildCache<T, Map<K, R>, R> selfMapSafeBuildCache(
            Function<Collection<T>, Map<K, R>> dataBuildFunc) {
        return new IncrBuildCache<>(safeMapFunc(dataBuildFunc),
                IncrBuildCacheFactory::mergeNonnullMaps, (T item, Map<K, R> cache) -> cache.get(item));
    }

    private static <T, K, R> Function<Collection<T>, Map<K, R>> safeMapFunc(
            Function<Collection<T>, Map<K, R>> function) {
        return safeFuncWithDefault(function, Collections.emptyMap());
    }

    private static <T> Set<T> mergeNonnullSets(Set<T> one, Set<T> another) {
        HashSet<T> result = new HashSet<>();
        result.addAll(one);
        result.addAll(another);
        return result;
    }

    private static <K, V> Map<K, V> mergeNonnullMaps(Map<K, V> one, Map<K, V> another) {
        HashMap<K, V> result = new HashMap<>();
        result.putAll(one);
        result.putAll(another);
        return result;
    }
}

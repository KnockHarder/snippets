package com.knockharder;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public class IncrBuildCacheMap<T> {

    private final HashMap<CacheKey<T, ?, ?>, IncrBuildCache<T, ?, ?>> cacheMap;

    public IncrBuildCacheMap() {
        cacheMap = new HashMap<>();
    }

    public <D, R> void addCache(CacheKey<T, D, R> key, IncrBuildCache<T, D, R> cache) {
        cacheMap.put(key, cache);
    }

    @SuppressWarnings("unchecked")
    public <D, R> IncrBuildCache<T, D, R> getCache(CacheKey<T, D, R> key) {
        return ((IncrBuildCache<T, D, R>) cacheMap.get(key));
    }

    public <D, R> R getCacheValue(CacheKey<T, D, R> key, T item) {
        return getCacheValueOptional(key, item).orElse(null);
    }

    public <D, R> Optional<R> getCacheValueOptional(CacheKey<T, D, R> key, T item) {
        return Optional.ofNullable(getCache(key))
                .map(x -> x.getValue(item));
    }

    public <D, R> R getCacheValueOrDefault(CacheKey<T, D, R> key, T item, R defaultValue) {
        return getCacheValueOptional(key, item).orElse(defaultValue);
    }

    public void appendToBuildItems(Collection<T> toBuildItems) {
        cacheMap.values().forEach(x -> x.appendToBuildItems(toBuildItems));
    }

    public void resetToBuildItems(Collection<T> toBuildItems) {
        cacheMap.values().forEach(x -> x.resetToBuildItems(toBuildItems));
    }

    public interface CacheKey<T, D, R> {

    }

    public static class IdInSetCacheKey<T, I> implements CacheKey<T, Set<I>, Boolean> {

    }

    public static class IdMapCacheKey<T, I, R> implements CacheKey<T, Map<I, R>, R> {

    }

    public static class SelfMapCacheKey<T, R> implements CacheKey<T, Map<T, R>, R> {

    }
}

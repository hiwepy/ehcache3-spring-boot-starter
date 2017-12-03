package org.springframework.cache.ehcache3;

import java.util.Collection;

import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;

public class EhCacheCacheManager implements CacheManager {

	protected org.ehcache.CacheManager cacheManager;

	public EhCacheCacheManager(org.ehcache.CacheManager cacheManager) {
		this.cacheManager = cacheManager;
	}

	@Override
	public Cache getCache(String name) {
		return cacheManager.getCache(name);
	}

	@Override
	public Collection<String> getCacheNames() {
		return cacheManager.getCacheNames();
	}

}

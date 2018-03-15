package org.springframework.cache.ehcache3;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Set;

import org.ehcache.Status;
import org.ehcache.UserManagedCache;
import org.springframework.cache.Cache;
import org.springframework.cache.transaction.AbstractTransactionSupportingCacheManager;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

public class EhCache3CacheManager extends AbstractTransactionSupportingCacheManager {

	@Nullable
	private org.ehcache.CacheManager cacheManager;
	
	/**
	 * Create a new EhCacheCacheManager, setting the target EhCache CacheManager
	 * through the {@link #setCacheManager} bean property.
	 */
	public EhCache3CacheManager() {
	}

	/**
	 * Create a new EhCache3CacheManager for the given backing EhCache CacheManager.
	 * @param cacheManager the backing EhCache {@link org.ehcache.CacheManager}
	 */
	public EhCache3CacheManager(org.ehcache.CacheManager cacheManager) {
		this.cacheManager = cacheManager;
	}


	/**
	 * Set the backing EhCache {@link org.ehcache.CacheManager}.
	 */
	public void setCacheManager(@Nullable org.ehcache.CacheManager cacheManager) {
		this.cacheManager = cacheManager;
	}

	/**
	 * Return the backing EhCache {@link org.ehcache.CacheManager}.
	 */
	@Nullable
	public org.ehcache.CacheManager getCacheManager() {
		return this.cacheManager;
	}

	@Override
	public void afterPropertiesSet() {
		if (getCacheManager() == null) {
			setCacheManager(EhCache3ManagerUtils.buildCacheManager());
		}
	}

	@Override
	protected Collection<Cache> loadCaches() {
		org.ehcache.CacheManager cacheManager = getCacheManager();
		Assert.state(cacheManager != null, "No CacheManager set");

		Status status = cacheManager.getStatus();
		if (!Status.AVAILABLE.equals(status)) {
			throw new IllegalStateException("An 'alive' EhCache CacheManager is required - current cache is " + status.toString());
		}

		Set<String> names = getCacheManager().getRuntimeConfiguration().getCacheConfigurations().keySet();
		Collection<Cache> caches = new LinkedHashSet<>(names.size());
		for (String name : names) {
			caches.add(new EhCache3Cache((UserManagedCache<String, Object>) getCacheManager().getCache(name, String.class, Object.class)));
		}
		return caches;
	}

	@Override
	protected Cache getMissingCache(String name) {
		org.ehcache.CacheManager cacheManager = getCacheManager();
		Assert.state(cacheManager != null, "No CacheManager set");
		
		// Check the EhCache cache again (in case the cache was added at runtime)
		org.ehcache.Cache<String, Object> ehcache = cacheManager.getCache(name, String.class, Object.class);
		if (ehcache != null) {
			return new EhCache3Cache((UserManagedCache<String, Object>) ehcache);
		}
		return null;
	}
	
	 

}

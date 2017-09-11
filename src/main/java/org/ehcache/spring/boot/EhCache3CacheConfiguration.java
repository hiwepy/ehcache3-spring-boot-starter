package org.ehcache.spring.boot;


import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ResourceCondition;
import org.springframework.cache.ehcache.EhCacheCacheManager;
import org.springframework.cache.ehcache.EhCacheManagerUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;

/**
 * EhCache cache configuration. Only kick in if a configuration file location is set or if
 * a default configuration file exists.
 *
 * @author Eddú Meléndez
 * @author Stephane Nicoll
 * @since 1.3.0
 */
@Configuration
@ConditionalOnClass({ Cache.class, EhCacheCacheManager.class })
@ConditionalOnMissingBean(org.springframework.cache.CacheManager.class)
@Conditional({ CacheCondition.class,
		EhCacheCacheConfiguration.ConfigAvailableCondition.class })
public class EhCache3CacheConfiguration {

	private final CacheProperties cacheProperties;

	private final CacheManagerCustomizers customizers;

	EhCacheCacheConfiguration(CacheProperties cacheProperties,
			CacheManagerCustomizers customizers) {
		this.cacheProperties = cacheProperties;
		this.customizers = customizers;
	}

	@Bean
	public EhCacheCacheManager cacheManager(CacheManager ehCacheCacheManager) {
		return this.customizers.customize(new EhCacheCacheManager(ehCacheCacheManager));
	}

	@Bean
	@ConditionalOnMissingBean
	public CacheManager ehCacheCacheManager() {
		Resource location = this.cacheProperties
				.resolveConfigLocation(this.cacheProperties.getEhcache().getConfig());
		if (location != null) {
			return EhCacheManagerUtils.buildCacheManager(location);
		}
		return EhCacheManagerUtils.buildCacheManager();
	}

	/**
	 * Determine if the EhCache configuration is available. This either kick in if a
	 * default configuration has been found or if property referring to the file to use
	 * has been set.
	 */
	static class ConfigAvailableCondition extends ResourceCondition {

		ConfigAvailableCondition() {
			super("EhCache", "spring.cache.ehcache", "config", "classpath:/ehcache.xml");
		}

	}

}

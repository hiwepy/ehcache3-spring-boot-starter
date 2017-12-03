package org.ehcache.spring.boot;

import org.springframework.boot.autoconfigure.condition.ConditionMessage;
import org.springframework.boot.autoconfigure.condition.ConditionOutcome;
import org.springframework.boot.autoconfigure.condition.SpringBootCondition;
import org.springframework.boot.bind.RelaxedPropertyResolver;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.core.type.ClassMetadata;

/**
 * General cache condition used with all cache configuration classes.
 *
 * @author Stephane Nicoll
 * @author Phillip Webb
 * @since 1.3.0
 */
public class CacheCondition extends SpringBootCondition {

	@Override
	public ConditionOutcome getMatchOutcome(ConditionContext context,
			AnnotatedTypeMetadata metadata) {
		String sourceClass = "";
		if (metadata instanceof ClassMetadata) {
			sourceClass = ((ClassMetadata) metadata).getClassName();
		}
		ConditionMessage.Builder message = ConditionMessage.forCondition("Cache",
				sourceClass);
		RelaxedPropertyResolver resolver = new RelaxedPropertyResolver(
				context.getEnvironment(), "spring.cache.");
		if (!resolver.containsProperty("type")) {
			return ConditionOutcome.match(message.because("automatic cache type"));
		}
		CacheType cacheType = CacheConfigurations
				.getType(((AnnotationMetadata) metadata).getClassName());
		String value = resolver.getProperty("type").replace('-', '_').toUpperCase();
		if (value.equals(cacheType.name())) {
			return ConditionOutcome.match(message.because(value + " cache type"));
		}
		return ConditionOutcome.noMatch(message.because(value + " cache type"));
	}

}


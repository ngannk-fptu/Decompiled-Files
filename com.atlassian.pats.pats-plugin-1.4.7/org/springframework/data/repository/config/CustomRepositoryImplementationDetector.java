/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.beans.factory.config.BeanDefinition
 *  org.springframework.beans.factory.support.AbstractBeanDefinition
 *  org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider
 *  org.springframework.core.env.Environment
 *  org.springframework.core.io.ResourceLoader
 *  org.springframework.util.Assert
 */
package org.springframework.data.repository.config;

import java.util.Collection;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ResourceLoader;
import org.springframework.data.repository.config.ImplementationDetectionConfiguration;
import org.springframework.data.repository.config.ImplementationLookupConfiguration;
import org.springframework.data.repository.config.SelectionSet;
import org.springframework.data.util.Lazy;
import org.springframework.data.util.StreamUtils;
import org.springframework.util.Assert;

public class CustomRepositoryImplementationDetector {
    private static final String CUSTOM_IMPLEMENTATION_RESOURCE_PATTERN = "**/*%s.class";
    private static final String AMBIGUOUS_CUSTOM_IMPLEMENTATIONS = "Ambiguous custom implementations detected! Found %s but expected a single implementation!";
    private final Environment environment;
    private final ResourceLoader resourceLoader;
    private final Lazy<Set<BeanDefinition>> implementationCandidates;

    public CustomRepositoryImplementationDetector(Environment environment, ResourceLoader resourceLoader, ImplementationDetectionConfiguration configuration) {
        Assert.notNull((Object)environment, (String)"Environment must not be null!");
        Assert.notNull((Object)resourceLoader, (String)"ResourceLoader must not be null!");
        Assert.notNull((Object)configuration, (String)"ImplementationDetectionConfiguration must not be null!");
        this.environment = environment;
        this.resourceLoader = resourceLoader;
        this.implementationCandidates = Lazy.of(() -> this.findCandidateBeanDefinitions(configuration));
    }

    public CustomRepositoryImplementationDetector(Environment environment, ResourceLoader resourceLoader) {
        Assert.notNull((Object)environment, (String)"Environment must not be null!");
        Assert.notNull((Object)resourceLoader, (String)"ResourceLoader must not be null!");
        this.environment = environment;
        this.resourceLoader = resourceLoader;
        this.implementationCandidates = Lazy.empty();
    }

    public Optional<AbstractBeanDefinition> detectCustomImplementation(ImplementationLookupConfiguration lookup) {
        Assert.notNull((Object)lookup, (String)"ImplementationLookupConfiguration must not be null!");
        Set definitions = this.implementationCandidates.getOptional().orElseGet(() -> this.findCandidateBeanDefinitions(lookup)).stream().filter(lookup::matches).collect(StreamUtils.toUnmodifiableSet());
        return SelectionSet.of(definitions, c -> c.isEmpty() ? Optional.empty() : CustomRepositoryImplementationDetector.throwAmbiguousCustomImplementationException(c)).filterIfNecessary(lookup::hasMatchingBeanName).uniqueResult().map(AbstractBeanDefinition.class::cast);
    }

    private Set<BeanDefinition> findCandidateBeanDefinitions(ImplementationDetectionConfiguration config) {
        String postfix = config.getImplementationPostfix();
        ClassPathScanningCandidateComponentProvider provider = new ClassPathScanningCandidateComponentProvider(false, this.environment);
        provider.setResourceLoader(this.resourceLoader);
        provider.setResourcePattern(String.format(CUSTOM_IMPLEMENTATION_RESOURCE_PATTERN, postfix));
        provider.setMetadataReaderFactory(config.getMetadataReaderFactory());
        provider.addIncludeFilter((reader, factory) -> true);
        config.getExcludeFilters().forEach(it -> provider.addExcludeFilter(it));
        return config.getBasePackages().stream().flatMap(it -> provider.findCandidateComponents(it).stream()).collect(Collectors.toSet());
    }

    private static Optional<BeanDefinition> throwAmbiguousCustomImplementationException(Collection<BeanDefinition> definitions) {
        String implementationNames = definitions.stream().map(BeanDefinition::getBeanClassName).collect(Collectors.joining(", "));
        throw new IllegalStateException(String.format(AMBIGUOUS_CUSTOM_IMPLEMENTATIONS, implementationNames));
    }
}


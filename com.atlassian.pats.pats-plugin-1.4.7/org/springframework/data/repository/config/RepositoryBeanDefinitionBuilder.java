/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 *  org.springframework.beans.factory.config.BeanDefinition
 *  org.springframework.beans.factory.support.AbstractBeanDefinition
 *  org.springframework.beans.factory.support.BeanDefinitionBuilder
 *  org.springframework.beans.factory.support.BeanDefinitionRegistry
 *  org.springframework.core.env.Environment
 *  org.springframework.core.io.ResourceLoader
 *  org.springframework.core.type.classreading.CachingMetadataReaderFactory
 *  org.springframework.core.type.classreading.MetadataReaderFactory
 *  org.springframework.util.Assert
 */
package org.springframework.data.repository.config;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.type.classreading.CachingMetadataReaderFactory;
import org.springframework.core.type.classreading.MetadataReaderFactory;
import org.springframework.data.config.ParsingUtils;
import org.springframework.data.repository.config.CustomRepositoryImplementationDetector;
import org.springframework.data.repository.config.FragmentMetadata;
import org.springframework.data.repository.config.ImplementationDetectionConfiguration;
import org.springframework.data.repository.config.ImplementationLookupConfiguration;
import org.springframework.data.repository.config.NamedQueriesBeanDefinitionBuilder;
import org.springframework.data.repository.config.RepositoryConfiguration;
import org.springframework.data.repository.config.RepositoryConfigurationExtension;
import org.springframework.data.repository.config.RepositoryConfigurationSource;
import org.springframework.data.repository.config.RepositoryFragmentConfiguration;
import org.springframework.data.repository.core.support.RepositoryFragment;
import org.springframework.data.repository.core.support.RepositoryFragmentsFactoryBean;
import org.springframework.data.util.Optionals;
import org.springframework.util.Assert;

class RepositoryBeanDefinitionBuilder {
    private static final Log logger = LogFactory.getLog(RepositoryBeanDefinitionBuilder.class);
    private final BeanDefinitionRegistry registry;
    private final RepositoryConfigurationExtension extension;
    private final ResourceLoader resourceLoader;
    private final MetadataReaderFactory metadataReaderFactory;
    private final FragmentMetadata fragmentMetadata;
    private final CustomRepositoryImplementationDetector implementationDetector;

    public RepositoryBeanDefinitionBuilder(BeanDefinitionRegistry registry, RepositoryConfigurationExtension extension, RepositoryConfigurationSource configurationSource, ResourceLoader resourceLoader, Environment environment) {
        Assert.notNull((Object)extension, (String)"RepositoryConfigurationExtension must not be null!");
        Assert.notNull((Object)resourceLoader, (String)"ResourceLoader must not be null!");
        Assert.notNull((Object)environment, (String)"Environment must not be null!");
        this.registry = registry;
        this.extension = extension;
        this.resourceLoader = resourceLoader;
        this.metadataReaderFactory = new CachingMetadataReaderFactory(resourceLoader);
        this.fragmentMetadata = new FragmentMetadata(this.metadataReaderFactory);
        this.implementationDetector = new CustomRepositoryImplementationDetector(environment, resourceLoader, configurationSource.toImplementationDetectionConfiguration(this.metadataReaderFactory));
    }

    public BeanDefinitionBuilder build(RepositoryConfiguration<?> configuration) {
        Assert.notNull((Object)this.registry, (String)"BeanDefinitionRegistry must not be null!");
        Assert.notNull((Object)this.resourceLoader, (String)"ResourceLoader must not be null!");
        BeanDefinitionBuilder builder = BeanDefinitionBuilder.rootBeanDefinition((String)configuration.getRepositoryFactoryBeanClassName());
        builder.getRawBeanDefinition().setSource(configuration.getSource());
        builder.addConstructorArgValue((Object)configuration.getRepositoryInterface());
        builder.addPropertyValue("queryLookupStrategyKey", configuration.getQueryLookupStrategyKey());
        builder.addPropertyValue("lazyInit", (Object)configuration.isLazyInit());
        builder.setLazyInit(configuration.isLazyInit());
        builder.setPrimary(configuration.isPrimary());
        configuration.getRepositoryBaseClassName().ifPresent(it -> builder.addPropertyValue("repositoryBaseClass", it));
        NamedQueriesBeanDefinitionBuilder definitionBuilder = new NamedQueriesBeanDefinitionBuilder(this.extension.getDefaultNamedQueryLocation());
        configuration.getNamedQueriesLocation().ifPresent(definitionBuilder::setLocations);
        builder.addPropertyValue("namedQueries", (Object)definitionBuilder.build(configuration.getSource()));
        this.registerCustomImplementation(configuration).ifPresent(it -> {
            builder.addPropertyReference("customImplementation", it);
            builder.addDependsOn(it);
        });
        BeanDefinitionBuilder fragmentsBuilder = BeanDefinitionBuilder.rootBeanDefinition(RepositoryFragmentsFactoryBean.class);
        List fragmentBeanNames = this.registerRepositoryFragmentsImplementation(configuration).map(RepositoryFragmentConfiguration::getFragmentBeanName).collect(Collectors.toList());
        fragmentsBuilder.addConstructorArgValue(fragmentBeanNames);
        builder.addPropertyValue("repositoryFragments", (Object)ParsingUtils.getSourceBeanDefinition(fragmentsBuilder, configuration.getSource()));
        return builder;
    }

    private Optional<String> registerCustomImplementation(RepositoryConfiguration<?> configuration) {
        ImplementationLookupConfiguration lookup = configuration.toLookupConfiguration(this.metadataReaderFactory);
        String beanName = lookup.getImplementationBeanName();
        if (this.registry.containsBeanDefinition(beanName)) {
            return Optional.of(beanName);
        }
        Optional<AbstractBeanDefinition> beanDefinition = this.implementationDetector.detectCustomImplementation(lookup);
        return beanDefinition.map(it -> {
            if (logger.isDebugEnabled()) {
                logger.debug((Object)("Registering custom repository implementation: " + lookup.getImplementationBeanName() + " " + it.getBeanClassName()));
            }
            it.setSource(configuration.getSource());
            this.registry.registerBeanDefinition(beanName, (BeanDefinition)it);
            return beanName;
        });
    }

    private Stream<RepositoryFragmentConfiguration> registerRepositoryFragmentsImplementation(RepositoryConfiguration<?> configuration) {
        ImplementationDetectionConfiguration config = configuration.toImplementationDetectionConfiguration(this.metadataReaderFactory);
        return this.fragmentMetadata.getFragmentInterfaces(configuration.getRepositoryInterface()).map(it -> this.detectRepositoryFragmentConfiguration((String)it, config)).flatMap(xva$0 -> Optionals.toStream(xva$0)).peek(it -> this.potentiallyRegisterFragmentImplementation(configuration, (RepositoryFragmentConfiguration)it)).peek(it -> this.potentiallyRegisterRepositoryFragment(configuration, (RepositoryFragmentConfiguration)it));
    }

    private Optional<RepositoryFragmentConfiguration> detectRepositoryFragmentConfiguration(String fragmentInterface, ImplementationDetectionConfiguration config) {
        ImplementationLookupConfiguration lookup = config.forFragment(fragmentInterface);
        Optional<AbstractBeanDefinition> beanDefinition = this.implementationDetector.detectCustomImplementation(lookup);
        return beanDefinition.map(bd -> new RepositoryFragmentConfiguration(fragmentInterface, (AbstractBeanDefinition)bd));
    }

    private void potentiallyRegisterFragmentImplementation(RepositoryConfiguration<?> repositoryConfiguration, RepositoryFragmentConfiguration fragmentConfiguration) {
        String beanName = fragmentConfiguration.getImplementationBeanName();
        if (this.registry.containsBeanDefinition(beanName)) {
            return;
        }
        if (logger.isDebugEnabled()) {
            logger.debug((Object)String.format("Registering repository fragment implementation: %s %s", beanName, fragmentConfiguration.getClassName()));
        }
        fragmentConfiguration.getBeanDefinition().ifPresent(bd -> {
            bd.setSource(repositoryConfiguration.getSource());
            this.registry.registerBeanDefinition(beanName, (BeanDefinition)bd);
        });
    }

    private void potentiallyRegisterRepositoryFragment(RepositoryConfiguration<?> configuration, RepositoryFragmentConfiguration fragmentConfiguration) {
        String beanName = fragmentConfiguration.getFragmentBeanName();
        if (this.registry.containsBeanDefinition(beanName)) {
            return;
        }
        if (logger.isDebugEnabled()) {
            logger.debug((Object)("Registering repository fragment: " + beanName));
        }
        BeanDefinitionBuilder fragmentBuilder = BeanDefinitionBuilder.rootBeanDefinition(RepositoryFragment.class, (String)"implemented");
        fragmentBuilder.addConstructorArgValue((Object)fragmentConfiguration.getInterfaceName());
        fragmentBuilder.addConstructorArgReference(fragmentConfiguration.getImplementationBeanName());
        this.registry.registerBeanDefinition(beanName, (BeanDefinition)ParsingUtils.getSourceBeanDefinition(fragmentBuilder, configuration.getSource()));
    }
}


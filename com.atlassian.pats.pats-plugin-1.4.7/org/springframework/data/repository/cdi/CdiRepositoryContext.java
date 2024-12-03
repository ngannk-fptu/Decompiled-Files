/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.enterprise.inject.UnsatisfiedResolutionException
 *  org.springframework.beans.factory.support.AbstractBeanDefinition
 *  org.springframework.core.env.Environment
 *  org.springframework.core.env.StandardEnvironment
 *  org.springframework.core.io.ResourceLoader
 *  org.springframework.core.io.support.PathMatchingResourcePatternResolver
 *  org.springframework.core.type.classreading.CachingMetadataReaderFactory
 *  org.springframework.core.type.classreading.MetadataReaderFactory
 *  org.springframework.core.type.filter.TypeFilter
 *  org.springframework.lang.Nullable
 *  org.springframework.util.Assert
 *  org.springframework.util.ClassUtils
 */
package org.springframework.data.repository.cdi;

import java.util.Optional;
import java.util.stream.Stream;
import javax.enterprise.inject.UnsatisfiedResolutionException;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.core.env.Environment;
import org.springframework.core.env.StandardEnvironment;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.type.classreading.CachingMetadataReaderFactory;
import org.springframework.core.type.classreading.MetadataReaderFactory;
import org.springframework.core.type.filter.TypeFilter;
import org.springframework.data.repository.cdi.CdiRepositoryConfiguration;
import org.springframework.data.repository.config.CustomRepositoryImplementationDetector;
import org.springframework.data.repository.config.FragmentMetadata;
import org.springframework.data.repository.config.ImplementationDetectionConfiguration;
import org.springframework.data.repository.config.ImplementationLookupConfiguration;
import org.springframework.data.repository.config.RepositoryFragmentConfiguration;
import org.springframework.data.util.Optionals;
import org.springframework.data.util.Streamable;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;

public class CdiRepositoryContext {
    private final ClassLoader classLoader;
    private final CustomRepositoryImplementationDetector detector;
    private final MetadataReaderFactory metadataReaderFactory;
    private final FragmentMetadata metdata;

    public CdiRepositoryContext(ClassLoader classLoader) {
        this(classLoader, new CustomRepositoryImplementationDetector((Environment)new StandardEnvironment(), (ResourceLoader)new PathMatchingResourcePatternResolver(classLoader)));
    }

    public CdiRepositoryContext(ClassLoader classLoader, CustomRepositoryImplementationDetector detector) {
        Assert.notNull((Object)classLoader, (String)"ClassLoader must not be null!");
        Assert.notNull((Object)detector, (String)"CustomRepositoryImplementationDetector must not be null!");
        PathMatchingResourcePatternResolver resourceLoader = new PathMatchingResourcePatternResolver(classLoader);
        this.classLoader = classLoader;
        this.metadataReaderFactory = new CachingMetadataReaderFactory((ResourceLoader)resourceLoader);
        this.metdata = new FragmentMetadata(this.metadataReaderFactory);
        this.detector = detector;
    }

    CustomRepositoryImplementationDetector getCustomRepositoryImplementationDetector() {
        return this.detector;
    }

    Class<?> loadClass(String className) {
        try {
            return ClassUtils.forName((String)className, (ClassLoader)this.classLoader);
        }
        catch (ClassNotFoundException e) {
            throw new UnsatisfiedResolutionException(String.format("Unable to resolve class for '%s'", className), (Throwable)e);
        }
    }

    Stream<RepositoryFragmentConfiguration> getRepositoryFragments(CdiRepositoryConfiguration configuration, Class<?> repositoryInterface) {
        CdiImplementationDetectionConfiguration config = new CdiImplementationDetectionConfiguration(configuration, this.metadataReaderFactory);
        return this.metdata.getFragmentInterfaces(repositoryInterface.getName()).map(it -> this.detectRepositoryFragmentConfiguration((String)it, config)).flatMap(xva$0 -> Optionals.toStream(xva$0));
    }

    Optional<Class<?>> getCustomImplementationClass(Class<?> repositoryType, CdiRepositoryConfiguration cdiRepositoryConfiguration) {
        CdiImplementationDetectionConfiguration configuration = new CdiImplementationDetectionConfiguration(cdiRepositoryConfiguration, this.metadataReaderFactory);
        ImplementationLookupConfiguration lookup = configuration.forFragment(repositoryType.getName());
        Optional<AbstractBeanDefinition> beanDefinition = this.detector.detectCustomImplementation(lookup);
        return beanDefinition.map(this::loadBeanClass);
    }

    private Optional<RepositoryFragmentConfiguration> detectRepositoryFragmentConfiguration(String fragmentInterfaceName, CdiImplementationDetectionConfiguration config) {
        ImplementationLookupConfiguration lookup = config.forFragment(fragmentInterfaceName);
        Optional<AbstractBeanDefinition> beanDefinition = this.detector.detectCustomImplementation(lookup);
        return beanDefinition.map(bd -> new RepositoryFragmentConfiguration(fragmentInterfaceName, (AbstractBeanDefinition)bd));
    }

    @Nullable
    private Class<?> loadBeanClass(AbstractBeanDefinition definition) {
        String beanClassName = definition.getBeanClassName();
        return beanClassName == null ? null : this.loadClass(beanClassName);
    }

    private static class CdiImplementationDetectionConfiguration
    implements ImplementationDetectionConfiguration {
        private final CdiRepositoryConfiguration configuration;
        private final MetadataReaderFactory metadataReaderFactory;

        CdiImplementationDetectionConfiguration(CdiRepositoryConfiguration configuration, MetadataReaderFactory metadataReaderFactory) {
            this.configuration = configuration;
            this.metadataReaderFactory = metadataReaderFactory;
        }

        @Override
        public String getImplementationPostfix() {
            return this.configuration.getRepositoryImplementationPostfix();
        }

        @Override
        public Streamable<String> getBasePackages() {
            return Streamable.empty();
        }

        @Override
        public Streamable<TypeFilter> getExcludeFilters() {
            return Streamable.empty();
        }

        @Override
        public MetadataReaderFactory getMetadataReaderFactory() {
            return this.metadataReaderFactory;
        }
    }
}


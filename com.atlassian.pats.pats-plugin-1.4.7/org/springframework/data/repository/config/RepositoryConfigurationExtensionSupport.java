/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 *  org.springframework.beans.factory.config.BeanDefinition
 *  org.springframework.beans.factory.support.AbstractBeanDefinition
 *  org.springframework.beans.factory.support.BeanDefinitionBuilder
 *  org.springframework.beans.factory.support.BeanDefinitionReaderUtils
 *  org.springframework.beans.factory.support.BeanDefinitionRegistry
 *  org.springframework.core.annotation.AnnotationUtils
 *  org.springframework.core.io.ResourceLoader
 *  org.springframework.core.log.LogMessage
 *  org.springframework.dao.InvalidDataAccessApiUsageException
 *  org.springframework.util.Assert
 *  org.springframework.util.ClassUtils
 *  org.springframework.util.StringUtils
 */
package org.springframework.data.repository.config;

import java.lang.annotation.Annotation;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionReaderUtils;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.log.LogMessage;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.data.repository.config.AnnotationRepositoryConfigurationSource;
import org.springframework.data.repository.config.DefaultRepositoryConfiguration;
import org.springframework.data.repository.config.RepositoryConfiguration;
import org.springframework.data.repository.config.RepositoryConfigurationExtension;
import org.springframework.data.repository.config.RepositoryConfigurationSource;
import org.springframework.data.repository.config.XmlRepositoryConfigurationSource;
import org.springframework.data.repository.core.RepositoryMetadata;
import org.springframework.data.repository.core.support.AbstractRepositoryMetadata;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.util.StringUtils;

public abstract class RepositoryConfigurationExtensionSupport
implements RepositoryConfigurationExtension {
    private static final Log logger = LogFactory.getLog(RepositoryConfigurationExtensionSupport.class);
    private static final String CLASS_LOADING_ERROR = "%s - Could not load type %s using class loader %s.";
    private static final String MULTI_STORE_DROPPED = "Spring Data %s - Could not safely identify store assignment for repository candidate %s. If you want this repository to be a %s repository,";
    private boolean noMultiStoreSupport = false;

    @Override
    public String getModuleName() {
        return StringUtils.capitalize((String)this.getModulePrefix());
    }

    public <T extends RepositoryConfigurationSource> Collection<RepositoryConfiguration<T>> getRepositoryConfigurations(T configSource, ResourceLoader loader) {
        return this.getRepositoryConfigurations(configSource, loader, false);
    }

    @Override
    public <T extends RepositoryConfigurationSource> Collection<RepositoryConfiguration<T>> getRepositoryConfigurations(T configSource, ResourceLoader loader, boolean strictMatchesOnly) {
        Assert.notNull(configSource, (String)"ConfigSource must not be null!");
        Assert.notNull((Object)loader, (String)"Loader must not be null!");
        HashSet<RepositoryConfiguration<T>> result = new HashSet<RepositoryConfiguration<T>>();
        for (BeanDefinition candidate : configSource.getCandidates(loader)) {
            RepositoryConfiguration<T> configuration = this.getRepositoryConfiguration(candidate, configSource);
            Class<?> repositoryInterface = this.loadRepositoryInterface(configuration, this.getConfigurationInspectionClassLoader(loader));
            if (repositoryInterface == null) {
                result.add(configuration);
                continue;
            }
            RepositoryMetadata metadata = AbstractRepositoryMetadata.getMetadata(repositoryInterface);
            boolean qualifiedForImplementation = !strictMatchesOnly || configSource.usesExplicitFilters() || this.isStrictRepositoryCandidate(metadata);
            if (!qualifiedForImplementation || !this.useRepositoryConfiguration(metadata)) continue;
            result.add(configuration);
        }
        return result;
    }

    @Override
    public String getDefaultNamedQueryLocation() {
        return String.format("classpath*:META-INF/%s-named-queries.properties", this.getModulePrefix());
    }

    @Override
    public void registerBeansForRoot(BeanDefinitionRegistry registry, RepositoryConfigurationSource configurationSource) {
    }

    protected abstract String getModulePrefix();

    @Override
    public void postProcess(BeanDefinitionBuilder builder, RepositoryConfigurationSource source) {
    }

    @Override
    public void postProcess(BeanDefinitionBuilder builder, AnnotationRepositoryConfigurationSource config) {
    }

    @Override
    public void postProcess(BeanDefinitionBuilder builder, XmlRepositoryConfigurationSource config) {
    }

    protected Collection<Class<? extends Annotation>> getIdentifyingAnnotations() {
        return Collections.emptySet();
    }

    protected Collection<Class<?>> getIdentifyingTypes() {
        return Collections.emptySet();
    }

    @Nullable
    protected ClassLoader getConfigurationInspectionClassLoader(ResourceLoader loader) {
        return loader.getClassLoader();
    }

    public static String registerWithSourceAndGeneratedBeanName(AbstractBeanDefinition bean, BeanDefinitionRegistry registry, Object source) {
        bean.setSource(source);
        String beanName = BeanDefinitionReaderUtils.generateBeanName((BeanDefinition)bean, (BeanDefinitionRegistry)registry);
        registry.registerBeanDefinition(beanName, (BeanDefinition)bean);
        return beanName;
    }

    public static void registerIfNotAlreadyRegistered(Supplier<AbstractBeanDefinition> supplier, BeanDefinitionRegistry registry, String beanName, Object source) {
        if (registry.containsBeanDefinition(beanName)) {
            return;
        }
        AbstractBeanDefinition bean = supplier.get();
        bean.setSource(source);
        registry.registerBeanDefinition(beanName, (BeanDefinition)bean);
    }

    public static void registerLazyIfNotAlreadyRegistered(Supplier<AbstractBeanDefinition> supplier, BeanDefinitionRegistry registry, String beanName, Object source) {
        if (registry.containsBeanDefinition(beanName)) {
            return;
        }
        AbstractBeanDefinition definition = supplier.get();
        definition.setSource(source);
        definition.setLazyInit(true);
        registry.registerBeanDefinition(beanName, (BeanDefinition)definition);
    }

    public static boolean hasBean(Class<?> type, BeanDefinitionRegistry registry) {
        String name = String.format("%s%s0", type.getName(), "#");
        return registry.containsBeanDefinition(name);
    }

    protected <T extends RepositoryConfigurationSource> RepositoryConfiguration<T> getRepositoryConfiguration(BeanDefinition definition, T configSource) {
        return new DefaultRepositoryConfiguration<T>(configSource, definition, this);
    }

    protected boolean isStrictRepositoryCandidate(RepositoryMetadata metadata) {
        if (this.noMultiStoreSupport) {
            return false;
        }
        Collection<Class<?>> types = this.getIdentifyingTypes();
        Collection<Class<? extends Annotation>> annotations = this.getIdentifyingAnnotations();
        String moduleName = this.getModuleName();
        if (types.isEmpty() && annotations.isEmpty() && !this.noMultiStoreSupport) {
            logger.warn((Object)LogMessage.format((String)"Spring Data %s does not support multi-store setups!", (Object)moduleName));
            this.noMultiStoreSupport = true;
            return false;
        }
        Class<?> repositoryInterface = metadata.getRepositoryInterface();
        for (Class<?> type : types) {
            if (!type.isAssignableFrom(repositoryInterface)) continue;
            return true;
        }
        Class<?> domainType = metadata.getDomainType();
        for (Class clazz : annotations) {
            if (AnnotationUtils.findAnnotation(domainType, (Class)clazz) == null) continue;
            return true;
        }
        String message = String.format(MULTI_STORE_DROPPED, moduleName, repositoryInterface, moduleName);
        if (!annotations.isEmpty()) {
            message = message.concat(" consider annotating your entities with one of these annotations: ").concat(RepositoryConfigurationExtensionSupport.toString(annotations)).concat(types.isEmpty() ? "." : " (preferred)");
        }
        if (!types.isEmpty()) {
            message = message.concat(annotations.isEmpty() ? " consider" : ", or consider").concat(" extending one of the following types with your repository: ").concat(RepositoryConfigurationExtensionSupport.toString(types)).concat(".");
        }
        logger.info((Object)message);
        return false;
    }

    protected boolean useRepositoryConfiguration(RepositoryMetadata metadata) {
        if (metadata.isReactiveRepository()) {
            throw new InvalidDataAccessApiUsageException(String.format("Reactive Repositories are not supported by %s. Offending repository is %s!", this.getModuleName(), metadata.getRepositoryInterface().getName()));
        }
        return true;
    }

    @Nullable
    private Class<?> loadRepositoryInterface(RepositoryConfiguration<?> configuration, @Nullable ClassLoader classLoader) {
        String repositoryInterface = configuration.getRepositoryInterface();
        try {
            return ClassUtils.forName((String)repositoryInterface, (ClassLoader)classLoader);
        }
        catch (ClassNotFoundException | LinkageError e) {
            logger.warn((Object)String.format(CLASS_LOADING_ERROR, this.getModuleName(), repositoryInterface, classLoader), e);
            return null;
        }
    }

    private static String toString(Collection<? extends Class<?>> types) {
        return types.stream().map(Class::getName).collect(Collectors.joining(", "));
    }
}


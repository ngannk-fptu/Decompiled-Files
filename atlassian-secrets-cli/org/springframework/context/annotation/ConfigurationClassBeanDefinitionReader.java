/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.context.annotation;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.BeanDefinitionStoreException;
import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.beans.factory.annotation.AnnotatedGenericBeanDefinition;
import org.springframework.beans.factory.annotation.Autowire;
import org.springframework.beans.factory.annotation.RequiredAnnotationBeanPostProcessor;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.groovy.GroovyBeanDefinitionReader;
import org.springframework.beans.factory.parsing.SourceExtractor;
import org.springframework.beans.factory.support.AbstractBeanDefinitionReader;
import org.springframework.beans.factory.support.BeanDefinitionReader;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanNameGenerator;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.beans.factory.xml.XmlBeanDefinitionReader;
import org.springframework.context.annotation.AnnotationConfigUtils;
import org.springframework.context.annotation.AnnotationScopeMetadataResolver;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.BeanAnnotationHelper;
import org.springframework.context.annotation.BeanMethod;
import org.springframework.context.annotation.ConditionEvaluator;
import org.springframework.context.annotation.ConfigurationClass;
import org.springframework.context.annotation.ConfigurationCondition;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.context.annotation.ImportRegistry;
import org.springframework.context.annotation.ScannedGenericBeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopeMetadata;
import org.springframework.context.annotation.ScopeMetadataResolver;
import org.springframework.context.annotation.ScopedProxyCreator;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.type.AnnotatedTypeMetadata;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.core.type.MethodMetadata;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

class ConfigurationClassBeanDefinitionReader {
    private static final Log logger = LogFactory.getLog(ConfigurationClassBeanDefinitionReader.class);
    private static final ScopeMetadataResolver scopeMetadataResolver = new AnnotationScopeMetadataResolver();
    private final BeanDefinitionRegistry registry;
    private final SourceExtractor sourceExtractor;
    private final ResourceLoader resourceLoader;
    private final Environment environment;
    private final BeanNameGenerator importBeanNameGenerator;
    private final ImportRegistry importRegistry;
    private final ConditionEvaluator conditionEvaluator;

    ConfigurationClassBeanDefinitionReader(BeanDefinitionRegistry registry, SourceExtractor sourceExtractor, ResourceLoader resourceLoader, Environment environment2, BeanNameGenerator importBeanNameGenerator, ImportRegistry importRegistry) {
        this.registry = registry;
        this.sourceExtractor = sourceExtractor;
        this.resourceLoader = resourceLoader;
        this.environment = environment2;
        this.importBeanNameGenerator = importBeanNameGenerator;
        this.importRegistry = importRegistry;
        this.conditionEvaluator = new ConditionEvaluator(registry, environment2, resourceLoader);
    }

    public void loadBeanDefinitions(Set<ConfigurationClass> configurationModel) {
        TrackedConditionEvaluator trackedConditionEvaluator = new TrackedConditionEvaluator();
        for (ConfigurationClass configClass : configurationModel) {
            this.loadBeanDefinitionsForConfigurationClass(configClass, trackedConditionEvaluator);
        }
    }

    private void loadBeanDefinitionsForConfigurationClass(ConfigurationClass configClass, TrackedConditionEvaluator trackedConditionEvaluator) {
        if (trackedConditionEvaluator.shouldSkip(configClass)) {
            String beanName = configClass.getBeanName();
            if (StringUtils.hasLength(beanName) && this.registry.containsBeanDefinition(beanName)) {
                this.registry.removeBeanDefinition(beanName);
            }
            this.importRegistry.removeImportingClass(configClass.getMetadata().getClassName());
            return;
        }
        if (configClass.isImported()) {
            this.registerBeanDefinitionForImportedConfigurationClass(configClass);
        }
        for (BeanMethod beanMethod : configClass.getBeanMethods()) {
            this.loadBeanDefinitionsForBeanMethod(beanMethod);
        }
        this.loadBeanDefinitionsFromImportedResources(configClass.getImportedResources());
        this.loadBeanDefinitionsFromRegistrars(configClass.getImportBeanDefinitionRegistrars());
    }

    private void registerBeanDefinitionForImportedConfigurationClass(ConfigurationClass configClass) {
        AnnotationMetadata metadata = configClass.getMetadata();
        AnnotatedGenericBeanDefinition configBeanDef = new AnnotatedGenericBeanDefinition(metadata);
        ScopeMetadata scopeMetadata = scopeMetadataResolver.resolveScopeMetadata(configBeanDef);
        configBeanDef.setScope(scopeMetadata.getScopeName());
        String configBeanName = this.importBeanNameGenerator.generateBeanName(configBeanDef, this.registry);
        AnnotationConfigUtils.processCommonDefinitionAnnotations(configBeanDef, metadata);
        BeanDefinitionHolder definitionHolder = new BeanDefinitionHolder(configBeanDef, configBeanName);
        definitionHolder = AnnotationConfigUtils.applyScopedProxyMode(scopeMetadata, definitionHolder, this.registry);
        this.registry.registerBeanDefinition(definitionHolder.getBeanName(), definitionHolder.getBeanDefinition());
        configClass.setBeanName(configBeanName);
        if (logger.isDebugEnabled()) {
            logger.debug("Registered bean definition for imported class '" + configBeanName + "'");
        }
    }

    private void loadBeanDefinitionsForBeanMethod(BeanMethod beanMethod) {
        String initMethodName;
        ConfigurationClass configClass = beanMethod.getConfigurationClass();
        MethodMetadata metadata = beanMethod.getMetadata();
        String methodName = metadata.getMethodName();
        if (this.conditionEvaluator.shouldSkip(metadata, ConfigurationCondition.ConfigurationPhase.REGISTER_BEAN)) {
            configClass.skippedBeanMethods.add(methodName);
            return;
        }
        if (configClass.skippedBeanMethods.contains(methodName)) {
            return;
        }
        AnnotationAttributes bean2 = AnnotationConfigUtils.attributesFor((AnnotatedTypeMetadata)metadata, Bean.class);
        Assert.state(bean2 != null, "No @Bean annotation attributes");
        ArrayList<String> names = new ArrayList<String>(Arrays.asList(bean2.getStringArray("name")));
        String beanName = !names.isEmpty() ? (String)names.remove(0) : methodName;
        for (String alias : names) {
            this.registry.registerAlias(beanName, alias);
        }
        if (this.isOverriddenByExistingDefinition(beanMethod, beanName)) {
            if (beanName.equals(beanMethod.getConfigurationClass().getBeanName())) {
                throw new BeanDefinitionStoreException(beanMethod.getConfigurationClass().getResource().getDescription(), beanName, "Bean name derived from @Bean method '" + beanMethod.getMetadata().getMethodName() + "' clashes with bean name for containing configuration class; please make those names unique!");
            }
            return;
        }
        ConfigurationClassBeanDefinition beanDef = new ConfigurationClassBeanDefinition(configClass, metadata);
        beanDef.setResource(configClass.getResource());
        beanDef.setSource(this.sourceExtractor.extractSource(metadata, configClass.getResource()));
        if (metadata.isStatic()) {
            beanDef.setBeanClassName(configClass.getMetadata().getClassName());
            beanDef.setFactoryMethodName(methodName);
        } else {
            beanDef.setFactoryBeanName(configClass.getBeanName());
            beanDef.setUniqueFactoryMethodName(methodName);
        }
        beanDef.setAutowireMode(3);
        beanDef.setAttribute(RequiredAnnotationBeanPostProcessor.SKIP_REQUIRED_CHECK_ATTRIBUTE, Boolean.TRUE);
        AnnotationConfigUtils.processCommonDefinitionAnnotations(beanDef, metadata);
        Autowire autowire = (Autowire)((Object)bean2.getEnum("autowire"));
        if (autowire.isAutowire()) {
            beanDef.setAutowireMode(autowire.value());
        }
        if (StringUtils.hasText(initMethodName = bean2.getString("initMethod"))) {
            beanDef.setInitMethodName(initMethodName);
        }
        String destroyMethodName = bean2.getString("destroyMethod");
        beanDef.setDestroyMethodName(destroyMethodName);
        ScopedProxyMode proxyMode = ScopedProxyMode.NO;
        AnnotationAttributes attributes = AnnotationConfigUtils.attributesFor((AnnotatedTypeMetadata)metadata, Scope.class);
        if (attributes != null) {
            beanDef.setScope(attributes.getString("value"));
            proxyMode = (ScopedProxyMode)((Object)attributes.getEnum("proxyMode"));
            if (proxyMode == ScopedProxyMode.DEFAULT) {
                proxyMode = ScopedProxyMode.NO;
            }
        }
        ConfigurationClassBeanDefinition beanDefToRegister = beanDef;
        if (proxyMode != ScopedProxyMode.NO) {
            BeanDefinitionHolder proxyDef = ScopedProxyCreator.createScopedProxy(new BeanDefinitionHolder(beanDef, beanName), this.registry, proxyMode == ScopedProxyMode.TARGET_CLASS);
            beanDefToRegister = new ConfigurationClassBeanDefinition((RootBeanDefinition)proxyDef.getBeanDefinition(), configClass, metadata);
        }
        if (logger.isDebugEnabled()) {
            logger.debug(String.format("Registering bean definition for @Bean method %s.%s()", configClass.getMetadata().getClassName(), beanName));
        }
        this.registry.registerBeanDefinition(beanName, beanDefToRegister);
    }

    protected boolean isOverriddenByExistingDefinition(BeanMethod beanMethod, String beanName) {
        if (!this.registry.containsBeanDefinition(beanName)) {
            return false;
        }
        BeanDefinition existingBeanDef = this.registry.getBeanDefinition(beanName);
        if (existingBeanDef instanceof ConfigurationClassBeanDefinition) {
            ConfigurationClassBeanDefinition ccbd = (ConfigurationClassBeanDefinition)existingBeanDef;
            return ccbd.getMetadata().getClassName().equals(beanMethod.getConfigurationClass().getMetadata().getClassName());
        }
        if (existingBeanDef instanceof ScannedGenericBeanDefinition) {
            return false;
        }
        if (existingBeanDef.getRole() > 0) {
            return false;
        }
        if (this.registry instanceof DefaultListableBeanFactory && !((DefaultListableBeanFactory)this.registry).isAllowBeanDefinitionOverriding()) {
            throw new BeanDefinitionStoreException(beanMethod.getConfigurationClass().getResource().getDescription(), beanName, "@Bean definition illegally overridden by existing bean definition: " + existingBeanDef);
        }
        if (logger.isInfoEnabled()) {
            logger.info(String.format("Skipping bean definition for %s: a definition for bean '%s' already exists. This top-level bean definition is considered as an override.", beanMethod, beanName));
        }
        return true;
    }

    private void loadBeanDefinitionsFromImportedResources(Map<String, Class<? extends BeanDefinitionReader>> importedResources) {
        HashMap readerInstanceCache = new HashMap();
        importedResources.forEach((resource, readerClass) -> {
            BeanDefinitionReader reader;
            if (BeanDefinitionReader.class == readerClass) {
                readerClass = StringUtils.endsWithIgnoreCase(resource, ".groovy") ? GroovyBeanDefinitionReader.class : XmlBeanDefinitionReader.class;
            }
            if ((reader = (BeanDefinitionReader)readerInstanceCache.get(readerClass)) == null) {
                try {
                    reader = (BeanDefinitionReader)readerClass.getConstructor(BeanDefinitionRegistry.class).newInstance(this.registry);
                    if (reader instanceof AbstractBeanDefinitionReader) {
                        AbstractBeanDefinitionReader abdr = (AbstractBeanDefinitionReader)reader;
                        abdr.setResourceLoader(this.resourceLoader);
                        abdr.setEnvironment(this.environment);
                    }
                    readerInstanceCache.put(readerClass, reader);
                }
                catch (Throwable ex) {
                    throw new IllegalStateException("Could not instantiate BeanDefinitionReader class [" + readerClass.getName() + "]");
                }
            }
            reader.loadBeanDefinitions((String)resource);
        });
    }

    private void loadBeanDefinitionsFromRegistrars(Map<ImportBeanDefinitionRegistrar, AnnotationMetadata> registrars) {
        registrars.forEach((registrar, metadata) -> registrar.registerBeanDefinitions((AnnotationMetadata)metadata, this.registry));
    }

    private class TrackedConditionEvaluator {
        private final Map<ConfigurationClass, Boolean> skipped = new HashMap<ConfigurationClass, Boolean>();

        private TrackedConditionEvaluator() {
        }

        public boolean shouldSkip(ConfigurationClass configClass) {
            Boolean skip = this.skipped.get(configClass);
            if (skip == null) {
                if (configClass.isImported()) {
                    boolean allSkipped = true;
                    for (ConfigurationClass importedBy : configClass.getImportedBy()) {
                        if (this.shouldSkip(importedBy)) continue;
                        allSkipped = false;
                        break;
                    }
                    if (allSkipped) {
                        skip = true;
                    }
                }
                if (skip == null) {
                    skip = ConfigurationClassBeanDefinitionReader.this.conditionEvaluator.shouldSkip(configClass.getMetadata(), ConfigurationCondition.ConfigurationPhase.REGISTER_BEAN);
                }
                this.skipped.put(configClass, skip);
            }
            return skip;
        }
    }

    private static class ConfigurationClassBeanDefinition
    extends RootBeanDefinition
    implements AnnotatedBeanDefinition {
        private final AnnotationMetadata annotationMetadata;
        private final MethodMetadata factoryMethodMetadata;

        public ConfigurationClassBeanDefinition(ConfigurationClass configClass, MethodMetadata beanMethodMetadata) {
            this.annotationMetadata = configClass.getMetadata();
            this.factoryMethodMetadata = beanMethodMetadata;
            this.setLenientConstructorResolution(false);
        }

        public ConfigurationClassBeanDefinition(RootBeanDefinition original, ConfigurationClass configClass, MethodMetadata beanMethodMetadata) {
            super(original);
            this.annotationMetadata = configClass.getMetadata();
            this.factoryMethodMetadata = beanMethodMetadata;
        }

        private ConfigurationClassBeanDefinition(ConfigurationClassBeanDefinition original) {
            super(original);
            this.annotationMetadata = original.annotationMetadata;
            this.factoryMethodMetadata = original.factoryMethodMetadata;
        }

        @Override
        public AnnotationMetadata getMetadata() {
            return this.annotationMetadata;
        }

        @Override
        public MethodMetadata getFactoryMethodMetadata() {
            return this.factoryMethodMetadata;
        }

        @Override
        public boolean isFactoryMethod(Method candidate) {
            return super.isFactoryMethod(candidate) && BeanAnnotationHelper.isBeanAnnotated(candidate);
        }

        @Override
        public ConfigurationClassBeanDefinition cloneBeanDefinition() {
            return new ConfigurationClassBeanDefinition(this);
        }
    }
}


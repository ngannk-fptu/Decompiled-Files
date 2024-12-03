/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 */
package org.springframework.context.annotation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.aop.framework.autoproxy.AutoProxyUtils;
import org.springframework.beans.PropertyValues;
import org.springframework.beans.factory.BeanClassLoaderAware;
import org.springframework.beans.factory.BeanDefinitionStoreException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.config.InstantiationAwareBeanPostProcessor;
import org.springframework.beans.factory.config.SingletonBeanRegistry;
import org.springframework.beans.factory.parsing.FailFastProblemReporter;
import org.springframework.beans.factory.parsing.PassThroughSourceExtractor;
import org.springframework.beans.factory.parsing.ProblemReporter;
import org.springframework.beans.factory.parsing.SourceExtractor;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.beans.factory.support.BeanNameGenerator;
import org.springframework.context.ApplicationStartupAware;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.context.annotation.AnnotationBeanNameGenerator;
import org.springframework.context.annotation.ConfigurationClass;
import org.springframework.context.annotation.ConfigurationClassBeanDefinitionReader;
import org.springframework.context.annotation.ConfigurationClassEnhancer;
import org.springframework.context.annotation.ConfigurationClassParser;
import org.springframework.context.annotation.ConfigurationClassUtils;
import org.springframework.context.annotation.FullyQualifiedAnnotationBeanNameGenerator;
import org.springframework.context.annotation.ImportAware;
import org.springframework.context.annotation.ImportRegistry;
import org.springframework.core.NativeDetector;
import org.springframework.core.PriorityOrdered;
import org.springframework.core.env.Environment;
import org.springframework.core.env.StandardEnvironment;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.metrics.ApplicationStartup;
import org.springframework.core.metrics.StartupStep;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.core.type.MethodMetadata;
import org.springframework.core.type.classreading.CachingMetadataReaderFactory;
import org.springframework.core.type.classreading.MetadataReaderFactory;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;

public class ConfigurationClassPostProcessor
implements BeanDefinitionRegistryPostProcessor,
PriorityOrdered,
ResourceLoaderAware,
ApplicationStartupAware,
BeanClassLoaderAware,
EnvironmentAware {
    public static final AnnotationBeanNameGenerator IMPORT_BEAN_NAME_GENERATOR = FullyQualifiedAnnotationBeanNameGenerator.INSTANCE;
    private static final String IMPORT_REGISTRY_BEAN_NAME = ConfigurationClassPostProcessor.class.getName() + ".importRegistry";
    private final Log logger = LogFactory.getLog(this.getClass());
    private SourceExtractor sourceExtractor = new PassThroughSourceExtractor();
    private ProblemReporter problemReporter = new FailFastProblemReporter();
    @Nullable
    private Environment environment;
    private ResourceLoader resourceLoader = new DefaultResourceLoader();
    @Nullable
    private ClassLoader beanClassLoader = ClassUtils.getDefaultClassLoader();
    private MetadataReaderFactory metadataReaderFactory = new CachingMetadataReaderFactory();
    private boolean setMetadataReaderFactoryCalled = false;
    private final Set<Integer> registriesPostProcessed = new HashSet<Integer>();
    private final Set<Integer> factoriesPostProcessed = new HashSet<Integer>();
    @Nullable
    private ConfigurationClassBeanDefinitionReader reader;
    private boolean localBeanNameGeneratorSet = false;
    private BeanNameGenerator componentScanBeanNameGenerator = AnnotationBeanNameGenerator.INSTANCE;
    private BeanNameGenerator importBeanNameGenerator = IMPORT_BEAN_NAME_GENERATOR;
    private ApplicationStartup applicationStartup = ApplicationStartup.DEFAULT;

    @Override
    public int getOrder() {
        return Integer.MAX_VALUE;
    }

    public void setSourceExtractor(@Nullable SourceExtractor sourceExtractor) {
        this.sourceExtractor = sourceExtractor != null ? sourceExtractor : new PassThroughSourceExtractor();
    }

    public void setProblemReporter(@Nullable ProblemReporter problemReporter) {
        this.problemReporter = problemReporter != null ? problemReporter : new FailFastProblemReporter();
    }

    public void setMetadataReaderFactory(MetadataReaderFactory metadataReaderFactory) {
        Assert.notNull((Object)metadataReaderFactory, "MetadataReaderFactory must not be null");
        this.metadataReaderFactory = metadataReaderFactory;
        this.setMetadataReaderFactoryCalled = true;
    }

    public void setBeanNameGenerator(BeanNameGenerator beanNameGenerator) {
        Assert.notNull((Object)beanNameGenerator, "BeanNameGenerator must not be null");
        this.localBeanNameGeneratorSet = true;
        this.componentScanBeanNameGenerator = beanNameGenerator;
        this.importBeanNameGenerator = beanNameGenerator;
    }

    @Override
    public void setEnvironment(Environment environment2) {
        Assert.notNull((Object)environment2, "Environment must not be null");
        this.environment = environment2;
    }

    @Override
    public void setResourceLoader(ResourceLoader resourceLoader) {
        Assert.notNull((Object)resourceLoader, "ResourceLoader must not be null");
        this.resourceLoader = resourceLoader;
        if (!this.setMetadataReaderFactoryCalled) {
            this.metadataReaderFactory = new CachingMetadataReaderFactory(resourceLoader);
        }
    }

    @Override
    public void setBeanClassLoader(ClassLoader beanClassLoader) {
        this.beanClassLoader = beanClassLoader;
        if (!this.setMetadataReaderFactoryCalled) {
            this.metadataReaderFactory = new CachingMetadataReaderFactory(beanClassLoader);
        }
    }

    @Override
    public void setApplicationStartup(ApplicationStartup applicationStartup) {
        this.applicationStartup = applicationStartup;
    }

    @Override
    public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) {
        int registryId = System.identityHashCode(registry);
        if (this.registriesPostProcessed.contains(registryId)) {
            throw new IllegalStateException("postProcessBeanDefinitionRegistry already called on this post-processor against " + registry);
        }
        if (this.factoriesPostProcessed.contains(registryId)) {
            throw new IllegalStateException("postProcessBeanFactory already called on this post-processor against " + registry);
        }
        this.registriesPostProcessed.add(registryId);
        this.processConfigBeanDefinitions(registry);
    }

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) {
        int factoryId = System.identityHashCode(beanFactory);
        if (this.factoriesPostProcessed.contains(factoryId)) {
            throw new IllegalStateException("postProcessBeanFactory already called on this post-processor against " + beanFactory);
        }
        this.factoriesPostProcessed.add(factoryId);
        if (!this.registriesPostProcessed.contains(factoryId)) {
            this.processConfigBeanDefinitions((BeanDefinitionRegistry)((Object)beanFactory));
        }
        this.enhanceConfigurationClasses(beanFactory);
        beanFactory.addBeanPostProcessor(new ImportAwareBeanPostProcessor(beanFactory));
    }

    public void processConfigBeanDefinitions(BeanDefinitionRegistry registry) {
        String[] candidateNames;
        ArrayList<BeanDefinitionHolder> configCandidates = new ArrayList<BeanDefinitionHolder>();
        for (String beanName : candidateNames = registry.getBeanDefinitionNames()) {
            BeanDefinition beanDef = registry.getBeanDefinition(beanName);
            if (beanDef.getAttribute(ConfigurationClassUtils.CONFIGURATION_CLASS_ATTRIBUTE) != null) {
                if (!this.logger.isDebugEnabled()) continue;
                this.logger.debug((Object)("Bean definition has already been processed as a configuration class: " + beanDef));
                continue;
            }
            if (!ConfigurationClassUtils.checkConfigurationClassCandidate(beanDef, this.metadataReaderFactory)) continue;
            configCandidates.add(new BeanDefinitionHolder(beanDef, beanName));
        }
        if (configCandidates.isEmpty()) {
            return;
        }
        configCandidates.sort((bd1, bd2) -> {
            int i1 = ConfigurationClassUtils.getOrder(bd1.getBeanDefinition());
            int i2 = ConfigurationClassUtils.getOrder(bd2.getBeanDefinition());
            return Integer.compare(i1, i2);
        });
        SingletonBeanRegistry sbr = null;
        if (registry instanceof SingletonBeanRegistry) {
            BeanNameGenerator generator;
            sbr = (SingletonBeanRegistry)((Object)registry);
            if (!this.localBeanNameGeneratorSet && (generator = (BeanNameGenerator)sbr.getSingleton("org.springframework.context.annotation.internalConfigurationBeanNameGenerator")) != null) {
                this.componentScanBeanNameGenerator = generator;
                this.importBeanNameGenerator = generator;
            }
        }
        if (this.environment == null) {
            this.environment = new StandardEnvironment();
        }
        ConfigurationClassParser parser = new ConfigurationClassParser(this.metadataReaderFactory, this.problemReporter, this.environment, this.resourceLoader, this.componentScanBeanNameGenerator, registry);
        LinkedHashSet<BeanDefinitionHolder> candidates = new LinkedHashSet<BeanDefinitionHolder>(configCandidates);
        HashSet<ConfigurationClass> alreadyParsed = new HashSet<ConfigurationClass>(configCandidates.size());
        do {
            StartupStep processConfig = this.applicationStartup.start("spring.context.config-classes.parse");
            parser.parse(candidates);
            parser.validate();
            LinkedHashSet<ConfigurationClass> configClasses = new LinkedHashSet<ConfigurationClass>(parser.getConfigurationClasses());
            configClasses.removeAll(alreadyParsed);
            if (this.reader == null) {
                this.reader = new ConfigurationClassBeanDefinitionReader(registry, this.sourceExtractor, this.resourceLoader, this.environment, this.importBeanNameGenerator, parser.getImportRegistry());
            }
            this.reader.loadBeanDefinitions(configClasses);
            alreadyParsed.addAll(configClasses);
            processConfig.tag("classCount", () -> String.valueOf(configClasses.size())).end();
            candidates.clear();
            if (registry.getBeanDefinitionCount() <= candidateNames.length) continue;
            String[] newCandidateNames = registry.getBeanDefinitionNames();
            HashSet<String> oldCandidateNames = new HashSet<String>(Arrays.asList(candidateNames));
            HashSet<String> alreadyParsedClasses = new HashSet<String>();
            for (ConfigurationClass configurationClass : alreadyParsed) {
                alreadyParsedClasses.add(configurationClass.getMetadata().getClassName());
            }
            for (String candidateName : newCandidateNames) {
                BeanDefinition bd;
                if (oldCandidateNames.contains(candidateName) || !ConfigurationClassUtils.checkConfigurationClassCandidate(bd = registry.getBeanDefinition(candidateName), this.metadataReaderFactory) || alreadyParsedClasses.contains(bd.getBeanClassName())) continue;
                candidates.add(new BeanDefinitionHolder(bd, candidateName));
            }
            candidateNames = newCandidateNames;
        } while (!candidates.isEmpty());
        if (sbr != null && !sbr.containsSingleton(IMPORT_REGISTRY_BEAN_NAME)) {
            sbr.registerSingleton(IMPORT_REGISTRY_BEAN_NAME, parser.getImportRegistry());
        }
        if (this.metadataReaderFactory instanceof CachingMetadataReaderFactory) {
            ((CachingMetadataReaderFactory)this.metadataReaderFactory).clearCache();
        }
    }

    public void enhanceConfigurationClasses(ConfigurableListableBeanFactory beanFactory) {
        StartupStep enhanceConfigClasses = this.applicationStartup.start("spring.context.config-classes.enhance");
        LinkedHashMap<String, AbstractBeanDefinition> configBeanDefs = new LinkedHashMap<String, AbstractBeanDefinition>();
        for (String beanName : beanFactory.getBeanDefinitionNames()) {
            AbstractBeanDefinition abd;
            BeanDefinition beanDef = beanFactory.getBeanDefinition(beanName);
            Object configClassAttr = beanDef.getAttribute(ConfigurationClassUtils.CONFIGURATION_CLASS_ATTRIBUTE);
            AnnotationMetadata annotationMetadata = null;
            MethodMetadata methodMetadata = null;
            if (beanDef instanceof AnnotatedBeanDefinition) {
                AnnotatedBeanDefinition annotatedBeanDefinition = (AnnotatedBeanDefinition)beanDef;
                annotationMetadata = annotatedBeanDefinition.getMetadata();
                methodMetadata = annotatedBeanDefinition.getFactoryMethodMetadata();
            }
            if ((configClassAttr != null || methodMetadata != null) && beanDef instanceof AbstractBeanDefinition && !(abd = (AbstractBeanDefinition)beanDef).hasBeanClass()) {
                boolean liteConfigurationCandidateWithoutBeanMethods;
                boolean bl = liteConfigurationCandidateWithoutBeanMethods = "lite".equals(configClassAttr) && annotationMetadata != null && !ConfigurationClassUtils.hasBeanMethods(annotationMetadata);
                if (!liteConfigurationCandidateWithoutBeanMethods) {
                    try {
                        abd.resolveBeanClass(this.beanClassLoader);
                    }
                    catch (Throwable ex) {
                        throw new IllegalStateException("Cannot load configuration class: " + beanDef.getBeanClassName(), ex);
                    }
                }
            }
            if (!"full".equals(configClassAttr)) continue;
            if (!(beanDef instanceof AbstractBeanDefinition)) {
                throw new BeanDefinitionStoreException("Cannot enhance @Configuration bean definition '" + beanName + "' since it is not stored in an AbstractBeanDefinition subclass");
            }
            if (this.logger.isInfoEnabled() && beanFactory.containsSingleton(beanName)) {
                this.logger.info((Object)("Cannot enhance @Configuration bean definition '" + beanName + "' since its singleton instance has been created too early. The typical cause is a non-static @Bean method with a BeanDefinitionRegistryPostProcessor return type: Consider declaring such methods as 'static'."));
            }
            configBeanDefs.put(beanName, (AbstractBeanDefinition)beanDef);
        }
        if (configBeanDefs.isEmpty() || NativeDetector.inNativeImage()) {
            enhanceConfigClasses.end();
            return;
        }
        ConfigurationClassEnhancer enhancer = new ConfigurationClassEnhancer();
        for (Map.Entry entry : configBeanDefs.entrySet()) {
            Class<?> enhancedClass;
            AbstractBeanDefinition beanDef = (AbstractBeanDefinition)entry.getValue();
            beanDef.setAttribute(AutoProxyUtils.PRESERVE_TARGET_CLASS_ATTRIBUTE, Boolean.TRUE);
            Class<?> configClass = beanDef.getBeanClass();
            if (configClass == (enhancedClass = enhancer.enhance(configClass, this.beanClassLoader))) continue;
            if (this.logger.isTraceEnabled()) {
                this.logger.trace((Object)String.format("Replacing bean definition '%s' existing class '%s' with enhanced class '%s'", entry.getKey(), configClass.getName(), enhancedClass.getName()));
            }
            beanDef.setBeanClass(enhancedClass);
        }
        enhanceConfigClasses.tag("classCount", () -> String.valueOf(configBeanDefs.keySet().size())).end();
    }

    private static class ImportAwareBeanPostProcessor
    implements InstantiationAwareBeanPostProcessor {
        private final BeanFactory beanFactory;

        public ImportAwareBeanPostProcessor(BeanFactory beanFactory) {
            this.beanFactory = beanFactory;
        }

        @Override
        public PropertyValues postProcessProperties(@Nullable PropertyValues pvs, Object bean2, String beanName) {
            if (bean2 instanceof ConfigurationClassEnhancer.EnhancedConfiguration) {
                ((ConfigurationClassEnhancer.EnhancedConfiguration)bean2).setBeanFactory(this.beanFactory);
            }
            return pvs;
        }

        @Override
        public Object postProcessBeforeInitialization(Object bean2, String beanName) {
            ImportRegistry ir;
            AnnotationMetadata importingClass;
            if (bean2 instanceof ImportAware && (importingClass = (ir = this.beanFactory.getBean(IMPORT_REGISTRY_BEAN_NAME, ImportRegistry.class)).getImportingClassFor(ClassUtils.getUserClass(bean2).getName())) != null) {
                ((ImportAware)bean2).setImportMetadata(importingClass);
            }
            return bean2;
        }
    }
}


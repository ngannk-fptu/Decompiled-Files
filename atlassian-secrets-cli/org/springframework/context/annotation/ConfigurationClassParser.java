/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.context.annotation;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.net.UnknownHostException;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Deque;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.BeanDefinitionStoreException;
import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.parsing.Location;
import org.springframework.beans.factory.parsing.Problem;
import org.springframework.beans.factory.parsing.ProblemReporter;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanNameGenerator;
import org.springframework.context.annotation.AnnotationConfigUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.BeanMethod;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ComponentScanAnnotationParser;
import org.springframework.context.annotation.ComponentScans;
import org.springframework.context.annotation.ConditionEvaluator;
import org.springframework.context.annotation.ConfigurationClass;
import org.springframework.context.annotation.ConfigurationClassUtils;
import org.springframework.context.annotation.ConfigurationCondition;
import org.springframework.context.annotation.DeferredImportSelector;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.context.annotation.ImportRegistry;
import org.springframework.context.annotation.ImportResource;
import org.springframework.context.annotation.ImportSelector;
import org.springframework.context.annotation.ParserStrategyUtils;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;
import org.springframework.core.NestedIOException;
import org.springframework.core.OrderComparator;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.annotation.AnnotationAwareOrderComparator;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.core.env.CompositePropertySource;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.Environment;
import org.springframework.core.env.MutablePropertySources;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.io.support.DefaultPropertySourceFactory;
import org.springframework.core.io.support.EncodedResource;
import org.springframework.core.io.support.PropertySourceFactory;
import org.springframework.core.io.support.ResourcePropertySource;
import org.springframework.core.type.AnnotatedTypeMetadata;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.core.type.MethodMetadata;
import org.springframework.core.type.StandardAnnotationMetadata;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.core.type.classreading.MetadataReaderFactory;
import org.springframework.core.type.filter.AssignableTypeFilter;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.util.CollectionUtils;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;

class ConfigurationClassParser {
    private static final PropertySourceFactory DEFAULT_PROPERTY_SOURCE_FACTORY = new DefaultPropertySourceFactory();
    private static final Comparator<DeferredImportSelectorHolder> DEFERRED_IMPORT_COMPARATOR = (o1, o2) -> AnnotationAwareOrderComparator.INSTANCE.compare(o1.getImportSelector(), o2.getImportSelector());
    private final Log logger = LogFactory.getLog(this.getClass());
    private final MetadataReaderFactory metadataReaderFactory;
    private final ProblemReporter problemReporter;
    private final Environment environment;
    private final ResourceLoader resourceLoader;
    private final BeanDefinitionRegistry registry;
    private final ComponentScanAnnotationParser componentScanParser;
    private final ConditionEvaluator conditionEvaluator;
    private final Map<ConfigurationClass, ConfigurationClass> configurationClasses = new LinkedHashMap<ConfigurationClass, ConfigurationClass>();
    private final Map<String, ConfigurationClass> knownSuperclasses = new HashMap<String, ConfigurationClass>();
    private final List<String> propertySourceNames = new ArrayList<String>();
    private final ImportStack importStack = new ImportStack();
    @Nullable
    private List<DeferredImportSelectorHolder> deferredImportSelectors;

    public ConfigurationClassParser(MetadataReaderFactory metadataReaderFactory, ProblemReporter problemReporter, Environment environment2, ResourceLoader resourceLoader, BeanNameGenerator componentScanBeanNameGenerator, BeanDefinitionRegistry registry) {
        this.metadataReaderFactory = metadataReaderFactory;
        this.problemReporter = problemReporter;
        this.environment = environment2;
        this.resourceLoader = resourceLoader;
        this.registry = registry;
        this.componentScanParser = new ComponentScanAnnotationParser(environment2, resourceLoader, componentScanBeanNameGenerator, registry);
        this.conditionEvaluator = new ConditionEvaluator(registry, environment2, resourceLoader);
    }

    public void parse(Set<BeanDefinitionHolder> configCandidates) {
        this.deferredImportSelectors = new LinkedList<DeferredImportSelectorHolder>();
        for (BeanDefinitionHolder holder : configCandidates) {
            BeanDefinition bd = holder.getBeanDefinition();
            try {
                if (bd instanceof AnnotatedBeanDefinition) {
                    this.parse(((AnnotatedBeanDefinition)bd).getMetadata(), holder.getBeanName());
                    continue;
                }
                if (bd instanceof AbstractBeanDefinition && ((AbstractBeanDefinition)bd).hasBeanClass()) {
                    this.parse(((AbstractBeanDefinition)bd).getBeanClass(), holder.getBeanName());
                    continue;
                }
                this.parse(bd.getBeanClassName(), holder.getBeanName());
            }
            catch (BeanDefinitionStoreException ex) {
                throw ex;
            }
            catch (Throwable ex) {
                throw new BeanDefinitionStoreException("Failed to parse configuration class [" + bd.getBeanClassName() + "]", ex);
            }
        }
        this.processDeferredImportSelectors();
    }

    protected final void parse(@Nullable String className, String beanName) throws IOException {
        Assert.notNull((Object)className, "No bean class name for configuration class bean definition");
        MetadataReader reader = this.metadataReaderFactory.getMetadataReader(className);
        this.processConfigurationClass(new ConfigurationClass(reader, beanName));
    }

    protected final void parse(Class<?> clazz, String beanName) throws IOException {
        this.processConfigurationClass(new ConfigurationClass(clazz, beanName));
    }

    protected final void parse(AnnotationMetadata metadata, String beanName) throws IOException {
        this.processConfigurationClass(new ConfigurationClass(metadata, beanName));
    }

    public void validate() {
        for (ConfigurationClass configClass : this.configurationClasses.keySet()) {
            configClass.validate(this.problemReporter);
        }
    }

    public Set<ConfigurationClass> getConfigurationClasses() {
        return this.configurationClasses.keySet();
    }

    protected void processConfigurationClass(ConfigurationClass configClass) throws IOException {
        if (this.conditionEvaluator.shouldSkip(configClass.getMetadata(), ConfigurationCondition.ConfigurationPhase.PARSE_CONFIGURATION)) {
            return;
        }
        ConfigurationClass existingClass = this.configurationClasses.get(configClass);
        if (existingClass != null) {
            if (configClass.isImported()) {
                if (existingClass.isImported()) {
                    existingClass.mergeImportedBy(configClass);
                }
                return;
            }
            this.configurationClasses.remove(configClass);
            this.knownSuperclasses.values().removeIf(configClass::equals);
        }
        SourceClass sourceClass = this.asSourceClass(configClass);
        while ((sourceClass = this.doProcessConfigurationClass(configClass, sourceClass)) != null) {
        }
        this.configurationClasses.put(configClass, configClass);
    }

    @Nullable
    protected final SourceClass doProcessConfigurationClass(ConfigurationClass configClass, SourceClass sourceClass) throws IOException {
        String superclass;
        this.processMemberClasses(configClass, sourceClass);
        for (AnnotationAttributes annotationAttributes : AnnotationConfigUtils.attributesForRepeatable(sourceClass.getMetadata(), PropertySources.class, PropertySource.class)) {
            if (this.environment instanceof ConfigurableEnvironment) {
                this.processPropertySource(annotationAttributes);
                continue;
            }
            this.logger.warn("Ignoring @PropertySource annotation on [" + sourceClass.getMetadata().getClassName() + "]. Reason: Environment must implement ConfigurableEnvironment");
        }
        Set<AnnotationAttributes> componentScans = AnnotationConfigUtils.attributesForRepeatable(sourceClass.getMetadata(), ComponentScans.class, ComponentScan.class);
        if (!componentScans.isEmpty() && !this.conditionEvaluator.shouldSkip(sourceClass.getMetadata(), ConfigurationCondition.ConfigurationPhase.REGISTER_BEAN)) {
            for (AnnotationAttributes componentScan : componentScans) {
                Set<BeanDefinitionHolder> scannedBeanDefinitions = this.componentScanParser.parse(componentScan, sourceClass.getMetadata().getClassName());
                for (BeanDefinitionHolder holder : scannedBeanDefinitions) {
                    BeanDefinition bdCand = holder.getBeanDefinition().getOriginatingBeanDefinition();
                    if (bdCand == null) {
                        bdCand = holder.getBeanDefinition();
                    }
                    if (!ConfigurationClassUtils.checkConfigurationClassCandidate(bdCand, this.metadataReaderFactory)) continue;
                    this.parse(bdCand.getBeanClassName(), holder.getBeanName());
                }
            }
        }
        this.processImports(configClass, sourceClass, this.getImports(sourceClass), true);
        AnnotationAttributes annotationAttributes = AnnotationConfigUtils.attributesFor((AnnotatedTypeMetadata)sourceClass.getMetadata(), ImportResource.class);
        if (annotationAttributes != null) {
            String[] resources = annotationAttributes.getStringArray("locations");
            Class readerClass = annotationAttributes.getClass("reader");
            for (String resource : resources) {
                String resolvedResource = this.environment.resolveRequiredPlaceholders(resource);
                configClass.addImportedResource(resolvedResource, readerClass);
            }
        }
        Set<MethodMetadata> beanMethods = this.retrieveBeanMethodMetadata(sourceClass);
        for (MethodMetadata methodMetadata : beanMethods) {
            configClass.addBeanMethod(new BeanMethod(methodMetadata, configClass));
        }
        this.processInterfaces(configClass, sourceClass);
        if (sourceClass.getMetadata().hasSuperClass() && (superclass = sourceClass.getMetadata().getSuperClassName()) != null && !superclass.startsWith("java") && !this.knownSuperclasses.containsKey(superclass)) {
            this.knownSuperclasses.put(superclass, configClass);
            return sourceClass.getSuperClass();
        }
        return null;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void processMemberClasses(ConfigurationClass configClass, SourceClass sourceClass) throws IOException {
        Collection<SourceClass> memberClasses = sourceClass.getMemberClasses();
        if (!memberClasses.isEmpty()) {
            ArrayList<SourceClass> candidates = new ArrayList<SourceClass>(memberClasses.size());
            for (SourceClass memberClass : memberClasses) {
                if (!ConfigurationClassUtils.isConfigurationCandidate(memberClass.getMetadata()) || memberClass.getMetadata().getClassName().equals(configClass.getMetadata().getClassName())) continue;
                candidates.add(memberClass);
            }
            OrderComparator.sort(candidates);
            for (SourceClass candidate : candidates) {
                if (this.importStack.contains(configClass)) {
                    this.problemReporter.error(new CircularImportProblem(configClass, this.importStack));
                    continue;
                }
                this.importStack.push(configClass);
                try {
                    this.processConfigurationClass(candidate.asConfigClass(configClass));
                }
                finally {
                    this.importStack.pop();
                }
            }
        }
    }

    private void processInterfaces(ConfigurationClass configClass, SourceClass sourceClass) throws IOException {
        for (SourceClass ifc : sourceClass.getInterfaces()) {
            Set<MethodMetadata> beanMethods = this.retrieveBeanMethodMetadata(ifc);
            for (MethodMetadata methodMetadata : beanMethods) {
                if (methodMetadata.isAbstract()) continue;
                configClass.addBeanMethod(new BeanMethod(methodMetadata, configClass));
            }
            this.processInterfaces(configClass, ifc);
        }
    }

    private Set<MethodMetadata> retrieveBeanMethodMetadata(SourceClass sourceClass) {
        AnnotationMetadata original = sourceClass.getMetadata();
        Set<MethodMetadata> beanMethods = original.getAnnotatedMethods(Bean.class.getName());
        if (beanMethods.size() > 1 && original instanceof StandardAnnotationMetadata) {
            try {
                AnnotationMetadata asm = this.metadataReaderFactory.getMetadataReader(original.getClassName()).getAnnotationMetadata();
                Set<MethodMetadata> asmMethods = asm.getAnnotatedMethods(Bean.class.getName());
                if (asmMethods.size() >= beanMethods.size()) {
                    LinkedHashSet<MethodMetadata> selectedMethods = new LinkedHashSet<MethodMetadata>(asmMethods.size());
                    block2: for (MethodMetadata asmMethod : asmMethods) {
                        for (MethodMetadata beanMethod : beanMethods) {
                            if (!beanMethod.getMethodName().equals(asmMethod.getMethodName())) continue;
                            selectedMethods.add(beanMethod);
                            continue block2;
                        }
                    }
                    if (selectedMethods.size() == beanMethods.size()) {
                        beanMethods = selectedMethods;
                    }
                }
            }
            catch (IOException ex) {
                this.logger.debug("Failed to read class file via ASM for determining @Bean method order", ex);
            }
        }
        return beanMethods;
    }

    private void processPropertySource(AnnotationAttributes propertySource) throws IOException {
        String[] locations;
        String encoding;
        String name = propertySource.getString("name");
        if (!StringUtils.hasLength(name)) {
            name = null;
        }
        if (!StringUtils.hasLength(encoding = propertySource.getString("encoding"))) {
            encoding = null;
        }
        Assert.isTrue((locations = propertySource.getStringArray("value")).length > 0, "At least one @PropertySource(value) location is required");
        boolean ignoreResourceNotFound = propertySource.getBoolean("ignoreResourceNotFound");
        Class factoryClass = propertySource.getClass("factory");
        PropertySourceFactory factory = factoryClass == PropertySourceFactory.class ? DEFAULT_PROPERTY_SOURCE_FACTORY : (PropertySourceFactory)BeanUtils.instantiateClass(factoryClass);
        for (String location : locations) {
            try {
                String resolvedLocation = this.environment.resolveRequiredPlaceholders(location);
                Resource resource = this.resourceLoader.getResource(resolvedLocation);
                this.addPropertySource(factory.createPropertySource(name, new EncodedResource(resource, encoding)));
            }
            catch (FileNotFoundException | IllegalArgumentException | UnknownHostException ex) {
                if (ignoreResourceNotFound) {
                    if (!this.logger.isInfoEnabled()) continue;
                    this.logger.info("Properties location [" + location + "] not resolvable: " + ex.getMessage());
                    continue;
                }
                throw ex;
            }
        }
    }

    private void addPropertySource(org.springframework.core.env.PropertySource<?> propertySource) {
        ResourcePropertySource existing;
        String name = propertySource.getName();
        MutablePropertySources propertySources = ((ConfigurableEnvironment)this.environment).getPropertySources();
        if (this.propertySourceNames.contains(name) && (existing = propertySources.get(name)) != null) {
            ResourcePropertySource newSource;
            ResourcePropertySource resourcePropertySource = newSource = propertySource instanceof ResourcePropertySource ? ((ResourcePropertySource)propertySource).withResourceName() : propertySource;
            if (existing instanceof CompositePropertySource) {
                ((CompositePropertySource)((Object)existing)).addFirstPropertySource(newSource);
            } else {
                if (existing instanceof ResourcePropertySource) {
                    existing = ((ResourcePropertySource)existing).withResourceName();
                }
                CompositePropertySource composite = new CompositePropertySource(name);
                composite.addPropertySource(newSource);
                composite.addPropertySource(existing);
                propertySources.replace(name, composite);
            }
            return;
        }
        if (this.propertySourceNames.isEmpty()) {
            propertySources.addLast(propertySource);
        } else {
            String firstProcessed = this.propertySourceNames.get(this.propertySourceNames.size() - 1);
            propertySources.addBefore(firstProcessed, propertySource);
        }
        this.propertySourceNames.add(name);
    }

    private Set<SourceClass> getImports(SourceClass sourceClass) throws IOException {
        LinkedHashSet<SourceClass> imports = new LinkedHashSet<SourceClass>();
        LinkedHashSet<SourceClass> visited = new LinkedHashSet<SourceClass>();
        this.collectImports(sourceClass, imports, visited);
        return imports;
    }

    private void collectImports(SourceClass sourceClass, Set<SourceClass> imports, Set<SourceClass> visited) throws IOException {
        if (visited.add(sourceClass)) {
            for (SourceClass annotation : sourceClass.getAnnotations()) {
                String annName = annotation.getMetadata().getClassName();
                if (annName.startsWith("java") || annName.equals(Import.class.getName())) continue;
                this.collectImports(annotation, imports, visited);
            }
            imports.addAll(sourceClass.getAnnotationAttributes(Import.class.getName(), "value"));
        }
    }

    private void processDeferredImportSelectors() {
        List<DeferredImportSelectorHolder> deferredImports = this.deferredImportSelectors;
        this.deferredImportSelectors = null;
        if (deferredImports == null) {
            return;
        }
        deferredImports.sort(DEFERRED_IMPORT_COMPARATOR);
        LinkedHashMap<Object, DeferredImportSelectorGrouping> groupings = new LinkedHashMap<Object, DeferredImportSelectorGrouping>();
        HashMap<AnnotationMetadata, ConfigurationClass> configurationClasses = new HashMap<AnnotationMetadata, ConfigurationClass>();
        for (DeferredImportSelectorHolder deferredImport : deferredImports) {
            Class<? extends DeferredImportSelector.Group> group = deferredImport.getImportSelector().getImportGroup();
            DeferredImportSelectorGrouping grouping = groupings.computeIfAbsent(group != null ? group : deferredImport, key -> new DeferredImportSelectorGrouping(this.createGroup(group)));
            grouping.add(deferredImport);
            configurationClasses.put(deferredImport.getConfigurationClass().getMetadata(), deferredImport.getConfigurationClass());
        }
        for (DeferredImportSelectorGrouping grouping : groupings.values()) {
            grouping.getImports().forEach(entry -> {
                ConfigurationClass configurationClass = (ConfigurationClass)configurationClasses.get(entry.getMetadata());
                try {
                    this.processImports(configurationClass, this.asSourceClass(configurationClass), this.asSourceClasses(entry.getImportClassName()), false);
                }
                catch (BeanDefinitionStoreException ex) {
                    throw ex;
                }
                catch (Throwable ex) {
                    throw new BeanDefinitionStoreException("Failed to process import candidates for configuration class [" + configurationClass.getMetadata().getClassName() + "]", ex);
                }
            });
        }
    }

    private DeferredImportSelector.Group createGroup(@Nullable Class<? extends DeferredImportSelector.Group> type) {
        Class<? extends DeferredImportSelector.Group> effectiveType = type != null ? type : DefaultDeferredImportSelectorGroup.class;
        DeferredImportSelector.Group group = BeanUtils.instantiateClass(effectiveType);
        ParserStrategyUtils.invokeAwareMethods(group, this.environment, this.resourceLoader, this.registry);
        return group;
    }

    private void processImports(ConfigurationClass configClass, SourceClass currentSourceClass, Collection<SourceClass> importCandidates, boolean checkForCircularImports) {
        if (importCandidates.isEmpty()) {
            return;
        }
        if (checkForCircularImports && this.isChainedImportOnStack(configClass)) {
            this.problemReporter.error(new CircularImportProblem(configClass, this.importStack));
        } else {
            this.importStack.push(configClass);
            try {
                for (SourceClass candidate : importCandidates) {
                    Class<?> candidateClass;
                    if (candidate.isAssignable(ImportSelector.class)) {
                        candidateClass = candidate.loadClass();
                        ImportSelector selector = BeanUtils.instantiateClass(candidateClass, ImportSelector.class);
                        ParserStrategyUtils.invokeAwareMethods(selector, this.environment, this.resourceLoader, this.registry);
                        if (this.deferredImportSelectors != null && selector instanceof DeferredImportSelector) {
                            this.deferredImportSelectors.add(new DeferredImportSelectorHolder(configClass, (DeferredImportSelector)selector));
                            continue;
                        }
                        String[] importClassNames = selector.selectImports(currentSourceClass.getMetadata());
                        Collection<SourceClass> importSourceClasses = this.asSourceClasses(importClassNames);
                        this.processImports(configClass, currentSourceClass, importSourceClasses, false);
                        continue;
                    }
                    if (candidate.isAssignable(ImportBeanDefinitionRegistrar.class)) {
                        candidateClass = candidate.loadClass();
                        ImportBeanDefinitionRegistrar registrar = BeanUtils.instantiateClass(candidateClass, ImportBeanDefinitionRegistrar.class);
                        ParserStrategyUtils.invokeAwareMethods(registrar, this.environment, this.resourceLoader, this.registry);
                        configClass.addImportBeanDefinitionRegistrar(registrar, currentSourceClass.getMetadata());
                        continue;
                    }
                    this.importStack.registerImport(currentSourceClass.getMetadata(), candidate.getMetadata().getClassName());
                    this.processConfigurationClass(candidate.asConfigClass(configClass));
                }
            }
            catch (BeanDefinitionStoreException ex) {
                throw ex;
            }
            catch (Throwable ex) {
                throw new BeanDefinitionStoreException("Failed to process import candidates for configuration class [" + configClass.getMetadata().getClassName() + "]", ex);
            }
            finally {
                this.importStack.pop();
            }
        }
    }

    private boolean isChainedImportOnStack(ConfigurationClass configClass) {
        if (this.importStack.contains(configClass)) {
            String configClassName = configClass.getMetadata().getClassName();
            AnnotationMetadata importingClass = this.importStack.getImportingClassFor(configClassName);
            while (importingClass != null) {
                if (configClassName.equals(importingClass.getClassName())) {
                    return true;
                }
                importingClass = this.importStack.getImportingClassFor(importingClass.getClassName());
            }
        }
        return false;
    }

    ImportRegistry getImportRegistry() {
        return this.importStack;
    }

    private SourceClass asSourceClass(ConfigurationClass configurationClass) throws IOException {
        AnnotationMetadata metadata = configurationClass.getMetadata();
        if (metadata instanceof StandardAnnotationMetadata) {
            return this.asSourceClass(((StandardAnnotationMetadata)metadata).getIntrospectedClass());
        }
        return this.asSourceClass(metadata.getClassName());
    }

    SourceClass asSourceClass(@Nullable Class<?> classType) throws IOException {
        if (classType == null) {
            return new SourceClass(Object.class);
        }
        try {
            for (Annotation ann : classType.getAnnotations()) {
                AnnotationUtils.validateAnnotation(ann);
            }
            return new SourceClass(classType);
        }
        catch (Throwable ex) {
            return this.asSourceClass(classType.getName());
        }
    }

    private Collection<SourceClass> asSourceClasses(String ... classNames) throws IOException {
        ArrayList<SourceClass> annotatedClasses = new ArrayList<SourceClass>(classNames.length);
        for (String className : classNames) {
            annotatedClasses.add(this.asSourceClass(className));
        }
        return annotatedClasses;
    }

    SourceClass asSourceClass(@Nullable String className) throws IOException {
        if (className == null) {
            return new SourceClass(Object.class);
        }
        if (className.startsWith("java")) {
            try {
                return new SourceClass(ClassUtils.forName(className, this.resourceLoader.getClassLoader()));
            }
            catch (ClassNotFoundException ex) {
                throw new NestedIOException("Failed to load class [" + className + "]", ex);
            }
        }
        return new SourceClass(this.metadataReaderFactory.getMetadataReader(className));
    }

    private static class CircularImportProblem
    extends Problem {
        public CircularImportProblem(ConfigurationClass attemptedImport, Deque<ConfigurationClass> importStack) {
            super(String.format("A circular @Import has been detected: Illegal attempt by @Configuration class '%s' to import class '%s' as '%s' is already present in the current import stack %s", importStack.element().getSimpleName(), attemptedImport.getSimpleName(), attemptedImport.getSimpleName(), importStack), new Location(importStack.element().getResource(), attemptedImport.getMetadata()));
        }
    }

    private class SourceClass
    implements Ordered {
        private final Object source;
        private final AnnotationMetadata metadata;

        public SourceClass(Object source) {
            this.source = source;
            this.metadata = source instanceof Class ? new StandardAnnotationMetadata((Class)source, true) : ((MetadataReader)source).getAnnotationMetadata();
        }

        public final AnnotationMetadata getMetadata() {
            return this.metadata;
        }

        @Override
        public int getOrder() {
            Integer order = ConfigurationClassUtils.getOrder(this.metadata);
            return order != null ? order : Integer.MAX_VALUE;
        }

        public Class<?> loadClass() throws ClassNotFoundException {
            if (this.source instanceof Class) {
                return (Class)this.source;
            }
            String className = ((MetadataReader)this.source).getClassMetadata().getClassName();
            return ClassUtils.forName(className, ConfigurationClassParser.this.resourceLoader.getClassLoader());
        }

        public boolean isAssignable(Class<?> clazz) throws IOException {
            if (this.source instanceof Class) {
                return clazz.isAssignableFrom((Class)this.source);
            }
            return new AssignableTypeFilter(clazz).match((MetadataReader)this.source, ConfigurationClassParser.this.metadataReaderFactory);
        }

        public ConfigurationClass asConfigClass(ConfigurationClass importedBy) throws IOException {
            if (this.source instanceof Class) {
                return new ConfigurationClass((Class)this.source, importedBy);
            }
            return new ConfigurationClass((MetadataReader)this.source, importedBy);
        }

        public Collection<SourceClass> getMemberClasses() throws IOException {
            Object sourceToProcess = this.source;
            if (sourceToProcess instanceof Class) {
                Class sourceClass = (Class)sourceToProcess;
                try {
                    Class<?>[] declaredClasses = sourceClass.getDeclaredClasses();
                    ArrayList<SourceClass> members = new ArrayList<SourceClass>(declaredClasses.length);
                    for (Class<?> declaredClass : declaredClasses) {
                        members.add(ConfigurationClassParser.this.asSourceClass(declaredClass));
                    }
                    return members;
                }
                catch (NoClassDefFoundError err) {
                    sourceToProcess = ConfigurationClassParser.this.metadataReaderFactory.getMetadataReader(sourceClass.getName());
                }
            }
            MetadataReader sourceReader = (MetadataReader)sourceToProcess;
            String[] memberClassNames = sourceReader.getClassMetadata().getMemberClassNames();
            ArrayList<SourceClass> members = new ArrayList<SourceClass>(memberClassNames.length);
            for (String memberClassName : memberClassNames) {
                try {
                    members.add(ConfigurationClassParser.this.asSourceClass(memberClassName));
                }
                catch (IOException ex) {
                    if (!ConfigurationClassParser.this.logger.isDebugEnabled()) continue;
                    ConfigurationClassParser.this.logger.debug("Failed to resolve member class [" + memberClassName + "] - not considering it as a configuration class candidate");
                }
            }
            return members;
        }

        public SourceClass getSuperClass() throws IOException {
            if (this.source instanceof Class) {
                return ConfigurationClassParser.this.asSourceClass(((Class)this.source).getSuperclass());
            }
            return ConfigurationClassParser.this.asSourceClass(((MetadataReader)this.source).getClassMetadata().getSuperClassName());
        }

        public Set<SourceClass> getInterfaces() throws IOException {
            LinkedHashSet<SourceClass> result = new LinkedHashSet<SourceClass>();
            if (this.source instanceof Class) {
                Class sourceClass = (Class)this.source;
                for (Class<?> ifcClass : sourceClass.getInterfaces()) {
                    result.add(ConfigurationClassParser.this.asSourceClass(ifcClass));
                }
            } else {
                for (String className : this.metadata.getInterfaceNames()) {
                    result.add(ConfigurationClassParser.this.asSourceClass(className));
                }
            }
            return result;
        }

        public Set<SourceClass> getAnnotations() throws IOException {
            LinkedHashSet<SourceClass> result = new LinkedHashSet<SourceClass>();
            for (String className : this.metadata.getAnnotationTypes()) {
                try {
                    result.add(this.getRelated(className));
                }
                catch (Throwable throwable) {}
            }
            return result;
        }

        public Collection<SourceClass> getAnnotationAttributes(String annType, String attribute) throws IOException {
            Map<String, Object> annotationAttributes = this.metadata.getAnnotationAttributes(annType, true);
            if (annotationAttributes == null || !annotationAttributes.containsKey(attribute)) {
                return Collections.emptySet();
            }
            String[] classNames = (String[])annotationAttributes.get(attribute);
            LinkedHashSet<SourceClass> result = new LinkedHashSet<SourceClass>();
            for (String className : classNames) {
                result.add(this.getRelated(className));
            }
            return result;
        }

        private SourceClass getRelated(String className) throws IOException {
            if (this.source instanceof Class) {
                try {
                    Class<?> clazz = ClassUtils.forName(className, ((Class)this.source).getClassLoader());
                    return ConfigurationClassParser.this.asSourceClass(clazz);
                }
                catch (ClassNotFoundException ex) {
                    if (className.startsWith("java")) {
                        throw new NestedIOException("Failed to load class [" + className + "]", ex);
                    }
                    return new SourceClass(ConfigurationClassParser.this.metadataReaderFactory.getMetadataReader(className));
                }
            }
            return ConfigurationClassParser.this.asSourceClass(className);
        }

        public boolean equals(Object other) {
            return this == other || other instanceof SourceClass && this.metadata.getClassName().equals(((SourceClass)other).metadata.getClassName());
        }

        public int hashCode() {
            return this.metadata.getClassName().hashCode();
        }

        public String toString() {
            return this.metadata.getClassName();
        }
    }

    private static class DefaultDeferredImportSelectorGroup
    implements DeferredImportSelector.Group {
        private final List<DeferredImportSelector.Group.Entry> imports = new ArrayList<DeferredImportSelector.Group.Entry>();

        private DefaultDeferredImportSelectorGroup() {
        }

        @Override
        public void process(AnnotationMetadata metadata, DeferredImportSelector selector) {
            for (String importClassName : selector.selectImports(metadata)) {
                this.imports.add(new DeferredImportSelector.Group.Entry(metadata, importClassName));
            }
        }

        @Override
        public Iterable<DeferredImportSelector.Group.Entry> selectImports() {
            return this.imports;
        }
    }

    private static class DeferredImportSelectorGrouping {
        private final DeferredImportSelector.Group group;
        private final List<DeferredImportSelectorHolder> deferredImports = new ArrayList<DeferredImportSelectorHolder>();

        DeferredImportSelectorGrouping(DeferredImportSelector.Group group) {
            this.group = group;
        }

        public void add(DeferredImportSelectorHolder deferredImport) {
            this.deferredImports.add(deferredImport);
        }

        public Iterable<DeferredImportSelector.Group.Entry> getImports() {
            for (DeferredImportSelectorHolder deferredImport : this.deferredImports) {
                this.group.process(deferredImport.getConfigurationClass().getMetadata(), deferredImport.getImportSelector());
            }
            return this.group.selectImports();
        }
    }

    private static class DeferredImportSelectorHolder {
        private final ConfigurationClass configurationClass;
        private final DeferredImportSelector importSelector;

        public DeferredImportSelectorHolder(ConfigurationClass configClass, DeferredImportSelector selector) {
            this.configurationClass = configClass;
            this.importSelector = selector;
        }

        public ConfigurationClass getConfigurationClass() {
            return this.configurationClass;
        }

        public DeferredImportSelector getImportSelector() {
            return this.importSelector;
        }
    }

    private static class ImportStack
    extends ArrayDeque<ConfigurationClass>
    implements ImportRegistry {
        private final MultiValueMap<String, AnnotationMetadata> imports = new LinkedMultiValueMap<String, AnnotationMetadata>();

        private ImportStack() {
        }

        public void registerImport(AnnotationMetadata importingClass, String importedClass) {
            this.imports.add(importedClass, importingClass);
        }

        @Override
        @Nullable
        public AnnotationMetadata getImportingClassFor(String importedClass) {
            return (AnnotationMetadata)CollectionUtils.lastElement((List)this.imports.get(importedClass));
        }

        @Override
        public void removeImportingClass(String importingClass) {
            block0: for (List list : this.imports.values()) {
                Iterator iterator = list.iterator();
                while (iterator.hasNext()) {
                    if (!((AnnotationMetadata)iterator.next()).getClassName().equals(importingClass)) continue;
                    iterator.remove();
                    continue block0;
                }
            }
        }

        @Override
        public String toString() {
            StringBuilder builder = new StringBuilder("[");
            Iterator iterator = this.iterator();
            while (iterator.hasNext()) {
                builder.append(((ConfigurationClass)iterator.next()).getSimpleName());
                if (!iterator.hasNext()) continue;
                builder.append("->");
            }
            return builder.append(']').toString();
        }
    }
}


/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 *  org.springframework.beans.factory.BeanDefinitionStoreException
 *  org.springframework.beans.factory.annotation.AnnotatedBeanDefinition
 *  org.springframework.beans.factory.annotation.Lookup
 *  org.springframework.beans.factory.config.BeanDefinition
 *  org.springframework.beans.factory.support.BeanDefinitionRegistry
 *  org.springframework.core.annotation.AnnotationUtils
 *  org.springframework.core.env.Environment
 *  org.springframework.core.env.EnvironmentCapable
 *  org.springframework.core.env.StandardEnvironment
 *  org.springframework.core.io.Resource
 *  org.springframework.core.io.ResourceLoader
 *  org.springframework.core.io.support.PathMatchingResourcePatternResolver
 *  org.springframework.core.io.support.ResourcePatternResolver
 *  org.springframework.core.io.support.ResourcePatternUtils
 *  org.springframework.core.type.AnnotatedTypeMetadata
 *  org.springframework.core.type.AnnotationMetadata
 *  org.springframework.core.type.classreading.CachingMetadataReaderFactory
 *  org.springframework.core.type.classreading.MetadataReader
 *  org.springframework.core.type.classreading.MetadataReaderFactory
 *  org.springframework.core.type.filter.AnnotationTypeFilter
 *  org.springframework.core.type.filter.AssignableTypeFilter
 *  org.springframework.core.type.filter.TypeFilter
 *  org.springframework.lang.Nullable
 *  org.springframework.util.Assert
 *  org.springframework.util.ClassUtils
 */
package org.springframework.context.annotation;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.BeanDefinitionStoreException;
import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.beans.factory.annotation.Lookup;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.context.annotation.ConditionEvaluator;
import org.springframework.context.annotation.ScannedGenericBeanDefinition;
import org.springframework.context.index.CandidateComponentsIndex;
import org.springframework.context.index.CandidateComponentsIndexLoader;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.core.env.Environment;
import org.springframework.core.env.EnvironmentCapable;
import org.springframework.core.env.StandardEnvironment;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternUtils;
import org.springframework.core.type.AnnotatedTypeMetadata;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.core.type.classreading.CachingMetadataReaderFactory;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.core.type.classreading.MetadataReaderFactory;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.core.type.filter.AssignableTypeFilter;
import org.springframework.core.type.filter.TypeFilter;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Indexed;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;

public class ClassPathScanningCandidateComponentProvider
implements EnvironmentCapable,
ResourceLoaderAware {
    static final String DEFAULT_RESOURCE_PATTERN = "**/*.class";
    protected final Log logger = LogFactory.getLog(this.getClass());
    private String resourcePattern = "**/*.class";
    private final List<TypeFilter> includeFilters = new ArrayList<TypeFilter>();
    private final List<TypeFilter> excludeFilters = new ArrayList<TypeFilter>();
    @Nullable
    private Environment environment;
    @Nullable
    private ConditionEvaluator conditionEvaluator;
    @Nullable
    private ResourcePatternResolver resourcePatternResolver;
    @Nullable
    private MetadataReaderFactory metadataReaderFactory;
    @Nullable
    private CandidateComponentsIndex componentsIndex;

    protected ClassPathScanningCandidateComponentProvider() {
    }

    public ClassPathScanningCandidateComponentProvider(boolean useDefaultFilters) {
        this(useDefaultFilters, (Environment)new StandardEnvironment());
    }

    public ClassPathScanningCandidateComponentProvider(boolean useDefaultFilters, Environment environment2) {
        if (useDefaultFilters) {
            this.registerDefaultFilters();
        }
        this.setEnvironment(environment2);
        this.setResourceLoader(null);
    }

    public void setResourcePattern(String resourcePattern) {
        Assert.notNull((Object)resourcePattern, (String)"'resourcePattern' must not be null");
        this.resourcePattern = resourcePattern;
    }

    public void addIncludeFilter(TypeFilter includeFilter) {
        this.includeFilters.add(includeFilter);
    }

    public void addExcludeFilter(TypeFilter excludeFilter) {
        this.excludeFilters.add(0, excludeFilter);
    }

    public void resetFilters(boolean useDefaultFilters) {
        this.includeFilters.clear();
        this.excludeFilters.clear();
        if (useDefaultFilters) {
            this.registerDefaultFilters();
        }
    }

    protected void registerDefaultFilters() {
        this.includeFilters.add((TypeFilter)new AnnotationTypeFilter(Component.class));
        ClassLoader cl = ClassPathScanningCandidateComponentProvider.class.getClassLoader();
        try {
            this.includeFilters.add((TypeFilter)new AnnotationTypeFilter(ClassUtils.forName((String)"javax.annotation.ManagedBean", (ClassLoader)cl), false));
            this.logger.trace((Object)"JSR-250 'javax.annotation.ManagedBean' found and supported for component scanning");
        }
        catch (ClassNotFoundException classNotFoundException) {
            // empty catch block
        }
        try {
            this.includeFilters.add((TypeFilter)new AnnotationTypeFilter(ClassUtils.forName((String)"javax.inject.Named", (ClassLoader)cl), false));
            this.logger.trace((Object)"JSR-330 'javax.inject.Named' annotation found and supported for component scanning");
        }
        catch (ClassNotFoundException classNotFoundException) {
            // empty catch block
        }
    }

    public void setEnvironment(Environment environment2) {
        Assert.notNull((Object)environment2, (String)"Environment must not be null");
        this.environment = environment2;
        this.conditionEvaluator = null;
    }

    public final Environment getEnvironment() {
        if (this.environment == null) {
            this.environment = new StandardEnvironment();
        }
        return this.environment;
    }

    @Nullable
    protected BeanDefinitionRegistry getRegistry() {
        return null;
    }

    @Override
    public void setResourceLoader(@Nullable ResourceLoader resourceLoader) {
        this.resourcePatternResolver = ResourcePatternUtils.getResourcePatternResolver((ResourceLoader)resourceLoader);
        this.metadataReaderFactory = new CachingMetadataReaderFactory(resourceLoader);
        this.componentsIndex = CandidateComponentsIndexLoader.loadIndex(this.resourcePatternResolver.getClassLoader());
    }

    public final ResourceLoader getResourceLoader() {
        return this.getResourcePatternResolver();
    }

    private ResourcePatternResolver getResourcePatternResolver() {
        if (this.resourcePatternResolver == null) {
            this.resourcePatternResolver = new PathMatchingResourcePatternResolver();
        }
        return this.resourcePatternResolver;
    }

    public void setMetadataReaderFactory(MetadataReaderFactory metadataReaderFactory) {
        this.metadataReaderFactory = metadataReaderFactory;
    }

    public final MetadataReaderFactory getMetadataReaderFactory() {
        if (this.metadataReaderFactory == null) {
            this.metadataReaderFactory = new CachingMetadataReaderFactory();
        }
        return this.metadataReaderFactory;
    }

    public Set<BeanDefinition> findCandidateComponents(String basePackage) {
        if (this.componentsIndex != null && this.indexSupportsIncludeFilters()) {
            return this.addCandidateComponentsFromIndex(this.componentsIndex, basePackage);
        }
        return this.scanCandidateComponents(basePackage);
    }

    private boolean indexSupportsIncludeFilters() {
        for (TypeFilter includeFilter : this.includeFilters) {
            if (this.indexSupportsIncludeFilter(includeFilter)) continue;
            return false;
        }
        return true;
    }

    private boolean indexSupportsIncludeFilter(TypeFilter filter) {
        if (filter instanceof AnnotationTypeFilter) {
            Class annotation = ((AnnotationTypeFilter)filter).getAnnotationType();
            return AnnotationUtils.isAnnotationDeclaredLocally(Indexed.class, (Class)annotation) || annotation.getName().startsWith("javax.");
        }
        if (filter instanceof AssignableTypeFilter) {
            Class target = ((AssignableTypeFilter)filter).getTargetType();
            return AnnotationUtils.isAnnotationDeclaredLocally(Indexed.class, (Class)target);
        }
        return false;
    }

    @Nullable
    private String extractStereotype(TypeFilter filter) {
        if (filter instanceof AnnotationTypeFilter) {
            return ((AnnotationTypeFilter)filter).getAnnotationType().getName();
        }
        if (filter instanceof AssignableTypeFilter) {
            return ((AssignableTypeFilter)filter).getTargetType().getName();
        }
        return null;
    }

    private Set<BeanDefinition> addCandidateComponentsFromIndex(CandidateComponentsIndex index, String basePackage) {
        LinkedHashSet<BeanDefinition> candidates = new LinkedHashSet<BeanDefinition>();
        try {
            HashSet<String> types = new HashSet<String>();
            for (TypeFilter filter : this.includeFilters) {
                String stereotype = this.extractStereotype(filter);
                if (stereotype == null) {
                    throw new IllegalArgumentException("Failed to extract stereotype from " + filter);
                }
                types.addAll(index.getCandidateTypes(basePackage, stereotype));
            }
            boolean traceEnabled = this.logger.isTraceEnabled();
            boolean debugEnabled = this.logger.isDebugEnabled();
            for (String type : types) {
                MetadataReader metadataReader = this.getMetadataReaderFactory().getMetadataReader(type);
                if (this.isCandidateComponent(metadataReader)) {
                    ScannedGenericBeanDefinition sbd = new ScannedGenericBeanDefinition(metadataReader);
                    sbd.setSource(metadataReader.getResource());
                    if (this.isCandidateComponent(sbd)) {
                        if (debugEnabled) {
                            this.logger.debug((Object)("Using candidate component class from index: " + type));
                        }
                        candidates.add((BeanDefinition)sbd);
                        continue;
                    }
                    if (!debugEnabled) continue;
                    this.logger.debug((Object)("Ignored because not a concrete top-level class: " + type));
                    continue;
                }
                if (!traceEnabled) continue;
                this.logger.trace((Object)("Ignored because matching an exclude filter: " + type));
            }
        }
        catch (IOException ex) {
            throw new BeanDefinitionStoreException("I/O failure during classpath scanning", (Throwable)ex);
        }
        return candidates;
    }

    private Set<BeanDefinition> scanCandidateComponents(String basePackage) {
        LinkedHashSet<BeanDefinition> candidates = new LinkedHashSet<BeanDefinition>();
        try {
            String packageSearchPath = "classpath*:" + this.resolveBasePackage(basePackage) + '/' + this.resourcePattern;
            Resource[] resources = this.getResourcePatternResolver().getResources(packageSearchPath);
            boolean traceEnabled = this.logger.isTraceEnabled();
            boolean debugEnabled = this.logger.isDebugEnabled();
            for (Resource resource : resources) {
                if (traceEnabled) {
                    this.logger.trace((Object)("Scanning " + resource));
                }
                try {
                    MetadataReader metadataReader = this.getMetadataReaderFactory().getMetadataReader(resource);
                    if (this.isCandidateComponent(metadataReader)) {
                        ScannedGenericBeanDefinition sbd = new ScannedGenericBeanDefinition(metadataReader);
                        sbd.setSource(resource);
                        if (this.isCandidateComponent(sbd)) {
                            if (debugEnabled) {
                                this.logger.debug((Object)("Identified candidate component class: " + resource));
                            }
                            candidates.add((BeanDefinition)sbd);
                            continue;
                        }
                        if (!debugEnabled) continue;
                        this.logger.debug((Object)("Ignored because not a concrete top-level class: " + resource));
                        continue;
                    }
                    if (!traceEnabled) continue;
                    this.logger.trace((Object)("Ignored because not matching any filter: " + resource));
                }
                catch (FileNotFoundException ex) {
                    if (!traceEnabled) continue;
                    this.logger.trace((Object)("Ignored non-readable " + resource + ": " + ex.getMessage()));
                }
                catch (Throwable ex) {
                    throw new BeanDefinitionStoreException("Failed to read candidate component class: " + resource, ex);
                }
            }
        }
        catch (IOException ex) {
            throw new BeanDefinitionStoreException("I/O failure during classpath scanning", (Throwable)ex);
        }
        return candidates;
    }

    protected String resolveBasePackage(String basePackage) {
        return ClassUtils.convertClassNameToResourcePath((String)this.getEnvironment().resolveRequiredPlaceholders(basePackage));
    }

    protected boolean isCandidateComponent(MetadataReader metadataReader) throws IOException {
        for (TypeFilter tf : this.excludeFilters) {
            if (!tf.match(metadataReader, this.getMetadataReaderFactory())) continue;
            return false;
        }
        for (TypeFilter tf : this.includeFilters) {
            if (!tf.match(metadataReader, this.getMetadataReaderFactory())) continue;
            return this.isConditionMatch(metadataReader);
        }
        return false;
    }

    private boolean isConditionMatch(MetadataReader metadataReader) {
        if (this.conditionEvaluator == null) {
            this.conditionEvaluator = new ConditionEvaluator(this.getRegistry(), this.environment, (ResourceLoader)this.resourcePatternResolver);
        }
        return !this.conditionEvaluator.shouldSkip((AnnotatedTypeMetadata)metadataReader.getAnnotationMetadata());
    }

    protected boolean isCandidateComponent(AnnotatedBeanDefinition beanDefinition) {
        AnnotationMetadata metadata = beanDefinition.getMetadata();
        return metadata.isIndependent() && (metadata.isConcrete() || metadata.isAbstract() && metadata.hasAnnotatedMethods(Lookup.class.getName()));
    }

    public void clearCache() {
        if (this.metadataReaderFactory instanceof CachingMetadataReaderFactory) {
            ((CachingMetadataReaderFactory)this.metadataReaderFactory).clearCache();
        }
    }
}


/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.beans.factory.annotation.AnnotatedBeanDefinition
 *  org.springframework.beans.factory.config.BeanDefinition
 *  org.springframework.context.EnvironmentAware
 *  org.springframework.context.ResourceLoaderAware
 *  org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider
 *  org.springframework.core.env.Environment
 *  org.springframework.core.io.ResourceLoader
 *  org.springframework.core.type.filter.AnnotationTypeFilter
 *  org.springframework.core.type.filter.TypeFilter
 *  org.springframework.lang.Nullable
 *  org.springframework.util.ClassUtils
 */
package org.springframework.data.util;

import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.core.type.filter.TypeFilter;
import org.springframework.lang.Nullable;
import org.springframework.util.ClassUtils;

public class AnnotatedTypeScanner
implements ResourceLoaderAware,
EnvironmentAware {
    private final Iterable<Class<? extends Annotation>> annotationTypess;
    private final boolean considerInterfaces;
    @Nullable
    private ResourceLoader resourceLoader;
    @Nullable
    private Environment environment;

    @SafeVarargs
    public AnnotatedTypeScanner(Class<? extends Annotation> ... annotationTypes) {
        this(true, annotationTypes);
    }

    @SafeVarargs
    public AnnotatedTypeScanner(boolean considerInterfaces, Class<? extends Annotation> ... annotationTypes) {
        this.annotationTypess = Arrays.asList(annotationTypes);
        this.considerInterfaces = considerInterfaces;
    }

    public void setResourceLoader(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }

    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }

    public Set<Class<?>> findTypes(String ... basePackages) {
        return this.findTypes(Arrays.asList(basePackages));
    }

    public Set<Class<?>> findTypes(Iterable<String> basePackages) {
        InterfaceAwareScanner provider = new InterfaceAwareScanner(this.considerInterfaces);
        if (this.resourceLoader != null) {
            provider.setResourceLoader(this.resourceLoader);
        }
        if (this.environment != null) {
            provider.setEnvironment(this.environment);
        }
        for (Class<? extends Annotation> annotationType : this.annotationTypess) {
            provider.addIncludeFilter((TypeFilter)new AnnotationTypeFilter(annotationType, true, this.considerInterfaces));
        }
        HashSet types = new HashSet();
        ResourceLoader loader = this.resourceLoader;
        ClassLoader classLoader = loader == null ? null : loader.getClassLoader();
        for (String basePackage : basePackages) {
            for (BeanDefinition definition : provider.findCandidateComponents(basePackage)) {
                String beanClassName = definition.getBeanClassName();
                if (beanClassName == null) {
                    throw new IllegalStateException(String.format("Unable to obtain bean class name from bean definition %s!", definition));
                }
                try {
                    types.add(ClassUtils.forName((String)beanClassName, (ClassLoader)classLoader));
                }
                catch (ClassNotFoundException o_O) {
                    throw new IllegalStateException(o_O);
                }
            }
        }
        return types;
    }

    private static class InterfaceAwareScanner
    extends ClassPathScanningCandidateComponentProvider {
        private final boolean considerInterfaces;

        public InterfaceAwareScanner(boolean considerInterfaces) {
            super(false);
            this.considerInterfaces = considerInterfaces;
        }

        protected boolean isCandidateComponent(AnnotatedBeanDefinition beanDefinition) {
            return super.isCandidateComponent(beanDefinition) || this.considerInterfaces && beanDefinition.getMetadata().isInterface();
        }
    }
}


/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.context.ResourceLoaderAware
 *  org.springframework.context.annotation.Import
 *  org.springframework.context.annotation.ImportSelector
 *  org.springframework.core.io.ResourceLoader
 *  org.springframework.core.io.support.SpringFactoriesLoader
 *  org.springframework.core.type.AnnotationMetadata
 *  org.springframework.util.ClassUtils
 */
package org.springframework.data.web.config;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.ImportSelector;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.io.support.SpringFactoriesLoader;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.data.querydsl.QuerydslUtils;
import org.springframework.data.web.config.HateoasAwareSpringDataWebConfiguration;
import org.springframework.data.web.config.ProjectingArgumentResolverRegistrar;
import org.springframework.data.web.config.QuerydslWebConfiguration;
import org.springframework.data.web.config.SpringDataJacksonModules;
import org.springframework.data.web.config.SpringDataWebConfiguration;
import org.springframework.util.ClassUtils;

@Retention(value=RetentionPolicy.RUNTIME)
@Target(value={ElementType.TYPE, ElementType.ANNOTATION_TYPE})
@Inherited
@Import(value={SpringDataWebConfigurationImportSelector.class, QuerydslActivator.class})
public @interface EnableSpringDataWebSupport {

    public static class QuerydslActivator
    implements ImportSelector {
        public String[] selectImports(AnnotationMetadata importingClassMetadata) {
            String[] stringArray;
            if (QuerydslUtils.QUERY_DSL_PRESENT) {
                String[] stringArray2 = new String[1];
                stringArray = stringArray2;
                stringArray2[0] = QuerydslWebConfiguration.class.getName();
            } else {
                stringArray = new String[]{};
            }
            return stringArray;
        }
    }

    public static class SpringDataWebConfigurationImportSelector
    implements ImportSelector,
    ResourceLoaderAware {
        private Optional<ClassLoader> resourceLoader = Optional.empty();

        public void setResourceLoader(ResourceLoader resourceLoader) {
            this.resourceLoader = Optional.of(resourceLoader).map(ResourceLoader::getClassLoader);
        }

        public String[] selectImports(AnnotationMetadata importingClassMetadata) {
            ArrayList<String> imports = new ArrayList<String>();
            imports.add(ProjectingArgumentResolverRegistrar.class.getName());
            imports.add(this.resourceLoader.filter(it -> ClassUtils.isPresent((String)"org.springframework.hateoas.Link", (ClassLoader)it)).map(it -> HateoasAwareSpringDataWebConfiguration.class.getName()).orElseGet(() -> SpringDataWebConfiguration.class.getName()));
            this.resourceLoader.filter(it -> ClassUtils.isPresent((String)"com.fasterxml.jackson.databind.ObjectMapper", (ClassLoader)it)).map(it -> SpringFactoriesLoader.loadFactoryNames(SpringDataJacksonModules.class, (ClassLoader)it)).ifPresent(it -> imports.addAll((Collection<String>)it));
            return imports.toArray(new String[imports.size()]);
        }
    }
}


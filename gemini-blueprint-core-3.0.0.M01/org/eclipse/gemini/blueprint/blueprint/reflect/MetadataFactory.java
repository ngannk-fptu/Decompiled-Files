/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.beans.factory.config.BeanDefinition
 *  org.springframework.beans.factory.config.ConfigurableListableBeanFactory
 */
package org.eclipse.gemini.blueprint.blueprint.reflect;

import java.util.Collection;
import java.util.Set;
import org.eclipse.gemini.blueprint.blueprint.reflect.BeanDefinitionFactory;
import org.eclipse.gemini.blueprint.blueprint.reflect.ComponentMetadataFactory;
import org.osgi.service.blueprint.reflect.ComponentMetadata;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;

public class MetadataFactory {
    private static final BeanDefinitionFactory springFactory = new BeanDefinitionFactory();
    private static final ComponentMetadataFactory blueprintFactory = new ComponentMetadataFactory();

    public static BeanDefinition buildBeanDefinitionFor(ComponentMetadata metadata) {
        return springFactory.buildBeanDefinitionFor(metadata);
    }

    public static ComponentMetadata buildComponentMetadataFor(String name, BeanDefinition beanDefinition) {
        return ComponentMetadataFactory.buildMetadata(name, beanDefinition);
    }

    public static Collection<ComponentMetadata> buildComponentMetadataFor(ConfigurableListableBeanFactory factory) {
        return ComponentMetadataFactory.buildComponentMetadataFor(factory);
    }

    static Collection<ComponentMetadata> buildNestedComponentMetadataFor(BeanDefinition beanDefinition) {
        return ComponentMetadataFactory.buildNestedMetadata(beanDefinition);
    }

    public static Set<String> filterIds(Set<String> components) {
        return ComponentMetadataFactory.filterIds(components);
    }
}


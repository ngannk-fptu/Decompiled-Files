/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.beans.factory.config.BeanDefinition
 *  org.springframework.beans.factory.config.ConfigurableListableBeanFactory
 *  org.springframework.context.ConfigurableApplicationContext
 *  org.springframework.util.CollectionUtils
 */
package org.eclipse.gemini.blueprint.blueprint.container;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;
import org.eclipse.gemini.blueprint.blueprint.reflect.MetadataFactory;
import org.osgi.service.blueprint.container.BlueprintContainer;
import org.osgi.service.blueprint.container.ComponentDefinitionException;
import org.osgi.service.blueprint.container.NoSuchComponentException;
import org.osgi.service.blueprint.reflect.ComponentMetadata;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.util.CollectionUtils;

public class SpringBlueprintContainer
implements BlueprintContainer {
    private final ConfigurableApplicationContext applicationContext;
    private volatile ConfigurableListableBeanFactory beanFactory;

    public SpringBlueprintContainer(ConfigurableApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    @Override
    public Object getComponentInstance(String name) throws NoSuchComponentException {
        if (this.getBeanFactory().containsBean(name)) {
            try {
                return this.getBeanFactory().getBean(name);
            }
            catch (RuntimeException ex) {
                throw new ComponentDefinitionException("Cannot get component instance " + name, ex);
            }
        }
        throw new NoSuchComponentException(name);
    }

    @Override
    public ComponentMetadata getComponentMetadata(String name) throws NoSuchComponentException {
        if (this.getBeanFactory().containsBeanDefinition(name)) {
            BeanDefinition beanDefinition = this.getBeanFactory().getBeanDefinition(name);
            return MetadataFactory.buildComponentMetadataFor(name, beanDefinition);
        }
        throw new NoSuchComponentException(name);
    }

    @Override
    public Set<String> getComponentIds() {
        String[] names = this.getBeanFactory().getBeanDefinitionNames();
        LinkedHashSet<String> components = new LinkedHashSet<String>(names.length);
        CollectionUtils.mergeArrayIntoCollection((Object)names, components);
        Set<String> filtered = MetadataFactory.filterIds(components);
        return Collections.unmodifiableSet(filtered);
    }

    @Override
    public Collection<?> getMetadata(Class type) {
        return this.getComponentMetadata(type);
    }

    private <T extends ComponentMetadata> Collection<T> getComponentMetadata(Class<T> clazz) {
        Collection<ComponentMetadata> metadatas = this.getComponentMetadataForAllComponents();
        ArrayList<ComponentMetadata> filteredMetadata = new ArrayList<ComponentMetadata>(metadatas.size());
        for (ComponentMetadata metadata : metadatas) {
            if (!clazz.isInstance(metadata)) continue;
            filteredMetadata.add(metadata);
        }
        return Collections.unmodifiableCollection(filteredMetadata);
    }

    private Collection<ComponentMetadata> getComponentMetadataForAllComponents() {
        return MetadataFactory.buildComponentMetadataFor(this.getBeanFactory());
    }

    private ConfigurableListableBeanFactory getBeanFactory() {
        if (this.beanFactory == null) {
            this.beanFactory = this.applicationContext.getBeanFactory();
        }
        return this.beanFactory;
    }
}


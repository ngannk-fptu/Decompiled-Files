/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.beans.BeanMetadataElement
 *  org.springframework.beans.Mergeable
 *  org.springframework.beans.MutablePropertyValues
 *  org.springframework.beans.PropertyValue
 *  org.springframework.beans.factory.config.BeanDefinition
 *  org.springframework.beans.factory.config.BeanDefinitionHolder
 *  org.springframework.beans.factory.config.ConfigurableListableBeanFactory
 *  org.springframework.beans.factory.config.ConstructorArgumentValues
 *  org.springframework.beans.factory.config.ConstructorArgumentValues$ValueHolder
 *  org.springframework.beans.factory.support.AbstractBeanDefinition
 */
package org.eclipse.gemini.blueprint.blueprint.reflect;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;
import org.eclipse.gemini.blueprint.blueprint.reflect.EnvironmentManagerMetadata;
import org.eclipse.gemini.blueprint.blueprint.reflect.MetadataConstants;
import org.eclipse.gemini.blueprint.blueprint.reflect.MetadataFactory;
import org.eclipse.gemini.blueprint.blueprint.reflect.MetadataUtils;
import org.eclipse.gemini.blueprint.blueprint.reflect.SimpleBeanMetadata;
import org.eclipse.gemini.blueprint.blueprint.reflect.SimpleReferenceListMetadata;
import org.eclipse.gemini.blueprint.blueprint.reflect.SimpleReferenceMetadata;
import org.eclipse.gemini.blueprint.blueprint.reflect.SimpleServiceExportComponentMetadata;
import org.eclipse.gemini.blueprint.util.BeanReferenceFactoryBean;
import org.osgi.service.blueprint.reflect.ComponentMetadata;
import org.springframework.beans.BeanMetadataElement;
import org.springframework.beans.Mergeable;
import org.springframework.beans.MutablePropertyValues;
import org.springframework.beans.PropertyValue;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.config.ConstructorArgumentValues;
import org.springframework.beans.factory.support.AbstractBeanDefinition;

class ComponentMetadataFactory
implements MetadataConstants {
    private static final String BEAN_REF_FB_CLASS_NAME = BeanReferenceFactoryBean.class.getName();
    private static final String GENERATED_REF = "org.eclipse.gemini.blueprint.config.reference.generated";
    private static final String PROMOTED_REF = "org.eclipse.gemini.blueprint.config.reference.promoted";
    private static final String REGEX = "\\.org\\.springframework\\.osgi\\.service\\.importer\\.support\\.OsgiService(?:Collection)*ProxyFactoryBean#\\d+#\\d+";
    private static final Pattern PATTERN = Pattern.compile("\\.org\\.springframework\\.osgi\\.service\\.importer\\.support\\.OsgiService(?:Collection)*ProxyFactoryBean#\\d+#\\d+");
    private static final String GENERATED_END = "#generated";
    private static final String GENERATED_START = ".org.eclipse.gemini.blueprint.service.importer.support.OsgiService";
    private static final String GENERATED_MIDDLE = "ProxyFactoryBean#";

    ComponentMetadataFactory() {
    }

    static ComponentMetadata buildMetadata(String name, BeanDefinition beanDefinition) {
        Object metadata = beanDefinition.getAttribute(COMPONENT_METADATA_ATTRIBUTE);
        if (metadata instanceof ComponentMetadata) {
            return (ComponentMetadata)metadata;
        }
        if (name == null) {
            name = (String)beanDefinition.getAttribute("spring.osgi.component.name");
        }
        if (ComponentMetadataFactory.isServiceExporter(beanDefinition)) {
            return new SimpleServiceExportComponentMetadata(name, beanDefinition);
        }
        if (ComponentMetadataFactory.isSingleServiceImporter(beanDefinition)) {
            return new SimpleReferenceMetadata(name, beanDefinition);
        }
        if (ComponentMetadataFactory.isCollectionImporter(beanDefinition)) {
            return new SimpleReferenceListMetadata(name, beanDefinition);
        }
        BeanDefinition original = ComponentMetadataFactory.unwrapImporterReference(beanDefinition);
        if (original != null) {
            return ComponentMetadataFactory.buildMetadata(null, original);
        }
        if (ComponentMetadataFactory.isEnvironmentManager(beanDefinition)) {
            return new EnvironmentManagerMetadata(name);
        }
        return new SimpleBeanMetadata(name, beanDefinition);
    }

    private static boolean isServiceExporter(BeanDefinition beanDefinition) {
        return ComponentMetadataFactory.checkBeanDefinitionClassCompatibility(beanDefinition, EXPORTER_CLASS);
    }

    private static boolean isSingleServiceImporter(BeanDefinition beanDefinition) {
        return ComponentMetadataFactory.checkBeanDefinitionClassCompatibility(beanDefinition, SINGLE_SERVICE_IMPORTER_CLASS);
    }

    private static boolean isCollectionImporter(BeanDefinition beanDefinition) {
        return ComponentMetadataFactory.checkBeanDefinitionClassCompatibility(beanDefinition, MULTI_SERVICE_IMPORTER_CLASS);
    }

    static BeanDefinition unwrapImporterReference(BeanDefinition beanDefinition) {
        AbstractBeanDefinition abd;
        if (BEAN_REF_FB_CLASS_NAME.equals(beanDefinition.getBeanClassName()) && beanDefinition instanceof AbstractBeanDefinition && (abd = (AbstractBeanDefinition)beanDefinition).isSynthetic() && abd.hasAttribute(GENERATED_REF)) {
            BeanDefinition actual = abd.getOriginatingBeanDefinition();
            return actual;
        }
        return null;
    }

    private static boolean isEnvironmentManager(BeanDefinition beanDefinition) {
        return ComponentMetadataFactory.checkBeanDefinitionClassCompatibility(beanDefinition, ENV_FB_CLASS);
    }

    private static boolean checkBeanDefinitionClassCompatibility(BeanDefinition definition, Class<?> clazz) {
        AbstractBeanDefinition abstractDefinition;
        if (definition instanceof AbstractBeanDefinition && (abstractDefinition = (AbstractBeanDefinition)definition).hasBeanClass()) {
            Class beanClass = abstractDefinition.getBeanClass();
            return clazz.isAssignableFrom(beanClass);
        }
        return clazz.getName().equals(definition.getBeanClassName());
    }

    static Collection<ComponentMetadata> buildNestedMetadata(BeanDefinition beanDefinition) {
        ArrayList<ComponentMetadata> col = new ArrayList<ComponentMetadata>(4);
        ComponentMetadataFactory.processBeanDefinition(beanDefinition, col);
        col.remove(0);
        return col;
    }

    private static void processBeanMetadata(BeanMetadataElement metadata, Collection<ComponentMetadata> to) {
        if (metadata instanceof BeanDefinition) {
            ComponentMetadataFactory.processBeanDefinition((BeanDefinition)metadata, to);
        } else if (metadata instanceof BeanDefinitionHolder) {
            BeanDefinitionHolder bh = (BeanDefinitionHolder)metadata;
            ComponentMetadataFactory.processBeanDefinition(bh.getBeanDefinition(), to);
        } else if (metadata instanceof Mergeable && metadata instanceof Iterable) {
            ComponentMetadataFactory.processIterable((Iterable)metadata, to);
        }
    }

    private static void processBeanDefinition(BeanDefinition definition, Collection<ComponentMetadata> to) {
        to.add(ComponentMetadataFactory.buildMetadata(null, definition));
        ConstructorArgumentValues cavs = definition.getConstructorArgumentValues();
        List genericValues = cavs.getGenericArgumentValues();
        for (Object valueHolder : genericValues) {
            Object value = MetadataUtils.getValue((ConstructorArgumentValues.ValueHolder)valueHolder);
            if (!(value instanceof BeanMetadataElement)) continue;
            ComponentMetadataFactory.processBeanMetadata((BeanMetadataElement)value, to);
        }
        Map indexedValues = cavs.getIndexedArgumentValues();
        for (ConstructorArgumentValues.ValueHolder valueHolder : indexedValues.values()) {
            Object value = MetadataUtils.getValue(valueHolder);
            if (!(value instanceof BeanMetadataElement)) continue;
            ComponentMetadataFactory.processBeanMetadata((BeanMetadataElement)value, to);
        }
        MutablePropertyValues pvs = definition.getPropertyValues();
        for (PropertyValue pv : pvs.getPropertyValues()) {
            Object value = MetadataUtils.getValue(pv);
            if (!(value instanceof BeanMetadataElement)) continue;
            ComponentMetadataFactory.processBeanMetadata((BeanMetadataElement)value, to);
        }
    }

    private static void processIterable(Iterable iterableMetadata, Collection<ComponentMetadata> to) {
        for (Object value : iterableMetadata) {
            if (!(value instanceof BeanMetadataElement)) continue;
            ComponentMetadataFactory.processBeanMetadata((BeanMetadataElement)value, to);
        }
    }

    public static List<ComponentMetadata> buildComponentMetadataFor(ConfigurableListableBeanFactory factory) {
        String[] components;
        ArrayList<ComponentMetadata> metadata = new ArrayList<ComponentMetadata>();
        for (String beanName : components = factory.getBeanDefinitionNames()) {
            BeanDefinition definition = factory.getBeanDefinition(beanName);
            if (definition.hasAttribute(PROMOTED_REF)) continue;
            metadata.add(MetadataFactory.buildComponentMetadataFor(beanName, definition));
            metadata.addAll(MetadataFactory.buildNestedComponentMetadataFor(definition));
        }
        return metadata;
    }

    public static Set<String> filterIds(Set<String> components) {
        LinkedHashSet<String> filtered = new LinkedHashSet<String>(components.size());
        for (String string : components) {
            if (string.startsWith(GENERATED_START) && string.endsWith(GENERATED_END) && string.contains(GENERATED_MIDDLE)) continue;
            filtered.add(string);
        }
        return filtered;
    }
}


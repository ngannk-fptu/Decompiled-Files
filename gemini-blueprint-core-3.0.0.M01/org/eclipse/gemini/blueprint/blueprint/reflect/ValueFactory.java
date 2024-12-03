/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.beans.BeanMetadataElement
 *  org.springframework.beans.PropertyValues
 *  org.springframework.beans.factory.config.BeanDefinition
 *  org.springframework.beans.factory.config.BeanDefinitionHolder
 *  org.springframework.beans.factory.config.RuntimeBeanNameReference
 *  org.springframework.beans.factory.config.RuntimeBeanReference
 *  org.springframework.beans.factory.config.TypedStringValue
 *  org.springframework.beans.factory.support.ManagedArray
 *  org.springframework.beans.factory.support.ManagedList
 *  org.springframework.beans.factory.support.ManagedMap
 *  org.springframework.beans.factory.support.ManagedProperties
 *  org.springframework.beans.factory.support.ManagedSet
 */
package org.eclipse.gemini.blueprint.blueprint.reflect;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import org.eclipse.gemini.blueprint.blueprint.reflect.ComponentMetadataFactory;
import org.eclipse.gemini.blueprint.blueprint.reflect.MetadataFactory;
import org.eclipse.gemini.blueprint.blueprint.reflect.MetadataUtils;
import org.eclipse.gemini.blueprint.blueprint.reflect.SimpleCollectionMetadata;
import org.eclipse.gemini.blueprint.blueprint.reflect.SimpleIdRefMetadata;
import org.eclipse.gemini.blueprint.blueprint.reflect.SimpleMapEntry;
import org.eclipse.gemini.blueprint.blueprint.reflect.SimpleMapMetadata;
import org.eclipse.gemini.blueprint.blueprint.reflect.SimplePropsMetadata;
import org.eclipse.gemini.blueprint.blueprint.reflect.SimpleRefMetadata;
import org.eclipse.gemini.blueprint.blueprint.reflect.SimpleValueMetadata;
import org.eclipse.gemini.blueprint.util.BeanReferenceFactoryBean;
import org.osgi.service.blueprint.reflect.MapEntry;
import org.osgi.service.blueprint.reflect.Metadata;
import org.osgi.service.blueprint.reflect.NonNullMetadata;
import org.osgi.service.blueprint.reflect.NullMetadata;
import org.springframework.beans.BeanMetadataElement;
import org.springframework.beans.PropertyValues;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.config.RuntimeBeanNameReference;
import org.springframework.beans.factory.config.RuntimeBeanReference;
import org.springframework.beans.factory.config.TypedStringValue;
import org.springframework.beans.factory.support.ManagedArray;
import org.springframework.beans.factory.support.ManagedList;
import org.springframework.beans.factory.support.ManagedMap;
import org.springframework.beans.factory.support.ManagedProperties;
import org.springframework.beans.factory.support.ManagedSet;

class ValueFactory {
    private static final String BEAN_REF_FB_CLASS_NAME = BeanReferenceFactoryBean.class.getName();
    private static final String BEAN_REF_NAME_PROP = "targetBeanName";

    ValueFactory() {
    }

    static Metadata buildValue(Object metadata) {
        if (metadata instanceof BeanMetadataElement) {
            if (metadata instanceof RuntimeBeanReference) {
                RuntimeBeanReference reference = (RuntimeBeanReference)metadata;
                return new SimpleRefMetadata(reference.getBeanName());
            }
            if (metadata instanceof RuntimeBeanNameReference) {
                RuntimeBeanNameReference reference = (RuntimeBeanNameReference)metadata;
                return new SimpleIdRefMetadata(reference.getBeanName());
            }
            if (metadata instanceof TypedStringValue) {
                TypedStringValue typedString = (TypedStringValue)metadata;
                return typedString.getValue() == null ? NullMetadata.NULL : new SimpleValueMetadata(typedString);
            }
            if (metadata instanceof BeanDefinition) {
                BeanDefinition def = (BeanDefinition)metadata;
                if (BEAN_REF_FB_CLASS_NAME.equals(def.getBeanClassName())) {
                    BeanDefinition unwrapped = ComponentMetadataFactory.unwrapImporterReference(def);
                    if (unwrapped != null) {
                        return ComponentMetadataFactory.buildMetadata(null, unwrapped);
                    }
                    return new SimpleRefMetadata((String)MetadataUtils.getValue((PropertyValues)def.getPropertyValues(), BEAN_REF_NAME_PROP));
                }
                return MetadataFactory.buildComponentMetadataFor(null, def);
            }
            if (metadata instanceof BeanDefinitionHolder) {
                BeanDefinitionHolder holder = (BeanDefinitionHolder)metadata;
                return MetadataFactory.buildComponentMetadataFor(null, holder.getBeanDefinition());
            }
            if (metadata instanceof ManagedArray) {
                ManagedArray array = (ManagedArray)metadata;
                return new SimpleCollectionMetadata(ValueFactory.getMetadata(array), SimpleCollectionMetadata.CollectionType.ARRAY, array.getElementTypeName());
            }
            if (metadata instanceof ManagedList) {
                ManagedList list = (ManagedList)metadata;
                return new SimpleCollectionMetadata(ValueFactory.getMetadata(list), SimpleCollectionMetadata.CollectionType.LIST, list.getElementTypeName());
            }
            if (metadata instanceof ManagedSet) {
                ManagedSet set = (ManagedSet)metadata;
                return new SimpleCollectionMetadata(ValueFactory.getMetadata(set), SimpleCollectionMetadata.CollectionType.SET, set.getElementTypeName());
            }
            if (metadata instanceof ManagedMap) {
                ManagedMap map = (ManagedMap)metadata;
                return new SimpleMapMetadata(ValueFactory.getEntries(map), map.getKeyTypeName(), map.getValueTypeName());
            }
            if (metadata instanceof ManagedProperties) {
                ManagedProperties properties = (ManagedProperties)metadata;
                return new SimplePropsMetadata(ValueFactory.getEntries(properties));
            }
            throw new IllegalArgumentException("Unsupported metadata type " + metadata.getClass());
        }
        return new SimpleValueMetadata(null, metadata.toString());
    }

    static <E> List<Metadata> getMetadata(Collection<E> collection) {
        if (collection.isEmpty()) {
            return Collections.emptyList();
        }
        ArrayList<Metadata> list = new ArrayList<Metadata>(collection.size());
        for (E value : collection) {
            list.add(ValueFactory.buildValue(value));
        }
        return Collections.unmodifiableList(list);
    }

    static <K, V> List<MapEntry> getEntries(Map<K, V> map) {
        if (map.isEmpty()) {
            return Collections.emptyList();
        }
        ArrayList<SimpleMapEntry> entries = new ArrayList<SimpleMapEntry>(map.size());
        for (Map.Entry<K, V> entry : map.entrySet()) {
            NonNullMetadata key = (NonNullMetadata)ValueFactory.buildValue(entry.getKey());
            Metadata value = ValueFactory.buildValue(entry.getValue());
            entries.add(new SimpleMapEntry(key, value));
        }
        return Collections.unmodifiableList(entries);
    }
}


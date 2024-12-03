/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.beans.BeanMetadataElement
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

import java.util.List;
import java.util.Set;
import org.eclipse.gemini.blueprint.blueprint.reflect.MetadataFactory;
import org.osgi.service.blueprint.reflect.CollectionMetadata;
import org.osgi.service.blueprint.reflect.ComponentMetadata;
import org.osgi.service.blueprint.reflect.IdRefMetadata;
import org.osgi.service.blueprint.reflect.MapEntry;
import org.osgi.service.blueprint.reflect.MapMetadata;
import org.osgi.service.blueprint.reflect.Metadata;
import org.osgi.service.blueprint.reflect.NullMetadata;
import org.osgi.service.blueprint.reflect.PropsMetadata;
import org.osgi.service.blueprint.reflect.RefMetadata;
import org.osgi.service.blueprint.reflect.ValueMetadata;
import org.springframework.beans.BeanMetadataElement;
import org.springframework.beans.factory.config.RuntimeBeanNameReference;
import org.springframework.beans.factory.config.RuntimeBeanReference;
import org.springframework.beans.factory.config.TypedStringValue;
import org.springframework.beans.factory.support.ManagedArray;
import org.springframework.beans.factory.support.ManagedList;
import org.springframework.beans.factory.support.ManagedMap;
import org.springframework.beans.factory.support.ManagedProperties;
import org.springframework.beans.factory.support.ManagedSet;

class BeanMetadataElementFactory {
    BeanMetadataElementFactory() {
    }

    static BeanMetadataElement buildBeanMetadata(Metadata metadata) {
        return BeanMetadataElementFactory.buildBeanMetadata(metadata, null);
    }

    static BeanMetadataElement buildBeanMetadata(Metadata value, String defaultTypeName) {
        if (value instanceof ValueMetadata) {
            ValueMetadata typedString = (ValueMetadata)value;
            String specifiedType = typedString.getType();
            if (specifiedType == null) {
                specifiedType = defaultTypeName;
            }
            return new TypedStringValue(typedString.getStringValue(), specifiedType);
        }
        if (value instanceof ComponentMetadata) {
            ComponentMetadata component = (ComponentMetadata)value;
            return MetadataFactory.buildBeanDefinitionFor(component);
        }
        if (value instanceof NullMetadata) {
            return new TypedStringValue(null);
        }
        if (value instanceof RefMetadata) {
            RefMetadata reference = (RefMetadata)value;
            return new RuntimeBeanReference(reference.getComponentId());
        }
        if (value instanceof IdRefMetadata) {
            IdRefMetadata reference = (IdRefMetadata)value;
            return new RuntimeBeanNameReference(reference.getComponentId());
        }
        if (value instanceof CollectionMetadata) {
            ManagedList coll;
            CollectionMetadata collection = (CollectionMetadata)value;
            Class type = collection.getCollectionClass();
            List values = collection.getValues();
            if (List.class.isAssignableFrom(type)) {
                ManagedList list = new ManagedList(values.size());
                list.setElementTypeName(collection.getValueType());
                coll = list;
            } else if (Set.class.isAssignableFrom(type)) {
                ManagedSet set = new ManagedSet(values.size());
                set.setElementTypeName(collection.getValueType());
                coll = set;
            } else if (Object[].class.isAssignableFrom(type)) {
                ManagedArray array = new ManagedArray(collection.getValueType(), values.size());
                coll = array;
            } else {
                throw new IllegalArgumentException("Cannot create collection for type " + type);
            }
            for (Metadata val : values) {
                coll.add(BeanMetadataElementFactory.buildBeanMetadata(val, collection.getValueType()));
            }
            return (BeanMetadataElement)coll;
        }
        if (value instanceof MapMetadata) {
            MapMetadata mapValue = (MapMetadata)value;
            List entries = mapValue.getEntries();
            String defaultKeyType = mapValue.getKeyType();
            String defaultValueType = mapValue.getValueType();
            ManagedMap managedMap = new ManagedMap(entries.size());
            managedMap.setKeyTypeName(defaultKeyType);
            managedMap.setValueTypeName(defaultValueType);
            for (MapEntry mapEntry : entries) {
                managedMap.put((Object)BeanMetadataElementFactory.buildBeanMetadata(mapEntry.getKey(), defaultKeyType), (Object)BeanMetadataElementFactory.buildBeanMetadata(mapEntry.getValue(), defaultValueType));
            }
            return managedMap;
        }
        if (value instanceof PropsMetadata) {
            PropsMetadata propertiesValue = (PropsMetadata)value;
            List entries = propertiesValue.getEntries();
            ManagedProperties managedProperties = new ManagedProperties();
            for (MapEntry mapEntry : entries) {
                managedProperties.put((Object)BeanMetadataElementFactory.buildBeanMetadata(mapEntry.getKey()), (Object)BeanMetadataElementFactory.buildBeanMetadata(mapEntry.getValue()));
            }
        }
        throw new IllegalArgumentException("Unknown value type " + value.getClass());
    }
}


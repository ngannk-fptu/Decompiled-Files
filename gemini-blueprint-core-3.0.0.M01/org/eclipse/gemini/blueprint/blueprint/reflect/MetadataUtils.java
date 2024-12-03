/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.beans.PropertyValue
 *  org.springframework.beans.PropertyValues
 *  org.springframework.beans.factory.config.BeanDefinition
 *  org.springframework.beans.factory.config.ConstructorArgumentValues
 *  org.springframework.beans.factory.config.ConstructorArgumentValues$ValueHolder
 */
package org.eclipse.gemini.blueprint.blueprint.reflect;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import org.eclipse.gemini.blueprint.blueprint.reflect.SimpleBeanArgument;
import org.eclipse.gemini.blueprint.blueprint.reflect.SimpleBeanProperty;
import org.osgi.service.blueprint.reflect.BeanArgument;
import org.osgi.service.blueprint.reflect.BeanProperty;
import org.springframework.beans.PropertyValue;
import org.springframework.beans.PropertyValues;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.ConstructorArgumentValues;

abstract class MetadataUtils {
    MetadataUtils() {
    }

    static Object getValue(PropertyValues pvs, String name) {
        if (pvs.contains(name)) {
            PropertyValue pv = pvs.getPropertyValue(name);
            return pv.getValue();
        }
        return null;
    }

    static Object getValue(PropertyValue pv) {
        return pv.getValue();
    }

    static Object getValue(ConstructorArgumentValues.ValueHolder valueHolder) {
        return valueHolder.getValue();
    }

    static List<BeanArgument> getBeanArguments(BeanDefinition definition) {
        ArrayList<SimpleBeanArgument> temp;
        ConstructorArgumentValues ctorValues = definition.getConstructorArgumentValues();
        Map indexedArguments = ctorValues.getIndexedArgumentValues();
        if (!indexedArguments.isEmpty()) {
            temp = new ArrayList<SimpleBeanArgument>(indexedArguments.size());
            for (Map.Entry entry : indexedArguments.entrySet()) {
                temp.add(new SimpleBeanArgument((Integer)entry.getKey(), (ConstructorArgumentValues.ValueHolder)entry.getValue()));
            }
        } else {
            List args = ctorValues.getGenericArgumentValues();
            temp = new ArrayList(args.size());
            for (ConstructorArgumentValues.ValueHolder valueHolder : args) {
                temp.add(new SimpleBeanArgument(valueHolder));
            }
        }
        return Collections.unmodifiableList(temp);
    }

    static List<BeanProperty> getBeanProperties(BeanDefinition definition) {
        List pvs = definition.getPropertyValues().getPropertyValueList();
        if (pvs.isEmpty()) {
            return Collections.emptyList();
        }
        ArrayList<SimpleBeanProperty> temp = new ArrayList<SimpleBeanProperty>(pvs.size());
        for (PropertyValue propertyValue : pvs) {
            temp.add(new SimpleBeanProperty(propertyValue));
        }
        return Collections.unmodifiableList(temp);
    }
}


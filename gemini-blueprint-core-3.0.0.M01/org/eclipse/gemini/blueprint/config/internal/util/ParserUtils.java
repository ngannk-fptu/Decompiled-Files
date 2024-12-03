/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.beans.factory.config.TypedStringValue
 *  org.springframework.beans.factory.support.BeanDefinitionBuilder
 *  org.springframework.beans.factory.support.ManagedSet
 *  org.springframework.util.CollectionUtils
 *  org.springframework.util.ObjectUtils
 */
package org.eclipse.gemini.blueprint.config.internal.util;

import java.util.ArrayList;
import java.util.Set;
import org.eclipse.gemini.blueprint.config.internal.adapter.ToStringClassAdapter;
import org.eclipse.gemini.blueprint.config.internal.util.AttributeCallback;
import org.eclipse.gemini.blueprint.config.internal.util.BlueprintAttributeCallback;
import org.eclipse.gemini.blueprint.config.internal.util.ConventionsCallback;
import org.eclipse.gemini.blueprint.config.internal.util.PropertyRefAttributeCallback;
import org.eclipse.gemini.blueprint.config.internal.util.StandardAttributeCallback;
import org.springframework.beans.factory.config.TypedStringValue;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.ManagedSet;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.w3c.dom.Attr;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;

public abstract class ParserUtils {
    private static final AttributeCallback STANDARD_ATTRS_CALLBACK = new StandardAttributeCallback();
    private static final AttributeCallback BLUEPRINT_ATTRS_CALLBACK = new BlueprintAttributeCallback();
    private static final AttributeCallback PROPERTY_REF_ATTRS_CALLBACK = new PropertyRefAttributeCallback();
    private static final AttributeCallback PROPERTY_CONV_ATTRS_CALLBACK = new ConventionsCallback();
    public static final String REFERENCE_LISTENER_REF_ATTR = "org.eclipse.gemini.blueprint.config.internal.reference.listener.ref.attr";

    public static void parseAttributes(Element element, BeanDefinitionBuilder builder, AttributeCallback[] callbacks) {
        NamedNodeMap attributes = element.getAttributes();
        for (int x = 0; x < attributes.getLength(); ++x) {
            Attr attr = (Attr)attributes.item(x);
            boolean shouldContinue = true;
            if (ObjectUtils.isEmpty((Object[])callbacks)) continue;
            for (int i = 0; i < callbacks.length && shouldContinue; ++i) {
                AttributeCallback callback = callbacks[i];
                shouldContinue = callback.process(element, attr, builder);
            }
        }
    }

    public static void parseCustomAttributes(Element element, BeanDefinitionBuilder builder, AttributeCallback[] callbacks) {
        ArrayList<AttributeCallback> list = new ArrayList<AttributeCallback>(8);
        if (!ObjectUtils.isEmpty((Object[])callbacks)) {
            CollectionUtils.mergeArrayIntoCollection((Object)callbacks, list);
        }
        list.add(STANDARD_ATTRS_CALLBACK);
        list.add(BLUEPRINT_ATTRS_CALLBACK);
        list.add(PROPERTY_REF_ATTRS_CALLBACK);
        list.add(PROPERTY_CONV_ATTRS_CALLBACK);
        AttributeCallback[] cbacks = list.toArray(new AttributeCallback[list.size()]);
        ParserUtils.parseAttributes(element, builder, cbacks);
    }

    public static void parseCustomAttributes(Element element, BeanDefinitionBuilder builder, AttributeCallback callback) {
        AttributeCallback[] attributeCallbackArray;
        if (callback == null) {
            attributeCallbackArray = new AttributeCallback[]{};
        } else {
            AttributeCallback[] attributeCallbackArray2 = new AttributeCallback[1];
            attributeCallbackArray = attributeCallbackArray2;
            attributeCallbackArray2[0] = callback;
        }
        AttributeCallback[] callbacks = attributeCallbackArray;
        ParserUtils.parseCustomAttributes(element, builder, callbacks);
    }

    public static AttributeCallback[] mergeCallbacks(AttributeCallback[] callbacksA, AttributeCallback[] callbacksB) {
        if (ObjectUtils.isEmpty((Object[])callbacksA)) {
            if (ObjectUtils.isEmpty((Object[])callbacksB)) {
                return new AttributeCallback[0];
            }
            return callbacksB;
        }
        if (ObjectUtils.isEmpty((Object[])callbacksB)) {
            return callbacksA;
        }
        AttributeCallback[] newCallbacks = new AttributeCallback[callbacksA.length + callbacksB.length];
        System.arraycopy(callbacksA, 0, newCallbacks, 0, callbacksA.length);
        System.arraycopy(callbacksB, 0, newCallbacks, callbacksA.length, callbacksB.length);
        return newCallbacks;
    }

    public static Set<?> convertClassesToStrings(Set<?> parsedClasses) {
        ManagedSet interfaces = new ManagedSet(parsedClasses.size());
        for (Object clazz : parsedClasses) {
            if (clazz instanceof TypedStringValue || clazz instanceof String) {
                interfaces.add(clazz);
                continue;
            }
            interfaces.add(BeanDefinitionBuilder.genericBeanDefinition(ToStringClassAdapter.class).addConstructorArgValue(clazz).getBeanDefinition());
        }
        return interfaces;
    }
}


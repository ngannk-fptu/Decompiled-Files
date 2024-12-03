/*
 * Decompiled with CFR 0.152.
 */
package org.terracotta.context.extractor;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import org.terracotta.context.ContextElement;
import org.terracotta.context.annotations.ContextAttribute;
import org.terracotta.context.extractor.AttributeGetter;
import org.terracotta.context.extractor.DirectAttributeGetter;
import org.terracotta.context.extractor.LazyContextElement;
import org.terracotta.context.extractor.WeakAttributeGetter;
import org.terracotta.context.extractor.WeakFieldAttributeGetter;
import org.terracotta.context.extractor.WeakMethodAttributeGetter;

public final class ObjectContextExtractor {
    private ObjectContextExtractor() {
    }

    public static ContextElement extract(Object from) {
        HashMap<? extends String, AttributeGetter<? extends Object>> attributes = new HashMap<String, AttributeGetter<? extends Object>>();
        attributes.putAll(ObjectContextExtractor.extractInstanceAttribute(from));
        attributes.putAll(ObjectContextExtractor.extractMethodAttributes(from));
        attributes.putAll(ObjectContextExtractor.extractFieldAttributes(from));
        return new LazyContextElement(from.getClass(), attributes);
    }

    private static Map<? extends String, ? extends AttributeGetter<? extends Object>> extractInstanceAttribute(Object from) {
        ContextAttribute annotation = from.getClass().getAnnotation(ContextAttribute.class);
        if (annotation == null) {
            return Collections.emptyMap();
        }
        return Collections.singletonMap(annotation.value(), new WeakAttributeGetter<Object>(from));
    }

    private static Map<String, AttributeGetter<? extends Object>> extractMethodAttributes(Object from) {
        HashMap<String, AttributeGetter<? extends Object>> attributes = new HashMap<String, AttributeGetter<? extends Object>>();
        for (Method m : from.getClass().getMethods()) {
            ContextAttribute annotation;
            if (m.getParameterTypes().length != 0 || m.getReturnType() == Void.TYPE || (annotation = m.getAnnotation(ContextAttribute.class)) == null) continue;
            attributes.put(annotation.value(), new WeakMethodAttributeGetter(from, m));
        }
        return attributes;
    }

    private static Map<String, AttributeGetter<? extends Object>> extractFieldAttributes(Object from) {
        HashMap<String, AttributeGetter<? extends Object>> attributes = new HashMap<String, AttributeGetter<? extends Object>>();
        for (Class<?> c = from.getClass(); c != null; c = c.getSuperclass()) {
            for (Field f : c.getDeclaredFields()) {
                ContextAttribute annotation = f.getAnnotation(ContextAttribute.class);
                if (annotation == null) continue;
                attributes.put(annotation.value(), ObjectContextExtractor.createFieldAttributeGetter(from, f));
            }
        }
        return attributes;
    }

    private static AttributeGetter<? extends Object> createFieldAttributeGetter(Object from, Field f) {
        f.setAccessible(true);
        if (Modifier.isFinal(f.getModifiers())) {
            try {
                return new DirectAttributeGetter<Object>(f.get(from));
            }
            catch (IllegalArgumentException ex) {
                throw new RuntimeException(ex);
            }
            catch (IllegalAccessException ex) {
                throw new RuntimeException(ex);
            }
        }
        return new WeakFieldAttributeGetter(from, f);
    }
}


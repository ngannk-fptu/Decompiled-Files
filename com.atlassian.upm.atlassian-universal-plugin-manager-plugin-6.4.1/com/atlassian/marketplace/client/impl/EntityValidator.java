/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.Iterables
 *  io.atlassian.fugue.Option
 */
package com.atlassian.marketplace.client.impl;

import com.atlassian.marketplace.client.encoding.MissingRequiredField;
import com.atlassian.marketplace.client.encoding.SchemaViolation;
import com.atlassian.marketplace.client.impl.SchemaViolationException;
import com.atlassian.marketplace.client.model.Links;
import com.atlassian.marketplace.client.model.RequiredLink;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.google.gson.JsonParseException;
import io.atlassian.fugue.Option;
import java.lang.reflect.Field;
import java.net.URI;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public abstract class EntityValidator {
    private static ConcurrentHashMap<Class<?>, Map<String, Field>> classFields = new ConcurrentHashMap();

    public static <T> T validateInstance(T instance) throws SchemaViolationException {
        Object violations = ImmutableList.of();
        for (Field f : EntityValidator.getClassFields(instance.getClass()).values()) {
            violations = Iterables.concat((Iterable)violations, EntityValidator.postProcessField(f, instance));
        }
        if (!Iterables.isEmpty((Iterable)violations)) {
            throw new SchemaViolationException((Iterable<SchemaViolation>)violations);
        }
        return instance;
    }

    private static Iterable<SchemaViolation> postProcessField(Field f, Object o) {
        f.setAccessible(true);
        try {
            if (f.get(o) == null) {
                if (Option.class.isAssignableFrom(f.getType())) {
                    f.set(o, Option.none());
                } else {
                    RequiredLink reqLinkAnno = f.getAnnotation(RequiredLink.class);
                    if (reqLinkAnno != null) {
                        return EntityValidator.setRequiredLinkField(reqLinkAnno, f, o);
                    }
                    return Option.some((Object)new MissingRequiredField(o.getClass(), f.getName()));
                }
            }
            return Option.none();
        }
        catch (IllegalAccessException e) {
            throw new JsonParseException(e);
        }
    }

    private static Iterable<SchemaViolation> setRequiredLinkField(RequiredLink anno, Field f, Object o) throws IllegalAccessException {
        Field linksField = EntityValidator.getClassFields(o.getClass()).get("_links");
        if (linksField == null || linksField.getType() != Links.class) {
            throw new IllegalStateException("@RequiredLink annotation was found in a class without a 'Links _links' field");
        }
        Links links = (Links)linksField.get(o);
        Iterator iterator = links.getUri(anno.rel()).iterator();
        if (iterator.hasNext()) {
            URI u = (URI)iterator.next();
            f.set(o, u);
            return Option.none();
        }
        return Option.some((Object)new MissingRequiredField(o.getClass(), "_links." + anno.rel()));
    }

    public static Map<String, Field> getClassFields(Class<?> c) {
        Map<String, Field> m = classFields.get(c);
        if (m == null) {
            m = new HashMap<String, Field>();
            for (Class<?> c1 = c; c1 != null; c1 = c1.getSuperclass()) {
                for (Field f : c1.getDeclaredFields()) {
                    f.setAccessible(true);
                    m.put(f.getName(), f);
                }
            }
            classFields.put(c, m);
        }
        return m;
    }
}


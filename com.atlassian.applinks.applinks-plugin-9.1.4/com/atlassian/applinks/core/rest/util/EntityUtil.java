/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Function
 *  com.google.common.collect.Iterables
 */
package com.atlassian.applinks.core.rest.util;

import com.google.common.base.Function;
import com.google.common.collect.Iterables;
import java.util.LinkedHashSet;
import java.util.Set;

public class EntityUtil {
    private EntityUtil() {
    }

    public static Set<String> getClassNames(Iterable types) {
        LinkedHashSet<String> type = new LinkedHashSet<String>();
        EntityUtil.addClassNames(type, types);
        return type;
    }

    private static void addClassNames(Set<String> target, Iterable types) {
        Iterables.addAll(target, (Iterable)Iterables.transform((Iterable)types, (Function)new Function<Class, String>(){

            public String apply(Class from) {
                return from.getName();
            }
        }));
    }
}


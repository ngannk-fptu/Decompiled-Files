/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 *  com.google.common.collect.Iterables
 *  org.apache.commons.lang3.ArrayUtils
 *  org.apache.commons.lang3.StringUtils
 */
package com.atlassian.streams.api.common;

import com.google.common.collect.Iterables;
import java.net.URI;
import java.util.Map;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

public final class Preconditions {
    private Preconditions() {
    }

    public static <T, C extends Iterable<T>> C checkNotEmpty(C iterable, String name) {
        if (Iterables.isEmpty((Iterable)((Iterable)com.google.common.base.Preconditions.checkNotNull(iterable, (Object)name)))) {
            throw new IllegalArgumentException(name + " must not be empty");
        }
        return iterable;
    }

    public static <T> T[] checkNotEmpty(T[] array, String name) {
        if (ArrayUtils.isEmpty((Object[])((Object[])com.google.common.base.Preconditions.checkNotNull(array, (Object)name)))) {
            throw new IllegalArgumentException(name + " must not be empty");
        }
        return array;
    }

    public static <K, V> Map<K, V> checkNotEmpty(Map<K, V> map, String name) {
        if (((Map)com.google.common.base.Preconditions.checkNotNull(map, (Object)name)).isEmpty()) {
            throw new IllegalArgumentException(name + " must not be empty");
        }
        return map;
    }

    public static String checkNotBlank(String text, String name) {
        if (StringUtils.isBlank((CharSequence)((CharSequence)com.google.common.base.Preconditions.checkNotNull((Object)text, (Object)name)))) {
            throw new IllegalArgumentException(name + " must not be empty or blank");
        }
        return text;
    }

    public static URI checkAbsolute(URI uri, String name) {
        if (uri != null && !uri.isAbsolute()) {
            throw new IllegalArgumentException(name + " must be an absolute URI");
        }
        return uri;
    }
}


/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.Internal
 *  com.google.common.collect.ImmutableMap
 *  javax.annotation.Nonnull
 *  javax.annotation.Nullable
 */
package com.atlassian.scheduler.util;

import com.atlassian.annotations.Internal;
import com.google.common.collect.ImmutableMap;
import java.io.Serializable;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

@Internal
public final class Safe {
    @Nullable
    public static Date copy(@Nullable Date date) {
        return date != null ? (Date)date.clone() : null;
    }

    @Nullable
    public static byte[] copy(@Nullable byte[] bytes) {
        return bytes != null ? (byte[])bytes.clone() : null;
    }

    @Nonnull
    public static Map<String, Serializable> copy(@Nullable Map<String, Serializable> map) {
        Object copy = map == null ? ImmutableMap.of() : (map instanceof ImmutableMap ? map : Collections.unmodifiableMap(new HashMap<String, Serializable>(map)));
        return copy;
    }

    private Safe() {
        throw new Error("I am static-only.");
    }
}


/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableSet
 */
package com.atlassian.http.method;

import com.google.common.collect.ImmutableSet;
import java.util.Locale;

public class Methods {
    public static boolean isMutative(String method) {
        return !ImmutableSet.of((Object)"GET", (Object)"HEAD", (Object)"OPTIONS", (Object)"TRACE").contains((Object)method.toUpperCase(Locale.US));
    }
}


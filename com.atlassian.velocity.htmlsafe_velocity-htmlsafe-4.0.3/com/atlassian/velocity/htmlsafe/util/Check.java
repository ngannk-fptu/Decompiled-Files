/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 */
package com.atlassian.velocity.htmlsafe.util;

import com.google.common.base.Preconditions;

@Deprecated
public final class Check {
    @Deprecated
    public static <T> T notNull(T reference) {
        return (T)Preconditions.checkNotNull(reference);
    }

    @Deprecated
    public static <T> T notNull(T reference, Object errorMessage) {
        return (T)Preconditions.checkNotNull(reference, (Object)errorMessage);
    }

    @Deprecated
    public static void argument(boolean expression) {
        Preconditions.checkArgument((boolean)expression);
    }

    @Deprecated
    public static void argument(boolean expression, Object errorMessage) {
        Preconditions.checkArgument((boolean)expression, (Object)errorMessage);
    }
}


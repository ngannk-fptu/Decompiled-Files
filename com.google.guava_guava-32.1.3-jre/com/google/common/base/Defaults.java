/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.CheckForNull
 */
package com.google.common.base;

import com.google.common.annotations.GwtIncompatible;
import com.google.common.annotations.J2ktIncompatible;
import com.google.common.base.ElementTypesAreNonnullByDefault;
import com.google.common.base.Preconditions;
import javax.annotation.CheckForNull;

@ElementTypesAreNonnullByDefault
@J2ktIncompatible
@GwtIncompatible
public final class Defaults {
    private static final Double DOUBLE_DEFAULT = 0.0;
    private static final Float FLOAT_DEFAULT = Float.valueOf(0.0f);

    private Defaults() {
    }

    @CheckForNull
    public static <T> T defaultValue(Class<T> type) {
        Preconditions.checkNotNull(type);
        if (type.isPrimitive()) {
            if (type == Boolean.TYPE) {
                return (T)Boolean.FALSE;
            }
            if (type == Character.TYPE) {
                return (T)Character.valueOf('\u0000');
            }
            if (type == Byte.TYPE) {
                return (T)Byte.valueOf((byte)0);
            }
            if (type == Short.TYPE) {
                return (T)Short.valueOf((short)0);
            }
            if (type == Integer.TYPE) {
                return (T)Integer.valueOf(0);
            }
            if (type == Long.TYPE) {
                return (T)Long.valueOf(0L);
            }
            if (type == Float.TYPE) {
                return (T)FLOAT_DEFAULT;
            }
            if (type == Double.TYPE) {
                return (T)DOUBLE_DEFAULT;
            }
        }
        return null;
    }
}


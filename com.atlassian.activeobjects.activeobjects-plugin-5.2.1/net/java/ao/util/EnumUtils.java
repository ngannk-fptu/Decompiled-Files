/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Iterables
 *  com.google.common.collect.Lists
 */
package net.java.ao.util;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import java.lang.reflect.InvocationTargetException;

public final class EnumUtils {
    public static Iterable<Enum> values(Class<? extends Enum> type) {
        try {
            return Lists.newArrayList((Object[])((Enum[])type.getMethod("values", new Class[0]).invoke(null, new Object[0])));
        }
        catch (IllegalArgumentException e) {
            throw new IllegalStateException(e);
        }
        catch (SecurityException e) {
            throw new IllegalStateException(e);
        }
        catch (IllegalAccessException e) {
            throw new IllegalStateException(e);
        }
        catch (InvocationTargetException e) {
            throw new IllegalStateException(e);
        }
        catch (NoSuchMethodException e) {
            throw new IllegalStateException(e);
        }
    }

    public static int size(Class<? extends Enum> type) {
        return Iterables.size(EnumUtils.values(type));
    }
}


/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.sun.istack.NotNull
 *  com.sun.istack.Nullable
 *  com.sun.xml.bind.api.impl.NameConverter
 */
package com.sun.xml.ws.spi.db;

import com.sun.istack.NotNull;
import com.sun.istack.Nullable;
import com.sun.xml.bind.api.impl.NameConverter;
import com.sun.xml.ws.spi.db.Utils;
import java.lang.reflect.Type;

public class BindingHelper {
    @NotNull
    public static String mangleNameToVariableName(@NotNull String localName) {
        return NameConverter.standard.toVariableName(localName);
    }

    @NotNull
    public static String mangleNameToClassName(@NotNull String localName) {
        return NameConverter.standard.toClassName(localName);
    }

    @NotNull
    public static String mangleNameToPropertyName(@NotNull String localName) {
        return NameConverter.standard.toPropertyName(localName);
    }

    @Nullable
    public static Type getBaseType(@NotNull Type type, @NotNull Class baseType) {
        return (Type)Utils.REFLECTION_NAVIGATOR.getBaseClass((Object)type, (Object)baseType);
    }

    public static <T> Class<T> erasure(Type t) {
        return (Class)Utils.REFLECTION_NAVIGATOR.erasure((Object)t);
    }
}


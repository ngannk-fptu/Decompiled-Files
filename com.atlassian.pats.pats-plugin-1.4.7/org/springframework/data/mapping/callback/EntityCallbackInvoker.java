/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.data.mapping.callback;

import java.util.function.BiFunction;
import org.springframework.data.mapping.callback.EntityCallback;

interface EntityCallbackInvoker {
    public <T> Object invokeCallback(EntityCallback<T> var1, T var2, BiFunction<EntityCallback<T>, T, Object> var3);

    public static boolean matchesClassCastMessage(String classCastMessage, Class<?> eventClass) {
        if (classCastMessage.startsWith(eventClass.getName())) {
            return true;
        }
        if (classCastMessage.startsWith(eventClass.toString())) {
            return true;
        }
        int moduleSeparatorIndex = classCastMessage.indexOf(47);
        return moduleSeparatorIndex != -1 && classCastMessage.startsWith(eventClass.getName(), moduleSeparatorIndex + 1);
    }
}


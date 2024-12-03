/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.velocity.htmlsafe.introspection;

import com.atlassian.velocity.htmlsafe.introspection.BoxedValue;

final class BoxingUtils {
    private BoxingUtils() {
    }

    static Object unboxObject(Object obj) {
        if (obj instanceof BoxedValue) {
            return ((BoxedValue)obj).unbox();
        }
        return obj;
    }

    static Object[] unboxArrayElements(Object[] array) {
        if (array == null || array.length == 0) {
            return array;
        }
        Object[] unboxedArgs = (Object[])array.clone();
        for (int idx = 0; idx < array.length; ++idx) {
            unboxedArgs[idx] = BoxingUtils.unboxObject(unboxedArgs[idx]);
        }
        return unboxedArgs;
    }
}


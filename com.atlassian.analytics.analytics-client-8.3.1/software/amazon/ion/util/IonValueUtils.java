/*
 * Decompiled with CFR 0.152.
 */
package software.amazon.ion.util;

import software.amazon.ion.IonValue;

public class IonValueUtils {
    public static final boolean anyNull(IonValue value) {
        return value == null || value.isNullValue();
    }
}


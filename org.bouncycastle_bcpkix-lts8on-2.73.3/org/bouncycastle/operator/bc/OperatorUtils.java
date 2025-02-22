/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.operator.bc;

import java.security.Key;
import org.bouncycastle.operator.GenericKey;

class OperatorUtils {
    OperatorUtils() {
    }

    static byte[] getKeyBytes(GenericKey key) {
        if (key.getRepresentation() instanceof Key) {
            return ((Key)key.getRepresentation()).getEncoded();
        }
        if (key.getRepresentation() instanceof byte[]) {
            return (byte[])key.getRepresentation();
        }
        throw new IllegalArgumentException("unknown generic key type");
    }
}


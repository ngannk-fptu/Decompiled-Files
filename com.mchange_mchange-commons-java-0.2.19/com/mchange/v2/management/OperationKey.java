/*
 * Decompiled with CFR 0.152.
 */
package com.mchange.v2.management;

import java.util.Arrays;

public final class OperationKey {
    String name;
    String[] signature;

    public OperationKey(String string, String[] stringArray) {
        this.name = string;
        this.signature = stringArray;
    }

    public boolean equals(Object object) {
        if (object instanceof OperationKey) {
            OperationKey operationKey = (OperationKey)object;
            return this.name.equals(operationKey.name) && Arrays.equals(this.signature, operationKey.signature);
        }
        return false;
    }

    public int hashCode() {
        return this.name.hashCode() ^ Arrays.hashCode(this.signature);
    }
}


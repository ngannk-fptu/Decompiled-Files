/*
 * Decompiled with CFR 0.152.
 */
package com.microsoft.sqlserver.jdbc;

enum EnclaveType {
    VBS("VBS"),
    SGX("SGX");

    private final String type;

    private EnclaveType(String type) {
        this.type = type;
    }

    public int getValue() {
        return this.ordinal() + 1;
    }

    static boolean isValidEnclaveType(String type) {
        for (EnclaveType t : EnclaveType.values()) {
            if (!type.equalsIgnoreCase(t.toString())) continue;
            return true;
        }
        return false;
    }

    public String toString() {
        return this.type;
    }
}


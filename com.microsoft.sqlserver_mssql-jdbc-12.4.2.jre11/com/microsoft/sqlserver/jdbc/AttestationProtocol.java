/*
 * Decompiled with CFR 0.152.
 */
package com.microsoft.sqlserver.jdbc;

enum AttestationProtocol {
    HGS("HGS"),
    AAS("AAS"),
    NONE("NONE");

    private final String protocol;

    private AttestationProtocol(String protocol) {
        this.protocol = protocol;
    }

    static boolean isValidAttestationProtocol(String protocol) {
        for (AttestationProtocol p : AttestationProtocol.values()) {
            if (!protocol.equalsIgnoreCase(p.toString())) continue;
            return true;
        }
        return false;
    }

    public String toString() {
        return this.protocol;
    }
}


/*
 * Decompiled with CFR 0.152.
 */
package com.amazonaws.services.s3.model.analytics;

import java.io.Serializable;

public enum StorageClassAnalysisSchemaVersion implements Serializable
{
    V_1("V_1");

    private final String version;

    private StorageClassAnalysisSchemaVersion(String version) {
        this.version = version;
    }

    public String toString() {
        return this.version;
    }
}


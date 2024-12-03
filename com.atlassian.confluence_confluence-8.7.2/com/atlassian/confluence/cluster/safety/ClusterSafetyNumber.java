/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.cluster.safety;

import com.atlassian.confluence.core.NotExportable;

public class ClusterSafetyNumber
implements NotExportable {
    private long id;
    private int safetyNumber;

    public long getId() {
        return this.id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public int getSafetyNumber() {
        return this.safetyNumber;
    }

    public void setSafetyNumber(int safetyNumber) {
        this.safetyNumber = safetyNumber;
    }
}


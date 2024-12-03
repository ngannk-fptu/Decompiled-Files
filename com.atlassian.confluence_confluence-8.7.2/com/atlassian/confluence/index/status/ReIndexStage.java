/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.index.status;

public enum ReIndexStage {
    REBUILDING,
    REBUILD_FAILED,
    PROPAGATING,
    COMPLETE,
    PROPAGATION_FAILED;


    public boolean isFinal() {
        return this == COMPLETE || this == PROPAGATION_FAILED || this == REBUILD_FAILED;
    }
}


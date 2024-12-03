/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.cluster;

public class ClusterException
extends Exception {
    public ClusterException() {
    }

    public ClusterException(String string) {
        super(string);
    }

    public ClusterException(String string, Throwable throwable) {
        super(string, throwable);
    }

    public ClusterException(Throwable throwable) {
        super(throwable);
    }
}


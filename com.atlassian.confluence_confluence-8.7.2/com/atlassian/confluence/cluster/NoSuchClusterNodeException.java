/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.cluster;

import com.atlassian.confluence.cluster.ClusterException;

public class NoSuchClusterNodeException
extends ClusterException {
    public NoSuchClusterNodeException(String message) {
        super(message);
    }
}


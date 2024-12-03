/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.cluster;

import com.atlassian.confluence.cluster.ClusterException;

public class ClusterNotPermittedException
extends ClusterException {
    @Override
    public String getMessage() {
        return "Clustering is not permitted with your license. Nice try, cowboy.";
    }
}


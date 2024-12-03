/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.cluster;

import com.atlassian.confluence.util.longrunning.ConfluenceAbstractLongRunningTask;

public class StartClusterLongRunningTask
extends ConfluenceAbstractLongRunningTask {
    public StartClusterLongRunningTask(String clusterName) {
    }

    @Override
    public void runInternal() {
        super.run();
    }

    public String getName() {
        return "Starting new cluster";
    }
}


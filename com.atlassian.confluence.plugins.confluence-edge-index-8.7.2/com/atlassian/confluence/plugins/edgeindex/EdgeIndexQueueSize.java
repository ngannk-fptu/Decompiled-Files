/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.stereotype.Component
 */
package com.atlassian.confluence.plugins.edgeindex;

import com.atlassian.confluence.plugins.edgeindex.EdgeIndexQueueSizeMBean;
import com.atlassian.confluence.plugins.edgeindex.EdgeIndexTaskQueue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class EdgeIndexQueueSize
implements EdgeIndexQueueSizeMBean {
    private final EdgeIndexTaskQueue queue;

    @Autowired
    public EdgeIndexQueueSize(EdgeIndexTaskQueue queue) {
        this.queue = queue;
    }

    @Override
    public double getValue() {
        return this.queue.getSize();
    }
}


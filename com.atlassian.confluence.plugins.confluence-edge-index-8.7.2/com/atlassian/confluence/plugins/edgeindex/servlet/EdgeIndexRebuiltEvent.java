/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.joda.time.Period
 */
package com.atlassian.confluence.plugins.edgeindex.servlet;

import java.io.Serializable;
import org.joda.time.Period;

public class EdgeIndexRebuiltEvent
implements Serializable {
    private final Period since;

    public EdgeIndexRebuiltEvent(Period since) {
        this.since = since;
    }

    public Period getSince() {
        return this.since;
    }
}


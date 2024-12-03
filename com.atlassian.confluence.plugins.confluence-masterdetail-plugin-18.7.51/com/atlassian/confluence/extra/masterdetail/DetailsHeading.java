/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.extra.masterdetail;

public class DetailsHeading {
    private final String heading;
    private final String renderedHeading;

    public DetailsHeading(String heading, String renderedHeading) {
        this.heading = heading;
        this.renderedHeading = renderedHeading;
    }

    public String getHeading() {
        return this.heading;
    }

    public String getRenderedHeading() {
        return this.renderedHeading;
    }
}


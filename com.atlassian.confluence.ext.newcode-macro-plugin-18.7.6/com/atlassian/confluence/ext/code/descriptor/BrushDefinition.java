/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.ext.code.descriptor;

public final class BrushDefinition {
    private String location;
    private String webResourceId;

    public BrushDefinition(String location, String webResourceId) {
        this.location = location;
        this.webResourceId = webResourceId;
    }

    public String getLocation() {
        return this.location;
    }

    public String getWebResourceId() {
        return this.webResourceId;
    }
}


/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.plugin.persistence;

import com.atlassian.confluence.plugin.persistence.AbstractPluginData;
import java.io.InputStream;

public class PluginData
extends AbstractPluginData {
    private InputStream data;

    public InputStream getData() {
        return this.data;
    }

    public void setData(InputStream data) {
        this.data = data;
    }
}


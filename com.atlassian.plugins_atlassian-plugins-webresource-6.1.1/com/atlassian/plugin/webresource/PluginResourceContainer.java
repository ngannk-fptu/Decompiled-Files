/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.plugin.webresource;

import com.atlassian.plugin.webresource.impl.snapshot.Bundle;
import com.atlassian.plugin.webresource.impl.snapshot.Snapshot;
import java.util.ArrayList;
import java.util.Date;

public class PluginResourceContainer
extends Bundle {
    public PluginResourceContainer(Snapshot snapshot, String pluginKey, Date updatedAt, String version) {
        super(snapshot, pluginKey, new ArrayList<String>(), updatedAt, version, false);
    }
}


/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.plugin.webresource.models;

import com.atlassian.plugin.webresource.models.WebResourceContextKey;

public class SyncBatchKey
extends WebResourceContextKey {
    private static final SyncBatchKey KEY = new SyncBatchKey();

    private SyncBatchKey() {
        super("_sync");
    }

    public static SyncBatchKey getInstance() {
        return KEY;
    }

    @Override
    public String toString() {
        return "<wrc!_sync>";
    }
}


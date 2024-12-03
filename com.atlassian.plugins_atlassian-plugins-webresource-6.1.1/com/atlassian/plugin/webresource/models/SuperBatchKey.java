/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.plugin.webresource.models;

import com.atlassian.plugin.webresource.models.WebResourceContextKey;

public class SuperBatchKey
extends WebResourceContextKey {
    private static final SuperBatchKey KEY = new SuperBatchKey();

    private SuperBatchKey() {
        super("_super");
    }

    public static SuperBatchKey getInstance() {
        return KEY;
    }

    @Override
    public String toString() {
        return "<wrc!_super>";
    }
}


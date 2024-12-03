/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.atlassian.util.concurrent.LazyReference
 */
package com.atlassian.analytics.client.pipeline.serialize.properties.extractors.mau;

import io.atlassian.util.concurrent.LazyReference;

public class IsMauEventAvailable
extends LazyReference<Boolean> {
    protected Boolean create() throws Exception {
        try {
            Thread.currentThread().getContextClassLoader().loadClass("com.atlassian.analytics.api.events.MauEvent");
            return true;
        }
        catch (ClassNotFoundException ignored) {
            return false;
        }
    }
}


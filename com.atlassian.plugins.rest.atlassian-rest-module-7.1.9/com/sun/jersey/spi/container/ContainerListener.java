/*
 * Decompiled with CFR 0.152.
 */
package com.sun.jersey.spi.container;

import com.sun.jersey.spi.container.ReloadListener;

public interface ContainerListener
extends ReloadListener {
    @Override
    public void onReload();
}


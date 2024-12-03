/*
 * Decompiled with CFR 0.152.
 */
package org.xhtmlrenderer.event;

public interface DocumentListener {
    public void documentStarted();

    public void documentLoaded();

    public void onLayoutException(Throwable var1);

    public void onRenderException(Throwable var1);
}


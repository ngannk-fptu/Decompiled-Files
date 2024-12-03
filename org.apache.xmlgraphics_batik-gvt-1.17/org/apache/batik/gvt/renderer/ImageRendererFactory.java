/*
 * Decompiled with CFR 0.152.
 */
package org.apache.batik.gvt.renderer;

import org.apache.batik.gvt.renderer.ImageRenderer;
import org.apache.batik.gvt.renderer.RendererFactory;

public interface ImageRendererFactory
extends RendererFactory {
    public ImageRenderer createStaticImageRenderer();

    public ImageRenderer createDynamicImageRenderer();
}


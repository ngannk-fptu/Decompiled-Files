/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.batik.util.Platform
 */
package org.apache.batik.gvt.renderer;

import org.apache.batik.gvt.renderer.DynamicRenderer;
import org.apache.batik.gvt.renderer.ImageRenderer;
import org.apache.batik.gvt.renderer.ImageRendererFactory;
import org.apache.batik.gvt.renderer.MacRenderer;
import org.apache.batik.gvt.renderer.Renderer;
import org.apache.batik.gvt.renderer.StaticRenderer;
import org.apache.batik.util.Platform;

public class ConcreteImageRendererFactory
implements ImageRendererFactory {
    @Override
    public Renderer createRenderer() {
        return this.createStaticImageRenderer();
    }

    @Override
    public ImageRenderer createStaticImageRenderer() {
        if (Platform.isOSX) {
            return new MacRenderer();
        }
        return new StaticRenderer();
    }

    @Override
    public ImageRenderer createDynamicImageRenderer() {
        if (Platform.isOSX) {
            return new MacRenderer();
        }
        return new DynamicRenderer();
    }
}


/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xmlgraphics.image.loader.impl;

import java.awt.GraphicsEnvironment;
import java.awt.Toolkit;
import org.apache.xmlgraphics.image.loader.ImageContext;

public class DefaultImageContext
implements ImageContext {
    private final float sourceResolution = GraphicsEnvironment.isHeadless() ? 72.0f : (float)Toolkit.getDefaultToolkit().getScreenResolution();

    @Override
    public float getSourceResolution() {
        return this.sourceResolution;
    }
}


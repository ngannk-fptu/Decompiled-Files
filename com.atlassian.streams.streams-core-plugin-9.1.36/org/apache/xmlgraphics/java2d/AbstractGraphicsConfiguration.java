/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xmlgraphics.java2d;

import java.awt.GraphicsConfiguration;
import java.awt.image.VolatileImage;

public abstract class AbstractGraphicsConfiguration
extends GraphicsConfiguration {
    @Override
    public VolatileImage createCompatibleVolatileImage(int width, int height) {
        return null;
    }

    @Override
    public VolatileImage createCompatibleVolatileImage(int width, int height, int transparency) {
        return null;
    }
}


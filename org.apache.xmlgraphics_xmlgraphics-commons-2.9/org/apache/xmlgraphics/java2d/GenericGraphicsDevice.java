/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xmlgraphics.java2d;

import java.awt.GraphicsConfigTemplate;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;

public class GenericGraphicsDevice
extends GraphicsDevice {
    private final GraphicsConfiguration gc;

    public GenericGraphicsDevice(GraphicsConfiguration gc) {
        this.gc = gc;
    }

    @Override
    public GraphicsConfiguration getBestConfiguration(GraphicsConfigTemplate gct) {
        return this.gc;
    }

    @Override
    public GraphicsConfiguration[] getConfigurations() {
        return new GraphicsConfiguration[]{this.gc};
    }

    @Override
    public GraphicsConfiguration getDefaultConfiguration() {
        return this.gc;
    }

    @Override
    public String getIDstring() {
        return this.toString();
    }

    @Override
    public int getType() {
        return 1;
    }
}


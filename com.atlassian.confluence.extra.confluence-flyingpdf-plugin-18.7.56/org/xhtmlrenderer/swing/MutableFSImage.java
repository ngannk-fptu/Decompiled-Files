/*
 * Decompiled with CFR 0.152.
 */
package org.xhtmlrenderer.swing;

import java.awt.EventQueue;
import java.awt.image.BufferedImage;
import java.util.logging.Level;
import org.xhtmlrenderer.swing.AWTFSImage;
import org.xhtmlrenderer.swing.RepaintListener;
import org.xhtmlrenderer.util.ImageUtil;
import org.xhtmlrenderer.util.XRLog;

public class MutableFSImage
extends AWTFSImage {
    private volatile BufferedImage img;
    private final RepaintListener repaintListener;
    private volatile boolean loaded;

    public MutableFSImage(RepaintListener repaintListener) {
        this.repaintListener = repaintListener;
        this.img = ImageUtil.createTransparentImage(10, 10);
    }

    @Override
    public synchronized BufferedImage getImage() {
        return this.img;
    }

    @Override
    public synchronized int getWidth() {
        return this.img.getWidth(null);
    }

    @Override
    public synchronized int getHeight() {
        return this.img.getHeight(null);
    }

    @Override
    public synchronized void scale(int width, int height) {
        this.img.getScaledInstance(width, height, 1);
    }

    public synchronized void setImage(String uri, BufferedImage newImg, boolean wasScaled) {
        assert (EventQueue.isDispatchThread()) : "setImage() must be called on EDT";
        this.img = newImg;
        this.loaded = true;
        XRLog.general(Level.FINE, "Mutable image " + uri + " loaded, repaint requested");
        this.repaintListener.repaintRequested(wasScaled);
    }

    public boolean isLoaded() {
        return this.loaded;
    }
}


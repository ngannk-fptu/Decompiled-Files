/*
 * Decompiled with CFR 0.152.
 */
package com.sun.pdfview;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

public class RefImage
extends BufferedImage {
    private Graphics2D g;

    public RefImage(int width, int height, int type) {
        super(width, height, type);
    }

    @Override
    public Graphics2D createGraphics() {
        if (this.g == null) {
            this.g = super.createGraphics();
        }
        return this.g;
    }
}


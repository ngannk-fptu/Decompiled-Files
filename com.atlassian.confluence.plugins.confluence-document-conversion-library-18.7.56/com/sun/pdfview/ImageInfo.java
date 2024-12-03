/*
 * Decompiled with CFR 0.152.
 */
package com.sun.pdfview;

import java.awt.Color;
import java.awt.geom.Rectangle2D;

public class ImageInfo {
    int width;
    int height;
    Rectangle2D clip;
    Color bgColor;

    public ImageInfo(int width, int height, Rectangle2D clip) {
        this(width, height, clip, Color.WHITE);
    }

    public ImageInfo(int width, int height, Rectangle2D clip, Color bgColor) {
        this.width = width;
        this.height = height;
        this.clip = clip;
        this.bgColor = bgColor;
    }

    public int hashCode() {
        int code = this.width ^ this.height << 16;
        if (this.clip != null) {
            code ^= ((int)this.clip.getWidth() | (int)this.clip.getHeight()) << 8;
            code ^= (int)this.clip.getMinX() | (int)this.clip.getMinY();
        }
        return code;
    }

    public boolean equals(Object o) {
        if (!(o instanceof ImageInfo)) {
            return false;
        }
        ImageInfo ii = (ImageInfo)o;
        if (this.width != ii.width || this.height != ii.height) {
            return false;
        }
        if (this.clip != null && ii.clip != null) {
            return this.clip.equals(ii.clip);
        }
        return this.clip == null && ii.clip == null;
    }
}


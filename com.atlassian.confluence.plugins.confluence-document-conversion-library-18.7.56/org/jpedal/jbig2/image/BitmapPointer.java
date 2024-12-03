/*
 * Decompiled with CFR 0.152.
 */
package org.jpedal.jbig2.image;

import org.jpedal.jbig2.image.JBIG2Bitmap;

public class BitmapPointer {
    private int x;
    private int y;
    private int width;
    private int height;
    private JBIG2Bitmap bitmap;

    public BitmapPointer(JBIG2Bitmap jBIG2Bitmap) {
        this.bitmap = jBIG2Bitmap;
        this.height = jBIG2Bitmap.getHeight();
        this.width = jBIG2Bitmap.getWidth();
    }

    public void setPointer(int n, int n2) {
        this.x = n;
        this.y = n2;
    }

    public int nextPixel() {
        if (this.y < 0 || this.y >= this.height || this.x >= this.width) {
            return 0;
        }
        if (this.x < 0) {
            ++this.x;
            return 0;
        }
        int n = this.bitmap.getPixel(this.x, this.y);
        ++this.x;
        return n;
    }
}


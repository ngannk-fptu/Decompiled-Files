/*
 * Decompiled with CFR 0.152.
 */
package com.twelvemonkeys.imageio.plugins.bmp;

import com.twelvemonkeys.imageio.plugins.bmp.BitmapDescriptor;
import com.twelvemonkeys.imageio.plugins.bmp.DIBHeader;
import com.twelvemonkeys.imageio.plugins.bmp.DirectoryEntry;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.awt.image.IndexColorModel;
import java.awt.image.WritableRaster;
import java.util.Hashtable;

class BitmapIndexed
extends BitmapDescriptor {
    protected final int[] bits = new int[this.getWidth() * this.getHeight()];
    protected final int[] colors = new int[this.getColorCount() + 1];

    public BitmapIndexed(DirectoryEntry directoryEntry, DIBHeader dIBHeader) {
        super(directoryEntry, dIBHeader);
    }

    public BufferedImage createImageIndexed() {
        IndexColorModel indexColorModel = this.createColorModel();
        Hashtable<String, Point> hashtable = null;
        if (this.entry instanceof DirectoryEntry.CUREntry) {
            hashtable = new Hashtable<String, Point>(1);
            hashtable.put("cursor_hotspot", ((DirectoryEntry.CUREntry)this.entry).getHotspot());
        }
        BufferedImage bufferedImage = new BufferedImage(indexColorModel, indexColorModel.createCompatibleWritableRaster(this.getWidth(), this.getHeight()), indexColorModel.isAlphaPremultiplied(), hashtable);
        WritableRaster writableRaster = bufferedImage.getRaster();
        int n = indexColorModel.getTransparentPixel();
        for (int i = 0; i < this.getHeight(); ++i) {
            for (int j = 0; j < this.getWidth(); ++j) {
                if (!this.mask.isTransparent(j, i)) continue;
                this.bits[j + this.getWidth() * i] = n;
            }
        }
        writableRaster.setSamples(0, 0, this.getWidth(), this.getHeight(), 0, this.bits);
        return bufferedImage;
    }

    IndexColorModel createColorModel() {
        int n = this.getBitCount();
        int n2 = this.colors.length;
        int n3 = -1;
        if (n2 > 1 << this.getBitCount()) {
            int n4 = BitmapIndexed.findTransparentIndexMaybeRemap(this.colors, this.bits);
            if (n4 == -1) {
                ++n;
                n3 = this.colors.length - 1;
            } else {
                n3 = n4;
                --n2;
            }
        }
        return new IndexColorModel(n, n2, this.colors, 0, true, n3, n <= 8 ? 0 : 1);
    }

    private static int findTransparentIndexMaybeRemap(int[] nArray, int[] nArray2) {
        int n;
        int n2;
        boolean[] blArray = new boolean[nArray.length - 1];
        for (int n3 : nArray2) {
            if (blArray[n3]) continue;
            blArray[n3] = true;
        }
        for (n2 = 0; n2 < blArray.length; ++n2) {
            if (blArray[n2]) continue;
            return n2;
        }
        n2 = -1;
        int n4 = -1;
        block2: for (n = 0; n2 == -1 && n < nArray.length - 1; ++n) {
            for (int n3 = n + 1; n3 < nArray.length - 1; ++n3) {
                if (nArray[n] != nArray[n3]) continue;
                n2 = n3;
                n4 = n;
                continue block2;
            }
        }
        if (n2 != -1) {
            for (n = 0; n < nArray2.length; ++n) {
                if (nArray2[n] != n2) continue;
                nArray2[n] = n4;
            }
        }
        return n2;
    }

    @Override
    public BufferedImage getImage() {
        if (this.image == null) {
            this.image = this.createImageIndexed();
        }
        return this.image;
    }
}


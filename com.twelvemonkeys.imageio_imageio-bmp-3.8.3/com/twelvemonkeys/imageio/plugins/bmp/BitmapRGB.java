/*
 * Decompiled with CFR 0.152.
 */
package com.twelvemonkeys.imageio.plugins.bmp;

import com.twelvemonkeys.imageio.plugins.bmp.BitmapDescriptor;
import com.twelvemonkeys.imageio.plugins.bmp.DIBHeader;
import com.twelvemonkeys.imageio.plugins.bmp.DirectoryEntry;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;

class BitmapRGB
extends BitmapDescriptor {
    public BitmapRGB(DirectoryEntry directoryEntry, DIBHeader dIBHeader) {
        super(directoryEntry, dIBHeader);
    }

    @Override
    public BufferedImage getImage() {
        if (this.mask != null) {
            this.image = this.createMaskedImage();
            this.mask = null;
        }
        return this.image;
    }

    private BufferedImage createMaskedImage() {
        BufferedImage bufferedImage = new BufferedImage(this.getWidth(), this.getHeight(), 6);
        Graphics2D graphics2D = bufferedImage.createGraphics();
        try {
            graphics2D.drawImage((Image)this.image, 0, 0, null);
        }
        finally {
            graphics2D.dispose();
        }
        WritableRaster writableRaster = bufferedImage.getAlphaRaster();
        byte[] byArray = new byte[]{0};
        for (int i = 0; i < this.getHeight(); ++i) {
            for (int j = 0; j < this.getWidth(); ++j) {
                if (!this.mask.isTransparent(j, i)) continue;
                writableRaster.setDataElements(j, i, byArray);
            }
        }
        return bufferedImage;
    }
}


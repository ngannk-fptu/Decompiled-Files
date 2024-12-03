/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.twelvemonkeys.lang.Validate
 */
package com.twelvemonkeys.imageio.util;

import com.twelvemonkeys.lang.Validate;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.WritableRaster;
import javax.imageio.ImageTypeSpecifier;

final class IndexedImageTypeSpecifier
extends ImageTypeSpecifier {
    IndexedImageTypeSpecifier(ColorModel colorModel) {
        super((ColorModel)Validate.notNull((Object)colorModel, (String)"colorModel"), colorModel.createCompatibleSampleModel(1, 1));
    }

    @Override
    public final BufferedImage createBufferedImage(int n, int n2) {
        try {
            WritableRaster writableRaster = this.colorModel.createCompatibleWritableRaster(n, n2);
            return new BufferedImage(this.colorModel, writableRaster, this.colorModel.isAlphaPremultiplied(), null);
        }
        catch (NegativeArraySizeException negativeArraySizeException) {
            throw new IllegalArgumentException("Array size > Integer.MAX_VALUE!");
        }
    }
}


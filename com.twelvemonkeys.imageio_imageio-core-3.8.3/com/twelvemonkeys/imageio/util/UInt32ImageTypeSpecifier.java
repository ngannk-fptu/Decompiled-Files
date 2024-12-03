/*
 * Decompiled with CFR 0.152.
 */
package com.twelvemonkeys.imageio.util;

import com.twelvemonkeys.imageio.color.UInt32ColorModel;
import java.awt.color.ColorSpace;
import java.awt.image.BandedSampleModel;
import java.awt.image.PixelInterleavedSampleModel;
import java.awt.image.SampleModel;
import javax.imageio.ImageTypeSpecifier;

final class UInt32ImageTypeSpecifier
extends ImageTypeSpecifier {
    private UInt32ImageTypeSpecifier(ColorSpace colorSpace, boolean bl, boolean bl2, SampleModel sampleModel) {
        super(new UInt32ColorModel(colorSpace, bl, bl2), sampleModel);
    }

    static ImageTypeSpecifier createInterleaved(ColorSpace colorSpace, int[] nArray, boolean bl, boolean bl2) {
        return new UInt32ImageTypeSpecifier(colorSpace, bl, bl2, new PixelInterleavedSampleModel(3, 1, 1, colorSpace.getNumComponents() + (bl ? 1 : 0), colorSpace.getNumComponents() + (bl ? 1 : 0), nArray));
    }

    static ImageTypeSpecifier createBanded(ColorSpace colorSpace, int[] nArray, int[] nArray2, boolean bl, boolean bl2) {
        return new UInt32ImageTypeSpecifier(colorSpace, bl, bl2, new BandedSampleModel(3, 1, 1, 1, nArray, nArray2));
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof UInt32ImageTypeSpecifier)) {
            return false;
        }
        UInt32ImageTypeSpecifier uInt32ImageTypeSpecifier = (UInt32ImageTypeSpecifier)object;
        return this.colorModel.equals(uInt32ImageTypeSpecifier.colorModel) && this.sampleModel.equals(uInt32ImageTypeSpecifier.sampleModel);
    }
}


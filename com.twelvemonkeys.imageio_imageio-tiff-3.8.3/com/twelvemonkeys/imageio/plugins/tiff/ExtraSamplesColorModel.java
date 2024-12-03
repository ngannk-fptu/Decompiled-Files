/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.twelvemonkeys.lang.Validate
 */
package com.twelvemonkeys.imageio.plugins.tiff;

import com.twelvemonkeys.lang.Validate;
import java.awt.color.ColorSpace;
import java.awt.image.ComponentColorModel;
import java.awt.image.ComponentSampleModel;
import java.awt.image.DataBuffer;
import java.awt.image.SampleModel;
import java.awt.image.WritableRaster;
import java.util.Objects;

final class ExtraSamplesColorModel
extends ComponentColorModel {
    private final int numComponents;
    private final int componentSize;

    ExtraSamplesColorModel(ColorSpace colorSpace, boolean bl, boolean bl2, int n, int n2) {
        super(colorSpace, bl, bl2, 3, n);
        Validate.isTrue((n2 > 0 ? 1 : 0) != 0, (String)"Extra components must be > 0");
        this.numComponents = colorSpace.getNumComponents() + (bl ? 1 : 0) + n2;
        this.componentSize = DataBuffer.getDataTypeSize(n);
    }

    @Override
    public int getNumComponents() {
        return this.numComponents;
    }

    @Override
    public int getComponentSize(int n) {
        return this.componentSize;
    }

    @Override
    public boolean isCompatibleSampleModel(SampleModel sampleModel) {
        if (!(sampleModel instanceof ComponentSampleModel)) {
            return false;
        }
        return this.numComponents == sampleModel.getNumBands() && this.transferType == sampleModel.getTransferType();
    }

    @Override
    public WritableRaster getAlphaRaster(WritableRaster writableRaster) {
        if (!this.hasAlpha()) {
            return null;
        }
        int n = writableRaster.getMinX();
        int n2 = writableRaster.getMinY();
        int[] nArray = new int[]{this.getAlphaComponent()};
        return writableRaster.createWritableChild(n, n2, writableRaster.getWidth(), writableRaster.getHeight(), n, n2, nArray);
    }

    private int getAlphaComponent() {
        return super.getNumComponents() - 1;
    }

    @Override
    public Object getDataElements(int n, Object object) {
        return super.getDataElements(n, object == null ? this.createDataArray() : object);
    }

    private Object createDataArray() {
        switch (this.transferType) {
            case 0: {
                return new byte[this.numComponents];
            }
            case 1: 
            case 2: {
                return new short[this.numComponents];
            }
            case 3: {
                return new int[this.numComponents];
            }
            case 4: {
                return new float[this.numComponents];
            }
            case 5: {
                return new double[this.numComponents];
            }
        }
        throw new IllegalArgumentException("This method has not been implemented for transferType " + this.transferType);
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (object == null || this.getClass() != object.getClass()) {
            return false;
        }
        ExtraSamplesColorModel extraSamplesColorModel = (ExtraSamplesColorModel)object;
        if (this.hasAlpha() != extraSamplesColorModel.hasAlpha() || this.isAlphaPremultiplied() != extraSamplesColorModel.isAlphaPremultiplied() || this.getPixelSize() != extraSamplesColorModel.getPixelSize() || this.getTransparency() != extraSamplesColorModel.getTransparency() || this.numComponents != extraSamplesColorModel.numComponents) {
            return false;
        }
        int[] nArray = this.getComponentSize();
        int[] nArray2 = extraSamplesColorModel.getComponentSize();
        if (nArray == null || nArray2 == null) {
            return nArray == null && nArray2 == null;
        }
        for (int i = 0; i < nArray.length; ++i) {
            if (nArray[i] == nArray2[i]) continue;
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), this.numComponents, this.componentSize);
    }
}


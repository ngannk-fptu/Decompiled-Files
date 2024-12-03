/*
 * Decompiled with CFR 0.152.
 */
package javax.media.jai;

import java.awt.image.ComponentSampleModel;
import java.awt.image.SampleModel;

public final class RasterFormatTag {
    private static final int COPY_MASK = 384;
    private static final int UNCOPIED = 0;
    private static final int COPIED = 128;
    private int formatTagID;
    private int[] bankIndices;
    private int numBands;
    private int[] bandOffsets;
    private int pixelStride;
    private boolean isPixelSequential;

    public RasterFormatTag(SampleModel sampleModel, int formatTagID) {
        this.formatTagID = formatTagID;
        if ((formatTagID & 0x180) == 0) {
            ComponentSampleModel csm = (ComponentSampleModel)sampleModel;
            this.bankIndices = csm.getBankIndices();
            this.numBands = csm.getNumDataElements();
            this.bandOffsets = csm.getBandOffsets();
            this.pixelStride = csm.getPixelStride();
            if (this.pixelStride != this.bandOffsets.length) {
                this.isPixelSequential = false;
            } else {
                this.isPixelSequential = true;
                for (int i = 0; i < this.bandOffsets.length; ++i) {
                    if (this.bandOffsets[i] >= this.pixelStride || this.bankIndices[i] != this.bankIndices[0]) {
                        this.isPixelSequential = false;
                    }
                    for (int j = i + 1; j < this.bandOffsets.length; ++j) {
                        if (this.bandOffsets[i] != this.bandOffsets[j]) continue;
                        this.isPixelSequential = false;
                    }
                    if (this.isPixelSequential) {
                        continue;
                    }
                    break;
                }
            }
        } else if ((formatTagID & 0x180) == 128) {
            this.numBands = sampleModel.getNumBands();
            this.bandOffsets = new int[this.numBands];
            this.pixelStride = this.numBands;
            this.bankIndices = new int[this.numBands];
            for (int i = 0; i < this.numBands; ++i) {
                this.bandOffsets[i] = i;
                this.bankIndices[i] = 0;
            }
            this.isPixelSequential = true;
        }
    }

    public final boolean isPixelSequential() {
        return this.isPixelSequential;
    }

    public final int getFormatTagID() {
        return this.formatTagID;
    }

    public final int[] getBankIndices() {
        if (this.isPixelSequential) {
            return this.bankIndices;
        }
        return null;
    }

    public final int getNumBands() {
        return this.numBands;
    }

    public final int[] getBandOffsets() {
        if (this.isPixelSequential) {
            return this.bandOffsets;
        }
        return null;
    }

    public final int getPixelStride() {
        return this.pixelStride;
    }
}


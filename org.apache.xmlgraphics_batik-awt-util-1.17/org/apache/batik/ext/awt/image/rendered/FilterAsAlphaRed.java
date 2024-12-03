/*
 * Decompiled with CFR 0.152.
 */
package org.apache.batik.ext.awt.image.rendered;

import java.awt.color.ColorSpace;
import java.awt.image.ColorModel;
import java.awt.image.ComponentColorModel;
import java.awt.image.DataBufferByte;
import java.awt.image.PixelInterleavedSampleModel;
import java.awt.image.Raster;
import java.awt.image.SampleModel;
import java.awt.image.WritableRaster;
import org.apache.batik.ext.awt.ColorSpaceHintKey;
import org.apache.batik.ext.awt.image.rendered.AbstractRed;
import org.apache.batik.ext.awt.image.rendered.Any2LumRed;
import org.apache.batik.ext.awt.image.rendered.CachableRed;

public class FilterAsAlphaRed
extends AbstractRed {
    public FilterAsAlphaRed(CachableRed src) {
        super(new Any2LumRed(src), src.getBounds(), (ColorModel)new ComponentColorModel(ColorSpace.getInstance(1003), new int[]{8}, false, false, 1, 0), (SampleModel)new PixelInterleavedSampleModel(0, src.getSampleModel().getWidth(), src.getSampleModel().getHeight(), 1, src.getSampleModel().getWidth(), new int[]{0}), src.getTileGridXOffset(), src.getTileGridYOffset(), null);
        this.props.put("org.apache.batik.gvt.filter.Colorspace", ColorSpaceHintKey.VALUE_COLORSPACE_ALPHA);
    }

    @Override
    public WritableRaster copyData(WritableRaster wr) {
        CachableRed srcRed = (CachableRed)this.getSources().get(0);
        SampleModel sm = srcRed.getSampleModel();
        if (sm.getNumBands() == 1) {
            return srcRed.copyData(wr);
        }
        Raster srcRas = srcRed.getData(wr.getBounds());
        PixelInterleavedSampleModel srcSM = (PixelInterleavedSampleModel)srcRas.getSampleModel();
        DataBufferByte srcDB = (DataBufferByte)srcRas.getDataBuffer();
        byte[] src = srcDB.getData();
        PixelInterleavedSampleModel dstSM = (PixelInterleavedSampleModel)wr.getSampleModel();
        DataBufferByte dstDB = (DataBufferByte)wr.getDataBuffer();
        byte[] dst = dstDB.getData();
        int srcX0 = srcRas.getMinX() - srcRas.getSampleModelTranslateX();
        int srcY0 = srcRas.getMinY() - srcRas.getSampleModelTranslateY();
        int dstX0 = wr.getMinX() - wr.getSampleModelTranslateX();
        int dstX1 = dstX0 + wr.getWidth() - 1;
        int dstY0 = wr.getMinY() - wr.getSampleModelTranslateY();
        int srcStep = srcSM.getPixelStride();
        int[] offsets = srcSM.getBandOffsets();
        int srcLOff = offsets[0];
        int srcAOff = offsets[1];
        if (srcRed.getColorModel().isAlphaPremultiplied()) {
            for (int y = 0; y < srcRas.getHeight(); ++y) {
                int srcI = srcDB.getOffset() + srcSM.getOffset(srcX0, srcY0);
                int dstI = dstDB.getOffset() + dstSM.getOffset(dstX0, dstY0);
                int dstE = dstDB.getOffset() + dstSM.getOffset(dstX1 + 1, dstY0);
                srcI += srcLOff;
                while (dstI < dstE) {
                    dst[dstI++] = src[srcI];
                    srcI += srcStep;
                }
                ++srcY0;
                ++dstY0;
            }
        } else {
            srcAOff -= srcLOff;
            for (int y = 0; y < srcRas.getHeight(); ++y) {
                int srcI = srcDB.getOffset() + srcSM.getOffset(srcX0, srcY0);
                int dstI = dstDB.getOffset() + dstSM.getOffset(dstX0, dstY0);
                int dstE = dstDB.getOffset() + dstSM.getOffset(dstX1 + 1, dstY0);
                srcI += srcLOff;
                while (dstI < dstE) {
                    int sl = src[srcI] & 0xFF;
                    int sa = src[srcI + srcAOff] & 0xFF;
                    dst[dstI++] = (byte)(sl * sa + 128 >> 8);
                    srcI += srcStep;
                }
                ++srcY0;
                ++dstY0;
            }
        }
        return wr;
    }
}


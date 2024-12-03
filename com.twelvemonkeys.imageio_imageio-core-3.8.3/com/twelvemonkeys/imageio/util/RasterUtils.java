/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.twelvemonkeys.lang.Validate
 */
package com.twelvemonkeys.imageio.util;

import com.twelvemonkeys.lang.Validate;
import java.awt.Point;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferInt;
import java.awt.image.PixelInterleavedSampleModel;
import java.awt.image.Raster;
import java.awt.image.SampleModel;
import java.awt.image.SinglePixelPackedSampleModel;
import java.awt.image.WritableRaster;

public final class RasterUtils {
    private RasterUtils() {
    }

    public static Raster asByteRaster(Raster raster) {
        return RasterUtils.asByteRaster0(raster);
    }

    public static WritableRaster asByteRaster(WritableRaster writableRaster) {
        return (WritableRaster)RasterUtils.asByteRaster0(writableRaster);
    }

    private static Raster asByteRaster0(Raster raster) {
        switch (raster.getTransferType()) {
            case 0: {
                return raster;
            }
            case 3: {
                SampleModel sampleModel = raster.getSampleModel();
                if (!(sampleModel instanceof SinglePixelPackedSampleModel)) {
                    throw new IllegalArgumentException(String.format("Requires SinglePixelPackedSampleModel, %s not supported", sampleModel.getClass().getSimpleName()));
                }
                final DataBufferInt dataBufferInt = (DataBufferInt)raster.getDataBuffer();
                int n = raster.getWidth();
                int n2 = raster.getHeight();
                int n3 = dataBufferInt.getSize();
                return new WritableRaster(new PixelInterleavedSampleModel(0, n, n2, 4, n * 4, RasterUtils.createBandOffsets((SinglePixelPackedSampleModel)sampleModel)), new DataBuffer(0, n3 * 4){
                    final int[] MASKS;
                    {
                        super(n, n2);
                        this.MASKS = new int[]{-256, -65281, -16711681, 0xFFFFFF};
                    }

                    @Override
                    public int getElem(int n, int n2) {
                        int n3 = n2 / 4;
                        int n4 = n2 % 4 * 8;
                        return dataBufferInt.getElem(n3) >>> n4 & 0xFF;
                    }

                    @Override
                    public void setElem(int n, int n2, int n3) {
                        int n4 = n2 / 4;
                        int n5 = n2 % 4;
                        int n6 = n5 * 8;
                        int n7 = dataBufferInt.getElem(n4) & this.MASKS[n5] | (n3 & 0xFF) << n6;
                        dataBufferInt.setElem(n4, n7);
                    }
                }, new Point()){};
            }
        }
        throw new IllegalArgumentException(String.format("Raster type %d not supported", raster.getTransferType()));
    }

    private static int[] createBandOffsets(SinglePixelPackedSampleModel singlePixelPackedSampleModel) {
        Validate.notNull((Object)singlePixelPackedSampleModel, (String)"sampleModel");
        int[] nArray = singlePixelPackedSampleModel.getBitMasks();
        int[] nArray2 = new int[nArray.length];
        for (int i = 0; i < nArray.length; ++i) {
            int n = nArray[i];
            int n2 = 0;
            if (n != 0) {
                while ((n & 0xFF) == 0) {
                    n >>>= 8;
                    ++n2;
                }
            }
            nArray2[i] = n2;
        }
        return nArray2;
    }
}


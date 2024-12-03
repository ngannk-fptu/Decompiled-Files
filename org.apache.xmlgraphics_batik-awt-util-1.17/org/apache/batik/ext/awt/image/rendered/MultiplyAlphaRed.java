/*
 * Decompiled with CFR 0.152.
 */
package org.apache.batik.ext.awt.image.rendered;

import java.awt.Rectangle;
import java.awt.color.ColorSpace;
import java.awt.image.ColorModel;
import java.awt.image.ComponentColorModel;
import java.awt.image.ComponentSampleModel;
import java.awt.image.DataBufferByte;
import java.awt.image.DataBufferInt;
import java.awt.image.PixelInterleavedSampleModel;
import java.awt.image.Raster;
import java.awt.image.SampleModel;
import java.awt.image.SinglePixelPackedSampleModel;
import java.awt.image.WritableRaster;
import java.util.ArrayList;
import java.util.List;
import org.apache.batik.ext.awt.image.rendered.AbstractRed;
import org.apache.batik.ext.awt.image.rendered.CachableRed;

public class MultiplyAlphaRed
extends AbstractRed {
    public MultiplyAlphaRed(CachableRed src, CachableRed alpha) {
        super(MultiplyAlphaRed.makeList(src, alpha), MultiplyAlphaRed.makeBounds(src, alpha), MultiplyAlphaRed.fixColorModel(src), MultiplyAlphaRed.fixSampleModel(src), src.getTileGridXOffset(), src.getTileGridYOffset(), null);
    }

    public boolean is_INT_PACK_BYTE_COMP(SampleModel srcSM, SampleModel alpSM) {
        if (!(srcSM instanceof SinglePixelPackedSampleModel)) {
            return false;
        }
        if (!(alpSM instanceof ComponentSampleModel)) {
            return false;
        }
        if (srcSM.getDataType() != 3) {
            return false;
        }
        if (alpSM.getDataType() != 0) {
            return false;
        }
        SinglePixelPackedSampleModel sppsm = (SinglePixelPackedSampleModel)srcSM;
        int[] masks = sppsm.getBitMasks();
        if (masks.length != 4) {
            return false;
        }
        if (masks[0] != 0xFF0000) {
            return false;
        }
        if (masks[1] != 65280) {
            return false;
        }
        if (masks[2] != 255) {
            return false;
        }
        if (masks[3] != -16777216) {
            return false;
        }
        ComponentSampleModel csm = (ComponentSampleModel)alpSM;
        if (csm.getNumBands() != 1) {
            return false;
        }
        return csm.getPixelStride() == 1;
    }

    public WritableRaster INT_PACK_BYTE_COMP_Impl(WritableRaster wr) {
        CachableRed srcRed = (CachableRed)this.getSources().get(0);
        CachableRed alphaRed = (CachableRed)this.getSources().get(1);
        srcRed.copyData(wr);
        Rectangle rgn = wr.getBounds();
        rgn = rgn.intersection(alphaRed.getBounds());
        Raster r = alphaRed.getData(rgn);
        ComponentSampleModel csm = (ComponentSampleModel)r.getSampleModel();
        int alpScanStride = csm.getScanlineStride();
        DataBufferByte alpDB = (DataBufferByte)r.getDataBuffer();
        int alpBase = alpDB.getOffset() + csm.getOffset(rgn.x - r.getSampleModelTranslateX(), rgn.y - r.getSampleModelTranslateY());
        byte[] alpPixels = alpDB.getBankData()[0];
        SinglePixelPackedSampleModel sppsm = (SinglePixelPackedSampleModel)wr.getSampleModel();
        int srcScanStride = sppsm.getScanlineStride();
        DataBufferInt srcDB = (DataBufferInt)wr.getDataBuffer();
        int srcBase = srcDB.getOffset() + sppsm.getOffset(rgn.x - wr.getSampleModelTranslateX(), rgn.y - wr.getSampleModelTranslateY());
        int[] srcPixels = srcDB.getBankData()[0];
        ColorModel cm = srcRed.getColorModel();
        if (cm.isAlphaPremultiplied()) {
            for (int y = 0; y < rgn.height; ++y) {
                int sp;
                int ap = alpBase + y * alpScanStride;
                int end = sp + rgn.width;
                for (sp = srcBase + y * srcScanStride; sp < end; ++sp) {
                    int a = alpPixels[ap++] & 0xFF;
                    int pix = srcPixels[sp];
                    srcPixels[sp] = ((pix >>> 24) * a & 0xFF00) << 16 | ((pix >>> 16 & 0xFF) * a & 0xFF00) << 8 | (pix >>> 8 & 0xFF) * a & 0xFF00 | ((pix & 0xFF) * a & 0xFF00) >> 8;
                }
            }
        } else {
            for (int y = 0; y < rgn.height; ++y) {
                int sp;
                int ap = alpBase + y * alpScanStride;
                int end = sp + rgn.width;
                for (sp = srcBase + y * srcScanStride; sp < end; ++sp) {
                    int a = alpPixels[ap++] & 0xFF;
                    int sa = srcPixels[sp] >>> 24;
                    srcPixels[sp] = (sa * a & 0xFF00) << 16 | srcPixels[sp] & 0xFFFFFF;
                }
            }
        }
        return wr;
    }

    @Override
    public WritableRaster copyData(WritableRaster wr) {
        CachableRed srcRed = (CachableRed)this.getSources().get(0);
        CachableRed alphaRed = (CachableRed)this.getSources().get(1);
        if (this.is_INT_PACK_BYTE_COMP(srcRed.getSampleModel(), alphaRed.getSampleModel())) {
            return this.INT_PACK_BYTE_COMP_Impl(wr);
        }
        ColorModel cm = srcRed.getColorModel();
        if (cm.hasAlpha()) {
            srcRed.copyData(wr);
            Rectangle rgn = wr.getBounds();
            if (!rgn.intersects(alphaRed.getBounds())) {
                return wr;
            }
            rgn = rgn.intersection(alphaRed.getBounds());
            int[] wrData = null;
            int[] alphaData = null;
            Raster r = alphaRed.getData(rgn);
            int w = rgn.width;
            int bands = wr.getSampleModel().getNumBands();
            if (cm.isAlphaPremultiplied()) {
                for (int y = rgn.y; y < rgn.y + rgn.height; ++y) {
                    wrData = wr.getPixels(rgn.x, y, w, 1, wrData);
                    alphaData = r.getSamples(rgn.x, y, w, 1, 0, alphaData);
                    int i = 0;
                    switch (bands) {
                        case 2: {
                            int a;
                            for (int anAlphaData2 : alphaData) {
                                a = anAlphaData2 & 0xFF;
                                wrData[i] = (wrData[i] & 0xFF) * a >> 8;
                                wrData[++i] = (wrData[i] & 0xFF) * a >> 8;
                                ++i;
                            }
                            break;
                        }
                        case 4: {
                            int a;
                            for (int anAlphaData1 : alphaData) {
                                a = anAlphaData1 & 0xFF;
                                wrData[i] = (wrData[i] & 0xFF) * a >> 8;
                                wrData[++i] = (wrData[i] & 0xFF) * a >> 8;
                                wrData[++i] = (wrData[i] & 0xFF) * a >> 8;
                                wrData[++i] = (wrData[i] & 0xFF) * a >> 8;
                                ++i;
                            }
                            break;
                        }
                        default: {
                            int a;
                            for (int anAlphaData : alphaData) {
                                a = anAlphaData & 0xFF;
                                for (int b = 0; b < bands; ++b) {
                                    wrData[i] = (wrData[i] & 0xFF) * a >> 8;
                                    ++i;
                                }
                            }
                        }
                    }
                    wr.setPixels(rgn.x, y, w, 1, wrData);
                }
            } else {
                int b = srcRed.getSampleModel().getNumBands() - 1;
                for (int y = rgn.y; y < rgn.y + rgn.height; ++y) {
                    wrData = wr.getSamples(rgn.x, y, w, 1, b, wrData);
                    alphaData = r.getSamples(rgn.x, y, w, 1, 0, alphaData);
                    for (int i = 0; i < wrData.length; ++i) {
                        wrData[i] = (wrData[i] & 0xFF) * (alphaData[i] & 0xFF) >> 8;
                    }
                    wr.setSamples(rgn.x, y, w, 1, b, wrData);
                }
            }
            return wr;
        }
        int[] bands = new int[wr.getNumBands() - 1];
        for (int i = 0; i < bands.length; ++i) {
            bands[i] = i;
        }
        WritableRaster subWr = wr.createWritableChild(wr.getMinX(), wr.getMinY(), wr.getWidth(), wr.getHeight(), wr.getMinX(), wr.getMinY(), bands);
        srcRed.copyData(subWr);
        Rectangle rgn = wr.getBounds();
        rgn = rgn.intersection(alphaRed.getBounds());
        bands = new int[]{wr.getNumBands() - 1};
        subWr = wr.createWritableChild(rgn.x, rgn.y, rgn.width, rgn.height, rgn.x, rgn.y, bands);
        alphaRed.copyData(subWr);
        return wr;
    }

    public static List makeList(CachableRed src1, CachableRed src2) {
        ArrayList<CachableRed> ret = new ArrayList<CachableRed>(2);
        ret.add(src1);
        ret.add(src2);
        return ret;
    }

    public static Rectangle makeBounds(CachableRed src1, CachableRed src2) {
        Rectangle r1 = src1.getBounds();
        Rectangle r2 = src2.getBounds();
        return r1.intersection(r2);
    }

    public static SampleModel fixSampleModel(CachableRed src) {
        ColorModel cm = src.getColorModel();
        SampleModel srcSM = src.getSampleModel();
        if (cm.hasAlpha()) {
            return srcSM;
        }
        int w = srcSM.getWidth();
        int h = srcSM.getHeight();
        int b = srcSM.getNumBands() + 1;
        int[] offsets = new int[b];
        for (int i = 0; i < b; ++i) {
            offsets[i] = i;
        }
        return new PixelInterleavedSampleModel(0, w, h, b, w * b, offsets);
    }

    public static ColorModel fixColorModel(CachableRed src) {
        ColorModel cm = src.getColorModel();
        if (cm.hasAlpha()) {
            return cm;
        }
        int b = src.getSampleModel().getNumBands() + 1;
        int[] bits = new int[b];
        for (int i = 0; i < b; ++i) {
            bits[i] = 8;
        }
        ColorSpace cs = cm.getColorSpace();
        return new ComponentColorModel(cs, bits, true, false, 3, 0);
    }
}


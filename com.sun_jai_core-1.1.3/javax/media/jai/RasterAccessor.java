/*
 * Decompiled with CFR 0.152.
 */
package javax.media.jai;

import com.sun.media.jai.util.DataBufferUtils;
import com.sun.media.jai.util.ImageUtil;
import java.awt.Rectangle;
import java.awt.image.ColorModel;
import java.awt.image.ComponentColorModel;
import java.awt.image.ComponentSampleModel;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferByte;
import java.awt.image.DataBufferInt;
import java.awt.image.DataBufferShort;
import java.awt.image.DataBufferUShort;
import java.awt.image.IndexColorModel;
import java.awt.image.Raster;
import java.awt.image.RenderedImage;
import java.awt.image.SampleModel;
import java.awt.image.WritableRaster;
import javax.media.jai.JaiI18N;
import javax.media.jai.RasterFormatTag;
import sun.awt.image.BytePackedRaster;

public class RasterAccessor {
    private static final int COPY_MASK_SHIFT = 7;
    private static final int COPY_MASK_SIZE = 2;
    public static final int COPY_MASK = 384;
    public static final int UNCOPIED = 0;
    public static final int COPIED = 128;
    private static final int EXPANSION_MASK_SHIFT = 9;
    private static final int EXPANSION_MASK_SIZE = 2;
    public static final int EXPANSION_MASK = 1536;
    public static final int DEFAULTEXPANSION = 0;
    public static final int EXPANDED = 512;
    public static final int UNEXPANDED = 1024;
    public static final int DATATYPE_MASK = 127;
    public static final int TAG_BYTE_UNCOPIED = 0;
    public static final int TAG_USHORT_UNCOPIED = 1;
    public static final int TAG_SHORT_UNCOPIED = 2;
    public static final int TAG_INT_UNCOPIED = 3;
    public static final int TAG_FLOAT_UNCOPIED = 4;
    public static final int TAG_DOUBLE_UNCOPIED = 5;
    public static final int TAG_INT_COPIED = 131;
    public static final int TAG_FLOAT_COPIED = 132;
    public static final int TAG_DOUBLE_COPIED = 133;
    public static final int TAG_BYTE_EXPANDED = 512;
    private static final int TAG_BINARY = 1152;
    protected Raster raster;
    protected int rectWidth;
    protected int rectHeight;
    protected int rectX;
    protected int rectY;
    protected int formatTagID;
    protected byte[] binaryDataArray;
    protected byte[][] byteDataArrays;
    protected short[][] shortDataArrays;
    protected int[][] intDataArrays;
    protected float[][] floatDataArrays;
    protected double[][] doubleDataArrays;
    protected int[] bandDataOffsets;
    protected int[] bandOffsets;
    protected int numBands;
    protected int scanlineStride;
    protected int pixelStride;

    public static RasterFormatTag[] findCompatibleTags(RenderedImage[] srcs, RenderedImage dst) {
        int dstDataType;
        int[] tagIDs = srcs != null ? new int[srcs.length + 1] : new int[1];
        SampleModel dstSampleModel = dst.getSampleModel();
        int defaultDataType = dstDataType = dstSampleModel.getTransferType();
        boolean binaryDst = ImageUtil.isBinary(dstSampleModel);
        if (binaryDst) {
            defaultDataType = 0;
        } else if (dstDataType == 0 || dstDataType == 1 || dstDataType == 2) {
            defaultDataType = 3;
        }
        if (srcs != null) {
            int numSources = srcs.length;
            for (int i = 0; i < numSources; ++i) {
                SampleModel srcSampleModel = srcs[i].getSampleModel();
                int srcDataType = srcSampleModel.getTransferType();
                if (binaryDst && ImageUtil.isBinary(srcSampleModel) || srcDataType <= defaultDataType) continue;
                defaultDataType = srcDataType;
            }
        }
        int tagID = defaultDataType | 0x80;
        if (dstSampleModel instanceof ComponentSampleModel) {
            if (srcs != null) {
                int i;
                int numSources = srcs.length;
                for (i = 0; i < numSources; ++i) {
                    SampleModel srcSampleModel = srcs[i].getSampleModel();
                    int srcDataType = srcSampleModel.getTransferType();
                    if (!(srcSampleModel instanceof ComponentSampleModel) || srcDataType != dstDataType) break;
                }
                if (i == numSources) {
                    tagID = dstDataType | 0;
                }
            } else {
                tagID = dstDataType | 0;
            }
        }
        RasterFormatTag[] rft = new RasterFormatTag[tagIDs.length];
        if (srcs != null) {
            int i;
            for (i = 0; i < srcs.length; ++i) {
                if (srcs[i].getColorModel() instanceof IndexColorModel) {
                    if (dst.getColorModel() instanceof IndexColorModel) {
                        tagIDs[i] = tagID | 0x400;
                        continue;
                    }
                    tagIDs[i] = tagID | 0x200;
                    continue;
                }
                tagIDs[i] = srcs[i].getColorModel() instanceof ComponentColorModel || binaryDst && ImageUtil.isBinary(srcs[i].getSampleModel()) ? tagID | 0x400 : tagID | 0;
            }
            tagIDs[srcs.length] = tagID | 0x400;
            for (i = 0; i < srcs.length; ++i) {
                rft[i] = new RasterFormatTag(srcs[i].getSampleModel(), tagIDs[i]);
            }
            rft[srcs.length] = new RasterFormatTag(dstSampleModel, tagIDs[srcs.length]);
        } else {
            rft[0] = new RasterFormatTag(dstSampleModel, tagID | 0x400);
        }
        return rft;
    }

    public static int findCompatibleTag(SampleModel[] srcSampleModels, SampleModel dstSampleModel) {
        int dstDataType = dstSampleModel.getTransferType();
        int tag = dstDataType | 0x80;
        if (ImageUtil.isBinary(dstSampleModel)) {
            tag = 128;
        } else if (dstDataType == 0 || dstDataType == 1 || dstDataType == 2) {
            tag = 131;
        }
        if (dstSampleModel instanceof ComponentSampleModel) {
            if (srcSampleModels != null) {
                int i;
                int numSources = srcSampleModels.length;
                for (i = 0; i < numSources; ++i) {
                    int srcDataType = srcSampleModels[i].getTransferType();
                    if (!(srcSampleModels[i] instanceof ComponentSampleModel) || srcDataType != dstDataType) break;
                }
                if (i == numSources) {
                    tag = dstDataType | 0;
                }
            } else {
                tag = dstDataType | 0;
            }
        }
        return tag | 0x400;
    }

    public RasterAccessor(Raster raster, Rectangle rect, RasterFormatTag rft, ColorModel theColorModel) {
        block89: {
            block90: {
                block88: {
                    int i;
                    this.binaryDataArray = null;
                    this.byteDataArrays = null;
                    this.shortDataArrays = null;
                    this.intDataArrays = null;
                    this.floatDataArrays = null;
                    this.doubleDataArrays = null;
                    if (raster == null || rect == null || rft == null) {
                        throw new IllegalArgumentException(JaiI18N.getString("Generic0"));
                    }
                    if (!raster.getBounds().contains(rect)) {
                        throw new IllegalArgumentException(JaiI18N.getString("RasterAccessor2"));
                    }
                    this.raster = raster;
                    this.rectX = rect.x;
                    this.rectY = rect.y;
                    this.rectWidth = rect.width;
                    this.rectHeight = rect.height;
                    this.formatTagID = rft.getFormatTagID();
                    if ((this.formatTagID & 0x180) != 0) break block88;
                    this.numBands = rft.getNumBands();
                    this.pixelStride = rft.getPixelStride();
                    ComponentSampleModel csm = (ComponentSampleModel)raster.getSampleModel();
                    this.scanlineStride = csm.getScanlineStride();
                    int[] bankIndices = null;
                    if (rft.isPixelSequential()) {
                        this.bandOffsets = rft.getBandOffsets();
                        bankIndices = rft.getBankIndices();
                    } else {
                        this.bandOffsets = csm.getBandOffsets();
                        bankIndices = csm.getBankIndices();
                    }
                    this.bandDataOffsets = new int[this.numBands];
                    int[] dataBufferOffsets = raster.getDataBuffer().getOffsets();
                    int subRasterOffset = (this.rectY - raster.getSampleModelTranslateY()) * this.scanlineStride + (this.rectX - raster.getSampleModelTranslateX()) * this.pixelStride;
                    if (dataBufferOffsets.length == 1) {
                        int theDataBufferOffset = dataBufferOffsets[0];
                        for (i = 0; i < this.numBands; ++i) {
                            this.bandDataOffsets[i] = this.bandOffsets[i] + theDataBufferOffset + subRasterOffset;
                        }
                    } else if (dataBufferOffsets.length == this.bandDataOffsets.length) {
                        for (int i2 = 0; i2 < this.numBands; ++i2) {
                            this.bandDataOffsets[i2] = this.bandOffsets[i2] + dataBufferOffsets[i2] + subRasterOffset;
                        }
                    } else {
                        throw new RuntimeException(JaiI18N.getString("RasterAccessor0"));
                    }
                    switch (this.formatTagID & 0x7F) {
                        case 0: {
                            DataBufferByte dbb = (DataBufferByte)raster.getDataBuffer();
                            this.byteDataArrays = new byte[this.numBands][];
                            for (i = 0; i < this.numBands; ++i) {
                                this.byteDataArrays[i] = dbb.getData(bankIndices[i]);
                            }
                            break;
                        }
                        case 1: {
                            DataBufferUShort dbus = (DataBufferUShort)raster.getDataBuffer();
                            this.shortDataArrays = new short[this.numBands][];
                            for (int i3 = 0; i3 < this.numBands; ++i3) {
                                this.shortDataArrays[i3] = dbus.getData(bankIndices[i3]);
                            }
                            break;
                        }
                        case 2: {
                            DataBufferShort dbs = (DataBufferShort)raster.getDataBuffer();
                            this.shortDataArrays = new short[this.numBands][];
                            for (int i4 = 0; i4 < this.numBands; ++i4) {
                                this.shortDataArrays[i4] = dbs.getData(bankIndices[i4]);
                            }
                            break;
                        }
                        case 3: {
                            DataBufferInt dbi = (DataBufferInt)raster.getDataBuffer();
                            this.intDataArrays = new int[this.numBands][];
                            for (int i5 = 0; i5 < this.numBands; ++i5) {
                                this.intDataArrays[i5] = dbi.getData(bankIndices[i5]);
                            }
                            break;
                        }
                        case 4: {
                            DataBuffer dbf = raster.getDataBuffer();
                            this.floatDataArrays = new float[this.numBands][];
                            for (int i6 = 0; i6 < this.numBands; ++i6) {
                                this.floatDataArrays[i6] = DataBufferUtils.getDataFloat(dbf, bankIndices[i6]);
                            }
                            break;
                        }
                        case 5: {
                            DataBuffer dbd = raster.getDataBuffer();
                            this.doubleDataArrays = new double[this.numBands][];
                            for (int i7 = 0; i7 < this.numBands; ++i7) {
                                this.doubleDataArrays[i7] = DataBufferUtils.getDataDouble(dbd, bankIndices[i7]);
                            }
                            break;
                        }
                    }
                    if ((this.formatTagID & 0x600) == 512 && theColorModel instanceof IndexColorModel) {
                        IndexColorModel icm = (IndexColorModel)theColorModel;
                        int newNumBands = icm.getNumComponents();
                        int mapSize = icm.getMapSize();
                        int[] newBandDataOffsets = new int[newNumBands];
                        int newScanlineStride = this.rectWidth * newNumBands;
                        int newPixelStride = newNumBands;
                        byte[][] ctable = new byte[newNumBands][mapSize];
                        icm.getReds(ctable[0]);
                        icm.getGreens(ctable[1]);
                        icm.getBlues(ctable[2]);
                        byte[] rtable = ctable[0];
                        byte[] gtable = ctable[1];
                        byte[] btable = ctable[2];
                        byte[] atable = null;
                        if (newNumBands == 4) {
                            icm.getAlphas(ctable[3]);
                            atable = ctable[3];
                        }
                        for (int i8 = 0; i8 < newNumBands; ++i8) {
                            newBandDataOffsets[i8] = i8;
                        }
                        switch (this.formatTagID & 0x7F) {
                            case 0: {
                                byte[] newBArray = new byte[this.rectWidth * this.rectHeight * newNumBands];
                                byte[] byteDataArray = this.byteDataArrays[0];
                                int scanlineOffset = this.bandDataOffsets[0];
                                int newScanlineOffset = 0;
                                for (int j = 0; j < this.rectHeight; ++j) {
                                    int pixelOffset = scanlineOffset;
                                    int newPixelOffset = newScanlineOffset;
                                    for (int i9 = 0; i9 < this.rectWidth; ++i9) {
                                        int index = byteDataArray[pixelOffset] & 0xFF;
                                        for (int k = 0; k < newNumBands; ++k) {
                                            newBArray[newPixelOffset + k] = ctable[k][index];
                                        }
                                        pixelOffset += this.pixelStride;
                                        newPixelOffset += newPixelStride;
                                    }
                                    scanlineOffset += this.scanlineStride;
                                    newScanlineOffset += newScanlineStride;
                                }
                                this.byteDataArrays = new byte[newNumBands][];
                                for (int i10 = 0; i10 < newNumBands; ++i10) {
                                    this.byteDataArrays[i10] = newBArray;
                                }
                                break;
                            }
                            case 1: {
                                short[] newIArray = new short[this.rectWidth * this.rectHeight * newNumBands];
                                short[] shortDataArray = this.shortDataArrays[0];
                                int scanlineOffset = this.bandDataOffsets[0];
                                int newScanlineOffset = 0;
                                for (int j = 0; j < this.rectHeight; ++j) {
                                    int pixelOffset = scanlineOffset;
                                    int newPixelOffset = newScanlineOffset;
                                    for (int i11 = 0; i11 < this.rectWidth; ++i11) {
                                        int index = shortDataArray[pixelOffset] & 0xFFFF;
                                        for (int k = 0; k < newNumBands; ++k) {
                                            newIArray[newPixelOffset + k] = (short)(ctable[k][index] & 0xFF);
                                        }
                                        pixelOffset += this.pixelStride;
                                        newPixelOffset += newPixelStride;
                                    }
                                    scanlineOffset += this.scanlineStride;
                                    newScanlineOffset += newScanlineStride;
                                }
                                this.shortDataArrays = new short[newNumBands][];
                                for (int i12 = 0; i12 < newNumBands; ++i12) {
                                    this.shortDataArrays[i12] = newIArray;
                                }
                                break;
                            }
                            case 2: {
                                short[] newIArray = new short[this.rectWidth * this.rectHeight * newNumBands];
                                short[] shortDataArray = this.shortDataArrays[0];
                                int scanlineOffset = this.bandDataOffsets[0];
                                int newScanlineOffset = 0;
                                for (int j = 0; j < this.rectHeight; ++j) {
                                    int pixelOffset = scanlineOffset;
                                    int newPixelOffset = newScanlineOffset;
                                    for (int i13 = 0; i13 < this.rectWidth; ++i13) {
                                        short index = shortDataArray[pixelOffset];
                                        for (int k = 0; k < newNumBands; ++k) {
                                            newIArray[newPixelOffset + k] = (short)(ctable[k][index] & 0xFF);
                                        }
                                        pixelOffset += this.pixelStride;
                                        newPixelOffset += newPixelStride;
                                    }
                                    scanlineOffset += this.scanlineStride;
                                    newScanlineOffset += newScanlineStride;
                                }
                                this.shortDataArrays = new short[newNumBands][];
                                for (int i14 = 0; i14 < newNumBands; ++i14) {
                                    this.shortDataArrays[i14] = newIArray;
                                }
                                break;
                            }
                            case 3: {
                                int[] newIArray = new int[this.rectWidth * this.rectHeight * newNumBands];
                                int[] intDataArray = this.intDataArrays[0];
                                int scanlineOffset = this.bandDataOffsets[0];
                                int newScanlineOffset = 0;
                                for (int j = 0; j < this.rectHeight; ++j) {
                                    int pixelOffset = scanlineOffset;
                                    int newPixelOffset = newScanlineOffset;
                                    for (int i15 = 0; i15 < this.rectWidth; ++i15) {
                                        int index = intDataArray[pixelOffset];
                                        for (int k = 0; k < newNumBands; ++k) {
                                            newIArray[newPixelOffset + k] = ctable[k][index] & 0xFF;
                                        }
                                        pixelOffset += this.pixelStride;
                                        newPixelOffset += newPixelStride;
                                    }
                                    scanlineOffset += this.scanlineStride;
                                    newScanlineOffset += newScanlineStride;
                                }
                                this.intDataArrays = new int[newNumBands][];
                                for (int i16 = 0; i16 < newNumBands; ++i16) {
                                    this.intDataArrays[i16] = newIArray;
                                }
                                break;
                            }
                            case 4: {
                                float[] newFArray = new float[this.rectWidth * this.rectHeight * newNumBands];
                                float[] floatDataArray = this.floatDataArrays[0];
                                int scanlineOffset = this.bandDataOffsets[0];
                                int newScanlineOffset = 0;
                                for (int j = 0; j < this.rectHeight; ++j) {
                                    int pixelOffset = scanlineOffset;
                                    int newPixelOffset = newScanlineOffset;
                                    for (int i17 = 0; i17 < this.rectWidth; ++i17) {
                                        int index = (int)floatDataArray[pixelOffset];
                                        for (int k = 0; k < newNumBands; ++k) {
                                            newFArray[newPixelOffset + k] = ctable[k][index] & 0xFF;
                                        }
                                        pixelOffset += this.pixelStride;
                                        newPixelOffset += newPixelStride;
                                    }
                                    scanlineOffset += this.scanlineStride;
                                    newScanlineOffset += newScanlineStride;
                                }
                                this.floatDataArrays = new float[newNumBands][];
                                for (int i18 = 0; i18 < newNumBands; ++i18) {
                                    this.floatDataArrays[i18] = newFArray;
                                }
                                break;
                            }
                            case 5: {
                                double[] newDArray = new double[this.rectWidth * this.rectHeight * newNumBands];
                                double[] doubleDataArray = this.doubleDataArrays[0];
                                int scanlineOffset = this.bandDataOffsets[0];
                                int newScanlineOffset = 0;
                                for (int j = 0; j < this.rectHeight; ++j) {
                                    int pixelOffset = scanlineOffset;
                                    int newPixelOffset = newScanlineOffset;
                                    for (int i19 = 0; i19 < this.rectWidth; ++i19) {
                                        int index = (int)doubleDataArray[pixelOffset];
                                        for (int k = 0; k < newNumBands; ++k) {
                                            newDArray[newPixelOffset + k] = ctable[k][index] & 0xFF;
                                        }
                                        pixelOffset += this.pixelStride;
                                        newPixelOffset += newPixelStride;
                                    }
                                    scanlineOffset += this.scanlineStride;
                                    newScanlineOffset += newScanlineStride;
                                }
                                this.doubleDataArrays = new double[newNumBands][];
                                for (int i20 = 0; i20 < newNumBands; ++i20) {
                                    this.doubleDataArrays[i20] = newDArray;
                                }
                                break;
                            }
                        }
                        this.numBands = newNumBands;
                        this.pixelStride = newPixelStride;
                        this.scanlineStride = newScanlineStride;
                        this.bandDataOffsets = newBandDataOffsets;
                        this.bandOffsets = newBandDataOffsets;
                    }
                    break block89;
                }
                if ((this.formatTagID & 0x180) != 128 || (this.formatTagID & 0x600) == 1024 || theColorModel == null) break block90;
                this.pixelStride = this.numBands = theColorModel instanceof IndexColorModel ? theColorModel.getNumComponents() : raster.getSampleModel().getNumBands();
                this.scanlineStride = this.rectWidth * this.numBands;
                this.bandOffsets = new int[this.numBands];
                for (int i = 0; i < this.numBands; ++i) {
                    this.bandOffsets[i] = i;
                }
                this.bandDataOffsets = this.bandOffsets;
                Object odata = null;
                int offset = 0;
                int[] components = new int[theColorModel.getNumComponents()];
                switch (this.formatTagID & 0x7F) {
                    case 3: {
                        int[] idata = new int[this.rectWidth * this.rectHeight * this.numBands];
                        this.intDataArrays = new int[this.numBands][];
                        for (int i = 0; i < this.numBands; ++i) {
                            this.intDataArrays[i] = idata;
                        }
                        odata = raster.getDataElements(this.rectX, this.rectY, null);
                        offset = 0;
                        byte[] bdata = null;
                        if (raster instanceof BytePackedRaster) {
                            bdata = (byte[])odata;
                        }
                        for (int j = this.rectY; j < this.rectY + this.rectHeight; ++j) {
                            for (int i = this.rectX; i < this.rectX + this.rectWidth; ++i) {
                                if (bdata != null) {
                                    bdata[0] = (byte)raster.getSample(i, j, 0);
                                } else {
                                    raster.getDataElements(i, j, odata);
                                }
                                theColorModel.getComponents(odata, components, 0);
                                idata[offset] = components[0];
                                idata[offset + 1] = components[1];
                                idata[offset + 2] = components[2];
                                if (this.numBands > 3) {
                                    idata[offset + 3] = components[3];
                                }
                                offset += this.pixelStride;
                            }
                        }
                        break block89;
                    }
                    case 4: {
                        float[] fdata = new float[this.rectWidth * this.rectHeight * this.numBands];
                        this.floatDataArrays = new float[this.numBands][];
                        for (int i = 0; i < this.numBands; ++i) {
                            this.floatDataArrays[i] = fdata;
                        }
                        odata = null;
                        offset = 0;
                        for (int j = this.rectY; j < this.rectY + this.rectHeight; ++j) {
                            for (int i = this.rectX; i < this.rectX + this.rectWidth; ++i) {
                                odata = raster.getDataElements(i, j, odata);
                                theColorModel.getComponents(odata, components, 0);
                                fdata[offset] = components[0];
                                fdata[offset + 1] = components[1];
                                fdata[offset + 2] = components[2];
                                if (this.numBands > 3) {
                                    fdata[offset + 3] = components[3];
                                }
                                offset += this.pixelStride;
                            }
                        }
                        break block89;
                    }
                    case 5: {
                        double[] ddata = new double[this.rectWidth * this.rectHeight * this.numBands];
                        this.doubleDataArrays = new double[this.numBands][];
                        for (int i = 0; i < this.numBands; ++i) {
                            this.doubleDataArrays[i] = ddata;
                        }
                        odata = null;
                        offset = 0;
                        for (int j = this.rectY; j < this.rectY + this.rectHeight; ++j) {
                            for (int i = this.rectX; i < this.rectX + this.rectWidth; ++i) {
                                odata = raster.getDataElements(i, j, odata);
                                theColorModel.getComponents(odata, components, 0);
                                ddata[offset] = components[0];
                                ddata[offset + 1] = components[1];
                                ddata[offset + 2] = components[2];
                                if (this.numBands > 3) {
                                    ddata[offset + 3] = components[3];
                                }
                                offset += this.pixelStride;
                            }
                        }
                        break;
                    }
                }
                break block89;
            }
            this.pixelStride = this.numBands = rft.getNumBands();
            this.scanlineStride = this.rectWidth * this.numBands;
            this.bandDataOffsets = rft.getBandOffsets();
            this.bandOffsets = this.bandDataOffsets;
            switch (this.formatTagID & 0x7F) {
                case 3: {
                    int[] idata = raster.getPixels(this.rectX, this.rectY, this.rectWidth, this.rectHeight, (int[])null);
                    this.intDataArrays = new int[this.numBands][];
                    for (int i = 0; i < this.numBands; ++i) {
                        this.intDataArrays[i] = idata;
                    }
                    break;
                }
                case 4: {
                    float[] fdata = raster.getPixels(this.rectX, this.rectY, this.rectWidth, this.rectHeight, (float[])null);
                    this.floatDataArrays = new float[this.numBands][];
                    for (int i = 0; i < this.numBands; ++i) {
                        this.floatDataArrays[i] = fdata;
                    }
                    break;
                }
                case 5: {
                    double[] ddata = raster.getPixels(this.rectX, this.rectY, this.rectWidth, this.rectHeight, (double[])null);
                    this.doubleDataArrays = new double[this.numBands][];
                    for (int i = 0; i < this.numBands; ++i) {
                        this.doubleDataArrays[i] = ddata;
                    }
                    break;
                }
            }
        }
    }

    public int getX() {
        return this.rectX;
    }

    public int getY() {
        return this.rectY;
    }

    public int getWidth() {
        return this.rectWidth;
    }

    public int getHeight() {
        return this.rectHeight;
    }

    public int getNumBands() {
        return this.numBands;
    }

    public boolean isBinary() {
        return (this.formatTagID & 0x480) == 1152 && ImageUtil.isBinary(this.raster.getSampleModel());
    }

    public byte[] getBinaryDataArray() {
        if (this.binaryDataArray == null && this.isBinary()) {
            this.binaryDataArray = ImageUtil.getPackedBinaryData(this.raster, new Rectangle(this.rectX, this.rectY, this.rectWidth, this.rectHeight));
        }
        return this.binaryDataArray;
    }

    public byte[][] getByteDataArrays() {
        if (this.byteDataArrays == null && this.isBinary()) {
            byte[] bdata = ImageUtil.getUnpackedBinaryData(this.raster, new Rectangle(this.rectX, this.rectY, this.rectWidth, this.rectHeight));
            this.byteDataArrays = new byte[][]{bdata};
        }
        return this.byteDataArrays;
    }

    public byte[] getByteDataArray(int b) {
        byte[][] bda = this.getByteDataArrays();
        return bda == null ? null : bda[b];
    }

    public short[][] getShortDataArrays() {
        return this.shortDataArrays;
    }

    public short[] getShortDataArray(int b) {
        return this.shortDataArrays == null ? null : this.shortDataArrays[b];
    }

    public int[][] getIntDataArrays() {
        return this.intDataArrays;
    }

    public int[] getIntDataArray(int b) {
        return this.intDataArrays == null ? null : this.intDataArrays[b];
    }

    public float[][] getFloatDataArrays() {
        return this.floatDataArrays;
    }

    public float[] getFloatDataArray(int b) {
        return this.floatDataArrays == null ? null : this.floatDataArrays[b];
    }

    public double[][] getDoubleDataArrays() {
        return this.doubleDataArrays;
    }

    public double[] getDoubleDataArray(int b) {
        return this.doubleDataArrays == null ? null : this.doubleDataArrays[b];
    }

    public Object getDataArray(int b) {
        Object[] dataArray = null;
        switch (this.getDataType()) {
            case 0: {
                dataArray = this.getByteDataArray(b);
                break;
            }
            case 1: 
            case 2: {
                dataArray = this.getShortDataArray(b);
                break;
            }
            case 3: {
                dataArray = this.getIntDataArray(b);
                break;
            }
            case 4: {
                dataArray = this.getFloatDataArray(b);
                break;
            }
            case 5: {
                dataArray = this.getDoubleDataArray(b);
                break;
            }
            default: {
                dataArray = null;
            }
        }
        return dataArray;
    }

    public int[] getBandOffsets() {
        return this.bandDataOffsets;
    }

    public int[] getOffsetsForBands() {
        return this.bandOffsets;
    }

    public int getBandOffset(int b) {
        return this.bandDataOffsets[b];
    }

    public int getOffsetForBand(int b) {
        return this.bandOffsets[b];
    }

    public int getScanlineStride() {
        return this.scanlineStride;
    }

    public int getPixelStride() {
        return this.pixelStride;
    }

    public int getDataType() {
        return this.formatTagID & 0x7F;
    }

    public boolean isDataCopy() {
        return (this.formatTagID & 0x180) == 128;
    }

    public void copyBinaryDataToRaster() {
        if (this.binaryDataArray == null || !this.isBinary()) {
            return;
        }
        ImageUtil.setPackedBinaryData(this.binaryDataArray, (WritableRaster)this.raster, new Rectangle(this.rectX, this.rectY, this.rectWidth, this.rectHeight));
    }

    public void copyDataToRaster() {
        if (this.isDataCopy()) {
            WritableRaster wr = (WritableRaster)this.raster;
            switch (this.getDataType()) {
                case 0: {
                    if (!this.isBinary()) {
                        throw new RuntimeException(JaiI18N.getString("RasterAccessor1"));
                    }
                    ImageUtil.setUnpackedBinaryData(this.byteDataArrays[0], wr, new Rectangle(this.rectX, this.rectY, this.rectWidth, this.rectHeight));
                    break;
                }
                case 3: {
                    wr.setPixels(this.rectX, this.rectY, this.rectWidth, this.rectHeight, this.intDataArrays[0]);
                    break;
                }
                case 4: {
                    wr.setPixels(this.rectX, this.rectY, this.rectWidth, this.rectHeight, this.floatDataArrays[0]);
                    break;
                }
                case 5: {
                    wr.setPixels(this.rectX, this.rectY, this.rectWidth, this.rectHeight, this.doubleDataArrays[0]);
                }
            }
        }
    }

    public boolean needsClamping() {
        int[] bits = this.raster.getSampleModel().getSampleSize();
        for (int i = 0; i < bits.length; ++i) {
            if (bits[i] >= 32) continue;
            return true;
        }
        return false;
    }

    public void clampDataArrays() {
        int[] bits = this.raster.getSampleModel().getSampleSize();
        boolean needClamp = false;
        boolean uniformBitSize = true;
        int bitSize = bits[0];
        for (int i = 0; i < bits.length; ++i) {
            if (bits[i] < 32) {
                needClamp = true;
            }
            if (bits[i] == bitSize) continue;
            uniformBitSize = false;
        }
        if (!needClamp) {
            return;
        }
        int dataType = this.raster.getDataBuffer().getDataType();
        double[] hiVals = new double[bits.length];
        double[] loVals = new double[bits.length];
        if (dataType == 1 && uniformBitSize && bits[0] == 16) {
            for (int i = 0; i < bits.length; ++i) {
                hiVals[i] = 65535.0;
                loVals[i] = 0.0;
            }
        } else if (dataType == 2 && uniformBitSize && bits[0] == 16) {
            for (int i = 0; i < bits.length; ++i) {
                hiVals[i] = 32767.0;
                loVals[i] = -32768.0;
            }
        } else if (dataType == 3 && uniformBitSize && bits[0] == 32) {
            for (int i = 0; i < bits.length; ++i) {
                hiVals[i] = 2.147483647E9;
                loVals[i] = -2.147483648E9;
            }
        } else {
            for (int i = 0; i < bits.length; ++i) {
                hiVals[i] = (1 << bits[i]) - 1;
                loVals[i] = 0.0;
            }
        }
        this.clampDataArray(hiVals, loVals);
    }

    private void clampDataArray(double[] hiVals, double[] loVals) {
        switch (this.getDataType()) {
            case 3: {
                this.clampIntArrays(this.toIntArray(hiVals), this.toIntArray(loVals));
                break;
            }
            case 4: {
                this.clampFloatArrays(this.toFloatArray(hiVals), this.toFloatArray(loVals));
                break;
            }
            case 5: {
                this.clampDoubleArrays(hiVals, loVals);
            }
        }
    }

    private int[] toIntArray(double[] vals) {
        int[] returnVals = new int[vals.length];
        for (int i = 0; i < vals.length; ++i) {
            returnVals[i] = (int)vals[i];
        }
        return returnVals;
    }

    private float[] toFloatArray(double[] vals) {
        float[] returnVals = new float[vals.length];
        for (int i = 0; i < vals.length; ++i) {
            returnVals[i] = (float)vals[i];
        }
        return returnVals;
    }

    private void clampIntArrays(int[] hiVals, int[] loVals) {
        int width = this.rectWidth;
        int height = this.rectHeight;
        for (int k = 0; k < this.numBands; ++k) {
            int[] data = this.intDataArrays[k];
            int scanlineOffset = this.bandDataOffsets[k];
            int hiVal = hiVals[k];
            int loVal = loVals[k];
            for (int j = 0; j < height; ++j) {
                int pixelOffset = scanlineOffset;
                for (int i = 0; i < width; ++i) {
                    int tmp = data[pixelOffset];
                    if (tmp < loVal) {
                        data[pixelOffset] = loVal;
                    } else if (tmp > hiVal) {
                        data[pixelOffset] = hiVal;
                    }
                    pixelOffset += this.pixelStride;
                }
                scanlineOffset += this.scanlineStride;
            }
        }
    }

    private void clampFloatArrays(float[] hiVals, float[] loVals) {
        int width = this.rectWidth;
        int height = this.rectHeight;
        for (int k = 0; k < this.numBands; ++k) {
            float[] data = this.floatDataArrays[k];
            int scanlineOffset = this.bandDataOffsets[k];
            float hiVal = hiVals[k];
            float loVal = loVals[k];
            for (int j = 0; j < height; ++j) {
                int pixelOffset = scanlineOffset;
                for (int i = 0; i < width; ++i) {
                    float tmp = data[pixelOffset];
                    if (tmp < loVal) {
                        data[pixelOffset] = loVal;
                    } else if (tmp > hiVal) {
                        data[pixelOffset] = hiVal;
                    }
                    pixelOffset += this.pixelStride;
                }
                scanlineOffset += this.scanlineStride;
            }
        }
    }

    private void clampDoubleArrays(double[] hiVals, double[] loVals) {
        int width = this.rectWidth;
        int height = this.rectHeight;
        for (int k = 0; k < this.numBands; ++k) {
            double[] data = this.doubleDataArrays[k];
            int scanlineOffset = this.bandDataOffsets[k];
            double hiVal = hiVals[k];
            double loVal = loVals[k];
            for (int j = 0; j < height; ++j) {
                int pixelOffset = scanlineOffset;
                for (int i = 0; i < width; ++i) {
                    double tmp = data[pixelOffset];
                    if (tmp < loVal) {
                        data[pixelOffset] = loVal;
                    } else if (tmp > hiVal) {
                        data[pixelOffset] = hiVal;
                    }
                    pixelOffset += this.pixelStride;
                }
                scanlineOffset += this.scanlineStride;
            }
        }
    }
}


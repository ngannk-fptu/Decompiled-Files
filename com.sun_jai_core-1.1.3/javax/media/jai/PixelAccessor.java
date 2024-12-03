/*
 * Decompiled with CFR 0.152.
 */
package javax.media.jai;

import com.sun.media.jai.util.DataBufferUtils;
import com.sun.media.jai.util.ImageUtil;
import com.sun.media.jai.util.JDKWorkarounds;
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
import java.awt.image.MultiPixelPackedSampleModel;
import java.awt.image.PackedColorModel;
import java.awt.image.Raster;
import java.awt.image.RenderedImage;
import java.awt.image.SampleModel;
import java.awt.image.SinglePixelPackedSampleModel;
import java.awt.image.WritableRaster;
import java.util.Vector;
import javax.media.jai.JaiI18N;
import javax.media.jai.PackedImageData;
import javax.media.jai.UnpackedImageData;

public final class PixelAccessor {
    public static final int TYPE_BIT = -1;
    public final SampleModel sampleModel;
    public final ColorModel colorModel;
    public final boolean isComponentSM;
    public final boolean isMultiPixelPackedSM;
    public final boolean isSinglePixelPackedSM;
    public final int sampleType;
    public final int bufferType;
    public final int transferType;
    public final int numBands;
    public final int[] sampleSize;
    public final boolean isPacked;
    public final boolean hasCompatibleCM;
    public final boolean isComponentCM;
    public final boolean isIndexCM;
    public final boolean isPackedCM;
    public final int componentType;
    public final int numComponents;
    public final int[] componentSize;

    private static SampleModel getSampleModel(RenderedImage image) {
        if (image == null) {
            throw new IllegalArgumentException(JaiI18N.getString("Generic0"));
        }
        return image.getSampleModel();
    }

    public PixelAccessor(RenderedImage image) {
        this(PixelAccessor.getSampleModel(image), image.getColorModel());
    }

    public PixelAccessor(SampleModel sm, ColorModel cm) {
        if (sm == null) {
            throw new IllegalArgumentException(JaiI18N.getString("Generic0"));
        }
        this.sampleModel = sm;
        this.colorModel = cm;
        this.isComponentSM = this.sampleModel instanceof ComponentSampleModel;
        this.isMultiPixelPackedSM = this.sampleModel instanceof MultiPixelPackedSampleModel;
        this.isSinglePixelPackedSM = this.sampleModel instanceof SinglePixelPackedSampleModel;
        this.bufferType = this.sampleModel.getDataType();
        this.transferType = this.sampleModel.getTransferType();
        this.numBands = this.sampleModel.getNumBands();
        this.sampleSize = this.sampleModel.getSampleSize();
        this.sampleType = this.isComponentSM ? this.bufferType : PixelAccessor.getType(this.sampleSize);
        this.isPacked = this.sampleType == -1 && this.numBands == 1;
        boolean bl = this.hasCompatibleCM = this.colorModel != null && JDKWorkarounds.areCompatibleDataModels(this.sampleModel, this.colorModel);
        if (this.hasCompatibleCM) {
            this.isComponentCM = this.colorModel instanceof ComponentColorModel;
            this.isIndexCM = this.colorModel instanceof IndexColorModel;
            this.isPackedCM = this.colorModel instanceof PackedColorModel;
            this.numComponents = this.colorModel.getNumComponents();
            this.componentSize = this.colorModel.getComponentSize();
            int tempType = PixelAccessor.getType(this.componentSize);
            this.componentType = tempType == -1 ? 0 : tempType;
        } else {
            this.isComponentCM = false;
            this.isIndexCM = false;
            this.isPackedCM = false;
            this.numComponents = this.numBands;
            this.componentSize = this.sampleSize;
            this.componentType = this.sampleType;
        }
    }

    private static int getType(int[] size) {
        int maxSize = size[0];
        for (int i = 1; i < size.length; ++i) {
            maxSize = Math.max(maxSize, size[i]);
        }
        int type = maxSize < 1 ? 32 : (maxSize == 1 ? -1 : (maxSize <= 8 ? 0 : (maxSize <= 16 ? 1 : (maxSize <= 32 ? 3 : (maxSize <= 64 ? 5 : 32)))));
        return type;
    }

    public static int getPixelType(SampleModel sm) {
        return sm instanceof ComponentSampleModel ? sm.getDataType() : PixelAccessor.getType(sm.getSampleSize());
    }

    public static int getDestPixelType(Vector sources) {
        if (sources == null) {
            throw new IllegalArgumentException(JaiI18N.getString("Generic0"));
        }
        int type = 32;
        int size = sources.size();
        if (size > 0) {
            RenderedImage src = (RenderedImage)sources.get(0);
            SampleModel sm = src.getSampleModel();
            type = PixelAccessor.getPixelType(sm);
            for (int i = 1; i < size; ++i) {
                src = (RenderedImage)sources.get(i);
                sm = src.getSampleModel();
                int t = PixelAccessor.getPixelType(sm);
                type = type == 1 && t == 2 || type == 2 && t == 1 ? 3 : Math.max(type, t);
            }
        }
        return type;
    }

    public static int getDestNumBands(Vector sources) {
        if (sources == null) {
            throw new IllegalArgumentException(JaiI18N.getString("Generic0"));
        }
        int bands = 0;
        int size = sources.size();
        if (size > 0) {
            RenderedImage src = (RenderedImage)sources.get(0);
            SampleModel sm = src.getSampleModel();
            bands = sm.getNumBands();
            for (int i = 1; i < size; ++i) {
                src = (RenderedImage)sources.get(i);
                sm = src.getSampleModel();
                int b = sm.getNumBands();
                bands = bands == 1 || b == 1 ? Math.max(bands, b) : Math.min(bands, b);
            }
        }
        return bands;
    }

    public static boolean isPackedOperation(PixelAccessor[] srcs, PixelAccessor dst) {
        boolean canBePacked = dst.isPacked;
        if (canBePacked && srcs != null) {
            for (int i = 0; i < srcs.length; ++i) {
                boolean bl = canBePacked = canBePacked && srcs[i].isPacked;
                if (!canBePacked) break;
            }
        }
        return canBePacked;
    }

    public static boolean isPackedOperation(PixelAccessor src, PixelAccessor dst) {
        return src.isPacked && dst.isPacked;
    }

    public static boolean isPackedOperation(PixelAccessor src1, PixelAccessor src2, PixelAccessor dst) {
        return src1.isPacked && src2.isPacked && dst.isPacked;
    }

    public UnpackedImageData getPixels(Raster raster, Rectangle rect, int type, boolean isDest) {
        if (!raster.getBounds().contains(rect)) {
            throw new IllegalArgumentException(JaiI18N.getString("PixelAccessor0"));
        }
        if (type < 0 || type > 5) {
            throw new IllegalArgumentException(JaiI18N.getString("PixelAccessor1"));
        }
        if (type < this.sampleType || this.sampleType == 1 && type == 2) {
            throw new IllegalArgumentException(JaiI18N.getString("PixelAccessor2"));
        }
        if (this.isComponentSM) {
            return this.getPixelsCSM(raster, rect, type, isDest);
        }
        int size = rect.width * rect.height * this.numBands;
        Object data = null;
        switch (type) {
            case 0: {
                byte[] bd;
                if (isDest) {
                    bd = new byte[size];
                } else if (this.isMultiPixelPackedSM && this.transferType == 0) {
                    bd = (byte[])raster.getDataElements(rect.x, rect.y, rect.width, rect.height, null);
                } else {
                    bd = new byte[size];
                    int[] d = raster.getPixels(rect.x, rect.y, rect.width, rect.height, (int[])null);
                    for (int i = 0; i < size; ++i) {
                        bd[i] = (byte)(d[i] & 0xFF);
                    }
                }
                data = this.repeatBand(bd, this.numBands);
                break;
            }
            case 1: {
                short[] usd;
                if (isDest) {
                    usd = new short[size];
                } else if (this.isMultiPixelPackedSM && this.transferType == 1) {
                    usd = (short[])raster.getDataElements(rect.x, rect.y, rect.width, rect.height, null);
                } else {
                    usd = new short[size];
                    int[] d = raster.getPixels(rect.x, rect.y, rect.width, rect.height, (int[])null);
                    for (int i = 0; i < size; ++i) {
                        usd[i] = (short)(d[i] & 0xFFFF);
                    }
                }
                data = this.repeatBand(usd, this.numBands);
                break;
            }
            case 2: {
                short[] sd = new short[size];
                if (!isDest) {
                    int[] d = raster.getPixels(rect.x, rect.y, rect.width, rect.height, (int[])null);
                    for (int i = 0; i < size; ++i) {
                        sd[i] = (short)d[i];
                    }
                }
                data = this.repeatBand(sd, this.numBands);
                break;
            }
            case 3: {
                return this.getPixelsInt(raster, rect, isDest);
            }
            case 4: {
                return this.getPixelsFloat(raster, rect, isDest);
            }
            case 5: {
                return this.getPixelsDouble(raster, rect, isDest);
            }
        }
        return new UnpackedImageData(raster, rect, type, data, this.numBands, this.numBands * rect.width, this.getInterleavedOffsets(this.numBands), isDest & raster instanceof WritableRaster);
    }

    private UnpackedImageData getPixelsCSM(Raster raster, Rectangle rect, int type, boolean isDest) {
        boolean set;
        int[] offsets;
        int lineStride;
        int pixelStride;
        Object data = null;
        ComponentSampleModel sm = (ComponentSampleModel)raster.getSampleModel();
        if (type == this.sampleType) {
            DataBuffer db = raster.getDataBuffer();
            int[] bankIndices = sm.getBankIndices();
            switch (this.sampleType) {
                case 0: {
                    byte[][] bbd = ((DataBufferByte)db).getBankData();
                    byte[][] bd = new byte[this.numBands][];
                    for (int b = 0; b < this.numBands; ++b) {
                        bd[b] = bbd[bankIndices[b]];
                    }
                    data = bd;
                    break;
                }
                case 1: 
                case 2: {
                    short[][] sbd = this.sampleType == 1 ? ((DataBufferUShort)db).getBankData() : ((DataBufferShort)db).getBankData();
                    short[][] sd = new short[this.numBands][];
                    for (int b = 0; b < this.numBands; ++b) {
                        sd[b] = sbd[bankIndices[b]];
                    }
                    data = sd;
                    break;
                }
                case 3: {
                    int[][] ibd = ((DataBufferInt)db).getBankData();
                    int[][] id = new int[this.numBands][];
                    for (int b = 0; b < this.numBands; ++b) {
                        id[b] = ibd[bankIndices[b]];
                    }
                    data = id;
                    break;
                }
                case 4: {
                    float[][] fbd = DataBufferUtils.getBankDataFloat(db);
                    float[][] fd = new float[this.numBands][];
                    for (int b = 0; b < this.numBands; ++b) {
                        fd[b] = fbd[bankIndices[b]];
                    }
                    data = fd;
                    break;
                }
                case 5: {
                    double[][] dbd = DataBufferUtils.getBankDataDouble(db);
                    double[][] dd = new double[this.numBands][];
                    for (int b = 0; b < this.numBands; ++b) {
                        dd[b] = dbd[bankIndices[b]];
                    }
                    data = dd;
                }
            }
            pixelStride = sm.getPixelStride();
            lineStride = sm.getScanlineStride();
            int[] dbOffsets = db.getOffsets();
            int x = rect.x - raster.getSampleModelTranslateX();
            int y = rect.y - raster.getSampleModelTranslateY();
            offsets = new int[this.numBands];
            for (int b = 0; b < this.numBands; ++b) {
                offsets[b] = sm.getOffset(x, y, b) + dbOffsets[bankIndices[b]];
            }
            set = false;
        } else {
            switch (type) {
                case 3: {
                    return this.getPixelsInt(raster, rect, isDest);
                }
                case 4: {
                    return this.getPixelsFloat(raster, rect, isDest);
                }
                case 5: {
                    return this.getPixelsDouble(raster, rect, isDest);
                }
            }
            int size = rect.width * rect.height * this.numBands;
            short[] sd = new short[size];
            if (!isDest) {
                UnpackedImageData uid = this.getPixelsCSM(raster, rect, this.sampleType, isDest);
                byte[][] bdata = uid.getByteData();
                for (int b = 0; b < this.numBands; ++b) {
                    byte[] bd = bdata[b];
                    int lo = uid.getOffset(b);
                    int i = b;
                    for (int h = 0; h < rect.height; ++h) {
                        int po = lo;
                        lo += uid.lineStride;
                        for (int w = 0; w < rect.width; ++w) {
                            sd[i] = (short)(bd[po] & 0xFF);
                            po += uid.pixelStride;
                            i += this.numBands;
                        }
                    }
                }
            }
            data = this.repeatBand(sd, this.numBands);
            pixelStride = this.numBands;
            lineStride = pixelStride * rect.width;
            offsets = this.getInterleavedOffsets(this.numBands);
            set = isDest & raster instanceof WritableRaster;
        }
        return new UnpackedImageData(raster, rect, type, data, pixelStride, lineStride, offsets, set);
    }

    private UnpackedImageData getPixelsInt(Raster raster, Rectangle rect, boolean isDest) {
        int size = rect.width * rect.height * this.numBands;
        int[] d = isDest ? new int[size] : raster.getPixels(rect.x, rect.y, rect.width, rect.height, (int[])null);
        return new UnpackedImageData(raster, rect, 3, this.repeatBand(d, this.numBands), this.numBands, this.numBands * rect.width, this.getInterleavedOffsets(this.numBands), isDest & raster instanceof WritableRaster);
    }

    private UnpackedImageData getPixelsFloat(Raster raster, Rectangle rect, boolean isDest) {
        int size = rect.width * rect.height * this.numBands;
        float[] d = isDest ? new float[size] : raster.getPixels(rect.x, rect.y, rect.width, rect.height, (float[])null);
        return new UnpackedImageData(raster, rect, 4, this.repeatBand(d, this.numBands), this.numBands, this.numBands * rect.width, this.getInterleavedOffsets(this.numBands), isDest & raster instanceof WritableRaster);
    }

    private UnpackedImageData getPixelsDouble(Raster raster, Rectangle rect, boolean isDest) {
        int size = rect.width * rect.height * this.numBands;
        double[] d = isDest ? new double[size] : raster.getPixels(rect.x, rect.y, rect.width, rect.height, (double[])null);
        return new UnpackedImageData(raster, rect, 5, this.repeatBand(d, this.numBands), this.numBands, this.numBands * rect.width, this.getInterleavedOffsets(this.numBands), isDest & raster instanceof WritableRaster);
    }

    private byte[][] repeatBand(byte[] d, int numBands) {
        byte[][] data = new byte[numBands][];
        for (int i = 0; i < numBands; ++i) {
            data[i] = d;
        }
        return data;
    }

    private short[][] repeatBand(short[] d, int numBands) {
        short[][] data = new short[numBands][];
        for (int i = 0; i < numBands; ++i) {
            data[i] = d;
        }
        return data;
    }

    private int[][] repeatBand(int[] d, int numBands) {
        int[][] data = new int[numBands][];
        for (int i = 0; i < numBands; ++i) {
            data[i] = d;
        }
        return data;
    }

    private float[][] repeatBand(float[] d, int numBands) {
        float[][] data = new float[numBands][];
        for (int i = 0; i < numBands; ++i) {
            data[i] = d;
        }
        return data;
    }

    private double[][] repeatBand(double[] d, int numBands) {
        double[][] data = new double[numBands][];
        for (int i = 0; i < numBands; ++i) {
            data[i] = d;
        }
        return data;
    }

    private int[] getInterleavedOffsets(int numBands) {
        int[] offsets = new int[numBands];
        for (int i = 0; i < numBands; ++i) {
            offsets[i] = i;
        }
        return offsets;
    }

    public void setPixels(UnpackedImageData uid) {
        if (uid == null) {
            throw new IllegalArgumentException(JaiI18N.getString("Generic0"));
        }
        this.setPixels(uid, true);
    }

    public void setPixels(UnpackedImageData uid, boolean clamp) {
        if (uid == null) {
            throw new IllegalArgumentException(JaiI18N.getString("Generic0"));
        }
        if (!uid.convertToDest) {
            return;
        }
        if (clamp) {
            switch (this.sampleType) {
                case 0: {
                    this.clampByte(uid.data, uid.type);
                    break;
                }
                case 1: {
                    this.clampUShort(uid.data, uid.type);
                    break;
                }
                case 2: {
                    this.clampShort(uid.data, uid.type);
                    break;
                }
                case 3: {
                    this.clampInt(uid.data, uid.type);
                    break;
                }
                case 4: {
                    this.clampFloat(uid.data, uid.type);
                }
            }
        }
        WritableRaster raster = (WritableRaster)uid.raster;
        Rectangle rect = uid.rect;
        int type = uid.type;
        switch (type) {
            case 0: {
                byte[] bd = uid.getByteData(0);
                if (this.isMultiPixelPackedSM && this.transferType == 0) {
                    raster.setDataElements(rect.x, rect.y, rect.width, rect.height, bd);
                    break;
                }
                int size = bd.length;
                int[] d = new int[size];
                for (int i = 0; i < size; ++i) {
                    d[i] = bd[i] & 0xFF;
                }
                raster.setPixels(rect.x, rect.y, rect.width, rect.height, d);
                break;
            }
            case 1: 
            case 2: {
                short[] sd = uid.getShortData(0);
                if (this.isComponentSM) {
                    UnpackedImageData buid = this.getPixelsCSM(raster, rect, 0, true);
                    byte[][] bdata = buid.getByteData();
                    for (int b = 0; b < this.numBands; ++b) {
                        byte[] d = bdata[b];
                        int lo = buid.getOffset(b);
                        int i = b;
                        for (int h = 0; h < rect.height; ++h) {
                            int po = lo;
                            lo += buid.lineStride;
                            for (int w = 0; w < rect.width; ++w) {
                                d[po] = (byte)sd[i];
                                po += buid.pixelStride;
                                i += this.numBands;
                            }
                        }
                    }
                    break;
                }
                if (this.isMultiPixelPackedSM && this.transferType == 1) {
                    raster.setDataElements(rect.x, rect.y, rect.width, rect.height, sd);
                    break;
                }
                int size = sd.length;
                int[] d = new int[size];
                if (type == 1) {
                    for (int i = 0; i < size; ++i) {
                        d[i] = sd[i] & 0xFFFF;
                    }
                } else {
                    for (int i = 0; i < size; ++i) {
                        d[i] = sd[i];
                    }
                }
                raster.setPixels(rect.x, rect.y, rect.width, rect.height, d);
                break;
            }
            case 3: {
                raster.setPixels(rect.x, rect.y, rect.width, rect.height, uid.getIntData(0));
                break;
            }
            case 4: {
                raster.setPixels(rect.x, rect.y, rect.width, rect.height, uid.getFloatData(0));
                break;
            }
            case 5: {
                raster.setPixels(rect.x, rect.y, rect.width, rect.height, uid.getDoubleData(0));
            }
        }
    }

    private void clampByte(Object data, int type) {
        switch (type) {
            case 1: {
                short[][] usd = (short[][])data;
                int bands = usd.length;
                for (int j = 0; j < bands; ++j) {
                    short[] d = usd[j];
                    int size = d.length;
                    for (int i = 0; i < size; ++i) {
                        int n = d[i] & 0xFFFF;
                        d[i] = (short)(n > 255 ? 255 : n);
                    }
                }
                break;
            }
            case 2: {
                short[][] sd = (short[][])data;
                int bands = sd.length;
                for (int j = 0; j < bands; ++j) {
                    short[] d = sd[j];
                    int size = d.length;
                    for (int i = 0; i < size; ++i) {
                        int n = d[i];
                        d[i] = (short)(n > 255 ? 255 : (n < 0 ? 0 : n));
                    }
                }
                break;
            }
            case 3: {
                int[][] id = (int[][])data;
                int bands = id.length;
                for (int j = 0; j < bands; ++j) {
                    int[] d = id[j];
                    int size = d.length;
                    for (int i = 0; i < size; ++i) {
                        int n = d[i];
                        d[i] = n > 255 ? 255 : (n < 0 ? 0 : n);
                    }
                }
                break;
            }
            case 4: {
                float[][] fd = (float[][])data;
                int bands = fd.length;
                for (int j = 0; j < bands; ++j) {
                    float[] d = fd[j];
                    int size = d.length;
                    for (int i = 0; i < size; ++i) {
                        float n = d[i];
                        d[i] = n > 255.0f ? 255.0f : (n < 0.0f ? 0.0f : n);
                    }
                }
                break;
            }
            case 5: {
                double[][] dd = (double[][])data;
                int bands = dd.length;
                for (int j = 0; j < bands; ++j) {
                    double[] d = dd[j];
                    int size = d.length;
                    for (int i = 0; i < size; ++i) {
                        double n = d[i];
                        d[i] = n > 255.0 ? 255.0 : (n < 0.0 ? 0.0 : n);
                    }
                }
                break;
            }
        }
    }

    private void clampUShort(Object data, int type) {
        switch (type) {
            case 3: {
                int[][] id = (int[][])data;
                int bands = id.length;
                for (int j = 0; j < bands; ++j) {
                    int[] d = id[j];
                    int size = d.length;
                    for (int i = 0; i < size; ++i) {
                        int n = d[i];
                        d[i] = n > 65535 ? 65535 : (n < 0 ? 0 : n);
                    }
                }
                break;
            }
            case 4: {
                float[][] fd = (float[][])data;
                int bands = fd.length;
                for (int j = 0; j < bands; ++j) {
                    float[] d = fd[j];
                    int size = d.length;
                    for (int i = 0; i < size; ++i) {
                        float n = d[i];
                        d[i] = n > 65535.0f ? 65535.0f : (n < 0.0f ? 0.0f : n);
                    }
                }
                break;
            }
            case 5: {
                double[][] dd = (double[][])data;
                int bands = dd.length;
                for (int j = 0; j < bands; ++j) {
                    double[] d = dd[j];
                    int size = d.length;
                    for (int i = 0; i < size; ++i) {
                        double n = d[i];
                        d[i] = n > 65535.0 ? 65535.0 : (n < 0.0 ? 0.0 : n);
                    }
                }
                break;
            }
        }
    }

    private void clampShort(Object data, int type) {
        switch (type) {
            case 3: {
                int[][] id = (int[][])data;
                int bands = id.length;
                for (int j = 0; j < bands; ++j) {
                    int[] d = id[j];
                    int size = d.length;
                    for (int i = 0; i < size; ++i) {
                        int n = d[i];
                        d[i] = n > Short.MAX_VALUE ? Short.MAX_VALUE : (n < Short.MIN_VALUE ? Short.MIN_VALUE : n);
                    }
                }
                break;
            }
            case 4: {
                float[][] fd = (float[][])data;
                int bands = fd.length;
                for (int j = 0; j < bands; ++j) {
                    float[] d = fd[j];
                    int size = d.length;
                    for (int i = 0; i < size; ++i) {
                        float n = d[i];
                        d[i] = n > 32767.0f ? 32767.0f : (n < -32768.0f ? -32768.0f : n);
                    }
                }
                break;
            }
            case 5: {
                double[][] dd = (double[][])data;
                int bands = dd.length;
                for (int j = 0; j < bands; ++j) {
                    double[] d = dd[j];
                    int size = d.length;
                    for (int i = 0; i < size; ++i) {
                        double n = d[i];
                        d[i] = n > 32767.0 ? 32767.0 : (n < -32768.0 ? -32768.0 : n);
                    }
                }
                break;
            }
        }
    }

    private void clampInt(Object data, int type) {
        switch (type) {
            case 4: {
                float[][] fd = (float[][])data;
                int bands = fd.length;
                for (int j = 0; j < bands; ++j) {
                    float[] d = fd[j];
                    int size = d.length;
                    for (int i = 0; i < size; ++i) {
                        float n = d[i];
                        d[i] = n > 2.14748365E9f ? 2.14748365E9f : (n < -2.14748365E9f ? -2.14748365E9f : n);
                    }
                }
                break;
            }
            case 5: {
                double[][] dd = (double[][])data;
                int bands = dd.length;
                for (int j = 0; j < bands; ++j) {
                    double[] d = dd[j];
                    int size = d.length;
                    for (int i = 0; i < size; ++i) {
                        double n = d[i];
                        d[i] = n > 2.147483647E9 ? 2.147483647E9 : (n < -2.147483648E9 ? -2.147483648E9 : n);
                    }
                }
                break;
            }
        }
    }

    private void clampFloat(Object data, int type) {
        switch (type) {
            case 5: {
                double[][] dd = (double[][])data;
                int bands = dd.length;
                for (int j = 0; j < bands; ++j) {
                    double[] d = dd[j];
                    int size = d.length;
                    for (int i = 0; i < size; ++i) {
                        double n = d[i];
                        d[i] = n > 3.4028234663852886E38 ? 3.4028234663852886E38 : (n < -3.4028234663852886E38 ? -3.4028234663852886E38 : n);
                    }
                }
                break;
            }
        }
    }

    public PackedImageData getPackedPixels(Raster raster, Rectangle rect, boolean isDest, boolean coerceZeroOffset) {
        int offset;
        int bitOffset;
        int lineStride;
        byte[] data;
        boolean set;
        block16: {
            block14: {
                block15: {
                    if (!this.isPacked) {
                        throw new IllegalArgumentException(JaiI18N.getString("PixelAccessor3"));
                    }
                    if (!raster.getBounds().contains(rect)) {
                        throw new IllegalArgumentException(JaiI18N.getString("PixelAccessor0"));
                    }
                    if (!this.isMultiPixelPackedSM) break block14;
                    set = isDest;
                    if (!coerceZeroOffset) break block15;
                    data = ImageUtil.getPackedBinaryData(raster, rect);
                    lineStride = (rect.width + 7) / 8;
                    bitOffset = 0;
                    offset = 0;
                    break block16;
                }
                MultiPixelPackedSampleModel sm = (MultiPixelPackedSampleModel)this.sampleModel;
                DataBuffer db = raster.getDataBuffer();
                int dbOffset = db.getOffset();
                int x = rect.x - raster.getSampleModelTranslateX();
                int y = rect.y - raster.getSampleModelTranslateY();
                int smLineStride = sm.getScanlineStride();
                int minOffset = sm.getOffset(x, y) + dbOffset;
                int maxOffset = sm.getOffset(x + rect.width - 1, y) + dbOffset;
                int numElements = maxOffset - minOffset + 1;
                int smBitOffset = sm.getBitOffset(x);
                switch (this.bufferType) {
                    case 0: {
                        data = ((DataBufferByte)db).getData();
                        lineStride = smLineStride;
                        offset = minOffset;
                        bitOffset = smBitOffset;
                        set = false;
                        break;
                    }
                    case 1: {
                        lineStride = numElements * 2;
                        offset = smBitOffset / 8;
                        bitOffset = smBitOffset % 8;
                        data = new byte[lineStride * rect.height];
                        short[] sd = ((DataBufferUShort)db).getData();
                        int i = 0;
                        for (int h = 0; h < rect.height; ++h) {
                            for (int w = minOffset; w <= maxOffset; ++w) {
                                short d = sd[w];
                                data[i++] = (byte)(d >>> 8 & 0xFF);
                                data[i++] = (byte)(d & 0xFF);
                            }
                            minOffset += smLineStride;
                            maxOffset += smLineStride;
                        }
                        break block16;
                    }
                    case 3: {
                        lineStride = numElements * 4;
                        offset = smBitOffset / 8;
                        bitOffset = smBitOffset % 8;
                        data = new byte[lineStride * rect.height];
                        int[] id = ((DataBufferInt)db).getData();
                        int i = 0;
                        for (int h = 0; h < rect.height; ++h) {
                            for (int w = minOffset; w <= maxOffset; ++w) {
                                int d = id[w];
                                data[i++] = (byte)(d >>> 24 & 0xFF);
                                data[i++] = (byte)(d >>> 16 & 0xFF);
                                data[i++] = (byte)(d >>> 8 & 0xFF);
                                data[i++] = (byte)(d & 0xFF);
                            }
                            minOffset += smLineStride;
                            maxOffset += smLineStride;
                        }
                        break block16;
                    }
                    default: {
                        throw new RuntimeException();
                    }
                }
                break block16;
            }
            lineStride = (rect.width + 7) / 8;
            offset = 0;
            bitOffset = 0;
            set = isDest & raster instanceof WritableRaster;
            data = new byte[lineStride * rect.height];
            if (!isDest) {
                int size = lineStride * 8;
                int[] p = new int[size];
                int i = 0;
                for (int h = 0; h < rect.height; ++h) {
                    p = raster.getPixels(rect.x, rect.y + h, rect.width, 1, p);
                    for (int w = 0; w < size; w += 8) {
                        data[i++] = (byte)(p[w] << 7 | p[w + 1] << 6 | p[w + 2] << 5 | p[w + 3] << 4 | p[w + 4] << 3 | p[w + 5] << 2 | p[w + 6] << 1 | p[w + 7]);
                    }
                }
            }
        }
        return new PackedImageData(raster, rect, data, lineStride, offset, bitOffset, coerceZeroOffset, set);
    }

    public void setPackedPixels(PackedImageData pid) {
        block14: {
            byte[] data;
            Rectangle rect;
            Raster raster;
            block12: {
                block13: {
                    if (pid == null) {
                        throw new IllegalArgumentException(JaiI18N.getString("Generic0"));
                    }
                    if (!pid.convertToDest) {
                        return;
                    }
                    raster = pid.raster;
                    rect = pid.rect;
                    data = pid.data;
                    if (!this.isMultiPixelPackedSM) break block12;
                    if (!pid.coercedZeroOffset) break block13;
                    ImageUtil.setPackedBinaryData(data, (WritableRaster)raster, rect);
                    break block14;
                }
                MultiPixelPackedSampleModel sm = (MultiPixelPackedSampleModel)this.sampleModel;
                DataBuffer db = raster.getDataBuffer();
                int dbOffset = db.getOffset();
                int x = rect.x - raster.getSampleModelTranslateX();
                int y = rect.y - raster.getSampleModelTranslateY();
                int lineStride = sm.getScanlineStride();
                int minOffset = sm.getOffset(x, y) + dbOffset;
                int maxOffset = sm.getOffset(x + rect.width - 1, y) + dbOffset;
                switch (this.bufferType) {
                    case 1: {
                        short[] sd = ((DataBufferUShort)db).getData();
                        int i = 0;
                        for (int h = 0; h < rect.height; ++h) {
                            for (int w = minOffset; w <= maxOffset; ++w) {
                                sd[w] = (short)(data[i++] << 8 | data[i++]);
                            }
                            minOffset += lineStride;
                            maxOffset += lineStride;
                        }
                        break block14;
                    }
                    case 3: {
                        int[] id = ((DataBufferInt)db).getData();
                        int i = 0;
                        for (int h = 0; h < rect.height; ++h) {
                            for (int w = minOffset; w <= maxOffset; ++w) {
                                id[w] = data[i++] << 24 | data[i++] << 16 | data[i++] << 8 | data[i++];
                            }
                            minOffset += lineStride;
                            maxOffset += lineStride;
                        }
                        break;
                    }
                }
                break block14;
            }
            WritableRaster wr = (WritableRaster)raster;
            int size = pid.lineStride * 8;
            int[] p = new int[size];
            int i = 0;
            for (int h = 0; h < rect.height; ++h) {
                for (int w = 0; w < size; w += 8) {
                    p[w] = data[i] >>> 7 & 1;
                    p[w + 1] = data[i] >>> 6 & 1;
                    p[w + 2] = data[i] >>> 5 & 1;
                    p[w + 3] = data[i] >>> 4 & 1;
                    p[w + 4] = data[i] >>> 3 & 1;
                    p[w + 5] = data[i] >>> 2 & 1;
                    p[w + 6] = data[i] >>> 1 & 1;
                    p[w + 7] = data[i] & 1;
                    ++i;
                }
                wr.setPixels(rect.x, rect.y + h, rect.width, 1, p);
            }
        }
    }

    public UnpackedImageData getComponents(Raster raster, Rectangle rect, int type) {
        if (!this.hasCompatibleCM) {
            throw new IllegalArgumentException(JaiI18N.getString("PixelAccessor5"));
        }
        if (!raster.getBounds().contains(rect)) {
            throw new IllegalArgumentException(JaiI18N.getString("PixelAccessor0"));
        }
        if (type < 0 || type > 5) {
            throw new IllegalArgumentException(JaiI18N.getString("PixelAccessor1"));
        }
        if (type < this.componentType || this.componentType == 1 && type == 2) {
            throw new IllegalArgumentException(JaiI18N.getString("PixelAccessor4"));
        }
        int size = rect.width * rect.height * this.numComponents;
        int[] ic = new int[size];
        int width = rect.x + rect.width;
        int height = rect.y + rect.height;
        int i = 0;
        for (int y = rect.y; y < height; ++y) {
            for (int x = rect.x; x < width; ++x) {
                Object p = raster.getDataElements(x, y, null);
                this.colorModel.getComponents(p, ic, i);
                i += this.numComponents;
            }
        }
        Object data = null;
        switch (type) {
            case 0: {
                byte[] bc = new byte[size];
                for (int i2 = 0; i2 < size; ++i2) {
                    bc[i2] = (byte)(ic[i2] & 0xFF);
                }
                data = this.repeatBand(bc, this.numComponents);
                break;
            }
            case 1: {
                short[] usc = new short[size];
                for (int i3 = 0; i3 < size; ++i3) {
                    usc[i3] = (short)(ic[i3] & 0xFFFF);
                }
                data = this.repeatBand(usc, this.numComponents);
                break;
            }
            case 2: {
                short[] sc = new short[size];
                for (int i4 = 0; i4 < size; ++i4) {
                    sc[i4] = (short)ic[i4];
                }
                data = this.repeatBand(sc, this.numComponents);
                break;
            }
            case 3: {
                data = this.repeatBand(ic, this.numComponents);
                break;
            }
            case 4: {
                float[] fc = new float[size];
                for (int i5 = 0; i5 < size; ++i5) {
                    fc[i5] = ic[i5];
                }
                data = this.repeatBand(fc, this.numComponents);
                break;
            }
            case 5: {
                double[] dc = new double[size];
                for (int i6 = 0; i6 < size; ++i6) {
                    dc[i6] = ic[i6];
                }
                data = this.repeatBand(dc, this.numComponents);
            }
        }
        return new UnpackedImageData(raster, rect, type, data, this.numComponents, this.numComponents * rect.width, this.getInterleavedOffsets(this.numComponents), raster instanceof WritableRaster);
    }

    public void setComponents(UnpackedImageData uid) {
        int i;
        if (uid == null) {
            throw new IllegalArgumentException(JaiI18N.getString("Generic0"));
        }
        if (!uid.convertToDest) {
            return;
        }
        WritableRaster raster = (WritableRaster)uid.raster;
        Rectangle rect = uid.rect;
        int type = uid.type;
        int size = rect.width * rect.height * this.numComponents;
        int[] ic = null;
        switch (type) {
            case 0: {
                byte[] bc = uid.getByteData(0);
                ic = new int[size];
                for (int i2 = 0; i2 < size; ++i2) {
                    ic[i2] = bc[i2] & 0xFF;
                }
                break;
            }
            case 1: {
                short[] usc = uid.getShortData(0);
                ic = new int[size];
                for (i = 0; i < size; ++i) {
                    ic[i] = usc[i] & 0xFFFF;
                }
                break;
            }
            case 2: {
                short[] sc = uid.getShortData(0);
                ic = new int[size];
                for (int i3 = 0; i3 < size; ++i3) {
                    ic[i3] = sc[i3];
                }
                break;
            }
            case 3: {
                ic = uid.getIntData(0);
                break;
            }
            case 4: {
                float[] fc = uid.getFloatData(0);
                ic = new int[size];
                for (int i4 = 0; i4 < size; ++i4) {
                    ic[i4] = (int)fc[i4];
                }
                break;
            }
            case 5: {
                double[] dc = uid.getDoubleData(0);
                ic = new int[size];
                for (int i5 = 0; i5 < size; ++i5) {
                    ic[i5] = (int)dc[i5];
                }
                break;
            }
        }
        int width = rect.x + rect.width;
        int height = rect.y + rect.height;
        i = 0;
        for (int y = rect.y; y < height; ++y) {
            for (int x = rect.x; x < width; ++x) {
                Object p = this.colorModel.getDataElements(ic, i, (Object)null);
                raster.setDataElements(x, y, p);
                i += this.numComponents;
            }
        }
    }

    public UnpackedImageData getComponentsRGB(Raster raster, Rectangle rect) {
        if (!this.hasCompatibleCM) {
            throw new IllegalArgumentException(JaiI18N.getString("PixelAccessor5"));
        }
        if (!raster.getBounds().contains(rect)) {
            throw new IllegalArgumentException(JaiI18N.getString("PixelAccessor0"));
        }
        int size = rect.width * rect.height;
        byte[][] data = new byte[4][size];
        byte[] r = data[0];
        byte[] g = data[1];
        byte[] b = data[2];
        byte[] a = data[3];
        int maxX = rect.x + rect.width;
        int maxY = rect.y + rect.height;
        if (this.isIndexCM) {
            IndexColorModel icm = (IndexColorModel)this.colorModel;
            int mapSize = icm.getMapSize();
            byte[] reds = new byte[mapSize];
            icm.getReds(reds);
            byte[] greens = new byte[mapSize];
            icm.getGreens(greens);
            byte[] blues = new byte[mapSize];
            icm.getBlues(blues);
            byte[] alphas = null;
            if (icm.hasAlpha()) {
                alphas = new byte[mapSize];
                icm.getAlphas(alphas);
            }
            int[] indices = raster.getPixels(rect.x, rect.y, rect.width, rect.height, (int[])null);
            if (alphas == null) {
                int i = 0;
                for (int y = rect.y; y < maxY; ++y) {
                    for (int x = rect.x; x < maxX; ++x) {
                        int index = indices[i];
                        r[i] = reds[index];
                        g[i] = greens[index];
                        b[i] = blues[index];
                        ++i;
                    }
                }
            } else {
                int i = 0;
                for (int y = rect.y; y < maxY; ++y) {
                    for (int x = rect.x; x < maxX; ++x) {
                        int index = indices[i];
                        r[i] = reds[index];
                        g[i] = greens[index];
                        b[i] = blues[index];
                        a[i] = alphas[index];
                        ++i;
                    }
                }
            }
        } else {
            int i = 0;
            for (int y = rect.y; y < maxY; ++y) {
                for (int x = rect.x; x < maxX; ++x) {
                    Object p = raster.getDataElements(x, y, null);
                    r[i] = (byte)this.colorModel.getRed(p);
                    g[i] = (byte)this.colorModel.getGreen(p);
                    b[i] = (byte)this.colorModel.getBlue(p);
                    a[i] = (byte)this.colorModel.getAlpha(p);
                    ++i;
                }
            }
        }
        return new UnpackedImageData(raster, rect, 0, data, 1, rect.width, new int[4], raster instanceof WritableRaster);
    }

    public void setComponentsRGB(UnpackedImageData uid) {
        if (uid == null) {
            throw new IllegalArgumentException(JaiI18N.getString("Generic0"));
        }
        if (!uid.convertToDest) {
            return;
        }
        byte[][] data = uid.getByteData();
        byte[] r = data[0];
        byte[] g = data[1];
        byte[] b = data[2];
        byte[] a = data[3];
        WritableRaster raster = (WritableRaster)uid.raster;
        Rectangle rect = uid.rect;
        int maxX = rect.x + rect.width;
        int maxY = rect.y + rect.height;
        int i = 0;
        for (int y = rect.y; y < maxY; ++y) {
            for (int x = rect.x; x < maxX; ++x) {
                int rgb = a[i] << 24 | b[i] << 16 | g[i] << 8 | r[i];
                Object p = this.colorModel.getDataElements(rgb, null);
                raster.setDataElements(x, y, p);
                ++i;
            }
        }
    }
}


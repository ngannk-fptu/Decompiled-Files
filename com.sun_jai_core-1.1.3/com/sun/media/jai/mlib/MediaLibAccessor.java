/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.sun.medialib.mlib.Image
 *  com.sun.medialib.mlib.mediaLibImage
 */
package com.sun.media.jai.mlib;

import com.sun.media.jai.mlib.JaiI18N;
import com.sun.media.jai.mlib.MediaLibLoadException;
import com.sun.media.jai.util.DataBufferUtils;
import com.sun.media.jai.util.ImageUtil;
import com.sun.medialib.mlib.Image;
import com.sun.medialib.mlib.mediaLibImage;
import java.awt.Rectangle;
import java.awt.image.ColorModel;
import java.awt.image.ComponentColorModel;
import java.awt.image.ComponentSampleModel;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferByte;
import java.awt.image.DataBufferInt;
import java.awt.image.DataBufferShort;
import java.awt.image.DataBufferUShort;
import java.awt.image.Raster;
import java.awt.image.RenderedImage;
import java.awt.image.SampleModel;
import java.awt.image.WritableRaster;
import java.awt.image.renderable.ParameterBlock;
import java.io.FilePermission;
import java.security.AccessControlException;
import java.security.AccessController;
import java.security.PrivilegedAction;
import javax.media.jai.ImageLayout;
import javax.media.jai.JAI;
import javax.media.jai.util.ImagingListener;

public class MediaLibAccessor {
    private static final int COPY_MASK_SHIFT = 7;
    private static final int COPY_MASK_SIZE = 1;
    public static final int COPY_MASK = 128;
    public static final int UNCOPIED = 0;
    public static final int COPIED = 128;
    public static final int DATATYPE_MASK = 127;
    private static final int BINARY_MASK_SHIFT = 8;
    private static final int BINARY_MASK_SIZE = 1;
    public static final int BINARY_MASK = 256;
    public static final int NONBINARY = 0;
    public static final int BINARY = 256;
    public static final int TAG_BYTE_UNCOPIED = 0;
    public static final int TAG_USHORT_UNCOPIED = 1;
    public static final int TAG_SHORT_UNCOPIED = 2;
    public static final int TAG_INT_UNCOPIED = 3;
    public static final int TAG_FLOAT_UNCOPIED = 4;
    public static final int TAG_DOUBLE_UNCOPIED = 5;
    public static final int TAG_BYTE_COPIED = 128;
    public static final int TAG_USHORT_COPIED = 129;
    public static final int TAG_SHORT_COPIED = 130;
    public static final int TAG_INT_COPIED = 131;
    public static final int TAG_FLOAT_COPIED = 132;
    public static final int TAG_DOUBLE_COPIED = 133;
    protected Raster raster;
    protected Rectangle rect;
    protected int numBands;
    protected int[] bandOffsets;
    protected int formatTag;
    protected mediaLibImage[] mlimages = null;
    private boolean areBinaryDataPacked = false;
    private static boolean useMlibVar = false;
    private static boolean useMlibVarSet = false;
    static /* synthetic */ Class class$com$sun$media$jai$mlib$MediaLibAccessor;

    private static synchronized boolean useMlib() {
        if (!useMlibVarSet) {
            MediaLibAccessor.setUseMlib();
            useMlibVarSet = true;
        }
        return useMlibVar;
    }

    private static void setUseMlib() {
        boolean disableMediaLib = false;
        try {
            disableMediaLib = Boolean.getBoolean("com.sun.media.jai.disableMediaLib");
        }
        catch (AccessControlException e) {
            // empty catch block
        }
        if (disableMediaLib) {
            useMlibVar = false;
            return;
        }
        try {
            Boolean result;
            SecurityManager securityManager = System.getSecurityManager();
            if (securityManager != null && (class$com$sun$media$jai$mlib$MediaLibAccessor == null ? (class$com$sun$media$jai$mlib$MediaLibAccessor = MediaLibAccessor.class$("com.sun.media.jai.mlib.MediaLibAccessor")) : class$com$sun$media$jai$mlib$MediaLibAccessor).getClassLoader() != null) {
                String osName = System.getProperty("os.name");
                String osArch = System.getProperty("os.arch");
                if ((osName.equals("Solaris") || osName.equals("SunOS")) && osArch.equals("sparc")) {
                    FilePermission fp = new FilePermission("/usr/bin/uname", "execute");
                    securityManager.checkPermission(fp);
                }
            }
            if (!(useMlibVar = (result = (Boolean)AccessController.doPrivileged(new PrivilegedAction(){

                public Object run() {
                    return new Boolean(Image.isAvailable());
                }
            })).booleanValue())) {
                MediaLibAccessor.forwardToListener(JaiI18N.getString("MediaLibAccessor2"), new MediaLibLoadException());
            }
        }
        catch (NoClassDefFoundError ncdfe) {
            useMlibVar = false;
            MediaLibAccessor.forwardToListener(JaiI18N.getString("MediaLibAccessor3"), ncdfe);
        }
        catch (ClassFormatError cfe) {
            useMlibVar = false;
            MediaLibAccessor.forwardToListener(JaiI18N.getString("MediaLibAccessor3"), cfe);
        }
        catch (SecurityException se) {
            useMlibVar = false;
            MediaLibAccessor.forwardToListener(JaiI18N.getString("MediaLibAccessor4"), se);
        }
        if (!useMlibVar) {
            return;
        }
    }

    private static void forwardToListener(String message, Throwable thrown) {
        ImagingListener listener = JAI.getDefaultInstance().getImagingListener();
        if (listener != null) {
            listener.errorOccurred(message, thrown, class$com$sun$media$jai$mlib$MediaLibAccessor == null ? (class$com$sun$media$jai$mlib$MediaLibAccessor = MediaLibAccessor.class$("com.sun.media.jai.mlib.MediaLibAccessor")) : class$com$sun$media$jai$mlib$MediaLibAccessor, false);
        } else {
            System.err.println(message);
        }
    }

    public static boolean isMediaLibCompatible(ParameterBlock args, ImageLayout layout) {
        if (!MediaLibAccessor.isMediaLibCompatible(args)) {
            return false;
        }
        if (layout != null) {
            SampleModel sm = layout.getSampleModel(null);
            if (!(sm == null || sm instanceof ComponentSampleModel && sm.getNumBands() <= 4)) {
                return false;
            }
            ColorModel cm = layout.getColorModel(null);
            if (cm != null && !(cm instanceof ComponentColorModel)) {
                return false;
            }
        }
        return true;
    }

    public static boolean isMediaLibCompatible(ParameterBlock args) {
        if (!MediaLibAccessor.useMlib()) {
            return false;
        }
        int numSrcs = args.getNumSources();
        for (int i = 0; i < numSrcs; ++i) {
            Object src = args.getSource(i);
            if (src instanceof RenderedImage && MediaLibAccessor.isMediaLibCompatible((RenderedImage)src)) continue;
            return false;
        }
        return true;
    }

    public static boolean isMediaLibCompatible(RenderedImage image) {
        if (!MediaLibAccessor.useMlib()) {
            return false;
        }
        SampleModel sm = image.getSampleModel();
        ColorModel cm = image.getColorModel();
        return sm instanceof ComponentSampleModel && sm.getNumBands() <= 4 && (cm == null || cm instanceof ComponentColorModel);
    }

    public static boolean isMediaLibCompatible(SampleModel sm, ColorModel cm) {
        if (!MediaLibAccessor.useMlib()) {
            return false;
        }
        return sm instanceof ComponentSampleModel && sm.getNumBands() <= 4 && (cm == null || cm instanceof ComponentColorModel);
    }

    public static boolean isMediaLibBinaryCompatible(ParameterBlock args, ImageLayout layout) {
        if (!MediaLibAccessor.useMlib()) {
            return false;
        }
        SampleModel sm = null;
        int numSrcs = args.getNumSources();
        for (int i = 0; i < numSrcs; ++i) {
            Object src = args.getSource(i);
            if (src instanceof RenderedImage && (sm = ((RenderedImage)src).getSampleModel()) != null && ImageUtil.isBinary(sm)) continue;
            return false;
        }
        return layout == null || (sm = layout.getSampleModel(null)) == null || ImageUtil.isBinary(sm);
    }

    public static boolean hasSameNumBands(ParameterBlock args, ImageLayout layout) {
        int numSrcs = args.getNumSources();
        if (numSrcs > 0) {
            SampleModel sm;
            RenderedImage src = args.getRenderedSource(0);
            int numBands = src.getSampleModel().getNumBands();
            for (int i = 1; i < numSrcs; ++i) {
                src = args.getRenderedSource(i);
                if (src.getSampleModel().getNumBands() == numBands) continue;
                return false;
            }
            if (layout != null && (sm = layout.getSampleModel(null)) != null && sm.getNumBands() != numBands) {
                return false;
            }
        }
        return true;
    }

    public static int findCompatibleTag(Raster[] srcs, Raster dst) {
        int i;
        SampleModel dstSM = dst.getSampleModel();
        int dstDT = dstSM.getDataType();
        int defaultDataType = dstSM.getDataType();
        boolean allComponentSampleModel = dstSM instanceof ComponentSampleModel;
        boolean allBinary = ImageUtil.isBinary(dstSM);
        if (srcs != null) {
            int numSources = srcs.length;
            for (int i2 = 0; i2 < numSources; ++i2) {
                int srcDataType;
                SampleModel srcSampleModel = srcs[i2].getSampleModel();
                if (!(srcSampleModel instanceof ComponentSampleModel)) {
                    allComponentSampleModel = false;
                }
                if (!ImageUtil.isBinary(srcSampleModel)) {
                    allBinary = false;
                }
                if ((srcDataType = srcSampleModel.getTransferType()) <= defaultDataType) continue;
                defaultDataType = srcDataType;
            }
        }
        if (allBinary) {
            return 256;
        }
        if (!(allComponentSampleModel || defaultDataType != 0 && defaultDataType != 1 && defaultDataType != 2)) {
            defaultDataType = 3;
        }
        int tag = defaultDataType | 0x80;
        if (!allComponentSampleModel) {
            return tag;
        }
        SampleModel[] srcSM = srcs == null ? new SampleModel[]{} : new SampleModel[srcs.length];
        for (i = 0; i < srcSM.length; ++i) {
            srcSM[i] = srcs[i].getSampleModel();
            if (dstDT == srcSM[i].getDataType()) continue;
            return tag;
        }
        if (MediaLibAccessor.isPixelSequential(dstSM)) {
            for (i = 0; i < srcSM.length; ++i) {
                if (MediaLibAccessor.isPixelSequential(srcSM[i])) continue;
                return tag;
            }
            for (i = 0; i < srcSM.length; ++i) {
                if (MediaLibAccessor.hasMatchingBandOffsets((ComponentSampleModel)dstSM, (ComponentSampleModel)srcSM[i])) continue;
                return tag;
            }
            return dstDT | 0;
        }
        return tag;
    }

    public static boolean isPixelSequential(SampleModel sm) {
        ComponentSampleModel csm = null;
        if (!(sm instanceof ComponentSampleModel)) {
            return false;
        }
        csm = (ComponentSampleModel)sm;
        int pixelStride = csm.getPixelStride();
        int[] bandOffsets = csm.getBandOffsets();
        int[] bankIndices = csm.getBankIndices();
        if (pixelStride != bandOffsets.length) {
            return false;
        }
        for (int i = 0; i < bandOffsets.length; ++i) {
            if (bandOffsets[i] >= pixelStride || bankIndices[i] != bankIndices[0]) {
                return false;
            }
            for (int j = i + 1; j < bandOffsets.length; ++j) {
                if (bandOffsets[i] != bandOffsets[j]) continue;
                return false;
            }
        }
        return true;
    }

    public static boolean hasMatchingBandOffsets(ComponentSampleModel dst, ComponentSampleModel src) {
        int[] dstBandOffsets;
        int[] srcBandOffsets = dst.getBandOffsets();
        if (srcBandOffsets.length != (dstBandOffsets = src.getBandOffsets()).length) {
            return false;
        }
        for (int i = 0; i < srcBandOffsets.length; ++i) {
            if (srcBandOffsets[i] == dstBandOffsets[i]) continue;
            return false;
        }
        return true;
    }

    public static int getMediaLibDataType(int formatTag) {
        int dataType = formatTag & 0x7F;
        switch (dataType) {
            case 0: {
                return 1;
            }
            case 1: {
                return 6;
            }
            case 2: {
                return 2;
            }
            case 3: {
                return 3;
            }
            case 5: {
                return 5;
            }
            case 4: {
                return 4;
            }
        }
        return -1;
    }

    public MediaLibAccessor(Raster raster, Rectangle rect, int formatTag, boolean preferPacked) {
        this.areBinaryDataPacked = preferPacked;
        this.raster = raster;
        this.rect = new Rectangle(rect);
        this.formatTag = formatTag;
        if (this.isBinary()) {
            byte[] bdata;
            int scanlineStride;
            int mlibType;
            this.numBands = 1;
            this.bandOffsets = new int[]{0};
            this.mlimages = new mediaLibImage[1];
            if (this.areBinaryDataPacked) {
                mlibType = 0;
                scanlineStride = (rect.width + 7) / 8;
                bdata = ImageUtil.getPackedBinaryData(raster, rect);
                this.formatTag = bdata == ((DataBufferByte)raster.getDataBuffer()).getData() ? (this.formatTag |= 0) : (this.formatTag |= 0x80);
            } else {
                mlibType = 1;
                scanlineStride = rect.width;
                bdata = ImageUtil.getUnpackedBinaryData(raster, rect);
                this.formatTag |= 0x80;
            }
            this.mlimages[0] = new mediaLibImage(mlibType, 1, rect.width, rect.height, scanlineStride, 0, (Object)bdata);
            return;
        }
        if ((formatTag & 0x80) == 0) {
            ComponentSampleModel csm = (ComponentSampleModel)raster.getSampleModel();
            this.numBands = csm.getNumBands();
            this.bandOffsets = csm.getBandOffsets();
            int dataOffset = raster.getDataBuffer().getOffset();
            dataOffset += (rect.y - raster.getSampleModelTranslateY()) * csm.getScanlineStride() + (rect.x - raster.getSampleModelTranslateX()) * csm.getPixelStride();
            int scanlineStride = csm.getScanlineStride();
            switch (formatTag & 0x7F) {
                case 0: {
                    DataBufferByte dbb = (DataBufferByte)raster.getDataBuffer();
                    this.mlimages = new mediaLibImage[1];
                    this.mlimages[0] = new mediaLibImage(1, this.numBands, rect.width, rect.height, scanlineStride, dataOffset, (Object)dbb.getData());
                    break;
                }
                case 1: {
                    DataBufferUShort dbus = (DataBufferUShort)raster.getDataBuffer();
                    this.mlimages = new mediaLibImage[1];
                    this.mlimages[0] = new mediaLibImage(6, this.numBands, rect.width, rect.height, scanlineStride, dataOffset, (Object)dbus.getData());
                    break;
                }
                case 2: {
                    DataBufferShort dbs = (DataBufferShort)raster.getDataBuffer();
                    this.mlimages = new mediaLibImage[1];
                    this.mlimages[0] = new mediaLibImage(2, this.numBands, rect.width, rect.height, scanlineStride, dataOffset, (Object)dbs.getData());
                    break;
                }
                case 3: {
                    DataBufferInt dbi = (DataBufferInt)raster.getDataBuffer();
                    this.mlimages = new mediaLibImage[1];
                    this.mlimages[0] = new mediaLibImage(3, this.numBands, rect.width, rect.height, scanlineStride, dataOffset, (Object)dbi.getData());
                    break;
                }
                case 4: {
                    DataBuffer dbf = raster.getDataBuffer();
                    this.mlimages = new mediaLibImage[1];
                    this.mlimages[0] = new mediaLibImage(4, this.numBands, rect.width, rect.height, scanlineStride, dataOffset, (Object)DataBufferUtils.getDataFloat(dbf));
                    break;
                }
                case 5: {
                    DataBuffer dbd = raster.getDataBuffer();
                    this.mlimages = new mediaLibImage[1];
                    this.mlimages[0] = new mediaLibImage(5, this.numBands, rect.width, rect.height, scanlineStride, dataOffset, (Object)DataBufferUtils.getDataDouble(dbd));
                    break;
                }
                default: {
                    throw new IllegalArgumentException((formatTag & 0x7F) + JaiI18N.getString("MediaLibAccessor1"));
                }
            }
        } else {
            this.numBands = raster.getNumBands();
            this.bandOffsets = new int[this.numBands];
            for (int i = 0; i < this.numBands; ++i) {
                this.bandOffsets[i] = i;
            }
            int scanlineStride = rect.width * this.numBands;
            switch (formatTag & 0x7F) {
                case 0: {
                    byte[] bdata = new byte[rect.width * rect.height * this.numBands];
                    this.mlimages = new mediaLibImage[1];
                    this.mlimages[0] = new mediaLibImage(1, this.numBands, rect.width, rect.height, scanlineStride, 0, (Object)bdata);
                    break;
                }
                case 1: {
                    short[] usdata = new short[rect.width * rect.height * this.numBands];
                    this.mlimages = new mediaLibImage[1];
                    this.mlimages[0] = new mediaLibImage(6, this.numBands, rect.width, rect.height, scanlineStride, 0, (Object)usdata);
                    break;
                }
                case 2: {
                    short[] sdata = new short[rect.width * rect.height * this.numBands];
                    this.mlimages = new mediaLibImage[1];
                    this.mlimages[0] = new mediaLibImage(2, this.numBands, rect.width, rect.height, scanlineStride, 0, (Object)sdata);
                    break;
                }
                case 3: {
                    int[] idata = new int[rect.width * rect.height * this.numBands];
                    this.mlimages = new mediaLibImage[1];
                    this.mlimages[0] = new mediaLibImage(3, this.numBands, rect.width, rect.height, scanlineStride, 0, (Object)idata);
                    break;
                }
                case 4: {
                    float[] fdata = new float[rect.width * rect.height * this.numBands];
                    this.mlimages = new mediaLibImage[1];
                    this.mlimages[0] = new mediaLibImage(4, this.numBands, rect.width, rect.height, scanlineStride, 0, (Object)fdata);
                    break;
                }
                case 5: {
                    double[] ddata = new double[rect.width * rect.height * this.numBands];
                    this.mlimages = new mediaLibImage[1];
                    this.mlimages[0] = new mediaLibImage(5, this.numBands, rect.width, rect.height, scanlineStride, 0, (Object)ddata);
                    break;
                }
                default: {
                    throw new IllegalArgumentException((formatTag & 0x7F) + JaiI18N.getString("MediaLibAccessor1"));
                }
            }
            this.copyDataFromRaster();
        }
    }

    public MediaLibAccessor(Raster raster, Rectangle rect, int formatTag) {
        this(raster, rect, formatTag, false);
    }

    public boolean isBinary() {
        return (this.formatTag & 0x100) == 256;
    }

    public mediaLibImage[] getMediaLibImages() {
        return this.mlimages;
    }

    public int getDataType() {
        return this.formatTag & 0x7F;
    }

    public boolean isDataCopy() {
        return (this.formatTag & 0x80) == 128;
    }

    public int[] getBandOffsets() {
        return this.bandOffsets;
    }

    public int[] getIntParameters(int band, int[] params) {
        int[] returnParams = new int[this.numBands];
        for (int i = 0; i < this.numBands; ++i) {
            returnParams[i] = params[this.bandOffsets[i + band]];
        }
        return returnParams;
    }

    public int[][] getIntArrayParameters(int band, int[][] params) {
        int[][] returnParams = new int[this.numBands][];
        for (int i = 0; i < this.numBands; ++i) {
            returnParams[i] = params[this.bandOffsets[i + band]];
        }
        return returnParams;
    }

    public double[] getDoubleParameters(int band, double[] params) {
        double[] returnParams = new double[this.numBands];
        for (int i = 0; i < this.numBands; ++i) {
            returnParams[i] = params[this.bandOffsets[i + band]];
        }
        return returnParams;
    }

    private void copyDataFromRaster() {
        if (this.raster.getSampleModel() instanceof ComponentSampleModel) {
            int i;
            int i2;
            ComponentSampleModel csm = (ComponentSampleModel)this.raster.getSampleModel();
            int rasScanlineStride = csm.getScanlineStride();
            int rasPixelStride = csm.getPixelStride();
            int subRasterOffset = (this.rect.y - this.raster.getSampleModelTranslateY()) * rasScanlineStride + (this.rect.x - this.raster.getSampleModelTranslateX()) * rasPixelStride;
            int[] rasBankIndices = csm.getBankIndices();
            int[] rasBandOffsets = csm.getBandOffsets();
            int[] rasDataOffsets = this.raster.getDataBuffer().getOffsets();
            if (rasDataOffsets.length == 1) {
                i2 = 0;
                while (i2 < this.numBands) {
                    int n = i2++;
                    rasBandOffsets[n] = rasBandOffsets[n] + (rasDataOffsets[0] + subRasterOffset);
                }
            } else if (rasDataOffsets.length == rasBandOffsets.length) {
                for (i2 = 0; i2 < this.numBands; ++i2) {
                    int n = i2;
                    rasBandOffsets[n] = rasBandOffsets[n] + (rasDataOffsets[i2] + subRasterOffset);
                }
            }
            Object mlibDataArray = null;
            switch (this.getDataType()) {
                case 0: {
                    byte[][] bArray = new byte[this.numBands][];
                    for (int i3 = 0; i3 < this.numBands; ++i3) {
                        bArray[i3] = this.mlimages[0].getByteData();
                    }
                    mlibDataArray = bArray;
                    break;
                }
                case 1: {
                    short[][] usArray = new short[this.numBands][];
                    for (int i4 = 0; i4 < this.numBands; ++i4) {
                        usArray[i4] = this.mlimages[0].getUShortData();
                    }
                    mlibDataArray = usArray;
                    break;
                }
                case 2: {
                    short[][] sArray = new short[this.numBands][];
                    for (i = 0; i < this.numBands; ++i) {
                        sArray[i] = this.mlimages[0].getShortData();
                    }
                    mlibDataArray = sArray;
                    break;
                }
                case 3: {
                    int[][] iArray = new int[this.numBands][];
                    for (int i5 = 0; i5 < this.numBands; ++i5) {
                        iArray[i5] = this.mlimages[0].getIntData();
                    }
                    mlibDataArray = iArray;
                    break;
                }
                case 4: {
                    float[][] fArray = new float[this.numBands][];
                    for (int i6 = 0; i6 < this.numBands; ++i6) {
                        fArray[i6] = this.mlimages[0].getFloatData();
                    }
                    mlibDataArray = fArray;
                    break;
                }
                case 5: {
                    double[][] dArray = new double[this.numBands][];
                    for (int i7 = 0; i7 < this.numBands; ++i7) {
                        dArray[i7] = this.mlimages[0].getDoubleData();
                    }
                    mlibDataArray = dArray;
                }
            }
            Object rasDataArray = null;
            switch (csm.getDataType()) {
                case 0: {
                    DataBufferByte dbb = (DataBufferByte)this.raster.getDataBuffer();
                    byte[][] rasByteDataArray = new byte[this.numBands][];
                    for (i = 0; i < this.numBands; ++i) {
                        rasByteDataArray[i] = dbb.getData(rasBankIndices[i]);
                    }
                    rasDataArray = rasByteDataArray;
                    break;
                }
                case 1: {
                    DataBufferUShort dbus = (DataBufferUShort)this.raster.getDataBuffer();
                    short[][] rasUShortDataArray = new short[this.numBands][];
                    for (i = 0; i < this.numBands; ++i) {
                        rasUShortDataArray[i] = dbus.getData(rasBankIndices[i]);
                    }
                    rasDataArray = rasUShortDataArray;
                    break;
                }
                case 2: {
                    DataBufferShort dbs = (DataBufferShort)this.raster.getDataBuffer();
                    short[][] rasShortDataArray = new short[this.numBands][];
                    for (i = 0; i < this.numBands; ++i) {
                        rasShortDataArray[i] = dbs.getData(rasBankIndices[i]);
                    }
                    rasDataArray = rasShortDataArray;
                    break;
                }
                case 3: {
                    DataBufferInt dbi = (DataBufferInt)this.raster.getDataBuffer();
                    int[][] rasIntDataArray = new int[this.numBands][];
                    for (i = 0; i < this.numBands; ++i) {
                        rasIntDataArray[i] = dbi.getData(rasBankIndices[i]);
                    }
                    rasDataArray = rasIntDataArray;
                    break;
                }
                case 4: {
                    DataBuffer dbf = this.raster.getDataBuffer();
                    float[][] rasFloatDataArray = new float[this.numBands][];
                    for (i = 0; i < this.numBands; ++i) {
                        rasFloatDataArray[i] = DataBufferUtils.getDataFloat(dbf, rasBankIndices[i]);
                    }
                    rasDataArray = rasFloatDataArray;
                    break;
                }
                case 5: {
                    DataBuffer dbd = this.raster.getDataBuffer();
                    double[][] rasDoubleDataArray = new double[this.numBands][];
                    for (i = 0; i < this.numBands; ++i) {
                        rasDoubleDataArray[i] = DataBufferUtils.getDataDouble(dbd, rasBankIndices[i]);
                    }
                    rasDataArray = rasDoubleDataArray;
                }
            }
            Image.Reformat((Object)mlibDataArray, (Object)rasDataArray, (int)this.numBands, (int)this.rect.width, (int)this.rect.height, (int)MediaLibAccessor.getMediaLibDataType(this.getDataType()), (int[])this.bandOffsets, (int)(this.rect.width * this.numBands), (int)this.numBands, (int)MediaLibAccessor.getMediaLibDataType(csm.getDataType()), (int[])rasBandOffsets, (int)rasScanlineStride, (int)rasPixelStride);
        } else {
            switch (this.getDataType()) {
                case 3: {
                    this.raster.getPixels(this.rect.x, this.rect.y, this.rect.width, this.rect.height, this.mlimages[0].getIntData());
                    break;
                }
                case 4: {
                    this.raster.getPixels(this.rect.x, this.rect.y, this.rect.width, this.rect.height, this.mlimages[0].getFloatData());
                    break;
                }
                case 5: {
                    this.raster.getPixels(this.rect.x, this.rect.y, this.rect.width, this.rect.height, this.mlimages[0].getDoubleData());
                }
            }
        }
    }

    public void copyDataToRaster() {
        if (this.isDataCopy()) {
            if (this.isBinary()) {
                if (this.areBinaryDataPacked) {
                    ImageUtil.setPackedBinaryData(this.mlimages[0].getBitData(), (WritableRaster)this.raster, this.rect);
                } else {
                    ImageUtil.setUnpackedBinaryData(this.mlimages[0].getByteData(), (WritableRaster)this.raster, this.rect);
                }
                return;
            }
            WritableRaster wr = (WritableRaster)this.raster;
            if (wr.getSampleModel() instanceof ComponentSampleModel) {
                int i;
                int i2;
                ComponentSampleModel csm = (ComponentSampleModel)wr.getSampleModel();
                int rasScanlineStride = csm.getScanlineStride();
                int rasPixelStride = csm.getPixelStride();
                int subRasterOffset = (this.rect.y - this.raster.getSampleModelTranslateY()) * rasScanlineStride + (this.rect.x - this.raster.getSampleModelTranslateX()) * rasPixelStride;
                int[] rasBankIndices = csm.getBankIndices();
                int[] rasBandOffsets = csm.getBandOffsets();
                int[] rasDataOffsets = this.raster.getDataBuffer().getOffsets();
                if (rasDataOffsets.length == 1) {
                    i2 = 0;
                    while (i2 < this.numBands) {
                        int n = i2++;
                        rasBandOffsets[n] = rasBandOffsets[n] + (rasDataOffsets[0] + subRasterOffset);
                    }
                } else if (rasDataOffsets.length == rasBandOffsets.length) {
                    for (i2 = 0; i2 < this.numBands; ++i2) {
                        int n = i2;
                        rasBandOffsets[n] = rasBandOffsets[n] + (rasDataOffsets[i2] + subRasterOffset);
                    }
                }
                Object mlibDataArray = null;
                switch (this.getDataType()) {
                    case 0: {
                        byte[][] bArray = new byte[this.numBands][];
                        for (int i3 = 0; i3 < this.numBands; ++i3) {
                            bArray[i3] = this.mlimages[0].getByteData();
                        }
                        mlibDataArray = bArray;
                        break;
                    }
                    case 1: {
                        short[][] usArray = new short[this.numBands][];
                        for (int i4 = 0; i4 < this.numBands; ++i4) {
                            usArray[i4] = this.mlimages[0].getUShortData();
                        }
                        mlibDataArray = usArray;
                        break;
                    }
                    case 2: {
                        short[][] sArray = new short[this.numBands][];
                        for (int i5 = 0; i5 < this.numBands; ++i5) {
                            sArray[i5] = this.mlimages[0].getShortData();
                        }
                        mlibDataArray = sArray;
                        break;
                    }
                    case 3: {
                        int[][] iArray = new int[this.numBands][];
                        for (i = 0; i < this.numBands; ++i) {
                            iArray[i] = this.mlimages[0].getIntData();
                        }
                        mlibDataArray = iArray;
                        break;
                    }
                    case 4: {
                        float[][] fArray = new float[this.numBands][];
                        for (int i6 = 0; i6 < this.numBands; ++i6) {
                            fArray[i6] = this.mlimages[0].getFloatData();
                        }
                        mlibDataArray = fArray;
                        break;
                    }
                    case 5: {
                        double[][] dArray = new double[this.numBands][];
                        for (int i7 = 0; i7 < this.numBands; ++i7) {
                            dArray[i7] = this.mlimages[0].getDoubleData();
                        }
                        mlibDataArray = dArray;
                    }
                }
                byte[] tmpDataArray = null;
                Object rasDataArray = null;
                switch (csm.getDataType()) {
                    case 0: {
                        DataBufferByte dbb = (DataBufferByte)this.raster.getDataBuffer();
                        byte[][] rasByteDataArray = new byte[this.numBands][];
                        for (i = 0; i < this.numBands; ++i) {
                            rasByteDataArray[i] = dbb.getData(rasBankIndices[i]);
                        }
                        tmpDataArray = rasByteDataArray[0];
                        rasDataArray = rasByteDataArray;
                        break;
                    }
                    case 1: {
                        DataBufferUShort dbus = (DataBufferUShort)this.raster.getDataBuffer();
                        short[][] rasUShortDataArray = new short[this.numBands][];
                        for (i = 0; i < this.numBands; ++i) {
                            rasUShortDataArray[i] = dbus.getData(rasBankIndices[i]);
                        }
                        rasDataArray = rasUShortDataArray;
                        break;
                    }
                    case 2: {
                        DataBufferShort dbs = (DataBufferShort)this.raster.getDataBuffer();
                        short[][] rasShortDataArray = new short[this.numBands][];
                        for (i = 0; i < this.numBands; ++i) {
                            rasShortDataArray[i] = dbs.getData(rasBankIndices[i]);
                        }
                        rasDataArray = rasShortDataArray;
                        break;
                    }
                    case 3: {
                        DataBufferInt dbi = (DataBufferInt)this.raster.getDataBuffer();
                        int[][] rasIntDataArray = new int[this.numBands][];
                        for (i = 0; i < this.numBands; ++i) {
                            rasIntDataArray[i] = dbi.getData(rasBankIndices[i]);
                        }
                        rasDataArray = rasIntDataArray;
                        break;
                    }
                    case 4: {
                        DataBuffer dbf = this.raster.getDataBuffer();
                        float[][] rasFloatDataArray = new float[this.numBands][];
                        for (i = 0; i < this.numBands; ++i) {
                            rasFloatDataArray[i] = DataBufferUtils.getDataFloat(dbf, rasBankIndices[i]);
                        }
                        rasDataArray = rasFloatDataArray;
                        break;
                    }
                    case 5: {
                        DataBuffer dbd = this.raster.getDataBuffer();
                        double[][] rasDoubleDataArray = new double[this.numBands][];
                        for (i = 0; i < this.numBands; ++i) {
                            rasDoubleDataArray[i] = DataBufferUtils.getDataDouble(dbd, rasBankIndices[i]);
                        }
                        rasDataArray = rasDoubleDataArray;
                    }
                }
                Image.Reformat((Object)rasDataArray, (Object)mlibDataArray, (int)this.numBands, (int)this.rect.width, (int)this.rect.height, (int)MediaLibAccessor.getMediaLibDataType(csm.getDataType()), (int[])rasBandOffsets, (int)rasScanlineStride, (int)rasPixelStride, (int)MediaLibAccessor.getMediaLibDataType(this.getDataType()), (int[])this.bandOffsets, (int)(this.rect.width * this.numBands), (int)this.numBands);
            } else {
                switch (this.getDataType()) {
                    case 3: {
                        wr.setPixels(this.rect.x, this.rect.y, this.rect.width, this.rect.height, this.mlimages[0].getIntData());
                        break;
                    }
                    case 4: {
                        wr.setPixels(this.rect.x, this.rect.y, this.rect.width, this.rect.height, this.mlimages[0].getFloatData());
                        break;
                    }
                    case 5: {
                        wr.setPixels(this.rect.x, this.rect.y, this.rect.width, this.rect.height, this.mlimages[0].getDoubleData());
                    }
                }
            }
        }
    }

    public void clampDataArrays() {
        if (!this.isDataCopy()) {
            return;
        }
        if (this.raster.getSampleModel() instanceof ComponentSampleModel) {
            return;
        }
        int[] bits = this.raster.getSampleModel().getSampleSize();
        boolean needClamp = false;
        boolean uniformBitSize = true;
        for (int i = 0; i < bits.length; ++i) {
            int bitSize = bits[0];
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
        int width = this.rect.width;
        int height = this.rect.height;
        int scanlineStride = this.numBands * width;
        for (int k = 0; k < this.numBands; ++k) {
            int[] data = this.mlimages[0].getIntData();
            int scanlineOffset = k;
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
                    pixelOffset += this.numBands;
                }
                scanlineOffset += scanlineStride;
            }
        }
    }

    private void clampFloatArrays(float[] hiVals, float[] loVals) {
        int width = this.rect.width;
        int height = this.rect.height;
        int scanlineStride = this.numBands * width;
        for (int k = 0; k < this.numBands; ++k) {
            float[] data = this.mlimages[0].getFloatData();
            int scanlineOffset = k;
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
                    pixelOffset += this.numBands;
                }
                scanlineOffset += scanlineStride;
            }
        }
    }

    private void clampDoubleArrays(double[] hiVals, double[] loVals) {
        int width = this.rect.width;
        int height = this.rect.height;
        int scanlineStride = this.numBands * width;
        for (int k = 0; k < this.numBands; ++k) {
            double[] data = this.mlimages[0].getDoubleData();
            int scanlineOffset = k;
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
                    pixelOffset += this.numBands;
                }
                scanlineOffset += scanlineStride;
            }
        }
    }

    static /* synthetic */ Class class$(String x0) {
        try {
            return Class.forName(x0);
        }
        catch (ClassNotFoundException x1) {
            throw new NoClassDefFoundError(x1.getMessage());
        }
    }
}


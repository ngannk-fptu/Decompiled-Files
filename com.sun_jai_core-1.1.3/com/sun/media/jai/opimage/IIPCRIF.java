/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.sun.media.jai.codec.ImageCodec
 *  com.sun.media.jai.codec.ImageDecoder
 *  com.sun.media.jai.codec.MemoryCacheSeekableStream
 */
package com.sun.media.jai.opimage;

import com.sun.media.jai.codec.ImageCodec;
import com.sun.media.jai.codec.ImageDecoder;
import com.sun.media.jai.codec.MemoryCacheSeekableStream;
import com.sun.media.jai.opimage.FilterCRIF;
import com.sun.media.jai.opimage.JaiI18N;
import com.sun.media.jai.util.ImageUtil;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.Rectangle2D;
import java.awt.image.ColorModel;
import java.awt.image.RenderedImage;
import java.awt.image.renderable.ParameterBlock;
import java.awt.image.renderable.RenderContext;
import java.awt.image.renderable.RenderableImage;
import java.awt.image.renderable.RenderableImageOp;
import java.io.InputStream;
import java.net.URL;
import java.util.Vector;
import javax.media.jai.CRIFImpl;
import javax.media.jai.ImageLayout;
import javax.media.jai.Interpolation;
import javax.media.jai.JAI;
import javax.media.jai.LookupTableJAI;
import javax.media.jai.MultiResolutionRenderableImage;
import javax.media.jai.PlanarImage;
import javax.media.jai.ROI;
import javax.media.jai.ROIShape;
import javax.media.jai.RenderableOp;
import javax.media.jai.RenderedOp;
import javax.media.jai.TiledImage;
import javax.media.jai.operator.TransposeDescriptor;
import javax.media.jai.operator.TransposeType;
import javax.media.jai.util.ImagingException;
import javax.media.jai.util.ImagingListener;

public class IIPCRIF
extends CRIFImpl {
    private static final int MASK_FILTER = 1;
    private static final int MASK_COLOR_TWIST = 2;
    private static final int MASK_CONTRAST = 4;
    private static final int MASK_ROI_SOURCE = 8;
    private static final int MASK_TRANSFORM = 16;
    private static final int MASK_ASPECT_RATIO = 32;
    private static final int MASK_ROI_DESTINATION = 64;
    private static final int MASK_ROTATION = 128;
    private static final int MASK_MIRROR_AXIS = 256;
    private static final int MASK_ICC_PROFILE = 512;
    private static final int MASK_JPEG_QUALITY = 1024;
    private static final int MASK_JPEG_TABLE = 2048;
    private static final int VENDOR_HP = 0;
    private static final int VENDOR_LIVE_PICTURE = 1;
    private static final int VENDOR_KODAK = 2;
    private static final int VENDOR_UNREGISTERED = 255;
    private static final int VENDOR_EXPERIMENTAL = 999;
    private static final int SERVER_CVT_JPEG = 1;
    private static final int SERVER_CVT_FPX = 2;
    private static final int SERVER_CVT_MJPEG = 4;
    private static final int SERVER_CVT_MFPX = 8;
    private static final int SERVER_CVT_M2JPEG = 16;
    private static final int SERVER_CVT_M2FPX = 32;
    private static final int SERVER_CVT_JTL = 64;
    private static final int SERVER_JPEG_PARTIAL = 5;
    private static final int SERVER_JPEG_FULL = 21;
    private static final int SERVER_FPX_PARTIAL = 10;
    private static final int SERVER_FPX_FULL = 42;
    private static final double[][] YCCA_TO_RGBA = new double[][]{{1.3584, 0.0, 1.8215, 0.0}, {1.3584, -0.4303, -0.9271, 0.0}, {1.3584, 2.2179, 0.0, 0.0}, {0.0, 0.0, 0.0, 1.0}};
    private static final double[][] YCCA_TO_RGBA_CONST = new double[][]{{-249.55}, {194.14}, {-345.99}, {0.0}};
    private static final double[][] RGBA_TO_YCCA = new double[][]{{0.220018, 0.432276, 0.083867, 0.0}, {-0.134755, -0.264756, 0.399511, 0.0}, {0.384918, -0.322373, -0.062544, 0.0}, {0.0, 0.0, 0.0, 1.0}};
    private static final double[][] RGBA_TO_YCCA_CONST = new double[][]{{5.726E-4}, {155.9984}, {137.0022}, {0.0}};
    private static final double[][] YCC_TO_RGB = new double[][]{{1.3584, 0.0, 1.8215}, {1.3584, -0.4303, -0.9271}, {1.3584, 2.2179, 0.0}};
    private static final double[][] YCC_TO_RGB_CONST = new double[][]{{-249.55}, {194.14}, {-345.99}};
    private static final double[][] RGB_TO_YCC = new double[][]{{0.220018, 0.432276, 0.083867}, {-0.134755, -0.264756, 0.399511}, {0.384918, -0.322373, -0.062544}};
    private static final double[][] RGB_TO_YCC_CONST = new double[][]{{5.726E-4}, {155.9984}, {137.0022}};

    private static final int getOperationMask(ParameterBlock pb) {
        AffineTransform tf;
        int opMask = 0;
        if (pb.getFloatParameter(2) != 0.0f) {
            opMask |= 1;
        }
        if (pb.getObjectParameter(3) != null) {
            opMask |= 2;
        }
        if (Math.abs(pb.getFloatParameter(4) - 1.0f) > 0.01f) {
            opMask |= 4;
        }
        if (pb.getObjectParameter(5) != null) {
            opMask |= 8;
        }
        if (!(tf = (AffineTransform)pb.getObjectParameter(6)).isIdentity()) {
            opMask |= 0x10;
        }
        if (pb.getObjectParameter(7) != null) {
            opMask |= 0x20;
        }
        if (pb.getObjectParameter(8) != null) {
            opMask |= 0x40;
        }
        if (pb.getIntParameter(9) != 0) {
            opMask |= 0x80;
        }
        if (pb.getObjectParameter(10) != null) {
            opMask |= 0x100;
        }
        if (pb.getObjectParameter(11) != null) {
            opMask |= 0x200;
        }
        if (pb.getObjectParameter(12) != null) {
            opMask |= 0x400;
        }
        if (pb.getObjectParameter(13) != null) {
            opMask |= 0x800;
        }
        return opMask;
    }

    private static final int getServerCapabilityMask(String URLSpec, RenderedImage lowRes) {
        int vendorID = 255;
        int serverMask = 0;
        if (lowRes.getProperty("iip-server") != null && lowRes.getProperty("iip-server") != Image.UndefinedProperty) {
            String serverString = (String)lowRes.getProperty("iip-server");
            int dot = serverString.indexOf(".");
            vendorID = Integer.valueOf(serverString.substring(0, dot));
            serverMask = Integer.valueOf(serverString.substring(dot + 1));
        }
        if (serverMask != 127 && vendorID != 0 && vendorID != 1 && vendorID != 2) {
            int[] maxSize = (int[])lowRes.getProperty("max-size");
            String rgn = "&RGN=0.0,0.0," + 64.0f / (float)maxSize[0] + "," + 64.0f / (float)maxSize[1];
            if (IIPCRIF.canDecode(URLSpec, "&CNT=0.9&WID=64&CVT=JPEG", "JPEG")) {
                serverMask = 21;
            } else if (IIPCRIF.canDecode(URLSpec, "&CNT=0.9&WID=64&CVT=FPX", "FPX")) {
                serverMask = 42;
            } else if (IIPCRIF.canDecode(URLSpec, rgn + "&CVT=JPEG", "JPEG")) {
                serverMask = 5;
            } else if (IIPCRIF.canDecode(URLSpec, rgn + "&CVT=FPX", "FPX")) {
                serverMask = 10;
            }
        }
        return serverMask;
    }

    private static boolean canDecode(String base, String suffix, String fmt) {
        StringBuffer buf = new StringBuffer(base);
        URL url = null;
        InputStream stream = null;
        RenderedImage rendering = null;
        boolean itWorks = false;
        try {
            buf.append(suffix);
            url = new URL(buf.toString());
            stream = url.openStream();
            ImageDecoder decoder = ImageCodec.createImageDecoder((String)fmt, (InputStream)stream, null);
            rendering = decoder.decodeAsRenderedImage();
            itWorks = true;
        }
        catch (Exception e) {
            itWorks = false;
        }
        return itWorks;
    }

    private static final double[][] matrixMultiply(double[][] A, double[][] B) {
        if (A[0].length != B.length) {
            throw new RuntimeException(JaiI18N.getString("IIPCRIF0"));
        }
        int nRows = A.length;
        int nCols = B[0].length;
        double[][] C = new double[nRows][nCols];
        int nSum = A[0].length;
        for (int r = 0; r < nRows; ++r) {
            for (int c = 0; c < nCols; ++c) {
                C[r][c] = 0.0;
                for (int k = 0; k < nSum; ++k) {
                    double[] dArray = C[r];
                    int n = c;
                    dArray[n] = dArray[n] + A[r][k] * B[k][c];
                }
            }
        }
        return C;
    }

    private static final double[][] composeMatrices(double[][] A, double[][] b) {
        int nRows = A.length;
        if (nRows != b.length) {
            throw new RuntimeException(JaiI18N.getString("IIPCRIF1"));
        }
        if (b[0].length != 1) {
            throw new RuntimeException(JaiI18N.getString("IIPCRIF2"));
        }
        int nCols = A[0].length;
        double[][] bcMatrix = new double[nRows][nCols + 1];
        for (int r = 0; r < nRows; ++r) {
            for (int c = 0; c < nCols; ++c) {
                bcMatrix[r][c] = A[r][c];
            }
            bcMatrix[r][nCols] = b[r][0];
        }
        return bcMatrix;
    }

    private static final double[][] getColorTwistMatrix(ColorModel colorModel, ParameterBlock pb) {
        float[] ctwParam = (float[])pb.getObjectParameter(3);
        double[][] ctw = new double[4][4];
        int k = 0;
        for (int r = 0; r < 4; ++r) {
            for (int c = 0; c < 4; ++c) {
                ctw[r][c] = ctwParam[k++];
            }
        }
        double[][] H = null;
        Object d = null;
        int csType = colorModel.getColorSpace().getType();
        if (csType == 6 || csType == 5) {
            H = IIPCRIF.matrixMultiply(IIPCRIF.matrixMultiply(YCCA_TO_RGBA, ctw), RGBA_TO_YCCA);
            d = YCCA_TO_RGBA_CONST;
        } else {
            H = ctw;
            d = new double[][]{{0.0}, {0.0}, {0.0}, {0.0}};
        }
        Object A = null;
        Object b = null;
        if (csType == 6) {
            if (colorModel.hasAlpha()) {
                A = new double[][]{{1.0, 0.0}, {1.0, 0.0}, {1.0, 0.0}, {0.0, 1.0}};
                b = new double[][]{{0.0}, {0.0}, {0.0}, {0.0}};
            } else {
                A = new double[][]{{1.0}, {1.0}, {1.0}, {0.0}};
                b = new double[][]{{0.0}, {0.0}, {0.0}, {255.0}};
            }
        } else if (!colorModel.hasAlpha()) {
            A = new double[][]{{1.0, 0.0, 0.0}, {0.0, 1.0, 0.0}, {0.0, 0.0, 1.0}, {0.0, 0.0, 0.0}};
            b = new double[][]{{0.0}, {0.0}, {0.0}, {255.0}};
        } else {
            A = new double[][]{{1.0, 0.0, 0.0, 0.0}, {0.0, 1.0, 0.0, 0.0}, {0.0, 0.0, 1.0, 0.0}, {0.0, 0.0, 0.0, 1.0}};
            b = new double[][]{{0.0}, {0.0}, {0.0}, {0.0}};
        }
        boolean truncateChroma = false;
        if (csType == 6 && ctwParam[4] == 0.0f && ctwParam[7] == 0.0f && ctwParam[8] == 0.0f && ctwParam[11] == 0.0f) {
            truncateChroma = true;
        }
        boolean truncateAlpha = false;
        if (!colorModel.hasAlpha() && ctwParam[15] == 1.0f) {
            truncateAlpha = true;
        }
        Object T = null;
        T = truncateAlpha && truncateChroma ? (Object)new double[][]{{1.0, 0.0, 0.0, 0.0}} : (truncateChroma ? (Object)new double[][]{{1.0, 0.0, 0.0, 0.0}, {0.0, 0.0, 0.0, 1.0}} : (truncateAlpha ? (Object)new double[][]{{1.0, 0.0, 0.0, 0.0}, {0.0, 1.0, 0.0, 0.0}, {0.0, 0.0, 1.0, 0.0}} : (Object)new double[][]{{1.0, 0.0, 0.0, 0.0}, {0.0, 1.0, 0.0, 0.0}, {0.0, 0.0, 1.0, 0.0}, {0.0, 0.0, 0.0, 1.0}}));
        double[][] TH = IIPCRIF.matrixMultiply((double[][])T, H);
        double[][] THA = IIPCRIF.matrixMultiply(TH, A);
        double[][] THb = IIPCRIF.matrixMultiply(TH, b);
        double[][] THd = IIPCRIF.matrixMultiply(TH, d);
        double[][] Td = IIPCRIF.matrixMultiply((double[][])T, d);
        for (int r = 0; r < THb.length; ++r) {
            for (int c = 0; c < THb[r].length; ++c) {
                double[] dArray = THb[r];
                int n = c;
                dArray[n] = dArray[n] + (Td[r][c] - THd[r][c]);
            }
        }
        return IIPCRIF.composeMatrices(THA, THb);
    }

    private static final LookupTableJAI createContrastLUT(float K, int numBands) {
        byte[] contrastTable = new byte[256];
        double p = 0.43f;
        for (int i = 0; i < 256; ++i) {
            float j = ((float)i - 127.5f) / 255.0f;
            float f = 0.0f;
            if (j < 0.0f) {
                f = (float)(-p * Math.pow((double)(-j) / p, K));
            } else if (j > 0.0f) {
                f = (float)(p * Math.pow((double)j / p, K));
            }
            int val = (int)(f * 255.0f + 127.5f);
            contrastTable[i] = val < 0 ? 0 : (val > 255 ? -1 : (byte)(val & 0xFF));
        }
        byte[][] data = new byte[numBands][];
        if (numBands % 2 == 1) {
            for (int i = 0; i < numBands; ++i) {
                data[i] = contrastTable;
            }
        } else {
            for (int i = 0; i < numBands - 1; ++i) {
                data[i] = contrastTable;
            }
            data[numBands - 1] = new byte[256];
            byte[] b = data[numBands - 1];
            for (int i = 0; i < 256; ++i) {
                b[i] = (byte)i;
            }
        }
        return new LookupTableJAI(data);
    }

    public IIPCRIF() {
        super("IIP");
    }

    private RenderedImage serverProc(int serverMask, RenderContext renderContext, ParameterBlock paramBlock, int opMask, RenderedImage lowRes) {
        if ((serverMask & 0x15) != 21 && (serverMask & 0x2A) != 42 && (serverMask & 5) != 5 && (serverMask & 0xA) != 10) {
            return null;
        }
        ImagingListener listener = ImageUtil.getImagingListener(renderContext);
        boolean isJPEG = false;
        boolean isFull = false;
        if ((serverMask & 0x15) == 21) {
            isFull = true;
            isJPEG = true;
        } else if ((serverMask & 0x2A) == 42) {
            isJPEG = false;
            isFull = true;
        } else if ((serverMask & 5) == 5) {
            isJPEG = true;
            isFull = false;
        }
        StringBuffer buf = new StringBuffer((String)paramBlock.getObjectParameter(0));
        if ((opMask & 1) != 0) {
            buf.append("&FTR=" + paramBlock.getFloatParameter(2));
        }
        if ((opMask & 2) != 0) {
            buf.append("&CTW=");
            float[] ctw = (float[])paramBlock.getObjectParameter(3);
            for (int i = 0; i < ctw.length; ++i) {
                buf.append(ctw[i]);
                if (i == ctw.length - 1) continue;
                buf.append(",");
            }
        }
        if ((opMask & 4) != 0) {
            buf.append("&CNT=" + paramBlock.getFloatParameter(4));
        }
        if ((opMask & 8) != 0) {
            Rectangle2D roi = (Rectangle2D)paramBlock.getObjectParameter(5);
            buf.append("&ROI=" + roi.getX() + "," + roi.getY() + "," + roi.getWidth() + "," + roi.getHeight());
        }
        AffineTransform postTransform = new AffineTransform();
        AffineTransform at = (AffineTransform)renderContext.getTransform().clone();
        if (at.getTranslateX() != 0.0 || at.getTranslateY() != 0.0) {
            postTransform.setToTranslation(at.getTranslateX(), at.getTranslateY());
            double[] m = new double[6];
            at.getMatrix(m);
            at.setTransform(m[0], m[1], m[2], m[3], 0.0, 0.0);
        }
        Rectangle2D rgn = null;
        if ((opMask & 0x40) != 0) {
            rgn = (Rectangle2D)paramBlock.getObjectParameter(8);
        } else {
            float aspectRatio = 1.0f;
            aspectRatio = (opMask & 0x20) != 0 ? paramBlock.getFloatParameter(7) : ((Float)lowRes.getProperty("aspect-ratio")).floatValue();
            rgn = new Rectangle2D.Float(0.0f, 0.0f, aspectRatio, 1.0f);
        }
        Rectangle dstROI = at.createTransformedShape(rgn).getBounds();
        AffineTransform scale = AffineTransform.getScaleInstance(dstROI.getWidth() / rgn.getWidth(), dstROI.getHeight() / rgn.getHeight());
        try {
            at.preConcatenate(scale.createInverse());
        }
        catch (Exception e) {
            String message = JaiI18N.getString("IIPCRIF6");
            listener.errorOccurred(message, new ImagingException(message, e), this, false);
        }
        AffineTransform afn = (AffineTransform)paramBlock.getObjectParameter(6);
        try {
            afn.preConcatenate(at.createInverse());
        }
        catch (Exception e) {
            String message = JaiI18N.getString("IIPCRIF6");
            listener.errorOccurred(message, new ImagingException(message, e), this, false);
        }
        if (isFull) {
            buf.append("&WID=" + dstROI.width + "&HEI=" + dstROI.height);
        }
        double[] matrix = new double[6];
        afn.getMatrix(matrix);
        buf.append("&AFN=" + matrix[0] + "," + matrix[2] + ",0," + matrix[4] + "," + matrix[1] + "," + matrix[3] + ",0," + matrix[5] + ",0,0,1,0,0,0,0,1");
        if ((opMask & 0x20) != 0) {
            buf.append("&RAR=" + paramBlock.getFloatParameter(7));
        }
        if ((opMask & 0x40) != 0) {
            Rectangle2D dstRGN = (Rectangle2D)paramBlock.getObjectParameter(8);
            buf.append("&RGN=" + dstRGN.getX() + "," + dstRGN.getY() + "," + dstRGN.getWidth() + "," + dstRGN.getHeight());
        }
        if (isFull && ((opMask & 0x80) != 0 || (opMask & 0x100) != 0)) {
            buf.append("&RFM=" + paramBlock.getIntParameter(9));
            if ((opMask & 0x100) != 0) {
                String axis = (String)paramBlock.getObjectParameter(10);
                if (axis.equalsIgnoreCase("x")) {
                    buf.append(",0");
                } else {
                    buf.append(",90");
                }
            }
        }
        if ((opMask & 0x200) != 0) {
            // empty if block
        }
        if (isJPEG) {
            if ((opMask & 0x400) != 0) {
                buf.append("&QLT=" + paramBlock.getIntParameter(12));
            }
            if ((opMask & 0x800) != 0) {
                buf.append("&CIN=" + paramBlock.getIntParameter(13));
            }
        }
        String format = isJPEG ? "JPEG" : "FPX";
        buf.append("&CVT=" + format);
        InputStream stream = null;
        RenderedOp rendering = null;
        try {
            URL url = new URL(buf.toString());
            stream = url.openStream();
            MemoryCacheSeekableStream sStream = new MemoryCacheSeekableStream(stream);
            rendering = JAI.create(format, sStream);
        }
        catch (Exception e) {
            String message = JaiI18N.getString("IIPCRIF7") + " " + buf.toString();
            listener.errorOccurred(message, new ImagingException(message, e), this, false);
        }
        if (!isFull) {
            postTransform.scale(dstROI.getWidth() / (double)rendering.getWidth(), dstROI.getHeight() / (double)rendering.getHeight());
        }
        if (!postTransform.isIdentity()) {
            Interpolation interp = Interpolation.getInstance(0);
            RenderingHints hints = renderContext.getRenderingHints();
            if (hints != null && hints.containsKey(JAI.KEY_INTERPOLATION)) {
                interp = (Interpolation)hints.get(JAI.KEY_INTERPOLATION);
            }
            rendering = JAI.create("affine", (RenderedImage)rendering, (Object)postTransform, (Object)interp);
        }
        return rendering;
    }

    private RenderedImage clientProc(RenderContext renderContext, ParameterBlock paramBlock, int opMask, RenderedImage lowRes) {
        Rectangle2D rgn;
        int subImage;
        int res;
        int width;
        int height;
        AffineTransform at = renderContext.getTransform();
        RenderingHints hints = renderContext.getRenderingHints();
        ImagingListener listener = ImageUtil.getImagingListener(renderContext);
        int[] maxSize = (int[])lowRes.getProperty("max-size");
        int maxWidth = maxSize[0];
        int maxHeight = maxSize[1];
        int numLevels = (Integer)lowRes.getProperty("resolution-number");
        float aspectRatioSource = (float)maxWidth / (float)maxHeight;
        float aspectRatio = (opMask & 0x20) != 0 ? paramBlock.getFloatParameter(7) : aspectRatioSource;
        Rectangle2D.Float bounds2D = new Rectangle2D.Float(0.0f, 0.0f, aspectRatio, 1.0f);
        if (at.isIdentity()) {
            AffineTransform afn = (AffineTransform)paramBlock.getObjectParameter(6);
            Rectangle2D bounds = afn.createTransformedShape(bounds2D).getBounds2D();
            double H = (double)maxHeight * bounds.getHeight();
            double W = (double)maxHeight * bounds.getWidth();
            double m = Math.max(H, W / (double)aspectRatioSource);
            height = (int)(m + 0.5);
            width = (int)((double)aspectRatioSource * m + 0.5);
            at = AffineTransform.getScaleInstance(width, height);
            renderContext = (RenderContext)renderContext.clone();
            renderContext.setTransform(at);
        } else {
            Rectangle bounds = at.createTransformedShape(bounds2D).getBounds();
            width = bounds.width;
            height = bounds.height;
        }
        int hRes = maxHeight;
        for (res = numLevels - 1; res > 0 && (hRes = (int)(((float)hRes + 1.0f) / 2.0f)) >= height; --res) {
        }
        int[] subImageArray = (int[])paramBlock.getObjectParameter(1);
        int n = subImage = subImageArray.length < res + 1 ? 0 : subImageArray[res];
        if (subImage < 0) {
            subImage = 0;
        }
        ParameterBlock pb = new ParameterBlock();
        pb.add(paramBlock.getObjectParameter(0)).add(res).add(subImage);
        RenderedOp iipRes = JAI.create("iipresolution", pb);
        Vector<RenderedOp> sources = new Vector<RenderedOp>(1);
        sources.add(iipRes);
        RenderableImage ri = new MultiResolutionRenderableImage(sources, 0.0f, 0.0f, 1.0f);
        if ((opMask & 1) != 0) {
            float filter = paramBlock.getFloatParameter(2);
            pb = new ParameterBlock().addSource(ri).add(filter);
            ri = new RenderableImageOp(new FilterCRIF(), pb);
        }
        int nBands = iipRes.getSampleModel().getNumBands();
        if ((opMask & 2) != 0) {
            double[][] ctw = IIPCRIF.getColorTwistMatrix(iipRes.getColorModel(), paramBlock);
            pb = new ParameterBlock().addSource(ri).add(ctw);
            ri = JAI.createRenderable("bandcombine", pb);
            nBands = ctw.length;
        }
        if ((opMask & 4) != 0) {
            boolean isPYCC;
            int csType = iipRes.getColorModel().getColorSpace().getType();
            boolean bl = isPYCC = csType != 6 && csType != 5;
            if (isPYCC) {
                double[][] matrix = nBands == 3 ? IIPCRIF.composeMatrices(YCC_TO_RGB, YCC_TO_RGB_CONST) : IIPCRIF.composeMatrices(YCCA_TO_RGBA, YCCA_TO_RGBA_CONST);
                pb = new ParameterBlock().addSource(ri).add(matrix);
                ri = JAI.createRenderable("bandcombine", pb);
            }
            float contrast = paramBlock.getFloatParameter(4);
            LookupTableJAI lut = IIPCRIF.createContrastLUT(contrast, nBands);
            pb = new ParameterBlock().addSource(ri).add(lut);
            ri = JAI.createRenderable("lookup", pb);
            if (isPYCC) {
                double[][] matrix = nBands == 3 ? IIPCRIF.composeMatrices(RGB_TO_YCC, RGB_TO_YCC_CONST) : IIPCRIF.composeMatrices(RGBA_TO_YCCA, RGBA_TO_YCCA_CONST);
                pb = new ParameterBlock().addSource(ri).add(matrix);
                ri = JAI.createRenderable("bandcombine", pb);
            }
        }
        if ((opMask & 8) != 0) {
            Rectangle2D rect = (Rectangle2D)paramBlock.getObjectParameter(5);
            if (!rect.intersects(0.0, 0.0, aspectRatioSource, 1.0)) {
                throw new RuntimeException(JaiI18N.getString("IIPCRIF5"));
            }
            Rectangle2D.Float rectS = new Rectangle2D.Float(0.0f, 0.0f, aspectRatioSource, 1.0f);
            if (!rect.equals(rectS)) {
                rect = rect.createIntersection(rectS);
                pb = new ParameterBlock().addSource(ri);
                pb.add((float)rect.getMinX()).add((float)rect.getMinY());
                pb.add((float)rect.getWidth()).add((float)rect.getHeight());
                ri = JAI.createRenderable("crop", pb);
            }
        }
        if ((opMask & 0x10) != 0) {
            AffineTransform afn = (AffineTransform)paramBlock.getObjectParameter(6);
            try {
                afn = afn.createInverse();
            }
            catch (NoninvertibleTransformException e) {
                listener.errorOccurred(JaiI18N.getString("AffineNotInvertible"), e, this, false);
            }
            pb = new ParameterBlock().addSource(ri).add(afn);
            if (hints != null && hints.containsKey(JAI.KEY_INTERPOLATION)) {
                pb.add(hints.get(JAI.KEY_INTERPOLATION));
            }
            ri = JAI.createRenderable("affine", pb);
        }
        Rectangle2D rectangle2D = rgn = (opMask & 0x40) != 0 ? (Rectangle2D)paramBlock.getObjectParameter(8) : bounds2D;
        if (rgn.isEmpty()) {
            throw new RuntimeException(JaiI18N.getString("IIPCRIF3"));
        }
        Rectangle2D.Float riRect = new Rectangle2D.Float(ri.getMinX(), ri.getMinY(), ri.getWidth(), ri.getHeight());
        if (!rgn.equals(riRect)) {
            rgn = rgn.createIntersection(riRect);
            pb = new ParameterBlock().addSource(ri);
            pb.add((float)rgn.getMinX()).add((float)rgn.getMinY());
            pb.add((float)rgn.getWidth()).add((float)rgn.getHeight());
            ri = JAI.createRenderable("crop", pb);
        }
        return ri.createRendering(renderContext);
    }

    public RenderedImage create(ParameterBlock paramBlock, RenderingHints renderHints) {
        RenderableOp iipImage = JAI.createRenderable("iip", paramBlock);
        return iipImage.createDefaultRendering();
    }

    public RenderedImage create(RenderContext renderContext, ParameterBlock paramBlock) {
        int opMask = IIPCRIF.getOperationMask(paramBlock);
        ImagingListener listener = ImageUtil.getImagingListener(renderContext);
        ParameterBlock pb = new ParameterBlock();
        int[] subImageArray = (int[])paramBlock.getObjectParameter(1);
        pb.add(paramBlock.getObjectParameter(0)).add(0).add(subImageArray[0]);
        RenderedOp lowRes = JAI.create("iipresolution", pb);
        int serverMask = IIPCRIF.getServerCapabilityMask((String)paramBlock.getObjectParameter(0), lowRes);
        RenderedImage rendering = null;
        if ((serverMask & 0x15) == 21 || (serverMask & 0x2A) == 42 || (serverMask & 5) == 5 || (serverMask & 0xA) == 10) {
            rendering = this.serverProc(serverMask, renderContext, paramBlock, opMask, lowRes);
        } else {
            rendering = this.clientProc(renderContext, paramBlock, opMask, lowRes);
            if ((opMask & 8) != 0) {
                Rectangle2D rgn = (Rectangle2D)paramBlock.getObjectParameter(5);
                AffineTransform at = (AffineTransform)((AffineTransform)paramBlock.getObjectParameter(6)).clone();
                if (!at.isIdentity()) {
                    try {
                        at = at.createInverse();
                    }
                    catch (Exception e) {
                        String message = JaiI18N.getString("IIPCRIF6");
                        listener.errorOccurred(message, new ImagingException(message, e), this, false);
                    }
                }
                at.preConcatenate(renderContext.getTransform());
                ROIShape roi = new ROIShape(at.createTransformedShape(rgn));
                TiledImage ti = new TiledImage(rendering.getMinX(), rendering.getMinY(), rendering.getWidth(), rendering.getHeight(), rendering.getTileGridXOffset(), rendering.getTileGridYOffset(), rendering.getSampleModel(), rendering.getColorModel());
                ti.set(rendering, roi);
                pb = new ParameterBlock();
                pb.add((float)ti.getWidth());
                pb.add((float)ti.getHeight());
                Byte[] bandValues = new Byte[ti.getSampleModel().getNumBands()];
                for (int b = 0; b < bandValues.length; ++b) {
                    bandValues[b] = new Byte(-1);
                }
                pb.add(bandValues);
                ImageLayout il = new ImageLayout();
                il.setSampleModel(ti.getSampleModel());
                RenderingHints rh = new RenderingHints(JAI.KEY_IMAGE_LAYOUT, il);
                RenderedOp constImage = JAI.create("constant", pb, rh);
                ROI complementROI = new ROIShape(ti.getBounds()).subtract(roi);
                int maxTileY = ti.getMaxTileY();
                int maxTileX = ti.getMaxTileX();
                for (int j = ti.getMinTileY(); j <= maxTileY; ++j) {
                    for (int i = ti.getMinTileX(); i <= maxTileX; ++i) {
                        if (roi.intersects(ti.getTileRect(i, j))) continue;
                        ti.setData(((PlanarImage)constImage).getTile(i, j), complementROI);
                    }
                }
                rendering = ti;
            }
        }
        if ((serverMask & 0x15) != 21 && (serverMask & 0x2A) != 42) {
            if ((opMask & 0x80) != 0) {
                TransposeType transposeType = null;
                switch (paramBlock.getIntParameter(9)) {
                    case 90: {
                        transposeType = TransposeDescriptor.ROTATE_270;
                        break;
                    }
                    case 180: {
                        transposeType = TransposeDescriptor.ROTATE_180;
                        break;
                    }
                    case 270: {
                        transposeType = TransposeDescriptor.ROTATE_90;
                    }
                }
                if (transposeType != null) {
                    rendering = JAI.create("transpose", rendering, (Object)transposeType);
                }
            }
            if ((opMask & 0x100) != 0) {
                String axis = (String)paramBlock.getObjectParameter(10);
                TransposeType transposeType = axis.equalsIgnoreCase("x") ? TransposeDescriptor.FLIP_VERTICAL : TransposeDescriptor.FLIP_HORIZONTAL;
                rendering = JAI.create("transpose", rendering, (Object)transposeType);
            }
        }
        return rendering;
    }

    public Rectangle2D getBounds2D(ParameterBlock paramBlock) {
        float aspectRatioDestination;
        int opMask = IIPCRIF.getOperationMask(paramBlock);
        if ((opMask & 0x40) != 0) {
            return (Rectangle2D)paramBlock.getObjectParameter(8);
        }
        if ((opMask & 0x20) != 0) {
            aspectRatioDestination = paramBlock.getFloatParameter(7);
        } else {
            ParameterBlock pb = new ParameterBlock();
            int[] subImageArray = (int[])paramBlock.getObjectParameter(1);
            pb.add(paramBlock.getObjectParameter(0));
            pb.add(0).add(subImageArray[0]);
            RenderedOp lowRes = JAI.create("iipresolution", pb);
            int[] maxSize = (int[])lowRes.getProperty("max-size");
            aspectRatioDestination = (float)maxSize[0] / (float)maxSize[1];
        }
        return new Rectangle2D.Float(0.0f, 0.0f, aspectRatioDestination, 1.0f);
    }

    public static void main(String[] args) {
        int c;
        int r;
        int nr = 0;
        int nc = 0;
        double[][] x = IIPCRIF.matrixMultiply(RGBA_TO_YCCA, YCCA_TO_RGBA);
        nr = x.length;
        nc = x[0].length;
        for (r = 0; r < nr; ++r) {
            for (c = 0; c < nc; ++c) {
                System.out.print(x[r][c] + " ");
            }
            System.out.println("");
        }
        System.out.println("");
        x = IIPCRIF.matrixMultiply(RGB_TO_YCC, YCC_TO_RGB);
        nr = x.length;
        nc = x[0].length;
        for (r = 0; r < nr; ++r) {
            for (c = 0; c < nc; ++c) {
                System.out.print(x[r][c] + " ");
            }
            System.out.println("");
        }
        System.out.println("");
        double[][] b = new double[][]{{1.0}, {2.0}, {3.0}, {4.0}};
        double[][] A = IIPCRIF.composeMatrices(YCCA_TO_RGBA, b);
        nr = A.length;
        nc = A[0].length;
        for (int r2 = 0; r2 < nr; ++r2) {
            for (int c2 = 0; c2 < nc; ++c2) {
                System.out.print(A[r2][c2] + " ");
            }
            System.out.println("");
        }
        System.out.println("");
        double[][] d4 = IIPCRIF.matrixMultiply(RGBA_TO_YCCA, YCCA_TO_RGBA_CONST);
        nr = d4.length;
        nc = d4[0].length;
        for (int r3 = 0; r3 < nr; ++r3) {
            for (int c3 = 0; c3 < nc; ++c3) {
                System.out.print(-d4[r3][c3] + " ");
            }
            System.out.println("");
        }
        System.out.println("");
        double[][] d3 = IIPCRIF.matrixMultiply(RGB_TO_YCC, YCC_TO_RGB_CONST);
        nr = d3.length;
        nc = d3[0].length;
        for (int r4 = 0; r4 < nr; ++r4) {
            for (int c4 = 0; c4 < nc; ++c4) {
                System.out.print(-d3[r4][c4] + " ");
            }
            System.out.println("");
        }
        System.out.println("");
    }
}


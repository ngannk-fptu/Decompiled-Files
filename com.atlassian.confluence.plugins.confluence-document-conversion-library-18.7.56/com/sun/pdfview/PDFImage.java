/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.sun.pdfview;

import com.sun.pdfview.PDFObject;
import com.sun.pdfview.PDFParseException;
import com.sun.pdfview.colorspace.AlternateColorSpace;
import com.sun.pdfview.colorspace.IndexedColor;
import com.sun.pdfview.colorspace.PDFColorSpace;
import com.sun.pdfview.function.FunctionType0;
import java.awt.Color;
import java.awt.Point;
import java.awt.color.ColorSpace;
import java.awt.color.ICC_ColorSpace;
import java.awt.image.BufferedImage;
import java.awt.image.ColorConvertOp;
import java.awt.image.ColorModel;
import java.awt.image.ComponentColorModel;
import java.awt.image.DataBufferByte;
import java.awt.image.IndexColorModel;
import java.awt.image.MultiPixelPackedSampleModel;
import java.awt.image.Raster;
import java.awt.image.RasterFormatException;
import java.awt.image.SampleModel;
import java.awt.image.WritableRaster;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PDFImage {
    private static final Logger log = LoggerFactory.getLogger(PDFImage.class);
    private int[] colorKeyMask = null;
    private int width;
    private int height;
    private PDFColorSpace colorSpace;
    private int bpc;
    private boolean imageMask = false;
    private PDFImage sMask;
    private float[] decode;
    private PDFObject imageObj;

    public static void dump(PDFObject obj) throws IOException {
        PDFImage.p("dumping PDF object: " + obj);
        if (obj == null) {
            return;
        }
        HashMap<String, PDFObject> dict = obj.getDictionary();
        PDFImage.p("   dict = " + dict);
        for (String key : dict.keySet()) {
            PDFImage.p("key = " + key + " value = " + dict.get(key));
        }
    }

    public static void p(String string) {
        System.out.println(string);
    }

    protected PDFImage(PDFObject imageObj) {
        this.imageObj = imageObj;
    }

    public static PDFImage createImage(PDFObject obj, Map resources, boolean useAsSMask) throws IOException {
        PDFImage image = new PDFImage(obj);
        PDFObject widthObj = obj.getDictRef("Width");
        if (widthObj == null) {
            throw new PDFParseException("Unable to read image width: " + obj);
        }
        image.setWidth(widthObj.getIntValue());
        PDFObject heightObj = obj.getDictRef("Height");
        if (heightObj == null) {
            throw new PDFParseException("Unable to get image height: " + obj);
        }
        image.setHeight(heightObj.getIntValue());
        PDFObject imageMaskObj = obj.getDictRef("ImageMask");
        if (imageMaskObj != null) {
            image.setImageMask(imageMaskObj.getBooleanValue());
        }
        if (image.isImageMask()) {
            PDFObject[] decodeArray;
            float decode0;
            Color[] colorArray;
            image.setBitsPerComponent(1);
            if (useAsSMask) {
                Color[] colorArray2 = new Color[2];
                colorArray2[0] = Color.WHITE;
                colorArray = colorArray2;
                colorArray2[1] = Color.BLACK;
            } else {
                Color[] colorArray3 = new Color[2];
                colorArray3[0] = Color.BLACK;
                colorArray = colorArray3;
                colorArray3[1] = Color.WHITE;
            }
            Color[] colors = colorArray;
            PDFObject imageMaskDecode = obj.getDictRef("Decode");
            if (imageMaskDecode != null && (decode0 = (decodeArray = imageMaskDecode.getArray())[0].getFloatValue()) == 1.0f) {
                Color[] colorArray4;
                if (useAsSMask) {
                    Color[] colorArray5 = new Color[2];
                    colorArray5[0] = Color.BLACK;
                    colorArray4 = colorArray5;
                    colorArray5[1] = Color.WHITE;
                } else {
                    Color[] colorArray6 = new Color[2];
                    colorArray6[0] = Color.WHITE;
                    colorArray4 = colorArray6;
                    colorArray6[1] = Color.BLACK;
                }
                colors = colorArray4;
            }
            image.setColorSpace(new IndexedColor(colors));
        } else {
            PDFObject sMaskObj;
            PDFObject bpcObj = obj.getDictRef("BitsPerComponent");
            if (bpcObj == null) {
                throw new PDFParseException("Unable to get bits per component: " + obj);
            }
            image.setBitsPerComponent(bpcObj.getIntValue());
            PDFObject csObj = obj.getDictRef("ColorSpace");
            if (csObj == null) {
                throw new PDFParseException("No ColorSpace for image: " + obj);
            }
            PDFColorSpace cs = PDFColorSpace.getColorSpace(csObj, resources);
            image.setColorSpace(cs);
            PDFObject decodeObj = obj.getDictRef("Decode");
            if (decodeObj != null) {
                PDFObject[] decodeArray = decodeObj.getArray();
                float[] decode = new float[decodeArray.length];
                for (int i = 0; i < decodeArray.length; ++i) {
                    decode[i] = decodeArray[i].getFloatValue();
                }
                image.setDecode(decode);
            }
            if ((sMaskObj = obj.getDictRef("SMask")) == null) {
                sMaskObj = obj.getDictRef("Mask");
            }
            if (sMaskObj != null) {
                if (sMaskObj.getType() == 7) {
                    try {
                        PDFImage sMaskImage = PDFImage.createImage(sMaskObj, resources, true);
                        image.setSMask(sMaskImage);
                    }
                    catch (IOException ex) {
                        PDFImage.p("ERROR: there was a problem parsing the mask for this object");
                        PDFImage.dump(obj);
                        ex.printStackTrace(System.out);
                    }
                } else if (sMaskObj.getType() == 5) {
                    try {
                        image.setColorKeyMask(sMaskObj);
                    }
                    catch (IOException ex) {
                        PDFImage.p("ERROR: there was a problem parsing the color mask for this object");
                        PDFImage.dump(obj);
                        ex.printStackTrace(System.out);
                    }
                }
            }
        }
        return image;
    }

    public BufferedImage getImage() {
        try {
            BufferedImage bi = (BufferedImage)this.imageObj.getCache();
            if (bi == null) {
                bi = this.parseData(this.imageObj.getStream());
                this.imageObj.setCache(bi);
            }
            return bi;
        }
        catch (IOException ioe) {
            System.out.println("Error reading image");
            ioe.printStackTrace();
            return null;
        }
    }

    protected BufferedImage parseData(byte[] data) {
        PDFImage sMaskImage;
        WritableRaster raster;
        DataBufferByte db = new DataBufferByte(data, data.length);
        ColorModel cm = this.getColorModel();
        SampleModel sm = cm.createCompatibleSampleModel(this.getWidth(), this.getHeight());
        try {
            raster = Raster.createWritableRaster(sm, db, new Point(0, 0));
        }
        catch (RasterFormatException e) {
            int tempExpectedSize = this.getWidth() * this.getHeight() * this.getColorSpace().getNumComponents() * this.getBitsPerComponent() / 8;
            if (tempExpectedSize > data.length) {
                byte[] tempLargerData = new byte[tempExpectedSize];
                System.arraycopy(data, 0, tempLargerData, 0, data.length);
                db = new DataBufferByte(tempLargerData, tempExpectedSize);
                raster = Raster.createWritableRaster(sm, db, new Point(0, 0));
            }
            throw e;
        }
        BufferedImage bi = null;
        if (cm instanceof IndexColorModel) {
            IndexColorModel icm = (IndexColorModel)cm;
            int type = 12;
            if (this.getBitsPerComponent() == 8) {
                type = 13;
            }
            bi = new BufferedImage(this.getWidth(), this.getHeight(), type, icm);
            bi.setData(raster);
        } else {
            bi = new BufferedImage(cm, raster, true, null);
        }
        ColorSpace cs = cm.getColorSpace();
        ColorSpace rgbCS = ColorSpace.getInstance(1000);
        if (!this.isImageMask() && cs instanceof ICC_ColorSpace && !cs.equals(rgbCS)) {
            ColorConvertOp op = new ColorConvertOp(cs, rgbCS, null);
            BufferedImage converted = new BufferedImage(this.getWidth(), this.getHeight(), 2);
            bi = op.filter(bi, converted);
        }
        if ((sMaskImage = this.getSMask()) != null) {
            BufferedImage si = sMaskImage.getImage();
            BufferedImage outImage = new BufferedImage(this.getWidth(), this.getHeight(), 2);
            int[] srcArray = new int[this.width];
            int[] maskArray = new int[this.width];
            for (int i = 0; i < this.height; ++i) {
                bi.getRGB(0, i, this.width, 1, srcArray, 0, this.width);
                si.getRGB(0, i, this.width, 1, maskArray, 0, this.width);
                for (int j = 0; j < this.width; ++j) {
                    int ac = -16777216;
                    maskArray[j] = (maskArray[j] & 0xFF) << 24 | srcArray[j] & ~ac;
                }
                outImage.setRGB(0, i, this.width, 1, maskArray, 0, this.width);
            }
            bi = outImage;
        }
        return bi;
    }

    private BufferedImage biColorToGrayscale(WritableRaster raster, byte[] ncc) {
        byte[] bufferO = ((DataBufferByte)raster.getDataBuffer()).getData();
        BufferedImage converted = new BufferedImage(this.getWidth(), this.getHeight(), 10);
        byte[] buffer = ((DataBufferByte)converted.getRaster().getDataBuffer()).getData();
        int i = 0;
        int height = converted.getHeight();
        int width = converted.getWidth();
        for (int y = 0; y < height; ++y) {
            byte bits;
            int x;
            int base = y * width + 7;
            if (width % 8 == 0 && (y + 1) * width < buffer.length) {
                for (x = 0; x < width; x += 8) {
                    bits = bufferO[i];
                    ++i;
                    buffer[base - 7] = ncc[bits >>> 7 & 1];
                    buffer[base - 6] = ncc[bits >>> 6 & 1];
                    buffer[base - 5] = ncc[bits >>> 5 & 1];
                    buffer[base - 4] = ncc[bits >>> 4 & 1];
                    buffer[base - 3] = ncc[bits >>> 3 & 1];
                    buffer[base - 2] = ncc[bits >>> 2 & 1];
                    buffer[base - 1] = ncc[bits >>> 1 & 1];
                    buffer[base] = ncc[bits & 1];
                    base += 8;
                }
                continue;
            }
            for (x = 0; x < width; x += 8) {
                bits = bufferO[i];
                ++i;
                for (int j = 7; j >= 0 && base - j < buffer.length; j = (int)((byte)(j - 1))) {
                    buffer[base - j] = ncc[bits >>> j & 1];
                }
                base += 8;
            }
        }
        return converted;
    }

    public int getWidth() {
        return this.width;
    }

    protected void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return this.height;
    }

    protected void setHeight(int height) {
        this.height = height;
    }

    private void setColorKeyMask(PDFObject maskArrayObject) throws IOException {
        PDFObject[] maskObjects = maskArrayObject.getArray();
        this.colorKeyMask = null;
        int[] masks = new int[maskObjects.length];
        for (int i = 0; i < masks.length; ++i) {
            masks[i] = maskObjects[i].getIntValue();
        }
        this.colorKeyMask = masks;
    }

    protected PDFColorSpace getColorSpace() {
        return this.colorSpace;
    }

    protected void setColorSpace(PDFColorSpace colorSpace) {
        this.colorSpace = colorSpace;
    }

    protected int getBitsPerComponent() {
        return this.bpc;
    }

    protected void setBitsPerComponent(int bpc) {
        this.bpc = bpc;
    }

    public boolean isImageMask() {
        return this.imageMask;
    }

    public void setImageMask(boolean imageMask) {
        this.imageMask = imageMask;
    }

    public PDFImage getSMask() {
        return this.sMask;
    }

    protected void setSMask(PDFImage sMask) {
        this.sMask = sMask;
    }

    protected float[] getDecode() {
        return this.decode;
    }

    protected void setDecode(float[] decode) {
        this.decode = decode;
    }

    private ColorModel getColorModel() {
        PDFColorSpace cs = this.getColorSpace();
        if (cs instanceof IndexedColor) {
            int i;
            int correctCount;
            IndexedColor ics = (IndexedColor)cs;
            byte[] components = ics.getColorComponents();
            int num = ics.getCount();
            if (this.decode != null) {
                byte[] normComps = new byte[components.length];
                for (int i2 = 0; i2 < num; ++i2) {
                    byte[] orig = new byte[]{(byte)i2};
                    float[] res = this.normalize(orig, null, 0);
                    int idx = (int)res[0];
                    normComps[i2 * 3] = components[idx * 3];
                    normComps[i2 * 3 + 1] = components[idx * 3 + 1];
                    normComps[i2 * 3 + 2] = components[idx * 3 + 2];
                }
                components = normComps;
            }
            if ((correctCount = 1 << this.getBitsPerComponent()) < num) {
                byte[] fewerComps = new byte[correctCount * 3];
                System.arraycopy(components, 0, fewerComps, 0, correctCount * 3);
                components = fewerComps;
                num = correctCount;
            }
            if (this.colorKeyMask == null || this.colorKeyMask.length == 0) {
                return new IndexColorModel(this.getBitsPerComponent(), num, components, 0, false);
            }
            byte[] aComps = new byte[num * 4];
            int idx = 0;
            for (i = 0; i < num; ++i) {
                aComps[idx++] = components[i * 3];
                aComps[idx++] = components[i * 3 + 1];
                aComps[idx++] = components[i * 3 + 2];
                aComps[idx++] = -1;
            }
            for (i = 0; i < this.colorKeyMask.length; i += 2) {
                for (int j = this.colorKeyMask[i]; j <= this.colorKeyMask[i + 1]; ++j) {
                    aComps[j * 4 + 3] = 0;
                }
            }
            return new IndexColorModel(this.getBitsPerComponent(), num, aComps, 0, true);
        }
        if (cs instanceof AlternateColorSpace) {
            ColorSpace altCS = cs.getColorSpace();
            int[] bits = new int[altCS.getNumComponents()];
            for (int i = 0; i < bits.length; ++i) {
                bits[i] = this.getBitsPerComponent();
            }
            return new DecodeComponentColorModel(altCS, bits);
        }
        if (cs.getColorSpace().getType() == 9 && this.isDCTDecoded()) {
            ColorSpace rgbCS = ColorSpace.getInstance(1000);
            int[] bits = new int[rgbCS.getNumComponents()];
            for (int i = 0; i < bits.length; ++i) {
                bits[i] = this.getBitsPerComponent();
            }
            return new DecodeComponentColorModel(rgbCS, bits);
        }
        int[] bits = new int[cs.getNumComponents()];
        for (int i = 0; i < bits.length; ++i) {
            bits[i] = this.getBitsPerComponent();
        }
        return new DecodeComponentColorModel(cs.getColorSpace(), bits);
    }

    private boolean isDCTDecoded() {
        try {
            PDFObject filterObj = this.imageObj.getDictRef("Filter");
            if (filterObj != null) {
                String enctype = filterObj.getStringValue();
                return enctype.equals("DCTDecode") || enctype.equals("DCT");
            }
            return false;
        }
        catch (IOException ioe) {
            log.error("Error reading enctype", (Throwable)ioe);
            return false;
        }
    }

    private float[] normalize(byte[] pixels, float[] normComponents, int normOffset) {
        if (normComponents == null) {
            normComponents = new float[normOffset + pixels.length];
        }
        float[] decodeArray = this.getDecode();
        for (int i = 0; i < pixels.length; ++i) {
            int val = pixels[i] & 0xFF;
            int pow = (int)Math.pow(2.0, this.getBitsPerComponent()) - 1;
            float ymin = decodeArray[i * 2];
            float ymax = decodeArray[i * 2 + 1];
            normComponents[normOffset + i] = FunctionType0.interpolate(val, 0.0f, pow, ymin, ymax);
        }
        return normComponents;
    }

    class DecodeComponentColorModel
    extends ComponentColorModel {
        public DecodeComponentColorModel(ColorSpace cs, int[] bpc) {
            super(cs, bpc, false, false, 1, 0);
            if (bpc != null) {
                this.pixel_bits = bpc.length * bpc[0];
            }
        }

        @Override
        public SampleModel createCompatibleSampleModel(int width, int height) {
            if (this.getNumComponents() == 1 && this.getPixelSize() < 8) {
                return new MultiPixelPackedSampleModel(this.getTransferType(), width, height, this.getPixelSize());
            }
            return super.createCompatibleSampleModel(width, height);
        }

        @Override
        public boolean isCompatibleRaster(Raster raster) {
            if (this.getNumComponents() == 1 && this.getPixelSize() < 8) {
                SampleModel sm = raster.getSampleModel();
                if (sm instanceof MultiPixelPackedSampleModel) {
                    return sm.getSampleSize(0) == this.getPixelSize();
                }
                return false;
            }
            return super.isCompatibleRaster(raster);
        }

        @Override
        public float[] getNormalizedComponents(Object pixel, float[] normComponents, int normOffset) {
            if (PDFImage.this.getDecode() == null) {
                return super.getNormalizedComponents(pixel, normComponents, normOffset);
            }
            return PDFImage.this.normalize((byte[])pixel, normComponents, normOffset);
        }
    }
}


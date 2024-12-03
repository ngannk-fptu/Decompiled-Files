/*
 * Decompiled with CFR 0.152.
 */
package com.lowagie.text;

import com.lowagie.text.Annotation;
import com.lowagie.text.BadElementException;
import com.lowagie.text.DocumentException;
import com.lowagie.text.ExceptionConverter;
import com.lowagie.text.ImageLoader;
import com.lowagie.text.ImgCCITT;
import com.lowagie.text.ImgJBIG2;
import com.lowagie.text.ImgRaw;
import com.lowagie.text.ImgTemplate;
import com.lowagie.text.ImgWMF;
import com.lowagie.text.Rectangle;
import com.lowagie.text.Utilities;
import com.lowagie.text.error_messages.MessageLocalization;
import com.lowagie.text.pdf.PRIndirectReference;
import com.lowagie.text.pdf.PdfArray;
import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfDictionary;
import com.lowagie.text.pdf.PdfIndirectReference;
import com.lowagie.text.pdf.PdfName;
import com.lowagie.text.pdf.PdfNumber;
import com.lowagie.text.pdf.PdfOCG;
import com.lowagie.text.pdf.PdfObject;
import com.lowagie.text.pdf.PdfReader;
import com.lowagie.text.pdf.PdfTemplate;
import com.lowagie.text.pdf.PdfWriter;
import com.lowagie.text.pdf.codec.CCITTG4Encoder;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.color.ICC_Profile;
import java.awt.image.BufferedImage;
import java.awt.image.PixelGrabber;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.net.URL;

public abstract class Image
extends Rectangle {
    public static final int DEFAULT = 0;
    public static final int RIGHT = 2;
    public static final int LEFT = 0;
    public static final int MIDDLE = 1;
    public static final int TEXTWRAP = 4;
    public static final int UNDERLYING = 8;
    public static final int AX = 0;
    public static final int AY = 1;
    public static final int BX = 2;
    public static final int BY = 3;
    public static final int CX = 4;
    public static final int CY = 5;
    public static final int DX = 6;
    public static final int DY = 7;
    public static final int ORIGINAL_NONE = 0;
    public static final int ORIGINAL_JPEG = 1;
    public static final int ORIGINAL_PNG = 2;
    public static final int ORIGINAL_GIF = 3;
    public static final int ORIGINAL_BMP = 4;
    public static final int ORIGINAL_TIFF = 5;
    public static final int ORIGINAL_WMF = 6;
    public static final int ORIGINAL_PS = 7;
    public static final int ORIGINAL_JPEG2000 = 8;
    public static final int ORIGINAL_JBIG2 = 9;
    protected int type;
    protected URL url;
    protected byte[] rawData;
    protected int bpc = 1;
    protected PdfTemplate[] template = new PdfTemplate[1];
    protected int alignment;
    protected String alt;
    protected float absoluteX = Float.NaN;
    protected float absoluteY = Float.NaN;
    protected float plainWidth;
    protected float plainHeight;
    protected float scaledWidth;
    protected float scaledHeight;
    protected int compressionLevel = -1;
    protected Long mySerialId = Image.getSerialId();
    public static final int[] PNGID = new int[]{137, 80, 78, 71, 13, 10, 26, 10};
    private PdfIndirectReference directReference;
    static long serialId = 0L;
    protected float rotationRadians;
    private float initialRotation;
    protected float indentationLeft = 0.0f;
    protected float indentationRight = 0.0f;
    protected float spacingBefore;
    protected float spacingAfter;
    private float widthPercentage = 100.0f;
    protected Annotation annotation = null;
    protected PdfOCG layer;
    protected boolean interpolation;
    protected int originalType = 0;
    protected byte[] originalData;
    protected boolean deflated = false;
    protected int dpiX = 0;
    protected int dpiY = 0;
    private float XYRatio = 0.0f;
    protected int colorspace = -1;
    protected boolean invert = false;
    protected ICC_Profile profile = null;
    private PdfDictionary additional = null;
    protected boolean mask = false;
    protected Image imageMask;
    private boolean smask;
    protected int[] transparency;

    public Image(URL url) {
        super(0.0f, 0.0f);
        this.url = url;
        this.alignment = 0;
        this.rotationRadians = 0.0f;
    }

    public static Image getInstance(URL url) throws BadElementException, IOException {
        InputStream is = null;
        Image img = null;
        try {
            is = url.openStream();
            int c1 = is.read();
            int c2 = is.read();
            int c3 = is.read();
            int c4 = is.read();
            int c5 = is.read();
            int c6 = is.read();
            int c7 = is.read();
            int c8 = is.read();
            is.close();
            is = null;
            if (c1 == 71 && c2 == 73 && c3 == 70) {
                Image image = img = ImageLoader.getGifImage(url);
                return image;
            }
            if (c1 == 255 && c2 == 216) {
                Image image = img = ImageLoader.getJpegImage(url);
                return image;
            }
            if (c1 == 0 && c2 == 0 && c3 == 0 && c4 == 12) {
                Image image = img = ImageLoader.getJpeg2000Image(url);
                return image;
            }
            if (c1 == 255 && c2 == 79 && c3 == 255 && c4 == 81) {
                Image image = img = ImageLoader.getJpeg2000Image(url);
                return image;
            }
            if (c1 == PNGID[0] && c2 == PNGID[1] && c3 == PNGID[2] && c4 == PNGID[3]) {
                Image image = img = ImageLoader.getPngImage(url);
                return image;
            }
            if (c1 == 215 && c2 == 205) {
                Image image = img = new ImgWMF(url);
                return image;
            }
            if (c1 == 66 && c2 == 77) {
                Image image = img = ImageLoader.getBmpImage(url);
                return image;
            }
            if (c1 == 77 && c2 == 77 && c3 == 0 && c4 == 42 || c1 == 73 && c2 == 73 && c3 == 42 && c4 == 0) {
                Image image = img = ImageLoader.getTiffImage(url);
                return image;
            }
            if (c1 == 151 && c2 == 74 && c3 == 66 && c4 == 50 && c5 == 13 && c6 == 10 && c7 == 26 && c8 == 10) {
                throw new IOException(url.toString() + " is not a recognized imageformat. JBIG2 support has been removed.");
            }
            throw new IOException(url.toString() + " is not a recognized imageformat.");
        }
        finally {
            if (is != null) {
                is.close();
            }
            if (img != null) {
                img.setUrl(url);
            }
        }
    }

    public static Image getInstance(String filename) throws BadElementException, IOException {
        return Image.getInstance(Utilities.toURL(filename));
    }

    public static Image getInstance(byte[] imgb) throws BadElementException, IOException {
        try (ByteArrayInputStream is = null;){
            is = new ByteArrayInputStream(imgb);
            int c1 = ((InputStream)is).read();
            int c2 = ((InputStream)is).read();
            int c3 = ((InputStream)is).read();
            int c4 = ((InputStream)is).read();
            ((InputStream)is).close();
            is = null;
            if (c1 == 71 && c2 == 73 && c3 == 70) {
                Image image = ImageLoader.getGifImage(imgb);
                return image;
            }
            if (c1 == 255 && c2 == 216) {
                Image image = ImageLoader.getJpegImage(imgb);
                return image;
            }
            if (c1 == 0 && c2 == 0 && c3 == 0 && c4 == 12) {
                Image image = ImageLoader.getJpeg2000Image(imgb);
                return image;
            }
            if (c1 == 255 && c2 == 79 && c3 == 255 && c4 == 81) {
                Image image = ImageLoader.getJpeg2000Image(imgb);
                return image;
            }
            if (c1 == PNGID[0] && c2 == PNGID[1] && c3 == PNGID[2] && c4 == PNGID[3]) {
                Image image = ImageLoader.getPngImage(imgb);
                return image;
            }
            if (c1 == 215 && c2 == 205) {
                ImgWMF imgWMF = new ImgWMF(imgb);
                return imgWMF;
            }
            if (c1 == 66 && c2 == 77) {
                Image image = ImageLoader.getBmpImage(imgb);
                return image;
            }
            if (c1 == 77 && c2 == 77 && c3 == 0 && c4 == 42 || c1 == 73 && c2 == 73 && c3 == 42 && c4 == 0) {
                Image image = ImageLoader.getTiffImage(imgb);
                return image;
            }
            if (c1 == 151 && c2 == 74 && c3 == 66 && c4 == 50) {
                is = new ByteArrayInputStream(imgb);
                ((InputStream)is).skip(4L);
                int c5 = ((InputStream)is).read();
                int c6 = ((InputStream)is).read();
                int c7 = ((InputStream)is).read();
                int c8 = ((InputStream)is).read();
                if (c5 == 13 && c6 == 10 && c7 == 26 && c8 == 10) {
                    throw new IOException(MessageLocalization.getComposedMessage("the.byte.array.is.not.a.recognized.imageformat"));
                }
            }
            throw new IOException(MessageLocalization.getComposedMessage("the.byte.array.is.not.a.recognized.imageformat"));
        }
    }

    public static Image getInstance(int width, int height, int components, int bpc, byte[] data) throws BadElementException {
        return Image.getInstance(width, height, components, bpc, data, null);
    }

    public static Image getInstance(int width, int height, byte[] data, byte[] globals) {
        ImgJBIG2 img = new ImgJBIG2(width, height, data, globals);
        return img;
    }

    public static Image getInstance(int width, int height, boolean reverseBits, int typeCCITT, int parameters, byte[] data) throws BadElementException {
        return Image.getInstance(width, height, reverseBits, typeCCITT, parameters, data, null);
    }

    public static Image getInstance(int width, int height, boolean reverseBits, int typeCCITT, int parameters, byte[] data, int[] transparency) throws BadElementException {
        if (transparency != null && transparency.length != 2) {
            throw new BadElementException(MessageLocalization.getComposedMessage("transparency.length.must.be.equal.to.2.with.ccitt.images"));
        }
        ImgCCITT img = new ImgCCITT(width, height, reverseBits, typeCCITT, parameters, data);
        img.transparency = transparency;
        return img;
    }

    public static Image getInstance(int width, int height, int components, int bpc, byte[] data, int[] transparency) throws BadElementException {
        if (transparency != null && transparency.length != components * 2) {
            throw new BadElementException(MessageLocalization.getComposedMessage("transparency.length.must.be.equal.to.componentes.2"));
        }
        if (components == 1 && bpc == 1) {
            byte[] g4 = CCITTG4Encoder.compress(data, width, height);
            return Image.getInstance(width, height, false, 256, 1, g4, transparency);
        }
        ImgRaw img = new ImgRaw(width, height, components, bpc, data);
        img.transparency = transparency;
        return img;
    }

    public static Image getInstance(PdfTemplate template) throws BadElementException {
        return new ImgTemplate(template);
    }

    public static Image getInstance(java.awt.Image image, Color color, boolean forceBW) throws BadElementException, IOException {
        BufferedImage bi;
        if (image instanceof BufferedImage && (bi = (BufferedImage)image).getType() == 12) {
            forceBW = true;
        }
        PixelGrabber pg = new PixelGrabber(image, 0, 0, -1, -1, true);
        try {
            pg.grabPixels();
        }
        catch (InterruptedException e) {
            throw new IOException(MessageLocalization.getComposedMessage("java.awt.image.interrupted.waiting.for.pixels"));
        }
        if ((pg.getStatus() & 0x80) != 0) {
            throw new IOException(MessageLocalization.getComposedMessage("java.awt.image.fetch.aborted.or.errored"));
        }
        int w = pg.getWidth();
        int h = pg.getHeight();
        int[] pixels = (int[])pg.getPixels();
        if (forceBW) {
            int byteWidth = w / 8 + ((w & 7) != 0 ? 1 : 0);
            byte[] pixelsByte = new byte[byteWidth * h];
            int index = 0;
            int size = h * w;
            boolean transColor = true;
            if (color != null) {
                transColor = color.getRed() + color.getGreen() + color.getBlue() >= 384;
            }
            int[] transparency = null;
            int cbyte = 128;
            int wMarker = 0;
            int currByte = 0;
            if (color != null) {
                for (int j = 0; j < size; ++j) {
                    int alpha = pixels[j] >> 24 & 0xFF;
                    if (alpha < 250) {
                        if (transColor) {
                            currByte |= cbyte;
                        }
                    } else if ((pixels[j] & 0x888) != 0) {
                        currByte |= cbyte;
                    }
                    if ((cbyte >>= 1) == 0 || wMarker + 1 >= w) {
                        pixelsByte[index++] = (byte)currByte;
                        cbyte = 128;
                        currByte = 0;
                    }
                    if (++wMarker < w) continue;
                    wMarker = 0;
                }
            } else {
                for (int j = 0; j < size; ++j) {
                    int alpha;
                    if (transparency == null && (alpha = pixels[j] >> 24 & 0xFF) == 0) {
                        transparency = new int[2];
                        transparency[1] = (pixels[j] & 0x888) != 0 ? 255 : 0;
                        transparency[0] = transparency[1];
                    }
                    if ((pixels[j] & 0x888) != 0) {
                        currByte |= cbyte;
                    }
                    if ((cbyte >>= 1) == 0 || wMarker + 1 >= w) {
                        pixelsByte[index++] = (byte)currByte;
                        cbyte = 128;
                        currByte = 0;
                    }
                    if (++wMarker < w) continue;
                    wMarker = 0;
                }
            }
            return Image.getInstance(w, h, 1, 1, pixelsByte, transparency);
        }
        byte[] pixelsByte = new byte[w * h * 3];
        byte[] smask = null;
        int index = 0;
        int size = h * w;
        int red = 255;
        int green = 255;
        int blue = 255;
        if (color != null) {
            red = color.getRed();
            green = color.getGreen();
            blue = color.getBlue();
        }
        int[] transparency = null;
        if (color != null) {
            for (int j = 0; j < size; ++j) {
                int alpha = pixels[j] >> 24 & 0xFF;
                if (alpha < 250) {
                    pixelsByte[index++] = (byte)red;
                    pixelsByte[index++] = (byte)green;
                    pixelsByte[index++] = (byte)blue;
                    continue;
                }
                pixelsByte[index++] = (byte)(pixels[j] >> 16 & 0xFF);
                pixelsByte[index++] = (byte)(pixels[j] >> 8 & 0xFF);
                pixelsByte[index++] = (byte)(pixels[j] & 0xFF);
            }
        } else {
            int transparentPixel = 0;
            smask = new byte[w * h];
            boolean shades = false;
            for (int j = 0; j < size; ++j) {
                byte alpha = smask[j] = (byte)(pixels[j] >> 24 & 0xFF);
                if (!shades) {
                    if (alpha != 0 && alpha != -1) {
                        shades = true;
                    } else if (transparency == null) {
                        if (alpha == 0) {
                            transparentPixel = pixels[j] & 0xFFFFFF;
                            transparency = new int[6];
                            transparency[0] = transparency[1] = transparentPixel >> 16 & 0xFF;
                            transparency[2] = transparency[3] = transparentPixel >> 8 & 0xFF;
                            transparency[4] = transparency[5] = transparentPixel & 0xFF;
                            for (int prevPixel = 0; prevPixel < j; ++prevPixel) {
                                if ((pixels[prevPixel] & 0xFFFFFF) != transparentPixel) continue;
                                shades = true;
                                break;
                            }
                        }
                    } else if ((pixels[j] & 0xFFFFFF) != transparentPixel && alpha == 0 || (pixels[j] & 0xFFFFFF) == transparentPixel && alpha != 0) {
                        shades = true;
                    }
                }
                pixelsByte[index++] = (byte)(pixels[j] >> 16 & 0xFF);
                pixelsByte[index++] = (byte)(pixels[j] >> 8 & 0xFF);
                pixelsByte[index++] = (byte)(pixels[j] & 0xFF);
            }
            if (shades) {
                transparency = null;
            } else {
                smask = null;
            }
        }
        Image img = Image.getInstance(w, h, 3, 8, pixelsByte, transparency);
        if (smask != null) {
            Image sm = Image.getInstance(w, h, 1, 8, smask);
            try {
                sm.makeMask();
                img.setImageMask(sm);
            }
            catch (DocumentException de) {
                throw new ExceptionConverter(de);
            }
        }
        return img;
    }

    public static Image getInstance(java.awt.Image image, Color color) throws BadElementException, IOException {
        return Image.getInstance(image, color, false);
    }

    public static Image getInstance(PdfWriter writer, java.awt.Image awtImage, float quality) throws BadElementException, IOException {
        return Image.getInstance(new PdfContentByte(writer), awtImage, quality);
    }

    public static Image getInstance(PdfContentByte cb, java.awt.Image awtImage, float quality) throws BadElementException, IOException {
        PixelGrabber pg = new PixelGrabber(awtImage, 0, 0, -1, -1, true);
        try {
            pg.grabPixels();
        }
        catch (InterruptedException e) {
            throw new IOException(MessageLocalization.getComposedMessage("java.awt.image.interrupted.waiting.for.pixels"));
        }
        if ((pg.getStatus() & 0x80) != 0) {
            throw new IOException(MessageLocalization.getComposedMessage("java.awt.image.fetch.aborted.or.errored"));
        }
        int w = pg.getWidth();
        int h = pg.getHeight();
        PdfTemplate tp = cb.createTemplate(w, h);
        Graphics2D g2d = tp.createGraphics(w, h, true, quality);
        g2d.drawImage(awtImage, 0, 0, null);
        g2d.dispose();
        return Image.getInstance(tp);
    }

    public PdfIndirectReference getDirectReference() {
        return this.directReference;
    }

    public void setDirectReference(PdfIndirectReference directReference) {
        this.directReference = directReference;
    }

    public static Image getInstance(PRIndirectReference ref) throws BadElementException {
        PdfDictionary dic = (PdfDictionary)PdfReader.getPdfObjectRelease(ref);
        int width = ((PdfNumber)PdfReader.getPdfObjectRelease(dic.get(PdfName.WIDTH))).intValue();
        int height = ((PdfNumber)PdfReader.getPdfObjectRelease(dic.get(PdfName.HEIGHT))).intValue();
        Image imask = null;
        PdfObject obj = dic.get(PdfName.SMASK);
        if (obj != null && obj.isIndirect()) {
            imask = Image.getInstance((PRIndirectReference)obj);
        } else {
            PdfObject obj2;
            obj = dic.get(PdfName.MASK);
            if (obj != null && obj.isIndirect() && (obj2 = PdfReader.getPdfObjectRelease(obj)) instanceof PdfDictionary) {
                imask = Image.getInstance((PRIndirectReference)obj);
            }
        }
        ImgRaw img = new ImgRaw(width, height, 1, 1, null);
        img.imageMask = imask;
        img.directReference = ref;
        return img;
    }

    protected Image(Image image) {
        super(image);
        this.type = image.type;
        this.url = image.url;
        this.rawData = image.rawData;
        this.bpc = image.bpc;
        this.template = image.template;
        this.alignment = image.alignment;
        this.alt = image.alt;
        this.absoluteX = image.absoluteX;
        this.absoluteY = image.absoluteY;
        this.plainWidth = image.plainWidth;
        this.plainHeight = image.plainHeight;
        this.scaledWidth = image.scaledWidth;
        this.scaledHeight = image.scaledHeight;
        this.mySerialId = image.mySerialId;
        this.directReference = image.directReference;
        this.rotationRadians = image.rotationRadians;
        this.initialRotation = image.initialRotation;
        this.indentationLeft = image.indentationLeft;
        this.indentationRight = image.indentationRight;
        this.spacingBefore = image.spacingBefore;
        this.spacingAfter = image.spacingAfter;
        this.widthPercentage = image.widthPercentage;
        this.annotation = image.annotation;
        this.layer = image.layer;
        this.interpolation = image.interpolation;
        this.originalType = image.originalType;
        this.originalData = image.originalData;
        this.deflated = image.deflated;
        this.dpiX = image.dpiX;
        this.dpiY = image.dpiY;
        this.XYRatio = image.XYRatio;
        this.colorspace = image.colorspace;
        this.invert = image.invert;
        this.profile = image.profile;
        this.additional = image.additional;
        this.mask = image.mask;
        this.imageMask = image.imageMask;
        this.smask = image.smask;
        this.transparency = image.transparency;
    }

    public static Image getInstance(Image image) {
        if (image == null) {
            return null;
        }
        try {
            Class<?> cs = image.getClass();
            Constructor<?> constructor = cs.getDeclaredConstructor(Image.class);
            return (Image)constructor.newInstance(image);
        }
        catch (Exception e) {
            throw new ExceptionConverter(e);
        }
    }

    @Override
    public int type() {
        return this.type;
    }

    @Override
    public boolean isNestable() {
        return true;
    }

    public boolean isJpeg() {
        return this.type == 32;
    }

    public boolean isImgRaw() {
        return this.type == 34;
    }

    public boolean isImgTemplate() {
        return this.type == 35;
    }

    public URL getUrl() {
        return this.url;
    }

    public void setUrl(URL url) {
        this.url = url;
    }

    public byte[] getRawData() {
        return this.rawData;
    }

    public int getBpc() {
        return this.bpc;
    }

    public PdfTemplate getTemplateData() {
        return this.template[0];
    }

    public void setTemplateData(PdfTemplate template) {
        this.template[0] = template;
    }

    public int getAlignment() {
        return this.alignment;
    }

    public void setAlignment(int alignment) {
        this.alignment = alignment;
    }

    public String getAlt() {
        return this.alt;
    }

    public void setAlt(String alt) {
        this.alt = alt;
    }

    public void setAbsolutePosition(float absoluteX, float absoluteY) {
        this.absoluteX = absoluteX;
        this.absoluteY = absoluteY;
    }

    public boolean hasAbsoluteX() {
        return !Float.isNaN(this.absoluteX);
    }

    public float getAbsoluteX() {
        return this.absoluteX;
    }

    public boolean hasAbsoluteY() {
        return !Float.isNaN(this.absoluteY);
    }

    public float getAbsoluteY() {
        return this.absoluteY;
    }

    public float getScaledWidth() {
        return this.scaledWidth;
    }

    public float getScaledHeight() {
        return this.scaledHeight;
    }

    public float getPlainWidth() {
        return this.plainWidth;
    }

    public float getPlainHeight() {
        return this.plainHeight;
    }

    public void scaleAbsolute(float newWidth, float newHeight) {
        this.plainWidth = newWidth;
        this.plainHeight = newHeight;
        float[] matrix = this.matrix();
        this.scaledWidth = matrix[6] - matrix[4];
        this.scaledHeight = matrix[7] - matrix[5];
        this.setWidthPercentage(0.0f);
    }

    public void scaleAbsoluteWidth(float newWidth) {
        this.plainWidth = newWidth;
        float[] matrix = this.matrix();
        this.scaledWidth = matrix[6] - matrix[4];
        this.scaledHeight = matrix[7] - matrix[5];
        this.setWidthPercentage(0.0f);
    }

    public void scaleAbsoluteHeight(float newHeight) {
        this.plainHeight = newHeight;
        float[] matrix = this.matrix();
        this.scaledWidth = matrix[6] - matrix[4];
        this.scaledHeight = matrix[7] - matrix[5];
        this.setWidthPercentage(0.0f);
    }

    public void scalePercent(float percent) {
        this.scalePercent(percent, percent);
    }

    public void scalePercent(float percentX, float percentY) {
        this.plainWidth = this.getWidth() * percentX / 100.0f;
        this.plainHeight = this.getHeight() * percentY / 100.0f;
        float[] matrix = this.matrix();
        this.scaledWidth = matrix[6] - matrix[4];
        this.scaledHeight = matrix[7] - matrix[5];
        this.setWidthPercentage(0.0f);
    }

    public void scaleToFit(float fitWidth, float fitHeight) {
        this.scalePercent(100.0f);
        float percentX = fitWidth * 100.0f / this.getScaledWidth();
        float percentY = fitHeight * 100.0f / this.getScaledHeight();
        this.scalePercent(percentX < percentY ? percentX : percentY);
        this.setWidthPercentage(0.0f);
    }

    public float[] matrix() {
        float[] matrix = new float[8];
        float cosX = (float)Math.cos(this.rotationRadians);
        float sinX = (float)Math.sin(this.rotationRadians);
        matrix[0] = this.plainWidth * cosX;
        matrix[1] = this.plainWidth * sinX;
        matrix[2] = -this.plainHeight * sinX;
        matrix[3] = this.plainHeight * cosX;
        if ((double)this.rotationRadians < 1.5707963267948966) {
            matrix[4] = matrix[2];
            matrix[5] = 0.0f;
            matrix[6] = matrix[0];
            matrix[7] = matrix[1] + matrix[3];
        } else if ((double)this.rotationRadians < Math.PI) {
            matrix[4] = matrix[0] + matrix[2];
            matrix[5] = matrix[3];
            matrix[6] = 0.0f;
            matrix[7] = matrix[1];
        } else if ((double)this.rotationRadians < 4.71238898038469) {
            matrix[4] = matrix[0];
            matrix[5] = matrix[1] + matrix[3];
            matrix[6] = matrix[2];
            matrix[7] = 0.0f;
        } else {
            matrix[4] = 0.0f;
            matrix[5] = matrix[1];
            matrix[6] = matrix[0] + matrix[2];
            matrix[7] = matrix[3];
        }
        return matrix;
    }

    protected static synchronized Long getSerialId() {
        return ++serialId;
    }

    public Long getMySerialId() {
        return this.mySerialId;
    }

    public float getImageRotation() {
        float d = (float)Math.PI * 2;
        float rot = (this.rotationRadians - this.initialRotation) % d;
        if (rot < 0.0f) {
            rot += d;
        }
        return rot;
    }

    public void setRotation(float r) {
        float d = (float)Math.PI * 2;
        this.rotationRadians = (r + this.initialRotation) % d;
        if (this.rotationRadians < 0.0f) {
            this.rotationRadians += d;
        }
        float[] matrix = this.matrix();
        this.scaledWidth = matrix[6] - matrix[4];
        this.scaledHeight = matrix[7] - matrix[5];
    }

    public void setRotationDegrees(float deg) {
        float d = (float)Math.PI;
        this.setRotation(deg / 180.0f * d);
    }

    public float getInitialRotation() {
        return this.initialRotation;
    }

    public void setInitialRotation(float initialRotation) {
        float old_rot = this.rotationRadians - this.initialRotation;
        this.initialRotation = initialRotation;
        this.setRotation(old_rot);
    }

    public float getIndentationLeft() {
        return this.indentationLeft;
    }

    public void setIndentationLeft(float f) {
        this.indentationLeft = f;
    }

    public float getIndentationRight() {
        return this.indentationRight;
    }

    public void setIndentationRight(float f) {
        this.indentationRight = f;
    }

    public float getSpacingBefore() {
        return this.spacingBefore;
    }

    public void setSpacingBefore(float spacing) {
        this.spacingBefore = spacing;
    }

    public float getSpacingAfter() {
        return this.spacingAfter;
    }

    public void setSpacingAfter(float spacing) {
        this.spacingAfter = spacing;
    }

    public float getWidthPercentage() {
        return this.widthPercentage;
    }

    public void setWidthPercentage(float widthPercentage) {
        this.widthPercentage = widthPercentage;
    }

    public void setAnnotation(Annotation annotation) {
        this.annotation = annotation;
    }

    public Annotation getAnnotation() {
        return this.annotation;
    }

    public PdfOCG getLayer() {
        return this.layer;
    }

    public void setLayer(PdfOCG layer) {
        this.layer = layer;
    }

    public boolean isInterpolation() {
        return this.interpolation;
    }

    public void setInterpolation(boolean interpolation) {
        this.interpolation = interpolation;
    }

    public int getOriginalType() {
        return this.originalType;
    }

    public void setOriginalType(int originalType) {
        this.originalType = originalType;
    }

    public byte[] getOriginalData() {
        return this.originalData;
    }

    public void setOriginalData(byte[] originalData) {
        this.originalData = originalData;
    }

    public boolean isDeflated() {
        return this.deflated;
    }

    public void setDeflated(boolean deflated) {
        this.deflated = deflated;
    }

    public int getDpiX() {
        return this.dpiX;
    }

    public int getDpiY() {
        return this.dpiY;
    }

    public void setDpi(int dpiX, int dpiY) {
        this.dpiX = dpiX;
        this.dpiY = dpiY;
    }

    public float getXYRatio() {
        return this.XYRatio;
    }

    public void setXYRatio(float XYRatio) {
        this.XYRatio = XYRatio;
    }

    public int getColorspace() {
        return this.colorspace;
    }

    public boolean isInverted() {
        return this.invert;
    }

    public void setInverted(boolean invert) {
        this.invert = invert;
    }

    public void tagICC(ICC_Profile profile) {
        this.profile = profile;
    }

    public boolean hasICCProfile() {
        return this.profile != null;
    }

    public ICC_Profile getICCProfile() {
        return this.profile;
    }

    public PdfDictionary getAdditional() {
        return this.additional;
    }

    public void setAdditional(PdfDictionary additional) {
        this.additional = additional;
    }

    public void simplifyColorspace() {
        PdfObject newValue;
        if (this.additional == null) {
            return;
        }
        PdfArray value = this.additional.getAsArray(PdfName.COLORSPACE);
        if (value == null) {
            return;
        }
        PdfObject cs = this.simplifyColorspace(value);
        if (cs.isName()) {
            newValue = cs;
        } else {
            PdfArray second;
            newValue = value;
            PdfName first = value.getAsName(0);
            if (PdfName.INDEXED.equals(first) && value.size() >= 2 && (second = value.getAsArray(1)) != null) {
                value.set(1, this.simplifyColorspace(second));
            }
        }
        this.additional.put(PdfName.COLORSPACE, newValue);
    }

    private PdfObject simplifyColorspace(PdfArray obj) {
        if (obj == null) {
            return obj;
        }
        PdfName first = obj.getAsName(0);
        if (PdfName.CALGRAY.equals(first)) {
            return PdfName.DEVICEGRAY;
        }
        if (PdfName.CALRGB.equals(first)) {
            return PdfName.DEVICERGB;
        }
        return obj;
    }

    public boolean isMask() {
        return this.mask;
    }

    public void makeMask() throws DocumentException {
        if (!this.isMaskCandidate()) {
            throw new DocumentException(MessageLocalization.getComposedMessage("this.image.can.not.be.an.image.mask"));
        }
        this.mask = true;
    }

    public boolean isMaskCandidate() {
        if (this.type == 34 && this.bpc > 255) {
            return true;
        }
        return this.colorspace == 1;
    }

    public Image getImageMask() {
        return this.imageMask;
    }

    public void setImageMask(Image mask) throws DocumentException {
        if (this.mask) {
            throw new DocumentException(MessageLocalization.getComposedMessage("an.image.mask.cannot.contain.another.image.mask"));
        }
        if (!mask.mask) {
            throw new DocumentException(MessageLocalization.getComposedMessage("the.image.mask.is.not.a.mask.did.you.do.makemask"));
        }
        this.imageMask = mask;
        this.smask = mask.bpc > 1 && mask.bpc <= 8;
    }

    public boolean isSmask() {
        return this.smask;
    }

    public void setSmask(boolean smask) {
        this.smask = smask;
    }

    public int[] getTransparency() {
        return this.transparency;
    }

    public void setTransparency(int[] transparency) {
        this.transparency = transparency;
    }

    public int getCompressionLevel() {
        return this.compressionLevel;
    }

    public void setCompressionLevel(int compressionLevel) {
        this.compressionLevel = compressionLevel < 0 || compressionLevel > 9 ? -1 : compressionLevel;
    }
}


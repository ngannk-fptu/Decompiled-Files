/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.io.IOUtils
 */
package org.apache.xmlgraphics.ps;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.color.ColorSpace;
import java.awt.geom.Dimension2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.ColorModel;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferByte;
import java.awt.image.IndexColorModel;
import java.awt.image.Raster;
import java.awt.image.RenderedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;
import org.apache.commons.io.IOUtils;
import org.apache.xmlgraphics.ps.ImageEncoder;
import org.apache.xmlgraphics.ps.ImageEncodingHelper;
import org.apache.xmlgraphics.ps.PSDictionary;
import org.apache.xmlgraphics.ps.PSGenerator;
import org.apache.xmlgraphics.ps.PSProcSets;
import org.apache.xmlgraphics.ps.PSResource;
import org.apache.xmlgraphics.util.io.ASCII85OutputStream;
import org.apache.xmlgraphics.util.io.Finalizable;
import org.apache.xmlgraphics.util.io.FlateEncodeOutputStream;
import org.apache.xmlgraphics.util.io.RunLengthEncodeOutputStream;

public class PSImageUtils {
    private static final char[] HEX = new char[]{'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};

    public static void writeImage(final byte[] img, Dimension imgDim, String imgDescription, Rectangle2D targetRect, final boolean isJPEG, ColorSpace colorSpace, PSGenerator gen) throws IOException {
        ImageEncoder encoder = new ImageEncoder(){

            @Override
            public void writeTo(OutputStream out) throws IOException {
                out.write(img);
            }

            @Override
            public String getImplicitFilter() {
                if (isJPEG) {
                    return "<< >> /DCTDecode";
                }
                return null;
            }
        };
        PSImageUtils.writeImage(encoder, imgDim, imgDescription, targetRect, colorSpace, 8, false, gen);
    }

    public static void writeImage(ImageEncoder encoder, Dimension imgDim, String imgDescription, Rectangle2D targetRect, ColorSpace colorSpace, int bitsPerComponent, boolean invertImage, PSGenerator gen) throws IOException {
        gen.saveGraphicsState();
        PSImageUtils.translateAndScale(gen, null, targetRect);
        gen.commentln("%AXGBeginBitmap: " + imgDescription);
        gen.writeln("{{");
        String implicitFilter = encoder.getImplicitFilter();
        if (implicitFilter != null) {
            gen.writeln("/RawData currentfile /ASCII85Decode filter def");
            gen.writeln("/Data RawData " + implicitFilter + " filter def");
        } else if (gen.getPSLevel() >= 3) {
            gen.writeln("/RawData currentfile /ASCII85Decode filter def");
            gen.writeln("/Data RawData /FlateDecode filter def");
        } else {
            gen.writeln("/RawData currentfile /ASCII85Decode filter def");
            gen.writeln("/Data RawData /RunLengthDecode filter def");
        }
        PSDictionary imageDict = new PSDictionary();
        imageDict.put("/DataSource", "Data");
        imageDict.put("/BitsPerComponent", Integer.toString(bitsPerComponent));
        PSImageUtils.writeImageCommand(imageDict, imgDim, colorSpace, invertImage, gen);
        gen.writeln("} stopped {handleerror} if");
        gen.writeln("  RawData flushfile");
        gen.writeln("} exec");
        PSImageUtils.compressAndWriteBitmap(encoder, gen);
        gen.newLine();
        gen.commentln("%AXGEndBitmap");
        gen.restoreGraphicsState();
    }

    public static void writeImage(ImageEncoder encoder, Dimension imgDim, String imgDescription, Rectangle2D targetRect, ColorModel colorModel, PSGenerator gen) throws IOException {
        PSImageUtils.writeImage(encoder, imgDim, imgDescription, targetRect, colorModel, gen, null, false);
    }

    public static void writeImage(ImageEncoder encoder, Dimension imgDim, String imgDescription, Rectangle2D targetRect, ColorModel colorModel, PSGenerator gen, RenderedImage ri, boolean maskBitmap) throws IOException {
        DataBuffer buffer;
        gen.saveGraphicsState();
        PSImageUtils.translateAndScale(gen, null, targetRect);
        gen.commentln("%AXGBeginBitmap: " + imgDescription);
        gen.writeln("{{");
        String implicitFilter = encoder.getImplicitFilter();
        if (implicitFilter != null) {
            gen.writeln("/RawData currentfile /ASCII85Decode filter def");
            gen.writeln("/Data RawData " + implicitFilter + " filter def");
        } else if (gen.getPSLevel() >= 3) {
            gen.writeln("/RawData currentfile /ASCII85Decode filter def");
            gen.writeln("/Data RawData /FlateDecode filter def");
        } else {
            gen.writeln("/RawData currentfile /ASCII85Decode filter def");
            gen.writeln("/Data RawData /RunLengthDecode filter def");
        }
        PSDictionary imageDict = new PSDictionary();
        imageDict.put("/DataSource", "Data");
        PSImageUtils.populateImageDictionary(imgDim, colorModel, imageDict);
        if (ri != null && !((buffer = ri.getData().getDataBuffer()) instanceof DataBufferByte)) {
            imageDict.put("/BitsPerComponent", 8);
        }
        PSImageUtils.writeImageCommand(imageDict, colorModel, gen, maskBitmap);
        gen.writeln("} stopped {handleerror} if");
        gen.writeln("  RawData flushfile");
        gen.writeln("} exec");
        PSImageUtils.compressAndWriteBitmap(encoder, gen);
        gen.newLine();
        gen.commentln("%AXGEndBitmap");
        gen.restoreGraphicsState();
    }

    public static void writeImage(ImageEncoder encoder, Dimension imgDim, String imgDescription, Rectangle2D targetRect, ColorModel colorModel, PSGenerator gen, RenderedImage ri, Color maskColor) throws IOException {
        DataBuffer buffer;
        gen.saveGraphicsState();
        PSImageUtils.translateAndScale(gen, null, targetRect);
        gen.commentln("%AXGBeginBitmap: " + imgDescription);
        gen.writeln("{{");
        String implicitFilter = encoder.getImplicitFilter();
        if (implicitFilter != null) {
            gen.writeln("/RawData currentfile /ASCII85Decode filter def");
            gen.writeln("/Data RawData " + implicitFilter + " filter def");
        } else if (gen.getPSLevel() >= 3) {
            gen.writeln("/RawData currentfile /ASCII85Decode filter def");
            gen.writeln("/Data RawData /FlateDecode filter def");
        } else {
            gen.writeln("/RawData currentfile /ASCII85Decode filter def");
            gen.writeln("/Data RawData /RunLengthDecode filter def");
        }
        PSDictionary imageDict = new PSDictionary();
        imageDict.put("/DataSource", "Data");
        PSImageUtils.populateImageDictionary(imgDim, colorModel, imageDict, maskColor);
        if (ri != null && !((buffer = ri.getData().getDataBuffer()) instanceof DataBufferByte)) {
            imageDict.put("/BitsPerComponent", 8);
        }
        PSImageUtils.writeImageCommand(imageDict, colorModel, gen, false);
        gen.writeln("} stopped {handleerror} if");
        gen.writeln("  RawData flushfile");
        gen.writeln("} exec");
        PSImageUtils.compressAndWriteBitmap(encoder, gen);
        gen.newLine();
        gen.commentln("%AXGEndBitmap");
        gen.restoreGraphicsState();
    }

    private static ColorModel populateImageDictionary(Dimension imgDim, ColorModel colorModel, PSDictionary imageDict) {
        imageDict.put("/ImageType", "1");
        colorModel = PSImageUtils.writeImageDictionary(imgDim, imageDict, colorModel);
        return colorModel;
    }

    private static ColorModel populateImageDictionary(Dimension imgDim, ColorModel colorModel, PSDictionary imageDict, Color maskColor) {
        imageDict.put("/ImageType", "4");
        colorModel = PSImageUtils.writeImageDictionary(imgDim, imageDict, colorModel);
        imageDict.put("/MaskColor", String.format("[ %d %d %d ]", maskColor.getRed(), maskColor.getGreen(), maskColor.getBlue()));
        return colorModel;
    }

    private static ColorModel writeImageDictionary(Dimension imgDim, PSDictionary imageDict, ColorModel colorModel) {
        String w = Integer.toString(imgDim.width);
        String h = Integer.toString(imgDim.height);
        imageDict.put("/Width", w);
        imageDict.put("/Height", h);
        boolean invertColors = false;
        String decodeArray = PSImageUtils.getDecodeArray(colorModel.getNumColorComponents(), invertColors);
        int bitsPerComp = colorModel.getComponentSize(0);
        imageDict.put("/ImageMatrix", "[" + w + " 0 0 " + h + " 0 0]");
        if (colorModel instanceof IndexColorModel) {
            IndexColorModel indexColorModel = (IndexColorModel)colorModel;
            int c = indexColorModel.getMapSize();
            int hival = c - 1;
            if (hival > 4095) {
                throw new UnsupportedOperationException("hival must not go beyond 4095");
            }
            bitsPerComp = indexColorModel.getPixelSize();
            int ceiling = (int)Math.pow(2.0, bitsPerComp) - 1;
            decodeArray = "[0 " + ceiling + "]";
        }
        imageDict.put("/BitsPerComponent", Integer.toString(bitsPerComp));
        imageDict.put("/Decode", decodeArray);
        return colorModel;
    }

    private static String getDecodeArray(int numComponents, boolean invertColors) {
        StringBuffer sb = new StringBuffer("[");
        for (int i = 0; i < numComponents; ++i) {
            if (i > 0) {
                sb.append(" ");
            }
            if (invertColors) {
                sb.append("1 0");
                continue;
            }
            sb.append("0 1");
        }
        sb.append("]");
        String decodeArray = sb.toString();
        return decodeArray;
    }

    private static void prepareColorspace(PSGenerator gen, ColorSpace colorSpace) throws IOException {
        gen.writeln(PSImageUtils.getColorSpaceName(colorSpace) + " setcolorspace");
    }

    private static void prepareColorSpace(PSGenerator gen, ColorModel cm) throws IOException {
        if (cm instanceof IndexColorModel) {
            boolean isDeviceGray;
            ColorSpace cs = cm.getColorSpace();
            IndexColorModel im = (IndexColorModel)cm;
            int c = im.getMapSize();
            int[] palette = new int[c];
            im.getRGBs(palette);
            byte[] reds = new byte[c];
            byte[] greens = new byte[c];
            byte[] blues = new byte[c];
            im.getReds(reds);
            im.getGreens(greens);
            im.getBlues(blues);
            int hival = c - 1;
            if (hival > 4095) {
                throw new UnsupportedOperationException("hival must not go beyond 4095");
            }
            boolean bl = isDeviceGray = Arrays.equals(reds, blues) && Arrays.equals(blues, greens);
            if (isDeviceGray) {
                gen.write("[/Indexed /DeviceGray");
            } else {
                gen.write("[/Indexed " + PSImageUtils.getColorSpaceName(cs));
            }
            gen.writeln(" " + Integer.toString(hival));
            gen.write("  <");
            if (isDeviceGray) {
                gen.write(PSImageUtils.toHexString(blues));
            } else {
                for (int i = 0; i < c; ++i) {
                    if (i > 0) {
                        if (i % 8 == 0) {
                            gen.newLine();
                            gen.write("   ");
                        } else {
                            gen.write(" ");
                        }
                    }
                    gen.write(PSImageUtils.rgb2Hex(palette[i]));
                }
            }
            gen.writeln(">");
            gen.writeln("] setcolorspace");
        } else {
            gen.writeln(PSImageUtils.getColorSpaceName(cm.getColorSpace()) + " setcolorspace");
        }
    }

    static String toHexString(byte[] color) {
        char[] hexChars = new char[color.length * 2];
        for (int i = 0; i < color.length; ++i) {
            int x = color[i] & 0xFF;
            hexChars[i * 2] = HEX[x >>> 4];
            hexChars[i * 2 + 1] = HEX[x & 0xF];
        }
        return new String(hexChars);
    }

    static void writeImageCommand(RenderedImage img, PSDictionary imageDict, PSGenerator gen) throws IOException {
        ImageEncodingHelper helper = new ImageEncodingHelper(img, true);
        ColorModel cm = helper.getEncodedColorModel();
        Dimension imgDim = new Dimension(img.getWidth(), img.getHeight());
        PSImageUtils.populateImageDictionary(imgDim, cm, imageDict);
        PSImageUtils.writeImageCommand(imageDict, cm, gen, false);
    }

    static void writeImageCommand(PSDictionary imageDict, ColorModel cm, PSGenerator gen, boolean maskBitmap) throws IOException {
        if (!maskBitmap) {
            PSImageUtils.prepareColorSpace(gen, cm);
        }
        gen.write(imageDict.toString());
        if (maskBitmap) {
            gen.writeln(" imagemask");
        } else {
            gen.writeln(" image");
        }
    }

    static void writeImageCommand(PSDictionary imageDict, Dimension imgDim, ColorSpace colorSpace, boolean invertImage, PSGenerator gen) throws IOException {
        imageDict.put("/ImageType", "1");
        imageDict.put("/Width", Integer.toString(imgDim.width));
        imageDict.put("/Height", Integer.toString(imgDim.height));
        String decodeArray = PSImageUtils.getDecodeArray(colorSpace.getNumComponents(), invertImage);
        imageDict.put("/Decode", decodeArray);
        imageDict.put("/ImageMatrix", "[" + imgDim.width + " 0 0 " + imgDim.height + " 0 0]");
        PSImageUtils.prepareColorspace(gen, colorSpace);
        gen.write(imageDict.toString());
        gen.writeln(" image");
    }

    private static String rgb2Hex(int rgb) {
        StringBuffer sb = new StringBuffer();
        for (int i = 5; i >= 0; --i) {
            int shift = i * 4;
            int n = (rgb & 15 << shift) >> shift;
            sb.append(HEX[n % 16]);
        }
        return sb.toString();
    }

    public static void renderBitmapImage(RenderedImage img, float x, float y, float w, float h, PSGenerator gen, Color mask, boolean maskBitmap) throws IOException {
        Rectangle2D.Double targetRect = new Rectangle2D.Double(x, y, w, h);
        ImageEncoder encoder = ImageEncodingHelper.createRenderedImageEncoder(img);
        Dimension imgDim = new Dimension(img.getWidth(), img.getHeight());
        String imgDescription = img.getClass().getName();
        ImageEncodingHelper helper = new ImageEncodingHelper(img);
        ColorModel cm = helper.getEncodedColorModel();
        if (mask == null) {
            PSImageUtils.writeImage(encoder, imgDim, imgDescription, (Rectangle2D)targetRect, cm, gen, img, maskBitmap);
        } else {
            PSImageUtils.writeImage(encoder, imgDim, imgDescription, (Rectangle2D)targetRect, cm, gen, img, mask);
        }
    }

    public static PSResource writeReusableImage(final byte[] img, Dimension imgDim, String formName, String imageDescription, final boolean isJPEG, ColorSpace colorSpace, PSGenerator gen) throws IOException {
        ImageEncoder encoder = new ImageEncoder(){

            @Override
            public void writeTo(OutputStream out) throws IOException {
                out.write(img);
            }

            @Override
            public String getImplicitFilter() {
                if (isJPEG) {
                    return "<< >> /DCTDecode";
                }
                return null;
            }
        };
        return PSImageUtils.writeReusableImage(encoder, imgDim, formName, imageDescription, colorSpace, false, gen);
    }

    protected static PSResource writeReusableImage(ImageEncoder encoder, Dimension imgDim, String formName, String imageDescription, ColorSpace colorSpace, boolean invertImage, PSGenerator gen) throws IOException {
        String implicitFilter;
        if (gen.getPSLevel() < 2) {
            throw new UnsupportedOperationException("Reusable images requires at least Level 2 PostScript");
        }
        String dataName = formName + ":Data";
        gen.writeDSCComment("BeginResource", formName);
        if (imageDescription != null) {
            gen.writeDSCComment("Title", imageDescription);
        }
        String additionalFilters = (implicitFilter = encoder.getImplicitFilter()) != null ? "/ASCII85Decode filter " + implicitFilter + " filter" : (gen.getPSLevel() >= 3 ? "/ASCII85Decode filter /FlateDecode filter" : "/ASCII85Decode filter /RunLengthDecode filter");
        gen.writeln("/" + formName);
        gen.writeln("<< /FormType 1");
        gen.writeln("  /BBox [0 0 " + imgDim.width + " " + imgDim.height + "]");
        gen.writeln("  /Matrix [1 0 0 1 0 0]");
        gen.writeln("  /PaintProc {");
        gen.writeln("    pop");
        gen.writeln("    gsave");
        if (gen.getPSLevel() == 2) {
            gen.writeln("    userdict /i 0 put");
        } else {
            gen.writeln("    " + dataName + " 0 setfileposition");
        }
        String dataSource = gen.getPSLevel() == 2 ? "{ " + dataName + " i get /i i 1 add store } bind" : dataName;
        PSDictionary imageDict = new PSDictionary();
        imageDict.put("/DataSource", dataSource);
        imageDict.put("/BitsPerComponent", Integer.toString(8));
        PSImageUtils.writeImageCommand(imageDict, imgDim, colorSpace, invertImage, gen);
        gen.writeln("    grestore");
        gen.writeln("  } bind");
        gen.writeln(">> def");
        gen.writeln("/" + dataName + " currentfile");
        gen.writeln(additionalFilters);
        if (gen.getPSLevel() == 2) {
            gen.writeln("{ /temp exch def [ { temp 16384 string readstring not {exit } if } loop ] } exec");
        } else {
            gen.writeln("/ReusableStreamDecode filter");
        }
        PSImageUtils.compressAndWriteBitmap(encoder, gen);
        gen.writeln("def");
        gen.writeDSCComment("EndResource");
        PSResource res = new PSResource("form", formName);
        gen.getResourceTracker().registerSuppliedResource(res);
        return res;
    }

    public static void paintReusableImage(String formName, Rectangle2D targetRect, PSGenerator gen) throws IOException {
        PSResource form = new PSResource("form", formName);
        PSImageUtils.paintForm(form, null, targetRect, gen);
    }

    public static void paintForm(PSResource form, Rectangle2D targetRect, PSGenerator gen) throws IOException {
        PSImageUtils.paintForm(form, null, targetRect, gen);
    }

    public static void paintForm(PSResource form, Dimension2D formDimensions, Rectangle2D targetRect, PSGenerator gen) throws IOException {
        gen.saveGraphicsState();
        PSImageUtils.translateAndScale(gen, formDimensions, targetRect);
        gen.writeln(form.getName() + " execform");
        gen.getResourceTracker().notifyResourceUsageOnPage(form);
        gen.restoreGraphicsState();
    }

    private static String getColorSpaceName(ColorSpace colorSpace) {
        if (colorSpace.getType() == 9) {
            return "/DeviceCMYK";
        }
        if (colorSpace.getType() == 6) {
            return "/DeviceGray";
        }
        return "/DeviceRGB";
    }

    static void compressAndWriteBitmap(ImageEncoder encoder, PSGenerator gen) throws IOException {
        OutputStream out = gen.getOutputStream();
        out = new ASCII85OutputStream(out);
        String implicitFilter = encoder.getImplicitFilter();
        if (implicitFilter == null) {
            out = gen.getPSLevel() >= 3 ? new FlateEncodeOutputStream(out) : new RunLengthEncodeOutputStream(out);
        }
        encoder.writeTo(out);
        if (out instanceof Finalizable) {
            ((Finalizable)((Object)out)).finalizeStream();
        } else {
            out.flush();
        }
        gen.newLine();
    }

    public static void translateAndScale(PSGenerator gen, Dimension2D imageDimensions, Rectangle2D targetRect) throws IOException {
        gen.writeln(gen.formatDouble(targetRect.getX()) + " " + gen.formatDouble(targetRect.getY()) + " translate");
        if (imageDimensions == null) {
            imageDimensions = new Dimension(1, 1);
        }
        double sx = targetRect.getWidth() / imageDimensions.getWidth();
        double sy = targetRect.getHeight() / imageDimensions.getHeight();
        if (sx != 1.0 || sy != 1.0) {
            gen.writeln(gen.formatDouble(sx) + " " + gen.formatDouble(sy) + " scale");
        }
    }

    public static int[] getRGB(RenderedImage img, int startX, int startY, int w, int h, int[] rgbArray, int offset, int scansize) {
        Object[] data;
        Raster raster = img.getData();
        int yoff = offset;
        int nbands = raster.getNumBands();
        int dataType = raster.getDataBuffer().getDataType();
        switch (dataType) {
            case 0: {
                data = new byte[nbands];
                break;
            }
            case 1: {
                data = new short[nbands];
                break;
            }
            case 3: {
                data = new int[nbands];
                break;
            }
            case 4: {
                data = new float[nbands];
                break;
            }
            case 5: {
                data = new double[nbands];
                break;
            }
            default: {
                throw new IllegalArgumentException("Unknown data buffer type: " + dataType);
            }
        }
        if (rgbArray == null) {
            rgbArray = new int[offset + h * scansize];
        }
        ColorModel colorModel = img.getColorModel();
        int y = startY;
        while (y < startY + h) {
            int off = yoff;
            for (int x = startX; x < startX + w; ++x) {
                rgbArray[off++] = colorModel.getRGB(raster.getDataElements(x, y, data));
            }
            ++y;
            yoff += scansize;
        }
        return rgbArray;
    }

    public static void renderEPS(byte[] rawEPS, String name, float x, float y, float w, float h, float bboxx, float bboxy, float bboxw, float bboxh, PSGenerator gen) throws IOException {
        PSImageUtils.renderEPS(new ByteArrayInputStream(rawEPS), name, new Rectangle2D.Float(x, y, w, h), new Rectangle2D.Float(bboxx, bboxy, bboxw, bboxh), gen);
    }

    public static void renderEPS(InputStream in, String name, Rectangle2D viewport, Rectangle2D bbox, PSGenerator gen) throws IOException {
        gen.getResourceTracker().notifyResourceUsageOnPage(PSProcSets.EPS_PROCSET);
        gen.writeln("%AXGBeginEPS: " + name);
        gen.writeln("BeginEPSF");
        gen.writeln(gen.formatDouble(viewport.getX()) + " " + gen.formatDouble(viewport.getY()) + " translate");
        gen.writeln("0 " + gen.formatDouble(viewport.getHeight()) + " translate");
        gen.writeln("1 -1 scale");
        double sx = viewport.getWidth() / bbox.getWidth();
        double sy = viewport.getHeight() / bbox.getHeight();
        if (sx != 1.0 || sy != 1.0) {
            gen.writeln(gen.formatDouble(sx) + " " + gen.formatDouble(sy) + " scale");
        }
        if (bbox.getX() != 0.0 || bbox.getY() != 0.0) {
            gen.writeln(gen.formatDouble(-bbox.getX()) + " " + gen.formatDouble(-bbox.getY()) + " translate");
        }
        gen.writeln(gen.formatDouble(bbox.getX()) + " " + gen.formatDouble(bbox.getY()) + " " + gen.formatDouble(bbox.getWidth()) + " " + gen.formatDouble(bbox.getHeight()) + " re clip");
        gen.writeln("newpath");
        PSResource res = new PSResource("file", name);
        gen.getResourceTracker().registerSuppliedResource(res);
        gen.getResourceTracker().notifyResourceUsageOnPage(res);
        gen.writeDSCComment("BeginDocument", res.getName());
        IOUtils.copy((InputStream)in, (OutputStream)gen.getOutputStream());
        gen.newLine();
        gen.writeDSCComment("EndDocument");
        gen.writeln("EndEPSF");
        gen.writeln("%AXGEndEPS");
    }
}


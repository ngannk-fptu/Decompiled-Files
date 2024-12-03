/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 */
package org.apache.pdfbox.pdmodel.graphics.image;

import java.awt.color.ColorSpace;
import java.awt.color.ICC_ColorSpace;
import java.awt.image.BufferedImage;
import java.awt.image.ColorConvertOp;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.ImageTypeSpecifier;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.plugins.jpeg.JPEGImageWriteParam;
import javax.imageio.stream.ImageInputStream;
import javax.imageio.stream.ImageOutputStream;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.pdfbox.cos.COSArray;
import org.apache.pdfbox.cos.COSInteger;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.filter.MissingImageReaderException;
import org.apache.pdfbox.io.IOUtils;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.common.COSObjectable;
import org.apache.pdfbox.pdmodel.graphics.color.PDColorSpace;
import org.apache.pdfbox.pdmodel.graphics.color.PDDeviceCMYK;
import org.apache.pdfbox.pdmodel.graphics.color.PDDeviceColorSpace;
import org.apache.pdfbox.pdmodel.graphics.color.PDDeviceGray;
import org.apache.pdfbox.pdmodel.graphics.color.PDDeviceRGB;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.w3c.dom.Element;

public final class JPEGFactory {
    private static final Log LOG = LogFactory.getLog(JPEGFactory.class);

    private JPEGFactory() {
    }

    public static PDImageXObject createFromStream(PDDocument document, InputStream stream) throws IOException {
        return JPEGFactory.createFromByteArray(document, IOUtils.toByteArray(stream));
    }

    public static PDImageXObject createFromByteArray(PDDocument document, byte[] byteArray) throws IOException {
        PDDeviceColorSpace colorSpace;
        ByteArrayInputStream byteStream = new ByteArrayInputStream(byteArray);
        Dimensions meta = JPEGFactory.retrieveDimensions(byteStream);
        switch (meta.numComponents) {
            case 1: {
                colorSpace = PDDeviceGray.INSTANCE;
                break;
            }
            case 3: {
                colorSpace = PDDeviceRGB.INSTANCE;
                break;
            }
            case 4: {
                colorSpace = PDDeviceCMYK.INSTANCE;
                break;
            }
            default: {
                throw new UnsupportedOperationException("number of data elements not supported: " + meta.numComponents);
            }
        }
        PDImageXObject pdImage = new PDImageXObject(document, byteStream, COSName.DCT_DECODE, meta.width, meta.height, 8, colorSpace);
        if (colorSpace instanceof PDDeviceCMYK) {
            COSArray decode = new COSArray();
            decode.add(COSInteger.ONE);
            decode.add(COSInteger.ZERO);
            decode.add(COSInteger.ONE);
            decode.add(COSInteger.ZERO);
            decode.add(COSInteger.ONE);
            decode.add(COSInteger.ZERO);
            decode.add(COSInteger.ONE);
            decode.add(COSInteger.ZERO);
            pdImage.setDecode(decode);
        }
        return pdImage;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private static Dimensions retrieveDimensions(ByteArrayInputStream stream) throws IOException {
        Iterator<ImageReader> readers = ImageIO.getImageReadersByFormatName("JPEG");
        ImageReader reader = null;
        while (readers.hasNext() && !(reader = readers.next()).canReadRaster()) {
        }
        if (reader == null) {
            throw new MissingImageReaderException("Cannot read JPEG image: a suitable JAI I/O image filter is not installed");
        }
        ImageInputStream iis = null;
        try {
            Dimensions meta;
            block10: {
                iis = ImageIO.createImageInputStream(stream);
                reader.setInput(iis);
                meta = new Dimensions();
                meta.width = reader.getWidth(0);
                meta.height = reader.getHeight(0);
                meta.numComponents = JPEGFactory.getNumComponentsFromImageMetadata(reader);
                if (meta.numComponents == 0) break block10;
                Dimensions dimensions = meta;
                return dimensions;
            }
            try {
                LOG.warn((Object)"No image metadata, will decode image and use raster size");
            }
            catch (IOException ex) {
                LOG.warn((Object)"Error reading image metadata, will decode image and use raster size", (Throwable)ex);
            }
            ImageIO.setUseCache(false);
            Raster raster = reader.readRaster(0, null);
            meta.numComponents = raster.getNumDataElements();
            Dimensions dimensions = meta;
            return dimensions;
        }
        finally {
            if (iis != null) {
                iis.close();
            }
            stream.reset();
            reader.dispose();
        }
    }

    private static int getNumComponentsFromImageMetadata(ImageReader reader) throws IOException {
        IIOMetadata imageMetadata = reader.getImageMetadata(0);
        if (imageMetadata == null) {
            return 0;
        }
        Element root = (Element)imageMetadata.getAsTree("javax_imageio_jpeg_image_1.0");
        if (root == null) {
            return 0;
        }
        try {
            XPath xpath = XPathFactory.newInstance().newXPath();
            String numFrameComponents = xpath.evaluate("markerSequence/sof/@numFrameComponents", root);
            if (numFrameComponents.isEmpty()) {
                return 0;
            }
            return Integer.parseInt(numFrameComponents);
        }
        catch (NumberFormatException ex) {
            LOG.warn((Object)ex.getMessage(), (Throwable)ex);
            return 0;
        }
        catch (XPathExpressionException ex) {
            LOG.warn((Object)ex.getMessage(), (Throwable)ex);
            return 0;
        }
    }

    public static PDImageXObject createFromImage(PDDocument document, BufferedImage image) throws IOException {
        return JPEGFactory.createFromImage(document, image, 0.75f);
    }

    public static PDImageXObject createFromImage(PDDocument document, BufferedImage image, float quality) throws IOException {
        return JPEGFactory.createFromImage(document, image, quality, 72);
    }

    public static PDImageXObject createFromImage(PDDocument document, BufferedImage image, float quality, int dpi) throws IOException {
        return JPEGFactory.createJPEG(document, image, quality, dpi);
    }

    private static BufferedImage getAlphaImage(BufferedImage image) {
        if (!image.getColorModel().hasAlpha()) {
            return null;
        }
        if (image.getTransparency() == 2) {
            throw new UnsupportedOperationException("BITMASK Transparency JPEG compression is not useful, use LosslessImageFactory instead");
        }
        WritableRaster alphaRaster = image.getAlphaRaster();
        if (alphaRaster == null) {
            return null;
        }
        BufferedImage alphaImage = new BufferedImage(image.getWidth(), image.getHeight(), 10);
        alphaImage.setData(alphaRaster);
        return alphaImage;
    }

    private static PDImageXObject createJPEG(PDDocument document, BufferedImage image, float quality, int dpi) throws IOException {
        BufferedImage awtColorImage = JPEGFactory.getColorImage(image);
        byte[] encoded = JPEGFactory.encodeImageToJPEGStream(awtColorImage, quality, dpi);
        ByteArrayInputStream encodedByteStream = new ByteArrayInputStream(encoded);
        PDImageXObject pdImage = new PDImageXObject(document, encodedByteStream, COSName.DCT_DECODE, awtColorImage.getWidth(), awtColorImage.getHeight(), 8, JPEGFactory.getColorSpaceFromAWT(awtColorImage));
        BufferedImage awtAlphaImage = JPEGFactory.getAlphaImage(image);
        if (awtAlphaImage != null) {
            PDImageXObject xAlpha = JPEGFactory.createFromImage(document, awtAlphaImage, quality);
            pdImage.getCOSObject().setItem(COSName.SMASK, (COSObjectable)xAlpha);
        }
        return pdImage;
    }

    private static ImageWriter getJPEGImageWriter() throws IOException {
        Iterator<ImageWriter> writers = ImageIO.getImageWritersBySuffix("jpeg");
        while (writers.hasNext()) {
            ImageWriter writer = writers.next();
            if (writer == null) continue;
            if (writer.getDefaultWriteParam() instanceof JPEGImageWriteParam) {
                return writer;
            }
            writer.dispose();
        }
        throw new IOException("No ImageWriter found for JPEG format");
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private static byte[] encodeImageToJPEGStream(BufferedImage image, float quality, int dpi) throws IOException {
        ImageWriter imageWriter = JPEGFactory.getJPEGImageWriter();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageOutputStream ios = null;
        try {
            ios = ImageIO.createImageOutputStream(baos);
            imageWriter.setOutput(ios);
            ImageWriteParam jpegParam = imageWriter.getDefaultWriteParam();
            jpegParam.setCompressionMode(2);
            jpegParam.setCompressionQuality(quality);
            ImageTypeSpecifier imageTypeSpecifier = new ImageTypeSpecifier(image);
            IIOMetadata metadata = imageWriter.getDefaultImageMetadata(imageTypeSpecifier, jpegParam);
            Element tree = (Element)metadata.getAsTree("javax_imageio_jpeg_image_1.0");
            Element jfif = (Element)tree.getElementsByTagName("app0JFIF").item(0);
            String dpiString = Integer.toString(dpi);
            jfif.setAttribute("Xdensity", dpiString);
            jfif.setAttribute("Ydensity", dpiString);
            jfif.setAttribute("resUnits", "1");
            imageWriter.write(metadata, new IIOImage(image, null, null), jpegParam);
            byte[] byArray = baos.toByteArray();
            return byArray;
        }
        finally {
            if (ios != null) {
                ios.close();
            }
            imageWriter.dispose();
        }
    }

    private static PDColorSpace getColorSpaceFromAWT(BufferedImage awtImage) {
        if (awtImage.getColorModel().getNumComponents() == 1) {
            return PDDeviceGray.INSTANCE;
        }
        ColorSpace awtColorSpace = awtImage.getColorModel().getColorSpace();
        if (awtColorSpace instanceof ICC_ColorSpace && !awtColorSpace.isCS_sRGB()) {
            throw new UnsupportedOperationException("ICC color spaces not implemented");
        }
        switch (awtColorSpace.getType()) {
            case 5: {
                return PDDeviceRGB.INSTANCE;
            }
            case 6: {
                return PDDeviceGray.INSTANCE;
            }
            case 9: {
                return PDDeviceCMYK.INSTANCE;
            }
        }
        throw new UnsupportedOperationException("color space not implemented: " + awtColorSpace.getType());
    }

    private static BufferedImage getColorImage(BufferedImage image) {
        if (!image.getColorModel().hasAlpha()) {
            return image;
        }
        if (image.getColorModel().getColorSpace().getType() != 5) {
            throw new UnsupportedOperationException("only RGB color spaces are implemented");
        }
        BufferedImage rgbImage = new BufferedImage(image.getWidth(), image.getHeight(), 5);
        return new ColorConvertOp(null).filter(image, rgbImage);
    }

    private static class Dimensions {
        private int width;
        private int height;
        private int numComponents;

        private Dimensions() {
        }
    }
}


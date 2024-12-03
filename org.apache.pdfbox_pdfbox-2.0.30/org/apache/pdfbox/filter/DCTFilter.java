/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 */
package org.apache.pdfbox.filter;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import javax.imageio.IIOException;
import javax.imageio.ImageIO;
import javax.imageio.ImageReadParam;
import javax.imageio.ImageReader;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.metadata.IIOMetadataNode;
import javax.imageio.stream.ImageInputStream;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.pdfbox.cos.COSDictionary;
import org.apache.pdfbox.filter.DecodeOptions;
import org.apache.pdfbox.filter.DecodeResult;
import org.apache.pdfbox.filter.Filter;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

final class DCTFilter
extends Filter {
    private static final Log LOG = LogFactory.getLog(DCTFilter.class);
    private static final int POS_TRANSFORM = 11;
    private static final String ADOBE = "Adobe";
    private static XPathExpression xPathExpression;

    DCTFilter() {
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public DecodeResult decode(InputStream encoded, OutputStream decoded, COSDictionary parameters, int index, DecodeOptions options) throws IOException {
        ImageReader reader = DCTFilter.findImageReader("JPEG", "a suitable JAI I/O image filter is not installed");
        ImageInputStream iis = null;
        try {
            Raster raster;
            iis = ImageIO.createImageInputStream(encoded);
            if (iis.read() != 10) {
                iis.seek(0L);
            }
            reader.setInput(iis);
            ImageReadParam irp = reader.getDefaultReadParam();
            irp.setSourceSubsampling(options.getSubsamplingX(), options.getSubsamplingY(), options.getSubsamplingOffsetX(), options.getSubsamplingOffsetY());
            irp.setSourceRegion(options.getSourceRegion());
            options.setFilterSubsampled(true);
            String numChannels = this.getNumChannels(reader);
            ImageIO.setUseCache(false);
            if ("3".equals(numChannels) || numChannels.isEmpty()) {
                try {
                    BufferedImage image = reader.read(0, irp);
                    raster = image.getRaster();
                }
                catch (IIOException e) {
                    raster = reader.readRaster(0, irp);
                }
            } else {
                raster = reader.readRaster(0, irp);
            }
            if (raster.getNumBands() == 4) {
                Integer transform;
                try {
                    transform = this.getAdobeTransform(reader.getImageMetadata(0));
                }
                catch (IIOException e) {
                    transform = this.getAdobeTransformByBruteForce(iis);
                }
                catch (NegativeArraySizeException e) {
                    transform = this.getAdobeTransformByBruteForce(iis);
                }
                int colorTransform = transform != null ? transform : 0;
                switch (colorTransform) {
                    case 0: {
                        break;
                    }
                    case 1: {
                        raster = this.fromYCbCrtoCMYK(raster);
                        break;
                    }
                    case 2: {
                        raster = this.fromYCCKtoCMYK(raster);
                        break;
                    }
                    default: {
                        throw new IllegalArgumentException("Unknown colorTransform");
                    }
                }
            } else if (raster.getNumBands() == 3) {
                raster = this.fromBGRtoRGB(raster);
            }
            DataBufferByte dataBuffer = (DataBufferByte)raster.getDataBuffer();
            decoded.write(dataBuffer.getData());
        }
        finally {
            if (iis != null) {
                iis.close();
            }
            reader.dispose();
        }
        return new DecodeResult(parameters);
    }

    @Override
    public DecodeResult decode(InputStream encoded, OutputStream decoded, COSDictionary parameters, int index) throws IOException {
        return this.decode(encoded, decoded, parameters, index, DecodeOptions.DEFAULT);
    }

    private Integer getAdobeTransform(IIOMetadata metadata) {
        int app14AdobeNodeListLength;
        Element tree = (Element)metadata.getAsTree("javax_imageio_jpeg_image_1.0");
        Element markerSequence = (Element)tree.getElementsByTagName("markerSequence").item(0);
        NodeList app14AdobeNodeList = markerSequence.getElementsByTagName("app14Adobe");
        if (app14AdobeNodeList != null && (app14AdobeNodeListLength = app14AdobeNodeList.getLength()) > 0) {
            if (app14AdobeNodeListLength > 1) {
                LOG.warn((Object)"app14Adobe entry appears several times, using the last one");
            }
            Element adobe = (Element)app14AdobeNodeList.item(app14AdobeNodeListLength - 1);
            return Integer.valueOf(adobe.getAttribute("transform"));
        }
        try {
            String value = xPathExpression.evaluate(metadata.getAsTree("javax_imageio_1.0"));
            if ("YCbCr".equals(value)) {
                return 1;
            }
            if ("YCCK".equals(value)) {
                return 2;
            }
        }
        catch (XPathExpressionException ex) {
            return 0;
        }
        return 0;
    }

    private int getAdobeTransformByBruteForce(ImageInputStream iis) throws IOException {
        int by;
        int a = 0;
        iis.seek(0L);
        while ((by = iis.read()) != -1) {
            if (ADOBE.charAt(a) == by) {
                byte[] app14;
                if (++a != ADOBE.length()) continue;
                a = 0;
                long afterAdobePos = iis.getStreamPosition();
                iis.seek(afterAdobePos - 9L);
                int tag = iis.readUnsignedShort();
                if (tag != 65518) {
                    iis.seek(afterAdobePos);
                    continue;
                }
                int len = iis.readUnsignedShort();
                if (len < 12 || iis.read(app14 = new byte[Math.max(len, 12)]) < 12) continue;
                return app14[11];
            }
            a = 0;
        }
        return 0;
    }

    private WritableRaster fromYCCKtoCMYK(Raster raster) {
        WritableRaster writableRaster = raster.createCompatibleWritableRaster();
        int[] value = new int[4];
        int height = raster.getHeight();
        for (int y = 0; y < height; ++y) {
            int width = raster.getWidth();
            for (int x = 0; x < width; ++x) {
                raster.getPixel(x, y, value);
                float Y = value[0];
                float Cb = value[1];
                float Cr = value[2];
                float K = value[3];
                int r = this.clamp(Y + 1.402f * Cr - 179.456f);
                int g = this.clamp(Y - 0.34414f * Cb - 0.71414f * Cr + 135.45984f);
                int b = this.clamp(Y + 1.772f * Cb - 226.816f);
                int cyan = 255 - r;
                int magenta = 255 - g;
                int yellow = 255 - b;
                value[0] = cyan;
                value[1] = magenta;
                value[2] = yellow;
                value[3] = (int)K;
                writableRaster.setPixel(x, y, value);
            }
        }
        return writableRaster;
    }

    private WritableRaster fromYCbCrtoCMYK(Raster raster) {
        WritableRaster writableRaster = raster.createCompatibleWritableRaster();
        int[] value = new int[4];
        int height = raster.getHeight();
        for (int y = 0; y < height; ++y) {
            int width = raster.getWidth();
            for (int x = 0; x < width; ++x) {
                raster.getPixel(x, y, value);
                float Y = value[0];
                float Cb = value[1];
                float Cr = value[2];
                float K = value[3];
                int r = this.clamp(1.164f * (Y - 16.0f) + 1.596f * (Cr - 128.0f));
                int g = this.clamp(1.164f * (Y - 16.0f) + -0.392f * (Cb - 128.0f) + -0.813f * (Cr - 128.0f));
                int b = this.clamp(1.164f * (Y - 16.0f) + 2.017f * (Cb - 128.0f));
                int cyan = 255 - r;
                int magenta = 255 - g;
                int yellow = 255 - b;
                value[0] = cyan;
                value[1] = magenta;
                value[2] = yellow;
                value[3] = (int)K;
                writableRaster.setPixel(x, y, value);
            }
        }
        return writableRaster;
    }

    private WritableRaster fromBGRtoRGB(Raster raster) {
        WritableRaster writableRaster = raster.createCompatibleWritableRaster();
        int width = raster.getWidth();
        int height = raster.getHeight();
        int w3 = width * 3;
        int[] tab = new int[w3];
        for (int y = 0; y < height; ++y) {
            raster.getPixels(0, y, width, 1, tab);
            for (int off = 0; off < w3; off += 3) {
                int tmp = tab[off];
                tab[off] = tab[off + 2];
                tab[off + 2] = tmp;
            }
            writableRaster.setPixels(0, y, width, 1, tab);
        }
        return writableRaster;
    }

    private String getNumChannels(ImageReader reader) {
        try {
            IIOMetadata imageMetadata = reader.getImageMetadata(0);
            if (imageMetadata == null) {
                return "";
            }
            IIOMetadataNode metaTree = (IIOMetadataNode)imageMetadata.getAsTree("javax_imageio_1.0");
            Element numChannelsItem = (Element)metaTree.getElementsByTagName("NumChannels").item(0);
            if (numChannelsItem == null) {
                return "";
            }
            return numChannelsItem.getAttribute("value");
        }
        catch (IOException e) {
            return "";
        }
        catch (NegativeArraySizeException e) {
            return "";
        }
    }

    private int clamp(float value) {
        return (int)(value < 0.0f ? 0.0f : (value > 255.0f ? 255.0f : value));
    }

    @Override
    protected void encode(InputStream input, OutputStream encoded, COSDictionary parameters) throws IOException {
        throw new UnsupportedOperationException("DCTFilter encoding not implemented, use the JPEGFactory methods instead");
    }

    static {
        try {
            xPathExpression = XPathFactory.newInstance().newXPath().compile("Chroma/ColorSpaceType/@name");
        }
        catch (XPathExpressionException ex) {
            LOG.error((Object)ex.getMessage(), (Throwable)ex);
        }
    }
}


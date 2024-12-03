/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 */
package org.apache.pdfbox.filter;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.color.ColorSpace;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.awt.image.DataBufferUShort;
import java.awt.image.IndexColorModel;
import java.awt.image.MultiPixelPackedSampleModel;
import java.awt.image.WritableRaster;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import javax.imageio.ImageReadParam;
import javax.imageio.ImageReader;
import javax.imageio.stream.MemoryCacheImageInputStream;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.pdfbox.cos.COSDictionary;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.filter.DecodeOptions;
import org.apache.pdfbox.filter.DecodeResult;
import org.apache.pdfbox.filter.Filter;
import org.apache.pdfbox.pdmodel.graphics.color.PDJPXColorSpace;

public final class JPXFilter
extends Filter {
    private static final Log LOG = LogFactory.getLog(JPXFilter.class);

    @Override
    public DecodeResult decode(InputStream encoded, OutputStream decoded, COSDictionary parameters, int index, DecodeOptions options) throws IOException {
        DecodeResult result = new DecodeResult(new COSDictionary());
        result.getParameters().addAll(parameters);
        BufferedImage image = this.readJPX(encoded, options, result);
        WritableRaster raster = image.getRaster();
        switch (raster.getDataBuffer().getDataType()) {
            case 0: {
                DataBufferByte byteBuffer = (DataBufferByte)raster.getDataBuffer();
                decoded.write(byteBuffer.getData());
                return result;
            }
            case 1: {
                DataBufferUShort wordBuffer = (DataBufferUShort)raster.getDataBuffer();
                for (short w : wordBuffer.getData()) {
                    decoded.write(w >> 8);
                    decoded.write(w);
                }
                return result;
            }
            case 3: {
                int[] ar = new int[raster.getNumBands()];
                for (int y = 0; y < image.getHeight(); ++y) {
                    for (int x = 0; x < image.getWidth(); ++x) {
                        raster.getPixel(x, y, ar);
                        for (int i = 0; i < ar.length; ++i) {
                            decoded.write(ar[i]);
                        }
                    }
                }
                return result;
            }
        }
        throw new IOException("Data type " + raster.getDataBuffer().getDataType() + " not implemented");
    }

    @Override
    public DecodeResult decode(InputStream encoded, OutputStream decoded, COSDictionary parameters, int index) throws IOException {
        return this.decode(encoded, decoded, parameters, index, DecodeOptions.DEFAULT);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private BufferedImage readJPX(InputStream input, DecodeOptions options, DecodeResult result) throws IOException {
        ImageReader reader = JPXFilter.findImageReader("JPEG2000", "Java Advanced Imaging (JAI) Image I/O Tools are not installed");
        MemoryCacheImageInputStream iis = null;
        try {
            BufferedImage image;
            iis = new MemoryCacheImageInputStream(input);
            reader.setInput(iis, true, true);
            ImageReadParam irp = reader.getDefaultReadParam();
            irp.setSourceRegion(options.getSourceRegion());
            irp.setSourceSubsampling(options.getSubsamplingX(), options.getSubsamplingY(), options.getSubsamplingOffsetX(), options.getSubsamplingOffsetY());
            options.setFilterSubsampled(true);
            try {
                image = reader.read(0, irp);
            }
            catch (Exception e) {
                throw new IOException("Could not read JPEG 2000 (JPX) image", e);
            }
            COSDictionary parameters = result.getParameters();
            int bpc = image.getColorModel().getPixelSize() / image.getRaster().getNumBands();
            parameters.setInt(COSName.BITS_PER_COMPONENT, bpc);
            if (!parameters.getBoolean(COSName.IMAGE_MASK, false)) {
                parameters.setItem(COSName.DECODE, null);
            }
            parameters.setInt(COSName.WIDTH, reader.getWidth(0));
            parameters.setInt(COSName.HEIGHT, reader.getHeight(0));
            if (!parameters.containsKey(COSName.COLORSPACE)) {
                if (image.getSampleModel() instanceof MultiPixelPackedSampleModel && image.getColorModel().getPixelSize() == 1 && image.getRaster().getNumBands() == 1 && image.getColorModel() instanceof IndexColorModel) {
                    result.setColorSpace(new PDJPXColorSpace(ColorSpace.getInstance(1003)));
                } else if (image.getTransparency() == 3 && parameters.getInt(COSName.SMASK_IN_DATA) > 0) {
                    LOG.warn((Object)"JPEG2000 SMaskInData is not supported, returning opaque image");
                    BufferedImage bim = new BufferedImage(image.getWidth(), image.getHeight(), 1);
                    Graphics2D g2d = (Graphics2D)bim.getGraphics();
                    g2d.drawImage((Image)image, 0, 0, null);
                    g2d.dispose();
                    image = bim;
                    result.setColorSpace(new PDJPXColorSpace(image.getColorModel().getColorSpace()));
                } else {
                    result.setColorSpace(new PDJPXColorSpace(image.getColorModel().getColorSpace()));
                }
            }
            BufferedImage bufferedImage = image;
            return bufferedImage;
        }
        finally {
            if (iis != null) {
                iis.close();
            }
            reader.dispose();
        }
    }

    @Override
    protected void encode(InputStream input, OutputStream encoded, COSDictionary parameters) throws IOException {
        throw new UnsupportedOperationException("JPX encoding not implemented");
    }
}


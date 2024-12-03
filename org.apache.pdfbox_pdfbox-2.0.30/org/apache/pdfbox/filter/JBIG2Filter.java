/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 */
package org.apache.pdfbox.filter;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferByte;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.SequenceInputStream;
import javax.imageio.ImageIO;
import javax.imageio.ImageReadParam;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.pdfbox.cos.COSDictionary;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.cos.COSStream;
import org.apache.pdfbox.filter.DecodeOptions;
import org.apache.pdfbox.filter.DecodeResult;
import org.apache.pdfbox.filter.Filter;

final class JBIG2Filter
extends Filter {
    private static final Log LOG = LogFactory.getLog(JBIG2Filter.class);
    private static boolean levigoLogged = false;

    JBIG2Filter() {
    }

    private static synchronized void logLevigoDonated() {
        if (!levigoLogged) {
            LOG.info((Object)"The Levigo JBIG2 plugin has been donated to the Apache Foundation");
            LOG.info((Object)"and an improved version is available for download at https://pdfbox.apache.org/download.cgi");
            levigoLogged = true;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public DecodeResult decode(InputStream encoded, OutputStream decoded, COSDictionary parameters, int index, DecodeOptions options) throws IOException {
        block13: {
            ImageReader reader = JBIG2Filter.findImageReader("JBIG2", "jbig2-imageio is not installed");
            if (reader.getClass().getName().contains("levigo")) {
                JBIG2Filter.logLevigoDonated();
            }
            int bits = parameters.getInt(COSName.BITS_PER_COMPONENT, 1);
            COSDictionary params = this.getDecodeParams(parameters, index);
            ImageReadParam irp = reader.getDefaultReadParam();
            irp.setSourceSubsampling(options.getSubsamplingX(), options.getSubsamplingY(), options.getSubsamplingOffsetX(), options.getSubsamplingOffsetY());
            irp.setSourceRegion(options.getSourceRegion());
            options.setFilterSubsampled(true);
            COSStream globals = null;
            if (params != null) {
                globals = (COSStream)params.getDictionaryObject(COSName.JBIG2_GLOBALS);
            }
            ImageInputStream iis = null;
            try {
                DataBuffer dBuf;
                BufferedImage image;
                if (globals != null) {
                    iis = ImageIO.createImageInputStream(new SequenceInputStream(globals.createInputStream(), encoded));
                    reader.setInput(iis);
                } else {
                    iis = ImageIO.createImageInputStream(encoded);
                    reader.setInput(iis);
                }
                try {
                    image = reader.read(0, irp);
                }
                catch (Exception e) {
                    throw new IOException("Could not read JBIG2 image", e);
                }
                if (image.getColorModel().getPixelSize() != bits) {
                    if (bits != 1) {
                        LOG.warn((Object)"Attempting to handle a JBIG2 with more than 1-bit depth");
                    }
                    BufferedImage packedImage = new BufferedImage(image.getWidth(), image.getHeight(), 12);
                    Graphics graphics = packedImage.getGraphics();
                    graphics.drawImage(image, 0, 0, null);
                    graphics.dispose();
                    image = packedImage;
                }
                if ((dBuf = image.getData().getDataBuffer()).getDataType() == 0) {
                    decoded.write(((DataBufferByte)dBuf).getData());
                    break block13;
                }
                throw new IOException("Unexpected image buffer type");
            }
            finally {
                if (iis != null) {
                    iis.close();
                }
                reader.dispose();
            }
        }
        return new DecodeResult(parameters);
    }

    @Override
    public DecodeResult decode(InputStream encoded, OutputStream decoded, COSDictionary parameters, int index) throws IOException {
        return this.decode(encoded, decoded, parameters, index, DecodeOptions.DEFAULT);
    }

    @Override
    protected void encode(InputStream input, OutputStream encoded, COSDictionary parameters) throws IOException {
        throw new UnsupportedOperationException("JBIG2 encoding not implemented");
    }
}


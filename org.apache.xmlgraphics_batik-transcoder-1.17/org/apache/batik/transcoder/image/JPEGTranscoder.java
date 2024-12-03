/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.batik.ext.awt.image.spi.ImageWriter
 *  org.apache.batik.ext.awt.image.spi.ImageWriterParams
 *  org.apache.batik.ext.awt.image.spi.ImageWriterRegistry
 */
package org.apache.batik.transcoder.image;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.IOException;
import java.io.OutputStream;
import org.apache.batik.ext.awt.image.spi.ImageWriter;
import org.apache.batik.ext.awt.image.spi.ImageWriterParams;
import org.apache.batik.ext.awt.image.spi.ImageWriterRegistry;
import org.apache.batik.transcoder.TranscoderException;
import org.apache.batik.transcoder.TranscoderOutput;
import org.apache.batik.transcoder.TranscodingHints;
import org.apache.batik.transcoder.image.ImageTranscoder;
import org.apache.batik.transcoder.image.resources.Messages;

public class JPEGTranscoder
extends ImageTranscoder {
    public static final TranscodingHints.Key KEY_QUALITY = new QualityKey();

    public JPEGTranscoder() {
        this.hints.put(ImageTranscoder.KEY_BACKGROUND_COLOR, Color.white);
    }

    @Override
    public BufferedImage createImage(int width, int height) {
        return new BufferedImage(width, height, 1);
    }

    @Override
    public void writeImage(BufferedImage img, TranscoderOutput output) throws TranscoderException {
        OutputStream ostream = output.getOutputStream();
        ostream = new OutputStreamWrapper(ostream);
        try {
            float quality;
            if (this.hints.containsKey(KEY_QUALITY)) {
                quality = ((Float)this.hints.get(KEY_QUALITY)).floatValue();
            } else {
                TranscoderException te = new TranscoderException(Messages.formatMessage("jpeg.unspecifiedQuality", null));
                this.handler.error(te);
                quality = 0.75f;
            }
            ImageWriter writer = ImageWriterRegistry.getInstance().getWriterFor("image/jpeg");
            ImageWriterParams params = new ImageWriterParams();
            params.setJPEGQuality(quality, true);
            float PixSzMM = this.userAgent.getPixelUnitToMillimeter();
            int PixSzInch = (int)(25.4 / (double)PixSzMM + 0.5);
            params.setResolution(PixSzInch);
            writer.writeImage((RenderedImage)img, ostream, params);
            ostream.flush();
        }
        catch (IOException ex) {
            throw new TranscoderException(ex);
        }
    }

    private static class OutputStreamWrapper
    extends OutputStream {
        OutputStream os;

        OutputStreamWrapper(OutputStream os) {
            this.os = os;
        }

        @Override
        public void close() throws IOException {
            if (this.os == null) {
                return;
            }
            try {
                this.os.close();
            }
            catch (IOException ioe) {
                this.os = null;
            }
        }

        @Override
        public void flush() throws IOException {
            if (this.os == null) {
                return;
            }
            try {
                this.os.flush();
            }
            catch (IOException ioe) {
                this.os = null;
            }
        }

        @Override
        public void write(byte[] b) throws IOException {
            if (this.os == null) {
                return;
            }
            try {
                this.os.write(b);
            }
            catch (IOException ioe) {
                this.os = null;
            }
        }

        @Override
        public void write(byte[] b, int off, int len) throws IOException {
            if (this.os == null) {
                return;
            }
            try {
                this.os.write(b, off, len);
            }
            catch (IOException ioe) {
                this.os = null;
            }
        }

        @Override
        public void write(int b) throws IOException {
            if (this.os == null) {
                return;
            }
            try {
                this.os.write(b);
            }
            catch (IOException ioe) {
                this.os = null;
            }
        }
    }

    private static class QualityKey
    extends TranscodingHints.Key {
        private QualityKey() {
        }

        @Override
        public boolean isCompatibleValue(Object v) {
            if (v instanceof Float) {
                float q = ((Float)v).floatValue();
                return q > 0.0f && q <= 1.0f;
            }
            return false;
        }
    }
}


/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.batik.ext.awt.image.rendered.IndexImage
 *  org.apache.batik.ext.awt.image.spi.ImageWriter
 *  org.apache.batik.ext.awt.image.spi.ImageWriterParams
 *  org.apache.batik.ext.awt.image.spi.ImageWriterRegistry
 *  org.apache.batik.transcoder.TranscoderException
 *  org.apache.batik.transcoder.TranscoderOutput
 *  org.apache.batik.transcoder.TranscodingHints
 *  org.apache.batik.transcoder.image.PNGTranscoder
 *  org.apache.batik.transcoder.image.PNGTranscoder$WriteAdapter
 */
package org.apache.batik.ext.awt.image.codec.imageio;

import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.IOException;
import java.io.OutputStream;
import org.apache.batik.ext.awt.image.rendered.IndexImage;
import org.apache.batik.ext.awt.image.spi.ImageWriter;
import org.apache.batik.ext.awt.image.spi.ImageWriterParams;
import org.apache.batik.ext.awt.image.spi.ImageWriterRegistry;
import org.apache.batik.transcoder.TranscoderException;
import org.apache.batik.transcoder.TranscoderOutput;
import org.apache.batik.transcoder.TranscodingHints;
import org.apache.batik.transcoder.image.PNGTranscoder;

public class PNGTranscoderImageIOWriteAdapter
implements PNGTranscoder.WriteAdapter {
    public void writeImage(PNGTranscoder transcoder, BufferedImage img, TranscoderOutput output) throws TranscoderException {
        TranscodingHints hints = transcoder.getTranscodingHints();
        int n = -1;
        if (hints.containsKey((Object)PNGTranscoder.KEY_INDEXED) && ((n = ((Integer)hints.get((Object)PNGTranscoder.KEY_INDEXED)).intValue()) == 1 || n == 2 || n == 4 || n == 8)) {
            img = IndexImage.getIndexedImage((BufferedImage)img, (int)(1 << n));
        }
        ImageWriter writer = ImageWriterRegistry.getInstance().getWriterFor("image/png");
        ImageWriterParams params = new ImageWriterParams();
        float PixSzMM = transcoder.getUserAgent().getPixelUnitToMillimeter();
        int PixSzInch = (int)(25.4 / (double)PixSzMM + 0.5);
        params.setResolution(PixSzInch);
        try {
            OutputStream ostream = output.getOutputStream();
            writer.writeImage((RenderedImage)img, ostream, params);
            ostream.flush();
        }
        catch (IOException ex) {
            throw new TranscoderException((Exception)ex);
        }
    }
}


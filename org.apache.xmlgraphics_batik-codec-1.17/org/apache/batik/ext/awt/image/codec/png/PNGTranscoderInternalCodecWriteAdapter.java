/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.batik.ext.awt.image.rendered.IndexImage
 *  org.apache.batik.transcoder.TranscoderException
 *  org.apache.batik.transcoder.TranscoderOutput
 *  org.apache.batik.transcoder.TranscodingHints
 *  org.apache.batik.transcoder.image.PNGTranscoder
 *  org.apache.batik.transcoder.image.PNGTranscoder$WriteAdapter
 */
package org.apache.batik.ext.awt.image.codec.png;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.OutputStream;
import org.apache.batik.ext.awt.image.codec.png.PNGEncodeParam;
import org.apache.batik.ext.awt.image.codec.png.PNGImageEncoder;
import org.apache.batik.ext.awt.image.rendered.IndexImage;
import org.apache.batik.transcoder.TranscoderException;
import org.apache.batik.transcoder.TranscoderOutput;
import org.apache.batik.transcoder.TranscodingHints;
import org.apache.batik.transcoder.image.PNGTranscoder;

public class PNGTranscoderInternalCodecWriteAdapter
implements PNGTranscoder.WriteAdapter {
    public void writeImage(PNGTranscoder transcoder, BufferedImage img, TranscoderOutput output) throws TranscoderException {
        PNGEncodeParam params;
        TranscodingHints hints = transcoder.getTranscodingHints();
        int n = -1;
        if (hints.containsKey((Object)PNGTranscoder.KEY_INDEXED) && ((n = ((Integer)hints.get((Object)PNGTranscoder.KEY_INDEXED)).intValue()) == 1 || n == 2 || n == 4 || n == 8)) {
            img = IndexImage.getIndexedImage((BufferedImage)img, (int)(1 << n));
        }
        if ((params = PNGEncodeParam.getDefaultEncodeParam(img)) instanceof PNGEncodeParam.RGB) {
            ((PNGEncodeParam.RGB)params).setBackgroundRGB(new int[]{255, 255, 255});
        }
        if (hints.containsKey((Object)PNGTranscoder.KEY_GAMMA)) {
            float gamma = ((Float)hints.get((Object)PNGTranscoder.KEY_GAMMA)).floatValue();
            if (gamma > 0.0f) {
                params.setGamma(gamma);
            }
            params.setChromaticity(PNGTranscoder.DEFAULT_CHROMA);
        } else {
            params.setSRGBIntent(0);
        }
        float PixSzMM = transcoder.getUserAgent().getPixelUnitToMillimeter();
        int numPix = (int)((double)(1000.0f / PixSzMM) + 0.5);
        params.setPhysicalDimension(numPix, numPix, 1);
        try {
            OutputStream ostream = output.getOutputStream();
            PNGImageEncoder pngEncoder = new PNGImageEncoder(ostream, params);
            pngEncoder.encode(img);
            ostream.flush();
        }
        catch (IOException ex) {
            throw new TranscoderException((Exception)ex);
        }
    }
}


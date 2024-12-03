/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.batik.bridge.UserAgent
 */
package org.apache.batik.transcoder.image;

import java.awt.image.BufferedImage;
import java.awt.image.SinglePixelPackedSampleModel;
import java.lang.reflect.InvocationTargetException;
import org.apache.batik.bridge.UserAgent;
import org.apache.batik.transcoder.TranscoderException;
import org.apache.batik.transcoder.TranscoderOutput;
import org.apache.batik.transcoder.TranscodingHints;
import org.apache.batik.transcoder.image.ImageTranscoder;
import org.apache.batik.transcoder.image.PNGTranscoder;
import org.apache.batik.transcoder.keys.StringKey;

public class TIFFTranscoder
extends ImageTranscoder {
    public static final TranscodingHints.Key KEY_FORCE_TRANSPARENT_WHITE = ImageTranscoder.KEY_FORCE_TRANSPARENT_WHITE;
    public static final TranscodingHints.Key KEY_COMPRESSION_METHOD = new StringKey();

    public TIFFTranscoder() {
        this.hints.put(KEY_FORCE_TRANSPARENT_WHITE, Boolean.FALSE);
    }

    public UserAgent getUserAgent() {
        return this.userAgent;
    }

    @Override
    public BufferedImage createImage(int width, int height) {
        return new BufferedImage(width, height, 2);
    }

    private WriteAdapter getWriteAdapter(String className) {
        try {
            Class<?> clazz = Class.forName(className);
            WriteAdapter adapter = (WriteAdapter)clazz.getDeclaredConstructor(new Class[0]).newInstance(new Object[0]);
            return adapter;
        }
        catch (ClassNotFoundException e) {
            return null;
        }
        catch (InstantiationException e) {
            return null;
        }
        catch (IllegalAccessException e) {
            return null;
        }
        catch (NoSuchMethodException e) {
            return null;
        }
        catch (InvocationTargetException e) {
            return null;
        }
    }

    @Override
    public void writeImage(BufferedImage img, TranscoderOutput output) throws TranscoderException {
        WriteAdapter adapter;
        boolean forceTransparentWhite = false;
        if (this.hints.containsKey(PNGTranscoder.KEY_FORCE_TRANSPARENT_WHITE)) {
            forceTransparentWhite = (Boolean)this.hints.get(PNGTranscoder.KEY_FORCE_TRANSPARENT_WHITE);
        }
        if (forceTransparentWhite) {
            SinglePixelPackedSampleModel sppsm = (SinglePixelPackedSampleModel)img.getSampleModel();
            this.forceTransparentWhite(img, sppsm);
        }
        if ((adapter = this.getWriteAdapter("org.apache.batik.ext.awt.image.codec.tiff.TIFFTranscoderInternalCodecWriteAdapter")) == null) {
            adapter = this.getWriteAdapter("org.apache.batik.ext.awt.image.codec.imageio.TIFFTranscoderImageIOWriteAdapter");
        }
        if (adapter == null) {
            throw new TranscoderException("Could not write TIFF file because no WriteAdapter is availble");
        }
        adapter.writeImage(this, img, output);
    }

    public static interface WriteAdapter {
        public void writeImage(TIFFTranscoder var1, BufferedImage var2, TranscoderOutput var3) throws TranscoderException;
    }
}


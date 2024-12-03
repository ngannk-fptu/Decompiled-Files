/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.batik.bridge.UserAgent
 */
package org.apache.batik.transcoder.image;

import java.awt.image.BufferedImage;
import java.awt.image.SinglePixelPackedSampleModel;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import org.apache.batik.bridge.UserAgent;
import org.apache.batik.transcoder.TranscoderException;
import org.apache.batik.transcoder.TranscoderOutput;
import org.apache.batik.transcoder.TranscodingHints;
import org.apache.batik.transcoder.image.ImageTranscoder;
import org.apache.batik.transcoder.image.resources.Messages;
import org.apache.batik.transcoder.keys.FloatKey;
import org.apache.batik.transcoder.keys.IntegerKey;

public class PNGTranscoder
extends ImageTranscoder {
    public static final TranscodingHints.Key KEY_GAMMA = new FloatKey();
    public static final float[] DEFAULT_CHROMA = new float[]{0.3127f, 0.329f, 0.64f, 0.33f, 0.3f, 0.6f, 0.15f, 0.06f};
    public static final TranscodingHints.Key KEY_INDEXED = new IntegerKey();

    public PNGTranscoder() {
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
        OutputStream ostream = output.getOutputStream();
        if (ostream == null) {
            throw new TranscoderException(Messages.formatMessage("png.badoutput", null));
        }
        boolean forceTransparentWhite = false;
        if (this.hints.containsKey(KEY_FORCE_TRANSPARENT_WHITE)) {
            forceTransparentWhite = (Boolean)this.hints.get(KEY_FORCE_TRANSPARENT_WHITE);
        }
        if (forceTransparentWhite) {
            SinglePixelPackedSampleModel sppsm = (SinglePixelPackedSampleModel)img.getSampleModel();
            this.forceTransparentWhite(img, sppsm);
        }
        if ((adapter = this.getWriteAdapter("org.apache.batik.ext.awt.image.codec.png.PNGTranscoderInternalCodecWriteAdapter")) == null) {
            adapter = this.getWriteAdapter("org.apache.batik.transcoder.image.PNGTranscoderImageIOWriteAdapter");
        }
        if (adapter == null) {
            throw new TranscoderException("Could not write PNG file because no WriteAdapter is availble");
        }
        adapter.writeImage(this, img, output);
    }

    public static interface WriteAdapter {
        public void writeImage(PNGTranscoder var1, BufferedImage var2, TranscoderOutput var3) throws TranscoderException;
    }
}


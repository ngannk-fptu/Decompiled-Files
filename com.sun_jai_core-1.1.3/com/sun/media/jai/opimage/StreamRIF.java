/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.sun.media.jai.codec.ImageCodec
 *  com.sun.media.jai.codec.ImageDecodeParam
 *  com.sun.media.jai.codec.ImageDecoder
 *  com.sun.media.jai.codec.SeekableStream
 */
package com.sun.media.jai.opimage;

import com.sun.media.jai.codec.ImageCodec;
import com.sun.media.jai.codec.ImageDecodeParam;
import com.sun.media.jai.codec.ImageDecoder;
import com.sun.media.jai.codec.SeekableStream;
import com.sun.media.jai.opimage.JaiI18N;
import com.sun.media.jai.opimage.RIFUtil;
import com.sun.media.jai.util.DisposableNullOpImage;
import com.sun.media.jai.util.ImageUtil;
import java.awt.RenderingHints;
import java.awt.image.RenderedImage;
import java.awt.image.renderable.ParameterBlock;
import java.awt.image.renderable.RenderedImageFactory;
import java.io.IOException;
import java.util.Map;
import javax.media.jai.ImageLayout;
import javax.media.jai.JAI;
import javax.media.jai.OperationRegistry;
import javax.media.jai.TileCache;
import javax.media.jai.registry.RIFRegistry;
import javax.media.jai.util.ImagingException;
import javax.media.jai.util.ImagingListener;

public class StreamRIF
implements RenderedImageFactory {
    public RenderedImage create(ParameterBlock paramBlock, RenderingHints renderHints) {
        ImagingListener listener = ImageUtil.getImagingListener(renderHints);
        SeekableStream src = (SeekableStream)paramBlock.getObjectParameter(0);
        try {
            src.seek(0L);
        }
        catch (IOException e) {
            listener.errorOccurred(JaiI18N.getString("StreamRIF0"), e, this, false);
            return null;
        }
        ImageDecodeParam param = null;
        if (paramBlock.getNumParameters() > 1) {
            param = (ImageDecodeParam)paramBlock.getObjectParameter(1);
        }
        String[] names = ImageCodec.getDecoderNames((SeekableStream)src);
        OperationRegistry registry = JAI.getDefaultInstance().getOperationRegistry();
        int bound = 2;
        ImageLayout layout = RIFUtil.getImageLayoutHint(renderHints);
        if (renderHints != null) {
            RenderingHints.Key key = JAI.KEY_OPERATION_REGISTRY;
            if (renderHints.containsKey(key)) {
                registry = (OperationRegistry)renderHints.get(key);
            }
            if (renderHints.containsKey(key = JAI.KEY_OPERATION_BOUND)) {
                bound = (Integer)renderHints.get(key);
            }
        }
        for (int i = 0; i < names.length; ++i) {
            RenderedImage im;
            RenderedImageFactory rif = null;
            try {
                rif = RIFRegistry.get(registry, names[i]);
            }
            catch (IllegalArgumentException iae) {
                // empty catch block
            }
            if (rif == null || (im = RIFRegistry.create(registry, names[i], paramBlock, renderHints)) == null) continue;
            return im;
        }
        boolean canAttemptRecovery = src.canSeekBackwards();
        long streamPosition = Long.MIN_VALUE;
        if (canAttemptRecovery) {
            try {
                streamPosition = src.getFilePointer();
            }
            catch (IOException ioe) {
                listener.errorOccurred(JaiI18N.getString("StreamRIF1"), ioe, this, false);
                canAttemptRecovery = false;
            }
        }
        for (int i = 0; i < names.length; ++i) {
            ImageDecoder dec = ImageCodec.createImageDecoder((String)names[i], (SeekableStream)src, (ImageDecodeParam)param);
            RenderedImage im = null;
            try {
                im = dec.decodeAsRenderedImage();
            }
            catch (OutOfMemoryError memoryError) {
                if (canAttemptRecovery) {
                    TileCache cache = RIFUtil.getTileCacheHint(renderHints);
                    if (cache != null) {
                        cache.flush();
                    }
                    System.gc();
                    try {
                        src.seek(streamPosition);
                        im = dec.decodeAsRenderedImage();
                    }
                    catch (IOException ioe) {
                        listener.errorOccurred(JaiI18N.getString("StreamRIF2"), ioe, this, false);
                        im = null;
                    }
                } else {
                    String message = JaiI18N.getString("CodecRIFUtil0");
                    listener.errorOccurred(message, new ImagingException(message, memoryError), this, false);
                }
            }
            catch (IOException e) {
                listener.errorOccurred(JaiI18N.getString("StreamRIF2"), e, this, false);
                im = null;
            }
            if (im == null) continue;
            return new DisposableNullOpImage(im, layout, (Map)renderHints, bound);
        }
        return null;
    }
}


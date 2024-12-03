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
import java.io.IOException;
import java.util.Map;
import javax.media.jai.ImageLayout;
import javax.media.jai.JAI;
import javax.media.jai.OpImage;
import javax.media.jai.TileCache;
import javax.media.jai.util.ImagingException;
import javax.media.jai.util.ImagingListener;

public class CodecRIFUtil {
    static /* synthetic */ Class class$com$sun$media$jai$opimage$CodecRIFUtil;

    private CodecRIFUtil() {
    }

    public static RenderedImage create(String type, ParameterBlock paramBlock, RenderingHints renderHints) {
        ImagingListener listener = ImageUtil.getImagingListener(renderHints);
        SeekableStream source = (SeekableStream)paramBlock.getObjectParameter(0);
        ImageDecodeParam param = null;
        if (paramBlock.getNumParameters() > 1) {
            param = (ImageDecodeParam)paramBlock.getObjectParameter(1);
        }
        int page = 0;
        if (paramBlock.getNumParameters() > 2) {
            page = paramBlock.getIntParameter(2);
        }
        ImageDecoder dec = ImageCodec.createImageDecoder((String)type, (SeekableStream)source, (ImageDecodeParam)param);
        try {
            RenderingHints.Key key;
            int bound = 2;
            ImageLayout layout = RIFUtil.getImageLayoutHint(renderHints);
            if (renderHints != null && renderHints.containsKey(key = JAI.KEY_OPERATION_BOUND)) {
                bound = (Integer)renderHints.get(key);
            }
            boolean canAttemptRecovery = source.canSeekBackwards();
            long streamPosition = Long.MIN_VALUE;
            if (canAttemptRecovery) {
                try {
                    streamPosition = source.getFilePointer();
                }
                catch (IOException ioe) {
                    listener.errorOccurred(JaiI18N.getString("StreamRIF1"), ioe, class$com$sun$media$jai$opimage$CodecRIFUtil == null ? (class$com$sun$media$jai$opimage$CodecRIFUtil = CodecRIFUtil.class$("com.sun.media.jai.opimage.CodecRIFUtil")) : class$com$sun$media$jai$opimage$CodecRIFUtil, false);
                    canAttemptRecovery = false;
                }
            }
            OpImage image = null;
            try {
                image = new DisposableNullOpImage(dec.decodeAsRenderedImage(page), layout, (Map)renderHints, bound);
            }
            catch (OutOfMemoryError memoryError) {
                if (canAttemptRecovery) {
                    TileCache cache;
                    TileCache tileCache = cache = image != null ? image.getTileCache() : RIFUtil.getTileCacheHint(renderHints);
                    if (cache != null) {
                        cache.flush();
                    }
                    System.gc();
                    source.seek(streamPosition);
                    image = new DisposableNullOpImage(dec.decodeAsRenderedImage(page), layout, (Map)renderHints, bound);
                }
                String message = JaiI18N.getString("CodecRIFUtil0");
                listener.errorOccurred(message, new ImagingException(message, memoryError), class$com$sun$media$jai$opimage$CodecRIFUtil == null ? (class$com$sun$media$jai$opimage$CodecRIFUtil = CodecRIFUtil.class$("com.sun.media.jai.opimage.CodecRIFUtil")) : class$com$sun$media$jai$opimage$CodecRIFUtil, false);
            }
            return image;
        }
        catch (Exception e) {
            listener.errorOccurred(JaiI18N.getString("CodecRIFUtil1"), e, class$com$sun$media$jai$opimage$CodecRIFUtil == null ? (class$com$sun$media$jai$opimage$CodecRIFUtil = CodecRIFUtil.class$("com.sun.media.jai.opimage.CodecRIFUtil")) : class$com$sun$media$jai$opimage$CodecRIFUtil, false);
            return null;
        }
    }

    static /* synthetic */ Class class$(String x0) {
        try {
            return Class.forName(x0);
        }
        catch (ClassNotFoundException x1) {
            throw new NoClassDefFoundError(x1.getMessage());
        }
    }
}


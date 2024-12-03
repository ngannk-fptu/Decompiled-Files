/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.sun.media.jai.codec.ImageDecodeParam
 *  com.sun.media.jai.codec.SeekableStream
 */
package com.sun.media.jai.opimage;

import com.sun.media.jai.codec.ImageDecodeParam;
import com.sun.media.jai.codec.SeekableStream;
import com.sun.media.jai.opimage.JaiI18N;
import com.sun.media.jai.opimage.StreamImage;
import com.sun.media.jai.util.ImageUtil;
import java.awt.RenderingHints;
import java.awt.image.RenderedImage;
import java.awt.image.renderable.ParameterBlock;
import java.awt.image.renderable.RenderedImageFactory;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import javax.media.jai.JAI;
import javax.media.jai.OperationRegistry;
import javax.media.jai.registry.RIFRegistry;
import javax.media.jai.util.ImagingListener;

public class URLRIF
implements RenderedImageFactory {
    public RenderedImage create(ParameterBlock paramBlock, RenderingHints renderHints) {
        try {
            URL url = (URL)paramBlock.getObjectParameter(0);
            InputStream stream = url.openStream();
            SeekableStream src = SeekableStream.wrapInputStream((InputStream)stream, (boolean)true);
            ImageDecodeParam param = null;
            if (paramBlock.getNumParameters() > 1) {
                param = (ImageDecodeParam)paramBlock.getObjectParameter(1);
            }
            ParameterBlock newParamBlock = new ParameterBlock();
            newParamBlock.add(src);
            newParamBlock.add(param);
            RenderingHints.Key key = JAI.KEY_OPERATION_BOUND;
            int bound = 3;
            if (renderHints == null) {
                renderHints = new RenderingHints(key, new Integer(bound));
            } else if (!renderHints.containsKey(key)) {
                renderHints.put(key, new Integer(bound));
            }
            OperationRegistry registry = (OperationRegistry)renderHints.get(JAI.KEY_OPERATION_REGISTRY);
            RenderedImage image = RIFRegistry.create(registry, "stream", newParamBlock, renderHints);
            return image == null ? null : new StreamImage(image, (InputStream)src);
        }
        catch (IOException e) {
            ImagingListener listener = ImageUtil.getImagingListener(renderHints);
            String message = JaiI18N.getString("URLRIF0");
            listener.errorOccurred(message, e, this, false);
            return null;
        }
    }
}


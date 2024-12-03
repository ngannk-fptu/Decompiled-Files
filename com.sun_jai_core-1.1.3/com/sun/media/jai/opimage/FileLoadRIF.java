/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.sun.media.jai.codec.FileSeekableStream
 *  com.sun.media.jai.codec.ImageDecodeParam
 *  com.sun.media.jai.codec.SeekableStream
 */
package com.sun.media.jai.opimage;

import com.sun.media.jai.codec.FileSeekableStream;
import com.sun.media.jai.codec.ImageDecodeParam;
import com.sun.media.jai.codec.SeekableStream;
import com.sun.media.jai.opimage.JaiI18N;
import com.sun.media.jai.opimage.StreamImage;
import com.sun.media.jai.util.ImageUtil;
import java.awt.RenderingHints;
import java.awt.image.RenderedImage;
import java.awt.image.renderable.ParameterBlock;
import java.awt.image.renderable.RenderedImageFactory;
import java.io.FileNotFoundException;
import java.io.InputStream;
import javax.media.jai.JAI;
import javax.media.jai.OperationRegistry;
import javax.media.jai.registry.RIFRegistry;
import javax.media.jai.util.ImagingListener;

public class FileLoadRIF
implements RenderedImageFactory {
    public RenderedImage create(ParameterBlock args, RenderingHints hints) {
        ImagingListener listener = ImageUtil.getImagingListener(hints);
        try {
            FileSeekableStream src;
            block9: {
                String fileName = (String)args.getObjectParameter(0);
                src = null;
                try {
                    src = new FileSeekableStream(fileName);
                }
                catch (FileNotFoundException fnfe) {
                    InputStream is = this.getClass().getClassLoader().getResourceAsStream(fileName);
                    if (is == null) break block9;
                    src = SeekableStream.wrapInputStream((InputStream)is, (boolean)true);
                }
            }
            ImageDecodeParam param = null;
            if (args.getNumParameters() > 1) {
                param = (ImageDecodeParam)args.getObjectParameter(1);
            }
            ParameterBlock newArgs = new ParameterBlock();
            newArgs.add(src);
            newArgs.add(param);
            RenderingHints.Key key = JAI.KEY_OPERATION_BOUND;
            int bound = 2;
            if (hints == null) {
                hints = new RenderingHints(key, new Integer(bound));
            } else if (!hints.containsKey(key)) {
                hints = (RenderingHints)hints.clone();
                hints.put(key, new Integer(bound));
            }
            OperationRegistry registry = (OperationRegistry)hints.get(JAI.KEY_OPERATION_REGISTRY);
            RenderedImage image = RIFRegistry.create(registry, "stream", newArgs, hints);
            return image == null ? null : new StreamImage(image, (InputStream)src);
        }
        catch (FileNotFoundException e) {
            String message = JaiI18N.getString("FileLoadRIF0") + args.getObjectParameter(0);
            listener.errorOccurred(message, e, this, false);
            return null;
        }
        catch (Exception e) {
            String message = JaiI18N.getString("FileLoadRIF1");
            listener.errorOccurred(message, e, this, false);
            return null;
        }
    }
}


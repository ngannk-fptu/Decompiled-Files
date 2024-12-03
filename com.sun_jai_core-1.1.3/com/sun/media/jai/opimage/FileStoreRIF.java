/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.sun.media.jai.codec.ImageEncodeParam
 *  com.sun.media.jai.codec.SeekableOutputStream
 */
package com.sun.media.jai.opimage;

import com.sun.media.jai.codec.ImageEncodeParam;
import com.sun.media.jai.codec.SeekableOutputStream;
import com.sun.media.jai.opimage.JaiI18N;
import com.sun.media.jai.util.ImageUtil;
import java.awt.RenderingHints;
import java.awt.image.RenderedImage;
import java.awt.image.renderable.ParameterBlock;
import java.awt.image.renderable.RenderedImageFactory;
import java.io.BufferedOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import javax.media.jai.JAI;
import javax.media.jai.OperationRegistry;
import javax.media.jai.RenderedImageAdapter;
import javax.media.jai.registry.RIFRegistry;
import javax.media.jai.util.ImagingListener;

public class FileStoreRIF
implements RenderedImageFactory {
    private static String DEFAULT_FORMAT = "tiff";

    public RenderedImage create(ParameterBlock paramBlock, RenderingHints renderHints) {
        ImagingListener listener = ImageUtil.getImagingListener(renderHints);
        String fileName = (String)paramBlock.getObjectParameter(0);
        String format = (String)paramBlock.getObjectParameter(1);
        if (format == null) {
            format = DEFAULT_FORMAT;
        }
        ImageEncodeParam param = null;
        if (paramBlock.getNumParameters() > 2) {
            param = (ImageEncodeParam)paramBlock.getObjectParameter(2);
        }
        BufferedOutputStream stream = null;
        try {
            stream = param == null ? new BufferedOutputStream(new FileOutputStream(fileName)) : new SeekableOutputStream(new RandomAccessFile(fileName, "rw"));
        }
        catch (FileNotFoundException e) {
            String message = JaiI18N.getString("FileLoadRIF0") + fileName;
            listener.errorOccurred(message, e, this, false);
            return null;
        }
        catch (SecurityException e) {
            String message = JaiI18N.getString("FileStoreRIF0");
            listener.errorOccurred(message, e, this, false);
            return null;
        }
        ParameterBlock pb = new ParameterBlock();
        pb.addSource(paramBlock.getSource(0));
        pb.add(stream).add(format).add(param);
        OperationRegistry registry = renderHints == null ? null : (OperationRegistry)renderHints.get(JAI.KEY_OPERATION_REGISTRY);
        FileStoreImage im = new FileStoreImage(RIFRegistry.create(registry, "encode", pb, renderHints), stream);
        return im;
    }

    private class FileStoreImage
    extends RenderedImageAdapter {
        private OutputStream stream;

        public FileStoreImage(RenderedImage image, OutputStream stream) {
            super(image);
            this.stream = stream;
        }

        public void dispose() {
            try {
                this.stream.close();
            }
            catch (IOException iOException) {
                // empty catch block
            }
            super.dispose();
        }
    }
}


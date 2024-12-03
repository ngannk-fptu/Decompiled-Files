/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xmlgraphics.image.loader.impl;

import java.awt.image.RenderedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import javax.imageio.stream.ImageInputStream;
import javax.xml.transform.Source;
import org.apache.xmlgraphics.image.codec.png.PNGDecodeParam;
import org.apache.xmlgraphics.image.codec.png.PNGImageDecoder;
import org.apache.xmlgraphics.image.codec.util.ImageInputStreamSeekableStreamAdapter;
import org.apache.xmlgraphics.image.loader.Image;
import org.apache.xmlgraphics.image.loader.ImageException;
import org.apache.xmlgraphics.image.loader.ImageFlavor;
import org.apache.xmlgraphics.image.loader.ImageInfo;
import org.apache.xmlgraphics.image.loader.ImageSessionContext;
import org.apache.xmlgraphics.image.loader.impl.AbstractImageLoader;
import org.apache.xmlgraphics.image.loader.impl.ImageRendered;
import org.apache.xmlgraphics.image.loader.util.ImageUtil;

public class ImageLoaderPNG
extends AbstractImageLoader {
    @Override
    public Image loadImage(ImageInfo info, Map hints, ImageSessionContext session) throws ImageException, IOException {
        Source src = session.needSource(info.getOriginalURI());
        ImageInputStream imgStream = ImageUtil.needImageInputStream(src);
        ImageInputStreamSeekableStreamAdapter seekStream = new ImageInputStreamSeekableStreamAdapter(imgStream);
        PNGImageDecoder decoder = new PNGImageDecoder((InputStream)seekStream, new PNGDecodeParam());
        RenderedImage image = decoder.decodeAsRenderedImage();
        return new ImageRendered(info, image, null);
    }

    @Override
    public ImageFlavor getTargetFlavor() {
        return ImageFlavor.RENDERED_IMAGE;
    }

    @Override
    public int getUsagePenalty() {
        return 1000;
    }
}


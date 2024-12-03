/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 */
package org.apache.xmlgraphics.image.loader.impl;

import java.io.IOException;
import java.util.Map;
import javax.imageio.stream.ImageInputStream;
import javax.xml.transform.Source;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.xmlgraphics.image.codec.util.ImageInputStreamSeekableStreamAdapter;
import org.apache.xmlgraphics.image.loader.Image;
import org.apache.xmlgraphics.image.loader.ImageException;
import org.apache.xmlgraphics.image.loader.ImageFlavor;
import org.apache.xmlgraphics.image.loader.ImageInfo;
import org.apache.xmlgraphics.image.loader.ImageSessionContext;
import org.apache.xmlgraphics.image.loader.impl.AbstractImageLoader;
import org.apache.xmlgraphics.image.loader.impl.ImageRawPNG;
import org.apache.xmlgraphics.image.loader.impl.PNGFile;
import org.apache.xmlgraphics.image.loader.util.ImageUtil;
import org.apache.xmlgraphics.io.XmlSourceUtil;

public class ImageLoaderRawPNG
extends AbstractImageLoader {
    protected static final Log log = LogFactory.getLog(ImageLoaderRawPNG.class);

    @Override
    public ImageFlavor getTargetFlavor() {
        return ImageFlavor.RAW_PNG;
    }

    @Override
    public Image loadImage(ImageInfo info, Map hints, ImageSessionContext session) throws ImageException, IOException {
        if (!"image/png".equals(info.getMimeType())) {
            throw new IllegalArgumentException("ImageInfo must be from a image with MIME type: image/png");
        }
        Source src = session.needSource(info.getOriginalURI());
        ImageInputStream in = ImageUtil.needImageInputStream(src);
        XmlSourceUtil.removeStreams(src);
        ImageInputStreamSeekableStreamAdapter seekStream = new ImageInputStreamSeekableStreamAdapter(in);
        PNGFile im = new PNGFile(seekStream, info.getOriginalURI());
        ImageRawPNG irpng = im.getImageRawPNG(info);
        return irpng;
    }
}


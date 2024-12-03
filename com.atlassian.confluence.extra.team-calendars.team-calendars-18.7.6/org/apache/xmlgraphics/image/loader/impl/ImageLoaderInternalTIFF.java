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
import org.apache.xmlgraphics.image.codec.tiff.TIFFImage;
import org.apache.xmlgraphics.image.codec.util.ImageInputStreamSeekableStreamAdapter;
import org.apache.xmlgraphics.image.loader.Image;
import org.apache.xmlgraphics.image.loader.ImageException;
import org.apache.xmlgraphics.image.loader.ImageFlavor;
import org.apache.xmlgraphics.image.loader.ImageInfo;
import org.apache.xmlgraphics.image.loader.ImageSessionContext;
import org.apache.xmlgraphics.image.loader.impl.AbstractImageLoader;
import org.apache.xmlgraphics.image.loader.impl.ImageRendered;
import org.apache.xmlgraphics.image.loader.util.ImageUtil;

public class ImageLoaderInternalTIFF
extends AbstractImageLoader {
    protected static final Log log = LogFactory.getLog(ImageLoaderInternalTIFF.class);

    @Override
    public ImageFlavor getTargetFlavor() {
        return ImageFlavor.RENDERED_IMAGE;
    }

    @Override
    public Image loadImage(ImageInfo info, Map hints, ImageSessionContext session) throws ImageException, IOException {
        Source src = session.needSource(info.getOriginalURI());
        ImageInputStream imgStream = ImageUtil.needImageInputStream(src);
        ImageInputStreamSeekableStreamAdapter seekStream = new ImageInputStreamSeekableStreamAdapter(imgStream);
        try {
            TIFFImage img = new TIFFImage(seekStream, null, 0);
            return new ImageRendered(info, img, null);
        }
        catch (RuntimeException e) {
            throw new ImageException("Could not load image with internal TIFF codec", e);
        }
    }

    @Override
    public int getUsagePenalty() {
        return 1000;
    }
}


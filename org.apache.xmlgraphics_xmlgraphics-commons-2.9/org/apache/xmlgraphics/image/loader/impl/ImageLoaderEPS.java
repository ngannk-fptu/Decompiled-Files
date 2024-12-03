/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xmlgraphics.image.loader.impl;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import javax.xml.transform.Source;
import org.apache.xmlgraphics.image.loader.Image;
import org.apache.xmlgraphics.image.loader.ImageException;
import org.apache.xmlgraphics.image.loader.ImageFlavor;
import org.apache.xmlgraphics.image.loader.ImageInfo;
import org.apache.xmlgraphics.image.loader.ImageSessionContext;
import org.apache.xmlgraphics.image.loader.impl.AbstractImageLoader;
import org.apache.xmlgraphics.image.loader.impl.ImageRawEPS;
import org.apache.xmlgraphics.image.loader.impl.PreloaderEPS;
import org.apache.xmlgraphics.io.XmlSourceUtil;
import org.apache.xmlgraphics.util.io.SubInputStream;

public class ImageLoaderEPS
extends AbstractImageLoader {
    @Override
    public ImageFlavor getTargetFlavor() {
        return ImageFlavor.RAW_EPS;
    }

    @Override
    public Image loadImage(ImageInfo info, Map hints, ImageSessionContext session) throws ImageException, IOException {
        if (!"application/postscript".equals(info.getMimeType())) {
            throw new IllegalArgumentException("ImageInfo must be from a image with MIME type: application/postscript");
        }
        Source src = session.needSource(info.getOriginalURI());
        InputStream in = XmlSourceUtil.needInputStream(src);
        XmlSourceUtil.removeStreams(src);
        PreloaderEPS.EPSBinaryFileHeader binaryHeader = (PreloaderEPS.EPSBinaryFileHeader)info.getCustomObjects().get(PreloaderEPS.EPS_BINARY_HEADER);
        if (binaryHeader != null) {
            in.skip(binaryHeader.getPSStart());
            in = new SubInputStream(in, binaryHeader.getPSLength(), true);
        }
        ImageRawEPS epsImage = new ImageRawEPS(info, in);
        return epsImage;
    }
}


/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 */
package org.apache.xmlgraphics.image.loader.impl;

import java.io.IOException;
import java.text.MessageFormat;
import javax.imageio.stream.ImageInputStream;
import javax.xml.transform.Source;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.xmlgraphics.image.codec.tiff.TIFFDirectory;
import org.apache.xmlgraphics.image.codec.tiff.TIFFField;
import org.apache.xmlgraphics.image.loader.ImageContext;
import org.apache.xmlgraphics.image.loader.ImageException;
import org.apache.xmlgraphics.image.loader.ImageInfo;
import org.apache.xmlgraphics.image.loader.ImageSize;
import org.apache.xmlgraphics.image.loader.SubImageNotFoundException;
import org.apache.xmlgraphics.image.loader.impl.AbstractImagePreloader;
import org.apache.xmlgraphics.image.loader.util.ImageUtil;
import org.apache.xmlgraphics.image.loader.util.SeekableStreamAdapter;
import org.apache.xmlgraphics.util.UnitConv;

public class PreloaderTIFF
extends AbstractImagePreloader {
    private static Log log = LogFactory.getLog(PreloaderTIFF.class);
    private static final int TIFF_SIG_LENGTH = 8;

    @Override
    public ImageInfo preloadImage(String uri, Source src, ImageContext context) throws IOException, ImageException {
        if (!ImageUtil.hasImageInputStream(src)) {
            return null;
        }
        ImageInputStream in = ImageUtil.needImageInputStream(src);
        byte[] header = this.getHeader(in, 8);
        boolean supported = false;
        if (header[0] == 73 && header[1] == 73 && header[2] == 42 && header[3] == 0) {
            supported = true;
        }
        if (header[0] == 77 && header[1] == 77 && header[2] == 0 && header[3] == 42) {
            supported = true;
        }
        if (supported) {
            ImageInfo info = this.createImageInfo(uri, in, context);
            return info;
        }
        return null;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private ImageInfo createImageInfo(String uri, ImageInputStream in, ImageContext context) throws IOException, ImageException {
        ImageInfo info = null;
        in.mark();
        try {
            TIFFDirectory dir;
            int pageIndex = ImageUtil.needPageIndexFromURI(uri);
            SeekableStreamAdapter seekable = new SeekableStreamAdapter(in);
            try {
                dir = new TIFFDirectory(seekable, pageIndex);
            }
            catch (IllegalArgumentException iae) {
                String errorMessage = MessageFormat.format("Subimage {0} does not exist.", pageIndex);
                throw new SubImageNotFoundException(errorMessage);
            }
            int width = (int)dir.getFieldAsLong(256);
            int height = (int)dir.getFieldAsLong(257);
            ImageSize size = new ImageSize();
            size.setSizeInPixels(width, height);
            int unit = 2;
            if (dir.isTagPresent(296)) {
                unit = (int)dir.getFieldAsLong(296);
            }
            if (unit == 2 || unit == 3) {
                float yRes;
                float xRes;
                TIFFField fldx = dir.getField(282);
                TIFFField fldy = dir.getField(283);
                if (fldx == null || fldy == null) {
                    unit = 2;
                    yRes = xRes = context.getSourceResolution();
                } else {
                    xRes = fldx.getAsFloat(0);
                    yRes = fldy.getAsFloat(0);
                }
                if (xRes == 0.0f || yRes == 0.0f) {
                    size.setResolution(context.getSourceResolution());
                } else if (unit == 2) {
                    size.setResolution(xRes, yRes);
                } else {
                    size.setResolution(UnitConv.in2mm(xRes) / 10.0, UnitConv.in2mm(yRes) / 10.0);
                }
            } else {
                size.setResolution(context.getSourceResolution());
            }
            size.calcSizeFromPixels();
            if (log.isTraceEnabled()) {
                log.trace((Object)("TIFF image detected: " + size));
            }
            info = new ImageInfo(uri, "image/tiff");
            info.setSize(size);
            TIFFField fld = dir.getField(259);
            if (fld != null) {
                int compression = fld.getAsInt(0);
                if (log.isTraceEnabled()) {
                    log.trace((Object)("TIFF compression: " + compression));
                }
                info.getCustomObjects().put("TIFF_COMPRESSION", compression);
            }
            if ((fld = dir.getField(322)) != null) {
                if (log.isTraceEnabled()) {
                    log.trace((Object)"TIFF is tiled");
                }
                info.getCustomObjects().put("TIFF_TILED", Boolean.TRUE);
            }
            int stripCount = (fld = dir.getField(278)) == null ? 1 : (int)Math.ceil((double)size.getHeightPx() / (double)fld.getAsLong(0));
            if (log.isTraceEnabled()) {
                log.trace((Object)("TIFF has " + stripCount + " strips."));
            }
            info.getCustomObjects().put("TIFF_STRIP_COUNT", stripCount);
            try {
                new TIFFDirectory(seekable, pageIndex + 1);
                info.getCustomObjects().put(ImageInfo.HAS_MORE_IMAGES, Boolean.TRUE);
                if (log.isTraceEnabled()) {
                    log.trace((Object)"TIFF is multi-page.");
                }
            }
            catch (IllegalArgumentException iae) {
                info.getCustomObjects().put(ImageInfo.HAS_MORE_IMAGES, Boolean.FALSE);
            }
        }
        finally {
            in.reset();
        }
        return info;
    }
}


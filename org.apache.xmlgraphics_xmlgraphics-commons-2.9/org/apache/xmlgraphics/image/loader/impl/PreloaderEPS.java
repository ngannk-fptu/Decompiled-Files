/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xmlgraphics.image.loader.impl;

import java.awt.geom.Rectangle2D;
import java.awt.geom.RectangularShape;
import java.io.IOException;
import java.nio.ByteOrder;
import javax.imageio.stream.ImageInputStream;
import javax.xml.transform.Source;
import org.apache.xmlgraphics.image.loader.ImageContext;
import org.apache.xmlgraphics.image.loader.ImageInfo;
import org.apache.xmlgraphics.image.loader.ImageSize;
import org.apache.xmlgraphics.image.loader.impl.AbstractImagePreloader;
import org.apache.xmlgraphics.image.loader.util.ImageInputStreamAdapter;
import org.apache.xmlgraphics.image.loader.util.ImageUtil;
import org.apache.xmlgraphics.ps.dsc.DSCException;
import org.apache.xmlgraphics.ps.dsc.DSCParser;
import org.apache.xmlgraphics.ps.dsc.events.DSCComment;
import org.apache.xmlgraphics.ps.dsc.events.DSCCommentBoundingBox;
import org.apache.xmlgraphics.ps.dsc.events.DSCEvent;

public class PreloaderEPS
extends AbstractImagePreloader {
    public static final Object EPS_BINARY_HEADER = EPSBinaryFileHeader.class;
    public static final Object EPS_BOUNDING_BOX = Rectangle2D.class;

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public ImageInfo preloadImage(String uri, Source src, ImageContext context) throws IOException {
        if (!ImageUtil.hasImageInputStream(src)) {
            return null;
        }
        ImageInputStream in = ImageUtil.needImageInputStream(src);
        in.mark();
        ByteOrder originalByteOrder = in.getByteOrder();
        in.setByteOrder(ByteOrder.LITTLE_ENDIAN);
        EPSBinaryFileHeader binaryHeader = null;
        try {
            long magic = in.readUnsignedInt();
            boolean supported = false;
            if ((magic &= 0xFFFFFFFFL) == 3335770309L) {
                supported = true;
                binaryHeader = this.readBinaryFileHeader(in);
                in.reset();
                in.mark();
                in.seek(binaryHeader.psStart);
            } else if (magic == 1397760293L) {
                supported = true;
                in.reset();
                in.mark();
            } else {
                in.reset();
            }
            if (supported) {
                ImageInfo info = new ImageInfo(uri, "application/postscript");
                boolean success = this.determineSize(in, context, info);
                in.reset();
                if (!success) {
                    ImageInfo imageInfo = null;
                    return imageInfo;
                }
                if (in.getStreamPosition() != 0L) {
                    throw new IllegalStateException("Need to be at the start of the file here");
                }
                if (binaryHeader != null) {
                    info.getCustomObjects().put(EPS_BINARY_HEADER, binaryHeader);
                }
                ImageInfo imageInfo = info;
                return imageInfo;
            }
            ImageInfo imageInfo = null;
            return imageInfo;
        }
        finally {
            in.setByteOrder(originalByteOrder);
        }
    }

    private EPSBinaryFileHeader readBinaryFileHeader(ImageInputStream in) throws IOException {
        EPSBinaryFileHeader offsets = new EPSBinaryFileHeader();
        offsets.psStart = in.readUnsignedInt();
        offsets.psLength = in.readUnsignedInt();
        offsets.wmfStart = in.readUnsignedInt();
        offsets.wmfLength = in.readUnsignedInt();
        offsets.tiffStart = in.readUnsignedInt();
        offsets.tiffLength = in.readUnsignedInt();
        return offsets;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private boolean determineSize(ImageInputStream in, ImageContext context, ImageInfo info) throws IOException {
        in.mark();
        try {
            RectangularShape bbox = null;
            try {
                DSCParser parser = new DSCParser(new ImageInputStreamAdapter(in));
                block10: while (parser.hasNext()) {
                    DSCEvent event = parser.nextEvent();
                    switch (event.getEventType()) {
                        case 0: 
                        case 2: {
                            break;
                        }
                        case 1: {
                            DSCComment comment = event.asDSCComment();
                            if (!(comment instanceof DSCCommentBoundingBox)) continue block10;
                            DSCCommentBoundingBox bboxComment = (DSCCommentBoundingBox)comment;
                            if ("BoundingBox".equals(bboxComment.getName()) && bbox == null) {
                                bbox = (Rectangle2D)bboxComment.getBoundingBox().clone();
                                break;
                            }
                            if (!"HiResBoundingBox".equals(bboxComment.getName())) continue block10;
                            bbox = (Rectangle2D)bboxComment.getBoundingBox().clone();
                            break block10;
                        }
                        default: {
                            break block10;
                        }
                    }
                }
                if (bbox == null) {
                    boolean event = false;
                    return event;
                }
            }
            catch (DSCException e) {
                throw new IOException("Error while parsing EPS file: " + e.getMessage());
            }
            ImageSize size = new ImageSize();
            size.setSizeInMillipoints((int)Math.round(bbox.getWidth() * 1000.0), (int)Math.round(bbox.getHeight() * 1000.0));
            size.setResolution(context.getSourceResolution());
            size.calcPixelsFromSize();
            info.setSize(size);
            info.getCustomObjects().put(EPS_BOUNDING_BOX, bbox);
            boolean bl = true;
            return bl;
        }
        finally {
            in.reset();
        }
    }

    public static class EPSBinaryFileHeader {
        private long psStart;
        private long psLength;
        private long wmfStart;
        private long wmfLength;
        private long tiffStart;
        private long tiffLength;

        public long getPSStart() {
            return this.psStart;
        }

        public long getPSLength() {
            return this.psLength;
        }

        public boolean hasWMFPreview() {
            return this.wmfStart != 0L;
        }

        public long getWMFStart() {
            return this.wmfStart;
        }

        public long getWMFLength() {
            return this.wmfLength;
        }

        public boolean hasTIFFPreview() {
            return this.tiffStart != 0L;
        }

        public long getTIFFStart() {
            return this.tiffStart;
        }

        public long getTIFFLength() {
            return this.tiffLength;
        }
    }
}


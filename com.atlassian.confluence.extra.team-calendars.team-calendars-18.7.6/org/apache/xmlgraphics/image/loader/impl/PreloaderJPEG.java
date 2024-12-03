/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xmlgraphics.image.loader.impl;

import java.io.IOException;
import java.nio.ByteOrder;
import javax.imageio.stream.ImageInputStream;
import javax.xml.transform.Source;
import org.apache.xmlgraphics.image.loader.ImageContext;
import org.apache.xmlgraphics.image.loader.ImageException;
import org.apache.xmlgraphics.image.loader.ImageInfo;
import org.apache.xmlgraphics.image.loader.ImageSize;
import org.apache.xmlgraphics.image.loader.impl.AbstractImagePreloader;
import org.apache.xmlgraphics.image.loader.impl.JPEGConstants;
import org.apache.xmlgraphics.image.loader.impl.JPEGFile;
import org.apache.xmlgraphics.image.loader.util.ImageUtil;

public class PreloaderJPEG
extends AbstractImagePreloader
implements JPEGConstants {
    private static final int JPG_SIG_LENGTH = 3;
    private static final int[] BYTES_PER_COMPONENT = new int[]{0, 1, 1, 2, 4, 8, 1, 1, 2, 4, 8, 4, 8};
    private static final int EXIF = 1165519206;
    private static final int II = 18761;
    private static final int MM = 19789;
    private static final int X_RESOLUTION = 282;
    private static final int Y_RESOLUTION = 283;
    private static final int RESOLUTION_UNIT = 296;

    @Override
    public ImageInfo preloadImage(String uri, Source src, ImageContext context) throws IOException, ImageException {
        boolean supported;
        if (!ImageUtil.hasImageInputStream(src)) {
            return null;
        }
        ImageInputStream in = ImageUtil.needImageInputStream(src);
        byte[] header = this.getHeader(in, 3);
        boolean bl = supported = header[0] == -1 && header[1] == -40 && header[2] == -1;
        if (supported) {
            ImageInfo info = new ImageInfo(uri, "image/jpeg");
            info.setSize(this.determineSize(in, context));
            return info;
        }
        return null;
    }

    private ImageSize determineSize(ImageInputStream in, ImageContext context) throws IOException, ImageException {
        in.mark();
        try {
            ImageSize size = new ImageSize();
            JPEGFile jpeg = new JPEGFile(in);
            block14: while (true) {
                int segID = jpeg.readMarkerSegment();
                switch (segID) {
                    case 0: 
                    case 216: {
                        continue block14;
                    }
                    case 224: {
                        int reclen = jpeg.readSegmentLength();
                        in.skipBytes(7);
                        int densityUnits = in.read();
                        int xdensity = in.readUnsignedShort();
                        int ydensity = in.readUnsignedShort();
                        if (size.getDpiHorizontal() == 0.0) {
                            if (densityUnits == 2) {
                                size.setResolution((float)xdensity * 2.54f, (float)ydensity * 2.54f);
                            } else if (densityUnits == 1) {
                                size.setResolution(xdensity, ydensity);
                            } else {
                                size.setResolution(context.getSourceResolution());
                            }
                        }
                        if (size.getWidthPx() != 0) {
                            size.calcSizeFromPixels();
                            ImageSize imageSize = size;
                            return imageSize;
                        }
                        in.skipBytes(reclen - 14);
                        continue block14;
                    }
                    case 225: {
                        int reclen = jpeg.readSegmentLength();
                        int bytesToEnd = reclen - 2;
                        int exif = in.readInt();
                        in.readUnsignedShort();
                        bytesToEnd -= 6;
                        if (exif != 1165519206) {
                            in.skipBytes(bytesToEnd);
                            continue block14;
                        }
                        int currentTIFFOffset = 0;
                        int align = in.readUnsignedShort();
                        bytesToEnd -= 2;
                        currentTIFFOffset += 2;
                        ByteOrder originalByteOrder = in.getByteOrder();
                        in.setByteOrder(align == 19789 ? ByteOrder.BIG_ENDIAN : ByteOrder.LITTLE_ENDIAN);
                        in.skipBytes(2);
                        bytesToEnd -= 2;
                        currentTIFFOffset += 2;
                        int firstIFDOffset = in.readInt();
                        bytesToEnd -= 4;
                        currentTIFFOffset += 4;
                        in.skipBytes(firstIFDOffset - 8);
                        bytesToEnd -= firstIFDOffset - 8;
                        currentTIFFOffset += firstIFDOffset - 8;
                        int directoryEntries = in.readUnsignedShort();
                        bytesToEnd -= 2;
                        currentTIFFOffset += 2;
                        int resolutionOffset = 0;
                        int resolutionFormat = 0;
                        int resolutionUnits = 0;
                        int resolution = 0;
                        boolean foundResolution = false;
                        for (int j = 0; j < directoryEntries; ++j) {
                            int value;
                            int dataByteLength;
                            int components;
                            int format;
                            int tag = in.readUnsignedShort();
                            if (!(tag != 282 && tag != 283 || foundResolution)) {
                                format = in.readUnsignedShort();
                                components = in.readInt();
                                dataByteLength = components * BYTES_PER_COMPONENT[format];
                                value = in.readInt();
                                if (dataByteLength > 4) {
                                    resolutionOffset = value;
                                } else {
                                    resolution = value;
                                }
                                resolutionFormat = format;
                                foundResolution = true;
                            } else if (tag == 296) {
                                format = in.readUnsignedShort();
                                components = in.readInt();
                                dataByteLength = components * BYTES_PER_COMPONENT[format];
                                if (dataByteLength < 5 && format == 3) {
                                    value = in.readUnsignedShort();
                                    in.skipBytes(2);
                                    resolutionUnits = value;
                                } else {
                                    in.skipBytes(4);
                                }
                            } else {
                                in.skipBytes(10);
                            }
                            bytesToEnd -= 12;
                            currentTIFFOffset += 12;
                        }
                        in.readInt();
                        bytesToEnd -= 4;
                        currentTIFFOffset += 4;
                        if (resolutionOffset != 0) {
                            in.skipBytes(resolutionOffset - currentTIFFOffset);
                            bytesToEnd -= resolutionOffset - currentTIFFOffset;
                            if (resolutionFormat == 5 || resolutionFormat == 10) {
                                int numerator = in.readInt();
                                int denominator = in.readInt();
                                resolution = numerator / denominator;
                                bytesToEnd -= 8;
                            }
                        }
                        in.skipBytes(bytesToEnd);
                        in.setByteOrder(originalByteOrder);
                        if (resolutionUnits == 3) {
                            size.setResolution((float)resolution * 2.54f, (float)resolution * 2.54f);
                        } else if (resolutionUnits == 2) {
                            size.setResolution(resolution, resolution);
                        } else if (size.getDpiHorizontal() == 0.0) {
                            size.setResolution(context.getSourceResolution());
                        }
                        if (size.getWidthPx() == 0) continue block14;
                        size.calcSizeFromPixels();
                        ImageSize numerator = size;
                        return numerator;
                    }
                    case 192: 
                    case 193: 
                    case 194: 
                    case 202: {
                        int reclen = jpeg.readSegmentLength();
                        in.skipBytes(1);
                        int height = in.readUnsignedShort();
                        int width = in.readUnsignedShort();
                        size.setSizeInPixels(width, height);
                        if (size.getDpiHorizontal() != 0.0) {
                            size.calcSizeFromPixels();
                            ImageSize imageSize = size;
                            return imageSize;
                        }
                        in.skipBytes(reclen - 7);
                        continue block14;
                    }
                    case 217: 
                    case 218: {
                        if (size.getDpiHorizontal() == 0.0) {
                            size.setResolution(context.getSourceResolution());
                            size.calcSizeFromPixels();
                        }
                        ImageSize imageSize = size;
                        return imageSize;
                    }
                }
                jpeg.skipCurrentMarkerSegment();
            }
        }
        finally {
            in.reset();
        }
    }
}


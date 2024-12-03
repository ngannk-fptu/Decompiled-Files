/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.imaging.formats.jpeg;

import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.imaging.ImageFormat;
import org.apache.commons.imaging.ImageFormats;
import org.apache.commons.imaging.ImageInfo;
import org.apache.commons.imaging.ImageParser;
import org.apache.commons.imaging.ImageReadException;
import org.apache.commons.imaging.common.BinaryFunctions;
import org.apache.commons.imaging.common.ImageMetadata;
import org.apache.commons.imaging.common.XmpEmbeddable;
import org.apache.commons.imaging.common.bytesource.ByteSource;
import org.apache.commons.imaging.formats.jpeg.JpegConstants;
import org.apache.commons.imaging.formats.jpeg.JpegImageMetadata;
import org.apache.commons.imaging.formats.jpeg.JpegPhotoshopMetadata;
import org.apache.commons.imaging.formats.jpeg.JpegUtils;
import org.apache.commons.imaging.formats.jpeg.decoder.JpegDecoder;
import org.apache.commons.imaging.formats.jpeg.iptc.IptcParser;
import org.apache.commons.imaging.formats.jpeg.iptc.PhotoshopApp13Data;
import org.apache.commons.imaging.formats.jpeg.segments.App13Segment;
import org.apache.commons.imaging.formats.jpeg.segments.App14Segment;
import org.apache.commons.imaging.formats.jpeg.segments.App2Segment;
import org.apache.commons.imaging.formats.jpeg.segments.ComSegment;
import org.apache.commons.imaging.formats.jpeg.segments.DqtSegment;
import org.apache.commons.imaging.formats.jpeg.segments.GenericSegment;
import org.apache.commons.imaging.formats.jpeg.segments.JfifSegment;
import org.apache.commons.imaging.formats.jpeg.segments.Segment;
import org.apache.commons.imaging.formats.jpeg.segments.SofnSegment;
import org.apache.commons.imaging.formats.jpeg.segments.UnknownSegment;
import org.apache.commons.imaging.formats.jpeg.xmp.JpegXmpParser;
import org.apache.commons.imaging.formats.tiff.TiffField;
import org.apache.commons.imaging.formats.tiff.TiffImageMetadata;
import org.apache.commons.imaging.formats.tiff.TiffImageParser;
import org.apache.commons.imaging.formats.tiff.constants.TiffTagConstants;
import org.apache.commons.imaging.internal.Debug;

public class JpegImageParser
extends ImageParser
implements XmpEmbeddable {
    private static final Logger LOGGER = Logger.getLogger(JpegImageParser.class.getName());
    private static final String DEFAULT_EXTENSION = ".jpg";
    private static final String[] ACCEPTED_EXTENSIONS = new String[]{".jpg", ".jpeg"};

    public JpegImageParser() {
        this.setByteOrder(ByteOrder.BIG_ENDIAN);
    }

    @Override
    protected ImageFormat[] getAcceptedTypes() {
        return new ImageFormat[]{ImageFormats.JPEG};
    }

    @Override
    public String getName() {
        return "Jpeg-Custom";
    }

    @Override
    public String getDefaultExtension() {
        return DEFAULT_EXTENSION;
    }

    @Override
    protected String[] getAcceptedExtensions() {
        return ACCEPTED_EXTENSIONS;
    }

    @Override
    public final BufferedImage getBufferedImage(ByteSource byteSource, Map<String, Object> params) throws ImageReadException, IOException {
        JpegDecoder jpegDecoder = new JpegDecoder();
        return jpegDecoder.decode(byteSource);
    }

    private boolean keepMarker(int marker, int[] markers) {
        if (markers == null) {
            return true;
        }
        for (int marker2 : markers) {
            if (marker2 != marker) continue;
            return true;
        }
        return false;
    }

    public List<Segment> readSegments(ByteSource byteSource, final int[] markers, final boolean returnAfterFirst, boolean readEverything) throws ImageReadException, IOException {
        final ArrayList<Segment> result = new ArrayList<Segment>();
        final JpegImageParser parser = this;
        final int[] sofnSegments = new int[]{65472, 65473, 65474, 65475, 65477, 65478, 65479, 65481, 65482, 65483, 65485, 65486, 65487};
        JpegUtils.Visitor visitor = new JpegUtils.Visitor(){

            @Override
            public boolean beginSOS() {
                return false;
            }

            @Override
            public void visitSOS(int marker, byte[] markerBytes, byte[] imageData) {
            }

            @Override
            public boolean visitSegment(int marker, byte[] markerBytes, int markerLength, byte[] markerLengthBytes, byte[] segmentData) throws ImageReadException, IOException {
                if (marker == 65497) {
                    return false;
                }
                if (!JpegImageParser.this.keepMarker(marker, markers)) {
                    return true;
                }
                if (marker == 65517) {
                    result.add(new App13Segment(parser, marker, segmentData));
                } else if (marker == 65518) {
                    result.add(new App14Segment(marker, segmentData));
                } else if (marker == 65506) {
                    result.add(new App2Segment(marker, segmentData));
                } else if (marker == 65504) {
                    result.add(new JfifSegment(marker, segmentData));
                } else if (Arrays.binarySearch(sofnSegments, marker) >= 0) {
                    result.add(new SofnSegment(marker, segmentData));
                } else if (marker == 65499) {
                    result.add(new DqtSegment(marker, segmentData));
                } else if (marker >= 65505 && marker <= 65519) {
                    result.add(new UnknownSegment(marker, segmentData));
                } else if (marker == 65534) {
                    result.add(new ComSegment(marker, segmentData));
                }
                return !returnAfterFirst;
            }
        };
        new JpegUtils().traverseJFIF(byteSource, visitor);
        return result;
    }

    private byte[] assembleSegments(List<App2Segment> segments) throws ImageReadException {
        try {
            return this.assembleSegments(segments, false);
        }
        catch (ImageReadException e) {
            return this.assembleSegments(segments, true);
        }
    }

    private byte[] assembleSegments(List<App2Segment> segments, boolean startWithZero) throws ImageReadException {
        if (segments.isEmpty()) {
            throw new ImageReadException("No App2 Segments Found.");
        }
        int markerCount = segments.get((int)0).numMarkers;
        if (segments.size() != markerCount) {
            throw new ImageReadException("App2 Segments Missing.  Found: " + segments.size() + ", Expected: " + markerCount + ".");
        }
        Collections.sort(segments);
        int offset = startWithZero ? 0 : 1;
        int total = 0;
        for (int i = 0; i < segments.size(); ++i) {
            App2Segment segment = segments.get(i);
            if (i + offset != segment.curMarker) {
                this.dumpSegments(segments);
                throw new ImageReadException("Incoherent App2 Segment Ordering.  i: " + i + ", segment[" + i + "].curMarker: " + segment.curMarker + ".");
            }
            if (markerCount != segment.numMarkers) {
                this.dumpSegments(segments);
                throw new ImageReadException("Inconsistent App2 Segment Count info.  markerCount: " + markerCount + ", segment[" + i + "].numMarkers: " + segment.numMarkers + ".");
            }
            total += segment.getIccBytes().length;
        }
        byte[] result = new byte[total];
        int progress = 0;
        for (App2Segment segment : segments) {
            System.arraycopy(segment.getIccBytes(), 0, result, progress, segment.getIccBytes().length);
            progress += segment.getIccBytes().length;
        }
        return result;
    }

    private void dumpSegments(List<? extends Segment> v) {
        Debug.debug();
        Debug.debug("dumpSegments: " + v.size());
        for (int i = 0; i < v.size(); ++i) {
            App2Segment segment = (App2Segment)v.get(i);
            Debug.debug(i + ": " + segment.curMarker + " / " + segment.numMarkers);
        }
        Debug.debug();
    }

    public List<Segment> readSegments(ByteSource byteSource, int[] markers, boolean returnAfterFirst) throws ImageReadException, IOException {
        return this.readSegments(byteSource, markers, returnAfterFirst, false);
    }

    @Override
    public byte[] getICCProfileBytes(ByteSource byteSource, Map<String, Object> params) throws ImageReadException, IOException {
        List<Segment> segments = this.readSegments(byteSource, new int[]{65506}, false);
        ArrayList<App2Segment> filtered = new ArrayList<App2Segment>();
        if (segments != null) {
            for (Segment s : segments) {
                App2Segment segment = (App2Segment)s;
                if (segment.getIccBytes() == null) continue;
                filtered.add(segment);
            }
        }
        if (filtered.isEmpty()) {
            return null;
        }
        byte[] bytes = this.assembleSegments(filtered);
        if (LOGGER.isLoggable(Level.FINEST)) {
            LOGGER.finest("bytes: " + bytes.length);
        }
        return bytes;
    }

    @Override
    public ImageMetadata getMetadata(ByteSource byteSource, Map<String, Object> params) throws ImageReadException, IOException {
        TiffImageMetadata exif = this.getExifMetadata(byteSource, params);
        JpegPhotoshopMetadata photoshop = this.getPhotoshopMetadata(byteSource, params);
        if (null == exif && null == photoshop) {
            return null;
        }
        return new JpegImageMetadata(photoshop, exif);
    }

    public static boolean isExifAPP1Segment(GenericSegment segment) {
        return BinaryFunctions.startsWith(segment.getSegmentData(), JpegConstants.EXIF_IDENTIFIER_CODE);
    }

    private List<Segment> filterAPP1Segments(List<Segment> segments) {
        ArrayList<Segment> result = new ArrayList<Segment>();
        for (Segment s : segments) {
            GenericSegment segment = (GenericSegment)s;
            if (!JpegImageParser.isExifAPP1Segment(segment)) continue;
            result.add(segment);
        }
        return result;
    }

    public TiffImageMetadata getExifMetadata(ByteSource byteSource, Map<String, Object> params) throws ImageReadException, IOException {
        byte[] bytes = this.getExifRawData(byteSource);
        if (null == bytes) {
            return null;
        }
        if (params == null) {
            params = new HashMap<String, Object>();
        }
        if (!params.containsKey("READ_THUMBNAILS")) {
            params.put("READ_THUMBNAILS", Boolean.TRUE);
        }
        return (TiffImageMetadata)new TiffImageParser().getMetadata(bytes, params);
    }

    public byte[] getExifRawData(ByteSource byteSource) throws ImageReadException, IOException {
        List<Segment> segments = this.readSegments(byteSource, new int[]{65505}, false);
        if (segments == null || segments.isEmpty()) {
            return null;
        }
        List<Segment> exifSegments = this.filterAPP1Segments(segments);
        if (LOGGER.isLoggable(Level.FINEST)) {
            LOGGER.finest("exif_segments.size: " + exifSegments.size());
        }
        if (exifSegments.isEmpty()) {
            return null;
        }
        if (exifSegments.size() > 1) {
            throw new ImageReadException("Imaging currently can't parse EXIF metadata split across multiple APP1 segments.  Please send this image to the Imaging project.");
        }
        GenericSegment segment = (GenericSegment)exifSegments.get(0);
        byte[] bytes = segment.getSegmentData();
        return BinaryFunctions.remainingBytes("trimmed exif bytes", bytes, 6);
    }

    public boolean hasExifSegment(ByteSource byteSource) throws ImageReadException, IOException {
        final boolean[] result = new boolean[]{false};
        JpegUtils.Visitor visitor = new JpegUtils.Visitor(){

            @Override
            public boolean beginSOS() {
                return false;
            }

            @Override
            public void visitSOS(int marker, byte[] markerBytes, byte[] imageData) {
            }

            @Override
            public boolean visitSegment(int marker, byte[] markerBytes, int markerLength, byte[] markerLengthBytes, byte[] segmentData) throws ImageReadException, IOException {
                if (marker == 65497) {
                    return false;
                }
                if (marker == 65505 && BinaryFunctions.startsWith(segmentData, JpegConstants.EXIF_IDENTIFIER_CODE)) {
                    result[0] = true;
                    return false;
                }
                return true;
            }
        };
        new JpegUtils().traverseJFIF(byteSource, visitor);
        return result[0];
    }

    public boolean hasIptcSegment(ByteSource byteSource) throws ImageReadException, IOException {
        final boolean[] result = new boolean[]{false};
        JpegUtils.Visitor visitor = new JpegUtils.Visitor(){

            @Override
            public boolean beginSOS() {
                return false;
            }

            @Override
            public void visitSOS(int marker, byte[] markerBytes, byte[] imageData) {
            }

            @Override
            public boolean visitSegment(int marker, byte[] markerBytes, int markerLength, byte[] markerLengthBytes, byte[] segmentData) throws ImageReadException, IOException {
                if (marker == 65497) {
                    return false;
                }
                if (marker == 65517 && new IptcParser().isPhotoshopJpegSegment(segmentData)) {
                    result[0] = true;
                    return false;
                }
                return true;
            }
        };
        new JpegUtils().traverseJFIF(byteSource, visitor);
        return result[0];
    }

    public boolean hasXmpSegment(ByteSource byteSource) throws ImageReadException, IOException {
        final boolean[] result = new boolean[]{false};
        JpegUtils.Visitor visitor = new JpegUtils.Visitor(){

            @Override
            public boolean beginSOS() {
                return false;
            }

            @Override
            public void visitSOS(int marker, byte[] markerBytes, byte[] imageData) {
            }

            @Override
            public boolean visitSegment(int marker, byte[] markerBytes, int markerLength, byte[] markerLengthBytes, byte[] segmentData) throws ImageReadException, IOException {
                if (marker == 65497) {
                    return false;
                }
                if (marker == 65505 && new JpegXmpParser().isXmpJpegSegment(segmentData)) {
                    result[0] = true;
                    return false;
                }
                return true;
            }
        };
        new JpegUtils().traverseJFIF(byteSource, visitor);
        return result[0];
    }

    @Override
    public String getXmpXml(ByteSource byteSource, Map<String, Object> params) throws ImageReadException, IOException {
        final ArrayList result = new ArrayList();
        JpegUtils.Visitor visitor = new JpegUtils.Visitor(){

            @Override
            public boolean beginSOS() {
                return false;
            }

            @Override
            public void visitSOS(int marker, byte[] markerBytes, byte[] imageData) {
            }

            @Override
            public boolean visitSegment(int marker, byte[] markerBytes, int markerLength, byte[] markerLengthBytes, byte[] segmentData) throws ImageReadException, IOException {
                if (marker == 65497) {
                    return false;
                }
                if (marker == 65505 && new JpegXmpParser().isXmpJpegSegment(segmentData)) {
                    result.add(new JpegXmpParser().parseXmpJpegSegment(segmentData));
                    return false;
                }
                return true;
            }
        };
        new JpegUtils().traverseJFIF(byteSource, visitor);
        if (result.isEmpty()) {
            return null;
        }
        if (result.size() > 1) {
            throw new ImageReadException("Jpeg file contains more than one XMP segment.");
        }
        return (String)result.get(0);
    }

    public JpegPhotoshopMetadata getPhotoshopMetadata(ByteSource byteSource, Map<String, Object> params) throws ImageReadException, IOException {
        List<Segment> segments = this.readSegments(byteSource, new int[]{65517}, false);
        if (segments == null || segments.isEmpty()) {
            return null;
        }
        PhotoshopApp13Data photoshopApp13Data = null;
        for (Segment s : segments) {
            App13Segment segment = (App13Segment)s;
            PhotoshopApp13Data data = segment.parsePhotoshopSegment(params);
            if (data == null) continue;
            if (photoshopApp13Data != null) {
                throw new ImageReadException("Jpeg contains more than one Photoshop App13 segment.");
            }
            photoshopApp13Data = data;
        }
        if (null == photoshopApp13Data) {
            return null;
        }
        return new JpegPhotoshopMetadata(photoshopApp13Data);
    }

    @Override
    public Dimension getImageSize(ByteSource byteSource, Map<String, Object> params) throws ImageReadException, IOException {
        List<Segment> segments = this.readSegments(byteSource, new int[]{65472, 65473, 65474, 65475, 65477, 65478, 65479, 65481, 65482, 65483, 65485, 65486, 65487}, true);
        if (segments == null || segments.isEmpty()) {
            throw new ImageReadException("No JFIF Data Found.");
        }
        if (segments.size() > 1) {
            throw new ImageReadException("Redundant JFIF Data Found.");
        }
        SofnSegment fSOFNSegment = (SofnSegment)segments.get(0);
        return new Dimension(fSOFNSegment.width, fSOFNSegment.height);
    }

    @Override
    public ImageInfo getImageInfo(ByteSource byteSource, Map<String, Object> params) throws ImageReadException, IOException {
        String formatDetails;
        List<Segment> SOF_segments = this.readSegments(byteSource, new int[]{65472, 65473, 65474, 65475, 65477, 65478, 65479, 65481, 65482, 65483, 65485, 65486, 65487}, false);
        if (SOF_segments == null) {
            throw new ImageReadException("No SOFN Data Found.");
        }
        List<Segment> jfifSegments = this.readSegments(byteSource, new int[]{65504}, true);
        SofnSegment fSOFNSegment = (SofnSegment)SOF_segments.get(0);
        if (fSOFNSegment == null) {
            throw new ImageReadException("No SOFN Data Found.");
        }
        int width = fSOFNSegment.width;
        int height = fSOFNSegment.height;
        JfifSegment jfifSegment = null;
        if (jfifSegments != null && !jfifSegments.isEmpty()) {
            jfifSegment = (JfifSegment)jfifSegments.get(0);
        }
        List<Segment> app14Segments = this.readSegments(byteSource, new int[]{65518}, true);
        App14Segment app14Segment = null;
        if (app14Segments != null && !app14Segments.isEmpty()) {
            app14Segment = (App14Segment)app14Segments.get(0);
        }
        double xDensity = -1.0;
        double yDensity = -1.0;
        double unitsPerInch = -1.0;
        if (jfifSegment != null) {
            xDensity = jfifSegment.xDensity;
            yDensity = jfifSegment.yDensity;
            int densityUnits = jfifSegment.densityUnits;
            formatDetails = "Jpeg/JFIF v." + jfifSegment.jfifMajorVersion + "." + jfifSegment.jfifMinorVersion;
            switch (densityUnits) {
                case 0: {
                    break;
                }
                case 1: {
                    unitsPerInch = 1.0;
                    break;
                }
                case 2: {
                    unitsPerInch = 2.54;
                    break;
                }
            }
        } else {
            JpegImageMetadata metadata = (JpegImageMetadata)this.getMetadata(byteSource, params);
            if (metadata != null) {
                TiffField field = metadata.findEXIFValue(TiffTagConstants.TIFF_TAG_XRESOLUTION);
                if (field != null) {
                    xDensity = ((Number)field.getValue()).doubleValue();
                }
                if ((field = metadata.findEXIFValue(TiffTagConstants.TIFF_TAG_YRESOLUTION)) != null) {
                    yDensity = ((Number)field.getValue()).doubleValue();
                }
                if ((field = metadata.findEXIFValue(TiffTagConstants.TIFF_TAG_RESOLUTION_UNIT)) != null) {
                    int densityUnits = ((Number)field.getValue()).intValue();
                    switch (densityUnits) {
                        case 1: {
                            break;
                        }
                        case 2: {
                            unitsPerInch = 1.0;
                            break;
                        }
                        case 3: {
                            unitsPerInch = 2.54;
                            break;
                        }
                    }
                }
            }
            formatDetails = "Jpeg/DCM";
        }
        int physicalHeightDpi = -1;
        float physicalHeightInch = -1.0f;
        int physicalWidthDpi = -1;
        float physicalWidthInch = -1.0f;
        if (unitsPerInch > 0.0) {
            physicalWidthDpi = (int)Math.round(xDensity * unitsPerInch);
            physicalWidthInch = (float)((double)width / (xDensity * unitsPerInch));
            physicalHeightDpi = (int)Math.round(yDensity * unitsPerInch);
            physicalHeightInch = (float)((double)height / (yDensity * unitsPerInch));
        }
        List<Segment> commentSegments = this.readSegments(byteSource, new int[]{65534}, false);
        ArrayList<String> comments = new ArrayList<String>(commentSegments.size());
        for (Segment commentSegment : commentSegments) {
            ComSegment comSegment = (ComSegment)commentSegment;
            String comment = "";
            comment = new String(comSegment.getComment(), StandardCharsets.UTF_8);
            comments.add(comment);
        }
        int numberOfComponents = fSOFNSegment.numberOfComponents;
        int precision = fSOFNSegment.precision;
        int bitsPerPixel = numberOfComponents * precision;
        ImageFormats format = ImageFormats.JPEG;
        String formatName = "JPEG (Joint Photographic Experts Group) Format";
        String mimeType = "image/jpeg";
        boolean numberOfImages = true;
        boolean progressive = fSOFNSegment.marker == 65474;
        boolean transparent = false;
        boolean usesPalette = false;
        ImageInfo.ColorType colorType = ImageInfo.ColorType.UNKNOWN;
        if (app14Segment != null && app14Segment.isAdobeJpegSegment()) {
            int colorTransform = app14Segment.getAdobeColorTransform();
            if (colorTransform == 0) {
                if (numberOfComponents == 3) {
                    colorType = ImageInfo.ColorType.RGB;
                } else if (numberOfComponents == 4) {
                    colorType = ImageInfo.ColorType.CMYK;
                }
            } else if (colorTransform == 1) {
                colorType = ImageInfo.ColorType.YCbCr;
            } else if (colorTransform == 2) {
                colorType = ImageInfo.ColorType.YCCK;
            }
        } else if (jfifSegment != null) {
            if (numberOfComponents == 1) {
                colorType = ImageInfo.ColorType.GRAYSCALE;
            } else if (numberOfComponents == 3) {
                colorType = ImageInfo.ColorType.YCbCr;
            }
        } else if (numberOfComponents == 1) {
            colorType = ImageInfo.ColorType.GRAYSCALE;
        } else if (numberOfComponents == 2) {
            colorType = ImageInfo.ColorType.GRAYSCALE;
            transparent = true;
        } else if (numberOfComponents == 3 || numberOfComponents == 4) {
            boolean have1 = false;
            boolean have2 = false;
            boolean have3 = false;
            boolean have4 = false;
            boolean haveOther = false;
            for (SofnSegment.Component component : fSOFNSegment.getComponents()) {
                int id = component.componentIdentifier;
                if (id == 1) {
                    have1 = true;
                    continue;
                }
                if (id == 2) {
                    have2 = true;
                    continue;
                }
                if (id == 3) {
                    have3 = true;
                    continue;
                }
                if (id == 4) {
                    have4 = true;
                    continue;
                }
                haveOther = true;
            }
            if (numberOfComponents == 3 && have1 && have2 && have3 && !have4 && !haveOther) {
                colorType = ImageInfo.ColorType.YCbCr;
            } else if (numberOfComponents == 4 && have1 && have2 && have3 && have4 && !haveOther) {
                colorType = ImageInfo.ColorType.YCbCr;
                transparent = true;
            } else {
                boolean haveR = false;
                boolean haveG = false;
                boolean haveB = false;
                boolean haveA = false;
                boolean haveC = false;
                boolean havec = false;
                boolean haveY = false;
                for (SofnSegment.Component component : fSOFNSegment.getComponents()) {
                    int id = component.componentIdentifier;
                    if (id == 82) {
                        haveR = true;
                        continue;
                    }
                    if (id == 71) {
                        haveG = true;
                        continue;
                    }
                    if (id == 66) {
                        haveB = true;
                        continue;
                    }
                    if (id == 65) {
                        haveA = true;
                        continue;
                    }
                    if (id == 67) {
                        haveC = true;
                        continue;
                    }
                    if (id == 99) {
                        havec = true;
                        continue;
                    }
                    if (id != 89) continue;
                    haveY = true;
                }
                if (haveR && haveG && haveB && !haveA && !haveC && !havec && !haveY) {
                    colorType = ImageInfo.ColorType.RGB;
                } else if (haveR && haveG && haveB && haveA && !haveC && !havec && !haveY) {
                    colorType = ImageInfo.ColorType.RGB;
                    transparent = true;
                } else if (haveY && haveC && havec && !haveR && !haveG && !haveB && !haveA) {
                    colorType = ImageInfo.ColorType.YCC;
                } else if (haveY && haveC && havec && haveA && !haveR && !haveG && !haveB) {
                    colorType = ImageInfo.ColorType.YCC;
                    transparent = true;
                } else {
                    boolean isSubsampled;
                    int minHorizontalSamplingFactor = Integer.MAX_VALUE;
                    int maxHorizontalSmaplingFactor = Integer.MIN_VALUE;
                    int minVerticalSamplingFactor = Integer.MAX_VALUE;
                    int maxVerticalSamplingFactor = Integer.MIN_VALUE;
                    for (SofnSegment.Component component : fSOFNSegment.getComponents()) {
                        if (minHorizontalSamplingFactor > component.horizontalSamplingFactor) {
                            minHorizontalSamplingFactor = component.horizontalSamplingFactor;
                        }
                        if (maxHorizontalSmaplingFactor < component.horizontalSamplingFactor) {
                            maxHorizontalSmaplingFactor = component.horizontalSamplingFactor;
                        }
                        if (minVerticalSamplingFactor > component.verticalSamplingFactor) {
                            minVerticalSamplingFactor = component.verticalSamplingFactor;
                        }
                        if (maxVerticalSamplingFactor >= component.verticalSamplingFactor) continue;
                        maxVerticalSamplingFactor = component.verticalSamplingFactor;
                    }
                    boolean bl = isSubsampled = minHorizontalSamplingFactor != maxHorizontalSmaplingFactor || minVerticalSamplingFactor != maxVerticalSamplingFactor;
                    if (numberOfComponents == 3) {
                        colorType = isSubsampled ? ImageInfo.ColorType.YCbCr : ImageInfo.ColorType.RGB;
                    } else if (numberOfComponents == 4) {
                        colorType = isSubsampled ? ImageInfo.ColorType.YCCK : ImageInfo.ColorType.CMYK;
                    }
                }
            }
        }
        ImageInfo.CompressionAlgorithm compressionAlgorithm = ImageInfo.CompressionAlgorithm.JPEG;
        return new ImageInfo(formatDetails, bitsPerPixel, comments, format, "JPEG (Joint Photographic Experts Group) Format", height, "image/jpeg", 1, physicalHeightDpi, physicalHeightInch, physicalWidthDpi, physicalWidthInch, width, progressive, transparent, false, colorType, compressionAlgorithm);
    }

    @Override
    public boolean dumpImageFile(PrintWriter pw, ByteSource byteSource) throws ImageReadException, IOException {
        pw.println("jpeg.dumpImageFile");
        ImageInfo imageInfo = this.getImageInfo(byteSource);
        if (imageInfo == null) {
            return false;
        }
        imageInfo.toString(pw, "");
        pw.println("");
        List<Segment> segments = this.readSegments(byteSource, null, false);
        if (segments == null) {
            throw new ImageReadException("No Segments Found.");
        }
        for (int d = 0; d < segments.size(); ++d) {
            Segment segment = segments.get(d);
            NumberFormat nf = NumberFormat.getIntegerInstance();
            pw.println(d + ": marker: " + Integer.toHexString(segment.marker) + ", " + segment.getDescription() + " (length: " + nf.format(segment.length) + ")");
            segment.dump(pw);
        }
        pw.println("");
        return true;
    }
}


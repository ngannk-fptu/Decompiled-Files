/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.imaging.formats.jpeg;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteOrder;
import org.apache.commons.imaging.ImageReadException;
import org.apache.commons.imaging.common.BinaryFileParser;
import org.apache.commons.imaging.common.BinaryFunctions;
import org.apache.commons.imaging.common.ByteConversions;
import org.apache.commons.imaging.common.bytesource.ByteSource;
import org.apache.commons.imaging.formats.jpeg.JpegConstants;
import org.apache.commons.imaging.internal.Debug;

public class JpegUtils
extends BinaryFileParser {
    public JpegUtils() {
        this.setByteOrder(ByteOrder.BIG_ENDIAN);
    }

    public void traverseJFIF(ByteSource byteSource, Visitor visitor) throws ImageReadException, IOException {
        try (InputStream is = byteSource.getInputStream();){
            int marker;
            byte[] markerBytes;
            BinaryFunctions.readAndVerifyBytes(is, JpegConstants.SOI, "Not a Valid JPEG File: doesn't begin with 0xffd8");
            int markerCount = 0;
            while (true) {
                markerBytes = new byte[]{markerBytes[1], BinaryFunctions.readByte("marker", is, "Could not read marker")};
                while ((0xFF & markerBytes[0]) != 255 || (0xFF & markerBytes[1]) == 255) {
                }
                marker = (0xFF & markerBytes[0]) << 8 | 0xFF & markerBytes[1];
                if (marker == 65497 || marker == 65498) {
                    if (!visitor.beginSOS()) {
                        return;
                    }
                    break;
                }
                byte[] segmentLengthBytes = BinaryFunctions.readBytes("segmentLengthBytes", is, 2, "segmentLengthBytes");
                int segmentLength = ByteConversions.toUInt16(segmentLengthBytes, this.getByteOrder());
                if (segmentLength < 2) {
                    throw new ImageReadException("Invalid segment size");
                }
                byte[] segmentData = BinaryFunctions.readBytes("Segment Data", is, segmentLength - 2, "Invalid Segment: insufficient data");
                if (!visitor.visitSegment(marker, markerBytes, segmentLength, segmentLengthBytes, segmentData)) {
                    return;
                }
                ++markerCount;
            }
            byte[] imageData = BinaryFunctions.getStreamBytes(is);
            visitor.visitSOS(marker, markerBytes, imageData);
            Debug.debug(markerCount + " markers");
        }
    }

    public static String getMarkerName(int marker) {
        switch (marker) {
            case 65498: {
                return "SOS_MARKER";
            }
            case 65505: {
                return "JPEG_APP1_MARKER";
            }
            case 65506: {
                return "JPEG_APP2_MARKER";
            }
            case 65517: {
                return "JPEG_APP13_MARKER";
            }
            case 65518: {
                return "JPEG_APP14_MARKER";
            }
            case 65519: {
                return "JPEG_APP15_MARKER";
            }
            case 65504: {
                return "JFIF_MARKER";
            }
            case 65472: {
                return "SOF0_MARKER";
            }
            case 65473: {
                return "SOF1_MARKER";
            }
            case 65474: {
                return "SOF2_MARKER";
            }
            case 65475: {
                return "SOF3_MARKER";
            }
            case 65476: {
                return "SOF4_MARKER";
            }
            case 65477: {
                return "SOF5_MARKER";
            }
            case 65478: {
                return "SOF6_MARKER";
            }
            case 65479: {
                return "SOF7_MARKER";
            }
            case 65480: {
                return "SOF8_MARKER";
            }
            case 65481: {
                return "SOF9_MARKER";
            }
            case 65482: {
                return "SOF10_MARKER";
            }
            case 65483: {
                return "SOF11_MARKER";
            }
            case 65484: {
                return "DAC_MARKER";
            }
            case 65485: {
                return "SOF13_MARKER";
            }
            case 65486: {
                return "SOF14_MARKER";
            }
            case 65487: {
                return "SOF15_MARKER";
            }
            case 65499: {
                return "DQT_MARKER";
            }
            case 65501: {
                return "DRI_MARKER";
            }
            case 65488: {
                return "RST0_MARKER";
            }
            case 65489: {
                return "RST1_MARKER";
            }
            case 65490: {
                return "RST2_MARKER";
            }
            case 65491: {
                return "RST3_MARKER";
            }
            case 65492: {
                return "RST4_MARKER";
            }
            case 65493: {
                return "RST5_MARKER";
            }
            case 65494: {
                return "RST6_MARKER";
            }
            case 65495: {
                return "RST7_MARKER";
            }
        }
        return "Unknown";
    }

    public void dumpJFIF(ByteSource byteSource) throws ImageReadException, IOException {
        Visitor visitor = new Visitor(){

            @Override
            public boolean beginSOS() {
                return true;
            }

            @Override
            public void visitSOS(int marker, byte[] markerBytes, byte[] imageData) {
                Debug.debug("SOS marker.  " + imageData.length + " bytes of image data.");
                Debug.debug("");
            }

            @Override
            public boolean visitSegment(int marker, byte[] markerBytes, int segmentLength, byte[] segmentLengthBytes, byte[] segmentData) {
                Debug.debug("Segment marker: " + Integer.toHexString(marker) + " (" + JpegUtils.getMarkerName(marker) + "), " + segmentData.length + " bytes of segment data.");
                return true;
            }
        };
        this.traverseJFIF(byteSource, visitor);
    }

    public static interface Visitor {
        public boolean beginSOS();

        public void visitSOS(int var1, byte[] var2, byte[] var3);

        public boolean visitSegment(int var1, byte[] var2, int var3, byte[] var4, byte[] var5) throws ImageReadException, IOException;
    }
}


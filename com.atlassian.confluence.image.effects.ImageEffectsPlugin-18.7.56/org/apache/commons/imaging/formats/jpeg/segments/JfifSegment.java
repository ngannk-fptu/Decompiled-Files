/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.imaging.formats.jpeg.segments;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import org.apache.commons.imaging.ImageReadException;
import org.apache.commons.imaging.common.BinaryFunctions;
import org.apache.commons.imaging.formats.jpeg.JpegConstants;
import org.apache.commons.imaging.formats.jpeg.segments.Segment;

public class JfifSegment
extends Segment {
    public final int jfifMajorVersion;
    public final int jfifMinorVersion;
    public final int densityUnits;
    public final int xDensity;
    public final int yDensity;
    public final int xThumbnail;
    public final int yThumbnail;
    public final int thumbnailSize;

    @Override
    public String getDescription() {
        return "JFIF (" + this.getSegmentType() + ")";
    }

    public JfifSegment(int marker, byte[] segmentData) throws ImageReadException, IOException {
        this(marker, segmentData.length, new ByteArrayInputStream(segmentData));
    }

    public JfifSegment(int marker, int markerLength, InputStream is) throws ImageReadException, IOException {
        super(marker, markerLength);
        byte[] signature = BinaryFunctions.readBytes(is, JpegConstants.JFIF0_SIGNATURE.size());
        if (!JpegConstants.JFIF0_SIGNATURE.equals(signature) && !JpegConstants.JFIF0_SIGNATURE_ALTERNATIVE.equals(signature)) {
            throw new ImageReadException("Not a Valid JPEG File: missing JFIF string");
        }
        this.jfifMajorVersion = BinaryFunctions.readByte("JFIF_major_version", is, "Not a Valid JPEG File");
        this.jfifMinorVersion = BinaryFunctions.readByte("JFIF_minor_version", is, "Not a Valid JPEG File");
        this.densityUnits = BinaryFunctions.readByte("density_units", is, "Not a Valid JPEG File");
        this.xDensity = BinaryFunctions.read2Bytes("x_density", is, "Not a Valid JPEG File", this.getByteOrder());
        this.yDensity = BinaryFunctions.read2Bytes("y_density", is, "Not a Valid JPEG File", this.getByteOrder());
        this.xThumbnail = BinaryFunctions.readByte("x_thumbnail", is, "Not a Valid JPEG File");
        this.yThumbnail = BinaryFunctions.readByte("y_thumbnail", is, "Not a Valid JPEG File");
        this.thumbnailSize = this.xThumbnail * this.yThumbnail;
        if (this.thumbnailSize > 0) {
            BinaryFunctions.skipBytes(is, this.thumbnailSize, "Not a Valid JPEG File: missing thumbnail");
        }
    }
}


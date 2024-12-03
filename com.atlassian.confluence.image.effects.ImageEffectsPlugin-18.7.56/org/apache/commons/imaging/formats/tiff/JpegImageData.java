/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.imaging.formats.tiff;

import org.apache.commons.imaging.formats.tiff.TiffElement;

public class JpegImageData
extends TiffElement.DataElement {
    public JpegImageData(long offset, int length, byte[] data) {
        super(offset, length, data);
    }

    @Override
    public String getElementDescription() {
        return "Jpeg image data: " + this.getDataLength() + " bytes";
    }
}


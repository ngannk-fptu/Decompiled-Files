/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.imaging.formats.tiff.write;

import org.apache.commons.imaging.formats.tiff.TiffElement;
import org.apache.commons.imaging.formats.tiff.write.TiffOutputField;
import org.apache.commons.imaging.formats.tiff.write.TiffOutputItem;

class ImageDataOffsets {
    final int[] imageDataOffsets;
    final TiffOutputField imageDataOffsetsField;
    final TiffOutputItem[] outputItems;

    ImageDataOffsets(TiffElement.DataElement[] imageData, int[] imageDataOffsets, TiffOutputField imageDataOffsetsField) {
        this.imageDataOffsets = imageDataOffsets;
        this.imageDataOffsetsField = imageDataOffsetsField;
        this.outputItems = new TiffOutputItem[imageData.length];
        for (int i = 0; i < imageData.length; ++i) {
            TiffOutputItem.Value item = new TiffOutputItem.Value("TIFF image data", imageData[i].getData());
            this.outputItems[i] = item;
        }
    }
}


/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.imaging.formats.tiff.write;

import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.apache.commons.imaging.ImageWriteException;
import org.apache.commons.imaging.formats.tiff.fieldtypes.FieldType;
import org.apache.commons.imaging.formats.tiff.write.ImageDataOffsets;
import org.apache.commons.imaging.formats.tiff.write.TiffOutputDirectory;
import org.apache.commons.imaging.formats.tiff.write.TiffOutputField;
import org.apache.commons.imaging.formats.tiff.write.TiffOutputItem;

class TiffOutputSummary {
    public final ByteOrder byteOrder;
    public final TiffOutputDirectory rootDirectory;
    public final Map<Integer, TiffOutputDirectory> directoryTypeMap;
    private final List<OffsetItem> offsetItems = new ArrayList<OffsetItem>();
    private final List<ImageDataOffsets> imageDataItems = new ArrayList<ImageDataOffsets>();

    TiffOutputSummary(ByteOrder byteOrder, TiffOutputDirectory rootDirectory, Map<Integer, TiffOutputDirectory> directoryTypeMap) {
        this.byteOrder = byteOrder;
        this.rootDirectory = rootDirectory;
        this.directoryTypeMap = directoryTypeMap;
    }

    public void add(TiffOutputItem item, TiffOutputField itemOffsetField) {
        this.offsetItems.add(new OffsetItem(item, itemOffsetField));
    }

    public void updateOffsets(ByteOrder byteOrder) throws ImageWriteException {
        for (OffsetItem offset : this.offsetItems) {
            byte[] value = FieldType.LONG.writeData((int)offset.item.getOffset(), byteOrder);
            offset.itemOffsetField.setData(value);
        }
        for (ImageDataOffsets imageDataInfo : this.imageDataItems) {
            for (int j = 0; j < imageDataInfo.outputItems.length; ++j) {
                TiffOutputItem item = imageDataInfo.outputItems[j];
                imageDataInfo.imageDataOffsets[j] = (int)item.getOffset();
            }
            imageDataInfo.imageDataOffsetsField.setData(FieldType.LONG.writeData(imageDataInfo.imageDataOffsets, byteOrder));
        }
    }

    public void addTiffImageData(ImageDataOffsets imageDataInfo) {
        this.imageDataItems.add(imageDataInfo);
    }

    private static class OffsetItem {
        public final TiffOutputItem item;
        public final TiffOutputField itemOffsetField;

        OffsetItem(TiffOutputItem item, TiffOutputField itemOffsetField) {
            this.itemOffsetField = itemOffsetField;
            this.item = item;
        }
    }
}


/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.imaging.formats.tiff.write;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteOrder;
import java.util.List;
import org.apache.commons.imaging.ImageWriteException;
import org.apache.commons.imaging.common.BinaryOutputStream;
import org.apache.commons.imaging.formats.tiff.write.TiffImageWriterBase;
import org.apache.commons.imaging.formats.tiff.write.TiffOutputItem;
import org.apache.commons.imaging.formats.tiff.write.TiffOutputSet;
import org.apache.commons.imaging.formats.tiff.write.TiffOutputSummary;

public class TiffImageWriterLossy
extends TiffImageWriterBase {
    public TiffImageWriterLossy() {
    }

    public TiffImageWriterLossy(ByteOrder byteOrder) {
        super(byteOrder);
    }

    @Override
    public void write(OutputStream os, TiffOutputSet outputSet) throws IOException, ImageWriteException {
        TiffOutputSummary outputSummary = this.validateDirectories(outputSet);
        List<TiffOutputItem> outputItems = outputSet.getOutputItems(outputSummary);
        this.updateOffsetsStep(outputItems);
        outputSummary.updateOffsets(this.byteOrder);
        BinaryOutputStream bos = new BinaryOutputStream(os, this.byteOrder);
        this.writeStep(bos, outputItems);
    }

    private void updateOffsetsStep(List<TiffOutputItem> outputItems) {
        int offset = 8;
        for (TiffOutputItem outputItem : outputItems) {
            outputItem.setOffset(offset);
            int itemLength = outputItem.getItemLength();
            offset += itemLength;
            int remainder = TiffImageWriterLossy.imageDataPaddingLength(itemLength);
            offset += remainder;
        }
    }

    private void writeStep(BinaryOutputStream bos, List<TiffOutputItem> outputItems) throws IOException, ImageWriteException {
        this.writeImageFileHeader(bos);
        for (TiffOutputItem outputItem : outputItems) {
            outputItem.writeItem(bos);
            int length = outputItem.getItemLength();
            int remainder = TiffImageWriterLossy.imageDataPaddingLength(length);
            for (int j = 0; j < remainder; ++j) {
                bos.write(0);
            }
        }
    }
}


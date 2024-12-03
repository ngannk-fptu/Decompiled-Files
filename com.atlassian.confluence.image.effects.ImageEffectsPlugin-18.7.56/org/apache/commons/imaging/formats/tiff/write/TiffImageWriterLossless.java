/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.imaging.formats.tiff.write;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.imaging.FormatCompliance;
import org.apache.commons.imaging.ImageReadException;
import org.apache.commons.imaging.ImageWriteException;
import org.apache.commons.imaging.common.BinaryOutputStream;
import org.apache.commons.imaging.common.bytesource.ByteSourceArray;
import org.apache.commons.imaging.formats.tiff.JpegImageData;
import org.apache.commons.imaging.formats.tiff.TiffContents;
import org.apache.commons.imaging.formats.tiff.TiffDirectory;
import org.apache.commons.imaging.formats.tiff.TiffElement;
import org.apache.commons.imaging.formats.tiff.TiffField;
import org.apache.commons.imaging.formats.tiff.TiffImageData;
import org.apache.commons.imaging.formats.tiff.TiffReader;
import org.apache.commons.imaging.formats.tiff.constants.ExifTagConstants;
import org.apache.commons.imaging.formats.tiff.write.TiffImageWriterBase;
import org.apache.commons.imaging.formats.tiff.write.TiffImageWriterLossy;
import org.apache.commons.imaging.formats.tiff.write.TiffOutputDirectory;
import org.apache.commons.imaging.formats.tiff.write.TiffOutputField;
import org.apache.commons.imaging.formats.tiff.write.TiffOutputItem;
import org.apache.commons.imaging.formats.tiff.write.TiffOutputSet;
import org.apache.commons.imaging.formats.tiff.write.TiffOutputSummary;

public class TiffImageWriterLossless
extends TiffImageWriterBase {
    private final byte[] exifBytes;
    private static final Comparator<TiffElement> ELEMENT_SIZE_COMPARATOR = (e1, e2) -> e1.length - e2.length;
    private static final Comparator<TiffOutputItem> ITEM_SIZE_COMPARATOR = (e1, e2) -> e1.getItemLength() - e2.getItemLength();

    public TiffImageWriterLossless(byte[] exifBytes) {
        this.exifBytes = exifBytes;
    }

    public TiffImageWriterLossless(ByteOrder byteOrder, byte[] exifBytes) {
        super(byteOrder);
        this.exifBytes = exifBytes;
    }

    private List<TiffElement> analyzeOldTiff(Map<Integer, TiffOutputField> frozenFields) throws ImageWriteException, IOException {
        try {
            ByteSourceArray byteSource = new ByteSourceArray(this.exifBytes);
            Map<String, Object> params = null;
            FormatCompliance formatCompliance = FormatCompliance.getDefault();
            TiffContents contents = new TiffReader(false).readContents(byteSource, params, formatCompliance);
            ArrayList<TiffElement> elements = new ArrayList<TiffElement>();
            List<TiffDirectory> directories = contents.directories;
            for (TiffDirectory directory : directories) {
                TiffImageData tiffImageData;
                elements.add(directory);
                for (TiffField field : directory.getDirectoryEntries()) {
                    TiffElement oversizeValue = field.getOversizeValueElement();
                    if (oversizeValue == null) continue;
                    TiffOutputField frozenField = frozenFields.get(field.getTag());
                    if (frozenField != null && frozenField.getSeperateValue() != null && frozenField.bytesEqual(field.getByteArrayValue())) {
                        frozenField.getSeperateValue().setOffset(field.getOffset());
                        continue;
                    }
                    elements.add(oversizeValue);
                }
                JpegImageData jpegImageData = directory.getJpegImageData();
                if (jpegImageData != null) {
                    elements.add(jpegImageData);
                }
                if ((tiffImageData = directory.getTiffImageData()) == null) continue;
                TiffElement.DataElement[] data = tiffImageData.getImageData();
                Collections.addAll(elements, data);
            }
            Collections.sort(elements, TiffElement.COMPARATOR);
            ArrayList<TiffElement> rewritableElements = new ArrayList<TiffElement>();
            int TOLERANCE = 3;
            TiffElement start = null;
            long index = -1L;
            for (TiffElement element : elements) {
                long lastElementByte = element.offset + (long)element.length;
                if (start == null) {
                    start = element;
                    index = lastElementByte;
                    continue;
                }
                if (element.offset - index > 3L) {
                    rewritableElements.add(new TiffElement.Stub(start.offset, (int)(index - start.offset)));
                    start = element;
                    index = lastElementByte;
                    continue;
                }
                index = lastElementByte;
            }
            if (null != start) {
                rewritableElements.add(new TiffElement.Stub(start.offset, (int)(index - start.offset)));
            }
            return rewritableElements;
        }
        catch (ImageReadException e) {
            throw new ImageWriteException(e.getMessage(), e);
        }
    }

    @Override
    public void write(OutputStream os, TiffOutputSet outputSet) throws IOException, ImageWriteException {
        HashMap<Integer, TiffOutputField> frozenFields = new HashMap<Integer, TiffOutputField>();
        TiffOutputField makerNoteField = outputSet.findField(ExifTagConstants.EXIF_TAG_MAKER_NOTE);
        if (makerNoteField != null && makerNoteField.getSeperateValue() != null) {
            frozenFields.put(ExifTagConstants.EXIF_TAG_MAKER_NOTE.tag, makerNoteField);
        }
        List<TiffElement> analysis = this.analyzeOldTiff(frozenFields);
        int oldLength = this.exifBytes.length;
        if (analysis.isEmpty()) {
            throw new ImageWriteException("Couldn't analyze old tiff data.");
        }
        if (analysis.size() == 1) {
            TiffElement onlyElement = analysis.get(0);
            if (onlyElement.offset == 8L && onlyElement.offset + (long)onlyElement.length + 8L == (long)oldLength) {
                new TiffImageWriterLossy(this.byteOrder).write(os, outputSet);
                return;
            }
        }
        HashMap<Long, TiffOutputField> frozenFieldOffsets = new HashMap<Long, TiffOutputField>();
        for (Map.Entry entry : frozenFields.entrySet()) {
            TiffOutputField frozenField = (TiffOutputField)entry.getValue();
            if (frozenField.getSeperateValue().getOffset() == -1L) continue;
            frozenFieldOffsets.put(frozenField.getSeperateValue().getOffset(), frozenField);
        }
        TiffOutputSummary outputSummary = this.validateDirectories(outputSet);
        List<TiffOutputItem> allOutputItems = outputSet.getOutputItems(outputSummary);
        ArrayList<TiffOutputItem> outputItems = new ArrayList<TiffOutputItem>();
        for (TiffOutputItem outputItem : allOutputItems) {
            if (frozenFieldOffsets.containsKey(outputItem.getOffset())) continue;
            outputItems.add(outputItem);
        }
        long outputLength = this.updateOffsetsStep(analysis, outputItems);
        outputSummary.updateOffsets(this.byteOrder);
        this.writeStep(os, outputSet, analysis, outputItems, outputLength);
    }

    private long updateOffsetsStep(List<TiffElement> analysis, List<TiffOutputItem> outputItems) {
        long overflowIndex = this.exifBytes.length;
        ArrayList<TiffElement> unusedElements = new ArrayList<TiffElement>(analysis);
        Collections.sort(unusedElements, TiffElement.COMPARATOR);
        Collections.reverse(unusedElements);
        while (!unusedElements.isEmpty()) {
            TiffElement element = (TiffElement)unusedElements.get(0);
            long elementEnd = element.offset + (long)element.length;
            if (elementEnd != overflowIndex) break;
            overflowIndex -= (long)element.length;
            unusedElements.remove(0);
        }
        Collections.sort(unusedElements, ELEMENT_SIZE_COMPARATOR);
        Collections.reverse(unusedElements);
        ArrayList<TiffOutputItem> unplacedItems = new ArrayList<TiffOutputItem>(outputItems);
        Collections.sort(unplacedItems, ITEM_SIZE_COMPARATOR);
        Collections.reverse(unplacedItems);
        while (!unplacedItems.isEmpty()) {
            TiffOutputItem outputItem = (TiffOutputItem)unplacedItems.remove(0);
            int outputItemLength = outputItem.getItemLength();
            TiffElement bestFit = null;
            for (TiffElement element : unusedElements) {
                if (element.length < outputItemLength) break;
                bestFit = element;
            }
            if (null == bestFit) {
                if ((overflowIndex & 1L) != 0L) {
                    ++overflowIndex;
                }
                outputItem.setOffset(overflowIndex);
                overflowIndex += (long)outputItemLength;
                continue;
            }
            long offset = bestFit.offset;
            if ((offset & 1L) != 0L) {
                ++offset;
            }
            outputItem.setOffset(offset);
            unusedElements.remove(bestFit);
            if (bestFit.length <= outputItemLength) continue;
            long excessOffset = bestFit.offset + (long)outputItemLength;
            int excessLength = bestFit.length - outputItemLength;
            unusedElements.add(new TiffElement.Stub(excessOffset, excessLength));
            Collections.sort(unusedElements, ELEMENT_SIZE_COMPARATOR);
            Collections.reverse(unusedElements);
        }
        return overflowIndex;
    }

    private void writeStep(OutputStream os, TiffOutputSet outputSet, List<TiffElement> analysis, List<TiffOutputItem> outputItems, long outputLength) throws IOException, ImageWriteException {
        TiffOutputDirectory rootDirectory = outputSet.getRootDirectory();
        byte[] output = new byte[(int)outputLength];
        System.arraycopy(this.exifBytes, 0, output, 0, Math.min(this.exifBytes.length, output.length));
        BufferOutputStream headerStream = new BufferOutputStream(output, 0);
        BinaryOutputStream headerBinaryStream = new BinaryOutputStream(headerStream, this.byteOrder);
        this.writeImageFileHeader(headerBinaryStream, rootDirectory.getOffset());
        for (TiffElement element : analysis) {
            Arrays.fill(output, (int)element.offset, (int)Math.min(element.offset + (long)element.length, (long)output.length), (byte)0);
        }
        for (TiffOutputItem outputItem : outputItems) {
            BinaryOutputStream bos = new BinaryOutputStream(new BufferOutputStream(output, (int)outputItem.getOffset()), this.byteOrder);
            Throwable throwable = null;
            try {
                outputItem.writeItem(bos);
            }
            catch (Throwable throwable2) {
                throwable = throwable2;
                throw throwable2;
            }
            finally {
                if (bos == null) continue;
                if (throwable != null) {
                    try {
                        bos.close();
                    }
                    catch (Throwable throwable3) {
                        throwable.addSuppressed(throwable3);
                    }
                    continue;
                }
                bos.close();
            }
        }
        os.write(output);
    }

    private static class BufferOutputStream
    extends OutputStream {
        private final byte[] buffer;
        private int index;

        BufferOutputStream(byte[] buffer, int index) {
            this.buffer = buffer;
            this.index = index;
        }

        @Override
        public void write(int b) throws IOException {
            if (this.index >= this.buffer.length) {
                throw new IOException("Buffer overflow.");
            }
            this.buffer[this.index++] = (byte)b;
        }

        @Override
        public void write(byte[] b, int off, int len) throws IOException {
            if (this.index + len > this.buffer.length) {
                throw new IOException("Buffer overflow.");
            }
            System.arraycopy(b, off, this.buffer, this.index, len);
            this.index += len;
        }
    }
}


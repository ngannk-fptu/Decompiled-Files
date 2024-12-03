/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.imaging.formats.tiff;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import org.apache.commons.imaging.FormatCompliance;
import org.apache.commons.imaging.ImageReadException;
import org.apache.commons.imaging.common.BinaryFileParser;
import org.apache.commons.imaging.common.BinaryFunctions;
import org.apache.commons.imaging.common.ByteConversions;
import org.apache.commons.imaging.common.bytesource.ByteSource;
import org.apache.commons.imaging.common.bytesource.ByteSourceFile;
import org.apache.commons.imaging.formats.tiff.JpegImageData;
import org.apache.commons.imaging.formats.tiff.TiffContents;
import org.apache.commons.imaging.formats.tiff.TiffDirectory;
import org.apache.commons.imaging.formats.tiff.TiffElement;
import org.apache.commons.imaging.formats.tiff.TiffField;
import org.apache.commons.imaging.formats.tiff.TiffHeader;
import org.apache.commons.imaging.formats.tiff.TiffImageData;
import org.apache.commons.imaging.formats.tiff.constants.ExifTagConstants;
import org.apache.commons.imaging.formats.tiff.constants.TiffTagConstants;
import org.apache.commons.imaging.formats.tiff.fieldtypes.FieldType;
import org.apache.commons.imaging.formats.tiff.taginfos.TagInfoDirectory;

public class TiffReader
extends BinaryFileParser {
    private final boolean strict;

    public TiffReader(boolean strict) {
        this.strict = strict;
    }

    private TiffHeader readTiffHeader(ByteSource byteSource) throws ImageReadException, IOException {
        try (InputStream is = byteSource.getInputStream();){
            TiffHeader tiffHeader = this.readTiffHeader(is);
            return tiffHeader;
        }
    }

    private ByteOrder getTiffByteOrder(int byteOrderByte) throws ImageReadException {
        if (byteOrderByte == 73) {
            return ByteOrder.LITTLE_ENDIAN;
        }
        if (byteOrderByte == 77) {
            return ByteOrder.BIG_ENDIAN;
        }
        throw new ImageReadException("Invalid TIFF byte order " + (0xFF & byteOrderByte));
    }

    private TiffHeader readTiffHeader(InputStream is) throws ImageReadException, IOException {
        byte byteOrder2;
        byte byteOrder1 = BinaryFunctions.readByte("BYTE_ORDER_1", is, "Not a Valid TIFF File");
        if (byteOrder1 != (byteOrder2 = BinaryFunctions.readByte("BYTE_ORDER_2", is, "Not a Valid TIFF File"))) {
            throw new ImageReadException("Byte Order bytes don't match (" + byteOrder1 + ", " + byteOrder2 + ").");
        }
        ByteOrder byteOrder = this.getTiffByteOrder(byteOrder1);
        this.setByteOrder(byteOrder);
        int tiffVersion = BinaryFunctions.read2Bytes("tiffVersion", is, "Not a Valid TIFF File", this.getByteOrder());
        if (tiffVersion != 42) {
            throw new ImageReadException("Unknown Tiff Version: " + tiffVersion);
        }
        long offsetToFirstIFD = 0xFFFFFFFFL & (long)BinaryFunctions.read4Bytes("offsetToFirstIFD", is, "Not a Valid TIFF File", this.getByteOrder());
        BinaryFunctions.skipBytes(is, offsetToFirstIFD - 8L, "Not a Valid TIFF File: couldn't find IFDs");
        return new TiffHeader(byteOrder, tiffVersion, offsetToFirstIFD);
    }

    private void readDirectories(ByteSource byteSource, FormatCompliance formatCompliance, Listener listener) throws ImageReadException, IOException {
        TiffHeader tiffHeader = this.readTiffHeader(byteSource);
        if (!listener.setTiffHeader(tiffHeader)) {
            return;
        }
        long offset = tiffHeader.offsetToFirstIFD;
        boolean dirType = false;
        ArrayList<Number> visited = new ArrayList<Number>();
        this.readDirectory(byteSource, offset, 0, formatCompliance, listener, visited);
    }

    private boolean readDirectory(ByteSource byteSource, long offset, int dirType, FormatCompliance formatCompliance, Listener listener, List<Number> visited) throws ImageReadException, IOException {
        boolean ignoreNextDirectory = false;
        return this.readDirectory(byteSource, offset, dirType, formatCompliance, listener, false, visited);
    }

    private boolean readDirectory(ByteSource byteSource, long directoryOffset, int dirType, FormatCompliance formatCompliance, Listener listener, boolean ignoreNextDirectory, List<Number> visited) throws ImageReadException, IOException {
        if (visited.contains(directoryOffset)) {
            return false;
        }
        visited.add(directoryOffset);
        try (InputStream is = byteSource.getInputStream();){
            int entryCount;
            if (directoryOffset >= byteSource.getLength()) {
                boolean bl = true;
                return bl;
            }
            BinaryFunctions.skipBytes(is, directoryOffset);
            ArrayList<TiffField> fields = new ArrayList<TiffField>();
            try {
                entryCount = BinaryFunctions.read2Bytes("DirectoryEntryCount", is, "Not a Valid TIFF File", this.getByteOrder());
            }
            catch (IOException e) {
                if (this.strict) {
                    throw e;
                }
                boolean bl = true;
                if (is != null) {
                    if (var10_9 != null) {
                        try {
                            is.close();
                        }
                        catch (Throwable throwable) {
                            var10_9.addSuppressed(throwable);
                        }
                    } else {
                        is.close();
                    }
                }
                return bl;
            }
            for (int i = 0; i < entryCount; ++i) {
                byte[] value;
                FieldType fieldType;
                int tag = BinaryFunctions.read2Bytes("Tag", is, "Not a Valid TIFF File", this.getByteOrder());
                int type = BinaryFunctions.read2Bytes("Type", is, "Not a Valid TIFF File", this.getByteOrder());
                long count = 0xFFFFFFFFL & (long)BinaryFunctions.read4Bytes("Count", is, "Not a Valid TIFF File", this.getByteOrder());
                byte[] offsetBytes = BinaryFunctions.readBytes("Offset", is, 4, "Not a Valid TIFF File");
                long offset = 0xFFFFFFFFL & (long)ByteConversions.toInt(offsetBytes, this.getByteOrder());
                if (tag == 0) continue;
                try {
                    fieldType = FieldType.getFieldType(type);
                }
                catch (ImageReadException imageReadEx) {
                    continue;
                }
                long valueLength = count * (long)fieldType.getSize();
                if (valueLength > 4L) {
                    if (offset < 0L || offset + valueLength > byteSource.getLength()) {
                        if (!this.strict) continue;
                        throw new IOException("Attempt to read byte range starting from " + offset + " of length " + valueLength + " which is outside the file's size of " + byteSource.getLength());
                    }
                    value = byteSource.getBlock(offset, (int)valueLength);
                } else {
                    value = offsetBytes;
                }
                TiffField field = new TiffField(tag, dirType, fieldType, count, offset, value, this.getByteOrder(), i);
                fields.add(field);
                if (listener.addField(field)) continue;
                boolean bl = true;
                return bl;
            }
            long nextDirectoryOffset = 0xFFFFFFFFL & (long)BinaryFunctions.read4Bytes("nextDirectoryOffset", is, "Not a Valid TIFF File", this.getByteOrder());
            TiffDirectory directory = new TiffDirectory(dirType, fields, directoryOffset, nextDirectoryOffset, this.getByteOrder());
            if (listener.readImageData()) {
                if (directory.hasTiffImageData()) {
                    TiffImageData rawImageData = this.getTiffRawImageData(byteSource, directory);
                    directory.setTiffImageData(rawImageData);
                }
                if (directory.hasJpegImageData()) {
                    JpegImageData rawJpegImageData = this.getJpegRawImageData(byteSource, directory);
                    directory.setJpegImageData(rawJpegImageData);
                }
            }
            if (!listener.addDirectory(directory)) {
                boolean rawJpegImageData = true;
                return rawJpegImageData;
            }
            if (listener.readOffsetDirectories()) {
                TagInfoDirectory[] offsetFields = new TagInfoDirectory[]{ExifTagConstants.EXIF_TAG_EXIF_OFFSET, ExifTagConstants.EXIF_TAG_GPSINFO, ExifTagConstants.EXIF_TAG_INTEROP_OFFSET};
                int[] directoryTypes = new int[]{-2, -3, -4};
                for (int i = 0; i < offsetFields.length; ++i) {
                    boolean subDirectoryRead;
                    TiffField field;
                    block47: {
                        TagInfoDirectory offsetField = offsetFields[i];
                        field = directory.findField(offsetField);
                        if (field == null) continue;
                        subDirectoryRead = false;
                        try {
                            long subDirectoryOffset = directory.getFieldValue(offsetField);
                            int subDirectoryType = directoryTypes[i];
                            subDirectoryRead = this.readDirectory(byteSource, subDirectoryOffset, subDirectoryType, formatCompliance, listener, true, visited);
                        }
                        catch (ImageReadException imageReadException) {
                            if (!this.strict) break block47;
                            throw imageReadException;
                        }
                    }
                    if (subDirectoryRead) continue;
                    fields.remove(field);
                }
            }
            if (!ignoreNextDirectory && directory.nextDirectoryOffset > 0L) {
                this.readDirectory(byteSource, directory.nextDirectoryOffset, dirType + 1, formatCompliance, listener, visited);
            }
            boolean bl = true;
            return bl;
        }
    }

    public TiffContents readFirstDirectory(ByteSource byteSource, Map<String, Object> params, boolean readImageData, FormatCompliance formatCompliance) throws ImageReadException, IOException {
        FirstDirectoryCollector collector = new FirstDirectoryCollector(readImageData);
        this.read(byteSource, params, formatCompliance, collector);
        TiffContents contents = collector.getContents();
        if (contents.directories.isEmpty()) {
            throw new ImageReadException("Image did not contain any directories.");
        }
        return contents;
    }

    public TiffContents readDirectories(ByteSource byteSource, boolean readImageData, FormatCompliance formatCompliance) throws ImageReadException, IOException {
        Map<String, Object> params = Collections.singletonMap("READ_THUMBNAILS", readImageData);
        Collector collector = new Collector(params);
        this.readDirectories(byteSource, formatCompliance, collector);
        TiffContents contents = collector.getContents();
        if (contents.directories.isEmpty()) {
            throw new ImageReadException("Image did not contain any directories.");
        }
        return contents;
    }

    public TiffContents readContents(ByteSource byteSource, Map<String, Object> params, FormatCompliance formatCompliance) throws ImageReadException, IOException {
        Collector collector = new Collector(params);
        this.read(byteSource, params, formatCompliance, collector);
        return collector.getContents();
    }

    public void read(ByteSource byteSource, Map<String, Object> params, FormatCompliance formatCompliance, Listener listener) throws ImageReadException, IOException {
        this.readDirectories(byteSource, formatCompliance, listener);
    }

    private TiffImageData getTiffRawImageData(ByteSource byteSource, TiffDirectory directory) throws ImageReadException, IOException {
        List<TiffDirectory.ImageDataElement> elements = directory.getTiffRawImageDataElements();
        TiffElement.DataElement[] data = new TiffImageData.Data[elements.size()];
        if (byteSource instanceof ByteSourceFile) {
            ByteSourceFile bsf = (ByteSourceFile)byteSource;
            for (int i = 0; i < elements.size(); ++i) {
                TiffDirectory.ImageDataElement element = elements.get(i);
                data[i] = new TiffImageData.ByteSourceData(element.offset, element.length, bsf);
            }
        } else {
            for (int i = 0; i < elements.size(); ++i) {
                TiffDirectory.ImageDataElement element = elements.get(i);
                byte[] bytes = byteSource.getBlock(element.offset, element.length);
                data[i] = new TiffImageData.Data(element.offset, element.length, bytes);
            }
        }
        if (directory.imageDataInStrips()) {
            TiffField rowsPerStripField = directory.findField(TiffTagConstants.TIFF_TAG_ROWS_PER_STRIP);
            int rowsPerStrip = Integer.MAX_VALUE;
            if (null != rowsPerStripField) {
                rowsPerStrip = rowsPerStripField.getIntValue();
            } else {
                TiffField imageHeight = directory.findField(TiffTagConstants.TIFF_TAG_IMAGE_LENGTH);
                if (imageHeight != null) {
                    rowsPerStrip = imageHeight.getIntValue();
                }
            }
            return new TiffImageData.Strips(data, rowsPerStrip);
        }
        TiffField tileWidthField = directory.findField(TiffTagConstants.TIFF_TAG_TILE_WIDTH);
        if (null == tileWidthField) {
            throw new ImageReadException("Can't find tile width field.");
        }
        int tileWidth = tileWidthField.getIntValue();
        TiffField tileLengthField = directory.findField(TiffTagConstants.TIFF_TAG_TILE_LENGTH);
        if (null == tileLengthField) {
            throw new ImageReadException("Can't find tile length field.");
        }
        int tileLength = tileLengthField.getIntValue();
        return new TiffImageData.Tiles(data, tileWidth, tileLength);
    }

    private JpegImageData getJpegRawImageData(ByteSource byteSource, TiffDirectory directory) throws ImageReadException, IOException {
        TiffDirectory.ImageDataElement element = directory.getJpegRawImageDataElement();
        long offset = element.offset;
        int length = element.length;
        if (offset + (long)length > byteSource.getLength()) {
            length = (int)(byteSource.getLength() - offset);
        }
        byte[] data = byteSource.getBlock(offset, length);
        if (this.strict && (length < 2 || ((data[data.length - 2] & 0xFF) << 8 | data[data.length - 1] & 0xFF) != 65497)) {
            throw new ImageReadException("JPEG EOI marker could not be found at expected location");
        }
        return new JpegImageData(offset, length, data);
    }

    private static class FirstDirectoryCollector
    extends Collector {
        private final boolean readImageData;

        FirstDirectoryCollector(boolean readImageData) {
            this.readImageData = readImageData;
        }

        @Override
        public boolean addDirectory(TiffDirectory directory) {
            super.addDirectory(directory);
            return false;
        }

        @Override
        public boolean readImageData() {
            return this.readImageData;
        }
    }

    private static class Collector
    implements Listener {
        private TiffHeader tiffHeader;
        private final List<TiffDirectory> directories = new ArrayList<TiffDirectory>();
        private final List<TiffField> fields = new ArrayList<TiffField>();
        private final boolean readThumbnails;

        Collector() {
            this(null);
        }

        Collector(Map<String, Object> params) {
            boolean tmpReadThumbnails = true;
            if (params != null && params.containsKey("READ_THUMBNAILS")) {
                tmpReadThumbnails = Boolean.TRUE.equals(params.get("READ_THUMBNAILS"));
            }
            this.readThumbnails = tmpReadThumbnails;
        }

        @Override
        public boolean setTiffHeader(TiffHeader tiffHeader) {
            this.tiffHeader = tiffHeader;
            return true;
        }

        @Override
        public boolean addDirectory(TiffDirectory directory) {
            this.directories.add(directory);
            return true;
        }

        @Override
        public boolean addField(TiffField field) {
            this.fields.add(field);
            return true;
        }

        @Override
        public boolean readImageData() {
            return this.readThumbnails;
        }

        @Override
        public boolean readOffsetDirectories() {
            return true;
        }

        public TiffContents getContents() {
            return new TiffContents(this.tiffHeader, this.directories, this.fields);
        }
    }

    public static interface Listener {
        public boolean setTiffHeader(TiffHeader var1);

        public boolean addDirectory(TiffDirectory var1);

        public boolean addField(TiffField var1);

        public boolean readImageData();

        public boolean readOffsetDirectories();
    }
}


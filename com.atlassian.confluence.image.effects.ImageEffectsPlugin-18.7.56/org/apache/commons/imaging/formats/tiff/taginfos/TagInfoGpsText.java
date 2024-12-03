/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.imaging.formats.tiff.taginfos;

import java.io.UnsupportedEncodingException;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;
import org.apache.commons.imaging.ImageReadException;
import org.apache.commons.imaging.ImageWriteException;
import org.apache.commons.imaging.common.BinaryFunctions;
import org.apache.commons.imaging.formats.tiff.TiffField;
import org.apache.commons.imaging.formats.tiff.constants.TiffDirectoryType;
import org.apache.commons.imaging.formats.tiff.fieldtypes.FieldType;
import org.apache.commons.imaging.formats.tiff.taginfos.TagInfo;
import org.apache.commons.imaging.internal.Debug;

public final class TagInfoGpsText
extends TagInfo {
    private static final TextEncoding TEXT_ENCODING_ASCII = new TextEncoding(new byte[]{65, 83, 67, 73, 73, 0, 0, 0}, "US-ASCII");
    private static final TextEncoding TEXT_ENCODING_JIS = new TextEncoding(new byte[]{74, 73, 83, 0, 0, 0, 0, 0}, "JIS");
    private static final TextEncoding TEXT_ENCODING_UNICODE_LE = new TextEncoding(new byte[]{85, 78, 73, 67, 79, 68, 69, 0}, "UTF-16LE");
    private static final TextEncoding TEXT_ENCODING_UNICODE_BE = new TextEncoding(new byte[]{85, 78, 73, 67, 79, 68, 69, 0}, "UTF-16BE");
    private static final TextEncoding TEXT_ENCODING_UNDEFINED = new TextEncoding(new byte[]{0, 0, 0, 0, 0, 0, 0, 0}, "ISO-8859-1");
    private static final TextEncoding[] TEXT_ENCODINGS = new TextEncoding[]{TEXT_ENCODING_ASCII, TEXT_ENCODING_JIS, TEXT_ENCODING_UNICODE_LE, TEXT_ENCODING_UNICODE_BE, TEXT_ENCODING_UNDEFINED};

    public TagInfoGpsText(String name, int tag, TiffDirectoryType exifDirectory) {
        super(name, tag, FieldType.UNDEFINED, -1, exifDirectory);
    }

    @Override
    public boolean isText() {
        return true;
    }

    @Override
    public byte[] encodeValue(FieldType fieldType, Object value, ByteOrder byteOrder) throws ImageWriteException {
        if (!(value instanceof String)) {
            throw new ImageWriteException("GPS text value not String", value);
        }
        String s = (String)value;
        try {
            byte[] asciiBytes = s.getBytes(TagInfoGpsText.TEXT_ENCODING_ASCII.encodingName);
            String decodedAscii = new String(asciiBytes, TagInfoGpsText.TEXT_ENCODING_ASCII.encodingName);
            if (decodedAscii.equals(s)) {
                byte[] result = new byte[asciiBytes.length + TagInfoGpsText.TEXT_ENCODING_ASCII.prefix.length];
                System.arraycopy(TagInfoGpsText.TEXT_ENCODING_ASCII.prefix, 0, result, 0, TagInfoGpsText.TEXT_ENCODING_ASCII.prefix.length);
                System.arraycopy(asciiBytes, 0, result, TagInfoGpsText.TEXT_ENCODING_ASCII.prefix.length, asciiBytes.length);
                return result;
            }
            TextEncoding encoding = byteOrder == ByteOrder.BIG_ENDIAN ? TEXT_ENCODING_UNICODE_BE : TEXT_ENCODING_UNICODE_LE;
            byte[] unicodeBytes = s.getBytes(encoding.encodingName);
            byte[] result = new byte[unicodeBytes.length + encoding.prefix.length];
            System.arraycopy(encoding.prefix, 0, result, 0, encoding.prefix.length);
            System.arraycopy(unicodeBytes, 0, result, encoding.prefix.length, unicodeBytes.length);
            return result;
        }
        catch (UnsupportedEncodingException e) {
            throw new ImageWriteException(e.getMessage(), e);
        }
    }

    @Override
    public String getValue(TiffField entry) throws ImageReadException {
        if (entry.getFieldType() == FieldType.ASCII) {
            Object object = FieldType.ASCII.getValue(entry);
            if (object instanceof String) {
                return (String)object;
            }
            if (object instanceof String[]) {
                return ((String[])object)[0];
            }
            throw new ImageReadException("Unexpected ASCII type decoded");
        }
        if (entry.getFieldType() != FieldType.UNDEFINED && entry.getFieldType() != FieldType.BYTE) {
            Debug.debug("entry.type: " + entry.getFieldType());
            Debug.debug("entry.directoryType: " + entry.getDirectoryType());
            Debug.debug("entry.type: " + entry.getDescriptionWithoutValue());
            Debug.debug("entry.type: " + entry.getFieldType());
            throw new ImageReadException("GPS text field not encoded as bytes.");
        }
        byte[] bytes = entry.getByteArrayValue();
        if (bytes.length < 8) {
            return new String(bytes, StandardCharsets.US_ASCII);
        }
        for (TextEncoding encoding : TEXT_ENCODINGS) {
            if (!BinaryFunctions.compareBytes(bytes, 0, encoding.prefix, 0, encoding.prefix.length)) continue;
            try {
                String decodedString = new String(bytes, encoding.prefix.length, bytes.length - encoding.prefix.length, encoding.encodingName);
                byte[] reEncodedBytes = decodedString.getBytes(encoding.encodingName);
                if (!BinaryFunctions.compareBytes(bytes, encoding.prefix.length, reEncodedBytes, 0, reEncodedBytes.length)) continue;
                return decodedString;
            }
            catch (UnsupportedEncodingException e) {
                throw new ImageReadException(e.getMessage(), e);
            }
        }
        return new String(bytes, StandardCharsets.US_ASCII);
    }

    private static final class TextEncoding {
        final byte[] prefix;
        public final String encodingName;

        TextEncoding(byte[] prefix, String encodingName) {
            this.prefix = prefix;
            this.encodingName = encodingName;
        }
    }
}


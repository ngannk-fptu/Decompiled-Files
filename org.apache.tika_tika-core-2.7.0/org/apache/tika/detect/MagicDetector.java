/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tika.detect;

import java.io.CharArrayWriter;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.tika.detect.Detector;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.mime.MediaType;

public class MagicDetector
implements Detector {
    private final MediaType type;
    private final int length;
    private final byte[] pattern;
    private final int patternLength;
    private final boolean isRegex;
    private final boolean isStringIgnoreCase;
    private final byte[] mask;
    private final int offsetRangeBegin;
    private final int offsetRangeEnd;

    public MagicDetector(MediaType type, byte[] pattern) {
        this(type, pattern, 0);
    }

    public MagicDetector(MediaType type, byte[] pattern, int offset) {
        this(type, pattern, null, offset, offset);
    }

    public MagicDetector(MediaType type, byte[] pattern, byte[] mask, int offsetRangeBegin, int offsetRangeEnd) {
        this(type, pattern, mask, false, offsetRangeBegin, offsetRangeEnd);
    }

    public MagicDetector(MediaType type, byte[] pattern, byte[] mask, boolean isRegex, int offsetRangeBegin, int offsetRangeEnd) {
        this(type, pattern, mask, isRegex, false, offsetRangeBegin, offsetRangeEnd);
    }

    public MagicDetector(MediaType type, byte[] pattern, byte[] mask, boolean isRegex, boolean isStringIgnoreCase, int offsetRangeBegin, int offsetRangeEnd) {
        if (type == null) {
            throw new IllegalArgumentException("Matching media type is null");
        }
        if (pattern == null) {
            throw new IllegalArgumentException("Magic match pattern is null");
        }
        if (offsetRangeBegin < 0 || offsetRangeEnd < offsetRangeBegin) {
            throw new IllegalArgumentException("Invalid offset range: [" + offsetRangeBegin + "," + offsetRangeEnd + "]");
        }
        this.type = type;
        this.isRegex = isRegex;
        this.isStringIgnoreCase = isStringIgnoreCase;
        this.patternLength = Math.max(pattern.length, mask != null ? mask.length : 0);
        this.length = this.isRegex ? 8192 : this.patternLength;
        this.mask = new byte[this.patternLength];
        this.pattern = new byte[this.patternLength];
        for (int i = 0; i < this.patternLength; ++i) {
            this.mask[i] = mask != null && i < mask.length ? mask[i] : -1;
            this.pattern[i] = i < pattern.length ? (byte)(pattern[i] & this.mask[i]) : (byte)0;
        }
        this.offsetRangeBegin = offsetRangeBegin;
        this.offsetRangeEnd = offsetRangeEnd;
    }

    public static MagicDetector parse(MediaType mediaType, String type, String offset, String value, String mask) {
        int start = 0;
        int end = 0;
        if (offset != null) {
            int colon = offset.indexOf(58);
            if (colon == -1) {
                end = start = Integer.parseInt(offset);
            } else {
                start = Integer.parseInt(offset.substring(0, colon));
                end = Integer.parseInt(offset.substring(colon + 1));
            }
        }
        byte[] patternBytes = MagicDetector.decodeValue(value, type);
        byte[] maskBytes = null;
        if (mask != null) {
            maskBytes = MagicDetector.decodeValue(mask, type);
        }
        return new MagicDetector(mediaType, patternBytes, maskBytes, type.equals("regex"), type.equals("stringignorecase"), start, end);
    }

    private static byte[] decodeValue(String value, String type) {
        if (value == null || type == null) {
            return null;
        }
        byte[] decoded = null;
        String tmpVal = null;
        int radix = 8;
        if (value.startsWith("0x")) {
            tmpVal = value.substring(2);
            radix = 16;
        } else {
            tmpVal = value;
            radix = 8;
        }
        switch (type) {
            case "string": 
            case "regex": 
            case "unicodeLE": 
            case "unicodeBE": {
                decoded = MagicDetector.decodeString(value, type);
                break;
            }
            case "stringignorecase": {
                decoded = MagicDetector.decodeString(value.toLowerCase(Locale.ROOT), type);
                break;
            }
            case "byte": {
                decoded = tmpVal.getBytes(StandardCharsets.UTF_8);
                break;
            }
            case "host16": 
            case "little16": {
                int i = Integer.parseInt(tmpVal, radix);
                decoded = new byte[]{(byte)(i & 0xFF), (byte)(i >> 8)};
                break;
            }
            case "big16": {
                int i = Integer.parseInt(tmpVal, radix);
                decoded = new byte[]{(byte)(i >> 8), (byte)(i & 0xFF)};
                break;
            }
            case "host32": 
            case "little32": {
                long i = Long.parseLong(tmpVal, radix);
                decoded = new byte[]{(byte)(i & 0xFFL), (byte)((i & 0xFF00L) >> 8), (byte)((i & 0xFF0000L) >> 16), (byte)((i & 0xFFFFFFFFFF000000L) >> 24)};
                break;
            }
            case "big32": {
                long i = Long.parseLong(tmpVal, radix);
                decoded = new byte[]{(byte)((i & 0xFFFFFFFFFF000000L) >> 24), (byte)((i & 0xFF0000L) >> 16), (byte)((i & 0xFF00L) >> 8), (byte)(i & 0xFFL)};
                break;
            }
        }
        return decoded;
    }

    private static byte[] decodeString(String value, String type) {
        byte[] bytes;
        if (value.startsWith("0x")) {
            byte[] vals = new byte[(value.length() - 2) / 2];
            for (int i = 0; i < vals.length; ++i) {
                vals[i] = (byte)Integer.parseInt(value.substring(2 + i * 2, 4 + i * 2), 16);
            }
            return vals;
        }
        CharArrayWriter decoded = new CharArrayWriter();
        for (int i = 0; i < value.length(); ++i) {
            if (value.charAt(i) == '\\') {
                int j;
                if (value.charAt(i + 1) == '\\') {
                    decoded.write(92);
                    ++i;
                    continue;
                }
                if (value.charAt(i + 1) == 'x') {
                    decoded.write(Integer.parseInt(value.substring(i + 2, i + 4), 16));
                    i += 3;
                    continue;
                }
                if (value.charAt(i + 1) == 'r') {
                    decoded.write(13);
                    ++i;
                    continue;
                }
                if (value.charAt(i + 1) == 'n') {
                    decoded.write(10);
                    ++i;
                    continue;
                }
                for (j = i + 1; j < i + 4 && j < value.length() && Character.isDigit(value.charAt(j)); ++j) {
                }
                decoded.write(Short.decode("0" + value.substring(i + 1, j)).byteValue());
                i = j - 1;
                continue;
            }
            decoded.write(value.charAt(i));
        }
        char[] chars = decoded.toCharArray();
        if ("unicodeLE".equals(type)) {
            bytes = new byte[chars.length * 2];
            for (int i = 0; i < chars.length; ++i) {
                bytes[i * 2] = (byte)(chars[i] & 0xFF);
                bytes[i * 2 + 1] = (byte)(chars[i] >> 8);
            }
        } else if ("unicodeBE".equals(type)) {
            bytes = new byte[chars.length * 2];
            for (int i = 0; i < chars.length; ++i) {
                bytes[i * 2] = (byte)(chars[i] >> 8);
                bytes[i * 2 + 1] = (byte)(chars[i] & 0xFF);
            }
        } else {
            bytes = new byte[chars.length];
            for (int i = 0; i < bytes.length; ++i) {
                bytes[i] = (byte)chars[i];
            }
        }
        return bytes;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public MediaType detect(InputStream input, Metadata metadata) throws IOException {
        if (input == null) {
            return MediaType.OCTET_STREAM;
        }
        input.mark(this.offsetRangeEnd + this.length);
        try {
            int offset = 0;
            while (offset < this.offsetRangeBegin) {
                long n = input.skip(this.offsetRangeBegin - offset);
                if (n > 0L) {
                    offset = (int)((long)offset + n);
                    continue;
                }
                if (input.read() != -1) {
                    ++offset;
                    continue;
                }
                MediaType mediaType = MediaType.OCTET_STREAM;
                return mediaType;
            }
            byte[] buffer = new byte[this.length + (this.offsetRangeEnd - this.offsetRangeBegin)];
            int n = input.read(buffer);
            if (n > 0) {
                offset += n;
            }
            while (n != -1 && offset < this.offsetRangeEnd + this.length) {
                int bufferOffset = offset - this.offsetRangeBegin;
                n = input.read(buffer, bufferOffset, buffer.length - bufferOffset);
                if (n <= 0) continue;
                offset += n;
            }
            if (this.isRegex) {
                int flags = 0;
                if (this.isStringIgnoreCase) {
                    flags = 2;
                }
                Pattern p = Pattern.compile(new String(this.pattern, StandardCharsets.UTF_8), flags);
                ByteBuffer bb = ByteBuffer.wrap(buffer);
                CharBuffer result = StandardCharsets.ISO_8859_1.decode(bb);
                Matcher m = p.matcher(result);
                boolean match = false;
                for (int i = 0; i <= this.offsetRangeEnd - this.offsetRangeBegin; ++i) {
                    m.region(i, this.length + i);
                    match = m.lookingAt();
                    if (!match) continue;
                    MediaType mediaType = this.type;
                    return mediaType;
                }
            } else {
                if (offset < this.offsetRangeBegin + this.length) {
                    MediaType flags = MediaType.OCTET_STREAM;
                    return flags;
                }
                for (int i = 0; i <= this.offsetRangeEnd - this.offsetRangeBegin; ++i) {
                    boolean match = true;
                    for (int j = 0; match && j < this.length; ++j) {
                        int masked = buffer[i + j] & this.mask[j];
                        if (this.isStringIgnoreCase) {
                            masked = Character.toLowerCase(masked);
                        }
                        match = masked == this.pattern[j];
                    }
                    if (!match) continue;
                    MediaType mediaType = this.type;
                    return mediaType;
                }
            }
            MediaType mediaType = MediaType.OCTET_STREAM;
            return mediaType;
        }
        finally {
            input.reset();
        }
    }

    public int getLength() {
        return this.patternLength;
    }

    public String toString() {
        return "Magic Detection for " + this.type + " looking for " + this.pattern.length + " bytes = " + this.pattern + " mask = " + this.mask;
    }
}


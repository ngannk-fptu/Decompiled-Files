/*
 * Decompiled with CFR 0.152.
 */
package nonapi.io.github.classgraph.utils;

public final class StringUtils {
    private StringUtils() {
    }

    public static String readString(byte[] arr, int startOffset, int numBytes, boolean replaceSlashWithDot, boolean stripLSemicolon) throws IllegalArgumentException {
        int c;
        int byteIdx;
        if ((long)startOffset < 0L || numBytes < 0 || startOffset + numBytes > arr.length) {
            throw new IllegalArgumentException("offset or numBytes out of range");
        }
        char[] chars = new char[numBytes];
        int charIdx = 0;
        for (byteIdx = 0; byteIdx < numBytes && (c = arr[startOffset + byteIdx] & 0xFF) <= 127; ++byteIdx) {
            chars[charIdx++] = (char)(replaceSlashWithDot && c == 47 ? 46 : c);
        }
        block6: while (byteIdx < numBytes) {
            c = arr[startOffset + byteIdx] & 0xFF;
            switch (c >> 4) {
                case 0: 
                case 1: 
                case 2: 
                case 3: 
                case 4: 
                case 5: 
                case 6: 
                case 7: {
                    ++byteIdx;
                    chars[charIdx++] = (char)(replaceSlashWithDot && c == 47 ? 46 : c);
                    continue block6;
                }
                case 12: 
                case 13: {
                    if ((byteIdx += 2) > numBytes) {
                        throw new IllegalArgumentException("Bad modified UTF8");
                    }
                    byte c2 = arr[startOffset + byteIdx - 1];
                    if ((c2 & 0xC0) != 128) {
                        throw new IllegalArgumentException("Bad modified UTF8");
                    }
                    int c3 = (c & 0x1F) << 6 | c2 & 0x3F;
                    chars[charIdx++] = (char)(replaceSlashWithDot && c3 == 47 ? 46 : c3);
                    continue block6;
                }
                case 14: {
                    if ((byteIdx += 3) > numBytes) {
                        throw new IllegalArgumentException("Bad modified UTF8");
                    }
                    byte c2 = arr[startOffset + byteIdx - 2];
                    int c3 = arr[startOffset + byteIdx - 1];
                    if ((c2 & 0xC0) != 128 || (c3 & 0xC0) != 128) {
                        throw new IllegalArgumentException("Bad modified UTF8");
                    }
                    int c4 = (c & 0xF) << 12 | (c2 & 0x3F) << 6 | c3 & 0x3F;
                    chars[charIdx++] = (char)(replaceSlashWithDot && c4 == 47 ? 46 : c4);
                    continue block6;
                }
            }
            throw new IllegalArgumentException("Bad modified UTF8");
        }
        if (charIdx == numBytes && !stripLSemicolon) {
            return new String(chars);
        }
        if (stripLSemicolon) {
            if (charIdx < 2 || chars[0] != 'L' || chars[charIdx - 1] != ';') {
                throw new IllegalArgumentException("Expected string to start with 'L' and end with ';', got \"" + new String(chars) + "\"");
            }
            return new String(chars, 1, charIdx - 2);
        }
        return new String(chars, 0, charIdx);
    }

    public static void join(StringBuilder buf, String addAtBeginning, String sep, String addAtEnd, Iterable<?> iterable) {
        if (!addAtBeginning.isEmpty()) {
            buf.append(addAtBeginning);
        }
        boolean first = true;
        for (Object item : iterable) {
            if (first) {
                first = false;
            } else {
                buf.append(sep);
            }
            buf.append(item == null ? "null" : item.toString());
        }
        if (!addAtEnd.isEmpty()) {
            buf.append(addAtEnd);
        }
    }

    public static String join(String sep, Iterable<?> iterable) {
        StringBuilder buf = new StringBuilder();
        StringUtils.join(buf, "", sep, "", iterable);
        return buf.toString();
    }

    public static String join(String sep, Object ... items) {
        StringBuilder buf = new StringBuilder();
        boolean first = true;
        for (Object item : items) {
            if (first) {
                first = false;
            } else {
                buf.append(sep);
            }
            buf.append(item.toString());
        }
        return buf.toString();
    }
}


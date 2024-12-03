/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.hwpf.util;

import java.nio.charset.Charset;
import java.util.Collections;
import java.util.Set;
import org.apache.poi.hwpf.util.LittleEndianCP950Reader;

public class DoubleByteUtil {
    public static final Charset BIG5 = Charset.forName("Big5");
    public static final Set<Charset> DOUBLE_BYTE_CHARSETS = Collections.singleton(BIG5);

    public static String cp950ToString(byte[] data, int offset, int lengthInBytes) {
        StringBuilder sb = new StringBuilder();
        try (LittleEndianCP950Reader reader = new LittleEndianCP950Reader(data, offset, lengthInBytes);){
            int c = reader.read();
            while (c != -1) {
                sb.append((char)c);
                c = reader.read();
            }
        }
        return sb.toString();
    }
}


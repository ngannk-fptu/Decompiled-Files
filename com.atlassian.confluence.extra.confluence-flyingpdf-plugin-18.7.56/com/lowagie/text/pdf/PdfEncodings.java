/*
 * Decompiled with CFR 0.152.
 */
package com.lowagie.text.pdf;

import com.lowagie.text.ExceptionConverter;
import com.lowagie.text.error_messages.MessageLocalization;
import com.lowagie.text.pdf.BaseFont;
import com.lowagie.text.pdf.ExtraEncoding;
import com.lowagie.text.pdf.IntHashtable;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Locale;
import java.util.StringTokenizer;
import java.util.concurrent.ConcurrentHashMap;

public class PdfEncodings {
    protected static final int CIDNONE = 0;
    protected static final int CIDRANGE = 1;
    protected static final int CIDCHAR = 2;
    static final char[] winansiByteToChar;
    static final char[] pdfEncodingByteToChar;
    static final IntHashtable winansi;
    static final IntHashtable pdfEncoding;
    static ConcurrentHashMap<String, ExtraEncoding> extraEncodings;
    static final ConcurrentHashMap<String, char[][]> cmaps;
    public static final byte[][] CRLF_CID_NEWLINE;

    public static final byte[] convertToBytes(String text, String encoding) {
        byte[] b;
        if (text == null) {
            return new byte[0];
        }
        if (encoding == null || encoding.length() == 0) {
            int len = text.length();
            byte[] b2 = new byte[len];
            for (int k = 0; k < len; ++k) {
                b2[k] = (byte)text.charAt(k);
            }
            return b2;
        }
        ExtraEncoding extra = extraEncodings.get(encoding.toLowerCase(Locale.ROOT));
        if (extra != null && (b = extra.charToByte(text, encoding)) != null) {
            return b;
        }
        IntHashtable hash = null;
        if (encoding.equals("Cp1252")) {
            hash = winansi;
        } else if (encoding.equals("PDF")) {
            hash = pdfEncoding;
        }
        if (hash != null) {
            char[] cc = text.toCharArray();
            int len = cc.length;
            int ptr = 0;
            byte[] b3 = new byte[len];
            int c = 0;
            for (int n : cc) {
                c = n < 128 || n > 160 && n <= 255 ? n : hash.get(n);
                if (c == 0) continue;
                b3[ptr++] = (byte)c;
            }
            if (ptr == len) {
                return b3;
            }
            byte[] b2 = new byte[ptr];
            System.arraycopy(b3, 0, b2, 0, ptr);
            return b2;
        }
        if (encoding.equals("UnicodeBig")) {
            char[] cc = text.toCharArray();
            int len = cc.length;
            byte[] b4 = new byte[cc.length * 2 + 2];
            b4[0] = -2;
            b4[1] = -1;
            int bptr = 2;
            for (char c : cc) {
                b4[bptr++] = (byte)(c >> 8);
                b4[bptr++] = (byte)(c & 0xFF);
            }
            return b4;
        }
        try {
            return text.getBytes(encoding);
        }
        catch (UnsupportedEncodingException e) {
            throw new ExceptionConverter(e);
        }
    }

    public static final byte[] convertToBytes(char char1, String encoding) {
        byte[] b;
        if (encoding == null || encoding.length() == 0) {
            return new byte[]{(byte)char1};
        }
        ExtraEncoding extra = extraEncodings.get(encoding.toLowerCase(Locale.ROOT));
        if (extra != null && (b = extra.charToByte((char)char1, encoding)) != null) {
            return b;
        }
        IntHashtable hash = null;
        if (encoding.equals("Cp1252")) {
            hash = winansi;
        } else if (encoding.equals("PDF")) {
            hash = pdfEncoding;
        }
        if (hash != null) {
            int c = 0;
            c = char1 < 128 || char1 > 160 && char1 <= 255 ? char1 : hash.get(char1);
            if (c != 0) {
                return new byte[]{(byte)c};
            }
            return new byte[0];
        }
        if (encoding.equals("UnicodeBig")) {
            byte[] b2 = new byte[]{-2, -1, (byte)(char1 >> 8), (byte)(char1 & 0xFF)};
            return b2;
        }
        try {
            return String.valueOf((char)char1).getBytes(encoding);
        }
        catch (UnsupportedEncodingException e) {
            throw new ExceptionConverter(e);
        }
    }

    public static final String convertToString(byte[] bytes, String encoding) {
        String text;
        if (bytes == null) {
            return "";
        }
        if (encoding == null || encoding.length() == 0) {
            char[] c = new char[bytes.length];
            for (int k = 0; k < bytes.length; ++k) {
                c[k] = (char)(bytes[k] & 0xFF);
            }
            return new String(c);
        }
        ExtraEncoding extra = extraEncodings.get(encoding.toLowerCase(Locale.ROOT));
        if (extra != null && (text = extra.byteToChar(bytes, encoding)) != null) {
            return text;
        }
        char[] ch = null;
        if (encoding.equals("Cp1252")) {
            ch = winansiByteToChar;
        } else if (encoding.equals("PDF")) {
            ch = pdfEncodingByteToChar;
        }
        if (ch != null) {
            int len = bytes.length;
            char[] c = new char[len];
            for (int k = 0; k < len; ++k) {
                c[k] = ch[bytes[k] & 0xFF];
            }
            return new String(c);
        }
        try {
            return new String(bytes, encoding);
        }
        catch (UnsupportedEncodingException e) {
            throw new ExceptionConverter(e);
        }
    }

    public static boolean isPdfDocEncoding(String text) {
        if (text == null) {
            return true;
        }
        int len = text.length();
        for (int k = 0; k < len; ++k) {
            char char1 = text.charAt(k);
            if (char1 < '\u0080' || char1 > '\u00a0' && char1 <= '\u00ff' || pdfEncoding.containsKey(char1)) continue;
            return false;
        }
        return true;
    }

    public static void clearCmap(String name) {
        if (name.length() == 0) {
            cmaps.clear();
        } else {
            cmaps.remove(name);
        }
    }

    public static void loadCmap(String name, byte[][] newline) {
        try {
            char[][] planes = null;
            planes = cmaps.get(name);
            if (planes == null) {
                planes = PdfEncodings.readCmap(name, newline);
                cmaps.putIfAbsent(name, planes);
            }
        }
        catch (IOException e) {
            throw new ExceptionConverter(e);
        }
    }

    public static String convertCmap(String name, byte[] seq) {
        return PdfEncodings.convertCmap(name, seq, 0, seq.length);
    }

    public static String convertCmap(String name, byte[] seq, int start, int length) {
        try {
            char[][] planes = null;
            planes = cmaps.get(name);
            if (planes == null) {
                planes = PdfEncodings.readCmap(name, (byte[][])null);
                cmaps.putIfAbsent(name, planes);
            }
            return PdfEncodings.decodeSequence(seq, start, length, planes);
        }
        catch (IOException e) {
            throw new ExceptionConverter(e);
        }
    }

    static String decodeSequence(byte[] seq, int start, int length, char[][] planes) {
        StringBuilder buf = new StringBuilder();
        int end = start + length;
        int currentPlane = 0;
        for (int k = start; k < end; ++k) {
            char[] plane = planes[currentPlane];
            int one = seq[k] & 0xFF;
            char cid = plane[one];
            if ((cid & 0x8000) == 0) {
                buf.append(cid);
                currentPlane = 0;
                continue;
            }
            currentPlane = cid & Short.MAX_VALUE;
        }
        return buf.toString();
    }

    static char[][] readCmap(String name, byte[][] newline) throws IOException {
        ArrayList<char[]> planes = new ArrayList<char[]>();
        planes.add(new char[256]);
        PdfEncodings.readCmap(name, planes);
        if (newline != null) {
            for (byte[] element : newline) {
                PdfEncodings.encodeSequence(element.length, element, Short.MAX_VALUE, planes);
            }
        }
        char[][] ret = new char[planes.size()][];
        return (char[][])planes.toArray((T[])ret);
    }

    static void readCmap(String name, ArrayList<char[]> planes) throws IOException {
        String fullName = "com/lowagie/text/pdf/fonts/cmaps/" + name;
        InputStream in = BaseFont.getResourceStream(fullName);
        if (in == null) {
            throw new IOException(MessageLocalization.getComposedMessage("the.cmap.1.was.not.found", name));
        }
        PdfEncodings.encodeStream(in, planes);
        in.close();
    }

    static void encodeStream(InputStream in, ArrayList<char[]> planes) throws IOException {
        BufferedReader rd = new BufferedReader(new InputStreamReader(in, StandardCharsets.ISO_8859_1));
        String line = null;
        int state = 0;
        byte[] seqs = new byte[7];
        block5: while ((line = rd.readLine()) != null) {
            if (line.length() < 6) continue;
            switch (state) {
                case 0: {
                    if (line.contains("begincidrange")) {
                        state = 1;
                        break;
                    }
                    if (line.contains("begincidchar")) {
                        state = 2;
                        break;
                    }
                    if (!line.contains("usecmap")) break;
                    StringTokenizer tk = new StringTokenizer(line);
                    String t = tk.nextToken();
                    PdfEncodings.readCmap(t.substring(1), planes);
                    break;
                }
                case 1: {
                    if (line.contains("endcidrange")) {
                        state = 0;
                        break;
                    }
                    StringTokenizer tk = new StringTokenizer(line);
                    String t = tk.nextToken();
                    int size = t.length() / 2 - 1;
                    long start = Long.parseLong(t.substring(1, t.length() - 1), 16);
                    t = tk.nextToken();
                    long end = Long.parseLong(t.substring(1, t.length() - 1), 16);
                    t = tk.nextToken();
                    int cid = Integer.parseInt(t);
                    for (long k = start; k <= end; ++k) {
                        PdfEncodings.breakLong(k, size, seqs);
                        PdfEncodings.encodeSequence(size, seqs, (char)cid, planes);
                        ++cid;
                    }
                    continue block5;
                }
                case 2: {
                    if (line.contains("endcidchar")) {
                        state = 0;
                        break;
                    }
                    StringTokenizer tk = new StringTokenizer(line);
                    String t = tk.nextToken();
                    int size = t.length() / 2 - 1;
                    long start = Long.parseLong(t.substring(1, t.length() - 1), 16);
                    t = tk.nextToken();
                    int cid = Integer.parseInt(t);
                    PdfEncodings.breakLong(start, size, seqs);
                    PdfEncodings.encodeSequence(size, seqs, (char)cid, planes);
                    break;
                }
            }
        }
    }

    static void breakLong(long n, int size, byte[] seqs) {
        for (int k = 0; k < size; ++k) {
            seqs[k] = (byte)(n >> (size - 1 - k) * 8);
        }
    }

    static void encodeSequence(int size, byte[] seqs, char cid, ArrayList<char[]> planes) {
        int one;
        --size;
        int nextPlane = 0;
        for (int idx = 0; idx < size; ++idx) {
            int one2;
            char[] plane = planes.get(nextPlane);
            char c = plane[one2 = seqs[idx] & 0xFF];
            if (c != '\u0000' && (c & 0x8000) == 0) {
                throw new RuntimeException(MessageLocalization.getComposedMessage("inconsistent.mapping"));
            }
            if (c == '\u0000') {
                planes.add(new char[256]);
                plane[one2] = c = (char)(planes.size() - 1 | 0x8000);
            }
            nextPlane = c & Short.MAX_VALUE;
        }
        char[] plane = planes.get(nextPlane);
        char c = plane[one = seqs[size] & 0xFF];
        if ((c & 0x8000) != 0) {
            throw new RuntimeException(MessageLocalization.getComposedMessage("inconsistent.mapping"));
        }
        plane[one] = cid;
    }

    public static void addExtraEncoding(String name, ExtraEncoding enc) {
        extraEncodings.putIfAbsent(name.toLowerCase(Locale.ROOT), enc);
    }

    static {
        char c;
        int k;
        winansiByteToChar = new char[]{'\u0000', '\u0001', '\u0002', '\u0003', '\u0004', '\u0005', '\u0006', '\u0007', '\b', '\t', '\n', '\u000b', '\f', '\r', '\u000e', '\u000f', '\u0010', '\u0011', '\u0012', '\u0013', '\u0014', '\u0015', '\u0016', '\u0017', '\u0018', '\u0019', '\u001a', '\u001b', '\u001c', '\u001d', '\u001e', '\u001f', ' ', '!', '\"', '#', '$', '%', '&', '\'', '(', ')', '*', '+', ',', '-', '.', '/', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', ':', ';', '<', '=', '>', '?', '@', 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z', '[', '\\', ']', '^', '_', '`', 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z', '{', '|', '}', '~', '\u007f', '\u20ac', '\ufffd', '\u201a', '\u0192', '\u201e', '\u2026', '\u2020', '\u2021', '\u02c6', '\u2030', '\u0160', '\u2039', '\u0152', '\ufffd', '\u017d', '\ufffd', '\ufffd', '\u2018', '\u2019', '\u201c', '\u201d', '\u2022', '\u2013', '\u2014', '\u02dc', '\u2122', '\u0161', '\u203a', '\u0153', '\ufffd', '\u017e', '\u0178', '\u00a0', '\u00a1', '\u00a2', '\u00a3', '\u00a4', '\u00a5', '\u00a6', '\u00a7', '\u00a8', '\u00a9', '\u00aa', '\u00ab', '\u00ac', '\u00ad', '\u00ae', '\u00af', '\u00b0', '\u00b1', '\u00b2', '\u00b3', '\u00b4', '\u00b5', '\u00b6', '\u00b7', '\u00b8', '\u00b9', '\u00ba', '\u00bb', '\u00bc', '\u00bd', '\u00be', '\u00bf', '\u00c0', '\u00c1', '\u00c2', '\u00c3', '\u00c4', '\u00c5', '\u00c6', '\u00c7', '\u00c8', '\u00c9', '\u00ca', '\u00cb', '\u00cc', '\u00cd', '\u00ce', '\u00cf', '\u00d0', '\u00d1', '\u00d2', '\u00d3', '\u00d4', '\u00d5', '\u00d6', '\u00d7', '\u00d8', '\u00d9', '\u00da', '\u00db', '\u00dc', '\u00dd', '\u00de', '\u00df', '\u00e0', '\u00e1', '\u00e2', '\u00e3', '\u00e4', '\u00e5', '\u00e6', '\u00e7', '\u00e8', '\u00e9', '\u00ea', '\u00eb', '\u00ec', '\u00ed', '\u00ee', '\u00ef', '\u00f0', '\u00f1', '\u00f2', '\u00f3', '\u00f4', '\u00f5', '\u00f6', '\u00f7', '\u00f8', '\u00f9', '\u00fa', '\u00fb', '\u00fc', '\u00fd', '\u00fe', '\u00ff'};
        pdfEncodingByteToChar = new char[]{'\u0000', '\u0001', '\u0002', '\u0003', '\u0004', '\u0005', '\u0006', '\u0007', '\b', '\t', '\n', '\u000b', '\f', '\r', '\u000e', '\u000f', '\u0010', '\u0011', '\u0012', '\u0013', '\u0014', '\u0015', '\u0016', '\u0017', '\u0018', '\u0019', '\u001a', '\u001b', '\u001c', '\u001d', '\u001e', '\u001f', ' ', '!', '\"', '#', '$', '%', '&', '\'', '(', ')', '*', '+', ',', '-', '.', '/', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', ':', ';', '<', '=', '>', '?', '@', 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z', '[', '\\', ']', '^', '_', '`', 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z', '{', '|', '}', '~', '\u007f', '\u2022', '\u2020', '\u2021', '\u2026', '\u2014', '\u2013', '\u0192', '\u2044', '\u2039', '\u203a', '\u2212', '\u2030', '\u201e', '\u201c', '\u201d', '\u2018', '\u2019', '\u201a', '\u2122', '\ufb01', '\ufb02', '\u0141', '\u0152', '\u0160', '\u0178', '\u017d', '\u0131', '\u0142', '\u0153', '\u0161', '\u017e', '\ufffd', '\u20ac', '\u00a1', '\u00a2', '\u00a3', '\u00a4', '\u00a5', '\u00a6', '\u00a7', '\u00a8', '\u00a9', '\u00aa', '\u00ab', '\u00ac', '\u00ad', '\u00ae', '\u00af', '\u00b0', '\u00b1', '\u00b2', '\u00b3', '\u00b4', '\u00b5', '\u00b6', '\u00b7', '\u00b8', '\u00b9', '\u00ba', '\u00bb', '\u00bc', '\u00bd', '\u00be', '\u00bf', '\u00c0', '\u00c1', '\u00c2', '\u00c3', '\u00c4', '\u00c5', '\u00c6', '\u00c7', '\u00c8', '\u00c9', '\u00ca', '\u00cb', '\u00cc', '\u00cd', '\u00ce', '\u00cf', '\u00d0', '\u00d1', '\u00d2', '\u00d3', '\u00d4', '\u00d5', '\u00d6', '\u00d7', '\u00d8', '\u00d9', '\u00da', '\u00db', '\u00dc', '\u00dd', '\u00de', '\u00df', '\u00e0', '\u00e1', '\u00e2', '\u00e3', '\u00e4', '\u00e5', '\u00e6', '\u00e7', '\u00e8', '\u00e9', '\u00ea', '\u00eb', '\u00ec', '\u00ed', '\u00ee', '\u00ef', '\u00f0', '\u00f1', '\u00f2', '\u00f3', '\u00f4', '\u00f5', '\u00f6', '\u00f7', '\u00f8', '\u00f9', '\u00fa', '\u00fb', '\u00fc', '\u00fd', '\u00fe', '\u00ff'};
        winansi = new IntHashtable();
        pdfEncoding = new IntHashtable();
        extraEncodings = new ConcurrentHashMap(200, 0.85f, 64);
        for (k = 128; k < 161; ++k) {
            c = winansiByteToChar[k];
            if (c == '\ufffd') continue;
            winansi.put(c, k);
        }
        for (k = 128; k < 161; ++k) {
            c = pdfEncodingByteToChar[k];
            if (c == '\ufffd') continue;
            pdfEncoding.put(c, k);
        }
        PdfEncodings.addExtraEncoding("Wingdings", new WingdingsConversion());
        PdfEncodings.addExtraEncoding("Symbol", new SymbolConversion(true));
        PdfEncodings.addExtraEncoding("ZapfDingbats", new SymbolConversion(false));
        PdfEncodings.addExtraEncoding("SymbolTT", new SymbolTTConversion());
        PdfEncodings.addExtraEncoding("Cp437", new Cp437Conversion());
        cmaps = new ConcurrentHashMap(100, 0.85f, 64);
        CRLF_CID_NEWLINE = new byte[][]{{10}, {13, 10}};
    }

    private static class SymbolTTConversion
    implements ExtraEncoding {
        private SymbolTTConversion() {
        }

        @Override
        public byte[] charToByte(char char1, String encoding) {
            if ((char1 & 0xFF00) == 0 || (char1 & 0xFF00) == 61440) {
                return new byte[]{(byte)char1};
            }
            return new byte[0];
        }

        @Override
        public byte[] charToByte(String text, String encoding) {
            char[] ch = text.toCharArray();
            byte[] b = new byte[ch.length];
            int ptr = 0;
            int len = ch.length;
            for (char c : ch) {
                if ((c & 0xFF00) != 0 && (c & 0xFF00) != 61440) continue;
                b[ptr++] = (byte)c;
            }
            if (ptr == len) {
                return b;
            }
            byte[] b2 = new byte[ptr];
            System.arraycopy(b, 0, b2, 0, ptr);
            return b2;
        }

        @Override
        public String byteToChar(byte[] b, String encoding) {
            return null;
        }
    }

    private static class SymbolConversion
    implements ExtraEncoding {
        private static final IntHashtable t1;
        private static final IntHashtable t2;
        private IntHashtable translation;
        private static final char[] table1;
        private static final char[] table2;

        SymbolConversion(boolean symbol) {
            this.translation = symbol ? t1 : t2;
        }

        @Override
        public byte[] charToByte(String text, String encoding) {
            char[] cc = text.toCharArray();
            byte[] b = new byte[cc.length];
            int ptr = 0;
            int len = cc.length;
            for (char c : cc) {
                byte v = (byte)this.translation.get(c);
                if (v == 0) continue;
                b[ptr++] = v;
            }
            if (ptr == len) {
                return b;
            }
            byte[] b2 = new byte[ptr];
            System.arraycopy(b, 0, b2, 0, ptr);
            return b2;
        }

        @Override
        public byte[] charToByte(char char1, String encoding) {
            byte v = (byte)this.translation.get(char1);
            if (v != 0) {
                return new byte[]{v};
            }
            return new byte[0];
        }

        @Override
        public String byteToChar(byte[] b, String encoding) {
            return null;
        }

        static {
            char v;
            int k;
            t1 = new IntHashtable();
            t2 = new IntHashtable();
            table1 = new char[]{' ', '!', '\u2200', '#', '\u2203', '%', '&', '\u220b', '(', ')', '*', '+', ',', '-', '.', '/', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', ':', ';', '<', '=', '>', '?', '\u2245', '\u0391', '\u0392', '\u03a7', '\u0394', '\u0395', '\u03a6', '\u0393', '\u0397', '\u0399', '\u03d1', '\u039a', '\u039b', '\u039c', '\u039d', '\u039f', '\u03a0', '\u0398', '\u03a1', '\u03a3', '\u03a4', '\u03a5', '\u03c2', '\u03a9', '\u039e', '\u03a8', '\u0396', '[', '\u2234', ']', '\u22a5', '_', '\u0305', '\u03b1', '\u03b2', '\u03c7', '\u03b4', '\u03b5', '\u03d5', '\u03b3', '\u03b7', '\u03b9', '\u03c6', '\u03ba', '\u03bb', '\u03bc', '\u03bd', '\u03bf', '\u03c0', '\u03b8', '\u03c1', '\u03c3', '\u03c4', '\u03c5', '\u03d6', '\u03c9', '\u03be', '\u03c8', '\u03b6', '{', '|', '}', '~', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u20ac', '\u03d2', '\u2032', '\u2264', '\u2044', '\u221e', '\u0192', '\u2663', '\u2666', '\u2665', '\u2660', '\u2194', '\u2190', '\u2191', '\u2192', '\u2193', '\u00b0', '\u00b1', '\u2033', '\u2265', '\u00d7', '\u221d', '\u2202', '\u2022', '\u00f7', '\u2260', '\u2261', '\u2248', '\u2026', '\u2502', '\u2500', '\u21b5', '\u2135', '\u2111', '\u211c', '\u2118', '\u2297', '\u2295', '\u2205', '\u2229', '\u222a', '\u2283', '\u2287', '\u2284', '\u2282', '\u2286', '\u2208', '\u2209', '\u2220', '\u2207', '\u00ae', '\u00a9', '\u2122', '\u220f', '\u221a', '\u2022', '\u00ac', '\u2227', '\u2228', '\u21d4', '\u21d0', '\u21d1', '\u21d2', '\u21d3', '\u25ca', '\u2329', '\u0000', '\u0000', '\u0000', '\u2211', '\u239b', '\u239c', '\u239d', '\u23a1', '\u23a2', '\u23a3', '\u23a7', '\u23a8', '\u23a9', '\u23aa', '\u0000', '\u232a', '\u222b', '\u2320', '\u23ae', '\u2321', '\u239e', '\u239f', '\u23a0', '\u23a4', '\u23a5', '\u23a6', '\u23ab', '\u23ac', '\u23ad', '\u0000'};
            table2 = new char[]{' ', '\u2701', '\u2702', '\u2703', '\u2704', '\u260e', '\u2706', '\u2707', '\u2708', '\u2709', '\u261b', '\u261e', '\u270c', '\u270d', '\u270e', '\u270f', '\u2710', '\u2711', '\u2712', '\u2713', '\u2714', '\u2715', '\u2716', '\u2717', '\u2718', '\u2719', '\u271a', '\u271b', '\u271c', '\u271d', '\u271e', '\u271f', '\u2720', '\u2721', '\u2722', '\u2723', '\u2724', '\u2725', '\u2726', '\u2727', '\u2605', '\u2729', '\u272a', '\u272b', '\u272c', '\u272d', '\u272e', '\u272f', '\u2730', '\u2731', '\u2732', '\u2733', '\u2734', '\u2735', '\u2736', '\u2737', '\u2738', '\u2739', '\u273a', '\u273b', '\u273c', '\u273d', '\u273e', '\u273f', '\u2740', '\u2741', '\u2742', '\u2743', '\u2744', '\u2745', '\u2746', '\u2747', '\u2748', '\u2749', '\u274a', '\u274b', '\u25cf', '\u274d', '\u25a0', '\u274f', '\u2750', '\u2751', '\u2752', '\u25b2', '\u25bc', '\u25c6', '\u2756', '\u25d7', '\u2758', '\u2759', '\u275a', '\u275b', '\u275c', '\u275d', '\u275e', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u2761', '\u2762', '\u2763', '\u2764', '\u2765', '\u2766', '\u2767', '\u2663', '\u2666', '\u2665', '\u2660', '\u2460', '\u2461', '\u2462', '\u2463', '\u2464', '\u2465', '\u2466', '\u2467', '\u2468', '\u2469', '\u2776', '\u2777', '\u2778', '\u2779', '\u277a', '\u277b', '\u277c', '\u277d', '\u277e', '\u277f', '\u2780', '\u2781', '\u2782', '\u2783', '\u2784', '\u2785', '\u2786', '\u2787', '\u2788', '\u2789', '\u278a', '\u278b', '\u278c', '\u278d', '\u278e', '\u278f', '\u2790', '\u2791', '\u2792', '\u2793', '\u2794', '\u2192', '\u2194', '\u2195', '\u2798', '\u2799', '\u279a', '\u279b', '\u279c', '\u279d', '\u279e', '\u279f', '\u27a0', '\u27a1', '\u27a2', '\u27a3', '\u27a4', '\u27a5', '\u27a6', '\u27a7', '\u27a8', '\u27a9', '\u27aa', '\u27ab', '\u27ac', '\u27ad', '\u27ae', '\u27af', '\u0000', '\u27b1', '\u27b2', '\u27b3', '\u27b4', '\u27b5', '\u27b6', '\u27b7', '\u27b8', '\u27b9', '\u27ba', '\u27bb', '\u27bc', '\u27bd', '\u27be', '\u0000'};
            for (k = 0; k < table1.length; ++k) {
                v = table1[k];
                if (v == '\u0000') continue;
                t1.put(v, k + 32);
            }
            for (k = 0; k < table2.length; ++k) {
                v = table2[k];
                if (v == '\u0000') continue;
                t2.put(v, k + 32);
            }
        }
    }

    private static class Cp437Conversion
    implements ExtraEncoding {
        private static IntHashtable c2b = new IntHashtable();
        private static final char[] table = new char[]{'\u00c7', '\u00fc', '\u00e9', '\u00e2', '\u00e4', '\u00e0', '\u00e5', '\u00e7', '\u00ea', '\u00eb', '\u00e8', '\u00ef', '\u00ee', '\u00ec', '\u00c4', '\u00c5', '\u00c9', '\u00e6', '\u00c6', '\u00f4', '\u00f6', '\u00f2', '\u00fb', '\u00f9', '\u00ff', '\u00d6', '\u00dc', '\u00a2', '\u00a3', '\u00a5', '\u20a7', '\u0192', '\u00e1', '\u00ed', '\u00f3', '\u00fa', '\u00f1', '\u00d1', '\u00aa', '\u00ba', '\u00bf', '\u2310', '\u00ac', '\u00bd', '\u00bc', '\u00a1', '\u00ab', '\u00bb', '\u2591', '\u2592', '\u2593', '\u2502', '\u2524', '\u2561', '\u2562', '\u2556', '\u2555', '\u2563', '\u2551', '\u2557', '\u255d', '\u255c', '\u255b', '\u2510', '\u2514', '\u2534', '\u252c', '\u251c', '\u2500', '\u253c', '\u255e', '\u255f', '\u255a', '\u2554', '\u2569', '\u2566', '\u2560', '\u2550', '\u256c', '\u2567', '\u2568', '\u2564', '\u2565', '\u2559', '\u2558', '\u2552', '\u2553', '\u256b', '\u256a', '\u2518', '\u250c', '\u2588', '\u2584', '\u258c', '\u2590', '\u2580', '\u03b1', '\u00df', '\u0393', '\u03c0', '\u03a3', '\u03c3', '\u00b5', '\u03c4', '\u03a6', '\u0398', '\u03a9', '\u03b4', '\u221e', '\u03c6', '\u03b5', '\u2229', '\u2261', '\u00b1', '\u2265', '\u2264', '\u2320', '\u2321', '\u00f7', '\u2248', '\u00b0', '\u2219', '\u00b7', '\u221a', '\u207f', '\u00b2', '\u25a0', '\u00a0'};

        private Cp437Conversion() {
        }

        @Override
        public byte[] charToByte(String text, String encoding) {
            char[] cc = text.toCharArray();
            byte[] b = new byte[cc.length];
            int ptr = 0;
            int len = cc.length;
            for (char c : cc) {
                if (c < '\u0080') {
                    b[ptr++] = (byte)c;
                    continue;
                }
                byte v = (byte)c2b.get(c);
                if (v == 0) continue;
                b[ptr++] = v;
            }
            if (ptr == len) {
                return b;
            }
            byte[] b2 = new byte[ptr];
            System.arraycopy(b, 0, b2, 0, ptr);
            return b2;
        }

        @Override
        public byte[] charToByte(char char1, String encoding) {
            if (char1 < '\u0080') {
                return new byte[]{(byte)char1};
            }
            byte v = (byte)c2b.get(char1);
            if (v != 0) {
                return new byte[]{v};
            }
            return new byte[0];
        }

        @Override
        public String byteToChar(byte[] b, String encoding) {
            int len = b.length;
            char[] cc = new char[len];
            int ptr = 0;
            for (byte b1 : b) {
                int c = b1 & 0xFF;
                if (c < 32) continue;
                if (c < 128) {
                    cc[ptr++] = (char)c;
                    continue;
                }
                char v = table[c - 128];
                cc[ptr++] = v;
            }
            return new String(cc, 0, ptr);
        }

        static {
            for (int k = 0; k < table.length; ++k) {
                c2b.put(table[k], k + 128);
            }
        }
    }

    private static class WingdingsConversion
    implements ExtraEncoding {
        private static final byte[] table = new byte[]{0, 35, 34, 0, 0, 0, 41, 62, 81, 42, 0, 0, 65, 63, 0, 0, 0, 0, 0, -4, 0, 0, 0, -5, 0, 0, 0, 0, 0, 0, 86, 0, 88, 89, 0, 0, 0, 0, 0, 0, 0, 0, -75, 0, 0, 0, 0, 0, -74, 0, 0, 0, -83, -81, -84, 0, 0, 0, 0, 0, 0, 0, 0, 124, 123, 0, 0, 0, 84, 0, 0, 0, 0, 0, 0, 0, 0, -90, 0, 0, 0, 113, 114, 0, 0, 0, 117, 0, 0, 0, 0, 0, 0, 125, 126, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, -116, -115, -114, -113, -112, -111, -110, -109, -108, -107, -127, -126, -125, -124, -123, -122, -121, -120, -119, -118, -116, -115, -114, -113, -112, -111, -110, -109, -108, -107, -24, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, -24, -40, 0, 0, -60, -58, 0, 0, -16, 0, 0, 0, 0, 0, 0, 0, 0, 0, -36, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};

        private WingdingsConversion() {
        }

        @Override
        public byte[] charToByte(char char1, String encoding) {
            byte v;
            if (char1 == ' ') {
                return new byte[]{(byte)char1};
            }
            if (char1 >= '\u2701' && char1 <= '\u27be' && (v = table[char1 - 9984]) != 0) {
                return new byte[]{v};
            }
            return new byte[0];
        }

        @Override
        public byte[] charToByte(String text, String encoding) {
            char[] cc = text.toCharArray();
            byte[] b = new byte[cc.length];
            int ptr = 0;
            int len = cc.length;
            for (char c : cc) {
                byte v;
                if (c == ' ') {
                    b[ptr++] = (byte)c;
                    continue;
                }
                if (c < '\u2701' || c > '\u27be' || (v = table[c - 9984]) == 0) continue;
                b[ptr++] = v;
            }
            if (ptr == len) {
                return b;
            }
            byte[] b2 = new byte[ptr];
            System.arraycopy(b, 0, b2, 0, ptr);
            return b2;
        }

        @Override
        public String byteToChar(byte[] b, String encoding) {
            return null;
        }
    }
}


/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.xml.bind.DatatypeConverterInterface
 */
package com.sun.xml.bind;

import com.sun.xml.bind.Messages;
import com.sun.xml.bind.WhiteSpaceProcessor;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Calendar;
import java.util.Collections;
import java.util.GregorianCalendar;
import java.util.Map;
import java.util.TimeZone;
import java.util.WeakHashMap;
import javax.xml.bind.DatatypeConverterInterface;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.namespace.NamespaceContext;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

@Deprecated
public final class DatatypeConverterImpl
implements DatatypeConverterInterface {
    @Deprecated
    public static final DatatypeConverterInterface theInstance = new DatatypeConverterImpl();
    private static final byte[] decodeMap = DatatypeConverterImpl.initDecodeMap();
    private static final byte PADDING = 127;
    private static final char[] encodeMap = DatatypeConverterImpl.initEncodeMap();
    private static final Map<ClassLoader, DatatypeFactory> DF_CACHE = Collections.synchronizedMap(new WeakHashMap());
    @Deprecated
    private static final char[] hexCode = "0123456789ABCDEF".toCharArray();

    protected DatatypeConverterImpl() {
    }

    public static BigInteger _parseInteger(CharSequence s) {
        return new BigInteger(DatatypeConverterImpl.removeOptionalPlus(WhiteSpaceProcessor.trim(s)).toString());
    }

    public static String _printInteger(BigInteger val) {
        return val.toString();
    }

    public static int _parseInt(CharSequence s) {
        int len = s.length();
        int sign = 1;
        int r = 0;
        for (int i = 0; i < len; ++i) {
            char ch = s.charAt(i);
            if (WhiteSpaceProcessor.isWhiteSpace(ch)) continue;
            if ('0' <= ch && ch <= '9') {
                r = r * 10 + (ch - 48);
                continue;
            }
            if (ch == '-') {
                sign = -1;
                continue;
            }
            if (ch == '+') continue;
            throw new NumberFormatException("Not a number: " + s);
        }
        return r * sign;
    }

    public static long _parseLong(CharSequence s) {
        return Long.parseLong(DatatypeConverterImpl.removeOptionalPlus(WhiteSpaceProcessor.trim(s)).toString());
    }

    public static short _parseShort(CharSequence s) {
        return (short)DatatypeConverterImpl._parseInt(s);
    }

    public static String _printShort(short val) {
        return String.valueOf(val);
    }

    public static BigDecimal _parseDecimal(CharSequence content) {
        if ((content = WhiteSpaceProcessor.trim(content)).length() <= 0) {
            return null;
        }
        return new BigDecimal(content.toString());
    }

    public static float _parseFloat(CharSequence _val) {
        String s = WhiteSpaceProcessor.trim(_val).toString();
        if (s.equals("NaN")) {
            return Float.NaN;
        }
        if (s.equals("INF")) {
            return Float.POSITIVE_INFINITY;
        }
        if (s.equals("-INF")) {
            return Float.NEGATIVE_INFINITY;
        }
        if (s.length() == 0 || !DatatypeConverterImpl.isDigitOrPeriodOrSign(s.charAt(0)) || !DatatypeConverterImpl.isDigitOrPeriodOrSign(s.charAt(s.length() - 1))) {
            throw new NumberFormatException();
        }
        return Float.parseFloat(s);
    }

    public static String _printFloat(float v) {
        if (Float.isNaN(v)) {
            return "NaN";
        }
        if (v == Float.POSITIVE_INFINITY) {
            return "INF";
        }
        if (v == Float.NEGATIVE_INFINITY) {
            return "-INF";
        }
        return String.valueOf(v);
    }

    public static double _parseDouble(CharSequence _val) {
        String val = WhiteSpaceProcessor.trim(_val).toString();
        if (val.equals("NaN")) {
            return Double.NaN;
        }
        if (val.equals("INF")) {
            return Double.POSITIVE_INFINITY;
        }
        if (val.equals("-INF")) {
            return Double.NEGATIVE_INFINITY;
        }
        if (val.length() == 0 || !DatatypeConverterImpl.isDigitOrPeriodOrSign(val.charAt(0)) || !DatatypeConverterImpl.isDigitOrPeriodOrSign(val.charAt(val.length() - 1))) {
            throw new NumberFormatException(val);
        }
        return Double.parseDouble(val);
    }

    public static Boolean _parseBoolean(CharSequence literal) {
        char ch;
        if (literal == null) {
            return null;
        }
        int i = 0;
        int len = literal.length();
        boolean value = false;
        if (literal.length() <= 0) {
            return null;
        }
        while (WhiteSpaceProcessor.isWhiteSpace(ch = literal.charAt(i++)) && i < len) {
        }
        int strIndex = 0;
        switch (ch) {
            case '1': {
                value = true;
                break;
            }
            case '0': {
                value = false;
                break;
            }
            case 't': {
                String strTrue = "rue";
                do {
                    ch = literal.charAt(i++);
                } while (strTrue.charAt(strIndex++) == ch && i < len && strIndex < 3);
                if (strIndex == 3) {
                    value = true;
                    break;
                }
                return false;
            }
            case 'f': {
                String strFalse = "alse";
                do {
                    ch = literal.charAt(i++);
                } while (strFalse.charAt(strIndex++) == ch && i < len && strIndex < 4);
                if (strIndex == 4) {
                    value = false;
                    break;
                }
                return false;
            }
        }
        if (i < len) {
            while (WhiteSpaceProcessor.isWhiteSpace(ch = literal.charAt(i++)) && i < len) {
            }
        }
        if (i == len) {
            return value;
        }
        return null;
    }

    public static String _printBoolean(boolean val) {
        return val ? "true" : "false";
    }

    public static byte _parseByte(CharSequence literal) {
        return (byte)DatatypeConverterImpl._parseInt(literal);
    }

    public static String _printByte(byte val) {
        return String.valueOf(val);
    }

    public static QName _parseQName(CharSequence text, NamespaceContext nsc) {
        String prefix;
        String localPart;
        String uri;
        int idx;
        int end;
        int start;
        int length = text.length();
        for (start = 0; start < length && WhiteSpaceProcessor.isWhiteSpace(text.charAt(start)); ++start) {
        }
        for (end = length; end > start && WhiteSpaceProcessor.isWhiteSpace(text.charAt(end - 1)); --end) {
        }
        if (end == start) {
            throw new IllegalArgumentException("input is empty");
        }
        for (idx = start + 1; idx < end && text.charAt(idx) != ':'; ++idx) {
        }
        if (idx == end) {
            uri = nsc.getNamespaceURI("");
            localPart = text.subSequence(start, end).toString();
            prefix = "";
        } else {
            prefix = text.subSequence(start, idx).toString();
            localPart = text.subSequence(idx + 1, end).toString();
            uri = nsc.getNamespaceURI(prefix);
            if (uri == null || uri.length() == 0) {
                throw new IllegalArgumentException("prefix " + prefix + " is not bound to a namespace");
            }
        }
        return new QName(uri, localPart, prefix);
    }

    public static GregorianCalendar _parseDateTime(CharSequence s) {
        String val = WhiteSpaceProcessor.trim(s).toString();
        return DatatypeConverterImpl.getDatatypeFactory().newXMLGregorianCalendar(val).toGregorianCalendar();
    }

    public static String _printDateTime(Calendar val) {
        return CalendarFormatter.doFormat("%Y-%M-%DT%h:%m:%s%z", val);
    }

    public static String _printDate(Calendar val) {
        return CalendarFormatter.doFormat("%Y-%M-%D" + "%z", val);
    }

    public static String _printInt(int val) {
        return String.valueOf(val);
    }

    public static String _printLong(long val) {
        return String.valueOf(val);
    }

    public static String _printDecimal(BigDecimal val) {
        return val.toPlainString();
    }

    public static String _printDouble(double v) {
        if (Double.isNaN(v)) {
            return "NaN";
        }
        if (v == Double.POSITIVE_INFINITY) {
            return "INF";
        }
        if (v == Double.NEGATIVE_INFINITY) {
            return "-INF";
        }
        return String.valueOf(v);
    }

    public static String _printQName(QName val, NamespaceContext nsc) {
        String prefix = nsc.getPrefix(val.getNamespaceURI());
        String localPart = val.getLocalPart();
        String qname = prefix == null || prefix.length() == 0 ? localPart : prefix + ':' + localPart;
        return qname;
    }

    private static byte[] initDecodeMap() {
        int i;
        byte[] map = new byte[128];
        for (i = 0; i < 128; ++i) {
            map[i] = -1;
        }
        for (i = 65; i <= 90; ++i) {
            map[i] = (byte)(i - 65);
        }
        for (i = 97; i <= 122; ++i) {
            map[i] = (byte)(i - 97 + 26);
        }
        for (i = 48; i <= 57; ++i) {
            map[i] = (byte)(i - 48 + 52);
        }
        map[43] = 62;
        map[47] = 63;
        map[61] = 127;
        return map;
    }

    private static int guessLength(String text) {
        int padSize;
        int j;
        int len = text.length();
        for (j = len - 1; j >= 0; --j) {
            byte code = decodeMap[text.charAt(j)];
            if (code == 127) continue;
            if (code != -1) break;
            return text.length() / 4 * 3;
        }
        if ((padSize = len - ++j) > 2) {
            return text.length() / 4 * 3;
        }
        return text.length() / 4 * 3 - padSize;
    }

    public static byte[] _parseBase64Binary(String text) {
        int buflen = DatatypeConverterImpl.guessLength(text);
        byte[] out = new byte[buflen];
        int o = 0;
        int len = text.length();
        byte[] quadruplet = new byte[4];
        int q = 0;
        for (int i = 0; i < len; ++i) {
            char ch = text.charAt(i);
            byte v = decodeMap[ch];
            if (v != -1) {
                quadruplet[q++] = v;
            }
            if (q != 4) continue;
            out[o++] = (byte)(quadruplet[0] << 2 | quadruplet[1] >> 4);
            if (quadruplet[2] != 127) {
                out[o++] = (byte)(quadruplet[1] << 4 | quadruplet[2] >> 2);
            }
            if (quadruplet[3] != 127) {
                out[o++] = (byte)(quadruplet[2] << 6 | quadruplet[3]);
            }
            q = 0;
        }
        if (buflen == o) {
            return out;
        }
        byte[] nb = new byte[o];
        System.arraycopy(out, 0, nb, 0, o);
        return nb;
    }

    private static char[] initEncodeMap() {
        int i;
        char[] map = new char[64];
        for (i = 0; i < 26; ++i) {
            map[i] = (char)(65 + i);
        }
        for (i = 26; i < 52; ++i) {
            map[i] = (char)(97 + (i - 26));
        }
        for (i = 52; i < 62; ++i) {
            map[i] = (char)(48 + (i - 52));
        }
        map[62] = 43;
        map[63] = 47;
        return map;
    }

    public static char encode(int i) {
        return encodeMap[i & 0x3F];
    }

    public static byte encodeByte(int i) {
        return (byte)encodeMap[i & 0x3F];
    }

    public static String _printBase64Binary(byte[] input) {
        return DatatypeConverterImpl._printBase64Binary(input, 0, input.length);
    }

    public static String _printBase64Binary(byte[] input, int offset, int len) {
        char[] buf = new char[(len + 2) / 3 * 4];
        int ptr = DatatypeConverterImpl._printBase64Binary(input, offset, len, buf, 0);
        assert (ptr == buf.length);
        return new String(buf);
    }

    public static int _printBase64Binary(byte[] input, int offset, int len, char[] buf, int ptr) {
        int remaining = len;
        int i = offset;
        while (remaining >= 3) {
            buf[ptr++] = DatatypeConverterImpl.encode(input[i] >> 2);
            buf[ptr++] = DatatypeConverterImpl.encode((input[i] & 3) << 4 | input[i + 1] >> 4 & 0xF);
            buf[ptr++] = DatatypeConverterImpl.encode((input[i + 1] & 0xF) << 2 | input[i + 2] >> 6 & 3);
            buf[ptr++] = DatatypeConverterImpl.encode(input[i + 2] & 0x3F);
            remaining -= 3;
            i += 3;
        }
        if (remaining == 1) {
            buf[ptr++] = DatatypeConverterImpl.encode(input[i] >> 2);
            buf[ptr++] = DatatypeConverterImpl.encode((input[i] & 3) << 4);
            buf[ptr++] = 61;
            buf[ptr++] = 61;
        }
        if (remaining == 2) {
            buf[ptr++] = DatatypeConverterImpl.encode(input[i] >> 2);
            buf[ptr++] = DatatypeConverterImpl.encode((input[i] & 3) << 4 | input[i + 1] >> 4 & 0xF);
            buf[ptr++] = DatatypeConverterImpl.encode((input[i + 1] & 0xF) << 2);
            buf[ptr++] = 61;
        }
        return ptr;
    }

    public static void _printBase64Binary(byte[] input, int offset, int len, XMLStreamWriter output) throws XMLStreamException {
        int remaining = len;
        char[] buf = new char[4];
        int i = offset;
        while (remaining >= 3) {
            buf[0] = DatatypeConverterImpl.encode(input[i] >> 2);
            buf[1] = DatatypeConverterImpl.encode((input[i] & 3) << 4 | input[i + 1] >> 4 & 0xF);
            buf[2] = DatatypeConverterImpl.encode((input[i + 1] & 0xF) << 2 | input[i + 2] >> 6 & 3);
            buf[3] = DatatypeConverterImpl.encode(input[i + 2] & 0x3F);
            output.writeCharacters(buf, 0, 4);
            remaining -= 3;
            i += 3;
        }
        if (remaining == 1) {
            buf[0] = DatatypeConverterImpl.encode(input[i] >> 2);
            buf[1] = DatatypeConverterImpl.encode((input[i] & 3) << 4);
            buf[2] = 61;
            buf[3] = 61;
            output.writeCharacters(buf, 0, 4);
        }
        if (remaining == 2) {
            buf[0] = DatatypeConverterImpl.encode(input[i] >> 2);
            buf[1] = DatatypeConverterImpl.encode((input[i] & 3) << 4 | input[i + 1] >> 4 & 0xF);
            buf[2] = DatatypeConverterImpl.encode((input[i + 1] & 0xF) << 2);
            buf[3] = 61;
            output.writeCharacters(buf, 0, 4);
        }
    }

    public static int _printBase64Binary(byte[] input, int offset, int len, byte[] out, int ptr) {
        byte[] buf = out;
        int remaining = len;
        int i = offset;
        while (remaining >= 3) {
            buf[ptr++] = DatatypeConverterImpl.encodeByte(input[i] >> 2);
            buf[ptr++] = DatatypeConverterImpl.encodeByte((input[i] & 3) << 4 | input[i + 1] >> 4 & 0xF);
            buf[ptr++] = DatatypeConverterImpl.encodeByte((input[i + 1] & 0xF) << 2 | input[i + 2] >> 6 & 3);
            buf[ptr++] = DatatypeConverterImpl.encodeByte(input[i + 2] & 0x3F);
            remaining -= 3;
            i += 3;
        }
        if (remaining == 1) {
            buf[ptr++] = DatatypeConverterImpl.encodeByte(input[i] >> 2);
            buf[ptr++] = DatatypeConverterImpl.encodeByte((input[i] & 3) << 4);
            buf[ptr++] = 61;
            buf[ptr++] = 61;
        }
        if (remaining == 2) {
            buf[ptr++] = DatatypeConverterImpl.encodeByte(input[i] >> 2);
            buf[ptr++] = DatatypeConverterImpl.encodeByte((input[i] & 3) << 4 | input[i + 1] >> 4 & 0xF);
            buf[ptr++] = DatatypeConverterImpl.encodeByte((input[i + 1] & 0xF) << 2);
            buf[ptr++] = 61;
        }
        return ptr;
    }

    private static CharSequence removeOptionalPlus(CharSequence s) {
        int len = s.length();
        if (len <= 1 || s.charAt(0) != '+') {
            return s;
        }
        char ch = (s = s.subSequence(1, len)).charAt(0);
        if ('0' <= ch && ch <= '9') {
            return s;
        }
        if ('.' == ch) {
            return s;
        }
        throw new NumberFormatException();
    }

    private static boolean isDigitOrPeriodOrSign(char ch) {
        if ('0' <= ch && ch <= '9') {
            return true;
        }
        return ch == '+' || ch == '-' || ch == '.';
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    public static DatatypeFactory getDatatypeFactory() {
        ClassLoader tccl = AccessController.doPrivileged(new PrivilegedAction<ClassLoader>(){

            @Override
            public ClassLoader run() {
                return Thread.currentThread().getContextClassLoader();
            }
        });
        DatatypeFactory df = DF_CACHE.get(tccl);
        if (df != null) return df;
        Class<DatatypeConverterImpl> clazz = DatatypeConverterImpl.class;
        synchronized (DatatypeConverterImpl.class) {
            df = DF_CACHE.get(tccl);
            if (df != null) return df;
            try {
                df = DatatypeFactory.newInstance();
            }
            catch (DatatypeConfigurationException e) {
                throw new Error(Messages.FAILED_TO_INITIALE_DATATYPE_FACTORY.format(new Object[0]), e);
            }
            DF_CACHE.put(tccl, df);
            // ** MonitorExit[var2_2] (shouldn't be in output)
            return df;
        }
    }

    @Deprecated
    public String parseString(String lexicalXSDString) {
        return lexicalXSDString;
    }

    @Deprecated
    public BigInteger parseInteger(String lexicalXSDInteger) {
        return DatatypeConverterImpl._parseInteger(lexicalXSDInteger);
    }

    @Deprecated
    public String printInteger(BigInteger val) {
        return DatatypeConverterImpl._printInteger(val);
    }

    @Deprecated
    public int parseInt(String s) {
        return DatatypeConverterImpl._parseInt(s);
    }

    @Deprecated
    public long parseLong(String lexicalXSLong) {
        return DatatypeConverterImpl._parseLong(lexicalXSLong);
    }

    @Deprecated
    public short parseShort(String lexicalXSDShort) {
        return DatatypeConverterImpl._parseShort(lexicalXSDShort);
    }

    @Deprecated
    public String printShort(short val) {
        return DatatypeConverterImpl._printShort(val);
    }

    @Deprecated
    public BigDecimal parseDecimal(String content) {
        return DatatypeConverterImpl._parseDecimal(content);
    }

    @Deprecated
    public float parseFloat(String lexicalXSDFloat) {
        return DatatypeConverterImpl._parseFloat(lexicalXSDFloat);
    }

    @Deprecated
    public String printFloat(float v) {
        return DatatypeConverterImpl._printFloat(v);
    }

    @Deprecated
    public double parseDouble(String lexicalXSDDouble) {
        return DatatypeConverterImpl._parseDouble(lexicalXSDDouble);
    }

    @Deprecated
    public boolean parseBoolean(String lexicalXSDBoolean) {
        Boolean b = DatatypeConverterImpl._parseBoolean(lexicalXSDBoolean);
        return b == null ? false : b;
    }

    @Deprecated
    public String printBoolean(boolean val) {
        return val ? "true" : "false";
    }

    @Deprecated
    public byte parseByte(String lexicalXSDByte) {
        return DatatypeConverterImpl._parseByte(lexicalXSDByte);
    }

    @Deprecated
    public String printByte(byte val) {
        return DatatypeConverterImpl._printByte(val);
    }

    @Deprecated
    public QName parseQName(String lexicalXSDQName, NamespaceContext nsc) {
        return DatatypeConverterImpl._parseQName(lexicalXSDQName, nsc);
    }

    @Deprecated
    public Calendar parseDateTime(String lexicalXSDDateTime) {
        return DatatypeConverterImpl._parseDateTime(lexicalXSDDateTime);
    }

    @Deprecated
    public String printDateTime(Calendar val) {
        return DatatypeConverterImpl._printDateTime(val);
    }

    @Deprecated
    public byte[] parseBase64Binary(String lexicalXSDBase64Binary) {
        return DatatypeConverterImpl._parseBase64Binary(lexicalXSDBase64Binary);
    }

    @Deprecated
    public byte[] parseHexBinary(String s) {
        int len = s.length();
        if (len % 2 != 0) {
            throw new IllegalArgumentException("hexBinary needs to be even-length: " + s);
        }
        byte[] out = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            int h = DatatypeConverterImpl.hexToBin(s.charAt(i));
            int l = DatatypeConverterImpl.hexToBin(s.charAt(i + 1));
            if (h == -1 || l == -1) {
                throw new IllegalArgumentException("contains illegal character for hexBinary: " + s);
            }
            out[i / 2] = (byte)(h * 16 + l);
        }
        return out;
    }

    @Deprecated
    private static int hexToBin(char ch) {
        if ('0' <= ch && ch <= '9') {
            return ch - 48;
        }
        if ('A' <= ch && ch <= 'F') {
            return ch - 65 + 10;
        }
        if ('a' <= ch && ch <= 'f') {
            return ch - 97 + 10;
        }
        return -1;
    }

    @Deprecated
    public String printHexBinary(byte[] data) {
        StringBuilder r = new StringBuilder(data.length * 2);
        for (byte b : data) {
            r.append(hexCode[b >> 4 & 0xF]);
            r.append(hexCode[b & 0xF]);
        }
        return r.toString();
    }

    @Deprecated
    public long parseUnsignedInt(String lexicalXSDUnsignedInt) {
        return DatatypeConverterImpl._parseLong(lexicalXSDUnsignedInt);
    }

    @Deprecated
    public String printUnsignedInt(long val) {
        return DatatypeConverterImpl._printLong(val);
    }

    @Deprecated
    public int parseUnsignedShort(String lexicalXSDUnsignedShort) {
        return DatatypeConverterImpl._parseInt(lexicalXSDUnsignedShort);
    }

    @Deprecated
    public Calendar parseTime(String lexicalXSDTime) {
        return DatatypeConverterImpl.getDatatypeFactory().newXMLGregorianCalendar(lexicalXSDTime).toGregorianCalendar();
    }

    @Deprecated
    public String printTime(Calendar val) {
        return CalendarFormatter.doFormat("%h:%m:%s%z", val);
    }

    @Deprecated
    public Calendar parseDate(String lexicalXSDDate) {
        return DatatypeConverterImpl.getDatatypeFactory().newXMLGregorianCalendar(lexicalXSDDate).toGregorianCalendar();
    }

    @Deprecated
    public String printDate(Calendar val) {
        return DatatypeConverterImpl._printDate(val);
    }

    @Deprecated
    public String parseAnySimpleType(String lexicalXSDAnySimpleType) {
        return lexicalXSDAnySimpleType;
    }

    @Deprecated
    public String printString(String val) {
        return val;
    }

    @Deprecated
    public String printInt(int val) {
        return DatatypeConverterImpl._printInt(val);
    }

    @Deprecated
    public String printLong(long val) {
        return DatatypeConverterImpl._printLong(val);
    }

    @Deprecated
    public String printDecimal(BigDecimal val) {
        return DatatypeConverterImpl._printDecimal(val);
    }

    @Deprecated
    public String printDouble(double v) {
        return DatatypeConverterImpl._printDouble(v);
    }

    @Deprecated
    public String printQName(QName val, NamespaceContext nsc) {
        return DatatypeConverterImpl._printQName(val, nsc);
    }

    @Deprecated
    public String printBase64Binary(byte[] val) {
        return DatatypeConverterImpl._printBase64Binary(val);
    }

    @Deprecated
    public String printUnsignedShort(int val) {
        return String.valueOf(val);
    }

    @Deprecated
    public String printAnySimpleType(String val) {
        return val;
    }

    private static final class CalendarFormatter {
        private CalendarFormatter() {
        }

        public static String doFormat(String format, Calendar cal) throws IllegalArgumentException {
            int fidx = 0;
            int flen = format.length();
            StringBuilder buf = new StringBuilder();
            block9: while (fidx < flen) {
                char fch;
                if ((fch = format.charAt(fidx++)) != '%') {
                    buf.append(fch);
                    continue;
                }
                switch (format.charAt(fidx++)) {
                    case 'Y': {
                        CalendarFormatter.formatYear(cal, buf);
                        continue block9;
                    }
                    case 'M': {
                        CalendarFormatter.formatMonth(cal, buf);
                        continue block9;
                    }
                    case 'D': {
                        CalendarFormatter.formatDays(cal, buf);
                        continue block9;
                    }
                    case 'h': {
                        CalendarFormatter.formatHours(cal, buf);
                        continue block9;
                    }
                    case 'm': {
                        CalendarFormatter.formatMinutes(cal, buf);
                        continue block9;
                    }
                    case 's': {
                        CalendarFormatter.formatSeconds(cal, buf);
                        continue block9;
                    }
                    case 'z': {
                        CalendarFormatter.formatTimeZone(cal, buf);
                        continue block9;
                    }
                }
                throw new InternalError();
            }
            return buf.toString();
        }

        private static void formatYear(Calendar cal, StringBuilder buf) {
            int year = cal.get(1);
            String s = year <= 0 ? Integer.toString(1 - year) : Integer.toString(year);
            while (s.length() < 4) {
                s = '0' + s;
            }
            if (year <= 0) {
                s = '-' + s;
            }
            buf.append(s);
        }

        private static void formatMonth(Calendar cal, StringBuilder buf) {
            CalendarFormatter.formatTwoDigits(cal.get(2) + 1, buf);
        }

        private static void formatDays(Calendar cal, StringBuilder buf) {
            CalendarFormatter.formatTwoDigits(cal.get(5), buf);
        }

        private static void formatHours(Calendar cal, StringBuilder buf) {
            CalendarFormatter.formatTwoDigits(cal.get(11), buf);
        }

        private static void formatMinutes(Calendar cal, StringBuilder buf) {
            CalendarFormatter.formatTwoDigits(cal.get(12), buf);
        }

        private static void formatSeconds(Calendar cal, StringBuilder buf) {
            int n;
            CalendarFormatter.formatTwoDigits(cal.get(13), buf);
            if (cal.isSet(14) && (n = cal.get(14)) != 0) {
                String ms = Integer.toString(n);
                while (ms.length() < 3) {
                    ms = '0' + ms;
                }
                buf.append('.');
                buf.append(ms);
            }
        }

        private static void formatTimeZone(Calendar cal, StringBuilder buf) {
            TimeZone tz = cal.getTimeZone();
            if (tz == null) {
                return;
            }
            int offset = tz.getOffset(cal.getTime().getTime());
            if (offset == 0) {
                buf.append('Z');
                return;
            }
            if (offset >= 0) {
                buf.append('+');
            } else {
                buf.append('-');
                offset *= -1;
            }
            CalendarFormatter.formatTwoDigits((offset /= 60000) / 60, buf);
            buf.append(':');
            CalendarFormatter.formatTwoDigits(offset % 60, buf);
        }

        private static void formatTwoDigits(int n, StringBuilder buf) {
            if (n < 10) {
                buf.append('0');
            }
            buf.append(n);
        }
    }
}


/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.activation.DataHandler
 *  javax.activation.DataSource
 */
package com.sun.xml.messaging.saaj.packaging.mime.internet;

import com.sun.xml.messaging.saaj.packaging.mime.MessagingException;
import com.sun.xml.messaging.saaj.packaging.mime.internet.AsciiOutputStream;
import com.sun.xml.messaging.saaj.packaging.mime.internet.ContentType;
import com.sun.xml.messaging.saaj.packaging.mime.internet.ParseException;
import com.sun.xml.messaging.saaj.packaging.mime.util.ASCIIUtility;
import com.sun.xml.messaging.saaj.packaging.mime.util.BASE64DecoderStream;
import com.sun.xml.messaging.saaj.packaging.mime.util.BASE64EncoderStream;
import com.sun.xml.messaging.saaj.packaging.mime.util.BEncoderStream;
import com.sun.xml.messaging.saaj.packaging.mime.util.LineInputStream;
import com.sun.xml.messaging.saaj.packaging.mime.util.QDecoderStream;
import com.sun.xml.messaging.saaj.packaging.mime.util.QEncoderStream;
import com.sun.xml.messaging.saaj.packaging.mime.util.QPDecoderStream;
import com.sun.xml.messaging.saaj.packaging.mime.util.QPEncoderStream;
import com.sun.xml.messaging.saaj.packaging.mime.util.UUDecoderStream;
import com.sun.xml.messaging.saaj.packaging.mime.util.UUEncoderStream;
import com.sun.xml.messaging.saaj.util.SAAJUtil;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FilterInputStream;
import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.Hashtable;
import java.util.NoSuchElementException;
import java.util.StringTokenizer;
import javax.activation.DataHandler;
import javax.activation.DataSource;

public class MimeUtility {
    public static final int ALL = -1;
    private static final int BUFFER_SIZE = 1024;
    private static boolean decodeStrict = true;
    private static boolean encodeEolStrict = false;
    private static boolean foldEncodedWords = false;
    private static boolean foldText = true;
    private static String defaultJavaCharset;
    private static String defaultMIMECharset;
    private static Hashtable<String, String> mime2java;
    private static Hashtable<String, String> java2mime;
    static final int ALL_ASCII = 1;
    static final int MOSTLY_ASCII = 2;
    static final int MOSTLY_NONASCII = 3;

    private MimeUtility() {
    }

    public static String getEncoding(DataSource ds) {
        ContentType cType = null;
        InputStream is = null;
        String encoding = null;
        try {
            cType = new ContentType(ds.getContentType());
            is = ds.getInputStream();
        }
        catch (Exception ex) {
            return "base64";
        }
        boolean isText = cType.match("text/*");
        int i = MimeUtility.checkAscii(is, -1, !isText);
        switch (i) {
            case 1: {
                encoding = "7bit";
                break;
            }
            case 2: {
                encoding = "quoted-printable";
                break;
            }
            default: {
                encoding = "base64";
            }
        }
        try {
            is.close();
        }
        catch (IOException iOException) {
            // empty catch block
        }
        return encoding;
    }

    public static String getEncoding(DataHandler dh) {
        ContentType cType = null;
        String encoding = null;
        if (dh.getName() != null) {
            return MimeUtility.getEncoding(dh.getDataSource());
        }
        try {
            cType = new ContentType(dh.getContentType());
        }
        catch (Exception ex) {
            return "base64";
        }
        if (cType.match("text/*")) {
            AsciiOutputStream aos = new AsciiOutputStream(false, false);
            try {
                dh.writeTo((OutputStream)aos);
            }
            catch (IOException iOException) {
                // empty catch block
            }
            switch (aos.getAscii()) {
                case 1: {
                    encoding = "7bit";
                    break;
                }
                case 2: {
                    encoding = "quoted-printable";
                    break;
                }
                default: {
                    encoding = "base64";
                    break;
                }
            }
        } else {
            AsciiOutputStream aos = new AsciiOutputStream(true, encodeEolStrict);
            try {
                dh.writeTo((OutputStream)aos);
            }
            catch (IOException iOException) {
                // empty catch block
            }
            encoding = aos.getAscii() == 1 ? "7bit" : "base64";
        }
        return encoding;
    }

    public static InputStream decode(InputStream is, String encoding) throws MessagingException {
        if (encoding.equalsIgnoreCase("base64")) {
            return new BASE64DecoderStream(is);
        }
        if (encoding.equalsIgnoreCase("quoted-printable")) {
            return new QPDecoderStream(is);
        }
        if (encoding.equalsIgnoreCase("uuencode") || encoding.equalsIgnoreCase("x-uuencode") || encoding.equalsIgnoreCase("x-uue")) {
            return new UUDecoderStream(is);
        }
        if (encoding.equalsIgnoreCase("binary") || encoding.equalsIgnoreCase("7bit") || encoding.equalsIgnoreCase("8bit")) {
            return is;
        }
        throw new MessagingException("Unknown encoding: " + encoding);
    }

    public static OutputStream encode(OutputStream os, String encoding) throws MessagingException {
        if (encoding == null) {
            return os;
        }
        if (encoding.equalsIgnoreCase("base64")) {
            return new BASE64EncoderStream(os);
        }
        if (encoding.equalsIgnoreCase("quoted-printable")) {
            return new QPEncoderStream(os);
        }
        if (encoding.equalsIgnoreCase("uuencode") || encoding.equalsIgnoreCase("x-uuencode") || encoding.equalsIgnoreCase("x-uue")) {
            return new UUEncoderStream(os);
        }
        if (encoding.equalsIgnoreCase("binary") || encoding.equalsIgnoreCase("7bit") || encoding.equalsIgnoreCase("8bit")) {
            return os;
        }
        throw new MessagingException("Unknown encoding: " + encoding);
    }

    public static OutputStream encode(OutputStream os, String encoding, String filename) throws MessagingException {
        if (encoding == null) {
            return os;
        }
        if (encoding.equalsIgnoreCase("base64")) {
            return new BASE64EncoderStream(os);
        }
        if (encoding.equalsIgnoreCase("quoted-printable")) {
            return new QPEncoderStream(os);
        }
        if (encoding.equalsIgnoreCase("uuencode") || encoding.equalsIgnoreCase("x-uuencode") || encoding.equalsIgnoreCase("x-uue")) {
            return new UUEncoderStream(os, filename);
        }
        if (encoding.equalsIgnoreCase("binary") || encoding.equalsIgnoreCase("7bit") || encoding.equalsIgnoreCase("8bit")) {
            return os;
        }
        throw new MessagingException("Unknown encoding: " + encoding);
    }

    public static String encodeText(String text) throws UnsupportedEncodingException {
        return MimeUtility.encodeText(text, null, null);
    }

    public static String encodeText(String text, String charset, String encoding) throws UnsupportedEncodingException {
        return MimeUtility.encodeWord(text, charset, encoding, false);
    }

    public static String decodeText(String etext) throws UnsupportedEncodingException {
        String lwsp = " \t\n\r";
        if (etext.indexOf("=?") == -1) {
            return etext;
        }
        StringTokenizer st = new StringTokenizer(etext, lwsp, true);
        StringBuilder sb = new StringBuilder();
        StringBuilder wsb = new StringBuilder();
        boolean prevWasEncoded = false;
        while (st.hasMoreTokens()) {
            String word;
            String s = st.nextToken();
            char c = s.charAt(0);
            if (c == ' ' || c == '\t' || c == '\r' || c == '\n') {
                wsb.append(c);
                continue;
            }
            try {
                word = MimeUtility.decodeWord(s);
                if (!prevWasEncoded && wsb.length() > 0) {
                    sb.append((CharSequence)wsb);
                }
                prevWasEncoded = true;
            }
            catch (ParseException pex) {
                word = s;
                if (!decodeStrict) {
                    word = MimeUtility.decodeInnerWords(word);
                }
                if (wsb.length() > 0) {
                    sb.append((CharSequence)wsb);
                }
                prevWasEncoded = false;
            }
            sb.append(word);
            wsb.setLength(0);
        }
        return sb.toString();
    }

    public static String encodeWord(String word) throws UnsupportedEncodingException {
        return MimeUtility.encodeWord(word, null, null);
    }

    public static String encodeWord(String word, String charset, String encoding) throws UnsupportedEncodingException {
        return MimeUtility.encodeWord(word, charset, encoding, true);
    }

    private static String encodeWord(String string, String charset, String encoding, boolean encodingWord) throws UnsupportedEncodingException {
        boolean b64;
        String jcharset;
        int ascii = MimeUtility.checkAscii(string);
        if (ascii == 1) {
            return string;
        }
        if (charset == null) {
            jcharset = MimeUtility.getDefaultJavaCharset();
            charset = MimeUtility.getDefaultMIMECharset();
        } else {
            jcharset = MimeUtility.javaCharset(charset);
        }
        if (encoding == null) {
            encoding = ascii != 3 ? "Q" : "B";
        }
        if (encoding.equalsIgnoreCase("B")) {
            b64 = true;
        } else if (encoding.equalsIgnoreCase("Q")) {
            b64 = false;
        } else {
            throw new UnsupportedEncodingException("Unknown transfer encoding: " + encoding);
        }
        StringBuilder outb = new StringBuilder();
        MimeUtility.doEncode(string, b64, jcharset, 68 - charset.length(), "=?" + charset + "?" + encoding + "?", true, encodingWord, outb);
        return outb.toString();
    }

    private static void doEncode(String string, boolean b64, String jcharset, int avail, String prefix, boolean first, boolean encodingWord, StringBuilder buf) throws UnsupportedEncodingException {
        int size;
        byte[] bytes = string.getBytes(jcharset);
        int len = b64 ? BEncoderStream.encodedLength(bytes) : QEncoderStream.encodedLength(bytes, encodingWord);
        if (len > avail && (size = string.length()) > 1) {
            MimeUtility.doEncode(string.substring(0, size / 2), b64, jcharset, avail, prefix, first, encodingWord, buf);
            MimeUtility.doEncode(string.substring(size / 2, size), b64, jcharset, avail, prefix, false, encodingWord, buf);
        } else {
            ByteArrayOutputStream os = new ByteArrayOutputStream(1024);
            FilterOutputStream eos = b64 ? new BEncoderStream(os) : new QEncoderStream((OutputStream)os, encodingWord);
            try {
                ((OutputStream)eos).write(bytes);
                ((OutputStream)eos).close();
            }
            catch (IOException iOException) {
                // empty catch block
            }
            byte[] encodedBytes = os.toByteArray();
            if (!first) {
                if (foldEncodedWords) {
                    buf.append("\r\n ");
                } else {
                    buf.append(" ");
                }
            }
            buf.append(prefix);
            for (int i = 0; i < encodedBytes.length; ++i) {
                buf.append((char)encodedBytes[i]);
            }
            buf.append("?=");
        }
    }

    public static String decodeWord(String eword) throws ParseException, UnsupportedEncodingException {
        if (!eword.startsWith("=?")) {
            throw new ParseException();
        }
        int start = 2;
        int pos = eword.indexOf(63, start);
        if (pos == -1) {
            throw new ParseException();
        }
        String charset = MimeUtility.javaCharset(eword.substring(start, pos));
        start = pos + 1;
        if ((pos = eword.indexOf(63, start)) == -1) {
            throw new ParseException();
        }
        String encoding = eword.substring(start, pos);
        start = pos + 1;
        if ((pos = eword.indexOf("?=", start)) == -1) {
            throw new ParseException();
        }
        String word = eword.substring(start, pos);
        try {
            FilterInputStream is;
            ByteArrayInputStream bis = new ByteArrayInputStream(ASCIIUtility.getBytes(word));
            if (encoding.equalsIgnoreCase("B")) {
                is = new BASE64DecoderStream(bis);
            } else if (encoding.equalsIgnoreCase("Q")) {
                is = new QDecoderStream(bis);
            } else {
                throw new UnsupportedEncodingException("unknown encoding: " + encoding);
            }
            int count = bis.available();
            byte[] bytes = new byte[count];
            count = ((InputStream)is).read(bytes, 0, count);
            String s = new String(bytes, 0, count, charset);
            if (pos + 2 < eword.length()) {
                String rest = eword.substring(pos + 2);
                if (!decodeStrict) {
                    rest = MimeUtility.decodeInnerWords(rest);
                }
                s = s + rest;
            }
            return s;
        }
        catch (UnsupportedEncodingException uex) {
            throw uex;
        }
        catch (IOException ioex) {
            throw new ParseException();
        }
        catch (IllegalArgumentException iex) {
            throw new UnsupportedEncodingException();
        }
    }

    private static String decodeInnerWords(String word) throws UnsupportedEncodingException {
        int i;
        int start = 0;
        StringBuilder buf = new StringBuilder();
        while ((i = word.indexOf("=?", start)) >= 0) {
            buf.append(word.substring(start, i));
            int end = word.indexOf("?=", i);
            if (end < 0) break;
            String s = word.substring(i, end + 2);
            try {
                s = MimeUtility.decodeWord(s);
            }
            catch (ParseException parseException) {
                // empty catch block
            }
            buf.append(s);
            start = end + 2;
        }
        if (start == 0) {
            return word;
        }
        if (start < word.length()) {
            buf.append(word.substring(start));
        }
        return buf.toString();
    }

    public static String quote(String word, String specials) {
        int len = word.length();
        boolean needQuoting = false;
        for (int i = 0; i < len; ++i) {
            char c = word.charAt(i);
            if (c == '\"' || c == '\\' || c == '\r' || c == '\n') {
                StringBuilder sb = new StringBuilder(len + 3);
                sb.append('\"');
                sb.append(word.substring(0, i));
                int lastc = 0;
                for (int j = i; j < len; ++j) {
                    char cc = word.charAt(j);
                    if (!(cc != '\"' && cc != '\\' && cc != '\r' && cc != '\n' || cc == '\n' && lastc == 13)) {
                        sb.append('\\');
                    }
                    sb.append(cc);
                    lastc = cc;
                }
                sb.append('\"');
                return sb.toString();
            }
            if (c >= ' ' && c < '\u007f' && specials.indexOf(c) < 0) continue;
            needQuoting = true;
        }
        if (needQuoting) {
            StringBuilder sb = new StringBuilder(len + 2);
            sb.append('\"').append(word).append('\"');
            return sb.toString();
        }
        return word;
    }

    static String fold(int used, String s) {
        char c;
        int end;
        if (!foldText) {
            return s;
        }
        for (end = s.length() - 1; end >= 0 && ((c = s.charAt(end)) == ' ' || c == '\t'); --end) {
        }
        if (end != s.length() - 1) {
            s = s.substring(0, end + 1);
        }
        if (used + s.length() <= 76) {
            return s;
        }
        StringBuilder sb = new StringBuilder(s.length() + 4);
        char lastc = '\u0000';
        while (used + s.length() > 76) {
            int lastspace = -1;
            for (int i = 0; i < s.length() && (lastspace == -1 || used + i <= 76); ++i) {
                c = s.charAt(i);
                if ((c == ' ' || c == '\t') && lastc != ' ' && lastc != '\t') {
                    lastspace = i;
                }
                lastc = c;
            }
            if (lastspace == -1) {
                sb.append(s);
                s = "";
                used = 0;
                break;
            }
            sb.append(s.substring(0, lastspace));
            sb.append("\r\n");
            lastc = s.charAt(lastspace);
            sb.append(lastc);
            s = s.substring(lastspace + 1);
            used = 1;
        }
        sb.append(s);
        return sb.toString();
    }

    static String unfold(String s) {
        int i;
        if (!foldText) {
            return s;
        }
        StringBuilder sb = null;
        while ((i = MimeUtility.indexOfAny(s, "\r\n")) >= 0) {
            int start = i++;
            int l = s.length();
            if (i < l && s.charAt(i - 1) == '\r' && s.charAt(i) == '\n') {
                ++i;
            }
            if (start == 0 || s.charAt(start - 1) != '\\') {
                char c;
                if (i < l && ((c = s.charAt(i)) == ' ' || c == '\t')) {
                    ++i;
                    while (i < l && ((c = s.charAt(i)) == ' ' || c == '\t')) {
                        ++i;
                    }
                    if (sb == null) {
                        sb = new StringBuilder(s.length());
                    }
                    if (start != 0) {
                        sb.append(s.substring(0, start));
                        sb.append(' ');
                    }
                    s = s.substring(i);
                    continue;
                }
                if (sb == null) {
                    sb = new StringBuilder(s.length());
                }
                sb.append(s.substring(0, i));
                s = s.substring(i);
                continue;
            }
            if (sb == null) {
                sb = new StringBuilder(s.length());
            }
            sb.append(s.substring(0, start - 1));
            sb.append(s.substring(start, i));
            s = s.substring(i);
        }
        if (sb != null) {
            sb.append(s);
            return sb.toString();
        }
        return s;
    }

    private static int indexOfAny(String s, String any) {
        return MimeUtility.indexOfAny(s, any, 0);
    }

    private static int indexOfAny(String s, String any, int start) {
        try {
            int len = s.length();
            for (int i = start; i < len; ++i) {
                if (any.indexOf(s.charAt(i)) < 0) continue;
                return i;
            }
            return -1;
        }
        catch (StringIndexOutOfBoundsException e) {
            return -1;
        }
    }

    public static String javaCharset(String charset) {
        if (mime2java == null || charset == null) {
            return charset;
        }
        String alias = mime2java.get(charset.toLowerCase());
        return alias == null ? charset : alias;
    }

    public static String mimeCharset(String charset) {
        if (java2mime == null || charset == null) {
            return charset;
        }
        String alias = java2mime.get(charset.toLowerCase());
        return alias == null ? charset : alias;
    }

    public static String getDefaultJavaCharset() {
        block4: {
            if (defaultJavaCharset == null) {
                String mimecs = null;
                mimecs = SAAJUtil.getSystemProperty("mail.mime.charset");
                if (mimecs != null && mimecs.length() > 0) {
                    defaultJavaCharset = MimeUtility.javaCharset(mimecs);
                    return defaultJavaCharset;
                }
                try {
                    defaultJavaCharset = System.getProperty("file.encoding", "8859_1");
                }
                catch (SecurityException sex) {
                    class NullInputStream
                    extends InputStream {
                        NullInputStream() {
                        }

                        @Override
                        public int read() {
                            return 0;
                        }
                    }
                    InputStreamReader reader = new InputStreamReader(new NullInputStream());
                    defaultJavaCharset = reader.getEncoding();
                    if (defaultJavaCharset != null) break block4;
                    defaultJavaCharset = "8859_1";
                }
            }
        }
        return defaultJavaCharset;
    }

    static String getDefaultMIMECharset() {
        if (defaultMIMECharset == null) {
            defaultMIMECharset = SAAJUtil.getSystemProperty("mail.mime.charset");
        }
        if (defaultMIMECharset == null) {
            defaultMIMECharset = MimeUtility.mimeCharset(MimeUtility.getDefaultJavaCharset());
        }
        return defaultMIMECharset;
    }

    private static void loadMappings(LineInputStream is, Hashtable<String, String> table) {
        while (true) {
            String currLine;
            try {
                currLine = is.readLine();
            }
            catch (IOException ioex) {
                break;
            }
            if (currLine == null || currLine.startsWith("--") && currLine.endsWith("--")) break;
            if (currLine.trim().length() == 0 || currLine.startsWith("#")) continue;
            StringTokenizer tk = new StringTokenizer(currLine, " \t");
            try {
                String key = tk.nextToken();
                String value = tk.nextToken();
                table.put(key.toLowerCase(), value);
            }
            catch (NoSuchElementException noSuchElementException) {}
        }
    }

    static int checkAscii(String s) {
        int ascii = 0;
        int non_ascii = 0;
        int l = s.length();
        for (int i = 0; i < l; ++i) {
            if (MimeUtility.nonascii(s.charAt(i))) {
                ++non_ascii;
                continue;
            }
            ++ascii;
        }
        if (non_ascii == 0) {
            return 1;
        }
        if (ascii > non_ascii) {
            return 2;
        }
        return 3;
    }

    static int checkAscii(byte[] b) {
        int ascii = 0;
        int non_ascii = 0;
        for (int i = 0; i < b.length; ++i) {
            if (MimeUtility.nonascii(b[i] & 0xFF)) {
                ++non_ascii;
                continue;
            }
            ++ascii;
        }
        if (non_ascii == 0) {
            return 1;
        }
        if (ascii > non_ascii) {
            return 2;
        }
        return 3;
    }

    static int checkAscii(InputStream is, int max, boolean breakOnNonAscii) {
        int ascii = 0;
        int non_ascii = 0;
        int block = 4096;
        int linelen = 0;
        boolean longLine = false;
        boolean badEOL = false;
        boolean checkEOL = encodeEolStrict && breakOnNonAscii;
        byte[] buf = null;
        if (max != 0) {
            block = max == -1 ? 4096 : Math.min(max, 4096);
            buf = new byte[block];
        }
        while (max != 0) {
            int len;
            try {
                len = is.read(buf, 0, block);
                if (len == -1) break;
                int lastb = 0;
                for (int i = 0; i < len; ++i) {
                    int b = buf[i] & 0xFF;
                    if (checkEOL && (lastb == 13 && b != 10 || lastb != 13 && b == 10)) {
                        badEOL = true;
                    }
                    if (b == 13 || b == 10) {
                        linelen = 0;
                    } else if (++linelen > 998) {
                        longLine = true;
                    }
                    if (MimeUtility.nonascii(b)) {
                        if (breakOnNonAscii) {
                            return 3;
                        }
                        ++non_ascii;
                    } else {
                        ++ascii;
                    }
                    lastb = b;
                }
            }
            catch (IOException ioex) {
                break;
            }
            if (max == -1) continue;
            max -= len;
        }
        if (max == 0 && breakOnNonAscii) {
            return 3;
        }
        if (non_ascii == 0) {
            if (badEOL) {
                return 3;
            }
            if (longLine) {
                return 2;
            }
            return 1;
        }
        if (ascii > non_ascii) {
            return 2;
        }
        return 3;
    }

    static final boolean nonascii(int b) {
        return b >= 127 || b < 32 && b != 13 && b != 10 && b != 9;
    }

    static {
        try {
            String s = SAAJUtil.getSystemProperty("mail.mime.decodetext.strict");
            decodeStrict = s == null || !s.equalsIgnoreCase("false");
            s = SAAJUtil.getSystemProperty("mail.mime.encodeeol.strict");
            encodeEolStrict = s != null && s.equalsIgnoreCase("true");
            s = SAAJUtil.getSystemProperty("mail.mime.foldencodedwords");
            foldEncodedWords = s != null && s.equalsIgnoreCase("true");
            s = SAAJUtil.getSystemProperty("mail.mime.foldtext");
            foldText = s == null || !s.equalsIgnoreCase("false");
        }
        catch (SecurityException s) {
            // empty catch block
        }
        java2mime = new Hashtable(40);
        mime2java = new Hashtable(10);
        try {
            InputStream is = MimeUtility.class.getResourceAsStream("/META-INF/javamail.charset.map");
            if (is != null) {
                is = new LineInputStream(is);
                MimeUtility.loadMappings((LineInputStream)is, java2mime);
                MimeUtility.loadMappings((LineInputStream)is, mime2java);
            }
        }
        catch (Exception exception) {
            // empty catch block
        }
        if (java2mime.isEmpty()) {
            java2mime.put("8859_1", "ISO-8859-1");
            java2mime.put("iso8859_1", "ISO-8859-1");
            java2mime.put("ISO8859-1", "ISO-8859-1");
            java2mime.put("8859_2", "ISO-8859-2");
            java2mime.put("iso8859_2", "ISO-8859-2");
            java2mime.put("ISO8859-2", "ISO-8859-2");
            java2mime.put("8859_3", "ISO-8859-3");
            java2mime.put("iso8859_3", "ISO-8859-3");
            java2mime.put("ISO8859-3", "ISO-8859-3");
            java2mime.put("8859_4", "ISO-8859-4");
            java2mime.put("iso8859_4", "ISO-8859-4");
            java2mime.put("ISO8859-4", "ISO-8859-4");
            java2mime.put("8859_5", "ISO-8859-5");
            java2mime.put("iso8859_5", "ISO-8859-5");
            java2mime.put("ISO8859-5", "ISO-8859-5");
            java2mime.put("8859_6", "ISO-8859-6");
            java2mime.put("iso8859_6", "ISO-8859-6");
            java2mime.put("ISO8859-6", "ISO-8859-6");
            java2mime.put("8859_7", "ISO-8859-7");
            java2mime.put("iso8859_7", "ISO-8859-7");
            java2mime.put("ISO8859-7", "ISO-8859-7");
            java2mime.put("8859_8", "ISO-8859-8");
            java2mime.put("iso8859_8", "ISO-8859-8");
            java2mime.put("ISO8859-8", "ISO-8859-8");
            java2mime.put("8859_9", "ISO-8859-9");
            java2mime.put("iso8859_9", "ISO-8859-9");
            java2mime.put("ISO8859-9", "ISO-8859-9");
            java2mime.put("SJIS", "Shift_JIS");
            java2mime.put("MS932", "Shift_JIS");
            java2mime.put("JIS", "ISO-2022-JP");
            java2mime.put("ISO2022JP", "ISO-2022-JP");
            java2mime.put("EUC_JP", "euc-jp");
            java2mime.put("KOI8_R", "koi8-r");
            java2mime.put("EUC_CN", "euc-cn");
            java2mime.put("EUC_TW", "euc-tw");
            java2mime.put("EUC_KR", "euc-kr");
        }
        if (mime2java.isEmpty()) {
            mime2java.put("iso-2022-cn", "ISO2022CN");
            mime2java.put("iso-2022-kr", "ISO2022KR");
            mime2java.put("utf-8", "UTF8");
            mime2java.put("utf8", "UTF8");
            mime2java.put("ja_jp.iso2022-7", "ISO2022JP");
            mime2java.put("ja_jp.eucjp", "EUCJIS");
            mime2java.put("euc-kr", "KSC5601");
            mime2java.put("euckr", "KSC5601");
            mime2java.put("us-ascii", "ISO-8859-1");
            mime2java.put("x-us-ascii", "ISO-8859-1");
        }
    }
}


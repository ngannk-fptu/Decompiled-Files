/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.batik.util.EncodingUtilities
 */
package org.apache.batik.xml;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PushbackInputStream;
import java.io.Reader;
import org.apache.batik.util.EncodingUtilities;
import org.apache.batik.xml.XMLCharacters;

public class XMLUtilities
extends XMLCharacters {
    public static final int IS_XML_10_NAME = 1;
    public static final int IS_XML_10_QNAME = 2;

    protected XMLUtilities() {
    }

    public static boolean isXMLSpace(char c) {
        return c <= ' ' && (4294977024L >> c & 1L) != 0L;
    }

    public static boolean isXMLNameFirstCharacter(char c) {
        return (NAME_FIRST_CHARACTER[c / 32] & 1 << c % 32) != 0;
    }

    public static boolean isXML11NameFirstCharacter(char c) {
        return (NAME11_FIRST_CHARACTER[c / 32] & 1 << c % 32) != 0;
    }

    public static boolean isXMLNameCharacter(char c) {
        return (NAME_CHARACTER[c / 32] & 1 << c % 32) != 0;
    }

    public static boolean isXML11NameCharacter(char c) {
        return (NAME11_CHARACTER[c / 32] & 1 << c % 32) != 0;
    }

    public static boolean isXMLCharacter(int c) {
        return (XML_CHARACTER[c >>> 5] & 1 << (c & 0x1F)) != 0 || c >= 65536 && c <= 0x10FFFF;
    }

    public static boolean isXML11Character(int c) {
        return c >= 1 && c <= 55295 || c >= 57344 && c <= 65533 || c >= 65536 && c <= 0x10FFFF;
    }

    public static boolean isXMLPublicIdCharacter(char c) {
        return c < '\u0080' && (PUBLIC_ID_CHARACTER[c / 32] & 1 << c % 32) != 0;
    }

    public static boolean isXMLVersionCharacter(char c) {
        return c < '\u0080' && (VERSION_CHARACTER[c / 32] & 1 << c % 32) != 0;
    }

    public static boolean isXMLAlphabeticCharacter(char c) {
        return c < '\u0080' && (ALPHABETIC_CHARACTER[c / 32] & 1 << c % 32) != 0;
    }

    public static int testXMLQName(String s) {
        int isQName = 2;
        boolean foundColon = false;
        int len = s.length();
        if (len == 0) {
            return 0;
        }
        char c = s.charAt(0);
        if (!XMLUtilities.isXMLNameFirstCharacter(c)) {
            return 0;
        }
        if (c == ':') {
            isQName = 0;
        }
        for (int i = 1; i < len; ++i) {
            c = s.charAt(i);
            if (!XMLUtilities.isXMLNameCharacter(c)) {
                return 0;
            }
            if (isQName == 0 || c != ':') continue;
            if (foundColon || i == len - 1) {
                isQName = 0;
                continue;
            }
            foundColon = true;
        }
        return 1 | isQName;
    }

    public static Reader createXMLDocumentReader(InputStream is) throws IOException {
        PushbackInputStream pbis = new PushbackInputStream(is, 128);
        byte[] buf = new byte[4];
        int len = pbis.read(buf);
        if (len > 0) {
            pbis.unread(buf, 0, len);
        }
        if (len == 4) {
            switch (buf[0] & 0xFF) {
                case 0: {
                    if (buf[1] != 60 || buf[2] != 0 || buf[3] != 63) break;
                    return new InputStreamReader((InputStream)pbis, "UnicodeBig");
                }
                case 60: {
                    switch (buf[1] & 0xFF) {
                        case 0: {
                            if (buf[2] != 63 || buf[3] != 0) break;
                            return new InputStreamReader((InputStream)pbis, "UnicodeLittle");
                        }
                        case 63: {
                            if (buf[2] != 120 || buf[3] != 109) break;
                            Reader r = XMLUtilities.createXMLDeclarationReader(pbis, "UTF8");
                            String enc = XMLUtilities.getXMLDeclarationEncoding(r, "UTF8");
                            return new InputStreamReader((InputStream)pbis, enc);
                        }
                    }
                    break;
                }
                case 76: {
                    if (buf[1] != 111 || (buf[2] & 0xFF) != 167 || (buf[3] & 0xFF) != 148) break;
                    Reader r = XMLUtilities.createXMLDeclarationReader(pbis, "CP037");
                    String enc = XMLUtilities.getXMLDeclarationEncoding(r, "CP037");
                    return new InputStreamReader((InputStream)pbis, enc);
                }
                case 254: {
                    if ((buf[1] & 0xFF) != 255) break;
                    return new InputStreamReader((InputStream)pbis, "Unicode");
                }
                case 255: {
                    if ((buf[1] & 0xFF) != 254) break;
                    return new InputStreamReader((InputStream)pbis, "Unicode");
                }
            }
        }
        return new InputStreamReader((InputStream)pbis, "UTF8");
    }

    protected static Reader createXMLDeclarationReader(PushbackInputStream pbis, String enc) throws IOException {
        byte[] buf = new byte[128];
        int len = pbis.read(buf);
        if (len > 0) {
            pbis.unread(buf, 0, len);
        }
        return new InputStreamReader((InputStream)new ByteArrayInputStream(buf, 4, len), enc);
    }

    protected static String getXMLDeclarationEncoding(Reader r, String e) throws IOException {
        int c = r.read();
        if (c != 108) {
            return e;
        }
        c = r.read();
        if (!XMLUtilities.isXMLSpace((char)c)) {
            return e;
        }
        while (XMLUtilities.isXMLSpace((char)(c = r.read()))) {
        }
        if (c != 118) {
            return e;
        }
        c = r.read();
        if (c != 101) {
            return e;
        }
        c = r.read();
        if (c != 114) {
            return e;
        }
        c = r.read();
        if (c != 115) {
            return e;
        }
        c = r.read();
        if (c != 105) {
            return e;
        }
        c = r.read();
        if (c != 111) {
            return e;
        }
        c = r.read();
        if (c != 110) {
            return e;
        }
        c = r.read();
        while (XMLUtilities.isXMLSpace((char)c)) {
            c = r.read();
        }
        if (c != 61) {
            return e;
        }
        while (XMLUtilities.isXMLSpace((char)(c = r.read()))) {
        }
        if (c != 34 && c != 39) {
            return e;
        }
        char sc = (char)c;
        while ((c = r.read()) != sc) {
            if (XMLUtilities.isXMLVersionCharacter((char)c)) continue;
            return e;
        }
        c = r.read();
        if (!XMLUtilities.isXMLSpace((char)c)) {
            return e;
        }
        while (XMLUtilities.isXMLSpace((char)(c = r.read()))) {
        }
        if (c != 101) {
            return e;
        }
        c = r.read();
        if (c != 110) {
            return e;
        }
        c = r.read();
        if (c != 99) {
            return e;
        }
        c = r.read();
        if (c != 111) {
            return e;
        }
        c = r.read();
        if (c != 100) {
            return e;
        }
        c = r.read();
        if (c != 105) {
            return e;
        }
        c = r.read();
        if (c != 110) {
            return e;
        }
        c = r.read();
        if (c != 103) {
            return e;
        }
        c = r.read();
        while (XMLUtilities.isXMLSpace((char)c)) {
            c = r.read();
        }
        if (c != 61) {
            return e;
        }
        while (XMLUtilities.isXMLSpace((char)(c = r.read()))) {
        }
        if (c != 34 && c != 39) {
            return e;
        }
        sc = (char)c;
        StringBuffer enc = new StringBuffer();
        while ((c = r.read()) != -1) {
            if (c == sc) {
                return XMLUtilities.encodingToJavaEncoding(enc.toString(), e);
            }
            enc.append((char)c);
        }
        return e;
    }

    public static String encodingToJavaEncoding(String e, String de) {
        String result = EncodingUtilities.javaEncoding((String)e);
        return result == null ? de : result;
    }
}


/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xml.serialize;

import java.io.UnsupportedEncodingException;
import java.util.Hashtable;
import java.util.Locale;
import org.apache.xerces.util.EncodingMap;
import org.apache.xml.serialize.EncodingInfo;

public class Encodings {
    static final int DEFAULT_LAST_PRINTABLE = 127;
    static final int LAST_PRINTABLE_UNICODE = 65535;
    static final String[] UNICODE_ENCODINGS = new String[]{"Unicode", "UnicodeBig", "UnicodeLittle", "GB2312", "UTF8", "UTF-16"};
    static final String DEFAULT_ENCODING = "UTF8";
    static Hashtable _encodings = new Hashtable();
    static final String JIS_DANGER_CHARS = "\\~\u007f\u00a2\u00a3\u00a5\u00ac\u2014\u2015\u2016\u2026\u203e\u203e\u2225\u222f\u301c\uff3c\uff5e\uffe0\uffe1\uffe2\uffe3";

    static EncodingInfo getEncodingInfo(String string, boolean bl) throws UnsupportedEncodingException {
        int n;
        EncodingInfo encodingInfo = null;
        if (string == null) {
            encodingInfo = (EncodingInfo)_encodings.get(DEFAULT_ENCODING);
            if (encodingInfo != null) {
                return encodingInfo;
            }
            encodingInfo = new EncodingInfo(EncodingMap.getJava2IANAMapping(DEFAULT_ENCODING), DEFAULT_ENCODING, 65535);
            _encodings.put(DEFAULT_ENCODING, encodingInfo);
            return encodingInfo;
        }
        String string2 = EncodingMap.getIANA2JavaMapping(string = string.toUpperCase(Locale.ENGLISH));
        if (string2 == null) {
            if (bl) {
                int n2;
                EncodingInfo.testJavaEncodingName(string);
                encodingInfo = (EncodingInfo)_encodings.get(string);
                if (encodingInfo != null) {
                    return encodingInfo;
                }
                for (n2 = 0; n2 < UNICODE_ENCODINGS.length; ++n2) {
                    if (!UNICODE_ENCODINGS[n2].equalsIgnoreCase(string)) continue;
                    encodingInfo = new EncodingInfo(EncodingMap.getJava2IANAMapping(string), string, 65535);
                    break;
                }
                if (n2 == UNICODE_ENCODINGS.length) {
                    encodingInfo = new EncodingInfo(EncodingMap.getJava2IANAMapping(string), string, 127);
                }
                _encodings.put(string, encodingInfo);
                return encodingInfo;
            }
            throw new UnsupportedEncodingException(string);
        }
        encodingInfo = (EncodingInfo)_encodings.get(string2);
        if (encodingInfo != null) {
            return encodingInfo;
        }
        for (n = 0; n < UNICODE_ENCODINGS.length; ++n) {
            if (!UNICODE_ENCODINGS[n].equalsIgnoreCase(string2)) continue;
            encodingInfo = new EncodingInfo(string, string2, 65535);
            break;
        }
        if (n == UNICODE_ENCODINGS.length) {
            encodingInfo = new EncodingInfo(string, string2, 127);
        }
        _encodings.put(string2, encodingInfo);
        return encodingInfo;
    }
}


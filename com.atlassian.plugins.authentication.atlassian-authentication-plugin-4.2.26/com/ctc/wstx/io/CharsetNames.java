/*
 * Decompiled with CFR 0.152.
 */
package com.ctc.wstx.io;

import com.ctc.wstx.util.StringUtil;
import java.io.OutputStreamWriter;
import java.io.Writer;

public final class CharsetNames {
    public static final String CS_US_ASCII = "US-ASCII";
    public static final String CS_UTF8 = "UTF-8";
    public static final String CS_UTF16 = "UTF-16";
    public static final String CS_UTF16BE = "UTF-16BE";
    public static final String CS_UTF16LE = "UTF-16LE";
    public static final String CS_UTF32 = "UTF-32";
    public static final String CS_UTF32BE = "UTF-32BE";
    public static final String CS_UTF32LE = "UTF-32LE";
    public static final String CS_ISO_LATIN1 = "ISO-8859-1";
    public static final String CS_SHIFT_JIS = "Shift_JIS";
    public static final String CS_EBCDIC_SUBSET = "IBM037";

    /*
     * Enabled aggressive block sorting
     */
    public static String normalize(String csName) {
        char d;
        if (csName == null) return csName;
        if (csName.length() < 3) {
            return csName;
        }
        boolean gotCsPrefix = false;
        char c = csName.charAt(0);
        if (!(c != 'c' && c != 'C' || (d = csName.charAt(1)) != 's' && d != 'S')) {
            csName = csName.substring(2);
            c = csName.charAt(0);
            gotCsPrefix = true;
        }
        switch (c) {
            case 'A': 
            case 'a': {
                if (!StringUtil.equalEncodings(csName, "ASCII")) return csName;
                return CS_US_ASCII;
            }
            case 'C': 
            case 'c': {
                if (StringUtil.encodingStartsWith(csName, "cp")) {
                    return "IBM" + StringUtil.trimEncoding(csName, true).substring(2);
                }
                if (!StringUtil.encodingStartsWith(csName, "cs")) return csName;
                if (!StringUtil.encodingStartsWith(csName, "csIBM")) return csName;
                return StringUtil.trimEncoding(csName, true).substring(2);
            }
            case 'E': 
            case 'e': {
                String type;
                if (!csName.startsWith("EBCDIC-CP-")) {
                    if (!csName.startsWith("ebcdic-cp-")) return csName;
                }
                if ((type = StringUtil.trimEncoding(csName, true).substring(8)).equals("US")) return CS_EBCDIC_SUBSET;
                if (type.equals("CA")) return CS_EBCDIC_SUBSET;
                if (type.equals("WT")) return CS_EBCDIC_SUBSET;
                if (type.equals("NL")) {
                    return CS_EBCDIC_SUBSET;
                }
                if (type.equals("DK")) return "IBM277";
                if (type.equals("NO")) {
                    return "IBM277";
                }
                if (type.equals("FI")) return "IBM278";
                if (type.equals("SE")) {
                    return "IBM278";
                }
                if (type.equals("ROECE")) return "IBM870";
                if (type.equals("YU")) {
                    return "IBM870";
                }
                if (type.equals("IT")) {
                    return "IBM280";
                }
                if (type.equals("ES")) {
                    return "IBM284";
                }
                if (type.equals("GB")) {
                    return "IBM285";
                }
                if (type.equals("FR")) {
                    return "IBM297";
                }
                if (type.equals("AR1")) {
                    return "IBM420";
                }
                if (type.equals("AR2")) {
                    return "IBM918";
                }
                if (type.equals("HE")) {
                    return "IBM424";
                }
                if (type.equals("CH")) {
                    return "IBM500";
                }
                if (!type.equals("IS")) return CS_EBCDIC_SUBSET;
                return "IBM871";
            }
            case 'I': 
            case 'i': {
                if (StringUtil.equalEncodings(csName, CS_ISO_LATIN1)) return CS_ISO_LATIN1;
                if (StringUtil.equalEncodings(csName, "ISO-Latin1")) {
                    return CS_ISO_LATIN1;
                }
                if (!StringUtil.encodingStartsWith(csName, "ISO-10646")) {
                    if (!StringUtil.encodingStartsWith(csName, "IBM")) return csName;
                    return csName;
                }
                int ix = csName.indexOf("10646");
                String suffix = csName.substring(ix + 5);
                if (StringUtil.equalEncodings(suffix, "UCS-Basic")) {
                    return CS_US_ASCII;
                }
                if (StringUtil.equalEncodings(suffix, "Unicode-Latin1")) {
                    return CS_ISO_LATIN1;
                }
                if (StringUtil.equalEncodings(suffix, "UCS-2")) {
                    return CS_UTF16;
                }
                if (StringUtil.equalEncodings(suffix, "UCS-4")) {
                    return CS_UTF32;
                }
                if (StringUtil.equalEncodings(suffix, "UTF-1")) {
                    return CS_US_ASCII;
                }
                if (StringUtil.equalEncodings(suffix, "J-1")) {
                    return CS_US_ASCII;
                }
                if (!StringUtil.equalEncodings(suffix, CS_US_ASCII)) return csName;
                return CS_US_ASCII;
            }
            case 'J': 
            case 'j': {
                if (!StringUtil.equalEncodings(csName, "JIS_Encoding")) return csName;
                return CS_SHIFT_JIS;
            }
            case 'S': 
            case 's': {
                if (!StringUtil.equalEncodings(csName, CS_SHIFT_JIS)) return csName;
                return CS_SHIFT_JIS;
            }
            case 'U': 
            case 'u': {
                if (csName.length() < 2) {
                    return csName;
                }
                switch (csName.charAt(1)) {
                    case 'C': 
                    case 'c': {
                        if (StringUtil.equalEncodings(csName, "UCS-2")) {
                            return CS_UTF16;
                        }
                        if (!StringUtil.equalEncodings(csName, "UCS-4")) return csName;
                        return CS_UTF32;
                    }
                    case 'N': 
                    case 'n': {
                        if (!gotCsPrefix) return csName;
                        if (StringUtil.equalEncodings(csName, "Unicode")) {
                            return CS_UTF16;
                        }
                        if (StringUtil.equalEncodings(csName, "UnicodeAscii")) {
                            return CS_ISO_LATIN1;
                        }
                        if (!StringUtil.equalEncodings(csName, "UnicodeAscii")) return csName;
                        return CS_US_ASCII;
                    }
                    case 'S': 
                    case 's': {
                        if (!StringUtil.equalEncodings(csName, CS_US_ASCII)) return csName;
                        return CS_US_ASCII;
                    }
                    case 'T': 
                    case 't': {
                        if (StringUtil.equalEncodings(csName, CS_UTF8)) {
                            return CS_UTF8;
                        }
                        if (StringUtil.equalEncodings(csName, CS_UTF16BE)) {
                            return CS_UTF16BE;
                        }
                        if (StringUtil.equalEncodings(csName, CS_UTF16LE)) {
                            return CS_UTF16LE;
                        }
                        if (StringUtil.equalEncodings(csName, CS_UTF16)) {
                            return CS_UTF16;
                        }
                        if (StringUtil.equalEncodings(csName, CS_UTF32BE)) {
                            return CS_UTF32BE;
                        }
                        if (StringUtil.equalEncodings(csName, CS_UTF32LE)) {
                            return CS_UTF32LE;
                        }
                        if (StringUtil.equalEncodings(csName, CS_UTF32)) {
                            return CS_UTF32;
                        }
                        if (!StringUtil.equalEncodings(csName, "UTF")) return csName;
                        return CS_UTF16;
                    }
                }
                return csName;
            }
        }
        return csName;
    }

    public static String findEncodingFor(Writer w) {
        if (w instanceof OutputStreamWriter) {
            String enc = ((OutputStreamWriter)w).getEncoding();
            return CharsetNames.normalize(enc);
        }
        return null;
    }
}


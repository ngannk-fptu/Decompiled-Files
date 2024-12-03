/*
 * Decompiled with CFR 0.152.
 */
package com.ctc.wstx.shaded.msv_core.datatype.xsd;

import com.ctc.wstx.shaded.msv.relaxng_datatype.ValidationContext;
import com.ctc.wstx.shaded.msv_core.datatype.SerializationContext;
import com.ctc.wstx.shaded.msv_core.datatype.xsd.BuiltinAtomicType;
import com.ctc.wstx.shaded.msv_core.datatype.xsd.Discrete;
import com.ctc.wstx.shaded.msv_core.datatype.xsd.SimpleURType;
import com.ctc.wstx.shaded.msv_core.datatype.xsd.UnicodeUtil;
import com.ctc.wstx.shaded.msv_core.datatype.xsd.XSDatatype;
import com.ctc.wstx.shaded.msv_core.datatype.xsd.regex.RegExp;
import com.ctc.wstx.shaded.msv_core.datatype.xsd.regex.RegExpFactory;
import java.text.ParseException;

public class AnyURIType
extends BuiltinAtomicType
implements Discrete {
    public static final AnyURIType theInstance = new AnyURIType();
    private static final boolean[] isUric = AnyURIType.createUricMap();
    static final RegExp regexp = AnyURIType.createRegExp();
    private static final long serialVersionUID = 1L;

    private AnyURIType() {
        super("anyURI");
    }

    protected boolean checkFormat(String content, ValidationContext context) {
        return regexp.matches(AnyURIType.escape(content));
    }

    private static void appendHex(StringBuffer buf, int hex) {
        if (hex < 10) {
            buf.append((char)(hex + 48));
        } else {
            buf.append((char)(hex - 10 + 65));
        }
    }

    private static void appendByte(StringBuffer buf, int ch) {
        buf.append('%');
        AnyURIType.appendHex(buf, ch / 16);
        AnyURIType.appendHex(buf, ch % 16);
    }

    private static void appendEscaped(StringBuffer buf, char ch) {
        if (ch < '\u007f') {
            AnyURIType.appendByte(buf, ch);
            return;
        }
        if (ch < '\u07ff') {
            AnyURIType.appendByte(buf, 192 + (ch >> 6));
            AnyURIType.appendByte(buf, 128 + ch % 64);
            return;
        }
        if (ch < '\uffff') {
            AnyURIType.appendByte(buf, 224 + (ch >> 12));
            AnyURIType.appendByte(buf, 128 + (ch >> 6) % 64);
            AnyURIType.appendByte(buf, 128 + ch % 64);
        }
    }

    private static void appendEscaped(StringBuffer buf, char ch1, char ch2) {
        int ucs = ((ch1 & 0x3FF) << 10) + (ch2 & 0x3FF);
        AnyURIType.appendByte(buf, 240 + (ucs >> 18));
        AnyURIType.appendByte(buf, 128 + (ucs >> 12) % 64);
        AnyURIType.appendByte(buf, 128 + (ucs >> 6) % 64);
        AnyURIType.appendByte(buf, 128 + ucs % 64);
    }

    private static boolean[] createUricMap() {
        int i;
        boolean[] r = new boolean[128];
        for (i = 97; i <= 122; ++i) {
            r[i] = true;
        }
        for (i = 65; i <= 90; ++i) {
            r[i] = true;
        }
        for (i = 48; i <= 57; ++i) {
            r[i] = true;
        }
        char[] mark = new char[]{'-', '_', '.', '!', '~', '*', '\'', '(', ')', '#', '%', '[', ']'};
        for (int i2 = 0; i2 < mark.length; ++i2) {
            r[mark[i2]] = true;
        }
        char[] reserved = new char[]{';', '/', '?', ':', '@', '&', '=', '+', '$', ','};
        for (int i3 = 0; i3 < reserved.length; ++i3) {
            r[reserved[i3]] = true;
        }
        return r;
    }

    public static String escape(String content) {
        StringBuffer escaped = new StringBuffer(content.length());
        for (int i = 0; i < content.length(); ++i) {
            char ch = content.charAt(i);
            if (ch < '\u0080' && isUric[ch]) {
                escaped.append(ch);
                continue;
            }
            if ('\ud800' <= ch && ch < '\udc00') {
                AnyURIType.appendEscaped(escaped, ch, content.charAt(++i));
                continue;
            }
            AnyURIType.appendEscaped(escaped, ch);
        }
        return new String(escaped);
    }

    static RegExp createRegExp() {
        String alpha = "[a-zA-Z]";
        String alphanum = "[0-9a-zA-Z]";
        String hex = "[0-9a-fA-F]";
        String escaped = "%" + hex + "{2}";
        String mark = "[\\-_\\.!~\\*'\\(\\)]";
        String unreserved = "(" + alphanum + "|" + mark + ")";
        String reserved = "[;/\\?:@&=\\+$,\\[\\]]";
        String uric = "(" + reserved + "|" + unreserved + "|" + escaped + ")";
        String fragment = uric + "*";
        String query = uric + "*";
        String pchar = "(" + unreserved + "|" + escaped + "|[:@&=\\+$,])";
        String param = pchar + "*";
        String segment = "(" + param + "(;" + param + ")*)";
        String pathSegments = "(" + segment + "(/" + segment + ")*)";
        String port = "[0-9]*";
        String __upTo3digits = "[0-9]{1,3}";
        String IPv4address = __upTo3digits + "\\." + __upTo3digits + "\\." + __upTo3digits + "\\." + __upTo3digits;
        String hex4 = hex + "{1,4}";
        String hexseq = hex4 + "(:" + hex4 + ")*";
        String hexpart = "((" + hexseq + "(::(" + hexseq + ")?)?)|(::(" + hexseq + ")?))";
        String IPv6address = "((" + hexpart + "(:" + IPv4address + ")?)|(::" + IPv4address + "))";
        String IPv6reference = "\\[" + IPv6address + "\\]";
        String domainlabel = alphanum + "([0-9A-Za-z\\-]*" + alphanum + ")?";
        String toplabel = alpha + "([0-9A-Za-z\\-]*" + alphanum + ")?";
        String hostname = "(" + domainlabel + "\\.)*" + toplabel + "(\\.)?";
        String host = "((" + hostname + ")|(" + IPv4address + ")|(" + IPv6reference + "))";
        String hostport = host + "(:" + port + ")?";
        String userinfo = "(" + unreserved + "|" + escaped + "|[;:&=\\+$,])*";
        String server = "((" + userinfo + "@)?" + hostport + ")?";
        String regName = "(" + unreserved + "|" + escaped + "|[$,;:@&=\\+])+";
        String authority = "((" + server + ")|(" + regName + "))";
        String scheme = alpha + "[A-Za-z0-9\\+\\-\\.]*";
        String relSegment = "(" + unreserved + "|" + escaped + "|[;@&=\\+$,])+";
        String absPath = "/" + pathSegments;
        String relPath = relSegment + "(" + absPath + ")?";
        String netPath = "//" + authority + "(" + absPath + ")?";
        String uricNoSlash = "(" + unreserved + "|" + escaped + "|[;\\?:@&=\\+$,])";
        String opaquePart = uricNoSlash + "(" + uric + ")*";
        String hierPart = "((" + netPath + ")|(" + absPath + "))(\\?" + query + ")?";
        String relativeURI = "((" + netPath + ")|(" + absPath + ")|(" + relPath + "))(\\?" + query + ")?";
        String absoluteURI = scheme + ":((" + hierPart + ")|(" + opaquePart + "))";
        String uriRef = "(" + absoluteURI + "|" + relativeURI + ")?(#" + fragment + ")?";
        try {
            return RegExpFactory.createFactory().compile(uriRef);
        }
        catch (ParseException e) {
            throw new Error();
        }
    }

    public Object _createValue(String content, ValidationContext context) {
        if (!regexp.matches(AnyURIType.escape(content))) {
            return null;
        }
        return content;
    }

    public String convertToLexicalValue(Object value, SerializationContext context) {
        if (value instanceof String) {
            return (String)value;
        }
        throw new IllegalArgumentException();
    }

    public final int isFacetApplicable(String facetName) {
        if (facetName.equals("length") || facetName.equals("minLength") || facetName.equals("maxLength") || facetName.equals("pattern") || facetName.equals("whiteSpace") || facetName.equals("enumeration")) {
            return 0;
        }
        return -2;
    }

    public final int countLength(Object value) {
        return UnicodeUtil.countLength((String)value);
    }

    public Class getJavaObjectType() {
        return String.class;
    }

    public XSDatatype getBaseType() {
        return SimpleURType.theInstance;
    }
}


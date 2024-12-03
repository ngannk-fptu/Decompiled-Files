/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.asn1.x500.style;

import java.io.IOException;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1String;
import org.bouncycastle.asn1.ASN1UniversalString;
import org.bouncycastle.asn1.x500.AttributeTypeAndValue;
import org.bouncycastle.asn1.x500.RDN;
import org.bouncycastle.asn1.x500.X500NameBuilder;
import org.bouncycastle.asn1.x500.X500NameStyle;
import org.bouncycastle.asn1.x500.style.X500NameTokenizer;
import org.bouncycastle.util.Strings;
import org.bouncycastle.util.encoders.Hex;

public class IETFUtils {
    private static String unescape(String elt) {
        if (elt.length() == 0 || elt.indexOf(92) < 0 && elt.indexOf(34) < 0) {
            return elt.trim();
        }
        char[] elts = elt.toCharArray();
        boolean escaped = false;
        boolean quoted = false;
        StringBuffer buf = new StringBuffer(elt.length());
        int start = 0;
        if (elts[0] == '\\' && elts[1] == '#') {
            start = 2;
            buf.append("\\#");
        }
        boolean nonWhiteSpaceEncountered = false;
        int lastEscaped = 0;
        char hex1 = '\u0000';
        for (int i = start; i != elts.length; ++i) {
            char c = elts[i];
            if (c != ' ') {
                nonWhiteSpaceEncountered = true;
            }
            if (c == '\"') {
                if (!escaped) {
                    quoted = !quoted;
                } else {
                    buf.append(c);
                }
                escaped = false;
                continue;
            }
            if (c == '\\' && !escaped && !quoted) {
                escaped = true;
                lastEscaped = buf.length();
                continue;
            }
            if (c == ' ' && !escaped && !nonWhiteSpaceEncountered) continue;
            if (escaped && IETFUtils.isHexDigit(c)) {
                if (hex1 != '\u0000') {
                    buf.append((char)(IETFUtils.convertHex(hex1) * 16 + IETFUtils.convertHex(c)));
                    escaped = false;
                    hex1 = '\u0000';
                    continue;
                }
                hex1 = c;
                continue;
            }
            buf.append(c);
            escaped = false;
        }
        if (buf.length() > 0) {
            while (buf.charAt(buf.length() - 1) == ' ' && lastEscaped != buf.length() - 1) {
                buf.setLength(buf.length() - 1);
            }
        }
        return buf.toString();
    }

    private static boolean isHexDigit(char c) {
        return '0' <= c && c <= '9' || 'a' <= c && c <= 'f' || 'A' <= c && c <= 'F';
    }

    private static int convertHex(char c) {
        if ('0' <= c && c <= '9') {
            return c - 48;
        }
        if ('a' <= c && c <= 'f') {
            return c - 97 + 10;
        }
        return c - 65 + 10;
    }

    public static RDN[] rDNsFromString(String name, X500NameStyle x500Style) {
        X500NameTokenizer nTok = new X500NameTokenizer(name);
        X500NameBuilder builder = new X500NameBuilder(x500Style);
        while (nTok.hasMoreTokens()) {
            String token = nTok.nextToken();
            if (token.indexOf(43) > 0) {
                X500NameTokenizer pTok = new X500NameTokenizer(token, '+');
                X500NameTokenizer vTok = new X500NameTokenizer(pTok.nextToken(), '=');
                String attr = vTok.nextToken();
                if (!vTok.hasMoreTokens()) {
                    throw new IllegalArgumentException("badly formatted directory string");
                }
                String value = vTok.nextToken();
                ASN1ObjectIdentifier oid = x500Style.attrNameToOID(attr.trim());
                if (pTok.hasMoreTokens()) {
                    Vector<ASN1ObjectIdentifier> oids = new Vector<ASN1ObjectIdentifier>();
                    Vector<String> values = new Vector<String>();
                    oids.addElement(oid);
                    values.addElement(IETFUtils.unescape(value));
                    while (pTok.hasMoreTokens()) {
                        vTok = new X500NameTokenizer(pTok.nextToken(), '=');
                        attr = vTok.nextToken();
                        if (!vTok.hasMoreTokens()) {
                            throw new IllegalArgumentException("badly formatted directory string");
                        }
                        value = vTok.nextToken();
                        oid = x500Style.attrNameToOID(attr.trim());
                        oids.addElement(oid);
                        values.addElement(IETFUtils.unescape(value));
                    }
                    builder.addMultiValuedRDN(IETFUtils.toOIDArray(oids), IETFUtils.toValueArray(values));
                    continue;
                }
                builder.addRDN(oid, IETFUtils.unescape(value));
                continue;
            }
            X500NameTokenizer vTok = new X500NameTokenizer(token, '=');
            String attr = vTok.nextToken();
            if (!vTok.hasMoreTokens()) {
                throw new IllegalArgumentException("badly formatted directory string");
            }
            String value = vTok.nextToken();
            ASN1ObjectIdentifier oid = x500Style.attrNameToOID(attr.trim());
            builder.addRDN(oid, IETFUtils.unescape(value));
        }
        return builder.build().getRDNs();
    }

    private static String[] toValueArray(Vector values) {
        String[] tmp = new String[values.size()];
        for (int i = 0; i != tmp.length; ++i) {
            tmp[i] = (String)values.elementAt(i);
        }
        return tmp;
    }

    private static ASN1ObjectIdentifier[] toOIDArray(Vector oids) {
        ASN1ObjectIdentifier[] tmp = new ASN1ObjectIdentifier[oids.size()];
        for (int i = 0; i != tmp.length; ++i) {
            tmp[i] = (ASN1ObjectIdentifier)oids.elementAt(i);
        }
        return tmp;
    }

    public static String[] findAttrNamesForOID(ASN1ObjectIdentifier oid, Hashtable lookup) {
        int count = 0;
        Enumeration en = lookup.elements();
        while (en.hasMoreElements()) {
            if (!oid.equals(en.nextElement())) continue;
            ++count;
        }
        String[] aliases = new String[count];
        count = 0;
        Enumeration en2 = lookup.keys();
        while (en2.hasMoreElements()) {
            String key = (String)en2.nextElement();
            if (!oid.equals(lookup.get(key))) continue;
            aliases[count++] = key;
        }
        return aliases;
    }

    public static ASN1ObjectIdentifier decodeAttrName(String name, Hashtable lookUp) {
        if (Strings.toUpperCase(name).startsWith("OID.")) {
            return new ASN1ObjectIdentifier(name.substring(4));
        }
        if (name.charAt(0) >= '0' && name.charAt(0) <= '9') {
            return new ASN1ObjectIdentifier(name);
        }
        ASN1ObjectIdentifier oid = (ASN1ObjectIdentifier)lookUp.get(Strings.toLowerCase(name));
        if (oid == null) {
            throw new IllegalArgumentException("Unknown object id - " + name + " - passed to distinguished name");
        }
        return oid;
    }

    public static ASN1Encodable valueFromHexString(String str, int off) throws IOException {
        byte[] data = new byte[(str.length() - off) / 2];
        for (int index = 0; index != data.length; ++index) {
            char left = str.charAt(index * 2 + off);
            char right = str.charAt(index * 2 + off + 1);
            data[index] = (byte)(IETFUtils.convertHex(left) << 4 | IETFUtils.convertHex(right));
        }
        return ASN1Primitive.fromByteArray(data);
    }

    public static void appendRDN(StringBuffer buf, RDN rdn, Hashtable oidSymbols) {
        if (rdn.isMultiValued()) {
            AttributeTypeAndValue[] atv = rdn.getTypesAndValues();
            boolean firstAtv = true;
            for (int j = 0; j != atv.length; ++j) {
                if (firstAtv) {
                    firstAtv = false;
                } else {
                    buf.append('+');
                }
                IETFUtils.appendTypeAndValue(buf, atv[j], oidSymbols);
            }
        } else if (rdn.getFirst() != null) {
            IETFUtils.appendTypeAndValue(buf, rdn.getFirst(), oidSymbols);
        }
    }

    public static void appendTypeAndValue(StringBuffer buf, AttributeTypeAndValue typeAndValue, Hashtable oidSymbols) {
        String sym = (String)oidSymbols.get(typeAndValue.getType());
        if (sym != null) {
            buf.append(sym);
        } else {
            buf.append(typeAndValue.getType().getId());
        }
        buf.append('=');
        buf.append(IETFUtils.valueToString(typeAndValue.getValue()));
    }

    public static String valueToString(ASN1Encodable value) {
        int start;
        StringBuffer vBuf = new StringBuffer();
        if (value instanceof ASN1String && !(value instanceof ASN1UniversalString)) {
            String v = ((ASN1String)((Object)value)).getString();
            if (v.length() > 0 && v.charAt(0) == '#') {
                vBuf.append('\\');
            }
            vBuf.append(v);
        } else {
            try {
                vBuf.append('#');
                vBuf.append(Hex.toHexString(value.toASN1Primitive().getEncoded("DER")));
            }
            catch (IOException e) {
                throw new IllegalArgumentException("Other value has no encoded form");
            }
        }
        int end = vBuf.length();
        int index = 0;
        if (vBuf.length() >= 2 && vBuf.charAt(0) == '\\' && vBuf.charAt(1) == '#') {
            index += 2;
        }
        block5: while (index != end) {
            switch (vBuf.charAt(index)) {
                case '\"': 
                case '+': 
                case ',': 
                case ';': 
                case '<': 
                case '=': 
                case '>': 
                case '\\': {
                    vBuf.insert(index, "\\");
                    index += 2;
                    ++end;
                    continue block5;
                }
            }
            ++index;
        }
        if (vBuf.length() > 0) {
            for (start = 0; vBuf.length() > start && vBuf.charAt(start) == ' '; start += 2) {
                vBuf.insert(start, "\\");
            }
        }
        for (int endBuf = vBuf.length() - 1; endBuf >= start && vBuf.charAt(endBuf) == ' '; --endBuf) {
            vBuf.insert(endBuf, '\\');
        }
        return vBuf.toString();
    }

    public static String canonicalize(String s) {
        int end;
        int start;
        int length;
        ASN1Primitive obj;
        if (s.length() > 0 && s.charAt(0) == '#' && (obj = IETFUtils.decodeObject(s)) instanceof ASN1String) {
            s = ((ASN1String)((Object)obj)).getString();
        }
        if ((length = (s = Strings.toLowerCase(s)).length()) < 2) {
            return s;
        }
        int last = length - 1;
        for (start = 0; start < last && s.charAt(start) == '\\' && s.charAt(start + 1) == ' '; start += 2) {
        }
        int first = start + 1;
        for (end = last; end > first && s.charAt(end - 1) == '\\' && s.charAt(end) == ' '; end -= 2) {
        }
        if (start > 0 || end < last) {
            s = s.substring(start, end + 1);
        }
        return IETFUtils.stripInternalSpaces(s);
    }

    public static String canonicalString(ASN1Encodable value) {
        return IETFUtils.canonicalize(IETFUtils.valueToString(value));
    }

    private static ASN1Primitive decodeObject(String oValue) {
        try {
            return ASN1Primitive.fromByteArray(Hex.decodeStrict(oValue, 1, oValue.length() - 1));
        }
        catch (IOException e) {
            throw new IllegalStateException("unknown encoding in name: " + e);
        }
    }

    public static String stripInternalSpaces(String str) {
        if (str.indexOf("  ") < 0) {
            return str;
        }
        StringBuffer res = new StringBuffer();
        char c1 = str.charAt(0);
        res.append(c1);
        for (int k = 1; k < str.length(); ++k) {
            char c2 = str.charAt(k);
            if (c1 == ' ' && c2 == ' ') continue;
            res.append(c2);
            c1 = c2;
        }
        return res.toString();
    }

    public static boolean rDNAreEqual(RDN rdn1, RDN rdn2) {
        AttributeTypeAndValue[] atvs2;
        if (rdn1.size() != rdn2.size()) {
            return false;
        }
        AttributeTypeAndValue[] atvs1 = rdn1.getTypesAndValues();
        if (atvs1.length != (atvs2 = rdn2.getTypesAndValues()).length) {
            return false;
        }
        for (int i = 0; i != atvs1.length; ++i) {
            if (IETFUtils.atvAreEqual(atvs1[i], atvs2[i])) continue;
            return false;
        }
        return true;
    }

    private static boolean atvAreEqual(AttributeTypeAndValue atv1, AttributeTypeAndValue atv2) {
        String v2;
        ASN1ObjectIdentifier o2;
        if (atv1 == atv2) {
            return true;
        }
        if (null == atv1 || null == atv2) {
            return false;
        }
        ASN1ObjectIdentifier o1 = atv1.getType();
        if (!o1.equals(o2 = atv2.getType())) {
            return false;
        }
        String v1 = IETFUtils.canonicalString(atv1.getValue());
        return v1.equals(v2 = IETFUtils.canonicalString(atv2.getValue()));
    }
}


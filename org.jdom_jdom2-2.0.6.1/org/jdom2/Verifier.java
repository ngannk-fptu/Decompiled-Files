/*
 * Decompiled with CFR 0.152.
 */
package org.jdom2;

import java.util.Iterator;
import java.util.List;
import org.jdom2.Attribute;
import org.jdom2.Element;
import org.jdom2.Namespace;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public final class Verifier {
    private static final byte[] VALCONST = new byte[]{0, 1, 0, 1, 0, 1, 65, 1, 65, 73, 65, 89, 65, 1, 65, 1, 65, 79, 1, 77, 1, 79, 1, 65, 1, 9, 1, 15, 1, 15, 1, 15, 1, 15, 1, 15, 1, 15, 1, 15, 1, 15, 1, 15, 1, 15, 1, 15, 1, 15, 1, 9, 1, 41, 1, 41, 1, 15, 9, 15, 1, 15, 1, 15, 1, 15, 1, 15, 1, 15, 1, 15, 1, 15, 1, 15, 1, 15, 1, 15, 1, 15, 1, 15, 1, 15, 1, 41, 1, 15, 1, 15, 1, 15, 1, 15, 1, 15, 1, 15, 1, 15, 1, 15, 1, 15, 1, 41, 1, 41, 1, 41, 1, 41, 1, 41, 1, 41, 1, 15, 1, 15, 1, 15, 1, 9, 15, 41, 1, 25, 1, 41, 15, 1, 15, 1, 15, 1, 15, 1, 15, 41, 15, 41, 1, 41, 1, 25, 1, 41, 1, 15, 1, 41, 15, 41, 1, 41, 1, 15, 41, 1, 25, 1, 41, 1, 15, 1, 15, 1, 15, 1, 15, 1, 15, 1, 15, 1, 41, 1, 41, 1, 41, 1, 41, 1, 41, 1, 15, 1, 15, 41, 1, 25, 15, 1, 41, 1, 15, 1, 15, 1, 15, 1, 15, 1, 15, 1, 15, 1, 15, 1, 41, 1, 41, 1, 41, 1, 41, 1, 15, 1, 15, 1, 25, 41, 15, 1, 41, 1, 15, 1, 15, 1, 15, 1, 15, 1, 15, 1, 15, 1, 15, 1, 41, 15, 41, 1, 41, 1, 41, 1, 15, 1, 25, 1, 41, 1, 15, 1, 15, 1, 15, 1, 15, 1, 15, 1, 15, 1, 41, 15, 41, 1, 41, 1, 41, 1, 41, 1, 15, 1, 15, 1, 25, 1, 41, 1, 15, 1, 15, 1, 15, 1, 15, 1, 15, 1, 15, 1, 15, 1, 15, 1, 15, 1, 15, 1, 41, 1, 41, 1, 41, 1, 41, 1, 25, 1, 41, 1, 15, 1, 15, 1, 15, 1, 15, 1, 15, 1, 41, 1, 41, 1, 41, 1, 41, 1, 15, 1, 25, 1, 41, 1, 15, 1, 15, 1, 15, 1, 15, 1, 15, 1, 41, 1, 41, 1, 41, 1, 41, 1, 15, 1, 15, 1, 25, 1, 41, 1, 15, 1, 15, 1, 15, 1, 15, 1, 41, 1, 41, 1, 41, 1, 41, 1, 15, 1, 25, 1, 15, 1, 15, 41, 15, 41, 1, 15, 9, 41, 1, 25, 1, 15, 1, 15, 1, 15, 1, 15, 1, 15, 1, 15, 1, 15, 1, 15, 1, 15, 1, 15, 1, 15, 1, 15, 1, 15, 41, 15, 41, 1, 41, 15, 1, 15, 1, 9, 1, 41, 1, 25, 1, 41, 1, 25, 1, 41, 1, 41, 1, 41, 1, 41, 15, 1, 15, 1, 41, 1, 41, 1, 41, 1, 41, 1, 41, 1, 41, 1, 41, 1, 15, 1, 15, 1, 15, 1, 15, 1, 15, 1, 15, 1, 15, 1, 15, 1, 15, 1, 15, 1, 15, 1, 15, 1, 15, 1, 15, 1, 15, 1, 15, 1, 15, 1, 15, 1, 15, 1, 15, 1, 15, 1, 15, 1, 15, 1, 15, 1, 15, 1, 15, 1, 15, 1, 15, 1, 15, 1, 15, 1, 15, 1, 15, 1, 15, 1, 15, 1, 15, 1, 15, 1, 15, 1, 15, 1, 15, 1, 15, 1, 15, 1, 15, 1, 15, 1, 15, 1, 15, 1, 15, 1, 15, 1, 15, 1, 15, 1, 15, 1, 15, 1, 15, 1, 15, 1, 15, 1, 15, 1, 41, 1, 41, 1, 15, 1, 15, 1, 15, 1, 15, 1, 9, 1, 15, 1, 15, 41, 1, 9, 1, 15, 1, 41, 1, 9, 1, 15, 1, 9, 1, 15, 1, 15, 1, 15, 1, 0, 1, 0};
    private static final int[] LENCONST = new int[]{9, 2, 2, 1, 18, 1, 1, 2, 9, 2, 1, 10, 1, 2, 1, 1, 2, 26, 4, 1, 1, 26, 3, 1, 56, 1, 8, 23, 1, 31, 1, 58, 2, 11, 2, 8, 1, 53, 1, 68, 9, 36, 3, 2, 4, 30, 56, 89, 18, 7, 14, 2, 46, 70, 26, 2, 36, 1, 1, 3, 1, 1, 1, 20, 1, 44, 1, 7, 3, 1, 1, 1, 1, 1, 1, 1, 1, 18, 13, 12, 1, 66, 1, 12, 1, 36, 1, 4, 9, 53, 2, 2, 2, 2, 3, 28, 2, 8, 2, 2, 55, 38, 2, 1, 7, 38, 10, 17, 1, 23, 1, 3, 1, 1, 1, 2, 1, 1, 11, 27, 5, 3, 46, 26, 5, 1, 10, 8, 13, 10, 6, 1, 71, 2, 5, 1, 15, 1, 4, 1, 1, 15, 2, 2, 1, 4, 2, 10, 519, 3, 1, 53, 2, 1, 1, 16, 3, 4, 3, 10, 2, 2, 10, 17, 3, 1, 8, 2, 2, 2, 22, 1, 7, 1, 1, 3, 4, 2, 1, 1, 7, 2, 2, 2, 3, 9, 1, 4, 2, 1, 3, 2, 2, 10, 2, 16, 1, 2, 6, 4, 2, 2, 22, 1, 7, 1, 2, 1, 2, 1, 2, 2, 1, 1, 5, 4, 2, 2, 3, 11, 4, 1, 1, 7, 10, 2, 3, 12, 3, 1, 7, 1, 1, 1, 3, 1, 22, 1, 7, 1, 2, 1, 5, 2, 1, 1, 8, 1, 3, 1, 3, 18, 1, 5, 10, 17, 3, 1, 8, 2, 2, 2, 22, 1, 7, 1, 2, 2, 4, 2, 1, 1, 6, 3, 2, 2, 3, 8, 2, 4, 2, 1, 3, 4, 10, 18, 2, 1, 6, 3, 3, 1, 4, 3, 2, 1, 1, 1, 2, 3, 2, 3, 3, 3, 8, 1, 3, 4, 5, 3, 3, 1, 4, 9, 1, 15, 9, 17, 3, 1, 8, 1, 3, 1, 23, 1, 10, 1, 5, 4, 7, 1, 3, 1, 4, 7, 2, 9, 2, 4, 10, 18, 2, 1, 8, 1, 3, 1, 23, 1, 10, 1, 5, 4, 7, 1, 3, 1, 4, 7, 2, 7, 1, 1, 2, 4, 10, 18, 2, 1, 8, 1, 3, 1, 23, 1, 16, 4, 6, 2, 3, 1, 4, 9, 1, 8, 2, 4, 10, 145, 46, 1, 1, 1, 2, 7, 5, 6, 1, 8, 1, 10, 39, 2, 1, 1, 2, 2, 1, 1, 2, 1, 6, 4, 1, 7, 1, 3, 1, 1, 1, 1, 2, 2, 1, 2, 1, 1, 1, 2, 6, 1, 2, 1, 2, 5, 1, 1, 1, 6, 2, 10, 62, 2, 6, 10, 11, 1, 1, 1, 1, 1, 4, 2, 8, 1, 33, 7, 20, 1, 6, 4, 6, 1, 1, 1, 21, 3, 7, 1, 1, 230, 38, 10, 39, 9, 1, 1, 2, 1, 3, 1, 1, 1, 2, 1, 5, 41, 1, 1, 1, 1, 1, 11, 1, 1, 1, 1, 1, 3, 2, 3, 1, 5, 3, 1, 1, 1, 1, 1, 1, 1, 1, 3, 2, 3, 2, 1, 1, 40, 1, 9, 1, 2, 1, 2, 2, 7, 2, 1, 1, 1, 7, 40, 1, 4, 1, 8, 1, 3078, 156, 4, 90, 6, 22, 2, 6, 2, 38, 2, 6, 2, 8, 1, 1, 1, 1, 1, 1, 1, 31, 2, 53, 1, 7, 1, 1, 3, 3, 1, 7, 3, 4, 2, 6, 4, 13, 5, 3, 1, 7, 211, 13, 4, 1, 68, 1, 3, 2, 2, 1, 81, 3, 3714, 1, 1, 1, 25, 9, 6, 1, 5, 11, 84, 4, 2, 2, 2, 2, 90, 1, 3, 6, 40, 7379, 20902, 3162, 11172, 92, 2048, 8190, 2};
    private static final int CHARCNT = 65536;
    private static final byte[] CHARFLAGS = Verifier.buildBitFlags();
    private static final byte MASKXMLCHARACTER = 1;
    private static final byte MASKXMLLETTER = 2;
    private static final byte MASKXMLSTARTCHAR = 4;
    private static final byte MASKXMLNAMECHAR = 8;
    private static final byte MASKXMLDIGIT = 16;
    private static final byte MASKXMLCOMBINING = 32;
    private static final byte MASKURICHAR = 64;
    private static final byte MASKXMLLETTERORDIGIT = 18;

    private static final byte[] buildBitFlags() {
        byte[] ret = new byte[65536];
        int index = 0;
        for (int i = 0; i < VALCONST.length; ++i) {
            byte v = VALCONST[i];
            int l = LENCONST[i];
            while (--l >= 0) {
                ret[index++] = v;
            }
        }
        return ret;
    }

    private Verifier() {
    }

    private static final String checkJDOMName(String name) {
        if (name == null) {
            return "XML names cannot be null";
        }
        if (name.length() == 0) {
            return "XML names cannot be empty";
        }
        if (0 == (CHARFLAGS[name.charAt(0)] & 4)) {
            return "XML name '" + name + "' cannot begin with the character \"" + name.charAt(0) + "\"";
        }
        for (int i = name.length() - 1; i >= 1; --i) {
            if (0 != (byte)(CHARFLAGS[name.charAt(i)] & 8)) continue;
            return "XML name '" + name + "' cannot contain the character \"" + name.charAt(i) + "\"";
        }
        return null;
    }

    public static String checkElementName(String name) {
        return Verifier.checkJDOMName(name);
    }

    public static String checkAttributeName(String name) {
        if ("xmlns".equals(name)) {
            return "An Attribute name may not be \"xmlns\"; use the Namespace class to manage namespaces";
        }
        return Verifier.checkJDOMName(name);
    }

    public static String checkCharacterData(String text) {
        if (text == null) {
            return "A null is not a legal XML value";
        }
        int len = text.length();
        for (int i = 0; i < len; ++i) {
            while (CHARFLAGS[text.charAt(i)] != 0) {
                if (++i != len) continue;
                return null;
            }
            if (Verifier.isHighSurrogate(text.charAt(i))) {
                if (++i >= len) {
                    return String.format("Truncated Surrogate Pair 0x%04x????", text.charAt(i - 1));
                }
                if (Verifier.isLowSurrogate(text.charAt(i))) {
                    if (Verifier.isXMLCharacter(Verifier.decodeSurrogatePair(text.charAt(i - 1), text.charAt(i)))) continue;
                    return String.format("0x%06x is not a legal XML character", Verifier.decodeSurrogatePair(text.charAt(i - 1), text.charAt(i)));
                }
                return String.format("Illegal Surrogate Pair 0x%04x%04x", text.charAt(i - 1), (int)text.charAt(i));
            }
            return String.format("0x%04x is not a legal XML character", text.charAt(i));
        }
        return null;
    }

    public static String checkCDATASection(String data) {
        String reason = null;
        reason = Verifier.checkCharacterData(data);
        if (reason != null) {
            return reason;
        }
        if (data.indexOf("]]>") != -1) {
            return "CDATA cannot internally contain a CDATA ending delimiter (]]>)";
        }
        return null;
    }

    public static String checkNamespacePrefix(String prefix) {
        if (prefix == null || prefix.equals("")) {
            return null;
        }
        if (Verifier.checkJDOMName(prefix) != null) {
            return Verifier.checkJDOMName(prefix);
        }
        return null;
    }

    public static String checkNamespaceURI(String uri) {
        if (uri == null || uri.equals("")) {
            return null;
        }
        char first = uri.charAt(0);
        if (Character.isDigit(first)) {
            return "Namespace URIs cannot begin with a number";
        }
        if (first == '$') {
            return "Namespace URIs cannot begin with a dollar sign ($)";
        }
        if (first == '-') {
            return "Namespace URIs cannot begin with a hyphen (-)";
        }
        if (Verifier.isXMLWhitespace(first)) {
            return "Namespace URIs cannot begin with white-space";
        }
        return null;
    }

    public static String checkNamespaceCollision(Namespace namespace, Namespace other) {
        String reason = null;
        String p1 = namespace.getPrefix();
        String u1 = namespace.getURI();
        String p2 = other.getPrefix();
        String u2 = other.getURI();
        if (p1.equals(p2) && !u1.equals(u2)) {
            reason = "The namespace prefix \"" + p1 + "\" collides";
        }
        return reason;
    }

    public static String checkNamespaceCollision(Attribute attribute, Element element) {
        return Verifier.checkNamespaceCollision(attribute, element, -1);
    }

    public static String checkNamespaceCollision(Attribute attribute, Element element, int ignoreatt) {
        Namespace namespace = attribute.getNamespace();
        String prefix = namespace.getPrefix();
        if ("".equals(prefix)) {
            return null;
        }
        return Verifier.checkNamespaceCollision(namespace, element, ignoreatt);
    }

    public static String checkNamespaceCollision(Namespace namespace, Element element) {
        return Verifier.checkNamespaceCollision(namespace, element, -1);
    }

    public static String checkNamespaceCollision(Namespace namespace, Element element, int ignoreatt) {
        String reason = Verifier.checkNamespaceCollision(namespace, element.getNamespace());
        if (reason != null) {
            return reason + " with the element namespace prefix";
        }
        if (element.hasAdditionalNamespaces() && (reason = Verifier.checkNamespaceCollision(namespace, element.getAdditionalNamespaces())) != null) {
            return reason;
        }
        if (element.hasAttributes() && (reason = Verifier.checkNamespaceCollision(namespace, element.getAttributes(), ignoreatt)) != null) {
            return reason;
        }
        return null;
    }

    public static String checkNamespaceCollision(Namespace namespace, Attribute attribute) {
        String reason = null;
        if (!attribute.getNamespace().equals(Namespace.NO_NAMESPACE) && (reason = Verifier.checkNamespaceCollision(namespace, attribute.getNamespace())) != null) {
            reason = reason + " with an attribute namespace prefix on the element";
        }
        return reason;
    }

    public static String checkNamespaceCollision(Namespace namespace, List<?> list) {
        return Verifier.checkNamespaceCollision(namespace, list, -1);
    }

    public static String checkNamespaceCollision(Namespace namespace, List<?> list, int ignoreatt) {
        if (list == null) {
            return null;
        }
        String reason = null;
        Iterator<?> i = list.iterator();
        int cnt = -1;
        while (reason == null && i.hasNext()) {
            Object obj = i.next();
            ++cnt;
            if (obj instanceof Attribute) {
                if (cnt == ignoreatt) continue;
                reason = Verifier.checkNamespaceCollision(namespace, (Attribute)obj);
                continue;
            }
            if (obj instanceof Element) {
                reason = Verifier.checkNamespaceCollision(namespace, (Element)obj);
                continue;
            }
            if (!(obj instanceof Namespace) || (reason = Verifier.checkNamespaceCollision(namespace, (Namespace)obj)) == null) continue;
            reason = reason + " with an additional namespace declared by the element";
        }
        return reason;
    }

    public static String checkProcessingInstructionTarget(String target) {
        String reason = Verifier.checkXMLName(target);
        if (reason != null) {
            return reason;
        }
        if (target.indexOf(":") != -1) {
            return "Processing instruction targets cannot contain colons";
        }
        if (target.equalsIgnoreCase("xml")) {
            return "Processing instructions cannot have a target of \"xml\" in any combination of case. (Note that the \"<?xml ... ?>\" declaration at the beginning of a document is not a processing instruction and should not be added as one; it is written automatically during output, e.g. by XMLOutputter.)";
        }
        return null;
    }

    public static String checkProcessingInstructionData(String data) {
        String reason = Verifier.checkCharacterData(data);
        if (reason == null && data.indexOf("?>") >= 0) {
            return "Processing instructions cannot contain the string \"?>\"";
        }
        return reason;
    }

    public static String checkCommentData(String data) {
        String reason = null;
        reason = Verifier.checkCharacterData(data);
        if (reason != null) {
            return reason;
        }
        if (data.indexOf("--") != -1) {
            return "Comments cannot contain double hyphens (--)";
        }
        if (data.endsWith("-")) {
            return "Comment data cannot end with a hyphen.";
        }
        return null;
    }

    public static int decodeSurrogatePair(char high, char low) {
        return 65536 + (high - 55296) * 1024 + (low - 56320);
    }

    public static boolean isXMLPublicIDCharacter(char c) {
        if (c >= 'a' && c <= 'z') {
            return true;
        }
        if (c >= '?' && c <= 'Z') {
            return true;
        }
        if (c >= '\'' && c <= ';') {
            return true;
        }
        if (c == ' ') {
            return true;
        }
        if (c == '!') {
            return true;
        }
        if (c == '=') {
            return true;
        }
        if (c == '#') {
            return true;
        }
        if (c == '$') {
            return true;
        }
        if (c == '_') {
            return true;
        }
        if (c == '%') {
            return true;
        }
        if (c == '\n') {
            return true;
        }
        if (c == '\r') {
            return true;
        }
        return c == '\t';
    }

    public static String checkPublicID(String publicID) {
        String reason = null;
        if (publicID == null) {
            return null;
        }
        for (int i = 0; i < publicID.length(); ++i) {
            char c = publicID.charAt(i);
            if (Verifier.isXMLPublicIDCharacter(c)) continue;
            reason = c + " is not a legal character in public IDs";
            break;
        }
        return reason;
    }

    public static String checkSystemLiteral(String systemLiteral) {
        String reason = null;
        if (systemLiteral == null) {
            return null;
        }
        reason = systemLiteral.indexOf(39) != -1 && systemLiteral.indexOf(34) != -1 ? "System literals cannot simultaneously contain both single and double quotes." : Verifier.checkCharacterData(systemLiteral);
        return reason;
    }

    public static String checkXMLName(String name) {
        if (name == null) {
            return "XML names cannot be null";
        }
        int len = name.length();
        if (len == 0) {
            return "XML names cannot be empty";
        }
        if (!Verifier.isXMLNameStartCharacter(name.charAt(0))) {
            return "XML names cannot begin with the character \"" + name.charAt(0) + "\"";
        }
        for (int i = 1; i < len; ++i) {
            if (Verifier.isXMLNameCharacter(name.charAt(i))) continue;
            return "XML names cannot contain the character \"" + name.charAt(i) + "\"";
        }
        return null;
    }

    public static String checkURI(String uri) {
        if (uri == null || uri.equals("")) {
            return null;
        }
        for (int i = 0; i < uri.length(); ++i) {
            char test = uri.charAt(i);
            if (!Verifier.isURICharacter(test)) {
                String msgNumber = "0x" + Integer.toHexString(test);
                if (test <= '\t') {
                    msgNumber = "0x0" + Integer.toHexString(test);
                }
                return "URIs cannot contain " + msgNumber;
            }
            if (test != '%') continue;
            try {
                char firstDigit = uri.charAt(i + 1);
                char secondDigit = uri.charAt(i + 2);
                if (Verifier.isHexDigit(firstDigit) && Verifier.isHexDigit(secondDigit)) continue;
                return "Percent signs in URIs must be followed by exactly two hexadecimal digits.";
            }
            catch (StringIndexOutOfBoundsException e) {
                return "Percent signs in URIs must be followed by exactly two hexadecimal digits.";
            }
        }
        return null;
    }

    public static boolean isHexDigit(char c) {
        if (c >= '0' && c <= '9') {
            return true;
        }
        if (c >= 'A' && c <= 'F') {
            return true;
        }
        return c >= 'a' && c <= 'f';
    }

    public static boolean isHighSurrogate(char ch) {
        return 54 == ch >>> 10;
    }

    public static boolean isLowSurrogate(char ch) {
        return 55 == ch >>> 10;
    }

    public static boolean isURICharacter(char c) {
        return 0 != (byte)(CHARFLAGS[c] & 0x40);
    }

    public static boolean isXMLCharacter(int c) {
        if (c >= 65536) {
            return c <= 0x10FFFF;
        }
        return 0 != (byte)(CHARFLAGS[c] & 1);
    }

    public static boolean isXMLNameCharacter(char c) {
        return 0 != (byte)(CHARFLAGS[c] & 8) || c == ':';
    }

    public static boolean isXMLNameStartCharacter(char c) {
        return 0 != (byte)(CHARFLAGS[c] & 4) || c == ':';
    }

    public static boolean isXMLLetterOrDigit(char c) {
        return 0 != (byte)(CHARFLAGS[c] & 0x12);
    }

    public static boolean isXMLLetter(char c) {
        return 0 != (byte)(CHARFLAGS[c] & 2);
    }

    public static boolean isXMLCombiningChar(char c) {
        return 0 != (byte)(CHARFLAGS[c] & 0x20);
    }

    public static boolean isXMLExtender(char c) {
        if (c < '\u00b6') {
            return false;
        }
        if (c == '\u00b7') {
            return true;
        }
        if (c == '\u02d0') {
            return true;
        }
        if (c == '\u02d1') {
            return true;
        }
        if (c == '\u0387') {
            return true;
        }
        if (c == '\u0640') {
            return true;
        }
        if (c == '\u0e46') {
            return true;
        }
        if (c == '\u0ec6') {
            return true;
        }
        if (c == '\u3005') {
            return true;
        }
        if (c < '\u3031') {
            return false;
        }
        if (c <= '\u3035') {
            return true;
        }
        if (c < '\u309d') {
            return false;
        }
        if (c <= '\u309e') {
            return true;
        }
        if (c < '\u30fc') {
            return false;
        }
        return c <= '\u30fe';
    }

    public static boolean isXMLDigit(char c) {
        return 0 != (byte)(CHARFLAGS[c] & 0x10);
    }

    public static boolean isXMLWhitespace(char c) {
        return c == ' ' || c == '\n' || c == '\t' || c == '\r';
    }

    public static final boolean isAllXMLWhitespace(String value) {
        int i = value.length();
        while (--i >= 0) {
            if (Verifier.isXMLWhitespace(value.charAt(i))) continue;
            return false;
        }
        return true;
    }
}


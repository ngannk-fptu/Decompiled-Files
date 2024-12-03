/*
 * Decompiled with CFR 0.152.
 */
package org.owasp.validator.html.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class XMLUtil {
    private static final Pattern encgt = Pattern.compile("&gt;");
    private static final Pattern enclt = Pattern.compile("&lt;");
    private static final Pattern encQuot = Pattern.compile("&quot;");
    private static final Pattern encAmp = Pattern.compile("&amp;");
    private static final Pattern gt = Pattern.compile(">");
    private static final Pattern lt = Pattern.compile("<");
    private static final Pattern quot = Pattern.compile("\"");
    private static final Pattern amp = Pattern.compile("&");

    public static String getAttributeValue(Element ele, String attrName) {
        return XMLUtil.decode(ele.getAttribute(attrName));
    }

    public static int getIntValue(Element ele, String tagName, int defaultValue) {
        int toReturn = defaultValue;
        try {
            toReturn = Integer.parseInt(XMLUtil.getTextValue(ele, tagName));
        }
        catch (Throwable throwable) {
            // empty catch block
        }
        return toReturn;
    }

    public static String getTextValue(Element ele, String tagName) {
        String textVal = null;
        NodeList nl = ele.getElementsByTagName(tagName);
        if (nl != null && nl.getLength() > 0) {
            Element el = (Element)nl.item(0);
            textVal = el.getFirstChild() != null ? el.getFirstChild().getNodeValue() : "";
        }
        return XMLUtil.decode(textVal);
    }

    public static boolean getBooleanValue(Element ele, String tagName) {
        boolean boolVal = false;
        NodeList nl = ele.getElementsByTagName(tagName);
        if (nl != null && nl.getLength() > 0) {
            Element el = (Element)nl.item(0);
            boolVal = el.getFirstChild().getNodeValue().equals("true");
        }
        return boolVal;
    }

    public static boolean getBooleanValue(Element ele, String tagName, boolean defaultValue) {
        boolean boolVal = defaultValue;
        NodeList nl = ele.getElementsByTagName(tagName);
        if (nl != null && nl.getLength() > 0) {
            Element el = (Element)nl.item(0);
            boolVal = el.getFirstChild().getNodeValue() != null ? "true".equals(el.getFirstChild().getNodeValue()) : defaultValue;
        }
        return boolVal;
    }

    public static String decode(String str) {
        Matcher ampMatcher;
        Matcher quotMatcher;
        Matcher ltmatcher;
        if (str == null) {
            return null;
        }
        Matcher gtmatcher = encgt.matcher(str);
        if (gtmatcher.matches()) {
            str = gtmatcher.replaceAll(">");
        }
        if ((ltmatcher = enclt.matcher(str)).matches()) {
            str = ltmatcher.replaceAll("<");
        }
        if ((quotMatcher = encQuot.matcher(str)).matches()) {
            str = quotMatcher.replaceAll("\"");
        }
        if ((ampMatcher = encAmp.matcher(str)).matches()) {
            str = ampMatcher.replaceAll("&");
        }
        return str;
    }

    public static String encode(String str) {
        Matcher ampMatcher;
        Matcher quotMatcher;
        Matcher ltMatcher;
        if (str == null) {
            return null;
        }
        Matcher gtMatcher = gt.matcher(str);
        if (gtMatcher.matches()) {
            str = gtMatcher.replaceAll("&gt;");
        }
        if ((ltMatcher = lt.matcher(str)).matches()) {
            str = ltMatcher.replaceAll("&lt;");
        }
        if ((quotMatcher = quot.matcher(str)).matches()) {
            str = quotMatcher.replaceAll("&quot;");
        }
        if ((ampMatcher = amp.matcher(str)).matches()) {
            str = ampMatcher.replaceAll("&amp;");
        }
        return str;
    }
}


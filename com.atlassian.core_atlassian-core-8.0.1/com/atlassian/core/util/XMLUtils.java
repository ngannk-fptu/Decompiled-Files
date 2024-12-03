/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.core.util;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;

public class XMLUtils {
    private static final ReplacePolicy DEFAULT_POLICY = new ReplacePolicy();
    private static int _lastPrintable = 126;

    public static String getContainedText(Node parent, String childTagName) {
        try {
            Node tag = ((Element)parent).getElementsByTagName(childTagName).item(0);
            return ((Text)tag.getFirstChild()).getData();
        }
        catch (Exception e) {
            return null;
        }
    }

    public static Element getSingleChildElement(Element el, String name) {
        NodeList children = el.getChildNodes();
        for (int i = 0; i < children.getLength(); ++i) {
            Node node = children.item(i);
            if (node.getNodeType() != 1 || !name.equals(node.getNodeName())) continue;
            return (Element)node;
        }
        return null;
    }

    public static String getAttributeWithDefault(Element element, String attributeName, String defaultValue) {
        String group = element.getAttribute(attributeName);
        if (group == null || "".equals(group.trim())) {
            group = defaultValue;
        }
        return group;
    }

    public static String escape(String source, TransformPolicy policy) {
        if (source == null) {
            return "";
        }
        StringBuilder sb = new StringBuilder(source.length() + 30);
        source.codePoints().forEach(codePoint -> XMLUtils.transform(sb, codePoint, policy));
        return sb.toString();
    }

    public static String escape(String source) {
        return XMLUtils.escape(source, (TransformPolicy)DEFAULT_POLICY);
    }

    public static String escapeForCdata(String source) {
        int index;
        if (source == null) {
            return null;
        }
        StringBuilder sb = new StringBuilder();
        int oldIndex = 0;
        while ((index = source.indexOf("]]>", oldIndex)) > -1) {
            String str = source.substring(oldIndex, index);
            XMLUtils.transformCData(sb, str, DEFAULT_POLICY);
            oldIndex = index + 3;
            sb.append("]]]]><![CDATA[>");
        }
        String rest = source.substring(oldIndex);
        XMLUtils.transformCData(sb, rest, DEFAULT_POLICY);
        return sb.toString();
    }

    private static String getEntityRef(int codePoint) {
        switch (codePoint) {
            case 60: {
                return "lt";
            }
            case 62: {
                return "gt";
            }
            case 34: {
                return "quot";
            }
            case 39: {
                return "apos";
            }
            case 38: {
                return "amp";
            }
        }
        return null;
    }

    public static String escape(int codePoint) {
        return XMLUtils.escape(codePoint, (TransformPolicy)DEFAULT_POLICY);
    }

    public static String escape(int codePoint, TransformPolicy policy) {
        StringBuilder sb = new StringBuilder();
        XMLUtils.transform(sb, codePoint, policy);
        return sb.toString();
    }

    @Deprecated
    public static String escape(char ch) {
        return XMLUtils.escape(ch, (TransformPolicy)DEFAULT_POLICY);
    }

    @Deprecated
    public static String escape(char ch, TransformPolicy policy) {
        StringBuilder sb = new StringBuilder();
        XMLUtils.transform(sb, ch, policy);
        return sb.toString();
    }

    private static void transform(StringBuilder sb, int codePoint, TransformPolicy policy) {
        if (!XMLUtils.validXml(codePoint)) {
            sb.append(policy.handle((char)codePoint));
        } else {
            String charRef = XMLUtils.getEntityRef(codePoint);
            if (charRef != null) {
                sb.append("&").append(charRef).append(";");
            } else if (codePoint >= 32 && codePoint <= _lastPrintable && codePoint != 247 || codePoint == 10 || codePoint == 13 || codePoint == 9) {
                sb.append(Character.toChars(codePoint));
            } else {
                sb.append("&#").append(codePoint).append(";");
            }
        }
    }

    private static void transformCData(StringBuilder sb, String cdata, TransformPolicy policy) {
        cdata.codePoints().forEach(codePoint -> {
            if (!XMLUtils.validXml(codePoint)) {
                sb.append(policy.handle((char)codePoint));
            } else {
                sb.append(Character.toChars(codePoint));
            }
        });
    }

    public static boolean validXml(int codePoint) {
        return codePoint == 9 || codePoint == 10 || codePoint == 13 || codePoint >= 32 && codePoint <= 55295 || codePoint >= 57344 && codePoint <= 65533 || codePoint >= 65536 && codePoint <= 0x10FFFF;
    }

    @Deprecated
    public static boolean validXml(char ch) {
        return XMLUtils.validXml(ch);
    }

    public static class ReplacePolicy
    implements TransformPolicy {
        @Override
        public String handle(char ch) {
            return "\ufffd";
        }
    }

    public static interface TransformPolicy {
        public String handle(char var1);
    }
}


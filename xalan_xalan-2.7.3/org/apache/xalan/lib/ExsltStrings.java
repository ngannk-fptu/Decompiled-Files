/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xalan.lib;

import java.util.StringTokenizer;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.apache.xalan.lib.ExsltBase;
import org.apache.xml.utils.WrappedRuntimeException;
import org.apache.xpath.NodeSet;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;

public class ExsltStrings
extends ExsltBase {
    public static String align(String targetStr, String paddingStr, String type) {
        if (targetStr.length() >= paddingStr.length()) {
            return targetStr.substring(0, paddingStr.length());
        }
        if (type.equals("right")) {
            return paddingStr.substring(0, paddingStr.length() - targetStr.length()) + targetStr;
        }
        if (type.equals("center")) {
            int startIndex = (paddingStr.length() - targetStr.length()) / 2;
            return paddingStr.substring(0, startIndex) + targetStr + paddingStr.substring(startIndex + targetStr.length());
        }
        return targetStr + paddingStr.substring(targetStr.length());
    }

    public static String align(String targetStr, String paddingStr) {
        return ExsltStrings.align(targetStr, paddingStr, "left");
    }

    public static String concat(NodeList nl) {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < nl.getLength(); ++i) {
            Node node = nl.item(i);
            String value = ExsltStrings.toString(node);
            if (value == null || value.length() <= 0) continue;
            sb.append(value);
        }
        return sb.toString();
    }

    public static String padding(double length, String pattern) {
        if (pattern == null || pattern.length() == 0) {
            return "";
        }
        StringBuffer sb = new StringBuffer();
        int len = (int)length;
        int index = 0;
        for (int numAdded = 0; numAdded < len; ++numAdded) {
            if (index == pattern.length()) {
                index = 0;
            }
            sb.append(pattern.charAt(index));
            ++index;
        }
        return sb.toString();
    }

    public static String padding(double length) {
        return ExsltStrings.padding(length, " ");
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static NodeList split(String str, String pattern) {
        NodeSet resultSet = new NodeSet();
        resultSet.setShouldCacheNodes(true);
        boolean done = false;
        int fromIndex = 0;
        int matchIndex = 0;
        String token = null;
        while (!done && fromIndex < str.length()) {
            Document doc;
            matchIndex = str.indexOf(pattern, fromIndex);
            if (matchIndex >= 0) {
                token = str.substring(fromIndex, matchIndex);
                fromIndex = matchIndex + pattern.length();
            } else {
                done = true;
                token = str.substring(fromIndex);
            }
            Document document = doc = DocumentHolder.m_doc;
            synchronized (document) {
                Element element = doc.createElement("token");
                Text text = doc.createTextNode(token);
                element.appendChild(text);
                resultSet.addNode(element);
            }
        }
        return resultSet;
    }

    public static NodeList split(String str) {
        return ExsltStrings.split(str, " ");
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static NodeList tokenize(String toTokenize, String delims) {
        Document doc;
        NodeSet resultSet = new NodeSet();
        if (delims != null && delims.length() > 0) {
            Document doc2;
            StringTokenizer lTokenizer = new StringTokenizer(toTokenize, delims);
            Document document = doc2 = DocumentHolder.m_doc;
            synchronized (document) {
                while (lTokenizer.hasMoreTokens()) {
                    Element element = doc2.createElement("token");
                    element.appendChild(doc2.createTextNode(lTokenizer.nextToken()));
                    resultSet.addNode(element);
                }
            }
        }
        Document document = doc = DocumentHolder.m_doc;
        synchronized (document) {
            for (int i = 0; i < toTokenize.length(); ++i) {
                Element element = doc.createElement("token");
                element.appendChild(doc.createTextNode(toTokenize.substring(i, i + 1)));
                resultSet.addNode(element);
            }
        }
        return resultSet;
    }

    public static NodeList tokenize(String toTokenize) {
        return ExsltStrings.tokenize(toTokenize, " \t\n\r");
    }

    private static class DocumentHolder {
        private static final Document m_doc;

        private DocumentHolder() {
        }

        static {
            try {
                m_doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
            }
            catch (ParserConfigurationException pce) {
                throw new WrappedRuntimeException(pce);
            }
        }
    }
}


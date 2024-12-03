/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tools.ant.util;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.w3c.dom.Attr;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;

public class DOMElementWriter {
    private static final int HEX = 16;
    private static final String[] WS_ENTITIES = new String[5];
    private static final String NS = "ns";
    private boolean xmlDeclaration = true;
    private XmlNamespacePolicy namespacePolicy = XmlNamespacePolicy.IGNORE;
    private Map<String, String> nsPrefixMap = new HashMap<String, String>();
    private int nextPrefix = 0;
    private Map<Element, List<String>> nsURIByElement = new HashMap<Element, List<String>>();
    protected String[] knownEntities = new String[]{"gt", "amp", "lt", "apos", "quot"};

    public DOMElementWriter() {
    }

    public DOMElementWriter(boolean xmlDeclaration) {
        this(xmlDeclaration, XmlNamespacePolicy.IGNORE);
    }

    public DOMElementWriter(boolean xmlDeclaration, XmlNamespacePolicy namespacePolicy) {
        this.xmlDeclaration = xmlDeclaration;
        this.namespacePolicy = namespacePolicy;
    }

    public void write(Element root, OutputStream out) throws IOException {
        OutputStreamWriter wri = new OutputStreamWriter(out, StandardCharsets.UTF_8);
        this.writeXMLDeclaration(wri);
        this.write(root, wri, 0, "  ");
        ((Writer)wri).flush();
    }

    public void writeXMLDeclaration(Writer wri) throws IOException {
        if (this.xmlDeclaration) {
            wri.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
        }
    }

    public void write(Element element, Writer out, int indent, String indentWith) throws IOException {
        NodeList children = element.getChildNodes();
        boolean hasChildren = children.getLength() > 0;
        boolean hasChildElements = false;
        this.openElement(element, out, indent, indentWith, hasChildren);
        if (hasChildren) {
            block8: for (int i = 0; i < children.getLength(); ++i) {
                Node child = children.item(i);
                switch (child.getNodeType()) {
                    case 1: {
                        hasChildElements = true;
                        if (i == 0) {
                            out.write(System.lineSeparator());
                        }
                        this.write((Element)child, out, indent + 1, indentWith);
                        continue block8;
                    }
                    case 3: {
                        out.write(this.encode(child.getNodeValue()));
                        continue block8;
                    }
                    case 8: {
                        out.write("<!--");
                        out.write(this.encode(child.getNodeValue()));
                        out.write("-->");
                        continue block8;
                    }
                    case 4: {
                        out.write("<![CDATA[");
                        this.encodedata(out, ((Text)child).getData());
                        out.write("]]>");
                        continue block8;
                    }
                    case 5: {
                        out.write(38);
                        out.write(child.getNodeName());
                        out.write(59);
                        continue block8;
                    }
                    case 7: {
                        out.write("<?");
                        out.write(child.getNodeName());
                        String data = child.getNodeValue();
                        if (data != null && !data.isEmpty()) {
                            out.write(32);
                            out.write(data);
                        }
                        out.write("?>");
                        continue block8;
                    }
                }
            }
            this.closeElement(element, out, indent, indentWith, hasChildElements);
        }
    }

    public void openElement(Element element, Writer out, int indent, String indentWith) throws IOException {
        this.openElement(element, out, indent, indentWith, true);
    }

    public void openElement(Element element, Writer out, int indent, String indentWith, boolean hasChildren) throws IOException {
        String prefix;
        for (int i = 0; i < indent; ++i) {
            out.write(indentWith);
        }
        out.write("<");
        if (this.namespacePolicy.qualifyElements) {
            String uri = DOMElementWriter.getNamespaceURI(element);
            String prefix2 = this.nsPrefixMap.get(uri);
            if (prefix2 == null) {
                prefix2 = this.nsPrefixMap.isEmpty() ? "" : NS + this.nextPrefix++;
                this.nsPrefixMap.put(uri, prefix2);
                this.addNSDefinition(element, uri);
            }
            if (!prefix2.isEmpty()) {
                out.write(prefix2);
                out.write(":");
            }
        }
        out.write(element.getTagName());
        NamedNodeMap attrs = element.getAttributes();
        for (int i = 0; i < attrs.getLength(); ++i) {
            Attr attr = (Attr)attrs.item(i);
            out.write(" ");
            if (this.namespacePolicy.qualifyAttributes) {
                String uri = DOMElementWriter.getNamespaceURI(attr);
                prefix = this.nsPrefixMap.get(uri);
                if (prefix == null) {
                    prefix = NS + this.nextPrefix++;
                    this.nsPrefixMap.put(uri, prefix);
                    this.addNSDefinition(element, uri);
                }
                out.write(prefix);
                out.write(":");
            }
            out.write(attr.getName());
            out.write("=\"");
            out.write(this.encodeAttributeValue(attr.getValue()));
            out.write("\"");
        }
        List<String> uris = this.nsURIByElement.get(element);
        if (uris != null) {
            for (String uri : uris) {
                prefix = this.nsPrefixMap.get(uri);
                out.write(" xmlns");
                if (!prefix.isEmpty()) {
                    out.write(":");
                    out.write(prefix);
                }
                out.write("=\"");
                out.write(uri);
                out.write("\"");
            }
        }
        if (hasChildren) {
            out.write(">");
        } else {
            this.removeNSDefinitions(element);
            out.write(String.format(" />%n", new Object[0]));
            out.flush();
        }
    }

    public void closeElement(Element element, Writer out, int indent, String indentWith, boolean hasChildren) throws IOException {
        if (hasChildren) {
            for (int i = 0; i < indent; ++i) {
                out.write(indentWith);
            }
        }
        out.write("</");
        if (this.namespacePolicy.qualifyElements) {
            String uri = DOMElementWriter.getNamespaceURI(element);
            String prefix = this.nsPrefixMap.get(uri);
            if (prefix != null && !prefix.isEmpty()) {
                out.write(prefix);
                out.write(":");
            }
            this.removeNSDefinitions(element);
        }
        out.write(element.getTagName());
        out.write(String.format(">%n", new Object[0]));
        out.flush();
    }

    public String encode(String value) {
        return this.encode(value, false);
    }

    public String encodeAttributeValue(String value) {
        return this.encode(value, true);
    }

    private String encode(String value, boolean encodeWhitespace) {
        StringBuilder sb = new StringBuilder(value.length());
        block8: for (char c : value.toCharArray()) {
            switch (c) {
                case '<': {
                    sb.append("&lt;");
                    continue block8;
                }
                case '>': {
                    sb.append("&gt;");
                    continue block8;
                }
                case '\'': {
                    sb.append("&apos;");
                    continue block8;
                }
                case '\"': {
                    sb.append("&quot;");
                    continue block8;
                }
                case '&': {
                    sb.append("&amp;");
                    continue block8;
                }
                case '\t': 
                case '\n': 
                case '\r': {
                    if (encodeWhitespace) {
                        sb.append(WS_ENTITIES[c - 9]);
                        continue block8;
                    }
                    sb.append(c);
                    continue block8;
                }
                default: {
                    if (!this.isLegalCharacter(c)) continue block8;
                    sb.append(c);
                }
            }
        }
        return sb.substring(0);
    }

    public String encodedata(String value) {
        StringWriter out = new StringWriter();
        try {
            this.encodedata(out, value);
        }
        catch (IOException ex) {
            throw new RuntimeException(ex);
        }
        return out.toString();
    }

    public void encodedata(Writer out, String value) throws IOException {
        int len = value.length();
        int prevEnd = 0;
        int cdataEndPos = value.indexOf("]]>");
        while (prevEnd < len) {
            int end = cdataEndPos < 0 ? len : cdataEndPos;
            int prevLegalCharPos = prevEnd;
            while (prevLegalCharPos < end) {
                int illegalCharPos;
                for (illegalCharPos = prevLegalCharPos; illegalCharPos < end && this.isLegalCharacter(value.charAt(illegalCharPos)); ++illegalCharPos) {
                }
                out.write(value, prevLegalCharPos, illegalCharPos - prevLegalCharPos);
                prevLegalCharPos = illegalCharPos + 1;
            }
            if (cdataEndPos >= 0) {
                out.write("]]]]><![CDATA[>");
                prevEnd = cdataEndPos + 3;
                cdataEndPos = value.indexOf("]]>", prevEnd);
                continue;
            }
            prevEnd = end;
        }
    }

    public boolean isReference(String ent) {
        if (ent.charAt(0) != '&' || !ent.endsWith(";")) {
            return false;
        }
        if (ent.charAt(1) == '#') {
            if (ent.charAt(2) == 'x') {
                try {
                    Integer.parseInt(ent.substring(3, ent.length() - 1), 16);
                    return true;
                }
                catch (NumberFormatException nfe) {
                    return false;
                }
            }
            try {
                Integer.parseInt(ent.substring(2, ent.length() - 1));
                return true;
            }
            catch (NumberFormatException nfe) {
                return false;
            }
        }
        String name = ent.substring(1, ent.length() - 1);
        for (String knownEntity : this.knownEntities) {
            if (!name.equals(knownEntity)) continue;
            return true;
        }
        return false;
    }

    public boolean isLegalCharacter(char c) {
        return DOMElementWriter.isLegalXmlCharacter(c);
    }

    public static boolean isLegalXmlCharacter(char c) {
        if (c == '\t' || c == '\n' || c == '\r') {
            return true;
        }
        if (c < ' ') {
            return false;
        }
        if (c <= '\ud7ff') {
            return true;
        }
        if (c < '\ue000') {
            return false;
        }
        return c <= '\ufffd';
    }

    private void removeNSDefinitions(Element element) {
        List<String> uris = this.nsURIByElement.get(element);
        if (uris != null) {
            uris.forEach(this.nsPrefixMap::remove);
            this.nsURIByElement.remove(element);
        }
    }

    private void addNSDefinition(Element element, String uri) {
        this.nsURIByElement.computeIfAbsent(element, e -> new ArrayList()).add(uri);
    }

    private static String getNamespaceURI(Node n) {
        String uri = n.getNamespaceURI();
        return uri == null ? "" : uri;
    }

    static {
        for (int i = 9; i < 14; ++i) {
            DOMElementWriter.WS_ENTITIES[i - 9] = "&#x" + Integer.toHexString(i) + ";";
        }
    }

    public static class XmlNamespacePolicy {
        private boolean qualifyElements;
        private boolean qualifyAttributes;
        public static final XmlNamespacePolicy IGNORE = new XmlNamespacePolicy(false, false);
        public static final XmlNamespacePolicy ONLY_QUALIFY_ELEMENTS = new XmlNamespacePolicy(true, false);
        public static final XmlNamespacePolicy QUALIFY_ALL = new XmlNamespacePolicy(true, true);

        public XmlNamespacePolicy(boolean qualifyElements, boolean qualifyAttributes) {
            this.qualifyElements = qualifyElements;
            this.qualifyAttributes = qualifyAttributes;
        }
    }
}


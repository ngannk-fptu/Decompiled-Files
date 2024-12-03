/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.twelvemonkeys.lang.StringUtil
 */
package com.twelvemonkeys.xml;

import com.twelvemonkeys.lang.StringUtil;
import com.twelvemonkeys.xml.DOMSerializer;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.util.Date;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Attr;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.w3c.dom.DocumentType;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.ProcessingInstruction;
import org.xml.sax.SAXException;

public class XMLSerializer {
    private final OutputStream output;
    private final Charset encoding;
    private final SerializationContext context;

    public XMLSerializer(OutputStream outputStream, String string) {
        this.output = outputStream;
        this.encoding = Charset.forName(string);
        this.context = new SerializationContext();
    }

    public final XMLSerializer indentation(String string) {
        this.context.indent = string != null ? string : "\t";
        return this;
    }

    public final XMLSerializer stripComments(boolean bl) {
        this.context.stripComments = bl;
        return this;
    }

    public void serialize(Document document) {
        this.serialize(document, true);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void serialize(Node node, boolean bl) {
        PrintWriter printWriter = new PrintWriter(new OutputStreamWriter(this.output, this.encoding));
        try {
            if (bl) {
                this.writeXMLDeclaration(printWriter);
            }
            this.writeXML(printWriter, node, this.context.copy());
        }
        finally {
            printWriter.flush();
        }
    }

    private void writeXMLDeclaration(PrintWriter printWriter) {
        printWriter.print("<?xml version=\"1.0\" encoding=\"");
        printWriter.print(this.encoding.name());
        printWriter.println("\"?>");
    }

    private void writeXML(PrintWriter printWriter, Node node, SerializationContext serializationContext) {
        this.writeNodeRecursive(printWriter, node, serializationContext);
    }

    private void writeNodeRecursive(PrintWriter printWriter, Node node, SerializationContext serializationContext) {
        if (node.getNodeType() != 3) {
            XMLSerializer.indentToLevel(printWriter, serializationContext);
        }
        switch (node.getNodeType()) {
            case 9: 
            case 11: {
                this.writeDocument(printWriter, node, serializationContext);
                break;
            }
            case 10: {
                this.writeDoctype(printWriter, (DocumentType)node);
                break;
            }
            case 1: {
                boolean bl = serializationContext.preserveSpace;
                XMLSerializer.updatePreserveSpace(node, serializationContext);
                this.writeElement(printWriter, (Element)node, serializationContext);
                serializationContext.preserveSpace = bl;
                break;
            }
            case 4: {
                this.writeCData(printWriter, node);
                break;
            }
            case 3: {
                this.writeText(printWriter, node, serializationContext);
                break;
            }
            case 8: {
                this.writeComment(printWriter, node, serializationContext);
                break;
            }
            case 7: {
                this.writeProcessingInstruction(printWriter, (ProcessingInstruction)node);
                break;
            }
            case 2: {
                throw new IllegalArgumentException("Malformed input Document: Attribute nodes should only occur inside Element nodes");
            }
            default: {
                throw new InternalError("Lazy programmer never implemented serialization of " + node.getClass());
            }
        }
    }

    private void writeProcessingInstruction(PrintWriter printWriter, ProcessingInstruction processingInstruction) {
        printWriter.print("\n<?");
        printWriter.print(processingInstruction.getTarget());
        String string = processingInstruction.getData();
        if (string != null) {
            printWriter.print(" ");
            printWriter.print(string);
        }
        printWriter.println("?>");
    }

    private void writeText(PrintWriter printWriter, Node node, SerializationContext serializationContext) {
        String string = node.getNodeValue();
        if (serializationContext.preserveSpace) {
            printWriter.print(XMLSerializer.maybeEscapeElementValue(string));
        } else if (!StringUtil.isEmpty((String)string)) {
            String string2 = XMLSerializer.maybeEscapeElementValue(string.trim());
            XMLSerializer.indentToLevel(printWriter, serializationContext);
            printWriter.println(string2);
        }
    }

    private void writeCData(PrintWriter printWriter, Node node) {
        printWriter.print("<![CDATA[");
        printWriter.print(XMLSerializer.validateCDataValue(node.getNodeValue()));
        printWriter.println("]]>");
    }

    private static void updatePreserveSpace(Node node, SerializationContext serializationContext) {
        Node node2;
        NamedNodeMap namedNodeMap = node.getAttributes();
        if (namedNodeMap != null && (node2 = namedNodeMap.getNamedItem("xml:space")) != null) {
            if ("preserve".equals(node2.getNodeValue())) {
                serializationContext.preserveSpace = true;
            } else if ("default".equals(node2.getNodeValue())) {
                serializationContext.preserveSpace = false;
            }
        }
    }

    private static void indentToLevel(PrintWriter printWriter, SerializationContext serializationContext) {
        for (int i = 0; i < serializationContext.level; ++i) {
            printWriter.print(serializationContext.indent);
        }
    }

    private void writeComment(PrintWriter printWriter, Node node, SerializationContext serializationContext) {
        if (serializationContext.stripComments) {
            return;
        }
        String string = node.getNodeValue();
        XMLSerializer.validateCommentValue(string);
        if (string.startsWith(" ")) {
            printWriter.print("<!--");
        } else {
            printWriter.print("<!-- ");
        }
        printWriter.print(string);
        if (string.endsWith(" ")) {
            printWriter.println("-->");
        } else {
            printWriter.println(" -->");
        }
    }

    static String maybeEscapeElementValue(String string) {
        int n;
        int n2 = XMLSerializer.needsEscapeElement(string);
        if (n2 < 0) {
            return string;
        }
        StringBuilder stringBuilder = new StringBuilder(string.substring(0, n2));
        stringBuilder.ensureCapacity(string.length() + 30);
        block5: for (int i = n = n2; i < string.length(); ++i) {
            switch (string.charAt(i)) {
                case '&': {
                    n = XMLSerializer.appendAndEscape(string, n, i, stringBuilder, "&amp;");
                    continue block5;
                }
                case '<': {
                    n = XMLSerializer.appendAndEscape(string, n, i, stringBuilder, "&lt;");
                    continue block5;
                }
                case '>': {
                    n = XMLSerializer.appendAndEscape(string, n, i, stringBuilder, "&gt;");
                    continue block5;
                }
            }
        }
        stringBuilder.append(string.substring(n));
        return stringBuilder.toString();
    }

    private static int appendAndEscape(String string, int n, int n2, StringBuilder stringBuilder, String string2) {
        stringBuilder.append(string, n, n2);
        stringBuilder.append(string2);
        return n2 + 1;
    }

    private static int needsEscapeElement(String string) {
        for (int i = 0; i < string.length(); ++i) {
            switch (string.charAt(i)) {
                case '&': 
                case '<': 
                case '>': {
                    return i;
                }
            }
        }
        return -1;
    }

    private static String maybeEscapeAttributeValue(String string) {
        int n;
        int n2 = XMLSerializer.needsEscapeAttribute(string);
        if (n2 < 0) {
            return string;
        }
        StringBuilder stringBuilder = new StringBuilder(string.substring(0, n2));
        stringBuilder.ensureCapacity(string.length() + 16);
        block4: for (int i = n = n2; i < string.length(); ++i) {
            switch (string.charAt(i)) {
                case '&': {
                    n = XMLSerializer.appendAndEscape(string, n, i, stringBuilder, "&amp;");
                    continue block4;
                }
                case '\"': {
                    n = XMLSerializer.appendAndEscape(string, n, i, stringBuilder, "&quot;");
                    continue block4;
                }
            }
        }
        stringBuilder.append(string.substring(n));
        return stringBuilder.toString();
    }

    private static int needsEscapeAttribute(String string) {
        for (int i = 0; i < string.length(); ++i) {
            switch (string.charAt(i)) {
                case '\"': 
                case '&': {
                    return i;
                }
            }
        }
        return -1;
    }

    private static String validateCDataValue(String string) {
        if (string.contains("]]>")) {
            throw new IllegalArgumentException("Malformed input document: CDATA block may not contain the string ']]>'");
        }
        return string;
    }

    private static String validateCommentValue(String string) {
        if (string.contains("--")) {
            throw new IllegalArgumentException("Malformed input document: Comment may not contain the string '--'");
        }
        return string;
    }

    private void writeDocument(PrintWriter printWriter, Node node, SerializationContext serializationContext) {
        if (node.hasChildNodes()) {
            for (Node node2 = node.getFirstChild(); node2 != null; node2 = node2.getNextSibling()) {
                this.writeNodeRecursive(printWriter, node2, serializationContext);
            }
        }
    }

    private void writeElement(PrintWriter printWriter, Element element, SerializationContext serializationContext) {
        Object object;
        printWriter.print("<");
        printWriter.print(element.getTagName());
        String string = element.getNamespaceURI();
        if (string != null && !string.equals(serializationContext.defaultNamespace)) {
            object = element.getPrefix();
            if (object == null) {
                serializationContext.defaultNamespace = string;
                printWriter.print(" xmlns");
            } else {
                printWriter.print(" xmlns:");
                printWriter.print((String)object);
            }
            printWriter.print("=\"");
            printWriter.print(string);
            printWriter.print("\"");
        }
        if (element.hasAttributes()) {
            object = element.getAttributes();
            for (int i = 0; i < object.getLength(); ++i) {
                Attr attr = (Attr)object.item(i);
                String string2 = attr.getName();
                if (string2.startsWith("xmlns") && (string2.length() == 5 || string2.charAt(5) == ':')) continue;
                printWriter.print(" ");
                printWriter.print(string2);
                printWriter.print("=\"");
                printWriter.print(XMLSerializer.maybeEscapeAttributeValue(attr.getValue()));
                printWriter.print("\"");
            }
        }
        if (element.hasChildNodes()) {
            printWriter.print(">");
            if (!serializationContext.preserveSpace) {
                printWriter.println();
            }
            for (object = element.getFirstChild(); object != null; object = object.getNextSibling()) {
                this.writeNodeRecursive(printWriter, (Node)object, serializationContext.push());
            }
            if (!serializationContext.preserveSpace) {
                XMLSerializer.indentToLevel(printWriter, serializationContext);
            }
            printWriter.print("</");
            printWriter.print(element.getTagName());
            printWriter.println(">");
        } else if (element.getNodeValue() != null) {
            printWriter.print(">");
            printWriter.print(element.getNodeValue());
            printWriter.print("</");
            printWriter.print(element.getTagName());
            printWriter.println(">");
        } else {
            printWriter.println("/>");
        }
    }

    private void writeDoctype(PrintWriter printWriter, DocumentType documentType) {
        if (documentType != null) {
            String string;
            String string2;
            printWriter.print("<!DOCTYPE ");
            printWriter.print(documentType.getName());
            String string3 = documentType.getPublicId();
            if (!StringUtil.isEmpty((String)string3)) {
                printWriter.print(" PUBLIC ");
                printWriter.print(string3);
            }
            if (!StringUtil.isEmpty((String)(string2 = documentType.getSystemId()))) {
                if (StringUtil.isEmpty((String)string3)) {
                    printWriter.print(" SYSTEM \"");
                } else {
                    printWriter.print(" \"");
                }
                printWriter.print(string2);
                printWriter.print("\"");
            }
            if (!StringUtil.isEmpty((String)(string = documentType.getInternalSubset()))) {
                printWriter.print(" [ ");
                printWriter.print(string);
                printWriter.print(" ]");
            }
            printWriter.println(">");
        }
    }

    public static void main(String[] stringArray) throws IOException, SAXException {
        DocumentBuilder documentBuilder;
        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        documentBuilderFactory.setNamespaceAware(true);
        try {
            documentBuilder = documentBuilderFactory.newDocumentBuilder();
        }
        catch (ParserConfigurationException parserConfigurationException) {
            throw new IOException(parserConfigurationException);
        }
        DOMImplementation dOMImplementation = documentBuilder.getDOMImplementation();
        Document document = dOMImplementation.createDocument("http://www.twelvemonkeys.com/xml/test", "test", dOMImplementation.createDocumentType("test", null, null));
        Element element = document.getDocumentElement();
        document.insertBefore(document.createComment(new Date().toString()), element);
        Element element2 = document.createElement("sub");
        element.appendChild(element2);
        Element element3 = document.createElementNS("http://more.com/1999/namespace", "more:more");
        element3.setAttribute("foo", "test");
        element3.setAttribute("bar", "'really' \"legal\" & ok");
        element2.appendChild(element3);
        element3.appendChild(document.createTextNode("Simply some text."));
        element3.appendChild(document.createCDATASection("&something escaped;"));
        element3.appendChild(document.createTextNode("More & <more>!"));
        element3.appendChild(document.createTextNode("\"<<'&'>>\""));
        Element element4 = document.createElement("another");
        element2.appendChild(element4);
        Element element5 = document.createElement("yet-another");
        element5.setAttribute("this-one", "with-params");
        element2.appendChild(element5);
        Element element6 = document.createElementNS("http://www.twelvemonkeys.com/xml/test", "pre");
        element6.setAttributeNS("http://www.w3.org/XML/1998/namespace", "xml:space", "preserve");
        element6.appendChild(document.createTextNode(" \t \n\r some text & white ' '   \n   "));
        element2.appendChild(element6);
        Element element7 = document.createElementNS("http://www.twelvemonkeys.com/xml/test", "tight");
        element7.setAttributeNS("http://www.w3.org/XML/1998/namespace", "xml:space", "preserve");
        element7.appendChild(document.createTextNode("no-space-around-me"));
        element2.appendChild(element7);
        System.out.println("XMLSerializer:");
        XMLSerializer xMLSerializer = new XMLSerializer(System.out, "UTF-8");
        xMLSerializer.serialize(document);
        System.out.println();
        System.out.println("DOMSerializer:");
        DOMSerializer dOMSerializer = new DOMSerializer(System.out, "UTF-8");
        dOMSerializer.setPrettyPrint(true);
        dOMSerializer.serialize(document);
        System.out.println();
        System.out.println("\n");
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        XMLSerializer xMLSerializer2 = new XMLSerializer(byteArrayOutputStream, "UTF-8");
        xMLSerializer2.serialize(document);
        ByteArrayOutputStream byteArrayOutputStream2 = new ByteArrayOutputStream();
        DOMSerializer dOMSerializer2 = new DOMSerializer(byteArrayOutputStream2, "UTF-8");
        dOMSerializer2.serialize(document);
        Document document2 = documentBuilder.parse(new ByteArrayInputStream(byteArrayOutputStream.toByteArray()));
        System.out.println("XMLSerializer reparsed XMLSerializer:");
        xMLSerializer.serialize(document2);
        System.out.println();
        System.out.println("DOMSerializer reparsed XMLSerializer:");
        dOMSerializer.serialize(document2);
        System.out.println();
        Document document3 = documentBuilder.parse(new ByteArrayInputStream(byteArrayOutputStream2.toByteArray()));
        System.out.println("XMLSerializer reparsed DOMSerializer:");
        xMLSerializer.serialize(document3);
        System.out.println();
        System.out.println("DOMSerializer reparsed DOMSerializer:");
        dOMSerializer.serialize(document3);
        System.out.println();
    }

    static class SerializationContext
    implements Cloneable {
        String indent = "\t";
        int level = 0;
        boolean preserveSpace = false;
        boolean stripComments = false;
        String defaultNamespace;

        SerializationContext() {
        }

        public SerializationContext copy() {
            try {
                return (SerializationContext)this.clone();
            }
            catch (CloneNotSupportedException cloneNotSupportedException) {
                throw new Error(cloneNotSupportedException);
            }
        }

        public SerializationContext push() {
            SerializationContext serializationContext = this.copy();
            ++serializationContext.level;
            return serializationContext;
        }
    }
}


/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.groovy.tools.xml;

import groovy.util.IndentPrinter;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.Reader;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.codehaus.groovy.syntax.Types;
import org.w3c.dom.Attr;
import org.w3c.dom.Comment;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.ProcessingInstruction;
import org.w3c.dom.Text;
import org.xml.sax.InputSource;

public class DomToGroovy {
    protected IndentPrinter out;
    protected boolean inMixed = false;
    protected String qt = "'";
    protected Collection<String> keywords = Types.getKeywords();

    public DomToGroovy(PrintWriter out) {
        this(new IndentPrinter(out));
    }

    public DomToGroovy(IndentPrinter out) {
        this.out = out;
    }

    public void print(Document document) {
        this.printChildren(document, new HashMap());
    }

    public static void main(String[] args) {
        if (args.length < 1) {
            System.out.println("Usage: DomToGroovy infilename [outfilename]");
            System.exit(1);
        }
        Document document = null;
        try {
            document = DomToGroovy.parse(args[0]);
        }
        catch (Exception e) {
            System.out.println("Unable to parse input file '" + args[0] + "': " + e.getMessage());
            System.exit(1);
        }
        PrintWriter writer = null;
        if (args.length < 2) {
            writer = new PrintWriter(System.out);
        } else {
            try {
                writer = new PrintWriter(new FileWriter(new File(args[1])));
            }
            catch (IOException e) {
                System.out.println("Unable to create output file '" + args[1] + "': " + e.getMessage());
                System.exit(1);
            }
        }
        DomToGroovy converter = new DomToGroovy(writer);
        converter.out.incrementIndent();
        writer.println("#!/bin/groovy");
        writer.println();
        writer.println("// generated from " + args[0]);
        writer.println("System.out << new groovy.xml.StreamingMarkupBuilder().bind {");
        converter.print(document);
        writer.println("}");
        writer.close();
    }

    protected static Document parse(String fileName) throws Exception {
        return DomToGroovy.parse(new File(fileName));
    }

    public static Document parse(File file) throws Exception {
        return DomToGroovy.parse(new BufferedReader(new FileReader(file)));
    }

    public static Document parse(Reader input) throws Exception {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(true);
        DocumentBuilder builder = factory.newDocumentBuilder();
        return builder.parse(new InputSource(input));
    }

    public static Document parse(InputStream input) throws Exception {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(true);
        DocumentBuilder builder = factory.newDocumentBuilder();
        return builder.parse(new InputSource(input));
    }

    protected void print(Node node, Map namespaces, boolean endWithComma) {
        switch (node.getNodeType()) {
            case 1: {
                this.printElement((Element)node, namespaces, endWithComma);
                break;
            }
            case 7: {
                this.printPI((ProcessingInstruction)node, endWithComma);
                break;
            }
            case 3: {
                this.printText((Text)node, endWithComma);
                break;
            }
            case 8: {
                this.printComment((Comment)node, endWithComma);
            }
        }
    }

    protected void printElement(Element element, Map namespaces, boolean endWithComma) {
        namespaces = this.defineNamespaces(element, namespaces);
        element.normalize();
        this.printIndent();
        String prefix = element.getPrefix();
        boolean hasPrefix = prefix != null && prefix.length() > 0;
        String localName = this.getLocalName(element);
        boolean isKeyword = this.checkEscaping(localName);
        if (isKeyword || hasPrefix) {
            this.print(this.qt);
        }
        if (hasPrefix) {
            this.print(prefix);
            this.print(".");
        }
        this.print(localName);
        if (isKeyword || hasPrefix) {
            this.print(this.qt);
        }
        this.print("(");
        boolean hasAttributes = this.printAttributes(element);
        NodeList list = element.getChildNodes();
        int length = list.getLength();
        if (length == 0) {
            this.printEnd(")", endWithComma);
        } else {
            Node node = list.item(0);
            if (length == 1 && node instanceof Text) {
                Text textNode = (Text)node;
                String text = this.getTextNodeData(textNode);
                if (hasAttributes) {
                    this.print(", ");
                }
                this.printQuoted(text);
                this.printEnd(")", endWithComma);
            } else if (this.mixedContent(list)) {
                this.println(") {");
                this.out.incrementIndent();
                boolean oldInMixed = this.inMixed;
                this.inMixed = true;
                for (node = element.getFirstChild(); node != null; node = node.getNextSibling()) {
                    this.print(node, namespaces, false);
                }
                this.inMixed = oldInMixed;
                this.out.decrementIndent();
                this.printIndent();
                this.printEnd("}", endWithComma);
            } else {
                this.println(") {");
                this.out.incrementIndent();
                this.printChildren(element, namespaces);
                this.out.decrementIndent();
                this.printIndent();
                this.printEnd("}", endWithComma);
            }
        }
    }

    protected void printQuoted(String text) {
        if (text.indexOf("\n") != -1) {
            this.print("'''");
            this.print(text);
            this.print("'''");
        } else {
            this.print(this.qt);
            this.print(this.escapeQuote(text));
            this.print(this.qt);
        }
    }

    protected void printPI(ProcessingInstruction instruction, boolean endWithComma) {
        this.printIndent();
        this.print("mkp.pi(" + this.qt);
        this.print(instruction.getTarget());
        this.print(this.qt + ", " + this.qt);
        this.print(instruction.getData());
        this.printEnd(this.qt + ");", endWithComma);
    }

    protected void printComment(Comment comment, boolean endWithComma) {
        String text = comment.getData().trim();
        if (text.length() > 0) {
            this.printIndent();
            this.print("/* ");
            this.print(text);
            this.printEnd(" */", endWithComma);
        }
    }

    protected void printText(Text node, boolean endWithComma) {
        String text = this.getTextNodeData(node);
        if (text.length() > 0) {
            this.printIndent();
            if (this.inMixed) {
                this.print("mkp.yield ");
            }
            this.printQuoted(text);
            this.printEnd("", endWithComma);
        }
    }

    protected String escapeQuote(String text) {
        return text.replaceAll("\\\\", "\\\\\\\\").replaceAll(this.qt, "\\\\" + this.qt);
    }

    protected Map defineNamespaces(Element element, Map namespaces) {
        HashMap answer = null;
        String prefix = element.getPrefix();
        if (prefix != null && prefix.length() > 0 && !namespaces.containsKey(prefix)) {
            answer = new HashMap(namespaces);
            this.defineNamespace(answer, prefix, element.getNamespaceURI());
        }
        NamedNodeMap attributes = element.getAttributes();
        int length = attributes.getLength();
        for (int i = 0; i < length; ++i) {
            Attr attribute = (Attr)attributes.item(i);
            prefix = attribute.getPrefix();
            if (prefix == null || prefix.length() <= 0 || namespaces.containsKey(prefix)) continue;
            if (answer == null) {
                answer = new HashMap(namespaces);
            }
            this.defineNamespace(answer, prefix, attribute.getNamespaceURI());
        }
        return answer != null ? answer : namespaces;
    }

    protected void defineNamespace(Map namespaces, String prefix, String uri) {
        namespaces.put(prefix, uri);
        if (!prefix.equals("xmlns") && !prefix.equals("xml")) {
            this.printIndent();
            this.print("mkp.declareNamespace(");
            this.print(prefix);
            this.print(":" + this.qt);
            this.print(uri);
            this.println(this.qt + ")");
        }
    }

    protected boolean printAttributes(Element element) {
        boolean hasAttribute = false;
        NamedNodeMap attributes = element.getAttributes();
        int length = attributes.getLength();
        if (length > 0) {
            int i;
            StringBuffer buffer = new StringBuffer();
            for (i = 0; i < length; ++i) {
                this.printAttributeWithPrefix((Attr)attributes.item(i), buffer);
            }
            for (i = 0; i < length; ++i) {
                hasAttribute = this.printAttributeWithoutPrefix((Attr)attributes.item(i), hasAttribute);
            }
            if (buffer.length() > 0) {
                if (hasAttribute) {
                    this.print(", ");
                }
                this.print(buffer.toString());
                hasAttribute = true;
            }
        }
        return hasAttribute;
    }

    protected void printAttributeWithPrefix(Attr attribute, StringBuffer buffer) {
        String prefix = attribute.getPrefix();
        if (prefix != null && prefix.length() > 0 && !prefix.equals("xmlns")) {
            if (buffer.length() > 0) {
                buffer.append(", ");
            }
            buffer.append(this.qt);
            buffer.append(prefix);
            buffer.append(":");
            buffer.append(this.getLocalName(attribute));
            buffer.append(this.qt + ":" + this.qt);
            buffer.append(this.escapeQuote(this.getAttributeValue(attribute)));
            buffer.append(this.qt);
        }
    }

    protected String getAttributeValue(Attr attribute) {
        return attribute.getValue();
    }

    protected boolean printAttributeWithoutPrefix(Attr attribute, boolean hasAttribute) {
        String prefix = attribute.getPrefix();
        if (prefix == null || prefix.length() == 0) {
            if (!hasAttribute) {
                hasAttribute = true;
            } else {
                this.print(", ");
            }
            String localName = this.getLocalName(attribute);
            boolean needsEscaping = this.checkEscaping(localName);
            if (needsEscaping) {
                this.print(this.qt);
            }
            this.print(localName);
            if (needsEscaping) {
                this.print(this.qt);
            }
            this.print(":");
            this.printQuoted(this.getAttributeValue(attribute));
        }
        return hasAttribute;
    }

    protected boolean checkEscaping(String localName) {
        return this.keywords.contains(localName) || localName.contains("-") || localName.contains(":") || localName.contains(".");
    }

    protected String getTextNodeData(Text node) {
        return node.getData().trim();
    }

    protected boolean mixedContent(NodeList list) {
        boolean hasText = false;
        boolean hasElement = false;
        int size = list.getLength();
        for (int i = 0; i < size; ++i) {
            String text;
            Node node = list.item(i);
            if (node instanceof Element) {
                hasElement = true;
            } else if (node instanceof Text && (text = this.getTextNodeData((Text)node)).length() > 0) {
                hasText = true;
            }
            if (hasText && hasElement) break;
        }
        return hasText && hasElement;
    }

    protected void printChildren(Node parent, Map namespaces) {
        for (Node node = parent.getFirstChild(); node != null; node = node.getNextSibling()) {
            this.print(node, namespaces, false);
        }
    }

    protected String getLocalName(Node node) {
        String answer = node.getLocalName();
        if (answer == null) {
            answer = node.getNodeName();
        }
        return answer.trim();
    }

    protected void printEnd(String text, boolean endWithComma) {
        if (endWithComma) {
            this.print(text);
            this.println(",");
        } else {
            this.println(text);
        }
    }

    protected void println(String text) {
        this.out.println(text);
    }

    protected void print(String text) {
        this.out.print(text);
    }

    protected void printIndent() {
        this.out.printIndent();
    }
}


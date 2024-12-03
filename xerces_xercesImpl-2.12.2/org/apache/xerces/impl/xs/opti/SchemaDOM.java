/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xerces.impl.xs.opti;

import java.util.ArrayList;
import java.util.Enumeration;
import org.apache.xerces.impl.xs.opti.AttrImpl;
import org.apache.xerces.impl.xs.opti.DefaultDocument;
import org.apache.xerces.impl.xs.opti.ElementImpl;
import org.apache.xerces.impl.xs.opti.NodeImpl;
import org.apache.xerces.impl.xs.opti.SchemaDOMImplementation;
import org.apache.xerces.util.XMLSymbols;
import org.apache.xerces.xni.NamespaceContext;
import org.apache.xerces.xni.QName;
import org.apache.xerces.xni.XMLAttributes;
import org.apache.xerces.xni.XMLString;
import org.w3c.dom.Attr;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public class SchemaDOM
extends DefaultDocument {
    static final int relationsRowResizeFactor = 15;
    static final int relationsColResizeFactor = 10;
    NodeImpl[][] relations;
    ElementImpl parent;
    int currLoc;
    int nextFreeLoc;
    boolean hidden;
    boolean inCDATA;
    private StringBuffer fAnnotationBuffer = null;

    public SchemaDOM() {
        this.reset();
    }

    public ElementImpl startElement(QName qName, XMLAttributes xMLAttributes, int n, int n2, int n3) {
        ElementImpl elementImpl = new ElementImpl(n, n2, n3);
        this.processElement(qName, xMLAttributes, elementImpl);
        this.parent = elementImpl;
        return elementImpl;
    }

    public ElementImpl emptyElement(QName qName, XMLAttributes xMLAttributes, int n, int n2, int n3) {
        ElementImpl elementImpl = new ElementImpl(n, n2, n3);
        this.processElement(qName, xMLAttributes, elementImpl);
        return elementImpl;
    }

    public ElementImpl startElement(QName qName, XMLAttributes xMLAttributes, int n, int n2) {
        return this.startElement(qName, xMLAttributes, n, n2, -1);
    }

    public ElementImpl emptyElement(QName qName, XMLAttributes xMLAttributes, int n, int n2) {
        return this.emptyElement(qName, xMLAttributes, n, n2, -1);
    }

    private void processElement(QName qName, XMLAttributes xMLAttributes, ElementImpl elementImpl) {
        int n;
        elementImpl.prefix = qName.prefix;
        elementImpl.localpart = qName.localpart;
        elementImpl.rawname = qName.rawname;
        elementImpl.uri = qName.uri;
        elementImpl.schemaDOM = this;
        Attr[] attrArray = new Attr[xMLAttributes.getLength()];
        for (n = 0; n < xMLAttributes.getLength(); ++n) {
            attrArray[n] = new AttrImpl(elementImpl, xMLAttributes.getPrefix(n), xMLAttributes.getLocalName(n), xMLAttributes.getQName(n), xMLAttributes.getURI(n), xMLAttributes.getValue(n));
        }
        elementImpl.attrs = attrArray;
        if (this.nextFreeLoc == this.relations.length) {
            this.resizeRelations();
        }
        if (this.relations[this.currLoc][0] != this.parent) {
            this.relations[this.nextFreeLoc][0] = this.parent;
            this.currLoc = this.nextFreeLoc++;
        }
        n = 0;
        int n2 = 1;
        for (n2 = 1; n2 < this.relations[this.currLoc].length; ++n2) {
            if (this.relations[this.currLoc][n2] != null) continue;
            n = 1;
            break;
        }
        if (n == 0) {
            this.resizeRelations(this.currLoc);
        }
        this.relations[this.currLoc][n2] = elementImpl;
        this.parent.parentRow = this.currLoc;
        elementImpl.row = this.currLoc;
        elementImpl.col = n2;
    }

    public void endElement() {
        this.currLoc = this.parent.row;
        this.parent = (ElementImpl)this.relations[this.currLoc][0];
    }

    void comment(XMLString xMLString) {
        this.fAnnotationBuffer.append("<!--");
        if (xMLString.length > 0) {
            this.fAnnotationBuffer.append(xMLString.ch, xMLString.offset, xMLString.length);
        }
        this.fAnnotationBuffer.append("-->");
    }

    void processingInstruction(String string, XMLString xMLString) {
        this.fAnnotationBuffer.append("<?").append(string);
        if (xMLString.length > 0) {
            this.fAnnotationBuffer.append(' ').append(xMLString.ch, xMLString.offset, xMLString.length);
        }
        this.fAnnotationBuffer.append("?>");
    }

    void characters(XMLString xMLString) {
        if (!this.inCDATA) {
            StringBuffer stringBuffer = this.fAnnotationBuffer;
            for (int i = xMLString.offset; i < xMLString.offset + xMLString.length; ++i) {
                char c = xMLString.ch[i];
                if (c == '&') {
                    stringBuffer.append("&amp;");
                    continue;
                }
                if (c == '<') {
                    stringBuffer.append("&lt;");
                    continue;
                }
                if (c == '>') {
                    stringBuffer.append("&gt;");
                    continue;
                }
                if (c == '\r') {
                    stringBuffer.append("&#xD;");
                    continue;
                }
                stringBuffer.append(c);
            }
        } else {
            this.fAnnotationBuffer.append(xMLString.ch, xMLString.offset, xMLString.length);
        }
    }

    void charactersRaw(String string) {
        this.fAnnotationBuffer.append(string);
    }

    void endAnnotation(QName qName, ElementImpl elementImpl) {
        this.fAnnotationBuffer.append("\n</").append(qName.rawname).append(">");
        elementImpl.fAnnotation = this.fAnnotationBuffer.toString();
        this.fAnnotationBuffer = null;
    }

    void endAnnotationElement(QName qName) {
        this.endAnnotationElement(qName.rawname);
    }

    void endAnnotationElement(String string) {
        this.fAnnotationBuffer.append("</").append(string).append(">");
    }

    void endSyntheticAnnotationElement(QName qName, boolean bl) {
        this.endSyntheticAnnotationElement(qName.rawname, bl);
    }

    void endSyntheticAnnotationElement(String string, boolean bl) {
        if (bl) {
            this.fAnnotationBuffer.append("\n</").append(string).append(">");
            this.parent.fSyntheticAnnotation = this.fAnnotationBuffer.toString();
            this.fAnnotationBuffer = null;
        } else {
            this.fAnnotationBuffer.append("</").append(string).append(">");
        }
    }

    void startAnnotationCDATA() {
        this.inCDATA = true;
        this.fAnnotationBuffer.append("<![CDATA[");
    }

    void endAnnotationCDATA() {
        this.fAnnotationBuffer.append("]]>");
        this.inCDATA = false;
    }

    private void resizeRelations() {
        NodeImpl[][] nodeImplArrayArray = new NodeImpl[this.relations.length + 15][];
        System.arraycopy(this.relations, 0, nodeImplArrayArray, 0, this.relations.length);
        for (int i = this.relations.length; i < nodeImplArrayArray.length; ++i) {
            nodeImplArrayArray[i] = new NodeImpl[10];
        }
        this.relations = nodeImplArrayArray;
    }

    private void resizeRelations(int n) {
        NodeImpl[] nodeImplArray = new NodeImpl[this.relations[n].length + 10];
        System.arraycopy(this.relations[n], 0, nodeImplArray, 0, this.relations[n].length);
        this.relations[n] = nodeImplArray;
    }

    public void reset() {
        int n;
        if (this.relations != null) {
            for (n = 0; n < this.relations.length; ++n) {
                for (int i = 0; i < this.relations[n].length; ++i) {
                    this.relations[n][i] = null;
                }
            }
        }
        this.relations = new NodeImpl[15][];
        this.parent = new ElementImpl(0, 0, 0);
        this.parent.rawname = "DOCUMENT_NODE";
        this.currLoc = 0;
        this.nextFreeLoc = 1;
        this.inCDATA = false;
        for (n = 0; n < 15; ++n) {
            this.relations[n] = new NodeImpl[10];
        }
        this.relations[this.currLoc][0] = this.parent;
    }

    public void printDOM() {
    }

    public static void traverse(Node node, int n) {
        Object object;
        SchemaDOM.indent(n);
        System.out.print("<" + node.getNodeName());
        if (node.hasAttributes()) {
            object = node.getAttributes();
            for (int i = 0; i < object.getLength(); ++i) {
                System.out.print("  " + ((Attr)object.item(i)).getName() + "=\"" + ((Attr)object.item(i)).getValue() + "\"");
            }
        }
        if (node.hasChildNodes()) {
            System.out.println(">");
            n += 4;
            for (object = node.getFirstChild(); object != null; object = object.getNextSibling()) {
                SchemaDOM.traverse((Node)object, n);
            }
            SchemaDOM.indent(n -= 4);
            System.out.println("</" + node.getNodeName() + ">");
        } else {
            System.out.println("/>");
        }
    }

    public static void indent(int n) {
        for (int i = 0; i < n; ++i) {
            System.out.print(' ');
        }
    }

    @Override
    public Element getDocumentElement() {
        return (ElementImpl)this.relations[0][1];
    }

    @Override
    public DOMImplementation getImplementation() {
        return SchemaDOMImplementation.getDOMImplementation();
    }

    void startAnnotation(QName qName, XMLAttributes xMLAttributes, NamespaceContext namespaceContext) {
        this.startAnnotation(qName.rawname, xMLAttributes, namespaceContext);
    }

    void startAnnotation(String string, XMLAttributes xMLAttributes, NamespaceContext namespaceContext) {
        String string2;
        String string3;
        if (this.fAnnotationBuffer == null) {
            this.fAnnotationBuffer = new StringBuffer(256);
        }
        this.fAnnotationBuffer.append("<").append(string).append(" ");
        ArrayList<String> arrayList = new ArrayList<String>();
        for (int i = 0; i < xMLAttributes.getLength(); ++i) {
            string3 = xMLAttributes.getValue(i);
            string2 = xMLAttributes.getPrefix(i);
            String string4 = xMLAttributes.getQName(i);
            if (string2 == XMLSymbols.PREFIX_XMLNS || string4 == XMLSymbols.PREFIX_XMLNS) {
                arrayList.add(string2 == XMLSymbols.PREFIX_XMLNS ? xMLAttributes.getLocalName(i) : XMLSymbols.EMPTY_STRING);
            }
            this.fAnnotationBuffer.append(string4).append("=\"").append(SchemaDOM.processAttValue(string3)).append("\" ");
        }
        Enumeration enumeration = namespaceContext.getAllPrefixes();
        while (enumeration.hasMoreElements()) {
            string3 = (String)enumeration.nextElement();
            string2 = namespaceContext.getURI(string3);
            if (string2 == null) {
                string2 = XMLSymbols.EMPTY_STRING;
            }
            if (arrayList.contains(string3)) continue;
            if (string3 == XMLSymbols.EMPTY_STRING) {
                this.fAnnotationBuffer.append("xmlns").append("=\"").append(SchemaDOM.processAttValue(string2)).append("\" ");
                continue;
            }
            this.fAnnotationBuffer.append("xmlns:").append(string3).append("=\"").append(SchemaDOM.processAttValue(string2)).append("\" ");
        }
        this.fAnnotationBuffer.append(">\n");
    }

    void startAnnotationElement(QName qName, XMLAttributes xMLAttributes) {
        this.startAnnotationElement(qName.rawname, xMLAttributes);
    }

    void startAnnotationElement(String string, XMLAttributes xMLAttributes) {
        this.fAnnotationBuffer.append("<").append(string);
        for (int i = 0; i < xMLAttributes.getLength(); ++i) {
            String string2 = xMLAttributes.getValue(i);
            this.fAnnotationBuffer.append(" ").append(xMLAttributes.getQName(i)).append("=\"").append(SchemaDOM.processAttValue(string2)).append("\"");
        }
        this.fAnnotationBuffer.append(">");
    }

    private static String processAttValue(String string) {
        int n = string.length();
        for (int i = 0; i < n; ++i) {
            char c = string.charAt(i);
            if (c != '\"' && c != '<' && c != '&' && c != '\t' && c != '\n' && c != '\r') continue;
            return SchemaDOM.escapeAttValue(string, i);
        }
        return string;
    }

    private static String escapeAttValue(String string, int n) {
        int n2 = string.length();
        StringBuffer stringBuffer = new StringBuffer(n2);
        stringBuffer.append(string.substring(0, n));
        for (int i = n; i < n2; ++i) {
            char c = string.charAt(i);
            if (c == '\"') {
                stringBuffer.append("&quot;");
                continue;
            }
            if (c == '<') {
                stringBuffer.append("&lt;");
                continue;
            }
            if (c == '&') {
                stringBuffer.append("&amp;");
                continue;
            }
            if (c == '\t') {
                stringBuffer.append("&#x9;");
                continue;
            }
            if (c == '\n') {
                stringBuffer.append("&#xA;");
                continue;
            }
            if (c == '\r') {
                stringBuffer.append("&#xD;");
                continue;
            }
            stringBuffer.append(c);
        }
        return stringBuffer.toString();
    }
}


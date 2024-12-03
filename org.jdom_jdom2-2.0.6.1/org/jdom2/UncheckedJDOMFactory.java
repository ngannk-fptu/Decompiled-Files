/*
 * Decompiled with CFR 0.152.
 */
package org.jdom2;

import java.util.ArrayList;
import java.util.Map;
import org.jdom2.Attribute;
import org.jdom2.AttributeType;
import org.jdom2.CDATA;
import org.jdom2.Comment;
import org.jdom2.Content;
import org.jdom2.DefaultJDOMFactory;
import org.jdom2.DocType;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.EntityRef;
import org.jdom2.Namespace;
import org.jdom2.Parent;
import org.jdom2.ProcessingInstruction;
import org.jdom2.Text;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class UncheckedJDOMFactory
extends DefaultJDOMFactory {
    @Override
    public Element element(int line, int col, String name, Namespace namespace) {
        Element e = new Element();
        e.name = name;
        if (namespace == null) {
            namespace = Namespace.NO_NAMESPACE;
        }
        e.namespace = namespace;
        return e;
    }

    @Override
    public Element element(int line, int col, String name) {
        Element e = new Element();
        e.name = name;
        e.namespace = Namespace.NO_NAMESPACE;
        return e;
    }

    @Override
    public Element element(int line, int col, String name, String uri) {
        return this.element(name, Namespace.getNamespace("", uri));
    }

    @Override
    public Element element(int line, int col, String name, String prefix, String uri) {
        return this.element(name, Namespace.getNamespace(prefix, uri));
    }

    @Override
    public Attribute attribute(String name, String value, Namespace namespace) {
        Attribute a = new Attribute();
        a.name = name;
        a.value = value;
        if (namespace == null) {
            namespace = Namespace.NO_NAMESPACE;
        }
        a.namespace = namespace;
        return a;
    }

    @Override
    @Deprecated
    public Attribute attribute(String name, String value, int type, Namespace namespace) {
        return this.attribute(name, value, AttributeType.byIndex(type), namespace);
    }

    @Override
    public Attribute attribute(String name, String value, AttributeType type, Namespace namespace) {
        Attribute a = new Attribute();
        a.name = name;
        a.type = type;
        a.value = value;
        if (namespace == null) {
            namespace = Namespace.NO_NAMESPACE;
        }
        a.namespace = namespace;
        return a;
    }

    @Override
    public Attribute attribute(String name, String value) {
        Attribute a = new Attribute();
        a.name = name;
        a.value = value;
        a.namespace = Namespace.NO_NAMESPACE;
        return a;
    }

    @Override
    @Deprecated
    public Attribute attribute(String name, String value, int type) {
        return this.attribute(name, value, AttributeType.byIndex(type));
    }

    @Override
    public Attribute attribute(String name, String value, AttributeType type) {
        Attribute a = new Attribute();
        a.name = name;
        a.type = type;
        a.value = value;
        a.namespace = Namespace.NO_NAMESPACE;
        return a;
    }

    @Override
    public Text text(int line, int col, String str) {
        Text t = new Text();
        t.value = str;
        return t;
    }

    @Override
    public CDATA cdata(int line, int col, String str) {
        CDATA c = new CDATA();
        c.value = str;
        return c;
    }

    @Override
    public Comment comment(int line, int col, String str) {
        Comment c = new Comment();
        c.text = str;
        return c;
    }

    @Override
    public ProcessingInstruction processingInstruction(int line, int col, String target, Map<String, String> data) {
        ProcessingInstruction p = new ProcessingInstruction();
        p.target = target;
        p.setData(data);
        return p;
    }

    @Override
    public ProcessingInstruction processingInstruction(int line, int col, String target, String data) {
        ProcessingInstruction p = new ProcessingInstruction();
        p.target = target;
        p.setData(data);
        return p;
    }

    @Override
    public ProcessingInstruction processingInstruction(int line, int col, String target) {
        ProcessingInstruction p = new ProcessingInstruction();
        p.target = target;
        p.rawData = "";
        return p;
    }

    @Override
    public EntityRef entityRef(int line, int col, String name) {
        EntityRef e = new EntityRef();
        e.name = name;
        return e;
    }

    @Override
    public EntityRef entityRef(int line, int col, String name, String systemID) {
        EntityRef e = new EntityRef();
        e.name = name;
        e.systemID = systemID;
        return e;
    }

    @Override
    public EntityRef entityRef(int line, int col, String name, String publicID, String systemID) {
        EntityRef e = new EntityRef();
        e.name = name;
        e.publicID = publicID;
        e.systemID = systemID;
        return e;
    }

    @Override
    public DocType docType(int line, int col, String elementName, String publicID, String systemID) {
        DocType d = new DocType();
        d.elementName = elementName;
        d.publicID = publicID;
        d.systemID = systemID;
        return d;
    }

    @Override
    public DocType docType(int line, int col, String elementName, String systemID) {
        return this.docType(elementName, null, systemID);
    }

    @Override
    public DocType docType(int line, int col, String elementName) {
        return this.docType(elementName, null, null);
    }

    @Override
    public Document document(Element rootElement, DocType docType, String baseURI) {
        Document d = new Document();
        if (docType != null) {
            this.addContent(d, docType);
        }
        if (rootElement != null) {
            this.addContent(d, rootElement);
        }
        if (baseURI != null) {
            d.baseURI = baseURI;
        }
        return d;
    }

    @Override
    public Document document(Element rootElement, DocType docType) {
        return this.document(rootElement, docType, null);
    }

    @Override
    public Document document(Element rootElement) {
        return this.document(rootElement, null, null);
    }

    @Override
    public void addContent(Parent parent, Content child) {
        if (parent instanceof Element) {
            Element elt = (Element)parent;
            elt.content.uncheckedAddContent(child);
        } else {
            Document doc = (Document)parent;
            doc.content.uncheckedAddContent(child);
        }
    }

    @Override
    public void setAttribute(Element parent, Attribute a) {
        parent.getAttributeList().uncheckedAddAttribute(a);
    }

    @Override
    public void addNamespaceDeclaration(Element parent, Namespace additional) {
        if (parent.additionalNamespaces == null) {
            parent.additionalNamespaces = new ArrayList<Namespace>(5);
        }
        parent.additionalNamespaces.add(additional);
    }

    @Override
    public void setRoot(Document doc, Element root) {
        doc.content.uncheckedAddContent(root);
    }
}


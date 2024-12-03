/*
 * Decompiled with CFR 0.152.
 */
package org.jdom;

import java.util.ArrayList;
import java.util.Map;
import org.jdom.Attribute;
import org.jdom.CDATA;
import org.jdom.Comment;
import org.jdom.Content;
import org.jdom.DocType;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.EntityRef;
import org.jdom.JDOMFactory;
import org.jdom.Namespace;
import org.jdom.Parent;
import org.jdom.ProcessingInstruction;
import org.jdom.Text;

public class UncheckedJDOMFactory
implements JDOMFactory {
    @Override
    public Element element(String name, Namespace namespace) {
        Element e = new Element();
        e.name = name;
        if (namespace == null) {
            namespace = Namespace.NO_NAMESPACE;
        }
        e.namespace = namespace;
        return e;
    }

    @Override
    public Element element(String name) {
        Element e = new Element();
        e.name = name;
        e.namespace = Namespace.NO_NAMESPACE;
        return e;
    }

    @Override
    public Element element(String name, String uri) {
        return this.element(name, Namespace.getNamespace("", uri));
    }

    @Override
    public Element element(String name, String prefix, String uri) {
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
    public Attribute attribute(String name, String value, int type, Namespace namespace) {
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
    public Attribute attribute(String name, String value, int type) {
        Attribute a = new Attribute();
        a.name = name;
        a.type = type;
        a.value = value;
        a.namespace = Namespace.NO_NAMESPACE;
        return a;
    }

    @Override
    public Text text(String str) {
        Text t = new Text();
        t.value = str;
        return t;
    }

    @Override
    public CDATA cdata(String str) {
        CDATA c = new CDATA();
        c.value = str;
        return c;
    }

    @Override
    public Comment comment(String str) {
        Comment c = new Comment();
        c.text = str;
        return c;
    }

    @Override
    public ProcessingInstruction processingInstruction(String target, Map data) {
        ProcessingInstruction p = new ProcessingInstruction();
        p.target = target;
        p.setData(data);
        return p;
    }

    @Override
    public ProcessingInstruction processingInstruction(String target, String data) {
        ProcessingInstruction p = new ProcessingInstruction();
        p.target = target;
        p.setData(data);
        return p;
    }

    @Override
    public EntityRef entityRef(String name) {
        EntityRef e = new EntityRef();
        e.name = name;
        return e;
    }

    @Override
    public EntityRef entityRef(String name, String systemID) {
        EntityRef e = new EntityRef();
        e.name = name;
        e.systemID = systemID;
        return e;
    }

    @Override
    public EntityRef entityRef(String name, String publicID, String systemID) {
        EntityRef e = new EntityRef();
        e.name = name;
        e.publicID = publicID;
        e.systemID = systemID;
        return e;
    }

    @Override
    public DocType docType(String elementName, String publicID, String systemID) {
        DocType d = new DocType();
        d.elementName = elementName;
        d.publicID = publicID;
        d.systemID = systemID;
        return d;
    }

    @Override
    public DocType docType(String elementName, String systemID) {
        return this.docType(elementName, null, systemID);
    }

    @Override
    public DocType docType(String elementName) {
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
        parent.attributes.uncheckedAddAttribute(a);
    }

    @Override
    public void addNamespaceDeclaration(Element parent, Namespace additional) {
        if (parent.additionalNamespaces == null) {
            parent.additionalNamespaces = new ArrayList(5);
        }
        parent.additionalNamespaces.add(additional);
    }
}


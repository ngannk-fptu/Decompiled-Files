/*
 * Decompiled with CFR 0.152.
 */
package org.jdom2;

import java.util.Map;
import org.jdom2.Attribute;
import org.jdom2.AttributeType;
import org.jdom2.CDATA;
import org.jdom2.Comment;
import org.jdom2.Content;
import org.jdom2.DocType;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.EntityRef;
import org.jdom2.JDOMFactory;
import org.jdom2.Namespace;
import org.jdom2.Parent;
import org.jdom2.ProcessingInstruction;
import org.jdom2.Text;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class DefaultJDOMFactory
implements JDOMFactory {
    @Override
    public Attribute attribute(String name, String value, Namespace namespace) {
        return new Attribute(name, value, namespace);
    }

    @Override
    @Deprecated
    public Attribute attribute(String name, String value, int type, Namespace namespace) {
        return new Attribute(name, value, AttributeType.byIndex(type), namespace);
    }

    @Override
    public Attribute attribute(String name, String value, AttributeType type, Namespace namespace) {
        return new Attribute(name, value, type, namespace);
    }

    @Override
    public Attribute attribute(String name, String value) {
        return new Attribute(name, value);
    }

    @Override
    @Deprecated
    public Attribute attribute(String name, String value, int type) {
        return new Attribute(name, value, type);
    }

    @Override
    public Attribute attribute(String name, String value, AttributeType type) {
        return new Attribute(name, value, type);
    }

    @Override
    public final CDATA cdata(String str) {
        return this.cdata(-1, -1, str);
    }

    @Override
    public CDATA cdata(int line, int col, String text) {
        return new CDATA(text);
    }

    @Override
    public final Text text(String str) {
        return this.text(-1, -1, str);
    }

    @Override
    public Text text(int line, int col, String text) {
        return new Text(text);
    }

    @Override
    public final Comment comment(String text) {
        return this.comment(-1, -1, text);
    }

    @Override
    public Comment comment(int line, int col, String text) {
        return new Comment(text);
    }

    @Override
    public final DocType docType(String elementName, String publicID, String systemID) {
        return this.docType(-1, -1, elementName, publicID, systemID);
    }

    @Override
    public DocType docType(int line, int col, String elementName, String publicID, String systemID) {
        return new DocType(elementName, publicID, systemID);
    }

    @Override
    public final DocType docType(String elementName, String systemID) {
        return this.docType(-1, -1, elementName, systemID);
    }

    @Override
    public DocType docType(int line, int col, String elementName, String systemID) {
        return new DocType(elementName, systemID);
    }

    @Override
    public final DocType docType(String elementName) {
        return this.docType(-1, -1, elementName);
    }

    @Override
    public DocType docType(int line, int col, String elementName) {
        return new DocType(elementName);
    }

    @Override
    public Document document(Element rootElement, DocType docType) {
        return new Document(rootElement, docType);
    }

    @Override
    public Document document(Element rootElement, DocType docType, String baseURI) {
        return new Document(rootElement, docType, baseURI);
    }

    @Override
    public Document document(Element rootElement) {
        return new Document(rootElement);
    }

    @Override
    public final Element element(String name, Namespace namespace) {
        return this.element(-1, -1, name, namespace);
    }

    @Override
    public Element element(int line, int col, String name, Namespace namespace) {
        return new Element(name, namespace);
    }

    @Override
    public final Element element(String name) {
        return this.element(-1, -1, name);
    }

    @Override
    public Element element(int line, int col, String name) {
        return new Element(name);
    }

    @Override
    public final Element element(String name, String uri) {
        return this.element(-1, -1, name, uri);
    }

    @Override
    public Element element(int line, int col, String name, String uri) {
        return new Element(name, uri);
    }

    @Override
    public final Element element(String name, String prefix, String uri) {
        return this.element(-1, -1, name, prefix, uri);
    }

    @Override
    public Element element(int line, int col, String name, String prefix, String uri) {
        return new Element(name, prefix, uri);
    }

    @Override
    public final ProcessingInstruction processingInstruction(String target) {
        return this.processingInstruction(-1, -1, target);
    }

    @Override
    public ProcessingInstruction processingInstruction(int line, int col, String target) {
        return new ProcessingInstruction(target);
    }

    @Override
    public final ProcessingInstruction processingInstruction(String target, Map<String, String> data) {
        return this.processingInstruction(-1, -1, target, data);
    }

    @Override
    public ProcessingInstruction processingInstruction(int line, int col, String target, Map<String, String> data) {
        return new ProcessingInstruction(target, data);
    }

    @Override
    public final ProcessingInstruction processingInstruction(String target, String data) {
        return this.processingInstruction(-1, -1, target, data);
    }

    @Override
    public ProcessingInstruction processingInstruction(int line, int col, String target, String data) {
        return new ProcessingInstruction(target, data);
    }

    @Override
    public final EntityRef entityRef(String name) {
        return this.entityRef(-1, -1, name);
    }

    @Override
    public EntityRef entityRef(int line, int col, String name) {
        return new EntityRef(name);
    }

    @Override
    public final EntityRef entityRef(String name, String publicID, String systemID) {
        return this.entityRef(-1, -1, name, publicID, systemID);
    }

    @Override
    public EntityRef entityRef(int line, int col, String name, String publicID, String systemID) {
        return new EntityRef(name, publicID, systemID);
    }

    @Override
    public final EntityRef entityRef(String name, String systemID) {
        return this.entityRef(-1, -1, name, systemID);
    }

    @Override
    public EntityRef entityRef(int line, int col, String name, String systemID) {
        return new EntityRef(name, systemID);
    }

    @Override
    public void addContent(Parent parent, Content child) {
        if (parent instanceof Document) {
            ((Document)parent).addContent(child);
        } else {
            ((Element)parent).addContent(child);
        }
    }

    @Override
    public void setAttribute(Element parent, Attribute a) {
        parent.setAttribute(a);
    }

    @Override
    public void addNamespaceDeclaration(Element parent, Namespace additional) {
        parent.addNamespaceDeclaration(additional);
    }

    @Override
    public void setRoot(Document doc, Element root) {
        doc.setRootElement(root);
    }
}


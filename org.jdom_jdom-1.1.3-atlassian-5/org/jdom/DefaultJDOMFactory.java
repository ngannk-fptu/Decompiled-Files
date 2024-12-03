/*
 * Decompiled with CFR 0.152.
 */
package org.jdom;

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

public class DefaultJDOMFactory
implements JDOMFactory {
    private static final String CVS_ID = "@(#) $RCSfile: DefaultJDOMFactory.java,v $ $Revision: 1.7 $ $Date: 2007/11/10 05:28:58 $ $Name:  $";

    @Override
    public Attribute attribute(String name, String value, Namespace namespace) {
        return new Attribute(name, value, namespace);
    }

    @Override
    public Attribute attribute(String name, String value, int type, Namespace namespace) {
        return new Attribute(name, value, type, namespace);
    }

    @Override
    public Attribute attribute(String name, String value) {
        return new Attribute(name, value);
    }

    @Override
    public Attribute attribute(String name, String value, int type) {
        return new Attribute(name, value, type);
    }

    @Override
    public CDATA cdata(String text) {
        return new CDATA(text);
    }

    @Override
    public Text text(String text) {
        return new Text(text);
    }

    @Override
    public Comment comment(String text) {
        return new Comment(text);
    }

    @Override
    public DocType docType(String elementName, String publicID, String systemID) {
        return new DocType(elementName, publicID, systemID);
    }

    @Override
    public DocType docType(String elementName, String systemID) {
        return new DocType(elementName, systemID);
    }

    @Override
    public DocType docType(String elementName) {
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
    public Element element(String name, Namespace namespace) {
        return new Element(name, namespace);
    }

    @Override
    public Element element(String name) {
        return new Element(name);
    }

    @Override
    public Element element(String name, String uri) {
        return new Element(name, uri);
    }

    @Override
    public Element element(String name, String prefix, String uri) {
        return new Element(name, prefix, uri);
    }

    @Override
    public ProcessingInstruction processingInstruction(String target, Map data) {
        return new ProcessingInstruction(target, data);
    }

    @Override
    public ProcessingInstruction processingInstruction(String target, String data) {
        return new ProcessingInstruction(target, data);
    }

    @Override
    public EntityRef entityRef(String name) {
        return new EntityRef(name);
    }

    @Override
    public EntityRef entityRef(String name, String publicID, String systemID) {
        return new EntityRef(name, publicID, systemID);
    }

    @Override
    public EntityRef entityRef(String name, String systemID) {
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
}


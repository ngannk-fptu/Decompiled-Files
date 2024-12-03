/*
 * Decompiled with CFR 0.152.
 */
package org.apache.abdera.parser.stax;

import java.util.List;
import javax.xml.namespace.QName;
import org.apache.abdera.model.Element;
import org.apache.abdera.model.ElementWrapper;
import org.apache.abdera.model.ExtensibleElement;
import org.apache.abdera.parser.stax.FOMElement;
import org.apache.abdera.parser.stax.FOMFactory;
import org.apache.abdera.parser.stax.util.FOMElementIteratorWrapper;
import org.apache.abdera.parser.stax.util.FOMExtensionIterator;
import org.apache.abdera.parser.stax.util.FOMList;
import org.apache.axiom.om.OMContainer;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMException;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.OMNamespace;
import org.apache.axiom.om.OMXMLParserWrapper;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class FOMExtensibleElement
extends FOMElement
implements ExtensibleElement {
    private static final long serialVersionUID = -1652430686161947531L;

    protected FOMExtensibleElement(String name, OMNamespace namespace, OMContainer parent, OMFactory factory) throws OMException {
        super(name, namespace, parent, factory);
    }

    protected FOMExtensibleElement(QName qname, OMContainer parent, OMFactory factory) throws OMException {
        super(qname, parent, factory);
    }

    protected FOMExtensibleElement(String localName, OMContainer parent, OMFactory factory, OMXMLParserWrapper builder) throws OMException {
        super(localName, parent, factory, builder);
    }

    @Override
    public List<Element> getExtensions() {
        return new FOMList<Element>(new FOMExtensionIterator(this));
    }

    @Override
    public List<Element> getExtensions(String uri) {
        return new FOMList<Element>(new FOMExtensionIterator(this, uri));
    }

    @Override
    public <T extends Element> List<T> getExtensions(QName qname) {
        FOMFactory factory = (FOMFactory)this.getFactory();
        return new FOMList(new FOMElementIteratorWrapper(factory, this.getChildrenWithName(qname)));
    }

    @Override
    public <T extends Element> T getExtension(QName qname) {
        FOMFactory factory = (FOMFactory)this.getFactory();
        Element t = (Element)((Object)this.getFirstChildWithName(qname));
        return t != null ? (T)factory.getElementWrapper(t) : null;
    }

    @Override
    public <T extends ExtensibleElement> T addExtension(Element extension) {
        this.complete();
        if (extension instanceof ElementWrapper) {
            ElementWrapper wrapper = (ElementWrapper)extension;
            extension = wrapper.getInternal();
        }
        QName qname = extension.getQName();
        String prefix = qname.getPrefix();
        this.declareIfNecessary(qname.getNamespaceURI(), prefix);
        this.addChild((OMElement)((Object)extension));
        return (T)this;
    }

    @Override
    public <T extends Element> T addExtension(QName qname) {
        this.complete();
        FOMFactory fomfactory = (FOMFactory)this.factory;
        String prefix = qname.getPrefix();
        this.declareIfNecessary(qname.getNamespaceURI(), prefix);
        return fomfactory.newExtensionElement(qname, this);
    }

    @Override
    public <T extends Element> T addExtension(String namespace, String localpart, String prefix) {
        this.complete();
        this.declareIfNecessary(namespace, prefix);
        return prefix != null ? this.addExtension(new QName(namespace, localpart, prefix)) : this.addExtension(new QName(namespace, localpart, ""));
    }

    @Override
    public Element addSimpleExtension(QName qname, String value) {
        this.complete();
        FOMFactory fomfactory = (FOMFactory)this.factory;
        Object el = fomfactory.newElement(qname, this);
        el.setText(value);
        String prefix = qname.getPrefix();
        this.declareIfNecessary(qname.getNamespaceURI(), prefix);
        return el;
    }

    @Override
    public Element addSimpleExtension(String namespace, String localPart, String prefix, String value) {
        this.complete();
        this.declareIfNecessary(namespace, prefix);
        return this.addSimpleExtension(prefix != null ? new QName(namespace, localPart, prefix) : new QName(namespace, localPart), value);
    }

    @Override
    public String getSimpleExtension(QName qname) {
        Object el = this.getExtension(qname);
        return el != null ? el.getText() : null;
    }

    @Override
    public String getSimpleExtension(String namespace, String localPart, String prefix) {
        return this.getSimpleExtension(new QName(namespace, localPart, prefix));
    }

    public void addExtensions(List<Element> extensions) {
        for (Element e : extensions) {
            this.addExtension(e);
        }
    }

    @Override
    public <T extends Element> T getExtension(Class<T> _class) {
        Element t = null;
        List<Element> extensions = this.getExtensions();
        for (Element ext : extensions) {
            if (!_class.isAssignableFrom(ext.getClass())) continue;
            t = ext;
            break;
        }
        return (T)t;
    }

    private Element getInternal(Element element) {
        if (element instanceof ElementWrapper) {
            ElementWrapper wrapper = (ElementWrapper)element;
            element = wrapper.getInternal();
        }
        return element;
    }

    @Override
    public <T extends ExtensibleElement> T addExtension(Element extension, Element before) {
        this.complete();
        extension = this.getInternal(extension);
        before = this.getInternal(before);
        if (before instanceof ElementWrapper) {
            ElementWrapper wrapper = (ElementWrapper)before;
            before = wrapper.getInternal();
        }
        if (before == null) {
            this.addExtension(extension);
        } else {
            extension.setParentElement(this);
            ((OMElement)((Object)before)).insertSiblingBefore((OMElement)((Object)extension));
        }
        return (T)this;
    }

    @Override
    public <T extends Element> T addExtension(QName qname, QName before) {
        this.complete();
        OMElement el = this.getFirstChildWithName(before);
        Object element = this.getFactory().newElement(qname);
        if (el == null) {
            this.addExtension((Element)element);
        } else {
            el.insertSiblingBefore((OMElement)((Object)this.getInternal((Element)element)));
        }
        return element;
    }
}


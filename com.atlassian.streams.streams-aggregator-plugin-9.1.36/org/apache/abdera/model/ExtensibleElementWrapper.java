/*
 * Decompiled with CFR 0.152.
 */
package org.apache.abdera.model;

import java.util.List;
import javax.xml.namespace.QName;
import org.apache.abdera.factory.Factory;
import org.apache.abdera.model.Element;
import org.apache.abdera.model.ElementWrapper;
import org.apache.abdera.model.ExtensibleElement;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public abstract class ExtensibleElementWrapper
extends ElementWrapper
implements ExtensibleElement {
    protected ExtensibleElementWrapper(Element internal) {
        super(internal);
    }

    public ExtensibleElementWrapper(Factory factory, QName qname) {
        super(factory, qname);
    }

    protected ExtensibleElement getExtInternal() {
        return (ExtensibleElement)this.getInternal();
    }

    @Override
    public <T extends ExtensibleElement> T addExtension(Element extension) {
        this.getExtInternal().addExtension(extension);
        return (T)this;
    }

    @Override
    public <T extends Element> T addExtension(QName qname) {
        return this.getExtInternal().addExtension(qname);
    }

    @Override
    public <T extends Element> T addExtension(String namespace, String localPart, String prefix) {
        return this.getExtInternal().addExtension(namespace, localPart, prefix);
    }

    @Override
    public Element addSimpleExtension(QName qname, String value) {
        return this.getExtInternal().addSimpleExtension(qname, value);
    }

    @Override
    public Element addSimpleExtension(String namespace, String localPart, String prefix, String value) {
        return this.getExtInternal().addSimpleExtension(namespace, localPart, prefix, value);
    }

    @Override
    public <T extends Element> T getExtension(QName qname) {
        return this.getExtInternal().getExtension(qname);
    }

    @Override
    public <T extends Element> T getExtension(Class<T> _class) {
        return this.getExtInternal().getExtension(_class);
    }

    @Override
    public List<Element> getExtensions() {
        return this.getExtInternal().getExtensions();
    }

    @Override
    public List<Element> getExtensions(String uri) {
        return this.getExtInternal().getExtensions(uri);
    }

    @Override
    public <T extends Element> List<T> getExtensions(QName qname) {
        return this.getExtInternal().getExtensions(qname);
    }

    @Override
    public String getSimpleExtension(QName qname) {
        return this.getExtInternal().getSimpleExtension(qname);
    }

    @Override
    public String getSimpleExtension(String namespace, String localPart, String prefix) {
        return this.getExtInternal().getSimpleExtension(namespace, localPart, prefix);
    }

    @Override
    public boolean getMustPreserveWhitespace() {
        return this.getExtInternal().getMustPreserveWhitespace();
    }

    @Override
    public <T extends Element> T setMustPreserveWhitespace(boolean preserve) {
        this.getExtInternal().setMustPreserveWhitespace(preserve);
        return (T)this;
    }

    @Override
    public <T extends ExtensibleElement> T addExtension(Element extension, Element before) {
        this.getExtInternal().addExtension(extension, before);
        return (T)this;
    }

    @Override
    public <T extends Element> T addExtension(QName qname, QName before) {
        return this.getExtInternal().addExtension(qname, before);
    }
}


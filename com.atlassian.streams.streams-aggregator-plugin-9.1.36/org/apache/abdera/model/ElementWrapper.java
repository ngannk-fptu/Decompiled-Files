/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.activation.DataHandler
 */
package org.apache.abdera.model;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import javax.activation.DataHandler;
import javax.xml.namespace.QName;
import org.apache.abdera.factory.Factory;
import org.apache.abdera.i18n.iri.IRI;
import org.apache.abdera.i18n.rfc4646.Lang;
import org.apache.abdera.model.Base;
import org.apache.abdera.model.Document;
import org.apache.abdera.model.Element;
import org.apache.abdera.writer.Writer;
import org.apache.abdera.writer.WriterOptions;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public abstract class ElementWrapper
implements Element {
    private Element internal;

    protected ElementWrapper(Element internal) {
        this.internal = internal;
    }

    protected ElementWrapper(Factory factory, QName qname) {
        Object el = factory.newElement(qname);
        this.internal = el instanceof ElementWrapper ? ((ElementWrapper)el).getInternal() : el;
    }

    @Override
    public <T extends Base> T addComment(String value) {
        this.internal.addComment(value);
        return (T)this;
    }

    @Override
    public Object clone() {
        try {
            ElementWrapper wrapper = (ElementWrapper)super.clone();
            wrapper.internal = (Element)this.internal.clone();
            return wrapper;
        }
        catch (CloneNotSupportedException e) {
            return null;
        }
    }

    @Override
    public <T extends Element> T declareNS(String uri, String prefix) {
        this.internal.declareNS(uri, prefix);
        return (T)this;
    }

    @Override
    public void discard() {
        this.internal.discard();
    }

    @Override
    public List<QName> getAttributes() {
        return this.internal.getAttributes();
    }

    @Override
    public String getAttributeValue(QName qname) {
        return this.internal.getAttributeValue(qname);
    }

    @Override
    public String getAttributeValue(String name) {
        return this.internal.getAttributeValue(name);
    }

    @Override
    public IRI getBaseUri() {
        return this.internal.getBaseUri();
    }

    @Override
    public <T extends Element> Document<T> getDocument() {
        return this.internal.getDocument();
    }

    @Override
    public List<QName> getExtensionAttributes() {
        return this.internal.getExtensionAttributes();
    }

    @Override
    public Factory getFactory() {
        return this.internal.getFactory();
    }

    @Override
    public <T extends Element> T getFirstChild() {
        return this.internal.getFirstChild();
    }

    @Override
    public <T extends Element> T getFirstChild(QName qname) {
        return this.internal.getFirstChild(qname);
    }

    @Override
    public String getLanguage() {
        return this.internal.getLanguage();
    }

    @Override
    public Lang getLanguageTag() {
        return this.internal.getLanguageTag();
    }

    @Override
    public Locale getLocale() {
        return this.internal.getLocale();
    }

    @Override
    public <T extends Element> T getNextSibling() {
        return this.internal.getNextSibling();
    }

    @Override
    public <T extends Element> T getNextSibling(QName qname) {
        return this.internal.getNextSibling(qname);
    }

    @Override
    public <T extends Base> T getParentElement() {
        return this.internal.getParentElement();
    }

    @Override
    public <T extends Element> T getPreviousSibling() {
        return this.internal.getPreviousSibling();
    }

    @Override
    public <T extends Element> T getPreviousSibling(QName qname) {
        return this.internal.getPreviousSibling(qname);
    }

    @Override
    public QName getQName() {
        return this.internal.getQName();
    }

    @Override
    public IRI getResolvedBaseUri() {
        return this.internal.getResolvedBaseUri();
    }

    @Override
    public String getText() {
        return this.internal.getText();
    }

    @Override
    public <T extends Element> T removeAttribute(QName qname) {
        this.internal.removeAttribute(qname);
        return (T)this;
    }

    @Override
    public <T extends Element> T removeAttribute(String name) {
        this.internal.removeAttribute(name);
        return (T)this;
    }

    @Override
    public <T extends Element> T setAttributeValue(QName qname, String value) {
        this.internal.setAttributeValue(qname, value);
        return (T)this;
    }

    @Override
    public <T extends Element> T setAttributeValue(String name, String value) {
        this.internal.setAttributeValue(name, value);
        return (T)this;
    }

    @Override
    public <T extends Element> T setBaseUri(IRI base) {
        this.internal.setBaseUri(base);
        return (T)this;
    }

    @Override
    public <T extends Element> T setBaseUri(String base) {
        this.internal.setBaseUri(base);
        return (T)this;
    }

    @Override
    public <T extends Element> T setLanguage(String language) {
        this.internal.setLanguage(language);
        return (T)this;
    }

    @Override
    public <T extends Element> T setParentElement(Element parent) {
        this.internal.setParentElement(parent);
        return (T)this;
    }

    @Override
    public void setText(String text) {
        this.internal.setText(text);
    }

    @Override
    public <T extends Element> T setText(DataHandler handler) {
        this.internal.setText(handler);
        return (T)this;
    }

    @Override
    public void writeTo(OutputStream out) throws IOException {
        this.internal.writeTo(out);
    }

    @Override
    public void writeTo(java.io.Writer writer) throws IOException {
        this.internal.writeTo(writer);
    }

    public boolean equals(Object other) {
        if (other instanceof ElementWrapper) {
            other = ((ElementWrapper)other).getInternal();
        }
        return this.internal.equals(other);
    }

    public String toString() {
        return this.internal.toString();
    }

    public int hashCode() {
        return this.internal.hashCode();
    }

    public Element getInternal() {
        return this.internal;
    }

    @Override
    public <T extends Element> List<T> getElements() {
        return this.internal.getElements();
    }

    @Override
    public Map<String, String> getNamespaces() {
        return this.internal.getNamespaces();
    }

    @Override
    public boolean getMustPreserveWhitespace() {
        return this.internal.getMustPreserveWhitespace();
    }

    @Override
    public <T extends Element> T setMustPreserveWhitespace(boolean preserve) {
        this.internal.setMustPreserveWhitespace(preserve);
        return (T)this;
    }

    @Override
    public void writeTo(OutputStream out, WriterOptions options) throws IOException {
        this.internal.writeTo(out, options);
    }

    @Override
    public void writeTo(Writer writer, OutputStream out, WriterOptions options) throws IOException {
        this.internal.writeTo(writer, out, options);
    }

    @Override
    public void writeTo(Writer writer, OutputStream out) throws IOException {
        this.internal.writeTo(writer, out);
    }

    @Override
    public void writeTo(Writer writer, java.io.Writer out, WriterOptions options) throws IOException {
        this.internal.writeTo(writer, out, options);
    }

    @Override
    public void writeTo(Writer writer, java.io.Writer out) throws IOException {
        this.internal.writeTo(writer, out);
    }

    @Override
    public void writeTo(String writer, OutputStream out, WriterOptions options) throws IOException {
        this.internal.writeTo(writer, out, options);
    }

    @Override
    public void writeTo(String writer, OutputStream out) throws IOException {
        this.internal.writeTo(writer, out);
    }

    @Override
    public void writeTo(String writer, java.io.Writer out, WriterOptions options) throws IOException {
        this.internal.writeTo(writer, out, options);
    }

    @Override
    public void writeTo(String writer, java.io.Writer out) throws IOException {
        this.internal.writeTo(writer, out);
    }

    @Override
    public void writeTo(java.io.Writer out, WriterOptions options) throws IOException {
        this.internal.writeTo(out, options);
    }

    @Override
    public WriterOptions getDefaultWriterOptions() {
        return this.internal.getDefaultWriterOptions();
    }

    @Override
    public <T extends Base> T complete() {
        this.internal.complete();
        return (T)this;
    }

    @Override
    public Iterator<Element> iterator() {
        return this.internal.iterator();
    }
}


/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.activation.DataHandler
 */
package org.apache.abdera.factory;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Writer;
import java.nio.channels.WritableByteChannel;
import java.util.Date;
import java.util.Locale;
import javax.activation.DataHandler;
import javax.xml.namespace.QName;
import org.apache.abdera.Abdera;
import org.apache.abdera.i18n.iri.IRI;
import org.apache.abdera.i18n.rfc4646.Lang;
import org.apache.abdera.model.Base;
import org.apache.abdera.model.Content;
import org.apache.abdera.model.Document;
import org.apache.abdera.model.Element;
import org.apache.abdera.model.Text;
import org.apache.abdera.util.AbstractStreamWriter;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class StreamBuilder
extends AbstractStreamWriter {
    private final Abdera abdera;
    private Base root = null;
    private Base current = null;

    public StreamBuilder() {
        this(Abdera.getInstance());
    }

    public StreamBuilder(Abdera abdera) {
        super(abdera, "fom");
        this.abdera = abdera;
    }

    public <T extends Base> T getBase() {
        return (T)this.root;
    }

    @Override
    public StreamBuilder startDocument(String xmlversion, String charset) {
        if (this.root != null) {
            throw new IllegalStateException("Document already started");
        }
        this.root = this.abdera.getFactory().newDocument();
        ((Document)this.root).setCharset(charset);
        this.current = this.root;
        return this;
    }

    @Override
    public StreamBuilder startDocument(String xmlversion) {
        return this.startDocument(xmlversion, "UTF-8");
    }

    private static QName getQName(String name, String namespace, String prefix) {
        if (prefix != null) {
            return new QName(namespace, name, prefix);
        }
        if (namespace != null) {
            return new QName(namespace, name);
        }
        return new QName(name);
    }

    @Override
    public StreamBuilder startElement(String name, String namespace, String prefix) {
        this.current = this.abdera.getFactory().newElement(StreamBuilder.getQName(name, namespace, prefix), this.current);
        if (this.root == null) {
            this.root = this.current;
        }
        return this;
    }

    @Override
    public StreamBuilder endElement() {
        this.current = this.current instanceof Element ? ((Element)this.current).getParentElement() : null;
        return this;
    }

    @Override
    public StreamBuilder writeAttribute(String name, String namespace, String prefix, String value) {
        if (!(this.current instanceof Element)) {
            throw new IllegalStateException("Not currently an element");
        }
        ((Element)this.current).setAttributeValue(StreamBuilder.getQName(name, namespace, prefix), value);
        return this;
    }

    @Override
    public StreamBuilder writeComment(String value) {
        this.current.addComment(value);
        return this;
    }

    @Override
    public StreamBuilder writeElementText(String value) {
        if (!(this.current instanceof Element)) {
            throw new IllegalStateException("Not currently an element");
        }
        Element element = (Element)this.current;
        String text = element.getText();
        element.setText(text + value);
        return this;
    }

    @Override
    public StreamBuilder writeId() {
        return this.writeId(this.abdera.getFactory().newUuidUri());
    }

    @Override
    public StreamBuilder writePI(String value) {
        return this.writePI(value, null);
    }

    @Override
    public StreamBuilder writePI(String value, String target) {
        if (!(this.current instanceof Document)) {
            throw new IllegalStateException("Not currently a document");
        }
        ((Document)this.current).addProcessingInstruction(target != null ? target : "", value);
        return this;
    }

    @Override
    public void close() throws IOException {
    }

    @Override
    public StreamBuilder flush() {
        return this;
    }

    @Override
    public StreamBuilder indent() {
        return this;
    }

    @Override
    public StreamBuilder setOutputStream(OutputStream out) {
        return this;
    }

    @Override
    public StreamBuilder setOutputStream(OutputStream out, String charset) {
        return this;
    }

    @Override
    public StreamBuilder setWriter(Writer writer) {
        return this;
    }

    @Override
    public StreamBuilder endAuthor() {
        return (StreamBuilder)super.endAuthor();
    }

    @Override
    public StreamBuilder endCategories() {
        return (StreamBuilder)super.endCategories();
    }

    @Override
    public StreamBuilder endCategory() {
        return (StreamBuilder)super.endCategory();
    }

    @Override
    public StreamBuilder endCollection() {
        return (StreamBuilder)super.endCollection();
    }

    @Override
    public StreamBuilder endContent() {
        return (StreamBuilder)super.endContent();
    }

    @Override
    public StreamBuilder endContributor() {
        return (StreamBuilder)super.endContributor();
    }

    @Override
    public StreamBuilder endControl() {
        return (StreamBuilder)super.endControl();
    }

    @Override
    public StreamBuilder endDocument() {
        return (StreamBuilder)super.endDocument();
    }

    @Override
    public StreamBuilder endEntry() {
        return (StreamBuilder)super.endEntry();
    }

    @Override
    public StreamBuilder endFeed() {
        return (StreamBuilder)super.endFeed();
    }

    @Override
    public StreamBuilder endGenerator() {
        return (StreamBuilder)super.endGenerator();
    }

    @Override
    public StreamBuilder endLink() {
        return (StreamBuilder)super.endLink();
    }

    @Override
    public StreamBuilder endPerson() {
        return (StreamBuilder)super.endPerson();
    }

    @Override
    public StreamBuilder endService() {
        return (StreamBuilder)super.endService();
    }

    @Override
    public StreamBuilder endSource() {
        return (StreamBuilder)super.endSource();
    }

    @Override
    public StreamBuilder endText() {
        return (StreamBuilder)super.endText();
    }

    @Override
    public StreamBuilder endWorkspace() {
        return (StreamBuilder)super.endWorkspace();
    }

    @Override
    public StreamBuilder setAutoclose(boolean auto) {
        return (StreamBuilder)super.setAutoclose(auto);
    }

    @Override
    public StreamBuilder setAutoflush(boolean auto) {
        return (StreamBuilder)super.setAutoflush(auto);
    }

    @Override
    public StreamBuilder setAutoIndent(boolean indent) {
        return (StreamBuilder)super.setAutoIndent(indent);
    }

    @Override
    public StreamBuilder setChannel(WritableByteChannel channel, String charset) {
        return (StreamBuilder)super.setChannel(channel, charset);
    }

    @Override
    public StreamBuilder setChannel(WritableByteChannel channel) {
        return (StreamBuilder)super.setChannel(channel);
    }

    @Override
    public StreamBuilder startAuthor() {
        return (StreamBuilder)super.startAuthor();
    }

    @Override
    public StreamBuilder startCategories() {
        return (StreamBuilder)super.startCategories();
    }

    @Override
    public StreamBuilder startCategories(boolean fixed, String scheme) {
        return (StreamBuilder)super.startCategories(fixed, scheme);
    }

    @Override
    public StreamBuilder startCategories(boolean fixed) {
        return (StreamBuilder)super.startCategories(fixed);
    }

    @Override
    public StreamBuilder startCategory(String term, String scheme, String label) {
        return (StreamBuilder)super.startCategory(term, scheme, label);
    }

    @Override
    public StreamBuilder startCategory(String term, String scheme) {
        return (StreamBuilder)super.startCategory(term, scheme);
    }

    @Override
    public StreamBuilder startCategory(String term) {
        return (StreamBuilder)super.startCategory(term);
    }

    @Override
    public StreamBuilder startCollection(String href) {
        return (StreamBuilder)super.startCollection(href);
    }

    @Override
    public StreamBuilder startContent(String type, String src) {
        return (StreamBuilder)super.startContent(type, src);
    }

    @Override
    public StreamBuilder startContent(String type) {
        return (StreamBuilder)super.startContent(type);
    }

    @Override
    public StreamBuilder startContent(Content.Type type, String src) {
        return (StreamBuilder)super.startContent(type, src);
    }

    @Override
    public StreamBuilder startContent(Content.Type type) {
        return (StreamBuilder)super.startContent(type);
    }

    @Override
    public StreamBuilder startContributor() {
        return (StreamBuilder)super.startContributor();
    }

    @Override
    public StreamBuilder startControl() {
        return (StreamBuilder)super.startControl();
    }

    @Override
    public StreamBuilder startDocument() {
        return (StreamBuilder)super.startDocument();
    }

    @Override
    public StreamBuilder startElement(QName qname) {
        return (StreamBuilder)super.startElement(qname);
    }

    @Override
    public StreamBuilder startElement(String name, String namespace) {
        return (StreamBuilder)super.startElement(name, namespace);
    }

    @Override
    public StreamBuilder startElement(String name) {
        return (StreamBuilder)super.startElement(name);
    }

    @Override
    public StreamBuilder startEntry() {
        return (StreamBuilder)super.startEntry();
    }

    @Override
    public StreamBuilder startFeed() {
        return (StreamBuilder)super.startFeed();
    }

    @Override
    public StreamBuilder startGenerator(String version, String uri) {
        return (StreamBuilder)super.startGenerator(version, uri);
    }

    @Override
    public StreamBuilder startLink(String iri, String rel, String type, String title, String hreflang, long length) {
        return (StreamBuilder)super.startLink(iri, rel, type, title, hreflang, length);
    }

    @Override
    public StreamBuilder startLink(String iri, String rel, String type) {
        return (StreamBuilder)super.startLink(iri, rel, type);
    }

    @Override
    public StreamBuilder startLink(String iri, String rel) {
        return (StreamBuilder)super.startLink(iri, rel);
    }

    @Override
    public StreamBuilder startLink(String iri) {
        return (StreamBuilder)super.startLink(iri);
    }

    @Override
    public StreamBuilder startPerson(QName qname) {
        return (StreamBuilder)super.startPerson(qname);
    }

    @Override
    public StreamBuilder startPerson(String name, String namespace, String prefix) {
        return (StreamBuilder)super.startPerson(name, namespace, prefix);
    }

    @Override
    public StreamBuilder startPerson(String name, String namespace) {
        return (StreamBuilder)super.startPerson(name, namespace);
    }

    @Override
    public StreamBuilder startPerson(String name) {
        return (StreamBuilder)super.startPerson(name);
    }

    @Override
    public StreamBuilder startService() {
        return (StreamBuilder)super.startService();
    }

    @Override
    public StreamBuilder startSource() {
        return (StreamBuilder)super.startSource();
    }

    @Override
    public StreamBuilder startText(QName qname, Text.Type type) {
        return (StreamBuilder)super.startText(qname, type);
    }

    @Override
    public StreamBuilder startText(String name, String namespace, String prefix, Text.Type type) {
        return (StreamBuilder)super.startText(name, namespace, prefix, type);
    }

    @Override
    public StreamBuilder startText(String name, String namespace, Text.Type type) {
        return (StreamBuilder)super.startText(name, namespace, type);
    }

    @Override
    public StreamBuilder startText(String name, Text.Type type) {
        return (StreamBuilder)super.startText(name, type);
    }

    @Override
    public StreamBuilder startWorkspace() {
        return (StreamBuilder)super.startWorkspace();
    }

    @Override
    public StreamBuilder writeAccepts(String ... accepts) {
        return (StreamBuilder)super.writeAccepts(accepts);
    }

    @Override
    public StreamBuilder writeAcceptsEntry() {
        return (StreamBuilder)super.writeAcceptsEntry();
    }

    @Override
    public StreamBuilder writeAcceptsNothing() {
        return (StreamBuilder)super.writeAcceptsNothing();
    }

    @Override
    public StreamBuilder writeAttribute(QName qname, Date value) {
        return (StreamBuilder)super.writeAttribute(qname, value);
    }

    @Override
    public StreamBuilder writeAttribute(QName qname, double value) {
        return (StreamBuilder)super.writeAttribute(qname, value);
    }

    @Override
    public StreamBuilder writeAttribute(QName qname, int value) {
        return (StreamBuilder)super.writeAttribute(qname, value);
    }

    @Override
    public StreamBuilder writeAttribute(QName qname, long value) {
        return (StreamBuilder)super.writeAttribute(qname, value);
    }

    @Override
    public StreamBuilder writeAttribute(QName qname, String value) {
        return (StreamBuilder)super.writeAttribute(qname, value);
    }

    @Override
    public StreamBuilder writeAttribute(String name, Date value) {
        return (StreamBuilder)super.writeAttribute(name, value);
    }

    @Override
    public StreamBuilder writeAttribute(String name, double value) {
        return (StreamBuilder)super.writeAttribute(name, value);
    }

    @Override
    public StreamBuilder writeAttribute(String name, int value) {
        return (StreamBuilder)super.writeAttribute(name, value);
    }

    @Override
    public StreamBuilder writeAttribute(String name, long value) {
        return (StreamBuilder)super.writeAttribute(name, value);
    }

    @Override
    public StreamBuilder writeAttribute(String name, String namespace, Date value) {
        return (StreamBuilder)super.writeAttribute(name, namespace, value);
    }

    @Override
    public StreamBuilder writeAttribute(String name, String namespace, double value) {
        return (StreamBuilder)super.writeAttribute(name, namespace, value);
    }

    @Override
    public StreamBuilder writeAttribute(String name, String namespace, int value) {
        return (StreamBuilder)super.writeAttribute(name, namespace, value);
    }

    @Override
    public StreamBuilder writeAttribute(String name, String namespace, long value) {
        return (StreamBuilder)super.writeAttribute(name, namespace, value);
    }

    @Override
    public StreamBuilder writeAttribute(String name, String namespace, String prefix, Date value) {
        return (StreamBuilder)super.writeAttribute(name, namespace, prefix, value);
    }

    @Override
    public StreamBuilder writeAttribute(String name, String namespace, String prefix, double value) {
        return (StreamBuilder)super.writeAttribute(name, namespace, prefix, value);
    }

    @Override
    public StreamBuilder writeAttribute(String name, String namespace, String prefix, int value) {
        return (StreamBuilder)super.writeAttribute(name, namespace, prefix, value);
    }

    @Override
    public StreamBuilder writeAttribute(String name, String namespace, String prefix, long value) {
        return (StreamBuilder)super.writeAttribute(name, namespace, prefix, value);
    }

    @Override
    public StreamBuilder writeAttribute(String name, String namespace, String value) {
        return (StreamBuilder)super.writeAttribute(name, namespace, value);
    }

    @Override
    public StreamBuilder writeAttribute(String name, String value) {
        return (StreamBuilder)super.writeAttribute(name, value);
    }

    @Override
    public StreamBuilder writeAuthor(String name, String email, String uri) {
        return (StreamBuilder)super.writeAuthor(name, email, uri);
    }

    @Override
    public StreamBuilder writeAuthor(String name) {
        return (StreamBuilder)super.writeAuthor(name);
    }

    @Override
    public StreamBuilder writeBase(IRI iri) {
        return (StreamBuilder)super.writeBase(iri);
    }

    @Override
    public StreamBuilder writeBase(String iri) {
        return (StreamBuilder)super.writeBase(iri);
    }

    @Override
    public StreamBuilder writeCategory(String term, String scheme, String label) {
        return (StreamBuilder)super.writeCategory(term, scheme, label);
    }

    @Override
    public StreamBuilder writeCategory(String term, String scheme) {
        return (StreamBuilder)super.writeCategory(term, scheme);
    }

    @Override
    public StreamBuilder writeCategory(String term) {
        return (StreamBuilder)super.writeCategory(term);
    }

    @Override
    public StreamBuilder writeContent(String type, String value) {
        return (StreamBuilder)super.writeContent(type, value);
    }

    @Override
    public StreamBuilder writeContent(Content.Type type, DataHandler value) throws IOException {
        return (StreamBuilder)super.writeContent(type, value);
    }

    @Override
    public StreamBuilder writeContent(Content.Type type, InputStream value) throws IOException {
        return (StreamBuilder)super.writeContent(type, value);
    }

    @Override
    public StreamBuilder writeContent(Content.Type type, String value) {
        return (StreamBuilder)super.writeContent(type, value);
    }

    @Override
    public StreamBuilder writeContributor(String name, String email, String uri) {
        return (StreamBuilder)super.writeContributor(name, email, uri);
    }

    @Override
    public StreamBuilder writeContributor(String name) {
        return (StreamBuilder)super.writeContributor(name);
    }

    @Override
    public StreamBuilder writeDate(QName qname, Date date) {
        return (StreamBuilder)super.writeDate(qname, date);
    }

    @Override
    public StreamBuilder writeDate(QName qname, String date) {
        return (StreamBuilder)super.writeDate(qname, date);
    }

    @Override
    public StreamBuilder writeDate(String name, Date date) {
        return (StreamBuilder)super.writeDate(name, date);
    }

    @Override
    public StreamBuilder writeDate(String name, String namespace, Date date) {
        return (StreamBuilder)super.writeDate(name, namespace, date);
    }

    @Override
    public StreamBuilder writeDate(String name, String namespace, String prefix, Date date) {
        return (StreamBuilder)super.writeDate(name, namespace, prefix, date);
    }

    @Override
    public StreamBuilder writeDate(String name, String namespace, String prefix, String date) {
        return (StreamBuilder)super.writeDate(name, namespace, prefix, date);
    }

    @Override
    public StreamBuilder writeDate(String name, String namespace, String date) {
        return (StreamBuilder)super.writeDate(name, namespace, date);
    }

    @Override
    public StreamBuilder writeDate(String name, String date) {
        return (StreamBuilder)super.writeDate(name, date);
    }

    @Override
    public StreamBuilder writeDraft(boolean draft) {
        return (StreamBuilder)super.writeDraft(draft);
    }

    @Override
    public StreamBuilder writeEdited(Date date) {
        return (StreamBuilder)super.writeEdited(date);
    }

    @Override
    public StreamBuilder writeEdited(String date) {
        return (StreamBuilder)super.writeEdited(date);
    }

    @Override
    public StreamBuilder writeElementText(DataHandler value) throws IOException {
        return (StreamBuilder)super.writeElementText(value);
    }

    @Override
    public StreamBuilder writeElementText(Date value) {
        return (StreamBuilder)super.writeElementText(value);
    }

    @Override
    public StreamBuilder writeElementText(double value) {
        return (StreamBuilder)super.writeElementText(value);
    }

    @Override
    public StreamBuilder writeElementText(InputStream value) throws IOException {
        return (StreamBuilder)super.writeElementText(value);
    }

    @Override
    public StreamBuilder writeElementText(int value) {
        return (StreamBuilder)super.writeElementText(value);
    }

    @Override
    public StreamBuilder writeElementText(long value) {
        return (StreamBuilder)super.writeElementText(value);
    }

    @Override
    public StreamBuilder writeElementText(String format, Object ... args) {
        return (StreamBuilder)super.writeElementText(format, args);
    }

    @Override
    public StreamBuilder writeGenerator(String version, String uri, String value) {
        return (StreamBuilder)super.writeGenerator(version, uri, value);
    }

    @Override
    public StreamBuilder writeIcon(IRI iri) {
        return (StreamBuilder)super.writeIcon(iri);
    }

    @Override
    public StreamBuilder writeIcon(String iri) {
        return (StreamBuilder)super.writeIcon(iri);
    }

    @Override
    public StreamBuilder writeId(IRI iri) {
        return (StreamBuilder)super.writeId(iri);
    }

    @Override
    public StreamBuilder writeId(String iri) {
        return (StreamBuilder)super.writeId(iri);
    }

    @Override
    public StreamBuilder writeIRIElement(QName qname, IRI iri) {
        return (StreamBuilder)super.writeIRIElement(qname, iri);
    }

    @Override
    public StreamBuilder writeIRIElement(QName qname, String iri) {
        return (StreamBuilder)super.writeIRIElement(qname, iri);
    }

    @Override
    public StreamBuilder writeIRIElement(String name, IRI iri) {
        return (StreamBuilder)super.writeIRIElement(name, iri);
    }

    @Override
    public StreamBuilder writeIRIElement(String name, String namespace, IRI iri) {
        return (StreamBuilder)super.writeIRIElement(name, namespace, iri);
    }

    @Override
    public StreamBuilder writeIRIElement(String name, String namespace, String prefix, IRI iri) {
        return (StreamBuilder)super.writeIRIElement(name, namespace, prefix, iri);
    }

    @Override
    public StreamBuilder writeIRIElement(String name, String namespace, String prefix, String iri) {
        return (StreamBuilder)super.writeIRIElement(name, namespace, prefix, iri);
    }

    @Override
    public StreamBuilder writeIRIElement(String name, String namespace, String iri) {
        return (StreamBuilder)super.writeIRIElement(name, namespace, iri);
    }

    @Override
    public StreamBuilder writeIRIElement(String name, String iri) {
        return (StreamBuilder)super.writeIRIElement(name, iri);
    }

    @Override
    public StreamBuilder writeLanguage(Lang lang) {
        return (StreamBuilder)super.writeLanguage(lang);
    }

    @Override
    public StreamBuilder writeLanguage(Locale locale) {
        return (StreamBuilder)super.writeLanguage(locale);
    }

    @Override
    public StreamBuilder writeLanguage(String lang) {
        return (StreamBuilder)super.writeLanguage(lang);
    }

    @Override
    public StreamBuilder writeLink(String iri, String rel, String type, String title, String hreflang, long length) {
        return (StreamBuilder)super.writeLink(iri, rel, type, title, hreflang, length);
    }

    @Override
    public StreamBuilder writeLink(String iri, String rel, String type) {
        return (StreamBuilder)super.writeLink(iri, rel, type);
    }

    @Override
    public StreamBuilder writeLink(String iri, String rel) {
        return (StreamBuilder)super.writeLink(iri, rel);
    }

    @Override
    public StreamBuilder writeLink(String iri) {
        return (StreamBuilder)super.writeLink(iri);
    }

    @Override
    public StreamBuilder writeLogo(IRI iri) {
        return (StreamBuilder)super.writeLogo(iri);
    }

    @Override
    public StreamBuilder writeLogo(String iri) {
        return (StreamBuilder)super.writeLogo(iri);
    }

    @Override
    public StreamBuilder writePerson(QName qname, String name, String email, String uri) {
        return (StreamBuilder)super.writePerson(qname, name, email, uri);
    }

    @Override
    public StreamBuilder writePerson(String localname, String namespace, String prefix, String name, String email, String uri) {
        return (StreamBuilder)super.writePerson(localname, namespace, prefix, name, email, uri);
    }

    @Override
    public StreamBuilder writePerson(String localname, String namespace, String name, String email, String uri) {
        return (StreamBuilder)super.writePerson(localname, namespace, name, email, uri);
    }

    @Override
    public StreamBuilder writePerson(String localname, String name, String email, String uri) {
        return (StreamBuilder)super.writePerson(localname, name, email, uri);
    }

    @Override
    public StreamBuilder writePersonEmail(String email) {
        return (StreamBuilder)super.writePersonEmail(email);
    }

    @Override
    public StreamBuilder writePersonName(String name) {
        return (StreamBuilder)super.writePersonName(name);
    }

    @Override
    public StreamBuilder writePersonUri(String uri) {
        return (StreamBuilder)super.writePersonUri(uri);
    }

    @Override
    public StreamBuilder writePublished(Date date) {
        return (StreamBuilder)super.writePublished(date);
    }

    @Override
    public StreamBuilder writePublished(String date) {
        return (StreamBuilder)super.writePublished(date);
    }

    @Override
    public StreamBuilder writeRights(String value) {
        return (StreamBuilder)super.writeRights(value);
    }

    @Override
    public StreamBuilder writeRights(Text.Type type, String value) {
        return (StreamBuilder)super.writeRights(type, value);
    }

    @Override
    public StreamBuilder writeSubtitle(String value) {
        return (StreamBuilder)super.writeSubtitle(value);
    }

    @Override
    public StreamBuilder writeSubtitle(Text.Type type, String value) {
        return (StreamBuilder)super.writeSubtitle(type, value);
    }

    @Override
    public StreamBuilder writeSummary(String value) {
        return (StreamBuilder)super.writeSummary(value);
    }

    @Override
    public StreamBuilder writeSummary(Text.Type type, String value) {
        return (StreamBuilder)super.writeSummary(type, value);
    }

    @Override
    public StreamBuilder writeText(QName qname, Text.Type type, String value) {
        return (StreamBuilder)super.writeText(qname, type, value);
    }

    @Override
    public StreamBuilder writeText(String name, String namespace, String prefix, Text.Type type, String value) {
        return (StreamBuilder)super.writeText(name, namespace, prefix, type, value);
    }

    @Override
    public StreamBuilder writeText(String name, String namespace, Text.Type type, String value) {
        return (StreamBuilder)super.writeText(name, namespace, type, value);
    }

    @Override
    public StreamBuilder writeText(String name, Text.Type type, String value) {
        return (StreamBuilder)super.writeText(name, type, value);
    }

    @Override
    public StreamBuilder writeTitle(String value) {
        return (StreamBuilder)super.writeTitle(value);
    }

    @Override
    public StreamBuilder writeTitle(Text.Type type, String value) {
        return (StreamBuilder)super.writeTitle(type, value);
    }

    @Override
    public StreamBuilder writeUpdated(Date date) {
        return (StreamBuilder)super.writeUpdated(date);
    }

    @Override
    public StreamBuilder writeUpdated(String date) {
        return (StreamBuilder)super.writeUpdated(date);
    }

    @Override
    public StreamBuilder setPrefix(String prefix, String uri) {
        if (!(this.current instanceof Element)) {
            throw new IllegalStateException("Not currently an element");
        }
        ((Element)this.current).declareNS(uri, prefix);
        return this;
    }

    @Override
    public StreamBuilder writeNamespace(String prefix, String uri) {
        return this.setPrefix(prefix, uri);
    }
}


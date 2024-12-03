/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.activation.DataHandler
 */
package org.apache.abdera.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.channels.Channels;
import java.nio.channels.WritableByteChannel;
import java.util.Date;
import java.util.Formatter;
import java.util.Locale;
import javax.activation.DataHandler;
import javax.xml.namespace.QName;
import org.apache.abdera.Abdera;
import org.apache.abdera.i18n.iri.IRI;
import org.apache.abdera.i18n.rfc4646.Lang;
import org.apache.abdera.model.AtomDate;
import org.apache.abdera.model.Content;
import org.apache.abdera.model.Text;
import org.apache.abdera.util.Constants;
import org.apache.abdera.writer.StreamWriter;
import org.apache.commons.codec.binary.Base64;

public abstract class AbstractStreamWriter
implements StreamWriter {
    protected final Abdera abdera;
    protected final String name;
    protected boolean autoflush = false;
    protected boolean autoclose = false;
    protected boolean autoindent = false;

    protected AbstractStreamWriter(Abdera abdera, String name) {
        this.abdera = abdera;
        this.name = name;
    }

    public StreamWriter setAutoflush(boolean auto) {
        this.autoflush = auto;
        return this;
    }

    public StreamWriter setAutoclose(boolean auto) {
        this.autoclose = auto;
        return this;
    }

    public StreamWriter setChannel(WritableByteChannel channel) {
        return this.setOutputStream(Channels.newOutputStream(channel));
    }

    public StreamWriter setChannel(WritableByteChannel channel, String charset) {
        return this.setWriter(Channels.newWriter(channel, charset));
    }

    public String getName() {
        return this.name;
    }

    public StreamWriter startDocument() {
        return this.startDocument("1.0");
    }

    public StreamWriter endDocument() {
        return this;
    }

    public StreamWriter startFeed() {
        return this.startElement(Constants.FEED);
    }

    public StreamWriter endFeed() {
        return this.endElement();
    }

    public StreamWriter startEntry() {
        return this.startElement(Constants.ENTRY);
    }

    public StreamWriter endEntry() {
        return this.endElement();
    }

    public StreamWriter endCategory() {
        return this.endElement();
    }

    public StreamWriter endContent() {
        return this.endElement();
    }

    public StreamWriter endLink() {
        return this.endElement();
    }

    public StreamWriter endPerson() {
        return this.endElement();
    }

    public StreamWriter endSource() {
        return this.endElement();
    }

    public StreamWriter endText() {
        return this.endElement();
    }

    public StreamWriter startLink(String iri, String rel, String type, String title, String hreflang, long length) {
        return this.startElement(Constants.LINK).writeAttribute("href", iri).writeAttribute("rel", rel).writeAttribute("type", type).writeAttribute("title", title).writeAttribute("hreflang", hreflang).writeAttribute("length", length > -1L ? String.valueOf(length) : null);
    }

    public StreamWriter startPerson(QName qname) {
        return this.startElement(qname);
    }

    public StreamWriter startSource() {
        return this.startElement(Constants.SOURCE);
    }

    public StreamWriter startText(QName qname, Text.Type type) {
        return this.startElement(qname).writeAttribute("type", type != null ? type.name().toLowerCase() : "text");
    }

    public StreamWriter writeDate(QName qname, String date) {
        return this.startElement(qname).writeElementText(date).endElement();
    }

    public StreamWriter writeIRIElement(QName qname, String iri) {
        return this.startElement(qname).writeElementText(iri).endElement();
    }

    public StreamWriter writePersonEmail(String email) {
        if (email == null) {
            return this;
        }
        return this.startElement(Constants.EMAIL).writeElementText(email).endElement();
    }

    public StreamWriter writePersonName(String name) {
        if (name == null) {
            return this;
        }
        return this.startElement(Constants.NAME).writeElementText(name).endElement();
    }

    public StreamWriter writePersonUri(String uri) {
        if (uri == null) {
            return this;
        }
        return this.startElement(Constants.URI).writeElementText(uri).endElement();
    }

    public StreamWriter startContent(Content.Type type, String src) {
        return this.startContent(type.name().toLowerCase(), src);
    }

    public StreamWriter startContent(String type, String src) {
        return this.startElement(Constants.CONTENT).writeAttribute("type", type).writeAttribute("src", src);
    }

    public StreamWriter startContent(Content.Type type) {
        return this.startContent(type, null);
    }

    public StreamWriter startContent(String type) {
        return this.startContent(type, null);
    }

    public StreamWriter startLink(String iri) {
        return this.startLink(iri, null, null, null, null, -1L);
    }

    public StreamWriter startLink(String iri, String rel) {
        return this.startLink(iri, rel, null, null, null, -1L);
    }

    public StreamWriter startLink(String iri, String rel, String type) {
        return this.startLink(iri, rel, type, null, null, -1L);
    }

    public StreamWriter writeCategory(String term) {
        return this.writeCategory(term, null, null);
    }

    public StreamWriter writeCategory(String term, String scheme) {
        return this.writeCategory(term, scheme, null);
    }

    public StreamWriter writeCategory(String term, String scheme, String label) {
        return this.startElement(Constants.CATEGORY).writeAttribute("term", term).writeAttribute("scheme", scheme).writeAttribute("label", label).endElement();
    }

    public StreamWriter writeContent(Content.Type type, String value) {
        return this.startContent(type).writeElementText(value).endContent();
    }

    public StreamWriter writeContent(Content.Type type, InputStream value) throws IOException {
        return this.startContent(type).writeElementText(value).endContent();
    }

    public StreamWriter writeContent(Content.Type type, DataHandler value) throws IOException {
        return this.startContent(type).writeElementText(value).endContent();
    }

    public StreamWriter writeContent(String type, String value) {
        return this.startContent(type).writeElementText(value).endContent();
    }

    public StreamWriter writeEdited(Date date) {
        this.writeDate(Constants.EDITED, date);
        return this;
    }

    public StreamWriter writeId(String iri) {
        return this.writeIRIElement(Constants.ID, iri);
    }

    public StreamWriter writeIcon(String iri) {
        return this.writeIRIElement(Constants.ICON, iri);
    }

    public StreamWriter writeLogo(String iri) {
        return this.writeIRIElement(Constants.LOGO, iri);
    }

    public StreamWriter writeLink(String iri) {
        return this.writeLink(iri, null, null, null, null, -1L);
    }

    public StreamWriter writeLink(String iri, String rel) {
        return this.writeLink(iri, rel, null, null, null, -1L);
    }

    public StreamWriter writeLink(String iri, String rel, String type) {
        return this.writeLink(iri, rel, type, null, null, -1L);
    }

    public StreamWriter writeLink(String iri, String rel, String type, String title, String hreflang, long length) {
        return this.startLink(iri, rel, type, title, hreflang, length).endLink();
    }

    public StreamWriter writePerson(QName qname, String name, String email, String uri) {
        return this.startPerson(qname).writePersonName(name).writePersonEmail(email).writePersonUri(uri).endPerson();
    }

    public StreamWriter writePublished(Date date) {
        return this.writeDate(Constants.PUBLISHED, date);
    }

    public StreamWriter writeText(QName qname, Text.Type type, String value) {
        return this.startText(qname, type).writeElementText(value).endElement();
    }

    public StreamWriter writeUpdated(Date date) {
        return this.writeDate(Constants.UPDATED, date);
    }

    public StreamWriter writeUpdated(String date) {
        return this.writeDate(Constants.UPDATED, date);
    }

    public StreamWriter writePublished(String date) {
        return this.writeDate(Constants.PUBLISHED, date);
    }

    public StreamWriter writeEdited(String date) {
        return this.writeDate(Constants.EDITED, date);
    }

    public StreamWriter writeDate(QName qname, Date date) {
        return this.writeDate(qname, AtomDate.format(date));
    }

    public StreamWriter writeId(IRI iri) {
        return this.writeIRIElement(Constants.ID, iri);
    }

    public StreamWriter writeIcon(IRI iri) {
        return this.writeIRIElement(Constants.ICON, iri);
    }

    public StreamWriter writeLogo(IRI iri) {
        return this.writeIRIElement(Constants.LOGO, iri);
    }

    public StreamWriter writeIRIElement(QName qname, IRI iri) {
        return this.writeIRIElement(qname, iri.toString());
    }

    public StreamWriter writeElementText(InputStream value) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        byte[] buf = new byte[1024];
        int r = -1;
        while ((r = value.read(buf)) != -1) {
            out.write(buf, 0, r);
        }
        byte[] data = out.toByteArray();
        this.writeElementText(new String(Base64.encodeBase64(data), "UTF-8"));
        return this;
    }

    public StreamWriter writeElementText(DataHandler value) throws IOException {
        this.writeElementText(value.getInputStream());
        return this;
    }

    public StreamWriter writeTitle(String value) {
        return this.writeText(Constants.TITLE, Text.Type.TEXT, value);
    }

    public StreamWriter writeTitle(Text.Type type, String value) {
        return this.writeText(Constants.TITLE, type, value);
    }

    public StreamWriter writeSubtitle(String value) {
        return this.writeText(Constants.SUBTITLE, Text.Type.TEXT, value);
    }

    public StreamWriter writeSubtitle(Text.Type type, String value) {
        return this.writeText(Constants.SUBTITLE, type, value);
    }

    public StreamWriter writeSummary(String value) {
        return this.writeText(Constants.SUMMARY, Text.Type.TEXT, value);
    }

    public StreamWriter writeSummary(Text.Type type, String value) {
        return this.writeText(Constants.SUMMARY, type, value);
    }

    public StreamWriter writeRights(String value) {
        return this.writeText(Constants.RIGHTS, Text.Type.TEXT, value);
    }

    public StreamWriter writeRights(Text.Type type, String value) {
        return this.writeText(Constants.RIGHTS, type, value);
    }

    public StreamWriter writeAuthor(String name, String email, String uri) {
        return this.writePerson(Constants.AUTHOR, name, email, uri);
    }

    public StreamWriter writeAuthor(String name) {
        return this.writeAuthor(name, null, null);
    }

    public StreamWriter startAuthor() {
        return this.startElement(Constants.AUTHOR);
    }

    public StreamWriter endAuthor() {
        return this.endElement();
    }

    public StreamWriter writeContributor(String name, String email, String uri) {
        return this.writePerson(Constants.CONTRIBUTOR, name, email, uri);
    }

    public StreamWriter writeContributor(String name) {
        return this.writeContributor(name, null, null);
    }

    public StreamWriter startContributor() {
        return this.startElement(Constants.CONTRIBUTOR);
    }

    public StreamWriter endContributor() {
        return this.endElement();
    }

    public StreamWriter writeGenerator(String version, String uri, String value) {
        return this.startElement(Constants.GENERATOR).writeAttribute("version", version).writeAttribute("uri", uri).writeElementText(value).endElement();
    }

    public StreamWriter startGenerator(String version, String uri) {
        return this.startElement(Constants.GENERATOR).writeAttribute("version", version).writeAttribute("uri", uri);
    }

    public StreamWriter endGenerator() {
        return this.endElement();
    }

    public StreamWriter startCategory(String term) {
        return this.startCategory(term, null, null);
    }

    public StreamWriter startCategory(String term, String scheme) {
        return this.startCategory(term, scheme, null);
    }

    public StreamWriter startCategory(String term, String scheme, String label) {
        return this.startElement(Constants.CATEGORY).writeAttribute("term", term).writeAttribute("scheme", scheme).writeAttribute("label", label);
    }

    public StreamWriter startService() {
        return this.startElement(Constants.SERVICE);
    }

    public StreamWriter endService() {
        return this.endElement();
    }

    public StreamWriter startWorkspace() {
        return this.startElement(Constants.WORKSPACE);
    }

    public StreamWriter endWorkspace() {
        return this.endElement();
    }

    public StreamWriter startCollection(String href) {
        return this.startElement(Constants.COLLECTION).writeAttribute("href", href);
    }

    public StreamWriter endCollection() {
        this.endElement();
        return this;
    }

    public StreamWriter writeAccepts(String ... accepts) {
        for (String accept : accepts) {
            this.startElement(Constants.ACCEPT).writeElementText(accept).endElement();
        }
        return this;
    }

    public StreamWriter writeAcceptsEntry() {
        return this.writeAccepts("application/atom+xml;type=entry");
    }

    public StreamWriter writeAcceptsNothing() {
        return this.writeAccepts("");
    }

    public StreamWriter startCategories() {
        return this.startCategories(false, null);
    }

    public StreamWriter startCategories(boolean fixed) {
        return this.startCategories(fixed, null);
    }

    public StreamWriter startCategories(boolean fixed, String scheme) {
        this.startElement(Constants.CATEGORIES);
        if (fixed) {
            this.writeAttribute("fixed", "yes");
        }
        if (scheme != null && scheme.length() > 0) {
            this.writeAttribute("scheme", scheme);
        }
        return this;
    }

    public StreamWriter endCategories() {
        return this.endElement();
    }

    public StreamWriter startControl() {
        return this.startElement(Constants.CONTROL);
    }

    public StreamWriter endControl() {
        return this.endElement();
    }

    public StreamWriter writeDraft(boolean draft) {
        return this.startElement(Constants.DRAFT).writeElementText(draft ? "yes" : "no").endElement();
    }

    public StreamWriter writeAttribute(String name, String value) {
        if (value == null) {
            return this;
        }
        return this.writeAttribute(name, null, null, value);
    }

    public StreamWriter writeAttribute(String name, String namespace, String value) {
        if (value == null) {
            return this;
        }
        return this.writeAttribute(name, namespace, null, value);
    }

    public StreamWriter writeAttribute(QName qname, String value) {
        if (value == null) {
            return this;
        }
        return this.writeAttribute(qname.getLocalPart(), qname.getNamespaceURI(), qname.getPrefix(), value);
    }

    public StreamWriter startElement(QName qname) {
        return this.startElement(qname.getLocalPart(), qname.getNamespaceURI(), qname.getPrefix());
    }

    public StreamWriter startElement(String name) {
        return this.startElement(name, null, null);
    }

    public StreamWriter startElement(String name, String namespace) {
        return this.startElement(name, namespace, null);
    }

    public StreamWriter setAutoIndent(boolean indent) {
        this.autoindent = indent;
        return this;
    }

    public StreamWriter writeAttribute(QName qname, Date value) {
        return this.writeAttribute(qname.getLocalPart(), qname.getNamespaceURI(), qname.getPrefix(), value);
    }

    public StreamWriter writeAttribute(String name, Date value) {
        return this.writeAttribute(name, null, null, value);
    }

    public StreamWriter writeAttribute(String name, String namespace, Date value) {
        return this.writeAttribute(name, namespace, null, value);
    }

    public StreamWriter writeAttribute(String name, String namespace, String prefix, Date value) {
        return this.writeAttribute(name, namespace, prefix, AtomDate.format(value));
    }

    public StreamWriter writeAttribute(QName qname, int value) {
        return this.writeAttribute(qname.getLocalPart(), qname.getNamespaceURI(), qname.getPrefix(), value);
    }

    public StreamWriter writeAttribute(String name, int value) {
        return this.writeAttribute(name, (String)null, (String)null, value);
    }

    public StreamWriter writeAttribute(String name, String namespace, int value) {
        return this.writeAttribute(name, namespace, (String)null, value);
    }

    public StreamWriter writeAttribute(String name, String namespace, String prefix, int value) {
        return this.writeAttribute(name, namespace, prefix, Integer.toString(value));
    }

    public StreamWriter writeAttribute(QName qname, long value) {
        return this.writeAttribute(qname.getLocalPart(), qname.getNamespaceURI(), qname.getPrefix(), value);
    }

    public StreamWriter writeAttribute(String name, long value) {
        return this.writeAttribute(name, (String)null, (String)null, value);
    }

    public StreamWriter writeAttribute(String name, String namespace, long value) {
        return this.writeAttribute(name, namespace, (String)null, value);
    }

    public StreamWriter writeAttribute(String name, String namespace, String prefix, long value) {
        return this.writeAttribute(name, namespace, prefix, Long.toString(value));
    }

    public StreamWriter writeAttribute(QName qname, double value) {
        return this.writeAttribute(qname.getLocalPart(), qname.getNamespaceURI(), qname.getPrefix(), value);
    }

    public StreamWriter writeAttribute(String name, double value) {
        return this.writeAttribute(name, null, null, value);
    }

    public StreamWriter writeAttribute(String name, String namespace, double value) {
        return this.writeAttribute(name, namespace, null, value);
    }

    public StreamWriter writeAttribute(String name, String namespace, String prefix, double value) {
        return this.writeAttribute(name, namespace, prefix, Double.toString(value));
    }

    public StreamWriter writeElementText(Date value) {
        return this.writeElementText(AtomDate.format(value));
    }

    public StreamWriter writeElementText(int value) {
        return this.writeElementText(Integer.toString(value));
    }

    public StreamWriter writeElementText(long value) {
        return this.writeElementText(Long.toString(value));
    }

    public StreamWriter writeElementText(double value) {
        return this.writeElementText(Double.toString(value));
    }

    public StreamWriter writeBase(String iri) {
        return this.writeAttribute(Constants.BASE, iri);
    }

    public StreamWriter writeBase(IRI iri) {
        return this.writeBase(iri.toString());
    }

    public StreamWriter writeLanguage(String lang) {
        return this.writeAttribute(Constants.LANG, lang);
    }

    public StreamWriter writeLanguage(Lang lang) {
        return this.writeLanguage(lang.toString());
    }

    public StreamWriter writeLanguage(Locale locale) {
        return this.writeLanguage(new Lang(locale));
    }

    public StreamWriter writeIRIElement(String name, IRI iri) {
        return this.startElement(name).writeElementText(iri.toString()).endElement();
    }

    public StreamWriter writeIRIElement(String name, String namespace, IRI iri) {
        return this.startElement(name, namespace).writeElementText(iri.toString()).endElement();
    }

    public StreamWriter writeIRIElement(String name, String namespace, String prefix, IRI iri) {
        return this.startElement(name, namespace, prefix).writeElementText(iri.toString()).endElement();
    }

    public StreamWriter writeIRIElement(String name, String namespace, String prefix, String iri) {
        return this.startElement(name, namespace, prefix).writeElementText(iri).endElement();
    }

    public StreamWriter writeIRIElement(String name, String namespace, String iri) {
        return this.startElement(name, namespace).writeElementText(iri).endElement();
    }

    public StreamWriter writeIRIElement(String name, String iri) {
        return this.startElement(name).writeElementText(iri).endElement();
    }

    public StreamWriter writeDate(String name, Date date) {
        return this.startElement(name).writeElementText(date).endElement();
    }

    public StreamWriter writeDate(String name, String namespace, Date date) {
        return this.startElement(name, namespace).writeElementText(date).endElement();
    }

    public StreamWriter writeDate(String name, String namespace, String prefix, Date date) {
        return this.startElement(name, namespace, prefix).writeElementText(date).endElement();
    }

    public StreamWriter writeDate(String name, String date) {
        return this.startElement(name).writeElementText(date).endElement();
    }

    public StreamWriter writeDate(String name, String namespace, String date) {
        return this.startElement(name, namespace).writeElementText(date).endElement();
    }

    public StreamWriter writeDate(String name, String namespace, String prefix, String date) {
        return this.startElement(name, namespace, prefix).writeElementText(date).endElement();
    }

    public StreamWriter startText(String name, String namespace, String prefix, Text.Type type) {
        return this.startElement(name, namespace, prefix).writeAttribute("type", type != null ? type.name().toLowerCase() : "text");
    }

    public StreamWriter startText(String name, String namespace, Text.Type type) {
        return this.startElement(name, namespace).writeAttribute("type", type != null ? type.name().toLowerCase() : "text");
    }

    public StreamWriter startText(String name, Text.Type type) {
        return this.startElement(name).writeAttribute("type", type != null ? type.name().toLowerCase() : "text");
    }

    public StreamWriter writeText(String name, String namespace, String prefix, Text.Type type, String value) {
        return this.startText(name, namespace, prefix, type).writeElementText(value).endElement();
    }

    public StreamWriter writeText(String name, String namespace, Text.Type type, String value) {
        return this.startText(name, namespace, type).writeElementText(value).endElement();
    }

    public StreamWriter writeText(String name, Text.Type type, String value) {
        return this.startText(name, type).writeElementText(value).endElement();
    }

    public StreamWriter startPerson(String name, String namespace, String prefix) {
        return this.startElement(name, namespace, prefix);
    }

    public StreamWriter startPerson(String name, String namespace) {
        return this.startElement(name, namespace);
    }

    public StreamWriter startPerson(String name) {
        return this.startElement(name);
    }

    public StreamWriter writePerson(String localname, String namespace, String prefix, String name, String email, String uri) {
        return this.startPerson(localname, namespace, prefix).writePersonName(name).writePersonEmail(email).writePersonUri(uri).endPerson();
    }

    public StreamWriter writePerson(String localname, String namespace, String name, String email, String uri) {
        return this.startPerson(localname, namespace).writePersonName(name).writePersonEmail(email).writePersonUri(uri).endPerson();
    }

    public StreamWriter writePerson(String localname, String name, String email, String uri) {
        return this.startPerson(localname).writePersonName(name).writePersonEmail(email).writePersonUri(uri).endPerson();
    }

    public Appendable append(char c) throws IOException {
        return this.writeElementText(String.valueOf(c));
    }

    public Appendable append(CharSequence csq, int start, int end) throws IOException {
        return this.append(csq.subSequence(start, end));
    }

    public Appendable append(CharSequence csq) throws IOException {
        return this.writeElementText(((Object)csq).toString());
    }

    public StreamWriter writeElementText(String format, Object ... args) {
        new Formatter(this).format(format, args);
        return this;
    }
}


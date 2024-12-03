/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.activation.MimeType
 *  javax.activation.MimeTypeParseException
 */
package org.apache.abdera.parser.stax;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import javax.activation.MimeType;
import javax.activation.MimeTypeParseException;
import javax.xml.stream.XMLStreamException;
import org.apache.abdera.factory.Factory;
import org.apache.abdera.i18n.iri.IRI;
import org.apache.abdera.i18n.rfc4646.Lang;
import org.apache.abdera.model.Base;
import org.apache.abdera.model.Document;
import org.apache.abdera.model.Element;
import org.apache.abdera.model.ElementWrapper;
import org.apache.abdera.parser.stax.FOMException;
import org.apache.abdera.parser.stax.FOMFactory;
import org.apache.abdera.parser.stax.FOMWriter;
import org.apache.abdera.util.EntityTag;
import org.apache.abdera.util.XmlUtil;
import org.apache.abdera.writer.Writer;
import org.apache.abdera.writer.WriterOptions;
import org.apache.axiom.om.OMComment;
import org.apache.axiom.om.OMDocument;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.OMNode;
import org.apache.axiom.om.OMOutputFormat;
import org.apache.axiom.om.OMProcessingInstruction;
import org.apache.axiom.om.OMXMLParserWrapper;
import org.apache.axiom.om.impl.MTOMXMLStreamWriter;
import org.apache.axiom.om.impl.llom.OMDocumentImpl;
import org.apache.axiom.om.util.StAXUtils;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class FOMDocument<T extends Element>
extends OMDocumentImpl
implements Document<T> {
    private static final long serialVersionUID = -3255339511063344662L;
    protected IRI base = null;
    protected MimeType contentType = null;
    protected Date lastModified = null;
    protected EntityTag etag = null;
    protected String language = null;
    protected String slug = null;
    protected boolean preserve = true;

    public FOMDocument() {
        super(new FOMFactory());
    }

    protected FOMDocument(OMFactory factory) {
        super(factory);
    }

    protected FOMDocument(OMXMLParserWrapper parserWrapper, OMFactory factory) {
        super(parserWrapper, factory);
    }

    protected FOMDocument(OMXMLParserWrapper parserWrapper) {
        super(parserWrapper, new FOMFactory());
    }

    @Override
    public T getRoot() {
        FOMFactory factory = (FOMFactory)this.getFactory();
        return factory.getElementWrapper((Element)((Object)this.getOMDocumentElement()));
    }

    @Override
    public Document<T> setRoot(T root) {
        if (root instanceof OMElement) {
            this.setOMDocumentElement((OMElement)root);
        } else if (root instanceof ElementWrapper) {
            this.setOMDocumentElement((OMElement)((Object)((ElementWrapper)root).getInternal()));
        }
        return this;
    }

    @Override
    public IRI getBaseUri() {
        return this.base;
    }

    @Override
    public Document<T> setBaseUri(String base) {
        this.base = new IRI(base);
        return this;
    }

    @Override
    public void writeTo(OutputStream out, WriterOptions options) throws IOException {
        Writer writer = this.getFactory().getAbdera().getWriter();
        writer.writeTo((Base)this, out, options);
    }

    @Override
    public void writeTo(java.io.Writer out, WriterOptions options) throws IOException {
        Writer writer = this.getFactory().getAbdera().getWriter();
        writer.writeTo((Base)this, out, options);
    }

    @Override
    public void writeTo(Writer writer, OutputStream out) throws IOException {
        writer.writeTo((Base)this, out);
    }

    @Override
    public void writeTo(Writer writer, java.io.Writer out) throws IOException {
        writer.writeTo((Base)this, out);
    }

    @Override
    public void writeTo(Writer writer, OutputStream out, WriterOptions options) throws IOException {
        writer.writeTo((Base)this, out, options);
    }

    @Override
    public void writeTo(Writer writer, java.io.Writer out, WriterOptions options) throws IOException {
        writer.writeTo((Base)this, out, options);
    }

    @Override
    public void writeTo(OutputStream out) throws IOException {
        String charset = this.getCharset();
        if (charset == null) {
            charset = "UTF-8";
        }
        Writer writer = this.getFactory().getAbdera().getWriter();
        this.writeTo(writer, (java.io.Writer)new OutputStreamWriter(out, charset));
    }

    @Override
    public void writeTo(java.io.Writer writer) throws IOException {
        Writer out = this.getFactory().getAbdera().getWriter();
        if (!(out instanceof FOMWriter)) {
            out.writeTo((Base)this, writer);
        } else {
            try {
                OMOutputFormat outputFormat = new OMOutputFormat();
                if (this.getCharsetEncoding() != null) {
                    outputFormat.setCharSetEncoding(this.getCharsetEncoding());
                }
                MTOMXMLStreamWriter omwriter = new MTOMXMLStreamWriter(StAXUtils.createXMLStreamWriter(writer));
                omwriter.setOutputFormat(outputFormat);
                this.internalSerialize(omwriter);
                omwriter.flush();
            }
            catch (XMLStreamException e) {
                throw new FOMException(e);
            }
        }
    }

    @Override
    public MimeType getContentType() {
        return this.contentType;
    }

    @Override
    public Document<T> setContentType(String contentType) {
        try {
            this.contentType = new MimeType(contentType);
            if (this.contentType.getParameter("charset") != null) {
                this.setCharset(this.contentType.getParameter("charset"));
            }
        }
        catch (MimeTypeParseException e) {
            throw new org.apache.abdera.util.MimeTypeParseException(e);
        }
        return this;
    }

    @Override
    public Date getLastModified() {
        return this.lastModified;
    }

    @Override
    public Document<T> setLastModified(Date lastModified) {
        this.lastModified = lastModified;
        return this;
    }

    @Override
    public Object clone() {
        Document doc = ((FOMFactory)this.factory).newDocument();
        OMDocument omdoc = (OMDocument)((Object)doc);
        Iterator i = this.getChildren();
        while (i.hasNext()) {
            OMNode node = (OMNode)i.next();
            switch (node.getType()) {
                case 5: {
                    OMComment comment = (OMComment)node;
                    this.factory.createOMComment(omdoc, comment.getValue());
                    break;
                }
                case 1: {
                    Element el = (Element)((Object)node);
                    omdoc.addChild((OMNode)el.clone());
                    break;
                }
                case 3: {
                    OMProcessingInstruction pi = (OMProcessingInstruction)node;
                    this.factory.createOMProcessingInstruction(omdoc, pi.getTarget(), pi.getValue());
                }
            }
        }
        return doc;
    }

    @Override
    public String getCharset() {
        return this.getCharsetEncoding();
    }

    @Override
    public Document<T> setCharset(String charset) {
        this.setCharsetEncoding(charset);
        return this;
    }

    @Override
    public Factory getFactory() {
        return (Factory)((Object)this.factory);
    }

    @Override
    public String[] getProcessingInstruction(String target) {
        ArrayList<String> values = new ArrayList<String>();
        Iterator i = this.getChildren();
        while (i.hasNext()) {
            OMProcessingInstruction pi;
            OMNode node = (OMNode)i.next();
            if (node.getType() != 3 || !(pi = (OMProcessingInstruction)node).getTarget().equalsIgnoreCase(target)) continue;
            values.add(pi.getValue());
        }
        return values.toArray(new String[values.size()]);
    }

    @Override
    public Document<T> addProcessingInstruction(String target, String value) {
        OMProcessingInstruction pi = this.factory.createOMProcessingInstruction(null, target, value);
        if (this.getOMDocumentElement() != null) {
            this.getOMDocumentElement().insertSiblingBefore(pi);
        } else {
            this.addChild(pi);
        }
        return this;
    }

    @Override
    public Document<T> addStylesheet(String href, String media) {
        if (media == null) {
            this.addProcessingInstruction("xml-stylesheet", "href=\"" + href + "\"");
        } else {
            this.addProcessingInstruction("xml-stylesheet", "href=\"" + href + "\" media=\"" + media + "\"");
        }
        return this;
    }

    public <X extends Base> X addComment(String value) {
        OMComment comment = this.factory.createOMComment(null, value);
        if (this.getOMDocumentElement() != null) {
            this.getOMDocumentElement().insertSiblingBefore(comment);
        } else {
            this.addChild(comment);
        }
        return (X)this;
    }

    @Override
    public EntityTag getEntityTag() {
        return this.etag;
    }

    @Override
    public Document<T> setEntityTag(EntityTag tag) {
        this.etag = tag;
        return this;
    }

    @Override
    public Document<T> setEntityTag(String tag) {
        this.etag = new EntityTag(tag);
        return this;
    }

    @Override
    public String getLanguage() {
        return this.language;
    }

    @Override
    public Lang getLanguageTag() {
        String lang = this.getLanguage();
        return lang != null ? new Lang(lang) : null;
    }

    @Override
    public Document<T> setLanguage(String lang) {
        this.language = lang;
        return this;
    }

    @Override
    public String getSlug() {
        return this.slug;
    }

    @Override
    public Document<T> setSlug(String slug) {
        this.slug = slug;
        return this;
    }

    @Override
    public boolean getMustPreserveWhitespace() {
        return this.preserve;
    }

    @Override
    public Document<T> setMustPreserveWhitespace(boolean preserve) {
        this.preserve = preserve;
        return this;
    }

    @Override
    public XmlUtil.XMLVersion getXmlVersion() {
        return XmlUtil.getVersion(super.getXMLVersion());
    }

    @Override
    public WriterOptions getDefaultWriterOptions() {
        return new FOMWriter().getDefaultWriterOptions();
    }

    public <X extends Base> X complete() {
        if (!this.isComplete() && this.getRoot() != null) {
            this.getRoot().complete();
        }
        return (X)this;
    }

    @Override
    public void writeTo(String writer, OutputStream out) throws IOException {
        this.writeTo((Writer)this.getFactory().getAbdera().getWriterFactory().getWriter(writer), out);
    }

    @Override
    public void writeTo(String writer, java.io.Writer out) throws IOException {
        this.writeTo((Writer)this.getFactory().getAbdera().getWriterFactory().getWriter(writer), out);
    }

    @Override
    public void writeTo(String writer, OutputStream out, WriterOptions options) throws IOException {
        this.writeTo((Writer)this.getFactory().getAbdera().getWriterFactory().getWriter(writer), out, options);
    }

    @Override
    public void writeTo(String writer, java.io.Writer out, WriterOptions options) throws IOException {
        this.writeTo((Writer)this.getFactory().getAbdera().getWriterFactory().getWriter(writer), out, options);
    }

    public String toFormattedString() {
        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            this.writeTo("prettyxml", (OutputStream)out);
            return new String(out.toByteArray(), "UTF-8");
        }
        catch (Exception e) {
            return this.toString();
        }
    }
}


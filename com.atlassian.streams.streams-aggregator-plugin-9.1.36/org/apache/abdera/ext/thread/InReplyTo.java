/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.activation.MimeType
 *  javax.activation.MimeTypeParseException
 */
package org.apache.abdera.ext.thread;

import javax.activation.MimeType;
import javax.activation.MimeTypeParseException;
import org.apache.abdera.ext.thread.ThreadConstants;
import org.apache.abdera.factory.Factory;
import org.apache.abdera.i18n.iri.IRI;
import org.apache.abdera.model.Element;
import org.apache.abdera.model.ElementWrapper;

public class InReplyTo
extends ElementWrapper {
    public InReplyTo(Element internal) {
        super(internal);
    }

    public InReplyTo(Factory factory) {
        super(factory, ThreadConstants.IN_REPLY_TO);
    }

    public IRI getHref() {
        String href = this.getAttributeValue("href");
        return href != null ? new IRI(href) : null;
    }

    public MimeType getMimeType() {
        try {
            String type = this.getAttributeValue("type");
            return type != null ? new MimeType(type) : null;
        }
        catch (MimeTypeParseException e) {
            throw new org.apache.abdera.util.MimeTypeParseException(e);
        }
    }

    public IRI getRef() {
        String ref = this.getAttributeValue("ref");
        return ref != null ? new IRI(ref) : null;
    }

    public IRI getResolvedHref() {
        IRI href = this.getHref();
        IRI base = this.getBaseUri();
        return base == null ? href : (href != null ? base.resolve(href) : null);
    }

    public IRI getResolvedSource() {
        IRI href = this.getSource();
        IRI base = this.getBaseUri();
        return base == null ? href : (href != null ? base.resolve(href) : null);
    }

    public IRI getSource() {
        String source = this.getAttributeValue("source");
        return source != null ? new IRI(source) : null;
    }

    public void setHref(IRI ref) {
        this.setAttributeValue("href", ref.toString());
    }

    public void setHref(String ref) {
        this.setAttributeValue("href", ref);
    }

    public void setMimeType(MimeType mimeType) {
        this.setAttributeValue("type", mimeType.toString());
    }

    public void setMimeType(String mimeType) {
        this.setAttributeValue("type", mimeType);
    }

    public void setRef(IRI ref) {
        this.setAttributeValue("ref", ref.toString());
    }

    public void setRef(String ref) {
        this.setAttributeValue("ref", ref);
    }

    public void setSource(IRI source) {
        this.setAttributeValue("source", source.toString());
    }

    public void setSource(String source) {
        this.setAttributeValue("source", source);
    }
}


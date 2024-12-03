/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.activation.MimeType
 *  javax.activation.MimeTypeParseException
 */
package org.apache.abdera.parser.stax;

import java.util.HashMap;
import java.util.Map;
import javax.activation.MimeType;
import javax.xml.namespace.QName;
import org.apache.abdera.i18n.iri.IRI;
import org.apache.abdera.model.Link;
import org.apache.abdera.parser.stax.FOMExtensibleElement;
import org.apache.abdera.util.MimeTypeParseException;
import org.apache.axiom.om.OMContainer;
import org.apache.axiom.om.OMException;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.OMNamespace;
import org.apache.axiom.om.OMXMLParserWrapper;

public class FOMLink
extends FOMExtensibleElement
implements Link {
    private static final long serialVersionUID = 2239772197929910635L;
    private static final Map<String, String> REL_EQUIVS = new HashMap<String, String>();

    protected FOMLink(String name, OMNamespace namespace, OMContainer parent, OMFactory factory) throws OMException {
        super(name, namespace, parent, factory);
    }

    protected FOMLink(OMContainer parent, OMFactory factory) throws OMException {
        super(LINK, parent, factory);
    }

    protected FOMLink(QName qname, OMContainer parent, OMFactory factory) throws OMException {
        super(qname, parent, factory);
    }

    protected FOMLink(String localName, OMContainer parent, OMFactory factory, OMXMLParserWrapper builder) throws OMException {
        super(localName, parent, factory, builder);
    }

    public IRI getHref() {
        return this._getUriValue(this.getAttributeValue(HREF));
    }

    public IRI getResolvedHref() {
        return this._resolve(this.getResolvedBaseUri(), this.getHref());
    }

    public Link setHref(String href) {
        this.complete();
        if (href != null) {
            this.setAttributeValue(HREF, new IRI(href).toString());
        } else {
            this.removeAttribute(HREF);
        }
        return this;
    }

    public String getRel() {
        return this.getAttributeValue(REL);
    }

    public Link setRel(String rel) {
        this.complete();
        this.setAttributeValue(REL, rel);
        return this;
    }

    public MimeType getMimeType() {
        try {
            String type = this.getAttributeValue(TYPE);
            return type != null ? new MimeType(type) : null;
        }
        catch (javax.activation.MimeTypeParseException e) {
            throw new MimeTypeParseException(e);
        }
    }

    public void setMimeType(MimeType type) {
        this.complete();
        this.setAttributeValue(TYPE, type != null ? type.toString() : null);
    }

    public Link setMimeType(String type) {
        this.complete();
        try {
            if (type != null) {
                this.setAttributeValue(TYPE, new MimeType(type).toString());
            } else {
                this.removeAttribute(TYPE);
            }
        }
        catch (javax.activation.MimeTypeParseException e) {
            throw new MimeTypeParseException(e);
        }
        return this;
    }

    public String getHrefLang() {
        return this.getAttributeValue(HREFLANG);
    }

    public Link setHrefLang(String lang) {
        this.complete();
        if (lang != null) {
            this.setAttributeValue(HREFLANG, lang);
        } else {
            this.removeAttribute(HREFLANG);
        }
        return this;
    }

    public String getTitle() {
        return this.getAttributeValue(ATITLE);
    }

    public Link setTitle(String title) {
        this.complete();
        if (title != null) {
            this.setAttributeValue(ATITLE, title);
        } else {
            this.removeAttribute(ATITLE);
        }
        return this;
    }

    public long getLength() {
        String l = this.getAttributeValue(LENGTH);
        return l != null ? Long.valueOf(l) : -1L;
    }

    public Link setLength(long length) {
        this.complete();
        if (length > -1L) {
            this.setAttributeValue(LENGTH, length >= 0L ? String.valueOf(length) : "0");
        } else {
            this.removeAttribute(LENGTH);
        }
        return this;
    }

    public static final String getRelEquiv(String val) {
        try {
            val = IRI.normalizeString(val);
        }
        catch (Exception e) {
            // empty catch block
        }
        String rel = REL_EQUIVS.get(val);
        return rel != null ? rel : val;
    }

    public String getValue() {
        return this.getText();
    }

    public void setValue(String value) {
        this.complete();
        if (value != null) {
            this.setText(value);
        } else {
            this._removeAllChildren();
        }
    }

    static {
        REL_EQUIVS.put("http://www.iana.org/assignments/relation/alternate", "alternate");
        REL_EQUIVS.put("http://www.iana.org/assignments/relation/current", "current");
        REL_EQUIVS.put("http://www.iana.org/assignments/relation/enclosure", "enclosure");
        REL_EQUIVS.put("http://www.iana.org/assignments/relation/first", "first");
        REL_EQUIVS.put("http://www.iana.org/assignments/relation/last", "last");
        REL_EQUIVS.put("http://www.iana.org/assignments/relation/next", "next");
        REL_EQUIVS.put("http://www.iana.org/assignments/relation/payment", "payment");
        REL_EQUIVS.put("http://www.iana.org/assignments/relation/previous", "previous");
        REL_EQUIVS.put("http://www.iana.org/assignments/relation/related", "related");
        REL_EQUIVS.put("http://www.iana.org/assignments/relation/self", "self");
        REL_EQUIVS.put("http://www.iana.org/assignments/relation/via", "via");
        REL_EQUIVS.put("http://www.iana.org/assignments/relation/replies", "replies");
        REL_EQUIVS.put("http://www.iana.org/assignments/relation/license", "license");
        REL_EQUIVS.put("http://www.iana.org/assignments/relation/edit", "edit");
        REL_EQUIVS.put("http://www.iana.org/assignments/relation/edit-media", "edit-media");
        REL_EQUIVS.put("http://www.iana.org/assignments/relation/service", "service");
    }
}


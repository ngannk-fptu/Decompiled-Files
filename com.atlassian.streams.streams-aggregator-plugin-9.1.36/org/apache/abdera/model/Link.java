/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.activation.MimeType
 */
package org.apache.abdera.model;

import javax.activation.MimeType;
import org.apache.abdera.i18n.iri.IRI;
import org.apache.abdera.model.ExtensibleElement;

public interface Link
extends ExtensibleElement {
    public static final String REL_ALTERNATE = "alternate";
    public static final String REL_CURRENT = "current";
    public static final String REL_ENCLOSURE = "enclosure";
    public static final String REL_FIRST = "first";
    public static final String REL_LAST = "last";
    public static final String REL_NEXT = "next";
    public static final String REL_PAYMENT = "payment";
    public static final String REL_PREVIOUS = "previous";
    public static final String REL_RELATED = "related";
    public static final String REL_SELF = "self";
    public static final String REL_VIA = "via";
    public static final String REL_REPLIES = "replies";
    public static final String REL_LICENSE = "license";
    public static final String REL_EDIT = "edit";
    public static final String REL_EDIT_MEDIA = "edit-media";
    public static final String REL_SERVICE = "service";
    public static final String IANA_BASE = "http://www.iana.org/assignments/relation/";
    public static final String REL_ALTERNATE_IANA = "http://www.iana.org/assignments/relation/alternate";
    public static final String REL_CURRENT_IANA = "http://www.iana.org/assignments/relation/current";
    public static final String REL_ENCLOSURE_IANA = "http://www.iana.org/assignments/relation/enclosure";
    public static final String REL_FIRST_IANA = "http://www.iana.org/assignments/relation/first";
    public static final String REL_LAST_IANA = "http://www.iana.org/assignments/relation/last";
    public static final String REL_NEXT_IANA = "http://www.iana.org/assignments/relation/next";
    public static final String REL_PAYMENT_IANA = "http://www.iana.org/assignments/relation/payment";
    public static final String REL_PREVIOUS_IANA = "http://www.iana.org/assignments/relation/previous";
    public static final String REL_RELATED_IANA = "http://www.iana.org/assignments/relation/related";
    public static final String REL_SELF_IANA = "http://www.iana.org/assignments/relation/self";
    public static final String REL_VIA_IANA = "http://www.iana.org/assignments/relation/via";
    public static final String REL_REPLIES_IANA = "http://www.iana.org/assignments/relation/replies";
    public static final String REL_LICENSE_IANA = "http://www.iana.org/assignments/relation/license";
    public static final String REL_EDIT_IANA = "http://www.iana.org/assignments/relation/edit";
    public static final String REL_EDIT_MEDIA_IANA = "http://www.iana.org/assignments/relation/edit-media";
    public static final String REL_SERVICE_IANA = "http://www.iana.org/assignments/relation/service";

    public IRI getHref();

    public IRI getResolvedHref();

    public Link setHref(String var1);

    public String getRel();

    public Link setRel(String var1);

    public MimeType getMimeType();

    public Link setMimeType(String var1);

    public String getHrefLang();

    public Link setHrefLang(String var1);

    public String getTitle();

    public Link setTitle(String var1);

    public long getLength();

    public Link setLength(long var1);
}


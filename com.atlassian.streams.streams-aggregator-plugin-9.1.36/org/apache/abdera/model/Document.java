/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.activation.MimeType
 */
package org.apache.abdera.model;

import java.io.Serializable;
import java.util.Date;
import javax.activation.MimeType;
import org.apache.abdera.i18n.iri.IRI;
import org.apache.abdera.i18n.rfc4646.Lang;
import org.apache.abdera.model.Base;
import org.apache.abdera.model.Element;
import org.apache.abdera.util.EntityTag;
import org.apache.abdera.util.XmlUtil;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public interface Document<T extends Element>
extends Base,
Serializable {
    public T getRoot();

    public Document<T> setRoot(T var1);

    public IRI getBaseUri();

    public Document<T> setBaseUri(String var1);

    public MimeType getContentType();

    public Document<T> setContentType(String var1);

    public Date getLastModified();

    public Document<T> setLastModified(Date var1);

    public String getCharset();

    public Document<T> setCharset(String var1);

    public Document<T> addProcessingInstruction(String var1, String var2);

    public String[] getProcessingInstruction(String var1);

    public Document<T> addStylesheet(String var1, String var2);

    public EntityTag getEntityTag();

    public Document<T> setEntityTag(EntityTag var1);

    public Document<T> setEntityTag(String var1);

    public String getLanguage();

    public Lang getLanguageTag();

    public Document<T> setLanguage(String var1);

    public String getSlug();

    public Document<T> setSlug(String var1);

    public boolean getMustPreserveWhitespace();

    public Document<T> setMustPreserveWhitespace(boolean var1);

    public XmlUtil.XMLVersion getXmlVersion();
}


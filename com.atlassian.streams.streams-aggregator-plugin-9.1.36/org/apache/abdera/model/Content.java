/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.activation.DataHandler
 *  javax.activation.MimeType
 */
package org.apache.abdera.model;

import javax.activation.DataHandler;
import javax.activation.MimeType;
import org.apache.abdera.i18n.iri.IRI;
import org.apache.abdera.model.Element;
import org.apache.abdera.util.MimeTypeHelper;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public interface Content
extends Element {
    public Type getContentType();

    public Content setContentType(Type var1);

    public <T extends Element> T getValueElement();

    public <T extends Element> Content setValueElement(T var1);

    public MimeType getMimeType();

    public Content setMimeType(String var1);

    public IRI getSrc();

    public IRI getResolvedSrc();

    public Content setSrc(String var1);

    public DataHandler getDataHandler();

    public Content setDataHandler(DataHandler var1);

    public String getValue();

    public Content setValue(String var1);

    public String getWrappedValue();

    public Content setWrappedValue(String var1);

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    public static enum Type {
        TEXT,
        HTML,
        XHTML,
        XML,
        MEDIA;


        public static Type typeFromString(String val) {
            Type type = TEXT;
            if (val != null) {
                type = val.equalsIgnoreCase("text") ? TEXT : (val.equalsIgnoreCase("html") ? HTML : (val.equalsIgnoreCase("xhtml") ? XHTML : (MimeTypeHelper.isXml(val) ? XML : (MimeTypeHelper.isMimeType(val) ? MEDIA : null))));
            }
            return type;
        }
    }
}


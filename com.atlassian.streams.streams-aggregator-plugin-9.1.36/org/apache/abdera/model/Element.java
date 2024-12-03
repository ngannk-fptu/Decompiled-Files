/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.activation.DataHandler
 */
package org.apache.abdera.model;

import java.util.List;
import java.util.Locale;
import java.util.Map;
import javax.activation.DataHandler;
import javax.xml.namespace.QName;
import org.apache.abdera.i18n.iri.IRI;
import org.apache.abdera.i18n.rfc4646.Lang;
import org.apache.abdera.model.Base;
import org.apache.abdera.model.Document;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public interface Element
extends Base,
Iterable<Element> {
    public <T extends Base> T getParentElement();

    public <T extends Element> T setParentElement(Element var1);

    public <T extends Element> T getPreviousSibling();

    public <T extends Element> T getNextSibling();

    public <T extends Element> T getFirstChild();

    public <T extends Element> T getPreviousSibling(QName var1);

    public <T extends Element> T getNextSibling(QName var1);

    public <T extends Element> T getFirstChild(QName var1);

    public QName getQName();

    public String getLanguage();

    public Lang getLanguageTag();

    public Locale getLocale();

    public <T extends Element> T setLanguage(String var1);

    public IRI getBaseUri();

    public IRI getResolvedBaseUri();

    public <T extends Element> T setBaseUri(IRI var1);

    public <T extends Element> T setBaseUri(String var1);

    public <T extends Element> Document<T> getDocument();

    public String getAttributeValue(String var1);

    public String getAttributeValue(QName var1);

    public List<QName> getAttributes();

    public List<QName> getExtensionAttributes();

    public <T extends Element> T removeAttribute(QName var1);

    public <T extends Element> T removeAttribute(String var1);

    public <T extends Element> T setAttributeValue(String var1, String var2);

    public <T extends Element> T setAttributeValue(QName var1, String var2);

    public void discard();

    public String getText();

    public void setText(String var1);

    public <T extends Element> T setText(DataHandler var1);

    public <T extends Element> T declareNS(String var1, String var2);

    public Map<String, String> getNamespaces();

    public <T extends Element> List<T> getElements();

    public boolean getMustPreserveWhitespace();

    public <T extends Element> T setMustPreserveWhitespace(boolean var1);
}


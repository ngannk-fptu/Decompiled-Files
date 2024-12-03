/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xmpbox.schema;

import java.util.List;
import org.apache.xmpbox.XMPMetadata;
import org.apache.xmpbox.schema.XMPSchema;
import org.apache.xmpbox.type.ArrayProperty;
import org.apache.xmpbox.type.BooleanType;
import org.apache.xmpbox.type.Cardinality;
import org.apache.xmpbox.type.PropertyType;
import org.apache.xmpbox.type.StructuredType;
import org.apache.xmpbox.type.TextType;
import org.apache.xmpbox.type.Types;
import org.apache.xmpbox.type.URLType;

@StructuredType(preferedPrefix="xmpRights", namespace="http://ns.adobe.com/xap/1.0/rights/")
public class XMPRightsManagementSchema
extends XMPSchema {
    @PropertyType(type=Types.URL, card=Cardinality.Simple)
    public static final String CERTIFICATE = "Certificate";
    @PropertyType(type=Types.Boolean, card=Cardinality.Simple)
    public static final String MARKED = "Marked";
    @PropertyType(type=Types.ProperName, card=Cardinality.Bag)
    public static final String OWNER = "Owner";
    @PropertyType(type=Types.LangAlt, card=Cardinality.Simple)
    public static final String USAGETERMS = "UsageTerms";
    @PropertyType(type=Types.URL, card=Cardinality.Simple)
    public static final String WEBSTATEMENT = "WebStatement";

    public XMPRightsManagementSchema(XMPMetadata metadata) {
        super(metadata);
    }

    public XMPRightsManagementSchema(XMPMetadata metadata, String ownPrefix) {
        super(metadata, ownPrefix);
    }

    public void addOwner(String value) {
        this.addQualifiedBagValue(OWNER, value);
    }

    public void removeOwner(String value) {
        this.removeUnqualifiedBagValue(OWNER, value);
    }

    public ArrayProperty getOwnersProperty() {
        return (ArrayProperty)this.getProperty(OWNER);
    }

    public List<String> getOwners() {
        return this.getUnqualifiedBagValueList(OWNER);
    }

    public void setMarked(Boolean marked) {
        BooleanType tt = (BooleanType)this.instanciateSimple(MARKED, marked != false ? "True" : "False");
        this.setMarkedProperty(tt);
    }

    public void setMarkedProperty(BooleanType marked) {
        this.addProperty(marked);
    }

    public BooleanType getMarkedProperty() {
        return (BooleanType)this.getProperty(MARKED);
    }

    public Boolean getMarked() {
        BooleanType bt = (BooleanType)this.getProperty(MARKED);
        return bt == null ? null : bt.getValue();
    }

    public void addUsageTerms(String lang, String value) {
        this.setUnqualifiedLanguagePropertyValue(USAGETERMS, lang, value);
    }

    public void setUsageTerms(String terms) {
        this.addUsageTerms(null, terms);
    }

    public ArrayProperty getUsageTermsProperty() {
        return (ArrayProperty)this.getProperty(USAGETERMS);
    }

    public List<String> getUsageTermsLanguages() {
        return this.getUnqualifiedLanguagePropertyLanguagesValue(USAGETERMS);
    }

    public String getUsageTerms(String lang) {
        return this.getUnqualifiedLanguagePropertyValue(USAGETERMS, lang);
    }

    public String getUsageTerms() {
        return this.getUsageTerms(null);
    }

    public TextType getWebStatementProperty() {
        return (TextType)this.getProperty(WEBSTATEMENT);
    }

    public String getWebStatement() {
        TextType tt = (TextType)this.getProperty(WEBSTATEMENT);
        return tt == null ? null : tt.getStringValue();
    }

    public void setWebStatement(String url) {
        URLType tt = (URLType)this.instanciateSimple(WEBSTATEMENT, url);
        this.setWebStatementProperty(tt);
    }

    public void setWebStatementProperty(URLType url) {
        this.addProperty(url);
    }

    public TextType getCertificateProperty() {
        return (TextType)this.getProperty(CERTIFICATE);
    }

    public String getCertificate() {
        TextType tt = (TextType)this.getProperty(CERTIFICATE);
        return tt == null ? null : tt.getStringValue();
    }

    public void setCertificate(String url) {
        URLType tt = (URLType)this.instanciateSimple(CERTIFICATE, url);
        this.setCertificateProperty(tt);
    }

    public void setCertificateProperty(URLType url) {
        this.addProperty(url);
    }
}


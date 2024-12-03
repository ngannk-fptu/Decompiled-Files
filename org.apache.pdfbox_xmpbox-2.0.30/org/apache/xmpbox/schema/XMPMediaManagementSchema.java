/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xmpbox.schema;

import java.util.List;
import org.apache.xmpbox.XMPMetadata;
import org.apache.xmpbox.schema.XMPSchema;
import org.apache.xmpbox.type.AgentNameType;
import org.apache.xmpbox.type.ArrayProperty;
import org.apache.xmpbox.type.Cardinality;
import org.apache.xmpbox.type.IntegerType;
import org.apache.xmpbox.type.PropertyType;
import org.apache.xmpbox.type.RenditionClassType;
import org.apache.xmpbox.type.ResourceRefType;
import org.apache.xmpbox.type.StructuredType;
import org.apache.xmpbox.type.TextType;
import org.apache.xmpbox.type.Types;
import org.apache.xmpbox.type.URIType;
import org.apache.xmpbox.type.URLType;

@StructuredType(preferedPrefix="xmpMM", namespace="http://ns.adobe.com/xap/1.0/mm/")
public class XMPMediaManagementSchema
extends XMPSchema {
    @PropertyType(type=Types.URL, card=Cardinality.Simple)
    public static final String LAST_URL = "LastURL";
    @PropertyType(type=Types.ResourceRef, card=Cardinality.Simple)
    public static final String RENDITION_OF = "RenditionOf";
    @PropertyType(type=Types.Integer, card=Cardinality.Simple)
    public static final String SAVE_ID = "SaveID";
    @PropertyType(type=Types.ResourceRef, card=Cardinality.Simple)
    public static final String DERIVED_FROM = "DerivedFrom";
    @PropertyType(type=Types.URI, card=Cardinality.Simple)
    public static final String DOCUMENTID = "DocumentID";
    @PropertyType(type=Types.AgentName, card=Cardinality.Simple)
    public static final String MANAGER = "Manager";
    @PropertyType(type=Types.URI, card=Cardinality.Simple)
    public static final String MANAGETO = "ManageTo";
    @PropertyType(type=Types.URI, card=Cardinality.Simple)
    public static final String MANAGEUI = "ManageUI";
    @PropertyType(type=Types.Text, card=Cardinality.Simple)
    public static final String MANAGERVARIANT = "ManagerVariant";
    @PropertyType(type=Types.URI, card=Cardinality.Simple)
    public static final String INSTANCEID = "InstanceID";
    @PropertyType(type=Types.ResourceRef, card=Cardinality.Simple)
    public static final String MANAGED_FROM = "ManagedFrom";
    @PropertyType(type=Types.Text, card=Cardinality.Simple)
    public static final String ORIGINALDOCUMENTID = "OriginalDocumentID";
    @PropertyType(type=Types.RenditionClass, card=Cardinality.Simple)
    public static final String RENDITIONCLASS = "RenditionClass";
    @PropertyType(type=Types.Text, card=Cardinality.Simple)
    public static final String RENDITIONPARAMS = "RenditionParams";
    @PropertyType(type=Types.Text, card=Cardinality.Simple)
    public static final String VERSIONID = "VersionID";
    @PropertyType(type=Types.Version, card=Cardinality.Seq)
    public static final String VERSIONS = "Versions";
    @PropertyType(type=Types.ResourceEvent, card=Cardinality.Seq)
    public static final String HISTORY = "History";
    @PropertyType(type=Types.Text, card=Cardinality.Bag)
    public static final String INGREDIENTS = "Ingredients";

    public XMPMediaManagementSchema(XMPMetadata metadata) {
        super(metadata);
    }

    public XMPMediaManagementSchema(XMPMetadata metadata, String ownPrefix) {
        super(metadata, ownPrefix);
    }

    public void setDerivedFromProperty(ResourceRefType tt) {
        this.addProperty(tt);
    }

    public ResourceRefType getResourceRefProperty() {
        return (ResourceRefType)this.getProperty(DERIVED_FROM);
    }

    public void setDocumentID(String url) {
        URIType tt = (URIType)this.instanciateSimple(DOCUMENTID, url);
        this.setDocumentIDProperty(tt);
    }

    public void setDocumentIDProperty(URIType tt) {
        this.addProperty(tt);
    }

    public TextType getDocumentIDProperty() {
        return (TextType)this.getProperty(DOCUMENTID);
    }

    public String getDocumentID() {
        TextType tt = this.getDocumentIDProperty();
        return tt != null ? tt.getStringValue() : null;
    }

    public void setLastURL(String url) {
        URLType tt = (URLType)this.instanciateSimple(LAST_URL, url);
        this.setLastURLProperty(tt);
    }

    public void setLastURLProperty(URLType tt) {
        this.addProperty(tt);
    }

    public URLType getLastURLProperty() {
        return (URLType)this.getProperty(LAST_URL);
    }

    public String getLastURL() {
        URLType tt = this.getLastURLProperty();
        return tt != null ? tt.getStringValue() : null;
    }

    public void setSaveId(Integer url) {
        IntegerType tt = (IntegerType)this.instanciateSimple(SAVE_ID, url);
        this.setSaveIDProperty(tt);
    }

    public void setSaveIDProperty(IntegerType tt) {
        this.addProperty(tt);
    }

    public IntegerType getSaveIDProperty() {
        return (IntegerType)this.getProperty(SAVE_ID);
    }

    public Integer getSaveID() {
        IntegerType tt = this.getSaveIDProperty();
        return tt != null ? tt.getValue() : null;
    }

    public void setManager(String value) {
        AgentNameType tt = (AgentNameType)this.instanciateSimple(MANAGER, value);
        this.setManagerProperty(tt);
    }

    public void setManagerProperty(AgentNameType tt) {
        this.addProperty(tt);
    }

    public TextType getManagerProperty() {
        return (TextType)this.getProperty(MANAGER);
    }

    public String getManager() {
        TextType tt = this.getManagerProperty();
        return tt != null ? tt.getStringValue() : null;
    }

    public void setManageTo(String value) {
        URIType tt = (URIType)this.instanciateSimple(MANAGETO, value);
        this.setManageToProperty(tt);
    }

    public void setManageToProperty(URIType tt) {
        this.addProperty(tt);
    }

    public TextType getManageToProperty() {
        return (TextType)this.getProperty(MANAGETO);
    }

    public String getManageTo() {
        TextType tt = this.getManageToProperty();
        return tt != null ? tt.getStringValue() : null;
    }

    public void setManageUI(String value) {
        URIType tt = (URIType)this.instanciateSimple(MANAGEUI, value);
        this.setManageUIProperty(tt);
    }

    public void setManageUIProperty(URIType tt) {
        this.addProperty(tt);
    }

    public TextType getManageUIProperty() {
        return (TextType)this.getProperty(MANAGEUI);
    }

    public String getManageUI() {
        TextType tt = this.getManageUIProperty();
        return tt != null ? tt.getStringValue() : null;
    }

    public void setManagerVariant(String value) {
        TextType tt = (TextType)this.instanciateSimple(MANAGERVARIANT, value);
        this.setManagerVariantProperty(tt);
    }

    public void setManagerVariantProperty(TextType tt) {
        this.addProperty(tt);
    }

    public TextType getManagerVariantProperty() {
        return (TextType)this.getProperty(MANAGERVARIANT);
    }

    public String getManagerVariant() {
        TextType tt = this.getManagerVariantProperty();
        return tt != null ? tt.getStringValue() : null;
    }

    public void setInstanceID(String value) {
        URIType tt = (URIType)this.instanciateSimple(INSTANCEID, value);
        this.setInstanceIDProperty(tt);
    }

    public void setInstanceIDProperty(URIType tt) {
        this.addProperty(tt);
    }

    public TextType getInstanceIDProperty() {
        return (TextType)this.getProperty(INSTANCEID);
    }

    public String getInstanceID() {
        TextType tt = this.getInstanceIDProperty();
        return tt != null ? tt.getStringValue() : null;
    }

    public void setManagedFromProperty(ResourceRefType resourceRef) {
        this.addProperty(resourceRef);
    }

    public ResourceRefType getManagedFromProperty() {
        return (ResourceRefType)this.getProperty(MANAGED_FROM);
    }

    public void setOriginalDocumentID(String url) {
        TextType tt = (TextType)this.instanciateSimple(ORIGINALDOCUMENTID, url);
        this.setOriginalDocumentIDProperty(tt);
    }

    public void setOriginalDocumentIDProperty(TextType tt) {
        this.addProperty(tt);
    }

    public TextType getOriginalDocumentIDProperty() {
        return (TextType)this.getProperty(ORIGINALDOCUMENTID);
    }

    public String getOriginalDocumentID() {
        TextType tt = this.getOriginalDocumentIDProperty();
        return tt != null ? tt.getStringValue() : null;
    }

    public void setRenditionClass(String value) {
        RenditionClassType tt = (RenditionClassType)this.instanciateSimple(RENDITIONCLASS, value);
        this.setRenditionClassProperty(tt);
    }

    public void setRenditionClassProperty(RenditionClassType tt) {
        this.addProperty(tt);
    }

    public TextType getRenditionClassProperty() {
        return (TextType)this.getProperty(RENDITIONCLASS);
    }

    public String getRenditionClass() {
        TextType tt = this.getRenditionClassProperty();
        return tt != null ? tt.getStringValue() : null;
    }

    public void setRenditionParams(String url) {
        TextType tt = (TextType)this.instanciateSimple(RENDITIONPARAMS, url);
        this.setRenditionParamsProperty(tt);
    }

    public void setRenditionParamsProperty(TextType tt) {
        this.addProperty(tt);
    }

    public TextType getRenditionParamsProperty() {
        return (TextType)this.getProperty(RENDITIONPARAMS);
    }

    public String getRenditionParams() {
        TextType tt = this.getRenditionParamsProperty();
        return tt != null ? tt.getStringValue() : null;
    }

    public void setVersionID(String value) {
        TextType tt = (TextType)this.instanciateSimple(VERSIONID, value);
        this.setVersionIDProperty(tt);
    }

    public void setVersionIDProperty(TextType tt) {
        this.addProperty(tt);
    }

    public TextType getVersionIDProperty() {
        return (TextType)this.getProperty(VERSIONID);
    }

    public String getVersionID() {
        TextType tt = this.getVersionIDProperty();
        return tt != null ? tt.getStringValue() : null;
    }

    public void addVersions(String value) {
        this.addQualifiedBagValue(VERSIONS, value);
    }

    public ArrayProperty getVersionsProperty() {
        return (ArrayProperty)this.getProperty(VERSIONS);
    }

    public List<String> getVersions() {
        return this.getUnqualifiedBagValueList(VERSIONS);
    }

    public void addHistory(String history) {
        this.addUnqualifiedSequenceValue(HISTORY, history);
    }

    public ArrayProperty getHistoryProperty() {
        return (ArrayProperty)this.getProperty(HISTORY);
    }

    public List<String> getHistory() {
        return this.getUnqualifiedSequenceValueList(HISTORY);
    }

    public void addIngredients(String ingredients) {
        this.addQualifiedBagValue(INGREDIENTS, ingredients);
    }

    public ArrayProperty getIngredientsProperty() {
        return (ArrayProperty)this.getProperty(INGREDIENTS);
    }

    public List<String> getIngredients() {
        return this.getUnqualifiedBagValueList(INGREDIENTS);
    }
}


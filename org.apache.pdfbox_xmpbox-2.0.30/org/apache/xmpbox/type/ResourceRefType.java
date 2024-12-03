/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xmpbox.type;

import java.util.Calendar;
import java.util.List;
import org.apache.xmpbox.XMPMetadata;
import org.apache.xmpbox.type.AbstractStructuredType;
import org.apache.xmpbox.type.AgentNameType;
import org.apache.xmpbox.type.ArrayProperty;
import org.apache.xmpbox.type.Cardinality;
import org.apache.xmpbox.type.ChoiceType;
import org.apache.xmpbox.type.DateType;
import org.apache.xmpbox.type.PartType;
import org.apache.xmpbox.type.PropertyType;
import org.apache.xmpbox.type.RenditionClassType;
import org.apache.xmpbox.type.StructuredType;
import org.apache.xmpbox.type.TextType;
import org.apache.xmpbox.type.TypeMapping;
import org.apache.xmpbox.type.Types;
import org.apache.xmpbox.type.URIType;

@StructuredType(preferedPrefix="stRef", namespace="http://ns.adobe.com/xap/1.0/sType/ResourceRef#")
public class ResourceRefType
extends AbstractStructuredType {
    @PropertyType(type=Types.URI, card=Cardinality.Simple)
    public static final String DOCUMENT_ID = "documentID";
    @PropertyType(type=Types.URI, card=Cardinality.Simple)
    public static final String FILE_PATH = "filePath";
    @PropertyType(type=Types.URI, card=Cardinality.Simple)
    public static final String INSTANCE_ID = "instanceID";
    @PropertyType(type=Types.Date, card=Cardinality.Simple)
    public static final String LAST_MODIFY_DATE = "lastModifyDate";
    @PropertyType(type=Types.URI, card=Cardinality.Simple)
    public static final String MANAGE_TO = "manageTo";
    @PropertyType(type=Types.URI, card=Cardinality.Simple)
    public static final String MANAGE_UI = "manageUI";
    @PropertyType(type=Types.AgentName, card=Cardinality.Simple)
    public static final String MANAGER = "manager";
    @PropertyType(type=Types.Text, card=Cardinality.Simple)
    public static final String MANAGER_VARIANT = "managerVariant";
    @PropertyType(type=Types.Text, card=Cardinality.Simple)
    public static final String PART_MAPPING = "partMapping";
    @PropertyType(type=Types.Text, card=Cardinality.Simple)
    public static final String RENDITION_PARAMS = "renditionParams";
    @PropertyType(type=Types.Text, card=Cardinality.Simple)
    public static final String VERSION_ID = "versionID";
    @PropertyType(type=Types.Choice, card=Cardinality.Simple)
    public static final String MASK_MARKERS = "maskMarkers";
    @PropertyType(type=Types.RenditionClass, card=Cardinality.Simple)
    public static final String RENDITION_CLASS = "renditionClass";
    @PropertyType(type=Types.Part, card=Cardinality.Simple)
    public static final String FROM_PART = "fromPart";
    @PropertyType(type=Types.Part, card=Cardinality.Simple)
    public static final String TO_PART = "toPart";
    public static final String ALTERNATE_PATHS = "alternatePaths";

    public ResourceRefType(XMPMetadata metadata) {
        super(metadata);
        this.addNamespace(this.getNamespace(), this.getPreferedPrefix());
    }

    public String getDocumentID() {
        TextType absProp = (TextType)this.getFirstEquivalentProperty(DOCUMENT_ID, URIType.class);
        if (absProp != null) {
            return absProp.getStringValue();
        }
        return null;
    }

    public void setDocumentID(String value) {
        this.addSimpleProperty(DOCUMENT_ID, value);
    }

    public String getFilePath() {
        TextType absProp = (TextType)this.getFirstEquivalentProperty(FILE_PATH, URIType.class);
        if (absProp != null) {
            return absProp.getStringValue();
        }
        return null;
    }

    public void setFilePath(String value) {
        this.addSimpleProperty(FILE_PATH, value);
    }

    public String getInstanceID() {
        TextType absProp = (TextType)this.getFirstEquivalentProperty(INSTANCE_ID, URIType.class);
        if (absProp != null) {
            return absProp.getStringValue();
        }
        return null;
    }

    public void setInstanceID(String value) {
        this.addSimpleProperty(INSTANCE_ID, value);
    }

    public Calendar getLastModifyDate() {
        DateType absProp = (DateType)this.getFirstEquivalentProperty(LAST_MODIFY_DATE, DateType.class);
        if (absProp != null) {
            return absProp.getValue();
        }
        return null;
    }

    public void setLastModifyDate(Calendar value) {
        this.addSimpleProperty(LAST_MODIFY_DATE, value);
    }

    public String getManageUI() {
        TextType absProp = (TextType)this.getFirstEquivalentProperty(MANAGE_UI, URIType.class);
        if (absProp != null) {
            return absProp.getStringValue();
        }
        return null;
    }

    public void setManageUI(String value) {
        this.addSimpleProperty(MANAGE_UI, value);
    }

    public String getManageTo() {
        TextType absProp = (TextType)this.getFirstEquivalentProperty(MANAGE_TO, URIType.class);
        if (absProp != null) {
            return absProp.getStringValue();
        }
        return null;
    }

    public void setManageTo(String value) {
        this.addSimpleProperty(MANAGE_TO, value);
    }

    public String getManager() {
        TextType absProp = (TextType)this.getFirstEquivalentProperty(MANAGER, AgentNameType.class);
        if (absProp != null) {
            return absProp.getStringValue();
        }
        return null;
    }

    public void setManager(String value) {
        this.addSimpleProperty(MANAGER, value);
    }

    public String getManagerVariant() {
        TextType absProp = (TextType)this.getFirstEquivalentProperty(MANAGER_VARIANT, TextType.class);
        if (absProp != null) {
            return absProp.getStringValue();
        }
        return null;
    }

    public void setManagerVariant(String value) {
        this.addSimpleProperty(MANAGER_VARIANT, value);
    }

    public String getPartMapping() {
        TextType absProp = (TextType)this.getFirstEquivalentProperty(PART_MAPPING, TextType.class);
        if (absProp != null) {
            return absProp.getStringValue();
        }
        return null;
    }

    public void setPartMapping(String value) {
        this.addSimpleProperty(PART_MAPPING, value);
    }

    public String getRenditionParams() {
        TextType absProp = (TextType)this.getFirstEquivalentProperty(RENDITION_PARAMS, TextType.class);
        if (absProp != null) {
            return absProp.getStringValue();
        }
        return null;
    }

    public void setRenditionParams(String value) {
        this.addSimpleProperty(RENDITION_PARAMS, value);
    }

    public String getVersionID() {
        TextType absProp = (TextType)this.getFirstEquivalentProperty(VERSION_ID, TextType.class);
        if (absProp != null) {
            return absProp.getStringValue();
        }
        return null;
    }

    public void setVersionID(String value) {
        this.addSimpleProperty(VERSION_ID, value);
    }

    public String getMaskMarkers() {
        TextType absProp = (TextType)this.getFirstEquivalentProperty(MASK_MARKERS, ChoiceType.class);
        if (absProp != null) {
            return absProp.getStringValue();
        }
        return null;
    }

    public void setMaskMarkers(String value) {
        this.addSimpleProperty(MASK_MARKERS, value);
    }

    public String getRenditionClass() {
        TextType absProp = (TextType)this.getFirstEquivalentProperty(RENDITION_CLASS, RenditionClassType.class);
        if (absProp != null) {
            return absProp.getStringValue();
        }
        return null;
    }

    public void setRenditionClass(String value) {
        this.addSimpleProperty(RENDITION_CLASS, value);
    }

    public String getFromPart() {
        TextType absProp = (TextType)this.getFirstEquivalentProperty(FROM_PART, PartType.class);
        if (absProp != null) {
            return absProp.getStringValue();
        }
        return null;
    }

    public void setFromPart(String value) {
        this.addSimpleProperty(FROM_PART, value);
    }

    public String getToPart() {
        TextType absProp = (TextType)this.getFirstEquivalentProperty(TO_PART, PartType.class);
        if (absProp != null) {
            return absProp.getStringValue();
        }
        return null;
    }

    public void setToPart(String value) {
        this.addSimpleProperty(TO_PART, value);
    }

    public void addAlternatePath(String value) {
        ArrayProperty seq = (ArrayProperty)this.getFirstEquivalentProperty(ALTERNATE_PATHS, ArrayProperty.class);
        if (seq == null) {
            seq = this.getMetadata().getTypeMapping().createArrayProperty(null, this.getPreferedPrefix(), ALTERNATE_PATHS, Cardinality.Seq);
            this.addProperty(seq);
        }
        TypeMapping tm = this.getMetadata().getTypeMapping();
        TextType tt = (TextType)tm.instanciateSimpleProperty(null, "rdf", "li", value, Types.Text);
        seq.addProperty(tt);
    }

    public ArrayProperty getAlternatePathsProperty() {
        return (ArrayProperty)this.getFirstEquivalentProperty(ALTERNATE_PATHS, ArrayProperty.class);
    }

    public List<String> getAlternatePaths() {
        ArrayProperty seq = (ArrayProperty)this.getFirstEquivalentProperty(ALTERNATE_PATHS, ArrayProperty.class);
        if (seq != null) {
            return seq.getElementsAsString();
        }
        return null;
    }
}


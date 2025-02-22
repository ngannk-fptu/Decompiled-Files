/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xmlgraphics.xmp.schemas;

import java.util.Date;
import org.apache.xmlgraphics.xmp.Metadata;
import org.apache.xmlgraphics.xmp.PropertyAccess;
import org.apache.xmlgraphics.xmp.XMPArrayType;
import org.apache.xmlgraphics.xmp.XMPConstants;
import org.apache.xmlgraphics.xmp.XMPProperty;
import org.apache.xmlgraphics.xmp.XMPSchemaAdapter;
import org.apache.xmlgraphics.xmp.XMPSchemaRegistry;
import org.apache.xmlgraphics.xmp.XMPStructure;
import org.apache.xmlgraphics.xmp.schemas.XMPBasicSchema;

public class XMPBasicAdapter
extends XMPSchemaAdapter {
    private static final String ADVISORY = "Advisory";
    private static final String BASE_URL = "BaseURL";
    private static final String CREATE_DATE = "CreateDate";
    private static final String CREATOR_TOOL = "CreatorTool";
    private static final String IDENTIFIER = "Identifier";
    private static final String LABEL = "Label";
    private static final String METADATA_DATE = "MetadataDate";
    private static final String MODIFY_DATE = "ModifyDate";
    private static final String NICKNAME = "Nickname";
    private static final String RATING = "Rating";
    private static final String THUMBNAILS = "Thumbnails";

    public XMPBasicAdapter(Metadata meta, String namespace) {
        super(meta, XMPSchemaRegistry.getInstance().getSchema(namespace));
    }

    public void setBaseUrl(String value) {
        this.setValue(BASE_URL, value);
    }

    public String getBaseUrl() {
        return this.getValue(BASE_URL);
    }

    public void setCreateDate(Date creationDate) {
        this.setDateValue(CREATE_DATE, creationDate);
    }

    public Date getCreateDate() {
        return this.getDateValue(CREATE_DATE);
    }

    public void setCreatorTool(String value) {
        this.setValue(CREATOR_TOOL, value);
    }

    public String getCreatorTool() {
        return this.getValue(CREATOR_TOOL);
    }

    public void addIdentifier(String value) {
        this.addStringToBag(IDENTIFIER, value);
    }

    public void setIdentifier(String value, String qualifier) {
        PropertyAccess pa = this.findQualifiedStructure(IDENTIFIER, XMPBasicSchema.SCHEME_QUALIFIER, qualifier);
        if (pa != null) {
            pa.setProperty(new XMPProperty(XMPConstants.RDF_VALUE, value));
        } else {
            XMPStructure struct = new XMPStructure();
            struct.setProperty(new XMPProperty(XMPConstants.RDF_VALUE, value));
            struct.setProperty(new XMPProperty(XMPBasicSchema.SCHEME_QUALIFIER, qualifier));
            this.addObjectToArray(IDENTIFIER, struct, XMPArrayType.BAG);
        }
    }

    public String[] getIdentifiers() {
        return this.getStringArray(IDENTIFIER);
    }

    public String getIdentifier(String qualifier) {
        Object value = this.findQualifiedValue(IDENTIFIER, XMPBasicSchema.SCHEME_QUALIFIER, qualifier);
        return value != null ? value.toString() : null;
    }

    public void setModifyDate(Date modifyDate) {
        this.setDateValue(MODIFY_DATE, modifyDate);
    }

    public Date getModifyDate() {
        return this.getDateValue(MODIFY_DATE);
    }

    public void setMetadataDate(Date metadataDate) {
        this.setDateValue(METADATA_DATE, metadataDate);
    }

    public Date getMetadataDate() {
        return this.getDateValue(METADATA_DATE);
    }
}


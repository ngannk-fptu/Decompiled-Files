/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.microsoft.schemas.office.office.STOLELinkType
 *  com.microsoft.schemas.office.office.STOLEUpdateMode
 */
package com.microsoft.schemas.office.office;

import com.microsoft.schemas.office.office.STOLEDrawAspect;
import com.microsoft.schemas.office.office.STOLELinkType;
import com.microsoft.schemas.office.office.STOLEType;
import com.microsoft.schemas.office.office.STOLEUpdateMode;
import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.XmlString;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.openxmlformats.schemas.officeDocument.x2006.relationships.STRelationshipId;
import org.openxmlformats.schemas.officeDocument.x2006.sharedTypes.STTrueFalseBlank;

public interface CTOLEObject
extends XmlObject {
    public static final DocumentFactory<CTOLEObject> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "ctoleobjecte5c0type");
    public static final SchemaType type = Factory.getType();

    public String getLinkType();

    public STOLELinkType xgetLinkType();

    public boolean isSetLinkType();

    public void setLinkType(String var1);

    public void xsetLinkType(STOLELinkType var1);

    public void unsetLinkType();

    public STTrueFalseBlank.Enum getLockedField();

    public STTrueFalseBlank xgetLockedField();

    public boolean isSetLockedField();

    public void setLockedField(STTrueFalseBlank.Enum var1);

    public void xsetLockedField(STTrueFalseBlank var1);

    public void unsetLockedField();

    public String getFieldCodes();

    public XmlString xgetFieldCodes();

    public boolean isSetFieldCodes();

    public void setFieldCodes(String var1);

    public void xsetFieldCodes(XmlString var1);

    public void unsetFieldCodes();

    public STOLEType.Enum getType();

    public STOLEType xgetType();

    public boolean isSetType();

    public void setType(STOLEType.Enum var1);

    public void xsetType(STOLEType var1);

    public void unsetType();

    public String getProgID();

    public XmlString xgetProgID();

    public boolean isSetProgID();

    public void setProgID(String var1);

    public void xsetProgID(XmlString var1);

    public void unsetProgID();

    public String getShapeID();

    public XmlString xgetShapeID();

    public boolean isSetShapeID();

    public void setShapeID(String var1);

    public void xsetShapeID(XmlString var1);

    public void unsetShapeID();

    public STOLEDrawAspect.Enum getDrawAspect();

    public STOLEDrawAspect xgetDrawAspect();

    public boolean isSetDrawAspect();

    public void setDrawAspect(STOLEDrawAspect.Enum var1);

    public void xsetDrawAspect(STOLEDrawAspect var1);

    public void unsetDrawAspect();

    public String getObjectID();

    public XmlString xgetObjectID();

    public boolean isSetObjectID();

    public void setObjectID(String var1);

    public void xsetObjectID(XmlString var1);

    public void unsetObjectID();

    public String getId();

    public STRelationshipId xgetId();

    public boolean isSetId();

    public void setId(String var1);

    public void xsetId(STRelationshipId var1);

    public void unsetId();

    public STOLEUpdateMode.Enum getUpdateMode();

    public STOLEUpdateMode xgetUpdateMode();

    public boolean isSetUpdateMode();

    public void setUpdateMode(STOLEUpdateMode.Enum var1);

    public void xsetUpdateMode(STOLEUpdateMode var1);

    public void unsetUpdateMode();
}


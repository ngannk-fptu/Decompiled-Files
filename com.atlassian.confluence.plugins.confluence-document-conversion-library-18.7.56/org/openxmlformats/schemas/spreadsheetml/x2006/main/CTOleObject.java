/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.openxmlformats.schemas.spreadsheetml.x2006.main.CTObjectPr
 *  org.openxmlformats.schemas.spreadsheetml.x2006.main.STOleUpdate
 */
package org.openxmlformats.schemas.spreadsheetml.x2006.main;

import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlBoolean;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.XmlString;
import org.apache.xmlbeans.XmlUnsignedInt;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.openxmlformats.schemas.officeDocument.x2006.relationships.STRelationshipId;
import org.openxmlformats.schemas.officeDocument.x2006.sharedTypes.STXstring;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTObjectPr;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.STDvAspect;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.STOleUpdate;

public interface CTOleObject
extends XmlObject {
    public static final DocumentFactory<CTOleObject> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "ctoleobjectd866type");
    public static final SchemaType type = Factory.getType();

    public CTObjectPr getObjectPr();

    public boolean isSetObjectPr();

    public void setObjectPr(CTObjectPr var1);

    public CTObjectPr addNewObjectPr();

    public void unsetObjectPr();

    public String getProgId();

    public XmlString xgetProgId();

    public boolean isSetProgId();

    public void setProgId(String var1);

    public void xsetProgId(XmlString var1);

    public void unsetProgId();

    public STDvAspect.Enum getDvAspect();

    public STDvAspect xgetDvAspect();

    public boolean isSetDvAspect();

    public void setDvAspect(STDvAspect.Enum var1);

    public void xsetDvAspect(STDvAspect var1);

    public void unsetDvAspect();

    public String getLink();

    public STXstring xgetLink();

    public boolean isSetLink();

    public void setLink(String var1);

    public void xsetLink(STXstring var1);

    public void unsetLink();

    public STOleUpdate.Enum getOleUpdate();

    public STOleUpdate xgetOleUpdate();

    public boolean isSetOleUpdate();

    public void setOleUpdate(STOleUpdate.Enum var1);

    public void xsetOleUpdate(STOleUpdate var1);

    public void unsetOleUpdate();

    public boolean getAutoLoad();

    public XmlBoolean xgetAutoLoad();

    public boolean isSetAutoLoad();

    public void setAutoLoad(boolean var1);

    public void xsetAutoLoad(XmlBoolean var1);

    public void unsetAutoLoad();

    public long getShapeId();

    public XmlUnsignedInt xgetShapeId();

    public void setShapeId(long var1);

    public void xsetShapeId(XmlUnsignedInt var1);

    public String getId();

    public STRelationshipId xgetId();

    public boolean isSetId();

    public void setId(String var1);

    public void xsetId(STRelationshipId var1);

    public void unsetId();
}


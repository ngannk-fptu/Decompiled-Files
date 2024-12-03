/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.openxmlformats.schemas.spreadsheetml.x2006.main.CTControlPr
 */
package org.openxmlformats.schemas.spreadsheetml.x2006.main;

import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.XmlString;
import org.apache.xmlbeans.XmlUnsignedInt;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.openxmlformats.schemas.officeDocument.x2006.relationships.STRelationshipId;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTControlPr;

public interface CTControl
extends XmlObject {
    public static final DocumentFactory<CTControl> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "ctcontrol997ctype");
    public static final SchemaType type = Factory.getType();

    public CTControlPr getControlPr();

    public boolean isSetControlPr();

    public void setControlPr(CTControlPr var1);

    public CTControlPr addNewControlPr();

    public void unsetControlPr();

    public long getShapeId();

    public XmlUnsignedInt xgetShapeId();

    public void setShapeId(long var1);

    public void xsetShapeId(XmlUnsignedInt var1);

    public String getId();

    public STRelationshipId xgetId();

    public void setId(String var1);

    public void xsetId(STRelationshipId var1);

    public String getName();

    public XmlString xgetName();

    public boolean isSetName();

    public void setName(String var1);

    public void xsetName(XmlString var1);

    public void unsetName();
}


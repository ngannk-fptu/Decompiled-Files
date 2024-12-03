/*
 * Decompiled with CFR 0.152.
 */
package org.openxmlformats.schemas.drawingml.x2006.main;

import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.openxmlformats.schemas.drawingml.x2006.main.CTGeomGuideList;
import org.openxmlformats.schemas.drawingml.x2006.main.STShapeType;

public interface CTPresetGeometry2D
extends XmlObject {
    public static final DocumentFactory<CTPresetGeometry2D> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "ctpresetgeometry2db1detype");
    public static final SchemaType type = Factory.getType();

    public CTGeomGuideList getAvLst();

    public boolean isSetAvLst();

    public void setAvLst(CTGeomGuideList var1);

    public CTGeomGuideList addNewAvLst();

    public void unsetAvLst();

    public STShapeType.Enum getPrst();

    public STShapeType xgetPrst();

    public void setPrst(STShapeType.Enum var1);

    public void xsetPrst(STShapeType var1);
}


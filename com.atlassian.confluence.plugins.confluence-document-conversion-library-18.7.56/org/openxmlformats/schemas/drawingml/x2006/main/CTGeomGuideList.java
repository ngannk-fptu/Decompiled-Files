/*
 * Decompiled with CFR 0.152.
 */
package org.openxmlformats.schemas.drawingml.x2006.main;

import java.util.List;
import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.openxmlformats.schemas.drawingml.x2006.main.CTGeomGuide;

public interface CTGeomGuideList
extends XmlObject {
    public static final DocumentFactory<CTGeomGuideList> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "ctgeomguidelist364ftype");
    public static final SchemaType type = Factory.getType();

    public List<CTGeomGuide> getGdList();

    public CTGeomGuide[] getGdArray();

    public CTGeomGuide getGdArray(int var1);

    public int sizeOfGdArray();

    public void setGdArray(CTGeomGuide[] var1);

    public void setGdArray(int var1, CTGeomGuide var2);

    public CTGeomGuide insertNewGd(int var1);

    public CTGeomGuide addNewGd();

    public void removeGd(int var1);
}


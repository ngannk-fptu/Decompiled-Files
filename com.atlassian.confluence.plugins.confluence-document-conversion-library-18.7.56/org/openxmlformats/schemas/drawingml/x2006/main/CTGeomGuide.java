/*
 * Decompiled with CFR 0.152.
 */
package org.openxmlformats.schemas.drawingml.x2006.main;

import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.openxmlformats.schemas.drawingml.x2006.main.STGeomGuideFormula;
import org.openxmlformats.schemas.drawingml.x2006.main.STGeomGuideName;

public interface CTGeomGuide
extends XmlObject {
    public static final DocumentFactory<CTGeomGuide> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "ctgeomguidef191type");
    public static final SchemaType type = Factory.getType();

    public String getName();

    public STGeomGuideName xgetName();

    public void setName(String var1);

    public void xsetName(STGeomGuideName var1);

    public String getFmla();

    public STGeomGuideFormula xgetFmla();

    public void setFmla(String var1);

    public void xsetFmla(STGeomGuideFormula var1);
}


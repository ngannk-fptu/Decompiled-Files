/*
 * Decompiled with CFR 0.152.
 */
package org.openxmlformats.schemas.presentationml.x2006.main;

import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.openxmlformats.schemas.drawingml.x2006.main.CTNonVisualDrawingProps;
import org.openxmlformats.schemas.drawingml.x2006.main.CTNonVisualGroupDrawingShapeProps;
import org.openxmlformats.schemas.presentationml.x2006.main.CTApplicationNonVisualDrawingProps;

public interface CTGroupShapeNonVisual
extends XmlObject {
    public static final DocumentFactory<CTGroupShapeNonVisual> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "ctgroupshapenonvisual3e44type");
    public static final SchemaType type = Factory.getType();

    public CTNonVisualDrawingProps getCNvPr();

    public void setCNvPr(CTNonVisualDrawingProps var1);

    public CTNonVisualDrawingProps addNewCNvPr();

    public CTNonVisualGroupDrawingShapeProps getCNvGrpSpPr();

    public void setCNvGrpSpPr(CTNonVisualGroupDrawingShapeProps var1);

    public CTNonVisualGroupDrawingShapeProps addNewCNvGrpSpPr();

    public CTApplicationNonVisualDrawingProps getNvPr();

    public void setNvPr(CTApplicationNonVisualDrawingProps var1);

    public CTApplicationNonVisualDrawingProps addNewNvPr();
}


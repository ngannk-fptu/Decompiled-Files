/*
 * Decompiled with CFR 0.152.
 */
package com.microsoft.schemas.office.drawing.x2008.diagram;

import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.openxmlformats.schemas.drawingml.x2006.main.CTNonVisualDrawingProps;
import org.openxmlformats.schemas.drawingml.x2006.main.CTNonVisualGroupDrawingShapeProps;

public interface CTGroupShapeNonVisual
extends XmlObject {
    public static final DocumentFactory<CTGroupShapeNonVisual> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "ctgroupshapenonvisualf80ctype");
    public static final SchemaType type = Factory.getType();

    public CTNonVisualDrawingProps getCNvPr();

    public void setCNvPr(CTNonVisualDrawingProps var1);

    public CTNonVisualDrawingProps addNewCNvPr();

    public CTNonVisualGroupDrawingShapeProps getCNvGrpSpPr();

    public void setCNvGrpSpPr(CTNonVisualGroupDrawingShapeProps var1);

    public CTNonVisualGroupDrawingShapeProps addNewCNvGrpSpPr();
}


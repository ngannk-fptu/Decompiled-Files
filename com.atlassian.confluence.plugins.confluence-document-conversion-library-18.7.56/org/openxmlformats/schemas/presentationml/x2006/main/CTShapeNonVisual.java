/*
 * Decompiled with CFR 0.152.
 */
package org.openxmlformats.schemas.presentationml.x2006.main;

import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.openxmlformats.schemas.drawingml.x2006.main.CTNonVisualDrawingProps;
import org.openxmlformats.schemas.drawingml.x2006.main.CTNonVisualDrawingShapeProps;
import org.openxmlformats.schemas.presentationml.x2006.main.CTApplicationNonVisualDrawingProps;

public interface CTShapeNonVisual
extends XmlObject {
    public static final DocumentFactory<CTShapeNonVisual> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "ctshapenonvisualb619type");
    public static final SchemaType type = Factory.getType();

    public CTNonVisualDrawingProps getCNvPr();

    public void setCNvPr(CTNonVisualDrawingProps var1);

    public CTNonVisualDrawingProps addNewCNvPr();

    public CTNonVisualDrawingShapeProps getCNvSpPr();

    public void setCNvSpPr(CTNonVisualDrawingShapeProps var1);

    public CTNonVisualDrawingShapeProps addNewCNvSpPr();

    public CTApplicationNonVisualDrawingProps getNvPr();

    public void setNvPr(CTApplicationNonVisualDrawingProps var1);

    public CTApplicationNonVisualDrawingProps addNewNvPr();
}


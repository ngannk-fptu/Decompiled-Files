/*
 * Decompiled with CFR 0.152.
 */
package org.openxmlformats.schemas.presentationml.x2006.main;

import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.openxmlformats.schemas.drawingml.x2006.main.CTNonVisualDrawingProps;
import org.openxmlformats.schemas.drawingml.x2006.main.CTNonVisualGraphicFrameProperties;
import org.openxmlformats.schemas.presentationml.x2006.main.CTApplicationNonVisualDrawingProps;

public interface CTGraphicalObjectFrameNonVisual
extends XmlObject {
    public static final DocumentFactory<CTGraphicalObjectFrameNonVisual> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "ctgraphicalobjectframenonvisual257dtype");
    public static final SchemaType type = Factory.getType();

    public CTNonVisualDrawingProps getCNvPr();

    public void setCNvPr(CTNonVisualDrawingProps var1);

    public CTNonVisualDrawingProps addNewCNvPr();

    public CTNonVisualGraphicFrameProperties getCNvGraphicFramePr();

    public void setCNvGraphicFramePr(CTNonVisualGraphicFrameProperties var1);

    public CTNonVisualGraphicFrameProperties addNewCNvGraphicFramePr();

    public CTApplicationNonVisualDrawingProps getNvPr();

    public void setNvPr(CTApplicationNonVisualDrawingProps var1);

    public CTApplicationNonVisualDrawingProps addNewNvPr();
}


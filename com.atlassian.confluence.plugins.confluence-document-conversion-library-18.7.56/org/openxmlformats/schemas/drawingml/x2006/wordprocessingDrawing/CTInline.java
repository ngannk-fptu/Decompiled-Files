/*
 * Decompiled with CFR 0.152.
 */
package org.openxmlformats.schemas.drawingml.x2006.wordprocessingDrawing;

import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.openxmlformats.schemas.drawingml.x2006.main.CTGraphicalObject;
import org.openxmlformats.schemas.drawingml.x2006.main.CTNonVisualDrawingProps;
import org.openxmlformats.schemas.drawingml.x2006.main.CTNonVisualGraphicFrameProperties;
import org.openxmlformats.schemas.drawingml.x2006.main.CTPositiveSize2D;
import org.openxmlformats.schemas.drawingml.x2006.wordprocessingDrawing.CTEffectExtent;
import org.openxmlformats.schemas.drawingml.x2006.wordprocessingDrawing.STWrapDistance;

public interface CTInline
extends XmlObject {
    public static final DocumentFactory<CTInline> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "ctinline5726type");
    public static final SchemaType type = Factory.getType();

    public CTPositiveSize2D getExtent();

    public void setExtent(CTPositiveSize2D var1);

    public CTPositiveSize2D addNewExtent();

    public CTEffectExtent getEffectExtent();

    public boolean isSetEffectExtent();

    public void setEffectExtent(CTEffectExtent var1);

    public CTEffectExtent addNewEffectExtent();

    public void unsetEffectExtent();

    public CTNonVisualDrawingProps getDocPr();

    public void setDocPr(CTNonVisualDrawingProps var1);

    public CTNonVisualDrawingProps addNewDocPr();

    public CTNonVisualGraphicFrameProperties getCNvGraphicFramePr();

    public boolean isSetCNvGraphicFramePr();

    public void setCNvGraphicFramePr(CTNonVisualGraphicFrameProperties var1);

    public CTNonVisualGraphicFrameProperties addNewCNvGraphicFramePr();

    public void unsetCNvGraphicFramePr();

    public CTGraphicalObject getGraphic();

    public void setGraphic(CTGraphicalObject var1);

    public CTGraphicalObject addNewGraphic();

    public long getDistT();

    public STWrapDistance xgetDistT();

    public boolean isSetDistT();

    public void setDistT(long var1);

    public void xsetDistT(STWrapDistance var1);

    public void unsetDistT();

    public long getDistB();

    public STWrapDistance xgetDistB();

    public boolean isSetDistB();

    public void setDistB(long var1);

    public void xsetDistB(STWrapDistance var1);

    public void unsetDistB();

    public long getDistL();

    public STWrapDistance xgetDistL();

    public boolean isSetDistL();

    public void setDistL(long var1);

    public void xsetDistL(STWrapDistance var1);

    public void unsetDistL();

    public long getDistR();

    public STWrapDistance xgetDistR();

    public boolean isSetDistR();

    public void setDistR(long var1);

    public void xsetDistR(STWrapDistance var1);

    public void unsetDistR();
}


/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.openxmlformats.schemas.drawingml.x2006.wordprocessingDrawing.CTWrapThrough
 *  org.openxmlformats.schemas.drawingml.x2006.wordprocessingDrawing.CTWrapTight
 *  org.openxmlformats.schemas.drawingml.x2006.wordprocessingDrawing.CTWrapTopBottom
 */
package org.openxmlformats.schemas.drawingml.x2006.wordprocessingDrawing;

import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlBoolean;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.XmlUnsignedInt;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.openxmlformats.schemas.drawingml.x2006.main.CTGraphicalObject;
import org.openxmlformats.schemas.drawingml.x2006.main.CTNonVisualDrawingProps;
import org.openxmlformats.schemas.drawingml.x2006.main.CTNonVisualGraphicFrameProperties;
import org.openxmlformats.schemas.drawingml.x2006.main.CTPoint2D;
import org.openxmlformats.schemas.drawingml.x2006.main.CTPositiveSize2D;
import org.openxmlformats.schemas.drawingml.x2006.wordprocessingDrawing.CTEffectExtent;
import org.openxmlformats.schemas.drawingml.x2006.wordprocessingDrawing.CTPosH;
import org.openxmlformats.schemas.drawingml.x2006.wordprocessingDrawing.CTPosV;
import org.openxmlformats.schemas.drawingml.x2006.wordprocessingDrawing.CTWrapNone;
import org.openxmlformats.schemas.drawingml.x2006.wordprocessingDrawing.CTWrapSquare;
import org.openxmlformats.schemas.drawingml.x2006.wordprocessingDrawing.CTWrapThrough;
import org.openxmlformats.schemas.drawingml.x2006.wordprocessingDrawing.CTWrapTight;
import org.openxmlformats.schemas.drawingml.x2006.wordprocessingDrawing.CTWrapTopBottom;
import org.openxmlformats.schemas.drawingml.x2006.wordprocessingDrawing.STWrapDistance;

public interface CTAnchor
extends XmlObject {
    public static final DocumentFactory<CTAnchor> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "ctanchorff8atype");
    public static final SchemaType type = Factory.getType();

    public CTPoint2D getSimplePos();

    public void setSimplePos(CTPoint2D var1);

    public CTPoint2D addNewSimplePos();

    public CTPosH getPositionH();

    public void setPositionH(CTPosH var1);

    public CTPosH addNewPositionH();

    public CTPosV getPositionV();

    public void setPositionV(CTPosV var1);

    public CTPosV addNewPositionV();

    public CTPositiveSize2D getExtent();

    public void setExtent(CTPositiveSize2D var1);

    public CTPositiveSize2D addNewExtent();

    public CTEffectExtent getEffectExtent();

    public boolean isSetEffectExtent();

    public void setEffectExtent(CTEffectExtent var1);

    public CTEffectExtent addNewEffectExtent();

    public void unsetEffectExtent();

    public CTWrapNone getWrapNone();

    public boolean isSetWrapNone();

    public void setWrapNone(CTWrapNone var1);

    public CTWrapNone addNewWrapNone();

    public void unsetWrapNone();

    public CTWrapSquare getWrapSquare();

    public boolean isSetWrapSquare();

    public void setWrapSquare(CTWrapSquare var1);

    public CTWrapSquare addNewWrapSquare();

    public void unsetWrapSquare();

    public CTWrapTight getWrapTight();

    public boolean isSetWrapTight();

    public void setWrapTight(CTWrapTight var1);

    public CTWrapTight addNewWrapTight();

    public void unsetWrapTight();

    public CTWrapThrough getWrapThrough();

    public boolean isSetWrapThrough();

    public void setWrapThrough(CTWrapThrough var1);

    public CTWrapThrough addNewWrapThrough();

    public void unsetWrapThrough();

    public CTWrapTopBottom getWrapTopAndBottom();

    public boolean isSetWrapTopAndBottom();

    public void setWrapTopAndBottom(CTWrapTopBottom var1);

    public CTWrapTopBottom addNewWrapTopAndBottom();

    public void unsetWrapTopAndBottom();

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

    public boolean getSimplePos2();

    public XmlBoolean xgetSimplePos2();

    public boolean isSetSimplePos2();

    public void setSimplePos2(boolean var1);

    public void xsetSimplePos2(XmlBoolean var1);

    public void unsetSimplePos2();

    public long getRelativeHeight();

    public XmlUnsignedInt xgetRelativeHeight();

    public void setRelativeHeight(long var1);

    public void xsetRelativeHeight(XmlUnsignedInt var1);

    public boolean getBehindDoc();

    public XmlBoolean xgetBehindDoc();

    public void setBehindDoc(boolean var1);

    public void xsetBehindDoc(XmlBoolean var1);

    public boolean getLocked();

    public XmlBoolean xgetLocked();

    public void setLocked(boolean var1);

    public void xsetLocked(XmlBoolean var1);

    public boolean getLayoutInCell();

    public XmlBoolean xgetLayoutInCell();

    public void setLayoutInCell(boolean var1);

    public void xsetLayoutInCell(XmlBoolean var1);

    public boolean getHidden();

    public XmlBoolean xgetHidden();

    public boolean isSetHidden();

    public void setHidden(boolean var1);

    public void xsetHidden(XmlBoolean var1);

    public void unsetHidden();

    public boolean getAllowOverlap();

    public XmlBoolean xgetAllowOverlap();

    public void setAllowOverlap(boolean var1);

    public void xsetAllowOverlap(XmlBoolean var1);
}


/*
 * Decompiled with CFR 0.152.
 */
package org.openxmlformats.schemas.drawingml.x2006.spreadsheetDrawing;

import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlBoolean;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.XmlString;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.openxmlformats.schemas.drawingml.x2006.main.CTGraphicalObject;
import org.openxmlformats.schemas.drawingml.x2006.main.CTTransform2D;
import org.openxmlformats.schemas.drawingml.x2006.spreadsheetDrawing.CTGraphicalObjectFrameNonVisual;

public interface CTGraphicalObjectFrame
extends XmlObject {
    public static final DocumentFactory<CTGraphicalObjectFrame> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "ctgraphicalobjectframe536ftype");
    public static final SchemaType type = Factory.getType();

    public CTGraphicalObjectFrameNonVisual getNvGraphicFramePr();

    public void setNvGraphicFramePr(CTGraphicalObjectFrameNonVisual var1);

    public CTGraphicalObjectFrameNonVisual addNewNvGraphicFramePr();

    public CTTransform2D getXfrm();

    public void setXfrm(CTTransform2D var1);

    public CTTransform2D addNewXfrm();

    public CTGraphicalObject getGraphic();

    public void setGraphic(CTGraphicalObject var1);

    public CTGraphicalObject addNewGraphic();

    public String getMacro();

    public XmlString xgetMacro();

    public boolean isSetMacro();

    public void setMacro(String var1);

    public void xsetMacro(XmlString var1);

    public void unsetMacro();

    public boolean getFPublished();

    public XmlBoolean xgetFPublished();

    public boolean isSetFPublished();

    public void setFPublished(boolean var1);

    public void xsetFPublished(XmlBoolean var1);

    public void unsetFPublished();
}


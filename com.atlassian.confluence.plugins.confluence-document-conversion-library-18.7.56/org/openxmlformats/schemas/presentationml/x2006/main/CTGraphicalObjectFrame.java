/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.openxmlformats.schemas.presentationml.x2006.main.CTExtensionListModify
 */
package org.openxmlformats.schemas.presentationml.x2006.main;

import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.openxmlformats.schemas.drawingml.x2006.main.CTGraphicalObject;
import org.openxmlformats.schemas.drawingml.x2006.main.CTTransform2D;
import org.openxmlformats.schemas.drawingml.x2006.main.STBlackWhiteMode;
import org.openxmlformats.schemas.presentationml.x2006.main.CTExtensionListModify;
import org.openxmlformats.schemas.presentationml.x2006.main.CTGraphicalObjectFrameNonVisual;

public interface CTGraphicalObjectFrame
extends XmlObject {
    public static final DocumentFactory<CTGraphicalObjectFrame> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "ctgraphicalobjectframebfeatype");
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

    public CTExtensionListModify getExtLst();

    public boolean isSetExtLst();

    public void setExtLst(CTExtensionListModify var1);

    public CTExtensionListModify addNewExtLst();

    public void unsetExtLst();

    public STBlackWhiteMode.Enum getBwMode();

    public STBlackWhiteMode xgetBwMode();

    public boolean isSetBwMode();

    public void setBwMode(STBlackWhiteMode.Enum var1);

    public void xsetBwMode(STBlackWhiteMode var1);

    public void unsetBwMode();
}


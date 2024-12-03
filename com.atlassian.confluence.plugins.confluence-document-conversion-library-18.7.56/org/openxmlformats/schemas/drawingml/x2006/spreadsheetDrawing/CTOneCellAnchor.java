/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.openxmlformats.schemas.drawingml.x2006.spreadsheetDrawing.CTRel
 */
package org.openxmlformats.schemas.drawingml.x2006.spreadsheetDrawing;

import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.openxmlformats.schemas.drawingml.x2006.main.CTPositiveSize2D;
import org.openxmlformats.schemas.drawingml.x2006.spreadsheetDrawing.CTAnchorClientData;
import org.openxmlformats.schemas.drawingml.x2006.spreadsheetDrawing.CTConnector;
import org.openxmlformats.schemas.drawingml.x2006.spreadsheetDrawing.CTGraphicalObjectFrame;
import org.openxmlformats.schemas.drawingml.x2006.spreadsheetDrawing.CTGroupShape;
import org.openxmlformats.schemas.drawingml.x2006.spreadsheetDrawing.CTMarker;
import org.openxmlformats.schemas.drawingml.x2006.spreadsheetDrawing.CTPicture;
import org.openxmlformats.schemas.drawingml.x2006.spreadsheetDrawing.CTRel;
import org.openxmlformats.schemas.drawingml.x2006.spreadsheetDrawing.CTShape;

public interface CTOneCellAnchor
extends XmlObject {
    public static final DocumentFactory<CTOneCellAnchor> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "ctonecellanchor0527type");
    public static final SchemaType type = Factory.getType();

    public CTMarker getFrom();

    public void setFrom(CTMarker var1);

    public CTMarker addNewFrom();

    public CTPositiveSize2D getExt();

    public void setExt(CTPositiveSize2D var1);

    public CTPositiveSize2D addNewExt();

    public CTShape getSp();

    public boolean isSetSp();

    public void setSp(CTShape var1);

    public CTShape addNewSp();

    public void unsetSp();

    public CTGroupShape getGrpSp();

    public boolean isSetGrpSp();

    public void setGrpSp(CTGroupShape var1);

    public CTGroupShape addNewGrpSp();

    public void unsetGrpSp();

    public CTGraphicalObjectFrame getGraphicFrame();

    public boolean isSetGraphicFrame();

    public void setGraphicFrame(CTGraphicalObjectFrame var1);

    public CTGraphicalObjectFrame addNewGraphicFrame();

    public void unsetGraphicFrame();

    public CTConnector getCxnSp();

    public boolean isSetCxnSp();

    public void setCxnSp(CTConnector var1);

    public CTConnector addNewCxnSp();

    public void unsetCxnSp();

    public CTPicture getPic();

    public boolean isSetPic();

    public void setPic(CTPicture var1);

    public CTPicture addNewPic();

    public void unsetPic();

    public CTRel getContentPart();

    public boolean isSetContentPart();

    public void setContentPart(CTRel var1);

    public CTRel addNewContentPart();

    public void unsetContentPart();

    public CTAnchorClientData getClientData();

    public void setClientData(CTAnchorClientData var1);

    public CTAnchorClientData addNewClientData();
}


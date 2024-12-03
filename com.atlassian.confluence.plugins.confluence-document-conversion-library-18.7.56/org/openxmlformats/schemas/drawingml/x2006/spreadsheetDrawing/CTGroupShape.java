/*
 * Decompiled with CFR 0.152.
 */
package org.openxmlformats.schemas.drawingml.x2006.spreadsheetDrawing;

import java.util.List;
import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.openxmlformats.schemas.drawingml.x2006.main.CTGroupShapeProperties;
import org.openxmlformats.schemas.drawingml.x2006.spreadsheetDrawing.CTConnector;
import org.openxmlformats.schemas.drawingml.x2006.spreadsheetDrawing.CTGraphicalObjectFrame;
import org.openxmlformats.schemas.drawingml.x2006.spreadsheetDrawing.CTGroupShapeNonVisual;
import org.openxmlformats.schemas.drawingml.x2006.spreadsheetDrawing.CTPicture;
import org.openxmlformats.schemas.drawingml.x2006.spreadsheetDrawing.CTShape;

public interface CTGroupShape
extends XmlObject {
    public static final DocumentFactory<CTGroupShape> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "ctgroupshape6c36type");
    public static final SchemaType type = Factory.getType();

    public CTGroupShapeNonVisual getNvGrpSpPr();

    public void setNvGrpSpPr(CTGroupShapeNonVisual var1);

    public CTGroupShapeNonVisual addNewNvGrpSpPr();

    public CTGroupShapeProperties getGrpSpPr();

    public void setGrpSpPr(CTGroupShapeProperties var1);

    public CTGroupShapeProperties addNewGrpSpPr();

    public List<CTShape> getSpList();

    public CTShape[] getSpArray();

    public CTShape getSpArray(int var1);

    public int sizeOfSpArray();

    public void setSpArray(CTShape[] var1);

    public void setSpArray(int var1, CTShape var2);

    public CTShape insertNewSp(int var1);

    public CTShape addNewSp();

    public void removeSp(int var1);

    public List<CTGroupShape> getGrpSpList();

    public CTGroupShape[] getGrpSpArray();

    public CTGroupShape getGrpSpArray(int var1);

    public int sizeOfGrpSpArray();

    public void setGrpSpArray(CTGroupShape[] var1);

    public void setGrpSpArray(int var1, CTGroupShape var2);

    public CTGroupShape insertNewGrpSp(int var1);

    public CTGroupShape addNewGrpSp();

    public void removeGrpSp(int var1);

    public List<CTGraphicalObjectFrame> getGraphicFrameList();

    public CTGraphicalObjectFrame[] getGraphicFrameArray();

    public CTGraphicalObjectFrame getGraphicFrameArray(int var1);

    public int sizeOfGraphicFrameArray();

    public void setGraphicFrameArray(CTGraphicalObjectFrame[] var1);

    public void setGraphicFrameArray(int var1, CTGraphicalObjectFrame var2);

    public CTGraphicalObjectFrame insertNewGraphicFrame(int var1);

    public CTGraphicalObjectFrame addNewGraphicFrame();

    public void removeGraphicFrame(int var1);

    public List<CTConnector> getCxnSpList();

    public CTConnector[] getCxnSpArray();

    public CTConnector getCxnSpArray(int var1);

    public int sizeOfCxnSpArray();

    public void setCxnSpArray(CTConnector[] var1);

    public void setCxnSpArray(int var1, CTConnector var2);

    public CTConnector insertNewCxnSp(int var1);

    public CTConnector addNewCxnSp();

    public void removeCxnSp(int var1);

    public List<CTPicture> getPicList();

    public CTPicture[] getPicArray();

    public CTPicture getPicArray(int var1);

    public int sizeOfPicArray();

    public void setPicArray(CTPicture[] var1);

    public void setPicArray(int var1, CTPicture var2);

    public CTPicture insertNewPic(int var1);

    public CTPicture addNewPic();

    public void removePic(int var1);
}


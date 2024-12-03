/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.openxmlformats.schemas.drawingml.x2006.main.CTFlatText
 *  org.openxmlformats.schemas.drawingml.x2006.main.CTPresetTextShape
 *  org.openxmlformats.schemas.drawingml.x2006.main.STTextColumnCount
 */
package org.openxmlformats.schemas.drawingml.x2006.main;

import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlBoolean;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.openxmlformats.schemas.drawingml.x2006.main.CTFlatText;
import org.openxmlformats.schemas.drawingml.x2006.main.CTOfficeArtExtensionList;
import org.openxmlformats.schemas.drawingml.x2006.main.CTPresetTextShape;
import org.openxmlformats.schemas.drawingml.x2006.main.CTScene3D;
import org.openxmlformats.schemas.drawingml.x2006.main.CTShape3D;
import org.openxmlformats.schemas.drawingml.x2006.main.CTTextNoAutofit;
import org.openxmlformats.schemas.drawingml.x2006.main.CTTextNormalAutofit;
import org.openxmlformats.schemas.drawingml.x2006.main.CTTextShapeAutofit;
import org.openxmlformats.schemas.drawingml.x2006.main.STAngle;
import org.openxmlformats.schemas.drawingml.x2006.main.STCoordinate32;
import org.openxmlformats.schemas.drawingml.x2006.main.STPositiveCoordinate32;
import org.openxmlformats.schemas.drawingml.x2006.main.STTextAnchoringType;
import org.openxmlformats.schemas.drawingml.x2006.main.STTextColumnCount;
import org.openxmlformats.schemas.drawingml.x2006.main.STTextHorzOverflowType;
import org.openxmlformats.schemas.drawingml.x2006.main.STTextVertOverflowType;
import org.openxmlformats.schemas.drawingml.x2006.main.STTextVerticalType;
import org.openxmlformats.schemas.drawingml.x2006.main.STTextWrappingType;

public interface CTTextBodyProperties
extends XmlObject {
    public static final DocumentFactory<CTTextBodyProperties> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "cttextbodyproperties87ddtype");
    public static final SchemaType type = Factory.getType();

    public CTPresetTextShape getPrstTxWarp();

    public boolean isSetPrstTxWarp();

    public void setPrstTxWarp(CTPresetTextShape var1);

    public CTPresetTextShape addNewPrstTxWarp();

    public void unsetPrstTxWarp();

    public CTTextNoAutofit getNoAutofit();

    public boolean isSetNoAutofit();

    public void setNoAutofit(CTTextNoAutofit var1);

    public CTTextNoAutofit addNewNoAutofit();

    public void unsetNoAutofit();

    public CTTextNormalAutofit getNormAutofit();

    public boolean isSetNormAutofit();

    public void setNormAutofit(CTTextNormalAutofit var1);

    public CTTextNormalAutofit addNewNormAutofit();

    public void unsetNormAutofit();

    public CTTextShapeAutofit getSpAutoFit();

    public boolean isSetSpAutoFit();

    public void setSpAutoFit(CTTextShapeAutofit var1);

    public CTTextShapeAutofit addNewSpAutoFit();

    public void unsetSpAutoFit();

    public CTScene3D getScene3D();

    public boolean isSetScene3D();

    public void setScene3D(CTScene3D var1);

    public CTScene3D addNewScene3D();

    public void unsetScene3D();

    public CTShape3D getSp3D();

    public boolean isSetSp3D();

    public void setSp3D(CTShape3D var1);

    public CTShape3D addNewSp3D();

    public void unsetSp3D();

    public CTFlatText getFlatTx();

    public boolean isSetFlatTx();

    public void setFlatTx(CTFlatText var1);

    public CTFlatText addNewFlatTx();

    public void unsetFlatTx();

    public CTOfficeArtExtensionList getExtLst();

    public boolean isSetExtLst();

    public void setExtLst(CTOfficeArtExtensionList var1);

    public CTOfficeArtExtensionList addNewExtLst();

    public void unsetExtLst();

    public int getRot();

    public STAngle xgetRot();

    public boolean isSetRot();

    public void setRot(int var1);

    public void xsetRot(STAngle var1);

    public void unsetRot();

    public boolean getSpcFirstLastPara();

    public XmlBoolean xgetSpcFirstLastPara();

    public boolean isSetSpcFirstLastPara();

    public void setSpcFirstLastPara(boolean var1);

    public void xsetSpcFirstLastPara(XmlBoolean var1);

    public void unsetSpcFirstLastPara();

    public STTextVertOverflowType.Enum getVertOverflow();

    public STTextVertOverflowType xgetVertOverflow();

    public boolean isSetVertOverflow();

    public void setVertOverflow(STTextVertOverflowType.Enum var1);

    public void xsetVertOverflow(STTextVertOverflowType var1);

    public void unsetVertOverflow();

    public STTextHorzOverflowType.Enum getHorzOverflow();

    public STTextHorzOverflowType xgetHorzOverflow();

    public boolean isSetHorzOverflow();

    public void setHorzOverflow(STTextHorzOverflowType.Enum var1);

    public void xsetHorzOverflow(STTextHorzOverflowType var1);

    public void unsetHorzOverflow();

    public STTextVerticalType.Enum getVert();

    public STTextVerticalType xgetVert();

    public boolean isSetVert();

    public void setVert(STTextVerticalType.Enum var1);

    public void xsetVert(STTextVerticalType var1);

    public void unsetVert();

    public STTextWrappingType.Enum getWrap();

    public STTextWrappingType xgetWrap();

    public boolean isSetWrap();

    public void setWrap(STTextWrappingType.Enum var1);

    public void xsetWrap(STTextWrappingType var1);

    public void unsetWrap();

    public Object getLIns();

    public STCoordinate32 xgetLIns();

    public boolean isSetLIns();

    public void setLIns(Object var1);

    public void xsetLIns(STCoordinate32 var1);

    public void unsetLIns();

    public Object getTIns();

    public STCoordinate32 xgetTIns();

    public boolean isSetTIns();

    public void setTIns(Object var1);

    public void xsetTIns(STCoordinate32 var1);

    public void unsetTIns();

    public Object getRIns();

    public STCoordinate32 xgetRIns();

    public boolean isSetRIns();

    public void setRIns(Object var1);

    public void xsetRIns(STCoordinate32 var1);

    public void unsetRIns();

    public Object getBIns();

    public STCoordinate32 xgetBIns();

    public boolean isSetBIns();

    public void setBIns(Object var1);

    public void xsetBIns(STCoordinate32 var1);

    public void unsetBIns();

    public int getNumCol();

    public STTextColumnCount xgetNumCol();

    public boolean isSetNumCol();

    public void setNumCol(int var1);

    public void xsetNumCol(STTextColumnCount var1);

    public void unsetNumCol();

    public int getSpcCol();

    public STPositiveCoordinate32 xgetSpcCol();

    public boolean isSetSpcCol();

    public void setSpcCol(int var1);

    public void xsetSpcCol(STPositiveCoordinate32 var1);

    public void unsetSpcCol();

    public boolean getRtlCol();

    public XmlBoolean xgetRtlCol();

    public boolean isSetRtlCol();

    public void setRtlCol(boolean var1);

    public void xsetRtlCol(XmlBoolean var1);

    public void unsetRtlCol();

    public boolean getFromWordArt();

    public XmlBoolean xgetFromWordArt();

    public boolean isSetFromWordArt();

    public void setFromWordArt(boolean var1);

    public void xsetFromWordArt(XmlBoolean var1);

    public void unsetFromWordArt();

    public STTextAnchoringType.Enum getAnchor();

    public STTextAnchoringType xgetAnchor();

    public boolean isSetAnchor();

    public void setAnchor(STTextAnchoringType.Enum var1);

    public void xsetAnchor(STTextAnchoringType var1);

    public void unsetAnchor();

    public boolean getAnchorCtr();

    public XmlBoolean xgetAnchorCtr();

    public boolean isSetAnchorCtr();

    public void setAnchorCtr(boolean var1);

    public void xsetAnchorCtr(XmlBoolean var1);

    public void unsetAnchorCtr();

    public boolean getForceAA();

    public XmlBoolean xgetForceAA();

    public boolean isSetForceAA();

    public void setForceAA(boolean var1);

    public void xsetForceAA(XmlBoolean var1);

    public void unsetForceAA();

    public boolean getUpright();

    public XmlBoolean xgetUpright();

    public boolean isSetUpright();

    public void setUpright(boolean var1);

    public void xsetUpright(XmlBoolean var1);

    public void unsetUpright();

    public boolean getCompatLnSpc();

    public XmlBoolean xgetCompatLnSpc();

    public boolean isSetCompatLnSpc();

    public void setCompatLnSpc(boolean var1);

    public void xsetCompatLnSpc(XmlBoolean var1);

    public void unsetCompatLnSpc();
}


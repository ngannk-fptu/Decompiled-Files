/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.openxmlformats.schemas.drawingml.x2006.main.CTCell3D
 *  org.openxmlformats.schemas.drawingml.x2006.main.CTHeaders
 */
package org.openxmlformats.schemas.drawingml.x2006.main;

import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlBoolean;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.openxmlformats.schemas.drawingml.x2006.main.CTBlipFillProperties;
import org.openxmlformats.schemas.drawingml.x2006.main.CTCell3D;
import org.openxmlformats.schemas.drawingml.x2006.main.CTGradientFillProperties;
import org.openxmlformats.schemas.drawingml.x2006.main.CTGroupFillProperties;
import org.openxmlformats.schemas.drawingml.x2006.main.CTHeaders;
import org.openxmlformats.schemas.drawingml.x2006.main.CTLineProperties;
import org.openxmlformats.schemas.drawingml.x2006.main.CTNoFillProperties;
import org.openxmlformats.schemas.drawingml.x2006.main.CTOfficeArtExtensionList;
import org.openxmlformats.schemas.drawingml.x2006.main.CTPatternFillProperties;
import org.openxmlformats.schemas.drawingml.x2006.main.CTSolidColorFillProperties;
import org.openxmlformats.schemas.drawingml.x2006.main.STCoordinate32;
import org.openxmlformats.schemas.drawingml.x2006.main.STTextAnchoringType;
import org.openxmlformats.schemas.drawingml.x2006.main.STTextHorzOverflowType;
import org.openxmlformats.schemas.drawingml.x2006.main.STTextVerticalType;

public interface CTTableCellProperties
extends XmlObject {
    public static final DocumentFactory<CTTableCellProperties> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "cttablecellproperties1614type");
    public static final SchemaType type = Factory.getType();

    public CTLineProperties getLnL();

    public boolean isSetLnL();

    public void setLnL(CTLineProperties var1);

    public CTLineProperties addNewLnL();

    public void unsetLnL();

    public CTLineProperties getLnR();

    public boolean isSetLnR();

    public void setLnR(CTLineProperties var1);

    public CTLineProperties addNewLnR();

    public void unsetLnR();

    public CTLineProperties getLnT();

    public boolean isSetLnT();

    public void setLnT(CTLineProperties var1);

    public CTLineProperties addNewLnT();

    public void unsetLnT();

    public CTLineProperties getLnB();

    public boolean isSetLnB();

    public void setLnB(CTLineProperties var1);

    public CTLineProperties addNewLnB();

    public void unsetLnB();

    public CTLineProperties getLnTlToBr();

    public boolean isSetLnTlToBr();

    public void setLnTlToBr(CTLineProperties var1);

    public CTLineProperties addNewLnTlToBr();

    public void unsetLnTlToBr();

    public CTLineProperties getLnBlToTr();

    public boolean isSetLnBlToTr();

    public void setLnBlToTr(CTLineProperties var1);

    public CTLineProperties addNewLnBlToTr();

    public void unsetLnBlToTr();

    public CTCell3D getCell3D();

    public boolean isSetCell3D();

    public void setCell3D(CTCell3D var1);

    public CTCell3D addNewCell3D();

    public void unsetCell3D();

    public CTNoFillProperties getNoFill();

    public boolean isSetNoFill();

    public void setNoFill(CTNoFillProperties var1);

    public CTNoFillProperties addNewNoFill();

    public void unsetNoFill();

    public CTSolidColorFillProperties getSolidFill();

    public boolean isSetSolidFill();

    public void setSolidFill(CTSolidColorFillProperties var1);

    public CTSolidColorFillProperties addNewSolidFill();

    public void unsetSolidFill();

    public CTGradientFillProperties getGradFill();

    public boolean isSetGradFill();

    public void setGradFill(CTGradientFillProperties var1);

    public CTGradientFillProperties addNewGradFill();

    public void unsetGradFill();

    public CTBlipFillProperties getBlipFill();

    public boolean isSetBlipFill();

    public void setBlipFill(CTBlipFillProperties var1);

    public CTBlipFillProperties addNewBlipFill();

    public void unsetBlipFill();

    public CTPatternFillProperties getPattFill();

    public boolean isSetPattFill();

    public void setPattFill(CTPatternFillProperties var1);

    public CTPatternFillProperties addNewPattFill();

    public void unsetPattFill();

    public CTGroupFillProperties getGrpFill();

    public boolean isSetGrpFill();

    public void setGrpFill(CTGroupFillProperties var1);

    public CTGroupFillProperties addNewGrpFill();

    public void unsetGrpFill();

    public CTHeaders getHeaders();

    public boolean isSetHeaders();

    public void setHeaders(CTHeaders var1);

    public CTHeaders addNewHeaders();

    public void unsetHeaders();

    public CTOfficeArtExtensionList getExtLst();

    public boolean isSetExtLst();

    public void setExtLst(CTOfficeArtExtensionList var1);

    public CTOfficeArtExtensionList addNewExtLst();

    public void unsetExtLst();

    public Object getMarL();

    public STCoordinate32 xgetMarL();

    public boolean isSetMarL();

    public void setMarL(Object var1);

    public void xsetMarL(STCoordinate32 var1);

    public void unsetMarL();

    public Object getMarR();

    public STCoordinate32 xgetMarR();

    public boolean isSetMarR();

    public void setMarR(Object var1);

    public void xsetMarR(STCoordinate32 var1);

    public void unsetMarR();

    public Object getMarT();

    public STCoordinate32 xgetMarT();

    public boolean isSetMarT();

    public void setMarT(Object var1);

    public void xsetMarT(STCoordinate32 var1);

    public void unsetMarT();

    public Object getMarB();

    public STCoordinate32 xgetMarB();

    public boolean isSetMarB();

    public void setMarB(Object var1);

    public void xsetMarB(STCoordinate32 var1);

    public void unsetMarB();

    public STTextVerticalType.Enum getVert();

    public STTextVerticalType xgetVert();

    public boolean isSetVert();

    public void setVert(STTextVerticalType.Enum var1);

    public void xsetVert(STTextVerticalType var1);

    public void unsetVert();

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

    public STTextHorzOverflowType.Enum getHorzOverflow();

    public STTextHorzOverflowType xgetHorzOverflow();

    public boolean isSetHorzOverflow();

    public void setHorzOverflow(STTextHorzOverflowType.Enum var1);

    public void xsetHorzOverflow(STTextHorzOverflowType var1);

    public void unsetHorzOverflow();
}


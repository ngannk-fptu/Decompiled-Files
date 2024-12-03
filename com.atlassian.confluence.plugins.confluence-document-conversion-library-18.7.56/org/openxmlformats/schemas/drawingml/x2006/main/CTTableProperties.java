/*
 * Decompiled with CFR 0.152.
 */
package org.openxmlformats.schemas.drawingml.x2006.main;

import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlBoolean;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.openxmlformats.schemas.drawingml.x2006.main.CTBlipFillProperties;
import org.openxmlformats.schemas.drawingml.x2006.main.CTEffectContainer;
import org.openxmlformats.schemas.drawingml.x2006.main.CTEffectList;
import org.openxmlformats.schemas.drawingml.x2006.main.CTGradientFillProperties;
import org.openxmlformats.schemas.drawingml.x2006.main.CTGroupFillProperties;
import org.openxmlformats.schemas.drawingml.x2006.main.CTNoFillProperties;
import org.openxmlformats.schemas.drawingml.x2006.main.CTOfficeArtExtensionList;
import org.openxmlformats.schemas.drawingml.x2006.main.CTPatternFillProperties;
import org.openxmlformats.schemas.drawingml.x2006.main.CTSolidColorFillProperties;
import org.openxmlformats.schemas.drawingml.x2006.main.CTTableStyle;
import org.openxmlformats.schemas.officeDocument.x2006.sharedTypes.STGuid;

public interface CTTableProperties
extends XmlObject {
    public static final DocumentFactory<CTTableProperties> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "cttableproperties3512type");
    public static final SchemaType type = Factory.getType();

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

    public CTEffectList getEffectLst();

    public boolean isSetEffectLst();

    public void setEffectLst(CTEffectList var1);

    public CTEffectList addNewEffectLst();

    public void unsetEffectLst();

    public CTEffectContainer getEffectDag();

    public boolean isSetEffectDag();

    public void setEffectDag(CTEffectContainer var1);

    public CTEffectContainer addNewEffectDag();

    public void unsetEffectDag();

    public CTTableStyle getTableStyle();

    public boolean isSetTableStyle();

    public void setTableStyle(CTTableStyle var1);

    public CTTableStyle addNewTableStyle();

    public void unsetTableStyle();

    public String getTableStyleId();

    public STGuid xgetTableStyleId();

    public boolean isSetTableStyleId();

    public void setTableStyleId(String var1);

    public void xsetTableStyleId(STGuid var1);

    public void unsetTableStyleId();

    public CTOfficeArtExtensionList getExtLst();

    public boolean isSetExtLst();

    public void setExtLst(CTOfficeArtExtensionList var1);

    public CTOfficeArtExtensionList addNewExtLst();

    public void unsetExtLst();

    public boolean getRtl();

    public XmlBoolean xgetRtl();

    public boolean isSetRtl();

    public void setRtl(boolean var1);

    public void xsetRtl(XmlBoolean var1);

    public void unsetRtl();

    public boolean getFirstRow();

    public XmlBoolean xgetFirstRow();

    public boolean isSetFirstRow();

    public void setFirstRow(boolean var1);

    public void xsetFirstRow(XmlBoolean var1);

    public void unsetFirstRow();

    public boolean getFirstCol();

    public XmlBoolean xgetFirstCol();

    public boolean isSetFirstCol();

    public void setFirstCol(boolean var1);

    public void xsetFirstCol(XmlBoolean var1);

    public void unsetFirstCol();

    public boolean getLastRow();

    public XmlBoolean xgetLastRow();

    public boolean isSetLastRow();

    public void setLastRow(boolean var1);

    public void xsetLastRow(XmlBoolean var1);

    public void unsetLastRow();

    public boolean getLastCol();

    public XmlBoolean xgetLastCol();

    public boolean isSetLastCol();

    public void setLastCol(boolean var1);

    public void xsetLastCol(XmlBoolean var1);

    public void unsetLastCol();

    public boolean getBandRow();

    public XmlBoolean xgetBandRow();

    public boolean isSetBandRow();

    public void setBandRow(boolean var1);

    public void xsetBandRow(XmlBoolean var1);

    public void unsetBandRow();

    public boolean getBandCol();

    public XmlBoolean xgetBandCol();

    public boolean isSetBandCol();

    public void setBandCol(boolean var1);

    public void xsetBandCol(XmlBoolean var1);

    public void unsetBandCol();
}


/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.openxmlformats.schemas.drawingml.x2006.main.CTBoolean
 *  org.openxmlformats.schemas.drawingml.x2006.main.CTTextUnderlineFillGroupWrapper
 *  org.openxmlformats.schemas.drawingml.x2006.main.CTTextUnderlineLineFollowText
 */
package org.openxmlformats.schemas.drawingml.x2006.main;

import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlBoolean;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.XmlString;
import org.apache.xmlbeans.XmlUnsignedInt;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.openxmlformats.schemas.drawingml.x2006.main.CTBlipFillProperties;
import org.openxmlformats.schemas.drawingml.x2006.main.CTBoolean;
import org.openxmlformats.schemas.drawingml.x2006.main.CTColor;
import org.openxmlformats.schemas.drawingml.x2006.main.CTEffectContainer;
import org.openxmlformats.schemas.drawingml.x2006.main.CTEffectList;
import org.openxmlformats.schemas.drawingml.x2006.main.CTGradientFillProperties;
import org.openxmlformats.schemas.drawingml.x2006.main.CTGroupFillProperties;
import org.openxmlformats.schemas.drawingml.x2006.main.CTHyperlink;
import org.openxmlformats.schemas.drawingml.x2006.main.CTLineProperties;
import org.openxmlformats.schemas.drawingml.x2006.main.CTNoFillProperties;
import org.openxmlformats.schemas.drawingml.x2006.main.CTOfficeArtExtensionList;
import org.openxmlformats.schemas.drawingml.x2006.main.CTPatternFillProperties;
import org.openxmlformats.schemas.drawingml.x2006.main.CTSolidColorFillProperties;
import org.openxmlformats.schemas.drawingml.x2006.main.CTTextFont;
import org.openxmlformats.schemas.drawingml.x2006.main.CTTextUnderlineFillFollowText;
import org.openxmlformats.schemas.drawingml.x2006.main.CTTextUnderlineFillGroupWrapper;
import org.openxmlformats.schemas.drawingml.x2006.main.CTTextUnderlineLineFollowText;
import org.openxmlformats.schemas.drawingml.x2006.main.STPercentage;
import org.openxmlformats.schemas.drawingml.x2006.main.STTextCapsType;
import org.openxmlformats.schemas.drawingml.x2006.main.STTextFontSize;
import org.openxmlformats.schemas.drawingml.x2006.main.STTextNonNegativePoint;
import org.openxmlformats.schemas.drawingml.x2006.main.STTextPoint;
import org.openxmlformats.schemas.drawingml.x2006.main.STTextStrikeType;
import org.openxmlformats.schemas.drawingml.x2006.main.STTextUnderlineType;
import org.openxmlformats.schemas.officeDocument.x2006.sharedTypes.STLang;

public interface CTTextCharacterProperties
extends XmlObject {
    public static final DocumentFactory<CTTextCharacterProperties> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "cttextcharacterproperties76c0type");
    public static final SchemaType type = Factory.getType();

    public CTLineProperties getLn();

    public boolean isSetLn();

    public void setLn(CTLineProperties var1);

    public CTLineProperties addNewLn();

    public void unsetLn();

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

    public CTColor getHighlight();

    public boolean isSetHighlight();

    public void setHighlight(CTColor var1);

    public CTColor addNewHighlight();

    public void unsetHighlight();

    public CTTextUnderlineLineFollowText getULnTx();

    public boolean isSetULnTx();

    public void setULnTx(CTTextUnderlineLineFollowText var1);

    public CTTextUnderlineLineFollowText addNewULnTx();

    public void unsetULnTx();

    public CTLineProperties getULn();

    public boolean isSetULn();

    public void setULn(CTLineProperties var1);

    public CTLineProperties addNewULn();

    public void unsetULn();

    public CTTextUnderlineFillFollowText getUFillTx();

    public boolean isSetUFillTx();

    public void setUFillTx(CTTextUnderlineFillFollowText var1);

    public CTTextUnderlineFillFollowText addNewUFillTx();

    public void unsetUFillTx();

    public CTTextUnderlineFillGroupWrapper getUFill();

    public boolean isSetUFill();

    public void setUFill(CTTextUnderlineFillGroupWrapper var1);

    public CTTextUnderlineFillGroupWrapper addNewUFill();

    public void unsetUFill();

    public CTTextFont getLatin();

    public boolean isSetLatin();

    public void setLatin(CTTextFont var1);

    public CTTextFont addNewLatin();

    public void unsetLatin();

    public CTTextFont getEa();

    public boolean isSetEa();

    public void setEa(CTTextFont var1);

    public CTTextFont addNewEa();

    public void unsetEa();

    public CTTextFont getCs();

    public boolean isSetCs();

    public void setCs(CTTextFont var1);

    public CTTextFont addNewCs();

    public void unsetCs();

    public CTTextFont getSym();

    public boolean isSetSym();

    public void setSym(CTTextFont var1);

    public CTTextFont addNewSym();

    public void unsetSym();

    public CTHyperlink getHlinkClick();

    public boolean isSetHlinkClick();

    public void setHlinkClick(CTHyperlink var1);

    public CTHyperlink addNewHlinkClick();

    public void unsetHlinkClick();

    public CTHyperlink getHlinkMouseOver();

    public boolean isSetHlinkMouseOver();

    public void setHlinkMouseOver(CTHyperlink var1);

    public CTHyperlink addNewHlinkMouseOver();

    public void unsetHlinkMouseOver();

    public CTBoolean getRtl();

    public boolean isSetRtl();

    public void setRtl(CTBoolean var1);

    public CTBoolean addNewRtl();

    public void unsetRtl();

    public CTOfficeArtExtensionList getExtLst();

    public boolean isSetExtLst();

    public void setExtLst(CTOfficeArtExtensionList var1);

    public CTOfficeArtExtensionList addNewExtLst();

    public void unsetExtLst();

    public boolean getKumimoji();

    public XmlBoolean xgetKumimoji();

    public boolean isSetKumimoji();

    public void setKumimoji(boolean var1);

    public void xsetKumimoji(XmlBoolean var1);

    public void unsetKumimoji();

    public String getLang();

    public STLang xgetLang();

    public boolean isSetLang();

    public void setLang(String var1);

    public void xsetLang(STLang var1);

    public void unsetLang();

    public String getAltLang();

    public STLang xgetAltLang();

    public boolean isSetAltLang();

    public void setAltLang(String var1);

    public void xsetAltLang(STLang var1);

    public void unsetAltLang();

    public int getSz();

    public STTextFontSize xgetSz();

    public boolean isSetSz();

    public void setSz(int var1);

    public void xsetSz(STTextFontSize var1);

    public void unsetSz();

    public boolean getB();

    public XmlBoolean xgetB();

    public boolean isSetB();

    public void setB(boolean var1);

    public void xsetB(XmlBoolean var1);

    public void unsetB();

    public boolean getI();

    public XmlBoolean xgetI();

    public boolean isSetI();

    public void setI(boolean var1);

    public void xsetI(XmlBoolean var1);

    public void unsetI();

    public STTextUnderlineType.Enum getU();

    public STTextUnderlineType xgetU();

    public boolean isSetU();

    public void setU(STTextUnderlineType.Enum var1);

    public void xsetU(STTextUnderlineType var1);

    public void unsetU();

    public STTextStrikeType.Enum getStrike();

    public STTextStrikeType xgetStrike();

    public boolean isSetStrike();

    public void setStrike(STTextStrikeType.Enum var1);

    public void xsetStrike(STTextStrikeType var1);

    public void unsetStrike();

    public int getKern();

    public STTextNonNegativePoint xgetKern();

    public boolean isSetKern();

    public void setKern(int var1);

    public void xsetKern(STTextNonNegativePoint var1);

    public void unsetKern();

    public STTextCapsType.Enum getCap();

    public STTextCapsType xgetCap();

    public boolean isSetCap();

    public void setCap(STTextCapsType.Enum var1);

    public void xsetCap(STTextCapsType var1);

    public void unsetCap();

    public Object getSpc();

    public STTextPoint xgetSpc();

    public boolean isSetSpc();

    public void setSpc(Object var1);

    public void xsetSpc(STTextPoint var1);

    public void unsetSpc();

    public boolean getNormalizeH();

    public XmlBoolean xgetNormalizeH();

    public boolean isSetNormalizeH();

    public void setNormalizeH(boolean var1);

    public void xsetNormalizeH(XmlBoolean var1);

    public void unsetNormalizeH();

    public Object getBaseline();

    public STPercentage xgetBaseline();

    public boolean isSetBaseline();

    public void setBaseline(Object var1);

    public void xsetBaseline(STPercentage var1);

    public void unsetBaseline();

    public boolean getNoProof();

    public XmlBoolean xgetNoProof();

    public boolean isSetNoProof();

    public void setNoProof(boolean var1);

    public void xsetNoProof(XmlBoolean var1);

    public void unsetNoProof();

    public boolean getDirty();

    public XmlBoolean xgetDirty();

    public boolean isSetDirty();

    public void setDirty(boolean var1);

    public void xsetDirty(XmlBoolean var1);

    public void unsetDirty();

    public boolean getErr();

    public XmlBoolean xgetErr();

    public boolean isSetErr();

    public void setErr(boolean var1);

    public void xsetErr(XmlBoolean var1);

    public void unsetErr();

    public boolean getSmtClean();

    public XmlBoolean xgetSmtClean();

    public boolean isSetSmtClean();

    public void setSmtClean(boolean var1);

    public void xsetSmtClean(XmlBoolean var1);

    public void unsetSmtClean();

    public long getSmtId();

    public XmlUnsignedInt xgetSmtId();

    public boolean isSetSmtId();

    public void setSmtId(long var1);

    public void xsetSmtId(XmlUnsignedInt var1);

    public void unsetSmtId();

    public String getBmk();

    public XmlString xgetBmk();

    public boolean isSetBmk();

    public void setBmk(String var1);

    public void xsetBmk(XmlString var1);

    public void unsetBmk();
}


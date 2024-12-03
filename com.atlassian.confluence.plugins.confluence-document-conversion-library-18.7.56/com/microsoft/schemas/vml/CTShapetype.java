/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.microsoft.schemas.office.office.CTCallout
 *  com.microsoft.schemas.office.office.CTClipPath
 *  com.microsoft.schemas.office.office.CTExtrusion
 *  com.microsoft.schemas.office.office.CTSkew
 *  com.microsoft.schemas.office.office.STDiagramLayout
 *  com.microsoft.schemas.office.powerpoint.CTRel
 *  com.microsoft.schemas.office.word.CTBorder
 */
package com.microsoft.schemas.vml;

import com.microsoft.schemas.office.excel.CTClientData;
import com.microsoft.schemas.office.office.CTCallout;
import com.microsoft.schemas.office.office.CTClipPath;
import com.microsoft.schemas.office.office.CTComplex;
import com.microsoft.schemas.office.office.CTExtrusion;
import com.microsoft.schemas.office.office.CTLock;
import com.microsoft.schemas.office.office.CTSignatureLine;
import com.microsoft.schemas.office.office.CTSkew;
import com.microsoft.schemas.office.office.STBWMode;
import com.microsoft.schemas.office.office.STConnectorType;
import com.microsoft.schemas.office.office.STDiagramLayout;
import com.microsoft.schemas.office.office.STHrAlign;
import com.microsoft.schemas.office.office.STInsetMode;
import com.microsoft.schemas.office.powerpoint.CTRel;
import com.microsoft.schemas.office.word.CTAnchorLock;
import com.microsoft.schemas.office.word.CTBorder;
import com.microsoft.schemas.office.word.CTWrap;
import com.microsoft.schemas.vml.CTFill;
import com.microsoft.schemas.vml.CTFormulas;
import com.microsoft.schemas.vml.CTHandles;
import com.microsoft.schemas.vml.CTImageData;
import com.microsoft.schemas.vml.CTPath;
import com.microsoft.schemas.vml.CTShadow;
import com.microsoft.schemas.vml.CTStroke;
import com.microsoft.schemas.vml.CTTextPath;
import com.microsoft.schemas.vml.CTTextbox;
import java.math.BigInteger;
import java.util.List;
import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlFloat;
import org.apache.xmlbeans.XmlInteger;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.XmlString;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.openxmlformats.schemas.officeDocument.x2006.sharedTypes.STColorType;
import org.openxmlformats.schemas.officeDocument.x2006.sharedTypes.STTrueFalse;
import org.openxmlformats.schemas.officeDocument.x2006.sharedTypes.STTrueFalseBlank;

public interface CTShapetype
extends XmlObject {
    public static final DocumentFactory<CTShapetype> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "ctshapetype5c6ftype");
    public static final SchemaType type = Factory.getType();

    public List<CTPath> getPathList();

    public CTPath[] getPathArray();

    public CTPath getPathArray(int var1);

    public int sizeOfPathArray();

    public void setPathArray(CTPath[] var1);

    public void setPathArray(int var1, CTPath var2);

    public CTPath insertNewPath(int var1);

    public CTPath addNewPath();

    public void removePath(int var1);

    public List<CTFormulas> getFormulasList();

    public CTFormulas[] getFormulasArray();

    public CTFormulas getFormulasArray(int var1);

    public int sizeOfFormulasArray();

    public void setFormulasArray(CTFormulas[] var1);

    public void setFormulasArray(int var1, CTFormulas var2);

    public CTFormulas insertNewFormulas(int var1);

    public CTFormulas addNewFormulas();

    public void removeFormulas(int var1);

    public List<CTHandles> getHandlesList();

    public CTHandles[] getHandlesArray();

    public CTHandles getHandlesArray(int var1);

    public int sizeOfHandlesArray();

    public void setHandlesArray(CTHandles[] var1);

    public void setHandlesArray(int var1, CTHandles var2);

    public CTHandles insertNewHandles(int var1);

    public CTHandles addNewHandles();

    public void removeHandles(int var1);

    public List<CTFill> getFillList();

    public CTFill[] getFillArray();

    public CTFill getFillArray(int var1);

    public int sizeOfFillArray();

    public void setFillArray(CTFill[] var1);

    public void setFillArray(int var1, CTFill var2);

    public CTFill insertNewFill(int var1);

    public CTFill addNewFill();

    public void removeFill(int var1);

    public List<CTStroke> getStrokeList();

    public CTStroke[] getStrokeArray();

    public CTStroke getStrokeArray(int var1);

    public int sizeOfStrokeArray();

    public void setStrokeArray(CTStroke[] var1);

    public void setStrokeArray(int var1, CTStroke var2);

    public CTStroke insertNewStroke(int var1);

    public CTStroke addNewStroke();

    public void removeStroke(int var1);

    public List<CTShadow> getShadowList();

    public CTShadow[] getShadowArray();

    public CTShadow getShadowArray(int var1);

    public int sizeOfShadowArray();

    public void setShadowArray(CTShadow[] var1);

    public void setShadowArray(int var1, CTShadow var2);

    public CTShadow insertNewShadow(int var1);

    public CTShadow addNewShadow();

    public void removeShadow(int var1);

    public List<CTTextbox> getTextboxList();

    public CTTextbox[] getTextboxArray();

    public CTTextbox getTextboxArray(int var1);

    public int sizeOfTextboxArray();

    public void setTextboxArray(CTTextbox[] var1);

    public void setTextboxArray(int var1, CTTextbox var2);

    public CTTextbox insertNewTextbox(int var1);

    public CTTextbox addNewTextbox();

    public void removeTextbox(int var1);

    public List<CTTextPath> getTextpathList();

    public CTTextPath[] getTextpathArray();

    public CTTextPath getTextpathArray(int var1);

    public int sizeOfTextpathArray();

    public void setTextpathArray(CTTextPath[] var1);

    public void setTextpathArray(int var1, CTTextPath var2);

    public CTTextPath insertNewTextpath(int var1);

    public CTTextPath addNewTextpath();

    public void removeTextpath(int var1);

    public List<CTImageData> getImagedataList();

    public CTImageData[] getImagedataArray();

    public CTImageData getImagedataArray(int var1);

    public int sizeOfImagedataArray();

    public void setImagedataArray(CTImageData[] var1);

    public void setImagedataArray(int var1, CTImageData var2);

    public CTImageData insertNewImagedata(int var1);

    public CTImageData addNewImagedata();

    public void removeImagedata(int var1);

    public List<CTSkew> getSkewList();

    public CTSkew[] getSkewArray();

    public CTSkew getSkewArray(int var1);

    public int sizeOfSkewArray();

    public void setSkewArray(CTSkew[] var1);

    public void setSkewArray(int var1, CTSkew var2);

    public CTSkew insertNewSkew(int var1);

    public CTSkew addNewSkew();

    public void removeSkew(int var1);

    public List<CTExtrusion> getExtrusionList();

    public CTExtrusion[] getExtrusionArray();

    public CTExtrusion getExtrusionArray(int var1);

    public int sizeOfExtrusionArray();

    public void setExtrusionArray(CTExtrusion[] var1);

    public void setExtrusionArray(int var1, CTExtrusion var2);

    public CTExtrusion insertNewExtrusion(int var1);

    public CTExtrusion addNewExtrusion();

    public void removeExtrusion(int var1);

    public List<CTCallout> getCalloutList();

    public CTCallout[] getCalloutArray();

    public CTCallout getCalloutArray(int var1);

    public int sizeOfCalloutArray();

    public void setCalloutArray(CTCallout[] var1);

    public void setCalloutArray(int var1, CTCallout var2);

    public CTCallout insertNewCallout(int var1);

    public CTCallout addNewCallout();

    public void removeCallout(int var1);

    public List<CTLock> getLockList();

    public CTLock[] getLockArray();

    public CTLock getLockArray(int var1);

    public int sizeOfLockArray();

    public void setLockArray(CTLock[] var1);

    public void setLockArray(int var1, CTLock var2);

    public CTLock insertNewLock(int var1);

    public CTLock addNewLock();

    public void removeLock(int var1);

    public List<CTClipPath> getClippathList();

    public CTClipPath[] getClippathArray();

    public CTClipPath getClippathArray(int var1);

    public int sizeOfClippathArray();

    public void setClippathArray(CTClipPath[] var1);

    public void setClippathArray(int var1, CTClipPath var2);

    public CTClipPath insertNewClippath(int var1);

    public CTClipPath addNewClippath();

    public void removeClippath(int var1);

    public List<CTSignatureLine> getSignaturelineList();

    public CTSignatureLine[] getSignaturelineArray();

    public CTSignatureLine getSignaturelineArray(int var1);

    public int sizeOfSignaturelineArray();

    public void setSignaturelineArray(CTSignatureLine[] var1);

    public void setSignaturelineArray(int var1, CTSignatureLine var2);

    public CTSignatureLine insertNewSignatureline(int var1);

    public CTSignatureLine addNewSignatureline();

    public void removeSignatureline(int var1);

    public List<CTWrap> getWrapList();

    public CTWrap[] getWrapArray();

    public CTWrap getWrapArray(int var1);

    public int sizeOfWrapArray();

    public void setWrapArray(CTWrap[] var1);

    public void setWrapArray(int var1, CTWrap var2);

    public CTWrap insertNewWrap(int var1);

    public CTWrap addNewWrap();

    public void removeWrap(int var1);

    public List<CTAnchorLock> getAnchorlockList();

    public CTAnchorLock[] getAnchorlockArray();

    public CTAnchorLock getAnchorlockArray(int var1);

    public int sizeOfAnchorlockArray();

    public void setAnchorlockArray(CTAnchorLock[] var1);

    public void setAnchorlockArray(int var1, CTAnchorLock var2);

    public CTAnchorLock insertNewAnchorlock(int var1);

    public CTAnchorLock addNewAnchorlock();

    public void removeAnchorlock(int var1);

    public List<CTBorder> getBordertopList();

    public CTBorder[] getBordertopArray();

    public CTBorder getBordertopArray(int var1);

    public int sizeOfBordertopArray();

    public void setBordertopArray(CTBorder[] var1);

    public void setBordertopArray(int var1, CTBorder var2);

    public CTBorder insertNewBordertop(int var1);

    public CTBorder addNewBordertop();

    public void removeBordertop(int var1);

    public List<CTBorder> getBorderbottomList();

    public CTBorder[] getBorderbottomArray();

    public CTBorder getBorderbottomArray(int var1);

    public int sizeOfBorderbottomArray();

    public void setBorderbottomArray(CTBorder[] var1);

    public void setBorderbottomArray(int var1, CTBorder var2);

    public CTBorder insertNewBorderbottom(int var1);

    public CTBorder addNewBorderbottom();

    public void removeBorderbottom(int var1);

    public List<CTBorder> getBorderleftList();

    public CTBorder[] getBorderleftArray();

    public CTBorder getBorderleftArray(int var1);

    public int sizeOfBorderleftArray();

    public void setBorderleftArray(CTBorder[] var1);

    public void setBorderleftArray(int var1, CTBorder var2);

    public CTBorder insertNewBorderleft(int var1);

    public CTBorder addNewBorderleft();

    public void removeBorderleft(int var1);

    public List<CTBorder> getBorderrightList();

    public CTBorder[] getBorderrightArray();

    public CTBorder getBorderrightArray(int var1);

    public int sizeOfBorderrightArray();

    public void setBorderrightArray(CTBorder[] var1);

    public void setBorderrightArray(int var1, CTBorder var2);

    public CTBorder insertNewBorderright(int var1);

    public CTBorder addNewBorderright();

    public void removeBorderright(int var1);

    public List<CTClientData> getClientDataList();

    public CTClientData[] getClientDataArray();

    public CTClientData getClientDataArray(int var1);

    public int sizeOfClientDataArray();

    public void setClientDataArray(CTClientData[] var1);

    public void setClientDataArray(int var1, CTClientData var2);

    public CTClientData insertNewClientData(int var1);

    public CTClientData addNewClientData();

    public void removeClientData(int var1);

    public List<CTRel> getTextdataList();

    public CTRel[] getTextdataArray();

    public CTRel getTextdataArray(int var1);

    public int sizeOfTextdataArray();

    public void setTextdataArray(CTRel[] var1);

    public void setTextdataArray(int var1, CTRel var2);

    public CTRel insertNewTextdata(int var1);

    public CTRel addNewTextdata();

    public void removeTextdata(int var1);

    public CTComplex getComplex();

    public boolean isSetComplex();

    public void setComplex(CTComplex var1);

    public CTComplex addNewComplex();

    public void unsetComplex();

    public String getId();

    public XmlString xgetId();

    public boolean isSetId();

    public void setId(String var1);

    public void xsetId(XmlString var1);

    public void unsetId();

    public String getStyle();

    public XmlString xgetStyle();

    public boolean isSetStyle();

    public void setStyle(String var1);

    public void xsetStyle(XmlString var1);

    public void unsetStyle();

    public String getHref();

    public XmlString xgetHref();

    public boolean isSetHref();

    public void setHref(String var1);

    public void xsetHref(XmlString var1);

    public void unsetHref();

    public String getTarget();

    public XmlString xgetTarget();

    public boolean isSetTarget();

    public void setTarget(String var1);

    public void xsetTarget(XmlString var1);

    public void unsetTarget();

    public String getClass1();

    public XmlString xgetClass1();

    public boolean isSetClass1();

    public void setClass1(String var1);

    public void xsetClass1(XmlString var1);

    public void unsetClass1();

    public String getTitle();

    public XmlString xgetTitle();

    public boolean isSetTitle();

    public void setTitle(String var1);

    public void xsetTitle(XmlString var1);

    public void unsetTitle();

    public String getAlt();

    public XmlString xgetAlt();

    public boolean isSetAlt();

    public void setAlt(String var1);

    public void xsetAlt(XmlString var1);

    public void unsetAlt();

    public String getCoordsize();

    public XmlString xgetCoordsize();

    public boolean isSetCoordsize();

    public void setCoordsize(String var1);

    public void xsetCoordsize(XmlString var1);

    public void unsetCoordsize();

    public String getCoordorigin();

    public XmlString xgetCoordorigin();

    public boolean isSetCoordorigin();

    public void setCoordorigin(String var1);

    public void xsetCoordorigin(XmlString var1);

    public void unsetCoordorigin();

    public String getWrapcoords();

    public XmlString xgetWrapcoords();

    public boolean isSetWrapcoords();

    public void setWrapcoords(String var1);

    public void xsetWrapcoords(XmlString var1);

    public void unsetWrapcoords();

    public STTrueFalse.Enum getPrint();

    public STTrueFalse xgetPrint();

    public boolean isSetPrint();

    public void setPrint(STTrueFalse.Enum var1);

    public void xsetPrint(STTrueFalse var1);

    public void unsetPrint();

    public String getSpid();

    public XmlString xgetSpid();

    public boolean isSetSpid();

    public void setSpid(String var1);

    public void xsetSpid(XmlString var1);

    public void unsetSpid();

    public STTrueFalse.Enum getOned();

    public STTrueFalse xgetOned();

    public boolean isSetOned();

    public void setOned(STTrueFalse.Enum var1);

    public void xsetOned(STTrueFalse var1);

    public void unsetOned();

    public BigInteger getRegroupid();

    public XmlInteger xgetRegroupid();

    public boolean isSetRegroupid();

    public void setRegroupid(BigInteger var1);

    public void xsetRegroupid(XmlInteger var1);

    public void unsetRegroupid();

    public STTrueFalse.Enum getDoubleclicknotify();

    public STTrueFalse xgetDoubleclicknotify();

    public boolean isSetDoubleclicknotify();

    public void setDoubleclicknotify(STTrueFalse.Enum var1);

    public void xsetDoubleclicknotify(STTrueFalse var1);

    public void unsetDoubleclicknotify();

    public STTrueFalse.Enum getButton();

    public STTrueFalse xgetButton();

    public boolean isSetButton();

    public void setButton(STTrueFalse.Enum var1);

    public void xsetButton(STTrueFalse var1);

    public void unsetButton();

    public STTrueFalse.Enum getUserhidden();

    public STTrueFalse xgetUserhidden();

    public boolean isSetUserhidden();

    public void setUserhidden(STTrueFalse.Enum var1);

    public void xsetUserhidden(STTrueFalse var1);

    public void unsetUserhidden();

    public STTrueFalse.Enum getBullet();

    public STTrueFalse xgetBullet();

    public boolean isSetBullet();

    public void setBullet(STTrueFalse.Enum var1);

    public void xsetBullet(STTrueFalse var1);

    public void unsetBullet();

    public STTrueFalse.Enum getHr();

    public STTrueFalse xgetHr();

    public boolean isSetHr();

    public void setHr(STTrueFalse.Enum var1);

    public void xsetHr(STTrueFalse var1);

    public void unsetHr();

    public STTrueFalse.Enum getHrstd();

    public STTrueFalse xgetHrstd();

    public boolean isSetHrstd();

    public void setHrstd(STTrueFalse.Enum var1);

    public void xsetHrstd(STTrueFalse var1);

    public void unsetHrstd();

    public STTrueFalse.Enum getHrnoshade();

    public STTrueFalse xgetHrnoshade();

    public boolean isSetHrnoshade();

    public void setHrnoshade(STTrueFalse.Enum var1);

    public void xsetHrnoshade(STTrueFalse var1);

    public void unsetHrnoshade();

    public float getHrpct();

    public XmlFloat xgetHrpct();

    public boolean isSetHrpct();

    public void setHrpct(float var1);

    public void xsetHrpct(XmlFloat var1);

    public void unsetHrpct();

    public STHrAlign.Enum getHralign();

    public STHrAlign xgetHralign();

    public boolean isSetHralign();

    public void setHralign(STHrAlign.Enum var1);

    public void xsetHralign(STHrAlign var1);

    public void unsetHralign();

    public STTrueFalse.Enum getAllowincell();

    public STTrueFalse xgetAllowincell();

    public boolean isSetAllowincell();

    public void setAllowincell(STTrueFalse.Enum var1);

    public void xsetAllowincell(STTrueFalse var1);

    public void unsetAllowincell();

    public STTrueFalse.Enum getAllowoverlap();

    public STTrueFalse xgetAllowoverlap();

    public boolean isSetAllowoverlap();

    public void setAllowoverlap(STTrueFalse.Enum var1);

    public void xsetAllowoverlap(STTrueFalse var1);

    public void unsetAllowoverlap();

    public STTrueFalse.Enum getUserdrawn();

    public STTrueFalse xgetUserdrawn();

    public boolean isSetUserdrawn();

    public void setUserdrawn(STTrueFalse.Enum var1);

    public void xsetUserdrawn(STTrueFalse var1);

    public void unsetUserdrawn();

    public String getBordertopcolor();

    public XmlString xgetBordertopcolor();

    public boolean isSetBordertopcolor();

    public void setBordertopcolor(String var1);

    public void xsetBordertopcolor(XmlString var1);

    public void unsetBordertopcolor();

    public String getBorderleftcolor();

    public XmlString xgetBorderleftcolor();

    public boolean isSetBorderleftcolor();

    public void setBorderleftcolor(String var1);

    public void xsetBorderleftcolor(XmlString var1);

    public void unsetBorderleftcolor();

    public String getBorderbottomcolor();

    public XmlString xgetBorderbottomcolor();

    public boolean isSetBorderbottomcolor();

    public void setBorderbottomcolor(String var1);

    public void xsetBorderbottomcolor(XmlString var1);

    public void unsetBorderbottomcolor();

    public String getBorderrightcolor();

    public XmlString xgetBorderrightcolor();

    public boolean isSetBorderrightcolor();

    public void setBorderrightcolor(String var1);

    public void xsetBorderrightcolor(XmlString var1);

    public void unsetBorderrightcolor();

    public BigInteger getDgmlayout();

    public STDiagramLayout xgetDgmlayout();

    public boolean isSetDgmlayout();

    public void setDgmlayout(BigInteger var1);

    public void xsetDgmlayout(STDiagramLayout var1);

    public void unsetDgmlayout();

    public BigInteger getDgmnodekind();

    public XmlInteger xgetDgmnodekind();

    public boolean isSetDgmnodekind();

    public void setDgmnodekind(BigInteger var1);

    public void xsetDgmnodekind(XmlInteger var1);

    public void unsetDgmnodekind();

    public BigInteger getDgmlayoutmru();

    public STDiagramLayout xgetDgmlayoutmru();

    public boolean isSetDgmlayoutmru();

    public void setDgmlayoutmru(BigInteger var1);

    public void xsetDgmlayoutmru(STDiagramLayout var1);

    public void unsetDgmlayoutmru();

    public STInsetMode.Enum getInsetmode();

    public STInsetMode xgetInsetmode();

    public boolean isSetInsetmode();

    public void setInsetmode(STInsetMode.Enum var1);

    public void xsetInsetmode(STInsetMode var1);

    public void unsetInsetmode();

    public String getChromakey();

    public STColorType xgetChromakey();

    public boolean isSetChromakey();

    public void setChromakey(String var1);

    public void xsetChromakey(STColorType var1);

    public void unsetChromakey();

    public STTrueFalse.Enum getFilled();

    public STTrueFalse xgetFilled();

    public boolean isSetFilled();

    public void setFilled(STTrueFalse.Enum var1);

    public void xsetFilled(STTrueFalse var1);

    public void unsetFilled();

    public String getFillcolor();

    public STColorType xgetFillcolor();

    public boolean isSetFillcolor();

    public void setFillcolor(String var1);

    public void xsetFillcolor(STColorType var1);

    public void unsetFillcolor();

    public String getOpacity();

    public XmlString xgetOpacity();

    public boolean isSetOpacity();

    public void setOpacity(String var1);

    public void xsetOpacity(XmlString var1);

    public void unsetOpacity();

    public STTrueFalse.Enum getStroked();

    public STTrueFalse xgetStroked();

    public boolean isSetStroked();

    public void setStroked(STTrueFalse.Enum var1);

    public void xsetStroked(STTrueFalse var1);

    public void unsetStroked();

    public String getStrokecolor();

    public STColorType xgetStrokecolor();

    public boolean isSetStrokecolor();

    public void setStrokecolor(String var1);

    public void xsetStrokecolor(STColorType var1);

    public void unsetStrokecolor();

    public String getStrokeweight();

    public XmlString xgetStrokeweight();

    public boolean isSetStrokeweight();

    public void setStrokeweight(String var1);

    public void xsetStrokeweight(XmlString var1);

    public void unsetStrokeweight();

    public STTrueFalse.Enum getInsetpen();

    public STTrueFalse xgetInsetpen();

    public boolean isSetInsetpen();

    public void setInsetpen(STTrueFalse.Enum var1);

    public void xsetInsetpen(STTrueFalse var1);

    public void unsetInsetpen();

    public float getSpt();

    public XmlFloat xgetSpt();

    public boolean isSetSpt();

    public void setSpt(float var1);

    public void xsetSpt(XmlFloat var1);

    public void unsetSpt();

    public STConnectorType.Enum getConnectortype();

    public STConnectorType xgetConnectortype();

    public boolean isSetConnectortype();

    public void setConnectortype(STConnectorType.Enum var1);

    public void xsetConnectortype(STConnectorType var1);

    public void unsetConnectortype();

    public STBWMode.Enum getBwmode();

    public STBWMode xgetBwmode();

    public boolean isSetBwmode();

    public void setBwmode(STBWMode.Enum var1);

    public void xsetBwmode(STBWMode var1);

    public void unsetBwmode();

    public STBWMode.Enum getBwpure();

    public STBWMode xgetBwpure();

    public boolean isSetBwpure();

    public void setBwpure(STBWMode.Enum var1);

    public void xsetBwpure(STBWMode var1);

    public void unsetBwpure();

    public STBWMode.Enum getBwnormal();

    public STBWMode xgetBwnormal();

    public boolean isSetBwnormal();

    public void setBwnormal(STBWMode.Enum var1);

    public void xsetBwnormal(STBWMode var1);

    public void unsetBwnormal();

    public STTrueFalse.Enum getForcedash();

    public STTrueFalse xgetForcedash();

    public boolean isSetForcedash();

    public void setForcedash(STTrueFalse.Enum var1);

    public void xsetForcedash(STTrueFalse var1);

    public void unsetForcedash();

    public STTrueFalse.Enum getOleicon();

    public STTrueFalse xgetOleicon();

    public boolean isSetOleicon();

    public void setOleicon(STTrueFalse.Enum var1);

    public void xsetOleicon(STTrueFalse var1);

    public void unsetOleicon();

    public STTrueFalseBlank.Enum getOle();

    public STTrueFalseBlank xgetOle();

    public boolean isSetOle();

    public void setOle(STTrueFalseBlank.Enum var1);

    public void xsetOle(STTrueFalseBlank var1);

    public void unsetOle();

    public STTrueFalse.Enum getPreferrelative();

    public STTrueFalse xgetPreferrelative();

    public boolean isSetPreferrelative();

    public void setPreferrelative(STTrueFalse.Enum var1);

    public void xsetPreferrelative(STTrueFalse var1);

    public void unsetPreferrelative();

    public STTrueFalse.Enum getCliptowrap();

    public STTrueFalse xgetCliptowrap();

    public boolean isSetCliptowrap();

    public void setCliptowrap(STTrueFalse.Enum var1);

    public void xsetCliptowrap(STTrueFalse var1);

    public void unsetCliptowrap();

    public STTrueFalse.Enum getClip();

    public STTrueFalse xgetClip();

    public boolean isSetClip();

    public void setClip(STTrueFalse.Enum var1);

    public void xsetClip(STTrueFalse var1);

    public void unsetClip();

    public String getAdj();

    public XmlString xgetAdj();

    public boolean isSetAdj();

    public void setAdj(String var1);

    public void xsetAdj(XmlString var1);

    public void unsetAdj();

    public String getPath2();

    public XmlString xgetPath2();

    public boolean isSetPath2();

    public void setPath2(String var1);

    public void xsetPath2(XmlString var1);

    public void unsetPath2();

    public String getMaster();

    public XmlString xgetMaster();

    public boolean isSetMaster();

    public void setMaster(String var1);

    public void xsetMaster(XmlString var1);

    public void unsetMaster();
}


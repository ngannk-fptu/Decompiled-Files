/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.microsoft.schemas.office.office.CTCallout
 *  com.microsoft.schemas.office.office.CTClipPath
 *  com.microsoft.schemas.office.office.CTDiagram
 *  com.microsoft.schemas.office.office.CTExtrusion
 *  com.microsoft.schemas.office.office.CTSkew
 *  com.microsoft.schemas.office.office.STDiagramLayout
 *  com.microsoft.schemas.office.powerpoint.CTRel
 *  com.microsoft.schemas.office.word.CTBorder
 *  com.microsoft.schemas.vml.CTArc
 *  com.microsoft.schemas.vml.CTCurve
 *  com.microsoft.schemas.vml.CTImage
 *  com.microsoft.schemas.vml.CTPolyLine
 */
package com.microsoft.schemas.vml;

import com.microsoft.schemas.office.excel.CTClientData;
import com.microsoft.schemas.office.office.CTCallout;
import com.microsoft.schemas.office.office.CTClipPath;
import com.microsoft.schemas.office.office.CTDiagram;
import com.microsoft.schemas.office.office.CTExtrusion;
import com.microsoft.schemas.office.office.CTLock;
import com.microsoft.schemas.office.office.CTSignatureLine;
import com.microsoft.schemas.office.office.CTSkew;
import com.microsoft.schemas.office.office.STDiagramLayout;
import com.microsoft.schemas.office.office.STHrAlign;
import com.microsoft.schemas.office.office.STInsetMode;
import com.microsoft.schemas.office.powerpoint.CTRel;
import com.microsoft.schemas.office.word.CTAnchorLock;
import com.microsoft.schemas.office.word.CTBorder;
import com.microsoft.schemas.office.word.CTWrap;
import com.microsoft.schemas.vml.CTArc;
import com.microsoft.schemas.vml.CTCurve;
import com.microsoft.schemas.vml.CTFill;
import com.microsoft.schemas.vml.CTFormulas;
import com.microsoft.schemas.vml.CTHandles;
import com.microsoft.schemas.vml.CTImage;
import com.microsoft.schemas.vml.CTImageData;
import com.microsoft.schemas.vml.CTLine;
import com.microsoft.schemas.vml.CTOval;
import com.microsoft.schemas.vml.CTPath;
import com.microsoft.schemas.vml.CTPolyLine;
import com.microsoft.schemas.vml.CTRect;
import com.microsoft.schemas.vml.CTRoundRect;
import com.microsoft.schemas.vml.CTShadow;
import com.microsoft.schemas.vml.CTShape;
import com.microsoft.schemas.vml.CTShapetype;
import com.microsoft.schemas.vml.CTStroke;
import com.microsoft.schemas.vml.CTTextPath;
import com.microsoft.schemas.vml.CTTextbox;
import com.microsoft.schemas.vml.STEditAs;
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

public interface CTGroup
extends XmlObject {
    public static final DocumentFactory<CTGroup> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "ctgroup2b13type");
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

    public List<CTGroup> getGroupList();

    public CTGroup[] getGroupArray();

    public CTGroup getGroupArray(int var1);

    public int sizeOfGroupArray();

    public void setGroupArray(CTGroup[] var1);

    public void setGroupArray(int var1, CTGroup var2);

    public CTGroup insertNewGroup(int var1);

    public CTGroup addNewGroup();

    public void removeGroup(int var1);

    public List<CTShape> getShapeList();

    public CTShape[] getShapeArray();

    public CTShape getShapeArray(int var1);

    public int sizeOfShapeArray();

    public void setShapeArray(CTShape[] var1);

    public void setShapeArray(int var1, CTShape var2);

    public CTShape insertNewShape(int var1);

    public CTShape addNewShape();

    public void removeShape(int var1);

    public List<CTShapetype> getShapetypeList();

    public CTShapetype[] getShapetypeArray();

    public CTShapetype getShapetypeArray(int var1);

    public int sizeOfShapetypeArray();

    public void setShapetypeArray(CTShapetype[] var1);

    public void setShapetypeArray(int var1, CTShapetype var2);

    public CTShapetype insertNewShapetype(int var1);

    public CTShapetype addNewShapetype();

    public void removeShapetype(int var1);

    public List<CTArc> getArcList();

    public CTArc[] getArcArray();

    public CTArc getArcArray(int var1);

    public int sizeOfArcArray();

    public void setArcArray(CTArc[] var1);

    public void setArcArray(int var1, CTArc var2);

    public CTArc insertNewArc(int var1);

    public CTArc addNewArc();

    public void removeArc(int var1);

    public List<CTCurve> getCurveList();

    public CTCurve[] getCurveArray();

    public CTCurve getCurveArray(int var1);

    public int sizeOfCurveArray();

    public void setCurveArray(CTCurve[] var1);

    public void setCurveArray(int var1, CTCurve var2);

    public CTCurve insertNewCurve(int var1);

    public CTCurve addNewCurve();

    public void removeCurve(int var1);

    public List<CTImage> getImageList();

    public CTImage[] getImageArray();

    public CTImage getImageArray(int var1);

    public int sizeOfImageArray();

    public void setImageArray(CTImage[] var1);

    public void setImageArray(int var1, CTImage var2);

    public CTImage insertNewImage(int var1);

    public CTImage addNewImage();

    public void removeImage(int var1);

    public List<CTLine> getLineList();

    public CTLine[] getLineArray();

    public CTLine getLineArray(int var1);

    public int sizeOfLineArray();

    public void setLineArray(CTLine[] var1);

    public void setLineArray(int var1, CTLine var2);

    public CTLine insertNewLine(int var1);

    public CTLine addNewLine();

    public void removeLine(int var1);

    public List<CTOval> getOvalList();

    public CTOval[] getOvalArray();

    public CTOval getOvalArray(int var1);

    public int sizeOfOvalArray();

    public void setOvalArray(CTOval[] var1);

    public void setOvalArray(int var1, CTOval var2);

    public CTOval insertNewOval(int var1);

    public CTOval addNewOval();

    public void removeOval(int var1);

    public List<CTPolyLine> getPolylineList();

    public CTPolyLine[] getPolylineArray();

    public CTPolyLine getPolylineArray(int var1);

    public int sizeOfPolylineArray();

    public void setPolylineArray(CTPolyLine[] var1);

    public void setPolylineArray(int var1, CTPolyLine var2);

    public CTPolyLine insertNewPolyline(int var1);

    public CTPolyLine addNewPolyline();

    public void removePolyline(int var1);

    public List<CTRect> getRectList();

    public CTRect[] getRectArray();

    public CTRect getRectArray(int var1);

    public int sizeOfRectArray();

    public void setRectArray(CTRect[] var1);

    public void setRectArray(int var1, CTRect var2);

    public CTRect insertNewRect(int var1);

    public CTRect addNewRect();

    public void removeRect(int var1);

    public List<CTRoundRect> getRoundrectList();

    public CTRoundRect[] getRoundrectArray();

    public CTRoundRect getRoundrectArray(int var1);

    public int sizeOfRoundrectArray();

    public void setRoundrectArray(CTRoundRect[] var1);

    public void setRoundrectArray(int var1, CTRoundRect var2);

    public CTRoundRect insertNewRoundrect(int var1);

    public CTRoundRect addNewRoundrect();

    public void removeRoundrect(int var1);

    public List<CTDiagram> getDiagramList();

    public CTDiagram[] getDiagramArray();

    public CTDiagram getDiagramArray(int var1);

    public int sizeOfDiagramArray();

    public void setDiagramArray(CTDiagram[] var1);

    public void setDiagramArray(int var1, CTDiagram var2);

    public CTDiagram insertNewDiagram(int var1);

    public CTDiagram addNewDiagram();

    public void removeDiagram(int var1);

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

    public STEditAs.Enum getEditas();

    public STEditAs xgetEditas();

    public boolean isSetEditas();

    public void setEditas(STEditAs.Enum var1);

    public void xsetEditas(STEditAs var1);

    public void unsetEditas();

    public String getTableproperties();

    public XmlString xgetTableproperties();

    public boolean isSetTableproperties();

    public void setTableproperties(String var1);

    public void xsetTableproperties(XmlString var1);

    public void unsetTableproperties();

    public String getTablelimits();

    public XmlString xgetTablelimits();

    public boolean isSetTablelimits();

    public void setTablelimits(String var1);

    public void xsetTablelimits(XmlString var1);

    public void unsetTablelimits();
}


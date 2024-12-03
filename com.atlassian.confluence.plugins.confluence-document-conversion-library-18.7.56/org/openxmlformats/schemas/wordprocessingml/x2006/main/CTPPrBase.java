/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTextboxTightWrap
 */
package org.openxmlformats.schemas.wordprocessingml.x2006.main;

import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTCnf;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTDecimalNumber;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTFramePr;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTInd;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTJc;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTNumPr;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTOnOff;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTPBdr;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTShd;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTSpacing;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTString;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTabs;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTextAlignment;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTextDirection;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTextboxTightWrap;

public interface CTPPrBase
extends XmlObject {
    public static final DocumentFactory<CTPPrBase> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "ctpprbasebaeftype");
    public static final SchemaType type = Factory.getType();

    public CTString getPStyle();

    public boolean isSetPStyle();

    public void setPStyle(CTString var1);

    public CTString addNewPStyle();

    public void unsetPStyle();

    public CTOnOff getKeepNext();

    public boolean isSetKeepNext();

    public void setKeepNext(CTOnOff var1);

    public CTOnOff addNewKeepNext();

    public void unsetKeepNext();

    public CTOnOff getKeepLines();

    public boolean isSetKeepLines();

    public void setKeepLines(CTOnOff var1);

    public CTOnOff addNewKeepLines();

    public void unsetKeepLines();

    public CTOnOff getPageBreakBefore();

    public boolean isSetPageBreakBefore();

    public void setPageBreakBefore(CTOnOff var1);

    public CTOnOff addNewPageBreakBefore();

    public void unsetPageBreakBefore();

    public CTFramePr getFramePr();

    public boolean isSetFramePr();

    public void setFramePr(CTFramePr var1);

    public CTFramePr addNewFramePr();

    public void unsetFramePr();

    public CTOnOff getWidowControl();

    public boolean isSetWidowControl();

    public void setWidowControl(CTOnOff var1);

    public CTOnOff addNewWidowControl();

    public void unsetWidowControl();

    public CTNumPr getNumPr();

    public boolean isSetNumPr();

    public void setNumPr(CTNumPr var1);

    public CTNumPr addNewNumPr();

    public void unsetNumPr();

    public CTOnOff getSuppressLineNumbers();

    public boolean isSetSuppressLineNumbers();

    public void setSuppressLineNumbers(CTOnOff var1);

    public CTOnOff addNewSuppressLineNumbers();

    public void unsetSuppressLineNumbers();

    public CTPBdr getPBdr();

    public boolean isSetPBdr();

    public void setPBdr(CTPBdr var1);

    public CTPBdr addNewPBdr();

    public void unsetPBdr();

    public CTShd getShd();

    public boolean isSetShd();

    public void setShd(CTShd var1);

    public CTShd addNewShd();

    public void unsetShd();

    public CTTabs getTabs();

    public boolean isSetTabs();

    public void setTabs(CTTabs var1);

    public CTTabs addNewTabs();

    public void unsetTabs();

    public CTOnOff getSuppressAutoHyphens();

    public boolean isSetSuppressAutoHyphens();

    public void setSuppressAutoHyphens(CTOnOff var1);

    public CTOnOff addNewSuppressAutoHyphens();

    public void unsetSuppressAutoHyphens();

    public CTOnOff getKinsoku();

    public boolean isSetKinsoku();

    public void setKinsoku(CTOnOff var1);

    public CTOnOff addNewKinsoku();

    public void unsetKinsoku();

    public CTOnOff getWordWrap();

    public boolean isSetWordWrap();

    public void setWordWrap(CTOnOff var1);

    public CTOnOff addNewWordWrap();

    public void unsetWordWrap();

    public CTOnOff getOverflowPunct();

    public boolean isSetOverflowPunct();

    public void setOverflowPunct(CTOnOff var1);

    public CTOnOff addNewOverflowPunct();

    public void unsetOverflowPunct();

    public CTOnOff getTopLinePunct();

    public boolean isSetTopLinePunct();

    public void setTopLinePunct(CTOnOff var1);

    public CTOnOff addNewTopLinePunct();

    public void unsetTopLinePunct();

    public CTOnOff getAutoSpaceDE();

    public boolean isSetAutoSpaceDE();

    public void setAutoSpaceDE(CTOnOff var1);

    public CTOnOff addNewAutoSpaceDE();

    public void unsetAutoSpaceDE();

    public CTOnOff getAutoSpaceDN();

    public boolean isSetAutoSpaceDN();

    public void setAutoSpaceDN(CTOnOff var1);

    public CTOnOff addNewAutoSpaceDN();

    public void unsetAutoSpaceDN();

    public CTOnOff getBidi();

    public boolean isSetBidi();

    public void setBidi(CTOnOff var1);

    public CTOnOff addNewBidi();

    public void unsetBidi();

    public CTOnOff getAdjustRightInd();

    public boolean isSetAdjustRightInd();

    public void setAdjustRightInd(CTOnOff var1);

    public CTOnOff addNewAdjustRightInd();

    public void unsetAdjustRightInd();

    public CTOnOff getSnapToGrid();

    public boolean isSetSnapToGrid();

    public void setSnapToGrid(CTOnOff var1);

    public CTOnOff addNewSnapToGrid();

    public void unsetSnapToGrid();

    public CTSpacing getSpacing();

    public boolean isSetSpacing();

    public void setSpacing(CTSpacing var1);

    public CTSpacing addNewSpacing();

    public void unsetSpacing();

    public CTInd getInd();

    public boolean isSetInd();

    public void setInd(CTInd var1);

    public CTInd addNewInd();

    public void unsetInd();

    public CTOnOff getContextualSpacing();

    public boolean isSetContextualSpacing();

    public void setContextualSpacing(CTOnOff var1);

    public CTOnOff addNewContextualSpacing();

    public void unsetContextualSpacing();

    public CTOnOff getMirrorIndents();

    public boolean isSetMirrorIndents();

    public void setMirrorIndents(CTOnOff var1);

    public CTOnOff addNewMirrorIndents();

    public void unsetMirrorIndents();

    public CTOnOff getSuppressOverlap();

    public boolean isSetSuppressOverlap();

    public void setSuppressOverlap(CTOnOff var1);

    public CTOnOff addNewSuppressOverlap();

    public void unsetSuppressOverlap();

    public CTJc getJc();

    public boolean isSetJc();

    public void setJc(CTJc var1);

    public CTJc addNewJc();

    public void unsetJc();

    public CTTextDirection getTextDirection();

    public boolean isSetTextDirection();

    public void setTextDirection(CTTextDirection var1);

    public CTTextDirection addNewTextDirection();

    public void unsetTextDirection();

    public CTTextAlignment getTextAlignment();

    public boolean isSetTextAlignment();

    public void setTextAlignment(CTTextAlignment var1);

    public CTTextAlignment addNewTextAlignment();

    public void unsetTextAlignment();

    public CTTextboxTightWrap getTextboxTightWrap();

    public boolean isSetTextboxTightWrap();

    public void setTextboxTightWrap(CTTextboxTightWrap var1);

    public CTTextboxTightWrap addNewTextboxTightWrap();

    public void unsetTextboxTightWrap();

    public CTDecimalNumber getOutlineLvl();

    public boolean isSetOutlineLvl();

    public void setOutlineLvl(CTDecimalNumber var1);

    public CTDecimalNumber addNewOutlineLvl();

    public void unsetOutlineLvl();

    public CTDecimalNumber getDivId();

    public boolean isSetDivId();

    public void setDivId(CTDecimalNumber var1);

    public CTDecimalNumber addNewDivId();

    public void unsetDivId();

    public CTCnf getCnfStyle();

    public boolean isSetCnfStyle();

    public void setCnfStyle(CTCnf var1);

    public CTCnf addNewCnfStyle();

    public void unsetCnfStyle();
}


/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.openxmlformats.schemas.wordprocessingml.x2006.main.CTEastAsianLayout
 *  org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTextEffect
 */
package org.openxmlformats.schemas.wordprocessingml.x2006.main;

import java.util.List;
import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTBorder;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTColor;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTEastAsianLayout;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTEm;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTFitText;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTFonts;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTHighlight;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTHpsMeasure;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTLanguage;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTOnOff;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTParaRPrChange;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTShd;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTSignedHpsMeasure;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTSignedTwipsMeasure;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTString;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTextEffect;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTextScale;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTrackChange;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTUnderline;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTVerticalAlignRun;

public interface CTParaRPr
extends XmlObject {
    public static final DocumentFactory<CTParaRPr> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "ctpararprd6fetype");
    public static final SchemaType type = Factory.getType();

    public CTTrackChange getIns();

    public boolean isSetIns();

    public void setIns(CTTrackChange var1);

    public CTTrackChange addNewIns();

    public void unsetIns();

    public CTTrackChange getDel();

    public boolean isSetDel();

    public void setDel(CTTrackChange var1);

    public CTTrackChange addNewDel();

    public void unsetDel();

    public CTTrackChange getMoveFrom();

    public boolean isSetMoveFrom();

    public void setMoveFrom(CTTrackChange var1);

    public CTTrackChange addNewMoveFrom();

    public void unsetMoveFrom();

    public CTTrackChange getMoveTo();

    public boolean isSetMoveTo();

    public void setMoveTo(CTTrackChange var1);

    public CTTrackChange addNewMoveTo();

    public void unsetMoveTo();

    public List<CTString> getRStyleList();

    public CTString[] getRStyleArray();

    public CTString getRStyleArray(int var1);

    public int sizeOfRStyleArray();

    public void setRStyleArray(CTString[] var1);

    public void setRStyleArray(int var1, CTString var2);

    public CTString insertNewRStyle(int var1);

    public CTString addNewRStyle();

    public void removeRStyle(int var1);

    public List<CTFonts> getRFontsList();

    public CTFonts[] getRFontsArray();

    public CTFonts getRFontsArray(int var1);

    public int sizeOfRFontsArray();

    public void setRFontsArray(CTFonts[] var1);

    public void setRFontsArray(int var1, CTFonts var2);

    public CTFonts insertNewRFonts(int var1);

    public CTFonts addNewRFonts();

    public void removeRFonts(int var1);

    public List<CTOnOff> getBList();

    public CTOnOff[] getBArray();

    public CTOnOff getBArray(int var1);

    public int sizeOfBArray();

    public void setBArray(CTOnOff[] var1);

    public void setBArray(int var1, CTOnOff var2);

    public CTOnOff insertNewB(int var1);

    public CTOnOff addNewB();

    public void removeB(int var1);

    public List<CTOnOff> getBCsList();

    public CTOnOff[] getBCsArray();

    public CTOnOff getBCsArray(int var1);

    public int sizeOfBCsArray();

    public void setBCsArray(CTOnOff[] var1);

    public void setBCsArray(int var1, CTOnOff var2);

    public CTOnOff insertNewBCs(int var1);

    public CTOnOff addNewBCs();

    public void removeBCs(int var1);

    public List<CTOnOff> getIList();

    public CTOnOff[] getIArray();

    public CTOnOff getIArray(int var1);

    public int sizeOfIArray();

    public void setIArray(CTOnOff[] var1);

    public void setIArray(int var1, CTOnOff var2);

    public CTOnOff insertNewI(int var1);

    public CTOnOff addNewI();

    public void removeI(int var1);

    public List<CTOnOff> getICsList();

    public CTOnOff[] getICsArray();

    public CTOnOff getICsArray(int var1);

    public int sizeOfICsArray();

    public void setICsArray(CTOnOff[] var1);

    public void setICsArray(int var1, CTOnOff var2);

    public CTOnOff insertNewICs(int var1);

    public CTOnOff addNewICs();

    public void removeICs(int var1);

    public List<CTOnOff> getCapsList();

    public CTOnOff[] getCapsArray();

    public CTOnOff getCapsArray(int var1);

    public int sizeOfCapsArray();

    public void setCapsArray(CTOnOff[] var1);

    public void setCapsArray(int var1, CTOnOff var2);

    public CTOnOff insertNewCaps(int var1);

    public CTOnOff addNewCaps();

    public void removeCaps(int var1);

    public List<CTOnOff> getSmallCapsList();

    public CTOnOff[] getSmallCapsArray();

    public CTOnOff getSmallCapsArray(int var1);

    public int sizeOfSmallCapsArray();

    public void setSmallCapsArray(CTOnOff[] var1);

    public void setSmallCapsArray(int var1, CTOnOff var2);

    public CTOnOff insertNewSmallCaps(int var1);

    public CTOnOff addNewSmallCaps();

    public void removeSmallCaps(int var1);

    public List<CTOnOff> getStrikeList();

    public CTOnOff[] getStrikeArray();

    public CTOnOff getStrikeArray(int var1);

    public int sizeOfStrikeArray();

    public void setStrikeArray(CTOnOff[] var1);

    public void setStrikeArray(int var1, CTOnOff var2);

    public CTOnOff insertNewStrike(int var1);

    public CTOnOff addNewStrike();

    public void removeStrike(int var1);

    public List<CTOnOff> getDstrikeList();

    public CTOnOff[] getDstrikeArray();

    public CTOnOff getDstrikeArray(int var1);

    public int sizeOfDstrikeArray();

    public void setDstrikeArray(CTOnOff[] var1);

    public void setDstrikeArray(int var1, CTOnOff var2);

    public CTOnOff insertNewDstrike(int var1);

    public CTOnOff addNewDstrike();

    public void removeDstrike(int var1);

    public List<CTOnOff> getOutlineList();

    public CTOnOff[] getOutlineArray();

    public CTOnOff getOutlineArray(int var1);

    public int sizeOfOutlineArray();

    public void setOutlineArray(CTOnOff[] var1);

    public void setOutlineArray(int var1, CTOnOff var2);

    public CTOnOff insertNewOutline(int var1);

    public CTOnOff addNewOutline();

    public void removeOutline(int var1);

    public List<CTOnOff> getShadowList();

    public CTOnOff[] getShadowArray();

    public CTOnOff getShadowArray(int var1);

    public int sizeOfShadowArray();

    public void setShadowArray(CTOnOff[] var1);

    public void setShadowArray(int var1, CTOnOff var2);

    public CTOnOff insertNewShadow(int var1);

    public CTOnOff addNewShadow();

    public void removeShadow(int var1);

    public List<CTOnOff> getEmbossList();

    public CTOnOff[] getEmbossArray();

    public CTOnOff getEmbossArray(int var1);

    public int sizeOfEmbossArray();

    public void setEmbossArray(CTOnOff[] var1);

    public void setEmbossArray(int var1, CTOnOff var2);

    public CTOnOff insertNewEmboss(int var1);

    public CTOnOff addNewEmboss();

    public void removeEmboss(int var1);

    public List<CTOnOff> getImprintList();

    public CTOnOff[] getImprintArray();

    public CTOnOff getImprintArray(int var1);

    public int sizeOfImprintArray();

    public void setImprintArray(CTOnOff[] var1);

    public void setImprintArray(int var1, CTOnOff var2);

    public CTOnOff insertNewImprint(int var1);

    public CTOnOff addNewImprint();

    public void removeImprint(int var1);

    public List<CTOnOff> getNoProofList();

    public CTOnOff[] getNoProofArray();

    public CTOnOff getNoProofArray(int var1);

    public int sizeOfNoProofArray();

    public void setNoProofArray(CTOnOff[] var1);

    public void setNoProofArray(int var1, CTOnOff var2);

    public CTOnOff insertNewNoProof(int var1);

    public CTOnOff addNewNoProof();

    public void removeNoProof(int var1);

    public List<CTOnOff> getSnapToGridList();

    public CTOnOff[] getSnapToGridArray();

    public CTOnOff getSnapToGridArray(int var1);

    public int sizeOfSnapToGridArray();

    public void setSnapToGridArray(CTOnOff[] var1);

    public void setSnapToGridArray(int var1, CTOnOff var2);

    public CTOnOff insertNewSnapToGrid(int var1);

    public CTOnOff addNewSnapToGrid();

    public void removeSnapToGrid(int var1);

    public List<CTOnOff> getVanishList();

    public CTOnOff[] getVanishArray();

    public CTOnOff getVanishArray(int var1);

    public int sizeOfVanishArray();

    public void setVanishArray(CTOnOff[] var1);

    public void setVanishArray(int var1, CTOnOff var2);

    public CTOnOff insertNewVanish(int var1);

    public CTOnOff addNewVanish();

    public void removeVanish(int var1);

    public List<CTOnOff> getWebHiddenList();

    public CTOnOff[] getWebHiddenArray();

    public CTOnOff getWebHiddenArray(int var1);

    public int sizeOfWebHiddenArray();

    public void setWebHiddenArray(CTOnOff[] var1);

    public void setWebHiddenArray(int var1, CTOnOff var2);

    public CTOnOff insertNewWebHidden(int var1);

    public CTOnOff addNewWebHidden();

    public void removeWebHidden(int var1);

    public List<CTColor> getColorList();

    public CTColor[] getColorArray();

    public CTColor getColorArray(int var1);

    public int sizeOfColorArray();

    public void setColorArray(CTColor[] var1);

    public void setColorArray(int var1, CTColor var2);

    public CTColor insertNewColor(int var1);

    public CTColor addNewColor();

    public void removeColor(int var1);

    public List<CTSignedTwipsMeasure> getSpacingList();

    public CTSignedTwipsMeasure[] getSpacingArray();

    public CTSignedTwipsMeasure getSpacingArray(int var1);

    public int sizeOfSpacingArray();

    public void setSpacingArray(CTSignedTwipsMeasure[] var1);

    public void setSpacingArray(int var1, CTSignedTwipsMeasure var2);

    public CTSignedTwipsMeasure insertNewSpacing(int var1);

    public CTSignedTwipsMeasure addNewSpacing();

    public void removeSpacing(int var1);

    public List<CTTextScale> getWList();

    public CTTextScale[] getWArray();

    public CTTextScale getWArray(int var1);

    public int sizeOfWArray();

    public void setWArray(CTTextScale[] var1);

    public void setWArray(int var1, CTTextScale var2);

    public CTTextScale insertNewW(int var1);

    public CTTextScale addNewW();

    public void removeW(int var1);

    public List<CTHpsMeasure> getKernList();

    public CTHpsMeasure[] getKernArray();

    public CTHpsMeasure getKernArray(int var1);

    public int sizeOfKernArray();

    public void setKernArray(CTHpsMeasure[] var1);

    public void setKernArray(int var1, CTHpsMeasure var2);

    public CTHpsMeasure insertNewKern(int var1);

    public CTHpsMeasure addNewKern();

    public void removeKern(int var1);

    public List<CTSignedHpsMeasure> getPositionList();

    public CTSignedHpsMeasure[] getPositionArray();

    public CTSignedHpsMeasure getPositionArray(int var1);

    public int sizeOfPositionArray();

    public void setPositionArray(CTSignedHpsMeasure[] var1);

    public void setPositionArray(int var1, CTSignedHpsMeasure var2);

    public CTSignedHpsMeasure insertNewPosition(int var1);

    public CTSignedHpsMeasure addNewPosition();

    public void removePosition(int var1);

    public List<CTHpsMeasure> getSzList();

    public CTHpsMeasure[] getSzArray();

    public CTHpsMeasure getSzArray(int var1);

    public int sizeOfSzArray();

    public void setSzArray(CTHpsMeasure[] var1);

    public void setSzArray(int var1, CTHpsMeasure var2);

    public CTHpsMeasure insertNewSz(int var1);

    public CTHpsMeasure addNewSz();

    public void removeSz(int var1);

    public List<CTHpsMeasure> getSzCsList();

    public CTHpsMeasure[] getSzCsArray();

    public CTHpsMeasure getSzCsArray(int var1);

    public int sizeOfSzCsArray();

    public void setSzCsArray(CTHpsMeasure[] var1);

    public void setSzCsArray(int var1, CTHpsMeasure var2);

    public CTHpsMeasure insertNewSzCs(int var1);

    public CTHpsMeasure addNewSzCs();

    public void removeSzCs(int var1);

    public List<CTHighlight> getHighlightList();

    public CTHighlight[] getHighlightArray();

    public CTHighlight getHighlightArray(int var1);

    public int sizeOfHighlightArray();

    public void setHighlightArray(CTHighlight[] var1);

    public void setHighlightArray(int var1, CTHighlight var2);

    public CTHighlight insertNewHighlight(int var1);

    public CTHighlight addNewHighlight();

    public void removeHighlight(int var1);

    public List<CTUnderline> getUList();

    public CTUnderline[] getUArray();

    public CTUnderline getUArray(int var1);

    public int sizeOfUArray();

    public void setUArray(CTUnderline[] var1);

    public void setUArray(int var1, CTUnderline var2);

    public CTUnderline insertNewU(int var1);

    public CTUnderline addNewU();

    public void removeU(int var1);

    public List<CTTextEffect> getEffectList();

    public CTTextEffect[] getEffectArray();

    public CTTextEffect getEffectArray(int var1);

    public int sizeOfEffectArray();

    public void setEffectArray(CTTextEffect[] var1);

    public void setEffectArray(int var1, CTTextEffect var2);

    public CTTextEffect insertNewEffect(int var1);

    public CTTextEffect addNewEffect();

    public void removeEffect(int var1);

    public List<CTBorder> getBdrList();

    public CTBorder[] getBdrArray();

    public CTBorder getBdrArray(int var1);

    public int sizeOfBdrArray();

    public void setBdrArray(CTBorder[] var1);

    public void setBdrArray(int var1, CTBorder var2);

    public CTBorder insertNewBdr(int var1);

    public CTBorder addNewBdr();

    public void removeBdr(int var1);

    public List<CTShd> getShdList();

    public CTShd[] getShdArray();

    public CTShd getShdArray(int var1);

    public int sizeOfShdArray();

    public void setShdArray(CTShd[] var1);

    public void setShdArray(int var1, CTShd var2);

    public CTShd insertNewShd(int var1);

    public CTShd addNewShd();

    public void removeShd(int var1);

    public List<CTFitText> getFitTextList();

    public CTFitText[] getFitTextArray();

    public CTFitText getFitTextArray(int var1);

    public int sizeOfFitTextArray();

    public void setFitTextArray(CTFitText[] var1);

    public void setFitTextArray(int var1, CTFitText var2);

    public CTFitText insertNewFitText(int var1);

    public CTFitText addNewFitText();

    public void removeFitText(int var1);

    public List<CTVerticalAlignRun> getVertAlignList();

    public CTVerticalAlignRun[] getVertAlignArray();

    public CTVerticalAlignRun getVertAlignArray(int var1);

    public int sizeOfVertAlignArray();

    public void setVertAlignArray(CTVerticalAlignRun[] var1);

    public void setVertAlignArray(int var1, CTVerticalAlignRun var2);

    public CTVerticalAlignRun insertNewVertAlign(int var1);

    public CTVerticalAlignRun addNewVertAlign();

    public void removeVertAlign(int var1);

    public List<CTOnOff> getRtlList();

    public CTOnOff[] getRtlArray();

    public CTOnOff getRtlArray(int var1);

    public int sizeOfRtlArray();

    public void setRtlArray(CTOnOff[] var1);

    public void setRtlArray(int var1, CTOnOff var2);

    public CTOnOff insertNewRtl(int var1);

    public CTOnOff addNewRtl();

    public void removeRtl(int var1);

    public List<CTOnOff> getCsList();

    public CTOnOff[] getCsArray();

    public CTOnOff getCsArray(int var1);

    public int sizeOfCsArray();

    public void setCsArray(CTOnOff[] var1);

    public void setCsArray(int var1, CTOnOff var2);

    public CTOnOff insertNewCs(int var1);

    public CTOnOff addNewCs();

    public void removeCs(int var1);

    public List<CTEm> getEmList();

    public CTEm[] getEmArray();

    public CTEm getEmArray(int var1);

    public int sizeOfEmArray();

    public void setEmArray(CTEm[] var1);

    public void setEmArray(int var1, CTEm var2);

    public CTEm insertNewEm(int var1);

    public CTEm addNewEm();

    public void removeEm(int var1);

    public List<CTLanguage> getLangList();

    public CTLanguage[] getLangArray();

    public CTLanguage getLangArray(int var1);

    public int sizeOfLangArray();

    public void setLangArray(CTLanguage[] var1);

    public void setLangArray(int var1, CTLanguage var2);

    public CTLanguage insertNewLang(int var1);

    public CTLanguage addNewLang();

    public void removeLang(int var1);

    public List<CTEastAsianLayout> getEastAsianLayoutList();

    public CTEastAsianLayout[] getEastAsianLayoutArray();

    public CTEastAsianLayout getEastAsianLayoutArray(int var1);

    public int sizeOfEastAsianLayoutArray();

    public void setEastAsianLayoutArray(CTEastAsianLayout[] var1);

    public void setEastAsianLayoutArray(int var1, CTEastAsianLayout var2);

    public CTEastAsianLayout insertNewEastAsianLayout(int var1);

    public CTEastAsianLayout addNewEastAsianLayout();

    public void removeEastAsianLayout(int var1);

    public List<CTOnOff> getSpecVanishList();

    public CTOnOff[] getSpecVanishArray();

    public CTOnOff getSpecVanishArray(int var1);

    public int sizeOfSpecVanishArray();

    public void setSpecVanishArray(CTOnOff[] var1);

    public void setSpecVanishArray(int var1, CTOnOff var2);

    public CTOnOff insertNewSpecVanish(int var1);

    public CTOnOff addNewSpecVanish();

    public void removeSpecVanish(int var1);

    public List<CTOnOff> getOMathList();

    public CTOnOff[] getOMathArray();

    public CTOnOff getOMathArray(int var1);

    public int sizeOfOMathArray();

    public void setOMathArray(CTOnOff[] var1);

    public void setOMathArray(int var1, CTOnOff var2);

    public CTOnOff insertNewOMath(int var1);

    public CTOnOff addNewOMath();

    public void removeOMath(int var1);

    public CTParaRPrChange getRPrChange();

    public boolean isSetRPrChange();

    public void setRPrChange(CTParaRPrChange var1);

    public CTParaRPrChange addNewRPrChange();

    public void unsetRPrChange();
}


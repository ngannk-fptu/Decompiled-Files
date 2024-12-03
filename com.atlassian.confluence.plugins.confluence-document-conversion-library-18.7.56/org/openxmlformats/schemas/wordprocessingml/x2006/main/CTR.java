/*
 * Decompiled with CFR 0.152.
 */
package org.openxmlformats.schemas.wordprocessingml.x2006.main;

import java.util.List;
import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTBr;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTDrawing;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTEmpty;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTFldChar;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTFtnEdnRef;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTMarkup;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTObject;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTPTab;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTPicture;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTRPr;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTRel;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTRuby;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTSym;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTText;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STLongHexNumber;

public interface CTR
extends XmlObject {
    public static final DocumentFactory<CTR> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "ctr8120type");
    public static final SchemaType type = Factory.getType();

    public CTRPr getRPr();

    public boolean isSetRPr();

    public void setRPr(CTRPr var1);

    public CTRPr addNewRPr();

    public void unsetRPr();

    public List<CTBr> getBrList();

    public CTBr[] getBrArray();

    public CTBr getBrArray(int var1);

    public int sizeOfBrArray();

    public void setBrArray(CTBr[] var1);

    public void setBrArray(int var1, CTBr var2);

    public CTBr insertNewBr(int var1);

    public CTBr addNewBr();

    public void removeBr(int var1);

    public List<CTText> getTList();

    public CTText[] getTArray();

    public CTText getTArray(int var1);

    public int sizeOfTArray();

    public void setTArray(CTText[] var1);

    public void setTArray(int var1, CTText var2);

    public CTText insertNewT(int var1);

    public CTText addNewT();

    public void removeT(int var1);

    public List<CTRel> getContentPartList();

    public CTRel[] getContentPartArray();

    public CTRel getContentPartArray(int var1);

    public int sizeOfContentPartArray();

    public void setContentPartArray(CTRel[] var1);

    public void setContentPartArray(int var1, CTRel var2);

    public CTRel insertNewContentPart(int var1);

    public CTRel addNewContentPart();

    public void removeContentPart(int var1);

    public List<CTText> getDelTextList();

    public CTText[] getDelTextArray();

    public CTText getDelTextArray(int var1);

    public int sizeOfDelTextArray();

    public void setDelTextArray(CTText[] var1);

    public void setDelTextArray(int var1, CTText var2);

    public CTText insertNewDelText(int var1);

    public CTText addNewDelText();

    public void removeDelText(int var1);

    public List<CTText> getInstrTextList();

    public CTText[] getInstrTextArray();

    public CTText getInstrTextArray(int var1);

    public int sizeOfInstrTextArray();

    public void setInstrTextArray(CTText[] var1);

    public void setInstrTextArray(int var1, CTText var2);

    public CTText insertNewInstrText(int var1);

    public CTText addNewInstrText();

    public void removeInstrText(int var1);

    public List<CTText> getDelInstrTextList();

    public CTText[] getDelInstrTextArray();

    public CTText getDelInstrTextArray(int var1);

    public int sizeOfDelInstrTextArray();

    public void setDelInstrTextArray(CTText[] var1);

    public void setDelInstrTextArray(int var1, CTText var2);

    public CTText insertNewDelInstrText(int var1);

    public CTText addNewDelInstrText();

    public void removeDelInstrText(int var1);

    public List<CTEmpty> getNoBreakHyphenList();

    public CTEmpty[] getNoBreakHyphenArray();

    public CTEmpty getNoBreakHyphenArray(int var1);

    public int sizeOfNoBreakHyphenArray();

    public void setNoBreakHyphenArray(CTEmpty[] var1);

    public void setNoBreakHyphenArray(int var1, CTEmpty var2);

    public CTEmpty insertNewNoBreakHyphen(int var1);

    public CTEmpty addNewNoBreakHyphen();

    public void removeNoBreakHyphen(int var1);

    public List<CTEmpty> getSoftHyphenList();

    public CTEmpty[] getSoftHyphenArray();

    public CTEmpty getSoftHyphenArray(int var1);

    public int sizeOfSoftHyphenArray();

    public void setSoftHyphenArray(CTEmpty[] var1);

    public void setSoftHyphenArray(int var1, CTEmpty var2);

    public CTEmpty insertNewSoftHyphen(int var1);

    public CTEmpty addNewSoftHyphen();

    public void removeSoftHyphen(int var1);

    public List<CTEmpty> getDayShortList();

    public CTEmpty[] getDayShortArray();

    public CTEmpty getDayShortArray(int var1);

    public int sizeOfDayShortArray();

    public void setDayShortArray(CTEmpty[] var1);

    public void setDayShortArray(int var1, CTEmpty var2);

    public CTEmpty insertNewDayShort(int var1);

    public CTEmpty addNewDayShort();

    public void removeDayShort(int var1);

    public List<CTEmpty> getMonthShortList();

    public CTEmpty[] getMonthShortArray();

    public CTEmpty getMonthShortArray(int var1);

    public int sizeOfMonthShortArray();

    public void setMonthShortArray(CTEmpty[] var1);

    public void setMonthShortArray(int var1, CTEmpty var2);

    public CTEmpty insertNewMonthShort(int var1);

    public CTEmpty addNewMonthShort();

    public void removeMonthShort(int var1);

    public List<CTEmpty> getYearShortList();

    public CTEmpty[] getYearShortArray();

    public CTEmpty getYearShortArray(int var1);

    public int sizeOfYearShortArray();

    public void setYearShortArray(CTEmpty[] var1);

    public void setYearShortArray(int var1, CTEmpty var2);

    public CTEmpty insertNewYearShort(int var1);

    public CTEmpty addNewYearShort();

    public void removeYearShort(int var1);

    public List<CTEmpty> getDayLongList();

    public CTEmpty[] getDayLongArray();

    public CTEmpty getDayLongArray(int var1);

    public int sizeOfDayLongArray();

    public void setDayLongArray(CTEmpty[] var1);

    public void setDayLongArray(int var1, CTEmpty var2);

    public CTEmpty insertNewDayLong(int var1);

    public CTEmpty addNewDayLong();

    public void removeDayLong(int var1);

    public List<CTEmpty> getMonthLongList();

    public CTEmpty[] getMonthLongArray();

    public CTEmpty getMonthLongArray(int var1);

    public int sizeOfMonthLongArray();

    public void setMonthLongArray(CTEmpty[] var1);

    public void setMonthLongArray(int var1, CTEmpty var2);

    public CTEmpty insertNewMonthLong(int var1);

    public CTEmpty addNewMonthLong();

    public void removeMonthLong(int var1);

    public List<CTEmpty> getYearLongList();

    public CTEmpty[] getYearLongArray();

    public CTEmpty getYearLongArray(int var1);

    public int sizeOfYearLongArray();

    public void setYearLongArray(CTEmpty[] var1);

    public void setYearLongArray(int var1, CTEmpty var2);

    public CTEmpty insertNewYearLong(int var1);

    public CTEmpty addNewYearLong();

    public void removeYearLong(int var1);

    public List<CTEmpty> getAnnotationRefList();

    public CTEmpty[] getAnnotationRefArray();

    public CTEmpty getAnnotationRefArray(int var1);

    public int sizeOfAnnotationRefArray();

    public void setAnnotationRefArray(CTEmpty[] var1);

    public void setAnnotationRefArray(int var1, CTEmpty var2);

    public CTEmpty insertNewAnnotationRef(int var1);

    public CTEmpty addNewAnnotationRef();

    public void removeAnnotationRef(int var1);

    public List<CTEmpty> getFootnoteRefList();

    public CTEmpty[] getFootnoteRefArray();

    public CTEmpty getFootnoteRefArray(int var1);

    public int sizeOfFootnoteRefArray();

    public void setFootnoteRefArray(CTEmpty[] var1);

    public void setFootnoteRefArray(int var1, CTEmpty var2);

    public CTEmpty insertNewFootnoteRef(int var1);

    public CTEmpty addNewFootnoteRef();

    public void removeFootnoteRef(int var1);

    public List<CTEmpty> getEndnoteRefList();

    public CTEmpty[] getEndnoteRefArray();

    public CTEmpty getEndnoteRefArray(int var1);

    public int sizeOfEndnoteRefArray();

    public void setEndnoteRefArray(CTEmpty[] var1);

    public void setEndnoteRefArray(int var1, CTEmpty var2);

    public CTEmpty insertNewEndnoteRef(int var1);

    public CTEmpty addNewEndnoteRef();

    public void removeEndnoteRef(int var1);

    public List<CTEmpty> getSeparatorList();

    public CTEmpty[] getSeparatorArray();

    public CTEmpty getSeparatorArray(int var1);

    public int sizeOfSeparatorArray();

    public void setSeparatorArray(CTEmpty[] var1);

    public void setSeparatorArray(int var1, CTEmpty var2);

    public CTEmpty insertNewSeparator(int var1);

    public CTEmpty addNewSeparator();

    public void removeSeparator(int var1);

    public List<CTEmpty> getContinuationSeparatorList();

    public CTEmpty[] getContinuationSeparatorArray();

    public CTEmpty getContinuationSeparatorArray(int var1);

    public int sizeOfContinuationSeparatorArray();

    public void setContinuationSeparatorArray(CTEmpty[] var1);

    public void setContinuationSeparatorArray(int var1, CTEmpty var2);

    public CTEmpty insertNewContinuationSeparator(int var1);

    public CTEmpty addNewContinuationSeparator();

    public void removeContinuationSeparator(int var1);

    public List<CTSym> getSymList();

    public CTSym[] getSymArray();

    public CTSym getSymArray(int var1);

    public int sizeOfSymArray();

    public void setSymArray(CTSym[] var1);

    public void setSymArray(int var1, CTSym var2);

    public CTSym insertNewSym(int var1);

    public CTSym addNewSym();

    public void removeSym(int var1);

    public List<CTEmpty> getPgNumList();

    public CTEmpty[] getPgNumArray();

    public CTEmpty getPgNumArray(int var1);

    public int sizeOfPgNumArray();

    public void setPgNumArray(CTEmpty[] var1);

    public void setPgNumArray(int var1, CTEmpty var2);

    public CTEmpty insertNewPgNum(int var1);

    public CTEmpty addNewPgNum();

    public void removePgNum(int var1);

    public List<CTEmpty> getCrList();

    public CTEmpty[] getCrArray();

    public CTEmpty getCrArray(int var1);

    public int sizeOfCrArray();

    public void setCrArray(CTEmpty[] var1);

    public void setCrArray(int var1, CTEmpty var2);

    public CTEmpty insertNewCr(int var1);

    public CTEmpty addNewCr();

    public void removeCr(int var1);

    public List<CTEmpty> getTabList();

    public CTEmpty[] getTabArray();

    public CTEmpty getTabArray(int var1);

    public int sizeOfTabArray();

    public void setTabArray(CTEmpty[] var1);

    public void setTabArray(int var1, CTEmpty var2);

    public CTEmpty insertNewTab(int var1);

    public CTEmpty addNewTab();

    public void removeTab(int var1);

    public List<CTObject> getObjectList();

    public CTObject[] getObjectArray();

    public CTObject getObjectArray(int var1);

    public int sizeOfObjectArray();

    public void setObjectArray(CTObject[] var1);

    public void setObjectArray(int var1, CTObject var2);

    public CTObject insertNewObject(int var1);

    public CTObject addNewObject();

    public void removeObject(int var1);

    public List<CTPicture> getPictList();

    public CTPicture[] getPictArray();

    public CTPicture getPictArray(int var1);

    public int sizeOfPictArray();

    public void setPictArray(CTPicture[] var1);

    public void setPictArray(int var1, CTPicture var2);

    public CTPicture insertNewPict(int var1);

    public CTPicture addNewPict();

    public void removePict(int var1);

    public List<CTFldChar> getFldCharList();

    public CTFldChar[] getFldCharArray();

    public CTFldChar getFldCharArray(int var1);

    public int sizeOfFldCharArray();

    public void setFldCharArray(CTFldChar[] var1);

    public void setFldCharArray(int var1, CTFldChar var2);

    public CTFldChar insertNewFldChar(int var1);

    public CTFldChar addNewFldChar();

    public void removeFldChar(int var1);

    public List<CTRuby> getRubyList();

    public CTRuby[] getRubyArray();

    public CTRuby getRubyArray(int var1);

    public int sizeOfRubyArray();

    public void setRubyArray(CTRuby[] var1);

    public void setRubyArray(int var1, CTRuby var2);

    public CTRuby insertNewRuby(int var1);

    public CTRuby addNewRuby();

    public void removeRuby(int var1);

    public List<CTFtnEdnRef> getFootnoteReferenceList();

    public CTFtnEdnRef[] getFootnoteReferenceArray();

    public CTFtnEdnRef getFootnoteReferenceArray(int var1);

    public int sizeOfFootnoteReferenceArray();

    public void setFootnoteReferenceArray(CTFtnEdnRef[] var1);

    public void setFootnoteReferenceArray(int var1, CTFtnEdnRef var2);

    public CTFtnEdnRef insertNewFootnoteReference(int var1);

    public CTFtnEdnRef addNewFootnoteReference();

    public void removeFootnoteReference(int var1);

    public List<CTFtnEdnRef> getEndnoteReferenceList();

    public CTFtnEdnRef[] getEndnoteReferenceArray();

    public CTFtnEdnRef getEndnoteReferenceArray(int var1);

    public int sizeOfEndnoteReferenceArray();

    public void setEndnoteReferenceArray(CTFtnEdnRef[] var1);

    public void setEndnoteReferenceArray(int var1, CTFtnEdnRef var2);

    public CTFtnEdnRef insertNewEndnoteReference(int var1);

    public CTFtnEdnRef addNewEndnoteReference();

    public void removeEndnoteReference(int var1);

    public List<CTMarkup> getCommentReferenceList();

    public CTMarkup[] getCommentReferenceArray();

    public CTMarkup getCommentReferenceArray(int var1);

    public int sizeOfCommentReferenceArray();

    public void setCommentReferenceArray(CTMarkup[] var1);

    public void setCommentReferenceArray(int var1, CTMarkup var2);

    public CTMarkup insertNewCommentReference(int var1);

    public CTMarkup addNewCommentReference();

    public void removeCommentReference(int var1);

    public List<CTDrawing> getDrawingList();

    public CTDrawing[] getDrawingArray();

    public CTDrawing getDrawingArray(int var1);

    public int sizeOfDrawingArray();

    public void setDrawingArray(CTDrawing[] var1);

    public void setDrawingArray(int var1, CTDrawing var2);

    public CTDrawing insertNewDrawing(int var1);

    public CTDrawing addNewDrawing();

    public void removeDrawing(int var1);

    public List<CTPTab> getPtabList();

    public CTPTab[] getPtabArray();

    public CTPTab getPtabArray(int var1);

    public int sizeOfPtabArray();

    public void setPtabArray(CTPTab[] var1);

    public void setPtabArray(int var1, CTPTab var2);

    public CTPTab insertNewPtab(int var1);

    public CTPTab addNewPtab();

    public void removePtab(int var1);

    public List<CTEmpty> getLastRenderedPageBreakList();

    public CTEmpty[] getLastRenderedPageBreakArray();

    public CTEmpty getLastRenderedPageBreakArray(int var1);

    public int sizeOfLastRenderedPageBreakArray();

    public void setLastRenderedPageBreakArray(CTEmpty[] var1);

    public void setLastRenderedPageBreakArray(int var1, CTEmpty var2);

    public CTEmpty insertNewLastRenderedPageBreak(int var1);

    public CTEmpty addNewLastRenderedPageBreak();

    public void removeLastRenderedPageBreak(int var1);

    public byte[] getRsidRPr();

    public STLongHexNumber xgetRsidRPr();

    public boolean isSetRsidRPr();

    public void setRsidRPr(byte[] var1);

    public void xsetRsidRPr(STLongHexNumber var1);

    public void unsetRsidRPr();

    public byte[] getRsidDel();

    public STLongHexNumber xgetRsidDel();

    public boolean isSetRsidDel();

    public void setRsidDel(byte[] var1);

    public void xsetRsidDel(STLongHexNumber var1);

    public void unsetRsidDel();

    public byte[] getRsidR();

    public STLongHexNumber xgetRsidR();

    public boolean isSetRsidR();

    public void setRsidR(byte[] var1);

    public void xsetRsidR(STLongHexNumber var1);

    public void unsetRsidR();
}


/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.openxmlformats.schemas.wordprocessingml.x2006.main.CTCustomXmlBlock
 */
package org.openxmlformats.schemas.wordprocessingml.x2006.main;

import java.util.List;
import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.openxmlformats.schemas.officeDocument.x2006.math.CTOMath;
import org.openxmlformats.schemas.officeDocument.x2006.math.CTOMathPara;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTAltChunk;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTBookmark;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTCustomXmlBlock;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTMarkup;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTMarkupRange;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTMoveBookmark;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTP;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTPerm;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTPermStart;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTProofErr;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTRunTrackChange;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTSdtBlock;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTbl;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTrackChange;

public interface CTHdrFtr
extends XmlObject {
    public static final DocumentFactory<CTHdrFtr> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "cthdrftr26datype");
    public static final SchemaType type = Factory.getType();

    public List<CTCustomXmlBlock> getCustomXmlList();

    public CTCustomXmlBlock[] getCustomXmlArray();

    public CTCustomXmlBlock getCustomXmlArray(int var1);

    public int sizeOfCustomXmlArray();

    public void setCustomXmlArray(CTCustomXmlBlock[] var1);

    public void setCustomXmlArray(int var1, CTCustomXmlBlock var2);

    public CTCustomXmlBlock insertNewCustomXml(int var1);

    public CTCustomXmlBlock addNewCustomXml();

    public void removeCustomXml(int var1);

    public List<CTSdtBlock> getSdtList();

    public CTSdtBlock[] getSdtArray();

    public CTSdtBlock getSdtArray(int var1);

    public int sizeOfSdtArray();

    public void setSdtArray(CTSdtBlock[] var1);

    public void setSdtArray(int var1, CTSdtBlock var2);

    public CTSdtBlock insertNewSdt(int var1);

    public CTSdtBlock addNewSdt();

    public void removeSdt(int var1);

    public List<CTP> getPList();

    public CTP[] getPArray();

    public CTP getPArray(int var1);

    public int sizeOfPArray();

    public void setPArray(CTP[] var1);

    public void setPArray(int var1, CTP var2);

    public CTP insertNewP(int var1);

    public CTP addNewP();

    public void removeP(int var1);

    public List<CTTbl> getTblList();

    public CTTbl[] getTblArray();

    public CTTbl getTblArray(int var1);

    public int sizeOfTblArray();

    public void setTblArray(CTTbl[] var1);

    public void setTblArray(int var1, CTTbl var2);

    public CTTbl insertNewTbl(int var1);

    public CTTbl addNewTbl();

    public void removeTbl(int var1);

    public List<CTProofErr> getProofErrList();

    public CTProofErr[] getProofErrArray();

    public CTProofErr getProofErrArray(int var1);

    public int sizeOfProofErrArray();

    public void setProofErrArray(CTProofErr[] var1);

    public void setProofErrArray(int var1, CTProofErr var2);

    public CTProofErr insertNewProofErr(int var1);

    public CTProofErr addNewProofErr();

    public void removeProofErr(int var1);

    public List<CTPermStart> getPermStartList();

    public CTPermStart[] getPermStartArray();

    public CTPermStart getPermStartArray(int var1);

    public int sizeOfPermStartArray();

    public void setPermStartArray(CTPermStart[] var1);

    public void setPermStartArray(int var1, CTPermStart var2);

    public CTPermStart insertNewPermStart(int var1);

    public CTPermStart addNewPermStart();

    public void removePermStart(int var1);

    public List<CTPerm> getPermEndList();

    public CTPerm[] getPermEndArray();

    public CTPerm getPermEndArray(int var1);

    public int sizeOfPermEndArray();

    public void setPermEndArray(CTPerm[] var1);

    public void setPermEndArray(int var1, CTPerm var2);

    public CTPerm insertNewPermEnd(int var1);

    public CTPerm addNewPermEnd();

    public void removePermEnd(int var1);

    public List<CTBookmark> getBookmarkStartList();

    public CTBookmark[] getBookmarkStartArray();

    public CTBookmark getBookmarkStartArray(int var1);

    public int sizeOfBookmarkStartArray();

    public void setBookmarkStartArray(CTBookmark[] var1);

    public void setBookmarkStartArray(int var1, CTBookmark var2);

    public CTBookmark insertNewBookmarkStart(int var1);

    public CTBookmark addNewBookmarkStart();

    public void removeBookmarkStart(int var1);

    public List<CTMarkupRange> getBookmarkEndList();

    public CTMarkupRange[] getBookmarkEndArray();

    public CTMarkupRange getBookmarkEndArray(int var1);

    public int sizeOfBookmarkEndArray();

    public void setBookmarkEndArray(CTMarkupRange[] var1);

    public void setBookmarkEndArray(int var1, CTMarkupRange var2);

    public CTMarkupRange insertNewBookmarkEnd(int var1);

    public CTMarkupRange addNewBookmarkEnd();

    public void removeBookmarkEnd(int var1);

    public List<CTMoveBookmark> getMoveFromRangeStartList();

    public CTMoveBookmark[] getMoveFromRangeStartArray();

    public CTMoveBookmark getMoveFromRangeStartArray(int var1);

    public int sizeOfMoveFromRangeStartArray();

    public void setMoveFromRangeStartArray(CTMoveBookmark[] var1);

    public void setMoveFromRangeStartArray(int var1, CTMoveBookmark var2);

    public CTMoveBookmark insertNewMoveFromRangeStart(int var1);

    public CTMoveBookmark addNewMoveFromRangeStart();

    public void removeMoveFromRangeStart(int var1);

    public List<CTMarkupRange> getMoveFromRangeEndList();

    public CTMarkupRange[] getMoveFromRangeEndArray();

    public CTMarkupRange getMoveFromRangeEndArray(int var1);

    public int sizeOfMoveFromRangeEndArray();

    public void setMoveFromRangeEndArray(CTMarkupRange[] var1);

    public void setMoveFromRangeEndArray(int var1, CTMarkupRange var2);

    public CTMarkupRange insertNewMoveFromRangeEnd(int var1);

    public CTMarkupRange addNewMoveFromRangeEnd();

    public void removeMoveFromRangeEnd(int var1);

    public List<CTMoveBookmark> getMoveToRangeStartList();

    public CTMoveBookmark[] getMoveToRangeStartArray();

    public CTMoveBookmark getMoveToRangeStartArray(int var1);

    public int sizeOfMoveToRangeStartArray();

    public void setMoveToRangeStartArray(CTMoveBookmark[] var1);

    public void setMoveToRangeStartArray(int var1, CTMoveBookmark var2);

    public CTMoveBookmark insertNewMoveToRangeStart(int var1);

    public CTMoveBookmark addNewMoveToRangeStart();

    public void removeMoveToRangeStart(int var1);

    public List<CTMarkupRange> getMoveToRangeEndList();

    public CTMarkupRange[] getMoveToRangeEndArray();

    public CTMarkupRange getMoveToRangeEndArray(int var1);

    public int sizeOfMoveToRangeEndArray();

    public void setMoveToRangeEndArray(CTMarkupRange[] var1);

    public void setMoveToRangeEndArray(int var1, CTMarkupRange var2);

    public CTMarkupRange insertNewMoveToRangeEnd(int var1);

    public CTMarkupRange addNewMoveToRangeEnd();

    public void removeMoveToRangeEnd(int var1);

    public List<CTMarkupRange> getCommentRangeStartList();

    public CTMarkupRange[] getCommentRangeStartArray();

    public CTMarkupRange getCommentRangeStartArray(int var1);

    public int sizeOfCommentRangeStartArray();

    public void setCommentRangeStartArray(CTMarkupRange[] var1);

    public void setCommentRangeStartArray(int var1, CTMarkupRange var2);

    public CTMarkupRange insertNewCommentRangeStart(int var1);

    public CTMarkupRange addNewCommentRangeStart();

    public void removeCommentRangeStart(int var1);

    public List<CTMarkupRange> getCommentRangeEndList();

    public CTMarkupRange[] getCommentRangeEndArray();

    public CTMarkupRange getCommentRangeEndArray(int var1);

    public int sizeOfCommentRangeEndArray();

    public void setCommentRangeEndArray(CTMarkupRange[] var1);

    public void setCommentRangeEndArray(int var1, CTMarkupRange var2);

    public CTMarkupRange insertNewCommentRangeEnd(int var1);

    public CTMarkupRange addNewCommentRangeEnd();

    public void removeCommentRangeEnd(int var1);

    public List<CTTrackChange> getCustomXmlInsRangeStartList();

    public CTTrackChange[] getCustomXmlInsRangeStartArray();

    public CTTrackChange getCustomXmlInsRangeStartArray(int var1);

    public int sizeOfCustomXmlInsRangeStartArray();

    public void setCustomXmlInsRangeStartArray(CTTrackChange[] var1);

    public void setCustomXmlInsRangeStartArray(int var1, CTTrackChange var2);

    public CTTrackChange insertNewCustomXmlInsRangeStart(int var1);

    public CTTrackChange addNewCustomXmlInsRangeStart();

    public void removeCustomXmlInsRangeStart(int var1);

    public List<CTMarkup> getCustomXmlInsRangeEndList();

    public CTMarkup[] getCustomXmlInsRangeEndArray();

    public CTMarkup getCustomXmlInsRangeEndArray(int var1);

    public int sizeOfCustomXmlInsRangeEndArray();

    public void setCustomXmlInsRangeEndArray(CTMarkup[] var1);

    public void setCustomXmlInsRangeEndArray(int var1, CTMarkup var2);

    public CTMarkup insertNewCustomXmlInsRangeEnd(int var1);

    public CTMarkup addNewCustomXmlInsRangeEnd();

    public void removeCustomXmlInsRangeEnd(int var1);

    public List<CTTrackChange> getCustomXmlDelRangeStartList();

    public CTTrackChange[] getCustomXmlDelRangeStartArray();

    public CTTrackChange getCustomXmlDelRangeStartArray(int var1);

    public int sizeOfCustomXmlDelRangeStartArray();

    public void setCustomXmlDelRangeStartArray(CTTrackChange[] var1);

    public void setCustomXmlDelRangeStartArray(int var1, CTTrackChange var2);

    public CTTrackChange insertNewCustomXmlDelRangeStart(int var1);

    public CTTrackChange addNewCustomXmlDelRangeStart();

    public void removeCustomXmlDelRangeStart(int var1);

    public List<CTMarkup> getCustomXmlDelRangeEndList();

    public CTMarkup[] getCustomXmlDelRangeEndArray();

    public CTMarkup getCustomXmlDelRangeEndArray(int var1);

    public int sizeOfCustomXmlDelRangeEndArray();

    public void setCustomXmlDelRangeEndArray(CTMarkup[] var1);

    public void setCustomXmlDelRangeEndArray(int var1, CTMarkup var2);

    public CTMarkup insertNewCustomXmlDelRangeEnd(int var1);

    public CTMarkup addNewCustomXmlDelRangeEnd();

    public void removeCustomXmlDelRangeEnd(int var1);

    public List<CTTrackChange> getCustomXmlMoveFromRangeStartList();

    public CTTrackChange[] getCustomXmlMoveFromRangeStartArray();

    public CTTrackChange getCustomXmlMoveFromRangeStartArray(int var1);

    public int sizeOfCustomXmlMoveFromRangeStartArray();

    public void setCustomXmlMoveFromRangeStartArray(CTTrackChange[] var1);

    public void setCustomXmlMoveFromRangeStartArray(int var1, CTTrackChange var2);

    public CTTrackChange insertNewCustomXmlMoveFromRangeStart(int var1);

    public CTTrackChange addNewCustomXmlMoveFromRangeStart();

    public void removeCustomXmlMoveFromRangeStart(int var1);

    public List<CTMarkup> getCustomXmlMoveFromRangeEndList();

    public CTMarkup[] getCustomXmlMoveFromRangeEndArray();

    public CTMarkup getCustomXmlMoveFromRangeEndArray(int var1);

    public int sizeOfCustomXmlMoveFromRangeEndArray();

    public void setCustomXmlMoveFromRangeEndArray(CTMarkup[] var1);

    public void setCustomXmlMoveFromRangeEndArray(int var1, CTMarkup var2);

    public CTMarkup insertNewCustomXmlMoveFromRangeEnd(int var1);

    public CTMarkup addNewCustomXmlMoveFromRangeEnd();

    public void removeCustomXmlMoveFromRangeEnd(int var1);

    public List<CTTrackChange> getCustomXmlMoveToRangeStartList();

    public CTTrackChange[] getCustomXmlMoveToRangeStartArray();

    public CTTrackChange getCustomXmlMoveToRangeStartArray(int var1);

    public int sizeOfCustomXmlMoveToRangeStartArray();

    public void setCustomXmlMoveToRangeStartArray(CTTrackChange[] var1);

    public void setCustomXmlMoveToRangeStartArray(int var1, CTTrackChange var2);

    public CTTrackChange insertNewCustomXmlMoveToRangeStart(int var1);

    public CTTrackChange addNewCustomXmlMoveToRangeStart();

    public void removeCustomXmlMoveToRangeStart(int var1);

    public List<CTMarkup> getCustomXmlMoveToRangeEndList();

    public CTMarkup[] getCustomXmlMoveToRangeEndArray();

    public CTMarkup getCustomXmlMoveToRangeEndArray(int var1);

    public int sizeOfCustomXmlMoveToRangeEndArray();

    public void setCustomXmlMoveToRangeEndArray(CTMarkup[] var1);

    public void setCustomXmlMoveToRangeEndArray(int var1, CTMarkup var2);

    public CTMarkup insertNewCustomXmlMoveToRangeEnd(int var1);

    public CTMarkup addNewCustomXmlMoveToRangeEnd();

    public void removeCustomXmlMoveToRangeEnd(int var1);

    public List<CTRunTrackChange> getInsList();

    public CTRunTrackChange[] getInsArray();

    public CTRunTrackChange getInsArray(int var1);

    public int sizeOfInsArray();

    public void setInsArray(CTRunTrackChange[] var1);

    public void setInsArray(int var1, CTRunTrackChange var2);

    public CTRunTrackChange insertNewIns(int var1);

    public CTRunTrackChange addNewIns();

    public void removeIns(int var1);

    public List<CTRunTrackChange> getDelList();

    public CTRunTrackChange[] getDelArray();

    public CTRunTrackChange getDelArray(int var1);

    public int sizeOfDelArray();

    public void setDelArray(CTRunTrackChange[] var1);

    public void setDelArray(int var1, CTRunTrackChange var2);

    public CTRunTrackChange insertNewDel(int var1);

    public CTRunTrackChange addNewDel();

    public void removeDel(int var1);

    public List<CTRunTrackChange> getMoveFromList();

    public CTRunTrackChange[] getMoveFromArray();

    public CTRunTrackChange getMoveFromArray(int var1);

    public int sizeOfMoveFromArray();

    public void setMoveFromArray(CTRunTrackChange[] var1);

    public void setMoveFromArray(int var1, CTRunTrackChange var2);

    public CTRunTrackChange insertNewMoveFrom(int var1);

    public CTRunTrackChange addNewMoveFrom();

    public void removeMoveFrom(int var1);

    public List<CTRunTrackChange> getMoveToList();

    public CTRunTrackChange[] getMoveToArray();

    public CTRunTrackChange getMoveToArray(int var1);

    public int sizeOfMoveToArray();

    public void setMoveToArray(CTRunTrackChange[] var1);

    public void setMoveToArray(int var1, CTRunTrackChange var2);

    public CTRunTrackChange insertNewMoveTo(int var1);

    public CTRunTrackChange addNewMoveTo();

    public void removeMoveTo(int var1);

    public List<CTOMathPara> getOMathParaList();

    public CTOMathPara[] getOMathParaArray();

    public CTOMathPara getOMathParaArray(int var1);

    public int sizeOfOMathParaArray();

    public void setOMathParaArray(CTOMathPara[] var1);

    public void setOMathParaArray(int var1, CTOMathPara var2);

    public CTOMathPara insertNewOMathPara(int var1);

    public CTOMathPara addNewOMathPara();

    public void removeOMathPara(int var1);

    public List<CTOMath> getOMathList();

    public CTOMath[] getOMathArray();

    public CTOMath getOMathArray(int var1);

    public int sizeOfOMathArray();

    public void setOMathArray(CTOMath[] var1);

    public void setOMathArray(int var1, CTOMath var2);

    public CTOMath insertNewOMath(int var1);

    public CTOMath addNewOMath();

    public void removeOMath(int var1);

    public List<CTAltChunk> getAltChunkList();

    public CTAltChunk[] getAltChunkArray();

    public CTAltChunk getAltChunkArray(int var1);

    public int sizeOfAltChunkArray();

    public void setAltChunkArray(CTAltChunk[] var1);

    public void setAltChunkArray(int var1, CTAltChunk var2);

    public CTAltChunk insertNewAltChunk(int var1);

    public CTAltChunk addNewAltChunk();

    public void removeAltChunk(int var1);
}


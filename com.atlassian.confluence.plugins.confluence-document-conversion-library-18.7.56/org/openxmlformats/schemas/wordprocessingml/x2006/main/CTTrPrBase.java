/*
 * Decompiled with CFR 0.152.
 */
package org.openxmlformats.schemas.wordprocessingml.x2006.main;

import java.util.List;
import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTCnf;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTDecimalNumber;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTHeight;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTJcTable;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTOnOff;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTblWidth;

public interface CTTrPrBase
extends XmlObject {
    public static final DocumentFactory<CTTrPrBase> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "cttrprbase5d77type");
    public static final SchemaType type = Factory.getType();

    public List<CTCnf> getCnfStyleList();

    public CTCnf[] getCnfStyleArray();

    public CTCnf getCnfStyleArray(int var1);

    public int sizeOfCnfStyleArray();

    public void setCnfStyleArray(CTCnf[] var1);

    public void setCnfStyleArray(int var1, CTCnf var2);

    public CTCnf insertNewCnfStyle(int var1);

    public CTCnf addNewCnfStyle();

    public void removeCnfStyle(int var1);

    public List<CTDecimalNumber> getDivIdList();

    public CTDecimalNumber[] getDivIdArray();

    public CTDecimalNumber getDivIdArray(int var1);

    public int sizeOfDivIdArray();

    public void setDivIdArray(CTDecimalNumber[] var1);

    public void setDivIdArray(int var1, CTDecimalNumber var2);

    public CTDecimalNumber insertNewDivId(int var1);

    public CTDecimalNumber addNewDivId();

    public void removeDivId(int var1);

    public List<CTDecimalNumber> getGridBeforeList();

    public CTDecimalNumber[] getGridBeforeArray();

    public CTDecimalNumber getGridBeforeArray(int var1);

    public int sizeOfGridBeforeArray();

    public void setGridBeforeArray(CTDecimalNumber[] var1);

    public void setGridBeforeArray(int var1, CTDecimalNumber var2);

    public CTDecimalNumber insertNewGridBefore(int var1);

    public CTDecimalNumber addNewGridBefore();

    public void removeGridBefore(int var1);

    public List<CTDecimalNumber> getGridAfterList();

    public CTDecimalNumber[] getGridAfterArray();

    public CTDecimalNumber getGridAfterArray(int var1);

    public int sizeOfGridAfterArray();

    public void setGridAfterArray(CTDecimalNumber[] var1);

    public void setGridAfterArray(int var1, CTDecimalNumber var2);

    public CTDecimalNumber insertNewGridAfter(int var1);

    public CTDecimalNumber addNewGridAfter();

    public void removeGridAfter(int var1);

    public List<CTTblWidth> getWBeforeList();

    public CTTblWidth[] getWBeforeArray();

    public CTTblWidth getWBeforeArray(int var1);

    public int sizeOfWBeforeArray();

    public void setWBeforeArray(CTTblWidth[] var1);

    public void setWBeforeArray(int var1, CTTblWidth var2);

    public CTTblWidth insertNewWBefore(int var1);

    public CTTblWidth addNewWBefore();

    public void removeWBefore(int var1);

    public List<CTTblWidth> getWAfterList();

    public CTTblWidth[] getWAfterArray();

    public CTTblWidth getWAfterArray(int var1);

    public int sizeOfWAfterArray();

    public void setWAfterArray(CTTblWidth[] var1);

    public void setWAfterArray(int var1, CTTblWidth var2);

    public CTTblWidth insertNewWAfter(int var1);

    public CTTblWidth addNewWAfter();

    public void removeWAfter(int var1);

    public List<CTOnOff> getCantSplitList();

    public CTOnOff[] getCantSplitArray();

    public CTOnOff getCantSplitArray(int var1);

    public int sizeOfCantSplitArray();

    public void setCantSplitArray(CTOnOff[] var1);

    public void setCantSplitArray(int var1, CTOnOff var2);

    public CTOnOff insertNewCantSplit(int var1);

    public CTOnOff addNewCantSplit();

    public void removeCantSplit(int var1);

    public List<CTHeight> getTrHeightList();

    public CTHeight[] getTrHeightArray();

    public CTHeight getTrHeightArray(int var1);

    public int sizeOfTrHeightArray();

    public void setTrHeightArray(CTHeight[] var1);

    public void setTrHeightArray(int var1, CTHeight var2);

    public CTHeight insertNewTrHeight(int var1);

    public CTHeight addNewTrHeight();

    public void removeTrHeight(int var1);

    public List<CTOnOff> getTblHeaderList();

    public CTOnOff[] getTblHeaderArray();

    public CTOnOff getTblHeaderArray(int var1);

    public int sizeOfTblHeaderArray();

    public void setTblHeaderArray(CTOnOff[] var1);

    public void setTblHeaderArray(int var1, CTOnOff var2);

    public CTOnOff insertNewTblHeader(int var1);

    public CTOnOff addNewTblHeader();

    public void removeTblHeader(int var1);

    public List<CTTblWidth> getTblCellSpacingList();

    public CTTblWidth[] getTblCellSpacingArray();

    public CTTblWidth getTblCellSpacingArray(int var1);

    public int sizeOfTblCellSpacingArray();

    public void setTblCellSpacingArray(CTTblWidth[] var1);

    public void setTblCellSpacingArray(int var1, CTTblWidth var2);

    public CTTblWidth insertNewTblCellSpacing(int var1);

    public CTTblWidth addNewTblCellSpacing();

    public void removeTblCellSpacing(int var1);

    public List<CTJcTable> getJcList();

    public CTJcTable[] getJcArray();

    public CTJcTable getJcArray(int var1);

    public int sizeOfJcArray();

    public void setJcArray(CTJcTable[] var1);

    public void setJcArray(int var1, CTJcTable var2);

    public CTJcTable insertNewJc(int var1);

    public CTJcTable addNewJc();

    public void removeJc(int var1);

    public List<CTOnOff> getHiddenList();

    public CTOnOff[] getHiddenArray();

    public CTOnOff getHiddenArray(int var1);

    public int sizeOfHiddenArray();

    public void setHiddenArray(CTOnOff[] var1);

    public void setHiddenArray(int var1, CTOnOff var2);

    public CTOnOff insertNewHidden(int var1);

    public CTOnOff addNewHidden();

    public void removeHidden(int var1);
}


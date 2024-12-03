/*
 * Decompiled with CFR 0.152.
 */
package com.microsoft.schemas.office.excel;

import com.microsoft.schemas.office.excel.STCF;
import com.microsoft.schemas.office.excel.STObjectType;
import java.math.BigInteger;
import java.util.List;
import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlInteger;
import org.apache.xmlbeans.XmlNonNegativeInteger;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.XmlString;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.openxmlformats.schemas.officeDocument.x2006.sharedTypes.STTrueFalseBlank;

public interface CTClientData
extends XmlObject {
    public static final DocumentFactory<CTClientData> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "ctclientdata433btype");
    public static final SchemaType type = Factory.getType();

    public List<STTrueFalseBlank.Enum> getMoveWithCellsList();

    public STTrueFalseBlank.Enum[] getMoveWithCellsArray();

    public STTrueFalseBlank.Enum getMoveWithCellsArray(int var1);

    public List<STTrueFalseBlank> xgetMoveWithCellsList();

    public STTrueFalseBlank[] xgetMoveWithCellsArray();

    public STTrueFalseBlank xgetMoveWithCellsArray(int var1);

    public int sizeOfMoveWithCellsArray();

    public void setMoveWithCellsArray(STTrueFalseBlank.Enum[] var1);

    public void setMoveWithCellsArray(int var1, STTrueFalseBlank.Enum var2);

    public void xsetMoveWithCellsArray(STTrueFalseBlank[] var1);

    public void xsetMoveWithCellsArray(int var1, STTrueFalseBlank var2);

    public void insertMoveWithCells(int var1, STTrueFalseBlank.Enum var2);

    public void addMoveWithCells(STTrueFalseBlank.Enum var1);

    public STTrueFalseBlank insertNewMoveWithCells(int var1);

    public STTrueFalseBlank addNewMoveWithCells();

    public void removeMoveWithCells(int var1);

    public List<STTrueFalseBlank.Enum> getSizeWithCellsList();

    public STTrueFalseBlank.Enum[] getSizeWithCellsArray();

    public STTrueFalseBlank.Enum getSizeWithCellsArray(int var1);

    public List<STTrueFalseBlank> xgetSizeWithCellsList();

    public STTrueFalseBlank[] xgetSizeWithCellsArray();

    public STTrueFalseBlank xgetSizeWithCellsArray(int var1);

    public int sizeOfSizeWithCellsArray();

    public void setSizeWithCellsArray(STTrueFalseBlank.Enum[] var1);

    public void setSizeWithCellsArray(int var1, STTrueFalseBlank.Enum var2);

    public void xsetSizeWithCellsArray(STTrueFalseBlank[] var1);

    public void xsetSizeWithCellsArray(int var1, STTrueFalseBlank var2);

    public void insertSizeWithCells(int var1, STTrueFalseBlank.Enum var2);

    public void addSizeWithCells(STTrueFalseBlank.Enum var1);

    public STTrueFalseBlank insertNewSizeWithCells(int var1);

    public STTrueFalseBlank addNewSizeWithCells();

    public void removeSizeWithCells(int var1);

    public List<String> getAnchorList();

    public String[] getAnchorArray();

    public String getAnchorArray(int var1);

    public List<XmlString> xgetAnchorList();

    public XmlString[] xgetAnchorArray();

    public XmlString xgetAnchorArray(int var1);

    public int sizeOfAnchorArray();

    public void setAnchorArray(String[] var1);

    public void setAnchorArray(int var1, String var2);

    public void xsetAnchorArray(XmlString[] var1);

    public void xsetAnchorArray(int var1, XmlString var2);

    public void insertAnchor(int var1, String var2);

    public void addAnchor(String var1);

    public XmlString insertNewAnchor(int var1);

    public XmlString addNewAnchor();

    public void removeAnchor(int var1);

    public List<STTrueFalseBlank.Enum> getLockedList();

    public STTrueFalseBlank.Enum[] getLockedArray();

    public STTrueFalseBlank.Enum getLockedArray(int var1);

    public List<STTrueFalseBlank> xgetLockedList();

    public STTrueFalseBlank[] xgetLockedArray();

    public STTrueFalseBlank xgetLockedArray(int var1);

    public int sizeOfLockedArray();

    public void setLockedArray(STTrueFalseBlank.Enum[] var1);

    public void setLockedArray(int var1, STTrueFalseBlank.Enum var2);

    public void xsetLockedArray(STTrueFalseBlank[] var1);

    public void xsetLockedArray(int var1, STTrueFalseBlank var2);

    public void insertLocked(int var1, STTrueFalseBlank.Enum var2);

    public void addLocked(STTrueFalseBlank.Enum var1);

    public STTrueFalseBlank insertNewLocked(int var1);

    public STTrueFalseBlank addNewLocked();

    public void removeLocked(int var1);

    public List<STTrueFalseBlank.Enum> getDefaultSizeList();

    public STTrueFalseBlank.Enum[] getDefaultSizeArray();

    public STTrueFalseBlank.Enum getDefaultSizeArray(int var1);

    public List<STTrueFalseBlank> xgetDefaultSizeList();

    public STTrueFalseBlank[] xgetDefaultSizeArray();

    public STTrueFalseBlank xgetDefaultSizeArray(int var1);

    public int sizeOfDefaultSizeArray();

    public void setDefaultSizeArray(STTrueFalseBlank.Enum[] var1);

    public void setDefaultSizeArray(int var1, STTrueFalseBlank.Enum var2);

    public void xsetDefaultSizeArray(STTrueFalseBlank[] var1);

    public void xsetDefaultSizeArray(int var1, STTrueFalseBlank var2);

    public void insertDefaultSize(int var1, STTrueFalseBlank.Enum var2);

    public void addDefaultSize(STTrueFalseBlank.Enum var1);

    public STTrueFalseBlank insertNewDefaultSize(int var1);

    public STTrueFalseBlank addNewDefaultSize();

    public void removeDefaultSize(int var1);

    public List<STTrueFalseBlank.Enum> getPrintObjectList();

    public STTrueFalseBlank.Enum[] getPrintObjectArray();

    public STTrueFalseBlank.Enum getPrintObjectArray(int var1);

    public List<STTrueFalseBlank> xgetPrintObjectList();

    public STTrueFalseBlank[] xgetPrintObjectArray();

    public STTrueFalseBlank xgetPrintObjectArray(int var1);

    public int sizeOfPrintObjectArray();

    public void setPrintObjectArray(STTrueFalseBlank.Enum[] var1);

    public void setPrintObjectArray(int var1, STTrueFalseBlank.Enum var2);

    public void xsetPrintObjectArray(STTrueFalseBlank[] var1);

    public void xsetPrintObjectArray(int var1, STTrueFalseBlank var2);

    public void insertPrintObject(int var1, STTrueFalseBlank.Enum var2);

    public void addPrintObject(STTrueFalseBlank.Enum var1);

    public STTrueFalseBlank insertNewPrintObject(int var1);

    public STTrueFalseBlank addNewPrintObject();

    public void removePrintObject(int var1);

    public List<STTrueFalseBlank.Enum> getDisabledList();

    public STTrueFalseBlank.Enum[] getDisabledArray();

    public STTrueFalseBlank.Enum getDisabledArray(int var1);

    public List<STTrueFalseBlank> xgetDisabledList();

    public STTrueFalseBlank[] xgetDisabledArray();

    public STTrueFalseBlank xgetDisabledArray(int var1);

    public int sizeOfDisabledArray();

    public void setDisabledArray(STTrueFalseBlank.Enum[] var1);

    public void setDisabledArray(int var1, STTrueFalseBlank.Enum var2);

    public void xsetDisabledArray(STTrueFalseBlank[] var1);

    public void xsetDisabledArray(int var1, STTrueFalseBlank var2);

    public void insertDisabled(int var1, STTrueFalseBlank.Enum var2);

    public void addDisabled(STTrueFalseBlank.Enum var1);

    public STTrueFalseBlank insertNewDisabled(int var1);

    public STTrueFalseBlank addNewDisabled();

    public void removeDisabled(int var1);

    public List<STTrueFalseBlank.Enum> getAutoFillList();

    public STTrueFalseBlank.Enum[] getAutoFillArray();

    public STTrueFalseBlank.Enum getAutoFillArray(int var1);

    public List<STTrueFalseBlank> xgetAutoFillList();

    public STTrueFalseBlank[] xgetAutoFillArray();

    public STTrueFalseBlank xgetAutoFillArray(int var1);

    public int sizeOfAutoFillArray();

    public void setAutoFillArray(STTrueFalseBlank.Enum[] var1);

    public void setAutoFillArray(int var1, STTrueFalseBlank.Enum var2);

    public void xsetAutoFillArray(STTrueFalseBlank[] var1);

    public void xsetAutoFillArray(int var1, STTrueFalseBlank var2);

    public void insertAutoFill(int var1, STTrueFalseBlank.Enum var2);

    public void addAutoFill(STTrueFalseBlank.Enum var1);

    public STTrueFalseBlank insertNewAutoFill(int var1);

    public STTrueFalseBlank addNewAutoFill();

    public void removeAutoFill(int var1);

    public List<STTrueFalseBlank.Enum> getAutoLineList();

    public STTrueFalseBlank.Enum[] getAutoLineArray();

    public STTrueFalseBlank.Enum getAutoLineArray(int var1);

    public List<STTrueFalseBlank> xgetAutoLineList();

    public STTrueFalseBlank[] xgetAutoLineArray();

    public STTrueFalseBlank xgetAutoLineArray(int var1);

    public int sizeOfAutoLineArray();

    public void setAutoLineArray(STTrueFalseBlank.Enum[] var1);

    public void setAutoLineArray(int var1, STTrueFalseBlank.Enum var2);

    public void xsetAutoLineArray(STTrueFalseBlank[] var1);

    public void xsetAutoLineArray(int var1, STTrueFalseBlank var2);

    public void insertAutoLine(int var1, STTrueFalseBlank.Enum var2);

    public void addAutoLine(STTrueFalseBlank.Enum var1);

    public STTrueFalseBlank insertNewAutoLine(int var1);

    public STTrueFalseBlank addNewAutoLine();

    public void removeAutoLine(int var1);

    public List<STTrueFalseBlank.Enum> getAutoPictList();

    public STTrueFalseBlank.Enum[] getAutoPictArray();

    public STTrueFalseBlank.Enum getAutoPictArray(int var1);

    public List<STTrueFalseBlank> xgetAutoPictList();

    public STTrueFalseBlank[] xgetAutoPictArray();

    public STTrueFalseBlank xgetAutoPictArray(int var1);

    public int sizeOfAutoPictArray();

    public void setAutoPictArray(STTrueFalseBlank.Enum[] var1);

    public void setAutoPictArray(int var1, STTrueFalseBlank.Enum var2);

    public void xsetAutoPictArray(STTrueFalseBlank[] var1);

    public void xsetAutoPictArray(int var1, STTrueFalseBlank var2);

    public void insertAutoPict(int var1, STTrueFalseBlank.Enum var2);

    public void addAutoPict(STTrueFalseBlank.Enum var1);

    public STTrueFalseBlank insertNewAutoPict(int var1);

    public STTrueFalseBlank addNewAutoPict();

    public void removeAutoPict(int var1);

    public List<String> getFmlaMacroList();

    public String[] getFmlaMacroArray();

    public String getFmlaMacroArray(int var1);

    public List<XmlString> xgetFmlaMacroList();

    public XmlString[] xgetFmlaMacroArray();

    public XmlString xgetFmlaMacroArray(int var1);

    public int sizeOfFmlaMacroArray();

    public void setFmlaMacroArray(String[] var1);

    public void setFmlaMacroArray(int var1, String var2);

    public void xsetFmlaMacroArray(XmlString[] var1);

    public void xsetFmlaMacroArray(int var1, XmlString var2);

    public void insertFmlaMacro(int var1, String var2);

    public void addFmlaMacro(String var1);

    public XmlString insertNewFmlaMacro(int var1);

    public XmlString addNewFmlaMacro();

    public void removeFmlaMacro(int var1);

    public List<String> getTextHAlignList();

    public String[] getTextHAlignArray();

    public String getTextHAlignArray(int var1);

    public List<XmlString> xgetTextHAlignList();

    public XmlString[] xgetTextHAlignArray();

    public XmlString xgetTextHAlignArray(int var1);

    public int sizeOfTextHAlignArray();

    public void setTextHAlignArray(String[] var1);

    public void setTextHAlignArray(int var1, String var2);

    public void xsetTextHAlignArray(XmlString[] var1);

    public void xsetTextHAlignArray(int var1, XmlString var2);

    public void insertTextHAlign(int var1, String var2);

    public void addTextHAlign(String var1);

    public XmlString insertNewTextHAlign(int var1);

    public XmlString addNewTextHAlign();

    public void removeTextHAlign(int var1);

    public List<String> getTextVAlignList();

    public String[] getTextVAlignArray();

    public String getTextVAlignArray(int var1);

    public List<XmlString> xgetTextVAlignList();

    public XmlString[] xgetTextVAlignArray();

    public XmlString xgetTextVAlignArray(int var1);

    public int sizeOfTextVAlignArray();

    public void setTextVAlignArray(String[] var1);

    public void setTextVAlignArray(int var1, String var2);

    public void xsetTextVAlignArray(XmlString[] var1);

    public void xsetTextVAlignArray(int var1, XmlString var2);

    public void insertTextVAlign(int var1, String var2);

    public void addTextVAlign(String var1);

    public XmlString insertNewTextVAlign(int var1);

    public XmlString addNewTextVAlign();

    public void removeTextVAlign(int var1);

    public List<STTrueFalseBlank.Enum> getLockTextList();

    public STTrueFalseBlank.Enum[] getLockTextArray();

    public STTrueFalseBlank.Enum getLockTextArray(int var1);

    public List<STTrueFalseBlank> xgetLockTextList();

    public STTrueFalseBlank[] xgetLockTextArray();

    public STTrueFalseBlank xgetLockTextArray(int var1);

    public int sizeOfLockTextArray();

    public void setLockTextArray(STTrueFalseBlank.Enum[] var1);

    public void setLockTextArray(int var1, STTrueFalseBlank.Enum var2);

    public void xsetLockTextArray(STTrueFalseBlank[] var1);

    public void xsetLockTextArray(int var1, STTrueFalseBlank var2);

    public void insertLockText(int var1, STTrueFalseBlank.Enum var2);

    public void addLockText(STTrueFalseBlank.Enum var1);

    public STTrueFalseBlank insertNewLockText(int var1);

    public STTrueFalseBlank addNewLockText();

    public void removeLockText(int var1);

    public List<STTrueFalseBlank.Enum> getJustLastXList();

    public STTrueFalseBlank.Enum[] getJustLastXArray();

    public STTrueFalseBlank.Enum getJustLastXArray(int var1);

    public List<STTrueFalseBlank> xgetJustLastXList();

    public STTrueFalseBlank[] xgetJustLastXArray();

    public STTrueFalseBlank xgetJustLastXArray(int var1);

    public int sizeOfJustLastXArray();

    public void setJustLastXArray(STTrueFalseBlank.Enum[] var1);

    public void setJustLastXArray(int var1, STTrueFalseBlank.Enum var2);

    public void xsetJustLastXArray(STTrueFalseBlank[] var1);

    public void xsetJustLastXArray(int var1, STTrueFalseBlank var2);

    public void insertJustLastX(int var1, STTrueFalseBlank.Enum var2);

    public void addJustLastX(STTrueFalseBlank.Enum var1);

    public STTrueFalseBlank insertNewJustLastX(int var1);

    public STTrueFalseBlank addNewJustLastX();

    public void removeJustLastX(int var1);

    public List<STTrueFalseBlank.Enum> getSecretEditList();

    public STTrueFalseBlank.Enum[] getSecretEditArray();

    public STTrueFalseBlank.Enum getSecretEditArray(int var1);

    public List<STTrueFalseBlank> xgetSecretEditList();

    public STTrueFalseBlank[] xgetSecretEditArray();

    public STTrueFalseBlank xgetSecretEditArray(int var1);

    public int sizeOfSecretEditArray();

    public void setSecretEditArray(STTrueFalseBlank.Enum[] var1);

    public void setSecretEditArray(int var1, STTrueFalseBlank.Enum var2);

    public void xsetSecretEditArray(STTrueFalseBlank[] var1);

    public void xsetSecretEditArray(int var1, STTrueFalseBlank var2);

    public void insertSecretEdit(int var1, STTrueFalseBlank.Enum var2);

    public void addSecretEdit(STTrueFalseBlank.Enum var1);

    public STTrueFalseBlank insertNewSecretEdit(int var1);

    public STTrueFalseBlank addNewSecretEdit();

    public void removeSecretEdit(int var1);

    public List<STTrueFalseBlank.Enum> getDefaultList();

    public STTrueFalseBlank.Enum[] getDefaultArray();

    public STTrueFalseBlank.Enum getDefaultArray(int var1);

    public List<STTrueFalseBlank> xgetDefaultList();

    public STTrueFalseBlank[] xgetDefaultArray();

    public STTrueFalseBlank xgetDefaultArray(int var1);

    public int sizeOfDefaultArray();

    public void setDefaultArray(STTrueFalseBlank.Enum[] var1);

    public void setDefaultArray(int var1, STTrueFalseBlank.Enum var2);

    public void xsetDefaultArray(STTrueFalseBlank[] var1);

    public void xsetDefaultArray(int var1, STTrueFalseBlank var2);

    public void insertDefault(int var1, STTrueFalseBlank.Enum var2);

    public void addDefault(STTrueFalseBlank.Enum var1);

    public STTrueFalseBlank insertNewDefault(int var1);

    public STTrueFalseBlank addNewDefault();

    public void removeDefault(int var1);

    public List<STTrueFalseBlank.Enum> getHelpList();

    public STTrueFalseBlank.Enum[] getHelpArray();

    public STTrueFalseBlank.Enum getHelpArray(int var1);

    public List<STTrueFalseBlank> xgetHelpList();

    public STTrueFalseBlank[] xgetHelpArray();

    public STTrueFalseBlank xgetHelpArray(int var1);

    public int sizeOfHelpArray();

    public void setHelpArray(STTrueFalseBlank.Enum[] var1);

    public void setHelpArray(int var1, STTrueFalseBlank.Enum var2);

    public void xsetHelpArray(STTrueFalseBlank[] var1);

    public void xsetHelpArray(int var1, STTrueFalseBlank var2);

    public void insertHelp(int var1, STTrueFalseBlank.Enum var2);

    public void addHelp(STTrueFalseBlank.Enum var1);

    public STTrueFalseBlank insertNewHelp(int var1);

    public STTrueFalseBlank addNewHelp();

    public void removeHelp(int var1);

    public List<STTrueFalseBlank.Enum> getCancelList();

    public STTrueFalseBlank.Enum[] getCancelArray();

    public STTrueFalseBlank.Enum getCancelArray(int var1);

    public List<STTrueFalseBlank> xgetCancelList();

    public STTrueFalseBlank[] xgetCancelArray();

    public STTrueFalseBlank xgetCancelArray(int var1);

    public int sizeOfCancelArray();

    public void setCancelArray(STTrueFalseBlank.Enum[] var1);

    public void setCancelArray(int var1, STTrueFalseBlank.Enum var2);

    public void xsetCancelArray(STTrueFalseBlank[] var1);

    public void xsetCancelArray(int var1, STTrueFalseBlank var2);

    public void insertCancel(int var1, STTrueFalseBlank.Enum var2);

    public void addCancel(STTrueFalseBlank.Enum var1);

    public STTrueFalseBlank insertNewCancel(int var1);

    public STTrueFalseBlank addNewCancel();

    public void removeCancel(int var1);

    public List<STTrueFalseBlank.Enum> getDismissList();

    public STTrueFalseBlank.Enum[] getDismissArray();

    public STTrueFalseBlank.Enum getDismissArray(int var1);

    public List<STTrueFalseBlank> xgetDismissList();

    public STTrueFalseBlank[] xgetDismissArray();

    public STTrueFalseBlank xgetDismissArray(int var1);

    public int sizeOfDismissArray();

    public void setDismissArray(STTrueFalseBlank.Enum[] var1);

    public void setDismissArray(int var1, STTrueFalseBlank.Enum var2);

    public void xsetDismissArray(STTrueFalseBlank[] var1);

    public void xsetDismissArray(int var1, STTrueFalseBlank var2);

    public void insertDismiss(int var1, STTrueFalseBlank.Enum var2);

    public void addDismiss(STTrueFalseBlank.Enum var1);

    public STTrueFalseBlank insertNewDismiss(int var1);

    public STTrueFalseBlank addNewDismiss();

    public void removeDismiss(int var1);

    public List<BigInteger> getAccelList();

    public BigInteger[] getAccelArray();

    public BigInteger getAccelArray(int var1);

    public List<XmlInteger> xgetAccelList();

    public XmlInteger[] xgetAccelArray();

    public XmlInteger xgetAccelArray(int var1);

    public int sizeOfAccelArray();

    public void setAccelArray(BigInteger[] var1);

    public void setAccelArray(int var1, BigInteger var2);

    public void xsetAccelArray(XmlInteger[] var1);

    public void xsetAccelArray(int var1, XmlInteger var2);

    public void insertAccel(int var1, BigInteger var2);

    public void addAccel(BigInteger var1);

    public XmlInteger insertNewAccel(int var1);

    public XmlInteger addNewAccel();

    public void removeAccel(int var1);

    public List<BigInteger> getAccel2List();

    public BigInteger[] getAccel2Array();

    public BigInteger getAccel2Array(int var1);

    public List<XmlInteger> xgetAccel2List();

    public XmlInteger[] xgetAccel2Array();

    public XmlInteger xgetAccel2Array(int var1);

    public int sizeOfAccel2Array();

    public void setAccel2Array(BigInteger[] var1);

    public void setAccel2Array(int var1, BigInteger var2);

    public void xsetAccel2Array(XmlInteger[] var1);

    public void xsetAccel2Array(int var1, XmlInteger var2);

    public void insertAccel2(int var1, BigInteger var2);

    public void addAccel2(BigInteger var1);

    public XmlInteger insertNewAccel2(int var1);

    public XmlInteger addNewAccel2();

    public void removeAccel2(int var1);

    public List<BigInteger> getRowList();

    public BigInteger[] getRowArray();

    public BigInteger getRowArray(int var1);

    public List<XmlInteger> xgetRowList();

    public XmlInteger[] xgetRowArray();

    public XmlInteger xgetRowArray(int var1);

    public int sizeOfRowArray();

    public void setRowArray(BigInteger[] var1);

    public void setRowArray(int var1, BigInteger var2);

    public void xsetRowArray(XmlInteger[] var1);

    public void xsetRowArray(int var1, XmlInteger var2);

    public void insertRow(int var1, BigInteger var2);

    public void addRow(BigInteger var1);

    public XmlInteger insertNewRow(int var1);

    public XmlInteger addNewRow();

    public void removeRow(int var1);

    public List<BigInteger> getColumnList();

    public BigInteger[] getColumnArray();

    public BigInteger getColumnArray(int var1);

    public List<XmlInteger> xgetColumnList();

    public XmlInteger[] xgetColumnArray();

    public XmlInteger xgetColumnArray(int var1);

    public int sizeOfColumnArray();

    public void setColumnArray(BigInteger[] var1);

    public void setColumnArray(int var1, BigInteger var2);

    public void xsetColumnArray(XmlInteger[] var1);

    public void xsetColumnArray(int var1, XmlInteger var2);

    public void insertColumn(int var1, BigInteger var2);

    public void addColumn(BigInteger var1);

    public XmlInteger insertNewColumn(int var1);

    public XmlInteger addNewColumn();

    public void removeColumn(int var1);

    public List<STTrueFalseBlank.Enum> getVisibleList();

    public STTrueFalseBlank.Enum[] getVisibleArray();

    public STTrueFalseBlank.Enum getVisibleArray(int var1);

    public List<STTrueFalseBlank> xgetVisibleList();

    public STTrueFalseBlank[] xgetVisibleArray();

    public STTrueFalseBlank xgetVisibleArray(int var1);

    public int sizeOfVisibleArray();

    public void setVisibleArray(STTrueFalseBlank.Enum[] var1);

    public void setVisibleArray(int var1, STTrueFalseBlank.Enum var2);

    public void xsetVisibleArray(STTrueFalseBlank[] var1);

    public void xsetVisibleArray(int var1, STTrueFalseBlank var2);

    public void insertVisible(int var1, STTrueFalseBlank.Enum var2);

    public void addVisible(STTrueFalseBlank.Enum var1);

    public STTrueFalseBlank insertNewVisible(int var1);

    public STTrueFalseBlank addNewVisible();

    public void removeVisible(int var1);

    public List<STTrueFalseBlank.Enum> getRowHiddenList();

    public STTrueFalseBlank.Enum[] getRowHiddenArray();

    public STTrueFalseBlank.Enum getRowHiddenArray(int var1);

    public List<STTrueFalseBlank> xgetRowHiddenList();

    public STTrueFalseBlank[] xgetRowHiddenArray();

    public STTrueFalseBlank xgetRowHiddenArray(int var1);

    public int sizeOfRowHiddenArray();

    public void setRowHiddenArray(STTrueFalseBlank.Enum[] var1);

    public void setRowHiddenArray(int var1, STTrueFalseBlank.Enum var2);

    public void xsetRowHiddenArray(STTrueFalseBlank[] var1);

    public void xsetRowHiddenArray(int var1, STTrueFalseBlank var2);

    public void insertRowHidden(int var1, STTrueFalseBlank.Enum var2);

    public void addRowHidden(STTrueFalseBlank.Enum var1);

    public STTrueFalseBlank insertNewRowHidden(int var1);

    public STTrueFalseBlank addNewRowHidden();

    public void removeRowHidden(int var1);

    public List<STTrueFalseBlank.Enum> getColHiddenList();

    public STTrueFalseBlank.Enum[] getColHiddenArray();

    public STTrueFalseBlank.Enum getColHiddenArray(int var1);

    public List<STTrueFalseBlank> xgetColHiddenList();

    public STTrueFalseBlank[] xgetColHiddenArray();

    public STTrueFalseBlank xgetColHiddenArray(int var1);

    public int sizeOfColHiddenArray();

    public void setColHiddenArray(STTrueFalseBlank.Enum[] var1);

    public void setColHiddenArray(int var1, STTrueFalseBlank.Enum var2);

    public void xsetColHiddenArray(STTrueFalseBlank[] var1);

    public void xsetColHiddenArray(int var1, STTrueFalseBlank var2);

    public void insertColHidden(int var1, STTrueFalseBlank.Enum var2);

    public void addColHidden(STTrueFalseBlank.Enum var1);

    public STTrueFalseBlank insertNewColHidden(int var1);

    public STTrueFalseBlank addNewColHidden();

    public void removeColHidden(int var1);

    public List<BigInteger> getVTEditList();

    public BigInteger[] getVTEditArray();

    public BigInteger getVTEditArray(int var1);

    public List<XmlInteger> xgetVTEditList();

    public XmlInteger[] xgetVTEditArray();

    public XmlInteger xgetVTEditArray(int var1);

    public int sizeOfVTEditArray();

    public void setVTEditArray(BigInteger[] var1);

    public void setVTEditArray(int var1, BigInteger var2);

    public void xsetVTEditArray(XmlInteger[] var1);

    public void xsetVTEditArray(int var1, XmlInteger var2);

    public void insertVTEdit(int var1, BigInteger var2);

    public void addVTEdit(BigInteger var1);

    public XmlInteger insertNewVTEdit(int var1);

    public XmlInteger addNewVTEdit();

    public void removeVTEdit(int var1);

    public List<STTrueFalseBlank.Enum> getMultiLineList();

    public STTrueFalseBlank.Enum[] getMultiLineArray();

    public STTrueFalseBlank.Enum getMultiLineArray(int var1);

    public List<STTrueFalseBlank> xgetMultiLineList();

    public STTrueFalseBlank[] xgetMultiLineArray();

    public STTrueFalseBlank xgetMultiLineArray(int var1);

    public int sizeOfMultiLineArray();

    public void setMultiLineArray(STTrueFalseBlank.Enum[] var1);

    public void setMultiLineArray(int var1, STTrueFalseBlank.Enum var2);

    public void xsetMultiLineArray(STTrueFalseBlank[] var1);

    public void xsetMultiLineArray(int var1, STTrueFalseBlank var2);

    public void insertMultiLine(int var1, STTrueFalseBlank.Enum var2);

    public void addMultiLine(STTrueFalseBlank.Enum var1);

    public STTrueFalseBlank insertNewMultiLine(int var1);

    public STTrueFalseBlank addNewMultiLine();

    public void removeMultiLine(int var1);

    public List<STTrueFalseBlank.Enum> getVScrollList();

    public STTrueFalseBlank.Enum[] getVScrollArray();

    public STTrueFalseBlank.Enum getVScrollArray(int var1);

    public List<STTrueFalseBlank> xgetVScrollList();

    public STTrueFalseBlank[] xgetVScrollArray();

    public STTrueFalseBlank xgetVScrollArray(int var1);

    public int sizeOfVScrollArray();

    public void setVScrollArray(STTrueFalseBlank.Enum[] var1);

    public void setVScrollArray(int var1, STTrueFalseBlank.Enum var2);

    public void xsetVScrollArray(STTrueFalseBlank[] var1);

    public void xsetVScrollArray(int var1, STTrueFalseBlank var2);

    public void insertVScroll(int var1, STTrueFalseBlank.Enum var2);

    public void addVScroll(STTrueFalseBlank.Enum var1);

    public STTrueFalseBlank insertNewVScroll(int var1);

    public STTrueFalseBlank addNewVScroll();

    public void removeVScroll(int var1);

    public List<STTrueFalseBlank.Enum> getValidIdsList();

    public STTrueFalseBlank.Enum[] getValidIdsArray();

    public STTrueFalseBlank.Enum getValidIdsArray(int var1);

    public List<STTrueFalseBlank> xgetValidIdsList();

    public STTrueFalseBlank[] xgetValidIdsArray();

    public STTrueFalseBlank xgetValidIdsArray(int var1);

    public int sizeOfValidIdsArray();

    public void setValidIdsArray(STTrueFalseBlank.Enum[] var1);

    public void setValidIdsArray(int var1, STTrueFalseBlank.Enum var2);

    public void xsetValidIdsArray(STTrueFalseBlank[] var1);

    public void xsetValidIdsArray(int var1, STTrueFalseBlank var2);

    public void insertValidIds(int var1, STTrueFalseBlank.Enum var2);

    public void addValidIds(STTrueFalseBlank.Enum var1);

    public STTrueFalseBlank insertNewValidIds(int var1);

    public STTrueFalseBlank addNewValidIds();

    public void removeValidIds(int var1);

    public List<String> getFmlaRangeList();

    public String[] getFmlaRangeArray();

    public String getFmlaRangeArray(int var1);

    public List<XmlString> xgetFmlaRangeList();

    public XmlString[] xgetFmlaRangeArray();

    public XmlString xgetFmlaRangeArray(int var1);

    public int sizeOfFmlaRangeArray();

    public void setFmlaRangeArray(String[] var1);

    public void setFmlaRangeArray(int var1, String var2);

    public void xsetFmlaRangeArray(XmlString[] var1);

    public void xsetFmlaRangeArray(int var1, XmlString var2);

    public void insertFmlaRange(int var1, String var2);

    public void addFmlaRange(String var1);

    public XmlString insertNewFmlaRange(int var1);

    public XmlString addNewFmlaRange();

    public void removeFmlaRange(int var1);

    public List<BigInteger> getWidthMinList();

    public BigInteger[] getWidthMinArray();

    public BigInteger getWidthMinArray(int var1);

    public List<XmlInteger> xgetWidthMinList();

    public XmlInteger[] xgetWidthMinArray();

    public XmlInteger xgetWidthMinArray(int var1);

    public int sizeOfWidthMinArray();

    public void setWidthMinArray(BigInteger[] var1);

    public void setWidthMinArray(int var1, BigInteger var2);

    public void xsetWidthMinArray(XmlInteger[] var1);

    public void xsetWidthMinArray(int var1, XmlInteger var2);

    public void insertWidthMin(int var1, BigInteger var2);

    public void addWidthMin(BigInteger var1);

    public XmlInteger insertNewWidthMin(int var1);

    public XmlInteger addNewWidthMin();

    public void removeWidthMin(int var1);

    public List<BigInteger> getSelList();

    public BigInteger[] getSelArray();

    public BigInteger getSelArray(int var1);

    public List<XmlInteger> xgetSelList();

    public XmlInteger[] xgetSelArray();

    public XmlInteger xgetSelArray(int var1);

    public int sizeOfSelArray();

    public void setSelArray(BigInteger[] var1);

    public void setSelArray(int var1, BigInteger var2);

    public void xsetSelArray(XmlInteger[] var1);

    public void xsetSelArray(int var1, XmlInteger var2);

    public void insertSel(int var1, BigInteger var2);

    public void addSel(BigInteger var1);

    public XmlInteger insertNewSel(int var1);

    public XmlInteger addNewSel();

    public void removeSel(int var1);

    public List<STTrueFalseBlank.Enum> getNoThreeD2List();

    public STTrueFalseBlank.Enum[] getNoThreeD2Array();

    public STTrueFalseBlank.Enum getNoThreeD2Array(int var1);

    public List<STTrueFalseBlank> xgetNoThreeD2List();

    public STTrueFalseBlank[] xgetNoThreeD2Array();

    public STTrueFalseBlank xgetNoThreeD2Array(int var1);

    public int sizeOfNoThreeD2Array();

    public void setNoThreeD2Array(STTrueFalseBlank.Enum[] var1);

    public void setNoThreeD2Array(int var1, STTrueFalseBlank.Enum var2);

    public void xsetNoThreeD2Array(STTrueFalseBlank[] var1);

    public void xsetNoThreeD2Array(int var1, STTrueFalseBlank var2);

    public void insertNoThreeD2(int var1, STTrueFalseBlank.Enum var2);

    public void addNoThreeD2(STTrueFalseBlank.Enum var1);

    public STTrueFalseBlank insertNewNoThreeD2(int var1);

    public STTrueFalseBlank addNewNoThreeD2();

    public void removeNoThreeD2(int var1);

    public List<String> getSelTypeList();

    public String[] getSelTypeArray();

    public String getSelTypeArray(int var1);

    public List<XmlString> xgetSelTypeList();

    public XmlString[] xgetSelTypeArray();

    public XmlString xgetSelTypeArray(int var1);

    public int sizeOfSelTypeArray();

    public void setSelTypeArray(String[] var1);

    public void setSelTypeArray(int var1, String var2);

    public void xsetSelTypeArray(XmlString[] var1);

    public void xsetSelTypeArray(int var1, XmlString var2);

    public void insertSelType(int var1, String var2);

    public void addSelType(String var1);

    public XmlString insertNewSelType(int var1);

    public XmlString addNewSelType();

    public void removeSelType(int var1);

    public List<String> getMultiSelList();

    public String[] getMultiSelArray();

    public String getMultiSelArray(int var1);

    public List<XmlString> xgetMultiSelList();

    public XmlString[] xgetMultiSelArray();

    public XmlString xgetMultiSelArray(int var1);

    public int sizeOfMultiSelArray();

    public void setMultiSelArray(String[] var1);

    public void setMultiSelArray(int var1, String var2);

    public void xsetMultiSelArray(XmlString[] var1);

    public void xsetMultiSelArray(int var1, XmlString var2);

    public void insertMultiSel(int var1, String var2);

    public void addMultiSel(String var1);

    public XmlString insertNewMultiSel(int var1);

    public XmlString addNewMultiSel();

    public void removeMultiSel(int var1);

    public List<String> getLCTList();

    public String[] getLCTArray();

    public String getLCTArray(int var1);

    public List<XmlString> xgetLCTList();

    public XmlString[] xgetLCTArray();

    public XmlString xgetLCTArray(int var1);

    public int sizeOfLCTArray();

    public void setLCTArray(String[] var1);

    public void setLCTArray(int var1, String var2);

    public void xsetLCTArray(XmlString[] var1);

    public void xsetLCTArray(int var1, XmlString var2);

    public void insertLCT(int var1, String var2);

    public void addLCT(String var1);

    public XmlString insertNewLCT(int var1);

    public XmlString addNewLCT();

    public void removeLCT(int var1);

    public List<String> getListItemList();

    public String[] getListItemArray();

    public String getListItemArray(int var1);

    public List<XmlString> xgetListItemList();

    public XmlString[] xgetListItemArray();

    public XmlString xgetListItemArray(int var1);

    public int sizeOfListItemArray();

    public void setListItemArray(String[] var1);

    public void setListItemArray(int var1, String var2);

    public void xsetListItemArray(XmlString[] var1);

    public void xsetListItemArray(int var1, XmlString var2);

    public void insertListItem(int var1, String var2);

    public void addListItem(String var1);

    public XmlString insertNewListItem(int var1);

    public XmlString addNewListItem();

    public void removeListItem(int var1);

    public List<String> getDropStyleList();

    public String[] getDropStyleArray();

    public String getDropStyleArray(int var1);

    public List<XmlString> xgetDropStyleList();

    public XmlString[] xgetDropStyleArray();

    public XmlString xgetDropStyleArray(int var1);

    public int sizeOfDropStyleArray();

    public void setDropStyleArray(String[] var1);

    public void setDropStyleArray(int var1, String var2);

    public void xsetDropStyleArray(XmlString[] var1);

    public void xsetDropStyleArray(int var1, XmlString var2);

    public void insertDropStyle(int var1, String var2);

    public void addDropStyle(String var1);

    public XmlString insertNewDropStyle(int var1);

    public XmlString addNewDropStyle();

    public void removeDropStyle(int var1);

    public List<STTrueFalseBlank.Enum> getColoredList();

    public STTrueFalseBlank.Enum[] getColoredArray();

    public STTrueFalseBlank.Enum getColoredArray(int var1);

    public List<STTrueFalseBlank> xgetColoredList();

    public STTrueFalseBlank[] xgetColoredArray();

    public STTrueFalseBlank xgetColoredArray(int var1);

    public int sizeOfColoredArray();

    public void setColoredArray(STTrueFalseBlank.Enum[] var1);

    public void setColoredArray(int var1, STTrueFalseBlank.Enum var2);

    public void xsetColoredArray(STTrueFalseBlank[] var1);

    public void xsetColoredArray(int var1, STTrueFalseBlank var2);

    public void insertColored(int var1, STTrueFalseBlank.Enum var2);

    public void addColored(STTrueFalseBlank.Enum var1);

    public STTrueFalseBlank insertNewColored(int var1);

    public STTrueFalseBlank addNewColored();

    public void removeColored(int var1);

    public List<BigInteger> getDropLinesList();

    public BigInteger[] getDropLinesArray();

    public BigInteger getDropLinesArray(int var1);

    public List<XmlInteger> xgetDropLinesList();

    public XmlInteger[] xgetDropLinesArray();

    public XmlInteger xgetDropLinesArray(int var1);

    public int sizeOfDropLinesArray();

    public void setDropLinesArray(BigInteger[] var1);

    public void setDropLinesArray(int var1, BigInteger var2);

    public void xsetDropLinesArray(XmlInteger[] var1);

    public void xsetDropLinesArray(int var1, XmlInteger var2);

    public void insertDropLines(int var1, BigInteger var2);

    public void addDropLines(BigInteger var1);

    public XmlInteger insertNewDropLines(int var1);

    public XmlInteger addNewDropLines();

    public void removeDropLines(int var1);

    public List<BigInteger> getCheckedList();

    public BigInteger[] getCheckedArray();

    public BigInteger getCheckedArray(int var1);

    public List<XmlInteger> xgetCheckedList();

    public XmlInteger[] xgetCheckedArray();

    public XmlInteger xgetCheckedArray(int var1);

    public int sizeOfCheckedArray();

    public void setCheckedArray(BigInteger[] var1);

    public void setCheckedArray(int var1, BigInteger var2);

    public void xsetCheckedArray(XmlInteger[] var1);

    public void xsetCheckedArray(int var1, XmlInteger var2);

    public void insertChecked(int var1, BigInteger var2);

    public void addChecked(BigInteger var1);

    public XmlInteger insertNewChecked(int var1);

    public XmlInteger addNewChecked();

    public void removeChecked(int var1);

    public List<String> getFmlaLinkList();

    public String[] getFmlaLinkArray();

    public String getFmlaLinkArray(int var1);

    public List<XmlString> xgetFmlaLinkList();

    public XmlString[] xgetFmlaLinkArray();

    public XmlString xgetFmlaLinkArray(int var1);

    public int sizeOfFmlaLinkArray();

    public void setFmlaLinkArray(String[] var1);

    public void setFmlaLinkArray(int var1, String var2);

    public void xsetFmlaLinkArray(XmlString[] var1);

    public void xsetFmlaLinkArray(int var1, XmlString var2);

    public void insertFmlaLink(int var1, String var2);

    public void addFmlaLink(String var1);

    public XmlString insertNewFmlaLink(int var1);

    public XmlString addNewFmlaLink();

    public void removeFmlaLink(int var1);

    public List<String> getFmlaPictList();

    public String[] getFmlaPictArray();

    public String getFmlaPictArray(int var1);

    public List<XmlString> xgetFmlaPictList();

    public XmlString[] xgetFmlaPictArray();

    public XmlString xgetFmlaPictArray(int var1);

    public int sizeOfFmlaPictArray();

    public void setFmlaPictArray(String[] var1);

    public void setFmlaPictArray(int var1, String var2);

    public void xsetFmlaPictArray(XmlString[] var1);

    public void xsetFmlaPictArray(int var1, XmlString var2);

    public void insertFmlaPict(int var1, String var2);

    public void addFmlaPict(String var1);

    public XmlString insertNewFmlaPict(int var1);

    public XmlString addNewFmlaPict();

    public void removeFmlaPict(int var1);

    public List<STTrueFalseBlank.Enum> getNoThreeDList();

    public STTrueFalseBlank.Enum[] getNoThreeDArray();

    public STTrueFalseBlank.Enum getNoThreeDArray(int var1);

    public List<STTrueFalseBlank> xgetNoThreeDList();

    public STTrueFalseBlank[] xgetNoThreeDArray();

    public STTrueFalseBlank xgetNoThreeDArray(int var1);

    public int sizeOfNoThreeDArray();

    public void setNoThreeDArray(STTrueFalseBlank.Enum[] var1);

    public void setNoThreeDArray(int var1, STTrueFalseBlank.Enum var2);

    public void xsetNoThreeDArray(STTrueFalseBlank[] var1);

    public void xsetNoThreeDArray(int var1, STTrueFalseBlank var2);

    public void insertNoThreeD(int var1, STTrueFalseBlank.Enum var2);

    public void addNoThreeD(STTrueFalseBlank.Enum var1);

    public STTrueFalseBlank insertNewNoThreeD(int var1);

    public STTrueFalseBlank addNewNoThreeD();

    public void removeNoThreeD(int var1);

    public List<STTrueFalseBlank.Enum> getFirstButtonList();

    public STTrueFalseBlank.Enum[] getFirstButtonArray();

    public STTrueFalseBlank.Enum getFirstButtonArray(int var1);

    public List<STTrueFalseBlank> xgetFirstButtonList();

    public STTrueFalseBlank[] xgetFirstButtonArray();

    public STTrueFalseBlank xgetFirstButtonArray(int var1);

    public int sizeOfFirstButtonArray();

    public void setFirstButtonArray(STTrueFalseBlank.Enum[] var1);

    public void setFirstButtonArray(int var1, STTrueFalseBlank.Enum var2);

    public void xsetFirstButtonArray(STTrueFalseBlank[] var1);

    public void xsetFirstButtonArray(int var1, STTrueFalseBlank var2);

    public void insertFirstButton(int var1, STTrueFalseBlank.Enum var2);

    public void addFirstButton(STTrueFalseBlank.Enum var1);

    public STTrueFalseBlank insertNewFirstButton(int var1);

    public STTrueFalseBlank addNewFirstButton();

    public void removeFirstButton(int var1);

    public List<String> getFmlaGroupList();

    public String[] getFmlaGroupArray();

    public String getFmlaGroupArray(int var1);

    public List<XmlString> xgetFmlaGroupList();

    public XmlString[] xgetFmlaGroupArray();

    public XmlString xgetFmlaGroupArray(int var1);

    public int sizeOfFmlaGroupArray();

    public void setFmlaGroupArray(String[] var1);

    public void setFmlaGroupArray(int var1, String var2);

    public void xsetFmlaGroupArray(XmlString[] var1);

    public void xsetFmlaGroupArray(int var1, XmlString var2);

    public void insertFmlaGroup(int var1, String var2);

    public void addFmlaGroup(String var1);

    public XmlString insertNewFmlaGroup(int var1);

    public XmlString addNewFmlaGroup();

    public void removeFmlaGroup(int var1);

    public List<BigInteger> getValList();

    public BigInteger[] getValArray();

    public BigInteger getValArray(int var1);

    public List<XmlInteger> xgetValList();

    public XmlInteger[] xgetValArray();

    public XmlInteger xgetValArray(int var1);

    public int sizeOfValArray();

    public void setValArray(BigInteger[] var1);

    public void setValArray(int var1, BigInteger var2);

    public void xsetValArray(XmlInteger[] var1);

    public void xsetValArray(int var1, XmlInteger var2);

    public void insertVal(int var1, BigInteger var2);

    public void addVal(BigInteger var1);

    public XmlInteger insertNewVal(int var1);

    public XmlInteger addNewVal();

    public void removeVal(int var1);

    public List<BigInteger> getMinList();

    public BigInteger[] getMinArray();

    public BigInteger getMinArray(int var1);

    public List<XmlInteger> xgetMinList();

    public XmlInteger[] xgetMinArray();

    public XmlInteger xgetMinArray(int var1);

    public int sizeOfMinArray();

    public void setMinArray(BigInteger[] var1);

    public void setMinArray(int var1, BigInteger var2);

    public void xsetMinArray(XmlInteger[] var1);

    public void xsetMinArray(int var1, XmlInteger var2);

    public void insertMin(int var1, BigInteger var2);

    public void addMin(BigInteger var1);

    public XmlInteger insertNewMin(int var1);

    public XmlInteger addNewMin();

    public void removeMin(int var1);

    public List<BigInteger> getMaxList();

    public BigInteger[] getMaxArray();

    public BigInteger getMaxArray(int var1);

    public List<XmlInteger> xgetMaxList();

    public XmlInteger[] xgetMaxArray();

    public XmlInteger xgetMaxArray(int var1);

    public int sizeOfMaxArray();

    public void setMaxArray(BigInteger[] var1);

    public void setMaxArray(int var1, BigInteger var2);

    public void xsetMaxArray(XmlInteger[] var1);

    public void xsetMaxArray(int var1, XmlInteger var2);

    public void insertMax(int var1, BigInteger var2);

    public void addMax(BigInteger var1);

    public XmlInteger insertNewMax(int var1);

    public XmlInteger addNewMax();

    public void removeMax(int var1);

    public List<BigInteger> getIncList();

    public BigInteger[] getIncArray();

    public BigInteger getIncArray(int var1);

    public List<XmlInteger> xgetIncList();

    public XmlInteger[] xgetIncArray();

    public XmlInteger xgetIncArray(int var1);

    public int sizeOfIncArray();

    public void setIncArray(BigInteger[] var1);

    public void setIncArray(int var1, BigInteger var2);

    public void xsetIncArray(XmlInteger[] var1);

    public void xsetIncArray(int var1, XmlInteger var2);

    public void insertInc(int var1, BigInteger var2);

    public void addInc(BigInteger var1);

    public XmlInteger insertNewInc(int var1);

    public XmlInteger addNewInc();

    public void removeInc(int var1);

    public List<BigInteger> getPageList();

    public BigInteger[] getPageArray();

    public BigInteger getPageArray(int var1);

    public List<XmlInteger> xgetPageList();

    public XmlInteger[] xgetPageArray();

    public XmlInteger xgetPageArray(int var1);

    public int sizeOfPageArray();

    public void setPageArray(BigInteger[] var1);

    public void setPageArray(int var1, BigInteger var2);

    public void xsetPageArray(XmlInteger[] var1);

    public void xsetPageArray(int var1, XmlInteger var2);

    public void insertPage(int var1, BigInteger var2);

    public void addPage(BigInteger var1);

    public XmlInteger insertNewPage(int var1);

    public XmlInteger addNewPage();

    public void removePage(int var1);

    public List<STTrueFalseBlank.Enum> getHorizList();

    public STTrueFalseBlank.Enum[] getHorizArray();

    public STTrueFalseBlank.Enum getHorizArray(int var1);

    public List<STTrueFalseBlank> xgetHorizList();

    public STTrueFalseBlank[] xgetHorizArray();

    public STTrueFalseBlank xgetHorizArray(int var1);

    public int sizeOfHorizArray();

    public void setHorizArray(STTrueFalseBlank.Enum[] var1);

    public void setHorizArray(int var1, STTrueFalseBlank.Enum var2);

    public void xsetHorizArray(STTrueFalseBlank[] var1);

    public void xsetHorizArray(int var1, STTrueFalseBlank var2);

    public void insertHoriz(int var1, STTrueFalseBlank.Enum var2);

    public void addHoriz(STTrueFalseBlank.Enum var1);

    public STTrueFalseBlank insertNewHoriz(int var1);

    public STTrueFalseBlank addNewHoriz();

    public void removeHoriz(int var1);

    public List<BigInteger> getDxList();

    public BigInteger[] getDxArray();

    public BigInteger getDxArray(int var1);

    public List<XmlInteger> xgetDxList();

    public XmlInteger[] xgetDxArray();

    public XmlInteger xgetDxArray(int var1);

    public int sizeOfDxArray();

    public void setDxArray(BigInteger[] var1);

    public void setDxArray(int var1, BigInteger var2);

    public void xsetDxArray(XmlInteger[] var1);

    public void xsetDxArray(int var1, XmlInteger var2);

    public void insertDx(int var1, BigInteger var2);

    public void addDx(BigInteger var1);

    public XmlInteger insertNewDx(int var1);

    public XmlInteger addNewDx();

    public void removeDx(int var1);

    public List<STTrueFalseBlank.Enum> getMapOCXList();

    public STTrueFalseBlank.Enum[] getMapOCXArray();

    public STTrueFalseBlank.Enum getMapOCXArray(int var1);

    public List<STTrueFalseBlank> xgetMapOCXList();

    public STTrueFalseBlank[] xgetMapOCXArray();

    public STTrueFalseBlank xgetMapOCXArray(int var1);

    public int sizeOfMapOCXArray();

    public void setMapOCXArray(STTrueFalseBlank.Enum[] var1);

    public void setMapOCXArray(int var1, STTrueFalseBlank.Enum var2);

    public void xsetMapOCXArray(STTrueFalseBlank[] var1);

    public void xsetMapOCXArray(int var1, STTrueFalseBlank var2);

    public void insertMapOCX(int var1, STTrueFalseBlank.Enum var2);

    public void addMapOCX(STTrueFalseBlank.Enum var1);

    public STTrueFalseBlank insertNewMapOCX(int var1);

    public STTrueFalseBlank addNewMapOCX();

    public void removeMapOCX(int var1);

    public List<String> getCFList();

    public String[] getCFArray();

    public String getCFArray(int var1);

    public List<STCF> xgetCFList();

    public STCF[] xgetCFArray();

    public STCF xgetCFArray(int var1);

    public int sizeOfCFArray();

    public void setCFArray(String[] var1);

    public void setCFArray(int var1, String var2);

    public void xsetCFArray(STCF[] var1);

    public void xsetCFArray(int var1, STCF var2);

    public void insertCF(int var1, String var2);

    public void addCF(String var1);

    public STCF insertNewCF(int var1);

    public STCF addNewCF();

    public void removeCF(int var1);

    public List<STTrueFalseBlank.Enum> getCameraList();

    public STTrueFalseBlank.Enum[] getCameraArray();

    public STTrueFalseBlank.Enum getCameraArray(int var1);

    public List<STTrueFalseBlank> xgetCameraList();

    public STTrueFalseBlank[] xgetCameraArray();

    public STTrueFalseBlank xgetCameraArray(int var1);

    public int sizeOfCameraArray();

    public void setCameraArray(STTrueFalseBlank.Enum[] var1);

    public void setCameraArray(int var1, STTrueFalseBlank.Enum var2);

    public void xsetCameraArray(STTrueFalseBlank[] var1);

    public void xsetCameraArray(int var1, STTrueFalseBlank var2);

    public void insertCamera(int var1, STTrueFalseBlank.Enum var2);

    public void addCamera(STTrueFalseBlank.Enum var1);

    public STTrueFalseBlank insertNewCamera(int var1);

    public STTrueFalseBlank addNewCamera();

    public void removeCamera(int var1);

    public List<STTrueFalseBlank.Enum> getRecalcAlwaysList();

    public STTrueFalseBlank.Enum[] getRecalcAlwaysArray();

    public STTrueFalseBlank.Enum getRecalcAlwaysArray(int var1);

    public List<STTrueFalseBlank> xgetRecalcAlwaysList();

    public STTrueFalseBlank[] xgetRecalcAlwaysArray();

    public STTrueFalseBlank xgetRecalcAlwaysArray(int var1);

    public int sizeOfRecalcAlwaysArray();

    public void setRecalcAlwaysArray(STTrueFalseBlank.Enum[] var1);

    public void setRecalcAlwaysArray(int var1, STTrueFalseBlank.Enum var2);

    public void xsetRecalcAlwaysArray(STTrueFalseBlank[] var1);

    public void xsetRecalcAlwaysArray(int var1, STTrueFalseBlank var2);

    public void insertRecalcAlways(int var1, STTrueFalseBlank.Enum var2);

    public void addRecalcAlways(STTrueFalseBlank.Enum var1);

    public STTrueFalseBlank insertNewRecalcAlways(int var1);

    public STTrueFalseBlank addNewRecalcAlways();

    public void removeRecalcAlways(int var1);

    public List<STTrueFalseBlank.Enum> getAutoScaleList();

    public STTrueFalseBlank.Enum[] getAutoScaleArray();

    public STTrueFalseBlank.Enum getAutoScaleArray(int var1);

    public List<STTrueFalseBlank> xgetAutoScaleList();

    public STTrueFalseBlank[] xgetAutoScaleArray();

    public STTrueFalseBlank xgetAutoScaleArray(int var1);

    public int sizeOfAutoScaleArray();

    public void setAutoScaleArray(STTrueFalseBlank.Enum[] var1);

    public void setAutoScaleArray(int var1, STTrueFalseBlank.Enum var2);

    public void xsetAutoScaleArray(STTrueFalseBlank[] var1);

    public void xsetAutoScaleArray(int var1, STTrueFalseBlank var2);

    public void insertAutoScale(int var1, STTrueFalseBlank.Enum var2);

    public void addAutoScale(STTrueFalseBlank.Enum var1);

    public STTrueFalseBlank insertNewAutoScale(int var1);

    public STTrueFalseBlank addNewAutoScale();

    public void removeAutoScale(int var1);

    public List<STTrueFalseBlank.Enum> getDDEList();

    public STTrueFalseBlank.Enum[] getDDEArray();

    public STTrueFalseBlank.Enum getDDEArray(int var1);

    public List<STTrueFalseBlank> xgetDDEList();

    public STTrueFalseBlank[] xgetDDEArray();

    public STTrueFalseBlank xgetDDEArray(int var1);

    public int sizeOfDDEArray();

    public void setDDEArray(STTrueFalseBlank.Enum[] var1);

    public void setDDEArray(int var1, STTrueFalseBlank.Enum var2);

    public void xsetDDEArray(STTrueFalseBlank[] var1);

    public void xsetDDEArray(int var1, STTrueFalseBlank var2);

    public void insertDDE(int var1, STTrueFalseBlank.Enum var2);

    public void addDDE(STTrueFalseBlank.Enum var1);

    public STTrueFalseBlank insertNewDDE(int var1);

    public STTrueFalseBlank addNewDDE();

    public void removeDDE(int var1);

    public List<STTrueFalseBlank.Enum> getUIObjList();

    public STTrueFalseBlank.Enum[] getUIObjArray();

    public STTrueFalseBlank.Enum getUIObjArray(int var1);

    public List<STTrueFalseBlank> xgetUIObjList();

    public STTrueFalseBlank[] xgetUIObjArray();

    public STTrueFalseBlank xgetUIObjArray(int var1);

    public int sizeOfUIObjArray();

    public void setUIObjArray(STTrueFalseBlank.Enum[] var1);

    public void setUIObjArray(int var1, STTrueFalseBlank.Enum var2);

    public void xsetUIObjArray(STTrueFalseBlank[] var1);

    public void xsetUIObjArray(int var1, STTrueFalseBlank var2);

    public void insertUIObj(int var1, STTrueFalseBlank.Enum var2);

    public void addUIObj(STTrueFalseBlank.Enum var1);

    public STTrueFalseBlank insertNewUIObj(int var1);

    public STTrueFalseBlank addNewUIObj();

    public void removeUIObj(int var1);

    public List<String> getScriptTextList();

    public String[] getScriptTextArray();

    public String getScriptTextArray(int var1);

    public List<XmlString> xgetScriptTextList();

    public XmlString[] xgetScriptTextArray();

    public XmlString xgetScriptTextArray(int var1);

    public int sizeOfScriptTextArray();

    public void setScriptTextArray(String[] var1);

    public void setScriptTextArray(int var1, String var2);

    public void xsetScriptTextArray(XmlString[] var1);

    public void xsetScriptTextArray(int var1, XmlString var2);

    public void insertScriptText(int var1, String var2);

    public void addScriptText(String var1);

    public XmlString insertNewScriptText(int var1);

    public XmlString addNewScriptText();

    public void removeScriptText(int var1);

    public List<String> getScriptExtendedList();

    public String[] getScriptExtendedArray();

    public String getScriptExtendedArray(int var1);

    public List<XmlString> xgetScriptExtendedList();

    public XmlString[] xgetScriptExtendedArray();

    public XmlString xgetScriptExtendedArray(int var1);

    public int sizeOfScriptExtendedArray();

    public void setScriptExtendedArray(String[] var1);

    public void setScriptExtendedArray(int var1, String var2);

    public void xsetScriptExtendedArray(XmlString[] var1);

    public void xsetScriptExtendedArray(int var1, XmlString var2);

    public void insertScriptExtended(int var1, String var2);

    public void addScriptExtended(String var1);

    public XmlString insertNewScriptExtended(int var1);

    public XmlString addNewScriptExtended();

    public void removeScriptExtended(int var1);

    public List<BigInteger> getScriptLanguageList();

    public BigInteger[] getScriptLanguageArray();

    public BigInteger getScriptLanguageArray(int var1);

    public List<XmlNonNegativeInteger> xgetScriptLanguageList();

    public XmlNonNegativeInteger[] xgetScriptLanguageArray();

    public XmlNonNegativeInteger xgetScriptLanguageArray(int var1);

    public int sizeOfScriptLanguageArray();

    public void setScriptLanguageArray(BigInteger[] var1);

    public void setScriptLanguageArray(int var1, BigInteger var2);

    public void xsetScriptLanguageArray(XmlNonNegativeInteger[] var1);

    public void xsetScriptLanguageArray(int var1, XmlNonNegativeInteger var2);

    public void insertScriptLanguage(int var1, BigInteger var2);

    public void addScriptLanguage(BigInteger var1);

    public XmlNonNegativeInteger insertNewScriptLanguage(int var1);

    public XmlNonNegativeInteger addNewScriptLanguage();

    public void removeScriptLanguage(int var1);

    public List<BigInteger> getScriptLocationList();

    public BigInteger[] getScriptLocationArray();

    public BigInteger getScriptLocationArray(int var1);

    public List<XmlNonNegativeInteger> xgetScriptLocationList();

    public XmlNonNegativeInteger[] xgetScriptLocationArray();

    public XmlNonNegativeInteger xgetScriptLocationArray(int var1);

    public int sizeOfScriptLocationArray();

    public void setScriptLocationArray(BigInteger[] var1);

    public void setScriptLocationArray(int var1, BigInteger var2);

    public void xsetScriptLocationArray(XmlNonNegativeInteger[] var1);

    public void xsetScriptLocationArray(int var1, XmlNonNegativeInteger var2);

    public void insertScriptLocation(int var1, BigInteger var2);

    public void addScriptLocation(BigInteger var1);

    public XmlNonNegativeInteger insertNewScriptLocation(int var1);

    public XmlNonNegativeInteger addNewScriptLocation();

    public void removeScriptLocation(int var1);

    public List<String> getFmlaTxbxList();

    public String[] getFmlaTxbxArray();

    public String getFmlaTxbxArray(int var1);

    public List<XmlString> xgetFmlaTxbxList();

    public XmlString[] xgetFmlaTxbxArray();

    public XmlString xgetFmlaTxbxArray(int var1);

    public int sizeOfFmlaTxbxArray();

    public void setFmlaTxbxArray(String[] var1);

    public void setFmlaTxbxArray(int var1, String var2);

    public void xsetFmlaTxbxArray(XmlString[] var1);

    public void xsetFmlaTxbxArray(int var1, XmlString var2);

    public void insertFmlaTxbx(int var1, String var2);

    public void addFmlaTxbx(String var1);

    public XmlString insertNewFmlaTxbx(int var1);

    public XmlString addNewFmlaTxbx();

    public void removeFmlaTxbx(int var1);

    public STObjectType.Enum getObjectType();

    public STObjectType xgetObjectType();

    public void setObjectType(STObjectType.Enum var1);

    public void xsetObjectType(STObjectType var1);
}


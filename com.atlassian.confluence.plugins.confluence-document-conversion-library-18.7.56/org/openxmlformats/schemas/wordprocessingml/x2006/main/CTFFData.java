/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.openxmlformats.schemas.wordprocessingml.x2006.main.CTFFDDList
 *  org.openxmlformats.schemas.wordprocessingml.x2006.main.CTFFHelpText
 *  org.openxmlformats.schemas.wordprocessingml.x2006.main.CTFFStatusText
 *  org.openxmlformats.schemas.wordprocessingml.x2006.main.CTMacroName
 *  org.openxmlformats.schemas.wordprocessingml.x2006.main.CTUnsignedDecimalNumber
 */
package org.openxmlformats.schemas.wordprocessingml.x2006.main;

import java.util.List;
import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTDecimalNumber;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTFFCheckBox;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTFFDDList;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTFFHelpText;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTFFName;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTFFStatusText;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTFFTextInput;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTMacroName;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTOnOff;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTUnsignedDecimalNumber;

public interface CTFFData
extends XmlObject {
    public static final DocumentFactory<CTFFData> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "ctffdataaa7etype");
    public static final SchemaType type = Factory.getType();

    public List<CTFFName> getNameList();

    public CTFFName[] getNameArray();

    public CTFFName getNameArray(int var1);

    public int sizeOfNameArray();

    public void setNameArray(CTFFName[] var1);

    public void setNameArray(int var1, CTFFName var2);

    public CTFFName insertNewName(int var1);

    public CTFFName addNewName();

    public void removeName(int var1);

    public List<CTDecimalNumber> getLabelList();

    public CTDecimalNumber[] getLabelArray();

    public CTDecimalNumber getLabelArray(int var1);

    public int sizeOfLabelArray();

    public void setLabelArray(CTDecimalNumber[] var1);

    public void setLabelArray(int var1, CTDecimalNumber var2);

    public CTDecimalNumber insertNewLabel(int var1);

    public CTDecimalNumber addNewLabel();

    public void removeLabel(int var1);

    public List<CTUnsignedDecimalNumber> getTabIndexList();

    public CTUnsignedDecimalNumber[] getTabIndexArray();

    public CTUnsignedDecimalNumber getTabIndexArray(int var1);

    public int sizeOfTabIndexArray();

    public void setTabIndexArray(CTUnsignedDecimalNumber[] var1);

    public void setTabIndexArray(int var1, CTUnsignedDecimalNumber var2);

    public CTUnsignedDecimalNumber insertNewTabIndex(int var1);

    public CTUnsignedDecimalNumber addNewTabIndex();

    public void removeTabIndex(int var1);

    public List<CTOnOff> getEnabledList();

    public CTOnOff[] getEnabledArray();

    public CTOnOff getEnabledArray(int var1);

    public int sizeOfEnabledArray();

    public void setEnabledArray(CTOnOff[] var1);

    public void setEnabledArray(int var1, CTOnOff var2);

    public CTOnOff insertNewEnabled(int var1);

    public CTOnOff addNewEnabled();

    public void removeEnabled(int var1);

    public List<CTOnOff> getCalcOnExitList();

    public CTOnOff[] getCalcOnExitArray();

    public CTOnOff getCalcOnExitArray(int var1);

    public int sizeOfCalcOnExitArray();

    public void setCalcOnExitArray(CTOnOff[] var1);

    public void setCalcOnExitArray(int var1, CTOnOff var2);

    public CTOnOff insertNewCalcOnExit(int var1);

    public CTOnOff addNewCalcOnExit();

    public void removeCalcOnExit(int var1);

    public List<CTMacroName> getEntryMacroList();

    public CTMacroName[] getEntryMacroArray();

    public CTMacroName getEntryMacroArray(int var1);

    public int sizeOfEntryMacroArray();

    public void setEntryMacroArray(CTMacroName[] var1);

    public void setEntryMacroArray(int var1, CTMacroName var2);

    public CTMacroName insertNewEntryMacro(int var1);

    public CTMacroName addNewEntryMacro();

    public void removeEntryMacro(int var1);

    public List<CTMacroName> getExitMacroList();

    public CTMacroName[] getExitMacroArray();

    public CTMacroName getExitMacroArray(int var1);

    public int sizeOfExitMacroArray();

    public void setExitMacroArray(CTMacroName[] var1);

    public void setExitMacroArray(int var1, CTMacroName var2);

    public CTMacroName insertNewExitMacro(int var1);

    public CTMacroName addNewExitMacro();

    public void removeExitMacro(int var1);

    public List<CTFFHelpText> getHelpTextList();

    public CTFFHelpText[] getHelpTextArray();

    public CTFFHelpText getHelpTextArray(int var1);

    public int sizeOfHelpTextArray();

    public void setHelpTextArray(CTFFHelpText[] var1);

    public void setHelpTextArray(int var1, CTFFHelpText var2);

    public CTFFHelpText insertNewHelpText(int var1);

    public CTFFHelpText addNewHelpText();

    public void removeHelpText(int var1);

    public List<CTFFStatusText> getStatusTextList();

    public CTFFStatusText[] getStatusTextArray();

    public CTFFStatusText getStatusTextArray(int var1);

    public int sizeOfStatusTextArray();

    public void setStatusTextArray(CTFFStatusText[] var1);

    public void setStatusTextArray(int var1, CTFFStatusText var2);

    public CTFFStatusText insertNewStatusText(int var1);

    public CTFFStatusText addNewStatusText();

    public void removeStatusText(int var1);

    public List<CTFFCheckBox> getCheckBoxList();

    public CTFFCheckBox[] getCheckBoxArray();

    public CTFFCheckBox getCheckBoxArray(int var1);

    public int sizeOfCheckBoxArray();

    public void setCheckBoxArray(CTFFCheckBox[] var1);

    public void setCheckBoxArray(int var1, CTFFCheckBox var2);

    public CTFFCheckBox insertNewCheckBox(int var1);

    public CTFFCheckBox addNewCheckBox();

    public void removeCheckBox(int var1);

    public List<CTFFDDList> getDdListList();

    public CTFFDDList[] getDdListArray();

    public CTFFDDList getDdListArray(int var1);

    public int sizeOfDdListArray();

    public void setDdListArray(CTFFDDList[] var1);

    public void setDdListArray(int var1, CTFFDDList var2);

    public CTFFDDList insertNewDdList(int var1);

    public CTFFDDList addNewDdList();

    public void removeDdList(int var1);

    public List<CTFFTextInput> getTextInputList();

    public CTFFTextInput[] getTextInputArray();

    public CTFFTextInput getTextInputArray(int var1);

    public int sizeOfTextInputArray();

    public void setTextInputArray(CTFFTextInput[] var1);

    public void setTextInputArray(int var1, CTFFTextInput var2);

    public CTFFTextInput insertNewTextInput(int var1);

    public CTFFTextInput addNewTextInput();

    public void removeTextInput(int var1);
}


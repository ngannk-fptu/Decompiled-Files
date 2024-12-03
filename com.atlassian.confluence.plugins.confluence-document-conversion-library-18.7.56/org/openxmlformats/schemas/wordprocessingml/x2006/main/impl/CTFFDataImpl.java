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
package org.openxmlformats.schemas.wordprocessingml.x2006.main.impl;

import java.util.List;
import javax.xml.namespace.QName;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.impl.values.JavaListXmlObject;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTDecimalNumber;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTFFCheckBox;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTFFDDList;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTFFData;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTFFHelpText;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTFFName;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTFFStatusText;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTFFTextInput;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTMacroName;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTOnOff;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTUnsignedDecimalNumber;

public class CTFFDataImpl
extends XmlComplexContentImpl
implements CTFFData {
    private static final long serialVersionUID = 1L;
    private static final QName[] PROPERTY_QNAME = new QName[]{new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "name"), new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "label"), new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "tabIndex"), new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "enabled"), new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "calcOnExit"), new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "entryMacro"), new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "exitMacro"), new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "helpText"), new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "statusText"), new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "checkBox"), new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "ddList"), new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "textInput")};

    public CTFFDataImpl(SchemaType sType) {
        super(sType);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public List<CTFFName> getNameList() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return new JavaListXmlObject<CTFFName>(this::getNameArray, this::setNameArray, this::insertNewName, this::removeName, this::sizeOfNameArray);
        }
    }

    @Override
    public CTFFName[] getNameArray() {
        return (CTFFName[])this.getXmlObjectArray(PROPERTY_QNAME[0], new CTFFName[0]);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTFFName getNameArray(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTFFName target = null;
            target = (CTFFName)((Object)this.get_store().find_element_user(PROPERTY_QNAME[0], i));
            if (target == null) {
                throw new IndexOutOfBoundsException();
            }
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public int sizeOfNameArray() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[0]);
        }
    }

    @Override
    public void setNameArray(CTFFName[] nameArray) {
        this.check_orphaned();
        this.arraySetterHelper(nameArray, PROPERTY_QNAME[0]);
    }

    @Override
    public void setNameArray(int i, CTFFName name) {
        this.generatedSetterHelperImpl(name, PROPERTY_QNAME[0], i, (short)2);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTFFName insertNewName(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTFFName target = null;
            target = (CTFFName)((Object)this.get_store().insert_element_user(PROPERTY_QNAME[0], i));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTFFName addNewName() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTFFName target = null;
            target = (CTFFName)((Object)this.get_store().add_element_user(PROPERTY_QNAME[0]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void removeName(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.get_store().remove_element(PROPERTY_QNAME[0], i);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public List<CTDecimalNumber> getLabelList() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return new JavaListXmlObject<CTDecimalNumber>(this::getLabelArray, this::setLabelArray, this::insertNewLabel, this::removeLabel, this::sizeOfLabelArray);
        }
    }

    @Override
    public CTDecimalNumber[] getLabelArray() {
        return (CTDecimalNumber[])this.getXmlObjectArray(PROPERTY_QNAME[1], new CTDecimalNumber[0]);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTDecimalNumber getLabelArray(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTDecimalNumber target = null;
            target = (CTDecimalNumber)((Object)this.get_store().find_element_user(PROPERTY_QNAME[1], i));
            if (target == null) {
                throw new IndexOutOfBoundsException();
            }
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public int sizeOfLabelArray() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[1]);
        }
    }

    @Override
    public void setLabelArray(CTDecimalNumber[] labelArray) {
        this.check_orphaned();
        this.arraySetterHelper(labelArray, PROPERTY_QNAME[1]);
    }

    @Override
    public void setLabelArray(int i, CTDecimalNumber label) {
        this.generatedSetterHelperImpl(label, PROPERTY_QNAME[1], i, (short)2);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTDecimalNumber insertNewLabel(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTDecimalNumber target = null;
            target = (CTDecimalNumber)((Object)this.get_store().insert_element_user(PROPERTY_QNAME[1], i));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTDecimalNumber addNewLabel() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTDecimalNumber target = null;
            target = (CTDecimalNumber)((Object)this.get_store().add_element_user(PROPERTY_QNAME[1]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void removeLabel(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.get_store().remove_element(PROPERTY_QNAME[1], i);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public List<CTUnsignedDecimalNumber> getTabIndexList() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return new JavaListXmlObject<CTUnsignedDecimalNumber>(this::getTabIndexArray, this::setTabIndexArray, this::insertNewTabIndex, this::removeTabIndex, this::sizeOfTabIndexArray);
        }
    }

    @Override
    public CTUnsignedDecimalNumber[] getTabIndexArray() {
        return (CTUnsignedDecimalNumber[])this.getXmlObjectArray(PROPERTY_QNAME[2], (XmlObject[])new CTUnsignedDecimalNumber[0]);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTUnsignedDecimalNumber getTabIndexArray(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTUnsignedDecimalNumber target = null;
            target = (CTUnsignedDecimalNumber)this.get_store().find_element_user(PROPERTY_QNAME[2], i);
            if (target == null) {
                throw new IndexOutOfBoundsException();
            }
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public int sizeOfTabIndexArray() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[2]);
        }
    }

    @Override
    public void setTabIndexArray(CTUnsignedDecimalNumber[] tabIndexArray) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])tabIndexArray, PROPERTY_QNAME[2]);
    }

    @Override
    public void setTabIndexArray(int i, CTUnsignedDecimalNumber tabIndex) {
        this.generatedSetterHelperImpl((XmlObject)tabIndex, PROPERTY_QNAME[2], i, (short)2);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTUnsignedDecimalNumber insertNewTabIndex(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTUnsignedDecimalNumber target = null;
            target = (CTUnsignedDecimalNumber)this.get_store().insert_element_user(PROPERTY_QNAME[2], i);
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTUnsignedDecimalNumber addNewTabIndex() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTUnsignedDecimalNumber target = null;
            target = (CTUnsignedDecimalNumber)this.get_store().add_element_user(PROPERTY_QNAME[2]);
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void removeTabIndex(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.get_store().remove_element(PROPERTY_QNAME[2], i);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public List<CTOnOff> getEnabledList() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return new JavaListXmlObject<CTOnOff>(this::getEnabledArray, this::setEnabledArray, this::insertNewEnabled, this::removeEnabled, this::sizeOfEnabledArray);
        }
    }

    @Override
    public CTOnOff[] getEnabledArray() {
        return (CTOnOff[])this.getXmlObjectArray(PROPERTY_QNAME[3], new CTOnOff[0]);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTOnOff getEnabledArray(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTOnOff target = null;
            target = (CTOnOff)((Object)this.get_store().find_element_user(PROPERTY_QNAME[3], i));
            if (target == null) {
                throw new IndexOutOfBoundsException();
            }
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public int sizeOfEnabledArray() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[3]);
        }
    }

    @Override
    public void setEnabledArray(CTOnOff[] enabledArray) {
        this.check_orphaned();
        this.arraySetterHelper(enabledArray, PROPERTY_QNAME[3]);
    }

    @Override
    public void setEnabledArray(int i, CTOnOff enabled) {
        this.generatedSetterHelperImpl(enabled, PROPERTY_QNAME[3], i, (short)2);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTOnOff insertNewEnabled(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTOnOff target = null;
            target = (CTOnOff)((Object)this.get_store().insert_element_user(PROPERTY_QNAME[3], i));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTOnOff addNewEnabled() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTOnOff target = null;
            target = (CTOnOff)((Object)this.get_store().add_element_user(PROPERTY_QNAME[3]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void removeEnabled(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.get_store().remove_element(PROPERTY_QNAME[3], i);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public List<CTOnOff> getCalcOnExitList() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return new JavaListXmlObject<CTOnOff>(this::getCalcOnExitArray, this::setCalcOnExitArray, this::insertNewCalcOnExit, this::removeCalcOnExit, this::sizeOfCalcOnExitArray);
        }
    }

    @Override
    public CTOnOff[] getCalcOnExitArray() {
        return (CTOnOff[])this.getXmlObjectArray(PROPERTY_QNAME[4], new CTOnOff[0]);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTOnOff getCalcOnExitArray(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTOnOff target = null;
            target = (CTOnOff)((Object)this.get_store().find_element_user(PROPERTY_QNAME[4], i));
            if (target == null) {
                throw new IndexOutOfBoundsException();
            }
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public int sizeOfCalcOnExitArray() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[4]);
        }
    }

    @Override
    public void setCalcOnExitArray(CTOnOff[] calcOnExitArray) {
        this.check_orphaned();
        this.arraySetterHelper(calcOnExitArray, PROPERTY_QNAME[4]);
    }

    @Override
    public void setCalcOnExitArray(int i, CTOnOff calcOnExit) {
        this.generatedSetterHelperImpl(calcOnExit, PROPERTY_QNAME[4], i, (short)2);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTOnOff insertNewCalcOnExit(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTOnOff target = null;
            target = (CTOnOff)((Object)this.get_store().insert_element_user(PROPERTY_QNAME[4], i));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTOnOff addNewCalcOnExit() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTOnOff target = null;
            target = (CTOnOff)((Object)this.get_store().add_element_user(PROPERTY_QNAME[4]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void removeCalcOnExit(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.get_store().remove_element(PROPERTY_QNAME[4], i);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public List<CTMacroName> getEntryMacroList() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return new JavaListXmlObject<CTMacroName>(this::getEntryMacroArray, this::setEntryMacroArray, this::insertNewEntryMacro, this::removeEntryMacro, this::sizeOfEntryMacroArray);
        }
    }

    @Override
    public CTMacroName[] getEntryMacroArray() {
        return (CTMacroName[])this.getXmlObjectArray(PROPERTY_QNAME[5], (XmlObject[])new CTMacroName[0]);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTMacroName getEntryMacroArray(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTMacroName target = null;
            target = (CTMacroName)this.get_store().find_element_user(PROPERTY_QNAME[5], i);
            if (target == null) {
                throw new IndexOutOfBoundsException();
            }
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public int sizeOfEntryMacroArray() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[5]);
        }
    }

    @Override
    public void setEntryMacroArray(CTMacroName[] entryMacroArray) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])entryMacroArray, PROPERTY_QNAME[5]);
    }

    @Override
    public void setEntryMacroArray(int i, CTMacroName entryMacro) {
        this.generatedSetterHelperImpl((XmlObject)entryMacro, PROPERTY_QNAME[5], i, (short)2);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTMacroName insertNewEntryMacro(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTMacroName target = null;
            target = (CTMacroName)this.get_store().insert_element_user(PROPERTY_QNAME[5], i);
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTMacroName addNewEntryMacro() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTMacroName target = null;
            target = (CTMacroName)this.get_store().add_element_user(PROPERTY_QNAME[5]);
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void removeEntryMacro(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.get_store().remove_element(PROPERTY_QNAME[5], i);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public List<CTMacroName> getExitMacroList() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return new JavaListXmlObject<CTMacroName>(this::getExitMacroArray, this::setExitMacroArray, this::insertNewExitMacro, this::removeExitMacro, this::sizeOfExitMacroArray);
        }
    }

    @Override
    public CTMacroName[] getExitMacroArray() {
        return (CTMacroName[])this.getXmlObjectArray(PROPERTY_QNAME[6], (XmlObject[])new CTMacroName[0]);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTMacroName getExitMacroArray(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTMacroName target = null;
            target = (CTMacroName)this.get_store().find_element_user(PROPERTY_QNAME[6], i);
            if (target == null) {
                throw new IndexOutOfBoundsException();
            }
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public int sizeOfExitMacroArray() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[6]);
        }
    }

    @Override
    public void setExitMacroArray(CTMacroName[] exitMacroArray) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])exitMacroArray, PROPERTY_QNAME[6]);
    }

    @Override
    public void setExitMacroArray(int i, CTMacroName exitMacro) {
        this.generatedSetterHelperImpl((XmlObject)exitMacro, PROPERTY_QNAME[6], i, (short)2);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTMacroName insertNewExitMacro(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTMacroName target = null;
            target = (CTMacroName)this.get_store().insert_element_user(PROPERTY_QNAME[6], i);
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTMacroName addNewExitMacro() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTMacroName target = null;
            target = (CTMacroName)this.get_store().add_element_user(PROPERTY_QNAME[6]);
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void removeExitMacro(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.get_store().remove_element(PROPERTY_QNAME[6], i);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public List<CTFFHelpText> getHelpTextList() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return new JavaListXmlObject<CTFFHelpText>(this::getHelpTextArray, this::setHelpTextArray, this::insertNewHelpText, this::removeHelpText, this::sizeOfHelpTextArray);
        }
    }

    @Override
    public CTFFHelpText[] getHelpTextArray() {
        return (CTFFHelpText[])this.getXmlObjectArray(PROPERTY_QNAME[7], (XmlObject[])new CTFFHelpText[0]);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTFFHelpText getHelpTextArray(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTFFHelpText target = null;
            target = (CTFFHelpText)this.get_store().find_element_user(PROPERTY_QNAME[7], i);
            if (target == null) {
                throw new IndexOutOfBoundsException();
            }
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public int sizeOfHelpTextArray() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[7]);
        }
    }

    @Override
    public void setHelpTextArray(CTFFHelpText[] helpTextArray) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])helpTextArray, PROPERTY_QNAME[7]);
    }

    @Override
    public void setHelpTextArray(int i, CTFFHelpText helpText) {
        this.generatedSetterHelperImpl((XmlObject)helpText, PROPERTY_QNAME[7], i, (short)2);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTFFHelpText insertNewHelpText(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTFFHelpText target = null;
            target = (CTFFHelpText)this.get_store().insert_element_user(PROPERTY_QNAME[7], i);
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTFFHelpText addNewHelpText() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTFFHelpText target = null;
            target = (CTFFHelpText)this.get_store().add_element_user(PROPERTY_QNAME[7]);
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void removeHelpText(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.get_store().remove_element(PROPERTY_QNAME[7], i);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public List<CTFFStatusText> getStatusTextList() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return new JavaListXmlObject<CTFFStatusText>(this::getStatusTextArray, this::setStatusTextArray, this::insertNewStatusText, this::removeStatusText, this::sizeOfStatusTextArray);
        }
    }

    @Override
    public CTFFStatusText[] getStatusTextArray() {
        return (CTFFStatusText[])this.getXmlObjectArray(PROPERTY_QNAME[8], (XmlObject[])new CTFFStatusText[0]);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTFFStatusText getStatusTextArray(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTFFStatusText target = null;
            target = (CTFFStatusText)this.get_store().find_element_user(PROPERTY_QNAME[8], i);
            if (target == null) {
                throw new IndexOutOfBoundsException();
            }
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public int sizeOfStatusTextArray() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[8]);
        }
    }

    @Override
    public void setStatusTextArray(CTFFStatusText[] statusTextArray) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])statusTextArray, PROPERTY_QNAME[8]);
    }

    @Override
    public void setStatusTextArray(int i, CTFFStatusText statusText) {
        this.generatedSetterHelperImpl((XmlObject)statusText, PROPERTY_QNAME[8], i, (short)2);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTFFStatusText insertNewStatusText(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTFFStatusText target = null;
            target = (CTFFStatusText)this.get_store().insert_element_user(PROPERTY_QNAME[8], i);
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTFFStatusText addNewStatusText() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTFFStatusText target = null;
            target = (CTFFStatusText)this.get_store().add_element_user(PROPERTY_QNAME[8]);
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void removeStatusText(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.get_store().remove_element(PROPERTY_QNAME[8], i);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public List<CTFFCheckBox> getCheckBoxList() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return new JavaListXmlObject<CTFFCheckBox>(this::getCheckBoxArray, this::setCheckBoxArray, this::insertNewCheckBox, this::removeCheckBox, this::sizeOfCheckBoxArray);
        }
    }

    @Override
    public CTFFCheckBox[] getCheckBoxArray() {
        return (CTFFCheckBox[])this.getXmlObjectArray(PROPERTY_QNAME[9], new CTFFCheckBox[0]);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTFFCheckBox getCheckBoxArray(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTFFCheckBox target = null;
            target = (CTFFCheckBox)((Object)this.get_store().find_element_user(PROPERTY_QNAME[9], i));
            if (target == null) {
                throw new IndexOutOfBoundsException();
            }
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public int sizeOfCheckBoxArray() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[9]);
        }
    }

    @Override
    public void setCheckBoxArray(CTFFCheckBox[] checkBoxArray) {
        this.check_orphaned();
        this.arraySetterHelper(checkBoxArray, PROPERTY_QNAME[9]);
    }

    @Override
    public void setCheckBoxArray(int i, CTFFCheckBox checkBox) {
        this.generatedSetterHelperImpl(checkBox, PROPERTY_QNAME[9], i, (short)2);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTFFCheckBox insertNewCheckBox(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTFFCheckBox target = null;
            target = (CTFFCheckBox)((Object)this.get_store().insert_element_user(PROPERTY_QNAME[9], i));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTFFCheckBox addNewCheckBox() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTFFCheckBox target = null;
            target = (CTFFCheckBox)((Object)this.get_store().add_element_user(PROPERTY_QNAME[9]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void removeCheckBox(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.get_store().remove_element(PROPERTY_QNAME[9], i);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public List<CTFFDDList> getDdListList() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return new JavaListXmlObject<CTFFDDList>(this::getDdListArray, this::setDdListArray, this::insertNewDdList, this::removeDdList, this::sizeOfDdListArray);
        }
    }

    @Override
    public CTFFDDList[] getDdListArray() {
        return (CTFFDDList[])this.getXmlObjectArray(PROPERTY_QNAME[10], (XmlObject[])new CTFFDDList[0]);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTFFDDList getDdListArray(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTFFDDList target = null;
            target = (CTFFDDList)this.get_store().find_element_user(PROPERTY_QNAME[10], i);
            if (target == null) {
                throw new IndexOutOfBoundsException();
            }
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public int sizeOfDdListArray() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[10]);
        }
    }

    @Override
    public void setDdListArray(CTFFDDList[] ddListArray) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])ddListArray, PROPERTY_QNAME[10]);
    }

    @Override
    public void setDdListArray(int i, CTFFDDList ddList) {
        this.generatedSetterHelperImpl((XmlObject)ddList, PROPERTY_QNAME[10], i, (short)2);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTFFDDList insertNewDdList(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTFFDDList target = null;
            target = (CTFFDDList)this.get_store().insert_element_user(PROPERTY_QNAME[10], i);
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTFFDDList addNewDdList() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTFFDDList target = null;
            target = (CTFFDDList)this.get_store().add_element_user(PROPERTY_QNAME[10]);
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void removeDdList(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.get_store().remove_element(PROPERTY_QNAME[10], i);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public List<CTFFTextInput> getTextInputList() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return new JavaListXmlObject<CTFFTextInput>(this::getTextInputArray, this::setTextInputArray, this::insertNewTextInput, this::removeTextInput, this::sizeOfTextInputArray);
        }
    }

    @Override
    public CTFFTextInput[] getTextInputArray() {
        return (CTFFTextInput[])this.getXmlObjectArray(PROPERTY_QNAME[11], new CTFFTextInput[0]);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTFFTextInput getTextInputArray(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTFFTextInput target = null;
            target = (CTFFTextInput)((Object)this.get_store().find_element_user(PROPERTY_QNAME[11], i));
            if (target == null) {
                throw new IndexOutOfBoundsException();
            }
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public int sizeOfTextInputArray() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[11]);
        }
    }

    @Override
    public void setTextInputArray(CTFFTextInput[] textInputArray) {
        this.check_orphaned();
        this.arraySetterHelper(textInputArray, PROPERTY_QNAME[11]);
    }

    @Override
    public void setTextInputArray(int i, CTFFTextInput textInput) {
        this.generatedSetterHelperImpl(textInput, PROPERTY_QNAME[11], i, (short)2);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTFFTextInput insertNewTextInput(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTFFTextInput target = null;
            target = (CTFFTextInput)((Object)this.get_store().insert_element_user(PROPERTY_QNAME[11], i));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTFFTextInput addNewTextInput() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTFFTextInput target = null;
            target = (CTFFTextInput)((Object)this.get_store().add_element_user(PROPERTY_QNAME[11]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void removeTextInput(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.get_store().remove_element(PROPERTY_QNAME[11], i);
        }
    }
}


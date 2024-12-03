/*
 * Decompiled with CFR 0.152.
 */
package com.microsoft.schemas.office.excel.impl;

import com.microsoft.schemas.office.excel.CTClientData;
import com.microsoft.schemas.office.excel.STCF;
import com.microsoft.schemas.office.excel.STObjectType;
import java.math.BigInteger;
import java.util.List;
import javax.xml.namespace.QName;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.SimpleValue;
import org.apache.xmlbeans.XmlInteger;
import org.apache.xmlbeans.XmlNonNegativeInteger;
import org.apache.xmlbeans.XmlString;
import org.apache.xmlbeans.impl.values.JavaListObject;
import org.apache.xmlbeans.impl.values.JavaListXmlObject;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;
import org.openxmlformats.schemas.officeDocument.x2006.sharedTypes.STTrueFalseBlank;

public class CTClientDataImpl
extends XmlComplexContentImpl
implements CTClientData {
    private static final long serialVersionUID = 1L;
    private static final QName[] PROPERTY_QNAME = new QName[]{new QName("urn:schemas-microsoft-com:office:excel", "MoveWithCells"), new QName("urn:schemas-microsoft-com:office:excel", "SizeWithCells"), new QName("urn:schemas-microsoft-com:office:excel", "Anchor"), new QName("urn:schemas-microsoft-com:office:excel", "Locked"), new QName("urn:schemas-microsoft-com:office:excel", "DefaultSize"), new QName("urn:schemas-microsoft-com:office:excel", "PrintObject"), new QName("urn:schemas-microsoft-com:office:excel", "Disabled"), new QName("urn:schemas-microsoft-com:office:excel", "AutoFill"), new QName("urn:schemas-microsoft-com:office:excel", "AutoLine"), new QName("urn:schemas-microsoft-com:office:excel", "AutoPict"), new QName("urn:schemas-microsoft-com:office:excel", "FmlaMacro"), new QName("urn:schemas-microsoft-com:office:excel", "TextHAlign"), new QName("urn:schemas-microsoft-com:office:excel", "TextVAlign"), new QName("urn:schemas-microsoft-com:office:excel", "LockText"), new QName("urn:schemas-microsoft-com:office:excel", "JustLastX"), new QName("urn:schemas-microsoft-com:office:excel", "SecretEdit"), new QName("urn:schemas-microsoft-com:office:excel", "Default"), new QName("urn:schemas-microsoft-com:office:excel", "Help"), new QName("urn:schemas-microsoft-com:office:excel", "Cancel"), new QName("urn:schemas-microsoft-com:office:excel", "Dismiss"), new QName("urn:schemas-microsoft-com:office:excel", "Accel"), new QName("urn:schemas-microsoft-com:office:excel", "Accel2"), new QName("urn:schemas-microsoft-com:office:excel", "Row"), new QName("urn:schemas-microsoft-com:office:excel", "Column"), new QName("urn:schemas-microsoft-com:office:excel", "Visible"), new QName("urn:schemas-microsoft-com:office:excel", "RowHidden"), new QName("urn:schemas-microsoft-com:office:excel", "ColHidden"), new QName("urn:schemas-microsoft-com:office:excel", "VTEdit"), new QName("urn:schemas-microsoft-com:office:excel", "MultiLine"), new QName("urn:schemas-microsoft-com:office:excel", "VScroll"), new QName("urn:schemas-microsoft-com:office:excel", "ValidIds"), new QName("urn:schemas-microsoft-com:office:excel", "FmlaRange"), new QName("urn:schemas-microsoft-com:office:excel", "WidthMin"), new QName("urn:schemas-microsoft-com:office:excel", "Sel"), new QName("urn:schemas-microsoft-com:office:excel", "NoThreeD2"), new QName("urn:schemas-microsoft-com:office:excel", "SelType"), new QName("urn:schemas-microsoft-com:office:excel", "MultiSel"), new QName("urn:schemas-microsoft-com:office:excel", "LCT"), new QName("urn:schemas-microsoft-com:office:excel", "ListItem"), new QName("urn:schemas-microsoft-com:office:excel", "DropStyle"), new QName("urn:schemas-microsoft-com:office:excel", "Colored"), new QName("urn:schemas-microsoft-com:office:excel", "DropLines"), new QName("urn:schemas-microsoft-com:office:excel", "Checked"), new QName("urn:schemas-microsoft-com:office:excel", "FmlaLink"), new QName("urn:schemas-microsoft-com:office:excel", "FmlaPict"), new QName("urn:schemas-microsoft-com:office:excel", "NoThreeD"), new QName("urn:schemas-microsoft-com:office:excel", "FirstButton"), new QName("urn:schemas-microsoft-com:office:excel", "FmlaGroup"), new QName("urn:schemas-microsoft-com:office:excel", "Val"), new QName("urn:schemas-microsoft-com:office:excel", "Min"), new QName("urn:schemas-microsoft-com:office:excel", "Max"), new QName("urn:schemas-microsoft-com:office:excel", "Inc"), new QName("urn:schemas-microsoft-com:office:excel", "Page"), new QName("urn:schemas-microsoft-com:office:excel", "Horiz"), new QName("urn:schemas-microsoft-com:office:excel", "Dx"), new QName("urn:schemas-microsoft-com:office:excel", "MapOCX"), new QName("urn:schemas-microsoft-com:office:excel", "CF"), new QName("urn:schemas-microsoft-com:office:excel", "Camera"), new QName("urn:schemas-microsoft-com:office:excel", "RecalcAlways"), new QName("urn:schemas-microsoft-com:office:excel", "AutoScale"), new QName("urn:schemas-microsoft-com:office:excel", "DDE"), new QName("urn:schemas-microsoft-com:office:excel", "UIObj"), new QName("urn:schemas-microsoft-com:office:excel", "ScriptText"), new QName("urn:schemas-microsoft-com:office:excel", "ScriptExtended"), new QName("urn:schemas-microsoft-com:office:excel", "ScriptLanguage"), new QName("urn:schemas-microsoft-com:office:excel", "ScriptLocation"), new QName("urn:schemas-microsoft-com:office:excel", "FmlaTxbx"), new QName("", "ObjectType")};

    public CTClientDataImpl(SchemaType sType) {
        super(sType);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public List<STTrueFalseBlank.Enum> getMoveWithCellsList() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return new JavaListObject<STTrueFalseBlank.Enum>(this::getMoveWithCellsArray, this::setMoveWithCellsArray, this::insertMoveWithCells, this::removeMoveWithCells, this::sizeOfMoveWithCellsArray);
        }
    }

    @Override
    public STTrueFalseBlank.Enum[] getMoveWithCellsArray() {
        return (STTrueFalseBlank.Enum[])this.getEnumArray(PROPERTY_QNAME[0], STTrueFalseBlank.Enum[]::new);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public STTrueFalseBlank.Enum getMoveWithCellsArray(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)((Object)this.get_store().find_element_user(PROPERTY_QNAME[0], i));
            if (target == null) {
                throw new IndexOutOfBoundsException();
            }
            return (STTrueFalseBlank.Enum)target.getEnumValue();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public List<STTrueFalseBlank> xgetMoveWithCellsList() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return new JavaListXmlObject<STTrueFalseBlank>(this::xgetMoveWithCellsArray, this::xsetMoveWithCellsArray, this::insertNewMoveWithCells, this::removeMoveWithCells, this::sizeOfMoveWithCellsArray);
        }
    }

    @Override
    public STTrueFalseBlank[] xgetMoveWithCellsArray() {
        return (STTrueFalseBlank[])this.xgetArray(PROPERTY_QNAME[0], STTrueFalseBlank[]::new);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public STTrueFalseBlank xgetMoveWithCellsArray(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            STTrueFalseBlank target = null;
            target = (STTrueFalseBlank)((Object)this.get_store().find_element_user(PROPERTY_QNAME[0], i));
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
    public int sizeOfMoveWithCellsArray() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[0]);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void setMoveWithCellsArray(STTrueFalseBlank.Enum[] moveWithCellsArray) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.arraySetterHelper(moveWithCellsArray, PROPERTY_QNAME[0]);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void setMoveWithCellsArray(int i, STTrueFalseBlank.Enum moveWithCells) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)((Object)this.get_store().find_element_user(PROPERTY_QNAME[0], i));
            if (target == null) {
                throw new IndexOutOfBoundsException();
            }
            target.setEnumValue(moveWithCells);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void xsetMoveWithCellsArray(STTrueFalseBlank[] moveWithCellsArray) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.arraySetterHelper(moveWithCellsArray, PROPERTY_QNAME[0]);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void xsetMoveWithCellsArray(int i, STTrueFalseBlank moveWithCells) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            STTrueFalseBlank target = null;
            target = (STTrueFalseBlank)((Object)this.get_store().find_element_user(PROPERTY_QNAME[0], i));
            if (target == null) {
                throw new IndexOutOfBoundsException();
            }
            target.set(moveWithCells);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void insertMoveWithCells(int i, STTrueFalseBlank.Enum moveWithCells) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            SimpleValue target = (SimpleValue)((Object)this.get_store().insert_element_user(PROPERTY_QNAME[0], i));
            target.setEnumValue(moveWithCells);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void addMoveWithCells(STTrueFalseBlank.Enum moveWithCells) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)((Object)this.get_store().add_element_user(PROPERTY_QNAME[0]));
            target.setEnumValue(moveWithCells);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public STTrueFalseBlank insertNewMoveWithCells(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            STTrueFalseBlank target = null;
            target = (STTrueFalseBlank)((Object)this.get_store().insert_element_user(PROPERTY_QNAME[0], i));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public STTrueFalseBlank addNewMoveWithCells() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            STTrueFalseBlank target = null;
            target = (STTrueFalseBlank)((Object)this.get_store().add_element_user(PROPERTY_QNAME[0]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void removeMoveWithCells(int i) {
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
    public List<STTrueFalseBlank.Enum> getSizeWithCellsList() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return new JavaListObject<STTrueFalseBlank.Enum>(this::getSizeWithCellsArray, this::setSizeWithCellsArray, this::insertSizeWithCells, this::removeSizeWithCells, this::sizeOfSizeWithCellsArray);
        }
    }

    @Override
    public STTrueFalseBlank.Enum[] getSizeWithCellsArray() {
        return (STTrueFalseBlank.Enum[])this.getEnumArray(PROPERTY_QNAME[1], STTrueFalseBlank.Enum[]::new);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public STTrueFalseBlank.Enum getSizeWithCellsArray(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)((Object)this.get_store().find_element_user(PROPERTY_QNAME[1], i));
            if (target == null) {
                throw new IndexOutOfBoundsException();
            }
            return (STTrueFalseBlank.Enum)target.getEnumValue();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public List<STTrueFalseBlank> xgetSizeWithCellsList() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return new JavaListXmlObject<STTrueFalseBlank>(this::xgetSizeWithCellsArray, this::xsetSizeWithCellsArray, this::insertNewSizeWithCells, this::removeSizeWithCells, this::sizeOfSizeWithCellsArray);
        }
    }

    @Override
    public STTrueFalseBlank[] xgetSizeWithCellsArray() {
        return (STTrueFalseBlank[])this.xgetArray(PROPERTY_QNAME[1], STTrueFalseBlank[]::new);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public STTrueFalseBlank xgetSizeWithCellsArray(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            STTrueFalseBlank target = null;
            target = (STTrueFalseBlank)((Object)this.get_store().find_element_user(PROPERTY_QNAME[1], i));
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
    public int sizeOfSizeWithCellsArray() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[1]);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void setSizeWithCellsArray(STTrueFalseBlank.Enum[] sizeWithCellsArray) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.arraySetterHelper(sizeWithCellsArray, PROPERTY_QNAME[1]);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void setSizeWithCellsArray(int i, STTrueFalseBlank.Enum sizeWithCells) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)((Object)this.get_store().find_element_user(PROPERTY_QNAME[1], i));
            if (target == null) {
                throw new IndexOutOfBoundsException();
            }
            target.setEnumValue(sizeWithCells);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void xsetSizeWithCellsArray(STTrueFalseBlank[] sizeWithCellsArray) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.arraySetterHelper(sizeWithCellsArray, PROPERTY_QNAME[1]);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void xsetSizeWithCellsArray(int i, STTrueFalseBlank sizeWithCells) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            STTrueFalseBlank target = null;
            target = (STTrueFalseBlank)((Object)this.get_store().find_element_user(PROPERTY_QNAME[1], i));
            if (target == null) {
                throw new IndexOutOfBoundsException();
            }
            target.set(sizeWithCells);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void insertSizeWithCells(int i, STTrueFalseBlank.Enum sizeWithCells) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            SimpleValue target = (SimpleValue)((Object)this.get_store().insert_element_user(PROPERTY_QNAME[1], i));
            target.setEnumValue(sizeWithCells);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void addSizeWithCells(STTrueFalseBlank.Enum sizeWithCells) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)((Object)this.get_store().add_element_user(PROPERTY_QNAME[1]));
            target.setEnumValue(sizeWithCells);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public STTrueFalseBlank insertNewSizeWithCells(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            STTrueFalseBlank target = null;
            target = (STTrueFalseBlank)((Object)this.get_store().insert_element_user(PROPERTY_QNAME[1], i));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public STTrueFalseBlank addNewSizeWithCells() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            STTrueFalseBlank target = null;
            target = (STTrueFalseBlank)((Object)this.get_store().add_element_user(PROPERTY_QNAME[1]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void removeSizeWithCells(int i) {
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
    public List<String> getAnchorList() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return new JavaListObject<String>(this::getAnchorArray, this::setAnchorArray, this::insertAnchor, this::removeAnchor, this::sizeOfAnchorArray);
        }
    }

    @Override
    public String[] getAnchorArray() {
        return this.getObjectArray(PROPERTY_QNAME[2], SimpleValue::getStringValue, String[]::new);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public String getAnchorArray(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)((Object)this.get_store().find_element_user(PROPERTY_QNAME[2], i));
            if (target == null) {
                throw new IndexOutOfBoundsException();
            }
            return target.getStringValue();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public List<XmlString> xgetAnchorList() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return new JavaListXmlObject<XmlString>(this::xgetAnchorArray, this::xsetAnchorArray, this::insertNewAnchor, this::removeAnchor, this::sizeOfAnchorArray);
        }
    }

    @Override
    public XmlString[] xgetAnchorArray() {
        return (XmlString[])this.xgetArray(PROPERTY_QNAME[2], XmlString[]::new);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public XmlString xgetAnchorArray(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            XmlString target = null;
            target = (XmlString)((Object)this.get_store().find_element_user(PROPERTY_QNAME[2], i));
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
    public int sizeOfAnchorArray() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[2]);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void setAnchorArray(String[] anchorArray) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.arraySetterHelper(anchorArray, PROPERTY_QNAME[2]);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void setAnchorArray(int i, String anchor) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)((Object)this.get_store().find_element_user(PROPERTY_QNAME[2], i));
            if (target == null) {
                throw new IndexOutOfBoundsException();
            }
            target.setStringValue(anchor);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void xsetAnchorArray(XmlString[] anchorArray) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.arraySetterHelper(anchorArray, PROPERTY_QNAME[2]);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void xsetAnchorArray(int i, XmlString anchor) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            XmlString target = null;
            target = (XmlString)((Object)this.get_store().find_element_user(PROPERTY_QNAME[2], i));
            if (target == null) {
                throw new IndexOutOfBoundsException();
            }
            target.set(anchor);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void insertAnchor(int i, String anchor) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            SimpleValue target = (SimpleValue)((Object)this.get_store().insert_element_user(PROPERTY_QNAME[2], i));
            target.setStringValue(anchor);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void addAnchor(String anchor) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)((Object)this.get_store().add_element_user(PROPERTY_QNAME[2]));
            target.setStringValue(anchor);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public XmlString insertNewAnchor(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            XmlString target = null;
            target = (XmlString)((Object)this.get_store().insert_element_user(PROPERTY_QNAME[2], i));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public XmlString addNewAnchor() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            XmlString target = null;
            target = (XmlString)((Object)this.get_store().add_element_user(PROPERTY_QNAME[2]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void removeAnchor(int i) {
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
    public List<STTrueFalseBlank.Enum> getLockedList() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return new JavaListObject<STTrueFalseBlank.Enum>(this::getLockedArray, this::setLockedArray, this::insertLocked, this::removeLocked, this::sizeOfLockedArray);
        }
    }

    @Override
    public STTrueFalseBlank.Enum[] getLockedArray() {
        return (STTrueFalseBlank.Enum[])this.getEnumArray(PROPERTY_QNAME[3], STTrueFalseBlank.Enum[]::new);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public STTrueFalseBlank.Enum getLockedArray(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)((Object)this.get_store().find_element_user(PROPERTY_QNAME[3], i));
            if (target == null) {
                throw new IndexOutOfBoundsException();
            }
            return (STTrueFalseBlank.Enum)target.getEnumValue();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public List<STTrueFalseBlank> xgetLockedList() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return new JavaListXmlObject<STTrueFalseBlank>(this::xgetLockedArray, this::xsetLockedArray, this::insertNewLocked, this::removeLocked, this::sizeOfLockedArray);
        }
    }

    @Override
    public STTrueFalseBlank[] xgetLockedArray() {
        return (STTrueFalseBlank[])this.xgetArray(PROPERTY_QNAME[3], STTrueFalseBlank[]::new);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public STTrueFalseBlank xgetLockedArray(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            STTrueFalseBlank target = null;
            target = (STTrueFalseBlank)((Object)this.get_store().find_element_user(PROPERTY_QNAME[3], i));
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
    public int sizeOfLockedArray() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[3]);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void setLockedArray(STTrueFalseBlank.Enum[] lockedArray) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.arraySetterHelper(lockedArray, PROPERTY_QNAME[3]);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void setLockedArray(int i, STTrueFalseBlank.Enum locked) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)((Object)this.get_store().find_element_user(PROPERTY_QNAME[3], i));
            if (target == null) {
                throw new IndexOutOfBoundsException();
            }
            target.setEnumValue(locked);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void xsetLockedArray(STTrueFalseBlank[] lockedArray) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.arraySetterHelper(lockedArray, PROPERTY_QNAME[3]);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void xsetLockedArray(int i, STTrueFalseBlank locked) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            STTrueFalseBlank target = null;
            target = (STTrueFalseBlank)((Object)this.get_store().find_element_user(PROPERTY_QNAME[3], i));
            if (target == null) {
                throw new IndexOutOfBoundsException();
            }
            target.set(locked);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void insertLocked(int i, STTrueFalseBlank.Enum locked) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            SimpleValue target = (SimpleValue)((Object)this.get_store().insert_element_user(PROPERTY_QNAME[3], i));
            target.setEnumValue(locked);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void addLocked(STTrueFalseBlank.Enum locked) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)((Object)this.get_store().add_element_user(PROPERTY_QNAME[3]));
            target.setEnumValue(locked);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public STTrueFalseBlank insertNewLocked(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            STTrueFalseBlank target = null;
            target = (STTrueFalseBlank)((Object)this.get_store().insert_element_user(PROPERTY_QNAME[3], i));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public STTrueFalseBlank addNewLocked() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            STTrueFalseBlank target = null;
            target = (STTrueFalseBlank)((Object)this.get_store().add_element_user(PROPERTY_QNAME[3]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void removeLocked(int i) {
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
    public List<STTrueFalseBlank.Enum> getDefaultSizeList() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return new JavaListObject<STTrueFalseBlank.Enum>(this::getDefaultSizeArray, this::setDefaultSizeArray, this::insertDefaultSize, this::removeDefaultSize, this::sizeOfDefaultSizeArray);
        }
    }

    @Override
    public STTrueFalseBlank.Enum[] getDefaultSizeArray() {
        return (STTrueFalseBlank.Enum[])this.getEnumArray(PROPERTY_QNAME[4], STTrueFalseBlank.Enum[]::new);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public STTrueFalseBlank.Enum getDefaultSizeArray(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)((Object)this.get_store().find_element_user(PROPERTY_QNAME[4], i));
            if (target == null) {
                throw new IndexOutOfBoundsException();
            }
            return (STTrueFalseBlank.Enum)target.getEnumValue();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public List<STTrueFalseBlank> xgetDefaultSizeList() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return new JavaListXmlObject<STTrueFalseBlank>(this::xgetDefaultSizeArray, this::xsetDefaultSizeArray, this::insertNewDefaultSize, this::removeDefaultSize, this::sizeOfDefaultSizeArray);
        }
    }

    @Override
    public STTrueFalseBlank[] xgetDefaultSizeArray() {
        return (STTrueFalseBlank[])this.xgetArray(PROPERTY_QNAME[4], STTrueFalseBlank[]::new);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public STTrueFalseBlank xgetDefaultSizeArray(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            STTrueFalseBlank target = null;
            target = (STTrueFalseBlank)((Object)this.get_store().find_element_user(PROPERTY_QNAME[4], i));
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
    public int sizeOfDefaultSizeArray() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[4]);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void setDefaultSizeArray(STTrueFalseBlank.Enum[] defaultSizeArray) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.arraySetterHelper(defaultSizeArray, PROPERTY_QNAME[4]);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void setDefaultSizeArray(int i, STTrueFalseBlank.Enum defaultSize) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)((Object)this.get_store().find_element_user(PROPERTY_QNAME[4], i));
            if (target == null) {
                throw new IndexOutOfBoundsException();
            }
            target.setEnumValue(defaultSize);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void xsetDefaultSizeArray(STTrueFalseBlank[] defaultSizeArray) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.arraySetterHelper(defaultSizeArray, PROPERTY_QNAME[4]);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void xsetDefaultSizeArray(int i, STTrueFalseBlank defaultSize) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            STTrueFalseBlank target = null;
            target = (STTrueFalseBlank)((Object)this.get_store().find_element_user(PROPERTY_QNAME[4], i));
            if (target == null) {
                throw new IndexOutOfBoundsException();
            }
            target.set(defaultSize);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void insertDefaultSize(int i, STTrueFalseBlank.Enum defaultSize) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            SimpleValue target = (SimpleValue)((Object)this.get_store().insert_element_user(PROPERTY_QNAME[4], i));
            target.setEnumValue(defaultSize);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void addDefaultSize(STTrueFalseBlank.Enum defaultSize) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)((Object)this.get_store().add_element_user(PROPERTY_QNAME[4]));
            target.setEnumValue(defaultSize);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public STTrueFalseBlank insertNewDefaultSize(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            STTrueFalseBlank target = null;
            target = (STTrueFalseBlank)((Object)this.get_store().insert_element_user(PROPERTY_QNAME[4], i));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public STTrueFalseBlank addNewDefaultSize() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            STTrueFalseBlank target = null;
            target = (STTrueFalseBlank)((Object)this.get_store().add_element_user(PROPERTY_QNAME[4]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void removeDefaultSize(int i) {
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
    public List<STTrueFalseBlank.Enum> getPrintObjectList() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return new JavaListObject<STTrueFalseBlank.Enum>(this::getPrintObjectArray, this::setPrintObjectArray, this::insertPrintObject, this::removePrintObject, this::sizeOfPrintObjectArray);
        }
    }

    @Override
    public STTrueFalseBlank.Enum[] getPrintObjectArray() {
        return (STTrueFalseBlank.Enum[])this.getEnumArray(PROPERTY_QNAME[5], STTrueFalseBlank.Enum[]::new);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public STTrueFalseBlank.Enum getPrintObjectArray(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)((Object)this.get_store().find_element_user(PROPERTY_QNAME[5], i));
            if (target == null) {
                throw new IndexOutOfBoundsException();
            }
            return (STTrueFalseBlank.Enum)target.getEnumValue();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public List<STTrueFalseBlank> xgetPrintObjectList() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return new JavaListXmlObject<STTrueFalseBlank>(this::xgetPrintObjectArray, this::xsetPrintObjectArray, this::insertNewPrintObject, this::removePrintObject, this::sizeOfPrintObjectArray);
        }
    }

    @Override
    public STTrueFalseBlank[] xgetPrintObjectArray() {
        return (STTrueFalseBlank[])this.xgetArray(PROPERTY_QNAME[5], STTrueFalseBlank[]::new);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public STTrueFalseBlank xgetPrintObjectArray(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            STTrueFalseBlank target = null;
            target = (STTrueFalseBlank)((Object)this.get_store().find_element_user(PROPERTY_QNAME[5], i));
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
    public int sizeOfPrintObjectArray() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[5]);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void setPrintObjectArray(STTrueFalseBlank.Enum[] printObjectArray) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.arraySetterHelper(printObjectArray, PROPERTY_QNAME[5]);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void setPrintObjectArray(int i, STTrueFalseBlank.Enum printObject) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)((Object)this.get_store().find_element_user(PROPERTY_QNAME[5], i));
            if (target == null) {
                throw new IndexOutOfBoundsException();
            }
            target.setEnumValue(printObject);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void xsetPrintObjectArray(STTrueFalseBlank[] printObjectArray) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.arraySetterHelper(printObjectArray, PROPERTY_QNAME[5]);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void xsetPrintObjectArray(int i, STTrueFalseBlank printObject) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            STTrueFalseBlank target = null;
            target = (STTrueFalseBlank)((Object)this.get_store().find_element_user(PROPERTY_QNAME[5], i));
            if (target == null) {
                throw new IndexOutOfBoundsException();
            }
            target.set(printObject);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void insertPrintObject(int i, STTrueFalseBlank.Enum printObject) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            SimpleValue target = (SimpleValue)((Object)this.get_store().insert_element_user(PROPERTY_QNAME[5], i));
            target.setEnumValue(printObject);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void addPrintObject(STTrueFalseBlank.Enum printObject) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)((Object)this.get_store().add_element_user(PROPERTY_QNAME[5]));
            target.setEnumValue(printObject);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public STTrueFalseBlank insertNewPrintObject(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            STTrueFalseBlank target = null;
            target = (STTrueFalseBlank)((Object)this.get_store().insert_element_user(PROPERTY_QNAME[5], i));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public STTrueFalseBlank addNewPrintObject() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            STTrueFalseBlank target = null;
            target = (STTrueFalseBlank)((Object)this.get_store().add_element_user(PROPERTY_QNAME[5]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void removePrintObject(int i) {
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
    public List<STTrueFalseBlank.Enum> getDisabledList() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return new JavaListObject<STTrueFalseBlank.Enum>(this::getDisabledArray, this::setDisabledArray, this::insertDisabled, this::removeDisabled, this::sizeOfDisabledArray);
        }
    }

    @Override
    public STTrueFalseBlank.Enum[] getDisabledArray() {
        return (STTrueFalseBlank.Enum[])this.getEnumArray(PROPERTY_QNAME[6], STTrueFalseBlank.Enum[]::new);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public STTrueFalseBlank.Enum getDisabledArray(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)((Object)this.get_store().find_element_user(PROPERTY_QNAME[6], i));
            if (target == null) {
                throw new IndexOutOfBoundsException();
            }
            return (STTrueFalseBlank.Enum)target.getEnumValue();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public List<STTrueFalseBlank> xgetDisabledList() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return new JavaListXmlObject<STTrueFalseBlank>(this::xgetDisabledArray, this::xsetDisabledArray, this::insertNewDisabled, this::removeDisabled, this::sizeOfDisabledArray);
        }
    }

    @Override
    public STTrueFalseBlank[] xgetDisabledArray() {
        return (STTrueFalseBlank[])this.xgetArray(PROPERTY_QNAME[6], STTrueFalseBlank[]::new);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public STTrueFalseBlank xgetDisabledArray(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            STTrueFalseBlank target = null;
            target = (STTrueFalseBlank)((Object)this.get_store().find_element_user(PROPERTY_QNAME[6], i));
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
    public int sizeOfDisabledArray() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[6]);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void setDisabledArray(STTrueFalseBlank.Enum[] disabledArray) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.arraySetterHelper(disabledArray, PROPERTY_QNAME[6]);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void setDisabledArray(int i, STTrueFalseBlank.Enum disabled) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)((Object)this.get_store().find_element_user(PROPERTY_QNAME[6], i));
            if (target == null) {
                throw new IndexOutOfBoundsException();
            }
            target.setEnumValue(disabled);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void xsetDisabledArray(STTrueFalseBlank[] disabledArray) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.arraySetterHelper(disabledArray, PROPERTY_QNAME[6]);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void xsetDisabledArray(int i, STTrueFalseBlank disabled) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            STTrueFalseBlank target = null;
            target = (STTrueFalseBlank)((Object)this.get_store().find_element_user(PROPERTY_QNAME[6], i));
            if (target == null) {
                throw new IndexOutOfBoundsException();
            }
            target.set(disabled);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void insertDisabled(int i, STTrueFalseBlank.Enum disabled) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            SimpleValue target = (SimpleValue)((Object)this.get_store().insert_element_user(PROPERTY_QNAME[6], i));
            target.setEnumValue(disabled);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void addDisabled(STTrueFalseBlank.Enum disabled) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)((Object)this.get_store().add_element_user(PROPERTY_QNAME[6]));
            target.setEnumValue(disabled);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public STTrueFalseBlank insertNewDisabled(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            STTrueFalseBlank target = null;
            target = (STTrueFalseBlank)((Object)this.get_store().insert_element_user(PROPERTY_QNAME[6], i));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public STTrueFalseBlank addNewDisabled() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            STTrueFalseBlank target = null;
            target = (STTrueFalseBlank)((Object)this.get_store().add_element_user(PROPERTY_QNAME[6]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void removeDisabled(int i) {
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
    public List<STTrueFalseBlank.Enum> getAutoFillList() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return new JavaListObject<STTrueFalseBlank.Enum>(this::getAutoFillArray, this::setAutoFillArray, this::insertAutoFill, this::removeAutoFill, this::sizeOfAutoFillArray);
        }
    }

    @Override
    public STTrueFalseBlank.Enum[] getAutoFillArray() {
        return (STTrueFalseBlank.Enum[])this.getEnumArray(PROPERTY_QNAME[7], STTrueFalseBlank.Enum[]::new);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public STTrueFalseBlank.Enum getAutoFillArray(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)((Object)this.get_store().find_element_user(PROPERTY_QNAME[7], i));
            if (target == null) {
                throw new IndexOutOfBoundsException();
            }
            return (STTrueFalseBlank.Enum)target.getEnumValue();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public List<STTrueFalseBlank> xgetAutoFillList() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return new JavaListXmlObject<STTrueFalseBlank>(this::xgetAutoFillArray, this::xsetAutoFillArray, this::insertNewAutoFill, this::removeAutoFill, this::sizeOfAutoFillArray);
        }
    }

    @Override
    public STTrueFalseBlank[] xgetAutoFillArray() {
        return (STTrueFalseBlank[])this.xgetArray(PROPERTY_QNAME[7], STTrueFalseBlank[]::new);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public STTrueFalseBlank xgetAutoFillArray(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            STTrueFalseBlank target = null;
            target = (STTrueFalseBlank)((Object)this.get_store().find_element_user(PROPERTY_QNAME[7], i));
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
    public int sizeOfAutoFillArray() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[7]);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void setAutoFillArray(STTrueFalseBlank.Enum[] autoFillArray) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.arraySetterHelper(autoFillArray, PROPERTY_QNAME[7]);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void setAutoFillArray(int i, STTrueFalseBlank.Enum autoFill) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)((Object)this.get_store().find_element_user(PROPERTY_QNAME[7], i));
            if (target == null) {
                throw new IndexOutOfBoundsException();
            }
            target.setEnumValue(autoFill);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void xsetAutoFillArray(STTrueFalseBlank[] autoFillArray) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.arraySetterHelper(autoFillArray, PROPERTY_QNAME[7]);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void xsetAutoFillArray(int i, STTrueFalseBlank autoFill) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            STTrueFalseBlank target = null;
            target = (STTrueFalseBlank)((Object)this.get_store().find_element_user(PROPERTY_QNAME[7], i));
            if (target == null) {
                throw new IndexOutOfBoundsException();
            }
            target.set(autoFill);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void insertAutoFill(int i, STTrueFalseBlank.Enum autoFill) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            SimpleValue target = (SimpleValue)((Object)this.get_store().insert_element_user(PROPERTY_QNAME[7], i));
            target.setEnumValue(autoFill);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void addAutoFill(STTrueFalseBlank.Enum autoFill) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)((Object)this.get_store().add_element_user(PROPERTY_QNAME[7]));
            target.setEnumValue(autoFill);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public STTrueFalseBlank insertNewAutoFill(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            STTrueFalseBlank target = null;
            target = (STTrueFalseBlank)((Object)this.get_store().insert_element_user(PROPERTY_QNAME[7], i));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public STTrueFalseBlank addNewAutoFill() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            STTrueFalseBlank target = null;
            target = (STTrueFalseBlank)((Object)this.get_store().add_element_user(PROPERTY_QNAME[7]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void removeAutoFill(int i) {
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
    public List<STTrueFalseBlank.Enum> getAutoLineList() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return new JavaListObject<STTrueFalseBlank.Enum>(this::getAutoLineArray, this::setAutoLineArray, this::insertAutoLine, this::removeAutoLine, this::sizeOfAutoLineArray);
        }
    }

    @Override
    public STTrueFalseBlank.Enum[] getAutoLineArray() {
        return (STTrueFalseBlank.Enum[])this.getEnumArray(PROPERTY_QNAME[8], STTrueFalseBlank.Enum[]::new);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public STTrueFalseBlank.Enum getAutoLineArray(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)((Object)this.get_store().find_element_user(PROPERTY_QNAME[8], i));
            if (target == null) {
                throw new IndexOutOfBoundsException();
            }
            return (STTrueFalseBlank.Enum)target.getEnumValue();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public List<STTrueFalseBlank> xgetAutoLineList() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return new JavaListXmlObject<STTrueFalseBlank>(this::xgetAutoLineArray, this::xsetAutoLineArray, this::insertNewAutoLine, this::removeAutoLine, this::sizeOfAutoLineArray);
        }
    }

    @Override
    public STTrueFalseBlank[] xgetAutoLineArray() {
        return (STTrueFalseBlank[])this.xgetArray(PROPERTY_QNAME[8], STTrueFalseBlank[]::new);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public STTrueFalseBlank xgetAutoLineArray(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            STTrueFalseBlank target = null;
            target = (STTrueFalseBlank)((Object)this.get_store().find_element_user(PROPERTY_QNAME[8], i));
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
    public int sizeOfAutoLineArray() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[8]);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void setAutoLineArray(STTrueFalseBlank.Enum[] autoLineArray) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.arraySetterHelper(autoLineArray, PROPERTY_QNAME[8]);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void setAutoLineArray(int i, STTrueFalseBlank.Enum autoLine) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)((Object)this.get_store().find_element_user(PROPERTY_QNAME[8], i));
            if (target == null) {
                throw new IndexOutOfBoundsException();
            }
            target.setEnumValue(autoLine);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void xsetAutoLineArray(STTrueFalseBlank[] autoLineArray) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.arraySetterHelper(autoLineArray, PROPERTY_QNAME[8]);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void xsetAutoLineArray(int i, STTrueFalseBlank autoLine) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            STTrueFalseBlank target = null;
            target = (STTrueFalseBlank)((Object)this.get_store().find_element_user(PROPERTY_QNAME[8], i));
            if (target == null) {
                throw new IndexOutOfBoundsException();
            }
            target.set(autoLine);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void insertAutoLine(int i, STTrueFalseBlank.Enum autoLine) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            SimpleValue target = (SimpleValue)((Object)this.get_store().insert_element_user(PROPERTY_QNAME[8], i));
            target.setEnumValue(autoLine);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void addAutoLine(STTrueFalseBlank.Enum autoLine) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)((Object)this.get_store().add_element_user(PROPERTY_QNAME[8]));
            target.setEnumValue(autoLine);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public STTrueFalseBlank insertNewAutoLine(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            STTrueFalseBlank target = null;
            target = (STTrueFalseBlank)((Object)this.get_store().insert_element_user(PROPERTY_QNAME[8], i));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public STTrueFalseBlank addNewAutoLine() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            STTrueFalseBlank target = null;
            target = (STTrueFalseBlank)((Object)this.get_store().add_element_user(PROPERTY_QNAME[8]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void removeAutoLine(int i) {
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
    public List<STTrueFalseBlank.Enum> getAutoPictList() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return new JavaListObject<STTrueFalseBlank.Enum>(this::getAutoPictArray, this::setAutoPictArray, this::insertAutoPict, this::removeAutoPict, this::sizeOfAutoPictArray);
        }
    }

    @Override
    public STTrueFalseBlank.Enum[] getAutoPictArray() {
        return (STTrueFalseBlank.Enum[])this.getEnumArray(PROPERTY_QNAME[9], STTrueFalseBlank.Enum[]::new);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public STTrueFalseBlank.Enum getAutoPictArray(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)((Object)this.get_store().find_element_user(PROPERTY_QNAME[9], i));
            if (target == null) {
                throw new IndexOutOfBoundsException();
            }
            return (STTrueFalseBlank.Enum)target.getEnumValue();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public List<STTrueFalseBlank> xgetAutoPictList() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return new JavaListXmlObject<STTrueFalseBlank>(this::xgetAutoPictArray, this::xsetAutoPictArray, this::insertNewAutoPict, this::removeAutoPict, this::sizeOfAutoPictArray);
        }
    }

    @Override
    public STTrueFalseBlank[] xgetAutoPictArray() {
        return (STTrueFalseBlank[])this.xgetArray(PROPERTY_QNAME[9], STTrueFalseBlank[]::new);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public STTrueFalseBlank xgetAutoPictArray(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            STTrueFalseBlank target = null;
            target = (STTrueFalseBlank)((Object)this.get_store().find_element_user(PROPERTY_QNAME[9], i));
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
    public int sizeOfAutoPictArray() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[9]);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void setAutoPictArray(STTrueFalseBlank.Enum[] autoPictArray) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.arraySetterHelper(autoPictArray, PROPERTY_QNAME[9]);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void setAutoPictArray(int i, STTrueFalseBlank.Enum autoPict) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)((Object)this.get_store().find_element_user(PROPERTY_QNAME[9], i));
            if (target == null) {
                throw new IndexOutOfBoundsException();
            }
            target.setEnumValue(autoPict);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void xsetAutoPictArray(STTrueFalseBlank[] autoPictArray) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.arraySetterHelper(autoPictArray, PROPERTY_QNAME[9]);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void xsetAutoPictArray(int i, STTrueFalseBlank autoPict) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            STTrueFalseBlank target = null;
            target = (STTrueFalseBlank)((Object)this.get_store().find_element_user(PROPERTY_QNAME[9], i));
            if (target == null) {
                throw new IndexOutOfBoundsException();
            }
            target.set(autoPict);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void insertAutoPict(int i, STTrueFalseBlank.Enum autoPict) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            SimpleValue target = (SimpleValue)((Object)this.get_store().insert_element_user(PROPERTY_QNAME[9], i));
            target.setEnumValue(autoPict);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void addAutoPict(STTrueFalseBlank.Enum autoPict) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)((Object)this.get_store().add_element_user(PROPERTY_QNAME[9]));
            target.setEnumValue(autoPict);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public STTrueFalseBlank insertNewAutoPict(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            STTrueFalseBlank target = null;
            target = (STTrueFalseBlank)((Object)this.get_store().insert_element_user(PROPERTY_QNAME[9], i));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public STTrueFalseBlank addNewAutoPict() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            STTrueFalseBlank target = null;
            target = (STTrueFalseBlank)((Object)this.get_store().add_element_user(PROPERTY_QNAME[9]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void removeAutoPict(int i) {
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
    public List<String> getFmlaMacroList() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return new JavaListObject<String>(this::getFmlaMacroArray, this::setFmlaMacroArray, this::insertFmlaMacro, this::removeFmlaMacro, this::sizeOfFmlaMacroArray);
        }
    }

    @Override
    public String[] getFmlaMacroArray() {
        return this.getObjectArray(PROPERTY_QNAME[10], SimpleValue::getStringValue, String[]::new);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public String getFmlaMacroArray(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)((Object)this.get_store().find_element_user(PROPERTY_QNAME[10], i));
            if (target == null) {
                throw new IndexOutOfBoundsException();
            }
            return target.getStringValue();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public List<XmlString> xgetFmlaMacroList() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return new JavaListXmlObject<XmlString>(this::xgetFmlaMacroArray, this::xsetFmlaMacroArray, this::insertNewFmlaMacro, this::removeFmlaMacro, this::sizeOfFmlaMacroArray);
        }
    }

    @Override
    public XmlString[] xgetFmlaMacroArray() {
        return (XmlString[])this.xgetArray(PROPERTY_QNAME[10], XmlString[]::new);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public XmlString xgetFmlaMacroArray(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            XmlString target = null;
            target = (XmlString)((Object)this.get_store().find_element_user(PROPERTY_QNAME[10], i));
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
    public int sizeOfFmlaMacroArray() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[10]);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void setFmlaMacroArray(String[] fmlaMacroArray) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.arraySetterHelper(fmlaMacroArray, PROPERTY_QNAME[10]);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void setFmlaMacroArray(int i, String fmlaMacro) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)((Object)this.get_store().find_element_user(PROPERTY_QNAME[10], i));
            if (target == null) {
                throw new IndexOutOfBoundsException();
            }
            target.setStringValue(fmlaMacro);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void xsetFmlaMacroArray(XmlString[] fmlaMacroArray) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.arraySetterHelper(fmlaMacroArray, PROPERTY_QNAME[10]);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void xsetFmlaMacroArray(int i, XmlString fmlaMacro) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            XmlString target = null;
            target = (XmlString)((Object)this.get_store().find_element_user(PROPERTY_QNAME[10], i));
            if (target == null) {
                throw new IndexOutOfBoundsException();
            }
            target.set(fmlaMacro);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void insertFmlaMacro(int i, String fmlaMacro) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            SimpleValue target = (SimpleValue)((Object)this.get_store().insert_element_user(PROPERTY_QNAME[10], i));
            target.setStringValue(fmlaMacro);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void addFmlaMacro(String fmlaMacro) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)((Object)this.get_store().add_element_user(PROPERTY_QNAME[10]));
            target.setStringValue(fmlaMacro);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public XmlString insertNewFmlaMacro(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            XmlString target = null;
            target = (XmlString)((Object)this.get_store().insert_element_user(PROPERTY_QNAME[10], i));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public XmlString addNewFmlaMacro() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            XmlString target = null;
            target = (XmlString)((Object)this.get_store().add_element_user(PROPERTY_QNAME[10]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void removeFmlaMacro(int i) {
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
    public List<String> getTextHAlignList() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return new JavaListObject<String>(this::getTextHAlignArray, this::setTextHAlignArray, this::insertTextHAlign, this::removeTextHAlign, this::sizeOfTextHAlignArray);
        }
    }

    @Override
    public String[] getTextHAlignArray() {
        return this.getObjectArray(PROPERTY_QNAME[11], SimpleValue::getStringValue, String[]::new);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public String getTextHAlignArray(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)((Object)this.get_store().find_element_user(PROPERTY_QNAME[11], i));
            if (target == null) {
                throw new IndexOutOfBoundsException();
            }
            return target.getStringValue();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public List<XmlString> xgetTextHAlignList() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return new JavaListXmlObject<XmlString>(this::xgetTextHAlignArray, this::xsetTextHAlignArray, this::insertNewTextHAlign, this::removeTextHAlign, this::sizeOfTextHAlignArray);
        }
    }

    @Override
    public XmlString[] xgetTextHAlignArray() {
        return (XmlString[])this.xgetArray(PROPERTY_QNAME[11], XmlString[]::new);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public XmlString xgetTextHAlignArray(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            XmlString target = null;
            target = (XmlString)((Object)this.get_store().find_element_user(PROPERTY_QNAME[11], i));
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
    public int sizeOfTextHAlignArray() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[11]);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void setTextHAlignArray(String[] textHAlignArray) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.arraySetterHelper(textHAlignArray, PROPERTY_QNAME[11]);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void setTextHAlignArray(int i, String textHAlign) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)((Object)this.get_store().find_element_user(PROPERTY_QNAME[11], i));
            if (target == null) {
                throw new IndexOutOfBoundsException();
            }
            target.setStringValue(textHAlign);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void xsetTextHAlignArray(XmlString[] textHAlignArray) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.arraySetterHelper(textHAlignArray, PROPERTY_QNAME[11]);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void xsetTextHAlignArray(int i, XmlString textHAlign) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            XmlString target = null;
            target = (XmlString)((Object)this.get_store().find_element_user(PROPERTY_QNAME[11], i));
            if (target == null) {
                throw new IndexOutOfBoundsException();
            }
            target.set(textHAlign);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void insertTextHAlign(int i, String textHAlign) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            SimpleValue target = (SimpleValue)((Object)this.get_store().insert_element_user(PROPERTY_QNAME[11], i));
            target.setStringValue(textHAlign);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void addTextHAlign(String textHAlign) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)((Object)this.get_store().add_element_user(PROPERTY_QNAME[11]));
            target.setStringValue(textHAlign);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public XmlString insertNewTextHAlign(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            XmlString target = null;
            target = (XmlString)((Object)this.get_store().insert_element_user(PROPERTY_QNAME[11], i));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public XmlString addNewTextHAlign() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            XmlString target = null;
            target = (XmlString)((Object)this.get_store().add_element_user(PROPERTY_QNAME[11]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void removeTextHAlign(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.get_store().remove_element(PROPERTY_QNAME[11], i);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public List<String> getTextVAlignList() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return new JavaListObject<String>(this::getTextVAlignArray, this::setTextVAlignArray, this::insertTextVAlign, this::removeTextVAlign, this::sizeOfTextVAlignArray);
        }
    }

    @Override
    public String[] getTextVAlignArray() {
        return this.getObjectArray(PROPERTY_QNAME[12], SimpleValue::getStringValue, String[]::new);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public String getTextVAlignArray(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)((Object)this.get_store().find_element_user(PROPERTY_QNAME[12], i));
            if (target == null) {
                throw new IndexOutOfBoundsException();
            }
            return target.getStringValue();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public List<XmlString> xgetTextVAlignList() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return new JavaListXmlObject<XmlString>(this::xgetTextVAlignArray, this::xsetTextVAlignArray, this::insertNewTextVAlign, this::removeTextVAlign, this::sizeOfTextVAlignArray);
        }
    }

    @Override
    public XmlString[] xgetTextVAlignArray() {
        return (XmlString[])this.xgetArray(PROPERTY_QNAME[12], XmlString[]::new);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public XmlString xgetTextVAlignArray(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            XmlString target = null;
            target = (XmlString)((Object)this.get_store().find_element_user(PROPERTY_QNAME[12], i));
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
    public int sizeOfTextVAlignArray() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[12]);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void setTextVAlignArray(String[] textVAlignArray) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.arraySetterHelper(textVAlignArray, PROPERTY_QNAME[12]);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void setTextVAlignArray(int i, String textVAlign) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)((Object)this.get_store().find_element_user(PROPERTY_QNAME[12], i));
            if (target == null) {
                throw new IndexOutOfBoundsException();
            }
            target.setStringValue(textVAlign);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void xsetTextVAlignArray(XmlString[] textVAlignArray) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.arraySetterHelper(textVAlignArray, PROPERTY_QNAME[12]);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void xsetTextVAlignArray(int i, XmlString textVAlign) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            XmlString target = null;
            target = (XmlString)((Object)this.get_store().find_element_user(PROPERTY_QNAME[12], i));
            if (target == null) {
                throw new IndexOutOfBoundsException();
            }
            target.set(textVAlign);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void insertTextVAlign(int i, String textVAlign) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            SimpleValue target = (SimpleValue)((Object)this.get_store().insert_element_user(PROPERTY_QNAME[12], i));
            target.setStringValue(textVAlign);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void addTextVAlign(String textVAlign) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)((Object)this.get_store().add_element_user(PROPERTY_QNAME[12]));
            target.setStringValue(textVAlign);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public XmlString insertNewTextVAlign(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            XmlString target = null;
            target = (XmlString)((Object)this.get_store().insert_element_user(PROPERTY_QNAME[12], i));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public XmlString addNewTextVAlign() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            XmlString target = null;
            target = (XmlString)((Object)this.get_store().add_element_user(PROPERTY_QNAME[12]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void removeTextVAlign(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.get_store().remove_element(PROPERTY_QNAME[12], i);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public List<STTrueFalseBlank.Enum> getLockTextList() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return new JavaListObject<STTrueFalseBlank.Enum>(this::getLockTextArray, this::setLockTextArray, this::insertLockText, this::removeLockText, this::sizeOfLockTextArray);
        }
    }

    @Override
    public STTrueFalseBlank.Enum[] getLockTextArray() {
        return (STTrueFalseBlank.Enum[])this.getEnumArray(PROPERTY_QNAME[13], STTrueFalseBlank.Enum[]::new);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public STTrueFalseBlank.Enum getLockTextArray(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)((Object)this.get_store().find_element_user(PROPERTY_QNAME[13], i));
            if (target == null) {
                throw new IndexOutOfBoundsException();
            }
            return (STTrueFalseBlank.Enum)target.getEnumValue();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public List<STTrueFalseBlank> xgetLockTextList() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return new JavaListXmlObject<STTrueFalseBlank>(this::xgetLockTextArray, this::xsetLockTextArray, this::insertNewLockText, this::removeLockText, this::sizeOfLockTextArray);
        }
    }

    @Override
    public STTrueFalseBlank[] xgetLockTextArray() {
        return (STTrueFalseBlank[])this.xgetArray(PROPERTY_QNAME[13], STTrueFalseBlank[]::new);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public STTrueFalseBlank xgetLockTextArray(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            STTrueFalseBlank target = null;
            target = (STTrueFalseBlank)((Object)this.get_store().find_element_user(PROPERTY_QNAME[13], i));
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
    public int sizeOfLockTextArray() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[13]);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void setLockTextArray(STTrueFalseBlank.Enum[] lockTextArray) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.arraySetterHelper(lockTextArray, PROPERTY_QNAME[13]);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void setLockTextArray(int i, STTrueFalseBlank.Enum lockText) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)((Object)this.get_store().find_element_user(PROPERTY_QNAME[13], i));
            if (target == null) {
                throw new IndexOutOfBoundsException();
            }
            target.setEnumValue(lockText);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void xsetLockTextArray(STTrueFalseBlank[] lockTextArray) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.arraySetterHelper(lockTextArray, PROPERTY_QNAME[13]);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void xsetLockTextArray(int i, STTrueFalseBlank lockText) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            STTrueFalseBlank target = null;
            target = (STTrueFalseBlank)((Object)this.get_store().find_element_user(PROPERTY_QNAME[13], i));
            if (target == null) {
                throw new IndexOutOfBoundsException();
            }
            target.set(lockText);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void insertLockText(int i, STTrueFalseBlank.Enum lockText) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            SimpleValue target = (SimpleValue)((Object)this.get_store().insert_element_user(PROPERTY_QNAME[13], i));
            target.setEnumValue(lockText);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void addLockText(STTrueFalseBlank.Enum lockText) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)((Object)this.get_store().add_element_user(PROPERTY_QNAME[13]));
            target.setEnumValue(lockText);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public STTrueFalseBlank insertNewLockText(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            STTrueFalseBlank target = null;
            target = (STTrueFalseBlank)((Object)this.get_store().insert_element_user(PROPERTY_QNAME[13], i));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public STTrueFalseBlank addNewLockText() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            STTrueFalseBlank target = null;
            target = (STTrueFalseBlank)((Object)this.get_store().add_element_user(PROPERTY_QNAME[13]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void removeLockText(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.get_store().remove_element(PROPERTY_QNAME[13], i);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public List<STTrueFalseBlank.Enum> getJustLastXList() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return new JavaListObject<STTrueFalseBlank.Enum>(this::getJustLastXArray, this::setJustLastXArray, this::insertJustLastX, this::removeJustLastX, this::sizeOfJustLastXArray);
        }
    }

    @Override
    public STTrueFalseBlank.Enum[] getJustLastXArray() {
        return (STTrueFalseBlank.Enum[])this.getEnumArray(PROPERTY_QNAME[14], STTrueFalseBlank.Enum[]::new);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public STTrueFalseBlank.Enum getJustLastXArray(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)((Object)this.get_store().find_element_user(PROPERTY_QNAME[14], i));
            if (target == null) {
                throw new IndexOutOfBoundsException();
            }
            return (STTrueFalseBlank.Enum)target.getEnumValue();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public List<STTrueFalseBlank> xgetJustLastXList() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return new JavaListXmlObject<STTrueFalseBlank>(this::xgetJustLastXArray, this::xsetJustLastXArray, this::insertNewJustLastX, this::removeJustLastX, this::sizeOfJustLastXArray);
        }
    }

    @Override
    public STTrueFalseBlank[] xgetJustLastXArray() {
        return (STTrueFalseBlank[])this.xgetArray(PROPERTY_QNAME[14], STTrueFalseBlank[]::new);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public STTrueFalseBlank xgetJustLastXArray(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            STTrueFalseBlank target = null;
            target = (STTrueFalseBlank)((Object)this.get_store().find_element_user(PROPERTY_QNAME[14], i));
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
    public int sizeOfJustLastXArray() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[14]);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void setJustLastXArray(STTrueFalseBlank.Enum[] justLastXArray) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.arraySetterHelper(justLastXArray, PROPERTY_QNAME[14]);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void setJustLastXArray(int i, STTrueFalseBlank.Enum justLastX) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)((Object)this.get_store().find_element_user(PROPERTY_QNAME[14], i));
            if (target == null) {
                throw new IndexOutOfBoundsException();
            }
            target.setEnumValue(justLastX);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void xsetJustLastXArray(STTrueFalseBlank[] justLastXArray) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.arraySetterHelper(justLastXArray, PROPERTY_QNAME[14]);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void xsetJustLastXArray(int i, STTrueFalseBlank justLastX) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            STTrueFalseBlank target = null;
            target = (STTrueFalseBlank)((Object)this.get_store().find_element_user(PROPERTY_QNAME[14], i));
            if (target == null) {
                throw new IndexOutOfBoundsException();
            }
            target.set(justLastX);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void insertJustLastX(int i, STTrueFalseBlank.Enum justLastX) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            SimpleValue target = (SimpleValue)((Object)this.get_store().insert_element_user(PROPERTY_QNAME[14], i));
            target.setEnumValue(justLastX);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void addJustLastX(STTrueFalseBlank.Enum justLastX) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)((Object)this.get_store().add_element_user(PROPERTY_QNAME[14]));
            target.setEnumValue(justLastX);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public STTrueFalseBlank insertNewJustLastX(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            STTrueFalseBlank target = null;
            target = (STTrueFalseBlank)((Object)this.get_store().insert_element_user(PROPERTY_QNAME[14], i));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public STTrueFalseBlank addNewJustLastX() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            STTrueFalseBlank target = null;
            target = (STTrueFalseBlank)((Object)this.get_store().add_element_user(PROPERTY_QNAME[14]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void removeJustLastX(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.get_store().remove_element(PROPERTY_QNAME[14], i);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public List<STTrueFalseBlank.Enum> getSecretEditList() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return new JavaListObject<STTrueFalseBlank.Enum>(this::getSecretEditArray, this::setSecretEditArray, this::insertSecretEdit, this::removeSecretEdit, this::sizeOfSecretEditArray);
        }
    }

    @Override
    public STTrueFalseBlank.Enum[] getSecretEditArray() {
        return (STTrueFalseBlank.Enum[])this.getEnumArray(PROPERTY_QNAME[15], STTrueFalseBlank.Enum[]::new);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public STTrueFalseBlank.Enum getSecretEditArray(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)((Object)this.get_store().find_element_user(PROPERTY_QNAME[15], i));
            if (target == null) {
                throw new IndexOutOfBoundsException();
            }
            return (STTrueFalseBlank.Enum)target.getEnumValue();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public List<STTrueFalseBlank> xgetSecretEditList() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return new JavaListXmlObject<STTrueFalseBlank>(this::xgetSecretEditArray, this::xsetSecretEditArray, this::insertNewSecretEdit, this::removeSecretEdit, this::sizeOfSecretEditArray);
        }
    }

    @Override
    public STTrueFalseBlank[] xgetSecretEditArray() {
        return (STTrueFalseBlank[])this.xgetArray(PROPERTY_QNAME[15], STTrueFalseBlank[]::new);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public STTrueFalseBlank xgetSecretEditArray(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            STTrueFalseBlank target = null;
            target = (STTrueFalseBlank)((Object)this.get_store().find_element_user(PROPERTY_QNAME[15], i));
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
    public int sizeOfSecretEditArray() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[15]);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void setSecretEditArray(STTrueFalseBlank.Enum[] secretEditArray) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.arraySetterHelper(secretEditArray, PROPERTY_QNAME[15]);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void setSecretEditArray(int i, STTrueFalseBlank.Enum secretEdit) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)((Object)this.get_store().find_element_user(PROPERTY_QNAME[15], i));
            if (target == null) {
                throw new IndexOutOfBoundsException();
            }
            target.setEnumValue(secretEdit);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void xsetSecretEditArray(STTrueFalseBlank[] secretEditArray) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.arraySetterHelper(secretEditArray, PROPERTY_QNAME[15]);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void xsetSecretEditArray(int i, STTrueFalseBlank secretEdit) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            STTrueFalseBlank target = null;
            target = (STTrueFalseBlank)((Object)this.get_store().find_element_user(PROPERTY_QNAME[15], i));
            if (target == null) {
                throw new IndexOutOfBoundsException();
            }
            target.set(secretEdit);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void insertSecretEdit(int i, STTrueFalseBlank.Enum secretEdit) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            SimpleValue target = (SimpleValue)((Object)this.get_store().insert_element_user(PROPERTY_QNAME[15], i));
            target.setEnumValue(secretEdit);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void addSecretEdit(STTrueFalseBlank.Enum secretEdit) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)((Object)this.get_store().add_element_user(PROPERTY_QNAME[15]));
            target.setEnumValue(secretEdit);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public STTrueFalseBlank insertNewSecretEdit(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            STTrueFalseBlank target = null;
            target = (STTrueFalseBlank)((Object)this.get_store().insert_element_user(PROPERTY_QNAME[15], i));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public STTrueFalseBlank addNewSecretEdit() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            STTrueFalseBlank target = null;
            target = (STTrueFalseBlank)((Object)this.get_store().add_element_user(PROPERTY_QNAME[15]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void removeSecretEdit(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.get_store().remove_element(PROPERTY_QNAME[15], i);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public List<STTrueFalseBlank.Enum> getDefaultList() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return new JavaListObject<STTrueFalseBlank.Enum>(this::getDefaultArray, this::setDefaultArray, this::insertDefault, this::removeDefault, this::sizeOfDefaultArray);
        }
    }

    @Override
    public STTrueFalseBlank.Enum[] getDefaultArray() {
        return (STTrueFalseBlank.Enum[])this.getEnumArray(PROPERTY_QNAME[16], STTrueFalseBlank.Enum[]::new);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public STTrueFalseBlank.Enum getDefaultArray(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)((Object)this.get_store().find_element_user(PROPERTY_QNAME[16], i));
            if (target == null) {
                throw new IndexOutOfBoundsException();
            }
            return (STTrueFalseBlank.Enum)target.getEnumValue();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public List<STTrueFalseBlank> xgetDefaultList() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return new JavaListXmlObject<STTrueFalseBlank>(this::xgetDefaultArray, this::xsetDefaultArray, this::insertNewDefault, this::removeDefault, this::sizeOfDefaultArray);
        }
    }

    @Override
    public STTrueFalseBlank[] xgetDefaultArray() {
        return (STTrueFalseBlank[])this.xgetArray(PROPERTY_QNAME[16], STTrueFalseBlank[]::new);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public STTrueFalseBlank xgetDefaultArray(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            STTrueFalseBlank target = null;
            target = (STTrueFalseBlank)((Object)this.get_store().find_element_user(PROPERTY_QNAME[16], i));
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
    public int sizeOfDefaultArray() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[16]);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void setDefaultArray(STTrueFalseBlank.Enum[] xdefaultArray) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.arraySetterHelper(xdefaultArray, PROPERTY_QNAME[16]);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void setDefaultArray(int i, STTrueFalseBlank.Enum xdefault) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)((Object)this.get_store().find_element_user(PROPERTY_QNAME[16], i));
            if (target == null) {
                throw new IndexOutOfBoundsException();
            }
            target.setEnumValue(xdefault);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void xsetDefaultArray(STTrueFalseBlank[] xdefaultArray) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.arraySetterHelper(xdefaultArray, PROPERTY_QNAME[16]);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void xsetDefaultArray(int i, STTrueFalseBlank xdefault) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            STTrueFalseBlank target = null;
            target = (STTrueFalseBlank)((Object)this.get_store().find_element_user(PROPERTY_QNAME[16], i));
            if (target == null) {
                throw new IndexOutOfBoundsException();
            }
            target.set(xdefault);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void insertDefault(int i, STTrueFalseBlank.Enum xdefault) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            SimpleValue target = (SimpleValue)((Object)this.get_store().insert_element_user(PROPERTY_QNAME[16], i));
            target.setEnumValue(xdefault);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void addDefault(STTrueFalseBlank.Enum xdefault) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)((Object)this.get_store().add_element_user(PROPERTY_QNAME[16]));
            target.setEnumValue(xdefault);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public STTrueFalseBlank insertNewDefault(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            STTrueFalseBlank target = null;
            target = (STTrueFalseBlank)((Object)this.get_store().insert_element_user(PROPERTY_QNAME[16], i));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public STTrueFalseBlank addNewDefault() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            STTrueFalseBlank target = null;
            target = (STTrueFalseBlank)((Object)this.get_store().add_element_user(PROPERTY_QNAME[16]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void removeDefault(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.get_store().remove_element(PROPERTY_QNAME[16], i);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public List<STTrueFalseBlank.Enum> getHelpList() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return new JavaListObject<STTrueFalseBlank.Enum>(this::getHelpArray, this::setHelpArray, this::insertHelp, this::removeHelp, this::sizeOfHelpArray);
        }
    }

    @Override
    public STTrueFalseBlank.Enum[] getHelpArray() {
        return (STTrueFalseBlank.Enum[])this.getEnumArray(PROPERTY_QNAME[17], STTrueFalseBlank.Enum[]::new);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public STTrueFalseBlank.Enum getHelpArray(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)((Object)this.get_store().find_element_user(PROPERTY_QNAME[17], i));
            if (target == null) {
                throw new IndexOutOfBoundsException();
            }
            return (STTrueFalseBlank.Enum)target.getEnumValue();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public List<STTrueFalseBlank> xgetHelpList() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return new JavaListXmlObject<STTrueFalseBlank>(this::xgetHelpArray, this::xsetHelpArray, this::insertNewHelp, this::removeHelp, this::sizeOfHelpArray);
        }
    }

    @Override
    public STTrueFalseBlank[] xgetHelpArray() {
        return (STTrueFalseBlank[])this.xgetArray(PROPERTY_QNAME[17], STTrueFalseBlank[]::new);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public STTrueFalseBlank xgetHelpArray(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            STTrueFalseBlank target = null;
            target = (STTrueFalseBlank)((Object)this.get_store().find_element_user(PROPERTY_QNAME[17], i));
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
    public int sizeOfHelpArray() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[17]);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void setHelpArray(STTrueFalseBlank.Enum[] helpArray) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.arraySetterHelper(helpArray, PROPERTY_QNAME[17]);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void setHelpArray(int i, STTrueFalseBlank.Enum help) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)((Object)this.get_store().find_element_user(PROPERTY_QNAME[17], i));
            if (target == null) {
                throw new IndexOutOfBoundsException();
            }
            target.setEnumValue(help);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void xsetHelpArray(STTrueFalseBlank[] helpArray) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.arraySetterHelper(helpArray, PROPERTY_QNAME[17]);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void xsetHelpArray(int i, STTrueFalseBlank help) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            STTrueFalseBlank target = null;
            target = (STTrueFalseBlank)((Object)this.get_store().find_element_user(PROPERTY_QNAME[17], i));
            if (target == null) {
                throw new IndexOutOfBoundsException();
            }
            target.set(help);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void insertHelp(int i, STTrueFalseBlank.Enum help) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            SimpleValue target = (SimpleValue)((Object)this.get_store().insert_element_user(PROPERTY_QNAME[17], i));
            target.setEnumValue(help);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void addHelp(STTrueFalseBlank.Enum help) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)((Object)this.get_store().add_element_user(PROPERTY_QNAME[17]));
            target.setEnumValue(help);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public STTrueFalseBlank insertNewHelp(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            STTrueFalseBlank target = null;
            target = (STTrueFalseBlank)((Object)this.get_store().insert_element_user(PROPERTY_QNAME[17], i));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public STTrueFalseBlank addNewHelp() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            STTrueFalseBlank target = null;
            target = (STTrueFalseBlank)((Object)this.get_store().add_element_user(PROPERTY_QNAME[17]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void removeHelp(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.get_store().remove_element(PROPERTY_QNAME[17], i);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public List<STTrueFalseBlank.Enum> getCancelList() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return new JavaListObject<STTrueFalseBlank.Enum>(this::getCancelArray, this::setCancelArray, this::insertCancel, this::removeCancel, this::sizeOfCancelArray);
        }
    }

    @Override
    public STTrueFalseBlank.Enum[] getCancelArray() {
        return (STTrueFalseBlank.Enum[])this.getEnumArray(PROPERTY_QNAME[18], STTrueFalseBlank.Enum[]::new);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public STTrueFalseBlank.Enum getCancelArray(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)((Object)this.get_store().find_element_user(PROPERTY_QNAME[18], i));
            if (target == null) {
                throw new IndexOutOfBoundsException();
            }
            return (STTrueFalseBlank.Enum)target.getEnumValue();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public List<STTrueFalseBlank> xgetCancelList() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return new JavaListXmlObject<STTrueFalseBlank>(this::xgetCancelArray, this::xsetCancelArray, this::insertNewCancel, this::removeCancel, this::sizeOfCancelArray);
        }
    }

    @Override
    public STTrueFalseBlank[] xgetCancelArray() {
        return (STTrueFalseBlank[])this.xgetArray(PROPERTY_QNAME[18], STTrueFalseBlank[]::new);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public STTrueFalseBlank xgetCancelArray(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            STTrueFalseBlank target = null;
            target = (STTrueFalseBlank)((Object)this.get_store().find_element_user(PROPERTY_QNAME[18], i));
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
    public int sizeOfCancelArray() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[18]);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void setCancelArray(STTrueFalseBlank.Enum[] cancelArray) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.arraySetterHelper(cancelArray, PROPERTY_QNAME[18]);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void setCancelArray(int i, STTrueFalseBlank.Enum cancel) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)((Object)this.get_store().find_element_user(PROPERTY_QNAME[18], i));
            if (target == null) {
                throw new IndexOutOfBoundsException();
            }
            target.setEnumValue(cancel);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void xsetCancelArray(STTrueFalseBlank[] cancelArray) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.arraySetterHelper(cancelArray, PROPERTY_QNAME[18]);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void xsetCancelArray(int i, STTrueFalseBlank cancel) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            STTrueFalseBlank target = null;
            target = (STTrueFalseBlank)((Object)this.get_store().find_element_user(PROPERTY_QNAME[18], i));
            if (target == null) {
                throw new IndexOutOfBoundsException();
            }
            target.set(cancel);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void insertCancel(int i, STTrueFalseBlank.Enum cancel) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            SimpleValue target = (SimpleValue)((Object)this.get_store().insert_element_user(PROPERTY_QNAME[18], i));
            target.setEnumValue(cancel);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void addCancel(STTrueFalseBlank.Enum cancel) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)((Object)this.get_store().add_element_user(PROPERTY_QNAME[18]));
            target.setEnumValue(cancel);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public STTrueFalseBlank insertNewCancel(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            STTrueFalseBlank target = null;
            target = (STTrueFalseBlank)((Object)this.get_store().insert_element_user(PROPERTY_QNAME[18], i));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public STTrueFalseBlank addNewCancel() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            STTrueFalseBlank target = null;
            target = (STTrueFalseBlank)((Object)this.get_store().add_element_user(PROPERTY_QNAME[18]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void removeCancel(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.get_store().remove_element(PROPERTY_QNAME[18], i);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public List<STTrueFalseBlank.Enum> getDismissList() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return new JavaListObject<STTrueFalseBlank.Enum>(this::getDismissArray, this::setDismissArray, this::insertDismiss, this::removeDismiss, this::sizeOfDismissArray);
        }
    }

    @Override
    public STTrueFalseBlank.Enum[] getDismissArray() {
        return (STTrueFalseBlank.Enum[])this.getEnumArray(PROPERTY_QNAME[19], STTrueFalseBlank.Enum[]::new);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public STTrueFalseBlank.Enum getDismissArray(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)((Object)this.get_store().find_element_user(PROPERTY_QNAME[19], i));
            if (target == null) {
                throw new IndexOutOfBoundsException();
            }
            return (STTrueFalseBlank.Enum)target.getEnumValue();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public List<STTrueFalseBlank> xgetDismissList() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return new JavaListXmlObject<STTrueFalseBlank>(this::xgetDismissArray, this::xsetDismissArray, this::insertNewDismiss, this::removeDismiss, this::sizeOfDismissArray);
        }
    }

    @Override
    public STTrueFalseBlank[] xgetDismissArray() {
        return (STTrueFalseBlank[])this.xgetArray(PROPERTY_QNAME[19], STTrueFalseBlank[]::new);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public STTrueFalseBlank xgetDismissArray(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            STTrueFalseBlank target = null;
            target = (STTrueFalseBlank)((Object)this.get_store().find_element_user(PROPERTY_QNAME[19], i));
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
    public int sizeOfDismissArray() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[19]);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void setDismissArray(STTrueFalseBlank.Enum[] dismissArray) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.arraySetterHelper(dismissArray, PROPERTY_QNAME[19]);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void setDismissArray(int i, STTrueFalseBlank.Enum dismiss) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)((Object)this.get_store().find_element_user(PROPERTY_QNAME[19], i));
            if (target == null) {
                throw new IndexOutOfBoundsException();
            }
            target.setEnumValue(dismiss);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void xsetDismissArray(STTrueFalseBlank[] dismissArray) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.arraySetterHelper(dismissArray, PROPERTY_QNAME[19]);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void xsetDismissArray(int i, STTrueFalseBlank dismiss) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            STTrueFalseBlank target = null;
            target = (STTrueFalseBlank)((Object)this.get_store().find_element_user(PROPERTY_QNAME[19], i));
            if (target == null) {
                throw new IndexOutOfBoundsException();
            }
            target.set(dismiss);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void insertDismiss(int i, STTrueFalseBlank.Enum dismiss) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            SimpleValue target = (SimpleValue)((Object)this.get_store().insert_element_user(PROPERTY_QNAME[19], i));
            target.setEnumValue(dismiss);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void addDismiss(STTrueFalseBlank.Enum dismiss) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)((Object)this.get_store().add_element_user(PROPERTY_QNAME[19]));
            target.setEnumValue(dismiss);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public STTrueFalseBlank insertNewDismiss(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            STTrueFalseBlank target = null;
            target = (STTrueFalseBlank)((Object)this.get_store().insert_element_user(PROPERTY_QNAME[19], i));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public STTrueFalseBlank addNewDismiss() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            STTrueFalseBlank target = null;
            target = (STTrueFalseBlank)((Object)this.get_store().add_element_user(PROPERTY_QNAME[19]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void removeDismiss(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.get_store().remove_element(PROPERTY_QNAME[19], i);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public List<BigInteger> getAccelList() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return new JavaListObject<BigInteger>(this::getAccelArray, this::setAccelArray, this::insertAccel, this::removeAccel, this::sizeOfAccelArray);
        }
    }

    @Override
    public BigInteger[] getAccelArray() {
        return this.getObjectArray(PROPERTY_QNAME[20], SimpleValue::getBigIntegerValue, BigInteger[]::new);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public BigInteger getAccelArray(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)((Object)this.get_store().find_element_user(PROPERTY_QNAME[20], i));
            if (target == null) {
                throw new IndexOutOfBoundsException();
            }
            return target.getBigIntegerValue();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public List<XmlInteger> xgetAccelList() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return new JavaListXmlObject<XmlInteger>(this::xgetAccelArray, this::xsetAccelArray, this::insertNewAccel, this::removeAccel, this::sizeOfAccelArray);
        }
    }

    @Override
    public XmlInteger[] xgetAccelArray() {
        return (XmlInteger[])this.xgetArray(PROPERTY_QNAME[20], XmlInteger[]::new);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public XmlInteger xgetAccelArray(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            XmlInteger target = null;
            target = (XmlInteger)((Object)this.get_store().find_element_user(PROPERTY_QNAME[20], i));
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
    public int sizeOfAccelArray() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[20]);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void setAccelArray(BigInteger[] accelArray) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.arraySetterHelper(accelArray, PROPERTY_QNAME[20]);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void setAccelArray(int i, BigInteger accel) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)((Object)this.get_store().find_element_user(PROPERTY_QNAME[20], i));
            if (target == null) {
                throw new IndexOutOfBoundsException();
            }
            target.setBigIntegerValue(accel);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void xsetAccelArray(XmlInteger[] accelArray) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.arraySetterHelper(accelArray, PROPERTY_QNAME[20]);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void xsetAccelArray(int i, XmlInteger accel) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            XmlInteger target = null;
            target = (XmlInteger)((Object)this.get_store().find_element_user(PROPERTY_QNAME[20], i));
            if (target == null) {
                throw new IndexOutOfBoundsException();
            }
            target.set(accel);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void insertAccel(int i, BigInteger accel) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            SimpleValue target = (SimpleValue)((Object)this.get_store().insert_element_user(PROPERTY_QNAME[20], i));
            target.setBigIntegerValue(accel);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void addAccel(BigInteger accel) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)((Object)this.get_store().add_element_user(PROPERTY_QNAME[20]));
            target.setBigIntegerValue(accel);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public XmlInteger insertNewAccel(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            XmlInteger target = null;
            target = (XmlInteger)((Object)this.get_store().insert_element_user(PROPERTY_QNAME[20], i));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public XmlInteger addNewAccel() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            XmlInteger target = null;
            target = (XmlInteger)((Object)this.get_store().add_element_user(PROPERTY_QNAME[20]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void removeAccel(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.get_store().remove_element(PROPERTY_QNAME[20], i);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public List<BigInteger> getAccel2List() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return new JavaListObject<BigInteger>(this::getAccel2Array, this::setAccel2Array, this::insertAccel2, this::removeAccel2, this::sizeOfAccel2Array);
        }
    }

    @Override
    public BigInteger[] getAccel2Array() {
        return this.getObjectArray(PROPERTY_QNAME[21], SimpleValue::getBigIntegerValue, BigInteger[]::new);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public BigInteger getAccel2Array(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)((Object)this.get_store().find_element_user(PROPERTY_QNAME[21], i));
            if (target == null) {
                throw new IndexOutOfBoundsException();
            }
            return target.getBigIntegerValue();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public List<XmlInteger> xgetAccel2List() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return new JavaListXmlObject<XmlInteger>(this::xgetAccel2Array, this::xsetAccel2Array, this::insertNewAccel2, this::removeAccel2, this::sizeOfAccel2Array);
        }
    }

    @Override
    public XmlInteger[] xgetAccel2Array() {
        return (XmlInteger[])this.xgetArray(PROPERTY_QNAME[21], XmlInteger[]::new);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public XmlInteger xgetAccel2Array(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            XmlInteger target = null;
            target = (XmlInteger)((Object)this.get_store().find_element_user(PROPERTY_QNAME[21], i));
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
    public int sizeOfAccel2Array() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[21]);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void setAccel2Array(BigInteger[] accel2Array) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.arraySetterHelper(accel2Array, PROPERTY_QNAME[21]);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void setAccel2Array(int i, BigInteger accel2) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)((Object)this.get_store().find_element_user(PROPERTY_QNAME[21], i));
            if (target == null) {
                throw new IndexOutOfBoundsException();
            }
            target.setBigIntegerValue(accel2);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void xsetAccel2Array(XmlInteger[] accel2Array) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.arraySetterHelper(accel2Array, PROPERTY_QNAME[21]);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void xsetAccel2Array(int i, XmlInteger accel2) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            XmlInteger target = null;
            target = (XmlInteger)((Object)this.get_store().find_element_user(PROPERTY_QNAME[21], i));
            if (target == null) {
                throw new IndexOutOfBoundsException();
            }
            target.set(accel2);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void insertAccel2(int i, BigInteger accel2) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            SimpleValue target = (SimpleValue)((Object)this.get_store().insert_element_user(PROPERTY_QNAME[21], i));
            target.setBigIntegerValue(accel2);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void addAccel2(BigInteger accel2) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)((Object)this.get_store().add_element_user(PROPERTY_QNAME[21]));
            target.setBigIntegerValue(accel2);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public XmlInteger insertNewAccel2(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            XmlInteger target = null;
            target = (XmlInteger)((Object)this.get_store().insert_element_user(PROPERTY_QNAME[21], i));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public XmlInteger addNewAccel2() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            XmlInteger target = null;
            target = (XmlInteger)((Object)this.get_store().add_element_user(PROPERTY_QNAME[21]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void removeAccel2(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.get_store().remove_element(PROPERTY_QNAME[21], i);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public List<BigInteger> getRowList() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return new JavaListObject<BigInteger>(this::getRowArray, this::setRowArray, this::insertRow, this::removeRow, this::sizeOfRowArray);
        }
    }

    @Override
    public BigInteger[] getRowArray() {
        return this.getObjectArray(PROPERTY_QNAME[22], SimpleValue::getBigIntegerValue, BigInteger[]::new);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public BigInteger getRowArray(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)((Object)this.get_store().find_element_user(PROPERTY_QNAME[22], i));
            if (target == null) {
                throw new IndexOutOfBoundsException();
            }
            return target.getBigIntegerValue();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public List<XmlInteger> xgetRowList() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return new JavaListXmlObject<XmlInteger>(this::xgetRowArray, this::xsetRowArray, this::insertNewRow, this::removeRow, this::sizeOfRowArray);
        }
    }

    @Override
    public XmlInteger[] xgetRowArray() {
        return (XmlInteger[])this.xgetArray(PROPERTY_QNAME[22], XmlInteger[]::new);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public XmlInteger xgetRowArray(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            XmlInteger target = null;
            target = (XmlInteger)((Object)this.get_store().find_element_user(PROPERTY_QNAME[22], i));
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
    public int sizeOfRowArray() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[22]);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void setRowArray(BigInteger[] rowArray) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.arraySetterHelper(rowArray, PROPERTY_QNAME[22]);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void setRowArray(int i, BigInteger row) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)((Object)this.get_store().find_element_user(PROPERTY_QNAME[22], i));
            if (target == null) {
                throw new IndexOutOfBoundsException();
            }
            target.setBigIntegerValue(row);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void xsetRowArray(XmlInteger[] rowArray) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.arraySetterHelper(rowArray, PROPERTY_QNAME[22]);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void xsetRowArray(int i, XmlInteger row) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            XmlInteger target = null;
            target = (XmlInteger)((Object)this.get_store().find_element_user(PROPERTY_QNAME[22], i));
            if (target == null) {
                throw new IndexOutOfBoundsException();
            }
            target.set(row);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void insertRow(int i, BigInteger row) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            SimpleValue target = (SimpleValue)((Object)this.get_store().insert_element_user(PROPERTY_QNAME[22], i));
            target.setBigIntegerValue(row);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void addRow(BigInteger row) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)((Object)this.get_store().add_element_user(PROPERTY_QNAME[22]));
            target.setBigIntegerValue(row);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public XmlInteger insertNewRow(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            XmlInteger target = null;
            target = (XmlInteger)((Object)this.get_store().insert_element_user(PROPERTY_QNAME[22], i));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public XmlInteger addNewRow() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            XmlInteger target = null;
            target = (XmlInteger)((Object)this.get_store().add_element_user(PROPERTY_QNAME[22]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void removeRow(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.get_store().remove_element(PROPERTY_QNAME[22], i);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public List<BigInteger> getColumnList() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return new JavaListObject<BigInteger>(this::getColumnArray, this::setColumnArray, this::insertColumn, this::removeColumn, this::sizeOfColumnArray);
        }
    }

    @Override
    public BigInteger[] getColumnArray() {
        return this.getObjectArray(PROPERTY_QNAME[23], SimpleValue::getBigIntegerValue, BigInteger[]::new);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public BigInteger getColumnArray(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)((Object)this.get_store().find_element_user(PROPERTY_QNAME[23], i));
            if (target == null) {
                throw new IndexOutOfBoundsException();
            }
            return target.getBigIntegerValue();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public List<XmlInteger> xgetColumnList() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return new JavaListXmlObject<XmlInteger>(this::xgetColumnArray, this::xsetColumnArray, this::insertNewColumn, this::removeColumn, this::sizeOfColumnArray);
        }
    }

    @Override
    public XmlInteger[] xgetColumnArray() {
        return (XmlInteger[])this.xgetArray(PROPERTY_QNAME[23], XmlInteger[]::new);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public XmlInteger xgetColumnArray(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            XmlInteger target = null;
            target = (XmlInteger)((Object)this.get_store().find_element_user(PROPERTY_QNAME[23], i));
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
    public int sizeOfColumnArray() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[23]);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void setColumnArray(BigInteger[] columnArray) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.arraySetterHelper(columnArray, PROPERTY_QNAME[23]);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void setColumnArray(int i, BigInteger column) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)((Object)this.get_store().find_element_user(PROPERTY_QNAME[23], i));
            if (target == null) {
                throw new IndexOutOfBoundsException();
            }
            target.setBigIntegerValue(column);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void xsetColumnArray(XmlInteger[] columnArray) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.arraySetterHelper(columnArray, PROPERTY_QNAME[23]);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void xsetColumnArray(int i, XmlInteger column) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            XmlInteger target = null;
            target = (XmlInteger)((Object)this.get_store().find_element_user(PROPERTY_QNAME[23], i));
            if (target == null) {
                throw new IndexOutOfBoundsException();
            }
            target.set(column);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void insertColumn(int i, BigInteger column) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            SimpleValue target = (SimpleValue)((Object)this.get_store().insert_element_user(PROPERTY_QNAME[23], i));
            target.setBigIntegerValue(column);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void addColumn(BigInteger column) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)((Object)this.get_store().add_element_user(PROPERTY_QNAME[23]));
            target.setBigIntegerValue(column);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public XmlInteger insertNewColumn(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            XmlInteger target = null;
            target = (XmlInteger)((Object)this.get_store().insert_element_user(PROPERTY_QNAME[23], i));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public XmlInteger addNewColumn() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            XmlInteger target = null;
            target = (XmlInteger)((Object)this.get_store().add_element_user(PROPERTY_QNAME[23]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void removeColumn(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.get_store().remove_element(PROPERTY_QNAME[23], i);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public List<STTrueFalseBlank.Enum> getVisibleList() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return new JavaListObject<STTrueFalseBlank.Enum>(this::getVisibleArray, this::setVisibleArray, this::insertVisible, this::removeVisible, this::sizeOfVisibleArray);
        }
    }

    @Override
    public STTrueFalseBlank.Enum[] getVisibleArray() {
        return (STTrueFalseBlank.Enum[])this.getEnumArray(PROPERTY_QNAME[24], STTrueFalseBlank.Enum[]::new);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public STTrueFalseBlank.Enum getVisibleArray(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)((Object)this.get_store().find_element_user(PROPERTY_QNAME[24], i));
            if (target == null) {
                throw new IndexOutOfBoundsException();
            }
            return (STTrueFalseBlank.Enum)target.getEnumValue();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public List<STTrueFalseBlank> xgetVisibleList() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return new JavaListXmlObject<STTrueFalseBlank>(this::xgetVisibleArray, this::xsetVisibleArray, this::insertNewVisible, this::removeVisible, this::sizeOfVisibleArray);
        }
    }

    @Override
    public STTrueFalseBlank[] xgetVisibleArray() {
        return (STTrueFalseBlank[])this.xgetArray(PROPERTY_QNAME[24], STTrueFalseBlank[]::new);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public STTrueFalseBlank xgetVisibleArray(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            STTrueFalseBlank target = null;
            target = (STTrueFalseBlank)((Object)this.get_store().find_element_user(PROPERTY_QNAME[24], i));
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
    public int sizeOfVisibleArray() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[24]);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void setVisibleArray(STTrueFalseBlank.Enum[] visibleArray) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.arraySetterHelper(visibleArray, PROPERTY_QNAME[24]);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void setVisibleArray(int i, STTrueFalseBlank.Enum visible) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)((Object)this.get_store().find_element_user(PROPERTY_QNAME[24], i));
            if (target == null) {
                throw new IndexOutOfBoundsException();
            }
            target.setEnumValue(visible);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void xsetVisibleArray(STTrueFalseBlank[] visibleArray) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.arraySetterHelper(visibleArray, PROPERTY_QNAME[24]);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void xsetVisibleArray(int i, STTrueFalseBlank visible) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            STTrueFalseBlank target = null;
            target = (STTrueFalseBlank)((Object)this.get_store().find_element_user(PROPERTY_QNAME[24], i));
            if (target == null) {
                throw new IndexOutOfBoundsException();
            }
            target.set(visible);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void insertVisible(int i, STTrueFalseBlank.Enum visible) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            SimpleValue target = (SimpleValue)((Object)this.get_store().insert_element_user(PROPERTY_QNAME[24], i));
            target.setEnumValue(visible);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void addVisible(STTrueFalseBlank.Enum visible) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)((Object)this.get_store().add_element_user(PROPERTY_QNAME[24]));
            target.setEnumValue(visible);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public STTrueFalseBlank insertNewVisible(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            STTrueFalseBlank target = null;
            target = (STTrueFalseBlank)((Object)this.get_store().insert_element_user(PROPERTY_QNAME[24], i));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public STTrueFalseBlank addNewVisible() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            STTrueFalseBlank target = null;
            target = (STTrueFalseBlank)((Object)this.get_store().add_element_user(PROPERTY_QNAME[24]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void removeVisible(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.get_store().remove_element(PROPERTY_QNAME[24], i);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public List<STTrueFalseBlank.Enum> getRowHiddenList() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return new JavaListObject<STTrueFalseBlank.Enum>(this::getRowHiddenArray, this::setRowHiddenArray, this::insertRowHidden, this::removeRowHidden, this::sizeOfRowHiddenArray);
        }
    }

    @Override
    public STTrueFalseBlank.Enum[] getRowHiddenArray() {
        return (STTrueFalseBlank.Enum[])this.getEnumArray(PROPERTY_QNAME[25], STTrueFalseBlank.Enum[]::new);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public STTrueFalseBlank.Enum getRowHiddenArray(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)((Object)this.get_store().find_element_user(PROPERTY_QNAME[25], i));
            if (target == null) {
                throw new IndexOutOfBoundsException();
            }
            return (STTrueFalseBlank.Enum)target.getEnumValue();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public List<STTrueFalseBlank> xgetRowHiddenList() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return new JavaListXmlObject<STTrueFalseBlank>(this::xgetRowHiddenArray, this::xsetRowHiddenArray, this::insertNewRowHidden, this::removeRowHidden, this::sizeOfRowHiddenArray);
        }
    }

    @Override
    public STTrueFalseBlank[] xgetRowHiddenArray() {
        return (STTrueFalseBlank[])this.xgetArray(PROPERTY_QNAME[25], STTrueFalseBlank[]::new);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public STTrueFalseBlank xgetRowHiddenArray(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            STTrueFalseBlank target = null;
            target = (STTrueFalseBlank)((Object)this.get_store().find_element_user(PROPERTY_QNAME[25], i));
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
    public int sizeOfRowHiddenArray() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[25]);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void setRowHiddenArray(STTrueFalseBlank.Enum[] rowHiddenArray) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.arraySetterHelper(rowHiddenArray, PROPERTY_QNAME[25]);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void setRowHiddenArray(int i, STTrueFalseBlank.Enum rowHidden) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)((Object)this.get_store().find_element_user(PROPERTY_QNAME[25], i));
            if (target == null) {
                throw new IndexOutOfBoundsException();
            }
            target.setEnumValue(rowHidden);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void xsetRowHiddenArray(STTrueFalseBlank[] rowHiddenArray) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.arraySetterHelper(rowHiddenArray, PROPERTY_QNAME[25]);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void xsetRowHiddenArray(int i, STTrueFalseBlank rowHidden) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            STTrueFalseBlank target = null;
            target = (STTrueFalseBlank)((Object)this.get_store().find_element_user(PROPERTY_QNAME[25], i));
            if (target == null) {
                throw new IndexOutOfBoundsException();
            }
            target.set(rowHidden);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void insertRowHidden(int i, STTrueFalseBlank.Enum rowHidden) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            SimpleValue target = (SimpleValue)((Object)this.get_store().insert_element_user(PROPERTY_QNAME[25], i));
            target.setEnumValue(rowHidden);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void addRowHidden(STTrueFalseBlank.Enum rowHidden) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)((Object)this.get_store().add_element_user(PROPERTY_QNAME[25]));
            target.setEnumValue(rowHidden);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public STTrueFalseBlank insertNewRowHidden(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            STTrueFalseBlank target = null;
            target = (STTrueFalseBlank)((Object)this.get_store().insert_element_user(PROPERTY_QNAME[25], i));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public STTrueFalseBlank addNewRowHidden() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            STTrueFalseBlank target = null;
            target = (STTrueFalseBlank)((Object)this.get_store().add_element_user(PROPERTY_QNAME[25]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void removeRowHidden(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.get_store().remove_element(PROPERTY_QNAME[25], i);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public List<STTrueFalseBlank.Enum> getColHiddenList() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return new JavaListObject<STTrueFalseBlank.Enum>(this::getColHiddenArray, this::setColHiddenArray, this::insertColHidden, this::removeColHidden, this::sizeOfColHiddenArray);
        }
    }

    @Override
    public STTrueFalseBlank.Enum[] getColHiddenArray() {
        return (STTrueFalseBlank.Enum[])this.getEnumArray(PROPERTY_QNAME[26], STTrueFalseBlank.Enum[]::new);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public STTrueFalseBlank.Enum getColHiddenArray(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)((Object)this.get_store().find_element_user(PROPERTY_QNAME[26], i));
            if (target == null) {
                throw new IndexOutOfBoundsException();
            }
            return (STTrueFalseBlank.Enum)target.getEnumValue();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public List<STTrueFalseBlank> xgetColHiddenList() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return new JavaListXmlObject<STTrueFalseBlank>(this::xgetColHiddenArray, this::xsetColHiddenArray, this::insertNewColHidden, this::removeColHidden, this::sizeOfColHiddenArray);
        }
    }

    @Override
    public STTrueFalseBlank[] xgetColHiddenArray() {
        return (STTrueFalseBlank[])this.xgetArray(PROPERTY_QNAME[26], STTrueFalseBlank[]::new);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public STTrueFalseBlank xgetColHiddenArray(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            STTrueFalseBlank target = null;
            target = (STTrueFalseBlank)((Object)this.get_store().find_element_user(PROPERTY_QNAME[26], i));
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
    public int sizeOfColHiddenArray() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[26]);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void setColHiddenArray(STTrueFalseBlank.Enum[] colHiddenArray) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.arraySetterHelper(colHiddenArray, PROPERTY_QNAME[26]);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void setColHiddenArray(int i, STTrueFalseBlank.Enum colHidden) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)((Object)this.get_store().find_element_user(PROPERTY_QNAME[26], i));
            if (target == null) {
                throw new IndexOutOfBoundsException();
            }
            target.setEnumValue(colHidden);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void xsetColHiddenArray(STTrueFalseBlank[] colHiddenArray) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.arraySetterHelper(colHiddenArray, PROPERTY_QNAME[26]);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void xsetColHiddenArray(int i, STTrueFalseBlank colHidden) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            STTrueFalseBlank target = null;
            target = (STTrueFalseBlank)((Object)this.get_store().find_element_user(PROPERTY_QNAME[26], i));
            if (target == null) {
                throw new IndexOutOfBoundsException();
            }
            target.set(colHidden);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void insertColHidden(int i, STTrueFalseBlank.Enum colHidden) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            SimpleValue target = (SimpleValue)((Object)this.get_store().insert_element_user(PROPERTY_QNAME[26], i));
            target.setEnumValue(colHidden);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void addColHidden(STTrueFalseBlank.Enum colHidden) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)((Object)this.get_store().add_element_user(PROPERTY_QNAME[26]));
            target.setEnumValue(colHidden);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public STTrueFalseBlank insertNewColHidden(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            STTrueFalseBlank target = null;
            target = (STTrueFalseBlank)((Object)this.get_store().insert_element_user(PROPERTY_QNAME[26], i));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public STTrueFalseBlank addNewColHidden() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            STTrueFalseBlank target = null;
            target = (STTrueFalseBlank)((Object)this.get_store().add_element_user(PROPERTY_QNAME[26]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void removeColHidden(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.get_store().remove_element(PROPERTY_QNAME[26], i);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public List<BigInteger> getVTEditList() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return new JavaListObject<BigInteger>(this::getVTEditArray, this::setVTEditArray, this::insertVTEdit, this::removeVTEdit, this::sizeOfVTEditArray);
        }
    }

    @Override
    public BigInteger[] getVTEditArray() {
        return this.getObjectArray(PROPERTY_QNAME[27], SimpleValue::getBigIntegerValue, BigInteger[]::new);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public BigInteger getVTEditArray(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)((Object)this.get_store().find_element_user(PROPERTY_QNAME[27], i));
            if (target == null) {
                throw new IndexOutOfBoundsException();
            }
            return target.getBigIntegerValue();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public List<XmlInteger> xgetVTEditList() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return new JavaListXmlObject<XmlInteger>(this::xgetVTEditArray, this::xsetVTEditArray, this::insertNewVTEdit, this::removeVTEdit, this::sizeOfVTEditArray);
        }
    }

    @Override
    public XmlInteger[] xgetVTEditArray() {
        return (XmlInteger[])this.xgetArray(PROPERTY_QNAME[27], XmlInteger[]::new);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public XmlInteger xgetVTEditArray(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            XmlInteger target = null;
            target = (XmlInteger)((Object)this.get_store().find_element_user(PROPERTY_QNAME[27], i));
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
    public int sizeOfVTEditArray() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[27]);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void setVTEditArray(BigInteger[] vtEditArray) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.arraySetterHelper(vtEditArray, PROPERTY_QNAME[27]);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void setVTEditArray(int i, BigInteger vtEdit) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)((Object)this.get_store().find_element_user(PROPERTY_QNAME[27], i));
            if (target == null) {
                throw new IndexOutOfBoundsException();
            }
            target.setBigIntegerValue(vtEdit);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void xsetVTEditArray(XmlInteger[] vtEditArray) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.arraySetterHelper(vtEditArray, PROPERTY_QNAME[27]);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void xsetVTEditArray(int i, XmlInteger vtEdit) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            XmlInteger target = null;
            target = (XmlInteger)((Object)this.get_store().find_element_user(PROPERTY_QNAME[27], i));
            if (target == null) {
                throw new IndexOutOfBoundsException();
            }
            target.set(vtEdit);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void insertVTEdit(int i, BigInteger vtEdit) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            SimpleValue target = (SimpleValue)((Object)this.get_store().insert_element_user(PROPERTY_QNAME[27], i));
            target.setBigIntegerValue(vtEdit);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void addVTEdit(BigInteger vtEdit) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)((Object)this.get_store().add_element_user(PROPERTY_QNAME[27]));
            target.setBigIntegerValue(vtEdit);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public XmlInteger insertNewVTEdit(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            XmlInteger target = null;
            target = (XmlInteger)((Object)this.get_store().insert_element_user(PROPERTY_QNAME[27], i));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public XmlInteger addNewVTEdit() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            XmlInteger target = null;
            target = (XmlInteger)((Object)this.get_store().add_element_user(PROPERTY_QNAME[27]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void removeVTEdit(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.get_store().remove_element(PROPERTY_QNAME[27], i);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public List<STTrueFalseBlank.Enum> getMultiLineList() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return new JavaListObject<STTrueFalseBlank.Enum>(this::getMultiLineArray, this::setMultiLineArray, this::insertMultiLine, this::removeMultiLine, this::sizeOfMultiLineArray);
        }
    }

    @Override
    public STTrueFalseBlank.Enum[] getMultiLineArray() {
        return (STTrueFalseBlank.Enum[])this.getEnumArray(PROPERTY_QNAME[28], STTrueFalseBlank.Enum[]::new);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public STTrueFalseBlank.Enum getMultiLineArray(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)((Object)this.get_store().find_element_user(PROPERTY_QNAME[28], i));
            if (target == null) {
                throw new IndexOutOfBoundsException();
            }
            return (STTrueFalseBlank.Enum)target.getEnumValue();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public List<STTrueFalseBlank> xgetMultiLineList() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return new JavaListXmlObject<STTrueFalseBlank>(this::xgetMultiLineArray, this::xsetMultiLineArray, this::insertNewMultiLine, this::removeMultiLine, this::sizeOfMultiLineArray);
        }
    }

    @Override
    public STTrueFalseBlank[] xgetMultiLineArray() {
        return (STTrueFalseBlank[])this.xgetArray(PROPERTY_QNAME[28], STTrueFalseBlank[]::new);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public STTrueFalseBlank xgetMultiLineArray(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            STTrueFalseBlank target = null;
            target = (STTrueFalseBlank)((Object)this.get_store().find_element_user(PROPERTY_QNAME[28], i));
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
    public int sizeOfMultiLineArray() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[28]);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void setMultiLineArray(STTrueFalseBlank.Enum[] multiLineArray) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.arraySetterHelper(multiLineArray, PROPERTY_QNAME[28]);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void setMultiLineArray(int i, STTrueFalseBlank.Enum multiLine) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)((Object)this.get_store().find_element_user(PROPERTY_QNAME[28], i));
            if (target == null) {
                throw new IndexOutOfBoundsException();
            }
            target.setEnumValue(multiLine);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void xsetMultiLineArray(STTrueFalseBlank[] multiLineArray) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.arraySetterHelper(multiLineArray, PROPERTY_QNAME[28]);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void xsetMultiLineArray(int i, STTrueFalseBlank multiLine) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            STTrueFalseBlank target = null;
            target = (STTrueFalseBlank)((Object)this.get_store().find_element_user(PROPERTY_QNAME[28], i));
            if (target == null) {
                throw new IndexOutOfBoundsException();
            }
            target.set(multiLine);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void insertMultiLine(int i, STTrueFalseBlank.Enum multiLine) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            SimpleValue target = (SimpleValue)((Object)this.get_store().insert_element_user(PROPERTY_QNAME[28], i));
            target.setEnumValue(multiLine);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void addMultiLine(STTrueFalseBlank.Enum multiLine) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)((Object)this.get_store().add_element_user(PROPERTY_QNAME[28]));
            target.setEnumValue(multiLine);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public STTrueFalseBlank insertNewMultiLine(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            STTrueFalseBlank target = null;
            target = (STTrueFalseBlank)((Object)this.get_store().insert_element_user(PROPERTY_QNAME[28], i));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public STTrueFalseBlank addNewMultiLine() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            STTrueFalseBlank target = null;
            target = (STTrueFalseBlank)((Object)this.get_store().add_element_user(PROPERTY_QNAME[28]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void removeMultiLine(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.get_store().remove_element(PROPERTY_QNAME[28], i);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public List<STTrueFalseBlank.Enum> getVScrollList() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return new JavaListObject<STTrueFalseBlank.Enum>(this::getVScrollArray, this::setVScrollArray, this::insertVScroll, this::removeVScroll, this::sizeOfVScrollArray);
        }
    }

    @Override
    public STTrueFalseBlank.Enum[] getVScrollArray() {
        return (STTrueFalseBlank.Enum[])this.getEnumArray(PROPERTY_QNAME[29], STTrueFalseBlank.Enum[]::new);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public STTrueFalseBlank.Enum getVScrollArray(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)((Object)this.get_store().find_element_user(PROPERTY_QNAME[29], i));
            if (target == null) {
                throw new IndexOutOfBoundsException();
            }
            return (STTrueFalseBlank.Enum)target.getEnumValue();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public List<STTrueFalseBlank> xgetVScrollList() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return new JavaListXmlObject<STTrueFalseBlank>(this::xgetVScrollArray, this::xsetVScrollArray, this::insertNewVScroll, this::removeVScroll, this::sizeOfVScrollArray);
        }
    }

    @Override
    public STTrueFalseBlank[] xgetVScrollArray() {
        return (STTrueFalseBlank[])this.xgetArray(PROPERTY_QNAME[29], STTrueFalseBlank[]::new);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public STTrueFalseBlank xgetVScrollArray(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            STTrueFalseBlank target = null;
            target = (STTrueFalseBlank)((Object)this.get_store().find_element_user(PROPERTY_QNAME[29], i));
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
    public int sizeOfVScrollArray() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[29]);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void setVScrollArray(STTrueFalseBlank.Enum[] vScrollArray) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.arraySetterHelper(vScrollArray, PROPERTY_QNAME[29]);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void setVScrollArray(int i, STTrueFalseBlank.Enum vScroll) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)((Object)this.get_store().find_element_user(PROPERTY_QNAME[29], i));
            if (target == null) {
                throw new IndexOutOfBoundsException();
            }
            target.setEnumValue(vScroll);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void xsetVScrollArray(STTrueFalseBlank[] vScrollArray) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.arraySetterHelper(vScrollArray, PROPERTY_QNAME[29]);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void xsetVScrollArray(int i, STTrueFalseBlank vScroll) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            STTrueFalseBlank target = null;
            target = (STTrueFalseBlank)((Object)this.get_store().find_element_user(PROPERTY_QNAME[29], i));
            if (target == null) {
                throw new IndexOutOfBoundsException();
            }
            target.set(vScroll);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void insertVScroll(int i, STTrueFalseBlank.Enum vScroll) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            SimpleValue target = (SimpleValue)((Object)this.get_store().insert_element_user(PROPERTY_QNAME[29], i));
            target.setEnumValue(vScroll);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void addVScroll(STTrueFalseBlank.Enum vScroll) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)((Object)this.get_store().add_element_user(PROPERTY_QNAME[29]));
            target.setEnumValue(vScroll);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public STTrueFalseBlank insertNewVScroll(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            STTrueFalseBlank target = null;
            target = (STTrueFalseBlank)((Object)this.get_store().insert_element_user(PROPERTY_QNAME[29], i));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public STTrueFalseBlank addNewVScroll() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            STTrueFalseBlank target = null;
            target = (STTrueFalseBlank)((Object)this.get_store().add_element_user(PROPERTY_QNAME[29]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void removeVScroll(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.get_store().remove_element(PROPERTY_QNAME[29], i);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public List<STTrueFalseBlank.Enum> getValidIdsList() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return new JavaListObject<STTrueFalseBlank.Enum>(this::getValidIdsArray, this::setValidIdsArray, this::insertValidIds, this::removeValidIds, this::sizeOfValidIdsArray);
        }
    }

    @Override
    public STTrueFalseBlank.Enum[] getValidIdsArray() {
        return (STTrueFalseBlank.Enum[])this.getEnumArray(PROPERTY_QNAME[30], STTrueFalseBlank.Enum[]::new);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public STTrueFalseBlank.Enum getValidIdsArray(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)((Object)this.get_store().find_element_user(PROPERTY_QNAME[30], i));
            if (target == null) {
                throw new IndexOutOfBoundsException();
            }
            return (STTrueFalseBlank.Enum)target.getEnumValue();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public List<STTrueFalseBlank> xgetValidIdsList() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return new JavaListXmlObject<STTrueFalseBlank>(this::xgetValidIdsArray, this::xsetValidIdsArray, this::insertNewValidIds, this::removeValidIds, this::sizeOfValidIdsArray);
        }
    }

    @Override
    public STTrueFalseBlank[] xgetValidIdsArray() {
        return (STTrueFalseBlank[])this.xgetArray(PROPERTY_QNAME[30], STTrueFalseBlank[]::new);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public STTrueFalseBlank xgetValidIdsArray(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            STTrueFalseBlank target = null;
            target = (STTrueFalseBlank)((Object)this.get_store().find_element_user(PROPERTY_QNAME[30], i));
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
    public int sizeOfValidIdsArray() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[30]);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void setValidIdsArray(STTrueFalseBlank.Enum[] validIdsArray) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.arraySetterHelper(validIdsArray, PROPERTY_QNAME[30]);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void setValidIdsArray(int i, STTrueFalseBlank.Enum validIds) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)((Object)this.get_store().find_element_user(PROPERTY_QNAME[30], i));
            if (target == null) {
                throw new IndexOutOfBoundsException();
            }
            target.setEnumValue(validIds);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void xsetValidIdsArray(STTrueFalseBlank[] validIdsArray) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.arraySetterHelper(validIdsArray, PROPERTY_QNAME[30]);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void xsetValidIdsArray(int i, STTrueFalseBlank validIds) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            STTrueFalseBlank target = null;
            target = (STTrueFalseBlank)((Object)this.get_store().find_element_user(PROPERTY_QNAME[30], i));
            if (target == null) {
                throw new IndexOutOfBoundsException();
            }
            target.set(validIds);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void insertValidIds(int i, STTrueFalseBlank.Enum validIds) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            SimpleValue target = (SimpleValue)((Object)this.get_store().insert_element_user(PROPERTY_QNAME[30], i));
            target.setEnumValue(validIds);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void addValidIds(STTrueFalseBlank.Enum validIds) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)((Object)this.get_store().add_element_user(PROPERTY_QNAME[30]));
            target.setEnumValue(validIds);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public STTrueFalseBlank insertNewValidIds(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            STTrueFalseBlank target = null;
            target = (STTrueFalseBlank)((Object)this.get_store().insert_element_user(PROPERTY_QNAME[30], i));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public STTrueFalseBlank addNewValidIds() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            STTrueFalseBlank target = null;
            target = (STTrueFalseBlank)((Object)this.get_store().add_element_user(PROPERTY_QNAME[30]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void removeValidIds(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.get_store().remove_element(PROPERTY_QNAME[30], i);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public List<String> getFmlaRangeList() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return new JavaListObject<String>(this::getFmlaRangeArray, this::setFmlaRangeArray, this::insertFmlaRange, this::removeFmlaRange, this::sizeOfFmlaRangeArray);
        }
    }

    @Override
    public String[] getFmlaRangeArray() {
        return this.getObjectArray(PROPERTY_QNAME[31], SimpleValue::getStringValue, String[]::new);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public String getFmlaRangeArray(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)((Object)this.get_store().find_element_user(PROPERTY_QNAME[31], i));
            if (target == null) {
                throw new IndexOutOfBoundsException();
            }
            return target.getStringValue();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public List<XmlString> xgetFmlaRangeList() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return new JavaListXmlObject<XmlString>(this::xgetFmlaRangeArray, this::xsetFmlaRangeArray, this::insertNewFmlaRange, this::removeFmlaRange, this::sizeOfFmlaRangeArray);
        }
    }

    @Override
    public XmlString[] xgetFmlaRangeArray() {
        return (XmlString[])this.xgetArray(PROPERTY_QNAME[31], XmlString[]::new);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public XmlString xgetFmlaRangeArray(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            XmlString target = null;
            target = (XmlString)((Object)this.get_store().find_element_user(PROPERTY_QNAME[31], i));
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
    public int sizeOfFmlaRangeArray() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[31]);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void setFmlaRangeArray(String[] fmlaRangeArray) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.arraySetterHelper(fmlaRangeArray, PROPERTY_QNAME[31]);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void setFmlaRangeArray(int i, String fmlaRange) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)((Object)this.get_store().find_element_user(PROPERTY_QNAME[31], i));
            if (target == null) {
                throw new IndexOutOfBoundsException();
            }
            target.setStringValue(fmlaRange);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void xsetFmlaRangeArray(XmlString[] fmlaRangeArray) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.arraySetterHelper(fmlaRangeArray, PROPERTY_QNAME[31]);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void xsetFmlaRangeArray(int i, XmlString fmlaRange) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            XmlString target = null;
            target = (XmlString)((Object)this.get_store().find_element_user(PROPERTY_QNAME[31], i));
            if (target == null) {
                throw new IndexOutOfBoundsException();
            }
            target.set(fmlaRange);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void insertFmlaRange(int i, String fmlaRange) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            SimpleValue target = (SimpleValue)((Object)this.get_store().insert_element_user(PROPERTY_QNAME[31], i));
            target.setStringValue(fmlaRange);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void addFmlaRange(String fmlaRange) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)((Object)this.get_store().add_element_user(PROPERTY_QNAME[31]));
            target.setStringValue(fmlaRange);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public XmlString insertNewFmlaRange(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            XmlString target = null;
            target = (XmlString)((Object)this.get_store().insert_element_user(PROPERTY_QNAME[31], i));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public XmlString addNewFmlaRange() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            XmlString target = null;
            target = (XmlString)((Object)this.get_store().add_element_user(PROPERTY_QNAME[31]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void removeFmlaRange(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.get_store().remove_element(PROPERTY_QNAME[31], i);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public List<BigInteger> getWidthMinList() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return new JavaListObject<BigInteger>(this::getWidthMinArray, this::setWidthMinArray, this::insertWidthMin, this::removeWidthMin, this::sizeOfWidthMinArray);
        }
    }

    @Override
    public BigInteger[] getWidthMinArray() {
        return this.getObjectArray(PROPERTY_QNAME[32], SimpleValue::getBigIntegerValue, BigInteger[]::new);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public BigInteger getWidthMinArray(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)((Object)this.get_store().find_element_user(PROPERTY_QNAME[32], i));
            if (target == null) {
                throw new IndexOutOfBoundsException();
            }
            return target.getBigIntegerValue();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public List<XmlInteger> xgetWidthMinList() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return new JavaListXmlObject<XmlInteger>(this::xgetWidthMinArray, this::xsetWidthMinArray, this::insertNewWidthMin, this::removeWidthMin, this::sizeOfWidthMinArray);
        }
    }

    @Override
    public XmlInteger[] xgetWidthMinArray() {
        return (XmlInteger[])this.xgetArray(PROPERTY_QNAME[32], XmlInteger[]::new);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public XmlInteger xgetWidthMinArray(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            XmlInteger target = null;
            target = (XmlInteger)((Object)this.get_store().find_element_user(PROPERTY_QNAME[32], i));
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
    public int sizeOfWidthMinArray() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[32]);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void setWidthMinArray(BigInteger[] widthMinArray) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.arraySetterHelper(widthMinArray, PROPERTY_QNAME[32]);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void setWidthMinArray(int i, BigInteger widthMin) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)((Object)this.get_store().find_element_user(PROPERTY_QNAME[32], i));
            if (target == null) {
                throw new IndexOutOfBoundsException();
            }
            target.setBigIntegerValue(widthMin);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void xsetWidthMinArray(XmlInteger[] widthMinArray) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.arraySetterHelper(widthMinArray, PROPERTY_QNAME[32]);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void xsetWidthMinArray(int i, XmlInteger widthMin) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            XmlInteger target = null;
            target = (XmlInteger)((Object)this.get_store().find_element_user(PROPERTY_QNAME[32], i));
            if (target == null) {
                throw new IndexOutOfBoundsException();
            }
            target.set(widthMin);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void insertWidthMin(int i, BigInteger widthMin) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            SimpleValue target = (SimpleValue)((Object)this.get_store().insert_element_user(PROPERTY_QNAME[32], i));
            target.setBigIntegerValue(widthMin);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void addWidthMin(BigInteger widthMin) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)((Object)this.get_store().add_element_user(PROPERTY_QNAME[32]));
            target.setBigIntegerValue(widthMin);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public XmlInteger insertNewWidthMin(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            XmlInteger target = null;
            target = (XmlInteger)((Object)this.get_store().insert_element_user(PROPERTY_QNAME[32], i));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public XmlInteger addNewWidthMin() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            XmlInteger target = null;
            target = (XmlInteger)((Object)this.get_store().add_element_user(PROPERTY_QNAME[32]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void removeWidthMin(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.get_store().remove_element(PROPERTY_QNAME[32], i);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public List<BigInteger> getSelList() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return new JavaListObject<BigInteger>(this::getSelArray, this::setSelArray, this::insertSel, this::removeSel, this::sizeOfSelArray);
        }
    }

    @Override
    public BigInteger[] getSelArray() {
        return this.getObjectArray(PROPERTY_QNAME[33], SimpleValue::getBigIntegerValue, BigInteger[]::new);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public BigInteger getSelArray(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)((Object)this.get_store().find_element_user(PROPERTY_QNAME[33], i));
            if (target == null) {
                throw new IndexOutOfBoundsException();
            }
            return target.getBigIntegerValue();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public List<XmlInteger> xgetSelList() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return new JavaListXmlObject<XmlInteger>(this::xgetSelArray, this::xsetSelArray, this::insertNewSel, this::removeSel, this::sizeOfSelArray);
        }
    }

    @Override
    public XmlInteger[] xgetSelArray() {
        return (XmlInteger[])this.xgetArray(PROPERTY_QNAME[33], XmlInteger[]::new);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public XmlInteger xgetSelArray(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            XmlInteger target = null;
            target = (XmlInteger)((Object)this.get_store().find_element_user(PROPERTY_QNAME[33], i));
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
    public int sizeOfSelArray() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[33]);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void setSelArray(BigInteger[] selArray) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.arraySetterHelper(selArray, PROPERTY_QNAME[33]);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void setSelArray(int i, BigInteger sel) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)((Object)this.get_store().find_element_user(PROPERTY_QNAME[33], i));
            if (target == null) {
                throw new IndexOutOfBoundsException();
            }
            target.setBigIntegerValue(sel);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void xsetSelArray(XmlInteger[] selArray) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.arraySetterHelper(selArray, PROPERTY_QNAME[33]);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void xsetSelArray(int i, XmlInteger sel) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            XmlInteger target = null;
            target = (XmlInteger)((Object)this.get_store().find_element_user(PROPERTY_QNAME[33], i));
            if (target == null) {
                throw new IndexOutOfBoundsException();
            }
            target.set(sel);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void insertSel(int i, BigInteger sel) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            SimpleValue target = (SimpleValue)((Object)this.get_store().insert_element_user(PROPERTY_QNAME[33], i));
            target.setBigIntegerValue(sel);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void addSel(BigInteger sel) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)((Object)this.get_store().add_element_user(PROPERTY_QNAME[33]));
            target.setBigIntegerValue(sel);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public XmlInteger insertNewSel(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            XmlInteger target = null;
            target = (XmlInteger)((Object)this.get_store().insert_element_user(PROPERTY_QNAME[33], i));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public XmlInteger addNewSel() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            XmlInteger target = null;
            target = (XmlInteger)((Object)this.get_store().add_element_user(PROPERTY_QNAME[33]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void removeSel(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.get_store().remove_element(PROPERTY_QNAME[33], i);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public List<STTrueFalseBlank.Enum> getNoThreeD2List() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return new JavaListObject<STTrueFalseBlank.Enum>(this::getNoThreeD2Array, this::setNoThreeD2Array, this::insertNoThreeD2, this::removeNoThreeD2, this::sizeOfNoThreeD2Array);
        }
    }

    @Override
    public STTrueFalseBlank.Enum[] getNoThreeD2Array() {
        return (STTrueFalseBlank.Enum[])this.getEnumArray(PROPERTY_QNAME[34], STTrueFalseBlank.Enum[]::new);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public STTrueFalseBlank.Enum getNoThreeD2Array(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)((Object)this.get_store().find_element_user(PROPERTY_QNAME[34], i));
            if (target == null) {
                throw new IndexOutOfBoundsException();
            }
            return (STTrueFalseBlank.Enum)target.getEnumValue();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public List<STTrueFalseBlank> xgetNoThreeD2List() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return new JavaListXmlObject<STTrueFalseBlank>(this::xgetNoThreeD2Array, this::xsetNoThreeD2Array, this::insertNewNoThreeD2, this::removeNoThreeD2, this::sizeOfNoThreeD2Array);
        }
    }

    @Override
    public STTrueFalseBlank[] xgetNoThreeD2Array() {
        return (STTrueFalseBlank[])this.xgetArray(PROPERTY_QNAME[34], STTrueFalseBlank[]::new);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public STTrueFalseBlank xgetNoThreeD2Array(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            STTrueFalseBlank target = null;
            target = (STTrueFalseBlank)((Object)this.get_store().find_element_user(PROPERTY_QNAME[34], i));
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
    public int sizeOfNoThreeD2Array() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[34]);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void setNoThreeD2Array(STTrueFalseBlank.Enum[] noThreeD2Array) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.arraySetterHelper(noThreeD2Array, PROPERTY_QNAME[34]);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void setNoThreeD2Array(int i, STTrueFalseBlank.Enum noThreeD2) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)((Object)this.get_store().find_element_user(PROPERTY_QNAME[34], i));
            if (target == null) {
                throw new IndexOutOfBoundsException();
            }
            target.setEnumValue(noThreeD2);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void xsetNoThreeD2Array(STTrueFalseBlank[] noThreeD2Array) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.arraySetterHelper(noThreeD2Array, PROPERTY_QNAME[34]);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void xsetNoThreeD2Array(int i, STTrueFalseBlank noThreeD2) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            STTrueFalseBlank target = null;
            target = (STTrueFalseBlank)((Object)this.get_store().find_element_user(PROPERTY_QNAME[34], i));
            if (target == null) {
                throw new IndexOutOfBoundsException();
            }
            target.set(noThreeD2);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void insertNoThreeD2(int i, STTrueFalseBlank.Enum noThreeD2) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            SimpleValue target = (SimpleValue)((Object)this.get_store().insert_element_user(PROPERTY_QNAME[34], i));
            target.setEnumValue(noThreeD2);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void addNoThreeD2(STTrueFalseBlank.Enum noThreeD2) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)((Object)this.get_store().add_element_user(PROPERTY_QNAME[34]));
            target.setEnumValue(noThreeD2);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public STTrueFalseBlank insertNewNoThreeD2(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            STTrueFalseBlank target = null;
            target = (STTrueFalseBlank)((Object)this.get_store().insert_element_user(PROPERTY_QNAME[34], i));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public STTrueFalseBlank addNewNoThreeD2() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            STTrueFalseBlank target = null;
            target = (STTrueFalseBlank)((Object)this.get_store().add_element_user(PROPERTY_QNAME[34]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void removeNoThreeD2(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.get_store().remove_element(PROPERTY_QNAME[34], i);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public List<String> getSelTypeList() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return new JavaListObject<String>(this::getSelTypeArray, this::setSelTypeArray, this::insertSelType, this::removeSelType, this::sizeOfSelTypeArray);
        }
    }

    @Override
    public String[] getSelTypeArray() {
        return this.getObjectArray(PROPERTY_QNAME[35], SimpleValue::getStringValue, String[]::new);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public String getSelTypeArray(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)((Object)this.get_store().find_element_user(PROPERTY_QNAME[35], i));
            if (target == null) {
                throw new IndexOutOfBoundsException();
            }
            return target.getStringValue();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public List<XmlString> xgetSelTypeList() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return new JavaListXmlObject<XmlString>(this::xgetSelTypeArray, this::xsetSelTypeArray, this::insertNewSelType, this::removeSelType, this::sizeOfSelTypeArray);
        }
    }

    @Override
    public XmlString[] xgetSelTypeArray() {
        return (XmlString[])this.xgetArray(PROPERTY_QNAME[35], XmlString[]::new);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public XmlString xgetSelTypeArray(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            XmlString target = null;
            target = (XmlString)((Object)this.get_store().find_element_user(PROPERTY_QNAME[35], i));
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
    public int sizeOfSelTypeArray() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[35]);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void setSelTypeArray(String[] selTypeArray) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.arraySetterHelper(selTypeArray, PROPERTY_QNAME[35]);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void setSelTypeArray(int i, String selType) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)((Object)this.get_store().find_element_user(PROPERTY_QNAME[35], i));
            if (target == null) {
                throw new IndexOutOfBoundsException();
            }
            target.setStringValue(selType);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void xsetSelTypeArray(XmlString[] selTypeArray) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.arraySetterHelper(selTypeArray, PROPERTY_QNAME[35]);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void xsetSelTypeArray(int i, XmlString selType) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            XmlString target = null;
            target = (XmlString)((Object)this.get_store().find_element_user(PROPERTY_QNAME[35], i));
            if (target == null) {
                throw new IndexOutOfBoundsException();
            }
            target.set(selType);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void insertSelType(int i, String selType) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            SimpleValue target = (SimpleValue)((Object)this.get_store().insert_element_user(PROPERTY_QNAME[35], i));
            target.setStringValue(selType);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void addSelType(String selType) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)((Object)this.get_store().add_element_user(PROPERTY_QNAME[35]));
            target.setStringValue(selType);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public XmlString insertNewSelType(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            XmlString target = null;
            target = (XmlString)((Object)this.get_store().insert_element_user(PROPERTY_QNAME[35], i));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public XmlString addNewSelType() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            XmlString target = null;
            target = (XmlString)((Object)this.get_store().add_element_user(PROPERTY_QNAME[35]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void removeSelType(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.get_store().remove_element(PROPERTY_QNAME[35], i);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public List<String> getMultiSelList() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return new JavaListObject<String>(this::getMultiSelArray, this::setMultiSelArray, this::insertMultiSel, this::removeMultiSel, this::sizeOfMultiSelArray);
        }
    }

    @Override
    public String[] getMultiSelArray() {
        return this.getObjectArray(PROPERTY_QNAME[36], SimpleValue::getStringValue, String[]::new);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public String getMultiSelArray(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)((Object)this.get_store().find_element_user(PROPERTY_QNAME[36], i));
            if (target == null) {
                throw new IndexOutOfBoundsException();
            }
            return target.getStringValue();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public List<XmlString> xgetMultiSelList() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return new JavaListXmlObject<XmlString>(this::xgetMultiSelArray, this::xsetMultiSelArray, this::insertNewMultiSel, this::removeMultiSel, this::sizeOfMultiSelArray);
        }
    }

    @Override
    public XmlString[] xgetMultiSelArray() {
        return (XmlString[])this.xgetArray(PROPERTY_QNAME[36], XmlString[]::new);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public XmlString xgetMultiSelArray(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            XmlString target = null;
            target = (XmlString)((Object)this.get_store().find_element_user(PROPERTY_QNAME[36], i));
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
    public int sizeOfMultiSelArray() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[36]);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void setMultiSelArray(String[] multiSelArray) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.arraySetterHelper(multiSelArray, PROPERTY_QNAME[36]);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void setMultiSelArray(int i, String multiSel) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)((Object)this.get_store().find_element_user(PROPERTY_QNAME[36], i));
            if (target == null) {
                throw new IndexOutOfBoundsException();
            }
            target.setStringValue(multiSel);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void xsetMultiSelArray(XmlString[] multiSelArray) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.arraySetterHelper(multiSelArray, PROPERTY_QNAME[36]);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void xsetMultiSelArray(int i, XmlString multiSel) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            XmlString target = null;
            target = (XmlString)((Object)this.get_store().find_element_user(PROPERTY_QNAME[36], i));
            if (target == null) {
                throw new IndexOutOfBoundsException();
            }
            target.set(multiSel);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void insertMultiSel(int i, String multiSel) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            SimpleValue target = (SimpleValue)((Object)this.get_store().insert_element_user(PROPERTY_QNAME[36], i));
            target.setStringValue(multiSel);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void addMultiSel(String multiSel) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)((Object)this.get_store().add_element_user(PROPERTY_QNAME[36]));
            target.setStringValue(multiSel);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public XmlString insertNewMultiSel(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            XmlString target = null;
            target = (XmlString)((Object)this.get_store().insert_element_user(PROPERTY_QNAME[36], i));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public XmlString addNewMultiSel() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            XmlString target = null;
            target = (XmlString)((Object)this.get_store().add_element_user(PROPERTY_QNAME[36]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void removeMultiSel(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.get_store().remove_element(PROPERTY_QNAME[36], i);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public List<String> getLCTList() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return new JavaListObject<String>(this::getLCTArray, this::setLCTArray, this::insertLCT, this::removeLCT, this::sizeOfLCTArray);
        }
    }

    @Override
    public String[] getLCTArray() {
        return this.getObjectArray(PROPERTY_QNAME[37], SimpleValue::getStringValue, String[]::new);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public String getLCTArray(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)((Object)this.get_store().find_element_user(PROPERTY_QNAME[37], i));
            if (target == null) {
                throw new IndexOutOfBoundsException();
            }
            return target.getStringValue();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public List<XmlString> xgetLCTList() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return new JavaListXmlObject<XmlString>(this::xgetLCTArray, this::xsetLCTArray, this::insertNewLCT, this::removeLCT, this::sizeOfLCTArray);
        }
    }

    @Override
    public XmlString[] xgetLCTArray() {
        return (XmlString[])this.xgetArray(PROPERTY_QNAME[37], XmlString[]::new);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public XmlString xgetLCTArray(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            XmlString target = null;
            target = (XmlString)((Object)this.get_store().find_element_user(PROPERTY_QNAME[37], i));
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
    public int sizeOfLCTArray() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[37]);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void setLCTArray(String[] lctArray) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.arraySetterHelper(lctArray, PROPERTY_QNAME[37]);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void setLCTArray(int i, String lct) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)((Object)this.get_store().find_element_user(PROPERTY_QNAME[37], i));
            if (target == null) {
                throw new IndexOutOfBoundsException();
            }
            target.setStringValue(lct);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void xsetLCTArray(XmlString[] lctArray) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.arraySetterHelper(lctArray, PROPERTY_QNAME[37]);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void xsetLCTArray(int i, XmlString lct) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            XmlString target = null;
            target = (XmlString)((Object)this.get_store().find_element_user(PROPERTY_QNAME[37], i));
            if (target == null) {
                throw new IndexOutOfBoundsException();
            }
            target.set(lct);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void insertLCT(int i, String lct) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            SimpleValue target = (SimpleValue)((Object)this.get_store().insert_element_user(PROPERTY_QNAME[37], i));
            target.setStringValue(lct);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void addLCT(String lct) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)((Object)this.get_store().add_element_user(PROPERTY_QNAME[37]));
            target.setStringValue(lct);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public XmlString insertNewLCT(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            XmlString target = null;
            target = (XmlString)((Object)this.get_store().insert_element_user(PROPERTY_QNAME[37], i));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public XmlString addNewLCT() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            XmlString target = null;
            target = (XmlString)((Object)this.get_store().add_element_user(PROPERTY_QNAME[37]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void removeLCT(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.get_store().remove_element(PROPERTY_QNAME[37], i);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public List<String> getListItemList() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return new JavaListObject<String>(this::getListItemArray, this::setListItemArray, this::insertListItem, this::removeListItem, this::sizeOfListItemArray);
        }
    }

    @Override
    public String[] getListItemArray() {
        return this.getObjectArray(PROPERTY_QNAME[38], SimpleValue::getStringValue, String[]::new);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public String getListItemArray(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)((Object)this.get_store().find_element_user(PROPERTY_QNAME[38], i));
            if (target == null) {
                throw new IndexOutOfBoundsException();
            }
            return target.getStringValue();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public List<XmlString> xgetListItemList() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return new JavaListXmlObject<XmlString>(this::xgetListItemArray, this::xsetListItemArray, this::insertNewListItem, this::removeListItem, this::sizeOfListItemArray);
        }
    }

    @Override
    public XmlString[] xgetListItemArray() {
        return (XmlString[])this.xgetArray(PROPERTY_QNAME[38], XmlString[]::new);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public XmlString xgetListItemArray(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            XmlString target = null;
            target = (XmlString)((Object)this.get_store().find_element_user(PROPERTY_QNAME[38], i));
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
    public int sizeOfListItemArray() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[38]);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void setListItemArray(String[] listItemArray) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.arraySetterHelper(listItemArray, PROPERTY_QNAME[38]);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void setListItemArray(int i, String listItem) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)((Object)this.get_store().find_element_user(PROPERTY_QNAME[38], i));
            if (target == null) {
                throw new IndexOutOfBoundsException();
            }
            target.setStringValue(listItem);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void xsetListItemArray(XmlString[] listItemArray) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.arraySetterHelper(listItemArray, PROPERTY_QNAME[38]);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void xsetListItemArray(int i, XmlString listItem) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            XmlString target = null;
            target = (XmlString)((Object)this.get_store().find_element_user(PROPERTY_QNAME[38], i));
            if (target == null) {
                throw new IndexOutOfBoundsException();
            }
            target.set(listItem);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void insertListItem(int i, String listItem) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            SimpleValue target = (SimpleValue)((Object)this.get_store().insert_element_user(PROPERTY_QNAME[38], i));
            target.setStringValue(listItem);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void addListItem(String listItem) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)((Object)this.get_store().add_element_user(PROPERTY_QNAME[38]));
            target.setStringValue(listItem);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public XmlString insertNewListItem(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            XmlString target = null;
            target = (XmlString)((Object)this.get_store().insert_element_user(PROPERTY_QNAME[38], i));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public XmlString addNewListItem() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            XmlString target = null;
            target = (XmlString)((Object)this.get_store().add_element_user(PROPERTY_QNAME[38]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void removeListItem(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.get_store().remove_element(PROPERTY_QNAME[38], i);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public List<String> getDropStyleList() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return new JavaListObject<String>(this::getDropStyleArray, this::setDropStyleArray, this::insertDropStyle, this::removeDropStyle, this::sizeOfDropStyleArray);
        }
    }

    @Override
    public String[] getDropStyleArray() {
        return this.getObjectArray(PROPERTY_QNAME[39], SimpleValue::getStringValue, String[]::new);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public String getDropStyleArray(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)((Object)this.get_store().find_element_user(PROPERTY_QNAME[39], i));
            if (target == null) {
                throw new IndexOutOfBoundsException();
            }
            return target.getStringValue();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public List<XmlString> xgetDropStyleList() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return new JavaListXmlObject<XmlString>(this::xgetDropStyleArray, this::xsetDropStyleArray, this::insertNewDropStyle, this::removeDropStyle, this::sizeOfDropStyleArray);
        }
    }

    @Override
    public XmlString[] xgetDropStyleArray() {
        return (XmlString[])this.xgetArray(PROPERTY_QNAME[39], XmlString[]::new);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public XmlString xgetDropStyleArray(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            XmlString target = null;
            target = (XmlString)((Object)this.get_store().find_element_user(PROPERTY_QNAME[39], i));
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
    public int sizeOfDropStyleArray() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[39]);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void setDropStyleArray(String[] dropStyleArray) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.arraySetterHelper(dropStyleArray, PROPERTY_QNAME[39]);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void setDropStyleArray(int i, String dropStyle) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)((Object)this.get_store().find_element_user(PROPERTY_QNAME[39], i));
            if (target == null) {
                throw new IndexOutOfBoundsException();
            }
            target.setStringValue(dropStyle);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void xsetDropStyleArray(XmlString[] dropStyleArray) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.arraySetterHelper(dropStyleArray, PROPERTY_QNAME[39]);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void xsetDropStyleArray(int i, XmlString dropStyle) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            XmlString target = null;
            target = (XmlString)((Object)this.get_store().find_element_user(PROPERTY_QNAME[39], i));
            if (target == null) {
                throw new IndexOutOfBoundsException();
            }
            target.set(dropStyle);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void insertDropStyle(int i, String dropStyle) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            SimpleValue target = (SimpleValue)((Object)this.get_store().insert_element_user(PROPERTY_QNAME[39], i));
            target.setStringValue(dropStyle);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void addDropStyle(String dropStyle) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)((Object)this.get_store().add_element_user(PROPERTY_QNAME[39]));
            target.setStringValue(dropStyle);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public XmlString insertNewDropStyle(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            XmlString target = null;
            target = (XmlString)((Object)this.get_store().insert_element_user(PROPERTY_QNAME[39], i));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public XmlString addNewDropStyle() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            XmlString target = null;
            target = (XmlString)((Object)this.get_store().add_element_user(PROPERTY_QNAME[39]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void removeDropStyle(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.get_store().remove_element(PROPERTY_QNAME[39], i);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public List<STTrueFalseBlank.Enum> getColoredList() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return new JavaListObject<STTrueFalseBlank.Enum>(this::getColoredArray, this::setColoredArray, this::insertColored, this::removeColored, this::sizeOfColoredArray);
        }
    }

    @Override
    public STTrueFalseBlank.Enum[] getColoredArray() {
        return (STTrueFalseBlank.Enum[])this.getEnumArray(PROPERTY_QNAME[40], STTrueFalseBlank.Enum[]::new);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public STTrueFalseBlank.Enum getColoredArray(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)((Object)this.get_store().find_element_user(PROPERTY_QNAME[40], i));
            if (target == null) {
                throw new IndexOutOfBoundsException();
            }
            return (STTrueFalseBlank.Enum)target.getEnumValue();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public List<STTrueFalseBlank> xgetColoredList() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return new JavaListXmlObject<STTrueFalseBlank>(this::xgetColoredArray, this::xsetColoredArray, this::insertNewColored, this::removeColored, this::sizeOfColoredArray);
        }
    }

    @Override
    public STTrueFalseBlank[] xgetColoredArray() {
        return (STTrueFalseBlank[])this.xgetArray(PROPERTY_QNAME[40], STTrueFalseBlank[]::new);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public STTrueFalseBlank xgetColoredArray(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            STTrueFalseBlank target = null;
            target = (STTrueFalseBlank)((Object)this.get_store().find_element_user(PROPERTY_QNAME[40], i));
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
    public int sizeOfColoredArray() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[40]);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void setColoredArray(STTrueFalseBlank.Enum[] coloredArray) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.arraySetterHelper(coloredArray, PROPERTY_QNAME[40]);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void setColoredArray(int i, STTrueFalseBlank.Enum colored) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)((Object)this.get_store().find_element_user(PROPERTY_QNAME[40], i));
            if (target == null) {
                throw new IndexOutOfBoundsException();
            }
            target.setEnumValue(colored);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void xsetColoredArray(STTrueFalseBlank[] coloredArray) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.arraySetterHelper(coloredArray, PROPERTY_QNAME[40]);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void xsetColoredArray(int i, STTrueFalseBlank colored) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            STTrueFalseBlank target = null;
            target = (STTrueFalseBlank)((Object)this.get_store().find_element_user(PROPERTY_QNAME[40], i));
            if (target == null) {
                throw new IndexOutOfBoundsException();
            }
            target.set(colored);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void insertColored(int i, STTrueFalseBlank.Enum colored) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            SimpleValue target = (SimpleValue)((Object)this.get_store().insert_element_user(PROPERTY_QNAME[40], i));
            target.setEnumValue(colored);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void addColored(STTrueFalseBlank.Enum colored) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)((Object)this.get_store().add_element_user(PROPERTY_QNAME[40]));
            target.setEnumValue(colored);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public STTrueFalseBlank insertNewColored(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            STTrueFalseBlank target = null;
            target = (STTrueFalseBlank)((Object)this.get_store().insert_element_user(PROPERTY_QNAME[40], i));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public STTrueFalseBlank addNewColored() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            STTrueFalseBlank target = null;
            target = (STTrueFalseBlank)((Object)this.get_store().add_element_user(PROPERTY_QNAME[40]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void removeColored(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.get_store().remove_element(PROPERTY_QNAME[40], i);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public List<BigInteger> getDropLinesList() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return new JavaListObject<BigInteger>(this::getDropLinesArray, this::setDropLinesArray, this::insertDropLines, this::removeDropLines, this::sizeOfDropLinesArray);
        }
    }

    @Override
    public BigInteger[] getDropLinesArray() {
        return this.getObjectArray(PROPERTY_QNAME[41], SimpleValue::getBigIntegerValue, BigInteger[]::new);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public BigInteger getDropLinesArray(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)((Object)this.get_store().find_element_user(PROPERTY_QNAME[41], i));
            if (target == null) {
                throw new IndexOutOfBoundsException();
            }
            return target.getBigIntegerValue();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public List<XmlInteger> xgetDropLinesList() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return new JavaListXmlObject<XmlInteger>(this::xgetDropLinesArray, this::xsetDropLinesArray, this::insertNewDropLines, this::removeDropLines, this::sizeOfDropLinesArray);
        }
    }

    @Override
    public XmlInteger[] xgetDropLinesArray() {
        return (XmlInteger[])this.xgetArray(PROPERTY_QNAME[41], XmlInteger[]::new);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public XmlInteger xgetDropLinesArray(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            XmlInteger target = null;
            target = (XmlInteger)((Object)this.get_store().find_element_user(PROPERTY_QNAME[41], i));
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
    public int sizeOfDropLinesArray() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[41]);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void setDropLinesArray(BigInteger[] dropLinesArray) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.arraySetterHelper(dropLinesArray, PROPERTY_QNAME[41]);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void setDropLinesArray(int i, BigInteger dropLines) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)((Object)this.get_store().find_element_user(PROPERTY_QNAME[41], i));
            if (target == null) {
                throw new IndexOutOfBoundsException();
            }
            target.setBigIntegerValue(dropLines);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void xsetDropLinesArray(XmlInteger[] dropLinesArray) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.arraySetterHelper(dropLinesArray, PROPERTY_QNAME[41]);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void xsetDropLinesArray(int i, XmlInteger dropLines) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            XmlInteger target = null;
            target = (XmlInteger)((Object)this.get_store().find_element_user(PROPERTY_QNAME[41], i));
            if (target == null) {
                throw new IndexOutOfBoundsException();
            }
            target.set(dropLines);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void insertDropLines(int i, BigInteger dropLines) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            SimpleValue target = (SimpleValue)((Object)this.get_store().insert_element_user(PROPERTY_QNAME[41], i));
            target.setBigIntegerValue(dropLines);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void addDropLines(BigInteger dropLines) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)((Object)this.get_store().add_element_user(PROPERTY_QNAME[41]));
            target.setBigIntegerValue(dropLines);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public XmlInteger insertNewDropLines(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            XmlInteger target = null;
            target = (XmlInteger)((Object)this.get_store().insert_element_user(PROPERTY_QNAME[41], i));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public XmlInteger addNewDropLines() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            XmlInteger target = null;
            target = (XmlInteger)((Object)this.get_store().add_element_user(PROPERTY_QNAME[41]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void removeDropLines(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.get_store().remove_element(PROPERTY_QNAME[41], i);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public List<BigInteger> getCheckedList() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return new JavaListObject<BigInteger>(this::getCheckedArray, this::setCheckedArray, this::insertChecked, this::removeChecked, this::sizeOfCheckedArray);
        }
    }

    @Override
    public BigInteger[] getCheckedArray() {
        return this.getObjectArray(PROPERTY_QNAME[42], SimpleValue::getBigIntegerValue, BigInteger[]::new);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public BigInteger getCheckedArray(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)((Object)this.get_store().find_element_user(PROPERTY_QNAME[42], i));
            if (target == null) {
                throw new IndexOutOfBoundsException();
            }
            return target.getBigIntegerValue();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public List<XmlInteger> xgetCheckedList() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return new JavaListXmlObject<XmlInteger>(this::xgetCheckedArray, this::xsetCheckedArray, this::insertNewChecked, this::removeChecked, this::sizeOfCheckedArray);
        }
    }

    @Override
    public XmlInteger[] xgetCheckedArray() {
        return (XmlInteger[])this.xgetArray(PROPERTY_QNAME[42], XmlInteger[]::new);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public XmlInteger xgetCheckedArray(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            XmlInteger target = null;
            target = (XmlInteger)((Object)this.get_store().find_element_user(PROPERTY_QNAME[42], i));
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
    public int sizeOfCheckedArray() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[42]);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void setCheckedArray(BigInteger[] checkedArray) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.arraySetterHelper(checkedArray, PROPERTY_QNAME[42]);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void setCheckedArray(int i, BigInteger checked) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)((Object)this.get_store().find_element_user(PROPERTY_QNAME[42], i));
            if (target == null) {
                throw new IndexOutOfBoundsException();
            }
            target.setBigIntegerValue(checked);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void xsetCheckedArray(XmlInteger[] checkedArray) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.arraySetterHelper(checkedArray, PROPERTY_QNAME[42]);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void xsetCheckedArray(int i, XmlInteger checked) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            XmlInteger target = null;
            target = (XmlInteger)((Object)this.get_store().find_element_user(PROPERTY_QNAME[42], i));
            if (target == null) {
                throw new IndexOutOfBoundsException();
            }
            target.set(checked);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void insertChecked(int i, BigInteger checked) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            SimpleValue target = (SimpleValue)((Object)this.get_store().insert_element_user(PROPERTY_QNAME[42], i));
            target.setBigIntegerValue(checked);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void addChecked(BigInteger checked) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)((Object)this.get_store().add_element_user(PROPERTY_QNAME[42]));
            target.setBigIntegerValue(checked);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public XmlInteger insertNewChecked(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            XmlInteger target = null;
            target = (XmlInteger)((Object)this.get_store().insert_element_user(PROPERTY_QNAME[42], i));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public XmlInteger addNewChecked() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            XmlInteger target = null;
            target = (XmlInteger)((Object)this.get_store().add_element_user(PROPERTY_QNAME[42]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void removeChecked(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.get_store().remove_element(PROPERTY_QNAME[42], i);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public List<String> getFmlaLinkList() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return new JavaListObject<String>(this::getFmlaLinkArray, this::setFmlaLinkArray, this::insertFmlaLink, this::removeFmlaLink, this::sizeOfFmlaLinkArray);
        }
    }

    @Override
    public String[] getFmlaLinkArray() {
        return this.getObjectArray(PROPERTY_QNAME[43], SimpleValue::getStringValue, String[]::new);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public String getFmlaLinkArray(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)((Object)this.get_store().find_element_user(PROPERTY_QNAME[43], i));
            if (target == null) {
                throw new IndexOutOfBoundsException();
            }
            return target.getStringValue();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public List<XmlString> xgetFmlaLinkList() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return new JavaListXmlObject<XmlString>(this::xgetFmlaLinkArray, this::xsetFmlaLinkArray, this::insertNewFmlaLink, this::removeFmlaLink, this::sizeOfFmlaLinkArray);
        }
    }

    @Override
    public XmlString[] xgetFmlaLinkArray() {
        return (XmlString[])this.xgetArray(PROPERTY_QNAME[43], XmlString[]::new);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public XmlString xgetFmlaLinkArray(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            XmlString target = null;
            target = (XmlString)((Object)this.get_store().find_element_user(PROPERTY_QNAME[43], i));
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
    public int sizeOfFmlaLinkArray() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[43]);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void setFmlaLinkArray(String[] fmlaLinkArray) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.arraySetterHelper(fmlaLinkArray, PROPERTY_QNAME[43]);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void setFmlaLinkArray(int i, String fmlaLink) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)((Object)this.get_store().find_element_user(PROPERTY_QNAME[43], i));
            if (target == null) {
                throw new IndexOutOfBoundsException();
            }
            target.setStringValue(fmlaLink);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void xsetFmlaLinkArray(XmlString[] fmlaLinkArray) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.arraySetterHelper(fmlaLinkArray, PROPERTY_QNAME[43]);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void xsetFmlaLinkArray(int i, XmlString fmlaLink) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            XmlString target = null;
            target = (XmlString)((Object)this.get_store().find_element_user(PROPERTY_QNAME[43], i));
            if (target == null) {
                throw new IndexOutOfBoundsException();
            }
            target.set(fmlaLink);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void insertFmlaLink(int i, String fmlaLink) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            SimpleValue target = (SimpleValue)((Object)this.get_store().insert_element_user(PROPERTY_QNAME[43], i));
            target.setStringValue(fmlaLink);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void addFmlaLink(String fmlaLink) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)((Object)this.get_store().add_element_user(PROPERTY_QNAME[43]));
            target.setStringValue(fmlaLink);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public XmlString insertNewFmlaLink(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            XmlString target = null;
            target = (XmlString)((Object)this.get_store().insert_element_user(PROPERTY_QNAME[43], i));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public XmlString addNewFmlaLink() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            XmlString target = null;
            target = (XmlString)((Object)this.get_store().add_element_user(PROPERTY_QNAME[43]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void removeFmlaLink(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.get_store().remove_element(PROPERTY_QNAME[43], i);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public List<String> getFmlaPictList() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return new JavaListObject<String>(this::getFmlaPictArray, this::setFmlaPictArray, this::insertFmlaPict, this::removeFmlaPict, this::sizeOfFmlaPictArray);
        }
    }

    @Override
    public String[] getFmlaPictArray() {
        return this.getObjectArray(PROPERTY_QNAME[44], SimpleValue::getStringValue, String[]::new);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public String getFmlaPictArray(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)((Object)this.get_store().find_element_user(PROPERTY_QNAME[44], i));
            if (target == null) {
                throw new IndexOutOfBoundsException();
            }
            return target.getStringValue();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public List<XmlString> xgetFmlaPictList() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return new JavaListXmlObject<XmlString>(this::xgetFmlaPictArray, this::xsetFmlaPictArray, this::insertNewFmlaPict, this::removeFmlaPict, this::sizeOfFmlaPictArray);
        }
    }

    @Override
    public XmlString[] xgetFmlaPictArray() {
        return (XmlString[])this.xgetArray(PROPERTY_QNAME[44], XmlString[]::new);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public XmlString xgetFmlaPictArray(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            XmlString target = null;
            target = (XmlString)((Object)this.get_store().find_element_user(PROPERTY_QNAME[44], i));
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
    public int sizeOfFmlaPictArray() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[44]);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void setFmlaPictArray(String[] fmlaPictArray) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.arraySetterHelper(fmlaPictArray, PROPERTY_QNAME[44]);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void setFmlaPictArray(int i, String fmlaPict) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)((Object)this.get_store().find_element_user(PROPERTY_QNAME[44], i));
            if (target == null) {
                throw new IndexOutOfBoundsException();
            }
            target.setStringValue(fmlaPict);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void xsetFmlaPictArray(XmlString[] fmlaPictArray) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.arraySetterHelper(fmlaPictArray, PROPERTY_QNAME[44]);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void xsetFmlaPictArray(int i, XmlString fmlaPict) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            XmlString target = null;
            target = (XmlString)((Object)this.get_store().find_element_user(PROPERTY_QNAME[44], i));
            if (target == null) {
                throw new IndexOutOfBoundsException();
            }
            target.set(fmlaPict);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void insertFmlaPict(int i, String fmlaPict) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            SimpleValue target = (SimpleValue)((Object)this.get_store().insert_element_user(PROPERTY_QNAME[44], i));
            target.setStringValue(fmlaPict);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void addFmlaPict(String fmlaPict) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)((Object)this.get_store().add_element_user(PROPERTY_QNAME[44]));
            target.setStringValue(fmlaPict);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public XmlString insertNewFmlaPict(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            XmlString target = null;
            target = (XmlString)((Object)this.get_store().insert_element_user(PROPERTY_QNAME[44], i));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public XmlString addNewFmlaPict() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            XmlString target = null;
            target = (XmlString)((Object)this.get_store().add_element_user(PROPERTY_QNAME[44]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void removeFmlaPict(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.get_store().remove_element(PROPERTY_QNAME[44], i);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public List<STTrueFalseBlank.Enum> getNoThreeDList() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return new JavaListObject<STTrueFalseBlank.Enum>(this::getNoThreeDArray, this::setNoThreeDArray, this::insertNoThreeD, this::removeNoThreeD, this::sizeOfNoThreeDArray);
        }
    }

    @Override
    public STTrueFalseBlank.Enum[] getNoThreeDArray() {
        return (STTrueFalseBlank.Enum[])this.getEnumArray(PROPERTY_QNAME[45], STTrueFalseBlank.Enum[]::new);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public STTrueFalseBlank.Enum getNoThreeDArray(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)((Object)this.get_store().find_element_user(PROPERTY_QNAME[45], i));
            if (target == null) {
                throw new IndexOutOfBoundsException();
            }
            return (STTrueFalseBlank.Enum)target.getEnumValue();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public List<STTrueFalseBlank> xgetNoThreeDList() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return new JavaListXmlObject<STTrueFalseBlank>(this::xgetNoThreeDArray, this::xsetNoThreeDArray, this::insertNewNoThreeD, this::removeNoThreeD, this::sizeOfNoThreeDArray);
        }
    }

    @Override
    public STTrueFalseBlank[] xgetNoThreeDArray() {
        return (STTrueFalseBlank[])this.xgetArray(PROPERTY_QNAME[45], STTrueFalseBlank[]::new);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public STTrueFalseBlank xgetNoThreeDArray(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            STTrueFalseBlank target = null;
            target = (STTrueFalseBlank)((Object)this.get_store().find_element_user(PROPERTY_QNAME[45], i));
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
    public int sizeOfNoThreeDArray() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[45]);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void setNoThreeDArray(STTrueFalseBlank.Enum[] noThreeDArray) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.arraySetterHelper(noThreeDArray, PROPERTY_QNAME[45]);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void setNoThreeDArray(int i, STTrueFalseBlank.Enum noThreeD) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)((Object)this.get_store().find_element_user(PROPERTY_QNAME[45], i));
            if (target == null) {
                throw new IndexOutOfBoundsException();
            }
            target.setEnumValue(noThreeD);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void xsetNoThreeDArray(STTrueFalseBlank[] noThreeDArray) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.arraySetterHelper(noThreeDArray, PROPERTY_QNAME[45]);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void xsetNoThreeDArray(int i, STTrueFalseBlank noThreeD) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            STTrueFalseBlank target = null;
            target = (STTrueFalseBlank)((Object)this.get_store().find_element_user(PROPERTY_QNAME[45], i));
            if (target == null) {
                throw new IndexOutOfBoundsException();
            }
            target.set(noThreeD);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void insertNoThreeD(int i, STTrueFalseBlank.Enum noThreeD) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            SimpleValue target = (SimpleValue)((Object)this.get_store().insert_element_user(PROPERTY_QNAME[45], i));
            target.setEnumValue(noThreeD);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void addNoThreeD(STTrueFalseBlank.Enum noThreeD) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)((Object)this.get_store().add_element_user(PROPERTY_QNAME[45]));
            target.setEnumValue(noThreeD);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public STTrueFalseBlank insertNewNoThreeD(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            STTrueFalseBlank target = null;
            target = (STTrueFalseBlank)((Object)this.get_store().insert_element_user(PROPERTY_QNAME[45], i));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public STTrueFalseBlank addNewNoThreeD() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            STTrueFalseBlank target = null;
            target = (STTrueFalseBlank)((Object)this.get_store().add_element_user(PROPERTY_QNAME[45]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void removeNoThreeD(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.get_store().remove_element(PROPERTY_QNAME[45], i);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public List<STTrueFalseBlank.Enum> getFirstButtonList() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return new JavaListObject<STTrueFalseBlank.Enum>(this::getFirstButtonArray, this::setFirstButtonArray, this::insertFirstButton, this::removeFirstButton, this::sizeOfFirstButtonArray);
        }
    }

    @Override
    public STTrueFalseBlank.Enum[] getFirstButtonArray() {
        return (STTrueFalseBlank.Enum[])this.getEnumArray(PROPERTY_QNAME[46], STTrueFalseBlank.Enum[]::new);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public STTrueFalseBlank.Enum getFirstButtonArray(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)((Object)this.get_store().find_element_user(PROPERTY_QNAME[46], i));
            if (target == null) {
                throw new IndexOutOfBoundsException();
            }
            return (STTrueFalseBlank.Enum)target.getEnumValue();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public List<STTrueFalseBlank> xgetFirstButtonList() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return new JavaListXmlObject<STTrueFalseBlank>(this::xgetFirstButtonArray, this::xsetFirstButtonArray, this::insertNewFirstButton, this::removeFirstButton, this::sizeOfFirstButtonArray);
        }
    }

    @Override
    public STTrueFalseBlank[] xgetFirstButtonArray() {
        return (STTrueFalseBlank[])this.xgetArray(PROPERTY_QNAME[46], STTrueFalseBlank[]::new);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public STTrueFalseBlank xgetFirstButtonArray(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            STTrueFalseBlank target = null;
            target = (STTrueFalseBlank)((Object)this.get_store().find_element_user(PROPERTY_QNAME[46], i));
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
    public int sizeOfFirstButtonArray() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[46]);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void setFirstButtonArray(STTrueFalseBlank.Enum[] firstButtonArray) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.arraySetterHelper(firstButtonArray, PROPERTY_QNAME[46]);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void setFirstButtonArray(int i, STTrueFalseBlank.Enum firstButton) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)((Object)this.get_store().find_element_user(PROPERTY_QNAME[46], i));
            if (target == null) {
                throw new IndexOutOfBoundsException();
            }
            target.setEnumValue(firstButton);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void xsetFirstButtonArray(STTrueFalseBlank[] firstButtonArray) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.arraySetterHelper(firstButtonArray, PROPERTY_QNAME[46]);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void xsetFirstButtonArray(int i, STTrueFalseBlank firstButton) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            STTrueFalseBlank target = null;
            target = (STTrueFalseBlank)((Object)this.get_store().find_element_user(PROPERTY_QNAME[46], i));
            if (target == null) {
                throw new IndexOutOfBoundsException();
            }
            target.set(firstButton);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void insertFirstButton(int i, STTrueFalseBlank.Enum firstButton) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            SimpleValue target = (SimpleValue)((Object)this.get_store().insert_element_user(PROPERTY_QNAME[46], i));
            target.setEnumValue(firstButton);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void addFirstButton(STTrueFalseBlank.Enum firstButton) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)((Object)this.get_store().add_element_user(PROPERTY_QNAME[46]));
            target.setEnumValue(firstButton);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public STTrueFalseBlank insertNewFirstButton(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            STTrueFalseBlank target = null;
            target = (STTrueFalseBlank)((Object)this.get_store().insert_element_user(PROPERTY_QNAME[46], i));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public STTrueFalseBlank addNewFirstButton() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            STTrueFalseBlank target = null;
            target = (STTrueFalseBlank)((Object)this.get_store().add_element_user(PROPERTY_QNAME[46]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void removeFirstButton(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.get_store().remove_element(PROPERTY_QNAME[46], i);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public List<String> getFmlaGroupList() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return new JavaListObject<String>(this::getFmlaGroupArray, this::setFmlaGroupArray, this::insertFmlaGroup, this::removeFmlaGroup, this::sizeOfFmlaGroupArray);
        }
    }

    @Override
    public String[] getFmlaGroupArray() {
        return this.getObjectArray(PROPERTY_QNAME[47], SimpleValue::getStringValue, String[]::new);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public String getFmlaGroupArray(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)((Object)this.get_store().find_element_user(PROPERTY_QNAME[47], i));
            if (target == null) {
                throw new IndexOutOfBoundsException();
            }
            return target.getStringValue();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public List<XmlString> xgetFmlaGroupList() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return new JavaListXmlObject<XmlString>(this::xgetFmlaGroupArray, this::xsetFmlaGroupArray, this::insertNewFmlaGroup, this::removeFmlaGroup, this::sizeOfFmlaGroupArray);
        }
    }

    @Override
    public XmlString[] xgetFmlaGroupArray() {
        return (XmlString[])this.xgetArray(PROPERTY_QNAME[47], XmlString[]::new);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public XmlString xgetFmlaGroupArray(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            XmlString target = null;
            target = (XmlString)((Object)this.get_store().find_element_user(PROPERTY_QNAME[47], i));
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
    public int sizeOfFmlaGroupArray() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[47]);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void setFmlaGroupArray(String[] fmlaGroupArray) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.arraySetterHelper(fmlaGroupArray, PROPERTY_QNAME[47]);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void setFmlaGroupArray(int i, String fmlaGroup) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)((Object)this.get_store().find_element_user(PROPERTY_QNAME[47], i));
            if (target == null) {
                throw new IndexOutOfBoundsException();
            }
            target.setStringValue(fmlaGroup);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void xsetFmlaGroupArray(XmlString[] fmlaGroupArray) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.arraySetterHelper(fmlaGroupArray, PROPERTY_QNAME[47]);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void xsetFmlaGroupArray(int i, XmlString fmlaGroup) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            XmlString target = null;
            target = (XmlString)((Object)this.get_store().find_element_user(PROPERTY_QNAME[47], i));
            if (target == null) {
                throw new IndexOutOfBoundsException();
            }
            target.set(fmlaGroup);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void insertFmlaGroup(int i, String fmlaGroup) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            SimpleValue target = (SimpleValue)((Object)this.get_store().insert_element_user(PROPERTY_QNAME[47], i));
            target.setStringValue(fmlaGroup);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void addFmlaGroup(String fmlaGroup) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)((Object)this.get_store().add_element_user(PROPERTY_QNAME[47]));
            target.setStringValue(fmlaGroup);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public XmlString insertNewFmlaGroup(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            XmlString target = null;
            target = (XmlString)((Object)this.get_store().insert_element_user(PROPERTY_QNAME[47], i));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public XmlString addNewFmlaGroup() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            XmlString target = null;
            target = (XmlString)((Object)this.get_store().add_element_user(PROPERTY_QNAME[47]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void removeFmlaGroup(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.get_store().remove_element(PROPERTY_QNAME[47], i);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public List<BigInteger> getValList() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return new JavaListObject<BigInteger>(this::getValArray, this::setValArray, this::insertVal, this::removeVal, this::sizeOfValArray);
        }
    }

    @Override
    public BigInteger[] getValArray() {
        return this.getObjectArray(PROPERTY_QNAME[48], SimpleValue::getBigIntegerValue, BigInteger[]::new);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public BigInteger getValArray(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)((Object)this.get_store().find_element_user(PROPERTY_QNAME[48], i));
            if (target == null) {
                throw new IndexOutOfBoundsException();
            }
            return target.getBigIntegerValue();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public List<XmlInteger> xgetValList() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return new JavaListXmlObject<XmlInteger>(this::xgetValArray, this::xsetValArray, this::insertNewVal, this::removeVal, this::sizeOfValArray);
        }
    }

    @Override
    public XmlInteger[] xgetValArray() {
        return (XmlInteger[])this.xgetArray(PROPERTY_QNAME[48], XmlInteger[]::new);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public XmlInteger xgetValArray(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            XmlInteger target = null;
            target = (XmlInteger)((Object)this.get_store().find_element_user(PROPERTY_QNAME[48], i));
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
    public int sizeOfValArray() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[48]);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void setValArray(BigInteger[] valArray) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.arraySetterHelper(valArray, PROPERTY_QNAME[48]);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void setValArray(int i, BigInteger val) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)((Object)this.get_store().find_element_user(PROPERTY_QNAME[48], i));
            if (target == null) {
                throw new IndexOutOfBoundsException();
            }
            target.setBigIntegerValue(val);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void xsetValArray(XmlInteger[] valArray) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.arraySetterHelper(valArray, PROPERTY_QNAME[48]);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void xsetValArray(int i, XmlInteger val) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            XmlInteger target = null;
            target = (XmlInteger)((Object)this.get_store().find_element_user(PROPERTY_QNAME[48], i));
            if (target == null) {
                throw new IndexOutOfBoundsException();
            }
            target.set(val);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void insertVal(int i, BigInteger val) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            SimpleValue target = (SimpleValue)((Object)this.get_store().insert_element_user(PROPERTY_QNAME[48], i));
            target.setBigIntegerValue(val);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void addVal(BigInteger val) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)((Object)this.get_store().add_element_user(PROPERTY_QNAME[48]));
            target.setBigIntegerValue(val);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public XmlInteger insertNewVal(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            XmlInteger target = null;
            target = (XmlInteger)((Object)this.get_store().insert_element_user(PROPERTY_QNAME[48], i));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public XmlInteger addNewVal() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            XmlInteger target = null;
            target = (XmlInteger)((Object)this.get_store().add_element_user(PROPERTY_QNAME[48]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void removeVal(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.get_store().remove_element(PROPERTY_QNAME[48], i);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public List<BigInteger> getMinList() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return new JavaListObject<BigInteger>(this::getMinArray, this::setMinArray, this::insertMin, this::removeMin, this::sizeOfMinArray);
        }
    }

    @Override
    public BigInteger[] getMinArray() {
        return this.getObjectArray(PROPERTY_QNAME[49], SimpleValue::getBigIntegerValue, BigInteger[]::new);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public BigInteger getMinArray(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)((Object)this.get_store().find_element_user(PROPERTY_QNAME[49], i));
            if (target == null) {
                throw new IndexOutOfBoundsException();
            }
            return target.getBigIntegerValue();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public List<XmlInteger> xgetMinList() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return new JavaListXmlObject<XmlInteger>(this::xgetMinArray, this::xsetMinArray, this::insertNewMin, this::removeMin, this::sizeOfMinArray);
        }
    }

    @Override
    public XmlInteger[] xgetMinArray() {
        return (XmlInteger[])this.xgetArray(PROPERTY_QNAME[49], XmlInteger[]::new);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public XmlInteger xgetMinArray(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            XmlInteger target = null;
            target = (XmlInteger)((Object)this.get_store().find_element_user(PROPERTY_QNAME[49], i));
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
    public int sizeOfMinArray() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[49]);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void setMinArray(BigInteger[] minArray) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.arraySetterHelper(minArray, PROPERTY_QNAME[49]);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void setMinArray(int i, BigInteger min) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)((Object)this.get_store().find_element_user(PROPERTY_QNAME[49], i));
            if (target == null) {
                throw new IndexOutOfBoundsException();
            }
            target.setBigIntegerValue(min);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void xsetMinArray(XmlInteger[] minArray) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.arraySetterHelper(minArray, PROPERTY_QNAME[49]);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void xsetMinArray(int i, XmlInteger min) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            XmlInteger target = null;
            target = (XmlInteger)((Object)this.get_store().find_element_user(PROPERTY_QNAME[49], i));
            if (target == null) {
                throw new IndexOutOfBoundsException();
            }
            target.set(min);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void insertMin(int i, BigInteger min) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            SimpleValue target = (SimpleValue)((Object)this.get_store().insert_element_user(PROPERTY_QNAME[49], i));
            target.setBigIntegerValue(min);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void addMin(BigInteger min) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)((Object)this.get_store().add_element_user(PROPERTY_QNAME[49]));
            target.setBigIntegerValue(min);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public XmlInteger insertNewMin(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            XmlInteger target = null;
            target = (XmlInteger)((Object)this.get_store().insert_element_user(PROPERTY_QNAME[49], i));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public XmlInteger addNewMin() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            XmlInteger target = null;
            target = (XmlInteger)((Object)this.get_store().add_element_user(PROPERTY_QNAME[49]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void removeMin(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.get_store().remove_element(PROPERTY_QNAME[49], i);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public List<BigInteger> getMaxList() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return new JavaListObject<BigInteger>(this::getMaxArray, this::setMaxArray, this::insertMax, this::removeMax, this::sizeOfMaxArray);
        }
    }

    @Override
    public BigInteger[] getMaxArray() {
        return this.getObjectArray(PROPERTY_QNAME[50], SimpleValue::getBigIntegerValue, BigInteger[]::new);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public BigInteger getMaxArray(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)((Object)this.get_store().find_element_user(PROPERTY_QNAME[50], i));
            if (target == null) {
                throw new IndexOutOfBoundsException();
            }
            return target.getBigIntegerValue();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public List<XmlInteger> xgetMaxList() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return new JavaListXmlObject<XmlInteger>(this::xgetMaxArray, this::xsetMaxArray, this::insertNewMax, this::removeMax, this::sizeOfMaxArray);
        }
    }

    @Override
    public XmlInteger[] xgetMaxArray() {
        return (XmlInteger[])this.xgetArray(PROPERTY_QNAME[50], XmlInteger[]::new);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public XmlInteger xgetMaxArray(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            XmlInteger target = null;
            target = (XmlInteger)((Object)this.get_store().find_element_user(PROPERTY_QNAME[50], i));
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
    public int sizeOfMaxArray() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[50]);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void setMaxArray(BigInteger[] maxArray) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.arraySetterHelper(maxArray, PROPERTY_QNAME[50]);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void setMaxArray(int i, BigInteger max) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)((Object)this.get_store().find_element_user(PROPERTY_QNAME[50], i));
            if (target == null) {
                throw new IndexOutOfBoundsException();
            }
            target.setBigIntegerValue(max);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void xsetMaxArray(XmlInteger[] maxArray) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.arraySetterHelper(maxArray, PROPERTY_QNAME[50]);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void xsetMaxArray(int i, XmlInteger max) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            XmlInteger target = null;
            target = (XmlInteger)((Object)this.get_store().find_element_user(PROPERTY_QNAME[50], i));
            if (target == null) {
                throw new IndexOutOfBoundsException();
            }
            target.set(max);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void insertMax(int i, BigInteger max) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            SimpleValue target = (SimpleValue)((Object)this.get_store().insert_element_user(PROPERTY_QNAME[50], i));
            target.setBigIntegerValue(max);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void addMax(BigInteger max) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)((Object)this.get_store().add_element_user(PROPERTY_QNAME[50]));
            target.setBigIntegerValue(max);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public XmlInteger insertNewMax(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            XmlInteger target = null;
            target = (XmlInteger)((Object)this.get_store().insert_element_user(PROPERTY_QNAME[50], i));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public XmlInteger addNewMax() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            XmlInteger target = null;
            target = (XmlInteger)((Object)this.get_store().add_element_user(PROPERTY_QNAME[50]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void removeMax(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.get_store().remove_element(PROPERTY_QNAME[50], i);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public List<BigInteger> getIncList() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return new JavaListObject<BigInteger>(this::getIncArray, this::setIncArray, this::insertInc, this::removeInc, this::sizeOfIncArray);
        }
    }

    @Override
    public BigInteger[] getIncArray() {
        return this.getObjectArray(PROPERTY_QNAME[51], SimpleValue::getBigIntegerValue, BigInteger[]::new);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public BigInteger getIncArray(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)((Object)this.get_store().find_element_user(PROPERTY_QNAME[51], i));
            if (target == null) {
                throw new IndexOutOfBoundsException();
            }
            return target.getBigIntegerValue();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public List<XmlInteger> xgetIncList() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return new JavaListXmlObject<XmlInteger>(this::xgetIncArray, this::xsetIncArray, this::insertNewInc, this::removeInc, this::sizeOfIncArray);
        }
    }

    @Override
    public XmlInteger[] xgetIncArray() {
        return (XmlInteger[])this.xgetArray(PROPERTY_QNAME[51], XmlInteger[]::new);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public XmlInteger xgetIncArray(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            XmlInteger target = null;
            target = (XmlInteger)((Object)this.get_store().find_element_user(PROPERTY_QNAME[51], i));
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
    public int sizeOfIncArray() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[51]);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void setIncArray(BigInteger[] incArray) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.arraySetterHelper(incArray, PROPERTY_QNAME[51]);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void setIncArray(int i, BigInteger inc) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)((Object)this.get_store().find_element_user(PROPERTY_QNAME[51], i));
            if (target == null) {
                throw new IndexOutOfBoundsException();
            }
            target.setBigIntegerValue(inc);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void xsetIncArray(XmlInteger[] incArray) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.arraySetterHelper(incArray, PROPERTY_QNAME[51]);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void xsetIncArray(int i, XmlInteger inc) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            XmlInteger target = null;
            target = (XmlInteger)((Object)this.get_store().find_element_user(PROPERTY_QNAME[51], i));
            if (target == null) {
                throw new IndexOutOfBoundsException();
            }
            target.set(inc);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void insertInc(int i, BigInteger inc) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            SimpleValue target = (SimpleValue)((Object)this.get_store().insert_element_user(PROPERTY_QNAME[51], i));
            target.setBigIntegerValue(inc);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void addInc(BigInteger inc) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)((Object)this.get_store().add_element_user(PROPERTY_QNAME[51]));
            target.setBigIntegerValue(inc);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public XmlInteger insertNewInc(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            XmlInteger target = null;
            target = (XmlInteger)((Object)this.get_store().insert_element_user(PROPERTY_QNAME[51], i));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public XmlInteger addNewInc() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            XmlInteger target = null;
            target = (XmlInteger)((Object)this.get_store().add_element_user(PROPERTY_QNAME[51]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void removeInc(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.get_store().remove_element(PROPERTY_QNAME[51], i);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public List<BigInteger> getPageList() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return new JavaListObject<BigInteger>(this::getPageArray, this::setPageArray, this::insertPage, this::removePage, this::sizeOfPageArray);
        }
    }

    @Override
    public BigInteger[] getPageArray() {
        return this.getObjectArray(PROPERTY_QNAME[52], SimpleValue::getBigIntegerValue, BigInteger[]::new);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public BigInteger getPageArray(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)((Object)this.get_store().find_element_user(PROPERTY_QNAME[52], i));
            if (target == null) {
                throw new IndexOutOfBoundsException();
            }
            return target.getBigIntegerValue();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public List<XmlInteger> xgetPageList() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return new JavaListXmlObject<XmlInteger>(this::xgetPageArray, this::xsetPageArray, this::insertNewPage, this::removePage, this::sizeOfPageArray);
        }
    }

    @Override
    public XmlInteger[] xgetPageArray() {
        return (XmlInteger[])this.xgetArray(PROPERTY_QNAME[52], XmlInteger[]::new);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public XmlInteger xgetPageArray(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            XmlInteger target = null;
            target = (XmlInteger)((Object)this.get_store().find_element_user(PROPERTY_QNAME[52], i));
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
    public int sizeOfPageArray() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[52]);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void setPageArray(BigInteger[] pageArray) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.arraySetterHelper(pageArray, PROPERTY_QNAME[52]);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void setPageArray(int i, BigInteger page) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)((Object)this.get_store().find_element_user(PROPERTY_QNAME[52], i));
            if (target == null) {
                throw new IndexOutOfBoundsException();
            }
            target.setBigIntegerValue(page);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void xsetPageArray(XmlInteger[] pageArray) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.arraySetterHelper(pageArray, PROPERTY_QNAME[52]);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void xsetPageArray(int i, XmlInteger page) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            XmlInteger target = null;
            target = (XmlInteger)((Object)this.get_store().find_element_user(PROPERTY_QNAME[52], i));
            if (target == null) {
                throw new IndexOutOfBoundsException();
            }
            target.set(page);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void insertPage(int i, BigInteger page) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            SimpleValue target = (SimpleValue)((Object)this.get_store().insert_element_user(PROPERTY_QNAME[52], i));
            target.setBigIntegerValue(page);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void addPage(BigInteger page) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)((Object)this.get_store().add_element_user(PROPERTY_QNAME[52]));
            target.setBigIntegerValue(page);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public XmlInteger insertNewPage(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            XmlInteger target = null;
            target = (XmlInteger)((Object)this.get_store().insert_element_user(PROPERTY_QNAME[52], i));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public XmlInteger addNewPage() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            XmlInteger target = null;
            target = (XmlInteger)((Object)this.get_store().add_element_user(PROPERTY_QNAME[52]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void removePage(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.get_store().remove_element(PROPERTY_QNAME[52], i);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public List<STTrueFalseBlank.Enum> getHorizList() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return new JavaListObject<STTrueFalseBlank.Enum>(this::getHorizArray, this::setHorizArray, this::insertHoriz, this::removeHoriz, this::sizeOfHorizArray);
        }
    }

    @Override
    public STTrueFalseBlank.Enum[] getHorizArray() {
        return (STTrueFalseBlank.Enum[])this.getEnumArray(PROPERTY_QNAME[53], STTrueFalseBlank.Enum[]::new);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public STTrueFalseBlank.Enum getHorizArray(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)((Object)this.get_store().find_element_user(PROPERTY_QNAME[53], i));
            if (target == null) {
                throw new IndexOutOfBoundsException();
            }
            return (STTrueFalseBlank.Enum)target.getEnumValue();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public List<STTrueFalseBlank> xgetHorizList() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return new JavaListXmlObject<STTrueFalseBlank>(this::xgetHorizArray, this::xsetHorizArray, this::insertNewHoriz, this::removeHoriz, this::sizeOfHorizArray);
        }
    }

    @Override
    public STTrueFalseBlank[] xgetHorizArray() {
        return (STTrueFalseBlank[])this.xgetArray(PROPERTY_QNAME[53], STTrueFalseBlank[]::new);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public STTrueFalseBlank xgetHorizArray(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            STTrueFalseBlank target = null;
            target = (STTrueFalseBlank)((Object)this.get_store().find_element_user(PROPERTY_QNAME[53], i));
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
    public int sizeOfHorizArray() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[53]);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void setHorizArray(STTrueFalseBlank.Enum[] horizArray) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.arraySetterHelper(horizArray, PROPERTY_QNAME[53]);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void setHorizArray(int i, STTrueFalseBlank.Enum horiz) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)((Object)this.get_store().find_element_user(PROPERTY_QNAME[53], i));
            if (target == null) {
                throw new IndexOutOfBoundsException();
            }
            target.setEnumValue(horiz);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void xsetHorizArray(STTrueFalseBlank[] horizArray) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.arraySetterHelper(horizArray, PROPERTY_QNAME[53]);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void xsetHorizArray(int i, STTrueFalseBlank horiz) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            STTrueFalseBlank target = null;
            target = (STTrueFalseBlank)((Object)this.get_store().find_element_user(PROPERTY_QNAME[53], i));
            if (target == null) {
                throw new IndexOutOfBoundsException();
            }
            target.set(horiz);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void insertHoriz(int i, STTrueFalseBlank.Enum horiz) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            SimpleValue target = (SimpleValue)((Object)this.get_store().insert_element_user(PROPERTY_QNAME[53], i));
            target.setEnumValue(horiz);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void addHoriz(STTrueFalseBlank.Enum horiz) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)((Object)this.get_store().add_element_user(PROPERTY_QNAME[53]));
            target.setEnumValue(horiz);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public STTrueFalseBlank insertNewHoriz(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            STTrueFalseBlank target = null;
            target = (STTrueFalseBlank)((Object)this.get_store().insert_element_user(PROPERTY_QNAME[53], i));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public STTrueFalseBlank addNewHoriz() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            STTrueFalseBlank target = null;
            target = (STTrueFalseBlank)((Object)this.get_store().add_element_user(PROPERTY_QNAME[53]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void removeHoriz(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.get_store().remove_element(PROPERTY_QNAME[53], i);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public List<BigInteger> getDxList() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return new JavaListObject<BigInteger>(this::getDxArray, this::setDxArray, this::insertDx, this::removeDx, this::sizeOfDxArray);
        }
    }

    @Override
    public BigInteger[] getDxArray() {
        return this.getObjectArray(PROPERTY_QNAME[54], SimpleValue::getBigIntegerValue, BigInteger[]::new);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public BigInteger getDxArray(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)((Object)this.get_store().find_element_user(PROPERTY_QNAME[54], i));
            if (target == null) {
                throw new IndexOutOfBoundsException();
            }
            return target.getBigIntegerValue();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public List<XmlInteger> xgetDxList() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return new JavaListXmlObject<XmlInteger>(this::xgetDxArray, this::xsetDxArray, this::insertNewDx, this::removeDx, this::sizeOfDxArray);
        }
    }

    @Override
    public XmlInteger[] xgetDxArray() {
        return (XmlInteger[])this.xgetArray(PROPERTY_QNAME[54], XmlInteger[]::new);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public XmlInteger xgetDxArray(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            XmlInteger target = null;
            target = (XmlInteger)((Object)this.get_store().find_element_user(PROPERTY_QNAME[54], i));
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
    public int sizeOfDxArray() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[54]);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void setDxArray(BigInteger[] dxArray) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.arraySetterHelper(dxArray, PROPERTY_QNAME[54]);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void setDxArray(int i, BigInteger dx) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)((Object)this.get_store().find_element_user(PROPERTY_QNAME[54], i));
            if (target == null) {
                throw new IndexOutOfBoundsException();
            }
            target.setBigIntegerValue(dx);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void xsetDxArray(XmlInteger[] dxArray) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.arraySetterHelper(dxArray, PROPERTY_QNAME[54]);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void xsetDxArray(int i, XmlInteger dx) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            XmlInteger target = null;
            target = (XmlInteger)((Object)this.get_store().find_element_user(PROPERTY_QNAME[54], i));
            if (target == null) {
                throw new IndexOutOfBoundsException();
            }
            target.set(dx);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void insertDx(int i, BigInteger dx) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            SimpleValue target = (SimpleValue)((Object)this.get_store().insert_element_user(PROPERTY_QNAME[54], i));
            target.setBigIntegerValue(dx);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void addDx(BigInteger dx) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)((Object)this.get_store().add_element_user(PROPERTY_QNAME[54]));
            target.setBigIntegerValue(dx);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public XmlInteger insertNewDx(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            XmlInteger target = null;
            target = (XmlInteger)((Object)this.get_store().insert_element_user(PROPERTY_QNAME[54], i));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public XmlInteger addNewDx() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            XmlInteger target = null;
            target = (XmlInteger)((Object)this.get_store().add_element_user(PROPERTY_QNAME[54]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void removeDx(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.get_store().remove_element(PROPERTY_QNAME[54], i);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public List<STTrueFalseBlank.Enum> getMapOCXList() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return new JavaListObject<STTrueFalseBlank.Enum>(this::getMapOCXArray, this::setMapOCXArray, this::insertMapOCX, this::removeMapOCX, this::sizeOfMapOCXArray);
        }
    }

    @Override
    public STTrueFalseBlank.Enum[] getMapOCXArray() {
        return (STTrueFalseBlank.Enum[])this.getEnumArray(PROPERTY_QNAME[55], STTrueFalseBlank.Enum[]::new);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public STTrueFalseBlank.Enum getMapOCXArray(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)((Object)this.get_store().find_element_user(PROPERTY_QNAME[55], i));
            if (target == null) {
                throw new IndexOutOfBoundsException();
            }
            return (STTrueFalseBlank.Enum)target.getEnumValue();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public List<STTrueFalseBlank> xgetMapOCXList() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return new JavaListXmlObject<STTrueFalseBlank>(this::xgetMapOCXArray, this::xsetMapOCXArray, this::insertNewMapOCX, this::removeMapOCX, this::sizeOfMapOCXArray);
        }
    }

    @Override
    public STTrueFalseBlank[] xgetMapOCXArray() {
        return (STTrueFalseBlank[])this.xgetArray(PROPERTY_QNAME[55], STTrueFalseBlank[]::new);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public STTrueFalseBlank xgetMapOCXArray(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            STTrueFalseBlank target = null;
            target = (STTrueFalseBlank)((Object)this.get_store().find_element_user(PROPERTY_QNAME[55], i));
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
    public int sizeOfMapOCXArray() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[55]);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void setMapOCXArray(STTrueFalseBlank.Enum[] mapOCXArray) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.arraySetterHelper(mapOCXArray, PROPERTY_QNAME[55]);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void setMapOCXArray(int i, STTrueFalseBlank.Enum mapOCX) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)((Object)this.get_store().find_element_user(PROPERTY_QNAME[55], i));
            if (target == null) {
                throw new IndexOutOfBoundsException();
            }
            target.setEnumValue(mapOCX);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void xsetMapOCXArray(STTrueFalseBlank[] mapOCXArray) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.arraySetterHelper(mapOCXArray, PROPERTY_QNAME[55]);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void xsetMapOCXArray(int i, STTrueFalseBlank mapOCX) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            STTrueFalseBlank target = null;
            target = (STTrueFalseBlank)((Object)this.get_store().find_element_user(PROPERTY_QNAME[55], i));
            if (target == null) {
                throw new IndexOutOfBoundsException();
            }
            target.set(mapOCX);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void insertMapOCX(int i, STTrueFalseBlank.Enum mapOCX) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            SimpleValue target = (SimpleValue)((Object)this.get_store().insert_element_user(PROPERTY_QNAME[55], i));
            target.setEnumValue(mapOCX);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void addMapOCX(STTrueFalseBlank.Enum mapOCX) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)((Object)this.get_store().add_element_user(PROPERTY_QNAME[55]));
            target.setEnumValue(mapOCX);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public STTrueFalseBlank insertNewMapOCX(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            STTrueFalseBlank target = null;
            target = (STTrueFalseBlank)((Object)this.get_store().insert_element_user(PROPERTY_QNAME[55], i));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public STTrueFalseBlank addNewMapOCX() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            STTrueFalseBlank target = null;
            target = (STTrueFalseBlank)((Object)this.get_store().add_element_user(PROPERTY_QNAME[55]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void removeMapOCX(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.get_store().remove_element(PROPERTY_QNAME[55], i);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public List<String> getCFList() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return new JavaListObject<String>(this::getCFArray, this::setCFArray, this::insertCF, this::removeCF, this::sizeOfCFArray);
        }
    }

    @Override
    public String[] getCFArray() {
        return this.getObjectArray(PROPERTY_QNAME[56], SimpleValue::getStringValue, String[]::new);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public String getCFArray(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)((Object)this.get_store().find_element_user(PROPERTY_QNAME[56], i));
            if (target == null) {
                throw new IndexOutOfBoundsException();
            }
            return target.getStringValue();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public List<STCF> xgetCFList() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return new JavaListXmlObject<STCF>(this::xgetCFArray, this::xsetCFArray, this::insertNewCF, this::removeCF, this::sizeOfCFArray);
        }
    }

    @Override
    public STCF[] xgetCFArray() {
        return (STCF[])this.xgetArray(PROPERTY_QNAME[56], STCF[]::new);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public STCF xgetCFArray(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            STCF target = null;
            target = (STCF)((Object)this.get_store().find_element_user(PROPERTY_QNAME[56], i));
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
    public int sizeOfCFArray() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[56]);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void setCFArray(String[] cfArray) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.arraySetterHelper(cfArray, PROPERTY_QNAME[56]);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void setCFArray(int i, String cf) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)((Object)this.get_store().find_element_user(PROPERTY_QNAME[56], i));
            if (target == null) {
                throw new IndexOutOfBoundsException();
            }
            target.setStringValue(cf);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void xsetCFArray(STCF[] cfArray) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.arraySetterHelper(cfArray, PROPERTY_QNAME[56]);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void xsetCFArray(int i, STCF cf) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            STCF target = null;
            target = (STCF)((Object)this.get_store().find_element_user(PROPERTY_QNAME[56], i));
            if (target == null) {
                throw new IndexOutOfBoundsException();
            }
            target.set(cf);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void insertCF(int i, String cf) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            SimpleValue target = (SimpleValue)((Object)this.get_store().insert_element_user(PROPERTY_QNAME[56], i));
            target.setStringValue(cf);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void addCF(String cf) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)((Object)this.get_store().add_element_user(PROPERTY_QNAME[56]));
            target.setStringValue(cf);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public STCF insertNewCF(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            STCF target = null;
            target = (STCF)((Object)this.get_store().insert_element_user(PROPERTY_QNAME[56], i));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public STCF addNewCF() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            STCF target = null;
            target = (STCF)((Object)this.get_store().add_element_user(PROPERTY_QNAME[56]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void removeCF(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.get_store().remove_element(PROPERTY_QNAME[56], i);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public List<STTrueFalseBlank.Enum> getCameraList() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return new JavaListObject<STTrueFalseBlank.Enum>(this::getCameraArray, this::setCameraArray, this::insertCamera, this::removeCamera, this::sizeOfCameraArray);
        }
    }

    @Override
    public STTrueFalseBlank.Enum[] getCameraArray() {
        return (STTrueFalseBlank.Enum[])this.getEnumArray(PROPERTY_QNAME[57], STTrueFalseBlank.Enum[]::new);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public STTrueFalseBlank.Enum getCameraArray(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)((Object)this.get_store().find_element_user(PROPERTY_QNAME[57], i));
            if (target == null) {
                throw new IndexOutOfBoundsException();
            }
            return (STTrueFalseBlank.Enum)target.getEnumValue();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public List<STTrueFalseBlank> xgetCameraList() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return new JavaListXmlObject<STTrueFalseBlank>(this::xgetCameraArray, this::xsetCameraArray, this::insertNewCamera, this::removeCamera, this::sizeOfCameraArray);
        }
    }

    @Override
    public STTrueFalseBlank[] xgetCameraArray() {
        return (STTrueFalseBlank[])this.xgetArray(PROPERTY_QNAME[57], STTrueFalseBlank[]::new);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public STTrueFalseBlank xgetCameraArray(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            STTrueFalseBlank target = null;
            target = (STTrueFalseBlank)((Object)this.get_store().find_element_user(PROPERTY_QNAME[57], i));
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
    public int sizeOfCameraArray() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[57]);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void setCameraArray(STTrueFalseBlank.Enum[] cameraArray) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.arraySetterHelper(cameraArray, PROPERTY_QNAME[57]);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void setCameraArray(int i, STTrueFalseBlank.Enum camera) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)((Object)this.get_store().find_element_user(PROPERTY_QNAME[57], i));
            if (target == null) {
                throw new IndexOutOfBoundsException();
            }
            target.setEnumValue(camera);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void xsetCameraArray(STTrueFalseBlank[] cameraArray) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.arraySetterHelper(cameraArray, PROPERTY_QNAME[57]);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void xsetCameraArray(int i, STTrueFalseBlank camera) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            STTrueFalseBlank target = null;
            target = (STTrueFalseBlank)((Object)this.get_store().find_element_user(PROPERTY_QNAME[57], i));
            if (target == null) {
                throw new IndexOutOfBoundsException();
            }
            target.set(camera);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void insertCamera(int i, STTrueFalseBlank.Enum camera) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            SimpleValue target = (SimpleValue)((Object)this.get_store().insert_element_user(PROPERTY_QNAME[57], i));
            target.setEnumValue(camera);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void addCamera(STTrueFalseBlank.Enum camera) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)((Object)this.get_store().add_element_user(PROPERTY_QNAME[57]));
            target.setEnumValue(camera);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public STTrueFalseBlank insertNewCamera(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            STTrueFalseBlank target = null;
            target = (STTrueFalseBlank)((Object)this.get_store().insert_element_user(PROPERTY_QNAME[57], i));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public STTrueFalseBlank addNewCamera() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            STTrueFalseBlank target = null;
            target = (STTrueFalseBlank)((Object)this.get_store().add_element_user(PROPERTY_QNAME[57]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void removeCamera(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.get_store().remove_element(PROPERTY_QNAME[57], i);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public List<STTrueFalseBlank.Enum> getRecalcAlwaysList() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return new JavaListObject<STTrueFalseBlank.Enum>(this::getRecalcAlwaysArray, this::setRecalcAlwaysArray, this::insertRecalcAlways, this::removeRecalcAlways, this::sizeOfRecalcAlwaysArray);
        }
    }

    @Override
    public STTrueFalseBlank.Enum[] getRecalcAlwaysArray() {
        return (STTrueFalseBlank.Enum[])this.getEnumArray(PROPERTY_QNAME[58], STTrueFalseBlank.Enum[]::new);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public STTrueFalseBlank.Enum getRecalcAlwaysArray(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)((Object)this.get_store().find_element_user(PROPERTY_QNAME[58], i));
            if (target == null) {
                throw new IndexOutOfBoundsException();
            }
            return (STTrueFalseBlank.Enum)target.getEnumValue();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public List<STTrueFalseBlank> xgetRecalcAlwaysList() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return new JavaListXmlObject<STTrueFalseBlank>(this::xgetRecalcAlwaysArray, this::xsetRecalcAlwaysArray, this::insertNewRecalcAlways, this::removeRecalcAlways, this::sizeOfRecalcAlwaysArray);
        }
    }

    @Override
    public STTrueFalseBlank[] xgetRecalcAlwaysArray() {
        return (STTrueFalseBlank[])this.xgetArray(PROPERTY_QNAME[58], STTrueFalseBlank[]::new);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public STTrueFalseBlank xgetRecalcAlwaysArray(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            STTrueFalseBlank target = null;
            target = (STTrueFalseBlank)((Object)this.get_store().find_element_user(PROPERTY_QNAME[58], i));
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
    public int sizeOfRecalcAlwaysArray() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[58]);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void setRecalcAlwaysArray(STTrueFalseBlank.Enum[] recalcAlwaysArray) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.arraySetterHelper(recalcAlwaysArray, PROPERTY_QNAME[58]);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void setRecalcAlwaysArray(int i, STTrueFalseBlank.Enum recalcAlways) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)((Object)this.get_store().find_element_user(PROPERTY_QNAME[58], i));
            if (target == null) {
                throw new IndexOutOfBoundsException();
            }
            target.setEnumValue(recalcAlways);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void xsetRecalcAlwaysArray(STTrueFalseBlank[] recalcAlwaysArray) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.arraySetterHelper(recalcAlwaysArray, PROPERTY_QNAME[58]);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void xsetRecalcAlwaysArray(int i, STTrueFalseBlank recalcAlways) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            STTrueFalseBlank target = null;
            target = (STTrueFalseBlank)((Object)this.get_store().find_element_user(PROPERTY_QNAME[58], i));
            if (target == null) {
                throw new IndexOutOfBoundsException();
            }
            target.set(recalcAlways);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void insertRecalcAlways(int i, STTrueFalseBlank.Enum recalcAlways) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            SimpleValue target = (SimpleValue)((Object)this.get_store().insert_element_user(PROPERTY_QNAME[58], i));
            target.setEnumValue(recalcAlways);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void addRecalcAlways(STTrueFalseBlank.Enum recalcAlways) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)((Object)this.get_store().add_element_user(PROPERTY_QNAME[58]));
            target.setEnumValue(recalcAlways);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public STTrueFalseBlank insertNewRecalcAlways(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            STTrueFalseBlank target = null;
            target = (STTrueFalseBlank)((Object)this.get_store().insert_element_user(PROPERTY_QNAME[58], i));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public STTrueFalseBlank addNewRecalcAlways() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            STTrueFalseBlank target = null;
            target = (STTrueFalseBlank)((Object)this.get_store().add_element_user(PROPERTY_QNAME[58]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void removeRecalcAlways(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.get_store().remove_element(PROPERTY_QNAME[58], i);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public List<STTrueFalseBlank.Enum> getAutoScaleList() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return new JavaListObject<STTrueFalseBlank.Enum>(this::getAutoScaleArray, this::setAutoScaleArray, this::insertAutoScale, this::removeAutoScale, this::sizeOfAutoScaleArray);
        }
    }

    @Override
    public STTrueFalseBlank.Enum[] getAutoScaleArray() {
        return (STTrueFalseBlank.Enum[])this.getEnumArray(PROPERTY_QNAME[59], STTrueFalseBlank.Enum[]::new);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public STTrueFalseBlank.Enum getAutoScaleArray(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)((Object)this.get_store().find_element_user(PROPERTY_QNAME[59], i));
            if (target == null) {
                throw new IndexOutOfBoundsException();
            }
            return (STTrueFalseBlank.Enum)target.getEnumValue();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public List<STTrueFalseBlank> xgetAutoScaleList() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return new JavaListXmlObject<STTrueFalseBlank>(this::xgetAutoScaleArray, this::xsetAutoScaleArray, this::insertNewAutoScale, this::removeAutoScale, this::sizeOfAutoScaleArray);
        }
    }

    @Override
    public STTrueFalseBlank[] xgetAutoScaleArray() {
        return (STTrueFalseBlank[])this.xgetArray(PROPERTY_QNAME[59], STTrueFalseBlank[]::new);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public STTrueFalseBlank xgetAutoScaleArray(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            STTrueFalseBlank target = null;
            target = (STTrueFalseBlank)((Object)this.get_store().find_element_user(PROPERTY_QNAME[59], i));
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
    public int sizeOfAutoScaleArray() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[59]);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void setAutoScaleArray(STTrueFalseBlank.Enum[] autoScaleArray) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.arraySetterHelper(autoScaleArray, PROPERTY_QNAME[59]);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void setAutoScaleArray(int i, STTrueFalseBlank.Enum autoScale) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)((Object)this.get_store().find_element_user(PROPERTY_QNAME[59], i));
            if (target == null) {
                throw new IndexOutOfBoundsException();
            }
            target.setEnumValue(autoScale);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void xsetAutoScaleArray(STTrueFalseBlank[] autoScaleArray) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.arraySetterHelper(autoScaleArray, PROPERTY_QNAME[59]);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void xsetAutoScaleArray(int i, STTrueFalseBlank autoScale) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            STTrueFalseBlank target = null;
            target = (STTrueFalseBlank)((Object)this.get_store().find_element_user(PROPERTY_QNAME[59], i));
            if (target == null) {
                throw new IndexOutOfBoundsException();
            }
            target.set(autoScale);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void insertAutoScale(int i, STTrueFalseBlank.Enum autoScale) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            SimpleValue target = (SimpleValue)((Object)this.get_store().insert_element_user(PROPERTY_QNAME[59], i));
            target.setEnumValue(autoScale);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void addAutoScale(STTrueFalseBlank.Enum autoScale) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)((Object)this.get_store().add_element_user(PROPERTY_QNAME[59]));
            target.setEnumValue(autoScale);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public STTrueFalseBlank insertNewAutoScale(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            STTrueFalseBlank target = null;
            target = (STTrueFalseBlank)((Object)this.get_store().insert_element_user(PROPERTY_QNAME[59], i));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public STTrueFalseBlank addNewAutoScale() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            STTrueFalseBlank target = null;
            target = (STTrueFalseBlank)((Object)this.get_store().add_element_user(PROPERTY_QNAME[59]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void removeAutoScale(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.get_store().remove_element(PROPERTY_QNAME[59], i);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public List<STTrueFalseBlank.Enum> getDDEList() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return new JavaListObject<STTrueFalseBlank.Enum>(this::getDDEArray, this::setDDEArray, this::insertDDE, this::removeDDE, this::sizeOfDDEArray);
        }
    }

    @Override
    public STTrueFalseBlank.Enum[] getDDEArray() {
        return (STTrueFalseBlank.Enum[])this.getEnumArray(PROPERTY_QNAME[60], STTrueFalseBlank.Enum[]::new);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public STTrueFalseBlank.Enum getDDEArray(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)((Object)this.get_store().find_element_user(PROPERTY_QNAME[60], i));
            if (target == null) {
                throw new IndexOutOfBoundsException();
            }
            return (STTrueFalseBlank.Enum)target.getEnumValue();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public List<STTrueFalseBlank> xgetDDEList() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return new JavaListXmlObject<STTrueFalseBlank>(this::xgetDDEArray, this::xsetDDEArray, this::insertNewDDE, this::removeDDE, this::sizeOfDDEArray);
        }
    }

    @Override
    public STTrueFalseBlank[] xgetDDEArray() {
        return (STTrueFalseBlank[])this.xgetArray(PROPERTY_QNAME[60], STTrueFalseBlank[]::new);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public STTrueFalseBlank xgetDDEArray(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            STTrueFalseBlank target = null;
            target = (STTrueFalseBlank)((Object)this.get_store().find_element_user(PROPERTY_QNAME[60], i));
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
    public int sizeOfDDEArray() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[60]);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void setDDEArray(STTrueFalseBlank.Enum[] ddeArray) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.arraySetterHelper(ddeArray, PROPERTY_QNAME[60]);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void setDDEArray(int i, STTrueFalseBlank.Enum dde) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)((Object)this.get_store().find_element_user(PROPERTY_QNAME[60], i));
            if (target == null) {
                throw new IndexOutOfBoundsException();
            }
            target.setEnumValue(dde);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void xsetDDEArray(STTrueFalseBlank[] ddeArray) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.arraySetterHelper(ddeArray, PROPERTY_QNAME[60]);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void xsetDDEArray(int i, STTrueFalseBlank dde) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            STTrueFalseBlank target = null;
            target = (STTrueFalseBlank)((Object)this.get_store().find_element_user(PROPERTY_QNAME[60], i));
            if (target == null) {
                throw new IndexOutOfBoundsException();
            }
            target.set(dde);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void insertDDE(int i, STTrueFalseBlank.Enum dde) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            SimpleValue target = (SimpleValue)((Object)this.get_store().insert_element_user(PROPERTY_QNAME[60], i));
            target.setEnumValue(dde);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void addDDE(STTrueFalseBlank.Enum dde) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)((Object)this.get_store().add_element_user(PROPERTY_QNAME[60]));
            target.setEnumValue(dde);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public STTrueFalseBlank insertNewDDE(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            STTrueFalseBlank target = null;
            target = (STTrueFalseBlank)((Object)this.get_store().insert_element_user(PROPERTY_QNAME[60], i));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public STTrueFalseBlank addNewDDE() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            STTrueFalseBlank target = null;
            target = (STTrueFalseBlank)((Object)this.get_store().add_element_user(PROPERTY_QNAME[60]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void removeDDE(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.get_store().remove_element(PROPERTY_QNAME[60], i);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public List<STTrueFalseBlank.Enum> getUIObjList() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return new JavaListObject<STTrueFalseBlank.Enum>(this::getUIObjArray, this::setUIObjArray, this::insertUIObj, this::removeUIObj, this::sizeOfUIObjArray);
        }
    }

    @Override
    public STTrueFalseBlank.Enum[] getUIObjArray() {
        return (STTrueFalseBlank.Enum[])this.getEnumArray(PROPERTY_QNAME[61], STTrueFalseBlank.Enum[]::new);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public STTrueFalseBlank.Enum getUIObjArray(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)((Object)this.get_store().find_element_user(PROPERTY_QNAME[61], i));
            if (target == null) {
                throw new IndexOutOfBoundsException();
            }
            return (STTrueFalseBlank.Enum)target.getEnumValue();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public List<STTrueFalseBlank> xgetUIObjList() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return new JavaListXmlObject<STTrueFalseBlank>(this::xgetUIObjArray, this::xsetUIObjArray, this::insertNewUIObj, this::removeUIObj, this::sizeOfUIObjArray);
        }
    }

    @Override
    public STTrueFalseBlank[] xgetUIObjArray() {
        return (STTrueFalseBlank[])this.xgetArray(PROPERTY_QNAME[61], STTrueFalseBlank[]::new);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public STTrueFalseBlank xgetUIObjArray(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            STTrueFalseBlank target = null;
            target = (STTrueFalseBlank)((Object)this.get_store().find_element_user(PROPERTY_QNAME[61], i));
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
    public int sizeOfUIObjArray() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[61]);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void setUIObjArray(STTrueFalseBlank.Enum[] uiObjArray) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.arraySetterHelper(uiObjArray, PROPERTY_QNAME[61]);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void setUIObjArray(int i, STTrueFalseBlank.Enum uiObj) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)((Object)this.get_store().find_element_user(PROPERTY_QNAME[61], i));
            if (target == null) {
                throw new IndexOutOfBoundsException();
            }
            target.setEnumValue(uiObj);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void xsetUIObjArray(STTrueFalseBlank[] uiObjArray) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.arraySetterHelper(uiObjArray, PROPERTY_QNAME[61]);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void xsetUIObjArray(int i, STTrueFalseBlank uiObj) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            STTrueFalseBlank target = null;
            target = (STTrueFalseBlank)((Object)this.get_store().find_element_user(PROPERTY_QNAME[61], i));
            if (target == null) {
                throw new IndexOutOfBoundsException();
            }
            target.set(uiObj);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void insertUIObj(int i, STTrueFalseBlank.Enum uiObj) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            SimpleValue target = (SimpleValue)((Object)this.get_store().insert_element_user(PROPERTY_QNAME[61], i));
            target.setEnumValue(uiObj);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void addUIObj(STTrueFalseBlank.Enum uiObj) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)((Object)this.get_store().add_element_user(PROPERTY_QNAME[61]));
            target.setEnumValue(uiObj);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public STTrueFalseBlank insertNewUIObj(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            STTrueFalseBlank target = null;
            target = (STTrueFalseBlank)((Object)this.get_store().insert_element_user(PROPERTY_QNAME[61], i));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public STTrueFalseBlank addNewUIObj() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            STTrueFalseBlank target = null;
            target = (STTrueFalseBlank)((Object)this.get_store().add_element_user(PROPERTY_QNAME[61]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void removeUIObj(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.get_store().remove_element(PROPERTY_QNAME[61], i);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public List<String> getScriptTextList() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return new JavaListObject<String>(this::getScriptTextArray, this::setScriptTextArray, this::insertScriptText, this::removeScriptText, this::sizeOfScriptTextArray);
        }
    }

    @Override
    public String[] getScriptTextArray() {
        return this.getObjectArray(PROPERTY_QNAME[62], SimpleValue::getStringValue, String[]::new);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public String getScriptTextArray(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)((Object)this.get_store().find_element_user(PROPERTY_QNAME[62], i));
            if (target == null) {
                throw new IndexOutOfBoundsException();
            }
            return target.getStringValue();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public List<XmlString> xgetScriptTextList() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return new JavaListXmlObject<XmlString>(this::xgetScriptTextArray, this::xsetScriptTextArray, this::insertNewScriptText, this::removeScriptText, this::sizeOfScriptTextArray);
        }
    }

    @Override
    public XmlString[] xgetScriptTextArray() {
        return (XmlString[])this.xgetArray(PROPERTY_QNAME[62], XmlString[]::new);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public XmlString xgetScriptTextArray(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            XmlString target = null;
            target = (XmlString)((Object)this.get_store().find_element_user(PROPERTY_QNAME[62], i));
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
    public int sizeOfScriptTextArray() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[62]);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void setScriptTextArray(String[] scriptTextArray) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.arraySetterHelper(scriptTextArray, PROPERTY_QNAME[62]);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void setScriptTextArray(int i, String scriptText) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)((Object)this.get_store().find_element_user(PROPERTY_QNAME[62], i));
            if (target == null) {
                throw new IndexOutOfBoundsException();
            }
            target.setStringValue(scriptText);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void xsetScriptTextArray(XmlString[] scriptTextArray) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.arraySetterHelper(scriptTextArray, PROPERTY_QNAME[62]);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void xsetScriptTextArray(int i, XmlString scriptText) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            XmlString target = null;
            target = (XmlString)((Object)this.get_store().find_element_user(PROPERTY_QNAME[62], i));
            if (target == null) {
                throw new IndexOutOfBoundsException();
            }
            target.set(scriptText);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void insertScriptText(int i, String scriptText) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            SimpleValue target = (SimpleValue)((Object)this.get_store().insert_element_user(PROPERTY_QNAME[62], i));
            target.setStringValue(scriptText);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void addScriptText(String scriptText) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)((Object)this.get_store().add_element_user(PROPERTY_QNAME[62]));
            target.setStringValue(scriptText);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public XmlString insertNewScriptText(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            XmlString target = null;
            target = (XmlString)((Object)this.get_store().insert_element_user(PROPERTY_QNAME[62], i));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public XmlString addNewScriptText() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            XmlString target = null;
            target = (XmlString)((Object)this.get_store().add_element_user(PROPERTY_QNAME[62]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void removeScriptText(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.get_store().remove_element(PROPERTY_QNAME[62], i);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public List<String> getScriptExtendedList() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return new JavaListObject<String>(this::getScriptExtendedArray, this::setScriptExtendedArray, this::insertScriptExtended, this::removeScriptExtended, this::sizeOfScriptExtendedArray);
        }
    }

    @Override
    public String[] getScriptExtendedArray() {
        return this.getObjectArray(PROPERTY_QNAME[63], SimpleValue::getStringValue, String[]::new);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public String getScriptExtendedArray(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)((Object)this.get_store().find_element_user(PROPERTY_QNAME[63], i));
            if (target == null) {
                throw new IndexOutOfBoundsException();
            }
            return target.getStringValue();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public List<XmlString> xgetScriptExtendedList() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return new JavaListXmlObject<XmlString>(this::xgetScriptExtendedArray, this::xsetScriptExtendedArray, this::insertNewScriptExtended, this::removeScriptExtended, this::sizeOfScriptExtendedArray);
        }
    }

    @Override
    public XmlString[] xgetScriptExtendedArray() {
        return (XmlString[])this.xgetArray(PROPERTY_QNAME[63], XmlString[]::new);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public XmlString xgetScriptExtendedArray(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            XmlString target = null;
            target = (XmlString)((Object)this.get_store().find_element_user(PROPERTY_QNAME[63], i));
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
    public int sizeOfScriptExtendedArray() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[63]);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void setScriptExtendedArray(String[] scriptExtendedArray) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.arraySetterHelper(scriptExtendedArray, PROPERTY_QNAME[63]);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void setScriptExtendedArray(int i, String scriptExtended) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)((Object)this.get_store().find_element_user(PROPERTY_QNAME[63], i));
            if (target == null) {
                throw new IndexOutOfBoundsException();
            }
            target.setStringValue(scriptExtended);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void xsetScriptExtendedArray(XmlString[] scriptExtendedArray) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.arraySetterHelper(scriptExtendedArray, PROPERTY_QNAME[63]);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void xsetScriptExtendedArray(int i, XmlString scriptExtended) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            XmlString target = null;
            target = (XmlString)((Object)this.get_store().find_element_user(PROPERTY_QNAME[63], i));
            if (target == null) {
                throw new IndexOutOfBoundsException();
            }
            target.set(scriptExtended);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void insertScriptExtended(int i, String scriptExtended) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            SimpleValue target = (SimpleValue)((Object)this.get_store().insert_element_user(PROPERTY_QNAME[63], i));
            target.setStringValue(scriptExtended);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void addScriptExtended(String scriptExtended) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)((Object)this.get_store().add_element_user(PROPERTY_QNAME[63]));
            target.setStringValue(scriptExtended);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public XmlString insertNewScriptExtended(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            XmlString target = null;
            target = (XmlString)((Object)this.get_store().insert_element_user(PROPERTY_QNAME[63], i));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public XmlString addNewScriptExtended() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            XmlString target = null;
            target = (XmlString)((Object)this.get_store().add_element_user(PROPERTY_QNAME[63]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void removeScriptExtended(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.get_store().remove_element(PROPERTY_QNAME[63], i);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public List<BigInteger> getScriptLanguageList() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return new JavaListObject<BigInteger>(this::getScriptLanguageArray, this::setScriptLanguageArray, this::insertScriptLanguage, this::removeScriptLanguage, this::sizeOfScriptLanguageArray);
        }
    }

    @Override
    public BigInteger[] getScriptLanguageArray() {
        return this.getObjectArray(PROPERTY_QNAME[64], SimpleValue::getBigIntegerValue, BigInteger[]::new);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public BigInteger getScriptLanguageArray(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)((Object)this.get_store().find_element_user(PROPERTY_QNAME[64], i));
            if (target == null) {
                throw new IndexOutOfBoundsException();
            }
            return target.getBigIntegerValue();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public List<XmlNonNegativeInteger> xgetScriptLanguageList() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return new JavaListXmlObject<XmlNonNegativeInteger>(this::xgetScriptLanguageArray, this::xsetScriptLanguageArray, this::insertNewScriptLanguage, this::removeScriptLanguage, this::sizeOfScriptLanguageArray);
        }
    }

    @Override
    public XmlNonNegativeInteger[] xgetScriptLanguageArray() {
        return (XmlNonNegativeInteger[])this.xgetArray(PROPERTY_QNAME[64], XmlNonNegativeInteger[]::new);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public XmlNonNegativeInteger xgetScriptLanguageArray(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            XmlNonNegativeInteger target = null;
            target = (XmlNonNegativeInteger)((Object)this.get_store().find_element_user(PROPERTY_QNAME[64], i));
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
    public int sizeOfScriptLanguageArray() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[64]);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void setScriptLanguageArray(BigInteger[] scriptLanguageArray) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.arraySetterHelper(scriptLanguageArray, PROPERTY_QNAME[64]);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void setScriptLanguageArray(int i, BigInteger scriptLanguage) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)((Object)this.get_store().find_element_user(PROPERTY_QNAME[64], i));
            if (target == null) {
                throw new IndexOutOfBoundsException();
            }
            target.setBigIntegerValue(scriptLanguage);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void xsetScriptLanguageArray(XmlNonNegativeInteger[] scriptLanguageArray) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.arraySetterHelper(scriptLanguageArray, PROPERTY_QNAME[64]);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void xsetScriptLanguageArray(int i, XmlNonNegativeInteger scriptLanguage) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            XmlNonNegativeInteger target = null;
            target = (XmlNonNegativeInteger)((Object)this.get_store().find_element_user(PROPERTY_QNAME[64], i));
            if (target == null) {
                throw new IndexOutOfBoundsException();
            }
            target.set(scriptLanguage);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void insertScriptLanguage(int i, BigInteger scriptLanguage) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            SimpleValue target = (SimpleValue)((Object)this.get_store().insert_element_user(PROPERTY_QNAME[64], i));
            target.setBigIntegerValue(scriptLanguage);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void addScriptLanguage(BigInteger scriptLanguage) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)((Object)this.get_store().add_element_user(PROPERTY_QNAME[64]));
            target.setBigIntegerValue(scriptLanguage);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public XmlNonNegativeInteger insertNewScriptLanguage(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            XmlNonNegativeInteger target = null;
            target = (XmlNonNegativeInteger)((Object)this.get_store().insert_element_user(PROPERTY_QNAME[64], i));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public XmlNonNegativeInteger addNewScriptLanguage() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            XmlNonNegativeInteger target = null;
            target = (XmlNonNegativeInteger)((Object)this.get_store().add_element_user(PROPERTY_QNAME[64]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void removeScriptLanguage(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.get_store().remove_element(PROPERTY_QNAME[64], i);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public List<BigInteger> getScriptLocationList() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return new JavaListObject<BigInteger>(this::getScriptLocationArray, this::setScriptLocationArray, this::insertScriptLocation, this::removeScriptLocation, this::sizeOfScriptLocationArray);
        }
    }

    @Override
    public BigInteger[] getScriptLocationArray() {
        return this.getObjectArray(PROPERTY_QNAME[65], SimpleValue::getBigIntegerValue, BigInteger[]::new);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public BigInteger getScriptLocationArray(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)((Object)this.get_store().find_element_user(PROPERTY_QNAME[65], i));
            if (target == null) {
                throw new IndexOutOfBoundsException();
            }
            return target.getBigIntegerValue();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public List<XmlNonNegativeInteger> xgetScriptLocationList() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return new JavaListXmlObject<XmlNonNegativeInteger>(this::xgetScriptLocationArray, this::xsetScriptLocationArray, this::insertNewScriptLocation, this::removeScriptLocation, this::sizeOfScriptLocationArray);
        }
    }

    @Override
    public XmlNonNegativeInteger[] xgetScriptLocationArray() {
        return (XmlNonNegativeInteger[])this.xgetArray(PROPERTY_QNAME[65], XmlNonNegativeInteger[]::new);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public XmlNonNegativeInteger xgetScriptLocationArray(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            XmlNonNegativeInteger target = null;
            target = (XmlNonNegativeInteger)((Object)this.get_store().find_element_user(PROPERTY_QNAME[65], i));
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
    public int sizeOfScriptLocationArray() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[65]);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void setScriptLocationArray(BigInteger[] scriptLocationArray) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.arraySetterHelper(scriptLocationArray, PROPERTY_QNAME[65]);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void setScriptLocationArray(int i, BigInteger scriptLocation) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)((Object)this.get_store().find_element_user(PROPERTY_QNAME[65], i));
            if (target == null) {
                throw new IndexOutOfBoundsException();
            }
            target.setBigIntegerValue(scriptLocation);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void xsetScriptLocationArray(XmlNonNegativeInteger[] scriptLocationArray) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.arraySetterHelper(scriptLocationArray, PROPERTY_QNAME[65]);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void xsetScriptLocationArray(int i, XmlNonNegativeInteger scriptLocation) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            XmlNonNegativeInteger target = null;
            target = (XmlNonNegativeInteger)((Object)this.get_store().find_element_user(PROPERTY_QNAME[65], i));
            if (target == null) {
                throw new IndexOutOfBoundsException();
            }
            target.set(scriptLocation);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void insertScriptLocation(int i, BigInteger scriptLocation) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            SimpleValue target = (SimpleValue)((Object)this.get_store().insert_element_user(PROPERTY_QNAME[65], i));
            target.setBigIntegerValue(scriptLocation);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void addScriptLocation(BigInteger scriptLocation) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)((Object)this.get_store().add_element_user(PROPERTY_QNAME[65]));
            target.setBigIntegerValue(scriptLocation);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public XmlNonNegativeInteger insertNewScriptLocation(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            XmlNonNegativeInteger target = null;
            target = (XmlNonNegativeInteger)((Object)this.get_store().insert_element_user(PROPERTY_QNAME[65], i));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public XmlNonNegativeInteger addNewScriptLocation() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            XmlNonNegativeInteger target = null;
            target = (XmlNonNegativeInteger)((Object)this.get_store().add_element_user(PROPERTY_QNAME[65]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void removeScriptLocation(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.get_store().remove_element(PROPERTY_QNAME[65], i);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public List<String> getFmlaTxbxList() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return new JavaListObject<String>(this::getFmlaTxbxArray, this::setFmlaTxbxArray, this::insertFmlaTxbx, this::removeFmlaTxbx, this::sizeOfFmlaTxbxArray);
        }
    }

    @Override
    public String[] getFmlaTxbxArray() {
        return this.getObjectArray(PROPERTY_QNAME[66], SimpleValue::getStringValue, String[]::new);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public String getFmlaTxbxArray(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)((Object)this.get_store().find_element_user(PROPERTY_QNAME[66], i));
            if (target == null) {
                throw new IndexOutOfBoundsException();
            }
            return target.getStringValue();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public List<XmlString> xgetFmlaTxbxList() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return new JavaListXmlObject<XmlString>(this::xgetFmlaTxbxArray, this::xsetFmlaTxbxArray, this::insertNewFmlaTxbx, this::removeFmlaTxbx, this::sizeOfFmlaTxbxArray);
        }
    }

    @Override
    public XmlString[] xgetFmlaTxbxArray() {
        return (XmlString[])this.xgetArray(PROPERTY_QNAME[66], XmlString[]::new);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public XmlString xgetFmlaTxbxArray(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            XmlString target = null;
            target = (XmlString)((Object)this.get_store().find_element_user(PROPERTY_QNAME[66], i));
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
    public int sizeOfFmlaTxbxArray() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[66]);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void setFmlaTxbxArray(String[] fmlaTxbxArray) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.arraySetterHelper(fmlaTxbxArray, PROPERTY_QNAME[66]);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void setFmlaTxbxArray(int i, String fmlaTxbx) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)((Object)this.get_store().find_element_user(PROPERTY_QNAME[66], i));
            if (target == null) {
                throw new IndexOutOfBoundsException();
            }
            target.setStringValue(fmlaTxbx);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void xsetFmlaTxbxArray(XmlString[] fmlaTxbxArray) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.arraySetterHelper(fmlaTxbxArray, PROPERTY_QNAME[66]);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void xsetFmlaTxbxArray(int i, XmlString fmlaTxbx) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            XmlString target = null;
            target = (XmlString)((Object)this.get_store().find_element_user(PROPERTY_QNAME[66], i));
            if (target == null) {
                throw new IndexOutOfBoundsException();
            }
            target.set(fmlaTxbx);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void insertFmlaTxbx(int i, String fmlaTxbx) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            SimpleValue target = (SimpleValue)((Object)this.get_store().insert_element_user(PROPERTY_QNAME[66], i));
            target.setStringValue(fmlaTxbx);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void addFmlaTxbx(String fmlaTxbx) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)((Object)this.get_store().add_element_user(PROPERTY_QNAME[66]));
            target.setStringValue(fmlaTxbx);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public XmlString insertNewFmlaTxbx(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            XmlString target = null;
            target = (XmlString)((Object)this.get_store().insert_element_user(PROPERTY_QNAME[66], i));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public XmlString addNewFmlaTxbx() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            XmlString target = null;
            target = (XmlString)((Object)this.get_store().add_element_user(PROPERTY_QNAME[66]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void removeFmlaTxbx(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.get_store().remove_element(PROPERTY_QNAME[66], i);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public STObjectType.Enum getObjectType() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[67]));
            return target == null ? null : (STObjectType.Enum)target.getEnumValue();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public STObjectType xgetObjectType() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            STObjectType target = null;
            target = (STObjectType)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[67]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void setObjectType(STObjectType.Enum objectType) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[67]));
            if (target == null) {
                target = (SimpleValue)((Object)this.get_store().add_attribute_user(PROPERTY_QNAME[67]));
            }
            target.setEnumValue(objectType);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void xsetObjectType(STObjectType objectType) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            STObjectType target = null;
            target = (STObjectType)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[67]));
            if (target == null) {
                target = (STObjectType)((Object)this.get_store().add_attribute_user(PROPERTY_QNAME[67]));
            }
            target.set(objectType);
        }
    }
}


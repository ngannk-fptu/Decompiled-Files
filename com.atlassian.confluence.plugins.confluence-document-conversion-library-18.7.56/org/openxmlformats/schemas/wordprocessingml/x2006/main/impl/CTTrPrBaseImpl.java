/*
 * Decompiled with CFR 0.152.
 */
package org.openxmlformats.schemas.wordprocessingml.x2006.main.impl;

import java.util.List;
import javax.xml.namespace.QName;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.impl.values.JavaListXmlObject;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTCnf;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTDecimalNumber;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTHeight;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTJcTable;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTOnOff;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTblWidth;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTrPrBase;

public class CTTrPrBaseImpl
extends XmlComplexContentImpl
implements CTTrPrBase {
    private static final long serialVersionUID = 1L;
    private static final QName[] PROPERTY_QNAME = new QName[]{new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "cnfStyle"), new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "divId"), new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "gridBefore"), new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "gridAfter"), new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "wBefore"), new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "wAfter"), new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "cantSplit"), new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "trHeight"), new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "tblHeader"), new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "tblCellSpacing"), new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "jc"), new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "hidden")};

    public CTTrPrBaseImpl(SchemaType sType) {
        super(sType);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public List<CTCnf> getCnfStyleList() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return new JavaListXmlObject<CTCnf>(this::getCnfStyleArray, this::setCnfStyleArray, this::insertNewCnfStyle, this::removeCnfStyle, this::sizeOfCnfStyleArray);
        }
    }

    @Override
    public CTCnf[] getCnfStyleArray() {
        return (CTCnf[])this.getXmlObjectArray(PROPERTY_QNAME[0], new CTCnf[0]);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTCnf getCnfStyleArray(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTCnf target = null;
            target = (CTCnf)((Object)this.get_store().find_element_user(PROPERTY_QNAME[0], i));
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
    public int sizeOfCnfStyleArray() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[0]);
        }
    }

    @Override
    public void setCnfStyleArray(CTCnf[] cnfStyleArray) {
        this.check_orphaned();
        this.arraySetterHelper(cnfStyleArray, PROPERTY_QNAME[0]);
    }

    @Override
    public void setCnfStyleArray(int i, CTCnf cnfStyle) {
        this.generatedSetterHelperImpl(cnfStyle, PROPERTY_QNAME[0], i, (short)2);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTCnf insertNewCnfStyle(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTCnf target = null;
            target = (CTCnf)((Object)this.get_store().insert_element_user(PROPERTY_QNAME[0], i));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTCnf addNewCnfStyle() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTCnf target = null;
            target = (CTCnf)((Object)this.get_store().add_element_user(PROPERTY_QNAME[0]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void removeCnfStyle(int i) {
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
    public List<CTDecimalNumber> getDivIdList() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return new JavaListXmlObject<CTDecimalNumber>(this::getDivIdArray, this::setDivIdArray, this::insertNewDivId, this::removeDivId, this::sizeOfDivIdArray);
        }
    }

    @Override
    public CTDecimalNumber[] getDivIdArray() {
        return (CTDecimalNumber[])this.getXmlObjectArray(PROPERTY_QNAME[1], new CTDecimalNumber[0]);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTDecimalNumber getDivIdArray(int i) {
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
    public int sizeOfDivIdArray() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[1]);
        }
    }

    @Override
    public void setDivIdArray(CTDecimalNumber[] divIdArray) {
        this.check_orphaned();
        this.arraySetterHelper(divIdArray, PROPERTY_QNAME[1]);
    }

    @Override
    public void setDivIdArray(int i, CTDecimalNumber divId) {
        this.generatedSetterHelperImpl(divId, PROPERTY_QNAME[1], i, (short)2);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTDecimalNumber insertNewDivId(int i) {
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
    public CTDecimalNumber addNewDivId() {
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
    public void removeDivId(int i) {
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
    public List<CTDecimalNumber> getGridBeforeList() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return new JavaListXmlObject<CTDecimalNumber>(this::getGridBeforeArray, this::setGridBeforeArray, this::insertNewGridBefore, this::removeGridBefore, this::sizeOfGridBeforeArray);
        }
    }

    @Override
    public CTDecimalNumber[] getGridBeforeArray() {
        return (CTDecimalNumber[])this.getXmlObjectArray(PROPERTY_QNAME[2], new CTDecimalNumber[0]);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTDecimalNumber getGridBeforeArray(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTDecimalNumber target = null;
            target = (CTDecimalNumber)((Object)this.get_store().find_element_user(PROPERTY_QNAME[2], i));
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
    public int sizeOfGridBeforeArray() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[2]);
        }
    }

    @Override
    public void setGridBeforeArray(CTDecimalNumber[] gridBeforeArray) {
        this.check_orphaned();
        this.arraySetterHelper(gridBeforeArray, PROPERTY_QNAME[2]);
    }

    @Override
    public void setGridBeforeArray(int i, CTDecimalNumber gridBefore) {
        this.generatedSetterHelperImpl(gridBefore, PROPERTY_QNAME[2], i, (short)2);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTDecimalNumber insertNewGridBefore(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTDecimalNumber target = null;
            target = (CTDecimalNumber)((Object)this.get_store().insert_element_user(PROPERTY_QNAME[2], i));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTDecimalNumber addNewGridBefore() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTDecimalNumber target = null;
            target = (CTDecimalNumber)((Object)this.get_store().add_element_user(PROPERTY_QNAME[2]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void removeGridBefore(int i) {
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
    public List<CTDecimalNumber> getGridAfterList() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return new JavaListXmlObject<CTDecimalNumber>(this::getGridAfterArray, this::setGridAfterArray, this::insertNewGridAfter, this::removeGridAfter, this::sizeOfGridAfterArray);
        }
    }

    @Override
    public CTDecimalNumber[] getGridAfterArray() {
        return (CTDecimalNumber[])this.getXmlObjectArray(PROPERTY_QNAME[3], new CTDecimalNumber[0]);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTDecimalNumber getGridAfterArray(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTDecimalNumber target = null;
            target = (CTDecimalNumber)((Object)this.get_store().find_element_user(PROPERTY_QNAME[3], i));
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
    public int sizeOfGridAfterArray() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[3]);
        }
    }

    @Override
    public void setGridAfterArray(CTDecimalNumber[] gridAfterArray) {
        this.check_orphaned();
        this.arraySetterHelper(gridAfterArray, PROPERTY_QNAME[3]);
    }

    @Override
    public void setGridAfterArray(int i, CTDecimalNumber gridAfter) {
        this.generatedSetterHelperImpl(gridAfter, PROPERTY_QNAME[3], i, (short)2);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTDecimalNumber insertNewGridAfter(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTDecimalNumber target = null;
            target = (CTDecimalNumber)((Object)this.get_store().insert_element_user(PROPERTY_QNAME[3], i));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTDecimalNumber addNewGridAfter() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTDecimalNumber target = null;
            target = (CTDecimalNumber)((Object)this.get_store().add_element_user(PROPERTY_QNAME[3]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void removeGridAfter(int i) {
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
    public List<CTTblWidth> getWBeforeList() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return new JavaListXmlObject<CTTblWidth>(this::getWBeforeArray, this::setWBeforeArray, this::insertNewWBefore, this::removeWBefore, this::sizeOfWBeforeArray);
        }
    }

    @Override
    public CTTblWidth[] getWBeforeArray() {
        return (CTTblWidth[])this.getXmlObjectArray(PROPERTY_QNAME[4], new CTTblWidth[0]);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTTblWidth getWBeforeArray(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTTblWidth target = null;
            target = (CTTblWidth)((Object)this.get_store().find_element_user(PROPERTY_QNAME[4], i));
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
    public int sizeOfWBeforeArray() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[4]);
        }
    }

    @Override
    public void setWBeforeArray(CTTblWidth[] wBeforeArray) {
        this.check_orphaned();
        this.arraySetterHelper(wBeforeArray, PROPERTY_QNAME[4]);
    }

    @Override
    public void setWBeforeArray(int i, CTTblWidth wBefore) {
        this.generatedSetterHelperImpl(wBefore, PROPERTY_QNAME[4], i, (short)2);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTTblWidth insertNewWBefore(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTTblWidth target = null;
            target = (CTTblWidth)((Object)this.get_store().insert_element_user(PROPERTY_QNAME[4], i));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTTblWidth addNewWBefore() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTTblWidth target = null;
            target = (CTTblWidth)((Object)this.get_store().add_element_user(PROPERTY_QNAME[4]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void removeWBefore(int i) {
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
    public List<CTTblWidth> getWAfterList() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return new JavaListXmlObject<CTTblWidth>(this::getWAfterArray, this::setWAfterArray, this::insertNewWAfter, this::removeWAfter, this::sizeOfWAfterArray);
        }
    }

    @Override
    public CTTblWidth[] getWAfterArray() {
        return (CTTblWidth[])this.getXmlObjectArray(PROPERTY_QNAME[5], new CTTblWidth[0]);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTTblWidth getWAfterArray(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTTblWidth target = null;
            target = (CTTblWidth)((Object)this.get_store().find_element_user(PROPERTY_QNAME[5], i));
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
    public int sizeOfWAfterArray() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[5]);
        }
    }

    @Override
    public void setWAfterArray(CTTblWidth[] wAfterArray) {
        this.check_orphaned();
        this.arraySetterHelper(wAfterArray, PROPERTY_QNAME[5]);
    }

    @Override
    public void setWAfterArray(int i, CTTblWidth wAfter) {
        this.generatedSetterHelperImpl(wAfter, PROPERTY_QNAME[5], i, (short)2);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTTblWidth insertNewWAfter(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTTblWidth target = null;
            target = (CTTblWidth)((Object)this.get_store().insert_element_user(PROPERTY_QNAME[5], i));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTTblWidth addNewWAfter() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTTblWidth target = null;
            target = (CTTblWidth)((Object)this.get_store().add_element_user(PROPERTY_QNAME[5]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void removeWAfter(int i) {
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
    public List<CTOnOff> getCantSplitList() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return new JavaListXmlObject<CTOnOff>(this::getCantSplitArray, this::setCantSplitArray, this::insertNewCantSplit, this::removeCantSplit, this::sizeOfCantSplitArray);
        }
    }

    @Override
    public CTOnOff[] getCantSplitArray() {
        return (CTOnOff[])this.getXmlObjectArray(PROPERTY_QNAME[6], new CTOnOff[0]);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTOnOff getCantSplitArray(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTOnOff target = null;
            target = (CTOnOff)((Object)this.get_store().find_element_user(PROPERTY_QNAME[6], i));
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
    public int sizeOfCantSplitArray() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[6]);
        }
    }

    @Override
    public void setCantSplitArray(CTOnOff[] cantSplitArray) {
        this.check_orphaned();
        this.arraySetterHelper(cantSplitArray, PROPERTY_QNAME[6]);
    }

    @Override
    public void setCantSplitArray(int i, CTOnOff cantSplit) {
        this.generatedSetterHelperImpl(cantSplit, PROPERTY_QNAME[6], i, (short)2);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTOnOff insertNewCantSplit(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTOnOff target = null;
            target = (CTOnOff)((Object)this.get_store().insert_element_user(PROPERTY_QNAME[6], i));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTOnOff addNewCantSplit() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTOnOff target = null;
            target = (CTOnOff)((Object)this.get_store().add_element_user(PROPERTY_QNAME[6]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void removeCantSplit(int i) {
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
    public List<CTHeight> getTrHeightList() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return new JavaListXmlObject<CTHeight>(this::getTrHeightArray, this::setTrHeightArray, this::insertNewTrHeight, this::removeTrHeight, this::sizeOfTrHeightArray);
        }
    }

    @Override
    public CTHeight[] getTrHeightArray() {
        return (CTHeight[])this.getXmlObjectArray(PROPERTY_QNAME[7], new CTHeight[0]);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTHeight getTrHeightArray(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTHeight target = null;
            target = (CTHeight)((Object)this.get_store().find_element_user(PROPERTY_QNAME[7], i));
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
    public int sizeOfTrHeightArray() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[7]);
        }
    }

    @Override
    public void setTrHeightArray(CTHeight[] trHeightArray) {
        this.check_orphaned();
        this.arraySetterHelper(trHeightArray, PROPERTY_QNAME[7]);
    }

    @Override
    public void setTrHeightArray(int i, CTHeight trHeight) {
        this.generatedSetterHelperImpl(trHeight, PROPERTY_QNAME[7], i, (short)2);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTHeight insertNewTrHeight(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTHeight target = null;
            target = (CTHeight)((Object)this.get_store().insert_element_user(PROPERTY_QNAME[7], i));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTHeight addNewTrHeight() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTHeight target = null;
            target = (CTHeight)((Object)this.get_store().add_element_user(PROPERTY_QNAME[7]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void removeTrHeight(int i) {
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
    public List<CTOnOff> getTblHeaderList() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return new JavaListXmlObject<CTOnOff>(this::getTblHeaderArray, this::setTblHeaderArray, this::insertNewTblHeader, this::removeTblHeader, this::sizeOfTblHeaderArray);
        }
    }

    @Override
    public CTOnOff[] getTblHeaderArray() {
        return (CTOnOff[])this.getXmlObjectArray(PROPERTY_QNAME[8], new CTOnOff[0]);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTOnOff getTblHeaderArray(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTOnOff target = null;
            target = (CTOnOff)((Object)this.get_store().find_element_user(PROPERTY_QNAME[8], i));
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
    public int sizeOfTblHeaderArray() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[8]);
        }
    }

    @Override
    public void setTblHeaderArray(CTOnOff[] tblHeaderArray) {
        this.check_orphaned();
        this.arraySetterHelper(tblHeaderArray, PROPERTY_QNAME[8]);
    }

    @Override
    public void setTblHeaderArray(int i, CTOnOff tblHeader) {
        this.generatedSetterHelperImpl(tblHeader, PROPERTY_QNAME[8], i, (short)2);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTOnOff insertNewTblHeader(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTOnOff target = null;
            target = (CTOnOff)((Object)this.get_store().insert_element_user(PROPERTY_QNAME[8], i));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTOnOff addNewTblHeader() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTOnOff target = null;
            target = (CTOnOff)((Object)this.get_store().add_element_user(PROPERTY_QNAME[8]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void removeTblHeader(int i) {
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
    public List<CTTblWidth> getTblCellSpacingList() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return new JavaListXmlObject<CTTblWidth>(this::getTblCellSpacingArray, this::setTblCellSpacingArray, this::insertNewTblCellSpacing, this::removeTblCellSpacing, this::sizeOfTblCellSpacingArray);
        }
    }

    @Override
    public CTTblWidth[] getTblCellSpacingArray() {
        return (CTTblWidth[])this.getXmlObjectArray(PROPERTY_QNAME[9], new CTTblWidth[0]);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTTblWidth getTblCellSpacingArray(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTTblWidth target = null;
            target = (CTTblWidth)((Object)this.get_store().find_element_user(PROPERTY_QNAME[9], i));
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
    public int sizeOfTblCellSpacingArray() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[9]);
        }
    }

    @Override
    public void setTblCellSpacingArray(CTTblWidth[] tblCellSpacingArray) {
        this.check_orphaned();
        this.arraySetterHelper(tblCellSpacingArray, PROPERTY_QNAME[9]);
    }

    @Override
    public void setTblCellSpacingArray(int i, CTTblWidth tblCellSpacing) {
        this.generatedSetterHelperImpl(tblCellSpacing, PROPERTY_QNAME[9], i, (short)2);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTTblWidth insertNewTblCellSpacing(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTTblWidth target = null;
            target = (CTTblWidth)((Object)this.get_store().insert_element_user(PROPERTY_QNAME[9], i));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTTblWidth addNewTblCellSpacing() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTTblWidth target = null;
            target = (CTTblWidth)((Object)this.get_store().add_element_user(PROPERTY_QNAME[9]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void removeTblCellSpacing(int i) {
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
    public List<CTJcTable> getJcList() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return new JavaListXmlObject<CTJcTable>(this::getJcArray, this::setJcArray, this::insertNewJc, this::removeJc, this::sizeOfJcArray);
        }
    }

    @Override
    public CTJcTable[] getJcArray() {
        return (CTJcTable[])this.getXmlObjectArray(PROPERTY_QNAME[10], new CTJcTable[0]);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTJcTable getJcArray(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTJcTable target = null;
            target = (CTJcTable)((Object)this.get_store().find_element_user(PROPERTY_QNAME[10], i));
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
    public int sizeOfJcArray() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[10]);
        }
    }

    @Override
    public void setJcArray(CTJcTable[] jcArray) {
        this.check_orphaned();
        this.arraySetterHelper(jcArray, PROPERTY_QNAME[10]);
    }

    @Override
    public void setJcArray(int i, CTJcTable jc) {
        this.generatedSetterHelperImpl(jc, PROPERTY_QNAME[10], i, (short)2);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTJcTable insertNewJc(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTJcTable target = null;
            target = (CTJcTable)((Object)this.get_store().insert_element_user(PROPERTY_QNAME[10], i));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTJcTable addNewJc() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTJcTable target = null;
            target = (CTJcTable)((Object)this.get_store().add_element_user(PROPERTY_QNAME[10]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void removeJc(int i) {
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
    public List<CTOnOff> getHiddenList() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return new JavaListXmlObject<CTOnOff>(this::getHiddenArray, this::setHiddenArray, this::insertNewHidden, this::removeHidden, this::sizeOfHiddenArray);
        }
    }

    @Override
    public CTOnOff[] getHiddenArray() {
        return (CTOnOff[])this.getXmlObjectArray(PROPERTY_QNAME[11], new CTOnOff[0]);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTOnOff getHiddenArray(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTOnOff target = null;
            target = (CTOnOff)((Object)this.get_store().find_element_user(PROPERTY_QNAME[11], i));
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
    public int sizeOfHiddenArray() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[11]);
        }
    }

    @Override
    public void setHiddenArray(CTOnOff[] hiddenArray) {
        this.check_orphaned();
        this.arraySetterHelper(hiddenArray, PROPERTY_QNAME[11]);
    }

    @Override
    public void setHiddenArray(int i, CTOnOff hidden) {
        this.generatedSetterHelperImpl(hidden, PROPERTY_QNAME[11], i, (short)2);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTOnOff insertNewHidden(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTOnOff target = null;
            target = (CTOnOff)((Object)this.get_store().insert_element_user(PROPERTY_QNAME[11], i));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTOnOff addNewHidden() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTOnOff target = null;
            target = (CTOnOff)((Object)this.get_store().add_element_user(PROPERTY_QNAME[11]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void removeHidden(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.get_store().remove_element(PROPERTY_QNAME[11], i);
        }
    }
}


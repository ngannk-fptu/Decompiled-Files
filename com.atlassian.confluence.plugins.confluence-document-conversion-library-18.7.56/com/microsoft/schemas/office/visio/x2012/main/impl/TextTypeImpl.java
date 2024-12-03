/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.microsoft.schemas.office.visio.x2012.main.CpType
 *  com.microsoft.schemas.office.visio.x2012.main.FldType
 *  com.microsoft.schemas.office.visio.x2012.main.PpType
 *  com.microsoft.schemas.office.visio.x2012.main.TpType
 */
package com.microsoft.schemas.office.visio.x2012.main.impl;

import com.microsoft.schemas.office.visio.x2012.main.CpType;
import com.microsoft.schemas.office.visio.x2012.main.FldType;
import com.microsoft.schemas.office.visio.x2012.main.PpType;
import com.microsoft.schemas.office.visio.x2012.main.TextType;
import com.microsoft.schemas.office.visio.x2012.main.TpType;
import java.util.List;
import javax.xml.namespace.QName;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.impl.values.JavaListXmlObject;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;

public class TextTypeImpl
extends XmlComplexContentImpl
implements TextType {
    private static final long serialVersionUID = 1L;
    private static final QName[] PROPERTY_QNAME = new QName[]{new QName("http://schemas.microsoft.com/office/visio/2012/main", "cp"), new QName("http://schemas.microsoft.com/office/visio/2012/main", "pp"), new QName("http://schemas.microsoft.com/office/visio/2012/main", "tp"), new QName("http://schemas.microsoft.com/office/visio/2012/main", "fld")};

    public TextTypeImpl(SchemaType sType) {
        super(sType);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public List<CpType> getCpList() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return new JavaListXmlObject<CpType>(this::getCpArray, this::setCpArray, this::insertNewCp, this::removeCp, this::sizeOfCpArray);
        }
    }

    @Override
    public CpType[] getCpArray() {
        return (CpType[])this.getXmlObjectArray(PROPERTY_QNAME[0], (XmlObject[])new CpType[0]);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CpType getCpArray(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CpType target = null;
            target = (CpType)this.get_store().find_element_user(PROPERTY_QNAME[0], i);
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
    public int sizeOfCpArray() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[0]);
        }
    }

    @Override
    public void setCpArray(CpType[] cpArray) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])cpArray, PROPERTY_QNAME[0]);
    }

    @Override
    public void setCpArray(int i, CpType cp) {
        this.generatedSetterHelperImpl((XmlObject)cp, PROPERTY_QNAME[0], i, (short)2);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CpType insertNewCp(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CpType target = null;
            target = (CpType)this.get_store().insert_element_user(PROPERTY_QNAME[0], i);
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CpType addNewCp() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CpType target = null;
            target = (CpType)this.get_store().add_element_user(PROPERTY_QNAME[0]);
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void removeCp(int i) {
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
    public List<PpType> getPpList() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return new JavaListXmlObject<PpType>(this::getPpArray, this::setPpArray, this::insertNewPp, this::removePp, this::sizeOfPpArray);
        }
    }

    @Override
    public PpType[] getPpArray() {
        return (PpType[])this.getXmlObjectArray(PROPERTY_QNAME[1], (XmlObject[])new PpType[0]);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public PpType getPpArray(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            PpType target = null;
            target = (PpType)this.get_store().find_element_user(PROPERTY_QNAME[1], i);
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
    public int sizeOfPpArray() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[1]);
        }
    }

    @Override
    public void setPpArray(PpType[] ppArray) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])ppArray, PROPERTY_QNAME[1]);
    }

    @Override
    public void setPpArray(int i, PpType pp) {
        this.generatedSetterHelperImpl((XmlObject)pp, PROPERTY_QNAME[1], i, (short)2);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public PpType insertNewPp(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            PpType target = null;
            target = (PpType)this.get_store().insert_element_user(PROPERTY_QNAME[1], i);
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public PpType addNewPp() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            PpType target = null;
            target = (PpType)this.get_store().add_element_user(PROPERTY_QNAME[1]);
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void removePp(int i) {
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
    public List<TpType> getTpList() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return new JavaListXmlObject<TpType>(this::getTpArray, this::setTpArray, this::insertNewTp, this::removeTp, this::sizeOfTpArray);
        }
    }

    @Override
    public TpType[] getTpArray() {
        return (TpType[])this.getXmlObjectArray(PROPERTY_QNAME[2], (XmlObject[])new TpType[0]);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public TpType getTpArray(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            TpType target = null;
            target = (TpType)this.get_store().find_element_user(PROPERTY_QNAME[2], i);
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
    public int sizeOfTpArray() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[2]);
        }
    }

    @Override
    public void setTpArray(TpType[] tpArray) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])tpArray, PROPERTY_QNAME[2]);
    }

    @Override
    public void setTpArray(int i, TpType tp) {
        this.generatedSetterHelperImpl((XmlObject)tp, PROPERTY_QNAME[2], i, (short)2);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public TpType insertNewTp(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            TpType target = null;
            target = (TpType)this.get_store().insert_element_user(PROPERTY_QNAME[2], i);
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public TpType addNewTp() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            TpType target = null;
            target = (TpType)this.get_store().add_element_user(PROPERTY_QNAME[2]);
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void removeTp(int i) {
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
    public List<FldType> getFldList() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return new JavaListXmlObject<FldType>(this::getFldArray, this::setFldArray, this::insertNewFld, this::removeFld, this::sizeOfFldArray);
        }
    }

    @Override
    public FldType[] getFldArray() {
        return (FldType[])this.getXmlObjectArray(PROPERTY_QNAME[3], (XmlObject[])new FldType[0]);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public FldType getFldArray(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            FldType target = null;
            target = (FldType)this.get_store().find_element_user(PROPERTY_QNAME[3], i);
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
    public int sizeOfFldArray() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[3]);
        }
    }

    @Override
    public void setFldArray(FldType[] fldArray) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])fldArray, PROPERTY_QNAME[3]);
    }

    @Override
    public void setFldArray(int i, FldType fld) {
        this.generatedSetterHelperImpl((XmlObject)fld, PROPERTY_QNAME[3], i, (short)2);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public FldType insertNewFld(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            FldType target = null;
            target = (FldType)this.get_store().insert_element_user(PROPERTY_QNAME[3], i);
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public FldType addNewFld() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            FldType target = null;
            target = (FldType)this.get_store().add_element_user(PROPERTY_QNAME[3]);
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void removeFld(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.get_store().remove_element(PROPERTY_QNAME[3], i);
        }
    }
}


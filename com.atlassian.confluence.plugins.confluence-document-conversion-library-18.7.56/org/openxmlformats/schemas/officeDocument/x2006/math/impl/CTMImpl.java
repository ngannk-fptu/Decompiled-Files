/*
 * Decompiled with CFR 0.152.
 */
package org.openxmlformats.schemas.officeDocument.x2006.math.impl;

import java.util.List;
import javax.xml.namespace.QName;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.impl.values.JavaListXmlObject;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;
import org.openxmlformats.schemas.officeDocument.x2006.math.CTM;
import org.openxmlformats.schemas.officeDocument.x2006.math.CTMPr;
import org.openxmlformats.schemas.officeDocument.x2006.math.CTMR;

public class CTMImpl
extends XmlComplexContentImpl
implements CTM {
    private static final long serialVersionUID = 1L;
    private static final QName[] PROPERTY_QNAME = new QName[]{new QName("http://schemas.openxmlformats.org/officeDocument/2006/math", "mPr"), new QName("http://schemas.openxmlformats.org/officeDocument/2006/math", "mr")};

    public CTMImpl(SchemaType sType) {
        super(sType);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTMPr getMPr() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTMPr target = null;
            target = (CTMPr)((Object)this.get_store().find_element_user(PROPERTY_QNAME[0], 0));
            return target == null ? null : target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean isSetMPr() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[0]) != 0;
        }
    }

    @Override
    public void setMPr(CTMPr mPr) {
        this.generatedSetterHelperImpl(mPr, PROPERTY_QNAME[0], 0, (short)1);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTMPr addNewMPr() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTMPr target = null;
            target = (CTMPr)((Object)this.get_store().add_element_user(PROPERTY_QNAME[0]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void unsetMPr() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.get_store().remove_element(PROPERTY_QNAME[0], 0);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public List<CTMR> getMrList() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return new JavaListXmlObject<CTMR>(this::getMrArray, this::setMrArray, this::insertNewMr, this::removeMr, this::sizeOfMrArray);
        }
    }

    @Override
    public CTMR[] getMrArray() {
        return (CTMR[])this.getXmlObjectArray(PROPERTY_QNAME[1], new CTMR[0]);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTMR getMrArray(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTMR target = null;
            target = (CTMR)((Object)this.get_store().find_element_user(PROPERTY_QNAME[1], i));
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
    public int sizeOfMrArray() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[1]);
        }
    }

    @Override
    public void setMrArray(CTMR[] mrArray) {
        this.check_orphaned();
        this.arraySetterHelper(mrArray, PROPERTY_QNAME[1]);
    }

    @Override
    public void setMrArray(int i, CTMR mr) {
        this.generatedSetterHelperImpl(mr, PROPERTY_QNAME[1], i, (short)2);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTMR insertNewMr(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTMR target = null;
            target = (CTMR)((Object)this.get_store().insert_element_user(PROPERTY_QNAME[1], i));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTMR addNewMr() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTMR target = null;
            target = (CTMR)((Object)this.get_store().add_element_user(PROPERTY_QNAME[1]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void removeMr(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.get_store().remove_element(PROPERTY_QNAME[1], i);
        }
    }
}


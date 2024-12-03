/*
 * Decompiled with CFR 0.152.
 */
package org.openxmlformats.schemas.officeDocument.x2006.math.impl;

import java.util.List;
import javax.xml.namespace.QName;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.impl.values.JavaListXmlObject;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;
import org.openxmlformats.schemas.officeDocument.x2006.math.CTMC;
import org.openxmlformats.schemas.officeDocument.x2006.math.CTMCS;

public class CTMCSImpl
extends XmlComplexContentImpl
implements CTMCS {
    private static final long serialVersionUID = 1L;
    private static final QName[] PROPERTY_QNAME = new QName[]{new QName("http://schemas.openxmlformats.org/officeDocument/2006/math", "mc")};

    public CTMCSImpl(SchemaType sType) {
        super(sType);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public List<CTMC> getMcList() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return new JavaListXmlObject<CTMC>(this::getMcArray, this::setMcArray, this::insertNewMc, this::removeMc, this::sizeOfMcArray);
        }
    }

    @Override
    public CTMC[] getMcArray() {
        return (CTMC[])this.getXmlObjectArray(PROPERTY_QNAME[0], new CTMC[0]);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTMC getMcArray(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTMC target = null;
            target = (CTMC)((Object)this.get_store().find_element_user(PROPERTY_QNAME[0], i));
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
    public int sizeOfMcArray() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[0]);
        }
    }

    @Override
    public void setMcArray(CTMC[] mcArray) {
        this.check_orphaned();
        this.arraySetterHelper(mcArray, PROPERTY_QNAME[0]);
    }

    @Override
    public void setMcArray(int i, CTMC mc) {
        this.generatedSetterHelperImpl(mc, PROPERTY_QNAME[0], i, (short)2);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTMC insertNewMc(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTMC target = null;
            target = (CTMC)((Object)this.get_store().insert_element_user(PROPERTY_QNAME[0], i));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTMC addNewMc() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTMC target = null;
            target = (CTMC)((Object)this.get_store().add_element_user(PROPERTY_QNAME[0]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void removeMc(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.get_store().remove_element(PROPERTY_QNAME[0], i);
        }
    }
}


/*
 * Decompiled with CFR 0.152.
 */
package org.openxmlformats.schemas.officeDocument.x2006.math.impl;

import java.util.List;
import javax.xml.namespace.QName;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.impl.values.JavaListXmlObject;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;
import org.openxmlformats.schemas.officeDocument.x2006.math.CTMR;
import org.openxmlformats.schemas.officeDocument.x2006.math.CTOMathArg;

public class CTMRImpl
extends XmlComplexContentImpl
implements CTMR {
    private static final long serialVersionUID = 1L;
    private static final QName[] PROPERTY_QNAME = new QName[]{new QName("http://schemas.openxmlformats.org/officeDocument/2006/math", "e")};

    public CTMRImpl(SchemaType sType) {
        super(sType);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public List<CTOMathArg> getEList() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return new JavaListXmlObject<CTOMathArg>(this::getEArray, this::setEArray, this::insertNewE, this::removeE, this::sizeOfEArray);
        }
    }

    @Override
    public CTOMathArg[] getEArray() {
        return (CTOMathArg[])this.getXmlObjectArray(PROPERTY_QNAME[0], new CTOMathArg[0]);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTOMathArg getEArray(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTOMathArg target = null;
            target = (CTOMathArg)((Object)this.get_store().find_element_user(PROPERTY_QNAME[0], i));
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
    public int sizeOfEArray() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[0]);
        }
    }

    @Override
    public void setEArray(CTOMathArg[] eArray) {
        this.check_orphaned();
        this.arraySetterHelper(eArray, PROPERTY_QNAME[0]);
    }

    @Override
    public void setEArray(int i, CTOMathArg e) {
        this.generatedSetterHelperImpl(e, PROPERTY_QNAME[0], i, (short)2);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTOMathArg insertNewE(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTOMathArg target = null;
            target = (CTOMathArg)((Object)this.get_store().insert_element_user(PROPERTY_QNAME[0], i));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTOMathArg addNewE() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTOMathArg target = null;
            target = (CTOMathArg)((Object)this.get_store().add_element_user(PROPERTY_QNAME[0]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void removeE(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.get_store().remove_element(PROPERTY_QNAME[0], i);
        }
    }
}


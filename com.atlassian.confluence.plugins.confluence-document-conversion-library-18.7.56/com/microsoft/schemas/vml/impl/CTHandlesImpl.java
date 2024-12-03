/*
 * Decompiled with CFR 0.152.
 */
package com.microsoft.schemas.vml.impl;

import com.microsoft.schemas.vml.CTH;
import com.microsoft.schemas.vml.CTHandles;
import java.util.List;
import javax.xml.namespace.QName;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.impl.values.JavaListXmlObject;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;

public class CTHandlesImpl
extends XmlComplexContentImpl
implements CTHandles {
    private static final long serialVersionUID = 1L;
    private static final QName[] PROPERTY_QNAME = new QName[]{new QName("urn:schemas-microsoft-com:vml", "h")};

    public CTHandlesImpl(SchemaType sType) {
        super(sType);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public List<CTH> getHList() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return new JavaListXmlObject<CTH>(this::getHArray, this::setHArray, this::insertNewH, this::removeH, this::sizeOfHArray);
        }
    }

    @Override
    public CTH[] getHArray() {
        return (CTH[])this.getXmlObjectArray(PROPERTY_QNAME[0], new CTH[0]);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTH getHArray(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTH target = null;
            target = (CTH)((Object)this.get_store().find_element_user(PROPERTY_QNAME[0], i));
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
    public int sizeOfHArray() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[0]);
        }
    }

    @Override
    public void setHArray(CTH[] hArray) {
        this.check_orphaned();
        this.arraySetterHelper(hArray, PROPERTY_QNAME[0]);
    }

    @Override
    public void setHArray(int i, CTH h) {
        this.generatedSetterHelperImpl(h, PROPERTY_QNAME[0], i, (short)2);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTH insertNewH(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTH target = null;
            target = (CTH)((Object)this.get_store().insert_element_user(PROPERTY_QNAME[0], i));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTH addNewH() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTH target = null;
            target = (CTH)((Object)this.get_store().add_element_user(PROPERTY_QNAME[0]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void removeH(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.get_store().remove_element(PROPERTY_QNAME[0], i);
        }
    }
}


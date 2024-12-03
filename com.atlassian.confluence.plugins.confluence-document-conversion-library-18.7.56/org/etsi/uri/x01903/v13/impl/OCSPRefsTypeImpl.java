/*
 * Decompiled with CFR 0.152.
 */
package org.etsi.uri.x01903.v13.impl;

import java.util.List;
import javax.xml.namespace.QName;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.impl.values.JavaListXmlObject;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;
import org.etsi.uri.x01903.v13.OCSPRefType;
import org.etsi.uri.x01903.v13.OCSPRefsType;

public class OCSPRefsTypeImpl
extends XmlComplexContentImpl
implements OCSPRefsType {
    private static final long serialVersionUID = 1L;
    private static final QName[] PROPERTY_QNAME = new QName[]{new QName("http://uri.etsi.org/01903/v1.3.2#", "OCSPRef")};

    public OCSPRefsTypeImpl(SchemaType sType) {
        super(sType);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public List<OCSPRefType> getOCSPRefList() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return new JavaListXmlObject<OCSPRefType>(this::getOCSPRefArray, this::setOCSPRefArray, this::insertNewOCSPRef, this::removeOCSPRef, this::sizeOfOCSPRefArray);
        }
    }

    @Override
    public OCSPRefType[] getOCSPRefArray() {
        return (OCSPRefType[])this.getXmlObjectArray(PROPERTY_QNAME[0], new OCSPRefType[0]);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public OCSPRefType getOCSPRefArray(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            OCSPRefType target = null;
            target = (OCSPRefType)((Object)this.get_store().find_element_user(PROPERTY_QNAME[0], i));
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
    public int sizeOfOCSPRefArray() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[0]);
        }
    }

    @Override
    public void setOCSPRefArray(OCSPRefType[] ocspRefArray) {
        this.check_orphaned();
        this.arraySetterHelper(ocspRefArray, PROPERTY_QNAME[0]);
    }

    @Override
    public void setOCSPRefArray(int i, OCSPRefType ocspRef) {
        this.generatedSetterHelperImpl(ocspRef, PROPERTY_QNAME[0], i, (short)2);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public OCSPRefType insertNewOCSPRef(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            OCSPRefType target = null;
            target = (OCSPRefType)((Object)this.get_store().insert_element_user(PROPERTY_QNAME[0], i));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public OCSPRefType addNewOCSPRef() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            OCSPRefType target = null;
            target = (OCSPRefType)((Object)this.get_store().add_element_user(PROPERTY_QNAME[0]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void removeOCSPRef(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.get_store().remove_element(PROPERTY_QNAME[0], i);
        }
    }
}


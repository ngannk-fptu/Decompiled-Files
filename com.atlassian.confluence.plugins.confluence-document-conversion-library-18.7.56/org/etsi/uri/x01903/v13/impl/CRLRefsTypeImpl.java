/*
 * Decompiled with CFR 0.152.
 */
package org.etsi.uri.x01903.v13.impl;

import java.util.List;
import javax.xml.namespace.QName;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.impl.values.JavaListXmlObject;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;
import org.etsi.uri.x01903.v13.CRLRefType;
import org.etsi.uri.x01903.v13.CRLRefsType;

public class CRLRefsTypeImpl
extends XmlComplexContentImpl
implements CRLRefsType {
    private static final long serialVersionUID = 1L;
    private static final QName[] PROPERTY_QNAME = new QName[]{new QName("http://uri.etsi.org/01903/v1.3.2#", "CRLRef")};

    public CRLRefsTypeImpl(SchemaType sType) {
        super(sType);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public List<CRLRefType> getCRLRefList() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return new JavaListXmlObject<CRLRefType>(this::getCRLRefArray, this::setCRLRefArray, this::insertNewCRLRef, this::removeCRLRef, this::sizeOfCRLRefArray);
        }
    }

    @Override
    public CRLRefType[] getCRLRefArray() {
        return (CRLRefType[])this.getXmlObjectArray(PROPERTY_QNAME[0], new CRLRefType[0]);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CRLRefType getCRLRefArray(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CRLRefType target = null;
            target = (CRLRefType)((Object)this.get_store().find_element_user(PROPERTY_QNAME[0], i));
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
    public int sizeOfCRLRefArray() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[0]);
        }
    }

    @Override
    public void setCRLRefArray(CRLRefType[] crlRefArray) {
        this.check_orphaned();
        this.arraySetterHelper(crlRefArray, PROPERTY_QNAME[0]);
    }

    @Override
    public void setCRLRefArray(int i, CRLRefType crlRef) {
        this.generatedSetterHelperImpl(crlRef, PROPERTY_QNAME[0], i, (short)2);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CRLRefType insertNewCRLRef(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CRLRefType target = null;
            target = (CRLRefType)((Object)this.get_store().insert_element_user(PROPERTY_QNAME[0], i));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CRLRefType addNewCRLRef() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CRLRefType target = null;
            target = (CRLRefType)((Object)this.get_store().add_element_user(PROPERTY_QNAME[0]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void removeCRLRef(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.get_store().remove_element(PROPERTY_QNAME[0], i);
        }
    }
}


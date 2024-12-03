/*
 * Decompiled with CFR 0.152.
 */
package org.etsi.uri.x01903.v13.impl;

import java.util.List;
import javax.xml.namespace.QName;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.impl.values.JavaListXmlObject;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;
import org.etsi.uri.x01903.v13.CertIDListType;
import org.etsi.uri.x01903.v13.CertIDType;

public class CertIDListTypeImpl
extends XmlComplexContentImpl
implements CertIDListType {
    private static final long serialVersionUID = 1L;
    private static final QName[] PROPERTY_QNAME = new QName[]{new QName("http://uri.etsi.org/01903/v1.3.2#", "Cert")};

    public CertIDListTypeImpl(SchemaType sType) {
        super(sType);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public List<CertIDType> getCertList() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return new JavaListXmlObject<CertIDType>(this::getCertArray, this::setCertArray, this::insertNewCert, this::removeCert, this::sizeOfCertArray);
        }
    }

    @Override
    public CertIDType[] getCertArray() {
        return (CertIDType[])this.getXmlObjectArray(PROPERTY_QNAME[0], new CertIDType[0]);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CertIDType getCertArray(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CertIDType target = null;
            target = (CertIDType)((Object)this.get_store().find_element_user(PROPERTY_QNAME[0], i));
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
    public int sizeOfCertArray() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[0]);
        }
    }

    @Override
    public void setCertArray(CertIDType[] certArray) {
        this.check_orphaned();
        this.arraySetterHelper(certArray, PROPERTY_QNAME[0]);
    }

    @Override
    public void setCertArray(int i, CertIDType cert) {
        this.generatedSetterHelperImpl(cert, PROPERTY_QNAME[0], i, (short)2);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CertIDType insertNewCert(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CertIDType target = null;
            target = (CertIDType)((Object)this.get_store().insert_element_user(PROPERTY_QNAME[0], i));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CertIDType addNewCert() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CertIDType target = null;
            target = (CertIDType)((Object)this.get_store().add_element_user(PROPERTY_QNAME[0]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void removeCert(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.get_store().remove_element(PROPERTY_QNAME[0], i);
        }
    }
}


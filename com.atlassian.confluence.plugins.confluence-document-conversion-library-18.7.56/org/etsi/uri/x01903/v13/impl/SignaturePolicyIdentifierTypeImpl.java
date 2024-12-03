/*
 * Decompiled with CFR 0.152.
 */
package org.etsi.uri.x01903.v13.impl;

import javax.xml.namespace.QName;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;
import org.etsi.uri.x01903.v13.SignaturePolicyIdType;
import org.etsi.uri.x01903.v13.SignaturePolicyIdentifierType;

public class SignaturePolicyIdentifierTypeImpl
extends XmlComplexContentImpl
implements SignaturePolicyIdentifierType {
    private static final long serialVersionUID = 1L;
    private static final QName[] PROPERTY_QNAME = new QName[]{new QName("http://uri.etsi.org/01903/v1.3.2#", "SignaturePolicyId"), new QName("http://uri.etsi.org/01903/v1.3.2#", "SignaturePolicyImplied")};

    public SignaturePolicyIdentifierTypeImpl(SchemaType sType) {
        super(sType);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public SignaturePolicyIdType getSignaturePolicyId() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            SignaturePolicyIdType target = null;
            target = (SignaturePolicyIdType)((Object)this.get_store().find_element_user(PROPERTY_QNAME[0], 0));
            return target == null ? null : target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean isSetSignaturePolicyId() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[0]) != 0;
        }
    }

    @Override
    public void setSignaturePolicyId(SignaturePolicyIdType signaturePolicyId) {
        this.generatedSetterHelperImpl(signaturePolicyId, PROPERTY_QNAME[0], 0, (short)1);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public SignaturePolicyIdType addNewSignaturePolicyId() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            SignaturePolicyIdType target = null;
            target = (SignaturePolicyIdType)((Object)this.get_store().add_element_user(PROPERTY_QNAME[0]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void unsetSignaturePolicyId() {
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
    public XmlObject getSignaturePolicyImplied() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            XmlObject target = null;
            target = (XmlObject)((Object)this.get_store().find_element_user(PROPERTY_QNAME[1], 0));
            return target == null ? null : target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean isSetSignaturePolicyImplied() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[1]) != 0;
        }
    }

    @Override
    public void setSignaturePolicyImplied(XmlObject signaturePolicyImplied) {
        this.generatedSetterHelperImpl(signaturePolicyImplied, PROPERTY_QNAME[1], 0, (short)1);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public XmlObject addNewSignaturePolicyImplied() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            XmlObject target = null;
            target = (XmlObject)((Object)this.get_store().add_element_user(PROPERTY_QNAME[1]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void unsetSignaturePolicyImplied() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.get_store().remove_element(PROPERTY_QNAME[1], 0);
        }
    }
}


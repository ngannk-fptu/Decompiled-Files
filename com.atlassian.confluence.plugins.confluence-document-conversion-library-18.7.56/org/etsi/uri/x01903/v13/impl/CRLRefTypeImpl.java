/*
 * Decompiled with CFR 0.152.
 */
package org.etsi.uri.x01903.v13.impl;

import javax.xml.namespace.QName;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;
import org.etsi.uri.x01903.v13.CRLIdentifierType;
import org.etsi.uri.x01903.v13.CRLRefType;
import org.etsi.uri.x01903.v13.DigestAlgAndValueType;

public class CRLRefTypeImpl
extends XmlComplexContentImpl
implements CRLRefType {
    private static final long serialVersionUID = 1L;
    private static final QName[] PROPERTY_QNAME = new QName[]{new QName("http://uri.etsi.org/01903/v1.3.2#", "DigestAlgAndValue"), new QName("http://uri.etsi.org/01903/v1.3.2#", "CRLIdentifier")};

    public CRLRefTypeImpl(SchemaType sType) {
        super(sType);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public DigestAlgAndValueType getDigestAlgAndValue() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            DigestAlgAndValueType target = null;
            target = (DigestAlgAndValueType)((Object)this.get_store().find_element_user(PROPERTY_QNAME[0], 0));
            return target == null ? null : target;
        }
    }

    @Override
    public void setDigestAlgAndValue(DigestAlgAndValueType digestAlgAndValue) {
        this.generatedSetterHelperImpl(digestAlgAndValue, PROPERTY_QNAME[0], 0, (short)1);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public DigestAlgAndValueType addNewDigestAlgAndValue() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            DigestAlgAndValueType target = null;
            target = (DigestAlgAndValueType)((Object)this.get_store().add_element_user(PROPERTY_QNAME[0]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CRLIdentifierType getCRLIdentifier() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CRLIdentifierType target = null;
            target = (CRLIdentifierType)((Object)this.get_store().find_element_user(PROPERTY_QNAME[1], 0));
            return target == null ? null : target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean isSetCRLIdentifier() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[1]) != 0;
        }
    }

    @Override
    public void setCRLIdentifier(CRLIdentifierType crlIdentifier) {
        this.generatedSetterHelperImpl(crlIdentifier, PROPERTY_QNAME[1], 0, (short)1);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CRLIdentifierType addNewCRLIdentifier() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CRLIdentifierType target = null;
            target = (CRLIdentifierType)((Object)this.get_store().add_element_user(PROPERTY_QNAME[1]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void unsetCRLIdentifier() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.get_store().remove_element(PROPERTY_QNAME[1], 0);
        }
    }
}


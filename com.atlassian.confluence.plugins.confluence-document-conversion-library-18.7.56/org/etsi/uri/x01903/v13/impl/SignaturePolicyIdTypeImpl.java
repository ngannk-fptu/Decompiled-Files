/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.w3.x2000.x09.xmldsig.TransformsType
 */
package org.etsi.uri.x01903.v13.impl;

import javax.xml.namespace.QName;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;
import org.etsi.uri.x01903.v13.DigestAlgAndValueType;
import org.etsi.uri.x01903.v13.ObjectIdentifierType;
import org.etsi.uri.x01903.v13.SigPolicyQualifiersListType;
import org.etsi.uri.x01903.v13.SignaturePolicyIdType;
import org.w3.x2000.x09.xmldsig.TransformsType;

public class SignaturePolicyIdTypeImpl
extends XmlComplexContentImpl
implements SignaturePolicyIdType {
    private static final long serialVersionUID = 1L;
    private static final QName[] PROPERTY_QNAME = new QName[]{new QName("http://uri.etsi.org/01903/v1.3.2#", "SigPolicyId"), new QName("http://www.w3.org/2000/09/xmldsig#", "Transforms"), new QName("http://uri.etsi.org/01903/v1.3.2#", "SigPolicyHash"), new QName("http://uri.etsi.org/01903/v1.3.2#", "SigPolicyQualifiers")};

    public SignaturePolicyIdTypeImpl(SchemaType sType) {
        super(sType);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public ObjectIdentifierType getSigPolicyId() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            ObjectIdentifierType target = null;
            target = (ObjectIdentifierType)((Object)this.get_store().find_element_user(PROPERTY_QNAME[0], 0));
            return target == null ? null : target;
        }
    }

    @Override
    public void setSigPolicyId(ObjectIdentifierType sigPolicyId) {
        this.generatedSetterHelperImpl(sigPolicyId, PROPERTY_QNAME[0], 0, (short)1);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public ObjectIdentifierType addNewSigPolicyId() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            ObjectIdentifierType target = null;
            target = (ObjectIdentifierType)((Object)this.get_store().add_element_user(PROPERTY_QNAME[0]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public TransformsType getTransforms() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            TransformsType target = null;
            target = (TransformsType)this.get_store().find_element_user(PROPERTY_QNAME[1], 0);
            return target == null ? null : target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean isSetTransforms() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[1]) != 0;
        }
    }

    @Override
    public void setTransforms(TransformsType transforms) {
        this.generatedSetterHelperImpl((XmlObject)transforms, PROPERTY_QNAME[1], 0, (short)1);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public TransformsType addNewTransforms() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            TransformsType target = null;
            target = (TransformsType)this.get_store().add_element_user(PROPERTY_QNAME[1]);
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void unsetTransforms() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.get_store().remove_element(PROPERTY_QNAME[1], 0);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public DigestAlgAndValueType getSigPolicyHash() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            DigestAlgAndValueType target = null;
            target = (DigestAlgAndValueType)((Object)this.get_store().find_element_user(PROPERTY_QNAME[2], 0));
            return target == null ? null : target;
        }
    }

    @Override
    public void setSigPolicyHash(DigestAlgAndValueType sigPolicyHash) {
        this.generatedSetterHelperImpl(sigPolicyHash, PROPERTY_QNAME[2], 0, (short)1);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public DigestAlgAndValueType addNewSigPolicyHash() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            DigestAlgAndValueType target = null;
            target = (DigestAlgAndValueType)((Object)this.get_store().add_element_user(PROPERTY_QNAME[2]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public SigPolicyQualifiersListType getSigPolicyQualifiers() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            SigPolicyQualifiersListType target = null;
            target = (SigPolicyQualifiersListType)((Object)this.get_store().find_element_user(PROPERTY_QNAME[3], 0));
            return target == null ? null : target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean isSetSigPolicyQualifiers() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[3]) != 0;
        }
    }

    @Override
    public void setSigPolicyQualifiers(SigPolicyQualifiersListType sigPolicyQualifiers) {
        this.generatedSetterHelperImpl(sigPolicyQualifiers, PROPERTY_QNAME[3], 0, (short)1);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public SigPolicyQualifiersListType addNewSigPolicyQualifiers() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            SigPolicyQualifiersListType target = null;
            target = (SigPolicyQualifiersListType)((Object)this.get_store().add_element_user(PROPERTY_QNAME[3]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void unsetSigPolicyQualifiers() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.get_store().remove_element(PROPERTY_QNAME[3], 0);
        }
    }
}


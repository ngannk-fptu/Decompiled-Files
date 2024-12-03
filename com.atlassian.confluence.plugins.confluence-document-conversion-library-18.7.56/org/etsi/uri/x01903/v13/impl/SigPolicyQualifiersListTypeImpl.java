/*
 * Decompiled with CFR 0.152.
 */
package org.etsi.uri.x01903.v13.impl;

import java.util.List;
import javax.xml.namespace.QName;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.impl.values.JavaListXmlObject;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;
import org.etsi.uri.x01903.v13.AnyType;
import org.etsi.uri.x01903.v13.SigPolicyQualifiersListType;

public class SigPolicyQualifiersListTypeImpl
extends XmlComplexContentImpl
implements SigPolicyQualifiersListType {
    private static final long serialVersionUID = 1L;
    private static final QName[] PROPERTY_QNAME = new QName[]{new QName("http://uri.etsi.org/01903/v1.3.2#", "SigPolicyQualifier")};

    public SigPolicyQualifiersListTypeImpl(SchemaType sType) {
        super(sType);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public List<AnyType> getSigPolicyQualifierList() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return new JavaListXmlObject<AnyType>(this::getSigPolicyQualifierArray, this::setSigPolicyQualifierArray, this::insertNewSigPolicyQualifier, this::removeSigPolicyQualifier, this::sizeOfSigPolicyQualifierArray);
        }
    }

    @Override
    public AnyType[] getSigPolicyQualifierArray() {
        return (AnyType[])this.getXmlObjectArray(PROPERTY_QNAME[0], new AnyType[0]);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public AnyType getSigPolicyQualifierArray(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            AnyType target = null;
            target = (AnyType)((Object)this.get_store().find_element_user(PROPERTY_QNAME[0], i));
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
    public int sizeOfSigPolicyQualifierArray() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[0]);
        }
    }

    @Override
    public void setSigPolicyQualifierArray(AnyType[] sigPolicyQualifierArray) {
        this.check_orphaned();
        this.arraySetterHelper(sigPolicyQualifierArray, PROPERTY_QNAME[0]);
    }

    @Override
    public void setSigPolicyQualifierArray(int i, AnyType sigPolicyQualifier) {
        this.generatedSetterHelperImpl(sigPolicyQualifier, PROPERTY_QNAME[0], i, (short)2);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public AnyType insertNewSigPolicyQualifier(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            AnyType target = null;
            target = (AnyType)((Object)this.get_store().insert_element_user(PROPERTY_QNAME[0], i));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public AnyType addNewSigPolicyQualifier() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            AnyType target = null;
            target = (AnyType)((Object)this.get_store().add_element_user(PROPERTY_QNAME[0]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void removeSigPolicyQualifier(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.get_store().remove_element(PROPERTY_QNAME[0], i);
        }
    }
}


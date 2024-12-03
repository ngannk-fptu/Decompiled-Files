/*
 * Decompiled with CFR 0.152.
 */
package org.etsi.uri.x01903.v13.impl;

import java.util.List;
import javax.xml.namespace.QName;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.impl.values.JavaListXmlObject;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;
import org.etsi.uri.x01903.v13.CRLValuesType;
import org.etsi.uri.x01903.v13.EncapsulatedPKIDataType;

public class CRLValuesTypeImpl
extends XmlComplexContentImpl
implements CRLValuesType {
    private static final long serialVersionUID = 1L;
    private static final QName[] PROPERTY_QNAME = new QName[]{new QName("http://uri.etsi.org/01903/v1.3.2#", "EncapsulatedCRLValue")};

    public CRLValuesTypeImpl(SchemaType sType) {
        super(sType);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public List<EncapsulatedPKIDataType> getEncapsulatedCRLValueList() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return new JavaListXmlObject<EncapsulatedPKIDataType>(this::getEncapsulatedCRLValueArray, this::setEncapsulatedCRLValueArray, this::insertNewEncapsulatedCRLValue, this::removeEncapsulatedCRLValue, this::sizeOfEncapsulatedCRLValueArray);
        }
    }

    @Override
    public EncapsulatedPKIDataType[] getEncapsulatedCRLValueArray() {
        return (EncapsulatedPKIDataType[])this.getXmlObjectArray(PROPERTY_QNAME[0], new EncapsulatedPKIDataType[0]);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public EncapsulatedPKIDataType getEncapsulatedCRLValueArray(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            EncapsulatedPKIDataType target = null;
            target = (EncapsulatedPKIDataType)((Object)this.get_store().find_element_user(PROPERTY_QNAME[0], i));
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
    public int sizeOfEncapsulatedCRLValueArray() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[0]);
        }
    }

    @Override
    public void setEncapsulatedCRLValueArray(EncapsulatedPKIDataType[] encapsulatedCRLValueArray) {
        this.check_orphaned();
        this.arraySetterHelper(encapsulatedCRLValueArray, PROPERTY_QNAME[0]);
    }

    @Override
    public void setEncapsulatedCRLValueArray(int i, EncapsulatedPKIDataType encapsulatedCRLValue) {
        this.generatedSetterHelperImpl(encapsulatedCRLValue, PROPERTY_QNAME[0], i, (short)2);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public EncapsulatedPKIDataType insertNewEncapsulatedCRLValue(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            EncapsulatedPKIDataType target = null;
            target = (EncapsulatedPKIDataType)((Object)this.get_store().insert_element_user(PROPERTY_QNAME[0], i));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public EncapsulatedPKIDataType addNewEncapsulatedCRLValue() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            EncapsulatedPKIDataType target = null;
            target = (EncapsulatedPKIDataType)((Object)this.get_store().add_element_user(PROPERTY_QNAME[0]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void removeEncapsulatedCRLValue(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.get_store().remove_element(PROPERTY_QNAME[0], i);
        }
    }
}


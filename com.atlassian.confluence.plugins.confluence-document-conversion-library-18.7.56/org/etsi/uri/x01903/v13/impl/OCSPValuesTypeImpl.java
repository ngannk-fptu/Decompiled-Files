/*
 * Decompiled with CFR 0.152.
 */
package org.etsi.uri.x01903.v13.impl;

import java.util.List;
import javax.xml.namespace.QName;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.impl.values.JavaListXmlObject;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;
import org.etsi.uri.x01903.v13.EncapsulatedPKIDataType;
import org.etsi.uri.x01903.v13.OCSPValuesType;

public class OCSPValuesTypeImpl
extends XmlComplexContentImpl
implements OCSPValuesType {
    private static final long serialVersionUID = 1L;
    private static final QName[] PROPERTY_QNAME = new QName[]{new QName("http://uri.etsi.org/01903/v1.3.2#", "EncapsulatedOCSPValue")};

    public OCSPValuesTypeImpl(SchemaType sType) {
        super(sType);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public List<EncapsulatedPKIDataType> getEncapsulatedOCSPValueList() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return new JavaListXmlObject<EncapsulatedPKIDataType>(this::getEncapsulatedOCSPValueArray, this::setEncapsulatedOCSPValueArray, this::insertNewEncapsulatedOCSPValue, this::removeEncapsulatedOCSPValue, this::sizeOfEncapsulatedOCSPValueArray);
        }
    }

    @Override
    public EncapsulatedPKIDataType[] getEncapsulatedOCSPValueArray() {
        return (EncapsulatedPKIDataType[])this.getXmlObjectArray(PROPERTY_QNAME[0], new EncapsulatedPKIDataType[0]);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public EncapsulatedPKIDataType getEncapsulatedOCSPValueArray(int i) {
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
    public int sizeOfEncapsulatedOCSPValueArray() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[0]);
        }
    }

    @Override
    public void setEncapsulatedOCSPValueArray(EncapsulatedPKIDataType[] encapsulatedOCSPValueArray) {
        this.check_orphaned();
        this.arraySetterHelper(encapsulatedOCSPValueArray, PROPERTY_QNAME[0]);
    }

    @Override
    public void setEncapsulatedOCSPValueArray(int i, EncapsulatedPKIDataType encapsulatedOCSPValue) {
        this.generatedSetterHelperImpl(encapsulatedOCSPValue, PROPERTY_QNAME[0], i, (short)2);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public EncapsulatedPKIDataType insertNewEncapsulatedOCSPValue(int i) {
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
    public EncapsulatedPKIDataType addNewEncapsulatedOCSPValue() {
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
    public void removeEncapsulatedOCSPValue(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.get_store().remove_element(PROPERTY_QNAME[0], i);
        }
    }
}


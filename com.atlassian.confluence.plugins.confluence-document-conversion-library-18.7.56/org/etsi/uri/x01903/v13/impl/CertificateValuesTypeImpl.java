/*
 * Decompiled with CFR 0.152.
 */
package org.etsi.uri.x01903.v13.impl;

import java.util.List;
import javax.xml.namespace.QName;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.SimpleValue;
import org.apache.xmlbeans.XmlID;
import org.apache.xmlbeans.impl.values.JavaListXmlObject;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;
import org.etsi.uri.x01903.v13.AnyType;
import org.etsi.uri.x01903.v13.CertificateValuesType;
import org.etsi.uri.x01903.v13.EncapsulatedPKIDataType;

public class CertificateValuesTypeImpl
extends XmlComplexContentImpl
implements CertificateValuesType {
    private static final long serialVersionUID = 1L;
    private static final QName[] PROPERTY_QNAME = new QName[]{new QName("http://uri.etsi.org/01903/v1.3.2#", "EncapsulatedX509Certificate"), new QName("http://uri.etsi.org/01903/v1.3.2#", "OtherCertificate"), new QName("", "Id")};

    public CertificateValuesTypeImpl(SchemaType sType) {
        super(sType);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public List<EncapsulatedPKIDataType> getEncapsulatedX509CertificateList() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return new JavaListXmlObject<EncapsulatedPKIDataType>(this::getEncapsulatedX509CertificateArray, this::setEncapsulatedX509CertificateArray, this::insertNewEncapsulatedX509Certificate, this::removeEncapsulatedX509Certificate, this::sizeOfEncapsulatedX509CertificateArray);
        }
    }

    @Override
    public EncapsulatedPKIDataType[] getEncapsulatedX509CertificateArray() {
        return (EncapsulatedPKIDataType[])this.getXmlObjectArray(PROPERTY_QNAME[0], new EncapsulatedPKIDataType[0]);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public EncapsulatedPKIDataType getEncapsulatedX509CertificateArray(int i) {
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
    public int sizeOfEncapsulatedX509CertificateArray() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[0]);
        }
    }

    @Override
    public void setEncapsulatedX509CertificateArray(EncapsulatedPKIDataType[] encapsulatedX509CertificateArray) {
        this.check_orphaned();
        this.arraySetterHelper(encapsulatedX509CertificateArray, PROPERTY_QNAME[0]);
    }

    @Override
    public void setEncapsulatedX509CertificateArray(int i, EncapsulatedPKIDataType encapsulatedX509Certificate) {
        this.generatedSetterHelperImpl(encapsulatedX509Certificate, PROPERTY_QNAME[0], i, (short)2);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public EncapsulatedPKIDataType insertNewEncapsulatedX509Certificate(int i) {
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
    public EncapsulatedPKIDataType addNewEncapsulatedX509Certificate() {
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
    public void removeEncapsulatedX509Certificate(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.get_store().remove_element(PROPERTY_QNAME[0], i);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public List<AnyType> getOtherCertificateList() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return new JavaListXmlObject<AnyType>(this::getOtherCertificateArray, this::setOtherCertificateArray, this::insertNewOtherCertificate, this::removeOtherCertificate, this::sizeOfOtherCertificateArray);
        }
    }

    @Override
    public AnyType[] getOtherCertificateArray() {
        return (AnyType[])this.getXmlObjectArray(PROPERTY_QNAME[1], new AnyType[0]);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public AnyType getOtherCertificateArray(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            AnyType target = null;
            target = (AnyType)((Object)this.get_store().find_element_user(PROPERTY_QNAME[1], i));
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
    public int sizeOfOtherCertificateArray() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[1]);
        }
    }

    @Override
    public void setOtherCertificateArray(AnyType[] otherCertificateArray) {
        this.check_orphaned();
        this.arraySetterHelper(otherCertificateArray, PROPERTY_QNAME[1]);
    }

    @Override
    public void setOtherCertificateArray(int i, AnyType otherCertificate) {
        this.generatedSetterHelperImpl(otherCertificate, PROPERTY_QNAME[1], i, (short)2);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public AnyType insertNewOtherCertificate(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            AnyType target = null;
            target = (AnyType)((Object)this.get_store().insert_element_user(PROPERTY_QNAME[1], i));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public AnyType addNewOtherCertificate() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            AnyType target = null;
            target = (AnyType)((Object)this.get_store().add_element_user(PROPERTY_QNAME[1]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void removeOtherCertificate(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.get_store().remove_element(PROPERTY_QNAME[1], i);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public String getId() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[2]));
            return target == null ? null : target.getStringValue();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public XmlID xgetId() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            XmlID target = null;
            target = (XmlID)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[2]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean isSetId() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(PROPERTY_QNAME[2]) != null;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void setId(String id) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[2]));
            if (target == null) {
                target = (SimpleValue)((Object)this.get_store().add_attribute_user(PROPERTY_QNAME[2]));
            }
            target.setStringValue(id);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void xsetId(XmlID id) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            XmlID target = null;
            target = (XmlID)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[2]));
            if (target == null) {
                target = (XmlID)((Object)this.get_store().add_attribute_user(PROPERTY_QNAME[2]));
            }
            target.set(id);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void unsetId() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.get_store().remove_attribute(PROPERTY_QNAME[2]);
        }
    }
}


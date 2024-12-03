/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.w3.x2000.x09.xmldsig.SignatureMethodType
 */
package org.w3.x2000.x09.xmldsig.impl;

import java.util.List;
import javax.xml.namespace.QName;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.SimpleValue;
import org.apache.xmlbeans.XmlID;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.impl.values.JavaListXmlObject;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;
import org.w3.x2000.x09.xmldsig.CanonicalizationMethodType;
import org.w3.x2000.x09.xmldsig.ReferenceType;
import org.w3.x2000.x09.xmldsig.SignatureMethodType;
import org.w3.x2000.x09.xmldsig.SignedInfoType;

public class SignedInfoTypeImpl
extends XmlComplexContentImpl
implements SignedInfoType {
    private static final long serialVersionUID = 1L;
    private static final QName[] PROPERTY_QNAME = new QName[]{new QName("http://www.w3.org/2000/09/xmldsig#", "CanonicalizationMethod"), new QName("http://www.w3.org/2000/09/xmldsig#", "SignatureMethod"), new QName("http://www.w3.org/2000/09/xmldsig#", "Reference"), new QName("", "Id")};

    public SignedInfoTypeImpl(SchemaType sType) {
        super(sType);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CanonicalizationMethodType getCanonicalizationMethod() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CanonicalizationMethodType target = null;
            target = (CanonicalizationMethodType)((Object)this.get_store().find_element_user(PROPERTY_QNAME[0], 0));
            return target == null ? null : target;
        }
    }

    @Override
    public void setCanonicalizationMethod(CanonicalizationMethodType canonicalizationMethod) {
        this.generatedSetterHelperImpl(canonicalizationMethod, PROPERTY_QNAME[0], 0, (short)1);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CanonicalizationMethodType addNewCanonicalizationMethod() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CanonicalizationMethodType target = null;
            target = (CanonicalizationMethodType)((Object)this.get_store().add_element_user(PROPERTY_QNAME[0]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public SignatureMethodType getSignatureMethod() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            SignatureMethodType target = null;
            target = (SignatureMethodType)this.get_store().find_element_user(PROPERTY_QNAME[1], 0);
            return target == null ? null : target;
        }
    }

    @Override
    public void setSignatureMethod(SignatureMethodType signatureMethod) {
        this.generatedSetterHelperImpl((XmlObject)signatureMethod, PROPERTY_QNAME[1], 0, (short)1);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public SignatureMethodType addNewSignatureMethod() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            SignatureMethodType target = null;
            target = (SignatureMethodType)this.get_store().add_element_user(PROPERTY_QNAME[1]);
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public List<ReferenceType> getReferenceList() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return new JavaListXmlObject<ReferenceType>(this::getReferenceArray, this::setReferenceArray, this::insertNewReference, this::removeReference, this::sizeOfReferenceArray);
        }
    }

    @Override
    public ReferenceType[] getReferenceArray() {
        return (ReferenceType[])this.getXmlObjectArray(PROPERTY_QNAME[2], new ReferenceType[0]);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public ReferenceType getReferenceArray(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            ReferenceType target = null;
            target = (ReferenceType)((Object)this.get_store().find_element_user(PROPERTY_QNAME[2], i));
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
    public int sizeOfReferenceArray() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[2]);
        }
    }

    @Override
    public void setReferenceArray(ReferenceType[] referenceArray) {
        this.check_orphaned();
        this.arraySetterHelper(referenceArray, PROPERTY_QNAME[2]);
    }

    @Override
    public void setReferenceArray(int i, ReferenceType reference) {
        this.generatedSetterHelperImpl(reference, PROPERTY_QNAME[2], i, (short)2);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public ReferenceType insertNewReference(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            ReferenceType target = null;
            target = (ReferenceType)((Object)this.get_store().insert_element_user(PROPERTY_QNAME[2], i));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public ReferenceType addNewReference() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            ReferenceType target = null;
            target = (ReferenceType)((Object)this.get_store().add_element_user(PROPERTY_QNAME[2]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void removeReference(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.get_store().remove_element(PROPERTY_QNAME[2], i);
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
            target = (SimpleValue)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[3]));
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
            target = (XmlID)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[3]));
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
            return this.get_store().find_attribute_user(PROPERTY_QNAME[3]) != null;
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
            target = (SimpleValue)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[3]));
            if (target == null) {
                target = (SimpleValue)((Object)this.get_store().add_attribute_user(PROPERTY_QNAME[3]));
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
            target = (XmlID)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[3]));
            if (target == null) {
                target = (XmlID)((Object)this.get_store().add_attribute_user(PROPERTY_QNAME[3]));
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
            this.get_store().remove_attribute(PROPERTY_QNAME[3]);
        }
    }
}


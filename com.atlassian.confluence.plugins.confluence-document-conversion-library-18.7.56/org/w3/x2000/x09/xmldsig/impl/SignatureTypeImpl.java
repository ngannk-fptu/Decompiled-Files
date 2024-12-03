/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.w3.x2000.x09.xmldsig.KeyInfoType
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
import org.w3.x2000.x09.xmldsig.KeyInfoType;
import org.w3.x2000.x09.xmldsig.ObjectType;
import org.w3.x2000.x09.xmldsig.SignatureType;
import org.w3.x2000.x09.xmldsig.SignatureValueType;
import org.w3.x2000.x09.xmldsig.SignedInfoType;

public class SignatureTypeImpl
extends XmlComplexContentImpl
implements SignatureType {
    private static final long serialVersionUID = 1L;
    private static final QName[] PROPERTY_QNAME = new QName[]{new QName("http://www.w3.org/2000/09/xmldsig#", "SignedInfo"), new QName("http://www.w3.org/2000/09/xmldsig#", "SignatureValue"), new QName("http://www.w3.org/2000/09/xmldsig#", "KeyInfo"), new QName("http://www.w3.org/2000/09/xmldsig#", "Object"), new QName("", "Id")};

    public SignatureTypeImpl(SchemaType sType) {
        super(sType);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public SignedInfoType getSignedInfo() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            SignedInfoType target = null;
            target = (SignedInfoType)((Object)this.get_store().find_element_user(PROPERTY_QNAME[0], 0));
            return target == null ? null : target;
        }
    }

    @Override
    public void setSignedInfo(SignedInfoType signedInfo) {
        this.generatedSetterHelperImpl(signedInfo, PROPERTY_QNAME[0], 0, (short)1);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public SignedInfoType addNewSignedInfo() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            SignedInfoType target = null;
            target = (SignedInfoType)((Object)this.get_store().add_element_user(PROPERTY_QNAME[0]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public SignatureValueType getSignatureValue() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            SignatureValueType target = null;
            target = (SignatureValueType)((Object)this.get_store().find_element_user(PROPERTY_QNAME[1], 0));
            return target == null ? null : target;
        }
    }

    @Override
    public void setSignatureValue(SignatureValueType signatureValue) {
        this.generatedSetterHelperImpl(signatureValue, PROPERTY_QNAME[1], 0, (short)1);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public SignatureValueType addNewSignatureValue() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            SignatureValueType target = null;
            target = (SignatureValueType)((Object)this.get_store().add_element_user(PROPERTY_QNAME[1]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public KeyInfoType getKeyInfo() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            KeyInfoType target = null;
            target = (KeyInfoType)this.get_store().find_element_user(PROPERTY_QNAME[2], 0);
            return target == null ? null : target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean isSetKeyInfo() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[2]) != 0;
        }
    }

    @Override
    public void setKeyInfo(KeyInfoType keyInfo) {
        this.generatedSetterHelperImpl((XmlObject)keyInfo, PROPERTY_QNAME[2], 0, (short)1);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public KeyInfoType addNewKeyInfo() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            KeyInfoType target = null;
            target = (KeyInfoType)this.get_store().add_element_user(PROPERTY_QNAME[2]);
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void unsetKeyInfo() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.get_store().remove_element(PROPERTY_QNAME[2], 0);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public List<ObjectType> getObjectList() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return new JavaListXmlObject<ObjectType>(this::getObjectArray, this::setObjectArray, this::insertNewObject, this::removeObject, this::sizeOfObjectArray);
        }
    }

    @Override
    public ObjectType[] getObjectArray() {
        return (ObjectType[])this.getXmlObjectArray(PROPERTY_QNAME[3], new ObjectType[0]);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public ObjectType getObjectArray(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            ObjectType target = null;
            target = (ObjectType)((Object)this.get_store().find_element_user(PROPERTY_QNAME[3], i));
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
    public int sizeOfObjectArray() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[3]);
        }
    }

    @Override
    public void setObjectArray(ObjectType[] objectArray) {
        this.check_orphaned();
        this.arraySetterHelper(objectArray, PROPERTY_QNAME[3]);
    }

    @Override
    public void setObjectArray(int i, ObjectType object) {
        this.generatedSetterHelperImpl(object, PROPERTY_QNAME[3], i, (short)2);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public ObjectType insertNewObject(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            ObjectType target = null;
            target = (ObjectType)((Object)this.get_store().insert_element_user(PROPERTY_QNAME[3], i));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public ObjectType addNewObject() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            ObjectType target = null;
            target = (ObjectType)((Object)this.get_store().add_element_user(PROPERTY_QNAME[3]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void removeObject(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.get_store().remove_element(PROPERTY_QNAME[3], i);
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
            target = (SimpleValue)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[4]));
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
            target = (XmlID)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[4]));
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
            return this.get_store().find_attribute_user(PROPERTY_QNAME[4]) != null;
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
            target = (SimpleValue)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[4]));
            if (target == null) {
                target = (SimpleValue)((Object)this.get_store().add_attribute_user(PROPERTY_QNAME[4]));
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
            target = (XmlID)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[4]));
            if (target == null) {
                target = (XmlID)((Object)this.get_store().add_attribute_user(PROPERTY_QNAME[4]));
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
            this.get_store().remove_attribute(PROPERTY_QNAME[4]);
        }
    }
}


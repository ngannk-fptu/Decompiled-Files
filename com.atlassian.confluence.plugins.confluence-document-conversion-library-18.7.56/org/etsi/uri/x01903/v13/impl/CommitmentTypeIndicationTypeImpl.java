/*
 * Decompiled with CFR 0.152.
 */
package org.etsi.uri.x01903.v13.impl;

import java.util.List;
import javax.xml.namespace.QName;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.SimpleValue;
import org.apache.xmlbeans.XmlAnyURI;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.impl.values.JavaListObject;
import org.apache.xmlbeans.impl.values.JavaListXmlObject;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;
import org.etsi.uri.x01903.v13.CommitmentTypeIndicationType;
import org.etsi.uri.x01903.v13.CommitmentTypeQualifiersListType;
import org.etsi.uri.x01903.v13.ObjectIdentifierType;

public class CommitmentTypeIndicationTypeImpl
extends XmlComplexContentImpl
implements CommitmentTypeIndicationType {
    private static final long serialVersionUID = 1L;
    private static final QName[] PROPERTY_QNAME = new QName[]{new QName("http://uri.etsi.org/01903/v1.3.2#", "CommitmentTypeId"), new QName("http://uri.etsi.org/01903/v1.3.2#", "ObjectReference"), new QName("http://uri.etsi.org/01903/v1.3.2#", "AllSignedDataObjects"), new QName("http://uri.etsi.org/01903/v1.3.2#", "CommitmentTypeQualifiers")};

    public CommitmentTypeIndicationTypeImpl(SchemaType sType) {
        super(sType);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public ObjectIdentifierType getCommitmentTypeId() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            ObjectIdentifierType target = null;
            target = (ObjectIdentifierType)((Object)this.get_store().find_element_user(PROPERTY_QNAME[0], 0));
            return target == null ? null : target;
        }
    }

    @Override
    public void setCommitmentTypeId(ObjectIdentifierType commitmentTypeId) {
        this.generatedSetterHelperImpl(commitmentTypeId, PROPERTY_QNAME[0], 0, (short)1);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public ObjectIdentifierType addNewCommitmentTypeId() {
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
    public List<String> getObjectReferenceList() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return new JavaListObject<String>(this::getObjectReferenceArray, this::setObjectReferenceArray, this::insertObjectReference, this::removeObjectReference, this::sizeOfObjectReferenceArray);
        }
    }

    @Override
    public String[] getObjectReferenceArray() {
        return this.getObjectArray(PROPERTY_QNAME[1], SimpleValue::getStringValue, String[]::new);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public String getObjectReferenceArray(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)((Object)this.get_store().find_element_user(PROPERTY_QNAME[1], i));
            if (target == null) {
                throw new IndexOutOfBoundsException();
            }
            return target.getStringValue();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public List<XmlAnyURI> xgetObjectReferenceList() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return new JavaListXmlObject<XmlAnyURI>(this::xgetObjectReferenceArray, this::xsetObjectReferenceArray, this::insertNewObjectReference, this::removeObjectReference, this::sizeOfObjectReferenceArray);
        }
    }

    @Override
    public XmlAnyURI[] xgetObjectReferenceArray() {
        return (XmlAnyURI[])this.xgetArray(PROPERTY_QNAME[1], XmlAnyURI[]::new);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public XmlAnyURI xgetObjectReferenceArray(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            XmlAnyURI target = null;
            target = (XmlAnyURI)((Object)this.get_store().find_element_user(PROPERTY_QNAME[1], i));
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
    public int sizeOfObjectReferenceArray() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[1]);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void setObjectReferenceArray(String[] objectReferenceArray) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.arraySetterHelper(objectReferenceArray, PROPERTY_QNAME[1]);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void setObjectReferenceArray(int i, String objectReference) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)((Object)this.get_store().find_element_user(PROPERTY_QNAME[1], i));
            if (target == null) {
                throw new IndexOutOfBoundsException();
            }
            target.setStringValue(objectReference);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void xsetObjectReferenceArray(XmlAnyURI[] objectReferenceArray) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.arraySetterHelper(objectReferenceArray, PROPERTY_QNAME[1]);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void xsetObjectReferenceArray(int i, XmlAnyURI objectReference) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            XmlAnyURI target = null;
            target = (XmlAnyURI)((Object)this.get_store().find_element_user(PROPERTY_QNAME[1], i));
            if (target == null) {
                throw new IndexOutOfBoundsException();
            }
            target.set(objectReference);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void insertObjectReference(int i, String objectReference) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            SimpleValue target = (SimpleValue)((Object)this.get_store().insert_element_user(PROPERTY_QNAME[1], i));
            target.setStringValue(objectReference);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void addObjectReference(String objectReference) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)((Object)this.get_store().add_element_user(PROPERTY_QNAME[1]));
            target.setStringValue(objectReference);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public XmlAnyURI insertNewObjectReference(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            XmlAnyURI target = null;
            target = (XmlAnyURI)((Object)this.get_store().insert_element_user(PROPERTY_QNAME[1], i));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public XmlAnyURI addNewObjectReference() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            XmlAnyURI target = null;
            target = (XmlAnyURI)((Object)this.get_store().add_element_user(PROPERTY_QNAME[1]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void removeObjectReference(int i) {
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
    public XmlObject getAllSignedDataObjects() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            XmlObject target = null;
            target = (XmlObject)((Object)this.get_store().find_element_user(PROPERTY_QNAME[2], 0));
            return target == null ? null : target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean isSetAllSignedDataObjects() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[2]) != 0;
        }
    }

    @Override
    public void setAllSignedDataObjects(XmlObject allSignedDataObjects) {
        this.generatedSetterHelperImpl(allSignedDataObjects, PROPERTY_QNAME[2], 0, (short)1);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public XmlObject addNewAllSignedDataObjects() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            XmlObject target = null;
            target = (XmlObject)((Object)this.get_store().add_element_user(PROPERTY_QNAME[2]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void unsetAllSignedDataObjects() {
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
    public CommitmentTypeQualifiersListType getCommitmentTypeQualifiers() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CommitmentTypeQualifiersListType target = null;
            target = (CommitmentTypeQualifiersListType)((Object)this.get_store().find_element_user(PROPERTY_QNAME[3], 0));
            return target == null ? null : target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean isSetCommitmentTypeQualifiers() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[3]) != 0;
        }
    }

    @Override
    public void setCommitmentTypeQualifiers(CommitmentTypeQualifiersListType commitmentTypeQualifiers) {
        this.generatedSetterHelperImpl(commitmentTypeQualifiers, PROPERTY_QNAME[3], 0, (short)1);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CommitmentTypeQualifiersListType addNewCommitmentTypeQualifiers() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CommitmentTypeQualifiersListType target = null;
            target = (CommitmentTypeQualifiersListType)((Object)this.get_store().add_element_user(PROPERTY_QNAME[3]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void unsetCommitmentTypeQualifiers() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.get_store().remove_element(PROPERTY_QNAME[3], 0);
        }
    }
}


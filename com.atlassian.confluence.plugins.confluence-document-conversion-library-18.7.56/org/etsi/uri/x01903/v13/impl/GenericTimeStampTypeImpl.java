/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.etsi.uri.x01903.v13.IncludeType
 *  org.etsi.uri.x01903.v13.ReferenceInfoType
 */
package org.etsi.uri.x01903.v13.impl;

import java.util.List;
import javax.xml.namespace.QName;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.SimpleValue;
import org.apache.xmlbeans.XmlID;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.impl.values.JavaListXmlObject;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;
import org.etsi.uri.x01903.v13.AnyType;
import org.etsi.uri.x01903.v13.EncapsulatedPKIDataType;
import org.etsi.uri.x01903.v13.GenericTimeStampType;
import org.etsi.uri.x01903.v13.IncludeType;
import org.etsi.uri.x01903.v13.ReferenceInfoType;
import org.w3.x2000.x09.xmldsig.CanonicalizationMethodType;

public class GenericTimeStampTypeImpl
extends XmlComplexContentImpl
implements GenericTimeStampType {
    private static final long serialVersionUID = 1L;
    private static final QName[] PROPERTY_QNAME = new QName[]{new QName("http://uri.etsi.org/01903/v1.3.2#", "Include"), new QName("http://uri.etsi.org/01903/v1.3.2#", "ReferenceInfo"), new QName("http://www.w3.org/2000/09/xmldsig#", "CanonicalizationMethod"), new QName("http://uri.etsi.org/01903/v1.3.2#", "EncapsulatedTimeStamp"), new QName("http://uri.etsi.org/01903/v1.3.2#", "XMLTimeStamp"), new QName("", "Id")};

    public GenericTimeStampTypeImpl(SchemaType sType) {
        super(sType);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public List<IncludeType> getIncludeList() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return new JavaListXmlObject<IncludeType>(this::getIncludeArray, this::setIncludeArray, this::insertNewInclude, this::removeInclude, this::sizeOfIncludeArray);
        }
    }

    @Override
    public IncludeType[] getIncludeArray() {
        return (IncludeType[])this.getXmlObjectArray(PROPERTY_QNAME[0], (XmlObject[])new IncludeType[0]);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public IncludeType getIncludeArray(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            IncludeType target = null;
            target = (IncludeType)this.get_store().find_element_user(PROPERTY_QNAME[0], i);
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
    public int sizeOfIncludeArray() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[0]);
        }
    }

    @Override
    public void setIncludeArray(IncludeType[] includeArray) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])includeArray, PROPERTY_QNAME[0]);
    }

    @Override
    public void setIncludeArray(int i, IncludeType include) {
        this.generatedSetterHelperImpl((XmlObject)include, PROPERTY_QNAME[0], i, (short)2);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public IncludeType insertNewInclude(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            IncludeType target = null;
            target = (IncludeType)this.get_store().insert_element_user(PROPERTY_QNAME[0], i);
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public IncludeType addNewInclude() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            IncludeType target = null;
            target = (IncludeType)this.get_store().add_element_user(PROPERTY_QNAME[0]);
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void removeInclude(int i) {
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
    public List<ReferenceInfoType> getReferenceInfoList() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return new JavaListXmlObject<ReferenceInfoType>(this::getReferenceInfoArray, this::setReferenceInfoArray, this::insertNewReferenceInfo, this::removeReferenceInfo, this::sizeOfReferenceInfoArray);
        }
    }

    @Override
    public ReferenceInfoType[] getReferenceInfoArray() {
        return (ReferenceInfoType[])this.getXmlObjectArray(PROPERTY_QNAME[1], (XmlObject[])new ReferenceInfoType[0]);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public ReferenceInfoType getReferenceInfoArray(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            ReferenceInfoType target = null;
            target = (ReferenceInfoType)this.get_store().find_element_user(PROPERTY_QNAME[1], i);
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
    public int sizeOfReferenceInfoArray() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[1]);
        }
    }

    @Override
    public void setReferenceInfoArray(ReferenceInfoType[] referenceInfoArray) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])referenceInfoArray, PROPERTY_QNAME[1]);
    }

    @Override
    public void setReferenceInfoArray(int i, ReferenceInfoType referenceInfo) {
        this.generatedSetterHelperImpl((XmlObject)referenceInfo, PROPERTY_QNAME[1], i, (short)2);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public ReferenceInfoType insertNewReferenceInfo(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            ReferenceInfoType target = null;
            target = (ReferenceInfoType)this.get_store().insert_element_user(PROPERTY_QNAME[1], i);
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public ReferenceInfoType addNewReferenceInfo() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            ReferenceInfoType target = null;
            target = (ReferenceInfoType)this.get_store().add_element_user(PROPERTY_QNAME[1]);
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void removeReferenceInfo(int i) {
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
    public CanonicalizationMethodType getCanonicalizationMethod() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CanonicalizationMethodType target = null;
            target = (CanonicalizationMethodType)((Object)this.get_store().find_element_user(PROPERTY_QNAME[2], 0));
            return target == null ? null : target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean isSetCanonicalizationMethod() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[2]) != 0;
        }
    }

    @Override
    public void setCanonicalizationMethod(CanonicalizationMethodType canonicalizationMethod) {
        this.generatedSetterHelperImpl(canonicalizationMethod, PROPERTY_QNAME[2], 0, (short)1);
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
            target = (CanonicalizationMethodType)((Object)this.get_store().add_element_user(PROPERTY_QNAME[2]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void unsetCanonicalizationMethod() {
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
    public List<EncapsulatedPKIDataType> getEncapsulatedTimeStampList() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return new JavaListXmlObject<EncapsulatedPKIDataType>(this::getEncapsulatedTimeStampArray, this::setEncapsulatedTimeStampArray, this::insertNewEncapsulatedTimeStamp, this::removeEncapsulatedTimeStamp, this::sizeOfEncapsulatedTimeStampArray);
        }
    }

    @Override
    public EncapsulatedPKIDataType[] getEncapsulatedTimeStampArray() {
        return (EncapsulatedPKIDataType[])this.getXmlObjectArray(PROPERTY_QNAME[3], new EncapsulatedPKIDataType[0]);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public EncapsulatedPKIDataType getEncapsulatedTimeStampArray(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            EncapsulatedPKIDataType target = null;
            target = (EncapsulatedPKIDataType)((Object)this.get_store().find_element_user(PROPERTY_QNAME[3], i));
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
    public int sizeOfEncapsulatedTimeStampArray() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[3]);
        }
    }

    @Override
    public void setEncapsulatedTimeStampArray(EncapsulatedPKIDataType[] encapsulatedTimeStampArray) {
        this.check_orphaned();
        this.arraySetterHelper(encapsulatedTimeStampArray, PROPERTY_QNAME[3]);
    }

    @Override
    public void setEncapsulatedTimeStampArray(int i, EncapsulatedPKIDataType encapsulatedTimeStamp) {
        this.generatedSetterHelperImpl(encapsulatedTimeStamp, PROPERTY_QNAME[3], i, (short)2);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public EncapsulatedPKIDataType insertNewEncapsulatedTimeStamp(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            EncapsulatedPKIDataType target = null;
            target = (EncapsulatedPKIDataType)((Object)this.get_store().insert_element_user(PROPERTY_QNAME[3], i));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public EncapsulatedPKIDataType addNewEncapsulatedTimeStamp() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            EncapsulatedPKIDataType target = null;
            target = (EncapsulatedPKIDataType)((Object)this.get_store().add_element_user(PROPERTY_QNAME[3]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void removeEncapsulatedTimeStamp(int i) {
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
    public List<AnyType> getXMLTimeStampList() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return new JavaListXmlObject<AnyType>(this::getXMLTimeStampArray, this::setXMLTimeStampArray, this::insertNewXMLTimeStamp, this::removeXMLTimeStamp, this::sizeOfXMLTimeStampArray);
        }
    }

    @Override
    public AnyType[] getXMLTimeStampArray() {
        return (AnyType[])this.getXmlObjectArray(PROPERTY_QNAME[4], new AnyType[0]);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public AnyType getXMLTimeStampArray(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            AnyType target = null;
            target = (AnyType)((Object)this.get_store().find_element_user(PROPERTY_QNAME[4], i));
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
    public int sizeOfXMLTimeStampArray() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[4]);
        }
    }

    @Override
    public void setXMLTimeStampArray(AnyType[] xmlTimeStampArray) {
        this.check_orphaned();
        this.arraySetterHelper(xmlTimeStampArray, PROPERTY_QNAME[4]);
    }

    @Override
    public void setXMLTimeStampArray(int i, AnyType xmlTimeStamp) {
        this.generatedSetterHelperImpl(xmlTimeStamp, PROPERTY_QNAME[4], i, (short)2);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public AnyType insertNewXMLTimeStamp(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            AnyType target = null;
            target = (AnyType)((Object)this.get_store().insert_element_user(PROPERTY_QNAME[4], i));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public AnyType addNewXMLTimeStamp() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            AnyType target = null;
            target = (AnyType)((Object)this.get_store().add_element_user(PROPERTY_QNAME[4]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void removeXMLTimeStamp(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.get_store().remove_element(PROPERTY_QNAME[4], i);
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
            target = (SimpleValue)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[5]));
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
            target = (XmlID)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[5]));
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
            return this.get_store().find_attribute_user(PROPERTY_QNAME[5]) != null;
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
            target = (SimpleValue)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[5]));
            if (target == null) {
                target = (SimpleValue)((Object)this.get_store().add_attribute_user(PROPERTY_QNAME[5]));
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
            target = (XmlID)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[5]));
            if (target == null) {
                target = (XmlID)((Object)this.get_store().add_attribute_user(PROPERTY_QNAME[5]));
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
            this.get_store().remove_attribute(PROPERTY_QNAME[5]);
        }
    }
}


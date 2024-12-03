/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.etsi.uri.x01903.v13.CounterSignatureType
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
import org.etsi.uri.x01903.v13.CertificateValuesType;
import org.etsi.uri.x01903.v13.CompleteCertificateRefsType;
import org.etsi.uri.x01903.v13.CompleteRevocationRefsType;
import org.etsi.uri.x01903.v13.CounterSignatureType;
import org.etsi.uri.x01903.v13.RevocationValuesType;
import org.etsi.uri.x01903.v13.UnsignedSignaturePropertiesType;
import org.etsi.uri.x01903.v13.XAdESTimeStampType;

public class UnsignedSignaturePropertiesTypeImpl
extends XmlComplexContentImpl
implements UnsignedSignaturePropertiesType {
    private static final long serialVersionUID = 1L;
    private static final QName[] PROPERTY_QNAME = new QName[]{new QName("http://uri.etsi.org/01903/v1.3.2#", "CounterSignature"), new QName("http://uri.etsi.org/01903/v1.3.2#", "SignatureTimeStamp"), new QName("http://uri.etsi.org/01903/v1.3.2#", "CompleteCertificateRefs"), new QName("http://uri.etsi.org/01903/v1.3.2#", "CompleteRevocationRefs"), new QName("http://uri.etsi.org/01903/v1.3.2#", "AttributeCertificateRefs"), new QName("http://uri.etsi.org/01903/v1.3.2#", "AttributeRevocationRefs"), new QName("http://uri.etsi.org/01903/v1.3.2#", "SigAndRefsTimeStamp"), new QName("http://uri.etsi.org/01903/v1.3.2#", "RefsOnlyTimeStamp"), new QName("http://uri.etsi.org/01903/v1.3.2#", "CertificateValues"), new QName("http://uri.etsi.org/01903/v1.3.2#", "RevocationValues"), new QName("http://uri.etsi.org/01903/v1.3.2#", "AttrAuthoritiesCertValues"), new QName("http://uri.etsi.org/01903/v1.3.2#", "AttributeRevocationValues"), new QName("http://uri.etsi.org/01903/v1.3.2#", "ArchiveTimeStamp"), new QName("", "Id")};

    public UnsignedSignaturePropertiesTypeImpl(SchemaType sType) {
        super(sType);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public List<CounterSignatureType> getCounterSignatureList() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return new JavaListXmlObject<CounterSignatureType>(this::getCounterSignatureArray, this::setCounterSignatureArray, this::insertNewCounterSignature, this::removeCounterSignature, this::sizeOfCounterSignatureArray);
        }
    }

    @Override
    public CounterSignatureType[] getCounterSignatureArray() {
        return (CounterSignatureType[])this.getXmlObjectArray(PROPERTY_QNAME[0], (XmlObject[])new CounterSignatureType[0]);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CounterSignatureType getCounterSignatureArray(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CounterSignatureType target = null;
            target = (CounterSignatureType)this.get_store().find_element_user(PROPERTY_QNAME[0], i);
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
    public int sizeOfCounterSignatureArray() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[0]);
        }
    }

    @Override
    public void setCounterSignatureArray(CounterSignatureType[] counterSignatureArray) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])counterSignatureArray, PROPERTY_QNAME[0]);
    }

    @Override
    public void setCounterSignatureArray(int i, CounterSignatureType counterSignature) {
        this.generatedSetterHelperImpl((XmlObject)counterSignature, PROPERTY_QNAME[0], i, (short)2);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CounterSignatureType insertNewCounterSignature(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CounterSignatureType target = null;
            target = (CounterSignatureType)this.get_store().insert_element_user(PROPERTY_QNAME[0], i);
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CounterSignatureType addNewCounterSignature() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CounterSignatureType target = null;
            target = (CounterSignatureType)this.get_store().add_element_user(PROPERTY_QNAME[0]);
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void removeCounterSignature(int i) {
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
    public List<XAdESTimeStampType> getSignatureTimeStampList() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return new JavaListXmlObject<XAdESTimeStampType>(this::getSignatureTimeStampArray, this::setSignatureTimeStampArray, this::insertNewSignatureTimeStamp, this::removeSignatureTimeStamp, this::sizeOfSignatureTimeStampArray);
        }
    }

    @Override
    public XAdESTimeStampType[] getSignatureTimeStampArray() {
        return (XAdESTimeStampType[])this.getXmlObjectArray(PROPERTY_QNAME[1], new XAdESTimeStampType[0]);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public XAdESTimeStampType getSignatureTimeStampArray(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            XAdESTimeStampType target = null;
            target = (XAdESTimeStampType)((Object)this.get_store().find_element_user(PROPERTY_QNAME[1], i));
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
    public int sizeOfSignatureTimeStampArray() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[1]);
        }
    }

    @Override
    public void setSignatureTimeStampArray(XAdESTimeStampType[] signatureTimeStampArray) {
        this.check_orphaned();
        this.arraySetterHelper(signatureTimeStampArray, PROPERTY_QNAME[1]);
    }

    @Override
    public void setSignatureTimeStampArray(int i, XAdESTimeStampType signatureTimeStamp) {
        this.generatedSetterHelperImpl(signatureTimeStamp, PROPERTY_QNAME[1], i, (short)2);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public XAdESTimeStampType insertNewSignatureTimeStamp(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            XAdESTimeStampType target = null;
            target = (XAdESTimeStampType)((Object)this.get_store().insert_element_user(PROPERTY_QNAME[1], i));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public XAdESTimeStampType addNewSignatureTimeStamp() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            XAdESTimeStampType target = null;
            target = (XAdESTimeStampType)((Object)this.get_store().add_element_user(PROPERTY_QNAME[1]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void removeSignatureTimeStamp(int i) {
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
    public List<CompleteCertificateRefsType> getCompleteCertificateRefsList() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return new JavaListXmlObject<CompleteCertificateRefsType>(this::getCompleteCertificateRefsArray, this::setCompleteCertificateRefsArray, this::insertNewCompleteCertificateRefs, this::removeCompleteCertificateRefs, this::sizeOfCompleteCertificateRefsArray);
        }
    }

    @Override
    public CompleteCertificateRefsType[] getCompleteCertificateRefsArray() {
        return (CompleteCertificateRefsType[])this.getXmlObjectArray(PROPERTY_QNAME[2], new CompleteCertificateRefsType[0]);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CompleteCertificateRefsType getCompleteCertificateRefsArray(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CompleteCertificateRefsType target = null;
            target = (CompleteCertificateRefsType)((Object)this.get_store().find_element_user(PROPERTY_QNAME[2], i));
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
    public int sizeOfCompleteCertificateRefsArray() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[2]);
        }
    }

    @Override
    public void setCompleteCertificateRefsArray(CompleteCertificateRefsType[] completeCertificateRefsArray) {
        this.check_orphaned();
        this.arraySetterHelper(completeCertificateRefsArray, PROPERTY_QNAME[2]);
    }

    @Override
    public void setCompleteCertificateRefsArray(int i, CompleteCertificateRefsType completeCertificateRefs) {
        this.generatedSetterHelperImpl(completeCertificateRefs, PROPERTY_QNAME[2], i, (short)2);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CompleteCertificateRefsType insertNewCompleteCertificateRefs(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CompleteCertificateRefsType target = null;
            target = (CompleteCertificateRefsType)((Object)this.get_store().insert_element_user(PROPERTY_QNAME[2], i));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CompleteCertificateRefsType addNewCompleteCertificateRefs() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CompleteCertificateRefsType target = null;
            target = (CompleteCertificateRefsType)((Object)this.get_store().add_element_user(PROPERTY_QNAME[2]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void removeCompleteCertificateRefs(int i) {
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
    public List<CompleteRevocationRefsType> getCompleteRevocationRefsList() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return new JavaListXmlObject<CompleteRevocationRefsType>(this::getCompleteRevocationRefsArray, this::setCompleteRevocationRefsArray, this::insertNewCompleteRevocationRefs, this::removeCompleteRevocationRefs, this::sizeOfCompleteRevocationRefsArray);
        }
    }

    @Override
    public CompleteRevocationRefsType[] getCompleteRevocationRefsArray() {
        return (CompleteRevocationRefsType[])this.getXmlObjectArray(PROPERTY_QNAME[3], new CompleteRevocationRefsType[0]);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CompleteRevocationRefsType getCompleteRevocationRefsArray(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CompleteRevocationRefsType target = null;
            target = (CompleteRevocationRefsType)((Object)this.get_store().find_element_user(PROPERTY_QNAME[3], i));
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
    public int sizeOfCompleteRevocationRefsArray() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[3]);
        }
    }

    @Override
    public void setCompleteRevocationRefsArray(CompleteRevocationRefsType[] completeRevocationRefsArray) {
        this.check_orphaned();
        this.arraySetterHelper(completeRevocationRefsArray, PROPERTY_QNAME[3]);
    }

    @Override
    public void setCompleteRevocationRefsArray(int i, CompleteRevocationRefsType completeRevocationRefs) {
        this.generatedSetterHelperImpl(completeRevocationRefs, PROPERTY_QNAME[3], i, (short)2);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CompleteRevocationRefsType insertNewCompleteRevocationRefs(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CompleteRevocationRefsType target = null;
            target = (CompleteRevocationRefsType)((Object)this.get_store().insert_element_user(PROPERTY_QNAME[3], i));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CompleteRevocationRefsType addNewCompleteRevocationRefs() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CompleteRevocationRefsType target = null;
            target = (CompleteRevocationRefsType)((Object)this.get_store().add_element_user(PROPERTY_QNAME[3]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void removeCompleteRevocationRefs(int i) {
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
    public List<CompleteCertificateRefsType> getAttributeCertificateRefsList() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return new JavaListXmlObject<CompleteCertificateRefsType>(this::getAttributeCertificateRefsArray, this::setAttributeCertificateRefsArray, this::insertNewAttributeCertificateRefs, this::removeAttributeCertificateRefs, this::sizeOfAttributeCertificateRefsArray);
        }
    }

    @Override
    public CompleteCertificateRefsType[] getAttributeCertificateRefsArray() {
        return (CompleteCertificateRefsType[])this.getXmlObjectArray(PROPERTY_QNAME[4], new CompleteCertificateRefsType[0]);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CompleteCertificateRefsType getAttributeCertificateRefsArray(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CompleteCertificateRefsType target = null;
            target = (CompleteCertificateRefsType)((Object)this.get_store().find_element_user(PROPERTY_QNAME[4], i));
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
    public int sizeOfAttributeCertificateRefsArray() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[4]);
        }
    }

    @Override
    public void setAttributeCertificateRefsArray(CompleteCertificateRefsType[] attributeCertificateRefsArray) {
        this.check_orphaned();
        this.arraySetterHelper(attributeCertificateRefsArray, PROPERTY_QNAME[4]);
    }

    @Override
    public void setAttributeCertificateRefsArray(int i, CompleteCertificateRefsType attributeCertificateRefs) {
        this.generatedSetterHelperImpl(attributeCertificateRefs, PROPERTY_QNAME[4], i, (short)2);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CompleteCertificateRefsType insertNewAttributeCertificateRefs(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CompleteCertificateRefsType target = null;
            target = (CompleteCertificateRefsType)((Object)this.get_store().insert_element_user(PROPERTY_QNAME[4], i));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CompleteCertificateRefsType addNewAttributeCertificateRefs() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CompleteCertificateRefsType target = null;
            target = (CompleteCertificateRefsType)((Object)this.get_store().add_element_user(PROPERTY_QNAME[4]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void removeAttributeCertificateRefs(int i) {
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
    public List<CompleteRevocationRefsType> getAttributeRevocationRefsList() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return new JavaListXmlObject<CompleteRevocationRefsType>(this::getAttributeRevocationRefsArray, this::setAttributeRevocationRefsArray, this::insertNewAttributeRevocationRefs, this::removeAttributeRevocationRefs, this::sizeOfAttributeRevocationRefsArray);
        }
    }

    @Override
    public CompleteRevocationRefsType[] getAttributeRevocationRefsArray() {
        return (CompleteRevocationRefsType[])this.getXmlObjectArray(PROPERTY_QNAME[5], new CompleteRevocationRefsType[0]);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CompleteRevocationRefsType getAttributeRevocationRefsArray(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CompleteRevocationRefsType target = null;
            target = (CompleteRevocationRefsType)((Object)this.get_store().find_element_user(PROPERTY_QNAME[5], i));
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
    public int sizeOfAttributeRevocationRefsArray() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[5]);
        }
    }

    @Override
    public void setAttributeRevocationRefsArray(CompleteRevocationRefsType[] attributeRevocationRefsArray) {
        this.check_orphaned();
        this.arraySetterHelper(attributeRevocationRefsArray, PROPERTY_QNAME[5]);
    }

    @Override
    public void setAttributeRevocationRefsArray(int i, CompleteRevocationRefsType attributeRevocationRefs) {
        this.generatedSetterHelperImpl(attributeRevocationRefs, PROPERTY_QNAME[5], i, (short)2);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CompleteRevocationRefsType insertNewAttributeRevocationRefs(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CompleteRevocationRefsType target = null;
            target = (CompleteRevocationRefsType)((Object)this.get_store().insert_element_user(PROPERTY_QNAME[5], i));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CompleteRevocationRefsType addNewAttributeRevocationRefs() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CompleteRevocationRefsType target = null;
            target = (CompleteRevocationRefsType)((Object)this.get_store().add_element_user(PROPERTY_QNAME[5]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void removeAttributeRevocationRefs(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.get_store().remove_element(PROPERTY_QNAME[5], i);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public List<XAdESTimeStampType> getSigAndRefsTimeStampList() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return new JavaListXmlObject<XAdESTimeStampType>(this::getSigAndRefsTimeStampArray, this::setSigAndRefsTimeStampArray, this::insertNewSigAndRefsTimeStamp, this::removeSigAndRefsTimeStamp, this::sizeOfSigAndRefsTimeStampArray);
        }
    }

    @Override
    public XAdESTimeStampType[] getSigAndRefsTimeStampArray() {
        return (XAdESTimeStampType[])this.getXmlObjectArray(PROPERTY_QNAME[6], new XAdESTimeStampType[0]);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public XAdESTimeStampType getSigAndRefsTimeStampArray(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            XAdESTimeStampType target = null;
            target = (XAdESTimeStampType)((Object)this.get_store().find_element_user(PROPERTY_QNAME[6], i));
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
    public int sizeOfSigAndRefsTimeStampArray() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[6]);
        }
    }

    @Override
    public void setSigAndRefsTimeStampArray(XAdESTimeStampType[] sigAndRefsTimeStampArray) {
        this.check_orphaned();
        this.arraySetterHelper(sigAndRefsTimeStampArray, PROPERTY_QNAME[6]);
    }

    @Override
    public void setSigAndRefsTimeStampArray(int i, XAdESTimeStampType sigAndRefsTimeStamp) {
        this.generatedSetterHelperImpl(sigAndRefsTimeStamp, PROPERTY_QNAME[6], i, (short)2);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public XAdESTimeStampType insertNewSigAndRefsTimeStamp(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            XAdESTimeStampType target = null;
            target = (XAdESTimeStampType)((Object)this.get_store().insert_element_user(PROPERTY_QNAME[6], i));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public XAdESTimeStampType addNewSigAndRefsTimeStamp() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            XAdESTimeStampType target = null;
            target = (XAdESTimeStampType)((Object)this.get_store().add_element_user(PROPERTY_QNAME[6]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void removeSigAndRefsTimeStamp(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.get_store().remove_element(PROPERTY_QNAME[6], i);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public List<XAdESTimeStampType> getRefsOnlyTimeStampList() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return new JavaListXmlObject<XAdESTimeStampType>(this::getRefsOnlyTimeStampArray, this::setRefsOnlyTimeStampArray, this::insertNewRefsOnlyTimeStamp, this::removeRefsOnlyTimeStamp, this::sizeOfRefsOnlyTimeStampArray);
        }
    }

    @Override
    public XAdESTimeStampType[] getRefsOnlyTimeStampArray() {
        return (XAdESTimeStampType[])this.getXmlObjectArray(PROPERTY_QNAME[7], new XAdESTimeStampType[0]);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public XAdESTimeStampType getRefsOnlyTimeStampArray(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            XAdESTimeStampType target = null;
            target = (XAdESTimeStampType)((Object)this.get_store().find_element_user(PROPERTY_QNAME[7], i));
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
    public int sizeOfRefsOnlyTimeStampArray() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[7]);
        }
    }

    @Override
    public void setRefsOnlyTimeStampArray(XAdESTimeStampType[] refsOnlyTimeStampArray) {
        this.check_orphaned();
        this.arraySetterHelper(refsOnlyTimeStampArray, PROPERTY_QNAME[7]);
    }

    @Override
    public void setRefsOnlyTimeStampArray(int i, XAdESTimeStampType refsOnlyTimeStamp) {
        this.generatedSetterHelperImpl(refsOnlyTimeStamp, PROPERTY_QNAME[7], i, (short)2);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public XAdESTimeStampType insertNewRefsOnlyTimeStamp(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            XAdESTimeStampType target = null;
            target = (XAdESTimeStampType)((Object)this.get_store().insert_element_user(PROPERTY_QNAME[7], i));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public XAdESTimeStampType addNewRefsOnlyTimeStamp() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            XAdESTimeStampType target = null;
            target = (XAdESTimeStampType)((Object)this.get_store().add_element_user(PROPERTY_QNAME[7]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void removeRefsOnlyTimeStamp(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.get_store().remove_element(PROPERTY_QNAME[7], i);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public List<CertificateValuesType> getCertificateValuesList() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return new JavaListXmlObject<CertificateValuesType>(this::getCertificateValuesArray, this::setCertificateValuesArray, this::insertNewCertificateValues, this::removeCertificateValues, this::sizeOfCertificateValuesArray);
        }
    }

    @Override
    public CertificateValuesType[] getCertificateValuesArray() {
        return (CertificateValuesType[])this.getXmlObjectArray(PROPERTY_QNAME[8], new CertificateValuesType[0]);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CertificateValuesType getCertificateValuesArray(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CertificateValuesType target = null;
            target = (CertificateValuesType)((Object)this.get_store().find_element_user(PROPERTY_QNAME[8], i));
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
    public int sizeOfCertificateValuesArray() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[8]);
        }
    }

    @Override
    public void setCertificateValuesArray(CertificateValuesType[] certificateValuesArray) {
        this.check_orphaned();
        this.arraySetterHelper(certificateValuesArray, PROPERTY_QNAME[8]);
    }

    @Override
    public void setCertificateValuesArray(int i, CertificateValuesType certificateValues) {
        this.generatedSetterHelperImpl(certificateValues, PROPERTY_QNAME[8], i, (short)2);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CertificateValuesType insertNewCertificateValues(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CertificateValuesType target = null;
            target = (CertificateValuesType)((Object)this.get_store().insert_element_user(PROPERTY_QNAME[8], i));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CertificateValuesType addNewCertificateValues() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CertificateValuesType target = null;
            target = (CertificateValuesType)((Object)this.get_store().add_element_user(PROPERTY_QNAME[8]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void removeCertificateValues(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.get_store().remove_element(PROPERTY_QNAME[8], i);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public List<RevocationValuesType> getRevocationValuesList() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return new JavaListXmlObject<RevocationValuesType>(this::getRevocationValuesArray, this::setRevocationValuesArray, this::insertNewRevocationValues, this::removeRevocationValues, this::sizeOfRevocationValuesArray);
        }
    }

    @Override
    public RevocationValuesType[] getRevocationValuesArray() {
        return (RevocationValuesType[])this.getXmlObjectArray(PROPERTY_QNAME[9], new RevocationValuesType[0]);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public RevocationValuesType getRevocationValuesArray(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            RevocationValuesType target = null;
            target = (RevocationValuesType)((Object)this.get_store().find_element_user(PROPERTY_QNAME[9], i));
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
    public int sizeOfRevocationValuesArray() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[9]);
        }
    }

    @Override
    public void setRevocationValuesArray(RevocationValuesType[] revocationValuesArray) {
        this.check_orphaned();
        this.arraySetterHelper(revocationValuesArray, PROPERTY_QNAME[9]);
    }

    @Override
    public void setRevocationValuesArray(int i, RevocationValuesType revocationValues) {
        this.generatedSetterHelperImpl(revocationValues, PROPERTY_QNAME[9], i, (short)2);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public RevocationValuesType insertNewRevocationValues(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            RevocationValuesType target = null;
            target = (RevocationValuesType)((Object)this.get_store().insert_element_user(PROPERTY_QNAME[9], i));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public RevocationValuesType addNewRevocationValues() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            RevocationValuesType target = null;
            target = (RevocationValuesType)((Object)this.get_store().add_element_user(PROPERTY_QNAME[9]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void removeRevocationValues(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.get_store().remove_element(PROPERTY_QNAME[9], i);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public List<CertificateValuesType> getAttrAuthoritiesCertValuesList() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return new JavaListXmlObject<CertificateValuesType>(this::getAttrAuthoritiesCertValuesArray, this::setAttrAuthoritiesCertValuesArray, this::insertNewAttrAuthoritiesCertValues, this::removeAttrAuthoritiesCertValues, this::sizeOfAttrAuthoritiesCertValuesArray);
        }
    }

    @Override
    public CertificateValuesType[] getAttrAuthoritiesCertValuesArray() {
        return (CertificateValuesType[])this.getXmlObjectArray(PROPERTY_QNAME[10], new CertificateValuesType[0]);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CertificateValuesType getAttrAuthoritiesCertValuesArray(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CertificateValuesType target = null;
            target = (CertificateValuesType)((Object)this.get_store().find_element_user(PROPERTY_QNAME[10], i));
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
    public int sizeOfAttrAuthoritiesCertValuesArray() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[10]);
        }
    }

    @Override
    public void setAttrAuthoritiesCertValuesArray(CertificateValuesType[] attrAuthoritiesCertValuesArray) {
        this.check_orphaned();
        this.arraySetterHelper(attrAuthoritiesCertValuesArray, PROPERTY_QNAME[10]);
    }

    @Override
    public void setAttrAuthoritiesCertValuesArray(int i, CertificateValuesType attrAuthoritiesCertValues) {
        this.generatedSetterHelperImpl(attrAuthoritiesCertValues, PROPERTY_QNAME[10], i, (short)2);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CertificateValuesType insertNewAttrAuthoritiesCertValues(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CertificateValuesType target = null;
            target = (CertificateValuesType)((Object)this.get_store().insert_element_user(PROPERTY_QNAME[10], i));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CertificateValuesType addNewAttrAuthoritiesCertValues() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CertificateValuesType target = null;
            target = (CertificateValuesType)((Object)this.get_store().add_element_user(PROPERTY_QNAME[10]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void removeAttrAuthoritiesCertValues(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.get_store().remove_element(PROPERTY_QNAME[10], i);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public List<RevocationValuesType> getAttributeRevocationValuesList() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return new JavaListXmlObject<RevocationValuesType>(this::getAttributeRevocationValuesArray, this::setAttributeRevocationValuesArray, this::insertNewAttributeRevocationValues, this::removeAttributeRevocationValues, this::sizeOfAttributeRevocationValuesArray);
        }
    }

    @Override
    public RevocationValuesType[] getAttributeRevocationValuesArray() {
        return (RevocationValuesType[])this.getXmlObjectArray(PROPERTY_QNAME[11], new RevocationValuesType[0]);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public RevocationValuesType getAttributeRevocationValuesArray(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            RevocationValuesType target = null;
            target = (RevocationValuesType)((Object)this.get_store().find_element_user(PROPERTY_QNAME[11], i));
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
    public int sizeOfAttributeRevocationValuesArray() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[11]);
        }
    }

    @Override
    public void setAttributeRevocationValuesArray(RevocationValuesType[] attributeRevocationValuesArray) {
        this.check_orphaned();
        this.arraySetterHelper(attributeRevocationValuesArray, PROPERTY_QNAME[11]);
    }

    @Override
    public void setAttributeRevocationValuesArray(int i, RevocationValuesType attributeRevocationValues) {
        this.generatedSetterHelperImpl(attributeRevocationValues, PROPERTY_QNAME[11], i, (short)2);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public RevocationValuesType insertNewAttributeRevocationValues(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            RevocationValuesType target = null;
            target = (RevocationValuesType)((Object)this.get_store().insert_element_user(PROPERTY_QNAME[11], i));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public RevocationValuesType addNewAttributeRevocationValues() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            RevocationValuesType target = null;
            target = (RevocationValuesType)((Object)this.get_store().add_element_user(PROPERTY_QNAME[11]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void removeAttributeRevocationValues(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.get_store().remove_element(PROPERTY_QNAME[11], i);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public List<XAdESTimeStampType> getArchiveTimeStampList() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return new JavaListXmlObject<XAdESTimeStampType>(this::getArchiveTimeStampArray, this::setArchiveTimeStampArray, this::insertNewArchiveTimeStamp, this::removeArchiveTimeStamp, this::sizeOfArchiveTimeStampArray);
        }
    }

    @Override
    public XAdESTimeStampType[] getArchiveTimeStampArray() {
        return (XAdESTimeStampType[])this.getXmlObjectArray(PROPERTY_QNAME[12], new XAdESTimeStampType[0]);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public XAdESTimeStampType getArchiveTimeStampArray(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            XAdESTimeStampType target = null;
            target = (XAdESTimeStampType)((Object)this.get_store().find_element_user(PROPERTY_QNAME[12], i));
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
    public int sizeOfArchiveTimeStampArray() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[12]);
        }
    }

    @Override
    public void setArchiveTimeStampArray(XAdESTimeStampType[] archiveTimeStampArray) {
        this.check_orphaned();
        this.arraySetterHelper(archiveTimeStampArray, PROPERTY_QNAME[12]);
    }

    @Override
    public void setArchiveTimeStampArray(int i, XAdESTimeStampType archiveTimeStamp) {
        this.generatedSetterHelperImpl(archiveTimeStamp, PROPERTY_QNAME[12], i, (short)2);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public XAdESTimeStampType insertNewArchiveTimeStamp(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            XAdESTimeStampType target = null;
            target = (XAdESTimeStampType)((Object)this.get_store().insert_element_user(PROPERTY_QNAME[12], i));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public XAdESTimeStampType addNewArchiveTimeStamp() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            XAdESTimeStampType target = null;
            target = (XAdESTimeStampType)((Object)this.get_store().add_element_user(PROPERTY_QNAME[12]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void removeArchiveTimeStamp(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.get_store().remove_element(PROPERTY_QNAME[12], i);
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
            target = (SimpleValue)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[13]));
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
            target = (XmlID)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[13]));
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
            return this.get_store().find_attribute_user(PROPERTY_QNAME[13]) != null;
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
            target = (SimpleValue)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[13]));
            if (target == null) {
                target = (SimpleValue)((Object)this.get_store().add_attribute_user(PROPERTY_QNAME[13]));
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
            target = (XmlID)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[13]));
            if (target == null) {
                target = (XmlID)((Object)this.get_store().add_attribute_user(PROPERTY_QNAME[13]));
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
            this.get_store().remove_attribute(PROPERTY_QNAME[13]);
        }
    }
}


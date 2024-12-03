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
import org.etsi.uri.x01903.v13.CommitmentTypeIndicationType;
import org.etsi.uri.x01903.v13.DataObjectFormatType;
import org.etsi.uri.x01903.v13.SignedDataObjectPropertiesType;
import org.etsi.uri.x01903.v13.XAdESTimeStampType;

public class SignedDataObjectPropertiesTypeImpl
extends XmlComplexContentImpl
implements SignedDataObjectPropertiesType {
    private static final long serialVersionUID = 1L;
    private static final QName[] PROPERTY_QNAME = new QName[]{new QName("http://uri.etsi.org/01903/v1.3.2#", "DataObjectFormat"), new QName("http://uri.etsi.org/01903/v1.3.2#", "CommitmentTypeIndication"), new QName("http://uri.etsi.org/01903/v1.3.2#", "AllDataObjectsTimeStamp"), new QName("http://uri.etsi.org/01903/v1.3.2#", "IndividualDataObjectsTimeStamp"), new QName("", "Id")};

    public SignedDataObjectPropertiesTypeImpl(SchemaType sType) {
        super(sType);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public List<DataObjectFormatType> getDataObjectFormatList() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return new JavaListXmlObject<DataObjectFormatType>(this::getDataObjectFormatArray, this::setDataObjectFormatArray, this::insertNewDataObjectFormat, this::removeDataObjectFormat, this::sizeOfDataObjectFormatArray);
        }
    }

    @Override
    public DataObjectFormatType[] getDataObjectFormatArray() {
        return (DataObjectFormatType[])this.getXmlObjectArray(PROPERTY_QNAME[0], new DataObjectFormatType[0]);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public DataObjectFormatType getDataObjectFormatArray(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            DataObjectFormatType target = null;
            target = (DataObjectFormatType)((Object)this.get_store().find_element_user(PROPERTY_QNAME[0], i));
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
    public int sizeOfDataObjectFormatArray() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[0]);
        }
    }

    @Override
    public void setDataObjectFormatArray(DataObjectFormatType[] dataObjectFormatArray) {
        this.check_orphaned();
        this.arraySetterHelper(dataObjectFormatArray, PROPERTY_QNAME[0]);
    }

    @Override
    public void setDataObjectFormatArray(int i, DataObjectFormatType dataObjectFormat) {
        this.generatedSetterHelperImpl(dataObjectFormat, PROPERTY_QNAME[0], i, (short)2);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public DataObjectFormatType insertNewDataObjectFormat(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            DataObjectFormatType target = null;
            target = (DataObjectFormatType)((Object)this.get_store().insert_element_user(PROPERTY_QNAME[0], i));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public DataObjectFormatType addNewDataObjectFormat() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            DataObjectFormatType target = null;
            target = (DataObjectFormatType)((Object)this.get_store().add_element_user(PROPERTY_QNAME[0]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void removeDataObjectFormat(int i) {
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
    public List<CommitmentTypeIndicationType> getCommitmentTypeIndicationList() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return new JavaListXmlObject<CommitmentTypeIndicationType>(this::getCommitmentTypeIndicationArray, this::setCommitmentTypeIndicationArray, this::insertNewCommitmentTypeIndication, this::removeCommitmentTypeIndication, this::sizeOfCommitmentTypeIndicationArray);
        }
    }

    @Override
    public CommitmentTypeIndicationType[] getCommitmentTypeIndicationArray() {
        return (CommitmentTypeIndicationType[])this.getXmlObjectArray(PROPERTY_QNAME[1], new CommitmentTypeIndicationType[0]);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CommitmentTypeIndicationType getCommitmentTypeIndicationArray(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CommitmentTypeIndicationType target = null;
            target = (CommitmentTypeIndicationType)((Object)this.get_store().find_element_user(PROPERTY_QNAME[1], i));
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
    public int sizeOfCommitmentTypeIndicationArray() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[1]);
        }
    }

    @Override
    public void setCommitmentTypeIndicationArray(CommitmentTypeIndicationType[] commitmentTypeIndicationArray) {
        this.check_orphaned();
        this.arraySetterHelper(commitmentTypeIndicationArray, PROPERTY_QNAME[1]);
    }

    @Override
    public void setCommitmentTypeIndicationArray(int i, CommitmentTypeIndicationType commitmentTypeIndication) {
        this.generatedSetterHelperImpl(commitmentTypeIndication, PROPERTY_QNAME[1], i, (short)2);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CommitmentTypeIndicationType insertNewCommitmentTypeIndication(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CommitmentTypeIndicationType target = null;
            target = (CommitmentTypeIndicationType)((Object)this.get_store().insert_element_user(PROPERTY_QNAME[1], i));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CommitmentTypeIndicationType addNewCommitmentTypeIndication() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CommitmentTypeIndicationType target = null;
            target = (CommitmentTypeIndicationType)((Object)this.get_store().add_element_user(PROPERTY_QNAME[1]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void removeCommitmentTypeIndication(int i) {
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
    public List<XAdESTimeStampType> getAllDataObjectsTimeStampList() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return new JavaListXmlObject<XAdESTimeStampType>(this::getAllDataObjectsTimeStampArray, this::setAllDataObjectsTimeStampArray, this::insertNewAllDataObjectsTimeStamp, this::removeAllDataObjectsTimeStamp, this::sizeOfAllDataObjectsTimeStampArray);
        }
    }

    @Override
    public XAdESTimeStampType[] getAllDataObjectsTimeStampArray() {
        return (XAdESTimeStampType[])this.getXmlObjectArray(PROPERTY_QNAME[2], new XAdESTimeStampType[0]);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public XAdESTimeStampType getAllDataObjectsTimeStampArray(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            XAdESTimeStampType target = null;
            target = (XAdESTimeStampType)((Object)this.get_store().find_element_user(PROPERTY_QNAME[2], i));
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
    public int sizeOfAllDataObjectsTimeStampArray() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[2]);
        }
    }

    @Override
    public void setAllDataObjectsTimeStampArray(XAdESTimeStampType[] allDataObjectsTimeStampArray) {
        this.check_orphaned();
        this.arraySetterHelper(allDataObjectsTimeStampArray, PROPERTY_QNAME[2]);
    }

    @Override
    public void setAllDataObjectsTimeStampArray(int i, XAdESTimeStampType allDataObjectsTimeStamp) {
        this.generatedSetterHelperImpl(allDataObjectsTimeStamp, PROPERTY_QNAME[2], i, (short)2);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public XAdESTimeStampType insertNewAllDataObjectsTimeStamp(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            XAdESTimeStampType target = null;
            target = (XAdESTimeStampType)((Object)this.get_store().insert_element_user(PROPERTY_QNAME[2], i));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public XAdESTimeStampType addNewAllDataObjectsTimeStamp() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            XAdESTimeStampType target = null;
            target = (XAdESTimeStampType)((Object)this.get_store().add_element_user(PROPERTY_QNAME[2]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void removeAllDataObjectsTimeStamp(int i) {
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
    public List<XAdESTimeStampType> getIndividualDataObjectsTimeStampList() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return new JavaListXmlObject<XAdESTimeStampType>(this::getIndividualDataObjectsTimeStampArray, this::setIndividualDataObjectsTimeStampArray, this::insertNewIndividualDataObjectsTimeStamp, this::removeIndividualDataObjectsTimeStamp, this::sizeOfIndividualDataObjectsTimeStampArray);
        }
    }

    @Override
    public XAdESTimeStampType[] getIndividualDataObjectsTimeStampArray() {
        return (XAdESTimeStampType[])this.getXmlObjectArray(PROPERTY_QNAME[3], new XAdESTimeStampType[0]);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public XAdESTimeStampType getIndividualDataObjectsTimeStampArray(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            XAdESTimeStampType target = null;
            target = (XAdESTimeStampType)((Object)this.get_store().find_element_user(PROPERTY_QNAME[3], i));
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
    public int sizeOfIndividualDataObjectsTimeStampArray() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[3]);
        }
    }

    @Override
    public void setIndividualDataObjectsTimeStampArray(XAdESTimeStampType[] individualDataObjectsTimeStampArray) {
        this.check_orphaned();
        this.arraySetterHelper(individualDataObjectsTimeStampArray, PROPERTY_QNAME[3]);
    }

    @Override
    public void setIndividualDataObjectsTimeStampArray(int i, XAdESTimeStampType individualDataObjectsTimeStamp) {
        this.generatedSetterHelperImpl(individualDataObjectsTimeStamp, PROPERTY_QNAME[3], i, (short)2);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public XAdESTimeStampType insertNewIndividualDataObjectsTimeStamp(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            XAdESTimeStampType target = null;
            target = (XAdESTimeStampType)((Object)this.get_store().insert_element_user(PROPERTY_QNAME[3], i));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public XAdESTimeStampType addNewIndividualDataObjectsTimeStamp() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            XAdESTimeStampType target = null;
            target = (XAdESTimeStampType)((Object)this.get_store().add_element_user(PROPERTY_QNAME[3]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void removeIndividualDataObjectsTimeStamp(int i) {
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


/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.openxmlformats.schemas.presentationml.x2006.main.CTCustomerData
 */
package org.openxmlformats.schemas.presentationml.x2006.main.impl;

import java.util.List;
import javax.xml.namespace.QName;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.impl.values.JavaListXmlObject;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;
import org.openxmlformats.schemas.presentationml.x2006.main.CTCustomerData;
import org.openxmlformats.schemas.presentationml.x2006.main.CTCustomerDataList;
import org.openxmlformats.schemas.presentationml.x2006.main.CTTagsData;

public class CTCustomerDataListImpl
extends XmlComplexContentImpl
implements CTCustomerDataList {
    private static final long serialVersionUID = 1L;
    private static final QName[] PROPERTY_QNAME = new QName[]{new QName("http://schemas.openxmlformats.org/presentationml/2006/main", "custData"), new QName("http://schemas.openxmlformats.org/presentationml/2006/main", "tags")};

    public CTCustomerDataListImpl(SchemaType sType) {
        super(sType);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public List<CTCustomerData> getCustDataList() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return new JavaListXmlObject<CTCustomerData>(this::getCustDataArray, this::setCustDataArray, this::insertNewCustData, this::removeCustData, this::sizeOfCustDataArray);
        }
    }

    @Override
    public CTCustomerData[] getCustDataArray() {
        return (CTCustomerData[])this.getXmlObjectArray(PROPERTY_QNAME[0], (XmlObject[])new CTCustomerData[0]);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTCustomerData getCustDataArray(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTCustomerData target = null;
            target = (CTCustomerData)this.get_store().find_element_user(PROPERTY_QNAME[0], i);
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
    public int sizeOfCustDataArray() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[0]);
        }
    }

    @Override
    public void setCustDataArray(CTCustomerData[] custDataArray) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])custDataArray, PROPERTY_QNAME[0]);
    }

    @Override
    public void setCustDataArray(int i, CTCustomerData custData) {
        this.generatedSetterHelperImpl((XmlObject)custData, PROPERTY_QNAME[0], i, (short)2);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTCustomerData insertNewCustData(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTCustomerData target = null;
            target = (CTCustomerData)this.get_store().insert_element_user(PROPERTY_QNAME[0], i);
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTCustomerData addNewCustData() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTCustomerData target = null;
            target = (CTCustomerData)this.get_store().add_element_user(PROPERTY_QNAME[0]);
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void removeCustData(int i) {
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
    public CTTagsData getTags() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTTagsData target = null;
            target = (CTTagsData)((Object)this.get_store().find_element_user(PROPERTY_QNAME[1], 0));
            return target == null ? null : target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean isSetTags() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[1]) != 0;
        }
    }

    @Override
    public void setTags(CTTagsData tags) {
        this.generatedSetterHelperImpl(tags, PROPERTY_QNAME[1], 0, (short)1);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTTagsData addNewTags() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTTagsData target = null;
            target = (CTTagsData)((Object)this.get_store().add_element_user(PROPERTY_QNAME[1]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void unsetTags() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.get_store().remove_element(PROPERTY_QNAME[1], 0);
        }
    }
}


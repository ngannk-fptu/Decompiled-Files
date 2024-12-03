/*
 * Decompiled with CFR 0.152.
 */
package org.openxmlformats.schemas.presentationml.x2006.main.impl;

import java.util.List;
import javax.xml.namespace.QName;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.impl.values.JavaListXmlObject;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;
import org.openxmlformats.schemas.presentationml.x2006.main.CTSlideMasterIdList;
import org.openxmlformats.schemas.presentationml.x2006.main.CTSlideMasterIdListEntry;

public class CTSlideMasterIdListImpl
extends XmlComplexContentImpl
implements CTSlideMasterIdList {
    private static final long serialVersionUID = 1L;
    private static final QName[] PROPERTY_QNAME = new QName[]{new QName("http://schemas.openxmlformats.org/presentationml/2006/main", "sldMasterId")};

    public CTSlideMasterIdListImpl(SchemaType sType) {
        super(sType);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public List<CTSlideMasterIdListEntry> getSldMasterIdList() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return new JavaListXmlObject<CTSlideMasterIdListEntry>(this::getSldMasterIdArray, this::setSldMasterIdArray, this::insertNewSldMasterId, this::removeSldMasterId, this::sizeOfSldMasterIdArray);
        }
    }

    @Override
    public CTSlideMasterIdListEntry[] getSldMasterIdArray() {
        return (CTSlideMasterIdListEntry[])this.getXmlObjectArray(PROPERTY_QNAME[0], new CTSlideMasterIdListEntry[0]);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTSlideMasterIdListEntry getSldMasterIdArray(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTSlideMasterIdListEntry target = null;
            target = (CTSlideMasterIdListEntry)((Object)this.get_store().find_element_user(PROPERTY_QNAME[0], i));
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
    public int sizeOfSldMasterIdArray() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[0]);
        }
    }

    @Override
    public void setSldMasterIdArray(CTSlideMasterIdListEntry[] sldMasterIdArray) {
        this.check_orphaned();
        this.arraySetterHelper(sldMasterIdArray, PROPERTY_QNAME[0]);
    }

    @Override
    public void setSldMasterIdArray(int i, CTSlideMasterIdListEntry sldMasterId) {
        this.generatedSetterHelperImpl(sldMasterId, PROPERTY_QNAME[0], i, (short)2);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTSlideMasterIdListEntry insertNewSldMasterId(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTSlideMasterIdListEntry target = null;
            target = (CTSlideMasterIdListEntry)((Object)this.get_store().insert_element_user(PROPERTY_QNAME[0], i));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTSlideMasterIdListEntry addNewSldMasterId() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTSlideMasterIdListEntry target = null;
            target = (CTSlideMasterIdListEntry)((Object)this.get_store().add_element_user(PROPERTY_QNAME[0]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void removeSldMasterId(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.get_store().remove_element(PROPERTY_QNAME[0], i);
        }
    }
}


/*
 * Decompiled with CFR 0.152.
 */
package org.openxmlformats.schemas.presentationml.x2006.main.impl;

import java.util.List;
import javax.xml.namespace.QName;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.impl.values.JavaListXmlObject;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;
import org.openxmlformats.schemas.presentationml.x2006.main.CTSlideIdList;
import org.openxmlformats.schemas.presentationml.x2006.main.CTSlideIdListEntry;

public class CTSlideIdListImpl
extends XmlComplexContentImpl
implements CTSlideIdList {
    private static final long serialVersionUID = 1L;
    private static final QName[] PROPERTY_QNAME = new QName[]{new QName("http://schemas.openxmlformats.org/presentationml/2006/main", "sldId")};

    public CTSlideIdListImpl(SchemaType sType) {
        super(sType);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public List<CTSlideIdListEntry> getSldIdList() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return new JavaListXmlObject<CTSlideIdListEntry>(this::getSldIdArray, this::setSldIdArray, this::insertNewSldId, this::removeSldId, this::sizeOfSldIdArray);
        }
    }

    @Override
    public CTSlideIdListEntry[] getSldIdArray() {
        return (CTSlideIdListEntry[])this.getXmlObjectArray(PROPERTY_QNAME[0], new CTSlideIdListEntry[0]);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTSlideIdListEntry getSldIdArray(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTSlideIdListEntry target = null;
            target = (CTSlideIdListEntry)((Object)this.get_store().find_element_user(PROPERTY_QNAME[0], i));
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
    public int sizeOfSldIdArray() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[0]);
        }
    }

    @Override
    public void setSldIdArray(CTSlideIdListEntry[] sldIdArray) {
        this.check_orphaned();
        this.arraySetterHelper(sldIdArray, PROPERTY_QNAME[0]);
    }

    @Override
    public void setSldIdArray(int i, CTSlideIdListEntry sldId) {
        this.generatedSetterHelperImpl(sldId, PROPERTY_QNAME[0], i, (short)2);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTSlideIdListEntry insertNewSldId(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTSlideIdListEntry target = null;
            target = (CTSlideIdListEntry)((Object)this.get_store().insert_element_user(PROPERTY_QNAME[0], i));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTSlideIdListEntry addNewSldId() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTSlideIdListEntry target = null;
            target = (CTSlideIdListEntry)((Object)this.get_store().add_element_user(PROPERTY_QNAME[0]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void removeSldId(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.get_store().remove_element(PROPERTY_QNAME[0], i);
        }
    }
}


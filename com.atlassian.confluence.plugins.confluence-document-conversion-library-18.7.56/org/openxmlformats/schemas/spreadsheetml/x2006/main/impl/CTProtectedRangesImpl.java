/*
 * Decompiled with CFR 0.152.
 */
package org.openxmlformats.schemas.spreadsheetml.x2006.main.impl;

import java.util.List;
import javax.xml.namespace.QName;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.impl.values.JavaListXmlObject;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTProtectedRange;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTProtectedRanges;

public class CTProtectedRangesImpl
extends XmlComplexContentImpl
implements CTProtectedRanges {
    private static final long serialVersionUID = 1L;
    private static final QName[] PROPERTY_QNAME = new QName[]{new QName("http://schemas.openxmlformats.org/spreadsheetml/2006/main", "protectedRange")};

    public CTProtectedRangesImpl(SchemaType sType) {
        super(sType);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public List<CTProtectedRange> getProtectedRangeList() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return new JavaListXmlObject<CTProtectedRange>(this::getProtectedRangeArray, this::setProtectedRangeArray, this::insertNewProtectedRange, this::removeProtectedRange, this::sizeOfProtectedRangeArray);
        }
    }

    @Override
    public CTProtectedRange[] getProtectedRangeArray() {
        return (CTProtectedRange[])this.getXmlObjectArray(PROPERTY_QNAME[0], new CTProtectedRange[0]);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTProtectedRange getProtectedRangeArray(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTProtectedRange target = null;
            target = (CTProtectedRange)((Object)this.get_store().find_element_user(PROPERTY_QNAME[0], i));
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
    public int sizeOfProtectedRangeArray() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[0]);
        }
    }

    @Override
    public void setProtectedRangeArray(CTProtectedRange[] protectedRangeArray) {
        this.check_orphaned();
        this.arraySetterHelper(protectedRangeArray, PROPERTY_QNAME[0]);
    }

    @Override
    public void setProtectedRangeArray(int i, CTProtectedRange protectedRange) {
        this.generatedSetterHelperImpl(protectedRange, PROPERTY_QNAME[0], i, (short)2);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTProtectedRange insertNewProtectedRange(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTProtectedRange target = null;
            target = (CTProtectedRange)((Object)this.get_store().insert_element_user(PROPERTY_QNAME[0], i));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTProtectedRange addNewProtectedRange() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTProtectedRange target = null;
            target = (CTProtectedRange)((Object)this.get_store().add_element_user(PROPERTY_QNAME[0]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void removeProtectedRange(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.get_store().remove_element(PROPERTY_QNAME[0], i);
        }
    }
}


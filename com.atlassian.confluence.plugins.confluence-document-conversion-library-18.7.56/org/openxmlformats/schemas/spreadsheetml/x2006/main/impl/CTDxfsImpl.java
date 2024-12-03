/*
 * Decompiled with CFR 0.152.
 */
package org.openxmlformats.schemas.spreadsheetml.x2006.main.impl;

import java.util.List;
import javax.xml.namespace.QName;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.SimpleValue;
import org.apache.xmlbeans.XmlUnsignedInt;
import org.apache.xmlbeans.impl.values.JavaListXmlObject;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTDxf;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTDxfs;

public class CTDxfsImpl
extends XmlComplexContentImpl
implements CTDxfs {
    private static final long serialVersionUID = 1L;
    private static final QName[] PROPERTY_QNAME = new QName[]{new QName("http://schemas.openxmlformats.org/spreadsheetml/2006/main", "dxf"), new QName("", "count")};

    public CTDxfsImpl(SchemaType sType) {
        super(sType);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public List<CTDxf> getDxfList() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return new JavaListXmlObject<CTDxf>(this::getDxfArray, this::setDxfArray, this::insertNewDxf, this::removeDxf, this::sizeOfDxfArray);
        }
    }

    @Override
    public CTDxf[] getDxfArray() {
        return (CTDxf[])this.getXmlObjectArray(PROPERTY_QNAME[0], new CTDxf[0]);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTDxf getDxfArray(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTDxf target = null;
            target = (CTDxf)((Object)this.get_store().find_element_user(PROPERTY_QNAME[0], i));
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
    public int sizeOfDxfArray() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[0]);
        }
    }

    @Override
    public void setDxfArray(CTDxf[] dxfArray) {
        this.check_orphaned();
        this.arraySetterHelper(dxfArray, PROPERTY_QNAME[0]);
    }

    @Override
    public void setDxfArray(int i, CTDxf dxf) {
        this.generatedSetterHelperImpl(dxf, PROPERTY_QNAME[0], i, (short)2);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTDxf insertNewDxf(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTDxf target = null;
            target = (CTDxf)((Object)this.get_store().insert_element_user(PROPERTY_QNAME[0], i));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTDxf addNewDxf() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTDxf target = null;
            target = (CTDxf)((Object)this.get_store().add_element_user(PROPERTY_QNAME[0]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void removeDxf(int i) {
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
    public long getCount() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[1]));
            return target == null ? 0L : target.getLongValue();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public XmlUnsignedInt xgetCount() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            XmlUnsignedInt target = null;
            target = (XmlUnsignedInt)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[1]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean isSetCount() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(PROPERTY_QNAME[1]) != null;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void setCount(long count) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[1]));
            if (target == null) {
                target = (SimpleValue)((Object)this.get_store().add_attribute_user(PROPERTY_QNAME[1]));
            }
            target.setLongValue(count);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void xsetCount(XmlUnsignedInt count) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            XmlUnsignedInt target = null;
            target = (XmlUnsignedInt)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[1]));
            if (target == null) {
                target = (XmlUnsignedInt)((Object)this.get_store().add_attribute_user(PROPERTY_QNAME[1]));
            }
            target.set(count);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void unsetCount() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.get_store().remove_attribute(PROPERTY_QNAME[1]);
        }
    }
}


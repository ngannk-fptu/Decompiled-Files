/*
 * Decompiled with CFR 0.152.
 */
package org.openxmlformats.schemas.drawingml.x2006.main.impl;

import java.util.List;
import javax.xml.namespace.QName;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.impl.values.JavaListXmlObject;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;
import org.openxmlformats.schemas.drawingml.x2006.main.CTAdjustHandleList;
import org.openxmlformats.schemas.drawingml.x2006.main.CTPolarAdjustHandle;
import org.openxmlformats.schemas.drawingml.x2006.main.CTXYAdjustHandle;

public class CTAdjustHandleListImpl
extends XmlComplexContentImpl
implements CTAdjustHandleList {
    private static final long serialVersionUID = 1L;
    private static final QName[] PROPERTY_QNAME = new QName[]{new QName("http://schemas.openxmlformats.org/drawingml/2006/main", "ahXY"), new QName("http://schemas.openxmlformats.org/drawingml/2006/main", "ahPolar")};

    public CTAdjustHandleListImpl(SchemaType sType) {
        super(sType);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public List<CTXYAdjustHandle> getAhXYList() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return new JavaListXmlObject<CTXYAdjustHandle>(this::getAhXYArray, this::setAhXYArray, this::insertNewAhXY, this::removeAhXY, this::sizeOfAhXYArray);
        }
    }

    @Override
    public CTXYAdjustHandle[] getAhXYArray() {
        return (CTXYAdjustHandle[])this.getXmlObjectArray(PROPERTY_QNAME[0], new CTXYAdjustHandle[0]);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTXYAdjustHandle getAhXYArray(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTXYAdjustHandle target = null;
            target = (CTXYAdjustHandle)((Object)this.get_store().find_element_user(PROPERTY_QNAME[0], i));
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
    public int sizeOfAhXYArray() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[0]);
        }
    }

    @Override
    public void setAhXYArray(CTXYAdjustHandle[] ahXYArray) {
        this.check_orphaned();
        this.arraySetterHelper(ahXYArray, PROPERTY_QNAME[0]);
    }

    @Override
    public void setAhXYArray(int i, CTXYAdjustHandle ahXY) {
        this.generatedSetterHelperImpl(ahXY, PROPERTY_QNAME[0], i, (short)2);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTXYAdjustHandle insertNewAhXY(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTXYAdjustHandle target = null;
            target = (CTXYAdjustHandle)((Object)this.get_store().insert_element_user(PROPERTY_QNAME[0], i));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTXYAdjustHandle addNewAhXY() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTXYAdjustHandle target = null;
            target = (CTXYAdjustHandle)((Object)this.get_store().add_element_user(PROPERTY_QNAME[0]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void removeAhXY(int i) {
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
    public List<CTPolarAdjustHandle> getAhPolarList() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return new JavaListXmlObject<CTPolarAdjustHandle>(this::getAhPolarArray, this::setAhPolarArray, this::insertNewAhPolar, this::removeAhPolar, this::sizeOfAhPolarArray);
        }
    }

    @Override
    public CTPolarAdjustHandle[] getAhPolarArray() {
        return (CTPolarAdjustHandle[])this.getXmlObjectArray(PROPERTY_QNAME[1], new CTPolarAdjustHandle[0]);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTPolarAdjustHandle getAhPolarArray(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTPolarAdjustHandle target = null;
            target = (CTPolarAdjustHandle)((Object)this.get_store().find_element_user(PROPERTY_QNAME[1], i));
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
    public int sizeOfAhPolarArray() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[1]);
        }
    }

    @Override
    public void setAhPolarArray(CTPolarAdjustHandle[] ahPolarArray) {
        this.check_orphaned();
        this.arraySetterHelper(ahPolarArray, PROPERTY_QNAME[1]);
    }

    @Override
    public void setAhPolarArray(int i, CTPolarAdjustHandle ahPolar) {
        this.generatedSetterHelperImpl(ahPolar, PROPERTY_QNAME[1], i, (short)2);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTPolarAdjustHandle insertNewAhPolar(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTPolarAdjustHandle target = null;
            target = (CTPolarAdjustHandle)((Object)this.get_store().insert_element_user(PROPERTY_QNAME[1], i));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTPolarAdjustHandle addNewAhPolar() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTPolarAdjustHandle target = null;
            target = (CTPolarAdjustHandle)((Object)this.get_store().add_element_user(PROPERTY_QNAME[1]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void removeAhPolar(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.get_store().remove_element(PROPERTY_QNAME[1], i);
        }
    }
}


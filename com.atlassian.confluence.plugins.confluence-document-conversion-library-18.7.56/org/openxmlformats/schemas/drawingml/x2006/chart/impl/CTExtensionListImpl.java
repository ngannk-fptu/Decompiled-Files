/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.openxmlformats.schemas.drawingml.x2006.chart.CTExtension
 */
package org.openxmlformats.schemas.drawingml.x2006.chart.impl;

import java.util.List;
import javax.xml.namespace.QName;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.impl.values.JavaListXmlObject;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTExtension;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTExtensionList;

public class CTExtensionListImpl
extends XmlComplexContentImpl
implements CTExtensionList {
    private static final long serialVersionUID = 1L;
    private static final QName[] PROPERTY_QNAME = new QName[]{new QName("http://schemas.openxmlformats.org/drawingml/2006/chart", "ext")};

    public CTExtensionListImpl(SchemaType sType) {
        super(sType);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public List<CTExtension> getExtList() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return new JavaListXmlObject<CTExtension>(this::getExtArray, this::setExtArray, this::insertNewExt, this::removeExt, this::sizeOfExtArray);
        }
    }

    @Override
    public CTExtension[] getExtArray() {
        return (CTExtension[])this.getXmlObjectArray(PROPERTY_QNAME[0], (XmlObject[])new CTExtension[0]);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTExtension getExtArray(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTExtension target = null;
            target = (CTExtension)this.get_store().find_element_user(PROPERTY_QNAME[0], i);
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
    public int sizeOfExtArray() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[0]);
        }
    }

    @Override
    public void setExtArray(CTExtension[] extArray) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])extArray, PROPERTY_QNAME[0]);
    }

    @Override
    public void setExtArray(int i, CTExtension ext) {
        this.generatedSetterHelperImpl((XmlObject)ext, PROPERTY_QNAME[0], i, (short)2);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTExtension insertNewExt(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTExtension target = null;
            target = (CTExtension)this.get_store().insert_element_user(PROPERTY_QNAME[0], i);
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTExtension addNewExt() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTExtension target = null;
            target = (CTExtension)this.get_store().add_element_user(PROPERTY_QNAME[0]);
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void removeExt(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.get_store().remove_element(PROPERTY_QNAME[0], i);
        }
    }
}


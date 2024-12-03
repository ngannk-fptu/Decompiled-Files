/*
 * Decompiled with CFR 0.152.
 */
package org.openxmlformats.schemas.drawingml.x2006.main.impl;

import java.util.List;
import javax.xml.namespace.QName;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.impl.values.JavaListXmlObject;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;
import org.openxmlformats.schemas.drawingml.x2006.main.CTDashStop;
import org.openxmlformats.schemas.drawingml.x2006.main.CTDashStopList;

public class CTDashStopListImpl
extends XmlComplexContentImpl
implements CTDashStopList {
    private static final long serialVersionUID = 1L;
    private static final QName[] PROPERTY_QNAME = new QName[]{new QName("http://schemas.openxmlformats.org/drawingml/2006/main", "ds")};

    public CTDashStopListImpl(SchemaType sType) {
        super(sType);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public List<CTDashStop> getDsList() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return new JavaListXmlObject<CTDashStop>(this::getDsArray, this::setDsArray, this::insertNewDs, this::removeDs, this::sizeOfDsArray);
        }
    }

    @Override
    public CTDashStop[] getDsArray() {
        return (CTDashStop[])this.getXmlObjectArray(PROPERTY_QNAME[0], new CTDashStop[0]);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTDashStop getDsArray(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTDashStop target = null;
            target = (CTDashStop)((Object)this.get_store().find_element_user(PROPERTY_QNAME[0], i));
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
    public int sizeOfDsArray() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[0]);
        }
    }

    @Override
    public void setDsArray(CTDashStop[] dsArray) {
        this.check_orphaned();
        this.arraySetterHelper(dsArray, PROPERTY_QNAME[0]);
    }

    @Override
    public void setDsArray(int i, CTDashStop ds) {
        this.generatedSetterHelperImpl(ds, PROPERTY_QNAME[0], i, (short)2);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTDashStop insertNewDs(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTDashStop target = null;
            target = (CTDashStop)((Object)this.get_store().insert_element_user(PROPERTY_QNAME[0], i));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTDashStop addNewDs() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTDashStop target = null;
            target = (CTDashStop)((Object)this.get_store().add_element_user(PROPERTY_QNAME[0]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void removeDs(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.get_store().remove_element(PROPERTY_QNAME[0], i);
        }
    }
}


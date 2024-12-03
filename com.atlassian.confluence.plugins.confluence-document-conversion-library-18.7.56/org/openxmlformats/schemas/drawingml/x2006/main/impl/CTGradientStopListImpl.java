/*
 * Decompiled with CFR 0.152.
 */
package org.openxmlformats.schemas.drawingml.x2006.main.impl;

import java.util.List;
import javax.xml.namespace.QName;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.impl.values.JavaListXmlObject;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;
import org.openxmlformats.schemas.drawingml.x2006.main.CTGradientStop;
import org.openxmlformats.schemas.drawingml.x2006.main.CTGradientStopList;

public class CTGradientStopListImpl
extends XmlComplexContentImpl
implements CTGradientStopList {
    private static final long serialVersionUID = 1L;
    private static final QName[] PROPERTY_QNAME = new QName[]{new QName("http://schemas.openxmlformats.org/drawingml/2006/main", "gs")};

    public CTGradientStopListImpl(SchemaType sType) {
        super(sType);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public List<CTGradientStop> getGsList() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return new JavaListXmlObject<CTGradientStop>(this::getGsArray, this::setGsArray, this::insertNewGs, this::removeGs, this::sizeOfGsArray);
        }
    }

    @Override
    public CTGradientStop[] getGsArray() {
        return (CTGradientStop[])this.getXmlObjectArray(PROPERTY_QNAME[0], new CTGradientStop[0]);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTGradientStop getGsArray(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTGradientStop target = null;
            target = (CTGradientStop)((Object)this.get_store().find_element_user(PROPERTY_QNAME[0], i));
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
    public int sizeOfGsArray() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[0]);
        }
    }

    @Override
    public void setGsArray(CTGradientStop[] gsArray) {
        this.check_orphaned();
        this.arraySetterHelper(gsArray, PROPERTY_QNAME[0]);
    }

    @Override
    public void setGsArray(int i, CTGradientStop gs) {
        this.generatedSetterHelperImpl(gs, PROPERTY_QNAME[0], i, (short)2);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTGradientStop insertNewGs(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTGradientStop target = null;
            target = (CTGradientStop)((Object)this.get_store().insert_element_user(PROPERTY_QNAME[0], i));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTGradientStop addNewGs() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTGradientStop target = null;
            target = (CTGradientStop)((Object)this.get_store().add_element_user(PROPERTY_QNAME[0]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void removeGs(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.get_store().remove_element(PROPERTY_QNAME[0], i);
        }
    }
}


/*
 * Decompiled with CFR 0.152.
 */
package org.openxmlformats.schemas.spreadsheetml.x2006.main.impl;

import java.util.List;
import javax.xml.namespace.QName;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.impl.values.JavaListXmlObject;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTControl;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTControls;

public class CTControlsImpl
extends XmlComplexContentImpl
implements CTControls {
    private static final long serialVersionUID = 1L;
    private static final QName[] PROPERTY_QNAME = new QName[]{new QName("http://schemas.openxmlformats.org/spreadsheetml/2006/main", "control")};

    public CTControlsImpl(SchemaType sType) {
        super(sType);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public List<CTControl> getControlList() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return new JavaListXmlObject<CTControl>(this::getControlArray, this::setControlArray, this::insertNewControl, this::removeControl, this::sizeOfControlArray);
        }
    }

    @Override
    public CTControl[] getControlArray() {
        return (CTControl[])this.getXmlObjectArray(PROPERTY_QNAME[0], new CTControl[0]);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTControl getControlArray(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTControl target = null;
            target = (CTControl)((Object)this.get_store().find_element_user(PROPERTY_QNAME[0], i));
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
    public int sizeOfControlArray() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[0]);
        }
    }

    @Override
    public void setControlArray(CTControl[] controlArray) {
        this.check_orphaned();
        this.arraySetterHelper(controlArray, PROPERTY_QNAME[0]);
    }

    @Override
    public void setControlArray(int i, CTControl control) {
        this.generatedSetterHelperImpl(control, PROPERTY_QNAME[0], i, (short)2);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTControl insertNewControl(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTControl target = null;
            target = (CTControl)((Object)this.get_store().insert_element_user(PROPERTY_QNAME[0], i));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTControl addNewControl() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTControl target = null;
            target = (CTControl)((Object)this.get_store().add_element_user(PROPERTY_QNAME[0]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void removeControl(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.get_store().remove_element(PROPERTY_QNAME[0], i);
        }
    }
}


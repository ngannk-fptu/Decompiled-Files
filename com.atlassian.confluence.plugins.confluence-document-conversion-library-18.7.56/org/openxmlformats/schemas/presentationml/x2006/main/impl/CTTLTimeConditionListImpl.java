/*
 * Decompiled with CFR 0.152.
 */
package org.openxmlformats.schemas.presentationml.x2006.main.impl;

import java.util.List;
import javax.xml.namespace.QName;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.impl.values.JavaListXmlObject;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;
import org.openxmlformats.schemas.presentationml.x2006.main.CTTLTimeCondition;
import org.openxmlformats.schemas.presentationml.x2006.main.CTTLTimeConditionList;

public class CTTLTimeConditionListImpl
extends XmlComplexContentImpl
implements CTTLTimeConditionList {
    private static final long serialVersionUID = 1L;
    private static final QName[] PROPERTY_QNAME = new QName[]{new QName("http://schemas.openxmlformats.org/presentationml/2006/main", "cond")};

    public CTTLTimeConditionListImpl(SchemaType sType) {
        super(sType);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public List<CTTLTimeCondition> getCondList() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return new JavaListXmlObject<CTTLTimeCondition>(this::getCondArray, this::setCondArray, this::insertNewCond, this::removeCond, this::sizeOfCondArray);
        }
    }

    @Override
    public CTTLTimeCondition[] getCondArray() {
        return (CTTLTimeCondition[])this.getXmlObjectArray(PROPERTY_QNAME[0], new CTTLTimeCondition[0]);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTTLTimeCondition getCondArray(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTTLTimeCondition target = null;
            target = (CTTLTimeCondition)((Object)this.get_store().find_element_user(PROPERTY_QNAME[0], i));
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
    public int sizeOfCondArray() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[0]);
        }
    }

    @Override
    public void setCondArray(CTTLTimeCondition[] condArray) {
        this.check_orphaned();
        this.arraySetterHelper(condArray, PROPERTY_QNAME[0]);
    }

    @Override
    public void setCondArray(int i, CTTLTimeCondition cond) {
        this.generatedSetterHelperImpl(cond, PROPERTY_QNAME[0], i, (short)2);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTTLTimeCondition insertNewCond(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTTLTimeCondition target = null;
            target = (CTTLTimeCondition)((Object)this.get_store().insert_element_user(PROPERTY_QNAME[0], i));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTTLTimeCondition addNewCond() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTTLTimeCondition target = null;
            target = (CTTLTimeCondition)((Object)this.get_store().add_element_user(PROPERTY_QNAME[0]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void removeCond(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.get_store().remove_element(PROPERTY_QNAME[0], i);
        }
    }
}


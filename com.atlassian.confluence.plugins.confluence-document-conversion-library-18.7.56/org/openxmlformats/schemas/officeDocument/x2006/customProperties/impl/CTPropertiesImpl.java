/*
 * Decompiled with CFR 0.152.
 */
package org.openxmlformats.schemas.officeDocument.x2006.customProperties.impl;

import java.util.List;
import javax.xml.namespace.QName;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.impl.values.JavaListXmlObject;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;
import org.openxmlformats.schemas.officeDocument.x2006.customProperties.CTProperties;
import org.openxmlformats.schemas.officeDocument.x2006.customProperties.CTProperty;

public class CTPropertiesImpl
extends XmlComplexContentImpl
implements CTProperties {
    private static final long serialVersionUID = 1L;
    private static final QName[] PROPERTY_QNAME = new QName[]{new QName("http://schemas.openxmlformats.org/officeDocument/2006/custom-properties", "property")};

    public CTPropertiesImpl(SchemaType sType) {
        super(sType);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public List<CTProperty> getPropertyList() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return new JavaListXmlObject<CTProperty>(this::getPropertyArray, this::setPropertyArray, this::insertNewProperty, this::removeProperty, this::sizeOfPropertyArray);
        }
    }

    @Override
    public CTProperty[] getPropertyArray() {
        return (CTProperty[])this.getXmlObjectArray(PROPERTY_QNAME[0], new CTProperty[0]);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTProperty getPropertyArray(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTProperty target = null;
            target = (CTProperty)((Object)this.get_store().find_element_user(PROPERTY_QNAME[0], i));
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
    public int sizeOfPropertyArray() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[0]);
        }
    }

    @Override
    public void setPropertyArray(CTProperty[] propertyArray) {
        this.check_orphaned();
        this.arraySetterHelper(propertyArray, PROPERTY_QNAME[0]);
    }

    @Override
    public void setPropertyArray(int i, CTProperty property) {
        this.generatedSetterHelperImpl(property, PROPERTY_QNAME[0], i, (short)2);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTProperty insertNewProperty(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTProperty target = null;
            target = (CTProperty)((Object)this.get_store().insert_element_user(PROPERTY_QNAME[0], i));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTProperty addNewProperty() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTProperty target = null;
            target = (CTProperty)((Object)this.get_store().add_element_user(PROPERTY_QNAME[0]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void removeProperty(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.get_store().remove_element(PROPERTY_QNAME[0], i);
        }
    }
}


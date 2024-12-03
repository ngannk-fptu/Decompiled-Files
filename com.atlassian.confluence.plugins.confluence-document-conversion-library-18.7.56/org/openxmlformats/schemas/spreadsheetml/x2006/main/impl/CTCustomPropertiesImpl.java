/*
 * Decompiled with CFR 0.152.
 */
package org.openxmlformats.schemas.spreadsheetml.x2006.main.impl;

import java.util.List;
import javax.xml.namespace.QName;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.impl.values.JavaListXmlObject;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTCustomProperties;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTCustomProperty;

public class CTCustomPropertiesImpl
extends XmlComplexContentImpl
implements CTCustomProperties {
    private static final long serialVersionUID = 1L;
    private static final QName[] PROPERTY_QNAME = new QName[]{new QName("http://schemas.openxmlformats.org/spreadsheetml/2006/main", "customPr")};

    public CTCustomPropertiesImpl(SchemaType sType) {
        super(sType);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public List<CTCustomProperty> getCustomPrList() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return new JavaListXmlObject<CTCustomProperty>(this::getCustomPrArray, this::setCustomPrArray, this::insertNewCustomPr, this::removeCustomPr, this::sizeOfCustomPrArray);
        }
    }

    @Override
    public CTCustomProperty[] getCustomPrArray() {
        return (CTCustomProperty[])this.getXmlObjectArray(PROPERTY_QNAME[0], new CTCustomProperty[0]);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTCustomProperty getCustomPrArray(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTCustomProperty target = null;
            target = (CTCustomProperty)((Object)this.get_store().find_element_user(PROPERTY_QNAME[0], i));
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
    public int sizeOfCustomPrArray() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[0]);
        }
    }

    @Override
    public void setCustomPrArray(CTCustomProperty[] customPrArray) {
        this.check_orphaned();
        this.arraySetterHelper(customPrArray, PROPERTY_QNAME[0]);
    }

    @Override
    public void setCustomPrArray(int i, CTCustomProperty customPr) {
        this.generatedSetterHelperImpl(customPr, PROPERTY_QNAME[0], i, (short)2);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTCustomProperty insertNewCustomPr(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTCustomProperty target = null;
            target = (CTCustomProperty)((Object)this.get_store().insert_element_user(PROPERTY_QNAME[0], i));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTCustomProperty addNewCustomPr() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTCustomProperty target = null;
            target = (CTCustomProperty)((Object)this.get_store().add_element_user(PROPERTY_QNAME[0]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void removeCustomPr(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.get_store().remove_element(PROPERTY_QNAME[0], i);
        }
    }
}


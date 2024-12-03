/*
 * Decompiled with CFR 0.152.
 */
package org.openxmlformats.schemas.spreadsheetml.x2006.main.impl;

import java.util.List;
import javax.xml.namespace.QName;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.impl.values.JavaListXmlObject;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTExternalSheetName;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTExternalSheetNames;

public class CTExternalSheetNamesImpl
extends XmlComplexContentImpl
implements CTExternalSheetNames {
    private static final long serialVersionUID = 1L;
    private static final QName[] PROPERTY_QNAME = new QName[]{new QName("http://schemas.openxmlformats.org/spreadsheetml/2006/main", "sheetName")};

    public CTExternalSheetNamesImpl(SchemaType sType) {
        super(sType);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public List<CTExternalSheetName> getSheetNameList() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return new JavaListXmlObject<CTExternalSheetName>(this::getSheetNameArray, this::setSheetNameArray, this::insertNewSheetName, this::removeSheetName, this::sizeOfSheetNameArray);
        }
    }

    @Override
    public CTExternalSheetName[] getSheetNameArray() {
        return (CTExternalSheetName[])this.getXmlObjectArray(PROPERTY_QNAME[0], new CTExternalSheetName[0]);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTExternalSheetName getSheetNameArray(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTExternalSheetName target = null;
            target = (CTExternalSheetName)((Object)this.get_store().find_element_user(PROPERTY_QNAME[0], i));
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
    public int sizeOfSheetNameArray() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[0]);
        }
    }

    @Override
    public void setSheetNameArray(CTExternalSheetName[] sheetNameArray) {
        this.check_orphaned();
        this.arraySetterHelper(sheetNameArray, PROPERTY_QNAME[0]);
    }

    @Override
    public void setSheetNameArray(int i, CTExternalSheetName sheetName) {
        this.generatedSetterHelperImpl(sheetName, PROPERTY_QNAME[0], i, (short)2);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTExternalSheetName insertNewSheetName(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTExternalSheetName target = null;
            target = (CTExternalSheetName)((Object)this.get_store().insert_element_user(PROPERTY_QNAME[0], i));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTExternalSheetName addNewSheetName() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTExternalSheetName target = null;
            target = (CTExternalSheetName)((Object)this.get_store().add_element_user(PROPERTY_QNAME[0]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void removeSheetName(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.get_store().remove_element(PROPERTY_QNAME[0], i);
        }
    }
}


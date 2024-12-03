/*
 * Decompiled with CFR 0.152.
 */
package org.openxmlformats.schemas.spreadsheetml.x2006.main.impl;

import java.util.List;
import javax.xml.namespace.QName;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.impl.values.JavaListXmlObject;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTExternalSheetData;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTExternalSheetDataSet;

public class CTExternalSheetDataSetImpl
extends XmlComplexContentImpl
implements CTExternalSheetDataSet {
    private static final long serialVersionUID = 1L;
    private static final QName[] PROPERTY_QNAME = new QName[]{new QName("http://schemas.openxmlformats.org/spreadsheetml/2006/main", "sheetData")};

    public CTExternalSheetDataSetImpl(SchemaType sType) {
        super(sType);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public List<CTExternalSheetData> getSheetDataList() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return new JavaListXmlObject<CTExternalSheetData>(this::getSheetDataArray, this::setSheetDataArray, this::insertNewSheetData, this::removeSheetData, this::sizeOfSheetDataArray);
        }
    }

    @Override
    public CTExternalSheetData[] getSheetDataArray() {
        return (CTExternalSheetData[])this.getXmlObjectArray(PROPERTY_QNAME[0], new CTExternalSheetData[0]);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTExternalSheetData getSheetDataArray(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTExternalSheetData target = null;
            target = (CTExternalSheetData)((Object)this.get_store().find_element_user(PROPERTY_QNAME[0], i));
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
    public int sizeOfSheetDataArray() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[0]);
        }
    }

    @Override
    public void setSheetDataArray(CTExternalSheetData[] sheetDataArray) {
        this.check_orphaned();
        this.arraySetterHelper(sheetDataArray, PROPERTY_QNAME[0]);
    }

    @Override
    public void setSheetDataArray(int i, CTExternalSheetData sheetData) {
        this.generatedSetterHelperImpl(sheetData, PROPERTY_QNAME[0], i, (short)2);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTExternalSheetData insertNewSheetData(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTExternalSheetData target = null;
            target = (CTExternalSheetData)((Object)this.get_store().insert_element_user(PROPERTY_QNAME[0], i));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTExternalSheetData addNewSheetData() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTExternalSheetData target = null;
            target = (CTExternalSheetData)((Object)this.get_store().add_element_user(PROPERTY_QNAME[0]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void removeSheetData(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.get_store().remove_element(PROPERTY_QNAME[0], i);
        }
    }
}


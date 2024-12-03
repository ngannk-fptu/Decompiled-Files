/*
 * Decompiled with CFR 0.152.
 */
package org.openxmlformats.schemas.spreadsheetml.x2006.main.impl;

import java.util.List;
import javax.xml.namespace.QName;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.impl.values.JavaListXmlObject;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTRow;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTSheetData;

public class CTSheetDataImpl
extends XmlComplexContentImpl
implements CTSheetData {
    private static final long serialVersionUID = 1L;
    private static final QName[] PROPERTY_QNAME = new QName[]{new QName("http://schemas.openxmlformats.org/spreadsheetml/2006/main", "row")};

    public CTSheetDataImpl(SchemaType sType) {
        super(sType);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public List<CTRow> getRowList() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return new JavaListXmlObject<CTRow>(this::getRowArray, this::setRowArray, this::insertNewRow, this::removeRow, this::sizeOfRowArray);
        }
    }

    @Override
    public CTRow[] getRowArray() {
        return (CTRow[])this.getXmlObjectArray(PROPERTY_QNAME[0], new CTRow[0]);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTRow getRowArray(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTRow target = null;
            target = (CTRow)((Object)this.get_store().find_element_user(PROPERTY_QNAME[0], i));
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
    public int sizeOfRowArray() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[0]);
        }
    }

    @Override
    public void setRowArray(CTRow[] rowArray) {
        this.check_orphaned();
        this.arraySetterHelper(rowArray, PROPERTY_QNAME[0]);
    }

    @Override
    public void setRowArray(int i, CTRow row) {
        this.generatedSetterHelperImpl(row, PROPERTY_QNAME[0], i, (short)2);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTRow insertNewRow(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTRow target = null;
            target = (CTRow)((Object)this.get_store().insert_element_user(PROPERTY_QNAME[0], i));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTRow addNewRow() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTRow target = null;
            target = (CTRow)((Object)this.get_store().add_element_user(PROPERTY_QNAME[0]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void removeRow(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.get_store().remove_element(PROPERTY_QNAME[0], i);
        }
    }
}


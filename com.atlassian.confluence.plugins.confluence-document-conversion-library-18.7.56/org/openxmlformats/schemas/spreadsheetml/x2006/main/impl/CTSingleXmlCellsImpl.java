/*
 * Decompiled with CFR 0.152.
 */
package org.openxmlformats.schemas.spreadsheetml.x2006.main.impl;

import java.util.List;
import javax.xml.namespace.QName;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.impl.values.JavaListXmlObject;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTSingleXmlCell;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTSingleXmlCells;

public class CTSingleXmlCellsImpl
extends XmlComplexContentImpl
implements CTSingleXmlCells {
    private static final long serialVersionUID = 1L;
    private static final QName[] PROPERTY_QNAME = new QName[]{new QName("http://schemas.openxmlformats.org/spreadsheetml/2006/main", "singleXmlCell")};

    public CTSingleXmlCellsImpl(SchemaType sType) {
        super(sType);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public List<CTSingleXmlCell> getSingleXmlCellList() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return new JavaListXmlObject<CTSingleXmlCell>(this::getSingleXmlCellArray, this::setSingleXmlCellArray, this::insertNewSingleXmlCell, this::removeSingleXmlCell, this::sizeOfSingleXmlCellArray);
        }
    }

    @Override
    public CTSingleXmlCell[] getSingleXmlCellArray() {
        return (CTSingleXmlCell[])this.getXmlObjectArray(PROPERTY_QNAME[0], new CTSingleXmlCell[0]);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTSingleXmlCell getSingleXmlCellArray(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTSingleXmlCell target = null;
            target = (CTSingleXmlCell)((Object)this.get_store().find_element_user(PROPERTY_QNAME[0], i));
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
    public int sizeOfSingleXmlCellArray() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[0]);
        }
    }

    @Override
    public void setSingleXmlCellArray(CTSingleXmlCell[] singleXmlCellArray) {
        this.check_orphaned();
        this.arraySetterHelper(singleXmlCellArray, PROPERTY_QNAME[0]);
    }

    @Override
    public void setSingleXmlCellArray(int i, CTSingleXmlCell singleXmlCell) {
        this.generatedSetterHelperImpl(singleXmlCell, PROPERTY_QNAME[0], i, (short)2);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTSingleXmlCell insertNewSingleXmlCell(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTSingleXmlCell target = null;
            target = (CTSingleXmlCell)((Object)this.get_store().insert_element_user(PROPERTY_QNAME[0], i));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTSingleXmlCell addNewSingleXmlCell() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTSingleXmlCell target = null;
            target = (CTSingleXmlCell)((Object)this.get_store().add_element_user(PROPERTY_QNAME[0]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void removeSingleXmlCell(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.get_store().remove_element(PROPERTY_QNAME[0], i);
        }
    }
}


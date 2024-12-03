/*
 * Decompiled with CFR 0.152.
 */
package org.openxmlformats.schemas.spreadsheetml.x2006.main.impl;

import java.util.List;
import javax.xml.namespace.QName;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.impl.values.JavaListXmlObject;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTCellWatch;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTCellWatches;

public class CTCellWatchesImpl
extends XmlComplexContentImpl
implements CTCellWatches {
    private static final long serialVersionUID = 1L;
    private static final QName[] PROPERTY_QNAME = new QName[]{new QName("http://schemas.openxmlformats.org/spreadsheetml/2006/main", "cellWatch")};

    public CTCellWatchesImpl(SchemaType sType) {
        super(sType);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public List<CTCellWatch> getCellWatchList() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return new JavaListXmlObject<CTCellWatch>(this::getCellWatchArray, this::setCellWatchArray, this::insertNewCellWatch, this::removeCellWatch, this::sizeOfCellWatchArray);
        }
    }

    @Override
    public CTCellWatch[] getCellWatchArray() {
        return (CTCellWatch[])this.getXmlObjectArray(PROPERTY_QNAME[0], new CTCellWatch[0]);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTCellWatch getCellWatchArray(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTCellWatch target = null;
            target = (CTCellWatch)((Object)this.get_store().find_element_user(PROPERTY_QNAME[0], i));
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
    public int sizeOfCellWatchArray() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[0]);
        }
    }

    @Override
    public void setCellWatchArray(CTCellWatch[] cellWatchArray) {
        this.check_orphaned();
        this.arraySetterHelper(cellWatchArray, PROPERTY_QNAME[0]);
    }

    @Override
    public void setCellWatchArray(int i, CTCellWatch cellWatch) {
        this.generatedSetterHelperImpl(cellWatch, PROPERTY_QNAME[0], i, (short)2);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTCellWatch insertNewCellWatch(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTCellWatch target = null;
            target = (CTCellWatch)((Object)this.get_store().insert_element_user(PROPERTY_QNAME[0], i));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTCellWatch addNewCellWatch() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTCellWatch target = null;
            target = (CTCellWatch)((Object)this.get_store().add_element_user(PROPERTY_QNAME[0]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void removeCellWatch(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.get_store().remove_element(PROPERTY_QNAME[0], i);
        }
    }
}


/*
 * Decompiled with CFR 0.152.
 */
package org.openxmlformats.schemas.drawingml.x2006.main.impl;

import java.util.List;
import javax.xml.namespace.QName;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.impl.values.JavaListXmlObject;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;
import org.openxmlformats.schemas.drawingml.x2006.main.CTTable;
import org.openxmlformats.schemas.drawingml.x2006.main.CTTableGrid;
import org.openxmlformats.schemas.drawingml.x2006.main.CTTableProperties;
import org.openxmlformats.schemas.drawingml.x2006.main.CTTableRow;

public class CTTableImpl
extends XmlComplexContentImpl
implements CTTable {
    private static final long serialVersionUID = 1L;
    private static final QName[] PROPERTY_QNAME = new QName[]{new QName("http://schemas.openxmlformats.org/drawingml/2006/main", "tblPr"), new QName("http://schemas.openxmlformats.org/drawingml/2006/main", "tblGrid"), new QName("http://schemas.openxmlformats.org/drawingml/2006/main", "tr")};

    public CTTableImpl(SchemaType sType) {
        super(sType);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTTableProperties getTblPr() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTTableProperties target = null;
            target = (CTTableProperties)((Object)this.get_store().find_element_user(PROPERTY_QNAME[0], 0));
            return target == null ? null : target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean isSetTblPr() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[0]) != 0;
        }
    }

    @Override
    public void setTblPr(CTTableProperties tblPr) {
        this.generatedSetterHelperImpl(tblPr, PROPERTY_QNAME[0], 0, (short)1);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTTableProperties addNewTblPr() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTTableProperties target = null;
            target = (CTTableProperties)((Object)this.get_store().add_element_user(PROPERTY_QNAME[0]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void unsetTblPr() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.get_store().remove_element(PROPERTY_QNAME[0], 0);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTTableGrid getTblGrid() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTTableGrid target = null;
            target = (CTTableGrid)((Object)this.get_store().find_element_user(PROPERTY_QNAME[1], 0));
            return target == null ? null : target;
        }
    }

    @Override
    public void setTblGrid(CTTableGrid tblGrid) {
        this.generatedSetterHelperImpl(tblGrid, PROPERTY_QNAME[1], 0, (short)1);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTTableGrid addNewTblGrid() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTTableGrid target = null;
            target = (CTTableGrid)((Object)this.get_store().add_element_user(PROPERTY_QNAME[1]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public List<CTTableRow> getTrList() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return new JavaListXmlObject<CTTableRow>(this::getTrArray, this::setTrArray, this::insertNewTr, this::removeTr, this::sizeOfTrArray);
        }
    }

    @Override
    public CTTableRow[] getTrArray() {
        return (CTTableRow[])this.getXmlObjectArray(PROPERTY_QNAME[2], new CTTableRow[0]);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTTableRow getTrArray(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTTableRow target = null;
            target = (CTTableRow)((Object)this.get_store().find_element_user(PROPERTY_QNAME[2], i));
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
    public int sizeOfTrArray() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[2]);
        }
    }

    @Override
    public void setTrArray(CTTableRow[] trArray) {
        this.check_orphaned();
        this.arraySetterHelper(trArray, PROPERTY_QNAME[2]);
    }

    @Override
    public void setTrArray(int i, CTTableRow tr) {
        this.generatedSetterHelperImpl(tr, PROPERTY_QNAME[2], i, (short)2);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTTableRow insertNewTr(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTTableRow target = null;
            target = (CTTableRow)((Object)this.get_store().insert_element_user(PROPERTY_QNAME[2], i));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTTableRow addNewTr() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTTableRow target = null;
            target = (CTTableRow)((Object)this.get_store().add_element_user(PROPERTY_QNAME[2]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void removeTr(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.get_store().remove_element(PROPERTY_QNAME[2], i);
        }
    }
}


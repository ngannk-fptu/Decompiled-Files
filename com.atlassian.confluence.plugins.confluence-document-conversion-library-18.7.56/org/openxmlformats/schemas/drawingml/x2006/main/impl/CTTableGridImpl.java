/*
 * Decompiled with CFR 0.152.
 */
package org.openxmlformats.schemas.drawingml.x2006.main.impl;

import java.util.List;
import javax.xml.namespace.QName;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.impl.values.JavaListXmlObject;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;
import org.openxmlformats.schemas.drawingml.x2006.main.CTTableCol;
import org.openxmlformats.schemas.drawingml.x2006.main.CTTableGrid;

public class CTTableGridImpl
extends XmlComplexContentImpl
implements CTTableGrid {
    private static final long serialVersionUID = 1L;
    private static final QName[] PROPERTY_QNAME = new QName[]{new QName("http://schemas.openxmlformats.org/drawingml/2006/main", "gridCol")};

    public CTTableGridImpl(SchemaType sType) {
        super(sType);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public List<CTTableCol> getGridColList() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return new JavaListXmlObject<CTTableCol>(this::getGridColArray, this::setGridColArray, this::insertNewGridCol, this::removeGridCol, this::sizeOfGridColArray);
        }
    }

    @Override
    public CTTableCol[] getGridColArray() {
        return (CTTableCol[])this.getXmlObjectArray(PROPERTY_QNAME[0], new CTTableCol[0]);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTTableCol getGridColArray(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTTableCol target = null;
            target = (CTTableCol)((Object)this.get_store().find_element_user(PROPERTY_QNAME[0], i));
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
    public int sizeOfGridColArray() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[0]);
        }
    }

    @Override
    public void setGridColArray(CTTableCol[] gridColArray) {
        this.check_orphaned();
        this.arraySetterHelper(gridColArray, PROPERTY_QNAME[0]);
    }

    @Override
    public void setGridColArray(int i, CTTableCol gridCol) {
        this.generatedSetterHelperImpl(gridCol, PROPERTY_QNAME[0], i, (short)2);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTTableCol insertNewGridCol(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTTableCol target = null;
            target = (CTTableCol)((Object)this.get_store().insert_element_user(PROPERTY_QNAME[0], i));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTTableCol addNewGridCol() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTTableCol target = null;
            target = (CTTableCol)((Object)this.get_store().add_element_user(PROPERTY_QNAME[0]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void removeGridCol(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.get_store().remove_element(PROPERTY_QNAME[0], i);
        }
    }
}


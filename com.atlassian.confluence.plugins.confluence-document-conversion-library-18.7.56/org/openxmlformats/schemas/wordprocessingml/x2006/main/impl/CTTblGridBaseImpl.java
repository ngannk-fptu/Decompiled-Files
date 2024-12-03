/*
 * Decompiled with CFR 0.152.
 */
package org.openxmlformats.schemas.wordprocessingml.x2006.main.impl;

import java.util.List;
import javax.xml.namespace.QName;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.impl.values.JavaListXmlObject;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTblGridBase;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTblGridCol;

public class CTTblGridBaseImpl
extends XmlComplexContentImpl
implements CTTblGridBase {
    private static final long serialVersionUID = 1L;
    private static final QName[] PROPERTY_QNAME = new QName[]{new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "gridCol")};

    public CTTblGridBaseImpl(SchemaType sType) {
        super(sType);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public List<CTTblGridCol> getGridColList() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return new JavaListXmlObject<CTTblGridCol>(this::getGridColArray, this::setGridColArray, this::insertNewGridCol, this::removeGridCol, this::sizeOfGridColArray);
        }
    }

    @Override
    public CTTblGridCol[] getGridColArray() {
        return (CTTblGridCol[])this.getXmlObjectArray(PROPERTY_QNAME[0], new CTTblGridCol[0]);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTTblGridCol getGridColArray(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTTblGridCol target = null;
            target = (CTTblGridCol)((Object)this.get_store().find_element_user(PROPERTY_QNAME[0], i));
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
    public void setGridColArray(CTTblGridCol[] gridColArray) {
        this.check_orphaned();
        this.arraySetterHelper(gridColArray, PROPERTY_QNAME[0]);
    }

    @Override
    public void setGridColArray(int i, CTTblGridCol gridCol) {
        this.generatedSetterHelperImpl(gridCol, PROPERTY_QNAME[0], i, (short)2);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTTblGridCol insertNewGridCol(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTTblGridCol target = null;
            target = (CTTblGridCol)((Object)this.get_store().insert_element_user(PROPERTY_QNAME[0], i));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTTblGridCol addNewGridCol() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTTblGridCol target = null;
            target = (CTTblGridCol)((Object)this.get_store().add_element_user(PROPERTY_QNAME[0]));
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


/*
 * Decompiled with CFR 0.152.
 */
package org.openxmlformats.schemas.drawingml.x2006.spreadsheetDrawing.impl;

import javax.xml.namespace.QName;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.SimpleValue;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;
import org.openxmlformats.schemas.drawingml.x2006.main.STCoordinate;
import org.openxmlformats.schemas.drawingml.x2006.spreadsheetDrawing.CTMarker;
import org.openxmlformats.schemas.drawingml.x2006.spreadsheetDrawing.STColID;
import org.openxmlformats.schemas.drawingml.x2006.spreadsheetDrawing.STRowID;

public class CTMarkerImpl
extends XmlComplexContentImpl
implements CTMarker {
    private static final long serialVersionUID = 1L;
    private static final QName[] PROPERTY_QNAME = new QName[]{new QName("http://schemas.openxmlformats.org/drawingml/2006/spreadsheetDrawing", "col"), new QName("http://schemas.openxmlformats.org/drawingml/2006/spreadsheetDrawing", "colOff"), new QName("http://schemas.openxmlformats.org/drawingml/2006/spreadsheetDrawing", "row"), new QName("http://schemas.openxmlformats.org/drawingml/2006/spreadsheetDrawing", "rowOff")};

    public CTMarkerImpl(SchemaType sType) {
        super(sType);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public int getCol() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)((Object)this.get_store().find_element_user(PROPERTY_QNAME[0], 0));
            return target == null ? 0 : target.getIntValue();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public STColID xgetCol() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            STColID target = null;
            target = (STColID)((Object)this.get_store().find_element_user(PROPERTY_QNAME[0], 0));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void setCol(int col) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)((Object)this.get_store().find_element_user(PROPERTY_QNAME[0], 0));
            if (target == null) {
                target = (SimpleValue)((Object)this.get_store().add_element_user(PROPERTY_QNAME[0]));
            }
            target.setIntValue(col);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void xsetCol(STColID col) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            STColID target = null;
            target = (STColID)((Object)this.get_store().find_element_user(PROPERTY_QNAME[0], 0));
            if (target == null) {
                target = (STColID)((Object)this.get_store().add_element_user(PROPERTY_QNAME[0]));
            }
            target.set(col);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public Object getColOff() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)((Object)this.get_store().find_element_user(PROPERTY_QNAME[1], 0));
            return target == null ? null : target.getObjectValue();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public STCoordinate xgetColOff() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            STCoordinate target = null;
            target = (STCoordinate)((Object)this.get_store().find_element_user(PROPERTY_QNAME[1], 0));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void setColOff(Object colOff) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)((Object)this.get_store().find_element_user(PROPERTY_QNAME[1], 0));
            if (target == null) {
                target = (SimpleValue)((Object)this.get_store().add_element_user(PROPERTY_QNAME[1]));
            }
            target.setObjectValue(colOff);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void xsetColOff(STCoordinate colOff) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            STCoordinate target = null;
            target = (STCoordinate)((Object)this.get_store().find_element_user(PROPERTY_QNAME[1], 0));
            if (target == null) {
                target = (STCoordinate)((Object)this.get_store().add_element_user(PROPERTY_QNAME[1]));
            }
            target.set(colOff);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public int getRow() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)((Object)this.get_store().find_element_user(PROPERTY_QNAME[2], 0));
            return target == null ? 0 : target.getIntValue();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public STRowID xgetRow() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            STRowID target = null;
            target = (STRowID)((Object)this.get_store().find_element_user(PROPERTY_QNAME[2], 0));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void setRow(int row) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)((Object)this.get_store().find_element_user(PROPERTY_QNAME[2], 0));
            if (target == null) {
                target = (SimpleValue)((Object)this.get_store().add_element_user(PROPERTY_QNAME[2]));
            }
            target.setIntValue(row);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void xsetRow(STRowID row) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            STRowID target = null;
            target = (STRowID)((Object)this.get_store().find_element_user(PROPERTY_QNAME[2], 0));
            if (target == null) {
                target = (STRowID)((Object)this.get_store().add_element_user(PROPERTY_QNAME[2]));
            }
            target.set(row);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public Object getRowOff() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)((Object)this.get_store().find_element_user(PROPERTY_QNAME[3], 0));
            return target == null ? null : target.getObjectValue();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public STCoordinate xgetRowOff() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            STCoordinate target = null;
            target = (STCoordinate)((Object)this.get_store().find_element_user(PROPERTY_QNAME[3], 0));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void setRowOff(Object rowOff) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)((Object)this.get_store().find_element_user(PROPERTY_QNAME[3], 0));
            if (target == null) {
                target = (SimpleValue)((Object)this.get_store().add_element_user(PROPERTY_QNAME[3]));
            }
            target.setObjectValue(rowOff);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void xsetRowOff(STCoordinate rowOff) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            STCoordinate target = null;
            target = (STCoordinate)((Object)this.get_store().find_element_user(PROPERTY_QNAME[3], 0));
            if (target == null) {
                target = (STCoordinate)((Object)this.get_store().add_element_user(PROPERTY_QNAME[3]));
            }
            target.set(rowOff);
        }
    }
}


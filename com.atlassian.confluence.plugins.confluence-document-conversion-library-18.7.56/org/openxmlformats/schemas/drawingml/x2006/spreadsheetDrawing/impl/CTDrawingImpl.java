/*
 * Decompiled with CFR 0.152.
 */
package org.openxmlformats.schemas.drawingml.x2006.spreadsheetDrawing.impl;

import java.util.List;
import javax.xml.namespace.QName;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.impl.values.JavaListXmlObject;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;
import org.openxmlformats.schemas.drawingml.x2006.spreadsheetDrawing.CTAbsoluteAnchor;
import org.openxmlformats.schemas.drawingml.x2006.spreadsheetDrawing.CTDrawing;
import org.openxmlformats.schemas.drawingml.x2006.spreadsheetDrawing.CTOneCellAnchor;
import org.openxmlformats.schemas.drawingml.x2006.spreadsheetDrawing.CTTwoCellAnchor;

public class CTDrawingImpl
extends XmlComplexContentImpl
implements CTDrawing {
    private static final long serialVersionUID = 1L;
    private static final QName[] PROPERTY_QNAME = new QName[]{new QName("http://schemas.openxmlformats.org/drawingml/2006/spreadsheetDrawing", "twoCellAnchor"), new QName("http://schemas.openxmlformats.org/drawingml/2006/spreadsheetDrawing", "oneCellAnchor"), new QName("http://schemas.openxmlformats.org/drawingml/2006/spreadsheetDrawing", "absoluteAnchor")};

    public CTDrawingImpl(SchemaType sType) {
        super(sType);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public List<CTTwoCellAnchor> getTwoCellAnchorList() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return new JavaListXmlObject<CTTwoCellAnchor>(this::getTwoCellAnchorArray, this::setTwoCellAnchorArray, this::insertNewTwoCellAnchor, this::removeTwoCellAnchor, this::sizeOfTwoCellAnchorArray);
        }
    }

    @Override
    public CTTwoCellAnchor[] getTwoCellAnchorArray() {
        return (CTTwoCellAnchor[])this.getXmlObjectArray(PROPERTY_QNAME[0], new CTTwoCellAnchor[0]);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTTwoCellAnchor getTwoCellAnchorArray(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTTwoCellAnchor target = null;
            target = (CTTwoCellAnchor)((Object)this.get_store().find_element_user(PROPERTY_QNAME[0], i));
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
    public int sizeOfTwoCellAnchorArray() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[0]);
        }
    }

    @Override
    public void setTwoCellAnchorArray(CTTwoCellAnchor[] twoCellAnchorArray) {
        this.check_orphaned();
        this.arraySetterHelper(twoCellAnchorArray, PROPERTY_QNAME[0]);
    }

    @Override
    public void setTwoCellAnchorArray(int i, CTTwoCellAnchor twoCellAnchor) {
        this.generatedSetterHelperImpl(twoCellAnchor, PROPERTY_QNAME[0], i, (short)2);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTTwoCellAnchor insertNewTwoCellAnchor(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTTwoCellAnchor target = null;
            target = (CTTwoCellAnchor)((Object)this.get_store().insert_element_user(PROPERTY_QNAME[0], i));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTTwoCellAnchor addNewTwoCellAnchor() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTTwoCellAnchor target = null;
            target = (CTTwoCellAnchor)((Object)this.get_store().add_element_user(PROPERTY_QNAME[0]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void removeTwoCellAnchor(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.get_store().remove_element(PROPERTY_QNAME[0], i);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public List<CTOneCellAnchor> getOneCellAnchorList() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return new JavaListXmlObject<CTOneCellAnchor>(this::getOneCellAnchorArray, this::setOneCellAnchorArray, this::insertNewOneCellAnchor, this::removeOneCellAnchor, this::sizeOfOneCellAnchorArray);
        }
    }

    @Override
    public CTOneCellAnchor[] getOneCellAnchorArray() {
        return (CTOneCellAnchor[])this.getXmlObjectArray(PROPERTY_QNAME[1], new CTOneCellAnchor[0]);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTOneCellAnchor getOneCellAnchorArray(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTOneCellAnchor target = null;
            target = (CTOneCellAnchor)((Object)this.get_store().find_element_user(PROPERTY_QNAME[1], i));
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
    public int sizeOfOneCellAnchorArray() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[1]);
        }
    }

    @Override
    public void setOneCellAnchorArray(CTOneCellAnchor[] oneCellAnchorArray) {
        this.check_orphaned();
        this.arraySetterHelper(oneCellAnchorArray, PROPERTY_QNAME[1]);
    }

    @Override
    public void setOneCellAnchorArray(int i, CTOneCellAnchor oneCellAnchor) {
        this.generatedSetterHelperImpl(oneCellAnchor, PROPERTY_QNAME[1], i, (short)2);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTOneCellAnchor insertNewOneCellAnchor(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTOneCellAnchor target = null;
            target = (CTOneCellAnchor)((Object)this.get_store().insert_element_user(PROPERTY_QNAME[1], i));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTOneCellAnchor addNewOneCellAnchor() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTOneCellAnchor target = null;
            target = (CTOneCellAnchor)((Object)this.get_store().add_element_user(PROPERTY_QNAME[1]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void removeOneCellAnchor(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.get_store().remove_element(PROPERTY_QNAME[1], i);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public List<CTAbsoluteAnchor> getAbsoluteAnchorList() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return new JavaListXmlObject<CTAbsoluteAnchor>(this::getAbsoluteAnchorArray, this::setAbsoluteAnchorArray, this::insertNewAbsoluteAnchor, this::removeAbsoluteAnchor, this::sizeOfAbsoluteAnchorArray);
        }
    }

    @Override
    public CTAbsoluteAnchor[] getAbsoluteAnchorArray() {
        return (CTAbsoluteAnchor[])this.getXmlObjectArray(PROPERTY_QNAME[2], new CTAbsoluteAnchor[0]);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTAbsoluteAnchor getAbsoluteAnchorArray(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTAbsoluteAnchor target = null;
            target = (CTAbsoluteAnchor)((Object)this.get_store().find_element_user(PROPERTY_QNAME[2], i));
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
    public int sizeOfAbsoluteAnchorArray() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[2]);
        }
    }

    @Override
    public void setAbsoluteAnchorArray(CTAbsoluteAnchor[] absoluteAnchorArray) {
        this.check_orphaned();
        this.arraySetterHelper(absoluteAnchorArray, PROPERTY_QNAME[2]);
    }

    @Override
    public void setAbsoluteAnchorArray(int i, CTAbsoluteAnchor absoluteAnchor) {
        this.generatedSetterHelperImpl(absoluteAnchor, PROPERTY_QNAME[2], i, (short)2);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTAbsoluteAnchor insertNewAbsoluteAnchor(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTAbsoluteAnchor target = null;
            target = (CTAbsoluteAnchor)((Object)this.get_store().insert_element_user(PROPERTY_QNAME[2], i));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTAbsoluteAnchor addNewAbsoluteAnchor() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTAbsoluteAnchor target = null;
            target = (CTAbsoluteAnchor)((Object)this.get_store().add_element_user(PROPERTY_QNAME[2]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void removeAbsoluteAnchor(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.get_store().remove_element(PROPERTY_QNAME[2], i);
        }
    }
}


/*
 * Decompiled with CFR 0.152.
 */
package org.openxmlformats.schemas.drawingml.x2006.main.impl;

import java.util.List;
import javax.xml.namespace.QName;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.impl.values.JavaListXmlObject;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;
import org.openxmlformats.schemas.drawingml.x2006.main.CTGeomGuide;
import org.openxmlformats.schemas.drawingml.x2006.main.CTGeomGuideList;

public class CTGeomGuideListImpl
extends XmlComplexContentImpl
implements CTGeomGuideList {
    private static final long serialVersionUID = 1L;
    private static final QName[] PROPERTY_QNAME = new QName[]{new QName("http://schemas.openxmlformats.org/drawingml/2006/main", "gd")};

    public CTGeomGuideListImpl(SchemaType sType) {
        super(sType);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public List<CTGeomGuide> getGdList() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return new JavaListXmlObject<CTGeomGuide>(this::getGdArray, this::setGdArray, this::insertNewGd, this::removeGd, this::sizeOfGdArray);
        }
    }

    @Override
    public CTGeomGuide[] getGdArray() {
        return (CTGeomGuide[])this.getXmlObjectArray(PROPERTY_QNAME[0], new CTGeomGuide[0]);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTGeomGuide getGdArray(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTGeomGuide target = null;
            target = (CTGeomGuide)((Object)this.get_store().find_element_user(PROPERTY_QNAME[0], i));
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
    public int sizeOfGdArray() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[0]);
        }
    }

    @Override
    public void setGdArray(CTGeomGuide[] gdArray) {
        this.check_orphaned();
        this.arraySetterHelper(gdArray, PROPERTY_QNAME[0]);
    }

    @Override
    public void setGdArray(int i, CTGeomGuide gd) {
        this.generatedSetterHelperImpl(gd, PROPERTY_QNAME[0], i, (short)2);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTGeomGuide insertNewGd(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTGeomGuide target = null;
            target = (CTGeomGuide)((Object)this.get_store().insert_element_user(PROPERTY_QNAME[0], i));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTGeomGuide addNewGd() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTGeomGuide target = null;
            target = (CTGeomGuide)((Object)this.get_store().add_element_user(PROPERTY_QNAME[0]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void removeGd(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.get_store().remove_element(PROPERTY_QNAME[0], i);
        }
    }
}


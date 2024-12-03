/*
 * Decompiled with CFR 0.152.
 */
package com.microsoft.schemas.office.visio.x2012.main.impl;

import com.microsoft.schemas.office.visio.x2012.main.ShapeSheetType;
import com.microsoft.schemas.office.visio.x2012.main.ShapesType;
import java.util.List;
import javax.xml.namespace.QName;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.impl.values.JavaListXmlObject;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;

public class ShapesTypeImpl
extends XmlComplexContentImpl
implements ShapesType {
    private static final long serialVersionUID = 1L;
    private static final QName[] PROPERTY_QNAME = new QName[]{new QName("http://schemas.microsoft.com/office/visio/2012/main", "Shape")};

    public ShapesTypeImpl(SchemaType sType) {
        super(sType);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public List<ShapeSheetType> getShapeList() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return new JavaListXmlObject<ShapeSheetType>(this::getShapeArray, this::setShapeArray, this::insertNewShape, this::removeShape, this::sizeOfShapeArray);
        }
    }

    @Override
    public ShapeSheetType[] getShapeArray() {
        return (ShapeSheetType[])this.getXmlObjectArray(PROPERTY_QNAME[0], new ShapeSheetType[0]);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public ShapeSheetType getShapeArray(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            ShapeSheetType target = null;
            target = (ShapeSheetType)((Object)this.get_store().find_element_user(PROPERTY_QNAME[0], i));
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
    public int sizeOfShapeArray() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[0]);
        }
    }

    @Override
    public void setShapeArray(ShapeSheetType[] shapeArray) {
        this.check_orphaned();
        this.arraySetterHelper(shapeArray, PROPERTY_QNAME[0]);
    }

    @Override
    public void setShapeArray(int i, ShapeSheetType shape) {
        this.generatedSetterHelperImpl(shape, PROPERTY_QNAME[0], i, (short)2);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public ShapeSheetType insertNewShape(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            ShapeSheetType target = null;
            target = (ShapeSheetType)((Object)this.get_store().insert_element_user(PROPERTY_QNAME[0], i));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public ShapeSheetType addNewShape() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            ShapeSheetType target = null;
            target = (ShapeSheetType)((Object)this.get_store().add_element_user(PROPERTY_QNAME[0]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void removeShape(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.get_store().remove_element(PROPERTY_QNAME[0], i);
        }
    }
}


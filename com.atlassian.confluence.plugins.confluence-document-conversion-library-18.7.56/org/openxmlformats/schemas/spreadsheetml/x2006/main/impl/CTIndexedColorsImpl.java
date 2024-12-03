/*
 * Decompiled with CFR 0.152.
 */
package org.openxmlformats.schemas.spreadsheetml.x2006.main.impl;

import java.util.List;
import javax.xml.namespace.QName;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.impl.values.JavaListXmlObject;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTIndexedColors;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTRgbColor;

public class CTIndexedColorsImpl
extends XmlComplexContentImpl
implements CTIndexedColors {
    private static final long serialVersionUID = 1L;
    private static final QName[] PROPERTY_QNAME = new QName[]{new QName("http://schemas.openxmlformats.org/spreadsheetml/2006/main", "rgbColor")};

    public CTIndexedColorsImpl(SchemaType sType) {
        super(sType);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public List<CTRgbColor> getRgbColorList() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return new JavaListXmlObject<CTRgbColor>(this::getRgbColorArray, this::setRgbColorArray, this::insertNewRgbColor, this::removeRgbColor, this::sizeOfRgbColorArray);
        }
    }

    @Override
    public CTRgbColor[] getRgbColorArray() {
        return (CTRgbColor[])this.getXmlObjectArray(PROPERTY_QNAME[0], new CTRgbColor[0]);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTRgbColor getRgbColorArray(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTRgbColor target = null;
            target = (CTRgbColor)((Object)this.get_store().find_element_user(PROPERTY_QNAME[0], i));
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
    public int sizeOfRgbColorArray() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[0]);
        }
    }

    @Override
    public void setRgbColorArray(CTRgbColor[] rgbColorArray) {
        this.check_orphaned();
        this.arraySetterHelper(rgbColorArray, PROPERTY_QNAME[0]);
    }

    @Override
    public void setRgbColorArray(int i, CTRgbColor rgbColor) {
        this.generatedSetterHelperImpl(rgbColor, PROPERTY_QNAME[0], i, (short)2);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTRgbColor insertNewRgbColor(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTRgbColor target = null;
            target = (CTRgbColor)((Object)this.get_store().insert_element_user(PROPERTY_QNAME[0], i));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTRgbColor addNewRgbColor() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTRgbColor target = null;
            target = (CTRgbColor)((Object)this.get_store().add_element_user(PROPERTY_QNAME[0]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void removeRgbColor(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.get_store().remove_element(PROPERTY_QNAME[0], i);
        }
    }
}


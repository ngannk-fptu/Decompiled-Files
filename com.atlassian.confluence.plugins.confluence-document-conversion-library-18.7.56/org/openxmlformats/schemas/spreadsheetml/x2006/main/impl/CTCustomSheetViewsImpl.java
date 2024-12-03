/*
 * Decompiled with CFR 0.152.
 */
package org.openxmlformats.schemas.spreadsheetml.x2006.main.impl;

import java.util.List;
import javax.xml.namespace.QName;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.impl.values.JavaListXmlObject;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTCustomSheetView;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTCustomSheetViews;

public class CTCustomSheetViewsImpl
extends XmlComplexContentImpl
implements CTCustomSheetViews {
    private static final long serialVersionUID = 1L;
    private static final QName[] PROPERTY_QNAME = new QName[]{new QName("http://schemas.openxmlformats.org/spreadsheetml/2006/main", "customSheetView")};

    public CTCustomSheetViewsImpl(SchemaType sType) {
        super(sType);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public List<CTCustomSheetView> getCustomSheetViewList() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return new JavaListXmlObject<CTCustomSheetView>(this::getCustomSheetViewArray, this::setCustomSheetViewArray, this::insertNewCustomSheetView, this::removeCustomSheetView, this::sizeOfCustomSheetViewArray);
        }
    }

    @Override
    public CTCustomSheetView[] getCustomSheetViewArray() {
        return (CTCustomSheetView[])this.getXmlObjectArray(PROPERTY_QNAME[0], new CTCustomSheetView[0]);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTCustomSheetView getCustomSheetViewArray(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTCustomSheetView target = null;
            target = (CTCustomSheetView)((Object)this.get_store().find_element_user(PROPERTY_QNAME[0], i));
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
    public int sizeOfCustomSheetViewArray() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[0]);
        }
    }

    @Override
    public void setCustomSheetViewArray(CTCustomSheetView[] customSheetViewArray) {
        this.check_orphaned();
        this.arraySetterHelper(customSheetViewArray, PROPERTY_QNAME[0]);
    }

    @Override
    public void setCustomSheetViewArray(int i, CTCustomSheetView customSheetView) {
        this.generatedSetterHelperImpl(customSheetView, PROPERTY_QNAME[0], i, (short)2);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTCustomSheetView insertNewCustomSheetView(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTCustomSheetView target = null;
            target = (CTCustomSheetView)((Object)this.get_store().insert_element_user(PROPERTY_QNAME[0], i));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTCustomSheetView addNewCustomSheetView() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTCustomSheetView target = null;
            target = (CTCustomSheetView)((Object)this.get_store().add_element_user(PROPERTY_QNAME[0]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void removeCustomSheetView(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.get_store().remove_element(PROPERTY_QNAME[0], i);
        }
    }
}


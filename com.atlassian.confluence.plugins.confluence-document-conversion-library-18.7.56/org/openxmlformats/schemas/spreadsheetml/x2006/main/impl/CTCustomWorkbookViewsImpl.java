/*
 * Decompiled with CFR 0.152.
 */
package org.openxmlformats.schemas.spreadsheetml.x2006.main.impl;

import java.util.List;
import javax.xml.namespace.QName;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.impl.values.JavaListXmlObject;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTCustomWorkbookView;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTCustomWorkbookViews;

public class CTCustomWorkbookViewsImpl
extends XmlComplexContentImpl
implements CTCustomWorkbookViews {
    private static final long serialVersionUID = 1L;
    private static final QName[] PROPERTY_QNAME = new QName[]{new QName("http://schemas.openxmlformats.org/spreadsheetml/2006/main", "customWorkbookView")};

    public CTCustomWorkbookViewsImpl(SchemaType sType) {
        super(sType);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public List<CTCustomWorkbookView> getCustomWorkbookViewList() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return new JavaListXmlObject<CTCustomWorkbookView>(this::getCustomWorkbookViewArray, this::setCustomWorkbookViewArray, this::insertNewCustomWorkbookView, this::removeCustomWorkbookView, this::sizeOfCustomWorkbookViewArray);
        }
    }

    @Override
    public CTCustomWorkbookView[] getCustomWorkbookViewArray() {
        return (CTCustomWorkbookView[])this.getXmlObjectArray(PROPERTY_QNAME[0], new CTCustomWorkbookView[0]);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTCustomWorkbookView getCustomWorkbookViewArray(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTCustomWorkbookView target = null;
            target = (CTCustomWorkbookView)((Object)this.get_store().find_element_user(PROPERTY_QNAME[0], i));
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
    public int sizeOfCustomWorkbookViewArray() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[0]);
        }
    }

    @Override
    public void setCustomWorkbookViewArray(CTCustomWorkbookView[] customWorkbookViewArray) {
        this.check_orphaned();
        this.arraySetterHelper(customWorkbookViewArray, PROPERTY_QNAME[0]);
    }

    @Override
    public void setCustomWorkbookViewArray(int i, CTCustomWorkbookView customWorkbookView) {
        this.generatedSetterHelperImpl(customWorkbookView, PROPERTY_QNAME[0], i, (short)2);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTCustomWorkbookView insertNewCustomWorkbookView(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTCustomWorkbookView target = null;
            target = (CTCustomWorkbookView)((Object)this.get_store().insert_element_user(PROPERTY_QNAME[0], i));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTCustomWorkbookView addNewCustomWorkbookView() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTCustomWorkbookView target = null;
            target = (CTCustomWorkbookView)((Object)this.get_store().add_element_user(PROPERTY_QNAME[0]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void removeCustomWorkbookView(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.get_store().remove_element(PROPERTY_QNAME[0], i);
        }
    }
}


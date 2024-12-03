/*
 * Decompiled with CFR 0.152.
 */
package org.openxmlformats.schemas.spreadsheetml.x2006.main.impl;

import java.util.List;
import javax.xml.namespace.QName;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.impl.values.JavaListXmlObject;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTBookView;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTBookViews;

public class CTBookViewsImpl
extends XmlComplexContentImpl
implements CTBookViews {
    private static final long serialVersionUID = 1L;
    private static final QName[] PROPERTY_QNAME = new QName[]{new QName("http://schemas.openxmlformats.org/spreadsheetml/2006/main", "workbookView")};

    public CTBookViewsImpl(SchemaType sType) {
        super(sType);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public List<CTBookView> getWorkbookViewList() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return new JavaListXmlObject<CTBookView>(this::getWorkbookViewArray, this::setWorkbookViewArray, this::insertNewWorkbookView, this::removeWorkbookView, this::sizeOfWorkbookViewArray);
        }
    }

    @Override
    public CTBookView[] getWorkbookViewArray() {
        return (CTBookView[])this.getXmlObjectArray(PROPERTY_QNAME[0], new CTBookView[0]);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTBookView getWorkbookViewArray(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTBookView target = null;
            target = (CTBookView)((Object)this.get_store().find_element_user(PROPERTY_QNAME[0], i));
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
    public int sizeOfWorkbookViewArray() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[0]);
        }
    }

    @Override
    public void setWorkbookViewArray(CTBookView[] workbookViewArray) {
        this.check_orphaned();
        this.arraySetterHelper(workbookViewArray, PROPERTY_QNAME[0]);
    }

    @Override
    public void setWorkbookViewArray(int i, CTBookView workbookView) {
        this.generatedSetterHelperImpl(workbookView, PROPERTY_QNAME[0], i, (short)2);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTBookView insertNewWorkbookView(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTBookView target = null;
            target = (CTBookView)((Object)this.get_store().insert_element_user(PROPERTY_QNAME[0], i));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTBookView addNewWorkbookView() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTBookView target = null;
            target = (CTBookView)((Object)this.get_store().add_element_user(PROPERTY_QNAME[0]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void removeWorkbookView(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.get_store().remove_element(PROPERTY_QNAME[0], i);
        }
    }
}


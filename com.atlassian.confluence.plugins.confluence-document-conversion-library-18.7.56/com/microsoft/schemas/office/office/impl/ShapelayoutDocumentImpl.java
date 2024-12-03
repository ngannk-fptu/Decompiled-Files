/*
 * Decompiled with CFR 0.152.
 */
package com.microsoft.schemas.office.office.impl;

import com.microsoft.schemas.office.office.CTShapeLayout;
import com.microsoft.schemas.office.office.ShapelayoutDocument;
import javax.xml.namespace.QName;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;

public class ShapelayoutDocumentImpl
extends XmlComplexContentImpl
implements ShapelayoutDocument {
    private static final long serialVersionUID = 1L;
    private static final QName[] PROPERTY_QNAME = new QName[]{new QName("urn:schemas-microsoft-com:office:office", "shapelayout")};

    public ShapelayoutDocumentImpl(SchemaType sType) {
        super(sType);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTShapeLayout getShapelayout() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTShapeLayout target = null;
            target = (CTShapeLayout)((Object)this.get_store().find_element_user(PROPERTY_QNAME[0], 0));
            return target == null ? null : target;
        }
    }

    @Override
    public void setShapelayout(CTShapeLayout shapelayout) {
        this.generatedSetterHelperImpl(shapelayout, PROPERTY_QNAME[0], 0, (short)1);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTShapeLayout addNewShapelayout() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTShapeLayout target = null;
            target = (CTShapeLayout)((Object)this.get_store().add_element_user(PROPERTY_QNAME[0]));
            return target;
        }
    }
}


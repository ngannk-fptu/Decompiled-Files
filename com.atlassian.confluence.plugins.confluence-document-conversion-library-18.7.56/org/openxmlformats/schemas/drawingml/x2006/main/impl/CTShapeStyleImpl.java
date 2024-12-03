/*
 * Decompiled with CFR 0.152.
 */
package org.openxmlformats.schemas.drawingml.x2006.main.impl;

import javax.xml.namespace.QName;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;
import org.openxmlformats.schemas.drawingml.x2006.main.CTFontReference;
import org.openxmlformats.schemas.drawingml.x2006.main.CTShapeStyle;
import org.openxmlformats.schemas.drawingml.x2006.main.CTStyleMatrixReference;

public class CTShapeStyleImpl
extends XmlComplexContentImpl
implements CTShapeStyle {
    private static final long serialVersionUID = 1L;
    private static final QName[] PROPERTY_QNAME = new QName[]{new QName("http://schemas.openxmlformats.org/drawingml/2006/main", "lnRef"), new QName("http://schemas.openxmlformats.org/drawingml/2006/main", "fillRef"), new QName("http://schemas.openxmlformats.org/drawingml/2006/main", "effectRef"), new QName("http://schemas.openxmlformats.org/drawingml/2006/main", "fontRef")};

    public CTShapeStyleImpl(SchemaType sType) {
        super(sType);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTStyleMatrixReference getLnRef() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTStyleMatrixReference target = null;
            target = (CTStyleMatrixReference)((Object)this.get_store().find_element_user(PROPERTY_QNAME[0], 0));
            return target == null ? null : target;
        }
    }

    @Override
    public void setLnRef(CTStyleMatrixReference lnRef) {
        this.generatedSetterHelperImpl(lnRef, PROPERTY_QNAME[0], 0, (short)1);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTStyleMatrixReference addNewLnRef() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTStyleMatrixReference target = null;
            target = (CTStyleMatrixReference)((Object)this.get_store().add_element_user(PROPERTY_QNAME[0]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTStyleMatrixReference getFillRef() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTStyleMatrixReference target = null;
            target = (CTStyleMatrixReference)((Object)this.get_store().find_element_user(PROPERTY_QNAME[1], 0));
            return target == null ? null : target;
        }
    }

    @Override
    public void setFillRef(CTStyleMatrixReference fillRef) {
        this.generatedSetterHelperImpl(fillRef, PROPERTY_QNAME[1], 0, (short)1);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTStyleMatrixReference addNewFillRef() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTStyleMatrixReference target = null;
            target = (CTStyleMatrixReference)((Object)this.get_store().add_element_user(PROPERTY_QNAME[1]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTStyleMatrixReference getEffectRef() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTStyleMatrixReference target = null;
            target = (CTStyleMatrixReference)((Object)this.get_store().find_element_user(PROPERTY_QNAME[2], 0));
            return target == null ? null : target;
        }
    }

    @Override
    public void setEffectRef(CTStyleMatrixReference effectRef) {
        this.generatedSetterHelperImpl(effectRef, PROPERTY_QNAME[2], 0, (short)1);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTStyleMatrixReference addNewEffectRef() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTStyleMatrixReference target = null;
            target = (CTStyleMatrixReference)((Object)this.get_store().add_element_user(PROPERTY_QNAME[2]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTFontReference getFontRef() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTFontReference target = null;
            target = (CTFontReference)((Object)this.get_store().find_element_user(PROPERTY_QNAME[3], 0));
            return target == null ? null : target;
        }
    }

    @Override
    public void setFontRef(CTFontReference fontRef) {
        this.generatedSetterHelperImpl(fontRef, PROPERTY_QNAME[3], 0, (short)1);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTFontReference addNewFontRef() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTFontReference target = null;
            target = (CTFontReference)((Object)this.get_store().add_element_user(PROPERTY_QNAME[3]));
            return target;
        }
    }
}


/*
 * Decompiled with CFR 0.152.
 */
package com.microsoft.schemas.office.drawing.x2008.diagram.impl;

import com.microsoft.schemas.office.drawing.x2008.diagram.CTDrawing;
import com.microsoft.schemas.office.drawing.x2008.diagram.CTGroupShape;
import javax.xml.namespace.QName;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;

public class CTDrawingImpl
extends XmlComplexContentImpl
implements CTDrawing {
    private static final long serialVersionUID = 1L;
    private static final QName[] PROPERTY_QNAME = new QName[]{new QName("http://schemas.microsoft.com/office/drawing/2008/diagram", "spTree")};

    public CTDrawingImpl(SchemaType sType) {
        super(sType);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTGroupShape getSpTree() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTGroupShape target = null;
            target = (CTGroupShape)((Object)this.get_store().find_element_user(PROPERTY_QNAME[0], 0));
            return target == null ? null : target;
        }
    }

    @Override
    public void setSpTree(CTGroupShape spTree) {
        this.generatedSetterHelperImpl(spTree, PROPERTY_QNAME[0], 0, (short)1);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTGroupShape addNewSpTree() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTGroupShape target = null;
            target = (CTGroupShape)((Object)this.get_store().add_element_user(PROPERTY_QNAME[0]));
            return target;
        }
    }
}


/*
 * Decompiled with CFR 0.152.
 */
package com.microsoft.schemas.office.drawing.x2008.diagram.impl;

import com.microsoft.schemas.office.drawing.x2008.diagram.CTGroupShapeNonVisual;
import javax.xml.namespace.QName;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;
import org.openxmlformats.schemas.drawingml.x2006.main.CTNonVisualDrawingProps;
import org.openxmlformats.schemas.drawingml.x2006.main.CTNonVisualGroupDrawingShapeProps;

public class CTGroupShapeNonVisualImpl
extends XmlComplexContentImpl
implements CTGroupShapeNonVisual {
    private static final long serialVersionUID = 1L;
    private static final QName[] PROPERTY_QNAME = new QName[]{new QName("http://schemas.microsoft.com/office/drawing/2008/diagram", "cNvPr"), new QName("http://schemas.microsoft.com/office/drawing/2008/diagram", "cNvGrpSpPr")};

    public CTGroupShapeNonVisualImpl(SchemaType sType) {
        super(sType);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTNonVisualDrawingProps getCNvPr() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTNonVisualDrawingProps target = null;
            target = (CTNonVisualDrawingProps)((Object)this.get_store().find_element_user(PROPERTY_QNAME[0], 0));
            return target == null ? null : target;
        }
    }

    @Override
    public void setCNvPr(CTNonVisualDrawingProps cNvPr) {
        this.generatedSetterHelperImpl(cNvPr, PROPERTY_QNAME[0], 0, (short)1);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTNonVisualDrawingProps addNewCNvPr() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTNonVisualDrawingProps target = null;
            target = (CTNonVisualDrawingProps)((Object)this.get_store().add_element_user(PROPERTY_QNAME[0]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTNonVisualGroupDrawingShapeProps getCNvGrpSpPr() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTNonVisualGroupDrawingShapeProps target = null;
            target = (CTNonVisualGroupDrawingShapeProps)((Object)this.get_store().find_element_user(PROPERTY_QNAME[1], 0));
            return target == null ? null : target;
        }
    }

    @Override
    public void setCNvGrpSpPr(CTNonVisualGroupDrawingShapeProps cNvGrpSpPr) {
        this.generatedSetterHelperImpl(cNvGrpSpPr, PROPERTY_QNAME[1], 0, (short)1);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTNonVisualGroupDrawingShapeProps addNewCNvGrpSpPr() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTNonVisualGroupDrawingShapeProps target = null;
            target = (CTNonVisualGroupDrawingShapeProps)((Object)this.get_store().add_element_user(PROPERTY_QNAME[1]));
            return target;
        }
    }
}


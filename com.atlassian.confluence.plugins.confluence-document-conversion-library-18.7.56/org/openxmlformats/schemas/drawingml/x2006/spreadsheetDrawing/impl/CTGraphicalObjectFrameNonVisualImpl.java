/*
 * Decompiled with CFR 0.152.
 */
package org.openxmlformats.schemas.drawingml.x2006.spreadsheetDrawing.impl;

import javax.xml.namespace.QName;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;
import org.openxmlformats.schemas.drawingml.x2006.main.CTNonVisualDrawingProps;
import org.openxmlformats.schemas.drawingml.x2006.main.CTNonVisualGraphicFrameProperties;
import org.openxmlformats.schemas.drawingml.x2006.spreadsheetDrawing.CTGraphicalObjectFrameNonVisual;

public class CTGraphicalObjectFrameNonVisualImpl
extends XmlComplexContentImpl
implements CTGraphicalObjectFrameNonVisual {
    private static final long serialVersionUID = 1L;
    private static final QName[] PROPERTY_QNAME = new QName[]{new QName("http://schemas.openxmlformats.org/drawingml/2006/spreadsheetDrawing", "cNvPr"), new QName("http://schemas.openxmlformats.org/drawingml/2006/spreadsheetDrawing", "cNvGraphicFramePr")};

    public CTGraphicalObjectFrameNonVisualImpl(SchemaType sType) {
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
    public CTNonVisualGraphicFrameProperties getCNvGraphicFramePr() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTNonVisualGraphicFrameProperties target = null;
            target = (CTNonVisualGraphicFrameProperties)((Object)this.get_store().find_element_user(PROPERTY_QNAME[1], 0));
            return target == null ? null : target;
        }
    }

    @Override
    public void setCNvGraphicFramePr(CTNonVisualGraphicFrameProperties cNvGraphicFramePr) {
        this.generatedSetterHelperImpl(cNvGraphicFramePr, PROPERTY_QNAME[1], 0, (short)1);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTNonVisualGraphicFrameProperties addNewCNvGraphicFramePr() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTNonVisualGraphicFrameProperties target = null;
            target = (CTNonVisualGraphicFrameProperties)((Object)this.get_store().add_element_user(PROPERTY_QNAME[1]));
            return target;
        }
    }
}


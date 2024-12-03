/*
 * Decompiled with CFR 0.152.
 */
package org.openxmlformats.schemas.presentationml.x2006.main.impl;

import javax.xml.namespace.QName;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;
import org.openxmlformats.schemas.drawingml.x2006.main.CTNonVisualConnectorProperties;
import org.openxmlformats.schemas.drawingml.x2006.main.CTNonVisualDrawingProps;
import org.openxmlformats.schemas.presentationml.x2006.main.CTApplicationNonVisualDrawingProps;
import org.openxmlformats.schemas.presentationml.x2006.main.CTConnectorNonVisual;

public class CTConnectorNonVisualImpl
extends XmlComplexContentImpl
implements CTConnectorNonVisual {
    private static final long serialVersionUID = 1L;
    private static final QName[] PROPERTY_QNAME = new QName[]{new QName("http://schemas.openxmlformats.org/presentationml/2006/main", "cNvPr"), new QName("http://schemas.openxmlformats.org/presentationml/2006/main", "cNvCxnSpPr"), new QName("http://schemas.openxmlformats.org/presentationml/2006/main", "nvPr")};

    public CTConnectorNonVisualImpl(SchemaType sType) {
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
    public CTNonVisualConnectorProperties getCNvCxnSpPr() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTNonVisualConnectorProperties target = null;
            target = (CTNonVisualConnectorProperties)((Object)this.get_store().find_element_user(PROPERTY_QNAME[1], 0));
            return target == null ? null : target;
        }
    }

    @Override
    public void setCNvCxnSpPr(CTNonVisualConnectorProperties cNvCxnSpPr) {
        this.generatedSetterHelperImpl(cNvCxnSpPr, PROPERTY_QNAME[1], 0, (short)1);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTNonVisualConnectorProperties addNewCNvCxnSpPr() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTNonVisualConnectorProperties target = null;
            target = (CTNonVisualConnectorProperties)((Object)this.get_store().add_element_user(PROPERTY_QNAME[1]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTApplicationNonVisualDrawingProps getNvPr() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTApplicationNonVisualDrawingProps target = null;
            target = (CTApplicationNonVisualDrawingProps)((Object)this.get_store().find_element_user(PROPERTY_QNAME[2], 0));
            return target == null ? null : target;
        }
    }

    @Override
    public void setNvPr(CTApplicationNonVisualDrawingProps nvPr) {
        this.generatedSetterHelperImpl(nvPr, PROPERTY_QNAME[2], 0, (short)1);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTApplicationNonVisualDrawingProps addNewNvPr() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTApplicationNonVisualDrawingProps target = null;
            target = (CTApplicationNonVisualDrawingProps)((Object)this.get_store().add_element_user(PROPERTY_QNAME[2]));
            return target;
        }
    }
}


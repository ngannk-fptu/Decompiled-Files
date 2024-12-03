/*
 * Decompiled with CFR 0.152.
 */
package org.openxmlformats.schemas.drawingml.x2006.main.impl;

import javax.xml.namespace.QName;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;
import org.openxmlformats.schemas.drawingml.x2006.main.CTAdjPoint2D;
import org.openxmlformats.schemas.drawingml.x2006.main.CTPath2DLineTo;

public class CTPath2DLineToImpl
extends XmlComplexContentImpl
implements CTPath2DLineTo {
    private static final long serialVersionUID = 1L;
    private static final QName[] PROPERTY_QNAME = new QName[]{new QName("http://schemas.openxmlformats.org/drawingml/2006/main", "pt")};

    public CTPath2DLineToImpl(SchemaType sType) {
        super(sType);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTAdjPoint2D getPt() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTAdjPoint2D target = null;
            target = (CTAdjPoint2D)((Object)this.get_store().find_element_user(PROPERTY_QNAME[0], 0));
            return target == null ? null : target;
        }
    }

    @Override
    public void setPt(CTAdjPoint2D pt) {
        this.generatedSetterHelperImpl(pt, PROPERTY_QNAME[0], 0, (short)1);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTAdjPoint2D addNewPt() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTAdjPoint2D target = null;
            target = (CTAdjPoint2D)((Object)this.get_store().add_element_user(PROPERTY_QNAME[0]));
            return target;
        }
    }
}


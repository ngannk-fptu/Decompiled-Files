/*
 * Decompiled with CFR 0.152.
 */
package org.openxmlformats.schemas.drawingml.x2006.chart.impl;

import javax.xml.namespace.QName;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTChartSpace;
import org.openxmlformats.schemas.drawingml.x2006.chart.ChartSpaceDocument;

public class ChartSpaceDocumentImpl
extends XmlComplexContentImpl
implements ChartSpaceDocument {
    private static final long serialVersionUID = 1L;
    private static final QName[] PROPERTY_QNAME = new QName[]{new QName("http://schemas.openxmlformats.org/drawingml/2006/chart", "chartSpace")};

    public ChartSpaceDocumentImpl(SchemaType sType) {
        super(sType);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTChartSpace getChartSpace() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTChartSpace target = null;
            target = (CTChartSpace)((Object)this.get_store().find_element_user(PROPERTY_QNAME[0], 0));
            return target == null ? null : target;
        }
    }

    @Override
    public void setChartSpace(CTChartSpace chartSpace) {
        this.generatedSetterHelperImpl(chartSpace, PROPERTY_QNAME[0], 0, (short)1);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTChartSpace addNewChartSpace() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTChartSpace target = null;
            target = (CTChartSpace)((Object)this.get_store().add_element_user(PROPERTY_QNAME[0]));
            return target;
        }
    }
}


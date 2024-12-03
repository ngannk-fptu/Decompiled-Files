/*
 * Decompiled with CFR 0.152.
 */
package org.openxmlformats.schemas.spreadsheetml.x2006.main.impl;

import javax.xml.namespace.QName;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTCalcChain;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CalcChainDocument;

public class CalcChainDocumentImpl
extends XmlComplexContentImpl
implements CalcChainDocument {
    private static final long serialVersionUID = 1L;
    private static final QName[] PROPERTY_QNAME = new QName[]{new QName("http://schemas.openxmlformats.org/spreadsheetml/2006/main", "calcChain")};

    public CalcChainDocumentImpl(SchemaType sType) {
        super(sType);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTCalcChain getCalcChain() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTCalcChain target = null;
            target = (CTCalcChain)((Object)this.get_store().find_element_user(PROPERTY_QNAME[0], 0));
            return target == null ? null : target;
        }
    }

    @Override
    public void setCalcChain(CTCalcChain calcChain) {
        this.generatedSetterHelperImpl(calcChain, PROPERTY_QNAME[0], 0, (short)1);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTCalcChain addNewCalcChain() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTCalcChain target = null;
            target = (CTCalcChain)((Object)this.get_store().add_element_user(PROPERTY_QNAME[0]));
            return target;
        }
    }
}


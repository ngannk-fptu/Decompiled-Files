/*
 * Decompiled with CFR 0.152.
 */
package org.openxmlformats.schemas.wordprocessingml.x2006.main.impl;

import javax.xml.namespace.QName;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTHdrFtr;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.FtrDocument;

public class FtrDocumentImpl
extends XmlComplexContentImpl
implements FtrDocument {
    private static final long serialVersionUID = 1L;
    private static final QName[] PROPERTY_QNAME = new QName[]{new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "ftr")};

    public FtrDocumentImpl(SchemaType sType) {
        super(sType);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTHdrFtr getFtr() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTHdrFtr target = null;
            target = (CTHdrFtr)((Object)this.get_store().find_element_user(PROPERTY_QNAME[0], 0));
            return target == null ? null : target;
        }
    }

    @Override
    public void setFtr(CTHdrFtr ftr) {
        this.generatedSetterHelperImpl(ftr, PROPERTY_QNAME[0], 0, (short)1);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTHdrFtr addNewFtr() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTHdrFtr target = null;
            target = (CTHdrFtr)((Object)this.get_store().add_element_user(PROPERTY_QNAME[0]));
            return target;
        }
    }
}


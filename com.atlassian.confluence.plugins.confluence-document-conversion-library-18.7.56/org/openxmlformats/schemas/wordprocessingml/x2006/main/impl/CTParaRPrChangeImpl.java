/*
 * Decompiled with CFR 0.152.
 */
package org.openxmlformats.schemas.wordprocessingml.x2006.main.impl;

import javax.xml.namespace.QName;
import org.apache.xmlbeans.SchemaType;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTParaRPrChange;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTParaRPrOriginal;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.impl.CTTrackChangeImpl;

public class CTParaRPrChangeImpl
extends CTTrackChangeImpl
implements CTParaRPrChange {
    private static final long serialVersionUID = 1L;
    private static final QName[] PROPERTY_QNAME = new QName[]{new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "rPr")};

    public CTParaRPrChangeImpl(SchemaType sType) {
        super(sType);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTParaRPrOriginal getRPr() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTParaRPrOriginal target = null;
            target = (CTParaRPrOriginal)((Object)this.get_store().find_element_user(PROPERTY_QNAME[0], 0));
            return target == null ? null : target;
        }
    }

    @Override
    public void setRPr(CTParaRPrOriginal rPr) {
        this.generatedSetterHelperImpl(rPr, PROPERTY_QNAME[0], 0, (short)1);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTParaRPrOriginal addNewRPr() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTParaRPrOriginal target = null;
            target = (CTParaRPrOriginal)((Object)this.get_store().add_element_user(PROPERTY_QNAME[0]));
            return target;
        }
    }
}


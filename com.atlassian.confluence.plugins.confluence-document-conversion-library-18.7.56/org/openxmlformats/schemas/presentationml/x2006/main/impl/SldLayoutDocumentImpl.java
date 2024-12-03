/*
 * Decompiled with CFR 0.152.
 */
package org.openxmlformats.schemas.presentationml.x2006.main.impl;

import javax.xml.namespace.QName;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;
import org.openxmlformats.schemas.presentationml.x2006.main.CTSlideLayout;
import org.openxmlformats.schemas.presentationml.x2006.main.SldLayoutDocument;

public class SldLayoutDocumentImpl
extends XmlComplexContentImpl
implements SldLayoutDocument {
    private static final long serialVersionUID = 1L;
    private static final QName[] PROPERTY_QNAME = new QName[]{new QName("http://schemas.openxmlformats.org/presentationml/2006/main", "sldLayout")};

    public SldLayoutDocumentImpl(SchemaType sType) {
        super(sType);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTSlideLayout getSldLayout() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTSlideLayout target = null;
            target = (CTSlideLayout)((Object)this.get_store().find_element_user(PROPERTY_QNAME[0], 0));
            return target == null ? null : target;
        }
    }

    @Override
    public void setSldLayout(CTSlideLayout sldLayout) {
        this.generatedSetterHelperImpl(sldLayout, PROPERTY_QNAME[0], 0, (short)1);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTSlideLayout addNewSldLayout() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTSlideLayout target = null;
            target = (CTSlideLayout)((Object)this.get_store().add_element_user(PROPERTY_QNAME[0]));
            return target;
        }
    }
}


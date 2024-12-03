/*
 * Decompiled with CFR 0.152.
 */
package org.openxmlformats.schemas.presentationml.x2006.main.impl;

import javax.xml.namespace.QName;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;
import org.openxmlformats.schemas.presentationml.x2006.main.CTPresentation;
import org.openxmlformats.schemas.presentationml.x2006.main.PresentationDocument;

public class PresentationDocumentImpl
extends XmlComplexContentImpl
implements PresentationDocument {
    private static final long serialVersionUID = 1L;
    private static final QName[] PROPERTY_QNAME = new QName[]{new QName("http://schemas.openxmlformats.org/presentationml/2006/main", "presentation")};

    public PresentationDocumentImpl(SchemaType sType) {
        super(sType);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTPresentation getPresentation() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTPresentation target = null;
            target = (CTPresentation)((Object)this.get_store().find_element_user(PROPERTY_QNAME[0], 0));
            return target == null ? null : target;
        }
    }

    @Override
    public void setPresentation(CTPresentation presentation) {
        this.generatedSetterHelperImpl(presentation, PROPERTY_QNAME[0], 0, (short)1);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTPresentation addNewPresentation() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTPresentation target = null;
            target = (CTPresentation)((Object)this.get_store().add_element_user(PROPERTY_QNAME[0]));
            return target;
        }
    }
}


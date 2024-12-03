/*
 * Decompiled with CFR 0.152.
 */
package org.openxmlformats.schemas.wordprocessingml.x2006.main.impl;

import javax.xml.namespace.QName;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTEndnotes;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.EndnotesDocument;

public class EndnotesDocumentImpl
extends XmlComplexContentImpl
implements EndnotesDocument {
    private static final long serialVersionUID = 1L;
    private static final QName[] PROPERTY_QNAME = new QName[]{new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "endnotes")};

    public EndnotesDocumentImpl(SchemaType sType) {
        super(sType);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTEndnotes getEndnotes() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTEndnotes target = null;
            target = (CTEndnotes)((Object)this.get_store().find_element_user(PROPERTY_QNAME[0], 0));
            return target == null ? null : target;
        }
    }

    @Override
    public void setEndnotes(CTEndnotes endnotes) {
        this.generatedSetterHelperImpl(endnotes, PROPERTY_QNAME[0], 0, (short)1);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTEndnotes addNewEndnotes() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTEndnotes target = null;
            target = (CTEndnotes)((Object)this.get_store().add_element_user(PROPERTY_QNAME[0]));
            return target;
        }
    }
}


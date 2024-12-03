/*
 * Decompiled with CFR 0.152.
 */
package org.openxmlformats.schemas.wordprocessingml.x2006.main.impl;

import javax.xml.namespace.QName;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTFootnotes;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.FootnotesDocument;

public class FootnotesDocumentImpl
extends XmlComplexContentImpl
implements FootnotesDocument {
    private static final long serialVersionUID = 1L;
    private static final QName[] PROPERTY_QNAME = new QName[]{new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "footnotes")};

    public FootnotesDocumentImpl(SchemaType sType) {
        super(sType);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTFootnotes getFootnotes() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTFootnotes target = null;
            target = (CTFootnotes)((Object)this.get_store().find_element_user(PROPERTY_QNAME[0], 0));
            return target == null ? null : target;
        }
    }

    @Override
    public void setFootnotes(CTFootnotes footnotes) {
        this.generatedSetterHelperImpl(footnotes, PROPERTY_QNAME[0], 0, (short)1);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTFootnotes addNewFootnotes() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTFootnotes target = null;
            target = (CTFootnotes)((Object)this.get_store().add_element_user(PROPERTY_QNAME[0]));
            return target;
        }
    }
}


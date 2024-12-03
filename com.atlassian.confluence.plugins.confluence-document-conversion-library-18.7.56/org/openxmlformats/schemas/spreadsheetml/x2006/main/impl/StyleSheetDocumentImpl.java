/*
 * Decompiled with CFR 0.152.
 */
package org.openxmlformats.schemas.spreadsheetml.x2006.main.impl;

import javax.xml.namespace.QName;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTStylesheet;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.StyleSheetDocument;

public class StyleSheetDocumentImpl
extends XmlComplexContentImpl
implements StyleSheetDocument {
    private static final long serialVersionUID = 1L;
    private static final QName[] PROPERTY_QNAME = new QName[]{new QName("http://schemas.openxmlformats.org/spreadsheetml/2006/main", "styleSheet")};

    public StyleSheetDocumentImpl(SchemaType sType) {
        super(sType);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTStylesheet getStyleSheet() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTStylesheet target = null;
            target = (CTStylesheet)((Object)this.get_store().find_element_user(PROPERTY_QNAME[0], 0));
            return target == null ? null : target;
        }
    }

    @Override
    public void setStyleSheet(CTStylesheet styleSheet) {
        this.generatedSetterHelperImpl(styleSheet, PROPERTY_QNAME[0], 0, (short)1);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTStylesheet addNewStyleSheet() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTStylesheet target = null;
            target = (CTStylesheet)((Object)this.get_store().add_element_user(PROPERTY_QNAME[0]));
            return target;
        }
    }
}


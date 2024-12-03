/*
 * Decompiled with CFR 0.152.
 */
package org.openxmlformats.schemas.spreadsheetml.x2006.main.impl;

import javax.xml.namespace.QName;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTWorkbook;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.WorkbookDocument;

public class WorkbookDocumentImpl
extends XmlComplexContentImpl
implements WorkbookDocument {
    private static final long serialVersionUID = 1L;
    private static final QName[] PROPERTY_QNAME = new QName[]{new QName("http://schemas.openxmlformats.org/spreadsheetml/2006/main", "workbook")};

    public WorkbookDocumentImpl(SchemaType sType) {
        super(sType);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTWorkbook getWorkbook() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTWorkbook target = null;
            target = (CTWorkbook)((Object)this.get_store().find_element_user(PROPERTY_QNAME[0], 0));
            return target == null ? null : target;
        }
    }

    @Override
    public void setWorkbook(CTWorkbook workbook) {
        this.generatedSetterHelperImpl(workbook, PROPERTY_QNAME[0], 0, (short)1);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTWorkbook addNewWorkbook() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTWorkbook target = null;
            target = (CTWorkbook)((Object)this.get_store().add_element_user(PROPERTY_QNAME[0]));
            return target;
        }
    }
}


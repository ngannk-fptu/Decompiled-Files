/*
 * Decompiled with CFR 0.152.
 */
package org.openxmlformats.schemas.drawingml.x2006.main.impl;

import javax.xml.namespace.QName;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;
import org.openxmlformats.schemas.drawingml.x2006.main.CTTableStyleList;
import org.openxmlformats.schemas.drawingml.x2006.main.TblStyleLstDocument;

public class TblStyleLstDocumentImpl
extends XmlComplexContentImpl
implements TblStyleLstDocument {
    private static final long serialVersionUID = 1L;
    private static final QName[] PROPERTY_QNAME = new QName[]{new QName("http://schemas.openxmlformats.org/drawingml/2006/main", "tblStyleLst")};

    public TblStyleLstDocumentImpl(SchemaType sType) {
        super(sType);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTTableStyleList getTblStyleLst() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTTableStyleList target = null;
            target = (CTTableStyleList)((Object)this.get_store().find_element_user(PROPERTY_QNAME[0], 0));
            return target == null ? null : target;
        }
    }

    @Override
    public void setTblStyleLst(CTTableStyleList tblStyleLst) {
        this.generatedSetterHelperImpl(tblStyleLst, PROPERTY_QNAME[0], 0, (short)1);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTTableStyleList addNewTblStyleLst() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTTableStyleList target = null;
            target = (CTTableStyleList)((Object)this.get_store().add_element_user(PROPERTY_QNAME[0]));
            return target;
        }
    }
}


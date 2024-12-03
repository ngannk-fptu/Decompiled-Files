/*
 * Decompiled with CFR 0.152.
 */
package com.microsoft.schemas.office.visio.x2012.main.impl;

import com.microsoft.schemas.office.visio.x2012.main.MasterContentsDocument;
import com.microsoft.schemas.office.visio.x2012.main.PageContentsType;
import javax.xml.namespace.QName;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;

public class MasterContentsDocumentImpl
extends XmlComplexContentImpl
implements MasterContentsDocument {
    private static final long serialVersionUID = 1L;
    private static final QName[] PROPERTY_QNAME = new QName[]{new QName("http://schemas.microsoft.com/office/visio/2012/main", "MasterContents")};

    public MasterContentsDocumentImpl(SchemaType sType) {
        super(sType);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public PageContentsType getMasterContents() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            PageContentsType target = null;
            target = (PageContentsType)((Object)this.get_store().find_element_user(PROPERTY_QNAME[0], 0));
            return target == null ? null : target;
        }
    }

    @Override
    public void setMasterContents(PageContentsType masterContents) {
        this.generatedSetterHelperImpl(masterContents, PROPERTY_QNAME[0], 0, (short)1);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public PageContentsType addNewMasterContents() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            PageContentsType target = null;
            target = (PageContentsType)((Object)this.get_store().add_element_user(PROPERTY_QNAME[0]));
            return target;
        }
    }
}


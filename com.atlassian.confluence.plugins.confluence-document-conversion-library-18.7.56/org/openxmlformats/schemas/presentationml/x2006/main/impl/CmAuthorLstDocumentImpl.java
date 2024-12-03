/*
 * Decompiled with CFR 0.152.
 */
package org.openxmlformats.schemas.presentationml.x2006.main.impl;

import javax.xml.namespace.QName;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;
import org.openxmlformats.schemas.presentationml.x2006.main.CTCommentAuthorList;
import org.openxmlformats.schemas.presentationml.x2006.main.CmAuthorLstDocument;

public class CmAuthorLstDocumentImpl
extends XmlComplexContentImpl
implements CmAuthorLstDocument {
    private static final long serialVersionUID = 1L;
    private static final QName[] PROPERTY_QNAME = new QName[]{new QName("http://schemas.openxmlformats.org/presentationml/2006/main", "cmAuthorLst")};

    public CmAuthorLstDocumentImpl(SchemaType sType) {
        super(sType);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTCommentAuthorList getCmAuthorLst() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTCommentAuthorList target = null;
            target = (CTCommentAuthorList)((Object)this.get_store().find_element_user(PROPERTY_QNAME[0], 0));
            return target == null ? null : target;
        }
    }

    @Override
    public void setCmAuthorLst(CTCommentAuthorList cmAuthorLst) {
        this.generatedSetterHelperImpl(cmAuthorLst, PROPERTY_QNAME[0], 0, (short)1);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTCommentAuthorList addNewCmAuthorLst() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTCommentAuthorList target = null;
            target = (CTCommentAuthorList)((Object)this.get_store().add_element_user(PROPERTY_QNAME[0]));
            return target;
        }
    }
}


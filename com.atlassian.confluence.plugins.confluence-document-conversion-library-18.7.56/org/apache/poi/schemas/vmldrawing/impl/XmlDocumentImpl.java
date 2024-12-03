/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.schemas.vmldrawing.impl;

import javax.xml.namespace.QName;
import org.apache.poi.schemas.vmldrawing.CTXML;
import org.apache.poi.schemas.vmldrawing.XmlDocument;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;

public class XmlDocumentImpl
extends XmlComplexContentImpl
implements XmlDocument {
    private static final long serialVersionUID = 1L;
    private static final QName[] PROPERTY_QNAME = new QName[]{new QName("urn:schemas-poi-apache-org:vmldrawing", "xml")};

    public XmlDocumentImpl(SchemaType sType) {
        super(sType);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTXML getXml() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTXML target = null;
            target = (CTXML)((Object)this.get_store().find_element_user(PROPERTY_QNAME[0], 0));
            return target == null ? null : target;
        }
    }

    @Override
    public void setXml(CTXML xml) {
        this.generatedSetterHelperImpl(xml, PROPERTY_QNAME[0], 0, (short)1);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTXML addNewXml() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTXML target = null;
            target = (CTXML)((Object)this.get_store().add_element_user(PROPERTY_QNAME[0]));
            return target;
        }
    }
}


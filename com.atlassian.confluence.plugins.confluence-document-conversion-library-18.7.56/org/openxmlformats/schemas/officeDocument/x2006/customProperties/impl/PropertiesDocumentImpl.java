/*
 * Decompiled with CFR 0.152.
 */
package org.openxmlformats.schemas.officeDocument.x2006.customProperties.impl;

import javax.xml.namespace.QName;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;
import org.openxmlformats.schemas.officeDocument.x2006.customProperties.CTProperties;
import org.openxmlformats.schemas.officeDocument.x2006.customProperties.PropertiesDocument;

public class PropertiesDocumentImpl
extends XmlComplexContentImpl
implements PropertiesDocument {
    private static final long serialVersionUID = 1L;
    private static final QName[] PROPERTY_QNAME = new QName[]{new QName("http://schemas.openxmlformats.org/officeDocument/2006/custom-properties", "Properties")};

    public PropertiesDocumentImpl(SchemaType sType) {
        super(sType);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTProperties getProperties() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTProperties target = null;
            target = (CTProperties)((Object)this.get_store().find_element_user(PROPERTY_QNAME[0], 0));
            return target == null ? null : target;
        }
    }

    @Override
    public void setProperties(CTProperties properties) {
        this.generatedSetterHelperImpl(properties, PROPERTY_QNAME[0], 0, (short)1);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTProperties addNewProperties() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTProperties target = null;
            target = (CTProperties)((Object)this.get_store().add_element_user(PROPERTY_QNAME[0]));
            return target;
        }
    }
}


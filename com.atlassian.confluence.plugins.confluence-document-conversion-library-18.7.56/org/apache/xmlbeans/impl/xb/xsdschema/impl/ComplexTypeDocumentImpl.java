/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xmlbeans.impl.xb.xsdschema.impl;

import javax.xml.namespace.QName;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;
import org.apache.xmlbeans.impl.xb.xsdschema.ComplexTypeDocument;
import org.apache.xmlbeans.impl.xb.xsdschema.TopLevelComplexType;

public class ComplexTypeDocumentImpl
extends XmlComplexContentImpl
implements ComplexTypeDocument {
    private static final long serialVersionUID = 1L;
    private static final QName[] PROPERTY_QNAME = new QName[]{new QName("http://www.w3.org/2001/XMLSchema", "complexType")};

    public ComplexTypeDocumentImpl(SchemaType sType) {
        super(sType);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public TopLevelComplexType getComplexType() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            TopLevelComplexType target = null;
            target = (TopLevelComplexType)((Object)this.get_store().find_element_user(PROPERTY_QNAME[0], 0));
            return target == null ? null : target;
        }
    }

    @Override
    public void setComplexType(TopLevelComplexType complexType) {
        this.generatedSetterHelperImpl(complexType, PROPERTY_QNAME[0], 0, (short)1);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public TopLevelComplexType addNewComplexType() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            TopLevelComplexType target = null;
            target = (TopLevelComplexType)((Object)this.get_store().add_element_user(PROPERTY_QNAME[0]));
            return target;
        }
    }
}


/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xmlbeans.impl.xb.xsdschema.impl;

import javax.xml.namespace.QName;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;
import org.apache.xmlbeans.impl.xb.xsdschema.AttributeGroupDocument;
import org.apache.xmlbeans.impl.xb.xsdschema.NamedAttributeGroup;

public class AttributeGroupDocumentImpl
extends XmlComplexContentImpl
implements AttributeGroupDocument {
    private static final long serialVersionUID = 1L;
    private static final QName[] PROPERTY_QNAME = new QName[]{new QName("http://www.w3.org/2001/XMLSchema", "attributeGroup")};

    public AttributeGroupDocumentImpl(SchemaType sType) {
        super(sType);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public NamedAttributeGroup getAttributeGroup() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            NamedAttributeGroup target = null;
            target = (NamedAttributeGroup)((Object)this.get_store().find_element_user(PROPERTY_QNAME[0], 0));
            return target == null ? null : target;
        }
    }

    @Override
    public void setAttributeGroup(NamedAttributeGroup attributeGroup) {
        this.generatedSetterHelperImpl(attributeGroup, PROPERTY_QNAME[0], 0, (short)1);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public NamedAttributeGroup addNewAttributeGroup() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            NamedAttributeGroup target = null;
            target = (NamedAttributeGroup)((Object)this.get_store().add_element_user(PROPERTY_QNAME[0]));
            return target;
        }
    }
}


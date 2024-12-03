/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xmlbeans.impl.xb.xsdschema.impl;

import javax.xml.namespace.QName;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;
import org.apache.xmlbeans.impl.xb.xsdschema.EnumerationDocument;
import org.apache.xmlbeans.impl.xb.xsdschema.NoFixedFacet;

public class EnumerationDocumentImpl
extends XmlComplexContentImpl
implements EnumerationDocument {
    private static final long serialVersionUID = 1L;
    private static final QName[] PROPERTY_QNAME = new QName[]{new QName("http://www.w3.org/2001/XMLSchema", "enumeration")};

    public EnumerationDocumentImpl(SchemaType sType) {
        super(sType);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public NoFixedFacet getEnumeration() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            NoFixedFacet target = null;
            target = (NoFixedFacet)((Object)this.get_store().find_element_user(PROPERTY_QNAME[0], 0));
            return target == null ? null : target;
        }
    }

    @Override
    public void setEnumeration(NoFixedFacet enumeration) {
        this.generatedSetterHelperImpl(enumeration, PROPERTY_QNAME[0], 0, (short)1);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public NoFixedFacet addNewEnumeration() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            NoFixedFacet target = null;
            target = (NoFixedFacet)((Object)this.get_store().add_element_user(PROPERTY_QNAME[0]));
            return target;
        }
    }
}


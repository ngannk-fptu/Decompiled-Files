/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xmlbeans.impl.xb.xsdschema.impl;

import javax.xml.namespace.QName;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;
import org.apache.xmlbeans.impl.xb.xsdschema.LengthDocument;
import org.apache.xmlbeans.impl.xb.xsdschema.NumFacet;

public class LengthDocumentImpl
extends XmlComplexContentImpl
implements LengthDocument {
    private static final long serialVersionUID = 1L;
    private static final QName[] PROPERTY_QNAME = new QName[]{new QName("http://www.w3.org/2001/XMLSchema", "length")};

    public LengthDocumentImpl(SchemaType sType) {
        super(sType);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public NumFacet getLength() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            NumFacet target = null;
            target = (NumFacet)((Object)this.get_store().find_element_user(PROPERTY_QNAME[0], 0));
            return target == null ? null : target;
        }
    }

    @Override
    public void setLength(NumFacet length) {
        this.generatedSetterHelperImpl(length, PROPERTY_QNAME[0], 0, (short)1);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public NumFacet addNewLength() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            NumFacet target = null;
            target = (NumFacet)((Object)this.get_store().add_element_user(PROPERTY_QNAME[0]));
            return target;
        }
    }
}


/*
 * Decompiled with CFR 0.152.
 */
package org.w3.x2000.x09.xmldsig.impl;

import javax.xml.namespace.QName;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;
import org.w3.x2000.x09.xmldsig.TransformDocument;
import org.w3.x2000.x09.xmldsig.TransformType;

public class TransformDocumentImpl
extends XmlComplexContentImpl
implements TransformDocument {
    private static final long serialVersionUID = 1L;
    private static final QName[] PROPERTY_QNAME = new QName[]{new QName("http://www.w3.org/2000/09/xmldsig#", "Transform")};

    public TransformDocumentImpl(SchemaType sType) {
        super(sType);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public TransformType getTransform() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            TransformType target = null;
            target = (TransformType)((Object)this.get_store().find_element_user(PROPERTY_QNAME[0], 0));
            return target == null ? null : target;
        }
    }

    @Override
    public void setTransform(TransformType transform) {
        this.generatedSetterHelperImpl(transform, PROPERTY_QNAME[0], 0, (short)1);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public TransformType addNewTransform() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            TransformType target = null;
            target = (TransformType)((Object)this.get_store().add_element_user(PROPERTY_QNAME[0]));
            return target;
        }
    }
}


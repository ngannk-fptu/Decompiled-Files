/*
 * Decompiled with CFR 0.152.
 */
package org.openxmlformats.schemas.officeDocument.x2006.extendedProperties.impl;

import javax.xml.namespace.QName;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;
import org.openxmlformats.schemas.officeDocument.x2006.docPropsVTypes.CTVector;
import org.openxmlformats.schemas.officeDocument.x2006.extendedProperties.CTVectorVariant;

public class CTVectorVariantImpl
extends XmlComplexContentImpl
implements CTVectorVariant {
    private static final long serialVersionUID = 1L;
    private static final QName[] PROPERTY_QNAME = new QName[]{new QName("http://schemas.openxmlformats.org/officeDocument/2006/docPropsVTypes", "vector")};

    public CTVectorVariantImpl(SchemaType sType) {
        super(sType);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTVector getVector() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTVector target = null;
            target = (CTVector)((Object)this.get_store().find_element_user(PROPERTY_QNAME[0], 0));
            return target == null ? null : target;
        }
    }

    @Override
    public void setVector(CTVector vector) {
        this.generatedSetterHelperImpl(vector, PROPERTY_QNAME[0], 0, (short)1);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTVector addNewVector() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTVector target = null;
            target = (CTVector)((Object)this.get_store().add_element_user(PROPERTY_QNAME[0]));
            return target;
        }
    }
}


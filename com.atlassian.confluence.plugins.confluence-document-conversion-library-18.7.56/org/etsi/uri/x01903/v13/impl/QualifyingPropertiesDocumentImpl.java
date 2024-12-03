/*
 * Decompiled with CFR 0.152.
 */
package org.etsi.uri.x01903.v13.impl;

import javax.xml.namespace.QName;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;
import org.etsi.uri.x01903.v13.QualifyingPropertiesDocument;
import org.etsi.uri.x01903.v13.QualifyingPropertiesType;

public class QualifyingPropertiesDocumentImpl
extends XmlComplexContentImpl
implements QualifyingPropertiesDocument {
    private static final long serialVersionUID = 1L;
    private static final QName[] PROPERTY_QNAME = new QName[]{new QName("http://uri.etsi.org/01903/v1.3.2#", "QualifyingProperties")};

    public QualifyingPropertiesDocumentImpl(SchemaType sType) {
        super(sType);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public QualifyingPropertiesType getQualifyingProperties() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            QualifyingPropertiesType target = null;
            target = (QualifyingPropertiesType)((Object)this.get_store().find_element_user(PROPERTY_QNAME[0], 0));
            return target == null ? null : target;
        }
    }

    @Override
    public void setQualifyingProperties(QualifyingPropertiesType qualifyingProperties) {
        this.generatedSetterHelperImpl(qualifyingProperties, PROPERTY_QNAME[0], 0, (short)1);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public QualifyingPropertiesType addNewQualifyingProperties() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            QualifyingPropertiesType target = null;
            target = (QualifyingPropertiesType)((Object)this.get_store().add_element_user(PROPERTY_QNAME[0]));
            return target;
        }
    }
}


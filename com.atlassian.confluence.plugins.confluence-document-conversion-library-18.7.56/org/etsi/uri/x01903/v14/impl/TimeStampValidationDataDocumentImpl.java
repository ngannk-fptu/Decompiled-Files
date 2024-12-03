/*
 * Decompiled with CFR 0.152.
 */
package org.etsi.uri.x01903.v14.impl;

import javax.xml.namespace.QName;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;
import org.etsi.uri.x01903.v14.TimeStampValidationDataDocument;
import org.etsi.uri.x01903.v14.ValidationDataType;

public class TimeStampValidationDataDocumentImpl
extends XmlComplexContentImpl
implements TimeStampValidationDataDocument {
    private static final long serialVersionUID = 1L;
    private static final QName[] PROPERTY_QNAME = new QName[]{new QName("http://uri.etsi.org/01903/v1.4.1#", "TimeStampValidationData")};

    public TimeStampValidationDataDocumentImpl(SchemaType sType) {
        super(sType);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public ValidationDataType getTimeStampValidationData() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            ValidationDataType target = null;
            target = (ValidationDataType)((Object)this.get_store().find_element_user(PROPERTY_QNAME[0], 0));
            return target == null ? null : target;
        }
    }

    @Override
    public void setTimeStampValidationData(ValidationDataType timeStampValidationData) {
        this.generatedSetterHelperImpl(timeStampValidationData, PROPERTY_QNAME[0], 0, (short)1);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public ValidationDataType addNewTimeStampValidationData() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            ValidationDataType target = null;
            target = (ValidationDataType)((Object)this.get_store().add_element_user(PROPERTY_QNAME[0]));
            return target;
        }
    }
}


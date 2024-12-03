/*
 * Decompiled with CFR 0.152.
 */
package org.etsi.uri.x01903.v13.impl;

import javax.xml.namespace.QName;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.SimpleValue;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;
import org.etsi.uri.x01903.v13.DigestAlgAndValueType;
import org.w3.x2000.x09.xmldsig.DigestMethodType;
import org.w3.x2000.x09.xmldsig.DigestValueType;

public class DigestAlgAndValueTypeImpl
extends XmlComplexContentImpl
implements DigestAlgAndValueType {
    private static final long serialVersionUID = 1L;
    private static final QName[] PROPERTY_QNAME = new QName[]{new QName("http://www.w3.org/2000/09/xmldsig#", "DigestMethod"), new QName("http://www.w3.org/2000/09/xmldsig#", "DigestValue")};

    public DigestAlgAndValueTypeImpl(SchemaType sType) {
        super(sType);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public DigestMethodType getDigestMethod() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            DigestMethodType target = null;
            target = (DigestMethodType)((Object)this.get_store().find_element_user(PROPERTY_QNAME[0], 0));
            return target == null ? null : target;
        }
    }

    @Override
    public void setDigestMethod(DigestMethodType digestMethod) {
        this.generatedSetterHelperImpl(digestMethod, PROPERTY_QNAME[0], 0, (short)1);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public DigestMethodType addNewDigestMethod() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            DigestMethodType target = null;
            target = (DigestMethodType)((Object)this.get_store().add_element_user(PROPERTY_QNAME[0]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public byte[] getDigestValue() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)((Object)this.get_store().find_element_user(PROPERTY_QNAME[1], 0));
            return target == null ? null : target.getByteArrayValue();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public DigestValueType xgetDigestValue() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            DigestValueType target = null;
            target = (DigestValueType)((Object)this.get_store().find_element_user(PROPERTY_QNAME[1], 0));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void setDigestValue(byte[] digestValue) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)((Object)this.get_store().find_element_user(PROPERTY_QNAME[1], 0));
            if (target == null) {
                target = (SimpleValue)((Object)this.get_store().add_element_user(PROPERTY_QNAME[1]));
            }
            target.setByteArrayValue(digestValue);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void xsetDigestValue(DigestValueType digestValue) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            DigestValueType target = null;
            target = (DigestValueType)((Object)this.get_store().find_element_user(PROPERTY_QNAME[1], 0));
            if (target == null) {
                target = (DigestValueType)((Object)this.get_store().add_element_user(PROPERTY_QNAME[1]));
            }
            target.set(digestValue);
        }
    }
}


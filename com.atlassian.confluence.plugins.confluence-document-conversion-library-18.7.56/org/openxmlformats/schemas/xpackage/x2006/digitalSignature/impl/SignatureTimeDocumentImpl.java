/*
 * Decompiled with CFR 0.152.
 */
package org.openxmlformats.schemas.xpackage.x2006.digitalSignature.impl;

import javax.xml.namespace.QName;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;
import org.openxmlformats.schemas.xpackage.x2006.digitalSignature.CTSignatureTime;
import org.openxmlformats.schemas.xpackage.x2006.digitalSignature.SignatureTimeDocument;

public class SignatureTimeDocumentImpl
extends XmlComplexContentImpl
implements SignatureTimeDocument {
    private static final long serialVersionUID = 1L;
    private static final QName[] PROPERTY_QNAME = new QName[]{new QName("http://schemas.openxmlformats.org/package/2006/digital-signature", "SignatureTime")};

    public SignatureTimeDocumentImpl(SchemaType sType) {
        super(sType);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTSignatureTime getSignatureTime() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTSignatureTime target = null;
            target = (CTSignatureTime)((Object)this.get_store().find_element_user(PROPERTY_QNAME[0], 0));
            return target == null ? null : target;
        }
    }

    @Override
    public void setSignatureTime(CTSignatureTime signatureTime) {
        this.generatedSetterHelperImpl(signatureTime, PROPERTY_QNAME[0], 0, (short)1);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTSignatureTime addNewSignatureTime() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTSignatureTime target = null;
            target = (CTSignatureTime)((Object)this.get_store().add_element_user(PROPERTY_QNAME[0]));
            return target;
        }
    }
}


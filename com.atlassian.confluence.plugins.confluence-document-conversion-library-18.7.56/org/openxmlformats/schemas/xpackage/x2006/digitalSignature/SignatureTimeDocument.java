/*
 * Decompiled with CFR 0.152.
 */
package org.openxmlformats.schemas.xpackage.x2006.digitalSignature;

import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.openxmlformats.schemas.xpackage.x2006.digitalSignature.CTSignatureTime;

public interface SignatureTimeDocument
extends XmlObject {
    public static final DocumentFactory<SignatureTimeDocument> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "signaturetime9c91doctype");
    public static final SchemaType type = Factory.getType();

    public CTSignatureTime getSignatureTime();

    public void setSignatureTime(CTSignatureTime var1);

    public CTSignatureTime addNewSignatureTime();
}


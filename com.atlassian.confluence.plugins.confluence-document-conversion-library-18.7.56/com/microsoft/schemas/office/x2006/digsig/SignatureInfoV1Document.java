/*
 * Decompiled with CFR 0.152.
 */
package com.microsoft.schemas.office.x2006.digsig;

import com.microsoft.schemas.office.x2006.digsig.CTSignatureInfoV1;
import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.impl.schema.DocumentFactory;

public interface SignatureInfoV1Document
extends XmlObject {
    public static final DocumentFactory<SignatureInfoV1Document> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "signatureinfov14a6bdoctype");
    public static final SchemaType type = Factory.getType();

    public CTSignatureInfoV1 getSignatureInfoV1();

    public void setSignatureInfoV1(CTSignatureInfoV1 var1);

    public CTSignatureInfoV1 addNewSignatureInfoV1();
}


/*
 * Decompiled with CFR 0.152.
 */
package org.etsi.uri.x01903.v13;

import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.etsi.uri.x01903.v13.CRLIdentifierType;
import org.etsi.uri.x01903.v13.DigestAlgAndValueType;

public interface CRLRefType
extends XmlObject {
    public static final DocumentFactory<CRLRefType> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "crlreftype4444type");
    public static final SchemaType type = Factory.getType();

    public DigestAlgAndValueType getDigestAlgAndValue();

    public void setDigestAlgAndValue(DigestAlgAndValueType var1);

    public DigestAlgAndValueType addNewDigestAlgAndValue();

    public CRLIdentifierType getCRLIdentifier();

    public boolean isSetCRLIdentifier();

    public void setCRLIdentifier(CRLIdentifierType var1);

    public CRLIdentifierType addNewCRLIdentifier();

    public void unsetCRLIdentifier();
}


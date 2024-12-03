/*
 * Decompiled with CFR 0.152.
 */
package org.etsi.uri.x01903.v13;

import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.etsi.uri.x01903.v13.DigestAlgAndValueType;
import org.etsi.uri.x01903.v13.OCSPIdentifierType;

public interface OCSPRefType
extends XmlObject {
    public static final DocumentFactory<OCSPRefType> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "ocspreftype089etype");
    public static final SchemaType type = Factory.getType();

    public OCSPIdentifierType getOCSPIdentifier();

    public void setOCSPIdentifier(OCSPIdentifierType var1);

    public OCSPIdentifierType addNewOCSPIdentifier();

    public DigestAlgAndValueType getDigestAlgAndValue();

    public boolean isSetDigestAlgAndValue();

    public void setDigestAlgAndValue(DigestAlgAndValueType var1);

    public DigestAlgAndValueType addNewDigestAlgAndValue();

    public void unsetDigestAlgAndValue();
}


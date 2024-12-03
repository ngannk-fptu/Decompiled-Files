/*
 * Decompiled with CFR 0.152.
 */
package org.etsi.uri.x01903.v13;

import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.etsi.uri.x01903.v13.SignaturePolicyIdType;

public interface SignaturePolicyIdentifierType
extends XmlObject {
    public static final DocumentFactory<SignaturePolicyIdentifierType> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "signaturepolicyidentifiertype80aftype");
    public static final SchemaType type = Factory.getType();

    public SignaturePolicyIdType getSignaturePolicyId();

    public boolean isSetSignaturePolicyId();

    public void setSignaturePolicyId(SignaturePolicyIdType var1);

    public SignaturePolicyIdType addNewSignaturePolicyId();

    public void unsetSignaturePolicyId();

    public XmlObject getSignaturePolicyImplied();

    public boolean isSetSignaturePolicyImplied();

    public void setSignaturePolicyImplied(XmlObject var1);

    public XmlObject addNewSignaturePolicyImplied();

    public void unsetSignaturePolicyImplied();
}


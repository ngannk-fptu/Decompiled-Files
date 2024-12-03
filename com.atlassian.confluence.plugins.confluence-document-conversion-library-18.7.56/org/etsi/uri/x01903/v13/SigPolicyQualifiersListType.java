/*
 * Decompiled with CFR 0.152.
 */
package org.etsi.uri.x01903.v13;

import java.util.List;
import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.etsi.uri.x01903.v13.AnyType;

public interface SigPolicyQualifiersListType
extends XmlObject {
    public static final DocumentFactory<SigPolicyQualifiersListType> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "sigpolicyqualifierslisttype3266type");
    public static final SchemaType type = Factory.getType();

    public List<AnyType> getSigPolicyQualifierList();

    public AnyType[] getSigPolicyQualifierArray();

    public AnyType getSigPolicyQualifierArray(int var1);

    public int sizeOfSigPolicyQualifierArray();

    public void setSigPolicyQualifierArray(AnyType[] var1);

    public void setSigPolicyQualifierArray(int var1, AnyType var2);

    public AnyType insertNewSigPolicyQualifier(int var1);

    public AnyType addNewSigPolicyQualifier();

    public void removeSigPolicyQualifier(int var1);
}


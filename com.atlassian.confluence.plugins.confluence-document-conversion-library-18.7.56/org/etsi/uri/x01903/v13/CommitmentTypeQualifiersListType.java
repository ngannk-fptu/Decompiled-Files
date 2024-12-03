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

public interface CommitmentTypeQualifiersListType
extends XmlObject {
    public static final DocumentFactory<CommitmentTypeQualifiersListType> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "commitmenttypequalifierslisttype2d24type");
    public static final SchemaType type = Factory.getType();

    public List<AnyType> getCommitmentTypeQualifierList();

    public AnyType[] getCommitmentTypeQualifierArray();

    public AnyType getCommitmentTypeQualifierArray(int var1);

    public int sizeOfCommitmentTypeQualifierArray();

    public void setCommitmentTypeQualifierArray(AnyType[] var1);

    public void setCommitmentTypeQualifierArray(int var1, AnyType var2);

    public AnyType insertNewCommitmentTypeQualifier(int var1);

    public AnyType addNewCommitmentTypeQualifier();

    public void removeCommitmentTypeQualifier(int var1);
}


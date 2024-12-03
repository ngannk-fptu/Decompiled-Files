/*
 * Decompiled with CFR 0.152.
 */
package org.etsi.uri.x01903.v13;

import java.util.List;
import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlAnyURI;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.etsi.uri.x01903.v13.CommitmentTypeQualifiersListType;
import org.etsi.uri.x01903.v13.ObjectIdentifierType;

public interface CommitmentTypeIndicationType
extends XmlObject {
    public static final DocumentFactory<CommitmentTypeIndicationType> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "commitmenttypeindicationtypef179type");
    public static final SchemaType type = Factory.getType();

    public ObjectIdentifierType getCommitmentTypeId();

    public void setCommitmentTypeId(ObjectIdentifierType var1);

    public ObjectIdentifierType addNewCommitmentTypeId();

    public List<String> getObjectReferenceList();

    public String[] getObjectReferenceArray();

    public String getObjectReferenceArray(int var1);

    public List<XmlAnyURI> xgetObjectReferenceList();

    public XmlAnyURI[] xgetObjectReferenceArray();

    public XmlAnyURI xgetObjectReferenceArray(int var1);

    public int sizeOfObjectReferenceArray();

    public void setObjectReferenceArray(String[] var1);

    public void setObjectReferenceArray(int var1, String var2);

    public void xsetObjectReferenceArray(XmlAnyURI[] var1);

    public void xsetObjectReferenceArray(int var1, XmlAnyURI var2);

    public void insertObjectReference(int var1, String var2);

    public void addObjectReference(String var1);

    public XmlAnyURI insertNewObjectReference(int var1);

    public XmlAnyURI addNewObjectReference();

    public void removeObjectReference(int var1);

    public XmlObject getAllSignedDataObjects();

    public boolean isSetAllSignedDataObjects();

    public void setAllSignedDataObjects(XmlObject var1);

    public XmlObject addNewAllSignedDataObjects();

    public void unsetAllSignedDataObjects();

    public CommitmentTypeQualifiersListType getCommitmentTypeQualifiers();

    public boolean isSetCommitmentTypeQualifiers();

    public void setCommitmentTypeQualifiers(CommitmentTypeQualifiersListType var1);

    public CommitmentTypeQualifiersListType addNewCommitmentTypeQualifiers();

    public void unsetCommitmentTypeQualifiers();
}


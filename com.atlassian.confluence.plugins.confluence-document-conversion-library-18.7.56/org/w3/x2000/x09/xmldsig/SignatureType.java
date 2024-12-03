/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.w3.x2000.x09.xmldsig.KeyInfoType
 */
package org.w3.x2000.x09.xmldsig;

import java.util.List;
import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlID;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.w3.x2000.x09.xmldsig.KeyInfoType;
import org.w3.x2000.x09.xmldsig.ObjectType;
import org.w3.x2000.x09.xmldsig.SignatureValueType;
import org.w3.x2000.x09.xmldsig.SignedInfoType;

public interface SignatureType
extends XmlObject {
    public static final DocumentFactory<SignatureType> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "signaturetype0a3ftype");
    public static final SchemaType type = Factory.getType();

    public SignedInfoType getSignedInfo();

    public void setSignedInfo(SignedInfoType var1);

    public SignedInfoType addNewSignedInfo();

    public SignatureValueType getSignatureValue();

    public void setSignatureValue(SignatureValueType var1);

    public SignatureValueType addNewSignatureValue();

    public KeyInfoType getKeyInfo();

    public boolean isSetKeyInfo();

    public void setKeyInfo(KeyInfoType var1);

    public KeyInfoType addNewKeyInfo();

    public void unsetKeyInfo();

    public List<ObjectType> getObjectList();

    public ObjectType[] getObjectArray();

    public ObjectType getObjectArray(int var1);

    public int sizeOfObjectArray();

    public void setObjectArray(ObjectType[] var1);

    public void setObjectArray(int var1, ObjectType var2);

    public ObjectType insertNewObject(int var1);

    public ObjectType addNewObject();

    public void removeObject(int var1);

    public String getId();

    public XmlID xgetId();

    public boolean isSetId();

    public void setId(String var1);

    public void xsetId(XmlID var1);

    public void unsetId();
}


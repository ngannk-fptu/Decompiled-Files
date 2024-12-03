/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.w3.x2000.x09.xmldsig.SignatureMethodType
 */
package org.w3.x2000.x09.xmldsig;

import java.util.List;
import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlID;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.w3.x2000.x09.xmldsig.CanonicalizationMethodType;
import org.w3.x2000.x09.xmldsig.ReferenceType;
import org.w3.x2000.x09.xmldsig.SignatureMethodType;

public interface SignedInfoType
extends XmlObject {
    public static final DocumentFactory<SignedInfoType> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "signedinfotype54dbtype");
    public static final SchemaType type = Factory.getType();

    public CanonicalizationMethodType getCanonicalizationMethod();

    public void setCanonicalizationMethod(CanonicalizationMethodType var1);

    public CanonicalizationMethodType addNewCanonicalizationMethod();

    public SignatureMethodType getSignatureMethod();

    public void setSignatureMethod(SignatureMethodType var1);

    public SignatureMethodType addNewSignatureMethod();

    public List<ReferenceType> getReferenceList();

    public ReferenceType[] getReferenceArray();

    public ReferenceType getReferenceArray(int var1);

    public int sizeOfReferenceArray();

    public void setReferenceArray(ReferenceType[] var1);

    public void setReferenceArray(int var1, ReferenceType var2);

    public ReferenceType insertNewReference(int var1);

    public ReferenceType addNewReference();

    public void removeReference(int var1);

    public String getId();

    public XmlID xgetId();

    public boolean isSetId();

    public void setId(String var1);

    public void xsetId(XmlID var1);

    public void unsetId();
}


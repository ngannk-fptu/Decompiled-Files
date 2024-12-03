/*
 * Decompiled with CFR 0.152.
 */
package org.etsi.uri.x01903.v13;

import java.util.List;
import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlID;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.etsi.uri.x01903.v13.AnyType;
import org.etsi.uri.x01903.v13.EncapsulatedPKIDataType;

public interface CertificateValuesType
extends XmlObject {
    public static final DocumentFactory<CertificateValuesType> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "certificatevaluestype5c75type");
    public static final SchemaType type = Factory.getType();

    public List<EncapsulatedPKIDataType> getEncapsulatedX509CertificateList();

    public EncapsulatedPKIDataType[] getEncapsulatedX509CertificateArray();

    public EncapsulatedPKIDataType getEncapsulatedX509CertificateArray(int var1);

    public int sizeOfEncapsulatedX509CertificateArray();

    public void setEncapsulatedX509CertificateArray(EncapsulatedPKIDataType[] var1);

    public void setEncapsulatedX509CertificateArray(int var1, EncapsulatedPKIDataType var2);

    public EncapsulatedPKIDataType insertNewEncapsulatedX509Certificate(int var1);

    public EncapsulatedPKIDataType addNewEncapsulatedX509Certificate();

    public void removeEncapsulatedX509Certificate(int var1);

    public List<AnyType> getOtherCertificateList();

    public AnyType[] getOtherCertificateArray();

    public AnyType getOtherCertificateArray(int var1);

    public int sizeOfOtherCertificateArray();

    public void setOtherCertificateArray(AnyType[] var1);

    public void setOtherCertificateArray(int var1, AnyType var2);

    public AnyType insertNewOtherCertificate(int var1);

    public AnyType addNewOtherCertificate();

    public void removeOtherCertificate(int var1);

    public String getId();

    public XmlID xgetId();

    public boolean isSetId();

    public void setId(String var1);

    public void xsetId(XmlID var1);

    public void unsetId();
}


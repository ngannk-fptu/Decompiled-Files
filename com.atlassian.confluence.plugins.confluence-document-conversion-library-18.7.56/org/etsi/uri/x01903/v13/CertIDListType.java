/*
 * Decompiled with CFR 0.152.
 */
package org.etsi.uri.x01903.v13;

import java.util.List;
import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.etsi.uri.x01903.v13.CertIDType;

public interface CertIDListType
extends XmlObject {
    public static final DocumentFactory<CertIDListType> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "certidlisttype488btype");
    public static final SchemaType type = Factory.getType();

    public List<CertIDType> getCertList();

    public CertIDType[] getCertArray();

    public CertIDType getCertArray(int var1);

    public int sizeOfCertArray();

    public void setCertArray(CertIDType[] var1);

    public void setCertArray(int var1, CertIDType var2);

    public CertIDType insertNewCert(int var1);

    public CertIDType addNewCert();

    public void removeCert(int var1);
}


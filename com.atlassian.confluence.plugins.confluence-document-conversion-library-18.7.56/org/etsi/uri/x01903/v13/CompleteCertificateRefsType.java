/*
 * Decompiled with CFR 0.152.
 */
package org.etsi.uri.x01903.v13;

import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlID;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.etsi.uri.x01903.v13.CertIDListType;

public interface CompleteCertificateRefsType
extends XmlObject {
    public static final DocumentFactory<CompleteCertificateRefsType> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "completecertificaterefstype07datype");
    public static final SchemaType type = Factory.getType();

    public CertIDListType getCertRefs();

    public void setCertRefs(CertIDListType var1);

    public CertIDListType addNewCertRefs();

    public String getId();

    public XmlID xgetId();

    public boolean isSetId();

    public void setId(String var1);

    public void xsetId(XmlID var1);

    public void unsetId();
}


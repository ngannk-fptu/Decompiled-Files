/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.etsi.uri.x01903.v13.OtherCertStatusRefsType
 */
package org.etsi.uri.x01903.v13;

import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlID;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.etsi.uri.x01903.v13.CRLRefsType;
import org.etsi.uri.x01903.v13.OCSPRefsType;
import org.etsi.uri.x01903.v13.OtherCertStatusRefsType;

public interface CompleteRevocationRefsType
extends XmlObject {
    public static final DocumentFactory<CompleteRevocationRefsType> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "completerevocationrefstyped8a5type");
    public static final SchemaType type = Factory.getType();

    public CRLRefsType getCRLRefs();

    public boolean isSetCRLRefs();

    public void setCRLRefs(CRLRefsType var1);

    public CRLRefsType addNewCRLRefs();

    public void unsetCRLRefs();

    public OCSPRefsType getOCSPRefs();

    public boolean isSetOCSPRefs();

    public void setOCSPRefs(OCSPRefsType var1);

    public OCSPRefsType addNewOCSPRefs();

    public void unsetOCSPRefs();

    public OtherCertStatusRefsType getOtherRefs();

    public boolean isSetOtherRefs();

    public void setOtherRefs(OtherCertStatusRefsType var1);

    public OtherCertStatusRefsType addNewOtherRefs();

    public void unsetOtherRefs();

    public String getId();

    public XmlID xgetId();

    public boolean isSetId();

    public void setId(String var1);

    public void xsetId(XmlID var1);

    public void unsetId();
}


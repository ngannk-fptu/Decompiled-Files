/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.etsi.uri.x01903.v13.CertifiedRolesListType
 */
package org.etsi.uri.x01903.v13;

import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.etsi.uri.x01903.v13.CertifiedRolesListType;
import org.etsi.uri.x01903.v13.ClaimedRolesListType;

public interface SignerRoleType
extends XmlObject {
    public static final DocumentFactory<SignerRoleType> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "signerroletypef58etype");
    public static final SchemaType type = Factory.getType();

    public ClaimedRolesListType getClaimedRoles();

    public boolean isSetClaimedRoles();

    public void setClaimedRoles(ClaimedRolesListType var1);

    public ClaimedRolesListType addNewClaimedRoles();

    public void unsetClaimedRoles();

    public CertifiedRolesListType getCertifiedRoles();

    public boolean isSetCertifiedRoles();

    public void setCertifiedRoles(CertifiedRolesListType var1);

    public CertifiedRolesListType addNewCertifiedRoles();

    public void unsetCertifiedRoles();
}


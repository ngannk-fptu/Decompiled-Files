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

public interface ClaimedRolesListType
extends XmlObject {
    public static final DocumentFactory<ClaimedRolesListType> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "claimedroleslisttypef16etype");
    public static final SchemaType type = Factory.getType();

    public List<AnyType> getClaimedRoleList();

    public AnyType[] getClaimedRoleArray();

    public AnyType getClaimedRoleArray(int var1);

    public int sizeOfClaimedRoleArray();

    public void setClaimedRoleArray(AnyType[] var1);

    public void setClaimedRoleArray(int var1, AnyType var2);

    public AnyType insertNewClaimedRole(int var1);

    public AnyType addNewClaimedRole();

    public void removeClaimedRole(int var1);
}


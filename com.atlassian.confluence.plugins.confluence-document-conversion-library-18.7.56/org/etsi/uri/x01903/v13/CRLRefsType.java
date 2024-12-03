/*
 * Decompiled with CFR 0.152.
 */
package org.etsi.uri.x01903.v13;

import java.util.List;
import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.etsi.uri.x01903.v13.CRLRefType;

public interface CRLRefsType
extends XmlObject {
    public static final DocumentFactory<CRLRefsType> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "crlrefstype2a59type");
    public static final SchemaType type = Factory.getType();

    public List<CRLRefType> getCRLRefList();

    public CRLRefType[] getCRLRefArray();

    public CRLRefType getCRLRefArray(int var1);

    public int sizeOfCRLRefArray();

    public void setCRLRefArray(CRLRefType[] var1);

    public void setCRLRefArray(int var1, CRLRefType var2);

    public CRLRefType insertNewCRLRef(int var1);

    public CRLRefType addNewCRLRef();

    public void removeCRLRef(int var1);
}


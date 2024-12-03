/*
 * Decompiled with CFR 0.152.
 */
package org.etsi.uri.x01903.v13;

import java.util.List;
import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.etsi.uri.x01903.v13.OCSPRefType;

public interface OCSPRefsType
extends XmlObject {
    public static final DocumentFactory<OCSPRefsType> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "ocsprefstypef13ftype");
    public static final SchemaType type = Factory.getType();

    public List<OCSPRefType> getOCSPRefList();

    public OCSPRefType[] getOCSPRefArray();

    public OCSPRefType getOCSPRefArray(int var1);

    public int sizeOfOCSPRefArray();

    public void setOCSPRefArray(OCSPRefType[] var1);

    public void setOCSPRefArray(int var1, OCSPRefType var2);

    public OCSPRefType insertNewOCSPRef(int var1);

    public OCSPRefType addNewOCSPRef();

    public void removeOCSPRef(int var1);
}


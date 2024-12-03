/*
 * Decompiled with CFR 0.152.
 */
package org.etsi.uri.x01903.v13;

import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlAnyURI;
import org.apache.xmlbeans.XmlBase64Binary;
import org.apache.xmlbeans.XmlID;
import org.apache.xmlbeans.impl.schema.DocumentFactory;

public interface EncapsulatedPKIDataType
extends XmlBase64Binary {
    public static final DocumentFactory<EncapsulatedPKIDataType> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "encapsulatedpkidatatype4081type");
    public static final SchemaType type = Factory.getType();

    public String getId();

    public XmlID xgetId();

    public boolean isSetId();

    public void setId(String var1);

    public void xsetId(XmlID var1);

    public void unsetId();

    public String getEncoding();

    public XmlAnyURI xgetEncoding();

    public boolean isSetEncoding();

    public void setEncoding(String var1);

    public void xsetEncoding(XmlAnyURI var1);

    public void unsetEncoding();
}


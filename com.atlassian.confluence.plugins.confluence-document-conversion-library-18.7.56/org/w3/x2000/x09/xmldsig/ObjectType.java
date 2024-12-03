/*
 * Decompiled with CFR 0.152.
 */
package org.w3.x2000.x09.xmldsig;

import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlAnyURI;
import org.apache.xmlbeans.XmlID;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.XmlString;
import org.apache.xmlbeans.impl.schema.DocumentFactory;

public interface ObjectType
extends XmlObject {
    public static final DocumentFactory<ObjectType> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "objecttypec966type");
    public static final SchemaType type = Factory.getType();

    public String getId();

    public XmlID xgetId();

    public boolean isSetId();

    public void setId(String var1);

    public void xsetId(XmlID var1);

    public void unsetId();

    public String getMimeType();

    public XmlString xgetMimeType();

    public boolean isSetMimeType();

    public void setMimeType(String var1);

    public void xsetMimeType(XmlString var1);

    public void unsetMimeType();

    public String getEncoding();

    public XmlAnyURI xgetEncoding();

    public boolean isSetEncoding();

    public void setEncoding(String var1);

    public void xsetEncoding(XmlAnyURI var1);

    public void unsetEncoding();
}


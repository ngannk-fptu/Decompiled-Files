/*
 * Decompiled with CFR 0.152.
 */
package org.etsi.uri.x01903.v13;

import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlAnyURI;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.XmlString;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.etsi.uri.x01903.v13.ObjectIdentifierType;

public interface DataObjectFormatType
extends XmlObject {
    public static final DocumentFactory<DataObjectFormatType> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "dataobjectformattype44eetype");
    public static final SchemaType type = Factory.getType();

    public String getDescription();

    public XmlString xgetDescription();

    public boolean isSetDescription();

    public void setDescription(String var1);

    public void xsetDescription(XmlString var1);

    public void unsetDescription();

    public ObjectIdentifierType getObjectIdentifier();

    public boolean isSetObjectIdentifier();

    public void setObjectIdentifier(ObjectIdentifierType var1);

    public ObjectIdentifierType addNewObjectIdentifier();

    public void unsetObjectIdentifier();

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

    public String getObjectReference();

    public XmlAnyURI xgetObjectReference();

    public void setObjectReference(String var1);

    public void xsetObjectReference(XmlAnyURI var1);
}


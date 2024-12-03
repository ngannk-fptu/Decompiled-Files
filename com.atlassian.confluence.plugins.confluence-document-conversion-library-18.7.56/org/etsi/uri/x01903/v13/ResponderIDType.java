/*
 * Decompiled with CFR 0.152.
 */
package org.etsi.uri.x01903.v13;

import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlBase64Binary;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.XmlString;
import org.apache.xmlbeans.impl.schema.DocumentFactory;

public interface ResponderIDType
extends XmlObject {
    public static final DocumentFactory<ResponderIDType> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "responderidtype55b9type");
    public static final SchemaType type = Factory.getType();

    public String getByName();

    public XmlString xgetByName();

    public boolean isSetByName();

    public void setByName(String var1);

    public void xsetByName(XmlString var1);

    public void unsetByName();

    public byte[] getByKey();

    public XmlBase64Binary xgetByKey();

    public boolean isSetByKey();

    public void setByKey(byte[] var1);

    public void xsetByKey(XmlBase64Binary var1);

    public void unsetByKey();
}


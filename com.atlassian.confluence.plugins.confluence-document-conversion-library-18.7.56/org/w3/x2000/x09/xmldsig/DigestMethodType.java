/*
 * Decompiled with CFR 0.152.
 */
package org.w3.x2000.x09.xmldsig;

import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlAnyURI;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.impl.schema.DocumentFactory;

public interface DigestMethodType
extends XmlObject {
    public static final DocumentFactory<DigestMethodType> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "digestmethodtype5ce0type");
    public static final SchemaType type = Factory.getType();

    public String getAlgorithm();

    public XmlAnyURI xgetAlgorithm();

    public void setAlgorithm(String var1);

    public void xsetAlgorithm(XmlAnyURI var1);
}


/*
 * Decompiled with CFR 0.152.
 */
package org.w3.x2000.x09.xmldsig;

import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlBase64Binary;
import org.apache.xmlbeans.impl.schema.SimpleTypeFactory;

public interface DigestValueType
extends XmlBase64Binary {
    public static final SimpleTypeFactory<DigestValueType> Factory = new SimpleTypeFactory(TypeSystemHolder.typeSystem, "digestvaluetype010atype");
    public static final SchemaType type = Factory.getType();
}


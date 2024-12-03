/*
 * Decompiled with CFR 0.152.
 */
package com.microsoft.schemas.office.x2006.digsig;

import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlString;
import org.apache.xmlbeans.impl.schema.SimpleTypeFactory;

public interface STUniqueIdentifierWithBraces
extends XmlString {
    public static final SimpleTypeFactory<STUniqueIdentifierWithBraces> Factory = new SimpleTypeFactory(TypeSystemHolder.typeSystem, "stuniqueidentifierwithbraces0a78type");
    public static final SchemaType type = Factory.getType();
}


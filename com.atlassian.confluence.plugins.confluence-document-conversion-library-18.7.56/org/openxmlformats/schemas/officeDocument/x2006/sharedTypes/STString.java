/*
 * Decompiled with CFR 0.152.
 */
package org.openxmlformats.schemas.officeDocument.x2006.sharedTypes;

import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlString;
import org.apache.xmlbeans.impl.schema.SimpleTypeFactory;

public interface STString
extends XmlString {
    public static final SimpleTypeFactory<STString> Factory = new SimpleTypeFactory(TypeSystemHolder.typeSystem, "ststring76cbtype");
    public static final SchemaType type = Factory.getType();
}


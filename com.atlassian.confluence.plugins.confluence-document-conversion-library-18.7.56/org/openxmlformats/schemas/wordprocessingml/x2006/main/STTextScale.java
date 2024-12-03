/*
 * Decompiled with CFR 0.152.
 */
package org.openxmlformats.schemas.wordprocessingml.x2006.main;

import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlAnySimpleType;
import org.apache.xmlbeans.impl.schema.SimpleTypeFactory;

public interface STTextScale
extends XmlAnySimpleType {
    public static final SimpleTypeFactory<STTextScale> Factory = new SimpleTypeFactory(TypeSystemHolder.typeSystem, "sttextscalea465type");
    public static final SchemaType type = Factory.getType();

    public Object getObjectValue();

    public void setObjectValue(Object var1);

    public SchemaType instanceType();
}


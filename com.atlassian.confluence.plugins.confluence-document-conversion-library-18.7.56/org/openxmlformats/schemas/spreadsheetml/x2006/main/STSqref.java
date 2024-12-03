/*
 * Decompiled with CFR 0.152.
 */
package org.openxmlformats.schemas.spreadsheetml.x2006.main;

import java.util.List;
import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlAnySimpleType;
import org.apache.xmlbeans.impl.schema.SimpleTypeFactory;

public interface STSqref
extends XmlAnySimpleType {
    public static final SimpleTypeFactory<STSqref> Factory = new SimpleTypeFactory(TypeSystemHolder.typeSystem, "stsqrefb044type");
    public static final SchemaType type = Factory.getType();

    public List getListValue();

    public List xgetListValue();

    public void setListValue(List<?> var1);
}


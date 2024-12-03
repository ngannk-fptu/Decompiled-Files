/*
 * Decompiled with CFR 0.152.
 */
package org.openxmlformats.schemas.wordprocessingml.x2006.main;

import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STSdtDateMappingType;

public interface CTSdtDateMappingType
extends XmlObject {
    public static final DocumentFactory<CTSdtDateMappingType> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "ctsdtdatemappingtype5fb1type");
    public static final SchemaType type = Factory.getType();

    public STSdtDateMappingType.Enum getVal();

    public STSdtDateMappingType xgetVal();

    public boolean isSetVal();

    public void setVal(STSdtDateMappingType.Enum var1);

    public void xsetVal(STSdtDateMappingType var1);

    public void unsetVal();
}


/*
 * Decompiled with CFR 0.152.
 */
package org.openxmlformats.schemas.wordprocessingml.x2006.main;

import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STTblLayoutType;

public interface CTTblLayoutType
extends XmlObject {
    public static final DocumentFactory<CTTblLayoutType> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "cttbllayouttype6830type");
    public static final SchemaType type = Factory.getType();

    public STTblLayoutType.Enum getType();

    public STTblLayoutType xgetType();

    public boolean isSetType();

    public void setType(STTblLayoutType.Enum var1);

    public void xsetType(STTblLayoutType var1);

    public void unsetType();
}


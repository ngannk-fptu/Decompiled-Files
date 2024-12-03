/*
 * Decompiled with CFR 0.152.
 */
package org.openxmlformats.schemas.wordprocessingml.x2006.main;

import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STBrClear;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STBrType;

public interface CTBr
extends XmlObject {
    public static final DocumentFactory<CTBr> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "ctbr7dd8type");
    public static final SchemaType type = Factory.getType();

    public STBrType.Enum getType();

    public STBrType xgetType();

    public boolean isSetType();

    public void setType(STBrType.Enum var1);

    public void xsetType(STBrType var1);

    public void unsetType();

    public STBrClear.Enum getClear();

    public STBrClear xgetClear();

    public boolean isSetClear();

    public void setClear(STBrClear.Enum var1);

    public void xsetClear(STBrClear var1);

    public void unsetClear();
}


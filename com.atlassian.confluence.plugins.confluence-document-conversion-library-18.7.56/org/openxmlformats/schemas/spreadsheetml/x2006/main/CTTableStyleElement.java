/*
 * Decompiled with CFR 0.152.
 */
package org.openxmlformats.schemas.spreadsheetml.x2006.main;

import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.XmlUnsignedInt;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.STDxfId;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.STTableStyleType;

public interface CTTableStyleElement
extends XmlObject {
    public static final DocumentFactory<CTTableStyleElement> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "cttablestyleelementa658type");
    public static final SchemaType type = Factory.getType();

    public STTableStyleType.Enum getType();

    public STTableStyleType xgetType();

    public void setType(STTableStyleType.Enum var1);

    public void xsetType(STTableStyleType var1);

    public long getSize();

    public XmlUnsignedInt xgetSize();

    public boolean isSetSize();

    public void setSize(long var1);

    public void xsetSize(XmlUnsignedInt var1);

    public void unsetSize();

    public long getDxfId();

    public STDxfId xgetDxfId();

    public boolean isSetDxfId();

    public void setDxfId(long var1);

    public void xsetDxfId(STDxfId var1);

    public void unsetDxfId();
}


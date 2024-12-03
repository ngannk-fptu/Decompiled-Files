/*
 * Decompiled with CFR 0.152.
 */
package org.openxmlformats.schemas.spreadsheetml.x2006.main;

import java.util.List;
import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.XmlUnsignedInt;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTTablePart;

public interface CTTableParts
extends XmlObject {
    public static final DocumentFactory<CTTableParts> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "cttablepartsf6bbtype");
    public static final SchemaType type = Factory.getType();

    public List<CTTablePart> getTablePartList();

    public CTTablePart[] getTablePartArray();

    public CTTablePart getTablePartArray(int var1);

    public int sizeOfTablePartArray();

    public void setTablePartArray(CTTablePart[] var1);

    public void setTablePartArray(int var1, CTTablePart var2);

    public CTTablePart insertNewTablePart(int var1);

    public CTTablePart addNewTablePart();

    public void removeTablePart(int var1);

    public long getCount();

    public XmlUnsignedInt xgetCount();

    public boolean isSetCount();

    public void setCount(long var1);

    public void xsetCount(XmlUnsignedInt var1);

    public void unsetCount();
}


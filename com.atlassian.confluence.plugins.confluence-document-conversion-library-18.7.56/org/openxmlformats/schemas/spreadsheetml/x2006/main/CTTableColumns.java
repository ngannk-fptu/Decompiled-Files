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
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTTableColumn;

public interface CTTableColumns
extends XmlObject {
    public static final DocumentFactory<CTTableColumns> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "cttablecolumnsebb8type");
    public static final SchemaType type = Factory.getType();

    public List<CTTableColumn> getTableColumnList();

    public CTTableColumn[] getTableColumnArray();

    public CTTableColumn getTableColumnArray(int var1);

    public int sizeOfTableColumnArray();

    public void setTableColumnArray(CTTableColumn[] var1);

    public void setTableColumnArray(int var1, CTTableColumn var2);

    public CTTableColumn insertNewTableColumn(int var1);

    public CTTableColumn addNewTableColumn();

    public void removeTableColumn(int var1);

    public long getCount();

    public XmlUnsignedInt xgetCount();

    public boolean isSetCount();

    public void setCount(long var1);

    public void xsetCount(XmlUnsignedInt var1);

    public void unsetCount();
}


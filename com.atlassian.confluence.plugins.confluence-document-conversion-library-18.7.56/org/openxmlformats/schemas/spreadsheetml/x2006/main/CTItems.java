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
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTItem;

public interface CTItems
extends XmlObject {
    public static final DocumentFactory<CTItems> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "ctitemsecdftype");
    public static final SchemaType type = Factory.getType();

    public List<CTItem> getItemList();

    public CTItem[] getItemArray();

    public CTItem getItemArray(int var1);

    public int sizeOfItemArray();

    public void setItemArray(CTItem[] var1);

    public void setItemArray(int var1, CTItem var2);

    public CTItem insertNewItem(int var1);

    public CTItem addNewItem();

    public void removeItem(int var1);

    public long getCount();

    public XmlUnsignedInt xgetCount();

    public boolean isSetCount();

    public void setCount(long var1);

    public void xsetCount(XmlUnsignedInt var1);

    public void unsetCount();
}


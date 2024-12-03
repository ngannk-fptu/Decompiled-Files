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
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTCacheField;

public interface CTCacheFields
extends XmlObject {
    public static final DocumentFactory<CTCacheFields> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "ctcachefieldsf5fatype");
    public static final SchemaType type = Factory.getType();

    public List<CTCacheField> getCacheFieldList();

    public CTCacheField[] getCacheFieldArray();

    public CTCacheField getCacheFieldArray(int var1);

    public int sizeOfCacheFieldArray();

    public void setCacheFieldArray(CTCacheField[] var1);

    public void setCacheFieldArray(int var1, CTCacheField var2);

    public CTCacheField insertNewCacheField(int var1);

    public CTCacheField addNewCacheField();

    public void removeCacheField(int var1);

    public long getCount();

    public XmlUnsignedInt xgetCount();

    public boolean isSetCount();

    public void setCount(long var1);

    public void xsetCount(XmlUnsignedInt var1);

    public void unsetCount();
}


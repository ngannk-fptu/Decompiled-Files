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
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTDxf;

public interface CTDxfs
extends XmlObject {
    public static final DocumentFactory<CTDxfs> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "ctdxfsb26atype");
    public static final SchemaType type = Factory.getType();

    public List<CTDxf> getDxfList();

    public CTDxf[] getDxfArray();

    public CTDxf getDxfArray(int var1);

    public int sizeOfDxfArray();

    public void setDxfArray(CTDxf[] var1);

    public void setDxfArray(int var1, CTDxf var2);

    public CTDxf insertNewDxf(int var1);

    public CTDxf addNewDxf();

    public void removeDxf(int var1);

    public long getCount();

    public XmlUnsignedInt xgetCount();

    public boolean isSetCount();

    public void setCount(long var1);

    public void xsetCount(XmlUnsignedInt var1);

    public void unsetCount();
}


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
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTXf;

public interface CTCellXfs
extends XmlObject {
    public static final DocumentFactory<CTCellXfs> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "ctcellxfs1322type");
    public static final SchemaType type = Factory.getType();

    public List<CTXf> getXfList();

    public CTXf[] getXfArray();

    public CTXf getXfArray(int var1);

    public int sizeOfXfArray();

    public void setXfArray(CTXf[] var1);

    public void setXfArray(int var1, CTXf var2);

    public CTXf insertNewXf(int var1);

    public CTXf addNewXf();

    public void removeXf(int var1);

    public long getCount();

    public XmlUnsignedInt xgetCount();

    public boolean isSetCount();

    public void setCount(long var1);

    public void xsetCount(XmlUnsignedInt var1);

    public void unsetCount();
}


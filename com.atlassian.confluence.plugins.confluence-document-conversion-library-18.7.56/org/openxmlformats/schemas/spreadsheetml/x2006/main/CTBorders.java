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
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTBorder;

public interface CTBorders
extends XmlObject {
    public static final DocumentFactory<CTBorders> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "ctborders0d66type");
    public static final SchemaType type = Factory.getType();

    public List<CTBorder> getBorderList();

    public CTBorder[] getBorderArray();

    public CTBorder getBorderArray(int var1);

    public int sizeOfBorderArray();

    public void setBorderArray(CTBorder[] var1);

    public void setBorderArray(int var1, CTBorder var2);

    public CTBorder insertNewBorder(int var1);

    public CTBorder addNewBorder();

    public void removeBorder(int var1);

    public long getCount();

    public XmlUnsignedInt xgetCount();

    public boolean isSetCount();

    public void setCount(long var1);

    public void xsetCount(XmlUnsignedInt var1);

    public void unsetCount();
}


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
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTFill;

public interface CTFills
extends XmlObject {
    public static final DocumentFactory<CTFills> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "ctfills2c6ftype");
    public static final SchemaType type = Factory.getType();

    public List<CTFill> getFillList();

    public CTFill[] getFillArray();

    public CTFill getFillArray(int var1);

    public int sizeOfFillArray();

    public void setFillArray(CTFill[] var1);

    public void setFillArray(int var1, CTFill var2);

    public CTFill insertNewFill(int var1);

    public CTFill addNewFill();

    public void removeFill(int var1);

    public long getCount();

    public XmlUnsignedInt xgetCount();

    public boolean isSetCount();

    public void setCount(long var1);

    public void xsetCount(XmlUnsignedInt var1);

    public void unsetCount();
}


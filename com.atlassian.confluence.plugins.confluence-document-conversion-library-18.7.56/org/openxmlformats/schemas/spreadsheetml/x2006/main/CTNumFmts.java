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
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTNumFmt;

public interface CTNumFmts
extends XmlObject {
    public static final DocumentFactory<CTNumFmts> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "ctnumfmtsb58btype");
    public static final SchemaType type = Factory.getType();

    public List<CTNumFmt> getNumFmtList();

    public CTNumFmt[] getNumFmtArray();

    public CTNumFmt getNumFmtArray(int var1);

    public int sizeOfNumFmtArray();

    public void setNumFmtArray(CTNumFmt[] var1);

    public void setNumFmtArray(int var1, CTNumFmt var2);

    public CTNumFmt insertNewNumFmt(int var1);

    public CTNumFmt addNewNumFmt();

    public void removeNumFmt(int var1);

    public long getCount();

    public XmlUnsignedInt xgetCount();

    public boolean isSetCount();

    public void setCount(long var1);

    public void xsetCount(XmlUnsignedInt var1);

    public void unsetCount();
}


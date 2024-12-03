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
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTFont;

public interface CTFonts
extends XmlObject {
    public static final DocumentFactory<CTFonts> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "ctfonts6623type");
    public static final SchemaType type = Factory.getType();

    public List<CTFont> getFontList();

    public CTFont[] getFontArray();

    public CTFont getFontArray(int var1);

    public int sizeOfFontArray();

    public void setFontArray(CTFont[] var1);

    public void setFontArray(int var1, CTFont var2);

    public CTFont insertNewFont(int var1);

    public CTFont addNewFont();

    public void removeFont(int var1);

    public long getCount();

    public XmlUnsignedInt xgetCount();

    public boolean isSetCount();

    public void setCount(long var1);

    public void xsetCount(XmlUnsignedInt var1);

    public void unsetCount();
}


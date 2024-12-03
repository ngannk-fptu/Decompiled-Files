/*
 * Decompiled with CFR 0.152.
 */
package org.openxmlformats.schemas.spreadsheetml.x2006.main;

import java.util.List;
import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.XmlString;
import org.apache.xmlbeans.XmlUnsignedInt;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTTableStyle;

public interface CTTableStyles
extends XmlObject {
    public static final DocumentFactory<CTTableStyles> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "cttablestyles872ftype");
    public static final SchemaType type = Factory.getType();

    public List<CTTableStyle> getTableStyleList();

    public CTTableStyle[] getTableStyleArray();

    public CTTableStyle getTableStyleArray(int var1);

    public int sizeOfTableStyleArray();

    public void setTableStyleArray(CTTableStyle[] var1);

    public void setTableStyleArray(int var1, CTTableStyle var2);

    public CTTableStyle insertNewTableStyle(int var1);

    public CTTableStyle addNewTableStyle();

    public void removeTableStyle(int var1);

    public long getCount();

    public XmlUnsignedInt xgetCount();

    public boolean isSetCount();

    public void setCount(long var1);

    public void xsetCount(XmlUnsignedInt var1);

    public void unsetCount();

    public String getDefaultTableStyle();

    public XmlString xgetDefaultTableStyle();

    public boolean isSetDefaultTableStyle();

    public void setDefaultTableStyle(String var1);

    public void xsetDefaultTableStyle(XmlString var1);

    public void unsetDefaultTableStyle();

    public String getDefaultPivotStyle();

    public XmlString xgetDefaultPivotStyle();

    public boolean isSetDefaultPivotStyle();

    public void setDefaultPivotStyle(String var1);

    public void xsetDefaultPivotStyle(XmlString var1);

    public void unsetDefaultPivotStyle();
}


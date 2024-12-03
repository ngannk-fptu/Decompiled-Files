/*
 * Decompiled with CFR 0.152.
 */
package org.openxmlformats.schemas.spreadsheetml.x2006.main;

import java.util.List;
import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlBoolean;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.XmlString;
import org.apache.xmlbeans.XmlUnsignedInt;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTTableStyleElement;

public interface CTTableStyle
extends XmlObject {
    public static final DocumentFactory<CTTableStyle> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "cttablestylea24ctype");
    public static final SchemaType type = Factory.getType();

    public List<CTTableStyleElement> getTableStyleElementList();

    public CTTableStyleElement[] getTableStyleElementArray();

    public CTTableStyleElement getTableStyleElementArray(int var1);

    public int sizeOfTableStyleElementArray();

    public void setTableStyleElementArray(CTTableStyleElement[] var1);

    public void setTableStyleElementArray(int var1, CTTableStyleElement var2);

    public CTTableStyleElement insertNewTableStyleElement(int var1);

    public CTTableStyleElement addNewTableStyleElement();

    public void removeTableStyleElement(int var1);

    public String getName();

    public XmlString xgetName();

    public void setName(String var1);

    public void xsetName(XmlString var1);

    public boolean getPivot();

    public XmlBoolean xgetPivot();

    public boolean isSetPivot();

    public void setPivot(boolean var1);

    public void xsetPivot(XmlBoolean var1);

    public void unsetPivot();

    public boolean getTable();

    public XmlBoolean xgetTable();

    public boolean isSetTable();

    public void setTable(boolean var1);

    public void xsetTable(XmlBoolean var1);

    public void unsetTable();

    public long getCount();

    public XmlUnsignedInt xgetCount();

    public boolean isSetCount();

    public void setCount(long var1);

    public void xsetCount(XmlUnsignedInt var1);

    public void unsetCount();
}


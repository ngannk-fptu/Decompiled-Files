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
import org.openxmlformats.schemas.spreadsheetml.x2006.main.STCellRef;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.STPane;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.STSqref;

public interface CTSelection
extends XmlObject {
    public static final DocumentFactory<CTSelection> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "ctselectionca2btype");
    public static final SchemaType type = Factory.getType();

    public STPane.Enum getPane();

    public STPane xgetPane();

    public boolean isSetPane();

    public void setPane(STPane.Enum var1);

    public void xsetPane(STPane var1);

    public void unsetPane();

    public String getActiveCell();

    public STCellRef xgetActiveCell();

    public boolean isSetActiveCell();

    public void setActiveCell(String var1);

    public void xsetActiveCell(STCellRef var1);

    public void unsetActiveCell();

    public long getActiveCellId();

    public XmlUnsignedInt xgetActiveCellId();

    public boolean isSetActiveCellId();

    public void setActiveCellId(long var1);

    public void xsetActiveCellId(XmlUnsignedInt var1);

    public void unsetActiveCellId();

    public List getSqref();

    public STSqref xgetSqref();

    public boolean isSetSqref();

    public void setSqref(List var1);

    public void xsetSqref(STSqref var1);

    public void unsetSqref();
}


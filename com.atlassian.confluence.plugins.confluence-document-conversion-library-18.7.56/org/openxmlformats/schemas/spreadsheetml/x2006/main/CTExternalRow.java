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
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTExternalCell;

public interface CTExternalRow
extends XmlObject {
    public static final DocumentFactory<CTExternalRow> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "ctexternalrowa22etype");
    public static final SchemaType type = Factory.getType();

    public List<CTExternalCell> getCellList();

    public CTExternalCell[] getCellArray();

    public CTExternalCell getCellArray(int var1);

    public int sizeOfCellArray();

    public void setCellArray(CTExternalCell[] var1);

    public void setCellArray(int var1, CTExternalCell var2);

    public CTExternalCell insertNewCell(int var1);

    public CTExternalCell addNewCell();

    public void removeCell(int var1);

    public long getR();

    public XmlUnsignedInt xgetR();

    public void setR(long var1);

    public void xsetR(XmlUnsignedInt var1);
}


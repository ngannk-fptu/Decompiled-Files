/*
 * Decompiled with CFR 0.152.
 */
package org.openxmlformats.schemas.spreadsheetml.x2006.main;

import java.util.List;
import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTRow;

public interface CTSheetData
extends XmlObject {
    public static final DocumentFactory<CTSheetData> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "ctsheetdata8408type");
    public static final SchemaType type = Factory.getType();

    public List<CTRow> getRowList();

    public CTRow[] getRowArray();

    public CTRow getRowArray(int var1);

    public int sizeOfRowArray();

    public void setRowArray(CTRow[] var1);

    public void setRowArray(int var1, CTRow var2);

    public CTRow insertNewRow(int var1);

    public CTRow addNewRow();

    public void removeRow(int var1);
}


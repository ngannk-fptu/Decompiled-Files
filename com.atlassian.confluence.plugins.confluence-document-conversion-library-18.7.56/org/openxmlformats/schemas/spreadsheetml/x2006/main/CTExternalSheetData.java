/*
 * Decompiled with CFR 0.152.
 */
package org.openxmlformats.schemas.spreadsheetml.x2006.main;

import java.util.List;
import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlBoolean;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.XmlUnsignedInt;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTExternalRow;

public interface CTExternalSheetData
extends XmlObject {
    public static final DocumentFactory<CTExternalSheetData> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "ctexternalsheetdatafd3dtype");
    public static final SchemaType type = Factory.getType();

    public List<CTExternalRow> getRowList();

    public CTExternalRow[] getRowArray();

    public CTExternalRow getRowArray(int var1);

    public int sizeOfRowArray();

    public void setRowArray(CTExternalRow[] var1);

    public void setRowArray(int var1, CTExternalRow var2);

    public CTExternalRow insertNewRow(int var1);

    public CTExternalRow addNewRow();

    public void removeRow(int var1);

    public long getSheetId();

    public XmlUnsignedInt xgetSheetId();

    public void setSheetId(long var1);

    public void xsetSheetId(XmlUnsignedInt var1);

    public boolean getRefreshError();

    public XmlBoolean xgetRefreshError();

    public boolean isSetRefreshError();

    public void setRefreshError(boolean var1);

    public void xsetRefreshError(XmlBoolean var1);

    public void unsetRefreshError();
}


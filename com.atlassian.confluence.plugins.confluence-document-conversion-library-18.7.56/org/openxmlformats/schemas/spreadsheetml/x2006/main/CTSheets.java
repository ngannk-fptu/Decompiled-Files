/*
 * Decompiled with CFR 0.152.
 */
package org.openxmlformats.schemas.spreadsheetml.x2006.main;

import java.util.List;
import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTSheet;

public interface CTSheets
extends XmlObject {
    public static final DocumentFactory<CTSheets> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "ctsheets49fdtype");
    public static final SchemaType type = Factory.getType();

    public List<CTSheet> getSheetList();

    public CTSheet[] getSheetArray();

    public CTSheet getSheetArray(int var1);

    public int sizeOfSheetArray();

    public void setSheetArray(CTSheet[] var1);

    public void setSheetArray(int var1, CTSheet var2);

    public CTSheet insertNewSheet(int var1);

    public CTSheet addNewSheet();

    public void removeSheet(int var1);
}


/*
 * Decompiled with CFR 0.152.
 */
package org.openxmlformats.schemas.spreadsheetml.x2006.main;

import java.util.List;
import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTExternalSheetData;

public interface CTExternalSheetDataSet
extends XmlObject {
    public static final DocumentFactory<CTExternalSheetDataSet> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "ctexternalsheetdataset07adtype");
    public static final SchemaType type = Factory.getType();

    public List<CTExternalSheetData> getSheetDataList();

    public CTExternalSheetData[] getSheetDataArray();

    public CTExternalSheetData getSheetDataArray(int var1);

    public int sizeOfSheetDataArray();

    public void setSheetDataArray(CTExternalSheetData[] var1);

    public void setSheetDataArray(int var1, CTExternalSheetData var2);

    public CTExternalSheetData insertNewSheetData(int var1);

    public CTExternalSheetData addNewSheetData();

    public void removeSheetData(int var1);
}


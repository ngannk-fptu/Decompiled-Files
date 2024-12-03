/*
 * Decompiled with CFR 0.152.
 */
package org.openxmlformats.schemas.spreadsheetml.x2006.main;

import java.util.List;
import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTCustomSheetView;

public interface CTCustomSheetViews
extends XmlObject {
    public static final DocumentFactory<CTCustomSheetViews> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "ctcustomsheetviewsc069type");
    public static final SchemaType type = Factory.getType();

    public List<CTCustomSheetView> getCustomSheetViewList();

    public CTCustomSheetView[] getCustomSheetViewArray();

    public CTCustomSheetView getCustomSheetViewArray(int var1);

    public int sizeOfCustomSheetViewArray();

    public void setCustomSheetViewArray(CTCustomSheetView[] var1);

    public void setCustomSheetViewArray(int var1, CTCustomSheetView var2);

    public CTCustomSheetView insertNewCustomSheetView(int var1);

    public CTCustomSheetView addNewCustomSheetView();

    public void removeCustomSheetView(int var1);
}


/*
 * Decompiled with CFR 0.152.
 */
package org.openxmlformats.schemas.spreadsheetml.x2006.main;

import java.util.List;
import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTExtensionList;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTSheetView;

public interface CTSheetViews
extends XmlObject {
    public static final DocumentFactory<CTSheetViews> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "ctsheetviewsb918type");
    public static final SchemaType type = Factory.getType();

    public List<CTSheetView> getSheetViewList();

    public CTSheetView[] getSheetViewArray();

    public CTSheetView getSheetViewArray(int var1);

    public int sizeOfSheetViewArray();

    public void setSheetViewArray(CTSheetView[] var1);

    public void setSheetViewArray(int var1, CTSheetView var2);

    public CTSheetView insertNewSheetView(int var1);

    public CTSheetView addNewSheetView();

    public void removeSheetView(int var1);

    public CTExtensionList getExtLst();

    public boolean isSetExtLst();

    public void setExtLst(CTExtensionList var1);

    public CTExtensionList addNewExtLst();

    public void unsetExtLst();
}


/*
 * Decompiled with CFR 0.152.
 */
package org.openxmlformats.schemas.spreadsheetml.x2006.main;

import java.util.List;
import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTCustomWorkbookView;

public interface CTCustomWorkbookViews
extends XmlObject {
    public static final DocumentFactory<CTCustomWorkbookViews> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "ctcustomworkbookviewse942type");
    public static final SchemaType type = Factory.getType();

    public List<CTCustomWorkbookView> getCustomWorkbookViewList();

    public CTCustomWorkbookView[] getCustomWorkbookViewArray();

    public CTCustomWorkbookView getCustomWorkbookViewArray(int var1);

    public int sizeOfCustomWorkbookViewArray();

    public void setCustomWorkbookViewArray(CTCustomWorkbookView[] var1);

    public void setCustomWorkbookViewArray(int var1, CTCustomWorkbookView var2);

    public CTCustomWorkbookView insertNewCustomWorkbookView(int var1);

    public CTCustomWorkbookView addNewCustomWorkbookView();

    public void removeCustomWorkbookView(int var1);
}


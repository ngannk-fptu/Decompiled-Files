/*
 * Decompiled with CFR 0.152.
 */
package org.openxmlformats.schemas.spreadsheetml.x2006.main;

import java.util.List;
import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTBookView;

public interface CTBookViews
extends XmlObject {
    public static final DocumentFactory<CTBookViews> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "ctbookviewsb864type");
    public static final SchemaType type = Factory.getType();

    public List<CTBookView> getWorkbookViewList();

    public CTBookView[] getWorkbookViewArray();

    public CTBookView getWorkbookViewArray(int var1);

    public int sizeOfWorkbookViewArray();

    public void setWorkbookViewArray(CTBookView[] var1);

    public void setWorkbookViewArray(int var1, CTBookView var2);

    public CTBookView insertNewWorkbookView(int var1);

    public CTBookView addNewWorkbookView();

    public void removeWorkbookView(int var1);
}


/*
 * Decompiled with CFR 0.152.
 */
package org.openxmlformats.schemas.wordprocessingml.x2006.main;

import java.util.List;
import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.openxmlformats.schemas.officeDocument.x2006.sharedTypes.STString;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTSdtListItem;

public interface CTSdtComboBox
extends XmlObject {
    public static final DocumentFactory<CTSdtComboBox> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "ctsdtcomboboxdb52type");
    public static final SchemaType type = Factory.getType();

    public List<CTSdtListItem> getListItemList();

    public CTSdtListItem[] getListItemArray();

    public CTSdtListItem getListItemArray(int var1);

    public int sizeOfListItemArray();

    public void setListItemArray(CTSdtListItem[] var1);

    public void setListItemArray(int var1, CTSdtListItem var2);

    public CTSdtListItem insertNewListItem(int var1);

    public CTSdtListItem addNewListItem();

    public void removeListItem(int var1);

    public String getLastValue();

    public STString xgetLastValue();

    public boolean isSetLastValue();

    public void setLastValue(String var1);

    public void xsetLastValue(STString var1);

    public void unsetLastValue();
}


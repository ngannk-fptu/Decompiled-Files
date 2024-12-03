/*
 * Decompiled with CFR 0.152.
 */
package org.openxmlformats.schemas.spreadsheetml.x2006.main;

import java.util.List;
import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTCustomProperty;

public interface CTCustomProperties
extends XmlObject {
    public static final DocumentFactory<CTCustomProperties> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "ctcustomproperties584dtype");
    public static final SchemaType type = Factory.getType();

    public List<CTCustomProperty> getCustomPrList();

    public CTCustomProperty[] getCustomPrArray();

    public CTCustomProperty getCustomPrArray(int var1);

    public int sizeOfCustomPrArray();

    public void setCustomPrArray(CTCustomProperty[] var1);

    public void setCustomPrArray(int var1, CTCustomProperty var2);

    public CTCustomProperty insertNewCustomPr(int var1);

    public CTCustomProperty addNewCustomPr();

    public void removeCustomPr(int var1);
}


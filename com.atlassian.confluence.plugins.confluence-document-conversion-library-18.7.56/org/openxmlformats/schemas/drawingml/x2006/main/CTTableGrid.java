/*
 * Decompiled with CFR 0.152.
 */
package org.openxmlformats.schemas.drawingml.x2006.main;

import java.util.List;
import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.openxmlformats.schemas.drawingml.x2006.main.CTTableCol;

public interface CTTableGrid
extends XmlObject {
    public static final DocumentFactory<CTTableGrid> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "cttablegrid69a5type");
    public static final SchemaType type = Factory.getType();

    public List<CTTableCol> getGridColList();

    public CTTableCol[] getGridColArray();

    public CTTableCol getGridColArray(int var1);

    public int sizeOfGridColArray();

    public void setGridColArray(CTTableCol[] var1);

    public void setGridColArray(int var1, CTTableCol var2);

    public CTTableCol insertNewGridCol(int var1);

    public CTTableCol addNewGridCol();

    public void removeGridCol(int var1);
}


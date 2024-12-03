/*
 * Decompiled with CFR 0.152.
 */
package org.openxmlformats.schemas.wordprocessingml.x2006.main;

import java.util.List;
import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTblGridCol;

public interface CTTblGridBase
extends XmlObject {
    public static final DocumentFactory<CTTblGridBase> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "cttblgridbasea11dtype");
    public static final SchemaType type = Factory.getType();

    public List<CTTblGridCol> getGridColList();

    public CTTblGridCol[] getGridColArray();

    public CTTblGridCol getGridColArray(int var1);

    public int sizeOfGridColArray();

    public void setGridColArray(CTTblGridCol[] var1);

    public void setGridColArray(int var1, CTTblGridCol var2);

    public CTTblGridCol insertNewGridCol(int var1);

    public CTTblGridCol addNewGridCol();

    public void removeGridCol(int var1);
}


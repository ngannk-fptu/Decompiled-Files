/*
 * Decompiled with CFR 0.152.
 */
package org.openxmlformats.schemas.spreadsheetml.x2006.main;

import java.util.List;
import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTCol;

public interface CTCols
extends XmlObject {
    public static final DocumentFactory<CTCols> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "ctcols627ctype");
    public static final SchemaType type = Factory.getType();

    public List<CTCol> getColList();

    public CTCol[] getColArray();

    public CTCol getColArray(int var1);

    public int sizeOfColArray();

    public void setColArray(CTCol[] var1);

    public void setColArray(int var1, CTCol var2);

    public CTCol insertNewCol(int var1);

    public CTCol addNewCol();

    public void removeCol(int var1);
}


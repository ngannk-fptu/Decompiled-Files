/*
 * Decompiled with CFR 0.152.
 */
package org.openxmlformats.schemas.spreadsheetml.x2006.main;

import java.util.List;
import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.XmlUnsignedInt;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTMergeCell;

public interface CTMergeCells
extends XmlObject {
    public static final DocumentFactory<CTMergeCells> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "ctmergecells1242type");
    public static final SchemaType type = Factory.getType();

    public List<CTMergeCell> getMergeCellList();

    public CTMergeCell[] getMergeCellArray();

    public CTMergeCell getMergeCellArray(int var1);

    public int sizeOfMergeCellArray();

    public void setMergeCellArray(CTMergeCell[] var1);

    public void setMergeCellArray(int var1, CTMergeCell var2);

    public CTMergeCell insertNewMergeCell(int var1);

    public CTMergeCell addNewMergeCell();

    public void removeMergeCell(int var1);

    public long getCount();

    public XmlUnsignedInt xgetCount();

    public boolean isSetCount();

    public void setCount(long var1);

    public void xsetCount(XmlUnsignedInt var1);

    public void unsetCount();
}


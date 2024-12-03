/*
 * Decompiled with CFR 0.152.
 */
package org.openxmlformats.schemas.spreadsheetml.x2006.main;

import java.util.List;
import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTSingleXmlCell;

public interface CTSingleXmlCells
extends XmlObject {
    public static final DocumentFactory<CTSingleXmlCells> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "ctsinglexmlcells5a6btype");
    public static final SchemaType type = Factory.getType();

    public List<CTSingleXmlCell> getSingleXmlCellList();

    public CTSingleXmlCell[] getSingleXmlCellArray();

    public CTSingleXmlCell getSingleXmlCellArray(int var1);

    public int sizeOfSingleXmlCellArray();

    public void setSingleXmlCellArray(CTSingleXmlCell[] var1);

    public void setSingleXmlCellArray(int var1, CTSingleXmlCell var2);

    public CTSingleXmlCell insertNewSingleXmlCell(int var1);

    public CTSingleXmlCell addNewSingleXmlCell();

    public void removeSingleXmlCell(int var1);
}


/*
 * Decompiled with CFR 0.152.
 */
package org.openxmlformats.schemas.spreadsheetml.x2006.main;

import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTSingleXmlCells;

public interface SingleXmlCellsDocument
extends XmlObject {
    public static final DocumentFactory<SingleXmlCellsDocument> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "singlexmlcells33bfdoctype");
    public static final SchemaType type = Factory.getType();

    public CTSingleXmlCells getSingleXmlCells();

    public void setSingleXmlCells(CTSingleXmlCells var1);

    public CTSingleXmlCells addNewSingleXmlCells();
}


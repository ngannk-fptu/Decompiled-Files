/*
 * Decompiled with CFR 0.152.
 */
package org.openxmlformats.schemas.drawingml.x2006.spreadsheetDrawing;

import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.openxmlformats.schemas.drawingml.x2006.main.STCoordinate;
import org.openxmlformats.schemas.drawingml.x2006.spreadsheetDrawing.STColID;
import org.openxmlformats.schemas.drawingml.x2006.spreadsheetDrawing.STRowID;

public interface CTMarker
extends XmlObject {
    public static final DocumentFactory<CTMarker> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "ctmarkeree8etype");
    public static final SchemaType type = Factory.getType();

    public int getCol();

    public STColID xgetCol();

    public void setCol(int var1);

    public void xsetCol(STColID var1);

    public Object getColOff();

    public STCoordinate xgetColOff();

    public void setColOff(Object var1);

    public void xsetColOff(STCoordinate var1);

    public int getRow();

    public STRowID xgetRow();

    public void setRow(int var1);

    public void xsetRow(STRowID var1);

    public Object getRowOff();

    public STCoordinate xgetRowOff();

    public void setRowOff(Object var1);

    public void xsetRowOff(STCoordinate var1);
}


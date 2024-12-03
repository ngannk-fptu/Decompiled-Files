/*
 * Decompiled with CFR 0.152.
 */
package org.openxmlformats.schemas.drawingml.x2006.spreadsheetDrawing;

import java.util.List;
import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.openxmlformats.schemas.drawingml.x2006.spreadsheetDrawing.CTAbsoluteAnchor;
import org.openxmlformats.schemas.drawingml.x2006.spreadsheetDrawing.CTOneCellAnchor;
import org.openxmlformats.schemas.drawingml.x2006.spreadsheetDrawing.CTTwoCellAnchor;

public interface CTDrawing
extends XmlObject {
    public static final DocumentFactory<CTDrawing> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "ctdrawing2748type");
    public static final SchemaType type = Factory.getType();

    public List<CTTwoCellAnchor> getTwoCellAnchorList();

    public CTTwoCellAnchor[] getTwoCellAnchorArray();

    public CTTwoCellAnchor getTwoCellAnchorArray(int var1);

    public int sizeOfTwoCellAnchorArray();

    public void setTwoCellAnchorArray(CTTwoCellAnchor[] var1);

    public void setTwoCellAnchorArray(int var1, CTTwoCellAnchor var2);

    public CTTwoCellAnchor insertNewTwoCellAnchor(int var1);

    public CTTwoCellAnchor addNewTwoCellAnchor();

    public void removeTwoCellAnchor(int var1);

    public List<CTOneCellAnchor> getOneCellAnchorList();

    public CTOneCellAnchor[] getOneCellAnchorArray();

    public CTOneCellAnchor getOneCellAnchorArray(int var1);

    public int sizeOfOneCellAnchorArray();

    public void setOneCellAnchorArray(CTOneCellAnchor[] var1);

    public void setOneCellAnchorArray(int var1, CTOneCellAnchor var2);

    public CTOneCellAnchor insertNewOneCellAnchor(int var1);

    public CTOneCellAnchor addNewOneCellAnchor();

    public void removeOneCellAnchor(int var1);

    public List<CTAbsoluteAnchor> getAbsoluteAnchorList();

    public CTAbsoluteAnchor[] getAbsoluteAnchorArray();

    public CTAbsoluteAnchor getAbsoluteAnchorArray(int var1);

    public int sizeOfAbsoluteAnchorArray();

    public void setAbsoluteAnchorArray(CTAbsoluteAnchor[] var1);

    public void setAbsoluteAnchorArray(int var1, CTAbsoluteAnchor var2);

    public CTAbsoluteAnchor insertNewAbsoluteAnchor(int var1);

    public CTAbsoluteAnchor addNewAbsoluteAnchor();

    public void removeAbsoluteAnchor(int var1);
}


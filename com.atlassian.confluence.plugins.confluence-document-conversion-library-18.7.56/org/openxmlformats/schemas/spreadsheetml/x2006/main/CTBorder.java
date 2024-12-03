/*
 * Decompiled with CFR 0.152.
 */
package org.openxmlformats.schemas.spreadsheetml.x2006.main;

import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlBoolean;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTBorderPr;

public interface CTBorder
extends XmlObject {
    public static final DocumentFactory<CTBorder> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "ctborderf935type");
    public static final SchemaType type = Factory.getType();

    public CTBorderPr getStart();

    public boolean isSetStart();

    public void setStart(CTBorderPr var1);

    public CTBorderPr addNewStart();

    public void unsetStart();

    public CTBorderPr getEnd();

    public boolean isSetEnd();

    public void setEnd(CTBorderPr var1);

    public CTBorderPr addNewEnd();

    public void unsetEnd();

    public CTBorderPr getLeft();

    public boolean isSetLeft();

    public void setLeft(CTBorderPr var1);

    public CTBorderPr addNewLeft();

    public void unsetLeft();

    public CTBorderPr getRight();

    public boolean isSetRight();

    public void setRight(CTBorderPr var1);

    public CTBorderPr addNewRight();

    public void unsetRight();

    public CTBorderPr getTop();

    public boolean isSetTop();

    public void setTop(CTBorderPr var1);

    public CTBorderPr addNewTop();

    public void unsetTop();

    public CTBorderPr getBottom();

    public boolean isSetBottom();

    public void setBottom(CTBorderPr var1);

    public CTBorderPr addNewBottom();

    public void unsetBottom();

    public CTBorderPr getDiagonal();

    public boolean isSetDiagonal();

    public void setDiagonal(CTBorderPr var1);

    public CTBorderPr addNewDiagonal();

    public void unsetDiagonal();

    public CTBorderPr getVertical();

    public boolean isSetVertical();

    public void setVertical(CTBorderPr var1);

    public CTBorderPr addNewVertical();

    public void unsetVertical();

    public CTBorderPr getHorizontal();

    public boolean isSetHorizontal();

    public void setHorizontal(CTBorderPr var1);

    public CTBorderPr addNewHorizontal();

    public void unsetHorizontal();

    public boolean getDiagonalUp();

    public XmlBoolean xgetDiagonalUp();

    public boolean isSetDiagonalUp();

    public void setDiagonalUp(boolean var1);

    public void xsetDiagonalUp(XmlBoolean var1);

    public void unsetDiagonalUp();

    public boolean getDiagonalDown();

    public XmlBoolean xgetDiagonalDown();

    public boolean isSetDiagonalDown();

    public void setDiagonalDown(boolean var1);

    public void xsetDiagonalDown(XmlBoolean var1);

    public void unsetDiagonalDown();

    public boolean getOutline();

    public XmlBoolean xgetOutline();

    public boolean isSetOutline();

    public void setOutline(boolean var1);

    public void xsetOutline(XmlBoolean var1);

    public void unsetOutline();
}


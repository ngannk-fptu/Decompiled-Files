/*
 * Decompiled with CFR 0.152.
 */
package org.openxmlformats.schemas.wordprocessingml.x2006.main;

import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTBorder;

public interface CTTcBorders
extends XmlObject {
    public static final DocumentFactory<CTTcBorders> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "cttcbordersa5fatype");
    public static final SchemaType type = Factory.getType();

    public CTBorder getTop();

    public boolean isSetTop();

    public void setTop(CTBorder var1);

    public CTBorder addNewTop();

    public void unsetTop();

    public CTBorder getStart();

    public boolean isSetStart();

    public void setStart(CTBorder var1);

    public CTBorder addNewStart();

    public void unsetStart();

    public CTBorder getLeft();

    public boolean isSetLeft();

    public void setLeft(CTBorder var1);

    public CTBorder addNewLeft();

    public void unsetLeft();

    public CTBorder getBottom();

    public boolean isSetBottom();

    public void setBottom(CTBorder var1);

    public CTBorder addNewBottom();

    public void unsetBottom();

    public CTBorder getEnd();

    public boolean isSetEnd();

    public void setEnd(CTBorder var1);

    public CTBorder addNewEnd();

    public void unsetEnd();

    public CTBorder getRight();

    public boolean isSetRight();

    public void setRight(CTBorder var1);

    public CTBorder addNewRight();

    public void unsetRight();

    public CTBorder getInsideH();

    public boolean isSetInsideH();

    public void setInsideH(CTBorder var1);

    public CTBorder addNewInsideH();

    public void unsetInsideH();

    public CTBorder getInsideV();

    public boolean isSetInsideV();

    public void setInsideV(CTBorder var1);

    public CTBorder addNewInsideV();

    public void unsetInsideV();

    public CTBorder getTl2Br();

    public boolean isSetTl2Br();

    public void setTl2Br(CTBorder var1);

    public CTBorder addNewTl2Br();

    public void unsetTl2Br();

    public CTBorder getTr2Bl();

    public boolean isSetTr2Bl();

    public void setTr2Bl(CTBorder var1);

    public CTBorder addNewTr2Bl();

    public void unsetTr2Bl();
}


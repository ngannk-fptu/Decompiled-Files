/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.openxmlformats.schemas.wordprocessingml.x2006.main.STPageBorderZOrder
 */
package org.openxmlformats.schemas.wordprocessingml.x2006.main;

import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTBottomPageBorder;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTPageBorder;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTopPageBorder;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STPageBorderDisplay;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STPageBorderOffset;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STPageBorderZOrder;

public interface CTPageBorders
extends XmlObject {
    public static final DocumentFactory<CTPageBorders> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "ctpagebordersa4datype");
    public static final SchemaType type = Factory.getType();

    public CTTopPageBorder getTop();

    public boolean isSetTop();

    public void setTop(CTTopPageBorder var1);

    public CTTopPageBorder addNewTop();

    public void unsetTop();

    public CTPageBorder getLeft();

    public boolean isSetLeft();

    public void setLeft(CTPageBorder var1);

    public CTPageBorder addNewLeft();

    public void unsetLeft();

    public CTBottomPageBorder getBottom();

    public boolean isSetBottom();

    public void setBottom(CTBottomPageBorder var1);

    public CTBottomPageBorder addNewBottom();

    public void unsetBottom();

    public CTPageBorder getRight();

    public boolean isSetRight();

    public void setRight(CTPageBorder var1);

    public CTPageBorder addNewRight();

    public void unsetRight();

    public STPageBorderZOrder.Enum getZOrder();

    public STPageBorderZOrder xgetZOrder();

    public boolean isSetZOrder();

    public void setZOrder(STPageBorderZOrder.Enum var1);

    public void xsetZOrder(STPageBorderZOrder var1);

    public void unsetZOrder();

    public STPageBorderDisplay.Enum getDisplay();

    public STPageBorderDisplay xgetDisplay();

    public boolean isSetDisplay();

    public void setDisplay(STPageBorderDisplay.Enum var1);

    public void xsetDisplay(STPageBorderDisplay var1);

    public void unsetDisplay();

    public STPageBorderOffset.Enum getOffsetFrom();

    public STPageBorderOffset xgetOffsetFrom();

    public boolean isSetOffsetFrom();

    public void setOffsetFrom(STPageBorderOffset.Enum var1);

    public void xsetOffsetFrom(STPageBorderOffset var1);

    public void unsetOffsetFrom();
}


/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.openxmlformats.schemas.drawingml.x2006.wordprocessingDrawing.STAlignH
 */
package org.openxmlformats.schemas.drawingml.x2006.wordprocessingDrawing;

import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.openxmlformats.schemas.drawingml.x2006.wordprocessingDrawing.STAlignH;
import org.openxmlformats.schemas.drawingml.x2006.wordprocessingDrawing.STPositionOffset;
import org.openxmlformats.schemas.drawingml.x2006.wordprocessingDrawing.STRelFromH;

public interface CTPosH
extends XmlObject {
    public static final DocumentFactory<CTPosH> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "ctposh7fabtype");
    public static final SchemaType type = Factory.getType();

    public STAlignH.Enum getAlign();

    public STAlignH xgetAlign();

    public boolean isSetAlign();

    public void setAlign(STAlignH.Enum var1);

    public void xsetAlign(STAlignH var1);

    public void unsetAlign();

    public int getPosOffset();

    public STPositionOffset xgetPosOffset();

    public boolean isSetPosOffset();

    public void setPosOffset(int var1);

    public void xsetPosOffset(STPositionOffset var1);

    public void unsetPosOffset();

    public STRelFromH.Enum getRelativeFrom();

    public STRelFromH xgetRelativeFrom();

    public void setRelativeFrom(STRelFromH.Enum var1);

    public void xsetRelativeFrom(STRelFromH var1);
}


/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.openxmlformats.schemas.drawingml.x2006.wordprocessingDrawing.STAlignV
 */
package org.openxmlformats.schemas.drawingml.x2006.wordprocessingDrawing;

import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.openxmlformats.schemas.drawingml.x2006.wordprocessingDrawing.STAlignV;
import org.openxmlformats.schemas.drawingml.x2006.wordprocessingDrawing.STPositionOffset;
import org.openxmlformats.schemas.drawingml.x2006.wordprocessingDrawing.STRelFromV;

public interface CTPosV
extends XmlObject {
    public static final DocumentFactory<CTPosV> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "ctposv63ddtype");
    public static final SchemaType type = Factory.getType();

    public STAlignV.Enum getAlign();

    public STAlignV xgetAlign();

    public boolean isSetAlign();

    public void setAlign(STAlignV.Enum var1);

    public void xsetAlign(STAlignV var1);

    public void unsetAlign();

    public int getPosOffset();

    public STPositionOffset xgetPosOffset();

    public boolean isSetPosOffset();

    public void setPosOffset(int var1);

    public void xsetPosOffset(STPositionOffset var1);

    public void unsetPosOffset();

    public STRelFromV.Enum getRelativeFrom();

    public STRelFromV xgetRelativeFrom();

    public void setRelativeFrom(STRelFromV.Enum var1);

    public void xsetRelativeFrom(STRelFromV var1);
}


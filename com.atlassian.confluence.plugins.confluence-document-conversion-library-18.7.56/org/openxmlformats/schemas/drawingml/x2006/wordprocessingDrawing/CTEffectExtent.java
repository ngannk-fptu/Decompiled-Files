/*
 * Decompiled with CFR 0.152.
 */
package org.openxmlformats.schemas.drawingml.x2006.wordprocessingDrawing;

import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.openxmlformats.schemas.drawingml.x2006.main.STCoordinate;

public interface CTEffectExtent
extends XmlObject {
    public static final DocumentFactory<CTEffectExtent> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "cteffectextent9724type");
    public static final SchemaType type = Factory.getType();

    public Object getL();

    public STCoordinate xgetL();

    public void setL(Object var1);

    public void xsetL(STCoordinate var1);

    public Object getT();

    public STCoordinate xgetT();

    public void setT(Object var1);

    public void xsetT(STCoordinate var1);

    public Object getR();

    public STCoordinate xgetR();

    public void setR(Object var1);

    public void xsetR(STCoordinate var1);

    public Object getB();

    public STCoordinate xgetB();

    public void setB(Object var1);

    public void xsetB(STCoordinate var1);
}


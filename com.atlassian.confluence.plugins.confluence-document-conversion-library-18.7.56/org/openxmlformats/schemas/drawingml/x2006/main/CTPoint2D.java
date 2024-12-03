/*
 * Decompiled with CFR 0.152.
 */
package org.openxmlformats.schemas.drawingml.x2006.main;

import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.openxmlformats.schemas.drawingml.x2006.main.STCoordinate;

public interface CTPoint2D
extends XmlObject {
    public static final DocumentFactory<CTPoint2D> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "ctpoint2d8193type");
    public static final SchemaType type = Factory.getType();

    public Object getX();

    public STCoordinate xgetX();

    public void setX(Object var1);

    public void xsetX(STCoordinate var1);

    public Object getY();

    public STCoordinate xgetY();

    public void setY(Object var1);

    public void xsetY(STCoordinate var1);
}


/*
 * Decompiled with CFR 0.152.
 */
package org.openxmlformats.schemas.drawingml.x2006.main;

import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.openxmlformats.schemas.drawingml.x2006.main.STAdjCoordinate;

public interface CTAdjPoint2D
extends XmlObject {
    public static final DocumentFactory<CTAdjPoint2D> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "ctadjpoint2d1656type");
    public static final SchemaType type = Factory.getType();

    public Object getX();

    public STAdjCoordinate xgetX();

    public void setX(Object var1);

    public void xsetX(STAdjCoordinate var1);

    public Object getY();

    public STAdjCoordinate xgetY();

    public void setY(Object var1);

    public void xsetY(STAdjCoordinate var1);
}


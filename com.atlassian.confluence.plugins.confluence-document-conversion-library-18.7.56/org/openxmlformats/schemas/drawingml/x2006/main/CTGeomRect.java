/*
 * Decompiled with CFR 0.152.
 */
package org.openxmlformats.schemas.drawingml.x2006.main;

import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.openxmlformats.schemas.drawingml.x2006.main.STAdjCoordinate;

public interface CTGeomRect
extends XmlObject {
    public static final DocumentFactory<CTGeomRect> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "ctgeomrect53dbtype");
    public static final SchemaType type = Factory.getType();

    public Object getL();

    public STAdjCoordinate xgetL();

    public void setL(Object var1);

    public void xsetL(STAdjCoordinate var1);

    public Object getT();

    public STAdjCoordinate xgetT();

    public void setT(Object var1);

    public void xsetT(STAdjCoordinate var1);

    public Object getR();

    public STAdjCoordinate xgetR();

    public void setR(Object var1);

    public void xsetR(STAdjCoordinate var1);

    public Object getB();

    public STAdjCoordinate xgetB();

    public void setB(Object var1);

    public void xsetB(STAdjCoordinate var1);
}


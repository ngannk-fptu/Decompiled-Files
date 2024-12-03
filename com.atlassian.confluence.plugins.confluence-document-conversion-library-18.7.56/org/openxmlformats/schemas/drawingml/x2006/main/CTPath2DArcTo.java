/*
 * Decompiled with CFR 0.152.
 */
package org.openxmlformats.schemas.drawingml.x2006.main;

import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.openxmlformats.schemas.drawingml.x2006.main.STAdjAngle;
import org.openxmlformats.schemas.drawingml.x2006.main.STAdjCoordinate;

public interface CTPath2DArcTo
extends XmlObject {
    public static final DocumentFactory<CTPath2DArcTo> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "ctpath2darctodaa7type");
    public static final SchemaType type = Factory.getType();

    public Object getWR();

    public STAdjCoordinate xgetWR();

    public void setWR(Object var1);

    public void xsetWR(STAdjCoordinate var1);

    public Object getHR();

    public STAdjCoordinate xgetHR();

    public void setHR(Object var1);

    public void xsetHR(STAdjCoordinate var1);

    public Object getStAng();

    public STAdjAngle xgetStAng();

    public void setStAng(Object var1);

    public void xsetStAng(STAdjAngle var1);

    public Object getSwAng();

    public STAdjAngle xgetSwAng();

    public void setSwAng(Object var1);

    public void xsetSwAng(STAdjAngle var1);
}


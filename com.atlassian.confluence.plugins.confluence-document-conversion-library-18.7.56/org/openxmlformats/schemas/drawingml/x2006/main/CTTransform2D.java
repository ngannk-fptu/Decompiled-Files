/*
 * Decompiled with CFR 0.152.
 */
package org.openxmlformats.schemas.drawingml.x2006.main;

import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlBoolean;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.openxmlformats.schemas.drawingml.x2006.main.CTPoint2D;
import org.openxmlformats.schemas.drawingml.x2006.main.CTPositiveSize2D;
import org.openxmlformats.schemas.drawingml.x2006.main.STAngle;

public interface CTTransform2D
extends XmlObject {
    public static final DocumentFactory<CTTransform2D> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "cttransform2d5deftype");
    public static final SchemaType type = Factory.getType();

    public CTPoint2D getOff();

    public boolean isSetOff();

    public void setOff(CTPoint2D var1);

    public CTPoint2D addNewOff();

    public void unsetOff();

    public CTPositiveSize2D getExt();

    public boolean isSetExt();

    public void setExt(CTPositiveSize2D var1);

    public CTPositiveSize2D addNewExt();

    public void unsetExt();

    public int getRot();

    public STAngle xgetRot();

    public boolean isSetRot();

    public void setRot(int var1);

    public void xsetRot(STAngle var1);

    public void unsetRot();

    public boolean getFlipH();

    public XmlBoolean xgetFlipH();

    public boolean isSetFlipH();

    public void setFlipH(boolean var1);

    public void xsetFlipH(XmlBoolean var1);

    public void unsetFlipH();

    public boolean getFlipV();

    public XmlBoolean xgetFlipV();

    public boolean isSetFlipV();

    public void setFlipV(boolean var1);

    public void xsetFlipV(XmlBoolean var1);

    public void unsetFlipV();
}


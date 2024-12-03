/*
 * Decompiled with CFR 0.152.
 */
package org.openxmlformats.schemas.drawingml.x2006.main;

import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlBoolean;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.openxmlformats.schemas.drawingml.x2006.main.STPositiveFixedAngle;

public interface CTLinearShadeProperties
extends XmlObject {
    public static final DocumentFactory<CTLinearShadeProperties> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "ctlinearshadeproperties7f0ctype");
    public static final SchemaType type = Factory.getType();

    public int getAng();

    public STPositiveFixedAngle xgetAng();

    public boolean isSetAng();

    public void setAng(int var1);

    public void xsetAng(STPositiveFixedAngle var1);

    public void unsetAng();

    public boolean getScaled();

    public XmlBoolean xgetScaled();

    public boolean isSetScaled();

    public void setScaled(boolean var1);

    public void xsetScaled(XmlBoolean var1);

    public void unsetScaled();
}


/*
 * Decompiled with CFR 0.152.
 */
package org.openxmlformats.schemas.drawingml.x2006.main;

import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.openxmlformats.schemas.drawingml.x2006.main.CTAdjPoint2D;
import org.openxmlformats.schemas.drawingml.x2006.main.STAdjAngle;
import org.openxmlformats.schemas.drawingml.x2006.main.STAdjCoordinate;
import org.openxmlformats.schemas.drawingml.x2006.main.STGeomGuideName;

public interface CTPolarAdjustHandle
extends XmlObject {
    public static final DocumentFactory<CTPolarAdjustHandle> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "ctpolaradjusthandled0a6type");
    public static final SchemaType type = Factory.getType();

    public CTAdjPoint2D getPos();

    public void setPos(CTAdjPoint2D var1);

    public CTAdjPoint2D addNewPos();

    public String getGdRefR();

    public STGeomGuideName xgetGdRefR();

    public boolean isSetGdRefR();

    public void setGdRefR(String var1);

    public void xsetGdRefR(STGeomGuideName var1);

    public void unsetGdRefR();

    public Object getMinR();

    public STAdjCoordinate xgetMinR();

    public boolean isSetMinR();

    public void setMinR(Object var1);

    public void xsetMinR(STAdjCoordinate var1);

    public void unsetMinR();

    public Object getMaxR();

    public STAdjCoordinate xgetMaxR();

    public boolean isSetMaxR();

    public void setMaxR(Object var1);

    public void xsetMaxR(STAdjCoordinate var1);

    public void unsetMaxR();

    public String getGdRefAng();

    public STGeomGuideName xgetGdRefAng();

    public boolean isSetGdRefAng();

    public void setGdRefAng(String var1);

    public void xsetGdRefAng(STGeomGuideName var1);

    public void unsetGdRefAng();

    public Object getMinAng();

    public STAdjAngle xgetMinAng();

    public boolean isSetMinAng();

    public void setMinAng(Object var1);

    public void xsetMinAng(STAdjAngle var1);

    public void unsetMinAng();

    public Object getMaxAng();

    public STAdjAngle xgetMaxAng();

    public boolean isSetMaxAng();

    public void setMaxAng(Object var1);

    public void xsetMaxAng(STAdjAngle var1);

    public void unsetMaxAng();
}


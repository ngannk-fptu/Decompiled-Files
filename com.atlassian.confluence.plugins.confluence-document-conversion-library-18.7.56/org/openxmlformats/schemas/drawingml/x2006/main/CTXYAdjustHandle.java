/*
 * Decompiled with CFR 0.152.
 */
package org.openxmlformats.schemas.drawingml.x2006.main;

import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.openxmlformats.schemas.drawingml.x2006.main.CTAdjPoint2D;
import org.openxmlformats.schemas.drawingml.x2006.main.STAdjCoordinate;
import org.openxmlformats.schemas.drawingml.x2006.main.STGeomGuideName;

public interface CTXYAdjustHandle
extends XmlObject {
    public static final DocumentFactory<CTXYAdjustHandle> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "ctxyadjusthandlefaf3type");
    public static final SchemaType type = Factory.getType();

    public CTAdjPoint2D getPos();

    public void setPos(CTAdjPoint2D var1);

    public CTAdjPoint2D addNewPos();

    public String getGdRefX();

    public STGeomGuideName xgetGdRefX();

    public boolean isSetGdRefX();

    public void setGdRefX(String var1);

    public void xsetGdRefX(STGeomGuideName var1);

    public void unsetGdRefX();

    public Object getMinX();

    public STAdjCoordinate xgetMinX();

    public boolean isSetMinX();

    public void setMinX(Object var1);

    public void xsetMinX(STAdjCoordinate var1);

    public void unsetMinX();

    public Object getMaxX();

    public STAdjCoordinate xgetMaxX();

    public boolean isSetMaxX();

    public void setMaxX(Object var1);

    public void xsetMaxX(STAdjCoordinate var1);

    public void unsetMaxX();

    public String getGdRefY();

    public STGeomGuideName xgetGdRefY();

    public boolean isSetGdRefY();

    public void setGdRefY(String var1);

    public void xsetGdRefY(STGeomGuideName var1);

    public void unsetGdRefY();

    public Object getMinY();

    public STAdjCoordinate xgetMinY();

    public boolean isSetMinY();

    public void setMinY(Object var1);

    public void xsetMinY(STAdjCoordinate var1);

    public void unsetMinY();

    public Object getMaxY();

    public STAdjCoordinate xgetMaxY();

    public boolean isSetMaxY();

    public void setMaxY(Object var1);

    public void xsetMaxY(STAdjCoordinate var1);

    public void unsetMaxY();
}


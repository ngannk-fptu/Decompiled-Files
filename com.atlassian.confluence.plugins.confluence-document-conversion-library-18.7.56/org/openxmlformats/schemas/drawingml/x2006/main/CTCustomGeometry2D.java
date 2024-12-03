/*
 * Decompiled with CFR 0.152.
 */
package org.openxmlformats.schemas.drawingml.x2006.main;

import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.openxmlformats.schemas.drawingml.x2006.main.CTAdjustHandleList;
import org.openxmlformats.schemas.drawingml.x2006.main.CTConnectionSiteList;
import org.openxmlformats.schemas.drawingml.x2006.main.CTGeomGuideList;
import org.openxmlformats.schemas.drawingml.x2006.main.CTGeomRect;
import org.openxmlformats.schemas.drawingml.x2006.main.CTPath2DList;

public interface CTCustomGeometry2D
extends XmlObject {
    public static final DocumentFactory<CTCustomGeometry2D> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "ctcustomgeometry2dca70type");
    public static final SchemaType type = Factory.getType();

    public CTGeomGuideList getAvLst();

    public boolean isSetAvLst();

    public void setAvLst(CTGeomGuideList var1);

    public CTGeomGuideList addNewAvLst();

    public void unsetAvLst();

    public CTGeomGuideList getGdLst();

    public boolean isSetGdLst();

    public void setGdLst(CTGeomGuideList var1);

    public CTGeomGuideList addNewGdLst();

    public void unsetGdLst();

    public CTAdjustHandleList getAhLst();

    public boolean isSetAhLst();

    public void setAhLst(CTAdjustHandleList var1);

    public CTAdjustHandleList addNewAhLst();

    public void unsetAhLst();

    public CTConnectionSiteList getCxnLst();

    public boolean isSetCxnLst();

    public void setCxnLst(CTConnectionSiteList var1);

    public CTConnectionSiteList addNewCxnLst();

    public void unsetCxnLst();

    public CTGeomRect getRect();

    public boolean isSetRect();

    public void setRect(CTGeomRect var1);

    public CTGeomRect addNewRect();

    public void unsetRect();

    public CTPath2DList getPathLst();

    public void setPathLst(CTPath2DList var1);

    public CTPath2DList addNewPathLst();
}


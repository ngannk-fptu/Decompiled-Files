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

public interface CTConnectionSite
extends XmlObject {
    public static final DocumentFactory<CTConnectionSite> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "ctconnectionsite6660type");
    public static final SchemaType type = Factory.getType();

    public CTAdjPoint2D getPos();

    public void setPos(CTAdjPoint2D var1);

    public CTAdjPoint2D addNewPos();

    public Object getAng();

    public STAdjAngle xgetAng();

    public void setAng(Object var1);

    public void xsetAng(STAdjAngle var1);
}


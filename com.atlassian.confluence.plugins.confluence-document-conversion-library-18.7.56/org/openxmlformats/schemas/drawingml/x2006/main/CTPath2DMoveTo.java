/*
 * Decompiled with CFR 0.152.
 */
package org.openxmlformats.schemas.drawingml.x2006.main;

import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.openxmlformats.schemas.drawingml.x2006.main.CTAdjPoint2D;

public interface CTPath2DMoveTo
extends XmlObject {
    public static final DocumentFactory<CTPath2DMoveTo> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "ctpath2dmovetoa01etype");
    public static final SchemaType type = Factory.getType();

    public CTAdjPoint2D getPt();

    public void setPt(CTAdjPoint2D var1);

    public CTAdjPoint2D addNewPt();
}


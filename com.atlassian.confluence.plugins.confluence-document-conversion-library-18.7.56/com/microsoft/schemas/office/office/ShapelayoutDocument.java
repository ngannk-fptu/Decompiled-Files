/*
 * Decompiled with CFR 0.152.
 */
package com.microsoft.schemas.office.office;

import com.microsoft.schemas.office.office.CTShapeLayout;
import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.impl.schema.DocumentFactory;

public interface ShapelayoutDocument
extends XmlObject {
    public static final DocumentFactory<ShapelayoutDocument> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "shapelayoutebb0doctype");
    public static final SchemaType type = Factory.getType();

    public CTShapeLayout getShapelayout();

    public void setShapelayout(CTShapeLayout var1);

    public CTShapeLayout addNewShapelayout();
}


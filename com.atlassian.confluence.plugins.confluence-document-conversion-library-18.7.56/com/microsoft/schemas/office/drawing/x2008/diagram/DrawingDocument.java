/*
 * Decompiled with CFR 0.152.
 */
package com.microsoft.schemas.office.drawing.x2008.diagram;

import com.microsoft.schemas.office.drawing.x2008.diagram.CTDrawing;
import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.impl.schema.DocumentFactory;

public interface DrawingDocument
extends XmlObject {
    public static final DocumentFactory<DrawingDocument> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "drawing324ddoctype");
    public static final SchemaType type = Factory.getType();

    public CTDrawing getDrawing();

    public void setDrawing(CTDrawing var1);

    public CTDrawing addNewDrawing();
}


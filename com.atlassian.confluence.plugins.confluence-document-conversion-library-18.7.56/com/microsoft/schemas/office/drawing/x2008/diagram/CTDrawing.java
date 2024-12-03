/*
 * Decompiled with CFR 0.152.
 */
package com.microsoft.schemas.office.drawing.x2008.diagram;

import com.microsoft.schemas.office.drawing.x2008.diagram.CTGroupShape;
import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.impl.schema.DocumentFactory;

public interface CTDrawing
extends XmlObject {
    public static final DocumentFactory<CTDrawing> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "ctdrawingc4f9type");
    public static final SchemaType type = Factory.getType();

    public CTGroupShape getSpTree();

    public void setSpTree(CTGroupShape var1);

    public CTGroupShape addNewSpTree();
}


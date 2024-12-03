/*
 * Decompiled with CFR 0.152.
 */
package org.openxmlformats.schemas.drawingml.x2006.spreadsheetDrawing;

import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.openxmlformats.schemas.drawingml.x2006.main.CTNonVisualConnectorProperties;
import org.openxmlformats.schemas.drawingml.x2006.main.CTNonVisualDrawingProps;

public interface CTConnectorNonVisual
extends XmlObject {
    public static final DocumentFactory<CTConnectorNonVisual> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "ctconnectornonvisual1a74type");
    public static final SchemaType type = Factory.getType();

    public CTNonVisualDrawingProps getCNvPr();

    public void setCNvPr(CTNonVisualDrawingProps var1);

    public CTNonVisualDrawingProps addNewCNvPr();

    public CTNonVisualConnectorProperties getCNvCxnSpPr();

    public void setCNvCxnSpPr(CTNonVisualConnectorProperties var1);

    public CTNonVisualConnectorProperties addNewCNvCxnSpPr();
}


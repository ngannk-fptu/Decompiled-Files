/*
 * Decompiled with CFR 0.152.
 */
package org.openxmlformats.schemas.spreadsheetml.x2006.main;

import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTWorksheet;

public interface WorksheetDocument
extends XmlObject {
    public static final DocumentFactory<WorksheetDocument> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "worksheetf539doctype");
    public static final SchemaType type = Factory.getType();

    public CTWorksheet getWorksheet();

    public void setWorksheet(CTWorksheet var1);

    public CTWorksheet addNewWorksheet();
}


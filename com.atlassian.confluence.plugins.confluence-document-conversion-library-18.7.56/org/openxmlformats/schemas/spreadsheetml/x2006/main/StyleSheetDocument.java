/*
 * Decompiled with CFR 0.152.
 */
package org.openxmlformats.schemas.spreadsheetml.x2006.main;

import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTStylesheet;

public interface StyleSheetDocument
extends XmlObject {
    public static final DocumentFactory<StyleSheetDocument> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "stylesheet5d8bdoctype");
    public static final SchemaType type = Factory.getType();

    public CTStylesheet getStyleSheet();

    public void setStyleSheet(CTStylesheet var1);

    public CTStylesheet addNewStyleSheet();
}


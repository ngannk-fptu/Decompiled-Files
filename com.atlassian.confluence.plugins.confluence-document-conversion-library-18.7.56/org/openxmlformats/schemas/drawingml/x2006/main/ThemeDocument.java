/*
 * Decompiled with CFR 0.152.
 */
package org.openxmlformats.schemas.drawingml.x2006.main;

import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.openxmlformats.schemas.drawingml.x2006.main.CTOfficeStyleSheet;

public interface ThemeDocument
extends XmlObject {
    public static final DocumentFactory<ThemeDocument> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "themefd26doctype");
    public static final SchemaType type = Factory.getType();

    public CTOfficeStyleSheet getTheme();

    public void setTheme(CTOfficeStyleSheet var1);

    public CTOfficeStyleSheet addNewTheme();
}


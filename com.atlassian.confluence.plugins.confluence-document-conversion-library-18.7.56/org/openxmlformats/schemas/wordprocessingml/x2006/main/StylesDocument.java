/*
 * Decompiled with CFR 0.152.
 */
package org.openxmlformats.schemas.wordprocessingml.x2006.main;

import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTStyles;

public interface StylesDocument
extends XmlObject {
    public static final DocumentFactory<StylesDocument> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "styles2732doctype");
    public static final SchemaType type = Factory.getType();

    public CTStyles getStyles();

    public void setStyles(CTStyles var1);

    public CTStyles addNewStyles();
}


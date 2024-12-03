/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.openxmlformats.schemas.spreadsheetml.x2006.main.CTMRUColors
 */
package org.openxmlformats.schemas.spreadsheetml.x2006.main;

import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTIndexedColors;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTMRUColors;

public interface CTColors
extends XmlObject {
    public static final DocumentFactory<CTColors> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "ctcolors6579type");
    public static final SchemaType type = Factory.getType();

    public CTIndexedColors getIndexedColors();

    public boolean isSetIndexedColors();

    public void setIndexedColors(CTIndexedColors var1);

    public CTIndexedColors addNewIndexedColors();

    public void unsetIndexedColors();

    public CTMRUColors getMruColors();

    public boolean isSetMruColors();

    public void setMruColors(CTMRUColors var1);

    public CTMRUColors addNewMruColors();

    public void unsetMruColors();
}


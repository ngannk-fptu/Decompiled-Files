/*
 * Decompiled with CFR 0.152.
 */
package com.microsoft.schemas.office.visio.x2012.main;

import com.microsoft.schemas.office.visio.x2012.main.StyleSheetType;
import java.util.List;
import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.impl.schema.DocumentFactory;

public interface StyleSheetsType
extends XmlObject {
    public static final DocumentFactory<StyleSheetsType> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "stylesheetstypeb706type");
    public static final SchemaType type = Factory.getType();

    public List<StyleSheetType> getStyleSheetList();

    public StyleSheetType[] getStyleSheetArray();

    public StyleSheetType getStyleSheetArray(int var1);

    public int sizeOfStyleSheetArray();

    public void setStyleSheetArray(StyleSheetType[] var1);

    public void setStyleSheetArray(int var1, StyleSheetType var2);

    public StyleSheetType insertNewStyleSheet(int var1);

    public StyleSheetType addNewStyleSheet();

    public void removeStyleSheet(int var1);
}


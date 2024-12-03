/*
 * Decompiled with CFR 0.152.
 */
package org.openxmlformats.schemas.spreadsheetml.x2006.main;

import java.util.List;
import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTRgbColor;

public interface CTIndexedColors
extends XmlObject {
    public static final DocumentFactory<CTIndexedColors> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "ctindexedcolorsa0a0type");
    public static final SchemaType type = Factory.getType();

    public List<CTRgbColor> getRgbColorList();

    public CTRgbColor[] getRgbColorArray();

    public CTRgbColor getRgbColorArray(int var1);

    public int sizeOfRgbColorArray();

    public void setRgbColorArray(CTRgbColor[] var1);

    public void setRgbColorArray(int var1, CTRgbColor var2);

    public CTRgbColor insertNewRgbColor(int var1);

    public CTRgbColor addNewRgbColor();

    public void removeRgbColor(int var1);
}


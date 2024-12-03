/*
 * Decompiled with CFR 0.152.
 */
package org.openxmlformats.schemas.presentationml.x2006.main;

import java.util.List;
import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.openxmlformats.schemas.presentationml.x2006.main.CTEmbeddedFontListEntry;

public interface CTEmbeddedFontList
extends XmlObject {
    public static final DocumentFactory<CTEmbeddedFontList> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "ctembeddedfontlist240etype");
    public static final SchemaType type = Factory.getType();

    public List<CTEmbeddedFontListEntry> getEmbeddedFontList();

    public CTEmbeddedFontListEntry[] getEmbeddedFontArray();

    public CTEmbeddedFontListEntry getEmbeddedFontArray(int var1);

    public int sizeOfEmbeddedFontArray();

    public void setEmbeddedFontArray(CTEmbeddedFontListEntry[] var1);

    public void setEmbeddedFontArray(int var1, CTEmbeddedFontListEntry var2);

    public CTEmbeddedFontListEntry insertNewEmbeddedFont(int var1);

    public CTEmbeddedFontListEntry addNewEmbeddedFont();

    public void removeEmbeddedFont(int var1);
}


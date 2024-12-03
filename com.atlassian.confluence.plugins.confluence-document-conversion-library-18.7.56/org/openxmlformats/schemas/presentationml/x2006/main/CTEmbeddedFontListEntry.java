/*
 * Decompiled with CFR 0.152.
 */
package org.openxmlformats.schemas.presentationml.x2006.main;

import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.openxmlformats.schemas.drawingml.x2006.main.CTTextFont;
import org.openxmlformats.schemas.presentationml.x2006.main.CTEmbeddedFontDataId;

public interface CTEmbeddedFontListEntry
extends XmlObject {
    public static final DocumentFactory<CTEmbeddedFontListEntry> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "ctembeddedfontlistentry48b4type");
    public static final SchemaType type = Factory.getType();

    public CTTextFont getFont();

    public void setFont(CTTextFont var1);

    public CTTextFont addNewFont();

    public CTEmbeddedFontDataId getRegular();

    public boolean isSetRegular();

    public void setRegular(CTEmbeddedFontDataId var1);

    public CTEmbeddedFontDataId addNewRegular();

    public void unsetRegular();

    public CTEmbeddedFontDataId getBold();

    public boolean isSetBold();

    public void setBold(CTEmbeddedFontDataId var1);

    public CTEmbeddedFontDataId addNewBold();

    public void unsetBold();

    public CTEmbeddedFontDataId getItalic();

    public boolean isSetItalic();

    public void setItalic(CTEmbeddedFontDataId var1);

    public CTEmbeddedFontDataId addNewItalic();

    public void unsetItalic();

    public CTEmbeddedFontDataId getBoldItalic();

    public boolean isSetBoldItalic();

    public void setBoldItalic(CTEmbeddedFontDataId var1);

    public CTEmbeddedFontDataId addNewBoldItalic();

    public void unsetBoldItalic();
}


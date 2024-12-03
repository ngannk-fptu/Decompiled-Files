/*
 * Decompiled with CFR 0.152.
 */
package org.openxmlformats.schemas.drawingml.x2006.main;

import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.openxmlformats.schemas.drawingml.x2006.main.CTColorScheme;
import org.openxmlformats.schemas.drawingml.x2006.main.CTFontScheme;
import org.openxmlformats.schemas.drawingml.x2006.main.CTOfficeArtExtensionList;
import org.openxmlformats.schemas.drawingml.x2006.main.CTStyleMatrix;

public interface CTBaseStyles
extends XmlObject {
    public static final DocumentFactory<CTBaseStyles> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "ctbasestyles122etype");
    public static final SchemaType type = Factory.getType();

    public CTColorScheme getClrScheme();

    public void setClrScheme(CTColorScheme var1);

    public CTColorScheme addNewClrScheme();

    public CTFontScheme getFontScheme();

    public void setFontScheme(CTFontScheme var1);

    public CTFontScheme addNewFontScheme();

    public CTStyleMatrix getFmtScheme();

    public void setFmtScheme(CTStyleMatrix var1);

    public CTStyleMatrix addNewFmtScheme();

    public CTOfficeArtExtensionList getExtLst();

    public boolean isSetExtLst();

    public void setExtLst(CTOfficeArtExtensionList var1);

    public CTOfficeArtExtensionList addNewExtLst();

    public void unsetExtLst();
}


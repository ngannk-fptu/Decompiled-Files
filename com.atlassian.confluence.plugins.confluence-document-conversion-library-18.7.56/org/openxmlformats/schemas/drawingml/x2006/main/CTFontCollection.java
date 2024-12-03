/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.openxmlformats.schemas.drawingml.x2006.main.CTSupplementalFont
 */
package org.openxmlformats.schemas.drawingml.x2006.main;

import java.util.List;
import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.openxmlformats.schemas.drawingml.x2006.main.CTOfficeArtExtensionList;
import org.openxmlformats.schemas.drawingml.x2006.main.CTSupplementalFont;
import org.openxmlformats.schemas.drawingml.x2006.main.CTTextFont;

public interface CTFontCollection
extends XmlObject {
    public static final DocumentFactory<CTFontCollection> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "ctfontcollectiondd68type");
    public static final SchemaType type = Factory.getType();

    public CTTextFont getLatin();

    public void setLatin(CTTextFont var1);

    public CTTextFont addNewLatin();

    public CTTextFont getEa();

    public void setEa(CTTextFont var1);

    public CTTextFont addNewEa();

    public CTTextFont getCs();

    public void setCs(CTTextFont var1);

    public CTTextFont addNewCs();

    public List<CTSupplementalFont> getFontList();

    public CTSupplementalFont[] getFontArray();

    public CTSupplementalFont getFontArray(int var1);

    public int sizeOfFontArray();

    public void setFontArray(CTSupplementalFont[] var1);

    public void setFontArray(int var1, CTSupplementalFont var2);

    public CTSupplementalFont insertNewFont(int var1);

    public CTSupplementalFont addNewFont();

    public void removeFont(int var1);

    public CTOfficeArtExtensionList getExtLst();

    public boolean isSetExtLst();

    public void setExtLst(CTOfficeArtExtensionList var1);

    public CTOfficeArtExtensionList addNewExtLst();

    public void unsetExtLst();
}


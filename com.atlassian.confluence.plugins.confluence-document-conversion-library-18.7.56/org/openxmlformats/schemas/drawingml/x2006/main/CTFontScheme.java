/*
 * Decompiled with CFR 0.152.
 */
package org.openxmlformats.schemas.drawingml.x2006.main;

import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.XmlString;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.openxmlformats.schemas.drawingml.x2006.main.CTFontCollection;
import org.openxmlformats.schemas.drawingml.x2006.main.CTOfficeArtExtensionList;

public interface CTFontScheme
extends XmlObject {
    public static final DocumentFactory<CTFontScheme> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "ctfontscheme232ftype");
    public static final SchemaType type = Factory.getType();

    public CTFontCollection getMajorFont();

    public void setMajorFont(CTFontCollection var1);

    public CTFontCollection addNewMajorFont();

    public CTFontCollection getMinorFont();

    public void setMinorFont(CTFontCollection var1);

    public CTFontCollection addNewMinorFont();

    public CTOfficeArtExtensionList getExtLst();

    public boolean isSetExtLst();

    public void setExtLst(CTOfficeArtExtensionList var1);

    public CTOfficeArtExtensionList addNewExtLst();

    public void unsetExtLst();

    public String getName();

    public XmlString xgetName();

    public void setName(String var1);

    public void xsetName(XmlString var1);
}


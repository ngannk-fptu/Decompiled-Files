/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.openxmlformats.schemas.drawingml.x2006.main.CTColorSchemeList
 *  org.openxmlformats.schemas.drawingml.x2006.main.CTCustomColorList
 *  org.openxmlformats.schemas.drawingml.x2006.main.CTObjectStyleDefaults
 */
package org.openxmlformats.schemas.drawingml.x2006.main;

import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.XmlString;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.openxmlformats.schemas.drawingml.x2006.main.CTBaseStyles;
import org.openxmlformats.schemas.drawingml.x2006.main.CTColorSchemeList;
import org.openxmlformats.schemas.drawingml.x2006.main.CTCustomColorList;
import org.openxmlformats.schemas.drawingml.x2006.main.CTObjectStyleDefaults;
import org.openxmlformats.schemas.drawingml.x2006.main.CTOfficeArtExtensionList;

public interface CTOfficeStyleSheet
extends XmlObject {
    public static final DocumentFactory<CTOfficeStyleSheet> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "ctofficestylesheetce25type");
    public static final SchemaType type = Factory.getType();

    public CTBaseStyles getThemeElements();

    public void setThemeElements(CTBaseStyles var1);

    public CTBaseStyles addNewThemeElements();

    public CTObjectStyleDefaults getObjectDefaults();

    public boolean isSetObjectDefaults();

    public void setObjectDefaults(CTObjectStyleDefaults var1);

    public CTObjectStyleDefaults addNewObjectDefaults();

    public void unsetObjectDefaults();

    public CTColorSchemeList getExtraClrSchemeLst();

    public boolean isSetExtraClrSchemeLst();

    public void setExtraClrSchemeLst(CTColorSchemeList var1);

    public CTColorSchemeList addNewExtraClrSchemeLst();

    public void unsetExtraClrSchemeLst();

    public CTCustomColorList getCustClrLst();

    public boolean isSetCustClrLst();

    public void setCustClrLst(CTCustomColorList var1);

    public CTCustomColorList addNewCustClrLst();

    public void unsetCustClrLst();

    public CTOfficeArtExtensionList getExtLst();

    public boolean isSetExtLst();

    public void setExtLst(CTOfficeArtExtensionList var1);

    public CTOfficeArtExtensionList addNewExtLst();

    public void unsetExtLst();

    public String getName();

    public XmlString xgetName();

    public boolean isSetName();

    public void setName(String var1);

    public void xsetName(XmlString var1);

    public void unsetName();
}


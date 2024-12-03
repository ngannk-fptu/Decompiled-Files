/*
 * Decompiled with CFR 0.152.
 */
package org.openxmlformats.schemas.drawingml.x2006.main;

import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.openxmlformats.schemas.drawingml.x2006.main.CTOfficeArtExtensionList;
import org.openxmlformats.schemas.drawingml.x2006.main.CTTextParagraphProperties;

public interface CTTextListStyle
extends XmlObject {
    public static final DocumentFactory<CTTextListStyle> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "cttextliststyleab77type");
    public static final SchemaType type = Factory.getType();

    public CTTextParagraphProperties getDefPPr();

    public boolean isSetDefPPr();

    public void setDefPPr(CTTextParagraphProperties var1);

    public CTTextParagraphProperties addNewDefPPr();

    public void unsetDefPPr();

    public CTTextParagraphProperties getLvl1PPr();

    public boolean isSetLvl1PPr();

    public void setLvl1PPr(CTTextParagraphProperties var1);

    public CTTextParagraphProperties addNewLvl1PPr();

    public void unsetLvl1PPr();

    public CTTextParagraphProperties getLvl2PPr();

    public boolean isSetLvl2PPr();

    public void setLvl2PPr(CTTextParagraphProperties var1);

    public CTTextParagraphProperties addNewLvl2PPr();

    public void unsetLvl2PPr();

    public CTTextParagraphProperties getLvl3PPr();

    public boolean isSetLvl3PPr();

    public void setLvl3PPr(CTTextParagraphProperties var1);

    public CTTextParagraphProperties addNewLvl3PPr();

    public void unsetLvl3PPr();

    public CTTextParagraphProperties getLvl4PPr();

    public boolean isSetLvl4PPr();

    public void setLvl4PPr(CTTextParagraphProperties var1);

    public CTTextParagraphProperties addNewLvl4PPr();

    public void unsetLvl4PPr();

    public CTTextParagraphProperties getLvl5PPr();

    public boolean isSetLvl5PPr();

    public void setLvl5PPr(CTTextParagraphProperties var1);

    public CTTextParagraphProperties addNewLvl5PPr();

    public void unsetLvl5PPr();

    public CTTextParagraphProperties getLvl6PPr();

    public boolean isSetLvl6PPr();

    public void setLvl6PPr(CTTextParagraphProperties var1);

    public CTTextParagraphProperties addNewLvl6PPr();

    public void unsetLvl6PPr();

    public CTTextParagraphProperties getLvl7PPr();

    public boolean isSetLvl7PPr();

    public void setLvl7PPr(CTTextParagraphProperties var1);

    public CTTextParagraphProperties addNewLvl7PPr();

    public void unsetLvl7PPr();

    public CTTextParagraphProperties getLvl8PPr();

    public boolean isSetLvl8PPr();

    public void setLvl8PPr(CTTextParagraphProperties var1);

    public CTTextParagraphProperties addNewLvl8PPr();

    public void unsetLvl8PPr();

    public CTTextParagraphProperties getLvl9PPr();

    public boolean isSetLvl9PPr();

    public void setLvl9PPr(CTTextParagraphProperties var1);

    public CTTextParagraphProperties addNewLvl9PPr();

    public void unsetLvl9PPr();

    public CTOfficeArtExtensionList getExtLst();

    public boolean isSetExtLst();

    public void setExtLst(CTOfficeArtExtensionList var1);

    public CTOfficeArtExtensionList addNewExtLst();

    public void unsetExtLst();
}


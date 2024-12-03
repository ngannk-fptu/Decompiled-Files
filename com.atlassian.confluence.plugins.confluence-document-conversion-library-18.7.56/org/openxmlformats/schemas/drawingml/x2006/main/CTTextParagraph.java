/*
 * Decompiled with CFR 0.152.
 */
package org.openxmlformats.schemas.drawingml.x2006.main;

import java.util.List;
import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.openxmlformats.schemas.drawingml.x2006.main.CTRegularTextRun;
import org.openxmlformats.schemas.drawingml.x2006.main.CTTextCharacterProperties;
import org.openxmlformats.schemas.drawingml.x2006.main.CTTextField;
import org.openxmlformats.schemas.drawingml.x2006.main.CTTextLineBreak;
import org.openxmlformats.schemas.drawingml.x2006.main.CTTextParagraphProperties;

public interface CTTextParagraph
extends XmlObject {
    public static final DocumentFactory<CTTextParagraph> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "cttextparagraphcaf2type");
    public static final SchemaType type = Factory.getType();

    public CTTextParagraphProperties getPPr();

    public boolean isSetPPr();

    public void setPPr(CTTextParagraphProperties var1);

    public CTTextParagraphProperties addNewPPr();

    public void unsetPPr();

    public List<CTRegularTextRun> getRList();

    public CTRegularTextRun[] getRArray();

    public CTRegularTextRun getRArray(int var1);

    public int sizeOfRArray();

    public void setRArray(CTRegularTextRun[] var1);

    public void setRArray(int var1, CTRegularTextRun var2);

    public CTRegularTextRun insertNewR(int var1);

    public CTRegularTextRun addNewR();

    public void removeR(int var1);

    public List<CTTextLineBreak> getBrList();

    public CTTextLineBreak[] getBrArray();

    public CTTextLineBreak getBrArray(int var1);

    public int sizeOfBrArray();

    public void setBrArray(CTTextLineBreak[] var1);

    public void setBrArray(int var1, CTTextLineBreak var2);

    public CTTextLineBreak insertNewBr(int var1);

    public CTTextLineBreak addNewBr();

    public void removeBr(int var1);

    public List<CTTextField> getFldList();

    public CTTextField[] getFldArray();

    public CTTextField getFldArray(int var1);

    public int sizeOfFldArray();

    public void setFldArray(CTTextField[] var1);

    public void setFldArray(int var1, CTTextField var2);

    public CTTextField insertNewFld(int var1);

    public CTTextField addNewFld();

    public void removeFld(int var1);

    public CTTextCharacterProperties getEndParaRPr();

    public boolean isSetEndParaRPr();

    public void setEndParaRPr(CTTextCharacterProperties var1);

    public CTTextCharacterProperties addNewEndParaRPr();

    public void unsetEndParaRPr();
}


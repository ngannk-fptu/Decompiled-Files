/*
 * Decompiled with CFR 0.152.
 */
package org.openxmlformats.schemas.officeDocument.x2006.extendedProperties;

import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlBoolean;
import org.apache.xmlbeans.XmlInt;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.XmlString;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.openxmlformats.schemas.officeDocument.x2006.extendedProperties.CTDigSigBlob;
import org.openxmlformats.schemas.officeDocument.x2006.extendedProperties.CTVectorLpstr;
import org.openxmlformats.schemas.officeDocument.x2006.extendedProperties.CTVectorVariant;

public interface CTProperties
extends XmlObject {
    public static final DocumentFactory<CTProperties> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "ctproperties3f10type");
    public static final SchemaType type = Factory.getType();

    public String getTemplate();

    public XmlString xgetTemplate();

    public boolean isSetTemplate();

    public void setTemplate(String var1);

    public void xsetTemplate(XmlString var1);

    public void unsetTemplate();

    public String getManager();

    public XmlString xgetManager();

    public boolean isSetManager();

    public void setManager(String var1);

    public void xsetManager(XmlString var1);

    public void unsetManager();

    public String getCompany();

    public XmlString xgetCompany();

    public boolean isSetCompany();

    public void setCompany(String var1);

    public void xsetCompany(XmlString var1);

    public void unsetCompany();

    public int getPages();

    public XmlInt xgetPages();

    public boolean isSetPages();

    public void setPages(int var1);

    public void xsetPages(XmlInt var1);

    public void unsetPages();

    public int getWords();

    public XmlInt xgetWords();

    public boolean isSetWords();

    public void setWords(int var1);

    public void xsetWords(XmlInt var1);

    public void unsetWords();

    public int getCharacters();

    public XmlInt xgetCharacters();

    public boolean isSetCharacters();

    public void setCharacters(int var1);

    public void xsetCharacters(XmlInt var1);

    public void unsetCharacters();

    public String getPresentationFormat();

    public XmlString xgetPresentationFormat();

    public boolean isSetPresentationFormat();

    public void setPresentationFormat(String var1);

    public void xsetPresentationFormat(XmlString var1);

    public void unsetPresentationFormat();

    public int getLines();

    public XmlInt xgetLines();

    public boolean isSetLines();

    public void setLines(int var1);

    public void xsetLines(XmlInt var1);

    public void unsetLines();

    public int getParagraphs();

    public XmlInt xgetParagraphs();

    public boolean isSetParagraphs();

    public void setParagraphs(int var1);

    public void xsetParagraphs(XmlInt var1);

    public void unsetParagraphs();

    public int getSlides();

    public XmlInt xgetSlides();

    public boolean isSetSlides();

    public void setSlides(int var1);

    public void xsetSlides(XmlInt var1);

    public void unsetSlides();

    public int getNotes();

    public XmlInt xgetNotes();

    public boolean isSetNotes();

    public void setNotes(int var1);

    public void xsetNotes(XmlInt var1);

    public void unsetNotes();

    public int getTotalTime();

    public XmlInt xgetTotalTime();

    public boolean isSetTotalTime();

    public void setTotalTime(int var1);

    public void xsetTotalTime(XmlInt var1);

    public void unsetTotalTime();

    public int getHiddenSlides();

    public XmlInt xgetHiddenSlides();

    public boolean isSetHiddenSlides();

    public void setHiddenSlides(int var1);

    public void xsetHiddenSlides(XmlInt var1);

    public void unsetHiddenSlides();

    public int getMMClips();

    public XmlInt xgetMMClips();

    public boolean isSetMMClips();

    public void setMMClips(int var1);

    public void xsetMMClips(XmlInt var1);

    public void unsetMMClips();

    public boolean getScaleCrop();

    public XmlBoolean xgetScaleCrop();

    public boolean isSetScaleCrop();

    public void setScaleCrop(boolean var1);

    public void xsetScaleCrop(XmlBoolean var1);

    public void unsetScaleCrop();

    public CTVectorVariant getHeadingPairs();

    public boolean isSetHeadingPairs();

    public void setHeadingPairs(CTVectorVariant var1);

    public CTVectorVariant addNewHeadingPairs();

    public void unsetHeadingPairs();

    public CTVectorLpstr getTitlesOfParts();

    public boolean isSetTitlesOfParts();

    public void setTitlesOfParts(CTVectorLpstr var1);

    public CTVectorLpstr addNewTitlesOfParts();

    public void unsetTitlesOfParts();

    public boolean getLinksUpToDate();

    public XmlBoolean xgetLinksUpToDate();

    public boolean isSetLinksUpToDate();

    public void setLinksUpToDate(boolean var1);

    public void xsetLinksUpToDate(XmlBoolean var1);

    public void unsetLinksUpToDate();

    public int getCharactersWithSpaces();

    public XmlInt xgetCharactersWithSpaces();

    public boolean isSetCharactersWithSpaces();

    public void setCharactersWithSpaces(int var1);

    public void xsetCharactersWithSpaces(XmlInt var1);

    public void unsetCharactersWithSpaces();

    public boolean getSharedDoc();

    public XmlBoolean xgetSharedDoc();

    public boolean isSetSharedDoc();

    public void setSharedDoc(boolean var1);

    public void xsetSharedDoc(XmlBoolean var1);

    public void unsetSharedDoc();

    public String getHyperlinkBase();

    public XmlString xgetHyperlinkBase();

    public boolean isSetHyperlinkBase();

    public void setHyperlinkBase(String var1);

    public void xsetHyperlinkBase(XmlString var1);

    public void unsetHyperlinkBase();

    public CTVectorVariant getHLinks();

    public boolean isSetHLinks();

    public void setHLinks(CTVectorVariant var1);

    public CTVectorVariant addNewHLinks();

    public void unsetHLinks();

    public boolean getHyperlinksChanged();

    public XmlBoolean xgetHyperlinksChanged();

    public boolean isSetHyperlinksChanged();

    public void setHyperlinksChanged(boolean var1);

    public void xsetHyperlinksChanged(XmlBoolean var1);

    public void unsetHyperlinksChanged();

    public CTDigSigBlob getDigSig();

    public boolean isSetDigSig();

    public void setDigSig(CTDigSigBlob var1);

    public CTDigSigBlob addNewDigSig();

    public void unsetDigSig();

    public String getApplication();

    public XmlString xgetApplication();

    public boolean isSetApplication();

    public void setApplication(String var1);

    public void xsetApplication(XmlString var1);

    public void unsetApplication();

    public String getAppVersion();

    public XmlString xgetAppVersion();

    public boolean isSetAppVersion();

    public void setAppVersion(String var1);

    public void xsetAppVersion(XmlString var1);

    public void unsetAppVersion();

    public int getDocSecurity();

    public XmlInt xgetDocSecurity();

    public boolean isSetDocSecurity();

    public void setDocSecurity(int var1);

    public void xsetDocSecurity(XmlInt var1);

    public void unsetDocSecurity();
}


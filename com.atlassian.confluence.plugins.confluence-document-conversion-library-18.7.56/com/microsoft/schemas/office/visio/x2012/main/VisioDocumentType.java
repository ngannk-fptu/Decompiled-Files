/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.microsoft.schemas.office.visio.x2012.main.ColorsType
 *  com.microsoft.schemas.office.visio.x2012.main.DocumentSheetType
 *  com.microsoft.schemas.office.visio.x2012.main.EventListType
 *  com.microsoft.schemas.office.visio.x2012.main.FaceNamesType
 *  com.microsoft.schemas.office.visio.x2012.main.HeaderFooterType
 *  com.microsoft.schemas.office.visio.x2012.main.PublishSettingsType
 */
package com.microsoft.schemas.office.visio.x2012.main;

import com.microsoft.schemas.office.visio.x2012.main.ColorsType;
import com.microsoft.schemas.office.visio.x2012.main.DocumentSettingsType;
import com.microsoft.schemas.office.visio.x2012.main.DocumentSheetType;
import com.microsoft.schemas.office.visio.x2012.main.EventListType;
import com.microsoft.schemas.office.visio.x2012.main.FaceNamesType;
import com.microsoft.schemas.office.visio.x2012.main.HeaderFooterType;
import com.microsoft.schemas.office.visio.x2012.main.PublishSettingsType;
import com.microsoft.schemas.office.visio.x2012.main.StyleSheetsType;
import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.impl.schema.DocumentFactory;

public interface VisioDocumentType
extends XmlObject {
    public static final DocumentFactory<VisioDocumentType> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "visiodocumenttypebfcatype");
    public static final SchemaType type = Factory.getType();

    public DocumentSettingsType getDocumentSettings();

    public boolean isSetDocumentSettings();

    public void setDocumentSettings(DocumentSettingsType var1);

    public DocumentSettingsType addNewDocumentSettings();

    public void unsetDocumentSettings();

    public ColorsType getColors();

    public boolean isSetColors();

    public void setColors(ColorsType var1);

    public ColorsType addNewColors();

    public void unsetColors();

    public FaceNamesType getFaceNames();

    public boolean isSetFaceNames();

    public void setFaceNames(FaceNamesType var1);

    public FaceNamesType addNewFaceNames();

    public void unsetFaceNames();

    public StyleSheetsType getStyleSheets();

    public boolean isSetStyleSheets();

    public void setStyleSheets(StyleSheetsType var1);

    public StyleSheetsType addNewStyleSheets();

    public void unsetStyleSheets();

    public DocumentSheetType getDocumentSheet();

    public boolean isSetDocumentSheet();

    public void setDocumentSheet(DocumentSheetType var1);

    public DocumentSheetType addNewDocumentSheet();

    public void unsetDocumentSheet();

    public EventListType getEventList();

    public boolean isSetEventList();

    public void setEventList(EventListType var1);

    public EventListType addNewEventList();

    public void unsetEventList();

    public HeaderFooterType getHeaderFooter();

    public boolean isSetHeaderFooter();

    public void setHeaderFooter(HeaderFooterType var1);

    public HeaderFooterType addNewHeaderFooter();

    public void unsetHeaderFooter();

    public PublishSettingsType getPublishSettings();

    public boolean isSetPublishSettings();

    public void setPublishSettings(PublishSettingsType var1);

    public PublishSettingsType addNewPublishSettings();

    public void unsetPublishSettings();
}


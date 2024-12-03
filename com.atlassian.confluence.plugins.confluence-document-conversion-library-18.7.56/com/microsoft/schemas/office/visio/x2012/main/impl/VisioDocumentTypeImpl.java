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
package com.microsoft.schemas.office.visio.x2012.main.impl;

import com.microsoft.schemas.office.visio.x2012.main.ColorsType;
import com.microsoft.schemas.office.visio.x2012.main.DocumentSettingsType;
import com.microsoft.schemas.office.visio.x2012.main.DocumentSheetType;
import com.microsoft.schemas.office.visio.x2012.main.EventListType;
import com.microsoft.schemas.office.visio.x2012.main.FaceNamesType;
import com.microsoft.schemas.office.visio.x2012.main.HeaderFooterType;
import com.microsoft.schemas.office.visio.x2012.main.PublishSettingsType;
import com.microsoft.schemas.office.visio.x2012.main.StyleSheetsType;
import com.microsoft.schemas.office.visio.x2012.main.VisioDocumentType;
import javax.xml.namespace.QName;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;

public class VisioDocumentTypeImpl
extends XmlComplexContentImpl
implements VisioDocumentType {
    private static final long serialVersionUID = 1L;
    private static final QName[] PROPERTY_QNAME = new QName[]{new QName("http://schemas.microsoft.com/office/visio/2012/main", "DocumentSettings"), new QName("http://schemas.microsoft.com/office/visio/2012/main", "Colors"), new QName("http://schemas.microsoft.com/office/visio/2012/main", "FaceNames"), new QName("http://schemas.microsoft.com/office/visio/2012/main", "StyleSheets"), new QName("http://schemas.microsoft.com/office/visio/2012/main", "DocumentSheet"), new QName("http://schemas.microsoft.com/office/visio/2012/main", "EventList"), new QName("http://schemas.microsoft.com/office/visio/2012/main", "HeaderFooter"), new QName("http://schemas.microsoft.com/office/visio/2012/main", "PublishSettings")};

    public VisioDocumentTypeImpl(SchemaType sType) {
        super(sType);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public DocumentSettingsType getDocumentSettings() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            DocumentSettingsType target = null;
            target = (DocumentSettingsType)((Object)this.get_store().find_element_user(PROPERTY_QNAME[0], 0));
            return target == null ? null : target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean isSetDocumentSettings() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[0]) != 0;
        }
    }

    @Override
    public void setDocumentSettings(DocumentSettingsType documentSettings) {
        this.generatedSetterHelperImpl(documentSettings, PROPERTY_QNAME[0], 0, (short)1);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public DocumentSettingsType addNewDocumentSettings() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            DocumentSettingsType target = null;
            target = (DocumentSettingsType)((Object)this.get_store().add_element_user(PROPERTY_QNAME[0]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void unsetDocumentSettings() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.get_store().remove_element(PROPERTY_QNAME[0], 0);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public ColorsType getColors() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            ColorsType target = null;
            target = (ColorsType)this.get_store().find_element_user(PROPERTY_QNAME[1], 0);
            return target == null ? null : target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean isSetColors() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[1]) != 0;
        }
    }

    @Override
    public void setColors(ColorsType colors) {
        this.generatedSetterHelperImpl((XmlObject)colors, PROPERTY_QNAME[1], 0, (short)1);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public ColorsType addNewColors() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            ColorsType target = null;
            target = (ColorsType)this.get_store().add_element_user(PROPERTY_QNAME[1]);
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void unsetColors() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.get_store().remove_element(PROPERTY_QNAME[1], 0);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public FaceNamesType getFaceNames() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            FaceNamesType target = null;
            target = (FaceNamesType)this.get_store().find_element_user(PROPERTY_QNAME[2], 0);
            return target == null ? null : target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean isSetFaceNames() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[2]) != 0;
        }
    }

    @Override
    public void setFaceNames(FaceNamesType faceNames) {
        this.generatedSetterHelperImpl((XmlObject)faceNames, PROPERTY_QNAME[2], 0, (short)1);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public FaceNamesType addNewFaceNames() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            FaceNamesType target = null;
            target = (FaceNamesType)this.get_store().add_element_user(PROPERTY_QNAME[2]);
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void unsetFaceNames() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.get_store().remove_element(PROPERTY_QNAME[2], 0);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public StyleSheetsType getStyleSheets() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            StyleSheetsType target = null;
            target = (StyleSheetsType)((Object)this.get_store().find_element_user(PROPERTY_QNAME[3], 0));
            return target == null ? null : target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean isSetStyleSheets() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[3]) != 0;
        }
    }

    @Override
    public void setStyleSheets(StyleSheetsType styleSheets) {
        this.generatedSetterHelperImpl(styleSheets, PROPERTY_QNAME[3], 0, (short)1);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public StyleSheetsType addNewStyleSheets() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            StyleSheetsType target = null;
            target = (StyleSheetsType)((Object)this.get_store().add_element_user(PROPERTY_QNAME[3]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void unsetStyleSheets() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.get_store().remove_element(PROPERTY_QNAME[3], 0);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public DocumentSheetType getDocumentSheet() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            DocumentSheetType target = null;
            target = (DocumentSheetType)this.get_store().find_element_user(PROPERTY_QNAME[4], 0);
            return target == null ? null : target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean isSetDocumentSheet() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[4]) != 0;
        }
    }

    @Override
    public void setDocumentSheet(DocumentSheetType documentSheet) {
        this.generatedSetterHelperImpl((XmlObject)documentSheet, PROPERTY_QNAME[4], 0, (short)1);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public DocumentSheetType addNewDocumentSheet() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            DocumentSheetType target = null;
            target = (DocumentSheetType)this.get_store().add_element_user(PROPERTY_QNAME[4]);
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void unsetDocumentSheet() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.get_store().remove_element(PROPERTY_QNAME[4], 0);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public EventListType getEventList() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            EventListType target = null;
            target = (EventListType)this.get_store().find_element_user(PROPERTY_QNAME[5], 0);
            return target == null ? null : target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean isSetEventList() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[5]) != 0;
        }
    }

    @Override
    public void setEventList(EventListType eventList) {
        this.generatedSetterHelperImpl((XmlObject)eventList, PROPERTY_QNAME[5], 0, (short)1);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public EventListType addNewEventList() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            EventListType target = null;
            target = (EventListType)this.get_store().add_element_user(PROPERTY_QNAME[5]);
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void unsetEventList() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.get_store().remove_element(PROPERTY_QNAME[5], 0);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public HeaderFooterType getHeaderFooter() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            HeaderFooterType target = null;
            target = (HeaderFooterType)this.get_store().find_element_user(PROPERTY_QNAME[6], 0);
            return target == null ? null : target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean isSetHeaderFooter() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[6]) != 0;
        }
    }

    @Override
    public void setHeaderFooter(HeaderFooterType headerFooter) {
        this.generatedSetterHelperImpl((XmlObject)headerFooter, PROPERTY_QNAME[6], 0, (short)1);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public HeaderFooterType addNewHeaderFooter() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            HeaderFooterType target = null;
            target = (HeaderFooterType)this.get_store().add_element_user(PROPERTY_QNAME[6]);
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void unsetHeaderFooter() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.get_store().remove_element(PROPERTY_QNAME[6], 0);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public PublishSettingsType getPublishSettings() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            PublishSettingsType target = null;
            target = (PublishSettingsType)this.get_store().find_element_user(PROPERTY_QNAME[7], 0);
            return target == null ? null : target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean isSetPublishSettings() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[7]) != 0;
        }
    }

    @Override
    public void setPublishSettings(PublishSettingsType publishSettings) {
        this.generatedSetterHelperImpl((XmlObject)publishSettings, PROPERTY_QNAME[7], 0, (short)1);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public PublishSettingsType addNewPublishSettings() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            PublishSettingsType target = null;
            target = (PublishSettingsType)this.get_store().add_element_user(PROPERTY_QNAME[7]);
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void unsetPublishSettings() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.get_store().remove_element(PROPERTY_QNAME[7], 0);
        }
    }
}


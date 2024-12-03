/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.openxmlformats.schemas.spreadsheetml.x2006.main.CTChartsheetPr
 *  org.openxmlformats.schemas.spreadsheetml.x2006.main.CTChartsheetProtection
 *  org.openxmlformats.schemas.spreadsheetml.x2006.main.CTChartsheetViews
 *  org.openxmlformats.schemas.spreadsheetml.x2006.main.CTCsPageSetup
 *  org.openxmlformats.schemas.spreadsheetml.x2006.main.CTCustomChartsheetViews
 *  org.openxmlformats.schemas.spreadsheetml.x2006.main.CTDrawingHF
 *  org.openxmlformats.schemas.spreadsheetml.x2006.main.CTWebPublishItems
 */
package org.openxmlformats.schemas.spreadsheetml.x2006.main.impl;

import javax.xml.namespace.QName;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTChartsheet;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTChartsheetPr;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTChartsheetProtection;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTChartsheetViews;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTCsPageSetup;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTCustomChartsheetViews;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTDrawing;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTDrawingHF;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTExtensionList;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTHeaderFooter;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTLegacyDrawing;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTPageMargins;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTSheetBackgroundPicture;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTWebPublishItems;

public class CTChartsheetImpl
extends XmlComplexContentImpl
implements CTChartsheet {
    private static final long serialVersionUID = 1L;
    private static final QName[] PROPERTY_QNAME = new QName[]{new QName("http://schemas.openxmlformats.org/spreadsheetml/2006/main", "sheetPr"), new QName("http://schemas.openxmlformats.org/spreadsheetml/2006/main", "sheetViews"), new QName("http://schemas.openxmlformats.org/spreadsheetml/2006/main", "sheetProtection"), new QName("http://schemas.openxmlformats.org/spreadsheetml/2006/main", "customSheetViews"), new QName("http://schemas.openxmlformats.org/spreadsheetml/2006/main", "pageMargins"), new QName("http://schemas.openxmlformats.org/spreadsheetml/2006/main", "pageSetup"), new QName("http://schemas.openxmlformats.org/spreadsheetml/2006/main", "headerFooter"), new QName("http://schemas.openxmlformats.org/spreadsheetml/2006/main", "drawing"), new QName("http://schemas.openxmlformats.org/spreadsheetml/2006/main", "legacyDrawing"), new QName("http://schemas.openxmlformats.org/spreadsheetml/2006/main", "legacyDrawingHF"), new QName("http://schemas.openxmlformats.org/spreadsheetml/2006/main", "drawingHF"), new QName("http://schemas.openxmlformats.org/spreadsheetml/2006/main", "picture"), new QName("http://schemas.openxmlformats.org/spreadsheetml/2006/main", "webPublishItems"), new QName("http://schemas.openxmlformats.org/spreadsheetml/2006/main", "extLst")};

    public CTChartsheetImpl(SchemaType sType) {
        super(sType);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTChartsheetPr getSheetPr() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTChartsheetPr target = null;
            target = (CTChartsheetPr)this.get_store().find_element_user(PROPERTY_QNAME[0], 0);
            return target == null ? null : target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean isSetSheetPr() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[0]) != 0;
        }
    }

    @Override
    public void setSheetPr(CTChartsheetPr sheetPr) {
        this.generatedSetterHelperImpl((XmlObject)sheetPr, PROPERTY_QNAME[0], 0, (short)1);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTChartsheetPr addNewSheetPr() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTChartsheetPr target = null;
            target = (CTChartsheetPr)this.get_store().add_element_user(PROPERTY_QNAME[0]);
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void unsetSheetPr() {
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
    public CTChartsheetViews getSheetViews() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTChartsheetViews target = null;
            target = (CTChartsheetViews)this.get_store().find_element_user(PROPERTY_QNAME[1], 0);
            return target == null ? null : target;
        }
    }

    @Override
    public void setSheetViews(CTChartsheetViews sheetViews) {
        this.generatedSetterHelperImpl((XmlObject)sheetViews, PROPERTY_QNAME[1], 0, (short)1);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTChartsheetViews addNewSheetViews() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTChartsheetViews target = null;
            target = (CTChartsheetViews)this.get_store().add_element_user(PROPERTY_QNAME[1]);
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTChartsheetProtection getSheetProtection() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTChartsheetProtection target = null;
            target = (CTChartsheetProtection)this.get_store().find_element_user(PROPERTY_QNAME[2], 0);
            return target == null ? null : target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean isSetSheetProtection() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[2]) != 0;
        }
    }

    @Override
    public void setSheetProtection(CTChartsheetProtection sheetProtection) {
        this.generatedSetterHelperImpl((XmlObject)sheetProtection, PROPERTY_QNAME[2], 0, (short)1);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTChartsheetProtection addNewSheetProtection() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTChartsheetProtection target = null;
            target = (CTChartsheetProtection)this.get_store().add_element_user(PROPERTY_QNAME[2]);
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void unsetSheetProtection() {
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
    public CTCustomChartsheetViews getCustomSheetViews() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTCustomChartsheetViews target = null;
            target = (CTCustomChartsheetViews)this.get_store().find_element_user(PROPERTY_QNAME[3], 0);
            return target == null ? null : target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean isSetCustomSheetViews() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[3]) != 0;
        }
    }

    @Override
    public void setCustomSheetViews(CTCustomChartsheetViews customSheetViews) {
        this.generatedSetterHelperImpl((XmlObject)customSheetViews, PROPERTY_QNAME[3], 0, (short)1);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTCustomChartsheetViews addNewCustomSheetViews() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTCustomChartsheetViews target = null;
            target = (CTCustomChartsheetViews)this.get_store().add_element_user(PROPERTY_QNAME[3]);
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void unsetCustomSheetViews() {
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
    public CTPageMargins getPageMargins() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTPageMargins target = null;
            target = (CTPageMargins)((Object)this.get_store().find_element_user(PROPERTY_QNAME[4], 0));
            return target == null ? null : target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean isSetPageMargins() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[4]) != 0;
        }
    }

    @Override
    public void setPageMargins(CTPageMargins pageMargins) {
        this.generatedSetterHelperImpl(pageMargins, PROPERTY_QNAME[4], 0, (short)1);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTPageMargins addNewPageMargins() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTPageMargins target = null;
            target = (CTPageMargins)((Object)this.get_store().add_element_user(PROPERTY_QNAME[4]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void unsetPageMargins() {
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
    public CTCsPageSetup getPageSetup() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTCsPageSetup target = null;
            target = (CTCsPageSetup)this.get_store().find_element_user(PROPERTY_QNAME[5], 0);
            return target == null ? null : target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean isSetPageSetup() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[5]) != 0;
        }
    }

    @Override
    public void setPageSetup(CTCsPageSetup pageSetup) {
        this.generatedSetterHelperImpl((XmlObject)pageSetup, PROPERTY_QNAME[5], 0, (short)1);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTCsPageSetup addNewPageSetup() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTCsPageSetup target = null;
            target = (CTCsPageSetup)this.get_store().add_element_user(PROPERTY_QNAME[5]);
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void unsetPageSetup() {
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
    public CTHeaderFooter getHeaderFooter() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTHeaderFooter target = null;
            target = (CTHeaderFooter)((Object)this.get_store().find_element_user(PROPERTY_QNAME[6], 0));
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
    public void setHeaderFooter(CTHeaderFooter headerFooter) {
        this.generatedSetterHelperImpl(headerFooter, PROPERTY_QNAME[6], 0, (short)1);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTHeaderFooter addNewHeaderFooter() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTHeaderFooter target = null;
            target = (CTHeaderFooter)((Object)this.get_store().add_element_user(PROPERTY_QNAME[6]));
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
    public CTDrawing getDrawing() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTDrawing target = null;
            target = (CTDrawing)((Object)this.get_store().find_element_user(PROPERTY_QNAME[7], 0));
            return target == null ? null : target;
        }
    }

    @Override
    public void setDrawing(CTDrawing drawing) {
        this.generatedSetterHelperImpl(drawing, PROPERTY_QNAME[7], 0, (short)1);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTDrawing addNewDrawing() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTDrawing target = null;
            target = (CTDrawing)((Object)this.get_store().add_element_user(PROPERTY_QNAME[7]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTLegacyDrawing getLegacyDrawing() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTLegacyDrawing target = null;
            target = (CTLegacyDrawing)((Object)this.get_store().find_element_user(PROPERTY_QNAME[8], 0));
            return target == null ? null : target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean isSetLegacyDrawing() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[8]) != 0;
        }
    }

    @Override
    public void setLegacyDrawing(CTLegacyDrawing legacyDrawing) {
        this.generatedSetterHelperImpl(legacyDrawing, PROPERTY_QNAME[8], 0, (short)1);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTLegacyDrawing addNewLegacyDrawing() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTLegacyDrawing target = null;
            target = (CTLegacyDrawing)((Object)this.get_store().add_element_user(PROPERTY_QNAME[8]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void unsetLegacyDrawing() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.get_store().remove_element(PROPERTY_QNAME[8], 0);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTLegacyDrawing getLegacyDrawingHF() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTLegacyDrawing target = null;
            target = (CTLegacyDrawing)((Object)this.get_store().find_element_user(PROPERTY_QNAME[9], 0));
            return target == null ? null : target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean isSetLegacyDrawingHF() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[9]) != 0;
        }
    }

    @Override
    public void setLegacyDrawingHF(CTLegacyDrawing legacyDrawingHF) {
        this.generatedSetterHelperImpl(legacyDrawingHF, PROPERTY_QNAME[9], 0, (short)1);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTLegacyDrawing addNewLegacyDrawingHF() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTLegacyDrawing target = null;
            target = (CTLegacyDrawing)((Object)this.get_store().add_element_user(PROPERTY_QNAME[9]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void unsetLegacyDrawingHF() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.get_store().remove_element(PROPERTY_QNAME[9], 0);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTDrawingHF getDrawingHF() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTDrawingHF target = null;
            target = (CTDrawingHF)this.get_store().find_element_user(PROPERTY_QNAME[10], 0);
            return target == null ? null : target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean isSetDrawingHF() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[10]) != 0;
        }
    }

    @Override
    public void setDrawingHF(CTDrawingHF drawingHF) {
        this.generatedSetterHelperImpl((XmlObject)drawingHF, PROPERTY_QNAME[10], 0, (short)1);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTDrawingHF addNewDrawingHF() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTDrawingHF target = null;
            target = (CTDrawingHF)this.get_store().add_element_user(PROPERTY_QNAME[10]);
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void unsetDrawingHF() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.get_store().remove_element(PROPERTY_QNAME[10], 0);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTSheetBackgroundPicture getPicture() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTSheetBackgroundPicture target = null;
            target = (CTSheetBackgroundPicture)((Object)this.get_store().find_element_user(PROPERTY_QNAME[11], 0));
            return target == null ? null : target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean isSetPicture() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[11]) != 0;
        }
    }

    @Override
    public void setPicture(CTSheetBackgroundPicture picture) {
        this.generatedSetterHelperImpl(picture, PROPERTY_QNAME[11], 0, (short)1);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTSheetBackgroundPicture addNewPicture() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTSheetBackgroundPicture target = null;
            target = (CTSheetBackgroundPicture)((Object)this.get_store().add_element_user(PROPERTY_QNAME[11]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void unsetPicture() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.get_store().remove_element(PROPERTY_QNAME[11], 0);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTWebPublishItems getWebPublishItems() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTWebPublishItems target = null;
            target = (CTWebPublishItems)this.get_store().find_element_user(PROPERTY_QNAME[12], 0);
            return target == null ? null : target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean isSetWebPublishItems() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[12]) != 0;
        }
    }

    @Override
    public void setWebPublishItems(CTWebPublishItems webPublishItems) {
        this.generatedSetterHelperImpl((XmlObject)webPublishItems, PROPERTY_QNAME[12], 0, (short)1);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTWebPublishItems addNewWebPublishItems() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTWebPublishItems target = null;
            target = (CTWebPublishItems)this.get_store().add_element_user(PROPERTY_QNAME[12]);
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void unsetWebPublishItems() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.get_store().remove_element(PROPERTY_QNAME[12], 0);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTExtensionList getExtLst() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTExtensionList target = null;
            target = (CTExtensionList)((Object)this.get_store().find_element_user(PROPERTY_QNAME[13], 0));
            return target == null ? null : target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean isSetExtLst() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[13]) != 0;
        }
    }

    @Override
    public void setExtLst(CTExtensionList extLst) {
        this.generatedSetterHelperImpl(extLst, PROPERTY_QNAME[13], 0, (short)1);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTExtensionList addNewExtLst() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTExtensionList target = null;
            target = (CTExtensionList)((Object)this.get_store().add_element_user(PROPERTY_QNAME[13]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void unsetExtLst() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.get_store().remove_element(PROPERTY_QNAME[13], 0);
        }
    }
}


/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.openxmlformats.schemas.spreadsheetml.x2006.main.CTDrawingHF
 *  org.openxmlformats.schemas.spreadsheetml.x2006.main.CTScenarios
 *  org.openxmlformats.schemas.spreadsheetml.x2006.main.CTSmartTags
 *  org.openxmlformats.schemas.spreadsheetml.x2006.main.CTWebPublishItems
 */
package org.openxmlformats.schemas.spreadsheetml.x2006.main.impl;

import java.util.List;
import javax.xml.namespace.QName;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.impl.values.JavaListXmlObject;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTAutoFilter;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTCellWatches;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTCols;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTConditionalFormatting;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTControls;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTCustomProperties;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTCustomSheetViews;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTDataConsolidate;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTDataValidations;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTDrawing;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTDrawingHF;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTExtensionList;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTHeaderFooter;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTHyperlinks;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTIgnoredErrors;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTLegacyDrawing;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTMergeCells;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTOleObjects;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTPageBreak;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTPageMargins;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTPageSetup;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTPhoneticPr;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTPrintOptions;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTProtectedRanges;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTScenarios;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTSheetBackgroundPicture;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTSheetCalcPr;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTSheetData;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTSheetDimension;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTSheetFormatPr;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTSheetPr;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTSheetProtection;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTSheetViews;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTSmartTags;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTSortState;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTTableParts;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTWebPublishItems;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTWorksheet;

public class CTWorksheetImpl
extends XmlComplexContentImpl
implements CTWorksheet {
    private static final long serialVersionUID = 1L;
    private static final QName[] PROPERTY_QNAME = new QName[]{new QName("http://schemas.openxmlformats.org/spreadsheetml/2006/main", "sheetPr"), new QName("http://schemas.openxmlformats.org/spreadsheetml/2006/main", "dimension"), new QName("http://schemas.openxmlformats.org/spreadsheetml/2006/main", "sheetViews"), new QName("http://schemas.openxmlformats.org/spreadsheetml/2006/main", "sheetFormatPr"), new QName("http://schemas.openxmlformats.org/spreadsheetml/2006/main", "cols"), new QName("http://schemas.openxmlformats.org/spreadsheetml/2006/main", "sheetData"), new QName("http://schemas.openxmlformats.org/spreadsheetml/2006/main", "sheetCalcPr"), new QName("http://schemas.openxmlformats.org/spreadsheetml/2006/main", "sheetProtection"), new QName("http://schemas.openxmlformats.org/spreadsheetml/2006/main", "protectedRanges"), new QName("http://schemas.openxmlformats.org/spreadsheetml/2006/main", "scenarios"), new QName("http://schemas.openxmlformats.org/spreadsheetml/2006/main", "autoFilter"), new QName("http://schemas.openxmlformats.org/spreadsheetml/2006/main", "sortState"), new QName("http://schemas.openxmlformats.org/spreadsheetml/2006/main", "dataConsolidate"), new QName("http://schemas.openxmlformats.org/spreadsheetml/2006/main", "customSheetViews"), new QName("http://schemas.openxmlformats.org/spreadsheetml/2006/main", "mergeCells"), new QName("http://schemas.openxmlformats.org/spreadsheetml/2006/main", "phoneticPr"), new QName("http://schemas.openxmlformats.org/spreadsheetml/2006/main", "conditionalFormatting"), new QName("http://schemas.openxmlformats.org/spreadsheetml/2006/main", "dataValidations"), new QName("http://schemas.openxmlformats.org/spreadsheetml/2006/main", "hyperlinks"), new QName("http://schemas.openxmlformats.org/spreadsheetml/2006/main", "printOptions"), new QName("http://schemas.openxmlformats.org/spreadsheetml/2006/main", "pageMargins"), new QName("http://schemas.openxmlformats.org/spreadsheetml/2006/main", "pageSetup"), new QName("http://schemas.openxmlformats.org/spreadsheetml/2006/main", "headerFooter"), new QName("http://schemas.openxmlformats.org/spreadsheetml/2006/main", "rowBreaks"), new QName("http://schemas.openxmlformats.org/spreadsheetml/2006/main", "colBreaks"), new QName("http://schemas.openxmlformats.org/spreadsheetml/2006/main", "customProperties"), new QName("http://schemas.openxmlformats.org/spreadsheetml/2006/main", "cellWatches"), new QName("http://schemas.openxmlformats.org/spreadsheetml/2006/main", "ignoredErrors"), new QName("http://schemas.openxmlformats.org/spreadsheetml/2006/main", "smartTags"), new QName("http://schemas.openxmlformats.org/spreadsheetml/2006/main", "drawing"), new QName("http://schemas.openxmlformats.org/spreadsheetml/2006/main", "legacyDrawing"), new QName("http://schemas.openxmlformats.org/spreadsheetml/2006/main", "legacyDrawingHF"), new QName("http://schemas.openxmlformats.org/spreadsheetml/2006/main", "drawingHF"), new QName("http://schemas.openxmlformats.org/spreadsheetml/2006/main", "picture"), new QName("http://schemas.openxmlformats.org/spreadsheetml/2006/main", "oleObjects"), new QName("http://schemas.openxmlformats.org/spreadsheetml/2006/main", "controls"), new QName("http://schemas.openxmlformats.org/spreadsheetml/2006/main", "webPublishItems"), new QName("http://schemas.openxmlformats.org/spreadsheetml/2006/main", "tableParts"), new QName("http://schemas.openxmlformats.org/spreadsheetml/2006/main", "extLst")};

    public CTWorksheetImpl(SchemaType sType) {
        super(sType);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTSheetPr getSheetPr() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTSheetPr target = null;
            target = (CTSheetPr)((Object)this.get_store().find_element_user(PROPERTY_QNAME[0], 0));
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
    public void setSheetPr(CTSheetPr sheetPr) {
        this.generatedSetterHelperImpl(sheetPr, PROPERTY_QNAME[0], 0, (short)1);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTSheetPr addNewSheetPr() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTSheetPr target = null;
            target = (CTSheetPr)((Object)this.get_store().add_element_user(PROPERTY_QNAME[0]));
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
    public CTSheetDimension getDimension() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTSheetDimension target = null;
            target = (CTSheetDimension)((Object)this.get_store().find_element_user(PROPERTY_QNAME[1], 0));
            return target == null ? null : target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean isSetDimension() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[1]) != 0;
        }
    }

    @Override
    public void setDimension(CTSheetDimension dimension) {
        this.generatedSetterHelperImpl(dimension, PROPERTY_QNAME[1], 0, (short)1);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTSheetDimension addNewDimension() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTSheetDimension target = null;
            target = (CTSheetDimension)((Object)this.get_store().add_element_user(PROPERTY_QNAME[1]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void unsetDimension() {
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
    public CTSheetViews getSheetViews() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTSheetViews target = null;
            target = (CTSheetViews)((Object)this.get_store().find_element_user(PROPERTY_QNAME[2], 0));
            return target == null ? null : target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean isSetSheetViews() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[2]) != 0;
        }
    }

    @Override
    public void setSheetViews(CTSheetViews sheetViews) {
        this.generatedSetterHelperImpl(sheetViews, PROPERTY_QNAME[2], 0, (short)1);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTSheetViews addNewSheetViews() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTSheetViews target = null;
            target = (CTSheetViews)((Object)this.get_store().add_element_user(PROPERTY_QNAME[2]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void unsetSheetViews() {
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
    public CTSheetFormatPr getSheetFormatPr() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTSheetFormatPr target = null;
            target = (CTSheetFormatPr)((Object)this.get_store().find_element_user(PROPERTY_QNAME[3], 0));
            return target == null ? null : target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean isSetSheetFormatPr() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[3]) != 0;
        }
    }

    @Override
    public void setSheetFormatPr(CTSheetFormatPr sheetFormatPr) {
        this.generatedSetterHelperImpl(sheetFormatPr, PROPERTY_QNAME[3], 0, (short)1);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTSheetFormatPr addNewSheetFormatPr() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTSheetFormatPr target = null;
            target = (CTSheetFormatPr)((Object)this.get_store().add_element_user(PROPERTY_QNAME[3]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void unsetSheetFormatPr() {
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
    public List<CTCols> getColsList() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return new JavaListXmlObject<CTCols>(this::getColsArray, this::setColsArray, this::insertNewCols, this::removeCols, this::sizeOfColsArray);
        }
    }

    @Override
    public CTCols[] getColsArray() {
        return (CTCols[])this.getXmlObjectArray(PROPERTY_QNAME[4], new CTCols[0]);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTCols getColsArray(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTCols target = null;
            target = (CTCols)((Object)this.get_store().find_element_user(PROPERTY_QNAME[4], i));
            if (target == null) {
                throw new IndexOutOfBoundsException();
            }
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public int sizeOfColsArray() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[4]);
        }
    }

    @Override
    public void setColsArray(CTCols[] colsArray) {
        this.check_orphaned();
        this.arraySetterHelper(colsArray, PROPERTY_QNAME[4]);
    }

    @Override
    public void setColsArray(int i, CTCols cols) {
        this.generatedSetterHelperImpl(cols, PROPERTY_QNAME[4], i, (short)2);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTCols insertNewCols(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTCols target = null;
            target = (CTCols)((Object)this.get_store().insert_element_user(PROPERTY_QNAME[4], i));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTCols addNewCols() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTCols target = null;
            target = (CTCols)((Object)this.get_store().add_element_user(PROPERTY_QNAME[4]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void removeCols(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.get_store().remove_element(PROPERTY_QNAME[4], i);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTSheetData getSheetData() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTSheetData target = null;
            target = (CTSheetData)((Object)this.get_store().find_element_user(PROPERTY_QNAME[5], 0));
            return target == null ? null : target;
        }
    }

    @Override
    public void setSheetData(CTSheetData sheetData) {
        this.generatedSetterHelperImpl(sheetData, PROPERTY_QNAME[5], 0, (short)1);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTSheetData addNewSheetData() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTSheetData target = null;
            target = (CTSheetData)((Object)this.get_store().add_element_user(PROPERTY_QNAME[5]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTSheetCalcPr getSheetCalcPr() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTSheetCalcPr target = null;
            target = (CTSheetCalcPr)((Object)this.get_store().find_element_user(PROPERTY_QNAME[6], 0));
            return target == null ? null : target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean isSetSheetCalcPr() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[6]) != 0;
        }
    }

    @Override
    public void setSheetCalcPr(CTSheetCalcPr sheetCalcPr) {
        this.generatedSetterHelperImpl(sheetCalcPr, PROPERTY_QNAME[6], 0, (short)1);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTSheetCalcPr addNewSheetCalcPr() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTSheetCalcPr target = null;
            target = (CTSheetCalcPr)((Object)this.get_store().add_element_user(PROPERTY_QNAME[6]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void unsetSheetCalcPr() {
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
    public CTSheetProtection getSheetProtection() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTSheetProtection target = null;
            target = (CTSheetProtection)((Object)this.get_store().find_element_user(PROPERTY_QNAME[7], 0));
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
            return this.get_store().count_elements(PROPERTY_QNAME[7]) != 0;
        }
    }

    @Override
    public void setSheetProtection(CTSheetProtection sheetProtection) {
        this.generatedSetterHelperImpl(sheetProtection, PROPERTY_QNAME[7], 0, (short)1);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTSheetProtection addNewSheetProtection() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTSheetProtection target = null;
            target = (CTSheetProtection)((Object)this.get_store().add_element_user(PROPERTY_QNAME[7]));
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
            this.get_store().remove_element(PROPERTY_QNAME[7], 0);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTProtectedRanges getProtectedRanges() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTProtectedRanges target = null;
            target = (CTProtectedRanges)((Object)this.get_store().find_element_user(PROPERTY_QNAME[8], 0));
            return target == null ? null : target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean isSetProtectedRanges() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[8]) != 0;
        }
    }

    @Override
    public void setProtectedRanges(CTProtectedRanges protectedRanges) {
        this.generatedSetterHelperImpl(protectedRanges, PROPERTY_QNAME[8], 0, (short)1);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTProtectedRanges addNewProtectedRanges() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTProtectedRanges target = null;
            target = (CTProtectedRanges)((Object)this.get_store().add_element_user(PROPERTY_QNAME[8]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void unsetProtectedRanges() {
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
    public CTScenarios getScenarios() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTScenarios target = null;
            target = (CTScenarios)this.get_store().find_element_user(PROPERTY_QNAME[9], 0);
            return target == null ? null : target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean isSetScenarios() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[9]) != 0;
        }
    }

    @Override
    public void setScenarios(CTScenarios scenarios) {
        this.generatedSetterHelperImpl((XmlObject)scenarios, PROPERTY_QNAME[9], 0, (short)1);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTScenarios addNewScenarios() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTScenarios target = null;
            target = (CTScenarios)this.get_store().add_element_user(PROPERTY_QNAME[9]);
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void unsetScenarios() {
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
    public CTAutoFilter getAutoFilter() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTAutoFilter target = null;
            target = (CTAutoFilter)((Object)this.get_store().find_element_user(PROPERTY_QNAME[10], 0));
            return target == null ? null : target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean isSetAutoFilter() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[10]) != 0;
        }
    }

    @Override
    public void setAutoFilter(CTAutoFilter autoFilter) {
        this.generatedSetterHelperImpl(autoFilter, PROPERTY_QNAME[10], 0, (short)1);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTAutoFilter addNewAutoFilter() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTAutoFilter target = null;
            target = (CTAutoFilter)((Object)this.get_store().add_element_user(PROPERTY_QNAME[10]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void unsetAutoFilter() {
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
    public CTSortState getSortState() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTSortState target = null;
            target = (CTSortState)((Object)this.get_store().find_element_user(PROPERTY_QNAME[11], 0));
            return target == null ? null : target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean isSetSortState() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[11]) != 0;
        }
    }

    @Override
    public void setSortState(CTSortState sortState) {
        this.generatedSetterHelperImpl(sortState, PROPERTY_QNAME[11], 0, (short)1);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTSortState addNewSortState() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTSortState target = null;
            target = (CTSortState)((Object)this.get_store().add_element_user(PROPERTY_QNAME[11]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void unsetSortState() {
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
    public CTDataConsolidate getDataConsolidate() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTDataConsolidate target = null;
            target = (CTDataConsolidate)((Object)this.get_store().find_element_user(PROPERTY_QNAME[12], 0));
            return target == null ? null : target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean isSetDataConsolidate() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[12]) != 0;
        }
    }

    @Override
    public void setDataConsolidate(CTDataConsolidate dataConsolidate) {
        this.generatedSetterHelperImpl(dataConsolidate, PROPERTY_QNAME[12], 0, (short)1);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTDataConsolidate addNewDataConsolidate() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTDataConsolidate target = null;
            target = (CTDataConsolidate)((Object)this.get_store().add_element_user(PROPERTY_QNAME[12]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void unsetDataConsolidate() {
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
    public CTCustomSheetViews getCustomSheetViews() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTCustomSheetViews target = null;
            target = (CTCustomSheetViews)((Object)this.get_store().find_element_user(PROPERTY_QNAME[13], 0));
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
            return this.get_store().count_elements(PROPERTY_QNAME[13]) != 0;
        }
    }

    @Override
    public void setCustomSheetViews(CTCustomSheetViews customSheetViews) {
        this.generatedSetterHelperImpl(customSheetViews, PROPERTY_QNAME[13], 0, (short)1);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTCustomSheetViews addNewCustomSheetViews() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTCustomSheetViews target = null;
            target = (CTCustomSheetViews)((Object)this.get_store().add_element_user(PROPERTY_QNAME[13]));
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
            this.get_store().remove_element(PROPERTY_QNAME[13], 0);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTMergeCells getMergeCells() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTMergeCells target = null;
            target = (CTMergeCells)((Object)this.get_store().find_element_user(PROPERTY_QNAME[14], 0));
            return target == null ? null : target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean isSetMergeCells() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[14]) != 0;
        }
    }

    @Override
    public void setMergeCells(CTMergeCells mergeCells) {
        this.generatedSetterHelperImpl(mergeCells, PROPERTY_QNAME[14], 0, (short)1);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTMergeCells addNewMergeCells() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTMergeCells target = null;
            target = (CTMergeCells)((Object)this.get_store().add_element_user(PROPERTY_QNAME[14]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void unsetMergeCells() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.get_store().remove_element(PROPERTY_QNAME[14], 0);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTPhoneticPr getPhoneticPr() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTPhoneticPr target = null;
            target = (CTPhoneticPr)((Object)this.get_store().find_element_user(PROPERTY_QNAME[15], 0));
            return target == null ? null : target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean isSetPhoneticPr() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[15]) != 0;
        }
    }

    @Override
    public void setPhoneticPr(CTPhoneticPr phoneticPr) {
        this.generatedSetterHelperImpl(phoneticPr, PROPERTY_QNAME[15], 0, (short)1);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTPhoneticPr addNewPhoneticPr() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTPhoneticPr target = null;
            target = (CTPhoneticPr)((Object)this.get_store().add_element_user(PROPERTY_QNAME[15]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void unsetPhoneticPr() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.get_store().remove_element(PROPERTY_QNAME[15], 0);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public List<CTConditionalFormatting> getConditionalFormattingList() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return new JavaListXmlObject<CTConditionalFormatting>(this::getConditionalFormattingArray, this::setConditionalFormattingArray, this::insertNewConditionalFormatting, this::removeConditionalFormatting, this::sizeOfConditionalFormattingArray);
        }
    }

    @Override
    public CTConditionalFormatting[] getConditionalFormattingArray() {
        return (CTConditionalFormatting[])this.getXmlObjectArray(PROPERTY_QNAME[16], new CTConditionalFormatting[0]);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTConditionalFormatting getConditionalFormattingArray(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTConditionalFormatting target = null;
            target = (CTConditionalFormatting)((Object)this.get_store().find_element_user(PROPERTY_QNAME[16], i));
            if (target == null) {
                throw new IndexOutOfBoundsException();
            }
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public int sizeOfConditionalFormattingArray() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[16]);
        }
    }

    @Override
    public void setConditionalFormattingArray(CTConditionalFormatting[] conditionalFormattingArray) {
        this.check_orphaned();
        this.arraySetterHelper(conditionalFormattingArray, PROPERTY_QNAME[16]);
    }

    @Override
    public void setConditionalFormattingArray(int i, CTConditionalFormatting conditionalFormatting) {
        this.generatedSetterHelperImpl(conditionalFormatting, PROPERTY_QNAME[16], i, (short)2);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTConditionalFormatting insertNewConditionalFormatting(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTConditionalFormatting target = null;
            target = (CTConditionalFormatting)((Object)this.get_store().insert_element_user(PROPERTY_QNAME[16], i));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTConditionalFormatting addNewConditionalFormatting() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTConditionalFormatting target = null;
            target = (CTConditionalFormatting)((Object)this.get_store().add_element_user(PROPERTY_QNAME[16]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void removeConditionalFormatting(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.get_store().remove_element(PROPERTY_QNAME[16], i);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTDataValidations getDataValidations() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTDataValidations target = null;
            target = (CTDataValidations)((Object)this.get_store().find_element_user(PROPERTY_QNAME[17], 0));
            return target == null ? null : target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean isSetDataValidations() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[17]) != 0;
        }
    }

    @Override
    public void setDataValidations(CTDataValidations dataValidations) {
        this.generatedSetterHelperImpl(dataValidations, PROPERTY_QNAME[17], 0, (short)1);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTDataValidations addNewDataValidations() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTDataValidations target = null;
            target = (CTDataValidations)((Object)this.get_store().add_element_user(PROPERTY_QNAME[17]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void unsetDataValidations() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.get_store().remove_element(PROPERTY_QNAME[17], 0);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTHyperlinks getHyperlinks() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTHyperlinks target = null;
            target = (CTHyperlinks)((Object)this.get_store().find_element_user(PROPERTY_QNAME[18], 0));
            return target == null ? null : target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean isSetHyperlinks() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[18]) != 0;
        }
    }

    @Override
    public void setHyperlinks(CTHyperlinks hyperlinks) {
        this.generatedSetterHelperImpl(hyperlinks, PROPERTY_QNAME[18], 0, (short)1);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTHyperlinks addNewHyperlinks() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTHyperlinks target = null;
            target = (CTHyperlinks)((Object)this.get_store().add_element_user(PROPERTY_QNAME[18]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void unsetHyperlinks() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.get_store().remove_element(PROPERTY_QNAME[18], 0);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTPrintOptions getPrintOptions() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTPrintOptions target = null;
            target = (CTPrintOptions)((Object)this.get_store().find_element_user(PROPERTY_QNAME[19], 0));
            return target == null ? null : target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean isSetPrintOptions() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[19]) != 0;
        }
    }

    @Override
    public void setPrintOptions(CTPrintOptions printOptions) {
        this.generatedSetterHelperImpl(printOptions, PROPERTY_QNAME[19], 0, (short)1);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTPrintOptions addNewPrintOptions() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTPrintOptions target = null;
            target = (CTPrintOptions)((Object)this.get_store().add_element_user(PROPERTY_QNAME[19]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void unsetPrintOptions() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.get_store().remove_element(PROPERTY_QNAME[19], 0);
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
            target = (CTPageMargins)((Object)this.get_store().find_element_user(PROPERTY_QNAME[20], 0));
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
            return this.get_store().count_elements(PROPERTY_QNAME[20]) != 0;
        }
    }

    @Override
    public void setPageMargins(CTPageMargins pageMargins) {
        this.generatedSetterHelperImpl(pageMargins, PROPERTY_QNAME[20], 0, (short)1);
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
            target = (CTPageMargins)((Object)this.get_store().add_element_user(PROPERTY_QNAME[20]));
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
            this.get_store().remove_element(PROPERTY_QNAME[20], 0);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTPageSetup getPageSetup() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTPageSetup target = null;
            target = (CTPageSetup)((Object)this.get_store().find_element_user(PROPERTY_QNAME[21], 0));
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
            return this.get_store().count_elements(PROPERTY_QNAME[21]) != 0;
        }
    }

    @Override
    public void setPageSetup(CTPageSetup pageSetup) {
        this.generatedSetterHelperImpl(pageSetup, PROPERTY_QNAME[21], 0, (short)1);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTPageSetup addNewPageSetup() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTPageSetup target = null;
            target = (CTPageSetup)((Object)this.get_store().add_element_user(PROPERTY_QNAME[21]));
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
            this.get_store().remove_element(PROPERTY_QNAME[21], 0);
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
            target = (CTHeaderFooter)((Object)this.get_store().find_element_user(PROPERTY_QNAME[22], 0));
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
            return this.get_store().count_elements(PROPERTY_QNAME[22]) != 0;
        }
    }

    @Override
    public void setHeaderFooter(CTHeaderFooter headerFooter) {
        this.generatedSetterHelperImpl(headerFooter, PROPERTY_QNAME[22], 0, (short)1);
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
            target = (CTHeaderFooter)((Object)this.get_store().add_element_user(PROPERTY_QNAME[22]));
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
            this.get_store().remove_element(PROPERTY_QNAME[22], 0);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTPageBreak getRowBreaks() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTPageBreak target = null;
            target = (CTPageBreak)((Object)this.get_store().find_element_user(PROPERTY_QNAME[23], 0));
            return target == null ? null : target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean isSetRowBreaks() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[23]) != 0;
        }
    }

    @Override
    public void setRowBreaks(CTPageBreak rowBreaks) {
        this.generatedSetterHelperImpl(rowBreaks, PROPERTY_QNAME[23], 0, (short)1);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTPageBreak addNewRowBreaks() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTPageBreak target = null;
            target = (CTPageBreak)((Object)this.get_store().add_element_user(PROPERTY_QNAME[23]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void unsetRowBreaks() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.get_store().remove_element(PROPERTY_QNAME[23], 0);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTPageBreak getColBreaks() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTPageBreak target = null;
            target = (CTPageBreak)((Object)this.get_store().find_element_user(PROPERTY_QNAME[24], 0));
            return target == null ? null : target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean isSetColBreaks() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[24]) != 0;
        }
    }

    @Override
    public void setColBreaks(CTPageBreak colBreaks) {
        this.generatedSetterHelperImpl(colBreaks, PROPERTY_QNAME[24], 0, (short)1);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTPageBreak addNewColBreaks() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTPageBreak target = null;
            target = (CTPageBreak)((Object)this.get_store().add_element_user(PROPERTY_QNAME[24]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void unsetColBreaks() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.get_store().remove_element(PROPERTY_QNAME[24], 0);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTCustomProperties getCustomProperties() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTCustomProperties target = null;
            target = (CTCustomProperties)((Object)this.get_store().find_element_user(PROPERTY_QNAME[25], 0));
            return target == null ? null : target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean isSetCustomProperties() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[25]) != 0;
        }
    }

    @Override
    public void setCustomProperties(CTCustomProperties customProperties) {
        this.generatedSetterHelperImpl(customProperties, PROPERTY_QNAME[25], 0, (short)1);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTCustomProperties addNewCustomProperties() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTCustomProperties target = null;
            target = (CTCustomProperties)((Object)this.get_store().add_element_user(PROPERTY_QNAME[25]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void unsetCustomProperties() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.get_store().remove_element(PROPERTY_QNAME[25], 0);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTCellWatches getCellWatches() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTCellWatches target = null;
            target = (CTCellWatches)((Object)this.get_store().find_element_user(PROPERTY_QNAME[26], 0));
            return target == null ? null : target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean isSetCellWatches() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[26]) != 0;
        }
    }

    @Override
    public void setCellWatches(CTCellWatches cellWatches) {
        this.generatedSetterHelperImpl(cellWatches, PROPERTY_QNAME[26], 0, (short)1);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTCellWatches addNewCellWatches() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTCellWatches target = null;
            target = (CTCellWatches)((Object)this.get_store().add_element_user(PROPERTY_QNAME[26]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void unsetCellWatches() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.get_store().remove_element(PROPERTY_QNAME[26], 0);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTIgnoredErrors getIgnoredErrors() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTIgnoredErrors target = null;
            target = (CTIgnoredErrors)((Object)this.get_store().find_element_user(PROPERTY_QNAME[27], 0));
            return target == null ? null : target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean isSetIgnoredErrors() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[27]) != 0;
        }
    }

    @Override
    public void setIgnoredErrors(CTIgnoredErrors ignoredErrors) {
        this.generatedSetterHelperImpl(ignoredErrors, PROPERTY_QNAME[27], 0, (short)1);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTIgnoredErrors addNewIgnoredErrors() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTIgnoredErrors target = null;
            target = (CTIgnoredErrors)((Object)this.get_store().add_element_user(PROPERTY_QNAME[27]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void unsetIgnoredErrors() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.get_store().remove_element(PROPERTY_QNAME[27], 0);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTSmartTags getSmartTags() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTSmartTags target = null;
            target = (CTSmartTags)this.get_store().find_element_user(PROPERTY_QNAME[28], 0);
            return target == null ? null : target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean isSetSmartTags() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[28]) != 0;
        }
    }

    @Override
    public void setSmartTags(CTSmartTags smartTags) {
        this.generatedSetterHelperImpl((XmlObject)smartTags, PROPERTY_QNAME[28], 0, (short)1);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTSmartTags addNewSmartTags() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTSmartTags target = null;
            target = (CTSmartTags)this.get_store().add_element_user(PROPERTY_QNAME[28]);
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void unsetSmartTags() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.get_store().remove_element(PROPERTY_QNAME[28], 0);
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
            target = (CTDrawing)((Object)this.get_store().find_element_user(PROPERTY_QNAME[29], 0));
            return target == null ? null : target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean isSetDrawing() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[29]) != 0;
        }
    }

    @Override
    public void setDrawing(CTDrawing drawing) {
        this.generatedSetterHelperImpl(drawing, PROPERTY_QNAME[29], 0, (short)1);
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
            target = (CTDrawing)((Object)this.get_store().add_element_user(PROPERTY_QNAME[29]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void unsetDrawing() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.get_store().remove_element(PROPERTY_QNAME[29], 0);
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
            target = (CTLegacyDrawing)((Object)this.get_store().find_element_user(PROPERTY_QNAME[30], 0));
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
            return this.get_store().count_elements(PROPERTY_QNAME[30]) != 0;
        }
    }

    @Override
    public void setLegacyDrawing(CTLegacyDrawing legacyDrawing) {
        this.generatedSetterHelperImpl(legacyDrawing, PROPERTY_QNAME[30], 0, (short)1);
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
            target = (CTLegacyDrawing)((Object)this.get_store().add_element_user(PROPERTY_QNAME[30]));
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
            this.get_store().remove_element(PROPERTY_QNAME[30], 0);
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
            target = (CTLegacyDrawing)((Object)this.get_store().find_element_user(PROPERTY_QNAME[31], 0));
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
            return this.get_store().count_elements(PROPERTY_QNAME[31]) != 0;
        }
    }

    @Override
    public void setLegacyDrawingHF(CTLegacyDrawing legacyDrawingHF) {
        this.generatedSetterHelperImpl(legacyDrawingHF, PROPERTY_QNAME[31], 0, (short)1);
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
            target = (CTLegacyDrawing)((Object)this.get_store().add_element_user(PROPERTY_QNAME[31]));
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
            this.get_store().remove_element(PROPERTY_QNAME[31], 0);
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
            target = (CTDrawingHF)this.get_store().find_element_user(PROPERTY_QNAME[32], 0);
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
            return this.get_store().count_elements(PROPERTY_QNAME[32]) != 0;
        }
    }

    @Override
    public void setDrawingHF(CTDrawingHF drawingHF) {
        this.generatedSetterHelperImpl((XmlObject)drawingHF, PROPERTY_QNAME[32], 0, (short)1);
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
            target = (CTDrawingHF)this.get_store().add_element_user(PROPERTY_QNAME[32]);
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
            this.get_store().remove_element(PROPERTY_QNAME[32], 0);
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
            target = (CTSheetBackgroundPicture)((Object)this.get_store().find_element_user(PROPERTY_QNAME[33], 0));
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
            return this.get_store().count_elements(PROPERTY_QNAME[33]) != 0;
        }
    }

    @Override
    public void setPicture(CTSheetBackgroundPicture picture) {
        this.generatedSetterHelperImpl(picture, PROPERTY_QNAME[33], 0, (short)1);
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
            target = (CTSheetBackgroundPicture)((Object)this.get_store().add_element_user(PROPERTY_QNAME[33]));
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
            this.get_store().remove_element(PROPERTY_QNAME[33], 0);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTOleObjects getOleObjects() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTOleObjects target = null;
            target = (CTOleObjects)((Object)this.get_store().find_element_user(PROPERTY_QNAME[34], 0));
            return target == null ? null : target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean isSetOleObjects() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[34]) != 0;
        }
    }

    @Override
    public void setOleObjects(CTOleObjects oleObjects) {
        this.generatedSetterHelperImpl(oleObjects, PROPERTY_QNAME[34], 0, (short)1);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTOleObjects addNewOleObjects() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTOleObjects target = null;
            target = (CTOleObjects)((Object)this.get_store().add_element_user(PROPERTY_QNAME[34]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void unsetOleObjects() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.get_store().remove_element(PROPERTY_QNAME[34], 0);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTControls getControls() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTControls target = null;
            target = (CTControls)((Object)this.get_store().find_element_user(PROPERTY_QNAME[35], 0));
            return target == null ? null : target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean isSetControls() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[35]) != 0;
        }
    }

    @Override
    public void setControls(CTControls controls) {
        this.generatedSetterHelperImpl(controls, PROPERTY_QNAME[35], 0, (short)1);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTControls addNewControls() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTControls target = null;
            target = (CTControls)((Object)this.get_store().add_element_user(PROPERTY_QNAME[35]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void unsetControls() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.get_store().remove_element(PROPERTY_QNAME[35], 0);
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
            target = (CTWebPublishItems)this.get_store().find_element_user(PROPERTY_QNAME[36], 0);
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
            return this.get_store().count_elements(PROPERTY_QNAME[36]) != 0;
        }
    }

    @Override
    public void setWebPublishItems(CTWebPublishItems webPublishItems) {
        this.generatedSetterHelperImpl((XmlObject)webPublishItems, PROPERTY_QNAME[36], 0, (short)1);
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
            target = (CTWebPublishItems)this.get_store().add_element_user(PROPERTY_QNAME[36]);
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
            this.get_store().remove_element(PROPERTY_QNAME[36], 0);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTTableParts getTableParts() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTTableParts target = null;
            target = (CTTableParts)((Object)this.get_store().find_element_user(PROPERTY_QNAME[37], 0));
            return target == null ? null : target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean isSetTableParts() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[37]) != 0;
        }
    }

    @Override
    public void setTableParts(CTTableParts tableParts) {
        this.generatedSetterHelperImpl(tableParts, PROPERTY_QNAME[37], 0, (short)1);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTTableParts addNewTableParts() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTTableParts target = null;
            target = (CTTableParts)((Object)this.get_store().add_element_user(PROPERTY_QNAME[37]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void unsetTableParts() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.get_store().remove_element(PROPERTY_QNAME[37], 0);
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
            target = (CTExtensionList)((Object)this.get_store().find_element_user(PROPERTY_QNAME[38], 0));
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
            return this.get_store().count_elements(PROPERTY_QNAME[38]) != 0;
        }
    }

    @Override
    public void setExtLst(CTExtensionList extLst) {
        this.generatedSetterHelperImpl(extLst, PROPERTY_QNAME[38], 0, (short)1);
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
            target = (CTExtensionList)((Object)this.get_store().add_element_user(PROPERTY_QNAME[38]));
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
            this.get_store().remove_element(PROPERTY_QNAME[38], 0);
        }
    }
}


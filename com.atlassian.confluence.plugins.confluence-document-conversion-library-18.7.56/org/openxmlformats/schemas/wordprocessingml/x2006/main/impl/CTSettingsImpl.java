/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.openxmlformats.schemas.officeDocument.x2006.math.CTMathPr
 *  org.openxmlformats.schemas.schemaLibrary.x2006.main.CTSchemaLibrary
 *  org.openxmlformats.schemas.wordprocessingml.x2006.main.CTCaptions
 *  org.openxmlformats.schemas.wordprocessingml.x2006.main.CTCharacterSpacing
 *  org.openxmlformats.schemas.wordprocessingml.x2006.main.CTColorSchemeMapping
 *  org.openxmlformats.schemas.wordprocessingml.x2006.main.CTCompat
 *  org.openxmlformats.schemas.wordprocessingml.x2006.main.CTDecimalNumberOrPrecent
 *  org.openxmlformats.schemas.wordprocessingml.x2006.main.CTDocRsids
 *  org.openxmlformats.schemas.wordprocessingml.x2006.main.CTDocType
 *  org.openxmlformats.schemas.wordprocessingml.x2006.main.CTDocVars
 *  org.openxmlformats.schemas.wordprocessingml.x2006.main.CTEdnDocProps
 *  org.openxmlformats.schemas.wordprocessingml.x2006.main.CTFtnDocProps
 *  org.openxmlformats.schemas.wordprocessingml.x2006.main.CTKinsoku
 *  org.openxmlformats.schemas.wordprocessingml.x2006.main.CTMailMerge
 *  org.openxmlformats.schemas.wordprocessingml.x2006.main.CTProof
 *  org.openxmlformats.schemas.wordprocessingml.x2006.main.CTReadingModeInkLockDown
 *  org.openxmlformats.schemas.wordprocessingml.x2006.main.CTSaveThroughXslt
 *  org.openxmlformats.schemas.wordprocessingml.x2006.main.CTShapeDefaults
 *  org.openxmlformats.schemas.wordprocessingml.x2006.main.CTSmartTagType
 *  org.openxmlformats.schemas.wordprocessingml.x2006.main.CTStylePaneFilter
 *  org.openxmlformats.schemas.wordprocessingml.x2006.main.CTStyleSort
 *  org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTrackChangesView
 *  org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTwipsMeasure
 *  org.openxmlformats.schemas.wordprocessingml.x2006.main.CTView
 *  org.openxmlformats.schemas.wordprocessingml.x2006.main.CTWriteProtection
 *  org.openxmlformats.schemas.wordprocessingml.x2006.main.CTWritingStyle
 */
package org.openxmlformats.schemas.wordprocessingml.x2006.main.impl;

import java.util.List;
import javax.xml.namespace.QName;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.impl.values.JavaListXmlObject;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;
import org.openxmlformats.schemas.officeDocument.x2006.math.CTMathPr;
import org.openxmlformats.schemas.schemaLibrary.x2006.main.CTSchemaLibrary;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTCaptions;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTCharacterSpacing;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTColorSchemeMapping;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTCompat;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTDecimalNumber;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTDecimalNumberOrPrecent;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTDocProtect;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTDocRsids;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTDocType;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTDocVars;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTEdnDocProps;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTEmpty;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTFtnDocProps;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTKinsoku;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTLanguage;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTMailMerge;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTOnOff;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTProof;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTReadingModeInkLockDown;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTRel;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTSaveThroughXslt;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTSettings;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTShapeDefaults;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTSmartTagType;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTString;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTStylePaneFilter;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTStyleSort;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTrackChangesView;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTwipsMeasure;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTView;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTWriteProtection;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTWritingStyle;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTZoom;

public class CTSettingsImpl
extends XmlComplexContentImpl
implements CTSettings {
    private static final long serialVersionUID = 1L;
    private static final QName[] PROPERTY_QNAME = new QName[]{new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "writeProtection"), new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "view"), new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "zoom"), new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "removePersonalInformation"), new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "removeDateAndTime"), new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "doNotDisplayPageBoundaries"), new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "displayBackgroundShape"), new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "printPostScriptOverText"), new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "printFractionalCharacterWidth"), new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "printFormsData"), new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "embedTrueTypeFonts"), new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "embedSystemFonts"), new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "saveSubsetFonts"), new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "saveFormsData"), new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "mirrorMargins"), new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "alignBordersAndEdges"), new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "bordersDoNotSurroundHeader"), new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "bordersDoNotSurroundFooter"), new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "gutterAtTop"), new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "hideSpellingErrors"), new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "hideGrammaticalErrors"), new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "activeWritingStyle"), new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "proofState"), new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "formsDesign"), new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "attachedTemplate"), new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "linkStyles"), new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "stylePaneFormatFilter"), new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "stylePaneSortMethod"), new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "documentType"), new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "mailMerge"), new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "revisionView"), new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "trackRevisions"), new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "doNotTrackMoves"), new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "doNotTrackFormatting"), new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "documentProtection"), new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "autoFormatOverride"), new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "styleLockTheme"), new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "styleLockQFSet"), new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "defaultTabStop"), new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "autoHyphenation"), new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "consecutiveHyphenLimit"), new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "hyphenationZone"), new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "doNotHyphenateCaps"), new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "showEnvelope"), new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "summaryLength"), new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "clickAndTypeStyle"), new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "defaultTableStyle"), new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "evenAndOddHeaders"), new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "bookFoldRevPrinting"), new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "bookFoldPrinting"), new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "bookFoldPrintingSheets"), new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "drawingGridHorizontalSpacing"), new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "drawingGridVerticalSpacing"), new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "displayHorizontalDrawingGridEvery"), new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "displayVerticalDrawingGridEvery"), new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "doNotUseMarginsForDrawingGridOrigin"), new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "drawingGridHorizontalOrigin"), new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "drawingGridVerticalOrigin"), new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "doNotShadeFormData"), new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "noPunctuationKerning"), new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "characterSpacingControl"), new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "printTwoOnOne"), new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "strictFirstAndLastChars"), new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "noLineBreaksAfter"), new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "noLineBreaksBefore"), new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "savePreviewPicture"), new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "doNotValidateAgainstSchema"), new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "saveInvalidXml"), new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "ignoreMixedContent"), new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "alwaysShowPlaceholderText"), new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "doNotDemarcateInvalidXml"), new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "saveXmlDataOnly"), new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "useXSLTWhenSaving"), new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "saveThroughXslt"), new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "showXMLTags"), new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "alwaysMergeEmptyNamespace"), new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "updateFields"), new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "hdrShapeDefaults"), new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "footnotePr"), new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "endnotePr"), new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "compat"), new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "docVars"), new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "rsids"), new QName("http://schemas.openxmlformats.org/officeDocument/2006/math", "mathPr"), new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "attachedSchema"), new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "themeFontLang"), new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "clrSchemeMapping"), new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "doNotIncludeSubdocsInStats"), new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "doNotAutoCompressPictures"), new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "forceUpgrade"), new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "captions"), new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "readModeInkLockDown"), new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "smartTagType"), new QName("http://schemas.openxmlformats.org/schemaLibrary/2006/main", "schemaLibrary"), new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "shapeDefaults"), new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "doNotEmbedSmartTags"), new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "decimalSymbol"), new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "listSeparator")};

    public CTSettingsImpl(SchemaType sType) {
        super(sType);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTWriteProtection getWriteProtection() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTWriteProtection target = null;
            target = (CTWriteProtection)this.get_store().find_element_user(PROPERTY_QNAME[0], 0);
            return target == null ? null : target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean isSetWriteProtection() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[0]) != 0;
        }
    }

    @Override
    public void setWriteProtection(CTWriteProtection writeProtection) {
        this.generatedSetterHelperImpl((XmlObject)writeProtection, PROPERTY_QNAME[0], 0, (short)1);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTWriteProtection addNewWriteProtection() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTWriteProtection target = null;
            target = (CTWriteProtection)this.get_store().add_element_user(PROPERTY_QNAME[0]);
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void unsetWriteProtection() {
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
    public CTView getView() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTView target = null;
            target = (CTView)this.get_store().find_element_user(PROPERTY_QNAME[1], 0);
            return target == null ? null : target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean isSetView() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[1]) != 0;
        }
    }

    @Override
    public void setView(CTView view) {
        this.generatedSetterHelperImpl((XmlObject)view, PROPERTY_QNAME[1], 0, (short)1);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTView addNewView() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTView target = null;
            target = (CTView)this.get_store().add_element_user(PROPERTY_QNAME[1]);
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void unsetView() {
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
    public CTZoom getZoom() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTZoom target = null;
            target = (CTZoom)((Object)this.get_store().find_element_user(PROPERTY_QNAME[2], 0));
            return target == null ? null : target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean isSetZoom() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[2]) != 0;
        }
    }

    @Override
    public void setZoom(CTZoom zoom) {
        this.generatedSetterHelperImpl(zoom, PROPERTY_QNAME[2], 0, (short)1);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTZoom addNewZoom() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTZoom target = null;
            target = (CTZoom)((Object)this.get_store().add_element_user(PROPERTY_QNAME[2]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void unsetZoom() {
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
    public CTOnOff getRemovePersonalInformation() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTOnOff target = null;
            target = (CTOnOff)((Object)this.get_store().find_element_user(PROPERTY_QNAME[3], 0));
            return target == null ? null : target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean isSetRemovePersonalInformation() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[3]) != 0;
        }
    }

    @Override
    public void setRemovePersonalInformation(CTOnOff removePersonalInformation) {
        this.generatedSetterHelperImpl(removePersonalInformation, PROPERTY_QNAME[3], 0, (short)1);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTOnOff addNewRemovePersonalInformation() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTOnOff target = null;
            target = (CTOnOff)((Object)this.get_store().add_element_user(PROPERTY_QNAME[3]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void unsetRemovePersonalInformation() {
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
    public CTOnOff getRemoveDateAndTime() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTOnOff target = null;
            target = (CTOnOff)((Object)this.get_store().find_element_user(PROPERTY_QNAME[4], 0));
            return target == null ? null : target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean isSetRemoveDateAndTime() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[4]) != 0;
        }
    }

    @Override
    public void setRemoveDateAndTime(CTOnOff removeDateAndTime) {
        this.generatedSetterHelperImpl(removeDateAndTime, PROPERTY_QNAME[4], 0, (short)1);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTOnOff addNewRemoveDateAndTime() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTOnOff target = null;
            target = (CTOnOff)((Object)this.get_store().add_element_user(PROPERTY_QNAME[4]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void unsetRemoveDateAndTime() {
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
    public CTOnOff getDoNotDisplayPageBoundaries() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTOnOff target = null;
            target = (CTOnOff)((Object)this.get_store().find_element_user(PROPERTY_QNAME[5], 0));
            return target == null ? null : target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean isSetDoNotDisplayPageBoundaries() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[5]) != 0;
        }
    }

    @Override
    public void setDoNotDisplayPageBoundaries(CTOnOff doNotDisplayPageBoundaries) {
        this.generatedSetterHelperImpl(doNotDisplayPageBoundaries, PROPERTY_QNAME[5], 0, (short)1);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTOnOff addNewDoNotDisplayPageBoundaries() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTOnOff target = null;
            target = (CTOnOff)((Object)this.get_store().add_element_user(PROPERTY_QNAME[5]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void unsetDoNotDisplayPageBoundaries() {
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
    public CTOnOff getDisplayBackgroundShape() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTOnOff target = null;
            target = (CTOnOff)((Object)this.get_store().find_element_user(PROPERTY_QNAME[6], 0));
            return target == null ? null : target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean isSetDisplayBackgroundShape() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[6]) != 0;
        }
    }

    @Override
    public void setDisplayBackgroundShape(CTOnOff displayBackgroundShape) {
        this.generatedSetterHelperImpl(displayBackgroundShape, PROPERTY_QNAME[6], 0, (short)1);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTOnOff addNewDisplayBackgroundShape() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTOnOff target = null;
            target = (CTOnOff)((Object)this.get_store().add_element_user(PROPERTY_QNAME[6]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void unsetDisplayBackgroundShape() {
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
    public CTOnOff getPrintPostScriptOverText() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTOnOff target = null;
            target = (CTOnOff)((Object)this.get_store().find_element_user(PROPERTY_QNAME[7], 0));
            return target == null ? null : target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean isSetPrintPostScriptOverText() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[7]) != 0;
        }
    }

    @Override
    public void setPrintPostScriptOverText(CTOnOff printPostScriptOverText) {
        this.generatedSetterHelperImpl(printPostScriptOverText, PROPERTY_QNAME[7], 0, (short)1);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTOnOff addNewPrintPostScriptOverText() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTOnOff target = null;
            target = (CTOnOff)((Object)this.get_store().add_element_user(PROPERTY_QNAME[7]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void unsetPrintPostScriptOverText() {
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
    public CTOnOff getPrintFractionalCharacterWidth() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTOnOff target = null;
            target = (CTOnOff)((Object)this.get_store().find_element_user(PROPERTY_QNAME[8], 0));
            return target == null ? null : target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean isSetPrintFractionalCharacterWidth() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[8]) != 0;
        }
    }

    @Override
    public void setPrintFractionalCharacterWidth(CTOnOff printFractionalCharacterWidth) {
        this.generatedSetterHelperImpl(printFractionalCharacterWidth, PROPERTY_QNAME[8], 0, (short)1);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTOnOff addNewPrintFractionalCharacterWidth() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTOnOff target = null;
            target = (CTOnOff)((Object)this.get_store().add_element_user(PROPERTY_QNAME[8]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void unsetPrintFractionalCharacterWidth() {
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
    public CTOnOff getPrintFormsData() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTOnOff target = null;
            target = (CTOnOff)((Object)this.get_store().find_element_user(PROPERTY_QNAME[9], 0));
            return target == null ? null : target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean isSetPrintFormsData() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[9]) != 0;
        }
    }

    @Override
    public void setPrintFormsData(CTOnOff printFormsData) {
        this.generatedSetterHelperImpl(printFormsData, PROPERTY_QNAME[9], 0, (short)1);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTOnOff addNewPrintFormsData() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTOnOff target = null;
            target = (CTOnOff)((Object)this.get_store().add_element_user(PROPERTY_QNAME[9]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void unsetPrintFormsData() {
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
    public CTOnOff getEmbedTrueTypeFonts() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTOnOff target = null;
            target = (CTOnOff)((Object)this.get_store().find_element_user(PROPERTY_QNAME[10], 0));
            return target == null ? null : target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean isSetEmbedTrueTypeFonts() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[10]) != 0;
        }
    }

    @Override
    public void setEmbedTrueTypeFonts(CTOnOff embedTrueTypeFonts) {
        this.generatedSetterHelperImpl(embedTrueTypeFonts, PROPERTY_QNAME[10], 0, (short)1);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTOnOff addNewEmbedTrueTypeFonts() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTOnOff target = null;
            target = (CTOnOff)((Object)this.get_store().add_element_user(PROPERTY_QNAME[10]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void unsetEmbedTrueTypeFonts() {
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
    public CTOnOff getEmbedSystemFonts() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTOnOff target = null;
            target = (CTOnOff)((Object)this.get_store().find_element_user(PROPERTY_QNAME[11], 0));
            return target == null ? null : target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean isSetEmbedSystemFonts() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[11]) != 0;
        }
    }

    @Override
    public void setEmbedSystemFonts(CTOnOff embedSystemFonts) {
        this.generatedSetterHelperImpl(embedSystemFonts, PROPERTY_QNAME[11], 0, (short)1);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTOnOff addNewEmbedSystemFonts() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTOnOff target = null;
            target = (CTOnOff)((Object)this.get_store().add_element_user(PROPERTY_QNAME[11]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void unsetEmbedSystemFonts() {
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
    public CTOnOff getSaveSubsetFonts() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTOnOff target = null;
            target = (CTOnOff)((Object)this.get_store().find_element_user(PROPERTY_QNAME[12], 0));
            return target == null ? null : target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean isSetSaveSubsetFonts() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[12]) != 0;
        }
    }

    @Override
    public void setSaveSubsetFonts(CTOnOff saveSubsetFonts) {
        this.generatedSetterHelperImpl(saveSubsetFonts, PROPERTY_QNAME[12], 0, (short)1);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTOnOff addNewSaveSubsetFonts() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTOnOff target = null;
            target = (CTOnOff)((Object)this.get_store().add_element_user(PROPERTY_QNAME[12]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void unsetSaveSubsetFonts() {
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
    public CTOnOff getSaveFormsData() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTOnOff target = null;
            target = (CTOnOff)((Object)this.get_store().find_element_user(PROPERTY_QNAME[13], 0));
            return target == null ? null : target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean isSetSaveFormsData() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[13]) != 0;
        }
    }

    @Override
    public void setSaveFormsData(CTOnOff saveFormsData) {
        this.generatedSetterHelperImpl(saveFormsData, PROPERTY_QNAME[13], 0, (short)1);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTOnOff addNewSaveFormsData() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTOnOff target = null;
            target = (CTOnOff)((Object)this.get_store().add_element_user(PROPERTY_QNAME[13]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void unsetSaveFormsData() {
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
    public CTOnOff getMirrorMargins() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTOnOff target = null;
            target = (CTOnOff)((Object)this.get_store().find_element_user(PROPERTY_QNAME[14], 0));
            return target == null ? null : target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean isSetMirrorMargins() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[14]) != 0;
        }
    }

    @Override
    public void setMirrorMargins(CTOnOff mirrorMargins) {
        this.generatedSetterHelperImpl(mirrorMargins, PROPERTY_QNAME[14], 0, (short)1);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTOnOff addNewMirrorMargins() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTOnOff target = null;
            target = (CTOnOff)((Object)this.get_store().add_element_user(PROPERTY_QNAME[14]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void unsetMirrorMargins() {
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
    public CTOnOff getAlignBordersAndEdges() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTOnOff target = null;
            target = (CTOnOff)((Object)this.get_store().find_element_user(PROPERTY_QNAME[15], 0));
            return target == null ? null : target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean isSetAlignBordersAndEdges() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[15]) != 0;
        }
    }

    @Override
    public void setAlignBordersAndEdges(CTOnOff alignBordersAndEdges) {
        this.generatedSetterHelperImpl(alignBordersAndEdges, PROPERTY_QNAME[15], 0, (short)1);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTOnOff addNewAlignBordersAndEdges() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTOnOff target = null;
            target = (CTOnOff)((Object)this.get_store().add_element_user(PROPERTY_QNAME[15]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void unsetAlignBordersAndEdges() {
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
    public CTOnOff getBordersDoNotSurroundHeader() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTOnOff target = null;
            target = (CTOnOff)((Object)this.get_store().find_element_user(PROPERTY_QNAME[16], 0));
            return target == null ? null : target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean isSetBordersDoNotSurroundHeader() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[16]) != 0;
        }
    }

    @Override
    public void setBordersDoNotSurroundHeader(CTOnOff bordersDoNotSurroundHeader) {
        this.generatedSetterHelperImpl(bordersDoNotSurroundHeader, PROPERTY_QNAME[16], 0, (short)1);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTOnOff addNewBordersDoNotSurroundHeader() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTOnOff target = null;
            target = (CTOnOff)((Object)this.get_store().add_element_user(PROPERTY_QNAME[16]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void unsetBordersDoNotSurroundHeader() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.get_store().remove_element(PROPERTY_QNAME[16], 0);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTOnOff getBordersDoNotSurroundFooter() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTOnOff target = null;
            target = (CTOnOff)((Object)this.get_store().find_element_user(PROPERTY_QNAME[17], 0));
            return target == null ? null : target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean isSetBordersDoNotSurroundFooter() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[17]) != 0;
        }
    }

    @Override
    public void setBordersDoNotSurroundFooter(CTOnOff bordersDoNotSurroundFooter) {
        this.generatedSetterHelperImpl(bordersDoNotSurroundFooter, PROPERTY_QNAME[17], 0, (short)1);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTOnOff addNewBordersDoNotSurroundFooter() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTOnOff target = null;
            target = (CTOnOff)((Object)this.get_store().add_element_user(PROPERTY_QNAME[17]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void unsetBordersDoNotSurroundFooter() {
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
    public CTOnOff getGutterAtTop() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTOnOff target = null;
            target = (CTOnOff)((Object)this.get_store().find_element_user(PROPERTY_QNAME[18], 0));
            return target == null ? null : target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean isSetGutterAtTop() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[18]) != 0;
        }
    }

    @Override
    public void setGutterAtTop(CTOnOff gutterAtTop) {
        this.generatedSetterHelperImpl(gutterAtTop, PROPERTY_QNAME[18], 0, (short)1);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTOnOff addNewGutterAtTop() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTOnOff target = null;
            target = (CTOnOff)((Object)this.get_store().add_element_user(PROPERTY_QNAME[18]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void unsetGutterAtTop() {
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
    public CTOnOff getHideSpellingErrors() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTOnOff target = null;
            target = (CTOnOff)((Object)this.get_store().find_element_user(PROPERTY_QNAME[19], 0));
            return target == null ? null : target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean isSetHideSpellingErrors() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[19]) != 0;
        }
    }

    @Override
    public void setHideSpellingErrors(CTOnOff hideSpellingErrors) {
        this.generatedSetterHelperImpl(hideSpellingErrors, PROPERTY_QNAME[19], 0, (short)1);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTOnOff addNewHideSpellingErrors() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTOnOff target = null;
            target = (CTOnOff)((Object)this.get_store().add_element_user(PROPERTY_QNAME[19]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void unsetHideSpellingErrors() {
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
    public CTOnOff getHideGrammaticalErrors() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTOnOff target = null;
            target = (CTOnOff)((Object)this.get_store().find_element_user(PROPERTY_QNAME[20], 0));
            return target == null ? null : target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean isSetHideGrammaticalErrors() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[20]) != 0;
        }
    }

    @Override
    public void setHideGrammaticalErrors(CTOnOff hideGrammaticalErrors) {
        this.generatedSetterHelperImpl(hideGrammaticalErrors, PROPERTY_QNAME[20], 0, (short)1);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTOnOff addNewHideGrammaticalErrors() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTOnOff target = null;
            target = (CTOnOff)((Object)this.get_store().add_element_user(PROPERTY_QNAME[20]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void unsetHideGrammaticalErrors() {
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
    public List<CTWritingStyle> getActiveWritingStyleList() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return new JavaListXmlObject<CTWritingStyle>(this::getActiveWritingStyleArray, this::setActiveWritingStyleArray, this::insertNewActiveWritingStyle, this::removeActiveWritingStyle, this::sizeOfActiveWritingStyleArray);
        }
    }

    @Override
    public CTWritingStyle[] getActiveWritingStyleArray() {
        return (CTWritingStyle[])this.getXmlObjectArray(PROPERTY_QNAME[21], (XmlObject[])new CTWritingStyle[0]);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTWritingStyle getActiveWritingStyleArray(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTWritingStyle target = null;
            target = (CTWritingStyle)this.get_store().find_element_user(PROPERTY_QNAME[21], i);
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
    public int sizeOfActiveWritingStyleArray() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[21]);
        }
    }

    @Override
    public void setActiveWritingStyleArray(CTWritingStyle[] activeWritingStyleArray) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])activeWritingStyleArray, PROPERTY_QNAME[21]);
    }

    @Override
    public void setActiveWritingStyleArray(int i, CTWritingStyle activeWritingStyle) {
        this.generatedSetterHelperImpl((XmlObject)activeWritingStyle, PROPERTY_QNAME[21], i, (short)2);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTWritingStyle insertNewActiveWritingStyle(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTWritingStyle target = null;
            target = (CTWritingStyle)this.get_store().insert_element_user(PROPERTY_QNAME[21], i);
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTWritingStyle addNewActiveWritingStyle() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTWritingStyle target = null;
            target = (CTWritingStyle)this.get_store().add_element_user(PROPERTY_QNAME[21]);
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void removeActiveWritingStyle(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.get_store().remove_element(PROPERTY_QNAME[21], i);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTProof getProofState() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTProof target = null;
            target = (CTProof)this.get_store().find_element_user(PROPERTY_QNAME[22], 0);
            return target == null ? null : target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean isSetProofState() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[22]) != 0;
        }
    }

    @Override
    public void setProofState(CTProof proofState) {
        this.generatedSetterHelperImpl((XmlObject)proofState, PROPERTY_QNAME[22], 0, (short)1);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTProof addNewProofState() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTProof target = null;
            target = (CTProof)this.get_store().add_element_user(PROPERTY_QNAME[22]);
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void unsetProofState() {
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
    public CTOnOff getFormsDesign() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTOnOff target = null;
            target = (CTOnOff)((Object)this.get_store().find_element_user(PROPERTY_QNAME[23], 0));
            return target == null ? null : target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean isSetFormsDesign() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[23]) != 0;
        }
    }

    @Override
    public void setFormsDesign(CTOnOff formsDesign) {
        this.generatedSetterHelperImpl(formsDesign, PROPERTY_QNAME[23], 0, (short)1);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTOnOff addNewFormsDesign() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTOnOff target = null;
            target = (CTOnOff)((Object)this.get_store().add_element_user(PROPERTY_QNAME[23]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void unsetFormsDesign() {
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
    public CTRel getAttachedTemplate() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTRel target = null;
            target = (CTRel)((Object)this.get_store().find_element_user(PROPERTY_QNAME[24], 0));
            return target == null ? null : target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean isSetAttachedTemplate() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[24]) != 0;
        }
    }

    @Override
    public void setAttachedTemplate(CTRel attachedTemplate) {
        this.generatedSetterHelperImpl(attachedTemplate, PROPERTY_QNAME[24], 0, (short)1);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTRel addNewAttachedTemplate() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTRel target = null;
            target = (CTRel)((Object)this.get_store().add_element_user(PROPERTY_QNAME[24]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void unsetAttachedTemplate() {
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
    public CTOnOff getLinkStyles() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTOnOff target = null;
            target = (CTOnOff)((Object)this.get_store().find_element_user(PROPERTY_QNAME[25], 0));
            return target == null ? null : target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean isSetLinkStyles() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[25]) != 0;
        }
    }

    @Override
    public void setLinkStyles(CTOnOff linkStyles) {
        this.generatedSetterHelperImpl(linkStyles, PROPERTY_QNAME[25], 0, (short)1);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTOnOff addNewLinkStyles() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTOnOff target = null;
            target = (CTOnOff)((Object)this.get_store().add_element_user(PROPERTY_QNAME[25]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void unsetLinkStyles() {
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
    public CTStylePaneFilter getStylePaneFormatFilter() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTStylePaneFilter target = null;
            target = (CTStylePaneFilter)this.get_store().find_element_user(PROPERTY_QNAME[26], 0);
            return target == null ? null : target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean isSetStylePaneFormatFilter() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[26]) != 0;
        }
    }

    @Override
    public void setStylePaneFormatFilter(CTStylePaneFilter stylePaneFormatFilter) {
        this.generatedSetterHelperImpl((XmlObject)stylePaneFormatFilter, PROPERTY_QNAME[26], 0, (short)1);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTStylePaneFilter addNewStylePaneFormatFilter() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTStylePaneFilter target = null;
            target = (CTStylePaneFilter)this.get_store().add_element_user(PROPERTY_QNAME[26]);
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void unsetStylePaneFormatFilter() {
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
    public CTStyleSort getStylePaneSortMethod() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTStyleSort target = null;
            target = (CTStyleSort)this.get_store().find_element_user(PROPERTY_QNAME[27], 0);
            return target == null ? null : target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean isSetStylePaneSortMethod() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[27]) != 0;
        }
    }

    @Override
    public void setStylePaneSortMethod(CTStyleSort stylePaneSortMethod) {
        this.generatedSetterHelperImpl((XmlObject)stylePaneSortMethod, PROPERTY_QNAME[27], 0, (short)1);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTStyleSort addNewStylePaneSortMethod() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTStyleSort target = null;
            target = (CTStyleSort)this.get_store().add_element_user(PROPERTY_QNAME[27]);
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void unsetStylePaneSortMethod() {
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
    public CTDocType getDocumentType() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTDocType target = null;
            target = (CTDocType)this.get_store().find_element_user(PROPERTY_QNAME[28], 0);
            return target == null ? null : target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean isSetDocumentType() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[28]) != 0;
        }
    }

    @Override
    public void setDocumentType(CTDocType documentType) {
        this.generatedSetterHelperImpl((XmlObject)documentType, PROPERTY_QNAME[28], 0, (short)1);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTDocType addNewDocumentType() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTDocType target = null;
            target = (CTDocType)this.get_store().add_element_user(PROPERTY_QNAME[28]);
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void unsetDocumentType() {
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
    public CTMailMerge getMailMerge() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTMailMerge target = null;
            target = (CTMailMerge)this.get_store().find_element_user(PROPERTY_QNAME[29], 0);
            return target == null ? null : target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean isSetMailMerge() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[29]) != 0;
        }
    }

    @Override
    public void setMailMerge(CTMailMerge mailMerge) {
        this.generatedSetterHelperImpl((XmlObject)mailMerge, PROPERTY_QNAME[29], 0, (short)1);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTMailMerge addNewMailMerge() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTMailMerge target = null;
            target = (CTMailMerge)this.get_store().add_element_user(PROPERTY_QNAME[29]);
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void unsetMailMerge() {
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
    public CTTrackChangesView getRevisionView() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTTrackChangesView target = null;
            target = (CTTrackChangesView)this.get_store().find_element_user(PROPERTY_QNAME[30], 0);
            return target == null ? null : target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean isSetRevisionView() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[30]) != 0;
        }
    }

    @Override
    public void setRevisionView(CTTrackChangesView revisionView) {
        this.generatedSetterHelperImpl((XmlObject)revisionView, PROPERTY_QNAME[30], 0, (short)1);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTTrackChangesView addNewRevisionView() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTTrackChangesView target = null;
            target = (CTTrackChangesView)this.get_store().add_element_user(PROPERTY_QNAME[30]);
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void unsetRevisionView() {
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
    public CTOnOff getTrackRevisions() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTOnOff target = null;
            target = (CTOnOff)((Object)this.get_store().find_element_user(PROPERTY_QNAME[31], 0));
            return target == null ? null : target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean isSetTrackRevisions() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[31]) != 0;
        }
    }

    @Override
    public void setTrackRevisions(CTOnOff trackRevisions) {
        this.generatedSetterHelperImpl(trackRevisions, PROPERTY_QNAME[31], 0, (short)1);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTOnOff addNewTrackRevisions() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTOnOff target = null;
            target = (CTOnOff)((Object)this.get_store().add_element_user(PROPERTY_QNAME[31]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void unsetTrackRevisions() {
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
    public CTOnOff getDoNotTrackMoves() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTOnOff target = null;
            target = (CTOnOff)((Object)this.get_store().find_element_user(PROPERTY_QNAME[32], 0));
            return target == null ? null : target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean isSetDoNotTrackMoves() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[32]) != 0;
        }
    }

    @Override
    public void setDoNotTrackMoves(CTOnOff doNotTrackMoves) {
        this.generatedSetterHelperImpl(doNotTrackMoves, PROPERTY_QNAME[32], 0, (short)1);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTOnOff addNewDoNotTrackMoves() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTOnOff target = null;
            target = (CTOnOff)((Object)this.get_store().add_element_user(PROPERTY_QNAME[32]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void unsetDoNotTrackMoves() {
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
    public CTOnOff getDoNotTrackFormatting() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTOnOff target = null;
            target = (CTOnOff)((Object)this.get_store().find_element_user(PROPERTY_QNAME[33], 0));
            return target == null ? null : target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean isSetDoNotTrackFormatting() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[33]) != 0;
        }
    }

    @Override
    public void setDoNotTrackFormatting(CTOnOff doNotTrackFormatting) {
        this.generatedSetterHelperImpl(doNotTrackFormatting, PROPERTY_QNAME[33], 0, (short)1);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTOnOff addNewDoNotTrackFormatting() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTOnOff target = null;
            target = (CTOnOff)((Object)this.get_store().add_element_user(PROPERTY_QNAME[33]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void unsetDoNotTrackFormatting() {
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
    public CTDocProtect getDocumentProtection() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTDocProtect target = null;
            target = (CTDocProtect)((Object)this.get_store().find_element_user(PROPERTY_QNAME[34], 0));
            return target == null ? null : target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean isSetDocumentProtection() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[34]) != 0;
        }
    }

    @Override
    public void setDocumentProtection(CTDocProtect documentProtection) {
        this.generatedSetterHelperImpl(documentProtection, PROPERTY_QNAME[34], 0, (short)1);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTDocProtect addNewDocumentProtection() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTDocProtect target = null;
            target = (CTDocProtect)((Object)this.get_store().add_element_user(PROPERTY_QNAME[34]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void unsetDocumentProtection() {
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
    public CTOnOff getAutoFormatOverride() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTOnOff target = null;
            target = (CTOnOff)((Object)this.get_store().find_element_user(PROPERTY_QNAME[35], 0));
            return target == null ? null : target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean isSetAutoFormatOverride() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[35]) != 0;
        }
    }

    @Override
    public void setAutoFormatOverride(CTOnOff autoFormatOverride) {
        this.generatedSetterHelperImpl(autoFormatOverride, PROPERTY_QNAME[35], 0, (short)1);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTOnOff addNewAutoFormatOverride() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTOnOff target = null;
            target = (CTOnOff)((Object)this.get_store().add_element_user(PROPERTY_QNAME[35]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void unsetAutoFormatOverride() {
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
    public CTOnOff getStyleLockTheme() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTOnOff target = null;
            target = (CTOnOff)((Object)this.get_store().find_element_user(PROPERTY_QNAME[36], 0));
            return target == null ? null : target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean isSetStyleLockTheme() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[36]) != 0;
        }
    }

    @Override
    public void setStyleLockTheme(CTOnOff styleLockTheme) {
        this.generatedSetterHelperImpl(styleLockTheme, PROPERTY_QNAME[36], 0, (short)1);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTOnOff addNewStyleLockTheme() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTOnOff target = null;
            target = (CTOnOff)((Object)this.get_store().add_element_user(PROPERTY_QNAME[36]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void unsetStyleLockTheme() {
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
    public CTOnOff getStyleLockQFSet() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTOnOff target = null;
            target = (CTOnOff)((Object)this.get_store().find_element_user(PROPERTY_QNAME[37], 0));
            return target == null ? null : target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean isSetStyleLockQFSet() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[37]) != 0;
        }
    }

    @Override
    public void setStyleLockQFSet(CTOnOff styleLockQFSet) {
        this.generatedSetterHelperImpl(styleLockQFSet, PROPERTY_QNAME[37], 0, (short)1);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTOnOff addNewStyleLockQFSet() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTOnOff target = null;
            target = (CTOnOff)((Object)this.get_store().add_element_user(PROPERTY_QNAME[37]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void unsetStyleLockQFSet() {
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
    public CTTwipsMeasure getDefaultTabStop() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTTwipsMeasure target = null;
            target = (CTTwipsMeasure)this.get_store().find_element_user(PROPERTY_QNAME[38], 0);
            return target == null ? null : target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean isSetDefaultTabStop() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[38]) != 0;
        }
    }

    @Override
    public void setDefaultTabStop(CTTwipsMeasure defaultTabStop) {
        this.generatedSetterHelperImpl((XmlObject)defaultTabStop, PROPERTY_QNAME[38], 0, (short)1);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTTwipsMeasure addNewDefaultTabStop() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTTwipsMeasure target = null;
            target = (CTTwipsMeasure)this.get_store().add_element_user(PROPERTY_QNAME[38]);
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void unsetDefaultTabStop() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.get_store().remove_element(PROPERTY_QNAME[38], 0);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTOnOff getAutoHyphenation() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTOnOff target = null;
            target = (CTOnOff)((Object)this.get_store().find_element_user(PROPERTY_QNAME[39], 0));
            return target == null ? null : target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean isSetAutoHyphenation() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[39]) != 0;
        }
    }

    @Override
    public void setAutoHyphenation(CTOnOff autoHyphenation) {
        this.generatedSetterHelperImpl(autoHyphenation, PROPERTY_QNAME[39], 0, (short)1);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTOnOff addNewAutoHyphenation() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTOnOff target = null;
            target = (CTOnOff)((Object)this.get_store().add_element_user(PROPERTY_QNAME[39]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void unsetAutoHyphenation() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.get_store().remove_element(PROPERTY_QNAME[39], 0);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTDecimalNumber getConsecutiveHyphenLimit() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTDecimalNumber target = null;
            target = (CTDecimalNumber)((Object)this.get_store().find_element_user(PROPERTY_QNAME[40], 0));
            return target == null ? null : target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean isSetConsecutiveHyphenLimit() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[40]) != 0;
        }
    }

    @Override
    public void setConsecutiveHyphenLimit(CTDecimalNumber consecutiveHyphenLimit) {
        this.generatedSetterHelperImpl(consecutiveHyphenLimit, PROPERTY_QNAME[40], 0, (short)1);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTDecimalNumber addNewConsecutiveHyphenLimit() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTDecimalNumber target = null;
            target = (CTDecimalNumber)((Object)this.get_store().add_element_user(PROPERTY_QNAME[40]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void unsetConsecutiveHyphenLimit() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.get_store().remove_element(PROPERTY_QNAME[40], 0);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTTwipsMeasure getHyphenationZone() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTTwipsMeasure target = null;
            target = (CTTwipsMeasure)this.get_store().find_element_user(PROPERTY_QNAME[41], 0);
            return target == null ? null : target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean isSetHyphenationZone() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[41]) != 0;
        }
    }

    @Override
    public void setHyphenationZone(CTTwipsMeasure hyphenationZone) {
        this.generatedSetterHelperImpl((XmlObject)hyphenationZone, PROPERTY_QNAME[41], 0, (short)1);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTTwipsMeasure addNewHyphenationZone() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTTwipsMeasure target = null;
            target = (CTTwipsMeasure)this.get_store().add_element_user(PROPERTY_QNAME[41]);
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void unsetHyphenationZone() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.get_store().remove_element(PROPERTY_QNAME[41], 0);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTOnOff getDoNotHyphenateCaps() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTOnOff target = null;
            target = (CTOnOff)((Object)this.get_store().find_element_user(PROPERTY_QNAME[42], 0));
            return target == null ? null : target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean isSetDoNotHyphenateCaps() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[42]) != 0;
        }
    }

    @Override
    public void setDoNotHyphenateCaps(CTOnOff doNotHyphenateCaps) {
        this.generatedSetterHelperImpl(doNotHyphenateCaps, PROPERTY_QNAME[42], 0, (short)1);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTOnOff addNewDoNotHyphenateCaps() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTOnOff target = null;
            target = (CTOnOff)((Object)this.get_store().add_element_user(PROPERTY_QNAME[42]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void unsetDoNotHyphenateCaps() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.get_store().remove_element(PROPERTY_QNAME[42], 0);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTOnOff getShowEnvelope() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTOnOff target = null;
            target = (CTOnOff)((Object)this.get_store().find_element_user(PROPERTY_QNAME[43], 0));
            return target == null ? null : target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean isSetShowEnvelope() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[43]) != 0;
        }
    }

    @Override
    public void setShowEnvelope(CTOnOff showEnvelope) {
        this.generatedSetterHelperImpl(showEnvelope, PROPERTY_QNAME[43], 0, (short)1);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTOnOff addNewShowEnvelope() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTOnOff target = null;
            target = (CTOnOff)((Object)this.get_store().add_element_user(PROPERTY_QNAME[43]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void unsetShowEnvelope() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.get_store().remove_element(PROPERTY_QNAME[43], 0);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTDecimalNumberOrPrecent getSummaryLength() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTDecimalNumberOrPrecent target = null;
            target = (CTDecimalNumberOrPrecent)this.get_store().find_element_user(PROPERTY_QNAME[44], 0);
            return target == null ? null : target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean isSetSummaryLength() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[44]) != 0;
        }
    }

    @Override
    public void setSummaryLength(CTDecimalNumberOrPrecent summaryLength) {
        this.generatedSetterHelperImpl((XmlObject)summaryLength, PROPERTY_QNAME[44], 0, (short)1);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTDecimalNumberOrPrecent addNewSummaryLength() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTDecimalNumberOrPrecent target = null;
            target = (CTDecimalNumberOrPrecent)this.get_store().add_element_user(PROPERTY_QNAME[44]);
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void unsetSummaryLength() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.get_store().remove_element(PROPERTY_QNAME[44], 0);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTString getClickAndTypeStyle() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTString target = null;
            target = (CTString)((Object)this.get_store().find_element_user(PROPERTY_QNAME[45], 0));
            return target == null ? null : target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean isSetClickAndTypeStyle() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[45]) != 0;
        }
    }

    @Override
    public void setClickAndTypeStyle(CTString clickAndTypeStyle) {
        this.generatedSetterHelperImpl(clickAndTypeStyle, PROPERTY_QNAME[45], 0, (short)1);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTString addNewClickAndTypeStyle() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTString target = null;
            target = (CTString)((Object)this.get_store().add_element_user(PROPERTY_QNAME[45]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void unsetClickAndTypeStyle() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.get_store().remove_element(PROPERTY_QNAME[45], 0);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTString getDefaultTableStyle() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTString target = null;
            target = (CTString)((Object)this.get_store().find_element_user(PROPERTY_QNAME[46], 0));
            return target == null ? null : target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean isSetDefaultTableStyle() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[46]) != 0;
        }
    }

    @Override
    public void setDefaultTableStyle(CTString defaultTableStyle) {
        this.generatedSetterHelperImpl(defaultTableStyle, PROPERTY_QNAME[46], 0, (short)1);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTString addNewDefaultTableStyle() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTString target = null;
            target = (CTString)((Object)this.get_store().add_element_user(PROPERTY_QNAME[46]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void unsetDefaultTableStyle() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.get_store().remove_element(PROPERTY_QNAME[46], 0);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTOnOff getEvenAndOddHeaders() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTOnOff target = null;
            target = (CTOnOff)((Object)this.get_store().find_element_user(PROPERTY_QNAME[47], 0));
            return target == null ? null : target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean isSetEvenAndOddHeaders() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[47]) != 0;
        }
    }

    @Override
    public void setEvenAndOddHeaders(CTOnOff evenAndOddHeaders) {
        this.generatedSetterHelperImpl(evenAndOddHeaders, PROPERTY_QNAME[47], 0, (short)1);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTOnOff addNewEvenAndOddHeaders() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTOnOff target = null;
            target = (CTOnOff)((Object)this.get_store().add_element_user(PROPERTY_QNAME[47]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void unsetEvenAndOddHeaders() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.get_store().remove_element(PROPERTY_QNAME[47], 0);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTOnOff getBookFoldRevPrinting() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTOnOff target = null;
            target = (CTOnOff)((Object)this.get_store().find_element_user(PROPERTY_QNAME[48], 0));
            return target == null ? null : target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean isSetBookFoldRevPrinting() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[48]) != 0;
        }
    }

    @Override
    public void setBookFoldRevPrinting(CTOnOff bookFoldRevPrinting) {
        this.generatedSetterHelperImpl(bookFoldRevPrinting, PROPERTY_QNAME[48], 0, (short)1);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTOnOff addNewBookFoldRevPrinting() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTOnOff target = null;
            target = (CTOnOff)((Object)this.get_store().add_element_user(PROPERTY_QNAME[48]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void unsetBookFoldRevPrinting() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.get_store().remove_element(PROPERTY_QNAME[48], 0);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTOnOff getBookFoldPrinting() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTOnOff target = null;
            target = (CTOnOff)((Object)this.get_store().find_element_user(PROPERTY_QNAME[49], 0));
            return target == null ? null : target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean isSetBookFoldPrinting() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[49]) != 0;
        }
    }

    @Override
    public void setBookFoldPrinting(CTOnOff bookFoldPrinting) {
        this.generatedSetterHelperImpl(bookFoldPrinting, PROPERTY_QNAME[49], 0, (short)1);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTOnOff addNewBookFoldPrinting() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTOnOff target = null;
            target = (CTOnOff)((Object)this.get_store().add_element_user(PROPERTY_QNAME[49]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void unsetBookFoldPrinting() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.get_store().remove_element(PROPERTY_QNAME[49], 0);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTDecimalNumber getBookFoldPrintingSheets() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTDecimalNumber target = null;
            target = (CTDecimalNumber)((Object)this.get_store().find_element_user(PROPERTY_QNAME[50], 0));
            return target == null ? null : target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean isSetBookFoldPrintingSheets() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[50]) != 0;
        }
    }

    @Override
    public void setBookFoldPrintingSheets(CTDecimalNumber bookFoldPrintingSheets) {
        this.generatedSetterHelperImpl(bookFoldPrintingSheets, PROPERTY_QNAME[50], 0, (short)1);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTDecimalNumber addNewBookFoldPrintingSheets() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTDecimalNumber target = null;
            target = (CTDecimalNumber)((Object)this.get_store().add_element_user(PROPERTY_QNAME[50]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void unsetBookFoldPrintingSheets() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.get_store().remove_element(PROPERTY_QNAME[50], 0);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTTwipsMeasure getDrawingGridHorizontalSpacing() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTTwipsMeasure target = null;
            target = (CTTwipsMeasure)this.get_store().find_element_user(PROPERTY_QNAME[51], 0);
            return target == null ? null : target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean isSetDrawingGridHorizontalSpacing() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[51]) != 0;
        }
    }

    @Override
    public void setDrawingGridHorizontalSpacing(CTTwipsMeasure drawingGridHorizontalSpacing) {
        this.generatedSetterHelperImpl((XmlObject)drawingGridHorizontalSpacing, PROPERTY_QNAME[51], 0, (short)1);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTTwipsMeasure addNewDrawingGridHorizontalSpacing() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTTwipsMeasure target = null;
            target = (CTTwipsMeasure)this.get_store().add_element_user(PROPERTY_QNAME[51]);
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void unsetDrawingGridHorizontalSpacing() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.get_store().remove_element(PROPERTY_QNAME[51], 0);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTTwipsMeasure getDrawingGridVerticalSpacing() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTTwipsMeasure target = null;
            target = (CTTwipsMeasure)this.get_store().find_element_user(PROPERTY_QNAME[52], 0);
            return target == null ? null : target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean isSetDrawingGridVerticalSpacing() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[52]) != 0;
        }
    }

    @Override
    public void setDrawingGridVerticalSpacing(CTTwipsMeasure drawingGridVerticalSpacing) {
        this.generatedSetterHelperImpl((XmlObject)drawingGridVerticalSpacing, PROPERTY_QNAME[52], 0, (short)1);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTTwipsMeasure addNewDrawingGridVerticalSpacing() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTTwipsMeasure target = null;
            target = (CTTwipsMeasure)this.get_store().add_element_user(PROPERTY_QNAME[52]);
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void unsetDrawingGridVerticalSpacing() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.get_store().remove_element(PROPERTY_QNAME[52], 0);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTDecimalNumber getDisplayHorizontalDrawingGridEvery() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTDecimalNumber target = null;
            target = (CTDecimalNumber)((Object)this.get_store().find_element_user(PROPERTY_QNAME[53], 0));
            return target == null ? null : target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean isSetDisplayHorizontalDrawingGridEvery() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[53]) != 0;
        }
    }

    @Override
    public void setDisplayHorizontalDrawingGridEvery(CTDecimalNumber displayHorizontalDrawingGridEvery) {
        this.generatedSetterHelperImpl(displayHorizontalDrawingGridEvery, PROPERTY_QNAME[53], 0, (short)1);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTDecimalNumber addNewDisplayHorizontalDrawingGridEvery() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTDecimalNumber target = null;
            target = (CTDecimalNumber)((Object)this.get_store().add_element_user(PROPERTY_QNAME[53]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void unsetDisplayHorizontalDrawingGridEvery() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.get_store().remove_element(PROPERTY_QNAME[53], 0);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTDecimalNumber getDisplayVerticalDrawingGridEvery() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTDecimalNumber target = null;
            target = (CTDecimalNumber)((Object)this.get_store().find_element_user(PROPERTY_QNAME[54], 0));
            return target == null ? null : target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean isSetDisplayVerticalDrawingGridEvery() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[54]) != 0;
        }
    }

    @Override
    public void setDisplayVerticalDrawingGridEvery(CTDecimalNumber displayVerticalDrawingGridEvery) {
        this.generatedSetterHelperImpl(displayVerticalDrawingGridEvery, PROPERTY_QNAME[54], 0, (short)1);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTDecimalNumber addNewDisplayVerticalDrawingGridEvery() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTDecimalNumber target = null;
            target = (CTDecimalNumber)((Object)this.get_store().add_element_user(PROPERTY_QNAME[54]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void unsetDisplayVerticalDrawingGridEvery() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.get_store().remove_element(PROPERTY_QNAME[54], 0);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTOnOff getDoNotUseMarginsForDrawingGridOrigin() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTOnOff target = null;
            target = (CTOnOff)((Object)this.get_store().find_element_user(PROPERTY_QNAME[55], 0));
            return target == null ? null : target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean isSetDoNotUseMarginsForDrawingGridOrigin() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[55]) != 0;
        }
    }

    @Override
    public void setDoNotUseMarginsForDrawingGridOrigin(CTOnOff doNotUseMarginsForDrawingGridOrigin) {
        this.generatedSetterHelperImpl(doNotUseMarginsForDrawingGridOrigin, PROPERTY_QNAME[55], 0, (short)1);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTOnOff addNewDoNotUseMarginsForDrawingGridOrigin() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTOnOff target = null;
            target = (CTOnOff)((Object)this.get_store().add_element_user(PROPERTY_QNAME[55]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void unsetDoNotUseMarginsForDrawingGridOrigin() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.get_store().remove_element(PROPERTY_QNAME[55], 0);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTTwipsMeasure getDrawingGridHorizontalOrigin() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTTwipsMeasure target = null;
            target = (CTTwipsMeasure)this.get_store().find_element_user(PROPERTY_QNAME[56], 0);
            return target == null ? null : target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean isSetDrawingGridHorizontalOrigin() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[56]) != 0;
        }
    }

    @Override
    public void setDrawingGridHorizontalOrigin(CTTwipsMeasure drawingGridHorizontalOrigin) {
        this.generatedSetterHelperImpl((XmlObject)drawingGridHorizontalOrigin, PROPERTY_QNAME[56], 0, (short)1);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTTwipsMeasure addNewDrawingGridHorizontalOrigin() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTTwipsMeasure target = null;
            target = (CTTwipsMeasure)this.get_store().add_element_user(PROPERTY_QNAME[56]);
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void unsetDrawingGridHorizontalOrigin() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.get_store().remove_element(PROPERTY_QNAME[56], 0);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTTwipsMeasure getDrawingGridVerticalOrigin() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTTwipsMeasure target = null;
            target = (CTTwipsMeasure)this.get_store().find_element_user(PROPERTY_QNAME[57], 0);
            return target == null ? null : target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean isSetDrawingGridVerticalOrigin() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[57]) != 0;
        }
    }

    @Override
    public void setDrawingGridVerticalOrigin(CTTwipsMeasure drawingGridVerticalOrigin) {
        this.generatedSetterHelperImpl((XmlObject)drawingGridVerticalOrigin, PROPERTY_QNAME[57], 0, (short)1);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTTwipsMeasure addNewDrawingGridVerticalOrigin() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTTwipsMeasure target = null;
            target = (CTTwipsMeasure)this.get_store().add_element_user(PROPERTY_QNAME[57]);
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void unsetDrawingGridVerticalOrigin() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.get_store().remove_element(PROPERTY_QNAME[57], 0);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTOnOff getDoNotShadeFormData() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTOnOff target = null;
            target = (CTOnOff)((Object)this.get_store().find_element_user(PROPERTY_QNAME[58], 0));
            return target == null ? null : target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean isSetDoNotShadeFormData() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[58]) != 0;
        }
    }

    @Override
    public void setDoNotShadeFormData(CTOnOff doNotShadeFormData) {
        this.generatedSetterHelperImpl(doNotShadeFormData, PROPERTY_QNAME[58], 0, (short)1);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTOnOff addNewDoNotShadeFormData() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTOnOff target = null;
            target = (CTOnOff)((Object)this.get_store().add_element_user(PROPERTY_QNAME[58]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void unsetDoNotShadeFormData() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.get_store().remove_element(PROPERTY_QNAME[58], 0);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTOnOff getNoPunctuationKerning() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTOnOff target = null;
            target = (CTOnOff)((Object)this.get_store().find_element_user(PROPERTY_QNAME[59], 0));
            return target == null ? null : target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean isSetNoPunctuationKerning() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[59]) != 0;
        }
    }

    @Override
    public void setNoPunctuationKerning(CTOnOff noPunctuationKerning) {
        this.generatedSetterHelperImpl(noPunctuationKerning, PROPERTY_QNAME[59], 0, (short)1);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTOnOff addNewNoPunctuationKerning() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTOnOff target = null;
            target = (CTOnOff)((Object)this.get_store().add_element_user(PROPERTY_QNAME[59]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void unsetNoPunctuationKerning() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.get_store().remove_element(PROPERTY_QNAME[59], 0);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTCharacterSpacing getCharacterSpacingControl() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTCharacterSpacing target = null;
            target = (CTCharacterSpacing)this.get_store().find_element_user(PROPERTY_QNAME[60], 0);
            return target == null ? null : target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean isSetCharacterSpacingControl() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[60]) != 0;
        }
    }

    @Override
    public void setCharacterSpacingControl(CTCharacterSpacing characterSpacingControl) {
        this.generatedSetterHelperImpl((XmlObject)characterSpacingControl, PROPERTY_QNAME[60], 0, (short)1);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTCharacterSpacing addNewCharacterSpacingControl() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTCharacterSpacing target = null;
            target = (CTCharacterSpacing)this.get_store().add_element_user(PROPERTY_QNAME[60]);
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void unsetCharacterSpacingControl() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.get_store().remove_element(PROPERTY_QNAME[60], 0);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTOnOff getPrintTwoOnOne() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTOnOff target = null;
            target = (CTOnOff)((Object)this.get_store().find_element_user(PROPERTY_QNAME[61], 0));
            return target == null ? null : target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean isSetPrintTwoOnOne() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[61]) != 0;
        }
    }

    @Override
    public void setPrintTwoOnOne(CTOnOff printTwoOnOne) {
        this.generatedSetterHelperImpl(printTwoOnOne, PROPERTY_QNAME[61], 0, (short)1);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTOnOff addNewPrintTwoOnOne() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTOnOff target = null;
            target = (CTOnOff)((Object)this.get_store().add_element_user(PROPERTY_QNAME[61]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void unsetPrintTwoOnOne() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.get_store().remove_element(PROPERTY_QNAME[61], 0);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTOnOff getStrictFirstAndLastChars() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTOnOff target = null;
            target = (CTOnOff)((Object)this.get_store().find_element_user(PROPERTY_QNAME[62], 0));
            return target == null ? null : target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean isSetStrictFirstAndLastChars() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[62]) != 0;
        }
    }

    @Override
    public void setStrictFirstAndLastChars(CTOnOff strictFirstAndLastChars) {
        this.generatedSetterHelperImpl(strictFirstAndLastChars, PROPERTY_QNAME[62], 0, (short)1);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTOnOff addNewStrictFirstAndLastChars() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTOnOff target = null;
            target = (CTOnOff)((Object)this.get_store().add_element_user(PROPERTY_QNAME[62]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void unsetStrictFirstAndLastChars() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.get_store().remove_element(PROPERTY_QNAME[62], 0);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTKinsoku getNoLineBreaksAfter() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTKinsoku target = null;
            target = (CTKinsoku)this.get_store().find_element_user(PROPERTY_QNAME[63], 0);
            return target == null ? null : target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean isSetNoLineBreaksAfter() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[63]) != 0;
        }
    }

    @Override
    public void setNoLineBreaksAfter(CTKinsoku noLineBreaksAfter) {
        this.generatedSetterHelperImpl((XmlObject)noLineBreaksAfter, PROPERTY_QNAME[63], 0, (short)1);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTKinsoku addNewNoLineBreaksAfter() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTKinsoku target = null;
            target = (CTKinsoku)this.get_store().add_element_user(PROPERTY_QNAME[63]);
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void unsetNoLineBreaksAfter() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.get_store().remove_element(PROPERTY_QNAME[63], 0);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTKinsoku getNoLineBreaksBefore() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTKinsoku target = null;
            target = (CTKinsoku)this.get_store().find_element_user(PROPERTY_QNAME[64], 0);
            return target == null ? null : target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean isSetNoLineBreaksBefore() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[64]) != 0;
        }
    }

    @Override
    public void setNoLineBreaksBefore(CTKinsoku noLineBreaksBefore) {
        this.generatedSetterHelperImpl((XmlObject)noLineBreaksBefore, PROPERTY_QNAME[64], 0, (short)1);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTKinsoku addNewNoLineBreaksBefore() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTKinsoku target = null;
            target = (CTKinsoku)this.get_store().add_element_user(PROPERTY_QNAME[64]);
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void unsetNoLineBreaksBefore() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.get_store().remove_element(PROPERTY_QNAME[64], 0);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTOnOff getSavePreviewPicture() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTOnOff target = null;
            target = (CTOnOff)((Object)this.get_store().find_element_user(PROPERTY_QNAME[65], 0));
            return target == null ? null : target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean isSetSavePreviewPicture() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[65]) != 0;
        }
    }

    @Override
    public void setSavePreviewPicture(CTOnOff savePreviewPicture) {
        this.generatedSetterHelperImpl(savePreviewPicture, PROPERTY_QNAME[65], 0, (short)1);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTOnOff addNewSavePreviewPicture() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTOnOff target = null;
            target = (CTOnOff)((Object)this.get_store().add_element_user(PROPERTY_QNAME[65]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void unsetSavePreviewPicture() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.get_store().remove_element(PROPERTY_QNAME[65], 0);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTOnOff getDoNotValidateAgainstSchema() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTOnOff target = null;
            target = (CTOnOff)((Object)this.get_store().find_element_user(PROPERTY_QNAME[66], 0));
            return target == null ? null : target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean isSetDoNotValidateAgainstSchema() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[66]) != 0;
        }
    }

    @Override
    public void setDoNotValidateAgainstSchema(CTOnOff doNotValidateAgainstSchema) {
        this.generatedSetterHelperImpl(doNotValidateAgainstSchema, PROPERTY_QNAME[66], 0, (short)1);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTOnOff addNewDoNotValidateAgainstSchema() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTOnOff target = null;
            target = (CTOnOff)((Object)this.get_store().add_element_user(PROPERTY_QNAME[66]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void unsetDoNotValidateAgainstSchema() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.get_store().remove_element(PROPERTY_QNAME[66], 0);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTOnOff getSaveInvalidXml() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTOnOff target = null;
            target = (CTOnOff)((Object)this.get_store().find_element_user(PROPERTY_QNAME[67], 0));
            return target == null ? null : target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean isSetSaveInvalidXml() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[67]) != 0;
        }
    }

    @Override
    public void setSaveInvalidXml(CTOnOff saveInvalidXml) {
        this.generatedSetterHelperImpl(saveInvalidXml, PROPERTY_QNAME[67], 0, (short)1);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTOnOff addNewSaveInvalidXml() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTOnOff target = null;
            target = (CTOnOff)((Object)this.get_store().add_element_user(PROPERTY_QNAME[67]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void unsetSaveInvalidXml() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.get_store().remove_element(PROPERTY_QNAME[67], 0);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTOnOff getIgnoreMixedContent() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTOnOff target = null;
            target = (CTOnOff)((Object)this.get_store().find_element_user(PROPERTY_QNAME[68], 0));
            return target == null ? null : target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean isSetIgnoreMixedContent() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[68]) != 0;
        }
    }

    @Override
    public void setIgnoreMixedContent(CTOnOff ignoreMixedContent) {
        this.generatedSetterHelperImpl(ignoreMixedContent, PROPERTY_QNAME[68], 0, (short)1);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTOnOff addNewIgnoreMixedContent() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTOnOff target = null;
            target = (CTOnOff)((Object)this.get_store().add_element_user(PROPERTY_QNAME[68]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void unsetIgnoreMixedContent() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.get_store().remove_element(PROPERTY_QNAME[68], 0);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTOnOff getAlwaysShowPlaceholderText() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTOnOff target = null;
            target = (CTOnOff)((Object)this.get_store().find_element_user(PROPERTY_QNAME[69], 0));
            return target == null ? null : target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean isSetAlwaysShowPlaceholderText() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[69]) != 0;
        }
    }

    @Override
    public void setAlwaysShowPlaceholderText(CTOnOff alwaysShowPlaceholderText) {
        this.generatedSetterHelperImpl(alwaysShowPlaceholderText, PROPERTY_QNAME[69], 0, (short)1);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTOnOff addNewAlwaysShowPlaceholderText() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTOnOff target = null;
            target = (CTOnOff)((Object)this.get_store().add_element_user(PROPERTY_QNAME[69]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void unsetAlwaysShowPlaceholderText() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.get_store().remove_element(PROPERTY_QNAME[69], 0);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTOnOff getDoNotDemarcateInvalidXml() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTOnOff target = null;
            target = (CTOnOff)((Object)this.get_store().find_element_user(PROPERTY_QNAME[70], 0));
            return target == null ? null : target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean isSetDoNotDemarcateInvalidXml() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[70]) != 0;
        }
    }

    @Override
    public void setDoNotDemarcateInvalidXml(CTOnOff doNotDemarcateInvalidXml) {
        this.generatedSetterHelperImpl(doNotDemarcateInvalidXml, PROPERTY_QNAME[70], 0, (short)1);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTOnOff addNewDoNotDemarcateInvalidXml() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTOnOff target = null;
            target = (CTOnOff)((Object)this.get_store().add_element_user(PROPERTY_QNAME[70]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void unsetDoNotDemarcateInvalidXml() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.get_store().remove_element(PROPERTY_QNAME[70], 0);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTOnOff getSaveXmlDataOnly() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTOnOff target = null;
            target = (CTOnOff)((Object)this.get_store().find_element_user(PROPERTY_QNAME[71], 0));
            return target == null ? null : target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean isSetSaveXmlDataOnly() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[71]) != 0;
        }
    }

    @Override
    public void setSaveXmlDataOnly(CTOnOff saveXmlDataOnly) {
        this.generatedSetterHelperImpl(saveXmlDataOnly, PROPERTY_QNAME[71], 0, (short)1);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTOnOff addNewSaveXmlDataOnly() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTOnOff target = null;
            target = (CTOnOff)((Object)this.get_store().add_element_user(PROPERTY_QNAME[71]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void unsetSaveXmlDataOnly() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.get_store().remove_element(PROPERTY_QNAME[71], 0);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTOnOff getUseXSLTWhenSaving() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTOnOff target = null;
            target = (CTOnOff)((Object)this.get_store().find_element_user(PROPERTY_QNAME[72], 0));
            return target == null ? null : target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean isSetUseXSLTWhenSaving() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[72]) != 0;
        }
    }

    @Override
    public void setUseXSLTWhenSaving(CTOnOff useXSLTWhenSaving) {
        this.generatedSetterHelperImpl(useXSLTWhenSaving, PROPERTY_QNAME[72], 0, (short)1);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTOnOff addNewUseXSLTWhenSaving() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTOnOff target = null;
            target = (CTOnOff)((Object)this.get_store().add_element_user(PROPERTY_QNAME[72]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void unsetUseXSLTWhenSaving() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.get_store().remove_element(PROPERTY_QNAME[72], 0);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTSaveThroughXslt getSaveThroughXslt() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTSaveThroughXslt target = null;
            target = (CTSaveThroughXslt)this.get_store().find_element_user(PROPERTY_QNAME[73], 0);
            return target == null ? null : target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean isSetSaveThroughXslt() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[73]) != 0;
        }
    }

    @Override
    public void setSaveThroughXslt(CTSaveThroughXslt saveThroughXslt) {
        this.generatedSetterHelperImpl((XmlObject)saveThroughXslt, PROPERTY_QNAME[73], 0, (short)1);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTSaveThroughXslt addNewSaveThroughXslt() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTSaveThroughXslt target = null;
            target = (CTSaveThroughXslt)this.get_store().add_element_user(PROPERTY_QNAME[73]);
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void unsetSaveThroughXslt() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.get_store().remove_element(PROPERTY_QNAME[73], 0);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTOnOff getShowXMLTags() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTOnOff target = null;
            target = (CTOnOff)((Object)this.get_store().find_element_user(PROPERTY_QNAME[74], 0));
            return target == null ? null : target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean isSetShowXMLTags() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[74]) != 0;
        }
    }

    @Override
    public void setShowXMLTags(CTOnOff showXMLTags) {
        this.generatedSetterHelperImpl(showXMLTags, PROPERTY_QNAME[74], 0, (short)1);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTOnOff addNewShowXMLTags() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTOnOff target = null;
            target = (CTOnOff)((Object)this.get_store().add_element_user(PROPERTY_QNAME[74]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void unsetShowXMLTags() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.get_store().remove_element(PROPERTY_QNAME[74], 0);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTOnOff getAlwaysMergeEmptyNamespace() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTOnOff target = null;
            target = (CTOnOff)((Object)this.get_store().find_element_user(PROPERTY_QNAME[75], 0));
            return target == null ? null : target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean isSetAlwaysMergeEmptyNamespace() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[75]) != 0;
        }
    }

    @Override
    public void setAlwaysMergeEmptyNamespace(CTOnOff alwaysMergeEmptyNamespace) {
        this.generatedSetterHelperImpl(alwaysMergeEmptyNamespace, PROPERTY_QNAME[75], 0, (short)1);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTOnOff addNewAlwaysMergeEmptyNamespace() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTOnOff target = null;
            target = (CTOnOff)((Object)this.get_store().add_element_user(PROPERTY_QNAME[75]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void unsetAlwaysMergeEmptyNamespace() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.get_store().remove_element(PROPERTY_QNAME[75], 0);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTOnOff getUpdateFields() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTOnOff target = null;
            target = (CTOnOff)((Object)this.get_store().find_element_user(PROPERTY_QNAME[76], 0));
            return target == null ? null : target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean isSetUpdateFields() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[76]) != 0;
        }
    }

    @Override
    public void setUpdateFields(CTOnOff updateFields) {
        this.generatedSetterHelperImpl(updateFields, PROPERTY_QNAME[76], 0, (short)1);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTOnOff addNewUpdateFields() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTOnOff target = null;
            target = (CTOnOff)((Object)this.get_store().add_element_user(PROPERTY_QNAME[76]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void unsetUpdateFields() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.get_store().remove_element(PROPERTY_QNAME[76], 0);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTShapeDefaults getHdrShapeDefaults() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTShapeDefaults target = null;
            target = (CTShapeDefaults)this.get_store().find_element_user(PROPERTY_QNAME[77], 0);
            return target == null ? null : target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean isSetHdrShapeDefaults() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[77]) != 0;
        }
    }

    @Override
    public void setHdrShapeDefaults(CTShapeDefaults hdrShapeDefaults) {
        this.generatedSetterHelperImpl((XmlObject)hdrShapeDefaults, PROPERTY_QNAME[77], 0, (short)1);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTShapeDefaults addNewHdrShapeDefaults() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTShapeDefaults target = null;
            target = (CTShapeDefaults)this.get_store().add_element_user(PROPERTY_QNAME[77]);
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void unsetHdrShapeDefaults() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.get_store().remove_element(PROPERTY_QNAME[77], 0);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTFtnDocProps getFootnotePr() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTFtnDocProps target = null;
            target = (CTFtnDocProps)this.get_store().find_element_user(PROPERTY_QNAME[78], 0);
            return target == null ? null : target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean isSetFootnotePr() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[78]) != 0;
        }
    }

    @Override
    public void setFootnotePr(CTFtnDocProps footnotePr) {
        this.generatedSetterHelperImpl((XmlObject)footnotePr, PROPERTY_QNAME[78], 0, (short)1);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTFtnDocProps addNewFootnotePr() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTFtnDocProps target = null;
            target = (CTFtnDocProps)this.get_store().add_element_user(PROPERTY_QNAME[78]);
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void unsetFootnotePr() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.get_store().remove_element(PROPERTY_QNAME[78], 0);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTEdnDocProps getEndnotePr() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTEdnDocProps target = null;
            target = (CTEdnDocProps)this.get_store().find_element_user(PROPERTY_QNAME[79], 0);
            return target == null ? null : target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean isSetEndnotePr() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[79]) != 0;
        }
    }

    @Override
    public void setEndnotePr(CTEdnDocProps endnotePr) {
        this.generatedSetterHelperImpl((XmlObject)endnotePr, PROPERTY_QNAME[79], 0, (short)1);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTEdnDocProps addNewEndnotePr() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTEdnDocProps target = null;
            target = (CTEdnDocProps)this.get_store().add_element_user(PROPERTY_QNAME[79]);
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void unsetEndnotePr() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.get_store().remove_element(PROPERTY_QNAME[79], 0);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTCompat getCompat() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTCompat target = null;
            target = (CTCompat)this.get_store().find_element_user(PROPERTY_QNAME[80], 0);
            return target == null ? null : target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean isSetCompat() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[80]) != 0;
        }
    }

    @Override
    public void setCompat(CTCompat compat) {
        this.generatedSetterHelperImpl((XmlObject)compat, PROPERTY_QNAME[80], 0, (short)1);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTCompat addNewCompat() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTCompat target = null;
            target = (CTCompat)this.get_store().add_element_user(PROPERTY_QNAME[80]);
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void unsetCompat() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.get_store().remove_element(PROPERTY_QNAME[80], 0);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTDocVars getDocVars() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTDocVars target = null;
            target = (CTDocVars)this.get_store().find_element_user(PROPERTY_QNAME[81], 0);
            return target == null ? null : target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean isSetDocVars() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[81]) != 0;
        }
    }

    @Override
    public void setDocVars(CTDocVars docVars) {
        this.generatedSetterHelperImpl((XmlObject)docVars, PROPERTY_QNAME[81], 0, (short)1);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTDocVars addNewDocVars() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTDocVars target = null;
            target = (CTDocVars)this.get_store().add_element_user(PROPERTY_QNAME[81]);
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void unsetDocVars() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.get_store().remove_element(PROPERTY_QNAME[81], 0);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTDocRsids getRsids() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTDocRsids target = null;
            target = (CTDocRsids)this.get_store().find_element_user(PROPERTY_QNAME[82], 0);
            return target == null ? null : target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean isSetRsids() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[82]) != 0;
        }
    }

    @Override
    public void setRsids(CTDocRsids rsids) {
        this.generatedSetterHelperImpl((XmlObject)rsids, PROPERTY_QNAME[82], 0, (short)1);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTDocRsids addNewRsids() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTDocRsids target = null;
            target = (CTDocRsids)this.get_store().add_element_user(PROPERTY_QNAME[82]);
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void unsetRsids() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.get_store().remove_element(PROPERTY_QNAME[82], 0);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTMathPr getMathPr() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTMathPr target = null;
            target = (CTMathPr)this.get_store().find_element_user(PROPERTY_QNAME[83], 0);
            return target == null ? null : target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean isSetMathPr() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[83]) != 0;
        }
    }

    @Override
    public void setMathPr(CTMathPr mathPr) {
        this.generatedSetterHelperImpl((XmlObject)mathPr, PROPERTY_QNAME[83], 0, (short)1);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTMathPr addNewMathPr() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTMathPr target = null;
            target = (CTMathPr)this.get_store().add_element_user(PROPERTY_QNAME[83]);
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void unsetMathPr() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.get_store().remove_element(PROPERTY_QNAME[83], 0);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public List<CTString> getAttachedSchemaList() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return new JavaListXmlObject<CTString>(this::getAttachedSchemaArray, this::setAttachedSchemaArray, this::insertNewAttachedSchema, this::removeAttachedSchema, this::sizeOfAttachedSchemaArray);
        }
    }

    @Override
    public CTString[] getAttachedSchemaArray() {
        return (CTString[])this.getXmlObjectArray(PROPERTY_QNAME[84], new CTString[0]);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTString getAttachedSchemaArray(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTString target = null;
            target = (CTString)((Object)this.get_store().find_element_user(PROPERTY_QNAME[84], i));
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
    public int sizeOfAttachedSchemaArray() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[84]);
        }
    }

    @Override
    public void setAttachedSchemaArray(CTString[] attachedSchemaArray) {
        this.check_orphaned();
        this.arraySetterHelper(attachedSchemaArray, PROPERTY_QNAME[84]);
    }

    @Override
    public void setAttachedSchemaArray(int i, CTString attachedSchema) {
        this.generatedSetterHelperImpl(attachedSchema, PROPERTY_QNAME[84], i, (short)2);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTString insertNewAttachedSchema(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTString target = null;
            target = (CTString)((Object)this.get_store().insert_element_user(PROPERTY_QNAME[84], i));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTString addNewAttachedSchema() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTString target = null;
            target = (CTString)((Object)this.get_store().add_element_user(PROPERTY_QNAME[84]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void removeAttachedSchema(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.get_store().remove_element(PROPERTY_QNAME[84], i);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTLanguage getThemeFontLang() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTLanguage target = null;
            target = (CTLanguage)((Object)this.get_store().find_element_user(PROPERTY_QNAME[85], 0));
            return target == null ? null : target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean isSetThemeFontLang() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[85]) != 0;
        }
    }

    @Override
    public void setThemeFontLang(CTLanguage themeFontLang) {
        this.generatedSetterHelperImpl(themeFontLang, PROPERTY_QNAME[85], 0, (short)1);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTLanguage addNewThemeFontLang() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTLanguage target = null;
            target = (CTLanguage)((Object)this.get_store().add_element_user(PROPERTY_QNAME[85]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void unsetThemeFontLang() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.get_store().remove_element(PROPERTY_QNAME[85], 0);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTColorSchemeMapping getClrSchemeMapping() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTColorSchemeMapping target = null;
            target = (CTColorSchemeMapping)this.get_store().find_element_user(PROPERTY_QNAME[86], 0);
            return target == null ? null : target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean isSetClrSchemeMapping() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[86]) != 0;
        }
    }

    @Override
    public void setClrSchemeMapping(CTColorSchemeMapping clrSchemeMapping) {
        this.generatedSetterHelperImpl((XmlObject)clrSchemeMapping, PROPERTY_QNAME[86], 0, (short)1);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTColorSchemeMapping addNewClrSchemeMapping() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTColorSchemeMapping target = null;
            target = (CTColorSchemeMapping)this.get_store().add_element_user(PROPERTY_QNAME[86]);
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void unsetClrSchemeMapping() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.get_store().remove_element(PROPERTY_QNAME[86], 0);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTOnOff getDoNotIncludeSubdocsInStats() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTOnOff target = null;
            target = (CTOnOff)((Object)this.get_store().find_element_user(PROPERTY_QNAME[87], 0));
            return target == null ? null : target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean isSetDoNotIncludeSubdocsInStats() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[87]) != 0;
        }
    }

    @Override
    public void setDoNotIncludeSubdocsInStats(CTOnOff doNotIncludeSubdocsInStats) {
        this.generatedSetterHelperImpl(doNotIncludeSubdocsInStats, PROPERTY_QNAME[87], 0, (short)1);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTOnOff addNewDoNotIncludeSubdocsInStats() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTOnOff target = null;
            target = (CTOnOff)((Object)this.get_store().add_element_user(PROPERTY_QNAME[87]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void unsetDoNotIncludeSubdocsInStats() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.get_store().remove_element(PROPERTY_QNAME[87], 0);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTOnOff getDoNotAutoCompressPictures() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTOnOff target = null;
            target = (CTOnOff)((Object)this.get_store().find_element_user(PROPERTY_QNAME[88], 0));
            return target == null ? null : target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean isSetDoNotAutoCompressPictures() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[88]) != 0;
        }
    }

    @Override
    public void setDoNotAutoCompressPictures(CTOnOff doNotAutoCompressPictures) {
        this.generatedSetterHelperImpl(doNotAutoCompressPictures, PROPERTY_QNAME[88], 0, (short)1);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTOnOff addNewDoNotAutoCompressPictures() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTOnOff target = null;
            target = (CTOnOff)((Object)this.get_store().add_element_user(PROPERTY_QNAME[88]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void unsetDoNotAutoCompressPictures() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.get_store().remove_element(PROPERTY_QNAME[88], 0);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTEmpty getForceUpgrade() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTEmpty target = null;
            target = (CTEmpty)((Object)this.get_store().find_element_user(PROPERTY_QNAME[89], 0));
            return target == null ? null : target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean isSetForceUpgrade() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[89]) != 0;
        }
    }

    @Override
    public void setForceUpgrade(CTEmpty forceUpgrade) {
        this.generatedSetterHelperImpl(forceUpgrade, PROPERTY_QNAME[89], 0, (short)1);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTEmpty addNewForceUpgrade() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTEmpty target = null;
            target = (CTEmpty)((Object)this.get_store().add_element_user(PROPERTY_QNAME[89]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void unsetForceUpgrade() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.get_store().remove_element(PROPERTY_QNAME[89], 0);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTCaptions getCaptions() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTCaptions target = null;
            target = (CTCaptions)this.get_store().find_element_user(PROPERTY_QNAME[90], 0);
            return target == null ? null : target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean isSetCaptions() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[90]) != 0;
        }
    }

    @Override
    public void setCaptions(CTCaptions captions) {
        this.generatedSetterHelperImpl((XmlObject)captions, PROPERTY_QNAME[90], 0, (short)1);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTCaptions addNewCaptions() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTCaptions target = null;
            target = (CTCaptions)this.get_store().add_element_user(PROPERTY_QNAME[90]);
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void unsetCaptions() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.get_store().remove_element(PROPERTY_QNAME[90], 0);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTReadingModeInkLockDown getReadModeInkLockDown() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTReadingModeInkLockDown target = null;
            target = (CTReadingModeInkLockDown)this.get_store().find_element_user(PROPERTY_QNAME[91], 0);
            return target == null ? null : target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean isSetReadModeInkLockDown() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[91]) != 0;
        }
    }

    @Override
    public void setReadModeInkLockDown(CTReadingModeInkLockDown readModeInkLockDown) {
        this.generatedSetterHelperImpl((XmlObject)readModeInkLockDown, PROPERTY_QNAME[91], 0, (short)1);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTReadingModeInkLockDown addNewReadModeInkLockDown() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTReadingModeInkLockDown target = null;
            target = (CTReadingModeInkLockDown)this.get_store().add_element_user(PROPERTY_QNAME[91]);
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void unsetReadModeInkLockDown() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.get_store().remove_element(PROPERTY_QNAME[91], 0);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public List<CTSmartTagType> getSmartTagTypeList() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return new JavaListXmlObject<CTSmartTagType>(this::getSmartTagTypeArray, this::setSmartTagTypeArray, this::insertNewSmartTagType, this::removeSmartTagType, this::sizeOfSmartTagTypeArray);
        }
    }

    @Override
    public CTSmartTagType[] getSmartTagTypeArray() {
        return (CTSmartTagType[])this.getXmlObjectArray(PROPERTY_QNAME[92], (XmlObject[])new CTSmartTagType[0]);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTSmartTagType getSmartTagTypeArray(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTSmartTagType target = null;
            target = (CTSmartTagType)this.get_store().find_element_user(PROPERTY_QNAME[92], i);
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
    public int sizeOfSmartTagTypeArray() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[92]);
        }
    }

    @Override
    public void setSmartTagTypeArray(CTSmartTagType[] smartTagTypeArray) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])smartTagTypeArray, PROPERTY_QNAME[92]);
    }

    @Override
    public void setSmartTagTypeArray(int i, CTSmartTagType smartTagType) {
        this.generatedSetterHelperImpl((XmlObject)smartTagType, PROPERTY_QNAME[92], i, (short)2);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTSmartTagType insertNewSmartTagType(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTSmartTagType target = null;
            target = (CTSmartTagType)this.get_store().insert_element_user(PROPERTY_QNAME[92], i);
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTSmartTagType addNewSmartTagType() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTSmartTagType target = null;
            target = (CTSmartTagType)this.get_store().add_element_user(PROPERTY_QNAME[92]);
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void removeSmartTagType(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.get_store().remove_element(PROPERTY_QNAME[92], i);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTSchemaLibrary getSchemaLibrary() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTSchemaLibrary target = null;
            target = (CTSchemaLibrary)this.get_store().find_element_user(PROPERTY_QNAME[93], 0);
            return target == null ? null : target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean isSetSchemaLibrary() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[93]) != 0;
        }
    }

    @Override
    public void setSchemaLibrary(CTSchemaLibrary schemaLibrary) {
        this.generatedSetterHelperImpl((XmlObject)schemaLibrary, PROPERTY_QNAME[93], 0, (short)1);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTSchemaLibrary addNewSchemaLibrary() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTSchemaLibrary target = null;
            target = (CTSchemaLibrary)this.get_store().add_element_user(PROPERTY_QNAME[93]);
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void unsetSchemaLibrary() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.get_store().remove_element(PROPERTY_QNAME[93], 0);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTShapeDefaults getShapeDefaults() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTShapeDefaults target = null;
            target = (CTShapeDefaults)this.get_store().find_element_user(PROPERTY_QNAME[94], 0);
            return target == null ? null : target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean isSetShapeDefaults() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[94]) != 0;
        }
    }

    @Override
    public void setShapeDefaults(CTShapeDefaults shapeDefaults) {
        this.generatedSetterHelperImpl((XmlObject)shapeDefaults, PROPERTY_QNAME[94], 0, (short)1);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTShapeDefaults addNewShapeDefaults() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTShapeDefaults target = null;
            target = (CTShapeDefaults)this.get_store().add_element_user(PROPERTY_QNAME[94]);
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void unsetShapeDefaults() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.get_store().remove_element(PROPERTY_QNAME[94], 0);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTOnOff getDoNotEmbedSmartTags() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTOnOff target = null;
            target = (CTOnOff)((Object)this.get_store().find_element_user(PROPERTY_QNAME[95], 0));
            return target == null ? null : target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean isSetDoNotEmbedSmartTags() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[95]) != 0;
        }
    }

    @Override
    public void setDoNotEmbedSmartTags(CTOnOff doNotEmbedSmartTags) {
        this.generatedSetterHelperImpl(doNotEmbedSmartTags, PROPERTY_QNAME[95], 0, (short)1);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTOnOff addNewDoNotEmbedSmartTags() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTOnOff target = null;
            target = (CTOnOff)((Object)this.get_store().add_element_user(PROPERTY_QNAME[95]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void unsetDoNotEmbedSmartTags() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.get_store().remove_element(PROPERTY_QNAME[95], 0);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTString getDecimalSymbol() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTString target = null;
            target = (CTString)((Object)this.get_store().find_element_user(PROPERTY_QNAME[96], 0));
            return target == null ? null : target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean isSetDecimalSymbol() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[96]) != 0;
        }
    }

    @Override
    public void setDecimalSymbol(CTString decimalSymbol) {
        this.generatedSetterHelperImpl(decimalSymbol, PROPERTY_QNAME[96], 0, (short)1);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTString addNewDecimalSymbol() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTString target = null;
            target = (CTString)((Object)this.get_store().add_element_user(PROPERTY_QNAME[96]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void unsetDecimalSymbol() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.get_store().remove_element(PROPERTY_QNAME[96], 0);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTString getListSeparator() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTString target = null;
            target = (CTString)((Object)this.get_store().find_element_user(PROPERTY_QNAME[97], 0));
            return target == null ? null : target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean isSetListSeparator() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[97]) != 0;
        }
    }

    @Override
    public void setListSeparator(CTString listSeparator) {
        this.generatedSetterHelperImpl(listSeparator, PROPERTY_QNAME[97], 0, (short)1);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTString addNewListSeparator() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTString target = null;
            target = (CTString)((Object)this.get_store().add_element_user(PROPERTY_QNAME[97]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void unsetListSeparator() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.get_store().remove_element(PROPERTY_QNAME[97], 0);
        }
    }
}


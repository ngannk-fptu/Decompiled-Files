/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.openxmlformats.schemas.officeDocument.x2006.sharedTypes.STConformanceClass
 *  org.openxmlformats.schemas.spreadsheetml.x2006.main.CTSmartTagPr
 *  org.openxmlformats.schemas.spreadsheetml.x2006.main.CTSmartTagTypes
 *  org.openxmlformats.schemas.spreadsheetml.x2006.main.CTWebPublishObjects
 */
package org.openxmlformats.schemas.spreadsheetml.x2006.main.impl;

import java.util.List;
import javax.xml.namespace.QName;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.SimpleValue;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.impl.values.JavaListXmlObject;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;
import org.openxmlformats.schemas.officeDocument.x2006.sharedTypes.STConformanceClass;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTBookViews;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTCalcPr;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTCustomWorkbookViews;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTDefinedNames;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTExtensionList;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTExternalReferences;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTFileRecoveryPr;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTFileSharing;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTFileVersion;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTFunctionGroups;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTOleSize;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTPivotCaches;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTSheets;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTSmartTagPr;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTSmartTagTypes;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTWebPublishObjects;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTWebPublishing;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTWorkbook;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTWorkbookPr;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTWorkbookProtection;

public class CTWorkbookImpl
extends XmlComplexContentImpl
implements CTWorkbook {
    private static final long serialVersionUID = 1L;
    private static final QName[] PROPERTY_QNAME = new QName[]{new QName("http://schemas.openxmlformats.org/spreadsheetml/2006/main", "fileVersion"), new QName("http://schemas.openxmlformats.org/spreadsheetml/2006/main", "fileSharing"), new QName("http://schemas.openxmlformats.org/spreadsheetml/2006/main", "workbookPr"), new QName("http://schemas.openxmlformats.org/spreadsheetml/2006/main", "workbookProtection"), new QName("http://schemas.openxmlformats.org/spreadsheetml/2006/main", "bookViews"), new QName("http://schemas.openxmlformats.org/spreadsheetml/2006/main", "sheets"), new QName("http://schemas.openxmlformats.org/spreadsheetml/2006/main", "functionGroups"), new QName("http://schemas.openxmlformats.org/spreadsheetml/2006/main", "externalReferences"), new QName("http://schemas.openxmlformats.org/spreadsheetml/2006/main", "definedNames"), new QName("http://schemas.openxmlformats.org/spreadsheetml/2006/main", "calcPr"), new QName("http://schemas.openxmlformats.org/spreadsheetml/2006/main", "oleSize"), new QName("http://schemas.openxmlformats.org/spreadsheetml/2006/main", "customWorkbookViews"), new QName("http://schemas.openxmlformats.org/spreadsheetml/2006/main", "pivotCaches"), new QName("http://schemas.openxmlformats.org/spreadsheetml/2006/main", "smartTagPr"), new QName("http://schemas.openxmlformats.org/spreadsheetml/2006/main", "smartTagTypes"), new QName("http://schemas.openxmlformats.org/spreadsheetml/2006/main", "webPublishing"), new QName("http://schemas.openxmlformats.org/spreadsheetml/2006/main", "fileRecoveryPr"), new QName("http://schemas.openxmlformats.org/spreadsheetml/2006/main", "webPublishObjects"), new QName("http://schemas.openxmlformats.org/spreadsheetml/2006/main", "extLst"), new QName("", "conformance")};

    public CTWorkbookImpl(SchemaType sType) {
        super(sType);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTFileVersion getFileVersion() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTFileVersion target = null;
            target = (CTFileVersion)((Object)this.get_store().find_element_user(PROPERTY_QNAME[0], 0));
            return target == null ? null : target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean isSetFileVersion() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[0]) != 0;
        }
    }

    @Override
    public void setFileVersion(CTFileVersion fileVersion) {
        this.generatedSetterHelperImpl(fileVersion, PROPERTY_QNAME[0], 0, (short)1);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTFileVersion addNewFileVersion() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTFileVersion target = null;
            target = (CTFileVersion)((Object)this.get_store().add_element_user(PROPERTY_QNAME[0]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void unsetFileVersion() {
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
    public CTFileSharing getFileSharing() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTFileSharing target = null;
            target = (CTFileSharing)((Object)this.get_store().find_element_user(PROPERTY_QNAME[1], 0));
            return target == null ? null : target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean isSetFileSharing() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[1]) != 0;
        }
    }

    @Override
    public void setFileSharing(CTFileSharing fileSharing) {
        this.generatedSetterHelperImpl(fileSharing, PROPERTY_QNAME[1], 0, (short)1);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTFileSharing addNewFileSharing() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTFileSharing target = null;
            target = (CTFileSharing)((Object)this.get_store().add_element_user(PROPERTY_QNAME[1]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void unsetFileSharing() {
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
    public CTWorkbookPr getWorkbookPr() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTWorkbookPr target = null;
            target = (CTWorkbookPr)((Object)this.get_store().find_element_user(PROPERTY_QNAME[2], 0));
            return target == null ? null : target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean isSetWorkbookPr() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[2]) != 0;
        }
    }

    @Override
    public void setWorkbookPr(CTWorkbookPr workbookPr) {
        this.generatedSetterHelperImpl(workbookPr, PROPERTY_QNAME[2], 0, (short)1);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTWorkbookPr addNewWorkbookPr() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTWorkbookPr target = null;
            target = (CTWorkbookPr)((Object)this.get_store().add_element_user(PROPERTY_QNAME[2]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void unsetWorkbookPr() {
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
    public CTWorkbookProtection getWorkbookProtection() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTWorkbookProtection target = null;
            target = (CTWorkbookProtection)((Object)this.get_store().find_element_user(PROPERTY_QNAME[3], 0));
            return target == null ? null : target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean isSetWorkbookProtection() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[3]) != 0;
        }
    }

    @Override
    public void setWorkbookProtection(CTWorkbookProtection workbookProtection) {
        this.generatedSetterHelperImpl(workbookProtection, PROPERTY_QNAME[3], 0, (short)1);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTWorkbookProtection addNewWorkbookProtection() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTWorkbookProtection target = null;
            target = (CTWorkbookProtection)((Object)this.get_store().add_element_user(PROPERTY_QNAME[3]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void unsetWorkbookProtection() {
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
    public CTBookViews getBookViews() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTBookViews target = null;
            target = (CTBookViews)((Object)this.get_store().find_element_user(PROPERTY_QNAME[4], 0));
            return target == null ? null : target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean isSetBookViews() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[4]) != 0;
        }
    }

    @Override
    public void setBookViews(CTBookViews bookViews) {
        this.generatedSetterHelperImpl(bookViews, PROPERTY_QNAME[4], 0, (short)1);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTBookViews addNewBookViews() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTBookViews target = null;
            target = (CTBookViews)((Object)this.get_store().add_element_user(PROPERTY_QNAME[4]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void unsetBookViews() {
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
    public CTSheets getSheets() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTSheets target = null;
            target = (CTSheets)((Object)this.get_store().find_element_user(PROPERTY_QNAME[5], 0));
            return target == null ? null : target;
        }
    }

    @Override
    public void setSheets(CTSheets sheets) {
        this.generatedSetterHelperImpl(sheets, PROPERTY_QNAME[5], 0, (short)1);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTSheets addNewSheets() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTSheets target = null;
            target = (CTSheets)((Object)this.get_store().add_element_user(PROPERTY_QNAME[5]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTFunctionGroups getFunctionGroups() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTFunctionGroups target = null;
            target = (CTFunctionGroups)((Object)this.get_store().find_element_user(PROPERTY_QNAME[6], 0));
            return target == null ? null : target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean isSetFunctionGroups() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[6]) != 0;
        }
    }

    @Override
    public void setFunctionGroups(CTFunctionGroups functionGroups) {
        this.generatedSetterHelperImpl(functionGroups, PROPERTY_QNAME[6], 0, (short)1);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTFunctionGroups addNewFunctionGroups() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTFunctionGroups target = null;
            target = (CTFunctionGroups)((Object)this.get_store().add_element_user(PROPERTY_QNAME[6]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void unsetFunctionGroups() {
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
    public CTExternalReferences getExternalReferences() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTExternalReferences target = null;
            target = (CTExternalReferences)((Object)this.get_store().find_element_user(PROPERTY_QNAME[7], 0));
            return target == null ? null : target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean isSetExternalReferences() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[7]) != 0;
        }
    }

    @Override
    public void setExternalReferences(CTExternalReferences externalReferences) {
        this.generatedSetterHelperImpl(externalReferences, PROPERTY_QNAME[7], 0, (short)1);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTExternalReferences addNewExternalReferences() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTExternalReferences target = null;
            target = (CTExternalReferences)((Object)this.get_store().add_element_user(PROPERTY_QNAME[7]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void unsetExternalReferences() {
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
    public CTDefinedNames getDefinedNames() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTDefinedNames target = null;
            target = (CTDefinedNames)((Object)this.get_store().find_element_user(PROPERTY_QNAME[8], 0));
            return target == null ? null : target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean isSetDefinedNames() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[8]) != 0;
        }
    }

    @Override
    public void setDefinedNames(CTDefinedNames definedNames) {
        this.generatedSetterHelperImpl(definedNames, PROPERTY_QNAME[8], 0, (short)1);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTDefinedNames addNewDefinedNames() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTDefinedNames target = null;
            target = (CTDefinedNames)((Object)this.get_store().add_element_user(PROPERTY_QNAME[8]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void unsetDefinedNames() {
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
    public CTCalcPr getCalcPr() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTCalcPr target = null;
            target = (CTCalcPr)((Object)this.get_store().find_element_user(PROPERTY_QNAME[9], 0));
            return target == null ? null : target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean isSetCalcPr() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[9]) != 0;
        }
    }

    @Override
    public void setCalcPr(CTCalcPr calcPr) {
        this.generatedSetterHelperImpl(calcPr, PROPERTY_QNAME[9], 0, (short)1);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTCalcPr addNewCalcPr() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTCalcPr target = null;
            target = (CTCalcPr)((Object)this.get_store().add_element_user(PROPERTY_QNAME[9]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void unsetCalcPr() {
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
    public CTOleSize getOleSize() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTOleSize target = null;
            target = (CTOleSize)((Object)this.get_store().find_element_user(PROPERTY_QNAME[10], 0));
            return target == null ? null : target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean isSetOleSize() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[10]) != 0;
        }
    }

    @Override
    public void setOleSize(CTOleSize oleSize) {
        this.generatedSetterHelperImpl(oleSize, PROPERTY_QNAME[10], 0, (short)1);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTOleSize addNewOleSize() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTOleSize target = null;
            target = (CTOleSize)((Object)this.get_store().add_element_user(PROPERTY_QNAME[10]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void unsetOleSize() {
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
    public CTCustomWorkbookViews getCustomWorkbookViews() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTCustomWorkbookViews target = null;
            target = (CTCustomWorkbookViews)((Object)this.get_store().find_element_user(PROPERTY_QNAME[11], 0));
            return target == null ? null : target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean isSetCustomWorkbookViews() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[11]) != 0;
        }
    }

    @Override
    public void setCustomWorkbookViews(CTCustomWorkbookViews customWorkbookViews) {
        this.generatedSetterHelperImpl(customWorkbookViews, PROPERTY_QNAME[11], 0, (short)1);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTCustomWorkbookViews addNewCustomWorkbookViews() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTCustomWorkbookViews target = null;
            target = (CTCustomWorkbookViews)((Object)this.get_store().add_element_user(PROPERTY_QNAME[11]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void unsetCustomWorkbookViews() {
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
    public CTPivotCaches getPivotCaches() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTPivotCaches target = null;
            target = (CTPivotCaches)((Object)this.get_store().find_element_user(PROPERTY_QNAME[12], 0));
            return target == null ? null : target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean isSetPivotCaches() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[12]) != 0;
        }
    }

    @Override
    public void setPivotCaches(CTPivotCaches pivotCaches) {
        this.generatedSetterHelperImpl(pivotCaches, PROPERTY_QNAME[12], 0, (short)1);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTPivotCaches addNewPivotCaches() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTPivotCaches target = null;
            target = (CTPivotCaches)((Object)this.get_store().add_element_user(PROPERTY_QNAME[12]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void unsetPivotCaches() {
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
    public CTSmartTagPr getSmartTagPr() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTSmartTagPr target = null;
            target = (CTSmartTagPr)this.get_store().find_element_user(PROPERTY_QNAME[13], 0);
            return target == null ? null : target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean isSetSmartTagPr() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[13]) != 0;
        }
    }

    @Override
    public void setSmartTagPr(CTSmartTagPr smartTagPr) {
        this.generatedSetterHelperImpl((XmlObject)smartTagPr, PROPERTY_QNAME[13], 0, (short)1);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTSmartTagPr addNewSmartTagPr() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTSmartTagPr target = null;
            target = (CTSmartTagPr)this.get_store().add_element_user(PROPERTY_QNAME[13]);
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void unsetSmartTagPr() {
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
    public CTSmartTagTypes getSmartTagTypes() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTSmartTagTypes target = null;
            target = (CTSmartTagTypes)this.get_store().find_element_user(PROPERTY_QNAME[14], 0);
            return target == null ? null : target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean isSetSmartTagTypes() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[14]) != 0;
        }
    }

    @Override
    public void setSmartTagTypes(CTSmartTagTypes smartTagTypes) {
        this.generatedSetterHelperImpl((XmlObject)smartTagTypes, PROPERTY_QNAME[14], 0, (short)1);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTSmartTagTypes addNewSmartTagTypes() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTSmartTagTypes target = null;
            target = (CTSmartTagTypes)this.get_store().add_element_user(PROPERTY_QNAME[14]);
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void unsetSmartTagTypes() {
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
    public CTWebPublishing getWebPublishing() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTWebPublishing target = null;
            target = (CTWebPublishing)((Object)this.get_store().find_element_user(PROPERTY_QNAME[15], 0));
            return target == null ? null : target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean isSetWebPublishing() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[15]) != 0;
        }
    }

    @Override
    public void setWebPublishing(CTWebPublishing webPublishing) {
        this.generatedSetterHelperImpl(webPublishing, PROPERTY_QNAME[15], 0, (short)1);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTWebPublishing addNewWebPublishing() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTWebPublishing target = null;
            target = (CTWebPublishing)((Object)this.get_store().add_element_user(PROPERTY_QNAME[15]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void unsetWebPublishing() {
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
    public List<CTFileRecoveryPr> getFileRecoveryPrList() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return new JavaListXmlObject<CTFileRecoveryPr>(this::getFileRecoveryPrArray, this::setFileRecoveryPrArray, this::insertNewFileRecoveryPr, this::removeFileRecoveryPr, this::sizeOfFileRecoveryPrArray);
        }
    }

    @Override
    public CTFileRecoveryPr[] getFileRecoveryPrArray() {
        return (CTFileRecoveryPr[])this.getXmlObjectArray(PROPERTY_QNAME[16], new CTFileRecoveryPr[0]);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTFileRecoveryPr getFileRecoveryPrArray(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTFileRecoveryPr target = null;
            target = (CTFileRecoveryPr)((Object)this.get_store().find_element_user(PROPERTY_QNAME[16], i));
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
    public int sizeOfFileRecoveryPrArray() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[16]);
        }
    }

    @Override
    public void setFileRecoveryPrArray(CTFileRecoveryPr[] fileRecoveryPrArray) {
        this.check_orphaned();
        this.arraySetterHelper(fileRecoveryPrArray, PROPERTY_QNAME[16]);
    }

    @Override
    public void setFileRecoveryPrArray(int i, CTFileRecoveryPr fileRecoveryPr) {
        this.generatedSetterHelperImpl(fileRecoveryPr, PROPERTY_QNAME[16], i, (short)2);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTFileRecoveryPr insertNewFileRecoveryPr(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTFileRecoveryPr target = null;
            target = (CTFileRecoveryPr)((Object)this.get_store().insert_element_user(PROPERTY_QNAME[16], i));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTFileRecoveryPr addNewFileRecoveryPr() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTFileRecoveryPr target = null;
            target = (CTFileRecoveryPr)((Object)this.get_store().add_element_user(PROPERTY_QNAME[16]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void removeFileRecoveryPr(int i) {
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
    public CTWebPublishObjects getWebPublishObjects() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTWebPublishObjects target = null;
            target = (CTWebPublishObjects)this.get_store().find_element_user(PROPERTY_QNAME[17], 0);
            return target == null ? null : target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean isSetWebPublishObjects() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[17]) != 0;
        }
    }

    @Override
    public void setWebPublishObjects(CTWebPublishObjects webPublishObjects) {
        this.generatedSetterHelperImpl((XmlObject)webPublishObjects, PROPERTY_QNAME[17], 0, (short)1);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTWebPublishObjects addNewWebPublishObjects() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTWebPublishObjects target = null;
            target = (CTWebPublishObjects)this.get_store().add_element_user(PROPERTY_QNAME[17]);
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void unsetWebPublishObjects() {
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
    public CTExtensionList getExtLst() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTExtensionList target = null;
            target = (CTExtensionList)((Object)this.get_store().find_element_user(PROPERTY_QNAME[18], 0));
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
            return this.get_store().count_elements(PROPERTY_QNAME[18]) != 0;
        }
    }

    @Override
    public void setExtLst(CTExtensionList extLst) {
        this.generatedSetterHelperImpl(extLst, PROPERTY_QNAME[18], 0, (short)1);
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
            target = (CTExtensionList)((Object)this.get_store().add_element_user(PROPERTY_QNAME[18]));
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
            this.get_store().remove_element(PROPERTY_QNAME[18], 0);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public STConformanceClass.Enum getConformance() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[19]));
            return target == null ? null : (STConformanceClass.Enum)target.getEnumValue();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public STConformanceClass xgetConformance() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            STConformanceClass target = null;
            target = (STConformanceClass)this.get_store().find_attribute_user(PROPERTY_QNAME[19]);
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean isSetConformance() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(PROPERTY_QNAME[19]) != null;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void setConformance(STConformanceClass.Enum conformance) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[19]));
            if (target == null) {
                target = (SimpleValue)((Object)this.get_store().add_attribute_user(PROPERTY_QNAME[19]));
            }
            target.setEnumValue(conformance);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void xsetConformance(STConformanceClass conformance) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            STConformanceClass target = null;
            target = (STConformanceClass)this.get_store().find_attribute_user(PROPERTY_QNAME[19]);
            if (target == null) {
                target = (STConformanceClass)this.get_store().add_attribute_user(PROPERTY_QNAME[19]);
            }
            target.set((XmlObject)conformance);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void unsetConformance() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.get_store().remove_attribute(PROPERTY_QNAME[19]);
        }
    }
}


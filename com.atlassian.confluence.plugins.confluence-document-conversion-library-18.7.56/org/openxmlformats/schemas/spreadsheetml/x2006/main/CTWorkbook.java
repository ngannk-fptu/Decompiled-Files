/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.openxmlformats.schemas.officeDocument.x2006.sharedTypes.STConformanceClass
 *  org.openxmlformats.schemas.spreadsheetml.x2006.main.CTSmartTagPr
 *  org.openxmlformats.schemas.spreadsheetml.x2006.main.CTSmartTagTypes
 *  org.openxmlformats.schemas.spreadsheetml.x2006.main.CTWebPublishObjects
 */
package org.openxmlformats.schemas.spreadsheetml.x2006.main;

import java.util.List;
import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
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
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTWorkbookPr;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTWorkbookProtection;

public interface CTWorkbook
extends XmlObject {
    public static final DocumentFactory<CTWorkbook> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "ctworkbook83c3type");
    public static final SchemaType type = Factory.getType();

    public CTFileVersion getFileVersion();

    public boolean isSetFileVersion();

    public void setFileVersion(CTFileVersion var1);

    public CTFileVersion addNewFileVersion();

    public void unsetFileVersion();

    public CTFileSharing getFileSharing();

    public boolean isSetFileSharing();

    public void setFileSharing(CTFileSharing var1);

    public CTFileSharing addNewFileSharing();

    public void unsetFileSharing();

    public CTWorkbookPr getWorkbookPr();

    public boolean isSetWorkbookPr();

    public void setWorkbookPr(CTWorkbookPr var1);

    public CTWorkbookPr addNewWorkbookPr();

    public void unsetWorkbookPr();

    public CTWorkbookProtection getWorkbookProtection();

    public boolean isSetWorkbookProtection();

    public void setWorkbookProtection(CTWorkbookProtection var1);

    public CTWorkbookProtection addNewWorkbookProtection();

    public void unsetWorkbookProtection();

    public CTBookViews getBookViews();

    public boolean isSetBookViews();

    public void setBookViews(CTBookViews var1);

    public CTBookViews addNewBookViews();

    public void unsetBookViews();

    public CTSheets getSheets();

    public void setSheets(CTSheets var1);

    public CTSheets addNewSheets();

    public CTFunctionGroups getFunctionGroups();

    public boolean isSetFunctionGroups();

    public void setFunctionGroups(CTFunctionGroups var1);

    public CTFunctionGroups addNewFunctionGroups();

    public void unsetFunctionGroups();

    public CTExternalReferences getExternalReferences();

    public boolean isSetExternalReferences();

    public void setExternalReferences(CTExternalReferences var1);

    public CTExternalReferences addNewExternalReferences();

    public void unsetExternalReferences();

    public CTDefinedNames getDefinedNames();

    public boolean isSetDefinedNames();

    public void setDefinedNames(CTDefinedNames var1);

    public CTDefinedNames addNewDefinedNames();

    public void unsetDefinedNames();

    public CTCalcPr getCalcPr();

    public boolean isSetCalcPr();

    public void setCalcPr(CTCalcPr var1);

    public CTCalcPr addNewCalcPr();

    public void unsetCalcPr();

    public CTOleSize getOleSize();

    public boolean isSetOleSize();

    public void setOleSize(CTOleSize var1);

    public CTOleSize addNewOleSize();

    public void unsetOleSize();

    public CTCustomWorkbookViews getCustomWorkbookViews();

    public boolean isSetCustomWorkbookViews();

    public void setCustomWorkbookViews(CTCustomWorkbookViews var1);

    public CTCustomWorkbookViews addNewCustomWorkbookViews();

    public void unsetCustomWorkbookViews();

    public CTPivotCaches getPivotCaches();

    public boolean isSetPivotCaches();

    public void setPivotCaches(CTPivotCaches var1);

    public CTPivotCaches addNewPivotCaches();

    public void unsetPivotCaches();

    public CTSmartTagPr getSmartTagPr();

    public boolean isSetSmartTagPr();

    public void setSmartTagPr(CTSmartTagPr var1);

    public CTSmartTagPr addNewSmartTagPr();

    public void unsetSmartTagPr();

    public CTSmartTagTypes getSmartTagTypes();

    public boolean isSetSmartTagTypes();

    public void setSmartTagTypes(CTSmartTagTypes var1);

    public CTSmartTagTypes addNewSmartTagTypes();

    public void unsetSmartTagTypes();

    public CTWebPublishing getWebPublishing();

    public boolean isSetWebPublishing();

    public void setWebPublishing(CTWebPublishing var1);

    public CTWebPublishing addNewWebPublishing();

    public void unsetWebPublishing();

    public List<CTFileRecoveryPr> getFileRecoveryPrList();

    public CTFileRecoveryPr[] getFileRecoveryPrArray();

    public CTFileRecoveryPr getFileRecoveryPrArray(int var1);

    public int sizeOfFileRecoveryPrArray();

    public void setFileRecoveryPrArray(CTFileRecoveryPr[] var1);

    public void setFileRecoveryPrArray(int var1, CTFileRecoveryPr var2);

    public CTFileRecoveryPr insertNewFileRecoveryPr(int var1);

    public CTFileRecoveryPr addNewFileRecoveryPr();

    public void removeFileRecoveryPr(int var1);

    public CTWebPublishObjects getWebPublishObjects();

    public boolean isSetWebPublishObjects();

    public void setWebPublishObjects(CTWebPublishObjects var1);

    public CTWebPublishObjects addNewWebPublishObjects();

    public void unsetWebPublishObjects();

    public CTExtensionList getExtLst();

    public boolean isSetExtLst();

    public void setExtLst(CTExtensionList var1);

    public CTExtensionList addNewExtLst();

    public void unsetExtLst();

    public STConformanceClass.Enum getConformance();

    public STConformanceClass xgetConformance();

    public boolean isSetConformance();

    public void setConformance(STConformanceClass.Enum var1);

    public void xsetConformance(STConformanceClass var1);

    public void unsetConformance();
}


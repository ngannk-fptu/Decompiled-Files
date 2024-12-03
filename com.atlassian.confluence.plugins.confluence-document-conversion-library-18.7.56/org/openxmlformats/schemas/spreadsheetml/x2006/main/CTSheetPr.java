/*
 * Decompiled with CFR 0.152.
 */
package org.openxmlformats.schemas.spreadsheetml.x2006.main;

import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlBoolean;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.XmlString;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTColor;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTOutlinePr;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTPageSetUpPr;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.STRef;

public interface CTSheetPr
extends XmlObject {
    public static final DocumentFactory<CTSheetPr> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "ctsheetpr3ae0type");
    public static final SchemaType type = Factory.getType();

    public CTColor getTabColor();

    public boolean isSetTabColor();

    public void setTabColor(CTColor var1);

    public CTColor addNewTabColor();

    public void unsetTabColor();

    public CTOutlinePr getOutlinePr();

    public boolean isSetOutlinePr();

    public void setOutlinePr(CTOutlinePr var1);

    public CTOutlinePr addNewOutlinePr();

    public void unsetOutlinePr();

    public CTPageSetUpPr getPageSetUpPr();

    public boolean isSetPageSetUpPr();

    public void setPageSetUpPr(CTPageSetUpPr var1);

    public CTPageSetUpPr addNewPageSetUpPr();

    public void unsetPageSetUpPr();

    public boolean getSyncHorizontal();

    public XmlBoolean xgetSyncHorizontal();

    public boolean isSetSyncHorizontal();

    public void setSyncHorizontal(boolean var1);

    public void xsetSyncHorizontal(XmlBoolean var1);

    public void unsetSyncHorizontal();

    public boolean getSyncVertical();

    public XmlBoolean xgetSyncVertical();

    public boolean isSetSyncVertical();

    public void setSyncVertical(boolean var1);

    public void xsetSyncVertical(XmlBoolean var1);

    public void unsetSyncVertical();

    public String getSyncRef();

    public STRef xgetSyncRef();

    public boolean isSetSyncRef();

    public void setSyncRef(String var1);

    public void xsetSyncRef(STRef var1);

    public void unsetSyncRef();

    public boolean getTransitionEvaluation();

    public XmlBoolean xgetTransitionEvaluation();

    public boolean isSetTransitionEvaluation();

    public void setTransitionEvaluation(boolean var1);

    public void xsetTransitionEvaluation(XmlBoolean var1);

    public void unsetTransitionEvaluation();

    public boolean getTransitionEntry();

    public XmlBoolean xgetTransitionEntry();

    public boolean isSetTransitionEntry();

    public void setTransitionEntry(boolean var1);

    public void xsetTransitionEntry(XmlBoolean var1);

    public void unsetTransitionEntry();

    public boolean getPublished();

    public XmlBoolean xgetPublished();

    public boolean isSetPublished();

    public void setPublished(boolean var1);

    public void xsetPublished(XmlBoolean var1);

    public void unsetPublished();

    public String getCodeName();

    public XmlString xgetCodeName();

    public boolean isSetCodeName();

    public void setCodeName(String var1);

    public void xsetCodeName(XmlString var1);

    public void unsetCodeName();

    public boolean getFilterMode();

    public XmlBoolean xgetFilterMode();

    public boolean isSetFilterMode();

    public void setFilterMode(boolean var1);

    public void xsetFilterMode(XmlBoolean var1);

    public void unsetFilterMode();

    public boolean getEnableFormatConditionsCalculation();

    public XmlBoolean xgetEnableFormatConditionsCalculation();

    public boolean isSetEnableFormatConditionsCalculation();

    public void setEnableFormatConditionsCalculation(boolean var1);

    public void xsetEnableFormatConditionsCalculation(XmlBoolean var1);

    public void unsetEnableFormatConditionsCalculation();
}


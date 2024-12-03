/*
 * Decompiled with CFR 0.152.
 */
package org.openxmlformats.schemas.spreadsheetml.x2006.main;

import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlBoolean;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.XmlString;
import org.apache.xmlbeans.XmlUnsignedInt;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.STObjects;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.STUpdateLinks;

public interface CTWorkbookPr
extends XmlObject {
    public static final DocumentFactory<CTWorkbookPr> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "ctworkbookpr03a5type");
    public static final SchemaType type = Factory.getType();

    public boolean getDate1904();

    public XmlBoolean xgetDate1904();

    public boolean isSetDate1904();

    public void setDate1904(boolean var1);

    public void xsetDate1904(XmlBoolean var1);

    public void unsetDate1904();

    public STObjects.Enum getShowObjects();

    public STObjects xgetShowObjects();

    public boolean isSetShowObjects();

    public void setShowObjects(STObjects.Enum var1);

    public void xsetShowObjects(STObjects var1);

    public void unsetShowObjects();

    public boolean getShowBorderUnselectedTables();

    public XmlBoolean xgetShowBorderUnselectedTables();

    public boolean isSetShowBorderUnselectedTables();

    public void setShowBorderUnselectedTables(boolean var1);

    public void xsetShowBorderUnselectedTables(XmlBoolean var1);

    public void unsetShowBorderUnselectedTables();

    public boolean getFilterPrivacy();

    public XmlBoolean xgetFilterPrivacy();

    public boolean isSetFilterPrivacy();

    public void setFilterPrivacy(boolean var1);

    public void xsetFilterPrivacy(XmlBoolean var1);

    public void unsetFilterPrivacy();

    public boolean getPromptedSolutions();

    public XmlBoolean xgetPromptedSolutions();

    public boolean isSetPromptedSolutions();

    public void setPromptedSolutions(boolean var1);

    public void xsetPromptedSolutions(XmlBoolean var1);

    public void unsetPromptedSolutions();

    public boolean getShowInkAnnotation();

    public XmlBoolean xgetShowInkAnnotation();

    public boolean isSetShowInkAnnotation();

    public void setShowInkAnnotation(boolean var1);

    public void xsetShowInkAnnotation(XmlBoolean var1);

    public void unsetShowInkAnnotation();

    public boolean getBackupFile();

    public XmlBoolean xgetBackupFile();

    public boolean isSetBackupFile();

    public void setBackupFile(boolean var1);

    public void xsetBackupFile(XmlBoolean var1);

    public void unsetBackupFile();

    public boolean getSaveExternalLinkValues();

    public XmlBoolean xgetSaveExternalLinkValues();

    public boolean isSetSaveExternalLinkValues();

    public void setSaveExternalLinkValues(boolean var1);

    public void xsetSaveExternalLinkValues(XmlBoolean var1);

    public void unsetSaveExternalLinkValues();

    public STUpdateLinks.Enum getUpdateLinks();

    public STUpdateLinks xgetUpdateLinks();

    public boolean isSetUpdateLinks();

    public void setUpdateLinks(STUpdateLinks.Enum var1);

    public void xsetUpdateLinks(STUpdateLinks var1);

    public void unsetUpdateLinks();

    public String getCodeName();

    public XmlString xgetCodeName();

    public boolean isSetCodeName();

    public void setCodeName(String var1);

    public void xsetCodeName(XmlString var1);

    public void unsetCodeName();

    public boolean getHidePivotFieldList();

    public XmlBoolean xgetHidePivotFieldList();

    public boolean isSetHidePivotFieldList();

    public void setHidePivotFieldList(boolean var1);

    public void xsetHidePivotFieldList(XmlBoolean var1);

    public void unsetHidePivotFieldList();

    public boolean getShowPivotChartFilter();

    public XmlBoolean xgetShowPivotChartFilter();

    public boolean isSetShowPivotChartFilter();

    public void setShowPivotChartFilter(boolean var1);

    public void xsetShowPivotChartFilter(XmlBoolean var1);

    public void unsetShowPivotChartFilter();

    public boolean getAllowRefreshQuery();

    public XmlBoolean xgetAllowRefreshQuery();

    public boolean isSetAllowRefreshQuery();

    public void setAllowRefreshQuery(boolean var1);

    public void xsetAllowRefreshQuery(XmlBoolean var1);

    public void unsetAllowRefreshQuery();

    public boolean getPublishItems();

    public XmlBoolean xgetPublishItems();

    public boolean isSetPublishItems();

    public void setPublishItems(boolean var1);

    public void xsetPublishItems(XmlBoolean var1);

    public void unsetPublishItems();

    public boolean getCheckCompatibility();

    public XmlBoolean xgetCheckCompatibility();

    public boolean isSetCheckCompatibility();

    public void setCheckCompatibility(boolean var1);

    public void xsetCheckCompatibility(XmlBoolean var1);

    public void unsetCheckCompatibility();

    public boolean getAutoCompressPictures();

    public XmlBoolean xgetAutoCompressPictures();

    public boolean isSetAutoCompressPictures();

    public void setAutoCompressPictures(boolean var1);

    public void xsetAutoCompressPictures(XmlBoolean var1);

    public void unsetAutoCompressPictures();

    public boolean getRefreshAllConnections();

    public XmlBoolean xgetRefreshAllConnections();

    public boolean isSetRefreshAllConnections();

    public void setRefreshAllConnections(boolean var1);

    public void xsetRefreshAllConnections(XmlBoolean var1);

    public void unsetRefreshAllConnections();

    public long getDefaultThemeVersion();

    public XmlUnsignedInt xgetDefaultThemeVersion();

    public boolean isSetDefaultThemeVersion();

    public void setDefaultThemeVersion(long var1);

    public void xsetDefaultThemeVersion(XmlUnsignedInt var1);

    public void unsetDefaultThemeVersion();
}


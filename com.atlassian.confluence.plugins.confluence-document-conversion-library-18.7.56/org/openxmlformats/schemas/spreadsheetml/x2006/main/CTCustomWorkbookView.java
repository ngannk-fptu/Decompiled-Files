/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.openxmlformats.schemas.spreadsheetml.x2006.main.STComments
 */
package org.openxmlformats.schemas.spreadsheetml.x2006.main;

import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlBoolean;
import org.apache.xmlbeans.XmlInt;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.XmlUnsignedInt;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.openxmlformats.schemas.officeDocument.x2006.sharedTypes.STGuid;
import org.openxmlformats.schemas.officeDocument.x2006.sharedTypes.STXstring;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTExtensionList;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.STComments;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.STObjects;

public interface CTCustomWorkbookView
extends XmlObject {
    public static final DocumentFactory<CTCustomWorkbookView> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "ctcustomworkbookview31d9type");
    public static final SchemaType type = Factory.getType();

    public CTExtensionList getExtLst();

    public boolean isSetExtLst();

    public void setExtLst(CTExtensionList var1);

    public CTExtensionList addNewExtLst();

    public void unsetExtLst();

    public String getName();

    public STXstring xgetName();

    public void setName(String var1);

    public void xsetName(STXstring var1);

    public String getGuid();

    public STGuid xgetGuid();

    public void setGuid(String var1);

    public void xsetGuid(STGuid var1);

    public boolean getAutoUpdate();

    public XmlBoolean xgetAutoUpdate();

    public boolean isSetAutoUpdate();

    public void setAutoUpdate(boolean var1);

    public void xsetAutoUpdate(XmlBoolean var1);

    public void unsetAutoUpdate();

    public long getMergeInterval();

    public XmlUnsignedInt xgetMergeInterval();

    public boolean isSetMergeInterval();

    public void setMergeInterval(long var1);

    public void xsetMergeInterval(XmlUnsignedInt var1);

    public void unsetMergeInterval();

    public boolean getChangesSavedWin();

    public XmlBoolean xgetChangesSavedWin();

    public boolean isSetChangesSavedWin();

    public void setChangesSavedWin(boolean var1);

    public void xsetChangesSavedWin(XmlBoolean var1);

    public void unsetChangesSavedWin();

    public boolean getOnlySync();

    public XmlBoolean xgetOnlySync();

    public boolean isSetOnlySync();

    public void setOnlySync(boolean var1);

    public void xsetOnlySync(XmlBoolean var1);

    public void unsetOnlySync();

    public boolean getPersonalView();

    public XmlBoolean xgetPersonalView();

    public boolean isSetPersonalView();

    public void setPersonalView(boolean var1);

    public void xsetPersonalView(XmlBoolean var1);

    public void unsetPersonalView();

    public boolean getIncludePrintSettings();

    public XmlBoolean xgetIncludePrintSettings();

    public boolean isSetIncludePrintSettings();

    public void setIncludePrintSettings(boolean var1);

    public void xsetIncludePrintSettings(XmlBoolean var1);

    public void unsetIncludePrintSettings();

    public boolean getIncludeHiddenRowCol();

    public XmlBoolean xgetIncludeHiddenRowCol();

    public boolean isSetIncludeHiddenRowCol();

    public void setIncludeHiddenRowCol(boolean var1);

    public void xsetIncludeHiddenRowCol(XmlBoolean var1);

    public void unsetIncludeHiddenRowCol();

    public boolean getMaximized();

    public XmlBoolean xgetMaximized();

    public boolean isSetMaximized();

    public void setMaximized(boolean var1);

    public void xsetMaximized(XmlBoolean var1);

    public void unsetMaximized();

    public boolean getMinimized();

    public XmlBoolean xgetMinimized();

    public boolean isSetMinimized();

    public void setMinimized(boolean var1);

    public void xsetMinimized(XmlBoolean var1);

    public void unsetMinimized();

    public boolean getShowHorizontalScroll();

    public XmlBoolean xgetShowHorizontalScroll();

    public boolean isSetShowHorizontalScroll();

    public void setShowHorizontalScroll(boolean var1);

    public void xsetShowHorizontalScroll(XmlBoolean var1);

    public void unsetShowHorizontalScroll();

    public boolean getShowVerticalScroll();

    public XmlBoolean xgetShowVerticalScroll();

    public boolean isSetShowVerticalScroll();

    public void setShowVerticalScroll(boolean var1);

    public void xsetShowVerticalScroll(XmlBoolean var1);

    public void unsetShowVerticalScroll();

    public boolean getShowSheetTabs();

    public XmlBoolean xgetShowSheetTabs();

    public boolean isSetShowSheetTabs();

    public void setShowSheetTabs(boolean var1);

    public void xsetShowSheetTabs(XmlBoolean var1);

    public void unsetShowSheetTabs();

    public int getXWindow();

    public XmlInt xgetXWindow();

    public boolean isSetXWindow();

    public void setXWindow(int var1);

    public void xsetXWindow(XmlInt var1);

    public void unsetXWindow();

    public int getYWindow();

    public XmlInt xgetYWindow();

    public boolean isSetYWindow();

    public void setYWindow(int var1);

    public void xsetYWindow(XmlInt var1);

    public void unsetYWindow();

    public long getWindowWidth();

    public XmlUnsignedInt xgetWindowWidth();

    public void setWindowWidth(long var1);

    public void xsetWindowWidth(XmlUnsignedInt var1);

    public long getWindowHeight();

    public XmlUnsignedInt xgetWindowHeight();

    public void setWindowHeight(long var1);

    public void xsetWindowHeight(XmlUnsignedInt var1);

    public long getTabRatio();

    public XmlUnsignedInt xgetTabRatio();

    public boolean isSetTabRatio();

    public void setTabRatio(long var1);

    public void xsetTabRatio(XmlUnsignedInt var1);

    public void unsetTabRatio();

    public long getActiveSheetId();

    public XmlUnsignedInt xgetActiveSheetId();

    public void setActiveSheetId(long var1);

    public void xsetActiveSheetId(XmlUnsignedInt var1);

    public boolean getShowFormulaBar();

    public XmlBoolean xgetShowFormulaBar();

    public boolean isSetShowFormulaBar();

    public void setShowFormulaBar(boolean var1);

    public void xsetShowFormulaBar(XmlBoolean var1);

    public void unsetShowFormulaBar();

    public boolean getShowStatusbar();

    public XmlBoolean xgetShowStatusbar();

    public boolean isSetShowStatusbar();

    public void setShowStatusbar(boolean var1);

    public void xsetShowStatusbar(XmlBoolean var1);

    public void unsetShowStatusbar();

    public STComments.Enum getShowComments();

    public STComments xgetShowComments();

    public boolean isSetShowComments();

    public void setShowComments(STComments.Enum var1);

    public void xsetShowComments(STComments var1);

    public void unsetShowComments();

    public STObjects.Enum getShowObjects();

    public STObjects xgetShowObjects();

    public boolean isSetShowObjects();

    public void setShowObjects(STObjects.Enum var1);

    public void xsetShowObjects(STObjects var1);

    public void unsetShowObjects();
}


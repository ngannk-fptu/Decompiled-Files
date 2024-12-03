/*
 * Decompiled with CFR 0.152.
 */
package org.openxmlformats.schemas.spreadsheetml.x2006.main;

import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlBoolean;
import org.apache.xmlbeans.XmlInt;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.XmlUnsignedInt;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTExtensionList;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.STVisibility;

public interface CTBookView
extends XmlObject {
    public static final DocumentFactory<CTBookView> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "ctbookviewf677type");
    public static final SchemaType type = Factory.getType();

    public CTExtensionList getExtLst();

    public boolean isSetExtLst();

    public void setExtLst(CTExtensionList var1);

    public CTExtensionList addNewExtLst();

    public void unsetExtLst();

    public STVisibility.Enum getVisibility();

    public STVisibility xgetVisibility();

    public boolean isSetVisibility();

    public void setVisibility(STVisibility.Enum var1);

    public void xsetVisibility(STVisibility var1);

    public void unsetVisibility();

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

    public boolean isSetWindowWidth();

    public void setWindowWidth(long var1);

    public void xsetWindowWidth(XmlUnsignedInt var1);

    public void unsetWindowWidth();

    public long getWindowHeight();

    public XmlUnsignedInt xgetWindowHeight();

    public boolean isSetWindowHeight();

    public void setWindowHeight(long var1);

    public void xsetWindowHeight(XmlUnsignedInt var1);

    public void unsetWindowHeight();

    public long getTabRatio();

    public XmlUnsignedInt xgetTabRatio();

    public boolean isSetTabRatio();

    public void setTabRatio(long var1);

    public void xsetTabRatio(XmlUnsignedInt var1);

    public void unsetTabRatio();

    public long getFirstSheet();

    public XmlUnsignedInt xgetFirstSheet();

    public boolean isSetFirstSheet();

    public void setFirstSheet(long var1);

    public void xsetFirstSheet(XmlUnsignedInt var1);

    public void unsetFirstSheet();

    public long getActiveTab();

    public XmlUnsignedInt xgetActiveTab();

    public boolean isSetActiveTab();

    public void setActiveTab(long var1);

    public void xsetActiveTab(XmlUnsignedInt var1);

    public void unsetActiveTab();

    public boolean getAutoFilterDateGrouping();

    public XmlBoolean xgetAutoFilterDateGrouping();

    public boolean isSetAutoFilterDateGrouping();

    public void setAutoFilterDateGrouping(boolean var1);

    public void xsetAutoFilterDateGrouping(XmlBoolean var1);

    public void unsetAutoFilterDateGrouping();
}


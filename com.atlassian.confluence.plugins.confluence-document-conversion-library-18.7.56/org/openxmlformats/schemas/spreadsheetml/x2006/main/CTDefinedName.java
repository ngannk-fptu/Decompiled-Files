/*
 * Decompiled with CFR 0.152.
 */
package org.openxmlformats.schemas.spreadsheetml.x2006.main;

import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlBoolean;
import org.apache.xmlbeans.XmlUnsignedInt;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.openxmlformats.schemas.officeDocument.x2006.sharedTypes.STXstring;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.STFormula;

public interface CTDefinedName
extends STFormula {
    public static final DocumentFactory<CTDefinedName> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "ctdefinedname9413type");
    public static final SchemaType type = Factory.getType();

    public String getName();

    public STXstring xgetName();

    public void setName(String var1);

    public void xsetName(STXstring var1);

    public String getComment();

    public STXstring xgetComment();

    public boolean isSetComment();

    public void setComment(String var1);

    public void xsetComment(STXstring var1);

    public void unsetComment();

    public String getCustomMenu();

    public STXstring xgetCustomMenu();

    public boolean isSetCustomMenu();

    public void setCustomMenu(String var1);

    public void xsetCustomMenu(STXstring var1);

    public void unsetCustomMenu();

    public String getDescription();

    public STXstring xgetDescription();

    public boolean isSetDescription();

    public void setDescription(String var1);

    public void xsetDescription(STXstring var1);

    public void unsetDescription();

    public String getHelp();

    public STXstring xgetHelp();

    public boolean isSetHelp();

    public void setHelp(String var1);

    public void xsetHelp(STXstring var1);

    public void unsetHelp();

    public String getStatusBar();

    public STXstring xgetStatusBar();

    public boolean isSetStatusBar();

    public void setStatusBar(String var1);

    public void xsetStatusBar(STXstring var1);

    public void unsetStatusBar();

    public long getLocalSheetId();

    public XmlUnsignedInt xgetLocalSheetId();

    public boolean isSetLocalSheetId();

    public void setLocalSheetId(long var1);

    public void xsetLocalSheetId(XmlUnsignedInt var1);

    public void unsetLocalSheetId();

    public boolean getHidden();

    public XmlBoolean xgetHidden();

    public boolean isSetHidden();

    public void setHidden(boolean var1);

    public void xsetHidden(XmlBoolean var1);

    public void unsetHidden();

    public boolean getFunction();

    public XmlBoolean xgetFunction();

    public boolean isSetFunction();

    public void setFunction(boolean var1);

    public void xsetFunction(XmlBoolean var1);

    public void unsetFunction();

    public boolean getVbProcedure();

    public XmlBoolean xgetVbProcedure();

    public boolean isSetVbProcedure();

    public void setVbProcedure(boolean var1);

    public void xsetVbProcedure(XmlBoolean var1);

    public void unsetVbProcedure();

    public boolean getXlm();

    public XmlBoolean xgetXlm();

    public boolean isSetXlm();

    public void setXlm(boolean var1);

    public void xsetXlm(XmlBoolean var1);

    public void unsetXlm();

    public long getFunctionGroupId();

    public XmlUnsignedInt xgetFunctionGroupId();

    public boolean isSetFunctionGroupId();

    public void setFunctionGroupId(long var1);

    public void xsetFunctionGroupId(XmlUnsignedInt var1);

    public void unsetFunctionGroupId();

    public String getShortcutKey();

    public STXstring xgetShortcutKey();

    public boolean isSetShortcutKey();

    public void setShortcutKey(String var1);

    public void xsetShortcutKey(STXstring var1);

    public void unsetShortcutKey();

    public boolean getPublishToServer();

    public XmlBoolean xgetPublishToServer();

    public boolean isSetPublishToServer();

    public void setPublishToServer(boolean var1);

    public void xsetPublishToServer(XmlBoolean var1);

    public void unsetPublishToServer();

    public boolean getWorkbookParameter();

    public XmlBoolean xgetWorkbookParameter();

    public boolean isSetWorkbookParameter();

    public void setWorkbookParameter(boolean var1);

    public void xsetWorkbookParameter(XmlBoolean var1);

    public void unsetWorkbookParameter();
}


/*
 * Decompiled with CFR 0.152.
 */
package org.openxmlformats.schemas.spreadsheetml.x2006.main;

import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlBoolean;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.impl.schema.DocumentFactory;

public interface CTFileRecoveryPr
extends XmlObject {
    public static final DocumentFactory<CTFileRecoveryPr> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "ctfilerecoveryprf05ctype");
    public static final SchemaType type = Factory.getType();

    public boolean getAutoRecover();

    public XmlBoolean xgetAutoRecover();

    public boolean isSetAutoRecover();

    public void setAutoRecover(boolean var1);

    public void xsetAutoRecover(XmlBoolean var1);

    public void unsetAutoRecover();

    public boolean getCrashSave();

    public XmlBoolean xgetCrashSave();

    public boolean isSetCrashSave();

    public void setCrashSave(boolean var1);

    public void xsetCrashSave(XmlBoolean var1);

    public void unsetCrashSave();

    public boolean getDataExtractLoad();

    public XmlBoolean xgetDataExtractLoad();

    public boolean isSetDataExtractLoad();

    public void setDataExtractLoad(boolean var1);

    public void xsetDataExtractLoad(XmlBoolean var1);

    public void unsetDataExtractLoad();

    public boolean getRepairLoad();

    public XmlBoolean xgetRepairLoad();

    public boolean isSetRepairLoad();

    public void setRepairLoad(boolean var1);

    public void xsetRepairLoad(XmlBoolean var1);

    public void unsetRepairLoad();
}


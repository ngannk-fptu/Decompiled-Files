/*
 * Decompiled with CFR 0.152.
 */
package org.openxmlformats.schemas.spreadsheetml.x2006.main;

import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlBoolean;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.openxmlformats.schemas.officeDocument.x2006.sharedTypes.STXstring;

public interface CTTableStyleInfo
extends XmlObject {
    public static final DocumentFactory<CTTableStyleInfo> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "cttablestyleinfo499atype");
    public static final SchemaType type = Factory.getType();

    public String getName();

    public STXstring xgetName();

    public boolean isSetName();

    public void setName(String var1);

    public void xsetName(STXstring var1);

    public void unsetName();

    public boolean getShowFirstColumn();

    public XmlBoolean xgetShowFirstColumn();

    public boolean isSetShowFirstColumn();

    public void setShowFirstColumn(boolean var1);

    public void xsetShowFirstColumn(XmlBoolean var1);

    public void unsetShowFirstColumn();

    public boolean getShowLastColumn();

    public XmlBoolean xgetShowLastColumn();

    public boolean isSetShowLastColumn();

    public void setShowLastColumn(boolean var1);

    public void xsetShowLastColumn(XmlBoolean var1);

    public void unsetShowLastColumn();

    public boolean getShowRowStripes();

    public XmlBoolean xgetShowRowStripes();

    public boolean isSetShowRowStripes();

    public void setShowRowStripes(boolean var1);

    public void xsetShowRowStripes(XmlBoolean var1);

    public void unsetShowRowStripes();

    public boolean getShowColumnStripes();

    public XmlBoolean xgetShowColumnStripes();

    public boolean isSetShowColumnStripes();

    public void setShowColumnStripes(boolean var1);

    public void xsetShowColumnStripes(XmlBoolean var1);

    public void unsetShowColumnStripes();
}


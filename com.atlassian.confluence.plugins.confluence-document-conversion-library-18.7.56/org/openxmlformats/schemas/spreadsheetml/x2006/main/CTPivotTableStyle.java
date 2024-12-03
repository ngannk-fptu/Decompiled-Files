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

public interface CTPivotTableStyle
extends XmlObject {
    public static final DocumentFactory<CTPivotTableStyle> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "ctpivottablestyle0f84type");
    public static final SchemaType type = Factory.getType();

    public String getName();

    public XmlString xgetName();

    public boolean isSetName();

    public void setName(String var1);

    public void xsetName(XmlString var1);

    public void unsetName();

    public boolean getShowRowHeaders();

    public XmlBoolean xgetShowRowHeaders();

    public boolean isSetShowRowHeaders();

    public void setShowRowHeaders(boolean var1);

    public void xsetShowRowHeaders(XmlBoolean var1);

    public void unsetShowRowHeaders();

    public boolean getShowColHeaders();

    public XmlBoolean xgetShowColHeaders();

    public boolean isSetShowColHeaders();

    public void setShowColHeaders(boolean var1);

    public void xsetShowColHeaders(XmlBoolean var1);

    public void unsetShowColHeaders();

    public boolean getShowRowStripes();

    public XmlBoolean xgetShowRowStripes();

    public boolean isSetShowRowStripes();

    public void setShowRowStripes(boolean var1);

    public void xsetShowRowStripes(XmlBoolean var1);

    public void unsetShowRowStripes();

    public boolean getShowColStripes();

    public XmlBoolean xgetShowColStripes();

    public boolean isSetShowColStripes();

    public void setShowColStripes(boolean var1);

    public void xsetShowColStripes(XmlBoolean var1);

    public void unsetShowColStripes();

    public boolean getShowLastColumn();

    public XmlBoolean xgetShowLastColumn();

    public boolean isSetShowLastColumn();

    public void setShowLastColumn(boolean var1);

    public void xsetShowLastColumn(XmlBoolean var1);

    public void unsetShowLastColumn();
}


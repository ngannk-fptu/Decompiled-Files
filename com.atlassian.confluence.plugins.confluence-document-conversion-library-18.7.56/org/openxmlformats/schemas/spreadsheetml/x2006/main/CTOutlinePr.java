/*
 * Decompiled with CFR 0.152.
 */
package org.openxmlformats.schemas.spreadsheetml.x2006.main;

import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlBoolean;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.impl.schema.DocumentFactory;

public interface CTOutlinePr
extends XmlObject {
    public static final DocumentFactory<CTOutlinePr> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "ctoutlineprc483type");
    public static final SchemaType type = Factory.getType();

    public boolean getApplyStyles();

    public XmlBoolean xgetApplyStyles();

    public boolean isSetApplyStyles();

    public void setApplyStyles(boolean var1);

    public void xsetApplyStyles(XmlBoolean var1);

    public void unsetApplyStyles();

    public boolean getSummaryBelow();

    public XmlBoolean xgetSummaryBelow();

    public boolean isSetSummaryBelow();

    public void setSummaryBelow(boolean var1);

    public void xsetSummaryBelow(XmlBoolean var1);

    public void unsetSummaryBelow();

    public boolean getSummaryRight();

    public XmlBoolean xgetSummaryRight();

    public boolean isSetSummaryRight();

    public void setSummaryRight(boolean var1);

    public void xsetSummaryRight(XmlBoolean var1);

    public void unsetSummaryRight();

    public boolean getShowOutlineSymbols();

    public XmlBoolean xgetShowOutlineSymbols();

    public boolean isSetShowOutlineSymbols();

    public void setShowOutlineSymbols(boolean var1);

    public void xsetShowOutlineSymbols(XmlBoolean var1);

    public void unsetShowOutlineSymbols();
}


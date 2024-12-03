/*
 * Decompiled with CFR 0.152.
 */
package org.openxmlformats.schemas.spreadsheetml.x2006.main;

import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlBoolean;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.impl.schema.DocumentFactory;

public interface CTSheetCalcPr
extends XmlObject {
    public static final DocumentFactory<CTSheetCalcPr> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "ctsheetcalcprc6d5type");
    public static final SchemaType type = Factory.getType();

    public boolean getFullCalcOnLoad();

    public XmlBoolean xgetFullCalcOnLoad();

    public boolean isSetFullCalcOnLoad();

    public void setFullCalcOnLoad(boolean var1);

    public void xsetFullCalcOnLoad(XmlBoolean var1);

    public void unsetFullCalcOnLoad();
}


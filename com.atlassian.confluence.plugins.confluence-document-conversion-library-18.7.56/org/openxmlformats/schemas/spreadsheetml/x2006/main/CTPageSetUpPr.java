/*
 * Decompiled with CFR 0.152.
 */
package org.openxmlformats.schemas.spreadsheetml.x2006.main;

import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlBoolean;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.impl.schema.DocumentFactory;

public interface CTPageSetUpPr
extends XmlObject {
    public static final DocumentFactory<CTPageSetUpPr> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "ctpagesetuppr24cftype");
    public static final SchemaType type = Factory.getType();

    public boolean getAutoPageBreaks();

    public XmlBoolean xgetAutoPageBreaks();

    public boolean isSetAutoPageBreaks();

    public void setAutoPageBreaks(boolean var1);

    public void xsetAutoPageBreaks(XmlBoolean var1);

    public void unsetAutoPageBreaks();

    public boolean getFitToPage();

    public XmlBoolean xgetFitToPage();

    public boolean isSetFitToPage();

    public void setFitToPage(boolean var1);

    public void xsetFitToPage(XmlBoolean var1);

    public void unsetFitToPage();
}


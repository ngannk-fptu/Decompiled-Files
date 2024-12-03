/*
 * Decompiled with CFR 0.152.
 */
package org.openxmlformats.schemas.spreadsheetml.x2006.main;

import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlBoolean;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.impl.schema.DocumentFactory;

public interface CTPrintOptions
extends XmlObject {
    public static final DocumentFactory<CTPrintOptions> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "ctprintoptions943atype");
    public static final SchemaType type = Factory.getType();

    public boolean getHorizontalCentered();

    public XmlBoolean xgetHorizontalCentered();

    public boolean isSetHorizontalCentered();

    public void setHorizontalCentered(boolean var1);

    public void xsetHorizontalCentered(XmlBoolean var1);

    public void unsetHorizontalCentered();

    public boolean getVerticalCentered();

    public XmlBoolean xgetVerticalCentered();

    public boolean isSetVerticalCentered();

    public void setVerticalCentered(boolean var1);

    public void xsetVerticalCentered(XmlBoolean var1);

    public void unsetVerticalCentered();

    public boolean getHeadings();

    public XmlBoolean xgetHeadings();

    public boolean isSetHeadings();

    public void setHeadings(boolean var1);

    public void xsetHeadings(XmlBoolean var1);

    public void unsetHeadings();

    public boolean getGridLines();

    public XmlBoolean xgetGridLines();

    public boolean isSetGridLines();

    public void setGridLines(boolean var1);

    public void xsetGridLines(XmlBoolean var1);

    public void unsetGridLines();

    public boolean getGridLinesSet();

    public XmlBoolean xgetGridLinesSet();

    public boolean isSetGridLinesSet();

    public void setGridLinesSet(boolean var1);

    public void xsetGridLinesSet(XmlBoolean var1);

    public void unsetGridLinesSet();
}


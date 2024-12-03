/*
 * Decompiled with CFR 0.152.
 */
package org.openxmlformats.schemas.drawingml.x2006.chart;

import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlBoolean;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.openxmlformats.schemas.officeDocument.x2006.sharedTypes.STXstring;

public interface CTHeaderFooter
extends XmlObject {
    public static final DocumentFactory<CTHeaderFooter> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "ctheaderfooter2c34type");
    public static final SchemaType type = Factory.getType();

    public String getOddHeader();

    public STXstring xgetOddHeader();

    public boolean isSetOddHeader();

    public void setOddHeader(String var1);

    public void xsetOddHeader(STXstring var1);

    public void unsetOddHeader();

    public String getOddFooter();

    public STXstring xgetOddFooter();

    public boolean isSetOddFooter();

    public void setOddFooter(String var1);

    public void xsetOddFooter(STXstring var1);

    public void unsetOddFooter();

    public String getEvenHeader();

    public STXstring xgetEvenHeader();

    public boolean isSetEvenHeader();

    public void setEvenHeader(String var1);

    public void xsetEvenHeader(STXstring var1);

    public void unsetEvenHeader();

    public String getEvenFooter();

    public STXstring xgetEvenFooter();

    public boolean isSetEvenFooter();

    public void setEvenFooter(String var1);

    public void xsetEvenFooter(STXstring var1);

    public void unsetEvenFooter();

    public String getFirstHeader();

    public STXstring xgetFirstHeader();

    public boolean isSetFirstHeader();

    public void setFirstHeader(String var1);

    public void xsetFirstHeader(STXstring var1);

    public void unsetFirstHeader();

    public String getFirstFooter();

    public STXstring xgetFirstFooter();

    public boolean isSetFirstFooter();

    public void setFirstFooter(String var1);

    public void xsetFirstFooter(STXstring var1);

    public void unsetFirstFooter();

    public boolean getAlignWithMargins();

    public XmlBoolean xgetAlignWithMargins();

    public boolean isSetAlignWithMargins();

    public void setAlignWithMargins(boolean var1);

    public void xsetAlignWithMargins(XmlBoolean var1);

    public void unsetAlignWithMargins();

    public boolean getDifferentOddEven();

    public XmlBoolean xgetDifferentOddEven();

    public boolean isSetDifferentOddEven();

    public void setDifferentOddEven(boolean var1);

    public void xsetDifferentOddEven(XmlBoolean var1);

    public void unsetDifferentOddEven();

    public boolean getDifferentFirst();

    public XmlBoolean xgetDifferentFirst();

    public boolean isSetDifferentFirst();

    public void setDifferentFirst(boolean var1);

    public void xsetDifferentFirst(XmlBoolean var1);

    public void unsetDifferentFirst();
}


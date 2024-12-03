/*
 * Decompiled with CFR 0.152.
 */
package org.openxmlformats.schemas.spreadsheetml.x2006.main;

import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlDouble;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.impl.schema.DocumentFactory;

public interface CTPageMargins
extends XmlObject {
    public static final DocumentFactory<CTPageMargins> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "ctpagemargins5455type");
    public static final SchemaType type = Factory.getType();

    public double getLeft();

    public XmlDouble xgetLeft();

    public void setLeft(double var1);

    public void xsetLeft(XmlDouble var1);

    public double getRight();

    public XmlDouble xgetRight();

    public void setRight(double var1);

    public void xsetRight(XmlDouble var1);

    public double getTop();

    public XmlDouble xgetTop();

    public void setTop(double var1);

    public void xsetTop(XmlDouble var1);

    public double getBottom();

    public XmlDouble xgetBottom();

    public void setBottom(double var1);

    public void xsetBottom(XmlDouble var1);

    public double getHeader();

    public XmlDouble xgetHeader();

    public void setHeader(double var1);

    public void xsetHeader(XmlDouble var1);

    public double getFooter();

    public XmlDouble xgetFooter();

    public void setFooter(double var1);

    public void xsetFooter(XmlDouble var1);
}


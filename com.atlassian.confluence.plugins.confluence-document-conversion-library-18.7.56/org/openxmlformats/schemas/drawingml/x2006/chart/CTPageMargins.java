/*
 * Decompiled with CFR 0.152.
 */
package org.openxmlformats.schemas.drawingml.x2006.chart;

import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlDouble;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.impl.schema.DocumentFactory;

public interface CTPageMargins
extends XmlObject {
    public static final DocumentFactory<CTPageMargins> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "ctpagemarginsb730type");
    public static final SchemaType type = Factory.getType();

    public double getL();

    public XmlDouble xgetL();

    public void setL(double var1);

    public void xsetL(XmlDouble var1);

    public double getR();

    public XmlDouble xgetR();

    public void setR(double var1);

    public void xsetR(XmlDouble var1);

    public double getT();

    public XmlDouble xgetT();

    public void setT(double var1);

    public void xsetT(XmlDouble var1);

    public double getB();

    public XmlDouble xgetB();

    public void setB(double var1);

    public void xsetB(XmlDouble var1);

    public double getHeader();

    public XmlDouble xgetHeader();

    public void setHeader(double var1);

    public void xsetHeader(XmlDouble var1);

    public double getFooter();

    public XmlDouble xgetFooter();

    public void setFooter(double var1);

    public void xsetFooter(XmlDouble var1);
}


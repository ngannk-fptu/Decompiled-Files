/*
 * Decompiled with CFR 0.152.
 */
package org.openxmlformats.schemas.wordprocessingml.x2006.main;

import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.openxmlformats.schemas.officeDocument.x2006.sharedTypes.STTwipsMeasure;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STSignedTwipsMeasure;

public interface CTPageMar
extends XmlObject {
    public static final DocumentFactory<CTPageMar> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "ctpagemar92a3type");
    public static final SchemaType type = Factory.getType();

    public Object getTop();

    public STSignedTwipsMeasure xgetTop();

    public void setTop(Object var1);

    public void xsetTop(STSignedTwipsMeasure var1);

    public Object getRight();

    public STTwipsMeasure xgetRight();

    public void setRight(Object var1);

    public void xsetRight(STTwipsMeasure var1);

    public Object getBottom();

    public STSignedTwipsMeasure xgetBottom();

    public void setBottom(Object var1);

    public void xsetBottom(STSignedTwipsMeasure var1);

    public Object getLeft();

    public STTwipsMeasure xgetLeft();

    public void setLeft(Object var1);

    public void xsetLeft(STTwipsMeasure var1);

    public Object getHeader();

    public STTwipsMeasure xgetHeader();

    public void setHeader(Object var1);

    public void xsetHeader(STTwipsMeasure var1);

    public Object getFooter();

    public STTwipsMeasure xgetFooter();

    public void setFooter(Object var1);

    public void xsetFooter(STTwipsMeasure var1);

    public Object getGutter();

    public STTwipsMeasure xgetGutter();

    public void setGutter(Object var1);

    public void xsetGutter(STTwipsMeasure var1);
}


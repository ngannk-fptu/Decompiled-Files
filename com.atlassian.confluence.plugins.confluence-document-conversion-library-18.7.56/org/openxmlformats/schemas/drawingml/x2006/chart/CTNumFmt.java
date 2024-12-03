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

public interface CTNumFmt
extends XmlObject {
    public static final DocumentFactory<CTNumFmt> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "ctnumfmtc0f5type");
    public static final SchemaType type = Factory.getType();

    public String getFormatCode();

    public STXstring xgetFormatCode();

    public void setFormatCode(String var1);

    public void xsetFormatCode(STXstring var1);

    public boolean getSourceLinked();

    public XmlBoolean xgetSourceLinked();

    public boolean isSetSourceLinked();

    public void setSourceLinked(boolean var1);

    public void xsetSourceLinked(XmlBoolean var1);

    public void unsetSourceLinked();
}


/*
 * Decompiled with CFR 0.152.
 */
package org.openxmlformats.schemas.drawingml.x2006.chart;

import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.XmlUnsignedInt;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.openxmlformats.schemas.officeDocument.x2006.sharedTypes.STXstring;

public interface CTNumVal
extends XmlObject {
    public static final DocumentFactory<CTNumVal> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "ctnumval2fe1type");
    public static final SchemaType type = Factory.getType();

    public String getV();

    public STXstring xgetV();

    public void setV(String var1);

    public void xsetV(STXstring var1);

    public long getIdx();

    public XmlUnsignedInt xgetIdx();

    public void setIdx(long var1);

    public void xsetIdx(XmlUnsignedInt var1);

    public String getFormatCode();

    public STXstring xgetFormatCode();

    public boolean isSetFormatCode();

    public void setFormatCode(String var1);

    public void xsetFormatCode(STXstring var1);

    public void unsetFormatCode();
}


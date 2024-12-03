/*
 * Decompiled with CFR 0.152.
 */
package org.openxmlformats.schemas.drawingml.x2006.chart;

import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTStrRef;
import org.openxmlformats.schemas.drawingml.x2006.main.CTTextBody;

public interface CTTx
extends XmlObject {
    public static final DocumentFactory<CTTx> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "cttx9678type");
    public static final SchemaType type = Factory.getType();

    public CTStrRef getStrRef();

    public boolean isSetStrRef();

    public void setStrRef(CTStrRef var1);

    public CTStrRef addNewStrRef();

    public void unsetStrRef();

    public CTTextBody getRich();

    public boolean isSetRich();

    public void setRich(CTTextBody var1);

    public CTTextBody addNewRich();

    public void unsetRich();
}


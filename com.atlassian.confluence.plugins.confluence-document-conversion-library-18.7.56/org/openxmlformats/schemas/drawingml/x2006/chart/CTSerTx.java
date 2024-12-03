/*
 * Decompiled with CFR 0.152.
 */
package org.openxmlformats.schemas.drawingml.x2006.chart;

import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTStrRef;
import org.openxmlformats.schemas.officeDocument.x2006.sharedTypes.STXstring;

public interface CTSerTx
extends XmlObject {
    public static final DocumentFactory<CTSerTx> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "ctsertxd722type");
    public static final SchemaType type = Factory.getType();

    public CTStrRef getStrRef();

    public boolean isSetStrRef();

    public void setStrRef(CTStrRef var1);

    public CTStrRef addNewStrRef();

    public void unsetStrRef();

    public String getV();

    public STXstring xgetV();

    public boolean isSetV();

    public void setV(String var1);

    public void xsetV(STXstring var1);

    public void unsetV();
}


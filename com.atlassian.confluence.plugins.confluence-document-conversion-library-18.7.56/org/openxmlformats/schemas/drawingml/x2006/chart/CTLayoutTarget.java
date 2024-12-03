/*
 * Decompiled with CFR 0.152.
 */
package org.openxmlformats.schemas.drawingml.x2006.chart;

import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.openxmlformats.schemas.drawingml.x2006.chart.STLayoutTarget;

public interface CTLayoutTarget
extends XmlObject {
    public static final DocumentFactory<CTLayoutTarget> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "ctlayouttarget1001type");
    public static final SchemaType type = Factory.getType();

    public STLayoutTarget.Enum getVal();

    public STLayoutTarget xgetVal();

    public boolean isSetVal();

    public void setVal(STLayoutTarget.Enum var1);

    public void xsetVal(STLayoutTarget var1);

    public void unsetVal();
}


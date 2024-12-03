/*
 * Decompiled with CFR 0.152.
 */
package org.openxmlformats.schemas.drawingml.x2006.chart;

import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTNumData;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTNumRef;

public interface CTNumDataSource
extends XmlObject {
    public static final DocumentFactory<CTNumDataSource> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "ctnumdatasourcef0bbtype");
    public static final SchemaType type = Factory.getType();

    public CTNumRef getNumRef();

    public boolean isSetNumRef();

    public void setNumRef(CTNumRef var1);

    public CTNumRef addNewNumRef();

    public void unsetNumRef();

    public CTNumData getNumLit();

    public boolean isSetNumLit();

    public void setNumLit(CTNumData var1);

    public CTNumData addNewNumLit();

    public void unsetNumLit();
}


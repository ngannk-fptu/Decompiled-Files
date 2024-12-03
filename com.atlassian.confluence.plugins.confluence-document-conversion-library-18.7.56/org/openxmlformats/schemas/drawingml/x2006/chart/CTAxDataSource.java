/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.openxmlformats.schemas.drawingml.x2006.chart.CTMultiLvlStrRef
 */
package org.openxmlformats.schemas.drawingml.x2006.chart;

import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTMultiLvlStrRef;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTNumData;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTNumRef;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTStrData;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTStrRef;

public interface CTAxDataSource
extends XmlObject {
    public static final DocumentFactory<CTAxDataSource> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "ctaxdatasource1440type");
    public static final SchemaType type = Factory.getType();

    public CTMultiLvlStrRef getMultiLvlStrRef();

    public boolean isSetMultiLvlStrRef();

    public void setMultiLvlStrRef(CTMultiLvlStrRef var1);

    public CTMultiLvlStrRef addNewMultiLvlStrRef();

    public void unsetMultiLvlStrRef();

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

    public CTStrRef getStrRef();

    public boolean isSetStrRef();

    public void setStrRef(CTStrRef var1);

    public CTStrRef addNewStrRef();

    public void unsetStrRef();

    public CTStrData getStrLit();

    public boolean isSetStrLit();

    public void setStrLit(CTStrData var1);

    public CTStrData addNewStrLit();

    public void unsetStrLit();
}


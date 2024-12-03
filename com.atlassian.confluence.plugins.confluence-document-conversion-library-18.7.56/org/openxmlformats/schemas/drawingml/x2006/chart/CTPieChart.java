/*
 * Decompiled with CFR 0.152.
 */
package org.openxmlformats.schemas.drawingml.x2006.chart;

import java.util.List;
import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTBoolean;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTDLbls;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTExtensionList;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTFirstSliceAng;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTPieSer;

public interface CTPieChart
extends XmlObject {
    public static final DocumentFactory<CTPieChart> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "ctpiechartd34atype");
    public static final SchemaType type = Factory.getType();

    public CTBoolean getVaryColors();

    public boolean isSetVaryColors();

    public void setVaryColors(CTBoolean var1);

    public CTBoolean addNewVaryColors();

    public void unsetVaryColors();

    public List<CTPieSer> getSerList();

    public CTPieSer[] getSerArray();

    public CTPieSer getSerArray(int var1);

    public int sizeOfSerArray();

    public void setSerArray(CTPieSer[] var1);

    public void setSerArray(int var1, CTPieSer var2);

    public CTPieSer insertNewSer(int var1);

    public CTPieSer addNewSer();

    public void removeSer(int var1);

    public CTDLbls getDLbls();

    public boolean isSetDLbls();

    public void setDLbls(CTDLbls var1);

    public CTDLbls addNewDLbls();

    public void unsetDLbls();

    public CTFirstSliceAng getFirstSliceAng();

    public boolean isSetFirstSliceAng();

    public void setFirstSliceAng(CTFirstSliceAng var1);

    public CTFirstSliceAng addNewFirstSliceAng();

    public void unsetFirstSliceAng();

    public CTExtensionList getExtLst();

    public boolean isSetExtLst();

    public void setExtLst(CTExtensionList var1);

    public CTExtensionList addNewExtLst();

    public void unsetExtLst();
}


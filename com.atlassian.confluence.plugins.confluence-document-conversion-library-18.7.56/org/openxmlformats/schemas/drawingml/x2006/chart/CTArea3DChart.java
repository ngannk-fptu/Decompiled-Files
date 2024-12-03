/*
 * Decompiled with CFR 0.152.
 */
package org.openxmlformats.schemas.drawingml.x2006.chart;

import java.util.List;
import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTAreaSer;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTBoolean;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTChartLines;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTDLbls;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTExtensionList;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTGapAmount;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTGrouping;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTUnsignedInt;

public interface CTArea3DChart
extends XmlObject {
    public static final DocumentFactory<CTArea3DChart> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "ctarea3dchart4c26type");
    public static final SchemaType type = Factory.getType();

    public CTGrouping getGrouping();

    public boolean isSetGrouping();

    public void setGrouping(CTGrouping var1);

    public CTGrouping addNewGrouping();

    public void unsetGrouping();

    public CTBoolean getVaryColors();

    public boolean isSetVaryColors();

    public void setVaryColors(CTBoolean var1);

    public CTBoolean addNewVaryColors();

    public void unsetVaryColors();

    public List<CTAreaSer> getSerList();

    public CTAreaSer[] getSerArray();

    public CTAreaSer getSerArray(int var1);

    public int sizeOfSerArray();

    public void setSerArray(CTAreaSer[] var1);

    public void setSerArray(int var1, CTAreaSer var2);

    public CTAreaSer insertNewSer(int var1);

    public CTAreaSer addNewSer();

    public void removeSer(int var1);

    public CTDLbls getDLbls();

    public boolean isSetDLbls();

    public void setDLbls(CTDLbls var1);

    public CTDLbls addNewDLbls();

    public void unsetDLbls();

    public CTChartLines getDropLines();

    public boolean isSetDropLines();

    public void setDropLines(CTChartLines var1);

    public CTChartLines addNewDropLines();

    public void unsetDropLines();

    public CTGapAmount getGapDepth();

    public boolean isSetGapDepth();

    public void setGapDepth(CTGapAmount var1);

    public CTGapAmount addNewGapDepth();

    public void unsetGapDepth();

    public List<CTUnsignedInt> getAxIdList();

    public CTUnsignedInt[] getAxIdArray();

    public CTUnsignedInt getAxIdArray(int var1);

    public int sizeOfAxIdArray();

    public void setAxIdArray(CTUnsignedInt[] var1);

    public void setAxIdArray(int var1, CTUnsignedInt var2);

    public CTUnsignedInt insertNewAxId(int var1);

    public CTUnsignedInt addNewAxId();

    public void removeAxId(int var1);

    public CTExtensionList getExtLst();

    public boolean isSetExtLst();

    public void setExtLst(CTExtensionList var1);

    public CTExtensionList addNewExtLst();

    public void unsetExtLst();
}


/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.openxmlformats.schemas.drawingml.x2006.chart.CTUpDownBars
 */
package org.openxmlformats.schemas.drawingml.x2006.chart;

import java.util.List;
import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTBoolean;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTChartLines;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTDLbls;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTExtensionList;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTGrouping;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTLineSer;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTUnsignedInt;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTUpDownBars;

public interface CTLineChart
extends XmlObject {
    public static final DocumentFactory<CTLineChart> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "ctlinechart249ctype");
    public static final SchemaType type = Factory.getType();

    public CTGrouping getGrouping();

    public void setGrouping(CTGrouping var1);

    public CTGrouping addNewGrouping();

    public CTBoolean getVaryColors();

    public boolean isSetVaryColors();

    public void setVaryColors(CTBoolean var1);

    public CTBoolean addNewVaryColors();

    public void unsetVaryColors();

    public List<CTLineSer> getSerList();

    public CTLineSer[] getSerArray();

    public CTLineSer getSerArray(int var1);

    public int sizeOfSerArray();

    public void setSerArray(CTLineSer[] var1);

    public void setSerArray(int var1, CTLineSer var2);

    public CTLineSer insertNewSer(int var1);

    public CTLineSer addNewSer();

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

    public CTChartLines getHiLowLines();

    public boolean isSetHiLowLines();

    public void setHiLowLines(CTChartLines var1);

    public CTChartLines addNewHiLowLines();

    public void unsetHiLowLines();

    public CTUpDownBars getUpDownBars();

    public boolean isSetUpDownBars();

    public void setUpDownBars(CTUpDownBars var1);

    public CTUpDownBars addNewUpDownBars();

    public void unsetUpDownBars();

    public CTBoolean getMarker();

    public boolean isSetMarker();

    public void setMarker(CTBoolean var1);

    public CTBoolean addNewMarker();

    public void unsetMarker();

    public CTBoolean getSmooth();

    public boolean isSetSmooth();

    public void setSmooth(CTBoolean var1);

    public CTBoolean addNewSmooth();

    public void unsetSmooth();

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


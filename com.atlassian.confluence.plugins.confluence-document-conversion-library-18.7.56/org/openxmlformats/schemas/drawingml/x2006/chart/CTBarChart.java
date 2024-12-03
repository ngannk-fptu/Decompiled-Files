/*
 * Decompiled with CFR 0.152.
 */
package org.openxmlformats.schemas.drawingml.x2006.chart;

import java.util.List;
import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTBarDir;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTBarGrouping;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTBarSer;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTBoolean;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTChartLines;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTDLbls;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTExtensionList;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTGapAmount;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTOverlap;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTUnsignedInt;

public interface CTBarChart
extends XmlObject {
    public static final DocumentFactory<CTBarChart> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "ctbarchart4151type");
    public static final SchemaType type = Factory.getType();

    public CTBarDir getBarDir();

    public void setBarDir(CTBarDir var1);

    public CTBarDir addNewBarDir();

    public CTBarGrouping getGrouping();

    public boolean isSetGrouping();

    public void setGrouping(CTBarGrouping var1);

    public CTBarGrouping addNewGrouping();

    public void unsetGrouping();

    public CTBoolean getVaryColors();

    public boolean isSetVaryColors();

    public void setVaryColors(CTBoolean var1);

    public CTBoolean addNewVaryColors();

    public void unsetVaryColors();

    public List<CTBarSer> getSerList();

    public CTBarSer[] getSerArray();

    public CTBarSer getSerArray(int var1);

    public int sizeOfSerArray();

    public void setSerArray(CTBarSer[] var1);

    public void setSerArray(int var1, CTBarSer var2);

    public CTBarSer insertNewSer(int var1);

    public CTBarSer addNewSer();

    public void removeSer(int var1);

    public CTDLbls getDLbls();

    public boolean isSetDLbls();

    public void setDLbls(CTDLbls var1);

    public CTDLbls addNewDLbls();

    public void unsetDLbls();

    public CTGapAmount getGapWidth();

    public boolean isSetGapWidth();

    public void setGapWidth(CTGapAmount var1);

    public CTGapAmount addNewGapWidth();

    public void unsetGapWidth();

    public CTOverlap getOverlap();

    public boolean isSetOverlap();

    public void setOverlap(CTOverlap var1);

    public CTOverlap addNewOverlap();

    public void unsetOverlap();

    public List<CTChartLines> getSerLinesList();

    public CTChartLines[] getSerLinesArray();

    public CTChartLines getSerLinesArray(int var1);

    public int sizeOfSerLinesArray();

    public void setSerLinesArray(CTChartLines[] var1);

    public void setSerLinesArray(int var1, CTChartLines var2);

    public CTChartLines insertNewSerLines(int var1);

    public CTChartLines addNewSerLines();

    public void removeSerLines(int var1);

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


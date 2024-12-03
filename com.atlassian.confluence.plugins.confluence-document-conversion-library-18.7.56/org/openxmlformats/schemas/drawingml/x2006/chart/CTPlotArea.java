/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.openxmlformats.schemas.drawingml.x2006.chart.CTDTable
 *  org.openxmlformats.schemas.drawingml.x2006.chart.CTStockChart
 */
package org.openxmlformats.schemas.drawingml.x2006.chart;

import java.util.List;
import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTArea3DChart;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTAreaChart;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTBar3DChart;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTBarChart;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTBubbleChart;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTCatAx;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTDTable;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTDateAx;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTDoughnutChart;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTExtensionList;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTLayout;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTLine3DChart;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTLineChart;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTOfPieChart;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTPie3DChart;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTPieChart;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTRadarChart;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTScatterChart;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTSerAx;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTStockChart;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTSurface3DChart;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTSurfaceChart;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTValAx;
import org.openxmlformats.schemas.drawingml.x2006.main.CTShapeProperties;

public interface CTPlotArea
extends XmlObject {
    public static final DocumentFactory<CTPlotArea> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "ctplotarea106etype");
    public static final SchemaType type = Factory.getType();

    public CTLayout getLayout();

    public boolean isSetLayout();

    public void setLayout(CTLayout var1);

    public CTLayout addNewLayout();

    public void unsetLayout();

    public List<CTAreaChart> getAreaChartList();

    public CTAreaChart[] getAreaChartArray();

    public CTAreaChart getAreaChartArray(int var1);

    public int sizeOfAreaChartArray();

    public void setAreaChartArray(CTAreaChart[] var1);

    public void setAreaChartArray(int var1, CTAreaChart var2);

    public CTAreaChart insertNewAreaChart(int var1);

    public CTAreaChart addNewAreaChart();

    public void removeAreaChart(int var1);

    public List<CTArea3DChart> getArea3DChartList();

    public CTArea3DChart[] getArea3DChartArray();

    public CTArea3DChart getArea3DChartArray(int var1);

    public int sizeOfArea3DChartArray();

    public void setArea3DChartArray(CTArea3DChart[] var1);

    public void setArea3DChartArray(int var1, CTArea3DChart var2);

    public CTArea3DChart insertNewArea3DChart(int var1);

    public CTArea3DChart addNewArea3DChart();

    public void removeArea3DChart(int var1);

    public List<CTLineChart> getLineChartList();

    public CTLineChart[] getLineChartArray();

    public CTLineChart getLineChartArray(int var1);

    public int sizeOfLineChartArray();

    public void setLineChartArray(CTLineChart[] var1);

    public void setLineChartArray(int var1, CTLineChart var2);

    public CTLineChart insertNewLineChart(int var1);

    public CTLineChart addNewLineChart();

    public void removeLineChart(int var1);

    public List<CTLine3DChart> getLine3DChartList();

    public CTLine3DChart[] getLine3DChartArray();

    public CTLine3DChart getLine3DChartArray(int var1);

    public int sizeOfLine3DChartArray();

    public void setLine3DChartArray(CTLine3DChart[] var1);

    public void setLine3DChartArray(int var1, CTLine3DChart var2);

    public CTLine3DChart insertNewLine3DChart(int var1);

    public CTLine3DChart addNewLine3DChart();

    public void removeLine3DChart(int var1);

    public List<CTStockChart> getStockChartList();

    public CTStockChart[] getStockChartArray();

    public CTStockChart getStockChartArray(int var1);

    public int sizeOfStockChartArray();

    public void setStockChartArray(CTStockChart[] var1);

    public void setStockChartArray(int var1, CTStockChart var2);

    public CTStockChart insertNewStockChart(int var1);

    public CTStockChart addNewStockChart();

    public void removeStockChart(int var1);

    public List<CTRadarChart> getRadarChartList();

    public CTRadarChart[] getRadarChartArray();

    public CTRadarChart getRadarChartArray(int var1);

    public int sizeOfRadarChartArray();

    public void setRadarChartArray(CTRadarChart[] var1);

    public void setRadarChartArray(int var1, CTRadarChart var2);

    public CTRadarChart insertNewRadarChart(int var1);

    public CTRadarChart addNewRadarChart();

    public void removeRadarChart(int var1);

    public List<CTScatterChart> getScatterChartList();

    public CTScatterChart[] getScatterChartArray();

    public CTScatterChart getScatterChartArray(int var1);

    public int sizeOfScatterChartArray();

    public void setScatterChartArray(CTScatterChart[] var1);

    public void setScatterChartArray(int var1, CTScatterChart var2);

    public CTScatterChart insertNewScatterChart(int var1);

    public CTScatterChart addNewScatterChart();

    public void removeScatterChart(int var1);

    public List<CTPieChart> getPieChartList();

    public CTPieChart[] getPieChartArray();

    public CTPieChart getPieChartArray(int var1);

    public int sizeOfPieChartArray();

    public void setPieChartArray(CTPieChart[] var1);

    public void setPieChartArray(int var1, CTPieChart var2);

    public CTPieChart insertNewPieChart(int var1);

    public CTPieChart addNewPieChart();

    public void removePieChart(int var1);

    public List<CTPie3DChart> getPie3DChartList();

    public CTPie3DChart[] getPie3DChartArray();

    public CTPie3DChart getPie3DChartArray(int var1);

    public int sizeOfPie3DChartArray();

    public void setPie3DChartArray(CTPie3DChart[] var1);

    public void setPie3DChartArray(int var1, CTPie3DChart var2);

    public CTPie3DChart insertNewPie3DChart(int var1);

    public CTPie3DChart addNewPie3DChart();

    public void removePie3DChart(int var1);

    public List<CTDoughnutChart> getDoughnutChartList();

    public CTDoughnutChart[] getDoughnutChartArray();

    public CTDoughnutChart getDoughnutChartArray(int var1);

    public int sizeOfDoughnutChartArray();

    public void setDoughnutChartArray(CTDoughnutChart[] var1);

    public void setDoughnutChartArray(int var1, CTDoughnutChart var2);

    public CTDoughnutChart insertNewDoughnutChart(int var1);

    public CTDoughnutChart addNewDoughnutChart();

    public void removeDoughnutChart(int var1);

    public List<CTBarChart> getBarChartList();

    public CTBarChart[] getBarChartArray();

    public CTBarChart getBarChartArray(int var1);

    public int sizeOfBarChartArray();

    public void setBarChartArray(CTBarChart[] var1);

    public void setBarChartArray(int var1, CTBarChart var2);

    public CTBarChart insertNewBarChart(int var1);

    public CTBarChart addNewBarChart();

    public void removeBarChart(int var1);

    public List<CTBar3DChart> getBar3DChartList();

    public CTBar3DChart[] getBar3DChartArray();

    public CTBar3DChart getBar3DChartArray(int var1);

    public int sizeOfBar3DChartArray();

    public void setBar3DChartArray(CTBar3DChart[] var1);

    public void setBar3DChartArray(int var1, CTBar3DChart var2);

    public CTBar3DChart insertNewBar3DChart(int var1);

    public CTBar3DChart addNewBar3DChart();

    public void removeBar3DChart(int var1);

    public List<CTOfPieChart> getOfPieChartList();

    public CTOfPieChart[] getOfPieChartArray();

    public CTOfPieChart getOfPieChartArray(int var1);

    public int sizeOfOfPieChartArray();

    public void setOfPieChartArray(CTOfPieChart[] var1);

    public void setOfPieChartArray(int var1, CTOfPieChart var2);

    public CTOfPieChart insertNewOfPieChart(int var1);

    public CTOfPieChart addNewOfPieChart();

    public void removeOfPieChart(int var1);

    public List<CTSurfaceChart> getSurfaceChartList();

    public CTSurfaceChart[] getSurfaceChartArray();

    public CTSurfaceChart getSurfaceChartArray(int var1);

    public int sizeOfSurfaceChartArray();

    public void setSurfaceChartArray(CTSurfaceChart[] var1);

    public void setSurfaceChartArray(int var1, CTSurfaceChart var2);

    public CTSurfaceChart insertNewSurfaceChart(int var1);

    public CTSurfaceChart addNewSurfaceChart();

    public void removeSurfaceChart(int var1);

    public List<CTSurface3DChart> getSurface3DChartList();

    public CTSurface3DChart[] getSurface3DChartArray();

    public CTSurface3DChart getSurface3DChartArray(int var1);

    public int sizeOfSurface3DChartArray();

    public void setSurface3DChartArray(CTSurface3DChart[] var1);

    public void setSurface3DChartArray(int var1, CTSurface3DChart var2);

    public CTSurface3DChart insertNewSurface3DChart(int var1);

    public CTSurface3DChart addNewSurface3DChart();

    public void removeSurface3DChart(int var1);

    public List<CTBubbleChart> getBubbleChartList();

    public CTBubbleChart[] getBubbleChartArray();

    public CTBubbleChart getBubbleChartArray(int var1);

    public int sizeOfBubbleChartArray();

    public void setBubbleChartArray(CTBubbleChart[] var1);

    public void setBubbleChartArray(int var1, CTBubbleChart var2);

    public CTBubbleChart insertNewBubbleChart(int var1);

    public CTBubbleChart addNewBubbleChart();

    public void removeBubbleChart(int var1);

    public List<CTValAx> getValAxList();

    public CTValAx[] getValAxArray();

    public CTValAx getValAxArray(int var1);

    public int sizeOfValAxArray();

    public void setValAxArray(CTValAx[] var1);

    public void setValAxArray(int var1, CTValAx var2);

    public CTValAx insertNewValAx(int var1);

    public CTValAx addNewValAx();

    public void removeValAx(int var1);

    public List<CTCatAx> getCatAxList();

    public CTCatAx[] getCatAxArray();

    public CTCatAx getCatAxArray(int var1);

    public int sizeOfCatAxArray();

    public void setCatAxArray(CTCatAx[] var1);

    public void setCatAxArray(int var1, CTCatAx var2);

    public CTCatAx insertNewCatAx(int var1);

    public CTCatAx addNewCatAx();

    public void removeCatAx(int var1);

    public List<CTDateAx> getDateAxList();

    public CTDateAx[] getDateAxArray();

    public CTDateAx getDateAxArray(int var1);

    public int sizeOfDateAxArray();

    public void setDateAxArray(CTDateAx[] var1);

    public void setDateAxArray(int var1, CTDateAx var2);

    public CTDateAx insertNewDateAx(int var1);

    public CTDateAx addNewDateAx();

    public void removeDateAx(int var1);

    public List<CTSerAx> getSerAxList();

    public CTSerAx[] getSerAxArray();

    public CTSerAx getSerAxArray(int var1);

    public int sizeOfSerAxArray();

    public void setSerAxArray(CTSerAx[] var1);

    public void setSerAxArray(int var1, CTSerAx var2);

    public CTSerAx insertNewSerAx(int var1);

    public CTSerAx addNewSerAx();

    public void removeSerAx(int var1);

    public CTDTable getDTable();

    public boolean isSetDTable();

    public void setDTable(CTDTable var1);

    public CTDTable addNewDTable();

    public void unsetDTable();

    public CTShapeProperties getSpPr();

    public boolean isSetSpPr();

    public void setSpPr(CTShapeProperties var1);

    public CTShapeProperties addNewSpPr();

    public void unsetSpPr();

    public CTExtensionList getExtLst();

    public boolean isSetExtLst();

    public void setExtLst(CTExtensionList var1);

    public CTExtensionList addNewExtLst();

    public void unsetExtLst();
}


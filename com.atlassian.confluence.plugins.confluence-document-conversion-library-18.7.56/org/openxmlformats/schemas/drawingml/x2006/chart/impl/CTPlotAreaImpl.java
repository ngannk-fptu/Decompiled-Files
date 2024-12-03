/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.openxmlformats.schemas.drawingml.x2006.chart.CTDTable
 *  org.openxmlformats.schemas.drawingml.x2006.chart.CTStockChart
 */
package org.openxmlformats.schemas.drawingml.x2006.chart.impl;

import java.util.List;
import javax.xml.namespace.QName;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.impl.values.JavaListXmlObject;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;
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
import org.openxmlformats.schemas.drawingml.x2006.chart.CTPlotArea;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTRadarChart;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTScatterChart;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTSerAx;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTStockChart;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTSurface3DChart;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTSurfaceChart;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTValAx;
import org.openxmlformats.schemas.drawingml.x2006.main.CTShapeProperties;

public class CTPlotAreaImpl
extends XmlComplexContentImpl
implements CTPlotArea {
    private static final long serialVersionUID = 1L;
    private static final QName[] PROPERTY_QNAME = new QName[]{new QName("http://schemas.openxmlformats.org/drawingml/2006/chart", "layout"), new QName("http://schemas.openxmlformats.org/drawingml/2006/chart", "areaChart"), new QName("http://schemas.openxmlformats.org/drawingml/2006/chart", "area3DChart"), new QName("http://schemas.openxmlformats.org/drawingml/2006/chart", "lineChart"), new QName("http://schemas.openxmlformats.org/drawingml/2006/chart", "line3DChart"), new QName("http://schemas.openxmlformats.org/drawingml/2006/chart", "stockChart"), new QName("http://schemas.openxmlformats.org/drawingml/2006/chart", "radarChart"), new QName("http://schemas.openxmlformats.org/drawingml/2006/chart", "scatterChart"), new QName("http://schemas.openxmlformats.org/drawingml/2006/chart", "pieChart"), new QName("http://schemas.openxmlformats.org/drawingml/2006/chart", "pie3DChart"), new QName("http://schemas.openxmlformats.org/drawingml/2006/chart", "doughnutChart"), new QName("http://schemas.openxmlformats.org/drawingml/2006/chart", "barChart"), new QName("http://schemas.openxmlformats.org/drawingml/2006/chart", "bar3DChart"), new QName("http://schemas.openxmlformats.org/drawingml/2006/chart", "ofPieChart"), new QName("http://schemas.openxmlformats.org/drawingml/2006/chart", "surfaceChart"), new QName("http://schemas.openxmlformats.org/drawingml/2006/chart", "surface3DChart"), new QName("http://schemas.openxmlformats.org/drawingml/2006/chart", "bubbleChart"), new QName("http://schemas.openxmlformats.org/drawingml/2006/chart", "valAx"), new QName("http://schemas.openxmlformats.org/drawingml/2006/chart", "catAx"), new QName("http://schemas.openxmlformats.org/drawingml/2006/chart", "dateAx"), new QName("http://schemas.openxmlformats.org/drawingml/2006/chart", "serAx"), new QName("http://schemas.openxmlformats.org/drawingml/2006/chart", "dTable"), new QName("http://schemas.openxmlformats.org/drawingml/2006/chart", "spPr"), new QName("http://schemas.openxmlformats.org/drawingml/2006/chart", "extLst")};

    public CTPlotAreaImpl(SchemaType sType) {
        super(sType);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTLayout getLayout() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTLayout target = null;
            target = (CTLayout)((Object)this.get_store().find_element_user(PROPERTY_QNAME[0], 0));
            return target == null ? null : target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean isSetLayout() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[0]) != 0;
        }
    }

    @Override
    public void setLayout(CTLayout layout) {
        this.generatedSetterHelperImpl(layout, PROPERTY_QNAME[0], 0, (short)1);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTLayout addNewLayout() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTLayout target = null;
            target = (CTLayout)((Object)this.get_store().add_element_user(PROPERTY_QNAME[0]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void unsetLayout() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.get_store().remove_element(PROPERTY_QNAME[0], 0);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public List<CTAreaChart> getAreaChartList() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return new JavaListXmlObject<CTAreaChart>(this::getAreaChartArray, this::setAreaChartArray, this::insertNewAreaChart, this::removeAreaChart, this::sizeOfAreaChartArray);
        }
    }

    @Override
    public CTAreaChart[] getAreaChartArray() {
        return (CTAreaChart[])this.getXmlObjectArray(PROPERTY_QNAME[1], new CTAreaChart[0]);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTAreaChart getAreaChartArray(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTAreaChart target = null;
            target = (CTAreaChart)((Object)this.get_store().find_element_user(PROPERTY_QNAME[1], i));
            if (target == null) {
                throw new IndexOutOfBoundsException();
            }
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public int sizeOfAreaChartArray() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[1]);
        }
    }

    @Override
    public void setAreaChartArray(CTAreaChart[] areaChartArray) {
        this.check_orphaned();
        this.arraySetterHelper(areaChartArray, PROPERTY_QNAME[1]);
    }

    @Override
    public void setAreaChartArray(int i, CTAreaChart areaChart) {
        this.generatedSetterHelperImpl(areaChart, PROPERTY_QNAME[1], i, (short)2);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTAreaChart insertNewAreaChart(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTAreaChart target = null;
            target = (CTAreaChart)((Object)this.get_store().insert_element_user(PROPERTY_QNAME[1], i));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTAreaChart addNewAreaChart() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTAreaChart target = null;
            target = (CTAreaChart)((Object)this.get_store().add_element_user(PROPERTY_QNAME[1]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void removeAreaChart(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.get_store().remove_element(PROPERTY_QNAME[1], i);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public List<CTArea3DChart> getArea3DChartList() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return new JavaListXmlObject<CTArea3DChart>(this::getArea3DChartArray, this::setArea3DChartArray, this::insertNewArea3DChart, this::removeArea3DChart, this::sizeOfArea3DChartArray);
        }
    }

    @Override
    public CTArea3DChart[] getArea3DChartArray() {
        return (CTArea3DChart[])this.getXmlObjectArray(PROPERTY_QNAME[2], new CTArea3DChart[0]);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTArea3DChart getArea3DChartArray(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTArea3DChart target = null;
            target = (CTArea3DChart)((Object)this.get_store().find_element_user(PROPERTY_QNAME[2], i));
            if (target == null) {
                throw new IndexOutOfBoundsException();
            }
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public int sizeOfArea3DChartArray() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[2]);
        }
    }

    @Override
    public void setArea3DChartArray(CTArea3DChart[] area3DChartArray) {
        this.check_orphaned();
        this.arraySetterHelper(area3DChartArray, PROPERTY_QNAME[2]);
    }

    @Override
    public void setArea3DChartArray(int i, CTArea3DChart area3DChart) {
        this.generatedSetterHelperImpl(area3DChart, PROPERTY_QNAME[2], i, (short)2);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTArea3DChart insertNewArea3DChart(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTArea3DChart target = null;
            target = (CTArea3DChart)((Object)this.get_store().insert_element_user(PROPERTY_QNAME[2], i));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTArea3DChart addNewArea3DChart() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTArea3DChart target = null;
            target = (CTArea3DChart)((Object)this.get_store().add_element_user(PROPERTY_QNAME[2]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void removeArea3DChart(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.get_store().remove_element(PROPERTY_QNAME[2], i);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public List<CTLineChart> getLineChartList() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return new JavaListXmlObject<CTLineChart>(this::getLineChartArray, this::setLineChartArray, this::insertNewLineChart, this::removeLineChart, this::sizeOfLineChartArray);
        }
    }

    @Override
    public CTLineChart[] getLineChartArray() {
        return (CTLineChart[])this.getXmlObjectArray(PROPERTY_QNAME[3], new CTLineChart[0]);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTLineChart getLineChartArray(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTLineChart target = null;
            target = (CTLineChart)((Object)this.get_store().find_element_user(PROPERTY_QNAME[3], i));
            if (target == null) {
                throw new IndexOutOfBoundsException();
            }
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public int sizeOfLineChartArray() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[3]);
        }
    }

    @Override
    public void setLineChartArray(CTLineChart[] lineChartArray) {
        this.check_orphaned();
        this.arraySetterHelper(lineChartArray, PROPERTY_QNAME[3]);
    }

    @Override
    public void setLineChartArray(int i, CTLineChart lineChart) {
        this.generatedSetterHelperImpl(lineChart, PROPERTY_QNAME[3], i, (short)2);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTLineChart insertNewLineChart(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTLineChart target = null;
            target = (CTLineChart)((Object)this.get_store().insert_element_user(PROPERTY_QNAME[3], i));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTLineChart addNewLineChart() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTLineChart target = null;
            target = (CTLineChart)((Object)this.get_store().add_element_user(PROPERTY_QNAME[3]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void removeLineChart(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.get_store().remove_element(PROPERTY_QNAME[3], i);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public List<CTLine3DChart> getLine3DChartList() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return new JavaListXmlObject<CTLine3DChart>(this::getLine3DChartArray, this::setLine3DChartArray, this::insertNewLine3DChart, this::removeLine3DChart, this::sizeOfLine3DChartArray);
        }
    }

    @Override
    public CTLine3DChart[] getLine3DChartArray() {
        return (CTLine3DChart[])this.getXmlObjectArray(PROPERTY_QNAME[4], new CTLine3DChart[0]);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTLine3DChart getLine3DChartArray(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTLine3DChart target = null;
            target = (CTLine3DChart)((Object)this.get_store().find_element_user(PROPERTY_QNAME[4], i));
            if (target == null) {
                throw new IndexOutOfBoundsException();
            }
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public int sizeOfLine3DChartArray() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[4]);
        }
    }

    @Override
    public void setLine3DChartArray(CTLine3DChart[] line3DChartArray) {
        this.check_orphaned();
        this.arraySetterHelper(line3DChartArray, PROPERTY_QNAME[4]);
    }

    @Override
    public void setLine3DChartArray(int i, CTLine3DChart line3DChart) {
        this.generatedSetterHelperImpl(line3DChart, PROPERTY_QNAME[4], i, (short)2);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTLine3DChart insertNewLine3DChart(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTLine3DChart target = null;
            target = (CTLine3DChart)((Object)this.get_store().insert_element_user(PROPERTY_QNAME[4], i));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTLine3DChart addNewLine3DChart() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTLine3DChart target = null;
            target = (CTLine3DChart)((Object)this.get_store().add_element_user(PROPERTY_QNAME[4]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void removeLine3DChart(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.get_store().remove_element(PROPERTY_QNAME[4], i);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public List<CTStockChart> getStockChartList() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return new JavaListXmlObject<CTStockChart>(this::getStockChartArray, this::setStockChartArray, this::insertNewStockChart, this::removeStockChart, this::sizeOfStockChartArray);
        }
    }

    @Override
    public CTStockChart[] getStockChartArray() {
        return (CTStockChart[])this.getXmlObjectArray(PROPERTY_QNAME[5], (XmlObject[])new CTStockChart[0]);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTStockChart getStockChartArray(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTStockChart target = null;
            target = (CTStockChart)this.get_store().find_element_user(PROPERTY_QNAME[5], i);
            if (target == null) {
                throw new IndexOutOfBoundsException();
            }
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public int sizeOfStockChartArray() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[5]);
        }
    }

    @Override
    public void setStockChartArray(CTStockChart[] stockChartArray) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])stockChartArray, PROPERTY_QNAME[5]);
    }

    @Override
    public void setStockChartArray(int i, CTStockChart stockChart) {
        this.generatedSetterHelperImpl((XmlObject)stockChart, PROPERTY_QNAME[5], i, (short)2);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTStockChart insertNewStockChart(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTStockChart target = null;
            target = (CTStockChart)this.get_store().insert_element_user(PROPERTY_QNAME[5], i);
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTStockChart addNewStockChart() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTStockChart target = null;
            target = (CTStockChart)this.get_store().add_element_user(PROPERTY_QNAME[5]);
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void removeStockChart(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.get_store().remove_element(PROPERTY_QNAME[5], i);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public List<CTRadarChart> getRadarChartList() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return new JavaListXmlObject<CTRadarChart>(this::getRadarChartArray, this::setRadarChartArray, this::insertNewRadarChart, this::removeRadarChart, this::sizeOfRadarChartArray);
        }
    }

    @Override
    public CTRadarChart[] getRadarChartArray() {
        return (CTRadarChart[])this.getXmlObjectArray(PROPERTY_QNAME[6], new CTRadarChart[0]);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTRadarChart getRadarChartArray(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTRadarChart target = null;
            target = (CTRadarChart)((Object)this.get_store().find_element_user(PROPERTY_QNAME[6], i));
            if (target == null) {
                throw new IndexOutOfBoundsException();
            }
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public int sizeOfRadarChartArray() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[6]);
        }
    }

    @Override
    public void setRadarChartArray(CTRadarChart[] radarChartArray) {
        this.check_orphaned();
        this.arraySetterHelper(radarChartArray, PROPERTY_QNAME[6]);
    }

    @Override
    public void setRadarChartArray(int i, CTRadarChart radarChart) {
        this.generatedSetterHelperImpl(radarChart, PROPERTY_QNAME[6], i, (short)2);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTRadarChart insertNewRadarChart(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTRadarChart target = null;
            target = (CTRadarChart)((Object)this.get_store().insert_element_user(PROPERTY_QNAME[6], i));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTRadarChart addNewRadarChart() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTRadarChart target = null;
            target = (CTRadarChart)((Object)this.get_store().add_element_user(PROPERTY_QNAME[6]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void removeRadarChart(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.get_store().remove_element(PROPERTY_QNAME[6], i);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public List<CTScatterChart> getScatterChartList() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return new JavaListXmlObject<CTScatterChart>(this::getScatterChartArray, this::setScatterChartArray, this::insertNewScatterChart, this::removeScatterChart, this::sizeOfScatterChartArray);
        }
    }

    @Override
    public CTScatterChart[] getScatterChartArray() {
        return (CTScatterChart[])this.getXmlObjectArray(PROPERTY_QNAME[7], new CTScatterChart[0]);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTScatterChart getScatterChartArray(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTScatterChart target = null;
            target = (CTScatterChart)((Object)this.get_store().find_element_user(PROPERTY_QNAME[7], i));
            if (target == null) {
                throw new IndexOutOfBoundsException();
            }
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public int sizeOfScatterChartArray() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[7]);
        }
    }

    @Override
    public void setScatterChartArray(CTScatterChart[] scatterChartArray) {
        this.check_orphaned();
        this.arraySetterHelper(scatterChartArray, PROPERTY_QNAME[7]);
    }

    @Override
    public void setScatterChartArray(int i, CTScatterChart scatterChart) {
        this.generatedSetterHelperImpl(scatterChart, PROPERTY_QNAME[7], i, (short)2);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTScatterChart insertNewScatterChart(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTScatterChart target = null;
            target = (CTScatterChart)((Object)this.get_store().insert_element_user(PROPERTY_QNAME[7], i));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTScatterChart addNewScatterChart() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTScatterChart target = null;
            target = (CTScatterChart)((Object)this.get_store().add_element_user(PROPERTY_QNAME[7]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void removeScatterChart(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.get_store().remove_element(PROPERTY_QNAME[7], i);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public List<CTPieChart> getPieChartList() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return new JavaListXmlObject<CTPieChart>(this::getPieChartArray, this::setPieChartArray, this::insertNewPieChart, this::removePieChart, this::sizeOfPieChartArray);
        }
    }

    @Override
    public CTPieChart[] getPieChartArray() {
        return (CTPieChart[])this.getXmlObjectArray(PROPERTY_QNAME[8], new CTPieChart[0]);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTPieChart getPieChartArray(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTPieChart target = null;
            target = (CTPieChart)((Object)this.get_store().find_element_user(PROPERTY_QNAME[8], i));
            if (target == null) {
                throw new IndexOutOfBoundsException();
            }
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public int sizeOfPieChartArray() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[8]);
        }
    }

    @Override
    public void setPieChartArray(CTPieChart[] pieChartArray) {
        this.check_orphaned();
        this.arraySetterHelper(pieChartArray, PROPERTY_QNAME[8]);
    }

    @Override
    public void setPieChartArray(int i, CTPieChart pieChart) {
        this.generatedSetterHelperImpl(pieChart, PROPERTY_QNAME[8], i, (short)2);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTPieChart insertNewPieChart(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTPieChart target = null;
            target = (CTPieChart)((Object)this.get_store().insert_element_user(PROPERTY_QNAME[8], i));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTPieChart addNewPieChart() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTPieChart target = null;
            target = (CTPieChart)((Object)this.get_store().add_element_user(PROPERTY_QNAME[8]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void removePieChart(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.get_store().remove_element(PROPERTY_QNAME[8], i);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public List<CTPie3DChart> getPie3DChartList() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return new JavaListXmlObject<CTPie3DChart>(this::getPie3DChartArray, this::setPie3DChartArray, this::insertNewPie3DChart, this::removePie3DChart, this::sizeOfPie3DChartArray);
        }
    }

    @Override
    public CTPie3DChart[] getPie3DChartArray() {
        return (CTPie3DChart[])this.getXmlObjectArray(PROPERTY_QNAME[9], new CTPie3DChart[0]);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTPie3DChart getPie3DChartArray(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTPie3DChart target = null;
            target = (CTPie3DChart)((Object)this.get_store().find_element_user(PROPERTY_QNAME[9], i));
            if (target == null) {
                throw new IndexOutOfBoundsException();
            }
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public int sizeOfPie3DChartArray() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[9]);
        }
    }

    @Override
    public void setPie3DChartArray(CTPie3DChart[] pie3DChartArray) {
        this.check_orphaned();
        this.arraySetterHelper(pie3DChartArray, PROPERTY_QNAME[9]);
    }

    @Override
    public void setPie3DChartArray(int i, CTPie3DChart pie3DChart) {
        this.generatedSetterHelperImpl(pie3DChart, PROPERTY_QNAME[9], i, (short)2);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTPie3DChart insertNewPie3DChart(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTPie3DChart target = null;
            target = (CTPie3DChart)((Object)this.get_store().insert_element_user(PROPERTY_QNAME[9], i));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTPie3DChart addNewPie3DChart() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTPie3DChart target = null;
            target = (CTPie3DChart)((Object)this.get_store().add_element_user(PROPERTY_QNAME[9]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void removePie3DChart(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.get_store().remove_element(PROPERTY_QNAME[9], i);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public List<CTDoughnutChart> getDoughnutChartList() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return new JavaListXmlObject<CTDoughnutChart>(this::getDoughnutChartArray, this::setDoughnutChartArray, this::insertNewDoughnutChart, this::removeDoughnutChart, this::sizeOfDoughnutChartArray);
        }
    }

    @Override
    public CTDoughnutChart[] getDoughnutChartArray() {
        return (CTDoughnutChart[])this.getXmlObjectArray(PROPERTY_QNAME[10], new CTDoughnutChart[0]);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTDoughnutChart getDoughnutChartArray(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTDoughnutChart target = null;
            target = (CTDoughnutChart)((Object)this.get_store().find_element_user(PROPERTY_QNAME[10], i));
            if (target == null) {
                throw new IndexOutOfBoundsException();
            }
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public int sizeOfDoughnutChartArray() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[10]);
        }
    }

    @Override
    public void setDoughnutChartArray(CTDoughnutChart[] doughnutChartArray) {
        this.check_orphaned();
        this.arraySetterHelper(doughnutChartArray, PROPERTY_QNAME[10]);
    }

    @Override
    public void setDoughnutChartArray(int i, CTDoughnutChart doughnutChart) {
        this.generatedSetterHelperImpl(doughnutChart, PROPERTY_QNAME[10], i, (short)2);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTDoughnutChart insertNewDoughnutChart(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTDoughnutChart target = null;
            target = (CTDoughnutChart)((Object)this.get_store().insert_element_user(PROPERTY_QNAME[10], i));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTDoughnutChart addNewDoughnutChart() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTDoughnutChart target = null;
            target = (CTDoughnutChart)((Object)this.get_store().add_element_user(PROPERTY_QNAME[10]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void removeDoughnutChart(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.get_store().remove_element(PROPERTY_QNAME[10], i);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public List<CTBarChart> getBarChartList() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return new JavaListXmlObject<CTBarChart>(this::getBarChartArray, this::setBarChartArray, this::insertNewBarChart, this::removeBarChart, this::sizeOfBarChartArray);
        }
    }

    @Override
    public CTBarChart[] getBarChartArray() {
        return (CTBarChart[])this.getXmlObjectArray(PROPERTY_QNAME[11], new CTBarChart[0]);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTBarChart getBarChartArray(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTBarChart target = null;
            target = (CTBarChart)((Object)this.get_store().find_element_user(PROPERTY_QNAME[11], i));
            if (target == null) {
                throw new IndexOutOfBoundsException();
            }
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public int sizeOfBarChartArray() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[11]);
        }
    }

    @Override
    public void setBarChartArray(CTBarChart[] barChartArray) {
        this.check_orphaned();
        this.arraySetterHelper(barChartArray, PROPERTY_QNAME[11]);
    }

    @Override
    public void setBarChartArray(int i, CTBarChart barChart) {
        this.generatedSetterHelperImpl(barChart, PROPERTY_QNAME[11], i, (short)2);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTBarChart insertNewBarChart(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTBarChart target = null;
            target = (CTBarChart)((Object)this.get_store().insert_element_user(PROPERTY_QNAME[11], i));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTBarChart addNewBarChart() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTBarChart target = null;
            target = (CTBarChart)((Object)this.get_store().add_element_user(PROPERTY_QNAME[11]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void removeBarChart(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.get_store().remove_element(PROPERTY_QNAME[11], i);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public List<CTBar3DChart> getBar3DChartList() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return new JavaListXmlObject<CTBar3DChart>(this::getBar3DChartArray, this::setBar3DChartArray, this::insertNewBar3DChart, this::removeBar3DChart, this::sizeOfBar3DChartArray);
        }
    }

    @Override
    public CTBar3DChart[] getBar3DChartArray() {
        return (CTBar3DChart[])this.getXmlObjectArray(PROPERTY_QNAME[12], new CTBar3DChart[0]);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTBar3DChart getBar3DChartArray(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTBar3DChart target = null;
            target = (CTBar3DChart)((Object)this.get_store().find_element_user(PROPERTY_QNAME[12], i));
            if (target == null) {
                throw new IndexOutOfBoundsException();
            }
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public int sizeOfBar3DChartArray() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[12]);
        }
    }

    @Override
    public void setBar3DChartArray(CTBar3DChart[] bar3DChartArray) {
        this.check_orphaned();
        this.arraySetterHelper(bar3DChartArray, PROPERTY_QNAME[12]);
    }

    @Override
    public void setBar3DChartArray(int i, CTBar3DChart bar3DChart) {
        this.generatedSetterHelperImpl(bar3DChart, PROPERTY_QNAME[12], i, (short)2);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTBar3DChart insertNewBar3DChart(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTBar3DChart target = null;
            target = (CTBar3DChart)((Object)this.get_store().insert_element_user(PROPERTY_QNAME[12], i));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTBar3DChart addNewBar3DChart() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTBar3DChart target = null;
            target = (CTBar3DChart)((Object)this.get_store().add_element_user(PROPERTY_QNAME[12]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void removeBar3DChart(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.get_store().remove_element(PROPERTY_QNAME[12], i);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public List<CTOfPieChart> getOfPieChartList() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return new JavaListXmlObject<CTOfPieChart>(this::getOfPieChartArray, this::setOfPieChartArray, this::insertNewOfPieChart, this::removeOfPieChart, this::sizeOfOfPieChartArray);
        }
    }

    @Override
    public CTOfPieChart[] getOfPieChartArray() {
        return (CTOfPieChart[])this.getXmlObjectArray(PROPERTY_QNAME[13], new CTOfPieChart[0]);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTOfPieChart getOfPieChartArray(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTOfPieChart target = null;
            target = (CTOfPieChart)((Object)this.get_store().find_element_user(PROPERTY_QNAME[13], i));
            if (target == null) {
                throw new IndexOutOfBoundsException();
            }
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public int sizeOfOfPieChartArray() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[13]);
        }
    }

    @Override
    public void setOfPieChartArray(CTOfPieChart[] ofPieChartArray) {
        this.check_orphaned();
        this.arraySetterHelper(ofPieChartArray, PROPERTY_QNAME[13]);
    }

    @Override
    public void setOfPieChartArray(int i, CTOfPieChart ofPieChart) {
        this.generatedSetterHelperImpl(ofPieChart, PROPERTY_QNAME[13], i, (short)2);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTOfPieChart insertNewOfPieChart(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTOfPieChart target = null;
            target = (CTOfPieChart)((Object)this.get_store().insert_element_user(PROPERTY_QNAME[13], i));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTOfPieChart addNewOfPieChart() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTOfPieChart target = null;
            target = (CTOfPieChart)((Object)this.get_store().add_element_user(PROPERTY_QNAME[13]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void removeOfPieChart(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.get_store().remove_element(PROPERTY_QNAME[13], i);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public List<CTSurfaceChart> getSurfaceChartList() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return new JavaListXmlObject<CTSurfaceChart>(this::getSurfaceChartArray, this::setSurfaceChartArray, this::insertNewSurfaceChart, this::removeSurfaceChart, this::sizeOfSurfaceChartArray);
        }
    }

    @Override
    public CTSurfaceChart[] getSurfaceChartArray() {
        return (CTSurfaceChart[])this.getXmlObjectArray(PROPERTY_QNAME[14], new CTSurfaceChart[0]);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTSurfaceChart getSurfaceChartArray(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTSurfaceChart target = null;
            target = (CTSurfaceChart)((Object)this.get_store().find_element_user(PROPERTY_QNAME[14], i));
            if (target == null) {
                throw new IndexOutOfBoundsException();
            }
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public int sizeOfSurfaceChartArray() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[14]);
        }
    }

    @Override
    public void setSurfaceChartArray(CTSurfaceChart[] surfaceChartArray) {
        this.check_orphaned();
        this.arraySetterHelper(surfaceChartArray, PROPERTY_QNAME[14]);
    }

    @Override
    public void setSurfaceChartArray(int i, CTSurfaceChart surfaceChart) {
        this.generatedSetterHelperImpl(surfaceChart, PROPERTY_QNAME[14], i, (short)2);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTSurfaceChart insertNewSurfaceChart(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTSurfaceChart target = null;
            target = (CTSurfaceChart)((Object)this.get_store().insert_element_user(PROPERTY_QNAME[14], i));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTSurfaceChart addNewSurfaceChart() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTSurfaceChart target = null;
            target = (CTSurfaceChart)((Object)this.get_store().add_element_user(PROPERTY_QNAME[14]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void removeSurfaceChart(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.get_store().remove_element(PROPERTY_QNAME[14], i);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public List<CTSurface3DChart> getSurface3DChartList() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return new JavaListXmlObject<CTSurface3DChart>(this::getSurface3DChartArray, this::setSurface3DChartArray, this::insertNewSurface3DChart, this::removeSurface3DChart, this::sizeOfSurface3DChartArray);
        }
    }

    @Override
    public CTSurface3DChart[] getSurface3DChartArray() {
        return (CTSurface3DChart[])this.getXmlObjectArray(PROPERTY_QNAME[15], new CTSurface3DChart[0]);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTSurface3DChart getSurface3DChartArray(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTSurface3DChart target = null;
            target = (CTSurface3DChart)((Object)this.get_store().find_element_user(PROPERTY_QNAME[15], i));
            if (target == null) {
                throw new IndexOutOfBoundsException();
            }
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public int sizeOfSurface3DChartArray() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[15]);
        }
    }

    @Override
    public void setSurface3DChartArray(CTSurface3DChart[] surface3DChartArray) {
        this.check_orphaned();
        this.arraySetterHelper(surface3DChartArray, PROPERTY_QNAME[15]);
    }

    @Override
    public void setSurface3DChartArray(int i, CTSurface3DChart surface3DChart) {
        this.generatedSetterHelperImpl(surface3DChart, PROPERTY_QNAME[15], i, (short)2);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTSurface3DChart insertNewSurface3DChart(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTSurface3DChart target = null;
            target = (CTSurface3DChart)((Object)this.get_store().insert_element_user(PROPERTY_QNAME[15], i));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTSurface3DChart addNewSurface3DChart() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTSurface3DChart target = null;
            target = (CTSurface3DChart)((Object)this.get_store().add_element_user(PROPERTY_QNAME[15]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void removeSurface3DChart(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.get_store().remove_element(PROPERTY_QNAME[15], i);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public List<CTBubbleChart> getBubbleChartList() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return new JavaListXmlObject<CTBubbleChart>(this::getBubbleChartArray, this::setBubbleChartArray, this::insertNewBubbleChart, this::removeBubbleChart, this::sizeOfBubbleChartArray);
        }
    }

    @Override
    public CTBubbleChart[] getBubbleChartArray() {
        return (CTBubbleChart[])this.getXmlObjectArray(PROPERTY_QNAME[16], new CTBubbleChart[0]);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTBubbleChart getBubbleChartArray(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTBubbleChart target = null;
            target = (CTBubbleChart)((Object)this.get_store().find_element_user(PROPERTY_QNAME[16], i));
            if (target == null) {
                throw new IndexOutOfBoundsException();
            }
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public int sizeOfBubbleChartArray() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[16]);
        }
    }

    @Override
    public void setBubbleChartArray(CTBubbleChart[] bubbleChartArray) {
        this.check_orphaned();
        this.arraySetterHelper(bubbleChartArray, PROPERTY_QNAME[16]);
    }

    @Override
    public void setBubbleChartArray(int i, CTBubbleChart bubbleChart) {
        this.generatedSetterHelperImpl(bubbleChart, PROPERTY_QNAME[16], i, (short)2);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTBubbleChart insertNewBubbleChart(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTBubbleChart target = null;
            target = (CTBubbleChart)((Object)this.get_store().insert_element_user(PROPERTY_QNAME[16], i));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTBubbleChart addNewBubbleChart() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTBubbleChart target = null;
            target = (CTBubbleChart)((Object)this.get_store().add_element_user(PROPERTY_QNAME[16]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void removeBubbleChart(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.get_store().remove_element(PROPERTY_QNAME[16], i);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public List<CTValAx> getValAxList() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return new JavaListXmlObject<CTValAx>(this::getValAxArray, this::setValAxArray, this::insertNewValAx, this::removeValAx, this::sizeOfValAxArray);
        }
    }

    @Override
    public CTValAx[] getValAxArray() {
        return (CTValAx[])this.getXmlObjectArray(PROPERTY_QNAME[17], new CTValAx[0]);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTValAx getValAxArray(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTValAx target = null;
            target = (CTValAx)((Object)this.get_store().find_element_user(PROPERTY_QNAME[17], i));
            if (target == null) {
                throw new IndexOutOfBoundsException();
            }
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public int sizeOfValAxArray() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[17]);
        }
    }

    @Override
    public void setValAxArray(CTValAx[] valAxArray) {
        this.check_orphaned();
        this.arraySetterHelper(valAxArray, PROPERTY_QNAME[17]);
    }

    @Override
    public void setValAxArray(int i, CTValAx valAx) {
        this.generatedSetterHelperImpl(valAx, PROPERTY_QNAME[17], i, (short)2);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTValAx insertNewValAx(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTValAx target = null;
            target = (CTValAx)((Object)this.get_store().insert_element_user(PROPERTY_QNAME[17], i));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTValAx addNewValAx() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTValAx target = null;
            target = (CTValAx)((Object)this.get_store().add_element_user(PROPERTY_QNAME[17]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void removeValAx(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.get_store().remove_element(PROPERTY_QNAME[17], i);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public List<CTCatAx> getCatAxList() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return new JavaListXmlObject<CTCatAx>(this::getCatAxArray, this::setCatAxArray, this::insertNewCatAx, this::removeCatAx, this::sizeOfCatAxArray);
        }
    }

    @Override
    public CTCatAx[] getCatAxArray() {
        return (CTCatAx[])this.getXmlObjectArray(PROPERTY_QNAME[18], new CTCatAx[0]);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTCatAx getCatAxArray(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTCatAx target = null;
            target = (CTCatAx)((Object)this.get_store().find_element_user(PROPERTY_QNAME[18], i));
            if (target == null) {
                throw new IndexOutOfBoundsException();
            }
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public int sizeOfCatAxArray() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[18]);
        }
    }

    @Override
    public void setCatAxArray(CTCatAx[] catAxArray) {
        this.check_orphaned();
        this.arraySetterHelper(catAxArray, PROPERTY_QNAME[18]);
    }

    @Override
    public void setCatAxArray(int i, CTCatAx catAx) {
        this.generatedSetterHelperImpl(catAx, PROPERTY_QNAME[18], i, (short)2);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTCatAx insertNewCatAx(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTCatAx target = null;
            target = (CTCatAx)((Object)this.get_store().insert_element_user(PROPERTY_QNAME[18], i));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTCatAx addNewCatAx() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTCatAx target = null;
            target = (CTCatAx)((Object)this.get_store().add_element_user(PROPERTY_QNAME[18]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void removeCatAx(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.get_store().remove_element(PROPERTY_QNAME[18], i);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public List<CTDateAx> getDateAxList() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return new JavaListXmlObject<CTDateAx>(this::getDateAxArray, this::setDateAxArray, this::insertNewDateAx, this::removeDateAx, this::sizeOfDateAxArray);
        }
    }

    @Override
    public CTDateAx[] getDateAxArray() {
        return (CTDateAx[])this.getXmlObjectArray(PROPERTY_QNAME[19], new CTDateAx[0]);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTDateAx getDateAxArray(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTDateAx target = null;
            target = (CTDateAx)((Object)this.get_store().find_element_user(PROPERTY_QNAME[19], i));
            if (target == null) {
                throw new IndexOutOfBoundsException();
            }
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public int sizeOfDateAxArray() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[19]);
        }
    }

    @Override
    public void setDateAxArray(CTDateAx[] dateAxArray) {
        this.check_orphaned();
        this.arraySetterHelper(dateAxArray, PROPERTY_QNAME[19]);
    }

    @Override
    public void setDateAxArray(int i, CTDateAx dateAx) {
        this.generatedSetterHelperImpl(dateAx, PROPERTY_QNAME[19], i, (short)2);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTDateAx insertNewDateAx(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTDateAx target = null;
            target = (CTDateAx)((Object)this.get_store().insert_element_user(PROPERTY_QNAME[19], i));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTDateAx addNewDateAx() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTDateAx target = null;
            target = (CTDateAx)((Object)this.get_store().add_element_user(PROPERTY_QNAME[19]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void removeDateAx(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.get_store().remove_element(PROPERTY_QNAME[19], i);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public List<CTSerAx> getSerAxList() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return new JavaListXmlObject<CTSerAx>(this::getSerAxArray, this::setSerAxArray, this::insertNewSerAx, this::removeSerAx, this::sizeOfSerAxArray);
        }
    }

    @Override
    public CTSerAx[] getSerAxArray() {
        return (CTSerAx[])this.getXmlObjectArray(PROPERTY_QNAME[20], new CTSerAx[0]);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTSerAx getSerAxArray(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTSerAx target = null;
            target = (CTSerAx)((Object)this.get_store().find_element_user(PROPERTY_QNAME[20], i));
            if (target == null) {
                throw new IndexOutOfBoundsException();
            }
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public int sizeOfSerAxArray() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[20]);
        }
    }

    @Override
    public void setSerAxArray(CTSerAx[] serAxArray) {
        this.check_orphaned();
        this.arraySetterHelper(serAxArray, PROPERTY_QNAME[20]);
    }

    @Override
    public void setSerAxArray(int i, CTSerAx serAx) {
        this.generatedSetterHelperImpl(serAx, PROPERTY_QNAME[20], i, (short)2);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTSerAx insertNewSerAx(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTSerAx target = null;
            target = (CTSerAx)((Object)this.get_store().insert_element_user(PROPERTY_QNAME[20], i));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTSerAx addNewSerAx() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTSerAx target = null;
            target = (CTSerAx)((Object)this.get_store().add_element_user(PROPERTY_QNAME[20]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void removeSerAx(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.get_store().remove_element(PROPERTY_QNAME[20], i);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTDTable getDTable() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTDTable target = null;
            target = (CTDTable)this.get_store().find_element_user(PROPERTY_QNAME[21], 0);
            return target == null ? null : target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean isSetDTable() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[21]) != 0;
        }
    }

    @Override
    public void setDTable(CTDTable dTable) {
        this.generatedSetterHelperImpl((XmlObject)dTable, PROPERTY_QNAME[21], 0, (short)1);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTDTable addNewDTable() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTDTable target = null;
            target = (CTDTable)this.get_store().add_element_user(PROPERTY_QNAME[21]);
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void unsetDTable() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.get_store().remove_element(PROPERTY_QNAME[21], 0);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTShapeProperties getSpPr() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTShapeProperties target = null;
            target = (CTShapeProperties)((Object)this.get_store().find_element_user(PROPERTY_QNAME[22], 0));
            return target == null ? null : target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean isSetSpPr() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[22]) != 0;
        }
    }

    @Override
    public void setSpPr(CTShapeProperties spPr) {
        this.generatedSetterHelperImpl(spPr, PROPERTY_QNAME[22], 0, (short)1);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTShapeProperties addNewSpPr() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTShapeProperties target = null;
            target = (CTShapeProperties)((Object)this.get_store().add_element_user(PROPERTY_QNAME[22]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void unsetSpPr() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.get_store().remove_element(PROPERTY_QNAME[22], 0);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTExtensionList getExtLst() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTExtensionList target = null;
            target = (CTExtensionList)((Object)this.get_store().find_element_user(PROPERTY_QNAME[23], 0));
            return target == null ? null : target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean isSetExtLst() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[23]) != 0;
        }
    }

    @Override
    public void setExtLst(CTExtensionList extLst) {
        this.generatedSetterHelperImpl(extLst, PROPERTY_QNAME[23], 0, (short)1);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTExtensionList addNewExtLst() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTExtensionList target = null;
            target = (CTExtensionList)((Object)this.get_store().add_element_user(PROPERTY_QNAME[23]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void unsetExtLst() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.get_store().remove_element(PROPERTY_QNAME[23], 0);
        }
    }
}


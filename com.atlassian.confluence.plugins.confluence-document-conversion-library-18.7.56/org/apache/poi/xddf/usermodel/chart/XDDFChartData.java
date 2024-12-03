/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.xddf.usermodel.chart;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.ss.util.CellReference;
import org.apache.poi.util.Internal;
import org.apache.poi.util.Removal;
import org.apache.poi.xddf.usermodel.XDDFFillProperties;
import org.apache.poi.xddf.usermodel.XDDFLineProperties;
import org.apache.poi.xddf.usermodel.XDDFShapeProperties;
import org.apache.poi.xddf.usermodel.chart.XDDFCategoryAxis;
import org.apache.poi.xddf.usermodel.chart.XDDFChart;
import org.apache.poi.xddf.usermodel.chart.XDDFChartAxis;
import org.apache.poi.xddf.usermodel.chart.XDDFDataPoint;
import org.apache.poi.xddf.usermodel.chart.XDDFDataSource;
import org.apache.poi.xddf.usermodel.chart.XDDFNumericalDataSource;
import org.apache.poi.xddf.usermodel.chart.XDDFValueAxis;
import org.apache.xmlbeans.XmlObject;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTAxDataSource;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTDPt;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTNumData;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTNumDataSource;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTNumRef;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTSerTx;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTStrData;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTStrRef;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTUnsignedInt;

public abstract class XDDFChartData {
    private static final Logger LOGGER = LogManager.getLogger(XDDFChartData.class);
    protected XDDFChart parent;
    protected List<Series> series;
    private XDDFCategoryAxis categoryAxis;
    private List<XDDFValueAxis> valueAxes;

    protected XDDFChartData(XDDFChart chart) {
        this.parent = chart;
        this.series = new ArrayList<Series>();
    }

    protected void defineAxes(CTUnsignedInt[] axes, Map<Long, XDDFChartAxis> categories, Map<Long, XDDFValueAxis> values) {
        ArrayList<XDDFValueAxis> list = new ArrayList<XDDFValueAxis>(axes.length);
        for (CTUnsignedInt axe : axes) {
            Long axisId = axe.getVal();
            XDDFChartAxis category = categories.get(axisId);
            if (category == null) {
                XDDFValueAxis axis = values.get(axisId);
                if (axis == null) continue;
                list.add(axis);
                continue;
            }
            if (!(category instanceof XDDFCategoryAxis)) continue;
            this.categoryAxis = (XDDFCategoryAxis)category;
        }
        this.valueAxes = Collections.unmodifiableList(list);
    }

    public XDDFCategoryAxis getCategoryAxis() {
        return this.categoryAxis;
    }

    public List<XDDFValueAxis> getValueAxes() {
        return this.valueAxes;
    }

    @Deprecated
    @Removal(version="5.3")
    public List<Series> getSeries() {
        return Collections.unmodifiableList(this.series);
    }

    public final int getSeriesCount() {
        return this.series.size();
    }

    public final Series getSeries(int n) {
        return this.series.get(n);
    }

    public final void removeSeries(int n) {
        String procName = "removeSeries";
        if (n < 0 || this.series.size() <= n) {
            throw new IllegalArgumentException(String.format(Locale.ROOT, "%s(%d): illegal index", "removeSeries", n));
        }
        this.series.remove(n);
        this.removeCTSeries(n);
    }

    @Internal
    protected abstract void removeCTSeries(int var1);

    public abstract void setVaryColors(Boolean var1);

    public abstract Series addSeries(XDDFDataSource<?> var1, XDDFNumericalDataSource<? extends Number> var2);

    public static abstract class Series {
        protected XDDFDataSource<?> categoryData;
        protected XDDFNumericalDataSource<? extends Number> valuesData;

        protected abstract CTSerTx getSeriesText();

        public abstract void setShowLeaderLines(boolean var1);

        public abstract XDDFShapeProperties getShapeProperties();

        public abstract void setShapeProperties(XDDFShapeProperties var1);

        protected abstract CTAxDataSource getAxDS();

        protected abstract CTNumDataSource getNumDS();

        protected abstract void setIndex(long var1);

        protected abstract void setOrder(long var1);

        protected abstract List<CTDPt> getDPtList();

        protected Series(XDDFDataSource<?> category, XDDFNumericalDataSource<? extends Number> values) {
            this.replaceData(category, values);
        }

        public void replaceData(XDDFDataSource<?> category, XDDFNumericalDataSource<? extends Number> values) {
            int numOfPoints;
            if (this.categoryData != null && values != null && (numOfPoints = category.getPointCount()) != values.getPointCount()) {
                LOGGER.warn("Category and values must have the same point count, but had " + numOfPoints + " categories and " + values.getPointCount() + " values.");
            }
            this.categoryData = category;
            this.valuesData = values;
        }

        public void setTitle(String title) {
            this.setTitle(title, null);
        }

        public void setTitle(String title, CellReference titleRef) {
            if (titleRef == null) {
                this.getSeriesText().setV(title);
            } else {
                CTStrRef ref = this.getSeriesText().isSetStrRef() ? this.getSeriesText().getStrRef() : this.getSeriesText().addNewStrRef();
                ref.setF(titleRef.formatAsString());
                if (title != null) {
                    CTStrData cache = ref.isSetStrCache() ? ref.getStrCache() : ref.addNewStrCache();
                    if (cache.sizeOfPtArray() < 1) {
                        cache.addNewPtCount().setVal(1L);
                        cache.addNewPt().setIdx(0L);
                    }
                    cache.getPtArray(0).setV(title);
                }
            }
        }

        public XDDFDataSource<?> getCategoryData() {
            return this.categoryData;
        }

        public XDDFNumericalDataSource<? extends Number> getValuesData() {
            return this.valuesData;
        }

        public void plot() {
            XmlObject cache;
            if (this.categoryData != null) {
                if (this.categoryData.isNumeric()) {
                    cache = this.retrieveNumCache(this.getAxDS(), this.categoryData);
                    this.categoryData.fillNumericalCache((CTNumData)cache);
                } else {
                    cache = this.retrieveStrCache(this.getAxDS(), this.categoryData);
                    this.categoryData.fillStringCache((CTStrData)cache);
                }
            }
            if (this.valuesData != null) {
                cache = this.retrieveNumCache(this.getNumDS(), this.valuesData);
                this.valuesData.fillNumericalCache((CTNumData)cache);
            }
        }

        public void setFillProperties(XDDFFillProperties fill) {
            XDDFShapeProperties properties = this.getShapeProperties();
            if (properties == null) {
                properties = new XDDFShapeProperties();
            }
            properties.setFillProperties(fill);
            this.setShapeProperties(properties);
        }

        public void setLineProperties(XDDFLineProperties line) {
            XDDFShapeProperties properties = this.getShapeProperties();
            if (properties == null) {
                properties = new XDDFShapeProperties();
            }
            properties.setLineProperties(line);
            this.setShapeProperties(properties);
        }

        public void clearDataPoint(long index) {
            List<CTDPt> points = this.getDPtList();
            for (int i = 0; i < points.size(); ++i) {
                if (points.get(i).getIdx().getVal() != index) continue;
                points.remove(i);
                break;
            }
        }

        public XDDFDataPoint getDataPoint(long index) {
            List<CTDPt> points = this.getDPtList();
            for (int i = 0; i < points.size(); ++i) {
                if (points.get(i).getIdx().getVal() == index) {
                    return new XDDFDataPoint(points.get(i));
                }
                if (points.get(i).getIdx().getVal() <= index) continue;
                points.add(i, CTDPt.Factory.newInstance());
                CTDPt point = points.get(i);
                point.addNewIdx().setVal(index);
                return new XDDFDataPoint(point);
            }
            points.add(CTDPt.Factory.newInstance());
            CTDPt point = points.get(points.size() - 1);
            point.addNewIdx().setVal(index);
            return new XDDFDataPoint(point);
        }

        private CTNumData retrieveNumCache(CTAxDataSource axDataSource, XDDFDataSource<?> data) {
            CTNumData numCache;
            if (data.isReference()) {
                CTNumRef numRef = axDataSource.isSetNumRef() ? axDataSource.getNumRef() : axDataSource.addNewNumRef();
                numCache = numRef.isSetNumCache() ? numRef.getNumCache() : numRef.addNewNumCache();
                numRef.setF(data.getDataRangeReference());
                if (axDataSource.isSetNumLit()) {
                    axDataSource.unsetNumLit();
                }
            } else {
                numCache = axDataSource.isSetNumLit() ? axDataSource.getNumLit() : axDataSource.addNewNumLit();
                if (axDataSource.isSetNumRef()) {
                    axDataSource.unsetNumRef();
                }
            }
            return numCache;
        }

        private CTStrData retrieveStrCache(CTAxDataSource axDataSource, XDDFDataSource<?> data) {
            CTStrData strCache;
            if (data.isReference()) {
                CTStrRef strRef = axDataSource.isSetStrRef() ? axDataSource.getStrRef() : axDataSource.addNewStrRef();
                strCache = strRef.isSetStrCache() ? strRef.getStrCache() : strRef.addNewStrCache();
                strRef.setF(data.getDataRangeReference());
                if (axDataSource.isSetStrLit()) {
                    axDataSource.unsetStrLit();
                }
            } else {
                strCache = axDataSource.isSetStrLit() ? axDataSource.getStrLit() : axDataSource.addNewStrLit();
                if (axDataSource.isSetStrRef()) {
                    axDataSource.unsetStrRef();
                }
            }
            return strCache;
        }

        protected CTNumData retrieveNumCache(CTNumDataSource numDataSource, XDDFDataSource<?> data) {
            CTNumData numCache;
            if (data.isReference()) {
                CTNumRef numRef = numDataSource.isSetNumRef() ? numDataSource.getNumRef() : numDataSource.addNewNumRef();
                numCache = numRef.isSetNumCache() ? numRef.getNumCache() : numRef.addNewNumCache();
                numRef.setF(data.getDataRangeReference());
                if (numDataSource.isSetNumLit()) {
                    numDataSource.unsetNumLit();
                }
            } else {
                numCache = numDataSource.isSetNumLit() ? numDataSource.getNumLit() : numDataSource.addNewNumLit();
                if (numDataSource.isSetNumRef()) {
                    numDataSource.unsetNumRef();
                }
            }
            return numCache;
        }
    }
}


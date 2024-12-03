/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.xddf.usermodel.chart;

import java.util.List;
import org.apache.poi.ooxml.util.POIXMLUnits;
import org.apache.poi.util.Internal;
import org.apache.poi.xddf.usermodel.XDDFShapeProperties;
import org.apache.poi.xddf.usermodel.chart.XDDFChart;
import org.apache.poi.xddf.usermodel.chart.XDDFChartData;
import org.apache.poi.xddf.usermodel.chart.XDDFDataSource;
import org.apache.poi.xddf.usermodel.chart.XDDFDataSourcesFactory;
import org.apache.poi.xddf.usermodel.chart.XDDFNumericalDataSource;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTAxDataSource;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTDPt;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTDoughnutChart;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTNumDataSource;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTPieSer;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTSerTx;

public class XDDFDoughnutChartData
extends XDDFChartData {
    private CTDoughnutChart chart;

    @Internal
    protected XDDFDoughnutChartData(XDDFChart parent, CTDoughnutChart chart) {
        super(parent);
        this.chart = chart;
        for (CTPieSer series : chart.getSerList()) {
            this.series.add(new Series(series, series.getCat(), series.getVal()));
        }
    }

    @Override
    @Internal
    protected void removeCTSeries(int n) {
        this.chart.removeSer(n);
    }

    @Override
    public void setVaryColors(Boolean varyColors) {
        if (varyColors == null) {
            if (this.chart.isSetVaryColors()) {
                this.chart.unsetVaryColors();
            }
        } else if (this.chart.isSetVaryColors()) {
            this.chart.getVaryColors().setVal(varyColors);
        } else {
            this.chart.addNewVaryColors().setVal(varyColors);
        }
    }

    public Integer getFirstSliceAngle() {
        if (this.chart.isSetFirstSliceAng()) {
            return this.chart.getFirstSliceAng().getVal();
        }
        return null;
    }

    public void setFirstSliceAngle(Integer angle) {
        if (angle == null) {
            if (this.chart.isSetFirstSliceAng()) {
                this.chart.unsetFirstSliceAng();
            }
        } else {
            if (angle < 0 || 360 < angle) {
                throw new IllegalArgumentException("Value of angle must be between 0 and 360, both inclusive.");
            }
            if (this.chart.isSetFirstSliceAng()) {
                this.chart.getFirstSliceAng().setVal(angle);
            } else {
                this.chart.addNewFirstSliceAng().setVal(angle);
            }
        }
    }

    public Integer getHoleSize() {
        if (this.chart.isSetHoleSize()) {
            return POIXMLUnits.parsePercent(this.chart.getHoleSize().xgetVal());
        }
        return null;
    }

    public void setHoleSize(Integer holeSize) {
        if (holeSize == null) {
            if (this.chart.isSetHoleSize()) {
                this.chart.unsetHoleSize();
            }
        } else {
            if (holeSize < 10 || holeSize > 90) {
                throw new IllegalArgumentException("Value of holeSize must be between 10 and 90, both inclusive.");
            }
            if (this.chart.isSetHoleSize()) {
                this.chart.getHoleSize().setVal(holeSize);
            } else {
                this.chart.addNewHoleSize().setVal(holeSize);
            }
        }
    }

    @Override
    public XDDFChartData.Series addSeries(XDDFDataSource<?> category, XDDFNumericalDataSource<? extends Number> values) {
        long index = this.parent.incrementSeriesCount();
        CTPieSer ctSer = this.chart.addNewSer();
        ctSer.addNewCat();
        ctSer.addNewVal();
        ctSer.addNewIdx().setVal(index);
        ctSer.addNewOrder().setVal(index);
        Series added = new Series(ctSer, category, values);
        this.series.add(added);
        return added;
    }

    public class Series
    extends XDDFChartData.Series {
        private CTPieSer series;

        protected Series(CTPieSer series, XDDFDataSource<?> category, XDDFNumericalDataSource<? extends Number> values) {
            super(category, values);
            this.series = series;
        }

        protected Series(CTPieSer series, CTAxDataSource category, CTNumDataSource values) {
            super(XDDFDataSourcesFactory.fromDataSource(category), XDDFDataSourcesFactory.fromDataSource(values));
            this.series = series;
        }

        public CTPieSer getCTPieSer() {
            return this.series;
        }

        @Override
        protected CTSerTx getSeriesText() {
            if (this.series.isSetTx()) {
                return this.series.getTx();
            }
            return this.series.addNewTx();
        }

        @Override
        public void setShowLeaderLines(boolean showLeaderLines) {
            if (!this.series.isSetDLbls()) {
                this.series.addNewDLbls();
            }
            if (this.series.getDLbls().isSetShowLeaderLines()) {
                this.series.getDLbls().getShowLeaderLines().setVal(showLeaderLines);
            } else {
                this.series.getDLbls().addNewShowLeaderLines().setVal(showLeaderLines);
            }
        }

        @Override
        public XDDFShapeProperties getShapeProperties() {
            if (this.series.isSetSpPr()) {
                return new XDDFShapeProperties(this.series.getSpPr());
            }
            return null;
        }

        @Override
        public void setShapeProperties(XDDFShapeProperties properties) {
            if (properties == null) {
                if (this.series.isSetSpPr()) {
                    this.series.unsetSpPr();
                }
            } else if (this.series.isSetSpPr()) {
                this.series.setSpPr(properties.getXmlObject());
            } else {
                this.series.addNewSpPr().set(properties.getXmlObject());
            }
        }

        public Long getExplosion() {
            if (this.series.isSetExplosion()) {
                return this.series.getExplosion().getVal();
            }
            return null;
        }

        public void setExplosion(Long explosion) {
            if (explosion == null) {
                if (this.series.isSetExplosion()) {
                    this.series.unsetExplosion();
                }
            } else if (this.series.isSetExplosion()) {
                this.series.getExplosion().setVal(explosion);
            } else {
                this.series.addNewExplosion().setVal(explosion);
            }
        }

        @Override
        protected CTAxDataSource getAxDS() {
            return this.series.getCat();
        }

        @Override
        protected CTNumDataSource getNumDS() {
            return this.series.getVal();
        }

        @Override
        protected void setIndex(long val) {
            this.series.getIdx().setVal(val);
        }

        @Override
        protected void setOrder(long val) {
            this.series.getOrder().setVal(val);
        }

        @Override
        protected List<CTDPt> getDPtList() {
            return this.series.getDPtList();
        }
    }
}


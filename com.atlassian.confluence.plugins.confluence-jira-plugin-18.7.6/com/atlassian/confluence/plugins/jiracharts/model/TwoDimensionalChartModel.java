/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.plugins.jiracharts.model;

import com.atlassian.confluence.plugins.jiracharts.model.JiraHtmlChartModel;
import java.util.List;

public class TwoDimensionalChartModel
implements JiraHtmlChartModel {
    private String xHeading;
    private String yHeading;
    private boolean showTotals;
    private int totalRows;
    private Row firstRow;
    private List<Row> rows;

    public String getxHeading() {
        return this.xHeading;
    }

    public void setxHeading(String xHeading) {
        this.xHeading = xHeading;
    }

    public String getyHeading() {
        return this.yHeading;
    }

    public void setyHeading(String yHeading) {
        this.yHeading = yHeading;
    }

    public Row getFirstRow() {
        return this.firstRow;
    }

    public void setFirstRow(Row firstRow) {
        this.firstRow = firstRow;
    }

    public List<Row> getRows() {
        return this.rows;
    }

    public void setRows(List<Row> rows) {
        this.rows = rows;
    }

    public boolean isShowTotals() {
        return this.showTotals;
    }

    public void setShowTotals(boolean showTotals) {
        this.showTotals = showTotals;
    }

    public int getTotalRows() {
        return this.totalRows;
    }

    public void setTotalRows(int totalRows) {
        this.totalRows = totalRows;
    }

    public static class Cell {
        private String markup;
        private String[] classes;

        public String getMarkup() {
            return this.markup;
        }

        public void setMarkup(String markup) {
            this.markup = markup;
        }

        public String[] getClasses() {
            return this.classes;
        }

        public String getCssClass() {
            Object cssClass = "";
            if (this.classes == null || this.classes.length == 0) {
                return cssClass;
            }
            for (String className : this.classes) {
                cssClass = (String)cssClass + className + " ";
            }
            return cssClass;
        }

        public void setClasses(String[] classes) {
            this.classes = classes;
        }
    }

    public static class Row {
        private List<Cell> cells;

        public List<Cell> getCells() {
            return this.cells;
        }

        public void setCells(List<Cell> cells) {
            this.cells = cells;
        }
    }
}


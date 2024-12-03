/*
 * Decompiled with CFR 0.152.
 */
package com.lowagie.text.html.simpleparser;

import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class IncTable {
    private Map<String, String> props = new HashMap<String, String>();
    private List<List<PdfPCell>> rows = new ArrayList<List<PdfPCell>>();
    private List<PdfPCell> cols;

    @Deprecated
    public IncTable(HashMap props) {
        this.props.putAll(props);
    }

    public IncTable(Map<String, String> props) {
        this.props.putAll(props);
    }

    public void addCol(PdfPCell cell) {
        if (this.cols == null) {
            this.cols = new ArrayList<PdfPCell>();
        }
        this.cols.add(cell);
    }

    @Deprecated
    public void addCols(ArrayList ncols) {
        if (this.cols == null) {
            this.cols = ncols;
        } else {
            this.cols.addAll(ncols);
        }
    }

    public void addCols(List<PdfPCell> ncols) {
        if (this.cols == null) {
            this.cols = new ArrayList<PdfPCell>(ncols);
        } else {
            this.cols.addAll(ncols);
        }
    }

    public void endRow() {
        if (this.cols != null) {
            Collections.reverse(this.cols);
            this.rows.add(this.cols);
            this.cols = null;
        }
    }

    @Deprecated
    public ArrayList getRows() {
        return (ArrayList)this.rows;
    }

    public List<List<PdfPCell>> getTableRows() {
        return this.rows;
    }

    public PdfPTable buildTable() {
        if (this.rows.isEmpty()) {
            return new PdfPTable(1);
        }
        int ncol = 0;
        for (PdfPCell pCell : this.rows.get(0)) {
            ncol += pCell.getColspan();
        }
        PdfPTable table = new PdfPTable(ncol);
        String width = this.props.get("width");
        if (width == null) {
            table.setWidthPercentage(100.0f);
        } else if (width.endsWith("%")) {
            table.setWidthPercentage(Float.parseFloat(width.substring(0, width.length() - 1)));
        } else {
            table.setTotalWidth(Float.parseFloat(width));
            table.setLockedWidth(true);
        }
        for (List<PdfPCell> col : this.rows) {
            for (PdfPCell pdfPCell : col) {
                table.addCell(pdfPCell);
            }
        }
        return table;
    }
}


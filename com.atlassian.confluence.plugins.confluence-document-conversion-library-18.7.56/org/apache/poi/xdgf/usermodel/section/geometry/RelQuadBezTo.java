/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.xdgf.usermodel.section.geometry;

import com.microsoft.schemas.office.visio.x2012.main.CellType;
import com.microsoft.schemas.office.visio.x2012.main.RowType;
import java.awt.geom.Path2D;
import org.apache.poi.ooxml.POIXMLException;
import org.apache.poi.xdgf.usermodel.XDGFCell;
import org.apache.poi.xdgf.usermodel.XDGFShape;
import org.apache.poi.xdgf.usermodel.section.geometry.GeometryRow;

public class RelQuadBezTo
implements GeometryRow {
    RelQuadBezTo _master;
    Double x;
    Double y;
    Double a;
    Double b;
    Boolean deleted;

    public RelQuadBezTo(RowType row) {
        if (row.isSetDel()) {
            this.deleted = row.getDel();
        }
        block12: for (CellType cell : row.getCellArray()) {
            String cellName;
            switch (cellName = cell.getN()) {
                case "X": {
                    this.x = XDGFCell.parseDoubleValue(cell);
                    continue block12;
                }
                case "Y": {
                    this.y = XDGFCell.parseDoubleValue(cell);
                    continue block12;
                }
                case "A": {
                    this.a = XDGFCell.parseDoubleValue(cell);
                    continue block12;
                }
                case "B": {
                    this.b = XDGFCell.parseDoubleValue(cell);
                    continue block12;
                }
                default: {
                    throw new POIXMLException("Invalid cell '" + cellName + "' in RelQuadBezTo row");
                }
            }
        }
    }

    public boolean getDel() {
        if (this.deleted != null) {
            return this.deleted;
        }
        return this._master != null && this._master.getDel();
    }

    public Double getX() {
        return this.x == null ? this._master.x : this.x;
    }

    public Double getY() {
        return this.y == null ? this._master.y : this.y;
    }

    public Double getA() {
        return this.a == null ? this._master.a : this.a;
    }

    public Double getB() {
        return this.b == null ? this._master.b : this.b;
    }

    @Override
    public void setupMaster(GeometryRow row) {
        this._master = (RelQuadBezTo)row;
    }

    @Override
    public void addToPath(Path2D.Double path, XDGFShape parent) {
        if (this.getDel()) {
            return;
        }
        double w = parent.getWidth();
        double h = parent.getHeight();
        path.quadTo(this.getA() * w, this.getB() * h, this.getX() * w, this.getY() * h);
    }
}


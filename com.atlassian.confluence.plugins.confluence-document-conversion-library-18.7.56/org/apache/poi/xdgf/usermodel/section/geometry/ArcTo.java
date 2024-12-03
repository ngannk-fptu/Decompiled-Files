/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.xdgf.usermodel.section.geometry;

import com.microsoft.schemas.office.visio.x2012.main.CellType;
import com.microsoft.schemas.office.visio.x2012.main.RowType;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;
import org.apache.poi.ooxml.POIXMLException;
import org.apache.poi.util.LocaleUtil;
import org.apache.poi.xdgf.usermodel.XDGFCell;
import org.apache.poi.xdgf.usermodel.XDGFShape;
import org.apache.poi.xdgf.usermodel.section.geometry.EllipticalArcTo;
import org.apache.poi.xdgf.usermodel.section.geometry.GeometryRow;

public class ArcTo
implements GeometryRow {
    ArcTo _master;
    Double x;
    Double y;
    Double a;
    Boolean deleted;

    public ArcTo(RowType row) {
        if (row.isSetDel()) {
            this.deleted = row.getDel();
        }
        block10: for (CellType cell : row.getCellArray()) {
            String cellName;
            switch (cellName = cell.getN()) {
                case "X": {
                    this.x = XDGFCell.parseDoubleValue(cell);
                    continue block10;
                }
                case "Y": {
                    this.y = XDGFCell.parseDoubleValue(cell);
                    continue block10;
                }
                case "A": {
                    this.a = XDGFCell.parseDoubleValue(cell);
                    continue block10;
                }
                default: {
                    throw new POIXMLException("Invalid cell '" + cellName + "' in ArcTo row");
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

    @Override
    public void setupMaster(GeometryRow row) {
        this._master = (ArcTo)row;
    }

    @Override
    public void addToPath(Path2D.Double path, XDGFShape parent) {
        if (this.getDel()) {
            return;
        }
        Point2D last = path.getCurrentPoint();
        double x = this.getX();
        double y = this.getY();
        double a = this.getA();
        if (a == 0.0) {
            path.lineTo(x, y);
            return;
        }
        double x0 = last.getX();
        double y0 = last.getY();
        double nx = y - y0;
        double ny = x0 - x;
        double nLength = Math.sqrt(nx * nx + ny * ny);
        double x1 = (x0 + x) / 2.0 + a * nx / nLength;
        double y1 = (y0 + y) / 2.0 + a * ny / nLength;
        EllipticalArcTo.createEllipticalArc(x, y, x1, y1, 0.0, 1.0, path);
    }

    public String toString() {
        return String.format(LocaleUtil.getUserLocale(), "ArcTo: x=%f; y=%f; a=%f", this.x, this.y, this.a);
    }
}


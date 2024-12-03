/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.xdgf.usermodel.section.geometry;

import com.microsoft.schemas.office.visio.x2012.main.CellType;
import com.microsoft.schemas.office.visio.x2012.main.RowType;
import java.awt.geom.Path2D;
import org.apache.poi.ooxml.POIXMLException;
import org.apache.poi.util.NotImplemented;
import org.apache.poi.xdgf.usermodel.XDGFCell;
import org.apache.poi.xdgf.usermodel.XDGFShape;
import org.apache.poi.xdgf.usermodel.section.geometry.GeometryRow;

public class PolyLineTo
implements GeometryRow {
    PolyLineTo _master;
    Double x;
    Double y;
    String a;
    Boolean deleted;

    public PolyLineTo(RowType row) {
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
                    this.a = cell.getV();
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

    public String getA() {
        return this.a == null ? this._master.a : this.a;
    }

    @Override
    public void setupMaster(GeometryRow row) {
        this._master = (PolyLineTo)row;
    }

    @Override
    @NotImplemented
    public void addToPath(Path2D.Double path, XDGFShape parent) {
        if (this.getDel()) {
            return;
        }
        throw new POIXMLException("Polyline support not implemented");
    }
}


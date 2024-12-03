/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.hslf.usermodel;

import java.awt.Color;
import java.awt.geom.Rectangle2D;
import org.apache.poi.ddf.AbstractEscherOptRecord;
import org.apache.poi.ddf.EscherContainerRecord;
import org.apache.poi.ddf.EscherPropertyTypes;
import org.apache.poi.hslf.usermodel.HSLFLine;
import org.apache.poi.hslf.usermodel.HSLFShape;
import org.apache.poi.hslf.usermodel.HSLFTable;
import org.apache.poi.hslf.usermodel.HSLFTextBox;
import org.apache.poi.hslf.usermodel.HSLFTextParagraph;
import org.apache.poi.sl.draw.DrawPaint;
import org.apache.poi.sl.usermodel.PaintStyle;
import org.apache.poi.sl.usermodel.ShapeType;
import org.apache.poi.sl.usermodel.StrokeStyle;
import org.apache.poi.sl.usermodel.TableCell;

public final class HSLFTableCell
extends HSLFTextBox
implements TableCell<HSLFShape, HSLFTextParagraph> {
    protected static final int DEFAULT_WIDTH = 100;
    protected static final int DEFAULT_HEIGHT = 40;
    HSLFLine borderLeft;
    HSLFLine borderRight;
    HSLFLine borderTop;
    HSLFLine borderBottom;
    private int gridSpan = 1;
    private int rowSpan = 1;

    protected HSLFTableCell(EscherContainerRecord escherRecord, HSLFTable parent) {
        super(escherRecord, parent);
    }

    public HSLFTableCell(HSLFTable parent) {
        super(parent);
        this.setShapeType(ShapeType.RECT);
    }

    @Override
    protected EscherContainerRecord createSpContainer(boolean isChild) {
        EscherContainerRecord ecr = super.createSpContainer(isChild);
        AbstractEscherOptRecord opt = this.getEscherOptRecord();
        HSLFTableCell.setEscherProperty(opt, EscherPropertyTypes.TEXT__TEXTID, 0);
        HSLFTableCell.setEscherProperty(opt, EscherPropertyTypes.TEXT__SIZE_TEXT_TO_FIT_SHAPE, 131072);
        HSLFTableCell.setEscherProperty(opt, EscherPropertyTypes.FILL__NOFILLHITTEST, 0x150001);
        HSLFTableCell.setEscherProperty(opt, EscherPropertyTypes.SHADOWSTYLE__SHADOWOBSURED, 131072);
        HSLFTableCell.setEscherProperty(opt, EscherPropertyTypes.PROTECTION__LOCKAGAINSTGROUPING, 262144);
        return ecr;
    }

    private void anchorBorder(TableCell.BorderEdge edge, HSLFLine line) {
        double h;
        double w;
        double y;
        double x;
        if (line == null) {
            return;
        }
        Rectangle2D cellAnchor = this.getAnchor();
        switch (edge) {
            case top: {
                x = cellAnchor.getX();
                y = cellAnchor.getY();
                w = cellAnchor.getWidth();
                h = 0.0;
                break;
            }
            case right: {
                x = cellAnchor.getX() + cellAnchor.getWidth();
                y = cellAnchor.getY();
                w = 0.0;
                h = cellAnchor.getHeight();
                break;
            }
            case bottom: {
                x = cellAnchor.getX();
                y = cellAnchor.getY() + cellAnchor.getHeight();
                w = cellAnchor.getWidth();
                h = 0.0;
                break;
            }
            case left: {
                x = cellAnchor.getX();
                y = cellAnchor.getY();
                w = 0.0;
                h = cellAnchor.getHeight();
                break;
            }
            default: {
                throw new IllegalArgumentException();
            }
        }
        line.setAnchor(new Rectangle2D.Double(x, y, w, h));
    }

    @Override
    public void setAnchor(Rectangle2D anchor) {
        super.setAnchor(anchor);
        this.anchorBorder(TableCell.BorderEdge.top, this.borderTop);
        this.anchorBorder(TableCell.BorderEdge.right, this.borderRight);
        this.anchorBorder(TableCell.BorderEdge.bottom, this.borderBottom);
        this.anchorBorder(TableCell.BorderEdge.left, this.borderLeft);
    }

    @Override
    public StrokeStyle getBorderStyle(final TableCell.BorderEdge edge) {
        final Double width = this.getBorderWidth(edge);
        return width == null ? null : new StrokeStyle(){

            @Override
            public PaintStyle getPaint() {
                return DrawPaint.createSolidPaint(HSLFTableCell.this.getBorderColor(edge));
            }

            @Override
            public StrokeStyle.LineCap getLineCap() {
                return null;
            }

            @Override
            public StrokeStyle.LineDash getLineDash() {
                return HSLFTableCell.this.getBorderDash(edge);
            }

            @Override
            public StrokeStyle.LineCompound getLineCompound() {
                return HSLFTableCell.this.getBorderCompound(edge);
            }

            @Override
            public double getLineWidth() {
                return width;
            }
        };
    }

    @Override
    public void setBorderStyle(TableCell.BorderEdge edge, StrokeStyle style) {
        StrokeStyle.LineDash dash;
        if (style == null) {
            throw new IllegalArgumentException("StrokeStyle needs to be specified.");
        }
        StrokeStyle.LineCompound compound = style.getLineCompound();
        if (compound != null) {
            this.setBorderCompound(edge, compound);
        }
        if ((dash = style.getLineDash()) != null) {
            this.setBorderDash(edge, dash);
        }
        double width = style.getLineWidth();
        this.setBorderWidth(edge, width);
    }

    public Double getBorderWidth(TableCell.BorderEdge edge) {
        HSLFLine l;
        switch (edge) {
            case bottom: {
                l = this.borderBottom;
                break;
            }
            case top: {
                l = this.borderTop;
                break;
            }
            case right: {
                l = this.borderRight;
                break;
            }
            case left: {
                l = this.borderLeft;
                break;
            }
            default: {
                throw new IllegalArgumentException();
            }
        }
        return l == null ? null : Double.valueOf(l.getLineWidth());
    }

    @Override
    public void setBorderWidth(TableCell.BorderEdge edge, double width) {
        HSLFLine l = this.addLine(edge);
        l.setLineWidth(width);
    }

    public Color getBorderColor(TableCell.BorderEdge edge) {
        HSLFLine l;
        switch (edge) {
            case bottom: {
                l = this.borderBottom;
                break;
            }
            case top: {
                l = this.borderTop;
                break;
            }
            case right: {
                l = this.borderRight;
                break;
            }
            case left: {
                l = this.borderLeft;
                break;
            }
            default: {
                throw new IllegalArgumentException();
            }
        }
        return l == null ? null : l.getLineColor();
    }

    @Override
    public void setBorderColor(TableCell.BorderEdge edge, Color color) {
        if (edge == null || color == null) {
            throw new IllegalArgumentException("BorderEdge and/or Color need to be specified.");
        }
        HSLFLine l = this.addLine(edge);
        l.setLineColor(color);
    }

    public StrokeStyle.LineDash getBorderDash(TableCell.BorderEdge edge) {
        HSLFLine l;
        switch (edge) {
            case bottom: {
                l = this.borderBottom;
                break;
            }
            case top: {
                l = this.borderTop;
                break;
            }
            case right: {
                l = this.borderRight;
                break;
            }
            case left: {
                l = this.borderLeft;
                break;
            }
            default: {
                throw new IllegalArgumentException();
            }
        }
        return l == null ? null : l.getLineDash();
    }

    @Override
    public void setBorderDash(TableCell.BorderEdge edge, StrokeStyle.LineDash dash) {
        if (edge == null || dash == null) {
            throw new IllegalArgumentException("BorderEdge and/or LineDash need to be specified.");
        }
        HSLFLine l = this.addLine(edge);
        l.setLineDash(dash);
    }

    public StrokeStyle.LineCompound getBorderCompound(TableCell.BorderEdge edge) {
        HSLFLine l;
        switch (edge) {
            case bottom: {
                l = this.borderBottom;
                break;
            }
            case top: {
                l = this.borderTop;
                break;
            }
            case right: {
                l = this.borderRight;
                break;
            }
            case left: {
                l = this.borderLeft;
                break;
            }
            default: {
                throw new IllegalArgumentException();
            }
        }
        return l == null ? null : l.getLineCompound();
    }

    @Override
    public void setBorderCompound(TableCell.BorderEdge edge, StrokeStyle.LineCompound compound) {
        if (edge == null || compound == null) {
            throw new IllegalArgumentException("BorderEdge and/or LineCompound need to be specified.");
        }
        HSLFLine l = this.addLine(edge);
        l.setLineCompound(compound);
    }

    protected HSLFLine addLine(TableCell.BorderEdge edge) {
        switch (edge) {
            case bottom: {
                if (this.borderBottom == null) {
                    this.borderBottom = this.createBorder(edge);
                    HSLFTableCell c = this.getSiblingCell(1, 0);
                    if (c != null) {
                        assert (c.borderTop == null);
                        c.borderTop = this.borderBottom;
                    }
                }
                return this.borderBottom;
            }
            case top: {
                if (this.borderTop == null) {
                    this.borderTop = this.createBorder(edge);
                    HSLFTableCell c = this.getSiblingCell(-1, 0);
                    if (c != null) {
                        assert (c.borderBottom == null);
                        c.borderBottom = this.borderTop;
                    }
                }
                return this.borderTop;
            }
            case right: {
                if (this.borderRight == null) {
                    this.borderRight = this.createBorder(edge);
                    HSLFTableCell c = this.getSiblingCell(0, 1);
                    if (c != null) {
                        assert (c.borderLeft == null);
                        c.borderLeft = this.borderRight;
                    }
                }
                return this.borderRight;
            }
            case left: {
                if (this.borderLeft == null) {
                    this.borderLeft = this.createBorder(edge);
                    HSLFTableCell c = this.getSiblingCell(0, -1);
                    if (c != null) {
                        assert (c.borderRight == null);
                        c.borderRight = this.borderLeft;
                    }
                }
                return this.borderLeft;
            }
        }
        throw new IllegalArgumentException();
    }

    @Override
    public void removeBorder(TableCell.BorderEdge edge) {
        switch (edge) {
            case bottom: {
                if (this.borderBottom == null) break;
                this.getParent().removeShape(this.borderBottom);
                this.borderBottom = null;
                HSLFTableCell c = this.getSiblingCell(1, 0);
                if (c == null) break;
                c.borderTop = null;
                break;
            }
            case top: {
                if (this.borderTop == null) break;
                this.getParent().removeShape(this.borderTop);
                this.borderTop = null;
                HSLFTableCell c = this.getSiblingCell(-1, 0);
                if (c == null) break;
                c.borderBottom = null;
                break;
            }
            case right: {
                if (this.borderRight == null) break;
                this.getParent().removeShape(this.borderRight);
                this.borderRight = null;
                HSLFTableCell c = this.getSiblingCell(0, 1);
                if (c == null) break;
                c.borderLeft = null;
                break;
            }
            case left: {
                if (this.borderLeft == null) break;
                this.getParent().removeShape(this.borderLeft);
                this.borderLeft = null;
                HSLFTableCell c = this.getSiblingCell(0, -1);
                if (c == null) break;
                c.borderRight = null;
                break;
            }
            default: {
                throw new IllegalArgumentException();
            }
        }
    }

    protected HSLFTableCell getSiblingCell(int row, int col) {
        return this.getParent().getRelativeCell(this, row, col);
    }

    private HSLFLine createBorder(TableCell.BorderEdge edge) {
        HSLFTable table = this.getParent();
        HSLFLine line = new HSLFLine(table);
        table.addShape(line);
        AbstractEscherOptRecord opt = this.getEscherOptRecord();
        HSLFTableCell.setEscherProperty(opt, EscherPropertyTypes.GEOMETRY__SHAPEPATH, -1);
        HSLFTableCell.setEscherProperty(opt, EscherPropertyTypes.GEOMETRY__FILLOK, -1);
        HSLFTableCell.setEscherProperty(opt, EscherPropertyTypes.SHADOWSTYLE__SHADOWOBSURED, 131072);
        HSLFTableCell.setEscherProperty(opt, EscherPropertyTypes.THREED__LIGHTFACE, 524288);
        this.anchorBorder(edge, line);
        return line;
    }

    protected void applyLineProperties(TableCell.BorderEdge edge, HSLFLine other) {
        HSLFLine line = this.addLine(edge);
        line.setLineWidth(other.getLineWidth());
        line.setLineColor(other.getLineColor());
    }

    public HSLFTable getParent() {
        return (HSLFTable)super.getParent();
    }

    protected void setGridSpan(int gridSpan) {
        this.gridSpan = gridSpan;
    }

    protected void setRowSpan(int rowSpan) {
        this.rowSpan = rowSpan;
    }

    @Override
    public int getGridSpan() {
        return this.gridSpan;
    }

    @Override
    public int getRowSpan() {
        return this.rowSpan;
    }

    @Override
    public boolean isMerged() {
        return false;
    }
}


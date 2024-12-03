/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.sl.draw.geom;

import java.awt.geom.Path2D;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import org.apache.poi.sl.draw.geom.Context;
import org.apache.poi.sl.draw.geom.PathCommand;
import org.apache.poi.sl.draw.geom.PathIf;
import org.apache.poi.sl.usermodel.PaintStyle;

public final class Path
implements PathIf {
    private final List<PathCommand> commands = new ArrayList<PathCommand>();
    private PaintStyle.PaintModifier fill = PaintStyle.PaintModifier.NORM;
    private boolean stroke = true;
    private boolean extrusionOk = false;
    private long w = -1L;
    private long h = -1L;

    @Override
    public void addCommand(PathCommand cmd) {
        this.commands.add(cmd);
    }

    @Override
    public Path2D.Double getPath(Context ctx) {
        Path2D.Double path = new Path2D.Double();
        for (PathCommand cmd : this.commands) {
            cmd.execute(path, ctx);
        }
        return path;
    }

    @Override
    public boolean isStroked() {
        return this.stroke;
    }

    @Override
    public void setStroke(boolean stroke) {
        this.stroke = stroke;
    }

    @Override
    public boolean isFilled() {
        return this.fill != PaintStyle.PaintModifier.NONE;
    }

    @Override
    public PaintStyle.PaintModifier getFill() {
        return this.fill;
    }

    @Override
    public void setFill(PaintStyle.PaintModifier fill) {
        this.fill = fill;
    }

    @Override
    public long getW() {
        return this.w;
    }

    @Override
    public void setW(long w) {
        this.w = w;
    }

    @Override
    public long getH() {
        return this.h;
    }

    @Override
    public void setH(long h) {
        this.h = h;
    }

    @Override
    public boolean isExtrusionOk() {
        return this.extrusionOk;
    }

    @Override
    public void setExtrusionOk(boolean extrusionOk) {
        this.extrusionOk = extrusionOk;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Path)) {
            return false;
        }
        Path ctPath2D = (Path)o;
        return Objects.equals(this.commands, ctPath2D.commands) && Objects.equals(this.w, ctPath2D.w) && Objects.equals(this.h, ctPath2D.h) && this.fill == ctPath2D.fill && Objects.equals(this.stroke, ctPath2D.stroke) && Objects.equals(this.extrusionOk, ctPath2D.extrusionOk);
    }

    public int hashCode() {
        return Objects.hash(this.commands, this.w, this.h, this.fill.ordinal(), this.stroke, this.extrusionOk);
    }
}


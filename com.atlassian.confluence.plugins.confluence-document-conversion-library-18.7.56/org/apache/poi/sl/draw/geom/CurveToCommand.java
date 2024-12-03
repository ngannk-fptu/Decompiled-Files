/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.sl.draw.geom;

import java.util.Objects;
import org.apache.poi.sl.draw.geom.AdjustPoint;
import org.apache.poi.sl.draw.geom.AdjustPointIf;
import org.apache.poi.sl.draw.geom.CurveToCommandIf;

public final class CurveToCommand
implements CurveToCommandIf {
    private final AdjustPoint pt1 = new AdjustPoint();
    private final AdjustPoint pt2 = new AdjustPoint();
    private final AdjustPoint pt3 = new AdjustPoint();

    @Override
    public AdjustPoint getPt1() {
        return this.pt1;
    }

    @Override
    public void setPt1(AdjustPointIf pt1) {
        if (pt1 != null) {
            this.pt1.setX(pt1.getX());
            this.pt1.setY(pt1.getY());
        }
    }

    @Override
    public AdjustPoint getPt2() {
        return this.pt2;
    }

    @Override
    public void setPt2(AdjustPointIf pt2) {
        if (pt2 != null) {
            this.pt2.setX(pt2.getX());
            this.pt2.setY(pt2.getY());
        }
    }

    @Override
    public AdjustPoint getPt3() {
        return this.pt3;
    }

    @Override
    public void setPt3(AdjustPointIf pt3) {
        if (pt3 != null) {
            this.pt3.setX(pt3.getX());
            this.pt3.setY(pt3.getY());
        }
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof CurveToCommand)) {
            return false;
        }
        CurveToCommand that = (CurveToCommand)o;
        return Objects.equals(this.pt1, that.pt1) && Objects.equals(this.pt2, that.pt2) && Objects.equals(this.pt3, that.pt3);
    }

    public int hashCode() {
        return Objects.hash(this.pt1, this.pt2, this.pt3);
    }
}


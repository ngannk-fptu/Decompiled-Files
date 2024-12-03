/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.sl.draw.geom;

import java.util.Objects;
import org.apache.poi.sl.draw.geom.AdjustPointIf;

public class AdjustPoint
implements AdjustPointIf {
    private String x;
    private String y;

    @Override
    public String getX() {
        return this.x;
    }

    @Override
    public void setX(String value) {
        this.x = value;
    }

    @Override
    public boolean isSetX() {
        return this.x != null;
    }

    @Override
    public String getY() {
        return this.y;
    }

    @Override
    public void setY(String value) {
        this.y = value;
    }

    @Override
    public boolean isSetY() {
        return this.y != null;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof AdjustPoint)) {
            return false;
        }
        AdjustPoint that = (AdjustPoint)o;
        return Objects.equals(this.x, that.x) && Objects.equals(this.y, that.y);
    }

    public int hashCode() {
        return Objects.hash(this.x, this.y);
    }
}


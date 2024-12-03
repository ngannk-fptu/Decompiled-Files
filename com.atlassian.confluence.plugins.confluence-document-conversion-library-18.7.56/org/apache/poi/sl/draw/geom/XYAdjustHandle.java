/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.sl.draw.geom;

import java.util.Objects;
import org.apache.poi.sl.draw.geom.AdjustHandle;
import org.apache.poi.sl.draw.geom.AdjustPoint;

public final class XYAdjustHandle
implements AdjustHandle {
    private AdjustPoint pos;
    private String gdRefX;
    private String minX;
    private String maxX;
    private String gdRefY;
    private String minY;
    private String maxY;

    public AdjustPoint getPos() {
        return this.pos;
    }

    public void setPos(AdjustPoint value) {
        this.pos = value;
    }

    public boolean isSetPos() {
        return this.pos != null;
    }

    public String getGdRefX() {
        return this.gdRefX;
    }

    public void setGdRefX(String value) {
        this.gdRefX = value;
    }

    public boolean isSetGdRefX() {
        return this.gdRefX != null;
    }

    public String getMinX() {
        return this.minX;
    }

    public void setMinX(String value) {
        this.minX = value;
    }

    public boolean isSetMinX() {
        return this.minX != null;
    }

    public String getMaxX() {
        return this.maxX;
    }

    public void setMaxX(String value) {
        this.maxX = value;
    }

    public boolean isSetMaxX() {
        return this.maxX != null;
    }

    public String getGdRefY() {
        return this.gdRefY;
    }

    public void setGdRefY(String value) {
        this.gdRefY = value;
    }

    public boolean isSetGdRefY() {
        return this.gdRefY != null;
    }

    public String getMinY() {
        return this.minY;
    }

    public void setMinY(String value) {
        this.minY = value;
    }

    public boolean isSetMinY() {
        return this.minY != null;
    }

    public String getMaxY() {
        return this.maxY;
    }

    public void setMaxY(String value) {
        this.maxY = value;
    }

    public boolean isSetMaxY() {
        return this.maxY != null;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof XYAdjustHandle)) {
            return false;
        }
        XYAdjustHandle that = (XYAdjustHandle)o;
        return Objects.equals(this.pos, that.pos) && Objects.equals(this.gdRefX, that.gdRefX) && Objects.equals(this.minX, that.minX) && Objects.equals(this.maxX, that.maxX) && Objects.equals(this.gdRefY, that.gdRefY) && Objects.equals(this.minY, that.minY) && Objects.equals(this.maxY, that.maxY);
    }

    public int hashCode() {
        return Objects.hash(this.pos, this.gdRefX, this.minX, this.maxX, this.gdRefY, this.minY, this.maxY);
    }
}


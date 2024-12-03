/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.sl.draw.geom;

import java.util.Objects;
import org.apache.poi.sl.draw.geom.AdjustHandle;
import org.apache.poi.sl.draw.geom.AdjustPoint;

public final class PolarAdjustHandle
implements AdjustHandle {
    private AdjustPoint pos;
    private String gdRefR;
    private String minR;
    private String maxR;
    private String gdRefAng;
    private String minAng;
    private String maxAng;

    public AdjustPoint getPos() {
        return this.pos;
    }

    public void setPos(AdjustPoint value) {
        this.pos = value;
    }

    public boolean isSetPos() {
        return this.pos != null;
    }

    public String getGdRefR() {
        return this.gdRefR;
    }

    public void setGdRefR(String value) {
        this.gdRefR = value;
    }

    public boolean isSetGdRefR() {
        return this.gdRefR != null;
    }

    public String getMinR() {
        return this.minR;
    }

    public void setMinR(String value) {
        this.minR = value;
    }

    public boolean isSetMinR() {
        return this.minR != null;
    }

    public String getMaxR() {
        return this.maxR;
    }

    public void setMaxR(String value) {
        this.maxR = value;
    }

    public boolean isSetMaxR() {
        return this.maxR != null;
    }

    public String getGdRefAng() {
        return this.gdRefAng;
    }

    public void setGdRefAng(String value) {
        this.gdRefAng = value;
    }

    public boolean isSetGdRefAng() {
        return this.gdRefAng != null;
    }

    public String getMinAng() {
        return this.minAng;
    }

    public void setMinAng(String value) {
        this.minAng = value;
    }

    public boolean isSetMinAng() {
        return this.minAng != null;
    }

    public String getMaxAng() {
        return this.maxAng;
    }

    public void setMaxAng(String value) {
        this.maxAng = value;
    }

    public boolean isSetMaxAng() {
        return this.maxAng != null;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof PolarAdjustHandle)) {
            return false;
        }
        PolarAdjustHandle that = (PolarAdjustHandle)o;
        return Objects.equals(this.pos, that.pos) && Objects.equals(this.gdRefR, that.gdRefR) && Objects.equals(this.minR, that.minR) && Objects.equals(this.maxR, that.maxR) && Objects.equals(this.gdRefAng, that.gdRefAng) && Objects.equals(this.minAng, that.minAng) && Objects.equals(this.maxAng, that.maxAng);
    }

    public int hashCode() {
        return Objects.hash(this.pos, this.gdRefR, this.minR, this.maxR, this.gdRefAng, this.minAng, this.maxAng);
    }
}


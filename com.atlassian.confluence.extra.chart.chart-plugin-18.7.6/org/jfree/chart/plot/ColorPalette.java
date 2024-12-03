/*
 * Decompiled with CFR 0.152.
 */
package org.jfree.chart.plot;

import java.awt.Color;
import java.awt.Paint;
import java.io.Serializable;
import java.util.Arrays;
import java.util.List;
import org.jfree.chart.axis.ValueTick;

public abstract class ColorPalette
implements Cloneable,
Serializable {
    private static final long serialVersionUID = -9029901853079622051L;
    protected double minZ = -1.0;
    protected double maxZ = -1.0;
    protected int[] r;
    protected int[] g;
    protected int[] b;
    protected double[] tickValues = null;
    protected boolean logscale = false;
    protected boolean inverse = false;
    protected String paletteName = null;
    protected boolean stepped = false;
    protected static final double log10 = Math.log(10.0);

    public Paint getColor(double value) {
        int izV = (int)(253.0 * (value - this.minZ) / (this.maxZ - this.minZ)) + 2;
        return new Color(this.r[izV], this.g[izV], this.b[izV]);
    }

    public Color getColor(int izV) {
        return new Color(this.r[izV], this.g[izV], this.b[izV]);
    }

    public Color getColorLinear(double value) {
        int izV = 0;
        if (this.stepped) {
            int index = Arrays.binarySearch(this.tickValues, value);
            if (index < 0) {
                index = -1 * index - 2;
            }
            value = index < 0 ? this.minZ : this.tickValues[index];
        }
        izV = (int)(253.0 * (value - this.minZ) / (this.maxZ - this.minZ)) + 2;
        izV = Math.min(izV, 255);
        izV = Math.max(izV, 2);
        return this.getColor(izV);
    }

    public Color getColorLog(double value) {
        int izV = 0;
        double minZtmp = this.minZ;
        double maxZtmp = this.maxZ;
        if (this.minZ <= 0.0) {
            this.maxZ = maxZtmp - minZtmp + 1.0;
            this.minZ = 1.0;
            value = value - minZtmp + 1.0;
        }
        double minZlog = Math.log(this.minZ) / log10;
        double maxZlog = Math.log(this.maxZ) / log10;
        value = Math.log(value) / log10;
        if (this.stepped) {
            int numSteps = this.tickValues.length;
            int steps = 256 / (numSteps - 1);
            izV = steps * (int)((double)numSteps * (value - minZlog) / (maxZlog - minZlog)) + 2;
        } else {
            izV = (int)(253.0 * (value - minZlog) / (maxZlog - minZlog)) + 2;
        }
        izV = Math.min(izV, 255);
        izV = Math.max(izV, 2);
        this.minZ = minZtmp;
        this.maxZ = maxZtmp;
        return this.getColor(izV);
    }

    public double getMaxZ() {
        return this.maxZ;
    }

    public double getMinZ() {
        return this.minZ;
    }

    public Paint getPaint(double value) {
        if (this.isLogscale()) {
            return this.getColorLog(value);
        }
        return this.getColorLinear(value);
    }

    public String getPaletteName() {
        return this.paletteName;
    }

    public double[] getTickValues() {
        return this.tickValues;
    }

    public abstract void initialize();

    public void invertPalette() {
        int i;
        int[] red = new int[256];
        int[] green = new int[256];
        int[] blue = new int[256];
        for (i = 0; i < 256; ++i) {
            red[i] = this.r[i];
            green[i] = this.g[i];
            blue[i] = this.b[i];
        }
        for (i = 2; i < 256; ++i) {
            this.r[i] = red[257 - i];
            this.g[i] = green[257 - i];
            this.b[i] = blue[257 - i];
        }
    }

    public boolean isInverse() {
        return this.inverse;
    }

    public boolean isLogscale() {
        return this.logscale;
    }

    public boolean isStepped() {
        return this.stepped;
    }

    public void setInverse(boolean inverse) {
        this.inverse = inverse;
        this.initialize();
        if (inverse) {
            this.invertPalette();
        }
    }

    public void setLogscale(boolean logscale) {
        this.logscale = logscale;
    }

    public void setMaxZ(double newMaxZ) {
        this.maxZ = newMaxZ;
    }

    public void setMinZ(double newMinZ) {
        this.minZ = newMinZ;
    }

    public void setPaletteName(String paletteName) {
        this.paletteName = paletteName;
    }

    public void setStepped(boolean stepped) {
        this.stepped = stepped;
    }

    public void setTickValues(double[] newTickValues) {
        this.tickValues = newTickValues;
    }

    public void setTickValues(List ticks) {
        this.tickValues = new double[ticks.size()];
        for (int i = 0; i < this.tickValues.length; ++i) {
            this.tickValues[i] = ((ValueTick)ticks.get(i)).getValue();
        }
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ColorPalette)) {
            return false;
        }
        ColorPalette colorPalette = (ColorPalette)o;
        if (this.inverse != colorPalette.inverse) {
            return false;
        }
        if (this.logscale != colorPalette.logscale) {
            return false;
        }
        if (this.maxZ != colorPalette.maxZ) {
            return false;
        }
        if (this.minZ != colorPalette.minZ) {
            return false;
        }
        if (this.stepped != colorPalette.stepped) {
            return false;
        }
        if (!Arrays.equals(this.b, colorPalette.b)) {
            return false;
        }
        if (!Arrays.equals(this.g, colorPalette.g)) {
            return false;
        }
        if (this.paletteName != null ? !this.paletteName.equals(colorPalette.paletteName) : colorPalette.paletteName != null) {
            return false;
        }
        if (!Arrays.equals(this.r, colorPalette.r)) {
            return false;
        }
        return Arrays.equals(this.tickValues, colorPalette.tickValues);
    }

    public int hashCode() {
        long temp = Double.doubleToLongBits(this.minZ);
        int result = (int)(temp ^ temp >>> 32);
        temp = Double.doubleToLongBits(this.maxZ);
        result = 29 * result + (int)(temp ^ temp >>> 32);
        result = 29 * result + (this.logscale ? 1 : 0);
        result = 29 * result + (this.inverse ? 1 : 0);
        result = 29 * result + (this.paletteName != null ? this.paletteName.hashCode() : 0);
        result = 29 * result + (this.stepped ? 1 : 0);
        return result;
    }

    public Object clone() throws CloneNotSupportedException {
        ColorPalette clone = (ColorPalette)super.clone();
        return clone;
    }
}


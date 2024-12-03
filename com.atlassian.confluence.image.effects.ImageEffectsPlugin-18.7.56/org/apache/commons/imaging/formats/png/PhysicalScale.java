/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.imaging.formats.png;

public class PhysicalScale {
    private static final int METER_UNITS = 1;
    private static final int RADIAN_UNITS = 2;
    public static final PhysicalScale UNDEFINED = PhysicalScale.createFromMeters(-1.0, -1.0);
    private final int units;
    private final double horizontalUnitsPerPixel;
    private final double verticalUnitsPerPixel;

    private PhysicalScale(int units, double horizontalUnitsPerPixel, double verticalUnitsPerPixel) {
        this.units = units;
        this.horizontalUnitsPerPixel = horizontalUnitsPerPixel;
        this.verticalUnitsPerPixel = verticalUnitsPerPixel;
    }

    public static PhysicalScale createFromMeters(double x, double y) {
        return new PhysicalScale(1, x, y);
    }

    public static PhysicalScale createFromRadians(double x, double y) {
        return new PhysicalScale(2, x, y);
    }

    public boolean isInMeters() {
        return 1 == this.units;
    }

    public boolean isInRadians() {
        return 2 == this.units;
    }

    public double getHorizontalUnitsPerPixel() {
        return this.horizontalUnitsPerPixel;
    }

    public double getVerticalUnitsPerPixel() {
        return this.verticalUnitsPerPixel;
    }
}


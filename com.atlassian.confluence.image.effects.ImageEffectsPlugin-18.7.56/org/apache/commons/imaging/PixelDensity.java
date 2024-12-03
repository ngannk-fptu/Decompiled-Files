/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.imaging;

public final class PixelDensity {
    private static final int PIXEL_NO_UNIT = 0;
    private static final int PIXEL_PER_INCH = 254;
    private static final int PIXEL_PER_METRE = 10000;
    private static final int PIXEL_PER_CENTIMETRE = 100;
    private final double horizontalDensity;
    private final double verticalDensity;
    private final int unitLength;

    private PixelDensity(double horizontalDensity, double verticalDensity, int unitLength) {
        this.horizontalDensity = horizontalDensity;
        this.verticalDensity = verticalDensity;
        this.unitLength = unitLength;
    }

    public static PixelDensity createUnitless(double x, double y) {
        return new PixelDensity(x, y, 0);
    }

    public static PixelDensity createFromPixelsPerInch(double x, double y) {
        return new PixelDensity(x, y, 254);
    }

    public static PixelDensity createFromPixelsPerMetre(double x, double y) {
        return new PixelDensity(x, y, 10000);
    }

    public static PixelDensity createFromPixelsPerCentimetre(double x, double y) {
        return new PixelDensity(x, y, 100);
    }

    public boolean isUnitless() {
        return this.unitLength == 0;
    }

    public boolean isInInches() {
        return this.unitLength == 254;
    }

    public boolean isInCentimetres() {
        return this.unitLength == 100;
    }

    public boolean isInMetres() {
        return this.unitLength == 10000;
    }

    public double getRawHorizontalDensity() {
        return this.horizontalDensity;
    }

    public double getRawVerticalDensity() {
        return this.verticalDensity;
    }

    public double horizontalDensityInches() {
        if (this.isInInches()) {
            return this.horizontalDensity;
        }
        return this.horizontalDensity * 254.0 / (double)this.unitLength;
    }

    public double verticalDensityInches() {
        if (this.isInInches()) {
            return this.verticalDensity;
        }
        return this.verticalDensity * 254.0 / (double)this.unitLength;
    }

    public double horizontalDensityMetres() {
        if (this.isInMetres()) {
            return this.horizontalDensity;
        }
        return this.horizontalDensity * 10000.0 / (double)this.unitLength;
    }

    public double verticalDensityMetres() {
        if (this.isInMetres()) {
            return this.verticalDensity;
        }
        return this.verticalDensity * 10000.0 / (double)this.unitLength;
    }

    public double horizontalDensityCentimetres() {
        if (this.isInCentimetres()) {
            return this.horizontalDensity;
        }
        return this.horizontalDensity * 100.0 / (double)this.unitLength;
    }

    public double verticalDensityCentimetres() {
        if (this.isInCentimetres()) {
            return this.verticalDensity;
        }
        return this.verticalDensity * 100.0 / (double)this.unitLength;
    }
}


/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xmlgraphics.util;

import java.awt.geom.AffineTransform;
import java.util.Locale;

public final class UnitConv {
    public static final float IN2MM = 25.4f;
    public static final float IN2CM = 2.54f;
    public static final int IN2PT = 72;
    public static final String PICA = "pc";
    public static final String POINT = "pt";
    public static final String MM = "mm";
    public static final String CM = "cm";
    public static final String INCH = "in";
    public static final String MPT = "mpt";
    public static final String PX = "px";

    private UnitConv() {
    }

    public static double mm2pt(double mm) {
        return mm * 72.0 / (double)25.4f;
    }

    public static double mm2mpt(double mm) {
        return mm * 1000.0 * 72.0 / (double)25.4f;
    }

    public static double pt2mm(double pt) {
        return pt * (double)25.4f / 72.0;
    }

    public static double mm2in(double mm) {
        return mm / (double)25.4f;
    }

    public static double in2mm(double in) {
        return in * (double)25.4f;
    }

    public static double in2mpt(double in) {
        return in * 72.0 * 1000.0;
    }

    public static double in2pt(double in) {
        return in * 72.0;
    }

    public static double mpt2in(double mpt) {
        return mpt / 72.0 / 1000.0;
    }

    public static double mm2px(double mm, int resolution) {
        return UnitConv.mm2in(mm) * (double)resolution;
    }

    public static double mpt2px(double mpt, int resolution) {
        return UnitConv.mpt2in(mpt) * (double)resolution;
    }

    public static AffineTransform mptToPt(AffineTransform at) {
        double[] matrix = new double[6];
        at.getMatrix(matrix);
        matrix[4] = matrix[4] / 1000.0;
        matrix[5] = matrix[5] / 1000.0;
        return new AffineTransform(matrix);
    }

    public static AffineTransform ptToMpt(AffineTransform at) {
        double[] matrix = new double[6];
        at.getMatrix(matrix);
        matrix[4] = matrix[4] * 1000.0;
        matrix[5] = matrix[5] * 1000.0;
        return new AffineTransform(matrix);
    }

    public static int convert(String value) {
        double retValue = 0.0;
        if (value != null) {
            if (value.toLowerCase(Locale.getDefault()).indexOf(PX) >= 0) {
                retValue = Double.parseDouble(value.substring(0, value.length() - 2));
                retValue *= 1000.0;
            } else if (value.toLowerCase(Locale.getDefault()).indexOf(INCH) >= 0) {
                retValue = Double.parseDouble(value.substring(0, value.length() - 2));
                retValue *= 72000.0;
            } else if (value.toLowerCase(Locale.getDefault()).indexOf(CM) >= 0) {
                retValue = Double.parseDouble(value.substring(0, value.length() - 2));
                retValue *= 28346.4567;
            } else if (value.toLowerCase(Locale.getDefault()).indexOf(MM) >= 0) {
                retValue = Double.parseDouble(value.substring(0, value.length() - 2));
                retValue *= 2834.64567;
            } else if (value.toLowerCase(Locale.getDefault()).indexOf(MPT) >= 0) {
                retValue = Double.parseDouble(value.substring(0, value.length() - 3));
            } else if (value.toLowerCase(Locale.getDefault()).indexOf(POINT) >= 0) {
                retValue = Double.parseDouble(value.substring(0, value.length() - 2));
                retValue *= 1000.0;
            } else if (value.toLowerCase(Locale.getDefault()).indexOf(PICA) >= 0) {
                retValue = Double.parseDouble(value.substring(0, value.length() - 2));
                retValue *= 12000.0;
            }
        }
        return (int)retValue;
    }
}


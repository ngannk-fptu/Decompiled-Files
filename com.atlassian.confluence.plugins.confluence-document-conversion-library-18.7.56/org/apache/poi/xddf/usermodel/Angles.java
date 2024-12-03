/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.xddf.usermodel;

public class Angles {
    public static final int OOXML_DEGREE = 60000;

    public static final int degreesToAttribute(double angle) {
        return Math.toIntExact(Math.round(60000.0 * angle));
    }

    public static final double attributeToDegrees(int angle) {
        return (double)angle / 60000.0;
    }
}


/*
 * Decompiled with CFR 0.152.
 */
package org.xhtmlrenderer.css.parser;

import org.xhtmlrenderer.css.parser.FSColor;

public class FSRGBColor
implements FSColor {
    public static final FSRGBColor TRANSPARENT = new FSRGBColor(0, 0, 0);
    public static final FSRGBColor RED = new FSRGBColor(255, 0, 0);
    public static final FSRGBColor GREEN = new FSRGBColor(0, 255, 0);
    public static final FSRGBColor BLUE = new FSRGBColor(0, 0, 255);
    private int _red;
    private int _green;
    private int _blue;

    public FSRGBColor(int red, int green, int blue) {
        if (red < 0 || red > 255) {
            throw new IllegalArgumentException();
        }
        if (green < 0 || green > 255) {
            throw new IllegalArgumentException();
        }
        if (blue < 0 || blue > 255) {
            throw new IllegalArgumentException();
        }
        this._red = red;
        this._green = green;
        this._blue = blue;
    }

    public FSRGBColor(int color) {
        this((color & 0xFF0000) >> 16, (color & 0xFF00) >> 8, color & 0xFF);
    }

    public int getBlue() {
        return this._blue;
    }

    public int getGreen() {
        return this._green;
    }

    public int getRed() {
        return this._red;
    }

    public String toString() {
        return '#' + this.toString(this._red) + this.toString(this._green) + this.toString(this._blue);
    }

    private String toString(int color) {
        String result = Integer.toHexString(color);
        if (result.length() == 1) {
            return "0" + result;
        }
        return result;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof FSRGBColor)) {
            return false;
        }
        FSRGBColor that = (FSRGBColor)o;
        if (this._blue != that._blue) {
            return false;
        }
        if (this._green != that._green) {
            return false;
        }
        return this._red == that._red;
    }

    public int hashCode() {
        int result = this._red;
        result = 31 * result + this._green;
        result = 31 * result + this._blue;
        return result;
    }

    @Override
    public FSColor lightenColor() {
        float[] hsb = FSRGBColor.RGBtoHSB(this.getRed(), this.getGreen(), this.getBlue(), null);
        float hBase = hsb[0];
        float sBase = hsb[1];
        float bBase = hsb[2];
        float hLighter = hBase;
        float sLighter = 0.35f * bBase * sBase;
        float bLighter = 0.6999f + 0.3f * bBase;
        int[] rgb = FSRGBColor.HSBtoRGB(hLighter, sLighter, bLighter);
        return new FSRGBColor(rgb[0], rgb[1], rgb[2]);
    }

    @Override
    public FSColor darkenColor() {
        float[] hsb = FSRGBColor.RGBtoHSB(this.getRed(), this.getGreen(), this.getBlue(), null);
        float hBase = hsb[0];
        float sBase = hsb[1];
        float bBase = hsb[2];
        float hDarker = hBase;
        float sDarker = sBase;
        float bDarker = 0.56f * bBase;
        int[] rgb = FSRGBColor.HSBtoRGB(hDarker, sDarker, bDarker);
        return new FSRGBColor(rgb[0], rgb[1], rgb[2]);
    }

    private static float[] RGBtoHSB(int r, int g, int b, float[] hsbvals) {
        float hue;
        int cmin;
        int cmax;
        if (hsbvals == null) {
            hsbvals = new float[3];
        }
        int n = cmax = r > g ? r : g;
        if (b > cmax) {
            cmax = b;
        }
        int n2 = cmin = r < g ? r : g;
        if (b < cmin) {
            cmin = b;
        }
        float brightness = (float)cmax / 255.0f;
        float saturation = cmax != 0 ? (float)(cmax - cmin) / (float)cmax : 0.0f;
        if (saturation == 0.0f) {
            hue = 0.0f;
        } else {
            float redc = (float)(cmax - r) / (float)(cmax - cmin);
            float greenc = (float)(cmax - g) / (float)(cmax - cmin);
            float bluec = (float)(cmax - b) / (float)(cmax - cmin);
            hue = r == cmax ? bluec - greenc : (g == cmax ? 2.0f + redc - bluec : 4.0f + greenc - redc);
            if ((hue /= 6.0f) < 0.0f) {
                hue += 1.0f;
            }
        }
        hsbvals[0] = hue;
        hsbvals[1] = saturation;
        hsbvals[2] = brightness;
        return hsbvals;
    }

    private static int[] HSBtoRGB(float hue, float saturation, float brightness) {
        int r = 0;
        int g = 0;
        int b = 0;
        if (saturation == 0.0f) {
            g = b = (int)(brightness * 255.0f + 0.5f);
            r = b;
        } else {
            float h = (hue - (float)Math.floor(hue)) * 6.0f;
            float f = h - (float)Math.floor(h);
            float p = brightness * (1.0f - saturation);
            float q = brightness * (1.0f - saturation * f);
            float t = brightness * (1.0f - saturation * (1.0f - f));
            switch ((int)h) {
                case 0: {
                    r = (int)(brightness * 255.0f + 0.5f);
                    g = (int)(t * 255.0f + 0.5f);
                    b = (int)(p * 255.0f + 0.5f);
                    break;
                }
                case 1: {
                    r = (int)(q * 255.0f + 0.5f);
                    g = (int)(brightness * 255.0f + 0.5f);
                    b = (int)(p * 255.0f + 0.5f);
                    break;
                }
                case 2: {
                    r = (int)(p * 255.0f + 0.5f);
                    g = (int)(brightness * 255.0f + 0.5f);
                    b = (int)(t * 255.0f + 0.5f);
                    break;
                }
                case 3: {
                    r = (int)(p * 255.0f + 0.5f);
                    g = (int)(q * 255.0f + 0.5f);
                    b = (int)(brightness * 255.0f + 0.5f);
                    break;
                }
                case 4: {
                    r = (int)(t * 255.0f + 0.5f);
                    g = (int)(p * 255.0f + 0.5f);
                    b = (int)(brightness * 255.0f + 0.5f);
                    break;
                }
                case 5: {
                    r = (int)(brightness * 255.0f + 0.5f);
                    g = (int)(p * 255.0f + 0.5f);
                    b = (int)(q * 255.0f + 0.5f);
                }
            }
        }
        return new int[]{r, g, b};
    }
}


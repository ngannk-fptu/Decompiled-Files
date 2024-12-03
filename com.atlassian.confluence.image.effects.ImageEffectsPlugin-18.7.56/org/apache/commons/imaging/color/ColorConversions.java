/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.imaging.color;

import org.apache.commons.imaging.color.ColorCieLab;
import org.apache.commons.imaging.color.ColorCieLch;
import org.apache.commons.imaging.color.ColorCieLuv;
import org.apache.commons.imaging.color.ColorCmy;
import org.apache.commons.imaging.color.ColorCmyk;
import org.apache.commons.imaging.color.ColorHsl;
import org.apache.commons.imaging.color.ColorHsv;
import org.apache.commons.imaging.color.ColorHunterLab;
import org.apache.commons.imaging.color.ColorXyz;

public final class ColorConversions {
    private static final double REF_X = 95.047;
    private static final double REF_Y = 100.0;
    private static final double REF_Z = 108.883;

    private ColorConversions() {
    }

    public static ColorCieLab convertXYZtoCIELab(ColorXyz xyz) {
        return ColorConversions.convertXYZtoCIELab(xyz.X, xyz.Y, xyz.Z);
    }

    public static ColorCieLab convertXYZtoCIELab(double X, double Y, double Z) {
        double var_X = X / 95.047;
        double var_Y = Y / 100.0;
        double var_Z = Z / 108.883;
        var_X = var_X > 0.008856 ? Math.pow(var_X, 0.3333333333333333) : 7.787 * var_X + 0.13793103448275862;
        var_Y = var_Y > 0.008856 ? Math.pow(var_Y, 0.3333333333333333) : 7.787 * var_Y + 0.13793103448275862;
        var_Z = var_Z > 0.008856 ? Math.pow(var_Z, 0.3333333333333333) : 7.787 * var_Z + 0.13793103448275862;
        double L = 116.0 * var_Y - 16.0;
        double a = 500.0 * (var_X - var_Y);
        double b = 200.0 * (var_Y - var_Z);
        return new ColorCieLab(L, a, b);
    }

    public static ColorXyz convertCIELabtoXYZ(ColorCieLab cielab) {
        return ColorConversions.convertCIELabtoXYZ(cielab.L, cielab.a, cielab.b);
    }

    public static ColorXyz convertCIELabtoXYZ(double L, double a, double b) {
        double var_Y = (L + 16.0) / 116.0;
        double var_X = a / 500.0 + var_Y;
        double var_Z = var_Y - b / 200.0;
        var_Y = Math.pow(var_Y, 3.0) > 0.008856 ? Math.pow(var_Y, 3.0) : (var_Y - 0.13793103448275862) / 7.787;
        var_X = Math.pow(var_X, 3.0) > 0.008856 ? Math.pow(var_X, 3.0) : (var_X - 0.13793103448275862) / 7.787;
        var_Z = Math.pow(var_Z, 3.0) > 0.008856 ? Math.pow(var_Z, 3.0) : (var_Z - 0.13793103448275862) / 7.787;
        double X = 95.047 * var_X;
        double Y = 100.0 * var_Y;
        double Z = 108.883 * var_Z;
        return new ColorXyz(X, Y, Z);
    }

    public static ColorHunterLab convertXYZtoHunterLab(ColorXyz xyz) {
        return ColorConversions.convertXYZtoHunterLab(xyz.X, xyz.Y, xyz.Z);
    }

    public static ColorHunterLab convertXYZtoHunterLab(double X, double Y, double Z) {
        double L = 10.0 * Math.sqrt(Y);
        double a = 17.5 * ((1.02 * X - Y) / Math.sqrt(Y));
        double b = 7.0 * ((Y - 0.847 * Z) / Math.sqrt(Y));
        return new ColorHunterLab(L, a, b);
    }

    public static ColorXyz convertHunterLabtoXYZ(ColorHunterLab cielab) {
        return ColorConversions.convertHunterLabtoXYZ(cielab.L, cielab.a, cielab.b);
    }

    public static ColorXyz convertHunterLabtoXYZ(double L, double a, double b) {
        double var_Y = L / 10.0;
        double var_X = a / 17.5 * L / 10.0;
        double var_Z = b / 7.0 * L / 10.0;
        double Y = Math.pow(var_Y, 2.0);
        double X = (var_X + Y) / 1.02;
        double Z = -(var_Z - Y) / 0.847;
        return new ColorXyz(X, Y, Z);
    }

    public static int convertXYZtoRGB(ColorXyz xyz) {
        return ColorConversions.convertXYZtoRGB(xyz.X, xyz.Y, xyz.Z);
    }

    public static int convertXYZtoRGB(double X, double Y, double Z) {
        double var_X = X / 100.0;
        double var_Y = Y / 100.0;
        double var_Z = Z / 100.0;
        double var_R = var_X * 3.2406 + var_Y * -1.5372 + var_Z * -0.4986;
        double var_G = var_X * -0.9689 + var_Y * 1.8758 + var_Z * 0.0415;
        double var_B = var_X * 0.0557 + var_Y * -0.204 + var_Z * 1.057;
        var_R = var_R > 0.0031308 ? 1.055 * Math.pow(var_R, 0.4166666666666667) - 0.055 : 12.92 * var_R;
        var_G = var_G > 0.0031308 ? 1.055 * Math.pow(var_G, 0.4166666666666667) - 0.055 : 12.92 * var_G;
        var_B = var_B > 0.0031308 ? 1.055 * Math.pow(var_B, 0.4166666666666667) - 0.055 : 12.92 * var_B;
        double R = var_R * 255.0;
        double G = var_G * 255.0;
        double B = var_B * 255.0;
        return ColorConversions.convertRGBtoRGB(R, G, B);
    }

    public static ColorXyz convertRGBtoXYZ(int rgb) {
        int r = 0xFF & rgb >> 16;
        int g = 0xFF & rgb >> 8;
        int b = 0xFF & rgb >> 0;
        double var_R = (double)r / 255.0;
        double var_G = (double)g / 255.0;
        double var_B = (double)b / 255.0;
        var_R = var_R > 0.04045 ? Math.pow((var_R + 0.055) / 1.055, 2.4) : (var_R /= 12.92);
        var_G = var_G > 0.04045 ? Math.pow((var_G + 0.055) / 1.055, 2.4) : (var_G /= 12.92);
        var_B = var_B > 0.04045 ? Math.pow((var_B + 0.055) / 1.055, 2.4) : (var_B /= 12.92);
        double X = (var_R *= 100.0) * 0.4124 + (var_G *= 100.0) * 0.3576 + (var_B *= 100.0) * 0.1805;
        double Y = var_R * 0.2126 + var_G * 0.7152 + var_B * 0.0722;
        double Z = var_R * 0.0193 + var_G * 0.1192 + var_B * 0.9505;
        return new ColorXyz(X, Y, Z);
    }

    public static ColorCmy convertRGBtoCMY(int rgb) {
        int R = 0xFF & rgb >> 16;
        int G = 0xFF & rgb >> 8;
        int B = 0xFF & rgb >> 0;
        double C = 1.0 - (double)R / 255.0;
        double M = 1.0 - (double)G / 255.0;
        double Y = 1.0 - (double)B / 255.0;
        return new ColorCmy(C, M, Y);
    }

    public static int convertCMYtoRGB(ColorCmy cmy) {
        double R = (1.0 - cmy.C) * 255.0;
        double G = (1.0 - cmy.M) * 255.0;
        double B = (1.0 - cmy.Y) * 255.0;
        return ColorConversions.convertRGBtoRGB(R, G, B);
    }

    public static ColorCmyk convertCMYtoCMYK(ColorCmy cmy) {
        double C = cmy.C;
        double M = cmy.M;
        double Y = cmy.Y;
        double var_K = 1.0;
        if (C < var_K) {
            var_K = C;
        }
        if (M < var_K) {
            var_K = M;
        }
        if (Y < var_K) {
            var_K = Y;
        }
        if (var_K == 1.0) {
            C = 0.0;
            M = 0.0;
            Y = 0.0;
        } else {
            C = (C - var_K) / (1.0 - var_K);
            M = (M - var_K) / (1.0 - var_K);
            Y = (Y - var_K) / (1.0 - var_K);
        }
        return new ColorCmyk(C, M, Y, var_K);
    }

    public static ColorCmy convertCMYKtoCMY(ColorCmyk cmyk) {
        return ColorConversions.convertCMYKtoCMY(cmyk.C, cmyk.M, cmyk.Y, cmyk.K);
    }

    public static ColorCmy convertCMYKtoCMY(double C, double M, double Y, double K) {
        C = C * (1.0 - K) + K;
        M = M * (1.0 - K) + K;
        Y = Y * (1.0 - K) + K;
        return new ColorCmy(C, M, Y);
    }

    public static int convertCMYKtoRGB(int c, int m, int y, int k) {
        double C = (double)c / 255.0;
        double M = (double)m / 255.0;
        double Y = (double)y / 255.0;
        double K = (double)k / 255.0;
        return ColorConversions.convertCMYtoRGB(ColorConversions.convertCMYKtoCMY(C, M, Y, K));
    }

    public static ColorHsl convertRGBtoHSL(int rgb) {
        double S;
        double H;
        double var_Max;
        int R = 0xFF & rgb >> 16;
        int G = 0xFF & rgb >> 8;
        int B = 0xFF & rgb >> 0;
        double var_R = (double)R / 255.0;
        double var_G = (double)G / 255.0;
        double var_B = (double)B / 255.0;
        double var_Min = Math.min(var_R, Math.min(var_G, var_B));
        boolean maxIsR = false;
        boolean maxIsG = false;
        if (var_R >= var_G && var_R >= var_B) {
            var_Max = var_R;
            maxIsR = true;
        } else if (var_G > var_B) {
            var_Max = var_G;
            maxIsG = true;
        } else {
            var_Max = var_B;
        }
        double del_Max = var_Max - var_Min;
        double L = (var_Max + var_Min) / 2.0;
        if (del_Max == 0.0) {
            H = 0.0;
            S = 0.0;
        } else {
            S = L < 0.5 ? del_Max / (var_Max + var_Min) : del_Max / (2.0 - var_Max - var_Min);
            double del_R = ((var_Max - var_R) / 6.0 + del_Max / 2.0) / del_Max;
            double del_G = ((var_Max - var_G) / 6.0 + del_Max / 2.0) / del_Max;
            double del_B = ((var_Max - var_B) / 6.0 + del_Max / 2.0) / del_Max;
            H = maxIsR ? del_B - del_G : (maxIsG ? 0.3333333333333333 + del_R - del_B : 0.6666666666666666 + del_G - del_R);
            if (H < 0.0) {
                H += 1.0;
            }
            if (H > 1.0) {
                H -= 1.0;
            }
        }
        return new ColorHsl(H, S, L);
    }

    public static int convertHSLtoRGB(ColorHsl hsl) {
        return ColorConversions.convertHSLtoRGB(hsl.H, hsl.S, hsl.L);
    }

    public static int convertHSLtoRGB(double H, double S, double L) {
        double B;
        double G;
        double R;
        if (S == 0.0) {
            R = L * 255.0;
            G = L * 255.0;
            B = L * 255.0;
        } else {
            double var_2 = L < 0.5 ? L * (1.0 + S) : L + S - S * L;
            double var_1 = 2.0 * L - var_2;
            R = 255.0 * ColorConversions.convertHuetoRGB(var_1, var_2, H + 0.3333333333333333);
            G = 255.0 * ColorConversions.convertHuetoRGB(var_1, var_2, H);
            B = 255.0 * ColorConversions.convertHuetoRGB(var_1, var_2, H - 0.3333333333333333);
        }
        return ColorConversions.convertRGBtoRGB(R, G, B);
    }

    private static double convertHuetoRGB(double v1, double v2, double vH) {
        if (vH < 0.0) {
            vH += 1.0;
        }
        if (vH > 1.0) {
            vH -= 1.0;
        }
        if (6.0 * vH < 1.0) {
            return v1 + (v2 - v1) * 6.0 * vH;
        }
        if (2.0 * vH < 1.0) {
            return v2;
        }
        if (3.0 * vH < 2.0) {
            return v1 + (v2 - v1) * (0.6666666666666666 - vH) * 6.0;
        }
        return v1;
    }

    public static ColorHsv convertRGBtoHSV(int rgb) {
        double S;
        double H;
        double var_Max;
        int R = 0xFF & rgb >> 16;
        int G = 0xFF & rgb >> 8;
        int B = 0xFF & rgb >> 0;
        double var_R = (double)R / 255.0;
        double var_G = (double)G / 255.0;
        double var_B = (double)B / 255.0;
        double var_Min = Math.min(var_R, Math.min(var_G, var_B));
        boolean maxIsR = false;
        boolean maxIsG = false;
        if (var_R >= var_G && var_R >= var_B) {
            var_Max = var_R;
            maxIsR = true;
        } else if (var_G > var_B) {
            var_Max = var_G;
            maxIsG = true;
        } else {
            var_Max = var_B;
        }
        double del_Max = var_Max - var_Min;
        double V = var_Max;
        if (del_Max == 0.0) {
            H = 0.0;
            S = 0.0;
        } else {
            S = del_Max / var_Max;
            double del_R = ((var_Max - var_R) / 6.0 + del_Max / 2.0) / del_Max;
            double del_G = ((var_Max - var_G) / 6.0 + del_Max / 2.0) / del_Max;
            double del_B = ((var_Max - var_B) / 6.0 + del_Max / 2.0) / del_Max;
            H = maxIsR ? del_B - del_G : (maxIsG ? 0.3333333333333333 + del_R - del_B : 0.6666666666666666 + del_G - del_R);
            if (H < 0.0) {
                H += 1.0;
            }
            if (H > 1.0) {
                H -= 1.0;
            }
        }
        return new ColorHsv(H, S, V);
    }

    public static int convertHSVtoRGB(ColorHsv HSV) {
        return ColorConversions.convertHSVtoRGB(HSV.H, HSV.S, HSV.V);
    }

    public static int convertHSVtoRGB(double H, double S, double V) {
        double B;
        double G;
        double R;
        if (S == 0.0) {
            R = V * 255.0;
            G = V * 255.0;
            B = V * 255.0;
        } else {
            double var_b;
            double var_g;
            double var_r;
            double var_h = H * 6.0;
            if (var_h == 6.0) {
                var_h = 0.0;
            }
            double var_i = Math.floor(var_h);
            double var_1 = V * (1.0 - S);
            double var_2 = V * (1.0 - S * (var_h - var_i));
            double var_3 = V * (1.0 - S * (1.0 - (var_h - var_i)));
            if (var_i == 0.0) {
                var_r = V;
                var_g = var_3;
                var_b = var_1;
            } else if (var_i == 1.0) {
                var_r = var_2;
                var_g = V;
                var_b = var_1;
            } else if (var_i == 2.0) {
                var_r = var_1;
                var_g = V;
                var_b = var_3;
            } else if (var_i == 3.0) {
                var_r = var_1;
                var_g = var_2;
                var_b = V;
            } else if (var_i == 4.0) {
                var_r = var_3;
                var_g = var_1;
                var_b = V;
            } else {
                var_r = V;
                var_g = var_1;
                var_b = var_2;
            }
            R = var_r * 255.0;
            G = var_g * 255.0;
            B = var_b * 255.0;
        }
        return ColorConversions.convertRGBtoRGB(R, G, B);
    }

    public static int convertCMYKtoRGB_Adobe(int sc, int sm, int sy, int sk) {
        int red = 255 - (sc + sk);
        int green = 255 - (sm + sk);
        int blue = 255 - (sy + sk);
        return ColorConversions.convertRGBtoRGB(red, green, blue);
    }

    private static double cube(double f) {
        return f * f * f;
    }

    private static double square(double f) {
        return f * f;
    }

    public static int convertCIELabtoARGBTest(int cieL, int cieA, int cieB) {
        double var_Y = ((double)cieL * 100.0 / 255.0 + 16.0) / 116.0;
        double var_X = (double)cieA / 500.0 + var_Y;
        double var_Z = var_Y - (double)cieB / 200.0;
        double var_x_cube = ColorConversions.cube(var_X);
        double var_y_cube = ColorConversions.cube(var_Y);
        double var_z_cube = ColorConversions.cube(var_Z);
        var_Y = var_y_cube > 0.008856 ? var_y_cube : (var_Y - 0.13793103448275862) / 7.787;
        var_X = var_x_cube > 0.008856 ? var_x_cube : (var_X - 0.13793103448275862) / 7.787;
        var_Z = var_z_cube > 0.008856 ? var_z_cube : (var_Z - 0.13793103448275862) / 7.787;
        double X = 95.047 * var_X;
        double Y = 100.0 * var_Y;
        double Z = 108.883 * var_Z;
        double var_X2 = X / 100.0;
        double var_Y2 = Y / 100.0;
        double var_Z2 = Z / 100.0;
        double var_R = var_X2 * 3.2406 + var_Y2 * -1.5372 + var_Z2 * -0.4986;
        double var_G = var_X2 * -0.9689 + var_Y2 * 1.8758 + var_Z2 * 0.0415;
        double var_B = var_X2 * 0.0557 + var_Y2 * -0.204 + var_Z2 * 1.057;
        var_R = var_R > 0.0031308 ? 1.055 * Math.pow(var_R, 0.4166666666666667) - 0.055 : 12.92 * var_R;
        var_G = var_G > 0.0031308 ? 1.055 * Math.pow(var_G, 0.4166666666666667) - 0.055 : 12.92 * var_G;
        var_B = var_B > 0.0031308 ? 1.055 * Math.pow(var_B, 0.4166666666666667) - 0.055 : 12.92 * var_B;
        double R = var_R * 255.0;
        double G = var_G * 255.0;
        double B = var_B * 255.0;
        return ColorConversions.convertRGBtoRGB(R, G, B);
    }

    private static int convertRGBtoRGB(double R, double G, double B) {
        int red = (int)Math.round(R);
        int green = (int)Math.round(G);
        int blue = (int)Math.round(B);
        red = Math.min(255, Math.max(0, red));
        green = Math.min(255, Math.max(0, green));
        blue = Math.min(255, Math.max(0, blue));
        int alpha = 255;
        int rgb = 0xFF000000 | red << 16 | green << 8 | blue << 0;
        return rgb;
    }

    private static int convertRGBtoRGB(int red, int green, int blue) {
        red = Math.min(255, Math.max(0, red));
        green = Math.min(255, Math.max(0, green));
        blue = Math.min(255, Math.max(0, blue));
        int alpha = 255;
        int rgb = 0xFF000000 | red << 16 | green << 8 | blue << 0;
        return rgb;
    }

    public static ColorCieLch convertCIELabtoCIELCH(ColorCieLab cielab) {
        return ColorConversions.convertCIELabtoCIELCH(cielab.L, cielab.a, cielab.b);
    }

    public static ColorCieLch convertCIELabtoCIELCH(double L, double a, double b) {
        double var_H = Math.atan2(b, a);
        var_H = var_H > 0.0 ? var_H / Math.PI * 180.0 : 360.0 - ColorConversions.radian_2_degree(Math.abs(var_H));
        double C = Math.sqrt(ColorConversions.square(a) + ColorConversions.square(b));
        double H = var_H;
        return new ColorCieLch(L, C, H);
    }

    public static ColorCieLab convertCIELCHtoCIELab(ColorCieLch cielch) {
        return ColorConversions.convertCIELCHtoCIELab(cielch.L, cielch.C, cielch.H);
    }

    public static ColorCieLab convertCIELCHtoCIELab(double L, double C, double H) {
        double a = Math.cos(ColorConversions.degree_2_radian(H)) * C;
        double b = Math.sin(ColorConversions.degree_2_radian(H)) * C;
        return new ColorCieLab(L, a, b);
    }

    public static double degree_2_radian(double degree) {
        return degree * Math.PI / 180.0;
    }

    public static double radian_2_degree(double radian) {
        return radian * 180.0 / Math.PI;
    }

    public static ColorCieLuv convertXYZtoCIELuv(ColorXyz xyz) {
        return ColorConversions.convertXYZtoCIELuv(xyz.X, xyz.Y, xyz.Z);
    }

    public static ColorCieLuv convertXYZtoCIELuv(double X, double Y, double Z) {
        double var_U = 4.0 * X / (X + 15.0 * Y + 3.0 * Z);
        double var_V = 9.0 * Y / (X + 15.0 * Y + 3.0 * Z);
        double var_Y = Y / 100.0;
        var_Y = var_Y > 0.008856 ? Math.pow(var_Y, 0.3333333333333333) : 7.787 * var_Y + 0.13793103448275862;
        double ref_U = 0.19783982482140777;
        double ref_V = 0.46833630293240974;
        double L = 116.0 * var_Y - 16.0;
        double u = 13.0 * L * (var_U - 0.19783982482140777);
        double v = 13.0 * L * (var_V - 0.46833630293240974);
        return new ColorCieLuv(L, u, v);
    }

    public static ColorXyz convertCIELuvtoXYZ(ColorCieLuv cielch) {
        return ColorConversions.convertCIELuvtoXYZ(cielch.L, cielch.u, cielch.v);
    }

    public static ColorXyz convertCIELuvtoXYZ(double L, double u, double v) {
        double var_Y = (L + 16.0) / 116.0;
        var_Y = Math.pow(var_Y, 3.0) > 0.008856 ? Math.pow(var_Y, 3.0) : (var_Y - 0.0) / 7.787;
        double ref_U = 0.19783982482140777;
        double ref_V = 0.46833630293240974;
        double var_U = u / (13.0 * L) + 0.19783982482140777;
        double var_V = v / (13.0 * L) + 0.46833630293240974;
        double Y = var_Y * 100.0;
        double X = -(9.0 * Y * var_U) / ((var_U - 4.0) * var_V - var_U * var_V);
        double Z = (9.0 * Y - 15.0 * var_V * Y - var_V * X) / (3.0 * var_V);
        return new ColorXyz(X, Y, Z);
    }
}


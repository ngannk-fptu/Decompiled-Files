/*
 * Decompiled with CFR 0.152.
 */
package com.twelvemonkeys.image;

import com.twelvemonkeys.image.AffineTransformOp;
import com.twelvemonkeys.image.ImageUtil;
import com.twelvemonkeys.image.MagickAccelerator;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;
import java.awt.image.ColorModel;
import java.awt.image.IndexColorModel;
import java.awt.image.WritableRaster;

public class ResampleOp
implements BufferedImageOp {
    public static final int FILTER_UNDEFINED = 0;
    public static final int FILTER_POINT = 1;
    public static final int FILTER_BOX = 2;
    public static final int FILTER_TRIANGLE = 3;
    public static final int FILTER_HERMITE = 4;
    public static final int FILTER_HANNING = 5;
    public static final int FILTER_HAMMING = 6;
    public static final int FILTER_BLACKMAN = 7;
    public static final int FILTER_GAUSSIAN = 8;
    public static final int FILTER_QUADRATIC = 9;
    public static final int FILTER_CUBIC = 10;
    public static final int FILTER_CATROM = 11;
    public static final int FILTER_MITCHELL = 12;
    public static final int FILTER_LANCZOS = 13;
    public static final int FILTER_BLACKMAN_BESSEL = 14;
    public static final int FILTER_BLACKMAN_SINC = 15;
    public static final RenderingHints.Key KEY_RESAMPLE_INTERPOLATION = new Key("ResampleInterpolation");
    public static final Object VALUE_INTERPOLATION_POINT = new Value(KEY_RESAMPLE_INTERPOLATION, "Point", 1);
    public static final Object VALUE_INTERPOLATION_BOX = new Value(KEY_RESAMPLE_INTERPOLATION, "Box", 2);
    public static final Object VALUE_INTERPOLATION_TRIANGLE = new Value(KEY_RESAMPLE_INTERPOLATION, "Triangle", 3);
    public static final Object VALUE_INTERPOLATION_HERMITE = new Value(KEY_RESAMPLE_INTERPOLATION, "Hermite", 4);
    public static final Object VALUE_INTERPOLATION_HANNING = new Value(KEY_RESAMPLE_INTERPOLATION, "Hanning", 5);
    public static final Object VALUE_INTERPOLATION_HAMMING = new Value(KEY_RESAMPLE_INTERPOLATION, "Hamming", 6);
    public static final Object VALUE_INTERPOLATION_BLACKMAN = new Value(KEY_RESAMPLE_INTERPOLATION, "Blackman", 7);
    public static final Object VALUE_INTERPOLATION_GAUSSIAN = new Value(KEY_RESAMPLE_INTERPOLATION, "Gaussian", 8);
    public static final Object VALUE_INTERPOLATION_QUADRATIC = new Value(KEY_RESAMPLE_INTERPOLATION, "Quadratic", 9);
    public static final Object VALUE_INTERPOLATION_CUBIC = new Value(KEY_RESAMPLE_INTERPOLATION, "Cubic", 10);
    public static final Object VALUE_INTERPOLATION_CATROM = new Value(KEY_RESAMPLE_INTERPOLATION, "Catrom", 11);
    public static final Object VALUE_INTERPOLATION_MITCHELL = new Value(KEY_RESAMPLE_INTERPOLATION, "Mitchell", 12);
    public static final Object VALUE_INTERPOLATION_LANCZOS = new Value(KEY_RESAMPLE_INTERPOLATION, "Lanczos", 13);
    public static final Object VALUE_INTERPOLATION_BLACKMAN_BESSEL = new Value(KEY_RESAMPLE_INTERPOLATION, "Blackman-Bessel", 14);
    public static final Object VALUE_INTERPOLATION_BLACKMAN_SINC = new Value(KEY_RESAMPLE_INTERPOLATION, "Blackman-Sinc", 15);
    int width;
    int height;
    int filterType;
    private static final double B = 0.3333333333333333;
    private static final double C = 0.3333333333333333;
    private static final double P0 = 0.8888888888888888;
    private static final double P2 = -2.0;
    private static final double P3 = 1.1666666666666667;
    private static final double Q0 = 1.7777777777777777;
    private static final double Q1 = -3.3333333333333335;
    private static final double Q2 = 2.0;
    private static final double Q3 = -0.3888888888888889;

    public ResampleOp(int n, int n2) {
        this(n, n2, 0);
    }

    public ResampleOp(int n, int n2, RenderingHints renderingHints) {
        this(n, n2, ResampleOp.getFilterType(renderingHints));
    }

    public ResampleOp(int n, int n2, int n3) {
        if (n <= 0 || n2 <= 0) {
            throw new IllegalArgumentException("width and height must be positive");
        }
        this.width = n;
        this.height = n2;
        this.filterType = ResampleOp.validateFilterType(n3);
    }

    private static int validateFilterType(int n) {
        switch (n) {
            case 0: 
            case 1: 
            case 2: 
            case 3: 
            case 4: 
            case 5: 
            case 6: 
            case 7: 
            case 8: 
            case 9: 
            case 10: 
            case 11: 
            case 12: 
            case 13: 
            case 14: 
            case 15: {
                return n;
            }
        }
        throw new IllegalArgumentException("Unknown filter type: " + n);
    }

    private static int getFilterType(RenderingHints renderingHints) {
        if (renderingHints == null) {
            return 0;
        }
        if (renderingHints.containsKey(KEY_RESAMPLE_INTERPOLATION)) {
            Object object = renderingHints.get(KEY_RESAMPLE_INTERPOLATION);
            if (!KEY_RESAMPLE_INTERPOLATION.isCompatibleValue(object)) {
                throw new IllegalArgumentException(object + " incompatible with key " + KEY_RESAMPLE_INTERPOLATION);
            }
            return object != null ? ((Value)object).getFilterType() : 0;
        }
        if (RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR.equals(renderingHints.get(RenderingHints.KEY_INTERPOLATION)) || !renderingHints.containsKey(RenderingHints.KEY_INTERPOLATION) && (RenderingHints.VALUE_RENDER_SPEED.equals(renderingHints.get(RenderingHints.KEY_RENDERING)) || RenderingHints.VALUE_COLOR_RENDER_SPEED.equals(renderingHints.get(RenderingHints.KEY_COLOR_RENDERING)))) {
            return 1;
        }
        if (RenderingHints.VALUE_INTERPOLATION_BILINEAR.equals(renderingHints.get(RenderingHints.KEY_INTERPOLATION))) {
            return 3;
        }
        if (RenderingHints.VALUE_INTERPOLATION_BICUBIC.equals(renderingHints.get(RenderingHints.KEY_INTERPOLATION))) {
            return 9;
        }
        if (RenderingHints.VALUE_RENDER_QUALITY.equals(renderingHints.get(RenderingHints.KEY_RENDERING)) || RenderingHints.VALUE_COLOR_RENDER_QUALITY.equals(renderingHints.get(RenderingHints.KEY_COLOR_RENDERING))) {
            return 12;
        }
        return 0;
    }

    @Override
    public final BufferedImage filter(BufferedImage bufferedImage, BufferedImage bufferedImage2) {
        ColorModel colorModel;
        if (bufferedImage == null) {
            throw new NullPointerException("Input == null");
        }
        if (bufferedImage == bufferedImage2) {
            throw new IllegalArgumentException("Output image cannot be the same as the input image");
        }
        switch (this.filterType) {
            case 1: {
                if (bufferedImage.getType() != 0) {
                    return ResampleOp.fastResample(bufferedImage, bufferedImage2, this.width, this.height, 1);
                }
            }
            case 3: {
                if (bufferedImage.getType() != 0) {
                    return ResampleOp.fastResample(bufferedImage, bufferedImage2, this.width, this.height, 2);
                }
            }
            case 9: {
                if (bufferedImage.getType() == 0) break;
                return ResampleOp.fastResample(bufferedImage, bufferedImage2, this.width, this.height, 3);
            }
        }
        InterpolationFilter interpolationFilter = ResampleOp.createFilter(this.filterType);
        if ((double)Math.min(bufferedImage.getWidth(), bufferedImage.getHeight()) <= interpolationFilter.support() || (double)Math.min(this.width, this.height) <= interpolationFilter.support()) {
            return ResampleOp.fastResample(bufferedImage, bufferedImage2, this.width, this.height, 2);
        }
        BufferedImage bufferedImage3 = MagickAccelerator.filter(this, bufferedImage, bufferedImage2);
        if (bufferedImage3 != null) {
            return bufferedImage3;
        }
        BufferedImage bufferedImage4 = this.filterType != 1 && this.filterType != 2 && (colorModel = bufferedImage.getColorModel()) instanceof IndexColorModel ? ImageUtil.toBuffered(bufferedImage, colorModel.hasAlpha() ? 6 : 5) : bufferedImage;
        bufferedImage3 = bufferedImage2 != null && bufferedImage4.getType() != 0 ? ImageUtil.toBuffered(bufferedImage2, bufferedImage4.getType()) : this.createCompatibleDestImage(bufferedImage4, null);
        this.resample(bufferedImage4, bufferedImage3, interpolationFilter);
        if (bufferedImage2 != null && bufferedImage2 != bufferedImage3) {
            ImageUtil.drawOnto(bufferedImage2, bufferedImage3);
            bufferedImage3 = bufferedImage2;
        }
        return bufferedImage3;
    }

    private static BufferedImage fastResample(BufferedImage bufferedImage, BufferedImage bufferedImage2, int n, int n2, int n3) {
        AffineTransformOp affineTransformOp;
        AffineTransform affineTransform;
        double d;
        double d2;
        BufferedImage bufferedImage3 = bufferedImage;
        if (n3 > 1 && (n < bufferedImage.getWidth() || n2 < bufferedImage.getHeight())) {
            int n4;
            int n5 = n2;
            for (n4 = n; n4 < bufferedImage.getWidth() / 2; n4 *= 2) {
            }
            while (n5 < bufferedImage.getHeight() / 2) {
                n5 *= 2;
            }
            d2 = (double)n4 / (double)bufferedImage.getWidth();
            d = (double)n5 / (double)bufferedImage.getHeight();
            affineTransform = AffineTransform.getScaleInstance(d2, d);
            affineTransformOp = new AffineTransformOp(affineTransform, 2);
            bufferedImage3 = affineTransformOp.filter(bufferedImage3, null);
        }
        affineTransformOp = null;
        d2 = (double)n / (double)bufferedImage3.getWidth();
        d = (double)n2 / (double)bufferedImage3.getHeight();
        if (n3 > 1) {
            while (d2 < 0.5 || d < 0.5) {
                if (d2 >= 0.5) {
                    affineTransform = AffineTransform.getScaleInstance(1.0, 0.5);
                    affineTransformOp = new AffineTransformOp(affineTransform, 2);
                    d *= 2.0;
                } else if (d >= 0.5) {
                    affineTransform = AffineTransform.getScaleInstance(0.5, 1.0);
                    affineTransformOp = new AffineTransformOp(affineTransform, 2);
                    d2 *= 2.0;
                } else {
                    d2 *= 2.0;
                    d *= 2.0;
                }
                if (affineTransformOp == null) {
                    affineTransform = AffineTransform.getScaleInstance(0.5, 0.5);
                    affineTransformOp = new AffineTransformOp(affineTransform, 2);
                }
                bufferedImage3 = affineTransformOp.filter(bufferedImage3, null);
            }
        }
        affineTransform = AffineTransform.getScaleInstance(d2, d);
        affineTransformOp = new AffineTransformOp(affineTransform, n3);
        return affineTransformOp.filter(bufferedImage3, bufferedImage2);
    }

    public int getFilterType() {
        return this.filterType;
    }

    private static InterpolationFilter createFilter(int n) {
        if (n == 0) {
            n = 13;
        }
        switch (n) {
            case 1: {
                return new PointFilter();
            }
            case 2: {
                return new BoxFilter();
            }
            case 3: {
                return new TriangleFilter();
            }
            case 4: {
                return new HermiteFilter();
            }
            case 5: {
                return new HanningFilter();
            }
            case 6: {
                return new HammingFilter();
            }
            case 7: {
                return new BlacmanFilter();
            }
            case 8: {
                return new GaussianFilter();
            }
            case 9: {
                return new QuadraticFilter();
            }
            case 10: {
                return new CubicFilter();
            }
            case 11: {
                return new CatromFilter();
            }
            case 12: {
                return new MitchellFilter();
            }
            case 13: {
                return new LanczosFilter();
            }
            case 14: {
                return new BlackmanBesselFilter();
            }
            case 15: {
                return new BlackmanSincFilter();
            }
        }
        throw new IllegalStateException("Unknown filter type: " + n);
    }

    @Override
    public final BufferedImage createCompatibleDestImage(BufferedImage bufferedImage, ColorModel colorModel) {
        if (bufferedImage == null) {
            throw new NullPointerException("pInput == null");
        }
        ColorModel colorModel2 = colorModel != null ? colorModel : bufferedImage.getColorModel();
        return new BufferedImage(colorModel2, ImageUtil.createCompatibleWritableRaster(bufferedImage, colorModel2, this.width, this.height), colorModel2.isAlphaPremultiplied(), null);
    }

    @Override
    public RenderingHints getRenderingHints() {
        Object object;
        switch (this.filterType) {
            case 0: {
                return null;
            }
            case 1: {
                object = VALUE_INTERPOLATION_POINT;
                break;
            }
            case 2: {
                object = VALUE_INTERPOLATION_BOX;
                break;
            }
            case 3: {
                object = VALUE_INTERPOLATION_TRIANGLE;
                break;
            }
            case 4: {
                object = VALUE_INTERPOLATION_HERMITE;
                break;
            }
            case 5: {
                object = VALUE_INTERPOLATION_HANNING;
                break;
            }
            case 6: {
                object = VALUE_INTERPOLATION_HAMMING;
                break;
            }
            case 7: {
                object = VALUE_INTERPOLATION_BLACKMAN;
                break;
            }
            case 8: {
                object = VALUE_INTERPOLATION_GAUSSIAN;
                break;
            }
            case 9: {
                object = VALUE_INTERPOLATION_QUADRATIC;
                break;
            }
            case 10: {
                object = VALUE_INTERPOLATION_CUBIC;
                break;
            }
            case 11: {
                object = VALUE_INTERPOLATION_CATROM;
                break;
            }
            case 12: {
                object = VALUE_INTERPOLATION_MITCHELL;
                break;
            }
            case 13: {
                object = VALUE_INTERPOLATION_LANCZOS;
                break;
            }
            case 14: {
                object = VALUE_INTERPOLATION_BLACKMAN_BESSEL;
                break;
            }
            case 15: {
                object = VALUE_INTERPOLATION_BLACKMAN_SINC;
                break;
            }
            default: {
                throw new IllegalStateException("Unknown filter type: " + this.filterType);
            }
        }
        return new RenderingHints(KEY_RESAMPLE_INTERPOLATION, object);
    }

    @Override
    public Rectangle2D getBounds2D(BufferedImage bufferedImage) {
        return new Rectangle(this.width, this.height);
    }

    @Override
    public Point2D getPoint2D(Point2D point2D, Point2D point2D2) {
        if (point2D2 == null) {
            point2D2 = point2D instanceof Point2D.Double ? new Point2D.Double() : new Point2D.Float();
            point2D2.setLocation(point2D);
        }
        return point2D2;
    }

    private static double sinc(double d) {
        if ((d *= Math.PI) != 0.0) {
            return Math.sin(d) / d;
        }
        return 1.0;
    }

    private static double j1(double d) {
        double[] dArray = new double[]{5.811993540016061E20, -6.672106568924916E19, 2.3164335806340024E18, -3.588817569910106E16, 2.9087952638347756E14, -1.3229834803321265E12, 3.4132341823017006E9, -4695753.530642996, 2701.1227108923235};
        double[] dArray2 = new double[]{1.1623987080032122E21, 1.185770712190321E19, 6.0920613989175216E16, 2.0816612213076075E14, 5.2437102621676495E11, 1.013863514358674E9, 1501793.5949985855, 1606.9315734814877, 1.0};
        double d2 = dArray[8];
        double d3 = dArray2[8];
        for (int i = 7; i >= 0; --i) {
            d2 = d2 * d * d + dArray[i];
            d3 = d3 * d * d + dArray2[i];
        }
        return d2 / d3;
    }

    private static double p1(double d) {
        double[] dArray = new double[]{35224.66491336798, 62758.84524716128, 31353.963110915956, 4985.4832060594335, 211.15291828539623, 1.2571716929145342};
        double[] dArray2 = new double[]{35224.66491336798, 62694.34695935605, 31240.406381904104, 4930.396490181089, 203.07751891347593, 1.0};
        double d2 = dArray[5];
        double d3 = dArray2[5];
        for (int i = 4; i >= 0; --i) {
            d2 = d2 * (8.0 / d) * (8.0 / d) + dArray[i];
            d3 = d3 * (8.0 / d) * (8.0 / d) + dArray2[i];
        }
        return d2 / d3;
    }

    private static double q1(double d) {
        double[] dArray = new double[]{351.17519143035526, 721.0391804904475, 425.98730116544425, 83.18989576738508, 4.568171629551227, 0.03532840052740124};
        double[] dArray2 = new double[]{7491.737417180912, 15414.177339265098, 9152.231701516992, 1811.1867005523513, 103.81875854621337, 1.0};
        double d2 = dArray[5];
        double d3 = dArray2[5];
        for (int i = 4; i >= 0; --i) {
            d2 = d2 * (8.0 / d) * (8.0 / d) + dArray[i];
            d3 = d3 * (8.0 / d) * (8.0 / d) + dArray2[i];
        }
        return d2 / d3;
    }

    static double besselOrderOne(double d) {
        if (d == 0.0) {
            return 0.0;
        }
        double d2 = d;
        if (d < 0.0) {
            d = -d;
        }
        if (d < 8.0) {
            return d2 * ResampleOp.j1(d);
        }
        double d3 = Math.sqrt(2.0 / (Math.PI * d)) * (ResampleOp.p1(d) * (1.0 / Math.sqrt(2.0) * (Math.sin(d) - Math.cos(d))) - 8.0 / d * ResampleOp.q1(d) * (-1.0 / Math.sqrt(2.0) * (Math.sin(d) + Math.cos(d))));
        if (d2 < 0.0) {
            d3 = -d3;
        }
        return d3;
    }

    private static double bessel(double d) {
        if (d == 0.0) {
            return 0.7853981633974483;
        }
        return ResampleOp.besselOrderOne(Math.PI * d) / (2.0 * d);
    }

    private static double blackman(double d) {
        return 0.42 + 0.5 * Math.cos(Math.PI * d) + 0.08 * Math.cos(Math.PI * 2 * d);
    }

    static int round(double d) {
        int n = (int)d;
        double d2 = d - (double)n;
        if (d2 < 0.0) {
            d2 = -d2;
        }
        if (d2 >= 0.5) {
            n = d < 0.0 ? --n : ++n;
        }
        return n;
    }

    private ContributorList calcXContrib(double d, double d2, int n, InterpolationFilter interpolationFilter, int n2) {
        ContributorList contributorList;
        block11: {
            block10: {
                int n3;
                contributorList = new ContributorList();
                if (!(d < 1.0)) break block10;
                double d3 = d2 / d;
                double d4 = 1.0 / d;
                if (d3 <= 0.5) {
                    d3 = 0.500001;
                    d4 = 1.0;
                }
                contributorList.p = new Contributor[(int)(d3 * 2.0 + 1.0 + 0.5)];
                double d5 = (double)n2 / d;
                int n4 = (int)Math.ceil(d5 - d3);
                int n5 = (int)Math.floor(d5 + d3);
                double d6 = 0.0;
                for (n3 = n4; n3 <= n5; ++n3) {
                    double d7 = d5 - (double)n3;
                    d7 = interpolationFilter.filter(d7 / d4) / d4;
                    int n6 = n3 < 0 ? -n3 : (n3 >= n ? n - n3 + n - 1 : n3);
                    if (n6 >= n) {
                        n6 %= n;
                    } else if (n6 < 0) {
                        n6 = n - 1;
                    }
                    ++contributorList.n;
                    contributorList.p[var23_22] = new Contributor();
                    contributorList.p[var23_22].pixel = n6;
                    contributorList.p[var23_22].weight = d7;
                    d6 += d7;
                }
                if (d6 == 0.0 || d6 == 1.0) break block11;
                d6 = 1.0 / d6;
                for (n3 = 0; n3 < contributorList.n; ++n3) {
                    contributorList.p[n3].weight *= d6;
                }
                break block11;
            }
            contributorList.p = new Contributor[(int)(d2 * 2.0 + 1.0 + 0.5)];
            double d8 = (double)n2 / d;
            int n7 = (int)Math.ceil(d8 - d2);
            int n8 = (int)Math.floor(d8 + d2);
            for (int i = n7; i <= n8; ++i) {
                double d9 = d8 - (double)i;
                d9 = interpolationFilter.filter(d9);
                int n9 = i < 0 ? -i : (i >= n ? n - i + n - 1 : i);
                if (n9 >= n) {
                    n9 %= n;
                } else if (n9 < 0) {
                    n9 = n - 1;
                }
                ++contributorList.n;
                contributorList.p[var21_18] = new Contributor();
                contributorList.p[var21_18].pixel = n9;
                contributorList.p[var21_18].weight = d9;
            }
        }
        return contributorList;
    }

    private BufferedImage resample(BufferedImage bufferedImage, BufferedImage bufferedImage2, InterpolationFilter interpolationFilter) {
        int n;
        int n2;
        double d;
        int n3;
        int n4 = bufferedImage2.getWidth();
        int n5 = bufferedImage2.getHeight();
        int n6 = bufferedImage.getWidth();
        int n7 = bufferedImage.getHeight();
        ColorModel colorModel = bufferedImage.getColorModel();
        WritableRaster writableRaster = ImageUtil.createCompatibleWritableRaster(bufferedImage, colorModel, 1, n7);
        double d2 = (double)n4 / (double)n6;
        double d3 = (double)n5 / (double)n7;
        ContributorList[] contributorListArray = new ContributorList[n5];
        for (int i = 0; i < contributorListArray.length; ++i) {
            contributorListArray[i] = new ContributorList();
        }
        double d4 = interpolationFilter.support();
        if (d3 < 1.0) {
            double d5 = d4 / d3;
            double d6 = 1.0 / d3;
            if (d5 <= 0.5) {
                d5 = 0.500001;
                d6 = 1.0;
            }
            for (n3 = 0; n3 < n5; ++n3) {
                int n8;
                contributorListArray[n3].p = new Contributor[(int)(d5 * 2.0 + 1.0 + 0.5)];
                d = (double)n3 / d3;
                n2 = (int)Math.ceil(d - d5);
                int n9 = (int)Math.floor(d + d5);
                double d7 = 0.0;
                for (n8 = n2; n8 <= n9; ++n8) {
                    double d8 = d - (double)n8;
                    d8 = interpolationFilter.filter(d8 / d6) / d6;
                    int n10 = n8 < 0 ? -n8 : (n8 >= n7 ? n7 - n8 + n7 - 1 : n8);
                    if (n10 >= n7) {
                        n10 %= n7;
                    } else if (n10 < 0) {
                        n10 = n7 - 1;
                    }
                    ++contributorListArray[n3].n;
                    contributorListArray[n3].p[var32_37] = new Contributor();
                    contributorListArray[n3].p[var32_37].pixel = n10;
                    contributorListArray[n3].p[var32_37].weight = d8;
                    d7 += d8;
                }
                if (d7 == 0.0 || d7 == 1.0) continue;
                d7 = 1.0 / d7;
                for (n8 = 0; n8 < contributorListArray[n3].n; ++n8) {
                    contributorListArray[n3].p[n8].weight *= d7;
                }
            }
        } else {
            for (int i = 0; i < n5; ++i) {
                contributorListArray[i].p = new Contributor[(int)(d4 * 2.0 + 1.0 + 0.5)];
                double d9 = (double)i / d3;
                double d10 = Math.ceil(d9 - d4);
                d = Math.floor(d9 + d4);
                n2 = (int)d10;
                while ((double)n2 <= d) {
                    double d11 = d9 - (double)n2;
                    d11 = interpolationFilter.filter(d11);
                    n = n2 < 0 ? -n2 : (n2 >= n7 ? n7 - n2 + n7 - 1 : n2);
                    if (n >= n7) {
                        n %= n7;
                    } else if (n < 0) {
                        n = n7 - 1;
                    }
                    ++contributorListArray[i].n;
                    contributorListArray[i].p[n8] = new Contributor();
                    contributorListArray[i].p[n8].pixel = n;
                    contributorListArray[i].p[n8].weight = d11;
                    ++n2;
                }
            }
        }
        WritableRaster writableRaster2 = bufferedImage.getRaster();
        WritableRaster writableRaster3 = bufferedImage2.getRaster();
        int n11 = writableRaster2.getNumBands();
        int[] nArray = new int[n11];
        for (n3 = 0; n3 < n11; ++n3) {
            nArray[n3] = (1 << bufferedImage.getColorModel().getComponentSize(n3)) - 1;
        }
        for (n3 = 0; n3 < n4; ++n3) {
            int n12;
            double d12;
            int n13;
            ContributorList contributorList = this.calcXContrib(d2, d4, n6, interpolationFilter, n3);
            for (n13 = 0; n13 < n7; ++n13) {
                for (n2 = 0; n2 < n11; ++n2) {
                    double d13 = 0.0;
                    n = 0;
                    d12 = writableRaster2.getSample(contributorList.p[0].pixel, n13, n2);
                    for (n12 = 0; n12 < contributorList.n; ++n12) {
                        double d14;
                        double d15 = d14 = n12 == 0 ? d12 : (double)writableRaster2.getSample(contributorList.p[n12].pixel, n13, n2);
                        if (d14 != d12) {
                            n = 1;
                        }
                        d13 += d14 * contributorList.p[n12].weight;
                    }
                    double d16 = d13 = n != 0 ? (double)ResampleOp.round(d13) : d12;
                    if (d13 < 0.0) {
                        d13 = 0.0;
                    } else if (d13 > (double)nArray[n2]) {
                        d13 = nArray[n2];
                    }
                    writableRaster.setSample(0, n13, n2, d13);
                }
            }
            for (n13 = 0; n13 < n5; ++n13) {
                for (n2 = 0; n2 < n11; ++n2) {
                    double d17 = 0.0;
                    n = 0;
                    d12 = writableRaster.getSample(0, contributorListArray[n13].p[0].pixel, n2);
                    for (n12 = 0; n12 < contributorListArray[n13].n; ++n12) {
                        double d18;
                        double d19 = d18 = n12 == 0 ? d12 : (double)writableRaster.getSample(0, contributorListArray[n13].p[n12].pixel, n2);
                        if (d18 != d12) {
                            n = 1;
                        }
                        d17 += d18 * contributorListArray[n13].p[n12].weight;
                    }
                    double d20 = d17 = n != 0 ? (double)ResampleOp.round(d17) : d12;
                    if (d17 < 0.0) {
                        d17 = 0.0;
                    } else if (d17 > (double)nArray[n2]) {
                        d17 = nArray[n2];
                    }
                    writableRaster3.setSample(n3, n13, n2, d17);
                }
            }
        }
        return bufferedImage2;
    }

    class ContributorList {
        int n;
        Contributor[] p;

        ContributorList() {
        }
    }

    class Contributor {
        int pixel;
        double weight;

        Contributor() {
        }
    }

    static class BlackmanSincFilter
    implements InterpolationFilter {
        BlackmanSincFilter() {
        }

        @Override
        public final double filter(double d) {
            return ResampleOp.blackman(d / this.support()) * ResampleOp.sinc(d);
        }

        @Override
        public final double support() {
            return 4.0;
        }
    }

    static class BlackmanBesselFilter
    implements InterpolationFilter {
        BlackmanBesselFilter() {
        }

        @Override
        public final double filter(double d) {
            return ResampleOp.blackman(d / this.support()) * ResampleOp.bessel(d);
        }

        @Override
        public final double support() {
            return 3.2383;
        }
    }

    static class HammingFilter
    implements InterpolationFilter {
        HammingFilter() {
        }

        @Override
        public final double filter(double d) {
            return 0.54 + 0.46 * Math.cos(Math.PI * d);
        }

        @Override
        public final double support() {
            return 1.0;
        }
    }

    static class HanningFilter
    implements InterpolationFilter {
        HanningFilter() {
        }

        @Override
        public final double filter(double d) {
            return 0.5 + 0.5 * Math.cos(Math.PI * d);
        }

        @Override
        public final double support() {
            return 1.0;
        }
    }

    static class GaussianFilter
    implements InterpolationFilter {
        GaussianFilter() {
        }

        @Override
        public final double filter(double d) {
            return Math.exp(-2.0 * d * d) * Math.sqrt(0.6366197723675814);
        }

        @Override
        public final double support() {
            return 1.25;
        }
    }

    static class CatromFilter
    implements InterpolationFilter {
        CatromFilter() {
        }

        @Override
        public final double filter(double d) {
            if (d < 0.0) {
                d = -d;
            }
            if (d < 1.0) {
                return 0.5 * (2.0 + d * d * (-5.0 + d * 3.0));
            }
            if (d < 2.0) {
                return 0.5 * (4.0 + d * (-8.0 + d * (5.0 - d)));
            }
            return 0.0;
        }

        @Override
        public final double support() {
            return 2.0;
        }
    }

    static class BlacmanFilter
    implements InterpolationFilter {
        BlacmanFilter() {
        }

        @Override
        public final double filter(double d) {
            return ResampleOp.blackman(d);
        }

        @Override
        public final double support() {
            return 1.0;
        }
    }

    static class MitchellFilter
    implements InterpolationFilter {
        MitchellFilter() {
        }

        @Override
        public final double filter(double d) {
            if (d < -2.0) {
                return 0.0;
            }
            if (d < -1.0) {
                return 1.7777777777777777 - d * (-3.3333333333333335 - d * (2.0 - d * -0.3888888888888889));
            }
            if (d < 0.0) {
                return 0.8888888888888888 + d * d * (-2.0 - d * 1.1666666666666667);
            }
            if (d < 1.0) {
                return 0.8888888888888888 + d * d * (-2.0 + d * 1.1666666666666667);
            }
            if (d < 2.0) {
                return 1.7777777777777777 + d * (-3.3333333333333335 + d * (2.0 + d * -0.3888888888888889));
            }
            return 0.0;
        }

        @Override
        public final double support() {
            return 2.0;
        }
    }

    static class LanczosFilter
    implements InterpolationFilter {
        LanczosFilter() {
        }

        @Override
        public final double filter(double d) {
            if (d < 0.0) {
                d = -d;
            }
            if (d < 3.0) {
                return ResampleOp.sinc(d) * ResampleOp.sinc(d / 3.0);
            }
            return 0.0;
        }

        @Override
        public final double support() {
            return 3.0;
        }
    }

    static class CubicFilter
    implements InterpolationFilter {
        CubicFilter() {
        }

        @Override
        public final double filter(double d) {
            if (d < 0.0) {
                d = -d;
            }
            if (d < 1.0) {
                double d2 = d * d;
                return 0.5 * d2 * d - d2 + 0.6666666666666666;
            }
            if (d < 2.0) {
                d = 2.0 - d;
                return 0.16666666666666666 * (d * d * d);
            }
            return 0.0;
        }

        @Override
        public final double support() {
            return 2.0;
        }
    }

    static class QuadraticFilter
    implements InterpolationFilter {
        QuadraticFilter() {
        }

        @Override
        public final double filter(double d) {
            if (d < 0.0) {
                d = -d;
            }
            if (d < 0.5) {
                return 0.75 - d * d;
            }
            if (d < 1.5) {
                return 0.5 * ((d -= 1.5) * d);
            }
            return 0.0;
        }

        @Override
        public final double support() {
            return 1.5;
        }
    }

    static class TriangleFilter
    implements InterpolationFilter {
        TriangleFilter() {
        }

        @Override
        public final double filter(double d) {
            if (d < 0.0) {
                d = -d;
            }
            if (d < 1.0) {
                return 1.0 - d;
            }
            return 0.0;
        }

        @Override
        public final double support() {
            return 1.0;
        }
    }

    static class BoxFilter
    implements InterpolationFilter {
        private final double mSupport;

        public BoxFilter() {
            this.mSupport = 0.5;
        }

        protected BoxFilter(double d) {
            this.mSupport = d;
        }

        @Override
        public final double filter(double d) {
            if (d >= -0.5 && d < 0.5) {
                return 1.0;
            }
            return 0.0;
        }

        @Override
        public final double support() {
            return this.mSupport;
        }
    }

    static class PointFilter
    extends BoxFilter {
        public PointFilter() {
            super(0.0);
        }
    }

    static class HermiteFilter
    implements InterpolationFilter {
        HermiteFilter() {
        }

        @Override
        public final double filter(double d) {
            if (d < 0.0) {
                d = -d;
            }
            if (d < 1.0) {
                return (2.0 * d - 3.0) * d * d + 1.0;
            }
            return 0.0;
        }

        @Override
        public final double support() {
            return 1.0;
        }
    }

    static interface InterpolationFilter {
        public double filter(double var1);

        public double support();
    }

    static final class Value {
        private final RenderingHints.Key key;
        private final String name;
        private final int type;

        public Value(RenderingHints.Key key, String string, int n) {
            this.key = key;
            this.name = string;
            this.type = ResampleOp.validateFilterType(n);
        }

        public boolean isCompatibleKey(Key key) {
            return key == this.key;
        }

        public int getFilterType() {
            return this.type;
        }

        public String toString() {
            return this.name;
        }
    }

    static class Key
    extends RenderingHints.Key {
        static int sIndex = 10000;
        private final String name;

        public Key(String string) {
            super(sIndex++);
            this.name = string;
        }

        @Override
        public boolean isCompatibleValue(Object object) {
            return object instanceof Value && ((Value)object).isCompatibleKey(this);
        }

        public String toString() {
            return this.name;
        }
    }
}


/*
 * Decompiled with CFR 0.152.
 */
package javax.media.jai;

import javax.media.jai.JaiI18N;
import javax.media.jai.LookupTableJAI;

public class ColorCube
extends LookupTableJAI {
    public static final ColorCube BYTE_496 = ColorCube.createColorCube(0, 38, new int[]{4, 9, 6});
    public static final ColorCube BYTE_855 = ColorCube.createColorCube(0, 54, new int[]{8, 5, 5});
    private int[] dimension;
    private int[] dimsLessOne;
    private int[] multipliers;
    private int adjustedOffset;
    private int dataType;
    private int numBands;

    /*
     * WARNING - void declaration
     */
    public static ColorCube createColorCube(int dataType, int offset, int[] dimension) {
        void var3_3;
        switch (dataType) {
            case 0: {
                ColorCube colorCube = ColorCube.createColorCubeByte(offset, dimension);
                break;
            }
            case 2: {
                ColorCube colorCube = ColorCube.createColorCubeShort(offset, dimension);
                break;
            }
            case 1: {
                ColorCube colorCube = ColorCube.createColorCubeUShort(offset, dimension);
                break;
            }
            case 3: {
                ColorCube colorCube = ColorCube.createColorCubeInt(offset, dimension);
                break;
            }
            case 4: {
                ColorCube colorCube = ColorCube.createColorCubeFloat(offset, dimension);
                break;
            }
            case 5: {
                ColorCube colorCube = ColorCube.createColorCubeDouble(offset, dimension);
                break;
            }
            default: {
                throw new RuntimeException(JaiI18N.getString("ColorCube0"));
            }
        }
        return var3_3;
    }

    public static ColorCube createColorCube(int dataType, int[] dimension) {
        if (dimension == null) {
            throw new IllegalArgumentException(JaiI18N.getString("Generic0"));
        }
        return ColorCube.createColorCube(dataType, 0, dimension);
    }

    private static ColorCube createColorCubeByte(int offset, int[] dimension) {
        ColorCube colorCube = new ColorCube(ColorCube.createDataArrayByte(offset, dimension), offset);
        colorCube.initFields(offset, dimension);
        return colorCube;
    }

    private static ColorCube createColorCubeShort(int offset, int[] dimension) {
        ColorCube colorCube = new ColorCube(ColorCube.createDataArrayShort(offset, dimension), offset, false);
        colorCube.initFields(offset, dimension);
        return colorCube;
    }

    private static ColorCube createColorCubeUShort(int offset, int[] dimension) {
        ColorCube colorCube = new ColorCube(ColorCube.createDataArrayUShort(offset, dimension), offset, true);
        colorCube.initFields(offset, dimension);
        return colorCube;
    }

    private static ColorCube createColorCubeInt(int offset, int[] dimension) {
        ColorCube colorCube = new ColorCube(ColorCube.createDataArrayInt(offset, dimension), offset);
        colorCube.initFields(offset, dimension);
        return colorCube;
    }

    private static ColorCube createColorCubeFloat(int offset, int[] dimension) {
        ColorCube colorCube = new ColorCube(ColorCube.createDataArrayFloat(offset, dimension), offset);
        colorCube.initFields(offset, dimension);
        return colorCube;
    }

    private static ColorCube createColorCubeDouble(int offset, int[] dimension) {
        ColorCube colorCube = new ColorCube(ColorCube.createDataArrayDouble(offset, dimension), offset);
        colorCube.initFields(offset, dimension);
        return colorCube;
    }

    /*
     * WARNING - void declaration
     */
    private static Object createDataArray(int dataType, int offset, int[] dimension) {
        void var12_11;
        int band;
        void var10_10;
        int nbands = dimension.length;
        if (nbands == 0) {
            throw new RuntimeException(JaiI18N.getString("ColorCube1"));
        }
        for (int band2 = 0; band2 < nbands; ++band2) {
            if (dimension[band2] != 0) continue;
            throw new RuntimeException(JaiI18N.getString("ColorCube2"));
        }
        int[] dimensionAbs = new int[nbands];
        for (int band3 = 0; band3 < nbands; ++band3) {
            dimensionAbs[band3] = Math.abs(dimension[band3]);
        }
        double floatSize = dimensionAbs[0];
        for (int band4 = 1; band4 < nbands; ++band4) {
            floatSize *= (double)dimensionAbs[band4];
        }
        if (floatSize > 2.147483647E9) {
            throw new RuntimeException(JaiI18N.getString("ColorCube3"));
        }
        int size = (int)floatSize;
        switch (dataType) {
            case 0: {
                double dataMin = 0.0;
                double dataMax = 255.0;
                Object dataArray = new byte[nbands][size];
                break;
            }
            case 2: {
                double dataMin = -32768.0;
                double dataMax = 32767.0;
                Object dataArray = new short[nbands][size];
                break;
            }
            case 1: {
                double dataMin = 0.0;
                double dataMax = 65535.0;
                Object dataArray = new short[nbands][size];
                break;
            }
            case 3: {
                double dataMin = -2.147483648E9;
                double dataMax = 2.147483647E9;
                Object dataArray = new int[nbands][size];
                break;
            }
            case 4: {
                double dataMin = -3.4028234663852886E38;
                double dataMax = 3.4028234663852886E38;
                Object dataArray = new float[nbands][size];
                break;
            }
            case 5: {
                double dataMin = -1.7976931348623157E308;
                double dataMax = Double.MAX_VALUE;
                Object dataArray = new double[nbands][size];
                break;
            }
            default: {
                throw new RuntimeException(JaiI18N.getString("ColorCube7"));
            }
        }
        if ((double)(size + offset) > var10_10) {
            throw new RuntimeException(JaiI18N.getString("ColorCube4"));
        }
        int[] multipliers = new int[nbands];
        multipliers[0] = 1;
        for (band = 1; band < nbands; ++band) {
            multipliers[band] = multipliers[band - 1] * dimensionAbs[band - 1];
        }
        block19: for (band = 0; band < nbands; ++band) {
            void start;
            void var8_9;
            int idimension = dimensionAbs[band];
            double delta = idimension == 1 ? 0.0 : (dataType == 4 || dataType == 5 ? 1.0 / (double)(idimension - 1) : (var10_10 - var8_9) / (double)(idimension - 1));
            if (dimension[band] < 0) {
                delta = -delta;
                start = var10_10;
            } else {
                start = var8_9;
            }
            int repeatCount = multipliers[band];
            switch (dataType) {
                case 0: {
                    byte[][] byteData = (byte[][])var12_11;
                    int index = 0;
                    while (index < size) {
                        void val = start;
                        for (int i = 0; i < idimension; ++i) {
                            for (int j = 0; j < repeatCount; ++j) {
                                byteData[band][index] = (byte)((int)(val + 0.5) & 0xFF);
                                ++index;
                            }
                            val += delta;
                        }
                    }
                    continue block19;
                }
                case 1: 
                case 2: {
                    short[][] shortData = (short[][])var12_11;
                    int index = 0;
                    while (index < size) {
                        void val = start;
                        for (int i = 0; i < idimension; ++i) {
                            for (int j = 0; j < repeatCount; ++j) {
                                shortData[band][index] = (short)(val + 0.5);
                                ++index;
                            }
                            val += delta;
                        }
                    }
                    continue block19;
                }
                case 3: {
                    int[][] intData = (int[][])var12_11;
                    int index = 0;
                    while (index < size) {
                        void val = start;
                        for (int i = 0; i < idimension; ++i) {
                            for (int j = 0; j < repeatCount; ++j) {
                                intData[band][index] = (int)(val + 0.5);
                                ++index;
                            }
                            val += delta;
                        }
                    }
                    continue block19;
                }
                case 4: {
                    float[][] floatData = (float[][])var12_11;
                    int index = 0;
                    while (index < size) {
                        void val = start;
                        for (int i = 0; i < idimension; ++i) {
                            for (int j = 0; j < repeatCount; ++j) {
                                floatData[band][index] = (float)val;
                                ++index;
                            }
                            val += delta;
                        }
                    }
                    continue block19;
                }
                case 5: {
                    double[][] doubleData = (double[][])var12_11;
                    int index = 0;
                    while (index < size) {
                        void val = start;
                        for (int i = 0; i < idimension; ++i) {
                            for (int j = 0; j < repeatCount; ++j) {
                                doubleData[band][index] = val;
                                ++index;
                            }
                            val += delta;
                        }
                    }
                    continue block19;
                }
                default: {
                    throw new RuntimeException(JaiI18N.getString("ColorCube5"));
                }
            }
        }
        return var12_11;
    }

    private static byte[][] createDataArrayByte(int offset, int[] dimension) {
        return (byte[][])ColorCube.createDataArray(0, offset, dimension);
    }

    private static short[][] createDataArrayShort(int offset, int[] dimension) {
        return (short[][])ColorCube.createDataArray(2, offset, dimension);
    }

    private static short[][] createDataArrayUShort(int offset, int[] dimension) {
        return (short[][])ColorCube.createDataArray(1, offset, dimension);
    }

    private static int[][] createDataArrayInt(int offset, int[] dimension) {
        return (int[][])ColorCube.createDataArray(3, offset, dimension);
    }

    private static float[][] createDataArrayFloat(int offset, int[] dimension) {
        return (float[][])ColorCube.createDataArray(4, offset, dimension);
    }

    private static double[][] createDataArrayDouble(int offset, int[] dimension) {
        return (double[][])ColorCube.createDataArray(5, offset, dimension);
    }

    protected ColorCube(byte[][] data, int offset) {
        super(data, offset);
    }

    protected ColorCube(short[][] data, int offset, boolean isUShort) {
        super(data, offset, isUShort);
    }

    protected ColorCube(int[][] data, int offset) {
        super(data, offset);
    }

    protected ColorCube(float[][] data, int offset) {
        super(data, offset);
    }

    protected ColorCube(double[][] data, int offset) {
        super(data, offset);
    }

    private void initFields(int offset, int[] dimension) {
        int i;
        this.dimension = dimension;
        this.multipliers = new int[dimension.length];
        this.dimsLessOne = new int[dimension.length];
        this.multipliers[0] = 1;
        for (i = 1; i < this.multipliers.length; ++i) {
            this.multipliers[i] = this.multipliers[i - 1] * Math.abs(dimension[i - 1]);
        }
        for (i = 0; i < this.multipliers.length; ++i) {
            if (dimension[i] < 0) {
                this.multipliers[i] = -this.multipliers[i];
            }
            this.dimsLessOne[i] = Math.abs(dimension[i]) - 1;
        }
        this.adjustedOffset = offset;
        for (i = 0; i < dimension.length; ++i) {
            if (dimension[i] <= 1 || this.multipliers[i] >= 0) continue;
            this.adjustedOffset += Math.abs(this.multipliers[i]) * this.dimsLessOne[i];
        }
        this.dataType = this.getDataType();
        this.numBands = this.getNumBands();
    }

    public int[] getDimension() {
        return this.dimension;
    }

    public int[] getDimsLessOne() {
        return this.dimsLessOne;
    }

    public int[] getMultipliers() {
        return this.multipliers;
    }

    public int getAdjustedOffset() {
        return this.adjustedOffset;
    }

    public int findNearestEntry(float[] pixel) {
        int index = -1;
        index = this.adjustedOffset;
        switch (this.dataType) {
            case 0: {
                for (int band = 0; band < this.numBands; ++band) {
                    int tmp = (int)(pixel[band] * (float)this.dimsLessOne[band]);
                    if ((tmp & 0xFF) > 127) {
                        tmp += 256;
                    }
                    index += (tmp >> 8) * this.multipliers[band];
                }
                break;
            }
            case 2: {
                for (int band = 0; band < this.numBands; ++band) {
                    int tmp = (int)(pixel[band] - -32768.0f) * this.dimsLessOne[band];
                    if ((tmp & 0xFFFF) > Short.MAX_VALUE) {
                        tmp += 65536;
                    }
                    index += (tmp >> 16) * this.multipliers[band];
                }
                break;
            }
            case 1: {
                for (int band = 0; band < this.numBands; ++band) {
                    int tmp = (int)(pixel[band] * (float)this.dimsLessOne[band]);
                    if ((tmp & 0xFFFF) > Short.MAX_VALUE) {
                        tmp += 65536;
                    }
                    index += (tmp >> 16) * this.multipliers[band];
                }
                break;
            }
            case 3: {
                for (int band = 0; band < this.numBands; ++band) {
                    long tmp = (long)((pixel[band] - -2.14748365E9f) * (float)this.dimsLessOne[band]);
                    if (tmp > Integer.MAX_VALUE) {
                        tmp += 0L;
                    }
                    index += (int)(tmp >> 32) * this.multipliers[band];
                }
                break;
            }
            case 4: {
                for (int band = 0; band < this.numBands; ++band) {
                    float ftmp = pixel[band] * (float)this.dimsLessOne[band];
                    int itmp = (int)ftmp;
                    if (ftmp - (float)itmp >= 0.5f) {
                        ++itmp;
                    }
                    index += itmp * this.multipliers[band];
                }
                break;
            }
            case 5: {
                for (int band = 0; band < this.numBands; ++band) {
                    double ftmp = pixel[band] * (float)this.dimsLessOne[band];
                    int itmp = (int)ftmp;
                    if (ftmp - (double)itmp >= 0.5) {
                        ++itmp;
                    }
                    index += itmp * this.multipliers[band];
                }
                break;
            }
            default: {
                throw new RuntimeException(JaiI18N.getString("ColorCube6"));
            }
        }
        return index;
    }
}


/*
 * Decompiled with CFR 0.152.
 */
package org.apache.pdfbox.pdmodel.graphics.blend;

import java.util.HashMap;
import java.util.Map;
import org.apache.pdfbox.cos.COSArray;
import org.apache.pdfbox.cos.COSBase;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.pdmodel.graphics.blend.NonSeparableBlendMode;
import org.apache.pdfbox.pdmodel.graphics.blend.SeparableBlendMode;

public abstract class BlendMode {
    public static final SeparableBlendMode NORMAL;
    public static final SeparableBlendMode COMPATIBLE;
    public static final SeparableBlendMode MULTIPLY;
    public static final SeparableBlendMode SCREEN;
    public static final SeparableBlendMode OVERLAY;
    public static final SeparableBlendMode DARKEN;
    public static final SeparableBlendMode LIGHTEN;
    public static final SeparableBlendMode COLOR_DODGE;
    public static final SeparableBlendMode COLOR_BURN;
    public static final SeparableBlendMode HARD_LIGHT;
    public static final SeparableBlendMode SOFT_LIGHT;
    public static final SeparableBlendMode DIFFERENCE;
    public static final SeparableBlendMode EXCLUSION;
    public static final NonSeparableBlendMode HUE;
    public static final NonSeparableBlendMode SATURATION;
    public static final NonSeparableBlendMode COLOR;
    public static final NonSeparableBlendMode LUMINOSITY;
    private static final Map<COSName, BlendMode> BLEND_MODES;
    private static final Map<BlendMode, COSName> BLEND_MODE_NAMES;

    BlendMode() {
    }

    public static BlendMode getInstance(COSBase cosBlendMode) {
        BlendMode result = null;
        if (cosBlendMode instanceof COSName) {
            result = BLEND_MODES.get(cosBlendMode);
        } else if (cosBlendMode instanceof COSArray) {
            COSArray cosBlendModeArray = (COSArray)cosBlendMode;
            for (int i = 0; i < cosBlendModeArray.size() && (result = BLEND_MODES.get(cosBlendModeArray.getObject(i))) == null; ++i) {
            }
        }
        if (result != null) {
            return result;
        }
        return NORMAL;
    }

    public static COSName getCOSName(BlendMode bm) {
        return BLEND_MODE_NAMES.get(bm);
    }

    private static int get255Value(float val) {
        return (int)Math.floor((double)val >= 1.0 ? 255.0 : (double)val * 255.0);
    }

    private static void getSaturationRGB(float[] srcValues, float[] dstValues, float[] result) {
        int b;
        int g;
        int maxb;
        int rd = BlendMode.get255Value(dstValues[0]);
        int gd = BlendMode.get255Value(dstValues[1]);
        int bd = BlendMode.get255Value(dstValues[2]);
        int rs = BlendMode.get255Value(srcValues[0]);
        int gs = BlendMode.get255Value(srcValues[1]);
        int bs = BlendMode.get255Value(srcValues[2]);
        int minb = Math.min(rd, Math.min(gd, bd));
        if (minb == (maxb = Math.max(rd, Math.max(gd, bd)))) {
            result[0] = (float)gd / 255.0f;
            result[1] = (float)gd / 255.0f;
            result[2] = (float)gd / 255.0f;
            return;
        }
        int mins = Math.min(rs, Math.min(gs, bs));
        int y = rd * 77 + gd * 151 + bd * 28 + 128 >> 8;
        int maxs = Math.max(rs, Math.max(gs, bs));
        int scale = (maxs - mins << 16) / (maxb - minb);
        int r = y + ((rd - y) * scale + 32768 >> 16);
        if (((r | (g = y + ((gd - y) * scale + 32768 >> 16)) | (b = y + ((bd - y) * scale + 32768 >> 16))) & 0x100) == 256) {
            int min = Math.min(r, Math.min(g, b));
            int max = Math.max(r, Math.max(g, b));
            int scalemin = min < 0 ? (y << 16) / (y - min) : 65536;
            int scalemax = max > 255 ? (255 - y << 16) / (max - y) : 65536;
            scale = Math.min(scalemin, scalemax);
            r = y + ((r - y) * scale + 32768 >> 16);
            g = y + ((g - y) * scale + 32768 >> 16);
            b = y + ((b - y) * scale + 32768 >> 16);
        }
        result[0] = (float)r / 255.0f;
        result[1] = (float)g / 255.0f;
        result[2] = (float)b / 255.0f;
    }

    private static void getLuminosityRGB(float[] srcValues, float[] dstValues, float[] result) {
        int b;
        int g;
        int bs;
        int gs;
        int rd = BlendMode.get255Value(dstValues[0]);
        int gd = BlendMode.get255Value(dstValues[1]);
        int bd = BlendMode.get255Value(dstValues[2]);
        int rs = BlendMode.get255Value(srcValues[0]);
        int delta = (rs - rd) * 77 + ((gs = BlendMode.get255Value(srcValues[1])) - gd) * 151 + ((bs = BlendMode.get255Value(srcValues[2])) - bd) * 28 + 128 >> 8;
        int r = rd + delta;
        if (((r | (g = gd + delta) | (b = bd + delta)) & 0x100) == 256) {
            int min;
            int max;
            int y = rs * 77 + gs * 151 + bs * 28 + 128 >> 8;
            int scale = delta > 0 ? ((max = Math.max(r, Math.max(g, b))) == y ? 0 : (255 - y << 16) / (max - y)) : (y == (min = Math.min(r, Math.min(g, b))) ? 0 : (y << 16) / (y - min));
            r = y + ((r - y) * scale + 32768 >> 16);
            g = y + ((g - y) * scale + 32768 >> 16);
            b = y + ((b - y) * scale + 32768 >> 16);
        }
        result[0] = (float)r / 255.0f;
        result[1] = (float)g / 255.0f;
        result[2] = (float)b / 255.0f;
    }

    private static Map<COSName, BlendMode> createBlendModeMap() {
        HashMap<COSName, BlendMode> map = new HashMap<COSName, BlendMode>(13);
        map.put(COSName.NORMAL, NORMAL);
        map.put(COSName.COMPATIBLE, NORMAL);
        map.put(COSName.MULTIPLY, MULTIPLY);
        map.put(COSName.SCREEN, SCREEN);
        map.put(COSName.OVERLAY, OVERLAY);
        map.put(COSName.DARKEN, DARKEN);
        map.put(COSName.LIGHTEN, LIGHTEN);
        map.put(COSName.COLOR_DODGE, COLOR_DODGE);
        map.put(COSName.COLOR_BURN, COLOR_BURN);
        map.put(COSName.HARD_LIGHT, HARD_LIGHT);
        map.put(COSName.SOFT_LIGHT, SOFT_LIGHT);
        map.put(COSName.DIFFERENCE, DIFFERENCE);
        map.put(COSName.EXCLUSION, EXCLUSION);
        map.put(COSName.HUE, HUE);
        map.put(COSName.SATURATION, SATURATION);
        map.put(COSName.LUMINOSITY, LUMINOSITY);
        map.put(COSName.COLOR, COLOR);
        return map;
    }

    private static Map<BlendMode, COSName> createBlendModeNamesMap() {
        HashMap<BlendMode, COSName> map = new HashMap<BlendMode, COSName>(13);
        map.put(NORMAL, COSName.NORMAL);
        map.put(COMPATIBLE, COSName.NORMAL);
        map.put(MULTIPLY, COSName.MULTIPLY);
        map.put(SCREEN, COSName.SCREEN);
        map.put(OVERLAY, COSName.OVERLAY);
        map.put(DARKEN, COSName.DARKEN);
        map.put(LIGHTEN, COSName.LIGHTEN);
        map.put(COLOR_DODGE, COSName.COLOR_DODGE);
        map.put(COLOR_BURN, COSName.COLOR_BURN);
        map.put(HARD_LIGHT, COSName.HARD_LIGHT);
        map.put(SOFT_LIGHT, COSName.SOFT_LIGHT);
        map.put(DIFFERENCE, COSName.DIFFERENCE);
        map.put(EXCLUSION, COSName.EXCLUSION);
        map.put(HUE, COSName.HUE);
        map.put(SATURATION, COSName.SATURATION);
        map.put(LUMINOSITY, COSName.LUMINOSITY);
        map.put(COLOR, COSName.COLOR);
        return map;
    }

    static {
        COMPATIBLE = NORMAL = new SeparableBlendMode(){

            @Override
            public float blendChannel(float srcValue, float dstValue) {
                return srcValue;
            }
        };
        MULTIPLY = new SeparableBlendMode(){

            @Override
            public float blendChannel(float srcValue, float dstValue) {
                return srcValue * dstValue;
            }
        };
        SCREEN = new SeparableBlendMode(){

            @Override
            public float blendChannel(float srcValue, float dstValue) {
                return srcValue + dstValue - srcValue * dstValue;
            }
        };
        OVERLAY = new SeparableBlendMode(){

            @Override
            public float blendChannel(float srcValue, float dstValue) {
                return (double)dstValue <= 0.5 ? 2.0f * dstValue * srcValue : 2.0f * (srcValue + dstValue - srcValue * dstValue) - 1.0f;
            }
        };
        DARKEN = new SeparableBlendMode(){

            @Override
            public float blendChannel(float srcValue, float dstValue) {
                return Math.min(srcValue, dstValue);
            }
        };
        LIGHTEN = new SeparableBlendMode(){

            @Override
            public float blendChannel(float srcValue, float dstValue) {
                return Math.max(srcValue, dstValue);
            }
        };
        COLOR_DODGE = new SeparableBlendMode(){

            @Override
            public float blendChannel(float srcValue, float dstValue) {
                if (dstValue == 0.0f) {
                    return 0.0f;
                }
                if (dstValue >= 1.0f - srcValue) {
                    return 1.0f;
                }
                return dstValue / (1.0f - srcValue);
            }
        };
        COLOR_BURN = new SeparableBlendMode(){

            @Override
            public float blendChannel(float srcValue, float dstValue) {
                if (dstValue == 1.0f) {
                    return 1.0f;
                }
                if (1.0f - dstValue >= srcValue) {
                    return 0.0f;
                }
                return 1.0f - (1.0f - dstValue) / srcValue;
            }
        };
        HARD_LIGHT = new SeparableBlendMode(){

            @Override
            public float blendChannel(float srcValue, float dstValue) {
                return (double)srcValue <= 0.5 ? 2.0f * dstValue * srcValue : 2.0f * (srcValue + dstValue - srcValue * dstValue) - 1.0f;
            }
        };
        SOFT_LIGHT = new SeparableBlendMode(){

            @Override
            public float blendChannel(float srcValue, float dstValue) {
                if ((double)srcValue <= 0.5) {
                    return dstValue - (1.0f - 2.0f * srcValue) * dstValue * (1.0f - dstValue);
                }
                float d = (double)dstValue <= 0.25 ? ((16.0f * dstValue - 12.0f) * dstValue + 4.0f) * dstValue : (float)Math.sqrt(dstValue);
                return dstValue + (2.0f * srcValue - 1.0f) * (d - dstValue);
            }
        };
        DIFFERENCE = new SeparableBlendMode(){

            @Override
            public float blendChannel(float srcValue, float dstValue) {
                return Math.abs(dstValue - srcValue);
            }
        };
        EXCLUSION = new SeparableBlendMode(){

            @Override
            public float blendChannel(float srcValue, float dstValue) {
                return dstValue + srcValue - 2.0f * dstValue * srcValue;
            }
        };
        HUE = new NonSeparableBlendMode(){

            @Override
            public void blend(float[] srcValues, float[] dstValues, float[] result) {
                float[] temp = new float[3];
                BlendMode.getSaturationRGB(dstValues, srcValues, temp);
                BlendMode.getLuminosityRGB(dstValues, temp, result);
            }
        };
        SATURATION = new NonSeparableBlendMode(){

            @Override
            public void blend(float[] srcValues, float[] dstValues, float[] result) {
                BlendMode.getSaturationRGB(srcValues, dstValues, result);
            }
        };
        COLOR = new NonSeparableBlendMode(){

            @Override
            public void blend(float[] srcValues, float[] dstValues, float[] result) {
                BlendMode.getLuminosityRGB(dstValues, srcValues, result);
            }
        };
        LUMINOSITY = new NonSeparableBlendMode(){

            @Override
            public void blend(float[] srcValues, float[] dstValues, float[] result) {
                BlendMode.getLuminosityRGB(srcValues, dstValues, result);
            }
        };
        BLEND_MODES = BlendMode.createBlendModeMap();
        BLEND_MODE_NAMES = BlendMode.createBlendModeNamesMap();
    }
}


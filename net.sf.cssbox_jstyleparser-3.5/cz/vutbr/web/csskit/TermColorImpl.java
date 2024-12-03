/*
 * Decompiled with CFR 0.152.
 */
package cz.vutbr.web.csskit;

import cz.vutbr.web.css.CSSFactory;
import cz.vutbr.web.css.Term;
import cz.vutbr.web.css.TermColor;
import cz.vutbr.web.css.TermFunction;
import cz.vutbr.web.css.TermIdent;
import cz.vutbr.web.css.TermInteger;
import cz.vutbr.web.css.TermNumber;
import cz.vutbr.web.css.TermPercent;
import cz.vutbr.web.csskit.Color;
import cz.vutbr.web.csskit.ColorCard;
import cz.vutbr.web.csskit.TermColorKeywordImpl;
import cz.vutbr.web.csskit.TermImpl;
import java.util.List;

public class TermColorImpl
extends TermImpl<Color>
implements TermColor {
    protected static final String COLOR_RGB_NAME = "rgb";
    protected static final String COLOR_RGBA_NAME = "rgba";
    protected static final String COLOR_HSL_NAME = "hsl";
    protected static final String COLOR_HSLA_NAME = "hsla";
    protected static final int COLOR_PARAMS_COUNT = 3;
    protected static final int MAX_VALUE = 255;
    protected static final int MIN_VALUE = 0;
    protected static final int PERCENT_CONVERSION = 100;
    protected static final int MAX_HUE = 360;

    protected TermColorImpl(int r, int g, int b) {
        this.value = new Color(r, g, b);
    }

    protected TermColorImpl(int r, int g, int b, int a) {
        this.value = new Color(r, g, b, a);
    }

    protected TermColorImpl(Color value) {
        this.value = value;
    }

    @Override
    public TermColor.Keyword getKeyword() {
        return TermColor.Keyword.none;
    }

    @Override
    public boolean isTransparent() {
        return ((Color)this.value).getAlpha() == 0;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        if (this.operator != null) {
            sb.append(this.operator.value());
        }
        if (((Color)this.value).getAlpha() == 255) {
            String s = Integer.toHexString(((Color)this.value).getRGB() & 0xFFFFFF);
            if (s.length() < 6) {
                s = "000000".substring(0, 6 - s.length()) + s;
            }
            sb.append("#").append(s);
        } else {
            sb.append("rgba(");
            sb.append(((Color)this.value).getRed());
            sb.append(',');
            sb.append(((Color)this.value).getGreen());
            sb.append(',');
            sb.append(((Color)this.value).getBlue());
            sb.append(',');
            sb.append((double)Math.round((double)((Color)this.value).getAlpha() / 2.55) / 100.0);
            sb.append(")");
        }
        return sb.toString();
    }

    public static TermColor getColorByIdent(TermIdent ident) {
        TermColor c = ColorCard.getTermColor((String)ident.getValue());
        if (c == null) {
            return null;
        }
        if (c instanceof TermColorKeywordImpl) {
            return new TermColorKeywordImpl(c.getKeyword(), (Color)c.getValue());
        }
        return new TermColorImpl((Color)c.getValue());
    }

    public static TermColor getColorByHash(String hash) {
        if (hash == null) {
            throw new IllegalArgumentException("Invalid hash value (null) for color construction");
        }
        if ((hash = hash.toLowerCase().replaceAll("^#", "")).matches("^[0-9a-f]{3}$")) {
            String r = hash.substring(0, 1);
            String g = hash.substring(1, 2);
            String b = hash.substring(2, 3);
            return new TermColorImpl(Integer.parseInt(r + r, 16), Integer.parseInt(g + g, 16), Integer.parseInt(b + b, 16));
        }
        if (hash.matches("^[0-9a-f]{6}$")) {
            String r = hash.substring(0, 2);
            String g = hash.substring(2, 4);
            String b = hash.substring(4, 6);
            return new TermColorImpl(Integer.parseInt(r, 16), Integer.parseInt(g, 16), Integer.parseInt(b, 16));
        }
        return null;
    }

    public static TermColor getColorByFunction(TermFunction func) {
        List<Term<?>> args = func.getSeparatedValues(CSSFactory.getTermFactory().createOperator(','), false);
        if (args != null) {
            if (COLOR_RGB_NAME.equals(func.getFunctionName()) && args.size() == 3 || COLOR_RGBA_NAME.equals(func.getFunctionName()) && args.size() == 4) {
                Term<?> term;
                int i;
                boolean percVals = false;
                boolean intVals = false;
                int[] rgb = new int[3];
                for (i = 0; i < 3; ++i) {
                    term = args.get(i);
                    if (term instanceof TermInteger) {
                        rgb[i] = ((TermInteger)term).getIntValue();
                        intVals = true;
                        continue;
                    }
                    if (term instanceof TermPercent) {
                        int value = ((Float)((TermPercent)term).getValue()).intValue();
                        rgb[i] = value * 255 / 100;
                        percVals = true;
                        continue;
                    }
                    return null;
                }
                if (percVals && intVals) {
                    return null;
                }
                for (i = 0; i < rgb.length; ++i) {
                    if (rgb[i] < 0) {
                        rgb[i] = 0;
                    }
                    if (rgb[i] <= 255) continue;
                    rgb[i] = 255;
                }
                int a = 255;
                if (args.size() > 3) {
                    term = args.get(3);
                    if (term instanceof TermNumber || term instanceof TermInteger) {
                        float alpha = TermColorImpl.getFloatValue(term);
                        a = Math.round(alpha * 255.0f);
                        if (a < 0) {
                            a = 0;
                        }
                        if (a > 255) {
                            a = 255;
                        }
                    } else {
                        return null;
                    }
                }
                return new TermColorImpl(rgb[0], rgb[1], rgb[2], a);
            }
            if (COLOR_HSL_NAME.equals(func.getFunctionName()) && args.size() == 3 || COLOR_HSLA_NAME.equals(func.getFunctionName()) && args.size() == 4) {
                int il;
                int is;
                float h;
                Term<?> hterm = args.get(0);
                if (hterm instanceof TermNumber || hterm instanceof TermInteger) {
                    for (h = TermColorImpl.getFloatValue(hterm); h >= 360.0f; h -= 360.0f) {
                    }
                    while (h < 0.0f) {
                        h += 360.0f;
                    }
                    h /= 360.0f;
                } else {
                    return null;
                }
                Term<?> sterm = args.get(1);
                if (sterm instanceof TermPercent) {
                    is = ((Float)((TermPercent)sterm).getValue()).intValue();
                    if (is > 100) {
                        is = 100;
                    } else if (is < 0) {
                        is = 0;
                    }
                } else {
                    return null;
                }
                float s = (float)is / 100.0f;
                Term<?> lterm = args.get(2);
                if (lterm instanceof TermPercent) {
                    il = ((Float)((TermPercent)lterm).getValue()).intValue();
                    if (il > 100) {
                        il = 100;
                    } else if (il < 0) {
                        il = 0;
                    }
                } else {
                    return null;
                }
                float l = (float)il / 100.0f;
                int[] rgb = TermColorImpl.hslToRgb(h, s, l);
                int a = 255;
                if (args.size() > 3) {
                    Term<?> term = args.get(3);
                    if (term instanceof TermNumber || term instanceof TermInteger) {
                        float alpha = TermColorImpl.getFloatValue(term);
                        a = Math.round(alpha * 255.0f);
                        if (a < 0) {
                            a = 0;
                        }
                        if (a > 255) {
                            a = 255;
                        }
                    } else {
                        return null;
                    }
                }
                return new TermColorImpl(rgb[0], rgb[1], rgb[2], a);
            }
            return null;
        }
        return null;
    }

    private static float getFloatValue(Term<?> term) {
        if (term instanceof TermNumber) {
            return ((Float)((TermNumber)term).getValue()).floatValue();
        }
        if (term instanceof TermInteger) {
            return ((Float)((TermInteger)term).getValue()).floatValue();
        }
        return 0.0f;
    }

    private static int[] hslToRgb(float h, float s, float l) {
        int[] ret = new int[3];
        float m2 = l <= 0.5f ? l * (s + 1.0f) : l + s - l * s;
        float m1 = l * 2.0f - m2;
        ret[0] = Math.round(TermColorImpl.hueToRgb(m1, m2, h + 0.33333334f) * 255.0f);
        ret[1] = Math.round(TermColorImpl.hueToRgb(m1, m2, h) * 255.0f);
        ret[2] = Math.round(TermColorImpl.hueToRgb(m1, m2, h - 0.33333334f) * 255.0f);
        return ret;
    }

    private static float hueToRgb(float m1, float m2, float h) {
        if (h < 0.0f) {
            h += 1.0f;
        }
        if (h > 1.0f) {
            h -= 1.0f;
        }
        if (h * 6.0f < 1.0f) {
            return m1 + (m2 - m1) * h * 6.0f;
        }
        if (h * 2.0f < 1.0f) {
            return m2;
        }
        if (h * 3.0f < 2.0f) {
            return m1 + (m2 - m1) * (0.6666667f - h) * 6.0f;
        }
        return m1;
    }
}


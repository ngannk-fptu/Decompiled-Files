/*
 * Decompiled with CFR 0.152.
 */
package org.xhtmlrenderer.css.style.derived;

import java.util.logging.Level;
import org.xhtmlrenderer.css.constants.CSSName;
import org.xhtmlrenderer.css.constants.ValueConstants;
import org.xhtmlrenderer.css.parser.PropertyValue;
import org.xhtmlrenderer.css.style.CalculatedStyle;
import org.xhtmlrenderer.css.style.CssContext;
import org.xhtmlrenderer.css.style.DerivedValue;
import org.xhtmlrenderer.css.value.FontSpecification;
import org.xhtmlrenderer.util.XRLog;

public class LengthValue
extends DerivedValue {
    private static final int MM__PER__CM = 10;
    private static final float CM__PER__IN = 2.54f;
    private static final float PT__PER__IN = 0.013888889f;
    private static final float PC__PER__PT = 12.0f;
    private float _lengthAsFloat;
    private CalculatedStyle _style;
    private short _lengthPrimitiveType;

    public LengthValue(CalculatedStyle style, CSSName name, PropertyValue value) {
        super(name, value.getPrimitiveType(), value.getCssText(), value.getCssText());
        this._style = style;
        this._lengthAsFloat = value.getFloatValue();
        this._lengthPrimitiveType = value.getPrimitiveType();
    }

    @Override
    public float asFloat() {
        return this._lengthAsFloat;
    }

    @Override
    public float getFloatProportionalTo(CSSName cssName, float baseValue, CssContext ctx) {
        return LengthValue.calcFloatProportionalValue(this.getStyle(), cssName, this.getStringValue(), this._lengthAsFloat, this._lengthPrimitiveType, baseValue, ctx);
    }

    @Override
    public boolean hasAbsoluteUnit() {
        return ValueConstants.isAbsoluteUnit(this.getCssSacUnitType());
    }

    @Override
    public boolean isDependentOnFontSize() {
        return this._lengthPrimitiveType == 4 || this._lengthPrimitiveType == 3;
    }

    public static float calcFloatProportionalValue(CalculatedStyle style, CSSName cssName, String stringValue, float relVal, short primitiveType, float baseValue, CssContext ctx) {
        float absVal = Float.MIN_VALUE;
        switch (primitiveType) {
            case 5: {
                absVal = relVal * (float)ctx.getDotsPerPixel();
                break;
            }
            case 8: {
                absVal = relVal * 2.54f * 10.0f / ctx.getMmPerDot();
                break;
            }
            case 6: {
                absVal = relVal * 10.0f / ctx.getMmPerDot();
                break;
            }
            case 7: {
                absVal = relVal / ctx.getMmPerDot();
                break;
            }
            case 9: {
                absVal = relVal * 0.013888889f * 2.54f * 10.0f / ctx.getMmPerDot();
                break;
            }
            case 10: {
                absVal = relVal * 12.0f * 0.013888889f * 2.54f * 10.0f / ctx.getMmPerDot();
                break;
            }
            case 3: {
                if (cssName == CSSName.FONT_SIZE) {
                    FontSpecification parentFont = style.getParent().getFont(ctx);
                    absVal = relVal * parentFont.size;
                    break;
                }
                absVal = relVal * style.getFont((CssContext)ctx).size;
                break;
            }
            case 4: {
                float xHeight;
                if (cssName == CSSName.FONT_SIZE) {
                    FontSpecification parentFont = style.getParent().getFont(ctx);
                    xHeight = ctx.getXHeight(parentFont);
                } else {
                    FontSpecification font = style.getFont(ctx);
                    xHeight = ctx.getXHeight(font);
                }
                absVal = relVal * xHeight;
                break;
            }
            case 2: {
                if (cssName == CSSName.VERTICAL_ALIGN) {
                    baseValue = style.getParent().getLineHeight(ctx);
                } else if (cssName == CSSName.FONT_SIZE) {
                    FontSpecification parentFont = style.getParent().getFont(ctx);
                    baseValue = ctx.getFontSize2D(parentFont);
                } else if (cssName == CSSName.LINE_HEIGHT) {
                    FontSpecification font = style.getFont(ctx);
                    baseValue = ctx.getFontSize2D(font);
                }
                absVal = relVal / 100.0f * baseValue;
                break;
            }
            case 1: {
                absVal = relVal;
                break;
            }
            default: {
                XRLog.cascade(Level.SEVERE, "Asked to convert " + cssName + " from relative to absolute,  don't recognize the datatype '" + ValueConstants.stringForSACPrimitiveType(primitiveType) + "' " + primitiveType + "(" + stringValue + ")");
            }
        }
        if (XRLog.isLoggingEnabled()) {
            if (cssName == CSSName.FONT_SIZE) {
                XRLog.cascade(Level.FINEST, cssName + ", relative= " + relVal + " (" + stringValue + "), absolute= " + absVal);
            } else {
                XRLog.cascade(Level.FINEST, cssName + ", relative= " + relVal + " (" + stringValue + "), absolute= " + absVal + " using base=" + baseValue);
            }
        }
        double d = Math.round((double)absVal);
        absVal = new Float(d).floatValue();
        return absVal;
    }

    private CalculatedStyle getStyle() {
        return this._style;
    }
}


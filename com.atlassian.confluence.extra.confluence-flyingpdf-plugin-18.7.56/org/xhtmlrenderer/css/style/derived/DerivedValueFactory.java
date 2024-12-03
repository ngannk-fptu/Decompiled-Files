/*
 * Decompiled with CFR 0.152.
 */
package org.xhtmlrenderer.css.style.derived;

import java.util.HashMap;
import java.util.Map;
import org.xhtmlrenderer.css.constants.CSSName;
import org.xhtmlrenderer.css.constants.IdentValue;
import org.xhtmlrenderer.css.parser.PropertyValue;
import org.xhtmlrenderer.css.style.CalculatedStyle;
import org.xhtmlrenderer.css.style.FSDerivedValue;
import org.xhtmlrenderer.css.style.derived.ColorValue;
import org.xhtmlrenderer.css.style.derived.FunctionValue;
import org.xhtmlrenderer.css.style.derived.LengthValue;
import org.xhtmlrenderer.css.style.derived.ListValue;
import org.xhtmlrenderer.css.style.derived.NumberValue;
import org.xhtmlrenderer.css.style.derived.StringValue;

public class DerivedValueFactory {
    private static final Map CACHED_COLORS = new HashMap();

    public static FSDerivedValue newDerivedValue(CalculatedStyle style, CSSName cssName, PropertyValue value) {
        if (value.getCssValueType() == 0) {
            return style.getParent().valueByName(cssName);
        }
        switch (value.getPropertyValueType()) {
            case 2: {
                return new LengthValue(style, cssName, value);
            }
            case 4: {
                IdentValue ident = value.getIdentValue();
                if (ident == null) {
                    ident = IdentValue.getByIdentString(value.getStringValue());
                }
                return ident;
            }
            case 5: {
                return new StringValue(cssName, value);
            }
            case 1: {
                return new NumberValue(cssName, value);
            }
            case 3: {
                FSDerivedValue color = (FSDerivedValue)CACHED_COLORS.get(value.getCssText());
                if (color == null) {
                    color = new ColorValue(cssName, value);
                    CACHED_COLORS.put(value.getCssText(), color);
                }
                return color;
            }
            case 6: {
                return new ListValue(cssName, value);
            }
            case 7: {
                return new FunctionValue(cssName, value);
            }
        }
        throw new IllegalArgumentException();
    }
}


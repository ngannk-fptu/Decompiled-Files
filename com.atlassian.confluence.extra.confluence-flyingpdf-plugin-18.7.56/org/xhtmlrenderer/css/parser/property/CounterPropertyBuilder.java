/*
 * Decompiled with CFR 0.152.
 */
package org.xhtmlrenderer.css.parser.property;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.w3c.dom.css.CSSPrimitiveValue;
import org.xhtmlrenderer.css.constants.CSSName;
import org.xhtmlrenderer.css.parser.CSSParseException;
import org.xhtmlrenderer.css.parser.CounterData;
import org.xhtmlrenderer.css.parser.PropertyValue;
import org.xhtmlrenderer.css.parser.property.AbstractPropertyBuilder;
import org.xhtmlrenderer.css.sheet.PropertyDeclaration;

public abstract class CounterPropertyBuilder
extends AbstractPropertyBuilder {
    protected abstract int getDefaultValue();

    @Override
    public List buildDeclarations(CSSName cssName, List values, int origin, boolean important, boolean inheritAllowed) {
        if (values.size() == 1) {
            PropertyValue value = (PropertyValue)values.get(0);
            this.checkInheritAllowed(value, inheritAllowed);
            if (value.getCssValueType() == 0) {
                return Collections.singletonList(new PropertyDeclaration(cssName, value, important, origin));
            }
            if (value.getPrimitiveType() == 21) {
                if (value.getCssText().equals("none")) {
                    return Collections.singletonList(new PropertyDeclaration(cssName, value, important, origin));
                }
                CounterData data = new CounterData(value.getStringValue(), this.getDefaultValue());
                return Collections.singletonList(new PropertyDeclaration(cssName, new PropertyValue(Collections.singletonList(data)), important, origin));
            }
            throw new CSSParseException("The syntax of the " + cssName + " property is invalid", -1);
        }
        ArrayList<CounterData> result = new ArrayList<CounterData>();
        for (int i = 0; i < values.size(); ++i) {
            int cValue;
            String name;
            PropertyValue value = (PropertyValue)values.get(i);
            if (value.getPrimitiveType() == 21) {
                name = value.getStringValue();
                cValue = this.getDefaultValue();
                if (i < values.size() - 1) {
                    PropertyValue next = (PropertyValue)values.get(i + 1);
                    if (next.getPrimitiveType() == 1) {
                        this.checkNumberIsInteger(cssName, next);
                        cValue = (int)next.getFloatValue();
                    }
                    ++i;
                }
            } else {
                throw new CSSParseException("The syntax of the " + cssName + " property is invalid", -1);
            }
            result.add(new CounterData(name, cValue));
        }
        return Collections.singletonList(new PropertyDeclaration(cssName, new PropertyValue(result), important, origin));
    }

    private void checkNumberIsInteger(CSSName cssName, CSSPrimitiveValue value) {
        if ((int)value.getFloatValue((short)1) != Math.round(value.getFloatValue((short)1))) {
            throw new CSSParseException("The value " + value.getFloatValue((short)1) + " in " + cssName + " must be an integer", -1);
        }
    }

    public static class CounterIncrement
    extends CounterPropertyBuilder {
        @Override
        protected int getDefaultValue() {
            return 1;
        }
    }

    public static class CounterReset
    extends CounterPropertyBuilder {
        @Override
        protected int getDefaultValue() {
            return 0;
        }
    }
}


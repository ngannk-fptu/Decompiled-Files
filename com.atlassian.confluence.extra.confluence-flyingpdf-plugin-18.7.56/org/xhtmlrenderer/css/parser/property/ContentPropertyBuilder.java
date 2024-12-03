/*
 * Decompiled with CFR 0.152.
 */
package org.xhtmlrenderer.css.parser.property;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.xhtmlrenderer.css.constants.CSSName;
import org.xhtmlrenderer.css.constants.IdentValue;
import org.xhtmlrenderer.css.parser.CSSParseException;
import org.xhtmlrenderer.css.parser.FSFunction;
import org.xhtmlrenderer.css.parser.PropertyValue;
import org.xhtmlrenderer.css.parser.property.AbstractPropertyBuilder;
import org.xhtmlrenderer.css.sheet.PropertyDeclaration;

public class ContentPropertyBuilder
extends AbstractPropertyBuilder {
    @Override
    public List buildDeclarations(CSSName cssName, List values, int origin, boolean important, boolean inheritAllowed) {
        if (values.size() == 1) {
            IdentValue ident;
            PropertyValue value = (PropertyValue)values.get(0);
            if (value.getCssValueType() == 0) {
                return Collections.EMPTY_LIST;
            }
            if (value.getPrimitiveType() == 21 && ((ident = this.checkIdent(CSSName.CONTENT, value)) == IdentValue.NONE || ident == IdentValue.NORMAL)) {
                return Collections.singletonList(new PropertyDeclaration(CSSName.CONTENT, value, important, origin));
            }
        }
        ArrayList<PropertyValue> resultValues = new ArrayList<PropertyValue>();
        for (PropertyValue value : values) {
            if (value.getOperator() != null) {
                throw new CSSParseException("Found unexpected operator, " + value.getOperator().getExternalName(), -1);
            }
            short type = value.getPrimitiveType();
            if (type == 20) continue;
            if (type == 19) {
                resultValues.add(value);
                continue;
            }
            if (value.getPropertyValueType() == 7) {
                if (!this.isFunctionAllowed(value.getFunction())) {
                    throw new CSSParseException("Function " + value.getFunction().getName() + " is not allowed here", -1);
                }
                resultValues.add(value);
                continue;
            }
            if (type == 21) {
                IdentValue ident = this.checkIdent(CSSName.CONTENT, value);
                if (ident == IdentValue.OPEN_QUOTE || ident == IdentValue.CLOSE_QUOTE || ident == IdentValue.NO_CLOSE_QUOTE || ident == IdentValue.NO_OPEN_QUOTE) {
                    resultValues.add(value);
                    continue;
                }
                throw new CSSParseException("Identifier " + ident + " is not a valid value for the content property", -1);
            }
            throw new CSSParseException(value.getCssText() + " is not a value value for the content property", -1);
        }
        if (resultValues.size() > 0) {
            return Collections.singletonList(new PropertyDeclaration(CSSName.CONTENT, new PropertyValue(resultValues), important, origin));
        }
        return Collections.EMPTY_LIST;
    }

    private boolean isFunctionAllowed(FSFunction function) {
        String name = function.getName();
        return name.equals("attr") || name.equals("counter") || name.equals("counters") || name.equals("element") || name.startsWith("-fs") || name.equals("target-counter") || name.equals("leader");
    }
}


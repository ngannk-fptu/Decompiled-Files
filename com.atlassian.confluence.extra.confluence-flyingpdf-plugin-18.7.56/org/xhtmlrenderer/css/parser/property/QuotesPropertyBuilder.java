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
import org.xhtmlrenderer.css.parser.PropertyValue;
import org.xhtmlrenderer.css.parser.property.AbstractPropertyBuilder;
import org.xhtmlrenderer.css.sheet.PropertyDeclaration;

public class QuotesPropertyBuilder
extends AbstractPropertyBuilder {
    @Override
    public List buildDeclarations(CSSName cssName, List values, int origin, boolean important, boolean inheritAllowed) {
        if (values.size() == 1) {
            IdentValue ident;
            PropertyValue value = (PropertyValue)values.get(0);
            if (value.getCssValueType() == 0) {
                return Collections.EMPTY_LIST;
            }
            if (value.getPrimitiveType() == 21 && (ident = this.checkIdent(CSSName.QUOTES, value)) == IdentValue.NONE) {
                return Collections.singletonList(new PropertyDeclaration(CSSName.QUOTES, value, important, origin));
            }
        }
        if (values.size() % 2 == 1) {
            throw new CSSParseException("Mismatched quotes " + values, -1);
        }
        ArrayList<String> resultValues = new ArrayList<String>();
        for (PropertyValue value : values) {
            if (value.getOperator() != null) {
                throw new CSSParseException("Found unexpected operator, " + value.getOperator().getExternalName(), -1);
            }
            short type = value.getPrimitiveType();
            if (type == 19) {
                resultValues.add(value.getStringValue());
                continue;
            }
            if (type == 20) {
                throw new CSSParseException("URI is not allowed here", -1);
            }
            if (value.getPropertyValueType() == 7) {
                throw new CSSParseException("Function " + value.getFunction().getName() + " is not allowed here", -1);
            }
            if (type == 21) {
                throw new CSSParseException("Identifier is not a valid value for the quotes property", -1);
            }
            throw new CSSParseException(value.getCssText() + " is not a value value for the quotes property", -1);
        }
        if (resultValues.size() > 0) {
            return Collections.singletonList(new PropertyDeclaration(CSSName.QUOTES, new PropertyValue(resultValues), important, origin));
        }
        return Collections.EMPTY_LIST;
    }
}


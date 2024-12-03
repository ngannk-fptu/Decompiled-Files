/*
 * Decompiled with CFR 0.152.
 */
package org.xhtmlrenderer.css.parser.property;

import java.util.ArrayList;
import java.util.List;
import org.xhtmlrenderer.css.constants.CSSName;
import org.xhtmlrenderer.css.parser.CSSParseException;
import org.xhtmlrenderer.css.parser.PropertyValue;
import org.xhtmlrenderer.css.parser.property.AbstractPropertyBuilder;
import org.xhtmlrenderer.css.sheet.PropertyDeclaration;

public class BorderSpacingPropertyBuilder
extends AbstractPropertyBuilder {
    private static final CSSName[] ALL = new CSSName[]{CSSName.FS_BORDER_SPACING_HORIZONTAL, CSSName.FS_BORDER_SPACING_VERTICAL};

    @Override
    public List buildDeclarations(CSSName cssName, List values, int origin, boolean important, boolean inheritAllowed) {
        ArrayList<PropertyDeclaration> result = this.checkInheritAll(ALL, values, origin, important, inheritAllowed);
        if (result != null) {
            return result;
        }
        this.checkValueCount(CSSName.BORDER_SPACING, 1, 2, values.size());
        PropertyDeclaration horizontalSpacing = null;
        PropertyDeclaration verticalSpacing = null;
        if (values.size() == 1) {
            PropertyValue value = (PropertyValue)values.get(0);
            this.checkLengthType(cssName, value);
            if (value.getFloatValue() < 0.0f) {
                throw new CSSParseException("border-spacing may not be negative", -1);
            }
            horizontalSpacing = new PropertyDeclaration(CSSName.FS_BORDER_SPACING_HORIZONTAL, value, important, origin);
            verticalSpacing = new PropertyDeclaration(CSSName.FS_BORDER_SPACING_VERTICAL, value, important, origin);
        } else {
            PropertyValue horizontal = (PropertyValue)values.get(0);
            this.checkLengthType(cssName, horizontal);
            if (horizontal.getFloatValue() < 0.0f) {
                throw new CSSParseException("border-spacing may not be negative", -1);
            }
            horizontalSpacing = new PropertyDeclaration(CSSName.FS_BORDER_SPACING_HORIZONTAL, horizontal, important, origin);
            PropertyValue vertical = (PropertyValue)values.get(1);
            this.checkLengthType(cssName, vertical);
            if (vertical.getFloatValue() < 0.0f) {
                throw new CSSParseException("border-spacing may not be negative", -1);
            }
            verticalSpacing = new PropertyDeclaration(CSSName.FS_BORDER_SPACING_VERTICAL, vertical, important, origin);
        }
        result = new ArrayList<PropertyDeclaration>(2);
        result.add(horizontalSpacing);
        result.add(verticalSpacing);
        return result;
    }
}


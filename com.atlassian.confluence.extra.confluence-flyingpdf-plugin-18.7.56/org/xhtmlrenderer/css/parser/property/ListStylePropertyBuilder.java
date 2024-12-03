/*
 * Decompiled with CFR 0.152.
 */
package org.xhtmlrenderer.css.parser.property;

import java.util.ArrayList;
import java.util.List;
import org.xhtmlrenderer.css.constants.CSSName;
import org.xhtmlrenderer.css.constants.IdentValue;
import org.xhtmlrenderer.css.parser.CSSParseException;
import org.xhtmlrenderer.css.parser.PropertyValue;
import org.xhtmlrenderer.css.parser.property.AbstractPropertyBuilder;
import org.xhtmlrenderer.css.parser.property.PrimitivePropertyBuilders;
import org.xhtmlrenderer.css.sheet.PropertyDeclaration;

public class ListStylePropertyBuilder
extends AbstractPropertyBuilder {
    private static final CSSName[] ALL = new CSSName[]{CSSName.LIST_STYLE_TYPE, CSSName.LIST_STYLE_POSITION, CSSName.LIST_STYLE_IMAGE};

    @Override
    public List buildDeclarations(CSSName cssName, List values, int origin, boolean important, boolean inheritAllowed) {
        ArrayList<PropertyDeclaration> result = this.checkInheritAll(ALL, values, origin, important, inheritAllowed);
        if (result != null) {
            return result;
        }
        PropertyDeclaration listStyleType = null;
        PropertyDeclaration listStylePosition = null;
        PropertyDeclaration listStyleImage = null;
        for (PropertyValue value : values) {
            this.checkInheritAllowed(value, false);
            short type = value.getPrimitiveType();
            if (type == 21) {
                IdentValue ident = this.checkIdent(CSSName.LIST_STYLE_SHORTHAND, value);
                if (ident == IdentValue.NONE) {
                    if (listStyleType == null) {
                        listStyleType = new PropertyDeclaration(CSSName.LIST_STYLE_TYPE, value, important, origin);
                    }
                    if (listStyleImage != null) continue;
                    listStyleImage = new PropertyDeclaration(CSSName.LIST_STYLE_IMAGE, value, important, origin);
                    continue;
                }
                if (PrimitivePropertyBuilders.LIST_STYLE_POSITIONS.get(ident.FS_ID)) {
                    if (listStylePosition != null) {
                        throw new CSSParseException("A list-style-position value cannot be set twice", -1);
                    }
                    listStylePosition = new PropertyDeclaration(CSSName.LIST_STYLE_POSITION, value, important, origin);
                    continue;
                }
                if (!PrimitivePropertyBuilders.LIST_STYLE_TYPES.get(ident.FS_ID)) continue;
                if (listStyleType != null) {
                    throw new CSSParseException("A list-style-type value cannot be set twice", -1);
                }
                listStyleType = new PropertyDeclaration(CSSName.LIST_STYLE_TYPE, value, important, origin);
                continue;
            }
            if (type != 20) continue;
            if (listStyleImage != null) {
                throw new CSSParseException("A list-style-image value cannot be set twice", -1);
            }
            listStyleImage = new PropertyDeclaration(CSSName.LIST_STYLE_IMAGE, value, important, origin);
        }
        result = new ArrayList<PropertyDeclaration>(3);
        if (listStyleType != null) {
            result.add(listStyleType);
        }
        if (listStylePosition != null) {
            result.add(listStylePosition);
        }
        if (listStyleImage != null) {
            result.add(listStyleImage);
        }
        return result;
    }
}


/*
 * Decompiled with CFR 0.152.
 */
package org.xhtmlrenderer.css.parser.property;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.ListIterator;
import org.xhtmlrenderer.css.constants.CSSName;
import org.xhtmlrenderer.css.constants.IdentValue;
import org.xhtmlrenderer.css.parser.CSSParseException;
import org.xhtmlrenderer.css.parser.PropertyValue;
import org.xhtmlrenderer.css.parser.Token;
import org.xhtmlrenderer.css.parser.property.AbstractPropertyBuilder;
import org.xhtmlrenderer.css.parser.property.Conversions;
import org.xhtmlrenderer.css.parser.property.PrimitivePropertyBuilders;
import org.xhtmlrenderer.css.parser.property.PropertyBuilder;
import org.xhtmlrenderer.css.sheet.PropertyDeclaration;

public class FontPropertyBuilder
extends AbstractPropertyBuilder {
    private static final CSSName[] ALL = new CSSName[]{CSSName.FONT_STYLE, CSSName.FONT_VARIANT, CSSName.FONT_WEIGHT, CSSName.FONT_SIZE, CSSName.LINE_HEIGHT, CSSName.FONT_FAMILY};

    @Override
    public List buildDeclarations(CSSName cssName, List values, int origin, boolean important, boolean inheritAllowed) {
        PropertyValue value;
        ArrayList<PropertyDeclaration> result = this.checkInheritAll(ALL, values, origin, important, inheritAllowed);
        if (result != null) {
            return result;
        }
        PropertyDeclaration fontStyle = null;
        PropertyDeclaration fontVariant = null;
        PropertyDeclaration fontWeight = null;
        PropertyDeclaration fontSize = null;
        PropertyDeclaration lineHeight = null;
        PropertyDeclaration fontFamily = null;
        boolean keepGoing = false;
        ListIterator i = values.listIterator();
        while (i.hasNext()) {
            value = (PropertyValue)i.next();
            short type = value.getPrimitiveType();
            if (type == 21) {
                String lowerCase = value.getStringValue().toLowerCase();
                IdentValue ident = this.checkIdent(cssName, value = new PropertyValue(21, lowerCase, lowerCase));
                if (ident == IdentValue.NORMAL) continue;
                if (PrimitivePropertyBuilders.FONT_STYLES.get(ident.FS_ID)) {
                    if (fontStyle != null) {
                        throw new CSSParseException("font-style cannot be set twice", -1);
                    }
                    fontStyle = new PropertyDeclaration(CSSName.FONT_STYLE, value, important, origin);
                    continue;
                }
                if (PrimitivePropertyBuilders.FONT_VARIANTS.get(ident.FS_ID)) {
                    if (fontVariant != null) {
                        throw new CSSParseException("font-variant cannot be set twice", -1);
                    }
                    fontVariant = new PropertyDeclaration(CSSName.FONT_VARIANT, value, important, origin);
                    continue;
                }
                if (PrimitivePropertyBuilders.FONT_WEIGHTS.get(ident.FS_ID)) {
                    if (fontWeight != null) {
                        throw new CSSParseException("font-weight cannot be set twice", -1);
                    }
                    fontWeight = new PropertyDeclaration(CSSName.FONT_WEIGHT, value, important, origin);
                    continue;
                }
                keepGoing = true;
                break;
            }
            if (type == 1 && value.getFloatValue() > 0.0f) {
                if (fontWeight != null) {
                    throw new CSSParseException("font-weight cannot be set twice", -1);
                }
                IdentValue weight = Conversions.getNumericFontWeight(value.getFloatValue());
                if (weight == null) {
                    throw new CSSParseException(value + " is not a valid font weight", -1);
                }
                PropertyValue replacement = new PropertyValue(21, weight.toString(), weight.toString());
                replacement.setIdentValue(weight);
                fontWeight = new PropertyDeclaration(CSSName.FONT_WEIGHT, replacement, important, origin);
                continue;
            }
            keepGoing = true;
            break;
        }
        if (keepGoing) {
            i.previous();
            value = (PropertyValue)i.next();
            if (value.getPrimitiveType() == 21) {
                String lowerCase = value.getStringValue().toLowerCase();
                value = new PropertyValue(21, lowerCase, lowerCase);
            }
            PropertyBuilder fontSizeBuilder = CSSName.getPropertyBuilder(CSSName.FONT_SIZE);
            List l = fontSizeBuilder.buildDeclarations(CSSName.FONT_SIZE, Collections.singletonList(value), origin, important);
            fontSize = (PropertyDeclaration)l.get(0);
            if (i.hasNext()) {
                value = (PropertyValue)i.next();
                if (value.getOperator() == Token.TK_VIRGULE) {
                    PropertyBuilder lineHeightBuilder = CSSName.getPropertyBuilder(CSSName.LINE_HEIGHT);
                    l = lineHeightBuilder.buildDeclarations(CSSName.LINE_HEIGHT, Collections.singletonList(value), origin, important);
                    lineHeight = (PropertyDeclaration)l.get(0);
                } else {
                    i.previous();
                }
            }
            if (i.hasNext()) {
                ArrayList families = new ArrayList();
                while (i.hasNext()) {
                    families.add(i.next());
                }
                PropertyBuilder fontFamilyBuilder = CSSName.getPropertyBuilder(CSSName.FONT_FAMILY);
                l = fontFamilyBuilder.buildDeclarations(CSSName.FONT_FAMILY, families, origin, important);
                fontFamily = (PropertyDeclaration)l.get(0);
            }
        }
        if (fontStyle == null) {
            fontStyle = new PropertyDeclaration(CSSName.FONT_STYLE, new PropertyValue(IdentValue.NORMAL), important, origin);
        }
        if (fontVariant == null) {
            fontVariant = new PropertyDeclaration(CSSName.FONT_VARIANT, new PropertyValue(IdentValue.NORMAL), important, origin);
        }
        if (fontWeight == null) {
            fontWeight = new PropertyDeclaration(CSSName.FONT_WEIGHT, new PropertyValue(IdentValue.NORMAL), important, origin);
        }
        if (fontSize == null) {
            throw new CSSParseException("A font-size value is required", -1);
        }
        if (lineHeight == null) {
            lineHeight = new PropertyDeclaration(CSSName.LINE_HEIGHT, new PropertyValue(IdentValue.NORMAL), important, origin);
        }
        result = new ArrayList<PropertyDeclaration>(ALL.length);
        result.add(fontStyle);
        result.add(fontVariant);
        result.add(fontWeight);
        result.add(fontSize);
        result.add(lineHeight);
        if (fontFamily != null) {
            result.add(fontFamily);
        }
        return result;
    }
}


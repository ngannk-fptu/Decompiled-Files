/*
 * Decompiled with CFR 0.152.
 */
package org.xhtmlrenderer.css.parser.property;

import java.util.ArrayList;
import java.util.List;
import org.xhtmlrenderer.css.constants.CSSName;
import org.xhtmlrenderer.css.constants.IdentValue;
import org.xhtmlrenderer.css.parser.CSSParseException;
import org.xhtmlrenderer.css.parser.FSRGBColor;
import org.xhtmlrenderer.css.parser.PropertyValue;
import org.xhtmlrenderer.css.parser.property.AbstractPropertyBuilder;
import org.xhtmlrenderer.css.parser.property.Conversions;
import org.xhtmlrenderer.css.parser.property.PrimitivePropertyBuilders;
import org.xhtmlrenderer.css.parser.property.PropertyBuilder;
import org.xhtmlrenderer.css.sheet.PropertyDeclaration;

public class BackgroundPropertyBuilder
extends AbstractPropertyBuilder {
    private static final CSSName[] ALL = new CSSName[]{CSSName.BACKGROUND_COLOR, CSSName.BACKGROUND_IMAGE, CSSName.BACKGROUND_REPEAT, CSSName.BACKGROUND_ATTACHMENT, CSSName.BACKGROUND_POSITION};

    private boolean isAppliesToBackgroundPosition(PropertyValue value) {
        short type = value.getPrimitiveType();
        if (this.isLength(value) || type == 2) {
            return true;
        }
        if (type != 21) {
            return false;
        }
        IdentValue ident = IdentValue.valueOf(value.getStringValue());
        return ident != null && PrimitivePropertyBuilders.BACKGROUND_POSITIONS.get(ident.FS_ID);
    }

    @Override
    public List buildDeclarations(CSSName cssName, List values, int origin, boolean important, boolean inheritAllowed) {
        ArrayList<PropertyDeclaration> result = this.checkInheritAll(ALL, values, origin, important, inheritAllowed);
        if (result != null) {
            return result;
        }
        PropertyDeclaration backgroundColor = null;
        PropertyDeclaration backgroundImage = null;
        PropertyDeclaration backgroundRepeat = null;
        PropertyDeclaration backgroundAttachment = null;
        PropertyDeclaration backgroundPosition = null;
        for (int i = 0; i < values.size(); ++i) {
            PropertyValue next;
            PropertyValue value = (PropertyValue)values.get(i);
            this.checkInheritAllowed(value, false);
            boolean processingBackgroundPosition = false;
            short type = value.getPrimitiveType();
            if (type == 21) {
                FSRGBColor color = Conversions.getColor(value.getStringValue());
                if (color != null) {
                    if (backgroundColor != null) {
                        throw new CSSParseException("A background-color value cannot be set twice", -1);
                    }
                    backgroundColor = new PropertyDeclaration(CSSName.BACKGROUND_COLOR, new PropertyValue(color), important, origin);
                    continue;
                }
                IdentValue ident = this.checkIdent(CSSName.BACKGROUND_SHORTHAND, value);
                if (PrimitivePropertyBuilders.BACKGROUND_REPEATS.get(ident.FS_ID)) {
                    if (backgroundRepeat != null) {
                        throw new CSSParseException("A background-repeat value cannot be set twice", -1);
                    }
                    backgroundRepeat = new PropertyDeclaration(CSSName.BACKGROUND_REPEAT, value, important, origin);
                }
                if (PrimitivePropertyBuilders.BACKGROUND_ATTACHMENTS.get(ident.FS_ID)) {
                    if (backgroundAttachment != null) {
                        throw new CSSParseException("A background-attachment value cannot be set twice", -1);
                    }
                    backgroundAttachment = new PropertyDeclaration(CSSName.BACKGROUND_ATTACHMENT, value, important, origin);
                }
                if (ident == IdentValue.TRANSPARENT) {
                    if (backgroundColor != null) {
                        throw new CSSParseException("A background-color value cannot be set twice", -1);
                    }
                    backgroundColor = new PropertyDeclaration(CSSName.BACKGROUND_COLOR, value, important, origin);
                }
                if (ident == IdentValue.NONE) {
                    if (backgroundImage != null) {
                        throw new CSSParseException("A background-image value cannot be set twice", -1);
                    }
                    backgroundImage = new PropertyDeclaration(CSSName.BACKGROUND_IMAGE, value, important, origin);
                }
                if (PrimitivePropertyBuilders.BACKGROUND_POSITIONS.get(ident.FS_ID)) {
                    processingBackgroundPosition = true;
                }
            } else if (type == 25) {
                if (backgroundColor != null) {
                    throw new CSSParseException("A background-color value cannot be set twice", -1);
                }
                backgroundColor = new PropertyDeclaration(CSSName.BACKGROUND_COLOR, value, important, origin);
            } else if (type == 20) {
                if (backgroundImage != null) {
                    throw new CSSParseException("A background-image value cannot be set twice", -1);
                }
                backgroundImage = new PropertyDeclaration(CSSName.BACKGROUND_IMAGE, value, important, origin);
            }
            if (!processingBackgroundPosition && !this.isLength(value) && type != 2) continue;
            if (backgroundPosition != null) {
                throw new CSSParseException("A background-position value cannot be set twice", -1);
            }
            ArrayList<PropertyValue> v = new ArrayList<PropertyValue>(2);
            v.add(value);
            if (i < values.size() - 1 && this.isAppliesToBackgroundPosition(next = (PropertyValue)values.get(i + 1))) {
                v.add(next);
                ++i;
            }
            PropertyBuilder builder = CSSName.getPropertyBuilder(CSSName.BACKGROUND_POSITION);
            backgroundPosition = (PropertyDeclaration)builder.buildDeclarations(CSSName.BACKGROUND_POSITION, v, origin, important).get(0);
        }
        if (backgroundColor == null) {
            backgroundColor = new PropertyDeclaration(CSSName.BACKGROUND_COLOR, new PropertyValue(IdentValue.TRANSPARENT), important, origin);
        }
        if (backgroundImage == null) {
            backgroundImage = new PropertyDeclaration(CSSName.BACKGROUND_IMAGE, new PropertyValue(IdentValue.NONE), important, origin);
        }
        if (backgroundRepeat == null) {
            backgroundRepeat = new PropertyDeclaration(CSSName.BACKGROUND_REPEAT, new PropertyValue(IdentValue.REPEAT), important, origin);
        }
        if (backgroundAttachment == null) {
            backgroundAttachment = new PropertyDeclaration(CSSName.BACKGROUND_ATTACHMENT, new PropertyValue(IdentValue.SCROLL), important, origin);
        }
        if (backgroundPosition == null) {
            ArrayList<PropertyValue> v = new ArrayList<PropertyValue>(2);
            v.add(new PropertyValue(2, 0.0f, "0%"));
            v.add(new PropertyValue(2, 0.0f, "0%"));
            backgroundPosition = new PropertyDeclaration(CSSName.BACKGROUND_POSITION, new PropertyValue(v), important, origin);
        }
        result = new ArrayList<PropertyDeclaration>(5);
        result.add(backgroundColor);
        result.add(backgroundImage);
        result.add(backgroundRepeat);
        result.add(backgroundAttachment);
        result.add(backgroundPosition);
        return result;
    }
}


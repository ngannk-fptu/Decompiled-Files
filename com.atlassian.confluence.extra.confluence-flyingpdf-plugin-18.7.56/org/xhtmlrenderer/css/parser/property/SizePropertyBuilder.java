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
import org.xhtmlrenderer.css.parser.property.PageSize;
import org.xhtmlrenderer.css.sheet.PropertyDeclaration;

public class SizePropertyBuilder
extends AbstractPropertyBuilder {
    private static final CSSName[] ALL = new CSSName[]{CSSName.FS_PAGE_ORIENTATION, CSSName.FS_PAGE_HEIGHT, CSSName.FS_PAGE_WIDTH};

    @Override
    public List buildDeclarations(CSSName cssName, List values, int origin, boolean important, boolean inheritAllowed) {
        ArrayList<PropertyDeclaration> result = new ArrayList<PropertyDeclaration>(3);
        this.checkValueCount(cssName, 1, 2, values.size());
        if (values.size() == 1) {
            PropertyValue value = (PropertyValue)values.get(0);
            this.checkInheritAllowed(value, inheritAllowed);
            if (value.getCssValueType() == 0) {
                return this.checkInheritAll(ALL, values, origin, important, inheritAllowed);
            }
            if (value.getPrimitiveType() == 21) {
                PageSize pageSize = PageSize.getPageSize(value.getStringValue());
                if (pageSize != null) {
                    result.add(new PropertyDeclaration(CSSName.FS_PAGE_ORIENTATION, new PropertyValue(IdentValue.AUTO), important, origin));
                    result.add(new PropertyDeclaration(CSSName.FS_PAGE_WIDTH, pageSize.getPageWidth(), important, origin));
                    result.add(new PropertyDeclaration(CSSName.FS_PAGE_HEIGHT, pageSize.getPageHeight(), important, origin));
                    return result;
                }
                IdentValue ident = this.checkIdent(cssName, value);
                if (ident == IdentValue.LANDSCAPE || ident == IdentValue.PORTRAIT) {
                    result.add(new PropertyDeclaration(CSSName.FS_PAGE_ORIENTATION, value, important, origin));
                    result.add(new PropertyDeclaration(CSSName.FS_PAGE_WIDTH, new PropertyValue(IdentValue.AUTO), important, origin));
                    result.add(new PropertyDeclaration(CSSName.FS_PAGE_HEIGHT, new PropertyValue(IdentValue.AUTO), important, origin));
                    return result;
                }
                if (ident == IdentValue.AUTO) {
                    result.add(new PropertyDeclaration(CSSName.FS_PAGE_ORIENTATION, value, important, origin));
                    result.add(new PropertyDeclaration(CSSName.FS_PAGE_WIDTH, value, important, origin));
                    result.add(new PropertyDeclaration(CSSName.FS_PAGE_HEIGHT, value, important, origin));
                    return result;
                }
                throw new CSSParseException("Identifier " + ident + " is not a valid value for " + cssName, -1);
            }
            if (this.isLength(value)) {
                if (value.getFloatValue() < 0.0f) {
                    throw new CSSParseException("A page dimension may not be negative", -1);
                }
                result.add(new PropertyDeclaration(CSSName.FS_PAGE_ORIENTATION, new PropertyValue(IdentValue.AUTO), important, origin));
                result.add(new PropertyDeclaration(CSSName.FS_PAGE_WIDTH, value, important, origin));
                result.add(new PropertyDeclaration(CSSName.FS_PAGE_HEIGHT, value, important, origin));
                return result;
            }
            throw new CSSParseException("Value for " + cssName + " must be a length or identifier", -1);
        }
        PropertyValue value1 = (PropertyValue)values.get(0);
        PropertyValue value2 = (PropertyValue)values.get(1);
        this.checkInheritAllowed(value2, false);
        if (this.isLength(value1) && this.isLength(value2)) {
            if (value1.getFloatValue() < 0.0f) {
                throw new CSSParseException("A page dimension may not be negative", -1);
            }
            if (value2.getFloatValue() < 0.0f) {
                throw new CSSParseException("A page dimension may not be negative", -1);
            }
            result.add(new PropertyDeclaration(CSSName.FS_PAGE_ORIENTATION, new PropertyValue(IdentValue.AUTO), important, origin));
            result.add(new PropertyDeclaration(CSSName.FS_PAGE_WIDTH, value1, important, origin));
            result.add(new PropertyDeclaration(CSSName.FS_PAGE_HEIGHT, value2, important, origin));
            return result;
        }
        if (value1.getPrimitiveType() == 21 && value2.getPrimitiveType() == 21) {
            if (value2.getStringValue().equals("landscape") || value2.getStringValue().equals("portrait")) {
                PropertyValue temp = value1;
                value1 = value2;
                value2 = temp;
            }
            if (!value1.toString().equals("landscape") && !value1.toString().equals("portrait")) {
                throw new CSSParseException("Value " + value1 + " is not a valid page orientation", -1);
            }
            result.add(new PropertyDeclaration(CSSName.FS_PAGE_ORIENTATION, value1, important, origin));
            PageSize pageSize = PageSize.getPageSize(value2.getStringValue());
            if (pageSize == null) {
                throw new CSSParseException("Value " + value1 + " is not a valid page size", -1);
            }
            result.add(new PropertyDeclaration(CSSName.FS_PAGE_WIDTH, pageSize.getPageWidth(), important, origin));
            result.add(new PropertyDeclaration(CSSName.FS_PAGE_HEIGHT, pageSize.getPageHeight(), important, origin));
            return result;
        }
        throw new CSSParseException("Invalid value for size property", -1);
    }
}


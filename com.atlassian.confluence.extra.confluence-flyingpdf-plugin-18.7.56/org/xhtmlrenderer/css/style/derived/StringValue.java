/*
 * Decompiled with CFR 0.152.
 */
package org.xhtmlrenderer.css.style.derived;

import org.xhtmlrenderer.css.constants.CSSName;
import org.xhtmlrenderer.css.parser.PropertyValue;
import org.xhtmlrenderer.css.style.DerivedValue;
import org.xhtmlrenderer.util.ArrayUtil;

public class StringValue
extends DerivedValue {
    private String[] _stringAsArray;

    public StringValue(CSSName name, PropertyValue value) {
        super(name, value.getPrimitiveType(), value.getCssText(), value.getStringValue());
        if (value.getStringArrayValue() != null) {
            this._stringAsArray = value.getStringArrayValue();
        }
    }

    @Override
    public String[] asStringArray() {
        return ArrayUtil.cloneOrEmpty(this._stringAsArray);
    }

    public String toString() {
        return this.getStringValue();
    }
}


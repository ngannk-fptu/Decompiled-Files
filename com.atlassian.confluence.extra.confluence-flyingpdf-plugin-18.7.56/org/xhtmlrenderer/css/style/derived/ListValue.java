/*
 * Decompiled with CFR 0.152.
 */
package org.xhtmlrenderer.css.style.derived;

import java.util.Iterator;
import java.util.List;
import org.xhtmlrenderer.css.constants.CSSName;
import org.xhtmlrenderer.css.parser.PropertyValue;
import org.xhtmlrenderer.css.style.DerivedValue;

public class ListValue
extends DerivedValue {
    private List _values;

    public ListValue(CSSName name, PropertyValue value) {
        super(name, value.getPrimitiveType(), value.getCssText(), value.getCssText());
        this._values = value.getValues();
    }

    public List getValues() {
        return this._values;
    }

    @Override
    public String[] asStringArray() {
        if (this._values == null || this._values.isEmpty()) {
            return new String[0];
        }
        String[] arr = new String[this._values.size()];
        int i = 0;
        Iterator iter = this._values.iterator();
        while (iter.hasNext()) {
            arr[i++] = iter.next().toString();
        }
        return arr;
    }
}


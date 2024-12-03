/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xmlbeans;

import org.apache.xmlbeans.XmlCursor;

public class SchemaBookmark
extends XmlCursor.XmlBookmark {
    private Object _value;

    public SchemaBookmark(Object value) {
        this._value = value;
    }

    public Object getValue() {
        return this._value;
    }
}


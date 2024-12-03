/*
 * Decompiled with CFR 0.152.
 */
package org.apache.batik.css.engine.value;

import org.apache.batik.css.engine.value.StringValue;

public class URIValue
extends StringValue {
    String cssText;

    public URIValue(String cssText, String uri) {
        super((short)20, uri);
        this.cssText = cssText;
    }

    @Override
    public String getCssText() {
        return "url(" + this.cssText + ')';
    }
}


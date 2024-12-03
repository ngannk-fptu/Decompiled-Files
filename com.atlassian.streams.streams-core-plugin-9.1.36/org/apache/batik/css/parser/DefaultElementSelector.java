/*
 * Decompiled with CFR 0.152.
 */
package org.apache.batik.css.parser;

import org.apache.batik.css.parser.AbstractElementSelector;

public class DefaultElementSelector
extends AbstractElementSelector {
    public DefaultElementSelector(String uri, String name) {
        super(uri, name);
    }

    @Override
    public short getSelectorType() {
        return 4;
    }

    public String toString() {
        String name = this.getLocalName();
        if (name == null) {
            return "*";
        }
        return name;
    }
}


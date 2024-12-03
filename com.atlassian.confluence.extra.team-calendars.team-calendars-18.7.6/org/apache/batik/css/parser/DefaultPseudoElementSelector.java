/*
 * Decompiled with CFR 0.152.
 */
package org.apache.batik.css.parser;

import org.apache.batik.css.parser.AbstractElementSelector;

public class DefaultPseudoElementSelector
extends AbstractElementSelector {
    public DefaultPseudoElementSelector(String uri, String name) {
        super(uri, name);
    }

    public short getSelectorType() {
        return 9;
    }

    public String toString() {
        return ":" + this.getLocalName();
    }
}


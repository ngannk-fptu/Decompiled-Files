/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tools.ant.attribute;

import org.apache.tools.ant.UnknownElement;
import org.apache.tools.ant.attribute.BaseIfAttribute;

public class IfBlankAttribute
extends BaseIfAttribute {
    @Override
    public boolean isEnabled(UnknownElement el, String value) {
        return this.convertResult(value == null || value.isEmpty());
    }

    public static class Unless
    extends IfBlankAttribute {
        public Unless() {
            this.setPositive(false);
        }
    }
}


/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tools.ant.attribute;

import org.apache.tools.ant.UnknownElement;
import org.apache.tools.ant.attribute.BaseIfAttribute;

public class IfSetAttribute
extends BaseIfAttribute {
    @Override
    public boolean isEnabled(UnknownElement el, String value) {
        return this.convertResult(this.getProject().getProperty(value) != null);
    }

    public static class Unless
    extends IfSetAttribute {
        public Unless() {
            this.setPositive(false);
        }
    }
}


/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tools.ant.attribute;

import org.apache.tools.ant.Project;
import org.apache.tools.ant.UnknownElement;
import org.apache.tools.ant.attribute.BaseIfAttribute;

public class IfTrueAttribute
extends BaseIfAttribute {
    @Override
    public boolean isEnabled(UnknownElement el, String value) {
        return this.convertResult(Project.toBoolean(value));
    }

    public static class Unless
    extends IfTrueAttribute {
        public Unless() {
            this.setPositive(false);
        }
    }
}


/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tools.ant.attribute;

import java.util.Map;
import java.util.stream.Collectors;
import org.apache.tools.ant.ProjectComponent;
import org.apache.tools.ant.UnknownElement;
import org.apache.tools.ant.attribute.EnableAttribute;

public abstract class BaseIfAttribute
extends ProjectComponent
implements EnableAttribute {
    private boolean positive = true;

    protected void setPositive(boolean positive) {
        this.positive = positive;
    }

    protected boolean isPositive() {
        return this.positive;
    }

    protected boolean convertResult(boolean val) {
        return this.positive == val;
    }

    protected Map<String, String> getParams(UnknownElement el) {
        return el.getWrapper().getAttributeMap().entrySet().stream().filter(e -> ((String)e.getKey()).startsWith("ant-attribute:param")).collect(Collectors.toMap(e -> ((String)e.getKey()).substring(((String)e.getKey()).lastIndexOf(58) + 1), e -> el.getProject().replaceProperties((String)e.getValue()), (a, b) -> b));
    }
}


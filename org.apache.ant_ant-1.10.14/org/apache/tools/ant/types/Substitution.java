/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tools.ant.types;

import org.apache.tools.ant.Project;
import org.apache.tools.ant.types.DataType;

public class Substitution
extends DataType {
    public static final String DATA_TYPE_NAME = "substitution";
    private String expression = null;

    public void setExpression(String expression) {
        this.expression = expression;
    }

    public String getExpression(Project p) {
        if (this.isReference()) {
            return this.getRef(p).getExpression(p);
        }
        return this.expression;
    }

    public Substitution getRef(Project p) {
        return this.getCheckedRef(Substitution.class, this.getDataTypeName(), p);
    }
}


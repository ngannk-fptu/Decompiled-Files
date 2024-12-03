/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.digester;

import org.apache.commons.digester.Rule;
import org.xml.sax.Attributes;

public class ObjectParamRule
extends Rule {
    protected String attributeName = null;
    protected int paramIndex = 0;
    protected Object param = null;

    public ObjectParamRule(int paramIndex, Object param) {
        this(paramIndex, null, param);
    }

    public ObjectParamRule(int paramIndex, String attributeName, Object param) {
        this.paramIndex = paramIndex;
        this.attributeName = attributeName;
        this.param = param;
    }

    public void begin(String namespace, String name, Attributes attributes) throws Exception {
        String anAttribute = null;
        Object[] parameters = (Object[])this.digester.peekParams();
        if (this.attributeName != null) {
            anAttribute = attributes.getValue(this.attributeName);
            if (anAttribute != null) {
                parameters[this.paramIndex] = this.param;
            }
        } else {
            parameters[this.paramIndex] = this.param;
        }
    }

    public String toString() {
        StringBuffer sb = new StringBuffer("ObjectParamRule[");
        sb.append("paramIndex=");
        sb.append(this.paramIndex);
        sb.append(", attributeName=");
        sb.append(this.attributeName);
        sb.append(", param=");
        sb.append(this.param);
        sb.append("]");
        return sb.toString();
    }
}


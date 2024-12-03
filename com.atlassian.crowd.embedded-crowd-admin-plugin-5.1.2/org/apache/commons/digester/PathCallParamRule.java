/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.digester;

import org.apache.commons.digester.Rule;
import org.xml.sax.Attributes;

public class PathCallParamRule
extends Rule {
    protected int paramIndex = 0;

    public PathCallParamRule(int paramIndex) {
        this.paramIndex = paramIndex;
    }

    public void begin(String namespace, String name, Attributes attributes) throws Exception {
        String param = this.getDigester().getMatch();
        if (param != null) {
            Object[] parameters = (Object[])this.digester.peekParams();
            parameters[this.paramIndex] = param;
        }
    }

    public String toString() {
        StringBuffer sb = new StringBuffer("PathCallParamRule[");
        sb.append("paramIndex=");
        sb.append(this.paramIndex);
        sb.append("]");
        return sb.toString();
    }
}


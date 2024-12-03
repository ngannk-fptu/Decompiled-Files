/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.digester;

import java.util.Stack;
import org.apache.commons.digester.Digester;
import org.apache.commons.digester.Rule;
import org.xml.sax.Attributes;

public class CallParamRule
extends Rule {
    protected String attributeName = null;
    protected int paramIndex = 0;
    protected boolean fromStack = false;
    protected int stackIndex = 0;
    protected Stack<String> bodyTextStack;

    @Deprecated
    public CallParamRule(Digester digester, int paramIndex) {
        this(paramIndex);
    }

    @Deprecated
    public CallParamRule(Digester digester, int paramIndex, String attributeName) {
        this(paramIndex, attributeName);
    }

    public CallParamRule(int paramIndex) {
        this(paramIndex, null);
    }

    public CallParamRule(int paramIndex, String attributeName) {
        this.paramIndex = paramIndex;
        this.attributeName = attributeName;
    }

    public CallParamRule(int paramIndex, boolean fromStack) {
        this.paramIndex = paramIndex;
        this.fromStack = fromStack;
    }

    public CallParamRule(int paramIndex, int stackIndex) {
        this.paramIndex = paramIndex;
        this.fromStack = true;
        this.stackIndex = stackIndex;
    }

    public void begin(Attributes attributes) throws Exception {
        Object param = null;
        if (this.attributeName != null) {
            param = attributes.getValue(this.attributeName);
        } else if (this.fromStack) {
            param = this.digester.peek(this.stackIndex);
            if (this.digester.log.isDebugEnabled()) {
                StringBuffer sb = new StringBuffer("[CallParamRule]{");
                sb.append(this.digester.match);
                sb.append("} Save from stack; from stack?").append(this.fromStack);
                sb.append("; object=").append(param);
                this.digester.log.debug((Object)sb.toString());
            }
        }
        if (param != null) {
            Object[] parameters = (Object[])this.digester.peekParams();
            parameters[this.paramIndex] = param;
        }
    }

    public void body(String bodyText) throws Exception {
        if (this.attributeName == null && !this.fromStack) {
            if (this.bodyTextStack == null) {
                this.bodyTextStack = new Stack();
            }
            this.bodyTextStack.push(bodyText.trim());
        }
    }

    public void end(String namespace, String name) {
        if (this.bodyTextStack != null && !this.bodyTextStack.empty()) {
            Object[] parameters = (Object[])this.digester.peekParams();
            parameters[this.paramIndex] = this.bodyTextStack.pop();
        }
    }

    public String toString() {
        StringBuffer sb = new StringBuffer("CallParamRule[");
        sb.append("paramIndex=");
        sb.append(this.paramIndex);
        sb.append(", attributeName=");
        sb.append(this.attributeName);
        sb.append(", from stack=");
        sb.append(this.fromStack);
        sb.append("]");
        return sb.toString();
    }
}


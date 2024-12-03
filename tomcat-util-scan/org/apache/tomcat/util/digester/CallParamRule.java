/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tomcat.util.digester;

import org.apache.tomcat.util.digester.ArrayStack;
import org.apache.tomcat.util.digester.Rule;
import org.xml.sax.Attributes;

public class CallParamRule
extends Rule {
    protected final String attributeName;
    protected final int paramIndex;
    protected final boolean fromStack;
    protected final int stackIndex;
    protected ArrayStack<String> bodyTextStack;

    public CallParamRule(int paramIndex) {
        this(paramIndex, null);
    }

    public CallParamRule(int paramIndex, String attributeName) {
        this(attributeName, paramIndex, 0, false);
    }

    private CallParamRule(String attributeName, int paramIndex, int stackIndex, boolean fromStack) {
        this.attributeName = attributeName;
        this.paramIndex = paramIndex;
        this.stackIndex = stackIndex;
        this.fromStack = fromStack;
    }

    @Override
    public void begin(String namespace, String name, Attributes attributes) throws Exception {
        Object param = null;
        if (this.attributeName != null) {
            param = attributes.getValue(this.attributeName);
        } else if (this.fromStack) {
            param = this.digester.peek(this.stackIndex);
            if (this.digester.log.isDebugEnabled()) {
                StringBuilder sb = new StringBuilder("[CallParamRule]{");
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

    @Override
    public void body(String namespace, String name, String bodyText) throws Exception {
        if (this.attributeName == null && !this.fromStack) {
            if (this.bodyTextStack == null) {
                this.bodyTextStack = new ArrayStack();
            }
            this.bodyTextStack.push(bodyText.trim());
        }
    }

    @Override
    public void end(String namespace, String name) {
        if (this.bodyTextStack != null && !this.bodyTextStack.empty()) {
            Object[] parameters = (Object[])this.digester.peekParams();
            parameters[this.paramIndex] = this.bodyTextStack.pop();
        }
    }

    public String toString() {
        StringBuilder sb = new StringBuilder("CallParamRule[");
        sb.append("paramIndex=");
        sb.append(this.paramIndex);
        sb.append(", attributeName=");
        sb.append(this.attributeName);
        sb.append(", from stack=");
        sb.append(this.fromStack);
        sb.append(']');
        return sb.toString();
    }
}


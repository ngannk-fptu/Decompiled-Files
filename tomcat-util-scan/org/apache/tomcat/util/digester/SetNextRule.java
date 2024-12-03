/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.tomcat.util.IntrospectionUtils
 */
package org.apache.tomcat.util.digester;

import org.apache.tomcat.util.IntrospectionUtils;
import org.apache.tomcat.util.digester.Rule;

public class SetNextRule
extends Rule {
    protected String methodName = null;
    protected String paramType = null;
    protected boolean useExactMatch = false;

    public SetNextRule(String methodName, String paramType) {
        this.methodName = methodName;
        this.paramType = paramType;
    }

    public boolean isExactMatch() {
        return this.useExactMatch;
    }

    public void setExactMatch(boolean useExactMatch) {
        this.useExactMatch = useExactMatch;
    }

    @Override
    public void end(String namespace, String name) throws Exception {
        Object child = this.digester.peek(0);
        Object parent = this.digester.peek(1);
        if (this.digester.log.isDebugEnabled()) {
            if (parent == null) {
                this.digester.log.debug((Object)("[SetNextRule]{" + this.digester.match + "} Call [NULL PARENT]." + this.methodName + "(" + child + ")"));
            } else {
                this.digester.log.debug((Object)("[SetNextRule]{" + this.digester.match + "} Call " + parent.getClass().getName() + "." + this.methodName + "(" + child + ")"));
            }
        }
        IntrospectionUtils.callMethod1((Object)parent, (String)this.methodName, (Object)child, (String)this.paramType, (ClassLoader)this.digester.getClassLoader());
        StringBuilder code = this.digester.getGeneratedCode();
        if (code != null) {
            code.append(this.digester.toVariableName(parent)).append('.');
            code.append(this.methodName).append('(').append(this.digester.toVariableName(child)).append(");");
            code.append(System.lineSeparator());
        }
    }

    public String toString() {
        StringBuilder sb = new StringBuilder("SetNextRule[");
        sb.append("methodName=");
        sb.append(this.methodName);
        sb.append(", paramType=");
        sb.append(this.paramType);
        sb.append(']');
        return sb.toString();
    }
}


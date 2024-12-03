/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.digester;

import org.apache.commons.beanutils.MethodUtils;
import org.apache.commons.digester.Digester;
import org.apache.commons.digester.Rule;

public class SetTopRule
extends Rule {
    protected String methodName = null;
    protected String paramType = null;
    protected boolean useExactMatch = false;

    public SetTopRule(Digester digester, String methodName) {
        this(methodName);
    }

    public SetTopRule(Digester digester, String methodName, String paramType) {
        this(methodName, paramType);
    }

    public SetTopRule(String methodName) {
        this(methodName, null);
    }

    public SetTopRule(String methodName, String paramType) {
        this.methodName = methodName;
        this.paramType = paramType;
    }

    public boolean isExactMatch() {
        return this.useExactMatch;
    }

    public void setExactMatch(boolean useExactMatch) {
        this.useExactMatch = useExactMatch;
    }

    public void end() throws Exception {
        Object child = this.digester.peek(0);
        Object parent = this.digester.peek(1);
        if (this.digester.log.isDebugEnabled()) {
            if (child == null) {
                this.digester.log.debug((Object)("[SetTopRule]{" + this.digester.match + "} Call [NULL CHILD]." + this.methodName + "(" + parent + ")"));
            } else {
                this.digester.log.debug((Object)("[SetTopRule]{" + this.digester.match + "} Call " + child.getClass().getName() + "." + this.methodName + "(" + parent + ")"));
            }
        }
        Class[] paramTypes = new Class[]{this.paramType != null ? this.digester.getClassLoader().loadClass(this.paramType) : parent.getClass()};
        if (this.useExactMatch) {
            MethodUtils.invokeExactMethod(child, this.methodName, new Object[]{parent}, paramTypes);
        } else {
            MethodUtils.invokeMethod(child, this.methodName, new Object[]{parent}, paramTypes);
        }
    }

    public String toString() {
        StringBuffer sb = new StringBuffer("SetTopRule[");
        sb.append("methodName=");
        sb.append(this.methodName);
        sb.append(", paramType=");
        sb.append(this.paramType);
        sb.append("]");
        return sb.toString();
    }
}


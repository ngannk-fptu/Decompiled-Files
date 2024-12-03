/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.beanutils.MethodUtils
 */
package org.apache.commons.digester;

import org.apache.commons.beanutils.MethodUtils;
import org.apache.commons.digester.Digester;
import org.apache.commons.digester.Rule;

public class SetNextRule
extends Rule {
    protected String methodName = null;
    protected String paramType = null;
    protected boolean useExactMatch = false;

    @Deprecated
    public SetNextRule(Digester digester, String methodName) {
        this(methodName);
    }

    @Deprecated
    public SetNextRule(Digester digester, String methodName, String paramType) {
        this(methodName, paramType);
    }

    public SetNextRule(String methodName) {
        this(methodName, null);
    }

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

    public void end() throws Exception {
        Object child = this.digester.peek(0);
        Object parent = this.digester.peek(1);
        if (this.digester.log.isDebugEnabled()) {
            if (parent == null) {
                this.digester.log.debug((Object)("[SetNextRule]{" + this.digester.match + "} Call [NULL PARENT]." + this.methodName + "(" + child + ")"));
            } else {
                this.digester.log.debug((Object)("[SetNextRule]{" + this.digester.match + "} Call " + parent.getClass().getName() + "." + this.methodName + "(" + child + ")"));
            }
        }
        Class[] paramTypes = new Class[]{this.paramType != null ? this.digester.getClassLoader().loadClass(this.paramType) : child.getClass()};
        if (this.useExactMatch) {
            MethodUtils.invokeExactMethod((Object)parent, (String)this.methodName, (Object[])new Object[]{child}, (Class[])paramTypes);
        } else {
            MethodUtils.invokeMethod((Object)parent, (String)this.methodName, (Object[])new Object[]{child}, (Class[])paramTypes);
        }
    }

    public String toString() {
        StringBuffer sb = new StringBuffer("SetNextRule[");
        sb.append("methodName=");
        sb.append(this.methodName);
        sb.append(", paramType=");
        sb.append(this.paramType);
        sb.append("]");
        return sb.toString();
    }
}


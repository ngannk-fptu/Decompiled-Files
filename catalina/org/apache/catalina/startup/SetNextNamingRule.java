/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.tomcat.util.IntrospectionUtils
 *  org.apache.tomcat.util.digester.Rule
 */
package org.apache.catalina.startup;

import org.apache.catalina.Context;
import org.apache.catalina.deploy.NamingResourcesImpl;
import org.apache.tomcat.util.IntrospectionUtils;
import org.apache.tomcat.util.digester.Rule;

public class SetNextNamingRule
extends Rule {
    protected final String methodName;
    protected final String paramType;

    public SetNextNamingRule(String methodName, String paramType) {
        this.methodName = methodName;
        this.paramType = paramType;
    }

    public void end(String namespace, String name) throws Exception {
        Object child = this.digester.peek(0);
        Object parent = this.digester.peek(1);
        boolean context = false;
        NamingResourcesImpl namingResources = null;
        if (parent instanceof Context) {
            namingResources = ((Context)parent).getNamingResources();
            context = true;
        } else {
            namingResources = (NamingResourcesImpl)parent;
        }
        IntrospectionUtils.callMethod1((Object)namingResources, (String)this.methodName, (Object)child, (String)this.paramType, (ClassLoader)this.digester.getClassLoader());
        StringBuilder code = this.digester.getGeneratedCode();
        if (code != null) {
            if (context) {
                code.append(this.digester.toVariableName(parent)).append(".getNamingResources()");
            } else {
                code.append(this.digester.toVariableName((Object)namingResources));
            }
            code.append('.').append(this.methodName).append('(');
            code.append(this.digester.toVariableName(child)).append(");");
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


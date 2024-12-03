/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tomcat.util.descriptor.web;

import java.lang.reflect.Method;
import org.apache.tomcat.util.digester.Rule;
import org.xml.sax.Attributes;

final class SetPublicIdRule
extends Rule {
    private String method = null;

    SetPublicIdRule(String method) {
        this.method = method;
    }

    @Override
    public void begin(String namespace, String name, Attributes attributes) throws Exception {
        StringBuilder code;
        Object top = this.digester.peek();
        Class[] paramClasses = new Class[]{"String".getClass()};
        String[] paramValues = new String[]{this.digester.getPublicId()};
        Method m = null;
        try {
            m = top.getClass().getMethod(this.method, paramClasses);
        }
        catch (NoSuchMethodException e) {
            this.digester.getLogger().error((Object)("Can't find method " + this.method + " in " + top + " CLASS " + top.getClass()));
            return;
        }
        m.invoke(top, (Object[])paramValues);
        if (this.digester.getLogger().isDebugEnabled()) {
            this.digester.getLogger().debug((Object)("" + top.getClass().getName() + "." + this.method + "(" + paramValues[0] + ")"));
        }
        if ((code = this.digester.getGeneratedCode()) != null) {
            code.append(System.lineSeparator());
            code.append(this.digester.toVariableName(top)).append(".").append(this.method).append("(\"");
            code.append(this.digester.getPublicId()).append("\");");
            code.append(System.lineSeparator());
        }
    }
}


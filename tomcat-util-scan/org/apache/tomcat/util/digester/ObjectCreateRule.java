/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tomcat.util.digester;

import org.apache.tomcat.util.digester.Rule;
import org.xml.sax.Attributes;

public class ObjectCreateRule
extends Rule {
    protected String attributeName = null;
    protected String className = null;

    public ObjectCreateRule(String className) {
        this(className, null);
    }

    public ObjectCreateRule(String className, String attributeName) {
        this.className = className;
        this.attributeName = attributeName;
    }

    @Override
    public void begin(String namespace, String name, Attributes attributes) throws Exception {
        String realClassName = this.getRealClassName(attributes);
        if (realClassName == null) {
            throw new NullPointerException(sm.getString("rule.noClassName", new Object[]{namespace, name}));
        }
        Class<?> clazz = this.digester.getClassLoader().loadClass(realClassName);
        Object instance = clazz.getConstructor(new Class[0]).newInstance(new Object[0]);
        this.digester.push(instance);
        StringBuilder code = this.digester.getGeneratedCode();
        if (code != null) {
            code.append(System.lineSeparator());
            code.append(System.lineSeparator());
            code.append(realClassName).append(' ').append(this.digester.toVariableName(instance)).append(" = new ");
            code.append(realClassName).append("();").append(System.lineSeparator());
        }
    }

    protected String getRealClassName(Attributes attributes) {
        String value;
        String realClassName = this.className;
        if (this.attributeName != null && (value = attributes.getValue(this.attributeName)) != null) {
            realClassName = value;
        }
        return realClassName;
    }

    @Override
    public void end(String namespace, String name) throws Exception {
        Object top = this.digester.pop();
        if (this.digester.log.isDebugEnabled()) {
            this.digester.log.debug((Object)("[ObjectCreateRule]{" + this.digester.match + "} Pop " + top.getClass().getName()));
        }
    }

    public String toString() {
        StringBuilder sb = new StringBuilder("ObjectCreateRule[");
        sb.append("className=");
        sb.append(this.className);
        sb.append(", attributeName=");
        sb.append(this.attributeName);
        sb.append(']');
        return sb.toString();
    }
}


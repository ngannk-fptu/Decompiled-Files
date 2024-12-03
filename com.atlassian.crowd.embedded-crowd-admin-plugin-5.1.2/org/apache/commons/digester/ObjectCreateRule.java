/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.digester;

import org.apache.commons.digester.Digester;
import org.apache.commons.digester.Rule;
import org.xml.sax.Attributes;

public class ObjectCreateRule
extends Rule {
    protected String attributeName = null;
    protected String className = null;

    public ObjectCreateRule(Digester digester, String className) {
        this(className);
    }

    public ObjectCreateRule(Digester digester, Class clazz) {
        this(clazz);
    }

    public ObjectCreateRule(Digester digester, String className, String attributeName) {
        this(className, attributeName);
    }

    public ObjectCreateRule(Digester digester, String attributeName, Class clazz) {
        this(attributeName, clazz);
    }

    public ObjectCreateRule(String className) {
        this(className, (String)null);
    }

    public ObjectCreateRule(Class clazz) {
        this(clazz.getName(), (String)null);
    }

    public ObjectCreateRule(String className, String attributeName) {
        this.className = className;
        this.attributeName = attributeName;
    }

    public ObjectCreateRule(String attributeName, Class clazz) {
        this(clazz.getName(), attributeName);
    }

    public void begin(Attributes attributes) throws Exception {
        String value;
        String realClassName = this.className;
        if (this.attributeName != null && (value = attributes.getValue(this.attributeName)) != null) {
            realClassName = value;
        }
        if (this.digester.log.isDebugEnabled()) {
            this.digester.log.debug((Object)("[ObjectCreateRule]{" + this.digester.match + "}New " + realClassName));
        }
        Class<?> clazz = this.digester.getClassLoader().loadClass(realClassName);
        Object instance = clazz.newInstance();
        this.digester.push(instance);
    }

    public void end() throws Exception {
        Object top = this.digester.pop();
        if (this.digester.log.isDebugEnabled()) {
            this.digester.log.debug((Object)("[ObjectCreateRule]{" + this.digester.match + "} Pop " + top.getClass().getName()));
        }
    }

    public String toString() {
        StringBuffer sb = new StringBuffer("ObjectCreateRule[");
        sb.append("className=");
        sb.append(this.className);
        sb.append(", attributeName=");
        sb.append(this.attributeName);
        sb.append("]");
        return sb.toString();
    }
}


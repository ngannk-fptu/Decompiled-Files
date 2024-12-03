/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xmlbeans.impl.tool;

import java.util.ArrayList;
import java.util.List;

public class Extension {
    private Class<?> className;
    private final List<Param> params = new ArrayList<Param>();

    public Class<?> getClassName() {
        return this.className;
    }

    public void setClassName(Class<?> className) {
        this.className = className;
    }

    public void setClassName(String className) throws ClassNotFoundException {
        this.className = Class.forName(className);
    }

    public List<Param> getParams() {
        return this.params;
    }

    public Param createParam() {
        Param p = new Param();
        this.params.add(p);
        return p;
    }

    public static class Param {
        private String name;
        private String value;

        public String getName() {
            return this.name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getValue() {
            return this.value;
        }

        public void setValue(String value) {
            this.value = value;
        }
    }
}


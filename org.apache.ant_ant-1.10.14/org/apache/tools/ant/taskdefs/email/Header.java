/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tools.ant.taskdefs.email;

public class Header {
    private String name;
    private String value;

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getValue() {
        return this.value;
    }
}


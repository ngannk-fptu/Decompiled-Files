/*
 * Decompiled with CFR 0.152.
 */
package org.apache.catalina.ant.jmx;

public class Arg {
    private String type;
    private String value;

    public void setType(String type) {
        this.type = type;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getValue() {
        return this.value;
    }

    public String getType() {
        return this.type;
    }
}


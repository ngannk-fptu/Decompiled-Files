/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tools.ant.types;

public final class Parameter {
    private String name = null;
    private String type = null;
    private String value = null;

    public void setName(String name) {
        this.name = name;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getName() {
        return this.name;
    }

    public String getType() {
        return this.type;
    }

    public String getValue() {
        return this.value;
    }
}


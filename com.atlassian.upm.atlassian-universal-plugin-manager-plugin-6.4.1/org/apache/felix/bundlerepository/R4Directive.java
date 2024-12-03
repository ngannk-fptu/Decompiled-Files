/*
 * Decompiled with CFR 0.152.
 */
package org.apache.felix.bundlerepository;

public class R4Directive {
    private String m_name = "";
    private String m_value = "";

    public R4Directive(String name, String value) {
        this.m_name = name;
        this.m_value = value;
    }

    public String getName() {
        return this.m_name;
    }

    public String getValue() {
        return this.m_value;
    }
}


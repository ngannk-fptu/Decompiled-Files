/*
 * Decompiled with CFR 0.152.
 */
package org.apache.felix.bundlerepository;

public class R4Attribute {
    private String m_name = "";
    private String m_value = "";
    private boolean m_isMandatory = false;

    public R4Attribute(String name, String value, boolean isMandatory) {
        this.m_name = name;
        this.m_value = value;
        this.m_isMandatory = isMandatory;
    }

    public String getName() {
        return this.m_name;
    }

    public String getValue() {
        return this.m_value;
    }

    public boolean isMandatory() {
        return this.m_isMandatory;
    }
}


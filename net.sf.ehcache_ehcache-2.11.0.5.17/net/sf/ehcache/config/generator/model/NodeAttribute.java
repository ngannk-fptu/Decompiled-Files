/*
 * Decompiled with CFR 0.152.
 */
package net.sf.ehcache.config.generator.model;

public interface NodeAttribute {
    public String getName();

    public String getValue();

    public boolean isOptional();

    public String getDefaultValue();

    public void setOptional(boolean var1);

    public void setDefaultValue(String var1);

    public void setValue(String var1);

    public NodeAttribute optional(boolean var1);

    public NodeAttribute defaultValue(String var1);
}


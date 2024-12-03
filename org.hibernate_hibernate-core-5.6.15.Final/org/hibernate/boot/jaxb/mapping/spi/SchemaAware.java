/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.boot.jaxb.mapping.spi;

public interface SchemaAware {
    public String getSchema();

    public void setSchema(String var1);

    public String getCatalog();

    public void setCatalog(String var1);
}


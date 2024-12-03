/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.boot.jaxb.hbm.spi;

public interface TableInformationContainer {
    public String getSchema();

    public String getCatalog();

    public String getTable();

    public String getSubselect();

    public String getSubselectAttribute();
}


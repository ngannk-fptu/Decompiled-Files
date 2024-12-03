/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.hql.spi.id;

public interface IdTableSupport {
    public String generateIdTableName(String var1);

    public String getCreateIdTableCommand();

    public String getCreateIdTableStatementOptions();

    public String getDropIdTableCommand();

    public String getTruncateIdTableCommand();
}


/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.jpa.spi;

public interface JpaCompliance {
    public boolean isJpaQueryComplianceEnabled();

    public boolean isJpaTransactionComplianceEnabled();

    public boolean isJpaListComplianceEnabled();

    public boolean isJpaClosedComplianceEnabled();

    public boolean isJpaProxyComplianceEnabled();

    public boolean isJpaCacheComplianceEnabled();

    public boolean isGlobalGeneratorScopeEnabled();
}


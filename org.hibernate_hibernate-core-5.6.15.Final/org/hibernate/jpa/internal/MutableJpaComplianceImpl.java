/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.jpa.internal;

import java.util.Map;
import org.hibernate.internal.util.config.ConfigurationHelper;
import org.hibernate.jpa.internal.JpaComplianceImpl;
import org.hibernate.jpa.spi.JpaCompliance;
import org.hibernate.jpa.spi.MutableJpaCompliance;

public class MutableJpaComplianceImpl
implements MutableJpaCompliance {
    private boolean queryCompliance;
    private boolean transactionCompliance;
    private boolean listCompliance;
    private boolean closedCompliance;
    private boolean proxyCompliance;
    private boolean cachingCompliance;
    private final boolean globalGeneratorNameScopeCompliance;

    public MutableJpaComplianceImpl(Map configurationSettings, boolean jpaByDefault) {
        Object legacyQueryCompliance = configurationSettings.get("hibernate.query.jpaql_strict_compliance");
        this.queryCompliance = ConfigurationHelper.getBoolean("hibernate.jpa.compliance.query", configurationSettings, ConfigurationHelper.toBoolean(legacyQueryCompliance, jpaByDefault));
        this.transactionCompliance = ConfigurationHelper.getBoolean("hibernate.jpa.compliance.transaction", configurationSettings, jpaByDefault);
        this.listCompliance = ConfigurationHelper.getBoolean("hibernate.jpa.compliance.list", configurationSettings, jpaByDefault);
        this.closedCompliance = ConfigurationHelper.getBoolean("hibernate.jpa.compliance.closed", configurationSettings, jpaByDefault);
        this.proxyCompliance = ConfigurationHelper.getBoolean("hibernate.jpa.compliance.proxy", configurationSettings, jpaByDefault);
        this.cachingCompliance = ConfigurationHelper.getBoolean("hibernate.jpa.compliance.caching", configurationSettings, jpaByDefault);
        this.globalGeneratorNameScopeCompliance = ConfigurationHelper.getBoolean("hibernate.jpa.compliance.global_id_generators", configurationSettings, jpaByDefault);
    }

    @Override
    public boolean isJpaQueryComplianceEnabled() {
        return this.queryCompliance;
    }

    @Override
    public boolean isJpaTransactionComplianceEnabled() {
        return this.transactionCompliance;
    }

    @Override
    public boolean isJpaListComplianceEnabled() {
        return this.listCompliance;
    }

    @Override
    public boolean isJpaClosedComplianceEnabled() {
        return this.closedCompliance;
    }

    @Override
    public boolean isJpaProxyComplianceEnabled() {
        return this.proxyCompliance;
    }

    @Override
    public boolean isJpaCacheComplianceEnabled() {
        return this.cachingCompliance;
    }

    @Override
    public boolean isGlobalGeneratorScopeEnabled() {
        return this.globalGeneratorNameScopeCompliance;
    }

    @Override
    public void setQueryCompliance(boolean queryCompliance) {
        this.queryCompliance = queryCompliance;
    }

    @Override
    public void setTransactionCompliance(boolean transactionCompliance) {
        this.transactionCompliance = transactionCompliance;
    }

    @Override
    public void setListCompliance(boolean listCompliance) {
        this.listCompliance = listCompliance;
    }

    @Override
    public void setClosedCompliance(boolean closedCompliance) {
        this.closedCompliance = closedCompliance;
    }

    @Override
    public void setProxyCompliance(boolean proxyCompliance) {
        this.proxyCompliance = proxyCompliance;
    }

    @Override
    public void setCachingCompliance(boolean cachingCompliance) {
        this.cachingCompliance = cachingCompliance;
    }

    @Override
    public JpaCompliance immutableCopy() {
        JpaComplianceImpl.JpaComplianceBuilder builder = new JpaComplianceImpl.JpaComplianceBuilder();
        builder.setQueryCompliance(this.queryCompliance).setTransactionCompliance(this.transactionCompliance).setListCompliance(this.listCompliance).setClosedCompliance(this.closedCompliance).setProxyCompliance(this.proxyCompliance).setCachingCompliance(this.cachingCompliance).setGlobalGeneratorNameCompliance(this.globalGeneratorNameScopeCompliance);
        return builder.createJpaCompliance();
    }
}


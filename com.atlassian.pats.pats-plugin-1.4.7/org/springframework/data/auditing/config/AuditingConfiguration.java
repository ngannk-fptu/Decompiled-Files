/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.data.auditing.config;

public interface AuditingConfiguration {
    public String getAuditorAwareRef();

    public boolean isSetDates();

    public boolean isModifyOnCreate();

    public String getDateTimeProviderRef();
}


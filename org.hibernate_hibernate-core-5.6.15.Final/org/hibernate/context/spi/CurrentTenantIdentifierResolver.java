/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.context.spi;

public interface CurrentTenantIdentifierResolver {
    public String resolveCurrentTenantIdentifier();

    public boolean validateExistingCurrentSessions();
}


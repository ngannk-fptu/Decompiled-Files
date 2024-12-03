/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.tenant;

import com.atlassian.confluence.tenant.TenantTicketCollector;

@Deprecated(forRemoval=true)
public class DefaultTenantTicketCollector
implements TenantTicketCollector {
    private final TenantState tenantState;

    public DefaultTenantTicketCollector(TenantState tenantState) {
        this.tenantState = tenantState;
    }

    @Override
    public boolean checkTicket() {
        return this.tenantState == TenantState.TENANTED;
    }

    public static enum TenantState {
        VACANT,
        TENANTED;

    }
}


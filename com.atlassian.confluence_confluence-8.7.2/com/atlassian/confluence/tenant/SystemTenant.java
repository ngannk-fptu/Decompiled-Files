/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.fugue.Option
 *  com.atlassian.tenancy.api.Tenant
 */
package com.atlassian.confluence.tenant;

import com.atlassian.confluence.tenant.TenantRegistry;
import com.atlassian.confluence.util.FugueConversionUtil;
import com.atlassian.fugue.Option;
import com.atlassian.tenancy.api.Tenant;
import java.util.Optional;

@Deprecated(forRemoval=true)
public class SystemTenant
implements Tenant {
    private static final String SYSTEM_TENANT_STRING = "system tenant";
    private static final Tenant HOLDER = new Tenant(){

        public String toString() {
            return SystemTenant.SYSTEM_TENANT_STRING;
        }

        public String name() {
            return SystemTenant.SYSTEM_TENANT_STRING;
        }
    };
    private final TenantRegistry tenantRegistry;

    public SystemTenant(TenantRegistry tenantRegistry) {
        this.tenantRegistry = tenantRegistry;
    }

    public String name() {
        return SYSTEM_TENANT_STRING;
    }

    @Deprecated
    public Option<Tenant> get() {
        if (this.tenantRegistry.isTenantRegistered(HOLDER)) {
            return Option.some((Object)HOLDER);
        }
        return Option.none();
    }

    public Optional<Tenant> getTenant() {
        return FugueConversionUtil.toOptional(this.get());
    }

    public boolean arrived() {
        return this.tenantRegistry.addTenant(HOLDER);
    }
}


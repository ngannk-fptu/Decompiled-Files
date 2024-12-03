/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.ExperimentalApi
 *  io.atlassian.fugue.Either
 *  io.atlassian.fugue.Iterables
 *  io.atlassian.fugue.Option
 */
package com.atlassian.tenancy.api.helper;

import com.atlassian.annotations.ExperimentalApi;
import com.atlassian.tenancy.api.Tenant;
import com.atlassian.tenancy.api.TenantAccessor;
import com.atlassian.tenancy.api.TenantUnavailableException;
import com.atlassian.tenancy.api.helper.Failure;
import io.atlassian.fugue.Either;
import io.atlassian.fugue.Iterables;
import io.atlassian.fugue.Option;
import java.util.Collections;
import java.util.concurrent.Callable;

@ExperimentalApi
@Deprecated
public class TenantAccessors {
    private TenantAccessors() {
    }

    @Deprecated
    public static <T> Iterable<Either<Failure, Option<T>>> forEachTenant(TenantAccessor tenantAccessor, Callable<T> callable) {
        return Iterables.flatMap(tenantAccessor.getAvailableTenants(), tenant -> {
            try {
                Object result = tenantAccessor.asTenant((Tenant)tenant, callable);
                return Collections.singleton(Either.right((Object)(result != null ? Option.some(result) : Option.none())));
            }
            catch (TenantUnavailableException e) {
                return Collections.emptyList();
            }
            catch (Exception e) {
                return Collections.singleton(Either.left((Object)new Failure((Tenant)tenant, e)));
            }
        });
    }

    @Deprecated
    public static Iterable<Failure> forEachTenant(TenantAccessor tenantAccessor, Runnable runnable) {
        return Iterables.flatMap(TenantAccessors.forEachTenant(tenantAccessor, () -> {
            runnable.run();
            return null;
        }), either -> either.left());
    }
}


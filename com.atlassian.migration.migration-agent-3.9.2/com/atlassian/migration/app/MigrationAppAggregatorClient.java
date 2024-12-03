/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.migration.app.dto.check.DisabledCheck
 *  kotlin.Metadata
 *  org.jetbrains.annotations.NotNull
 */
package com.atlassian.migration.app;

import com.atlassian.migration.app.dto.check.DisabledCheck;
import java.util.Set;
import kotlin.Metadata;
import org.jetbrains.annotations.NotNull;

@Metadata(mv={1, 7, 1}, k=1, xi=48, d1={"\u0000\u001a\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\"\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0000\bf\u0018\u00002\u00020\u0001J\u001c\u0010\u0002\u001a\b\u0012\u0004\u0012\u00020\u00040\u00032\f\u0010\u0005\u001a\b\u0012\u0004\u0012\u00020\u00060\u0003H&\u00a8\u0006\u0007"}, d2={"Lcom/atlassian/migration/app/MigrationAppAggregatorClient;", "", "getDisabledAppVendorChecks", "", "Lcom/atlassian/migration/app/dto/check/DisabledCheck;", "serverAppKeys", "", "app-migration-assistant"})
public interface MigrationAppAggregatorClient {
    @NotNull
    public Set<DisabledCheck> getDisabledAppVendorChecks(@NotNull Set<String> var1);
}


/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.migration.app.check.MigrationPlanContext
 *  com.atlassian.migration.app.dto.check.ParentAppPreflightChecksResponse
 *  kotlin.Metadata
 *  org.jetbrains.annotations.NotNull
 */
package com.atlassian.migration.app;

import com.atlassian.migration.app.check.MigrationPlanContext;
import com.atlassian.migration.app.dto.check.ParentAppPreflightChecksResponse;
import java.util.Set;
import kotlin.Metadata;
import org.jetbrains.annotations.NotNull;

@Metadata(mv={1, 7, 1}, k=1, xi=48, d1={"\u0000 \n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\"\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0000\bf\u0018\u00002\u00020\u0001J$\u0010\u0002\u001a\b\u0012\u0004\u0012\u00020\u00040\u00032\u0006\u0010\u0005\u001a\u00020\u00062\f\u0010\u0007\u001a\b\u0012\u0004\u0012\u00020\b0\u0003H&\u00a8\u0006\t"}, d2={"Lcom/atlassian/migration/app/AppPreflightExecutor;", "", "executePreflightChecks", "", "Lcom/atlassian/migration/app/dto/check/ParentAppPreflightChecksResponse;", "context", "Lcom/atlassian/migration/app/check/MigrationPlanContext;", "selectedApps", "", "app-migration-assistant"})
public interface AppPreflightExecutor {
    @NotNull
    public Set<ParentAppPreflightChecksResponse> executePreflightChecks(@NotNull MigrationPlanContext var1, @NotNull Set<String> var2);
}


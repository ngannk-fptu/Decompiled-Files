/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.migration.app.dto.MigrationMappingResponse
 *  kotlin.Metadata
 *  kotlin.jvm.functions.Function1
 *  kotlin.jvm.internal.Intrinsics
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 */
package com.atlassian.migration.app;

import com.atlassian.migration.app.AppMigrationServiceClient;
import com.atlassian.migration.app.DefaultPaginatedMapping;
import com.atlassian.migration.app.DefaultPaginatedMappingKt;
import com.atlassian.migration.app.PaginatedMapping;
import com.atlassian.migration.app.dto.MigrationMappingResponse;
import java.util.Map;
import java.util.Optional;
import kotlin.Metadata;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(mv={1, 7, 1}, k=1, xi=48, d1={"\u00004\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0003\n\u0002\u0010\b\n\u0002\b\u0002\n\u0002\u0010\u000b\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010$\n\u0002\b\u0002\u0018\u00002\u00020\u0001B-\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u0012\u0006\u0010\u0006\u001a\u00020\u0005\u0012\u0006\u0010\u0007\u001a\u00020\u0005\u0012\u0006\u0010\b\u001a\u00020\t\u00a2\u0006\u0002\u0010\nJ\u0014\u0010\u000f\u001a\u000e\u0012\u0004\u0012\u00020\u0005\u0012\u0004\u0012\u00020\u00050\u0010H\u0016J\t\u0010\u0011\u001a\u00020\fH\u0096\u0002R\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0004\u001a\u00020\u0005X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u000b\u001a\u00020\fX\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u0010\u0010\r\u001a\u0004\u0018\u00010\u000eX\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0007\u001a\u00020\u0005X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\b\u001a\u00020\tX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0006\u001a\u00020\u0005X\u0082\u0004\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u0012"}, d2={"Lcom/atlassian/migration/app/DefaultPaginatedMapping;", "Lcom/atlassian/migration/app/PaginatedMapping;", "appMigrationServiceClient", "Lcom/atlassian/migration/app/AppMigrationServiceClient;", "cloudId", "", "transferId", "namespace", "pageSize", "", "(Lcom/atlassian/migration/app/AppMigrationServiceClient;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;I)V", "hasNext", "", "migrationMappingResponse", "Lcom/atlassian/migration/app/dto/MigrationMappingResponse;", "getMapping", "", "next", "app-migration-assistant"})
public final class DefaultPaginatedMapping
implements PaginatedMapping {
    @NotNull
    private final AppMigrationServiceClient appMigrationServiceClient;
    @NotNull
    private final String cloudId;
    @NotNull
    private final String transferId;
    @NotNull
    private final String namespace;
    private final int pageSize;
    @Nullable
    private MigrationMappingResponse migrationMappingResponse;
    private boolean hasNext;

    public DefaultPaginatedMapping(@NotNull AppMigrationServiceClient appMigrationServiceClient, @NotNull String cloudId, @NotNull String transferId, @NotNull String namespace, int pageSize) {
        Intrinsics.checkNotNullParameter((Object)appMigrationServiceClient, (String)"appMigrationServiceClient");
        Intrinsics.checkNotNullParameter((Object)cloudId, (String)"cloudId");
        Intrinsics.checkNotNullParameter((Object)transferId, (String)"transferId");
        Intrinsics.checkNotNullParameter((Object)namespace, (String)"namespace");
        this.appMigrationServiceClient = appMigrationServiceClient;
        this.cloudId = cloudId;
        this.transferId = transferId;
        this.namespace = namespace;
        this.pageSize = pageSize;
        this.hasNext = true;
    }

    @Override
    public boolean next() {
        DefaultPaginatedMappingKt.access$getLog$p().debug("Getting next mapping with hasNext={}", (Object)this.hasNext);
        if (!this.hasNext) {
            return false;
        }
        String lastEntity2 = Optional.ofNullable(this.migrationMappingResponse).map(arg_0 -> DefaultPaginatedMapping.next$lambda$0(next.lastEntity.1.INSTANCE, arg_0)).orElse(null);
        MigrationMappingResponse migrationMappingResponse = this.migrationMappingResponse = this.appMigrationServiceClient.getMigrationMappingByPage(this.cloudId, this.transferId, this.namespace, lastEntity2, this.pageSize);
        Intrinsics.checkNotNull((Object)migrationMappingResponse);
        this.hasNext = migrationMappingResponse.getMeta().hasNext();
        DefaultPaginatedMappingKt.access$getLog$p().debug("Got mapping for lastEntity={}, hasNext={}", (Object)lastEntity2, (Object)this.hasNext);
        MigrationMappingResponse migrationMappingResponse2 = this.migrationMappingResponse;
        Intrinsics.checkNotNull((Object)migrationMappingResponse2);
        return !migrationMappingResponse2.getItems().isEmpty();
    }

    @Override
    @NotNull
    public Map<String, String> getMapping() {
        DefaultPaginatedMappingKt.access$getLog$p().debug("Getting mapping for nameSpace={}, pageSize={}, ", (Object)this.namespace, (Object)this.pageSize);
        if (this.migrationMappingResponse == null) {
            boolean bl = false;
            String string = "Migration mapping has not been initialised. Call next() first before getMapping().";
            throw new IllegalStateException(string.toString());
        }
        MigrationMappingResponse migrationMappingResponse = this.migrationMappingResponse;
        Intrinsics.checkNotNull((Object)migrationMappingResponse);
        return migrationMappingResponse.getItems();
    }

    private static final String next$lambda$0(Function1 $tmp0, Object p0) {
        Intrinsics.checkNotNullParameter((Object)$tmp0, (String)"$tmp0");
        return (String)$tmp0.invoke(p0);
    }
}


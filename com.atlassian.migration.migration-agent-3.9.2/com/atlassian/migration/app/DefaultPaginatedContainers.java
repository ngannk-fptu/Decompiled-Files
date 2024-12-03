/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.migration.app.dto.ContainersByPageResponse
 *  kotlin.Metadata
 *  kotlin.jvm.functions.Function1
 *  kotlin.jvm.internal.Intrinsics
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 */
package com.atlassian.migration.app;

import com.atlassian.migration.app.AppMigrationServiceClient;
import com.atlassian.migration.app.ContainerType;
import com.atlassian.migration.app.ContainerV1;
import com.atlassian.migration.app.DefaultPaginatedContainers;
import com.atlassian.migration.app.DefaultPaginatedContainersKt;
import com.atlassian.migration.app.PaginatedContainers;
import com.atlassian.migration.app.dto.ContainersByPageResponse;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import kotlin.Metadata;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(mv={1, 7, 1}, k=1, xi=48, d1={"\u0000>\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\b\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000b\n\u0000\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0002\b\u0002\u0018\u00002\u00020\u0001B-\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u0012\u0006\u0010\u0006\u001a\u00020\u0005\u0012\u0006\u0010\u0007\u001a\u00020\b\u0012\u0006\u0010\t\u001a\u00020\n\u00a2\u0006\u0002\u0010\u000bJ\u000e\u0010\u0010\u001a\b\u0012\u0004\u0012\u00020\u00120\u0011H\u0016J\t\u0010\u0013\u001a\u00020\u000fH\u0096\u0002R\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0004\u001a\u00020\u0005X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0007\u001a\u00020\bX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0010\u0010\f\u001a\u0004\u0018\u00010\rX\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u000e\u001a\u00020\u000fX\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u000e\u0010\t\u001a\u00020\nX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0006\u001a\u00020\u0005X\u0082\u0004\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u0014"}, d2={"Lcom/atlassian/migration/app/DefaultPaginatedContainers;", "Lcom/atlassian/migration/app/PaginatedContainers;", "appMigrationServiceClient", "Lcom/atlassian/migration/app/AppMigrationServiceClient;", "cloudId", "", "transferId", "containerType", "Lcom/atlassian/migration/app/ContainerType;", "pageSize", "", "(Lcom/atlassian/migration/app/AppMigrationServiceClient;Ljava/lang/String;Ljava/lang/String;Lcom/atlassian/migration/app/ContainerType;I)V", "containersByPageResponse", "Lcom/atlassian/migration/app/dto/ContainersByPageResponse;", "hasNext", "", "getContainers", "", "Lcom/atlassian/migration/app/ContainerV1;", "next", "app-migration-assistant"})
public final class DefaultPaginatedContainers
implements PaginatedContainers {
    @NotNull
    private final AppMigrationServiceClient appMigrationServiceClient;
    @NotNull
    private final String cloudId;
    @NotNull
    private final String transferId;
    @NotNull
    private final ContainerType containerType;
    private final int pageSize;
    @Nullable
    private ContainersByPageResponse containersByPageResponse;
    private boolean hasNext;

    public DefaultPaginatedContainers(@NotNull AppMigrationServiceClient appMigrationServiceClient, @NotNull String cloudId, @NotNull String transferId, @NotNull ContainerType containerType, int pageSize) {
        Intrinsics.checkNotNullParameter((Object)appMigrationServiceClient, (String)"appMigrationServiceClient");
        Intrinsics.checkNotNullParameter((Object)cloudId, (String)"cloudId");
        Intrinsics.checkNotNullParameter((Object)transferId, (String)"transferId");
        Intrinsics.checkNotNullParameter((Object)((Object)containerType), (String)"containerType");
        this.appMigrationServiceClient = appMigrationServiceClient;
        this.cloudId = cloudId;
        this.transferId = transferId;
        this.containerType = containerType;
        this.pageSize = pageSize;
        this.hasNext = true;
    }

    @Override
    public boolean next() {
        DefaultPaginatedContainersKt.access$getLog$p().debug("Getting next container with hasNext={}", (Object)this.hasNext);
        if (!this.hasNext) {
            return false;
        }
        String lastEntity2 = Optional.ofNullable(this.containersByPageResponse).map(arg_0 -> DefaultPaginatedContainers.next$lambda$0(next.lastEntity.1.INSTANCE, arg_0)).orElse(null);
        ContainersByPageResponse containersByPageResponse = this.containersByPageResponse = this.appMigrationServiceClient.getContainersByPage(this.cloudId, this.transferId, this.containerType, lastEntity2, this.pageSize);
        Intrinsics.checkNotNull((Object)containersByPageResponse);
        this.hasNext = containersByPageResponse.getMeta().hasNext();
        DefaultPaginatedContainersKt.access$getLog$p().debug("Got container for lastEntity={}, hasNext={}", (Object)lastEntity2, (Object)this.hasNext);
        ContainersByPageResponse containersByPageResponse2 = this.containersByPageResponse;
        Intrinsics.checkNotNull((Object)containersByPageResponse2);
        return !((Collection)containersByPageResponse2.getContainers()).isEmpty();
    }

    @Override
    @NotNull
    public List<ContainerV1> getContainers() {
        DefaultPaginatedContainersKt.access$getLog$p().debug("Getting containers for containerType={}, pageSize={}, ", (Object)this.containerType, (Object)this.pageSize);
        if (this.containersByPageResponse == null) {
            boolean bl = false;
            String string = "Container pagination has not been initialised. Call next() first before getContainers().";
            throw new IllegalStateException(string.toString());
        }
        ContainersByPageResponse containersByPageResponse = this.containersByPageResponse;
        Intrinsics.checkNotNull((Object)containersByPageResponse);
        return containersByPageResponse.getContainers();
    }

    private static final String next$lambda$0(Function1 $tmp0, Object p0) {
        Intrinsics.checkNotNullParameter((Object)$tmp0, (String)"$tmp0");
        return (String)$tmp0.invoke(p0);
    }
}


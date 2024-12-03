/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.migration.app.dto.CloudFeedbackResponse
 *  com.atlassian.migration.app.dto.ConsentRequest
 *  com.atlassian.migration.app.dto.ConsentResponse
 *  com.atlassian.migration.app.dto.ContainersByPageResponse
 *  com.atlassian.migration.app.dto.FinalizeUploadRequest
 *  com.atlassian.migration.app.dto.GetUploadUrlRequest
 *  com.atlassian.migration.app.dto.GetUrlResponse
 *  com.atlassian.migration.app.dto.InitializeUploadResponse
 *  com.atlassian.migration.app.dto.MigrationMappingResponse
 *  com.atlassian.migration.app.dto.RegisterForgeTransferRequest
 *  com.atlassian.migration.app.dto.RegisterTransferRequest
 *  com.atlassian.migration.app.dto.RegisterTransferRerunRequest
 *  com.atlassian.migration.app.dto.RerunEnablementDto
 *  com.atlassian.migration.app.dto.RerunTransferResponse
 *  com.atlassian.migration.app.dto.TransferErrorRequest
 *  kotlin.Metadata
 *  kotlin.jvm.internal.Intrinsics
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 */
package com.atlassian.migration.app;

import com.atlassian.migration.app.ContainerType;
import com.atlassian.migration.app.MigrationDetailsV1;
import com.atlassian.migration.app.dto.CloudFeedbackResponse;
import com.atlassian.migration.app.dto.ConsentRequest;
import com.atlassian.migration.app.dto.ConsentResponse;
import com.atlassian.migration.app.dto.ContainersByPageResponse;
import com.atlassian.migration.app.dto.FinalizeUploadRequest;
import com.atlassian.migration.app.dto.GetUploadUrlRequest;
import com.atlassian.migration.app.dto.GetUrlResponse;
import com.atlassian.migration.app.dto.InitializeUploadResponse;
import com.atlassian.migration.app.dto.MigrationMappingResponse;
import com.atlassian.migration.app.dto.RegisterForgeTransferRequest;
import com.atlassian.migration.app.dto.RegisterTransferRequest;
import com.atlassian.migration.app.dto.RegisterTransferRerunRequest;
import com.atlassian.migration.app.dto.RerunEnablementDto;
import com.atlassian.migration.app.dto.RerunTransferResponse;
import com.atlassian.migration.app.dto.TransferErrorRequest;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(mv={1, 7, 1}, k=1, xi=48, d1={"\u0000\u00aa\u0001\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0010\u000e\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\b\n\u0000\n\u0002\u0010$\n\u0002\b\u0002\n\u0002\u0010\"\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0010\u0012\n\u0000\bf\u0018\u00002\u00020\u0001J\u0018\u0010\u0002\u001a\u00020\u00032\u0006\u0010\u0004\u001a\u00020\u00052\u0006\u0010\u0002\u001a\u00020\u0006H&J\u0018\u0010\u0007\u001a\u00020\b2\u0006\u0010\u0004\u001a\u00020\u00052\u0006\u0010\t\u001a\u00020\u0005H&J \u0010\n\u001a\u00020\u000b2\u0006\u0010\u0004\u001a\u00020\u00052\u0006\u0010\f\u001a\u00020\u00052\u0006\u0010\r\u001a\u00020\u0005H&J2\u0010\u000e\u001a\u00020\u000f2\u0006\u0010\u0004\u001a\u00020\u00052\u0006\u0010\t\u001a\u00020\u00052\u0006\u0010\u0010\u001a\u00020\u00112\b\u0010\u0012\u001a\u0004\u0018\u00010\u00052\u0006\u0010\u0013\u001a\u00020\u0014H&J:\u0010\u0015\u001a\u000e\u0012\u0004\u0012\u00020\u0005\u0012\u0004\u0012\u00020\u00050\u00162\u0006\u0010\u0004\u001a\u00020\u00052\u0006\u0010\t\u001a\u00020\u00052\u0006\u0010\u0017\u001a\u00020\u00052\f\u0010\u0018\u001a\b\u0012\u0004\u0012\u00020\u00050\u0019H&J\u0018\u0010\u001a\u001a\u00020\u001b2\u0006\u0010\u0004\u001a\u00020\u00052\u0006\u0010\u001c\u001a\u00020\u0005H&J2\u0010\u001d\u001a\u00020\u001e2\u0006\u0010\u0004\u001a\u00020\u00052\u0006\u0010\t\u001a\u00020\u00052\u0006\u0010\u0017\u001a\u00020\u00052\b\u0010\u0012\u001a\u0004\u0018\u00010\u00052\u0006\u0010\u0013\u001a\u00020\u0014H&J\u0018\u0010\u001f\u001a\u00020 2\u0006\u0010\u0004\u001a\u00020\u00052\u0006\u0010!\u001a\u00020\"H&J&\u0010#\u001a\u00020$2\u0006\u0010\u0004\u001a\u00020\u00052\u0006\u0010\t\u001a\u00020\u00052\f\u0010%\u001a\b\u0012\u0004\u0012\u00020\u00050&H&J\u0018\u0010'\u001a\u00020(2\u0006\u0010\u0004\u001a\u00020\u00052\u0006\u0010)\u001a\u00020\u0005H&J\u0018\u0010*\u001a\u00020\u00032\u0006\u0010\u0004\u001a\u00020\u00052\u0006\u0010\t\u001a\u00020\u0005H&J \u0010+\u001a\u00020\u00032\u0006\u0010\u0004\u001a\u00020\u00052\u0006\u0010\t\u001a\u00020\u00052\u0006\u0010,\u001a\u00020-H&J \u0010.\u001a\u00020/2\u0006\u0010\u0004\u001a\u00020\u00052\u0006\u0010)\u001a\u00020\u00052\u0006\u00100\u001a\u000201H\u0016J \u00102\u001a\u0002032\u0006\u0010\u0004\u001a\u00020\u00052\u0006\u0010)\u001a\u00020\u00052\u0006\u00100\u001a\u000204H&J \u00100\u001a\u00020/2\u0006\u0010\u0004\u001a\u00020\u00052\u0006\u0010)\u001a\u00020\u00052\u0006\u00100\u001a\u000205H&J \u00106\u001a\u00020\u000b2\u0006\u0010\u0004\u001a\u00020\u00052\u0006\u0010\f\u001a\u00020\u00052\u0006\u00107\u001a\u000208H&J \u00109\u001a\u00020\u00052\u0006\u0010:\u001a\u00020\u00052\u0006\u0010;\u001a\u00020\u00052\u0006\u0010<\u001a\u00020=H&\u00a8\u0006>"}, d2={"Lcom/atlassian/migration/app/AppMigrationServiceClient;", "", "finalizeUpload", "", "cloudId", "", "Lcom/atlassian/migration/app/dto/FinalizeUploadRequest;", "getCloudFeedback", "Lcom/atlassian/migration/app/dto/CloudFeedbackResponse;", "transferId", "getConsent", "Lcom/atlassian/migration/app/dto/ConsentResponse;", "consentKey", "sen", "getContainersByPage", "Lcom/atlassian/migration/app/dto/ContainersByPageResponse;", "containerType", "Lcom/atlassian/migration/app/ContainerType;", "lastEntity", "pageSize", "", "getMappingById", "", "namespace", "ids", "", "getMigrationDetailsV1", "Lcom/atlassian/migration/app/MigrationDetailsV1;", "migrationId", "getMigrationMappingByPage", "Lcom/atlassian/migration/app/dto/MigrationMappingResponse;", "getMultipartUploadUrl", "Lcom/atlassian/migration/app/dto/GetUrlResponse;", "getUrlRequest", "Lcom/atlassian/migration/app/dto/GetUploadUrlRequest;", "initializeUpload", "Lcom/atlassian/migration/app/dto/InitializeUploadResponse;", "label", "Ljava/util/Optional;", "isRerunEnabled", "Lcom/atlassian/migration/app/dto/RerunEnablementDto;", "containerId", "notifyListenerTriggered", "recordTransferError", "transferError", "Lcom/atlassian/migration/app/dto/TransferErrorRequest;", "registerForgeTransfer", "Ljava/util/UUID;", "registerTransfer", "Lcom/atlassian/migration/app/dto/RegisterForgeTransferRequest;", "registerRerunTransfer", "Lcom/atlassian/migration/app/dto/RerunTransferResponse;", "Lcom/atlassian/migration/app/dto/RegisterTransferRerunRequest;", "Lcom/atlassian/migration/app/dto/RegisterTransferRequest;", "saveConsent", "consent", "Lcom/atlassian/migration/app/dto/ConsentRequest;", "uploadToS3", "s3SignedUrl", "contentMd5", "bytes", "", "app-migration-assistant"})
public interface AppMigrationServiceClient {
    @NotNull
    public UUID registerTransfer(@NotNull String var1, @NotNull String var2, @NotNull RegisterTransferRequest var3);

    @NotNull
    public UUID registerForgeTransfer(@NotNull String var1, @NotNull String var2, @NotNull RegisterForgeTransferRequest var3);

    @NotNull
    public RerunTransferResponse registerRerunTransfer(@NotNull String var1, @NotNull String var2, @NotNull RegisterTransferRerunRequest var3);

    @NotNull
    public CloudFeedbackResponse getCloudFeedback(@NotNull String var1, @NotNull String var2);

    @NotNull
    public InitializeUploadResponse initializeUpload(@NotNull String var1, @NotNull String var2, @NotNull Optional<String> var3);

    @NotNull
    public GetUrlResponse getMultipartUploadUrl(@NotNull String var1, @NotNull GetUploadUrlRequest var2);

    @NotNull
    public String uploadToS3(@NotNull String var1, @NotNull String var2, @NotNull byte[] var3);

    public void finalizeUpload(@NotNull String var1, @NotNull FinalizeUploadRequest var2);

    @NotNull
    public MigrationDetailsV1 getMigrationDetailsV1(@NotNull String var1, @NotNull String var2);

    @NotNull
    public MigrationMappingResponse getMigrationMappingByPage(@NotNull String var1, @NotNull String var2, @NotNull String var3, @Nullable String var4, int var5);

    @NotNull
    public Map<String, String> getMappingById(@NotNull String var1, @NotNull String var2, @NotNull String var3, @NotNull Set<String> var4);

    @NotNull
    public ContainersByPageResponse getContainersByPage(@NotNull String var1, @NotNull String var2, @NotNull ContainerType var3, @Nullable String var4, int var5);

    @NotNull
    public ConsentResponse saveConsent(@NotNull String var1, @NotNull String var2, @NotNull ConsentRequest var3);

    @NotNull
    public ConsentResponse getConsent(@NotNull String var1, @NotNull String var2, @NotNull String var3);

    public void recordTransferError(@NotNull String var1, @NotNull String var2, @NotNull TransferErrorRequest var3);

    public void notifyListenerTriggered(@NotNull String var1, @NotNull String var2);

    @NotNull
    public RerunEnablementDto isRerunEnabled(@NotNull String var1, @NotNull String var2);

    @Metadata(mv={1, 7, 1}, k=3, xi=48)
    public static final class DefaultImpls {
        @NotNull
        public static UUID registerForgeTransfer(@NotNull AppMigrationServiceClient $this, @NotNull String cloudId, @NotNull String containerId, @NotNull RegisterForgeTransferRequest registerTransfer) {
            Intrinsics.checkNotNullParameter((Object)cloudId, (String)"cloudId");
            Intrinsics.checkNotNullParameter((Object)containerId, (String)"containerId");
            Intrinsics.checkNotNullParameter((Object)registerTransfer, (String)"registerTransfer");
            UUID uUID = UUID.randomUUID();
            Intrinsics.checkNotNullExpressionValue((Object)uUID, (String)"randomUUID()");
            return uUID;
        }
    }
}


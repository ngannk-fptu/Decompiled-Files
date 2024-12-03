/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.migration.app.check.CheckResult
 *  com.atlassian.migration.app.check.MigrationPlanContext
 *  com.atlassian.migration.app.dto.check.AppPreflightCheckInternalResponse
 *  com.atlassian.migration.app.dto.check.CheckDetail
 *  com.atlassian.migration.app.dto.check.CheckStatus
 *  com.atlassian.migration.app.dto.check.ParentPreflightCheckSpec
 *  com.atlassian.migration.app.dto.check.PreflightCheckSpec
 *  com.atlassian.migration.app.dto.check.VendorCheckRepositoryProxy
 *  kotlin.Metadata
 *  kotlin.collections.CollectionsKt
 *  kotlin.collections.SetsKt
 *  kotlin.comparisons.ComparisonsKt
 *  kotlin.jvm.internal.Intrinsics
 *  org.jetbrains.annotations.NotNull
 */
package com.atlassian.migration.app;

import com.atlassian.migration.app.AsyncExecutionContext;
import com.atlassian.migration.app.CopyCsvContentResult;
import com.atlassian.migration.app.OsgiBundleHelper;
import com.atlassian.migration.app.VendorCheckExecutorKt;
import com.atlassian.migration.app.check.CheckResult;
import com.atlassian.migration.app.check.MigrationPlanContext;
import com.atlassian.migration.app.dto.check.AppPreflightCheckInternalResponse;
import com.atlassian.migration.app.dto.check.CheckDetail;
import com.atlassian.migration.app.dto.check.CheckStatus;
import com.atlassian.migration.app.dto.check.ParentPreflightCheckSpec;
import com.atlassian.migration.app.dto.check.PreflightCheckSpec;
import com.atlassian.migration.app.dto.check.VendorCheckRepositoryProxy;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import kotlin.Metadata;
import kotlin.collections.CollectionsKt;
import kotlin.collections.SetsKt;
import kotlin.comparisons.ComparisonsKt;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;

@Metadata(mv={1, 7, 1}, k=1, xi=48, d1={"\u0000\\\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\b\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\t\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\"\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0003\u0018\u00002\u00020\u0001B-\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u0012\u0006\u0010\u0006\u001a\u00020\u0005\u0012\u0006\u0010\u0007\u001a\u00020\b\u0012\u0006\u0010\t\u001a\u00020\n\u00a2\u0006\u0002\u0010\u000bJ\u001e\u0010\u000f\u001a\b\u0012\u0004\u0012\u00020\u00110\u00102\u0006\u0010\u0012\u001a\u00020\u00132\u0006\u0010\u0014\u001a\u00020\u0015H\u0002J\u0018\u0010\u0016\u001a\u00020\u00172\u0006\u0010\u0018\u001a\u00020\u00112\u0006\u0010\u0014\u001a\u00020\u0015H\u0002J\"\u0010\u0019\u001a\b\u0012\u0004\u0012\u00020\u00170\u001a2\u0006\u0010\u0014\u001a\u00020\u00152\f\u0010\u001b\u001a\b\u0012\u0004\u0012\u00020\u00130\u001aJ\u0010\u0010\u001c\u001a\u00020\b2\u0006\u0010\u001d\u001a\u00020\u001eH\u0002J\u0018\u0010\u001f\u001a\u00020\u00172\u0006\u0010 \u001a\u00020\u00112\u0006\u0010\u001d\u001a\u00020\u001eH\u0002R\u000e\u0010\u0004\u001a\u00020\u0005X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0016\u0010\f\u001a\n \u000e*\u0004\u0018\u00010\r0\rX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0007\u001a\u00020\bX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\t\u001a\u00020\nX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0006\u001a\u00020\u0005X\u0082\u0004\u00a2\u0006\u0002\n\u0000\u00a8\u0006!"}, d2={"Lcom/atlassian/migration/app/VendorCheckExecutor;", "", "parallelExecutions", "", "checkTimeout", "Ljava/time/Duration;", "totalTimeout", "maxCSVFileSize", "", "osgiBundleHelper", "Lcom/atlassian/migration/app/OsgiBundleHelper;", "(ILjava/time/Duration;Ljava/time/Duration;JLcom/atlassian/migration/app/OsgiBundleHelper;)V", "executor", "Ljava/util/concurrent/ScheduledExecutorService;", "kotlin.jvm.PlatformType", "breakDownCallables", "", "Lcom/atlassian/migration/app/AsyncExecutionContext;", "parentRequest", "Lcom/atlassian/migration/app/dto/check/ParentPreflightCheckSpec;", "context", "Lcom/atlassian/migration/app/check/MigrationPlanContext;", "executeCheck", "Lcom/atlassian/migration/app/dto/check/AppPreflightCheckInternalResponse;", "executionContext", "executePreflightChecks", "", "appPreflightChecksRequests", "getAvailableTimeForExecution", "deadline", "Ljava/time/Instant;", "retrieveResponses", "it", "app-migration-assistant"})
public final class VendorCheckExecutor {
    @NotNull
    private final Duration checkTimeout;
    @NotNull
    private final Duration totalTimeout;
    private final long maxCSVFileSize;
    @NotNull
    private final OsgiBundleHelper osgiBundleHelper;
    private final ScheduledExecutorService executor;

    public VendorCheckExecutor(int parallelExecutions, @NotNull Duration checkTimeout, @NotNull Duration totalTimeout, long maxCSVFileSize, @NotNull OsgiBundleHelper osgiBundleHelper) {
        Intrinsics.checkNotNullParameter((Object)checkTimeout, (String)"checkTimeout");
        Intrinsics.checkNotNullParameter((Object)totalTimeout, (String)"totalTimeout");
        Intrinsics.checkNotNullParameter((Object)osgiBundleHelper, (String)"osgiBundleHelper");
        this.checkTimeout = checkTimeout;
        this.totalTimeout = totalTimeout;
        this.maxCSVFileSize = maxCSVFileSize;
        this.osgiBundleHelper = osgiBundleHelper;
        this.executor = Executors.newScheduledThreadPool(parallelExecutions);
    }

    /*
     * WARNING - void declaration
     */
    @NotNull
    public final Set<AppPreflightCheckInternalResponse> executePreflightChecks(@NotNull MigrationPlanContext context, @NotNull Set<ParentPreflightCheckSpec> appPreflightChecksRequests) {
        void $this$mapTo$iv$iv;
        void $this$map$iv;
        Object list$iv$iv;
        ParentPreflightCheckSpec it;
        void $this$flatMapTo$iv$iv;
        Iterable $this$flatMap$iv;
        Intrinsics.checkNotNullParameter((Object)context, (String)"context");
        Intrinsics.checkNotNullParameter(appPreflightChecksRequests, (String)"appPreflightChecksRequests");
        Instant deadline = Instant.now().plus(this.totalTimeout);
        Iterable iterable = appPreflightChecksRequests;
        boolean $i$f$flatMap = false;
        void var6_6 = $this$flatMap$iv;
        Collection destination$iv$iv = new ArrayList();
        boolean $i$f$flatMapTo = false;
        for (Object element$iv$iv : $this$flatMapTo$iv$iv) {
            it = (ParentPreflightCheckSpec)element$iv$iv;
            boolean bl = false;
            list$iv$iv = this.breakDownCallables(it, context);
            CollectionsKt.addAll((Collection)destination$iv$iv, (Iterable)list$iv$iv);
        }
        $this$flatMap$iv = (List)destination$iv$iv;
        boolean $i$f$map = false;
        $this$flatMapTo$iv$iv = $this$map$iv;
        destination$iv$iv = new ArrayList(CollectionsKt.collectionSizeOrDefault((Iterable)$this$map$iv, (int)10));
        boolean $i$f$mapTo = false;
        for (Object item$iv$iv : $this$mapTo$iv$iv) {
            list$iv$iv = (AsyncExecutionContext)item$iv$iv;
            Collection collection = destination$iv$iv;
            boolean bl = false;
            Intrinsics.checkNotNullExpressionValue((Object)deadline, (String)"deadline");
            collection.add(this.retrieveResponses((AsyncExecutionContext)it, deadline));
        }
        return CollectionsKt.toSet((Iterable)((List)destination$iv$iv));
    }

    /*
     * WARNING - void declaration
     */
    private final List<AsyncExecutionContext> breakDownCallables(ParentPreflightCheckSpec parentRequest, MigrationPlanContext context) {
        void $this$mapTo$iv$iv;
        Map.Entry entry = (Map.Entry)CollectionsKt.firstOrNull((Iterable)this.osgiBundleHelper.getVendorChecks(SetsKt.setOf((Object)parentRequest.getServerAppKey())).entrySet());
        if (entry == null || (entry = (VendorCheckRepositoryProxy)entry.getValue()) == null) {
            throw new IllegalArgumentException("No check repository found for " + parentRequest.getServerAppKey());
        }
        Map.Entry proxy = entry;
        Iterable $this$map$iv = parentRequest.getPreflightCheckSpecs();
        boolean $i$f$map = false;
        Iterable iterable = $this$map$iv;
        Collection destination$iv$iv = new ArrayList(CollectionsKt.collectionSizeOrDefault((Iterable)$this$map$iv, (int)10));
        boolean $i$f$mapTo = false;
        for (Object item$iv$iv : $this$mapTo$iv$iv) {
            void checkSpec;
            AsyncExecutionContext asyncExecutionContext;
            PreflightCheckSpec preflightCheckSpec = (PreflightCheckSpec)item$iv$iv;
            Collection collection = destination$iv$iv;
            boolean bl = false;
            AsyncExecutionContext $this$breakDownCallables_u24lambda_u244_u24lambda_u243 = asyncExecutionContext = new AsyncExecutionContext(parentRequest.getServerAppKey(), checkSpec.getPreflightCheckId(), (VendorCheckRepositoryProxy)proxy, null, 8, null);
            boolean bl2 = false;
            $this$breakDownCallables_u24lambda_u244_u24lambda_u243.setFuture(this.executor.submit(() -> VendorCheckExecutor.breakDownCallables$lambda$4$lambda$3$lambda$2(this, $this$breakDownCallables_u24lambda_u244_u24lambda_u243, context)));
            collection.add(asyncExecutionContext);
        }
        return (List)destination$iv$iv;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private final AppPreflightCheckInternalResponse retrieveResponses(AsyncExecutionContext it, Instant deadline) {
        AppPreflightCheckInternalResponse appPreflightCheckInternalResponse;
        try {
            VendorCheckExecutorKt.access$getLog$p().info("Waiting for check {} with {}ms timeout", (Object)it.getCheckId(), (Object)this.getAvailableTimeForExecution(deadline));
            Future<AppPreflightCheckInternalResponse> future = it.getFuture();
            Intrinsics.checkNotNull(future);
            AppPreflightCheckInternalResponse appPreflightCheckInternalResponse2 = future.get(this.getAvailableTimeForExecution(deadline), TimeUnit.MILLISECONDS);
            Intrinsics.checkNotNullExpressionValue((Object)appPreflightCheckInternalResponse2, (String)"{\n            log.info(\"\u2026t.MILLISECONDS)\n        }");
            appPreflightCheckInternalResponse = appPreflightCheckInternalResponse2;
        }
        catch (TimeoutException e) {
            Object[] objectArray = new Object[]{it.getAppKey(), it.getCheckId(), e};
            VendorCheckExecutorKt.access$getLog$p().warn("Vendor check timed out for appKey={} checkId={}", objectArray);
            appPreflightCheckInternalResponse = new AppPreflightCheckInternalResponse(it.getAppKey(), it.getCheckId(), CheckStatus.CHECK_EXECUTION_ERROR, null, null, SetsKt.setOf((Object)CheckDetail.TIMEOUT), 24, null);
        }
        catch (Exception e) {
            Object[] objectArray = new Object[]{it.getAppKey(), it.getCheckId(), e};
            VendorCheckExecutorKt.access$getLog$p().warn("Vendor check failed for appKey={} checkId={}", objectArray);
            appPreflightCheckInternalResponse = new AppPreflightCheckInternalResponse(it.getAppKey(), it.getCheckId(), CheckStatus.CHECK_EXECUTION_ERROR, null, null, SetsKt.setOf((Object)CheckDetail.EXCEPTION), 24, null);
        }
        finally {
            Future<AppPreflightCheckInternalResponse> future = it.getFuture();
            Intrinsics.checkNotNull(future);
            future.cancel(true);
        }
        return appPreflightCheckInternalResponse;
    }

    private final long getAvailableTimeForExecution(Instant deadline) {
        return Math.max(((Duration)ComparisonsKt.minOf((Comparable)this.checkTimeout, (Comparable)Duration.between(Instant.now(), deadline))).toMillis(), 0L);
    }

    private final AppPreflightCheckInternalResponse executeCheck(AsyncExecutionContext executionContext, MigrationPlanContext context) {
        AsyncExecutionContext $this$executeCheck_u24lambda_u245 = executionContext;
        boolean bl = false;
        CheckResult executePreflightCheck = $this$executeCheck_u24lambda_u245.getProxy().executeCheck($this$executeCheck_u24lambda_u245.getCheckId(), context);
        CopyCsvContentResult copyResult = VendorCheckExecutorKt.access$copyCsvContentWithLimitsApplied(executePreflightCheck.getCsvFileContent(), this.maxCSVFileSize);
        Set checkDetails = copyResult.getTruncated() ? SetsKt.setOf((Object)CheckDetail.CSV_TRUNCATED) : null;
        return new AppPreflightCheckInternalResponse($this$executeCheck_u24lambda_u245.getAppKey(), $this$executeCheck_u24lambda_u245.getCheckId(), CheckStatus.valueOf((String)executePreflightCheck.getStatus().name()), executePreflightCheck.getStepsToResolveKey(), copyResult.getCsvFileContent(), checkDetails);
    }

    private static final AppPreflightCheckInternalResponse breakDownCallables$lambda$4$lambda$3$lambda$2(VendorCheckExecutor this$0, AsyncExecutionContext $this_apply, MigrationPlanContext $context) {
        Intrinsics.checkNotNullParameter((Object)this$0, (String)"this$0");
        Intrinsics.checkNotNullParameter((Object)$this_apply, (String)"$this_apply");
        Intrinsics.checkNotNullParameter((Object)$context, (String)"$context");
        return this$0.executeCheck($this_apply, $context);
    }
}


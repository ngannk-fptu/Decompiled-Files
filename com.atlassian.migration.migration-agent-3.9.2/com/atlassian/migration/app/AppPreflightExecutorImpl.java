/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.migration.app.check.MigrationPlanContext
 *  com.atlassian.migration.app.dto.check.AppPreflightCheckInternalResponse
 *  com.atlassian.migration.app.dto.check.AppPreflightCheckResponse
 *  com.atlassian.migration.app.dto.check.AppVendorCheckProperties
 *  com.atlassian.migration.app.dto.check.DisabledCheck
 *  com.atlassian.migration.app.dto.check.ParentAppPreflightChecksResponse
 *  com.atlassian.migration.app.dto.check.ParentPreflightCheckSpec
 *  com.atlassian.migration.app.dto.check.PreflightCheckSpec
 *  kotlin.Metadata
 *  kotlin.Pair
 *  kotlin.TuplesKt
 *  kotlin.collections.CollectionsKt
 *  kotlin.collections.MapsKt
 *  kotlin.collections.SetsKt
 *  kotlin.jvm.internal.Intrinsics
 *  kotlin.ranges.RangesKt
 *  org.jetbrains.annotations.NotNull
 */
package com.atlassian.migration.app;

import com.atlassian.migration.app.AppMigrationDarkFeatures;
import com.atlassian.migration.app.AppPreflightExecutor;
import com.atlassian.migration.app.AppPreflightExecutorImplKt;
import com.atlassian.migration.app.MigrationAppAggregatorClient;
import com.atlassian.migration.app.OsgiBundleHelper;
import com.atlassian.migration.app.VendorCheckExecutor;
import com.atlassian.migration.app.VendorCheckRetriever;
import com.atlassian.migration.app.check.MigrationPlanContext;
import com.atlassian.migration.app.dto.check.AppPreflightCheckInternalResponse;
import com.atlassian.migration.app.dto.check.AppPreflightCheckResponse;
import com.atlassian.migration.app.dto.check.AppVendorCheckProperties;
import com.atlassian.migration.app.dto.check.DisabledCheck;
import com.atlassian.migration.app.dto.check.ParentAppPreflightChecksResponse;
import com.atlassian.migration.app.dto.check.ParentPreflightCheckSpec;
import com.atlassian.migration.app.dto.check.PreflightCheckSpec;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import kotlin.Metadata;
import kotlin.Pair;
import kotlin.TuplesKt;
import kotlin.collections.CollectionsKt;
import kotlin.collections.MapsKt;
import kotlin.collections.SetsKt;
import kotlin.jvm.internal.Intrinsics;
import kotlin.ranges.RangesKt;
import org.jetbrains.annotations.NotNull;

@Metadata(mv={1, 7, 1}, k=1, xi=48, d1={"\u0000n\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\"\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0003\n\u0002\u0010$\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0006\u0018\u00002\u00020\u0001B'\b\u0016\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u0012\u0006\u0010\u0006\u001a\u00020\u0007\u0012\u0006\u0010\b\u001a\u00020\t\u00a2\u0006\u0002\u0010\nB%\u0012\u0006\u0010\u000b\u001a\u00020\f\u0012\u0006\u0010\r\u001a\u00020\u000e\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u0012\u0006\u0010\u0006\u001a\u00020\u0007\u00a2\u0006\u0002\u0010\u000fJ\u0010\u0010\u0010\u001a\u00020\u00112\u0006\u0010\u0012\u001a\u00020\u0013H\u0002J\u001c\u0010\u0014\u001a\b\u0012\u0004\u0012\u00020\u00160\u00152\f\u0010\u0017\u001a\b\u0012\u0004\u0012\u00020\u00160\u0015H\u0002J$\u0010\u0018\u001a\b\u0012\u0004\u0012\u00020\u00190\u00152\u0006\u0010\u001a\u001a\u00020\u001b2\f\u0010\u001c\u001a\b\u0012\u0004\u0012\u00020\u001d0\u0015H\u0016J6\u0010\u001e\u001a\b\u0012\u0004\u0012\u00020\u00160\u00152\f\u0010\u001f\u001a\b\u0012\u0004\u0012\u00020\u00160\u00152\u0018\u0010 \u001a\u0014\u0012\u0004\u0012\u00020\u001d\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u001d0\u00150!H\u0002J(\u0010\"\u001a\u0014\u0012\u0004\u0012\u00020\u001d\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u001d0\u00150!2\f\u0010\u001c\u001a\b\u0012\u0004\u0012\u00020\u001d0\u0015H\u0002J\u001a\u0010#\u001a\u0004\u0018\u00010$2\u0006\u0010%\u001a\u00020\u001d2\u0006\u0010&\u001a\u00020\u001dH\u0002J$\u0010'\u001a\u0004\u0018\u00010\u001d2\u0006\u0010%\u001a\u00020\u001d2\u0006\u0010&\u001a\u00020\u001d2\b\u0010(\u001a\u0004\u0018\u00010\u001dH\u0002J\u0018\u0010)\u001a\u00020\u001d2\u0006\u0010%\u001a\u00020\u001d2\u0006\u0010&\u001a\u00020\u001dH\u0002R\u000e\u0010\u0006\u001a\u00020\u0007X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0004\u001a\u00020\u0005X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\r\u001a\u00020\u000eX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u000b\u001a\u00020\fX\u0082\u0004\u00a2\u0006\u0002\n\u0000\u00a8\u0006*"}, d2={"Lcom/atlassian/migration/app/AppPreflightExecutorImpl;", "Lcom/atlassian/migration/app/AppPreflightExecutor;", "osgiBundleHelper", "Lcom/atlassian/migration/app/OsgiBundleHelper;", "migrationAppAggregatorClient", "Lcom/atlassian/migration/app/MigrationAppAggregatorClient;", "appMigrationDarkFeatures", "Lcom/atlassian/migration/app/AppMigrationDarkFeatures;", "appVendorCheckProperties", "Lcom/atlassian/migration/app/dto/check/AppVendorCheckProperties;", "(Lcom/atlassian/migration/app/OsgiBundleHelper;Lcom/atlassian/migration/app/MigrationAppAggregatorClient;Lcom/atlassian/migration/app/AppMigrationDarkFeatures;Lcom/atlassian/migration/app/dto/check/AppVendorCheckProperties;)V", "vendorCheckRetriever", "Lcom/atlassian/migration/app/VendorCheckRetriever;", "vendorCheckExecutor", "Lcom/atlassian/migration/app/VendorCheckExecutor;", "(Lcom/atlassian/migration/app/VendorCheckRetriever;Lcom/atlassian/migration/app/VendorCheckExecutor;Lcom/atlassian/migration/app/MigrationAppAggregatorClient;Lcom/atlassian/migration/app/AppMigrationDarkFeatures;)V", "appPreflightCheckResponse", "Lcom/atlassian/migration/app/dto/check/AppPreflightCheckResponse;", "internalResponse", "Lcom/atlassian/migration/app/dto/check/AppPreflightCheckInternalResponse;", "applyCheckNumberLimitOnAppChecks", "", "Lcom/atlassian/migration/app/dto/check/ParentPreflightCheckSpec;", "filteredAppVendorCheckSpecs", "executePreflightChecks", "Lcom/atlassian/migration/app/dto/check/ParentAppPreflightChecksResponse;", "context", "Lcom/atlassian/migration/app/check/MigrationPlanContext;", "selectedApps", "", "filterDisabledCheckSpecs", "parentPreflightCheckSpecs", "disabledChecksMap", "", "retrieveDisabledAppVendorChecks", "retrieveSpecForCheckId", "Lcom/atlassian/migration/app/dto/check/PreflightCheckSpec;", "serverAppKey", "checkId", "retrieveStepsToResolve", "nextStepsKey", "retrieveTitle", "app-migration-assistant"})
public final class AppPreflightExecutorImpl
implements AppPreflightExecutor {
    @NotNull
    private final VendorCheckRetriever vendorCheckRetriever;
    @NotNull
    private final VendorCheckExecutor vendorCheckExecutor;
    @NotNull
    private final MigrationAppAggregatorClient migrationAppAggregatorClient;
    @NotNull
    private final AppMigrationDarkFeatures appMigrationDarkFeatures;

    public AppPreflightExecutorImpl(@NotNull VendorCheckRetriever vendorCheckRetriever, @NotNull VendorCheckExecutor vendorCheckExecutor, @NotNull MigrationAppAggregatorClient migrationAppAggregatorClient, @NotNull AppMigrationDarkFeatures appMigrationDarkFeatures) {
        Intrinsics.checkNotNullParameter((Object)vendorCheckRetriever, (String)"vendorCheckRetriever");
        Intrinsics.checkNotNullParameter((Object)vendorCheckExecutor, (String)"vendorCheckExecutor");
        Intrinsics.checkNotNullParameter((Object)migrationAppAggregatorClient, (String)"migrationAppAggregatorClient");
        Intrinsics.checkNotNullParameter((Object)appMigrationDarkFeatures, (String)"appMigrationDarkFeatures");
        this.vendorCheckRetriever = vendorCheckRetriever;
        this.vendorCheckExecutor = vendorCheckExecutor;
        this.migrationAppAggregatorClient = migrationAppAggregatorClient;
        this.appMigrationDarkFeatures = appMigrationDarkFeatures;
    }

    public AppPreflightExecutorImpl(@NotNull OsgiBundleHelper osgiBundleHelper, @NotNull MigrationAppAggregatorClient migrationAppAggregatorClient, @NotNull AppMigrationDarkFeatures appMigrationDarkFeatures, @NotNull AppVendorCheckProperties appVendorCheckProperties) {
        Intrinsics.checkNotNullParameter((Object)osgiBundleHelper, (String)"osgiBundleHelper");
        Intrinsics.checkNotNullParameter((Object)migrationAppAggregatorClient, (String)"migrationAppAggregatorClient");
        Intrinsics.checkNotNullParameter((Object)appMigrationDarkFeatures, (String)"appMigrationDarkFeatures");
        Intrinsics.checkNotNullParameter((Object)appVendorCheckProperties, (String)"appVendorCheckProperties");
        VendorCheckRetriever vendorCheckRetriever = new VendorCheckRetriever(osgiBundleHelper);
        Duration duration = Duration.ofMinutes(appVendorCheckProperties.getEachCheckTimeoutInMinutes());
        Intrinsics.checkNotNullExpressionValue((Object)duration, (String)"ofMinutes(appVendorCheck\u2026imeoutInMinutes.toLong())");
        Duration duration2 = Duration.ofMinutes(appVendorCheckProperties.getGlobalCheckTimeoutInMinutes());
        Intrinsics.checkNotNullExpressionValue((Object)duration2, (String)"ofMinutes(appVendorCheck\u2026imeoutInMinutes.toLong())");
        this(vendorCheckRetriever, new VendorCheckExecutor(4, duration, duration2, appVendorCheckProperties.getMaxCSVFileSize(), osgiBundleHelper), migrationAppAggregatorClient, appMigrationDarkFeatures);
    }

    /*
     * WARNING - void declaration
     */
    @Override
    @NotNull
    public Set<ParentAppPreflightChecksResponse> executePreflightChecks(@NotNull MigrationPlanContext context, @NotNull Set<String> selectedApps) {
        void $this$mapTo$iv$iv;
        void $this$map$iv;
        Object list$iv$iv;
        Object key$iv$iv;
        void $this$groupByTo$iv$iv;
        Map $this$groupBy$iv;
        Set<ParentPreflightCheckSpec> set;
        ParentPreflightCheckSpec it;
        boolean bl;
        Set<ParentPreflightCheckSpec> parentPreflightCheckSpecs;
        block11: {
            Intrinsics.checkNotNullParameter((Object)context, (String)"context");
            Intrinsics.checkNotNullParameter(selectedApps, (String)"selectedApps");
            parentPreflightCheckSpecs = this.vendorCheckRetriever.retrievePreflightSpecs$app_migration_assistant(selectedApps);
            Iterable $this$all$iv = parentPreflightCheckSpecs;
            boolean $i$f$all = false;
            if ($this$all$iv instanceof Collection && ((Collection)$this$all$iv).isEmpty()) {
                bl = true;
            } else {
                for (Object element$iv : $this$all$iv) {
                    it = (ParentPreflightCheckSpec)element$iv;
                    boolean bl2 = false;
                    if (it.getPreflightCheckSpecs().isEmpty()) continue;
                    bl = false;
                    break block11;
                }
                bl = true;
            }
        }
        if (bl) {
            return SetsKt.emptySet();
        }
        if (this.appMigrationDarkFeatures.isAppVendorCheckMaaFilteringEnabled()) {
            AppPreflightExecutorImplKt.access$getLog$p().info("App vendor check filtering is enabled");
            Map<String, Set<String>> disabledChecks = this.retrieveDisabledAppVendorChecks(selectedApps);
            set = this.filterDisabledCheckSpecs(parentPreflightCheckSpecs, disabledChecks);
        } else {
            set = parentPreflightCheckSpecs;
        }
        Set<ParentPreflightCheckSpec> filteredChecks = set;
        Set<ParentPreflightCheckSpec> filteredChecksWithLimitApplied = this.applyCheckNumberLimitOnAppChecks(filteredChecks);
        Iterable iterable = this.vendorCheckExecutor.executePreflightChecks(context, filteredChecksWithLimitApplied);
        boolean $i$f$groupBy = false;
        it = $this$groupBy$iv;
        Object destination$iv$iv = new LinkedHashMap();
        boolean $i$f$groupByTo = false;
        for (Object t : $this$groupByTo$iv$iv) {
            Object object;
            AppPreflightCheckInternalResponse it2 = (AppPreflightCheckInternalResponse)t;
            boolean bl3 = false;
            key$iv$iv = it2.getServerAppKey();
            Map $this$getOrPut$iv$iv$iv = destination$iv$iv;
            boolean $i$f$getOrPut = false;
            Object value$iv$iv$iv = $this$getOrPut$iv$iv$iv.get(key$iv$iv);
            if (value$iv$iv$iv == null) {
                boolean bl4 = false;
                List answer$iv$iv$iv = new ArrayList();
                $this$getOrPut$iv$iv$iv.put(key$iv$iv, answer$iv$iv$iv);
                object = answer$iv$iv$iv;
            } else {
                object = value$iv$iv$iv;
            }
            list$iv$iv = (List)object;
            list$iv$iv.add(t);
        }
        $this$groupBy$iv = destination$iv$iv;
        boolean $i$f$map = false;
        $this$groupByTo$iv$iv = $this$map$iv;
        destination$iv$iv = new ArrayList($this$map$iv.size());
        boolean $i$f$mapTo = false;
        for (Map.Entry entry : $this$mapTo$iv$iv.entrySet()) {
            void $this$mapTo$iv$iv2;
            void $this$map$iv2;
            void group;
            list$iv$iv = entry;
            Object object = destination$iv$iv;
            boolean bl5 = false;
            key$iv$iv = (Iterable)group.getValue();
            String string = (String)group.getKey();
            boolean $i$f$map2 = false;
            void var20_28 = $this$map$iv2;
            Collection destination$iv$iv2 = new ArrayList(CollectionsKt.collectionSizeOrDefault((Iterable)$this$map$iv2, (int)10));
            boolean $i$f$mapTo2 = false;
            for (Object item$iv$iv2 : $this$mapTo$iv$iv2) {
                void it3;
                AppPreflightCheckInternalResponse appPreflightCheckInternalResponse = (AppPreflightCheckInternalResponse)item$iv$iv2;
                Collection collection = destination$iv$iv2;
                boolean bl6 = false;
                collection.add(this.appPreflightCheckResponse((AppPreflightCheckInternalResponse)it3));
            }
            Set set2 = CollectionsKt.toSet((Iterable)((List)destination$iv$iv2));
            String string2 = string;
            object.add(new ParentAppPreflightChecksResponse(string2, set2));
        }
        return CollectionsKt.toSet((Iterable)((List)destination$iv$iv));
    }

    /*
     * WARNING - void declaration
     */
    private final Map<String, Set<String>> retrieveDisabledAppVendorChecks(Set<String> selectedApps) {
        void $this$associateTo$iv$iv;
        Iterable $this$associate$iv = this.migrationAppAggregatorClient.getDisabledAppVendorChecks(selectedApps);
        boolean $i$f$associate = false;
        int capacity$iv = RangesKt.coerceAtLeast((int)MapsKt.mapCapacity((int)CollectionsKt.collectionSizeOrDefault((Iterable)$this$associate$iv, (int)10)), (int)16);
        Iterable iterable = $this$associate$iv;
        Map destination$iv$iv = new LinkedHashMap(capacity$iv);
        boolean $i$f$associateTo = false;
        for (Object element$iv$iv : $this$associateTo$iv$iv) {
            Map map = destination$iv$iv;
            DisabledCheck disabledCheck = (DisabledCheck)element$iv$iv;
            boolean bl = false;
            Pair pair = TuplesKt.to((Object)disabledCheck.getServerAppKey(), (Object)disabledCheck.getCheckIds());
            map.put(pair.getFirst(), pair.getSecond());
        }
        return destination$iv$iv;
    }

    /*
     * WARNING - void declaration
     */
    private final Set<ParentPreflightCheckSpec> filterDisabledCheckSpecs(Set<ParentPreflightCheckSpec> parentPreflightCheckSpecs, Map<String, ? extends Set<String>> disabledChecksMap) {
        void $this$mapTo$iv$iv;
        Iterable $this$map$iv = parentPreflightCheckSpecs;
        boolean $i$f$map = false;
        Iterable iterable = $this$map$iv;
        Collection destination$iv$iv = new ArrayList(CollectionsKt.collectionSizeOrDefault((Iterable)$this$map$iv, (int)10));
        boolean $i$f$mapTo = false;
        for (Object item$iv$iv : $this$mapTo$iv$iv) {
            Set set;
            void appChecks;
            ParentPreflightCheckSpec parentPreflightCheckSpec = (ParentPreflightCheckSpec)item$iv$iv;
            Collection collection = destination$iv$iv;
            boolean bl = false;
            Set<String> disabledChecksOfApp = disabledChecksMap.get(appChecks.getServerAppKey());
            Collection collection2 = disabledChecksOfApp;
            if (!(collection2 == null || collection2.isEmpty())) {
                void $this$filterNotTo$iv$iv;
                Iterable $this$filterNot$iv = appChecks.getPreflightCheckSpecs();
                boolean $i$f$filterNot = false;
                Iterable iterable2 = $this$filterNot$iv;
                Collection destination$iv$iv2 = new ArrayList();
                boolean $i$f$filterNotTo = false;
                for (Object element$iv$iv : $this$filterNotTo$iv$iv) {
                    PreflightCheckSpec eachCheck = (PreflightCheckSpec)element$iv$iv;
                    boolean bl2 = false;
                    if (disabledChecksOfApp.contains(eachCheck.getPreflightCheckId())) continue;
                    destination$iv$iv2.add(element$iv$iv);
                }
                set = CollectionsKt.toSet((Iterable)((List)destination$iv$iv2));
            } else {
                set = appChecks.getPreflightCheckSpecs();
            }
            Set enabledChecks = set;
            collection.add(new ParentPreflightCheckSpec(appChecks.getServerAppKey(), enabledChecks));
        }
        return CollectionsKt.toSet((Iterable)((List)destination$iv$iv));
    }

    /*
     * WARNING - void declaration
     */
    private final Set<ParentPreflightCheckSpec> applyCheckNumberLimitOnAppChecks(Set<ParentPreflightCheckSpec> filteredAppVendorCheckSpecs) {
        void $this$mapTo$iv$iv;
        Iterable $this$map$iv = filteredAppVendorCheckSpecs;
        boolean $i$f$map = false;
        Iterable iterable = $this$map$iv;
        Collection destination$iv$iv = new ArrayList(CollectionsKt.collectionSizeOrDefault((Iterable)$this$map$iv, (int)10));
        boolean $i$f$mapTo = false;
        for (Object item$iv$iv : $this$mapTo$iv$iv) {
            void eachAppChecks;
            ParentPreflightCheckSpec parentPreflightCheckSpec = (ParentPreflightCheckSpec)item$iv$iv;
            Collection collection = destination$iv$iv;
            boolean bl = false;
            List checksIncluded = CollectionsKt.take((Iterable)eachAppChecks.getPreflightCheckSpecs(), (int)3);
            Set checksSkipped = SetsKt.minus((Set)eachAppChecks.getPreflightCheckSpecs(), (Iterable)CollectionsKt.toSet((Iterable)checksIncluded));
            if (!((Collection)checksSkipped).isEmpty()) {
                AppPreflightExecutorImplKt.access$getLog$p().warn("App [" + eachAppChecks.getServerAppKey() + "] has provided more checks [" + eachAppChecks.getPreflightCheckSpecs().size() + "] than allowed limit: 3. Some checks may not run..");
                AppPreflightExecutorImplKt.access$getLog$p().warn("App [" + eachAppChecks.getServerAppKey() + "] : checks skipped " + checksSkipped + ' ');
            }
            AppPreflightExecutorImplKt.access$getLog$p().debug("App [" + eachAppChecks.getServerAppKey() + "] : checks included: " + checksIncluded + ' ');
            collection.add(new ParentPreflightCheckSpec(eachAppChecks.getServerAppKey(), CollectionsKt.toSet((Iterable)checksIncluded)));
        }
        return CollectionsKt.toSet((Iterable)((List)destination$iv$iv));
    }

    private final String retrieveStepsToResolve(String serverAppKey, String checkId, String nextStepsKey) {
        String string;
        String string2 = nextStepsKey;
        if (string2 != null) {
            String it = string2;
            boolean bl = false;
            Object object = this.retrieveSpecForCheckId(serverAppKey, checkId);
            string = object != null && (object = object.getStepsToResolve()) != null ? (String)object.get(it) : null;
        } else {
            string = null;
        }
        return string;
    }

    private final String retrieveTitle(String serverAppKey, String checkId) {
        Object object = this.retrieveSpecForCheckId(serverAppKey, checkId);
        if (object == null || (object = object.getTitle()) == null) {
            throw new RuntimeException("No title for " + serverAppKey + '.' + checkId);
        }
        return object;
    }

    private final PreflightCheckSpec retrieveSpecForCheckId(String serverAppKey, String checkId) {
        PreflightCheckSpec preflightCheckSpec;
        Object object = (ParentPreflightCheckSpec)CollectionsKt.firstOrNull((Iterable)this.vendorCheckRetriever.retrievePreflightSpecs$app_migration_assistant(SetsKt.setOf((Object)serverAppKey)));
        if (object != null && (object = object.getPreflightCheckSpecs()) != null) {
            Object v1;
            block3: {
                Iterable $this$firstOrNull$iv = (Iterable)object;
                boolean $i$f$firstOrNull = false;
                for (Object element$iv : $this$firstOrNull$iv) {
                    PreflightCheckSpec it = (PreflightCheckSpec)element$iv;
                    boolean bl = false;
                    if (!Intrinsics.areEqual((Object)it.getPreflightCheckId(), (Object)checkId)) continue;
                    v1 = element$iv;
                    break block3;
                }
                v1 = null;
            }
            preflightCheckSpec = v1;
        } else {
            preflightCheckSpec = null;
        }
        return preflightCheckSpec;
    }

    private final AppPreflightCheckResponse appPreflightCheckResponse(AppPreflightCheckInternalResponse internalResponse) {
        AppPreflightCheckInternalResponse $this$appPreflightCheckResponse_u24lambda_u2410 = internalResponse;
        boolean bl = false;
        return new AppPreflightCheckResponse($this$appPreflightCheckResponse_u24lambda_u2410.getCheckId(), this.retrieveTitle($this$appPreflightCheckResponse_u24lambda_u2410.getServerAppKey(), $this$appPreflightCheckResponse_u24lambda_u2410.getCheckId()), $this$appPreflightCheckResponse_u24lambda_u2410.getStatus(), this.retrieveStepsToResolve($this$appPreflightCheckResponse_u24lambda_u2410.getServerAppKey(), $this$appPreflightCheckResponse_u24lambda_u2410.getCheckId(), $this$appPreflightCheckResponse_u24lambda_u2410.getStepsToResolveKey()), $this$appPreflightCheckResponse_u24lambda_u2410.getCsvFileContent(), $this$appPreflightCheckResponse_u24lambda_u2410.getCheckDetails());
    }
}


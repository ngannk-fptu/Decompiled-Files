/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.migration.app.check.CheckSpec
 *  com.atlassian.migration.app.dto.check.ParentPreflightCheckSpec
 *  com.atlassian.migration.app.dto.check.PreflightCheckSpec
 *  com.atlassian.migration.app.dto.check.VendorCheckRepositoryProxy
 *  kotlin.Metadata
 *  kotlin.collections.CollectionsKt
 *  kotlin.jvm.internal.Intrinsics
 *  org.jetbrains.annotations.NotNull
 */
package com.atlassian.migration.app;

import com.atlassian.migration.app.OsgiBundleHelper;
import com.atlassian.migration.app.VendorCheckRetrieverKt;
import com.atlassian.migration.app.check.CheckSpec;
import com.atlassian.migration.app.dto.check.ParentPreflightCheckSpec;
import com.atlassian.migration.app.dto.check.PreflightCheckSpec;
import com.atlassian.migration.app.dto.check.VendorCheckRepositoryProxy;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import kotlin.Metadata;
import kotlin.collections.CollectionsKt;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;

@Metadata(mv={1, 7, 1}, k=1, xi=48, d1={"\u0000$\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\"\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0002\u0018\u00002\u00020\u0001B\r\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\u0002\u0010\u0004J!\u0010\u0005\u001a\b\u0012\u0004\u0012\u00020\u00070\u00062\f\u0010\b\u001a\b\u0012\u0004\u0012\u00020\t0\u0006H\u0000\u00a2\u0006\u0002\b\nR\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u000b"}, d2={"Lcom/atlassian/migration/app/VendorCheckRetriever;", "", "osgiBundleHelper", "Lcom/atlassian/migration/app/OsgiBundleHelper;", "(Lcom/atlassian/migration/app/OsgiBundleHelper;)V", "retrievePreflightSpecs", "", "Lcom/atlassian/migration/app/dto/check/ParentPreflightCheckSpec;", "selectedApps", "", "retrievePreflightSpecs$app_migration_assistant", "app-migration-assistant"})
public final class VendorCheckRetriever {
    @NotNull
    private final OsgiBundleHelper osgiBundleHelper;

    public VendorCheckRetriever(@NotNull OsgiBundleHelper osgiBundleHelper) {
        Intrinsics.checkNotNullParameter((Object)osgiBundleHelper, (String)"osgiBundleHelper");
        this.osgiBundleHelper = osgiBundleHelper;
    }

    /*
     * WARNING - void declaration
     */
    @NotNull
    public final Set<ParentPreflightCheckSpec> retrievePreflightSpecs$app_migration_assistant(@NotNull Set<String> selectedApps) {
        Map<String, VendorCheckRepositoryProxy> vendorCheckRepositoryProxyMap;
        Intrinsics.checkNotNullParameter(selectedApps, (String)"selectedApps");
        Set parentPreflightCheckSpecSet = new LinkedHashSet();
        Map<String, VendorCheckRepositoryProxy> $this$forEach$iv = vendorCheckRepositoryProxyMap = this.osgiBundleHelper.getVendorChecks(selectedApps);
        boolean $i$f$forEach = false;
        Iterator<Map.Entry<String, VendorCheckRepositoryProxy>> iterator = $this$forEach$iv.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, VendorCheckRepositoryProxy> element$iv;
            Map.Entry<String, VendorCheckRepositoryProxy> vendorCheckRepositoryProxyEntry = element$iv = iterator.next();
            boolean bl = false;
            VendorCheckRetriever $this$retrievePreflightSpecs_u24lambda_u242_u24lambda_u241 = this;
            boolean bl2 = false;
            try {
                void $this$mapTo$iv$iv;
                Iterable $this$map$iv = vendorCheckRepositoryProxyEntry.getValue().getAvailableChecks();
                boolean $i$f$map = false;
                Iterable iterable = $this$map$iv;
                Collection destination$iv$iv = new ArrayList(CollectionsKt.collectionSizeOrDefault((Iterable)$this$map$iv, (int)10));
                boolean $i$f$mapTo = false;
                for (Object item$iv$iv : $this$mapTo$iv$iv) {
                    void it;
                    CheckSpec checkSpec = (CheckSpec)item$iv$iv;
                    Collection collection = destination$iv$iv;
                    boolean bl3 = false;
                    String string = it.getCheckId();
                    Intrinsics.checkNotNullExpressionValue((Object)string, (String)"it.checkId");
                    String string2 = it.getTitle();
                    Intrinsics.checkNotNullExpressionValue((Object)string2, (String)"it.title");
                    Map map = it.getStepsToResolve();
                    Intrinsics.checkNotNullExpressionValue((Object)map, (String)"it.stepsToResolve");
                    collection.add(new PreflightCheckSpec(string, string2, map, it.getDescription()));
                }
                Set preflightCheckSpecSet = CollectionsKt.toSet((Iterable)((List)destination$iv$iv));
                parentPreflightCheckSpecSet.add(new ParentPreflightCheckSpec(vendorCheckRepositoryProxyEntry.getKey(), preflightCheckSpecSet));
            }
            catch (Exception e) {
                VendorCheckRetrieverKt.access$getLog$p().warn("Exception thrown while getting available checks for selected app: {}", (Object)vendorCheckRepositoryProxyEntry.getKey(), (Object)e);
            }
        }
        return parentPreflightCheckSpecSet;
    }
}


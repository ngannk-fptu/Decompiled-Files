/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.migration.app.check.PreMigrationCheckRepository
 *  com.atlassian.migration.app.dto.check.VendorCheckRepositoryProxy
 *  com.atlassian.migration.app.listener.DiscoverableForgeListener
 *  com.atlassian.migration.app.listener.DiscoverableListener
 *  kotlin.Metadata
 *  kotlin.Pair
 *  kotlin.TuplesKt
 *  kotlin.collections.ArraysKt
 *  kotlin.collections.CollectionsKt
 *  kotlin.collections.MapsKt
 *  kotlin.jvm.internal.Intrinsics
 *  kotlin.ranges.RangesKt
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 *  org.osgi.framework.BundleContext
 *  org.osgi.framework.ServiceReference
 */
package com.atlassian.migration.app;

import com.atlassian.migration.app.AppMigrationDarkFeatures;
import com.atlassian.migration.app.BaseAppCloudMigrationListener;
import com.atlassian.migration.app.DiscoverableListenerProxy;
import com.atlassian.migration.app.check.PreMigrationCheckRepository;
import com.atlassian.migration.app.dto.check.VendorCheckRepositoryProxy;
import com.atlassian.migration.app.listener.DiscoverableForgeListener;
import com.atlassian.migration.app.listener.DiscoverableListener;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import kotlin.Metadata;
import kotlin.Pair;
import kotlin.TuplesKt;
import kotlin.collections.ArraysKt;
import kotlin.collections.CollectionsKt;
import kotlin.collections.MapsKt;
import kotlin.jvm.internal.Intrinsics;
import kotlin.ranges.RangesKt;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;

@Metadata(mv={1, 7, 1}, k=1, xi=48, d1={"\u0000L\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0011\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010 \n\u0000\n\u0002\u0010$\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\"\n\u0000\u0018\u00002\u00020\u0001B\u0015\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u00a2\u0006\u0002\u0010\u0006J\u001c\u0010\u0007\u001a\n \t*\u0004\u0018\u00010\b0\b2\n\u0010\n\u001a\u0006\u0012\u0002\b\u00030\u000bH\u0002J\u0017\u0010\f\u001a\f\u0012\b\u0012\u0006\u0012\u0002\b\u00030\u000b0\rH\u0002\u00a2\u0006\u0002\u0010\u000eJ\u0010\u0010\u000f\u001a\u0004\u0018\u00010\u00102\u0006\u0010\u0011\u001a\u00020\bJ\f\u0010\u0012\u001a\b\u0012\u0004\u0012\u00020\u00100\u0013J \u0010\u0014\u001a\u000e\u0012\u0004\u0012\u00020\b\u0012\u0004\u0012\u00020\u00160\u00152\f\u0010\u0017\u001a\b\u0012\u0004\u0012\u00020\b0\u0018R\u000e\u0010\u0004\u001a\u00020\u0005X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u0019"}, d2={"Lcom/atlassian/migration/app/OsgiBundleHelper;", "", "bundleContext", "Lorg/osgi/framework/BundleContext;", "appMigrationDarkFeatures", "Lcom/atlassian/migration/app/AppMigrationDarkFeatures;", "(Lorg/osgi/framework/BundleContext;Lcom/atlassian/migration/app/AppMigrationDarkFeatures;)V", "getAppKey", "", "kotlin.jvm.PlatformType", "it", "Lorg/osgi/framework/ServiceReference;", "getDiscoveredForgeListeners", "", "()[Lorg/osgi/framework/ServiceReference;", "getDiscoveredListener", "Lcom/atlassian/migration/app/BaseAppCloudMigrationListener;", "serverAppKey", "getDiscoveredListeners", "", "getVendorChecks", "", "Lcom/atlassian/migration/app/dto/check/VendorCheckRepositoryProxy;", "appKeys", "", "app-migration-assistant"})
public final class OsgiBundleHelper {
    @NotNull
    private final BundleContext bundleContext;
    @NotNull
    private final AppMigrationDarkFeatures appMigrationDarkFeatures;

    public OsgiBundleHelper(@NotNull BundleContext bundleContext, @NotNull AppMigrationDarkFeatures appMigrationDarkFeatures) {
        Intrinsics.checkNotNullParameter((Object)bundleContext, (String)"bundleContext");
        Intrinsics.checkNotNullParameter((Object)appMigrationDarkFeatures, (String)"appMigrationDarkFeatures");
        this.bundleContext = bundleContext;
        this.appMigrationDarkFeatures = appMigrationDarkFeatures;
    }

    /*
     * WARNING - void declaration
     */
    @NotNull
    public final List<BaseAppCloudMigrationListener> getDiscoveredListeners() {
        void $this$mapTo$iv$iv;
        Object[] objectArray;
        Object[] objectArray2;
        Object[] objectArray3 = this.bundleContext.getAllServiceReferences(DiscoverableListener.class.getName(), null);
        if (objectArray3 == null) {
            boolean $i$f$emptyArray = false;
            objectArray2 = (ServiceReference[])((Object[])new ServiceReference[0]);
        } else {
            objectArray2 = objectArray3;
        }
        if (this.appMigrationDarkFeatures.isForgeMigrationPathEnabled()) {
            objectArray = this.getDiscoveredForgeListeners();
        } else {
            boolean $i$f$emptyArray = false;
            objectArray = (ServiceReference[])((Object[])new ServiceReference[0]);
        }
        Object[] $this$map$iv = ArraysKt.plus((Object[])objectArray2, (Object[])objectArray);
        boolean $i$f$map = false;
        Object[] objectArray4 = $this$map$iv;
        Collection destination$iv$iv = new ArrayList($this$map$iv.length);
        boolean $i$f$mapTo = false;
        for (void item$iv$iv : $this$mapTo$iv$iv) {
            void it;
            ServiceReference serviceReference = (ServiceReference)item$iv$iv;
            Collection collection = destination$iv$iv;
            boolean bl = false;
            Object object = this.bundleContext.getService((ServiceReference)it);
            Intrinsics.checkNotNullExpressionValue((Object)object, (String)"bundleContext.getService(it)");
            Intrinsics.checkNotNullExpressionValue((Object)it, (String)"it");
            String string = this.getAppKey((ServiceReference<?>)it);
            Intrinsics.checkNotNullExpressionValue((Object)string, (String)"getAppKey(it)");
            collection.add(new DiscoverableListenerProxy(object, string));
        }
        return (List)destination$iv$iv;
    }

    private final ServiceReference<?>[] getDiscoveredForgeListeners() {
        ServiceReference[] serviceReferenceArray;
        try {
            ServiceReference[] serviceReferenceArray2;
            serviceReferenceArray = this.bundleContext.getAllServiceReferences(DiscoverableForgeListener.class.getName(), null);
            if (serviceReferenceArray == null) {
                boolean $i$f$emptyArray = false;
                serviceReferenceArray2 = (ServiceReference[])((Object[])new ServiceReference[0]);
            } else {
                serviceReferenceArray2 = serviceReferenceArray;
            }
            serviceReferenceArray = serviceReferenceArray2;
        }
        catch (Exception ex) {
            boolean $i$f$emptyArray = false;
            serviceReferenceArray = (ServiceReference[])((Object[])new ServiceReference[0]);
        }
        return serviceReferenceArray;
    }

    /*
     * WARNING - void declaration
     */
    @NotNull
    public final Map<String, VendorCheckRepositoryProxy> getVendorChecks(@NotNull Set<String> appKeys) {
        void $this$associateTo$iv$iv;
        void $this$filterTo$iv$iv;
        ServiceReference[] serviceReferenceArray;
        Intrinsics.checkNotNullParameter(appKeys, (String)"appKeys");
        ServiceReference[] serviceReferenceArray2 = this.bundleContext.getAllServiceReferences(PreMigrationCheckRepository.class.getName(), null);
        if (serviceReferenceArray2 == null) {
            boolean $i$f$emptyArray = false;
            serviceReferenceArray = (ServiceReference[])((Object[])new ServiceReference[0]);
        } else {
            serviceReferenceArray = serviceReferenceArray2;
        }
        ServiceReference[] $this$filter$iv = serviceReferenceArray;
        boolean $i$f$filter = false;
        ServiceReference[] serviceReferenceArray3 = $this$filter$iv;
        Iterable<void> destination$iv$iv = new ArrayList();
        boolean $i$f$filterTo = false;
        for (Object element$iv$iv : $this$filterTo$iv$iv) {
            void it = element$iv$iv;
            boolean bl = false;
            Intrinsics.checkNotNullExpressionValue((Object)it, (String)"it");
            if (!appKeys.contains(this.getAppKey((ServiceReference<?>)it))) continue;
            destination$iv$iv.add(element$iv$iv);
        }
        Iterable $this$associate$iv = (List)destination$iv$iv;
        boolean $i$f$associate = false;
        int capacity$iv = RangesKt.coerceAtLeast((int)MapsKt.mapCapacity((int)CollectionsKt.collectionSizeOrDefault((Iterable)$this$associate$iv, (int)10)), (int)16);
        destination$iv$iv = $this$associate$iv;
        Map destination$iv$iv2 = new LinkedHashMap(capacity$iv);
        boolean $i$f$associateTo = false;
        for (Object element$iv$iv : $this$associateTo$iv$iv) {
            Map map = destination$iv$iv2;
            ServiceReference it = (ServiceReference)element$iv$iv;
            boolean bl = false;
            Intrinsics.checkNotNullExpressionValue((Object)it, (String)"it");
            String string = this.getAppKey(it);
            Object object = this.bundleContext.getService(it);
            Intrinsics.checkNotNullExpressionValue((Object)object, (String)"bundleContext.getService(it)");
            Pair pair = TuplesKt.to((Object)string, (Object)new VendorCheckRepositoryProxy(object));
            map.put(pair.getFirst(), pair.getSecond());
        }
        return destination$iv$iv2;
    }

    @Nullable
    public final BaseAppCloudMigrationListener getDiscoveredListener(@NotNull String serverAppKey) {
        Object v0;
        block1: {
            Intrinsics.checkNotNullParameter((Object)serverAppKey, (String)"serverAppKey");
            Iterable $this$firstOrNull$iv = this.getDiscoveredListeners();
            boolean $i$f$firstOrNull = false;
            for (Object element$iv : $this$firstOrNull$iv) {
                BaseAppCloudMigrationListener it = (BaseAppCloudMigrationListener)element$iv;
                boolean bl = false;
                if (!Intrinsics.areEqual((Object)it.getServerAppKey(), (Object)serverAppKey)) continue;
                v0 = element$iv;
                break block1;
            }
            v0 = null;
        }
        return v0;
    }

    private final String getAppKey(ServiceReference<?> it) {
        return (String)it.getBundle().getHeaders().get("Atlassian-Plugin-Key");
    }
}


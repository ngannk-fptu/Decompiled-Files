/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.inject.Inject
 *  javax.inject.Named
 *  kotlin.Metadata
 *  kotlin.collections.CollectionsKt
 *  kotlin.jvm.internal.Intrinsics
 *  kotlin.jvm.internal.Reflection
 *  kotlin.jvm.internal.SourceDebugExtension
 *  org.jetbrains.annotations.NotNull
 */
package com.addonengine.addons.analytics.store.server;

import com.addonengine.addons.analytics.store.RestrictionsRepository;
import com.addonengine.addons.analytics.store.model.InstanceRestrictionsData;
import com.addonengine.addons.analytics.store.model.SpaceRestrictionsData;
import com.addonengine.addons.analytics.store.model.UserGroupRestrictionData;
import com.addonengine.addons.analytics.store.model.UserRestrictionData;
import com.addonengine.addons.analytics.store.server.settings.Settings;
import com.addonengine.addons.analytics.store.server.settings.model.InstanceRestrictionsSetting;
import com.addonengine.addons.analytics.store.server.settings.model.SpaceRestrictionsSetting;
import com.addonengine.addons.analytics.store.server.settings.model.UserGroupRestriction;
import com.addonengine.addons.analytics.store.server.settings.model.UserRestriction;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javax.inject.Inject;
import javax.inject.Named;
import kotlin.Metadata;
import kotlin.collections.CollectionsKt;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.Reflection;
import kotlin.jvm.internal.SourceDebugExtension;
import org.jetbrains.annotations.NotNull;

@Named
@Metadata(mv={1, 9, 0}, k=1, xi=48, d1={"\u0000&\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0006\b\u0007\u0018\u00002\u00020\u0001B\u000f\b\u0007\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\u0002\u0010\u0004J\b\u0010\u0007\u001a\u00020\bH\u0016J\u0010\u0010\t\u001a\u00020\n2\u0006\u0010\u000b\u001a\u00020\u0006H\u0016J\u0010\u0010\f\u001a\u00020\u00062\u0006\u0010\u000b\u001a\u00020\u0006H\u0002J\u0010\u0010\r\u001a\u00020\b2\u0006\u0010\u000e\u001a\u00020\bH\u0016J\u0018\u0010\u000f\u001a\u00020\n2\u0006\u0010\u000b\u001a\u00020\u00062\u0006\u0010\u000e\u001a\u00020\nH\u0016R\u000e\u0010\u0005\u001a\u00020\u0006X\u0082D\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u0010"}, d2={"Lcom/addonengine/addons/analytics/store/server/RestrictionsRepositoryServerImpl;", "Lcom/addonengine/addons/analytics/store/RestrictionsRepository;", "settings", "Lcom/addonengine/addons/analytics/store/server/settings/Settings;", "(Lcom/addonengine/addons/analytics/store/server/settings/Settings;)V", "instanceRestrictionsKey", "", "getInstanceRestrictions", "Lcom/addonengine/addons/analytics/store/model/InstanceRestrictionsData;", "getSpaceRestrictions", "Lcom/addonengine/addons/analytics/store/model/SpaceRestrictionsData;", "spaceKey", "getSpaceRestrictionsKey", "saveInstanceRestrictions", "restrictions", "saveSpaceRestrictions", "analytics"})
@SourceDebugExtension(value={"SMAP\nRestrictionsRepositoryServerImpl.kt\nKotlin\n*S Kotlin\n*F\n+ 1 RestrictionsRepositoryServerImpl.kt\ncom/addonengine/addons/analytics/store/server/RestrictionsRepositoryServerImpl\n+ 2 Settings.kt\ncom/addonengine/addons/analytics/store/server/settings/Settings\n+ 3 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n*L\n1#1,58:1\n25#2:59\n25#2:64\n1549#3:60\n1620#3,3:61\n1549#3:65\n1620#3,3:66\n1549#3:69\n1620#3,3:70\n1549#3:73\n1620#3,3:74\n1549#3:77\n1620#3,3:78\n1549#3:81\n1620#3,3:82\n*S KotlinDebug\n*F\n+ 1 RestrictionsRepositoryServerImpl.kt\ncom/addonengine/addons/analytics/store/server/RestrictionsRepositoryServerImpl\n*L\n24#1:59\n32#1:64\n26#1:60\n26#1:61,3\n34#1:65\n34#1:66,3\n36#1:69\n36#1:70,3\n42#1:73\n42#1:74,3\n51#1:77\n51#1:78,3\n52#1:81\n52#1:82,3\n*E\n"})
public final class RestrictionsRepositoryServerImpl
implements RestrictionsRepository {
    @NotNull
    private final Settings settings;
    @NotNull
    private final String instanceRestrictionsKey;

    @Inject
    public RestrictionsRepositoryServerImpl(@NotNull Settings settings) {
        Intrinsics.checkNotNullParameter((Object)settings, (String)"settings");
        this.settings = settings;
        this.instanceRestrictionsKey = "INSTANCE_RESTRICTIONS";
    }

    private final String getSpaceRestrictionsKey(String spaceKey) {
        StringBuilder stringBuilder = new StringBuilder().append("SPACE_RESTRICTIONS_");
        String string = spaceKey.toUpperCase();
        Intrinsics.checkNotNullExpressionValue((Object)string, (String)"this as java.lang.String).toUpperCase()");
        return stringBuilder.append(string).toString();
    }

    /*
     * WARNING - void declaration
     */
    @Override
    @NotNull
    public InstanceRestrictionsData getInstanceRestrictions() {
        List list;
        void this_$iv;
        Settings settings = this.settings;
        String key$iv = this.instanceRestrictionsKey;
        boolean $i$f$get = false;
        InstanceRestrictionsSetting instanceRestrictionsSetting = (InstanceRestrictionsSetting)this_$iv.get(key$iv, Reflection.getOrCreateKotlinClass(InstanceRestrictionsSetting.class));
        Object object = instanceRestrictionsSetting;
        if (object != null && (object = ((InstanceRestrictionsSetting)object).getUserGroups()) != null) {
            void $this$mapTo$iv$iv;
            Iterable $this$map$iv = (Iterable)object;
            boolean $i$f$map = false;
            Iterable iterable = $this$map$iv;
            Collection destination$iv$iv = new ArrayList(CollectionsKt.collectionSizeOrDefault((Iterable)$this$map$iv, (int)10));
            boolean $i$f$mapTo = false;
            for (Object item$iv$iv : $this$mapTo$iv$iv) {
                void it;
                UserGroupRestriction userGroupRestriction = (UserGroupRestriction)item$iv$iv;
                Collection collection = destination$iv$iv;
                boolean bl = false;
                collection.add(new UserGroupRestrictionData(it.getGroupName(), it.getUseAnalytics()));
            }
            list = (List)destination$iv$iv;
        } else {
            list = CollectionsKt.emptyList();
        }
        List userGroupRestrictions = list;
        return new InstanceRestrictionsData(userGroupRestrictions);
    }

    /*
     * WARNING - void declaration
     */
    @Override
    @NotNull
    public SpaceRestrictionsData getSpaceRestrictions(@NotNull String spaceKey) {
        List list;
        Object object;
        List userGroupRestrictions;
        List list2;
        Iterable iterable;
        Iterable destination$iv$iv;
        Intrinsics.checkNotNullParameter((Object)spaceKey, (String)"spaceKey");
        String key = this.getSpaceRestrictionsKey(spaceKey);
        Settings this_$iv = this.settings;
        boolean $i$f$get = false;
        SpaceRestrictionsSetting spaceRestrictionsSetting = (SpaceRestrictionsSetting)this_$iv.get(key, Reflection.getOrCreateKotlinClass(SpaceRestrictionsSetting.class));
        Object object2 = spaceRestrictionsSetting;
        if (object2 != null && (object2 = ((SpaceRestrictionsSetting)object2).getUserGroups()) != null) {
            void $this$mapTo$iv$iv;
            Iterable $this$map$iv = (Iterable)object2;
            boolean $i$f$map = false;
            Iterable iterable2 = $this$map$iv;
            destination$iv$iv = new ArrayList(CollectionsKt.collectionSizeOrDefault((Iterable)$this$map$iv, (int)10));
            boolean $i$f$mapTo = false;
            for (Object item$iv$iv : $this$mapTo$iv$iv) {
                void it;
                UserGroupRestriction userGroupRestriction = (UserGroupRestriction)item$iv$iv;
                iterable = destination$iv$iv;
                boolean bl = false;
                iterable.add(new UserGroupRestrictionData(it.getGroupName(), it.getUseAnalytics()));
            }
            list2 = (List)destination$iv$iv;
        } else {
            list2 = userGroupRestrictions = CollectionsKt.emptyList();
        }
        if ((object = spaceRestrictionsSetting) != null && (object = ((SpaceRestrictionsSetting)object).getUsers()) != null) {
            void $this$mapTo$iv$iv;
            Iterable $this$map$iv = (Iterable)object;
            boolean $i$f$map = false;
            destination$iv$iv = $this$map$iv;
            Collection destination$iv$iv2 = new ArrayList(CollectionsKt.collectionSizeOrDefault((Iterable)$this$map$iv, (int)10));
            boolean $i$f$mapTo = false;
            for (Object item$iv$iv : $this$mapTo$iv$iv) {
                void it;
                UserRestriction bl = (UserRestriction)item$iv$iv;
                iterable = destination$iv$iv2;
                boolean bl2 = false;
                iterable.add(new UserRestrictionData(it.getUserKey(), it.getUseAnalytics()));
            }
            list = (List)destination$iv$iv2;
        } else {
            list = CollectionsKt.emptyList();
        }
        List userRestrictions = list;
        return new SpaceRestrictionsData(userGroupRestrictions, userRestrictions);
    }

    /*
     * WARNING - void declaration
     */
    @Override
    @NotNull
    public InstanceRestrictionsData saveInstanceRestrictions(@NotNull InstanceRestrictionsData restrictions) {
        void $this$mapTo$iv$iv;
        Intrinsics.checkNotNullParameter((Object)restrictions, (String)"restrictions");
        Iterable $this$map$iv = restrictions.getUserGroups();
        boolean $i$f$map = false;
        Iterable iterable = $this$map$iv;
        Collection destination$iv$iv = new ArrayList(CollectionsKt.collectionSizeOrDefault((Iterable)$this$map$iv, (int)10));
        boolean $i$f$mapTo = false;
        for (Object item$iv$iv : $this$mapTo$iv$iv) {
            void it;
            UserGroupRestrictionData userGroupRestrictionData = (UserGroupRestrictionData)item$iv$iv;
            Collection collection = destination$iv$iv;
            boolean bl = false;
            collection.add(new UserGroupRestriction(it.getGroupName(), it.getUseAnalytics()));
        }
        List list = (List)destination$iv$iv;
        InstanceRestrictionsSetting value = new InstanceRestrictionsSetting(list);
        this.settings.set(this.instanceRestrictionsKey, value);
        return restrictions;
    }

    @Override
    @NotNull
    public SpaceRestrictionsData saveSpaceRestrictions(@NotNull String spaceKey, @NotNull SpaceRestrictionsData restrictions) {
        UserRestrictionData it;
        Collection collection;
        Iterable $this$mapTo$iv$iv;
        Intrinsics.checkNotNullParameter((Object)spaceKey, (String)"spaceKey");
        Intrinsics.checkNotNullParameter((Object)restrictions, (String)"restrictions");
        String spaceRestrictionsKey = this.getSpaceRestrictionsKey(spaceKey);
        Iterable $this$map$iv = restrictions.getUserGroups();
        boolean $i$f$map = false;
        Iterable iterable = $this$map$iv;
        Collection destination$iv$iv = new ArrayList(CollectionsKt.collectionSizeOrDefault((Iterable)$this$map$iv, (int)10));
        boolean $i$f$mapTo = false;
        for (Object item$iv$iv : $this$mapTo$iv$iv) {
            UserGroupRestrictionData userGroupRestrictionData = (UserGroupRestrictionData)item$iv$iv;
            collection = destination$iv$iv;
            boolean bl = false;
            collection.add(new UserGroupRestriction(((UserGroupRestrictionData)((Object)it)).getGroupName(), ((UserGroupRestrictionData)((Object)it)).getUseAnalytics()));
        }
        $this$map$iv = restrictions.getUsers();
        collection = (List)destination$iv$iv;
        $i$f$map = false;
        $this$mapTo$iv$iv = $this$map$iv;
        destination$iv$iv = new ArrayList(CollectionsKt.collectionSizeOrDefault((Iterable)$this$map$iv, (int)10));
        $i$f$mapTo = false;
        for (Object item$iv$iv : $this$mapTo$iv$iv) {
            it = (UserRestrictionData)item$iv$iv;
            Collection collection2 = destination$iv$iv;
            boolean bl = false;
            collection2.add(new UserRestriction(it.getUserKey(), it.getUseAnalytics()));
        }
        List list = (List)destination$iv$iv;
        Collection collection3 = collection;
        SpaceRestrictionsSetting value = new SpaceRestrictionsSetting((List<UserGroupRestriction>)collection3, list);
        this.settings.set(spaceRestrictionsKey, value);
        return restrictions;
    }
}


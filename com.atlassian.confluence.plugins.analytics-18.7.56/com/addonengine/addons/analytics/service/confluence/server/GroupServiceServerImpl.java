/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.user.UserAccessor
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.atlassian.user.Group
 *  javax.inject.Inject
 *  javax.inject.Named
 *  kotlin.Metadata
 *  kotlin.collections.CollectionsKt
 *  kotlin.jvm.internal.Intrinsics
 *  kotlin.jvm.internal.SourceDebugExtension
 *  org.jetbrains.annotations.NotNull
 */
package com.addonengine.addons.analytics.service.confluence.server;

import com.addonengine.addons.analytics.service.confluence.GroupService;
import com.atlassian.confluence.user.UserAccessor;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.user.Group;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javax.inject.Inject;
import javax.inject.Named;
import kotlin.Metadata;
import kotlin.collections.CollectionsKt;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;
import org.jetbrains.annotations.NotNull;

@Named
@Metadata(mv={1, 9, 0}, k=1, xi=48, d1={"\u0000\u001e\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010 \n\u0002\u0010\u000e\n\u0002\b\u0002\b\u0007\u0018\u00002\u00020\u0001B\u0011\b\u0007\u0012\b\b\u0001\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\u0002\u0010\u0004J\u001c\u0010\u0005\u001a\b\u0012\u0004\u0012\u00020\u00070\u00062\f\u0010\b\u001a\b\u0012\u0004\u0012\u00020\u00070\u0006H\u0016R\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004\u00a2\u0006\u0002\n\u0000\u00a8\u0006\t"}, d2={"Lcom/addonengine/addons/analytics/service/confluence/server/GroupServiceServerImpl;", "Lcom/addonengine/addons/analytics/service/confluence/GroupService;", "userAccessor", "Lcom/atlassian/confluence/user/UserAccessor;", "(Lcom/atlassian/confluence/user/UserAccessor;)V", "getGroupNamesInConfluence", "", "", "groupNames", "analytics"})
@SourceDebugExtension(value={"SMAP\nGroupServiceServerImpl.kt\nKotlin\n*S Kotlin\n*F\n+ 1 GroupServiceServerImpl.kt\ncom/addonengine/addons/analytics/service/confluence/server/GroupServiceServerImpl\n+ 2 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n*L\n1#1,16:1\n1549#2:17\n1620#2,3:18\n*S KotlinDebug\n*F\n+ 1 GroupServiceServerImpl.kt\ncom/addonengine/addons/analytics/service/confluence/server/GroupServiceServerImpl\n*L\n14#1:17\n14#1:18,3\n*E\n"})
public final class GroupServiceServerImpl
implements GroupService {
    @NotNull
    private final UserAccessor userAccessor;

    @Inject
    public GroupServiceServerImpl(@ComponentImport @NotNull UserAccessor userAccessor) {
        Intrinsics.checkNotNullParameter((Object)userAccessor, (String)"userAccessor");
        this.userAccessor = userAccessor;
    }

    /*
     * WARNING - void declaration
     */
    @Override
    @NotNull
    public List<String> getGroupNamesInConfluence(@NotNull List<String> groupNames) {
        void $this$mapTo$iv$iv;
        Intrinsics.checkNotNullParameter(groupNames, (String)"groupNames");
        List list = this.userAccessor.getGroupsByGroupNames(groupNames);
        Intrinsics.checkNotNullExpressionValue((Object)list, (String)"getGroupsByGroupNames(...)");
        Iterable $this$map$iv = list;
        boolean $i$f$map = false;
        Iterable iterable = $this$map$iv;
        Collection destination$iv$iv = new ArrayList(CollectionsKt.collectionSizeOrDefault((Iterable)$this$map$iv, (int)10));
        boolean $i$f$mapTo = false;
        for (Object item$iv$iv : $this$mapTo$iv$iv) {
            void it;
            Group group = (Group)item$iv$iv;
            Collection collection = destination$iv$iv;
            boolean bl = false;
            collection.add(it.getName());
        }
        return (List)destination$iv$iv;
    }
}


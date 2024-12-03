/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.core.ListBuilder
 *  com.atlassian.confluence.labels.Label
 *  com.atlassian.confluence.pages.BlogPost
 *  com.atlassian.confluence.pages.Page
 *  com.atlassian.confluence.pages.PageManager
 *  com.atlassian.confluence.security.Permission
 *  com.atlassian.confluence.security.PermissionManager
 *  com.atlassian.confluence.spaces.Space
 *  com.atlassian.confluence.spaces.SpaceManager
 *  com.atlassian.confluence.spaces.SpaceStatus
 *  com.atlassian.confluence.spaces.SpaceType
 *  com.atlassian.confluence.spaces.SpacesQuery
 *  com.atlassian.confluence.spaces.SpacesQuery$Builder
 *  com.atlassian.confluence.user.UserAccessor
 *  com.atlassian.plugin.spring.scanner.annotation.component.ConfluenceComponent
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.atlassian.sal.api.features.DarkFeatureManager
 *  com.atlassian.sal.api.rdbms.TransactionalExecutorFactory
 *  com.atlassian.sal.api.user.UserManager
 *  com.atlassian.user.User
 *  kotlin.Metadata
 *  kotlin.ResultKt
 *  kotlin.Unit
 *  kotlin.collections.CollectionsKt
 *  kotlin.coroutines.Continuation
 *  kotlin.coroutines.intrinsics.IntrinsicsKt
 *  kotlin.jvm.functions.Function2
 *  kotlin.jvm.internal.DefaultConstructorMarker
 *  kotlin.jvm.internal.Intrinsics
 *  kotlin.jvm.internal.SourceDebugExtension
 *  kotlinx.coroutines.BuildersKt
 *  kotlinx.coroutines.CoroutineScope
 *  kotlinx.coroutines.Deferred
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 *  org.springframework.beans.factory.annotation.Autowired
 */
package com.addonengine.addons.analytics.service.confluence.server;

import com.addonengine.addons.analytics.service.confluence.NoSpaceOrNoPermissionException;
import com.addonengine.addons.analytics.service.confluence.SpaceService;
import com.addonengine.addons.analytics.service.confluence.UrlBuilder;
import com.addonengine.addons.analytics.service.confluence.model.Content;
import com.addonengine.addons.analytics.service.confluence.server.SpaceServiceServerImpl;
import com.addonengine.addons.analytics.service.model.ContentType;
import com.addonengine.addons.analytics.service.model.SpaceType;
import com.atlassian.confluence.core.ListBuilder;
import com.atlassian.confluence.labels.Label;
import com.atlassian.confluence.pages.BlogPost;
import com.atlassian.confluence.pages.Page;
import com.atlassian.confluence.pages.PageManager;
import com.atlassian.confluence.security.Permission;
import com.atlassian.confluence.security.PermissionManager;
import com.atlassian.confluence.spaces.Space;
import com.atlassian.confluence.spaces.SpaceManager;
import com.atlassian.confluence.spaces.SpaceStatus;
import com.atlassian.confluence.spaces.SpacesQuery;
import com.atlassian.confluence.user.UserAccessor;
import com.atlassian.plugin.spring.scanner.annotation.component.ConfluenceComponent;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.sal.api.features.DarkFeatureManager;
import com.atlassian.sal.api.rdbms.TransactionalExecutorFactory;
import com.atlassian.sal.api.user.UserManager;
import com.atlassian.user.User;
import java.net.URL;
import java.sql.Connection;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Set;
import kotlin.Metadata;
import kotlin.ResultKt;
import kotlin.Unit;
import kotlin.collections.CollectionsKt;
import kotlin.coroutines.Continuation;
import kotlin.coroutines.intrinsics.IntrinsicsKt;
import kotlin.jvm.functions.Function2;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;
import kotlinx.coroutines.BuildersKt;
import kotlinx.coroutines.CoroutineScope;
import kotlinx.coroutines.Deferred;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.beans.factory.annotation.Autowired;

@ConfluenceComponent
@Metadata(mv={1, 9, 0}, k=1, xi=48, d1={"\u0000\u0082\u0001\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0002\n\u0002\u0010\u000b\n\u0000\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\"\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\b\b\u0007\u0018\u0000 02\u00020\u0001:\u00010BU\b\u0007\u0012\b\b\u0001\u0010\u0002\u001a\u00020\u0003\u0012\b\b\u0001\u0010\u0004\u001a\u00020\u0005\u0012\b\b\u0001\u0010\u0006\u001a\u00020\u0007\u0012\b\b\u0001\u0010\b\u001a\u00020\t\u0012\b\b\u0001\u0010\n\u001a\u00020\u000b\u0012\b\b\u0001\u0010\f\u001a\u00020\r\u0012\b\b\u0001\u0010\u000e\u001a\u00020\u000f\u0012\u0006\u0010\u0010\u001a\u00020\u0011\u00a2\u0006\u0002\u0010\u0012J\u0010\u0010\u0013\u001a\u00020\u00142\u0006\u0010\u0015\u001a\u00020\u0016H\u0016J\u001a\u0010\u0017\u001a\u0004\u0018\u00010\u00142\u0006\u0010\u0015\u001a\u00020\u00162\u0006\u0010\u0018\u001a\u00020\u0019H\u0016J\u001c\u0010\u001a\u001a\b\u0012\u0004\u0012\u00020\u001c0\u001b2\f\u0010\u001d\u001a\b\u0012\u0004\u0012\u00020\u00160\u001bH\u0002J\n\u0010\u001e\u001a\u0004\u0018\u00010\u001fH\u0002J\b\u0010 \u001a\u00020!H\u0016J$\u0010\"\u001a\b\u0012\u0004\u0012\u00020#0\u001b2\u0006\u0010\u0015\u001a\u00020\u00162\f\u0010$\u001a\b\u0012\u0004\u0012\u00020&0%H\u0016J\u001c\u0010'\u001a\b\u0012\u0004\u0012\u00020\u00140\u001b2\f\u0010(\u001a\b\u0012\u0004\u0012\u00020)0%H\u0016J$\u0010'\u001a\b\u0012\u0004\u0012\u00020\u00140\u001b2\f\u0010(\u001a\b\u0012\u0004\u0012\u00020)0%2\u0006\u0010\u0018\u001a\u00020\u0019H\u0002J\u0018\u0010*\u001a\u00020!2\u0006\u0010+\u001a\u00020\u00162\u0006\u0010,\u001a\u00020\u0019H\u0016J\u001c\u0010-\u001a\b\u0012\u0004\u0012\u00020\u00140\u001b2\f\u0010(\u001a\b\u0012\u0004\u0012\u00020)0%H\u0016J\u0018\u0010.\u001a\u00020\u00142\u0006\u0010/\u001a\u00020\u001c2\u0006\u0010\u0018\u001a\u00020\u0019H\u0002R\u000e\u0010\u000e\u001a\u00020\u000fX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0004\u001a\u00020\u0005X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0006\u001a\u00020\u0007X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\f\u001a\u00020\rX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0010\u001a\u00020\u0011X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\b\u001a\u00020\tX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\n\u001a\u00020\u000bX\u0082\u0004\u00a2\u0006\u0002\n\u0000\u00a8\u00061"}, d2={"Lcom/addonengine/addons/analytics/service/confluence/server/SpaceServiceServerImpl;", "Lcom/addonengine/addons/analytics/service/confluence/SpaceService;", "spaceManager", "Lcom/atlassian/confluence/spaces/SpaceManager;", "pageManager", "Lcom/atlassian/confluence/pages/PageManager;", "permissionManager", "Lcom/atlassian/confluence/security/PermissionManager;", "userAccessor", "Lcom/atlassian/confluence/user/UserAccessor;", "userManager", "Lcom/atlassian/sal/api/user/UserManager;", "transactionalExecutorFactory", "Lcom/atlassian/sal/api/rdbms/TransactionalExecutorFactory;", "darkFeatureManager", "Lcom/atlassian/sal/api/features/DarkFeatureManager;", "urlBuilder", "Lcom/addonengine/addons/analytics/service/confluence/UrlBuilder;", "(Lcom/atlassian/confluence/spaces/SpaceManager;Lcom/atlassian/confluence/pages/PageManager;Lcom/atlassian/confluence/security/PermissionManager;Lcom/atlassian/confluence/user/UserAccessor;Lcom/atlassian/sal/api/user/UserManager;Lcom/atlassian/sal/api/rdbms/TransactionalExecutorFactory;Lcom/atlassian/sal/api/features/DarkFeatureManager;Lcom/addonengine/addons/analytics/service/confluence/UrlBuilder;)V", "getByKey", "Lcom/addonengine/addons/analytics/service/confluence/model/Space;", "key", "", "getByKeyOrNull", "includeCategories", "", "getByKeysInternal", "", "Lcom/atlassian/confluence/spaces/Space;", "keys", "getCurrentUser", "Lcom/atlassian/user/User;", "getDefaultSpacesLogoUrl", "Ljava/net/URL;", "getSpaceContent", "Lcom/addonengine/addons/analytics/service/confluence/model/Content;", "contentTypes", "", "Lcom/addonengine/addons/analytics/service/model/ContentType;", "getSpaces", "spaceTypes", "Lcom/addonengine/addons/analytics/service/model/SpaceType;", "getSpacesLogoUrl", "spaceKey", "redirectUrl", "getSpacesWithCategories", "mapConfluenceSpaceToModelSpace", "space", "Companion", "analytics"})
@SourceDebugExtension(value={"SMAP\nSpaceServiceServerImpl.kt\nKotlin\n*S Kotlin\n*F\n+ 1 SpaceServiceServerImpl.kt\ncom/addonengine/addons/analytics/service/confluence/server/SpaceServiceServerImpl\n+ 2 fake.kt\nkotlin/jvm/internal/FakeKt\n+ 3 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n*L\n1#1,192:1\n1#2:193\n1549#3:194\n1620#3,3:195\n1549#3:198\n1620#3,3:199\n*S KotlinDebug\n*F\n+ 1 SpaceServiceServerImpl.kt\ncom/addonengine/addons/analytics/service/confluence/server/SpaceServiceServerImpl\n*L\n154#1:194\n154#1:195,3\n184#1:198\n184#1:199,3\n*E\n"})
public final class SpaceServiceServerImpl
implements SpaceService {
    @NotNull
    public static final Companion Companion = new Companion(null);
    @NotNull
    private final SpaceManager spaceManager;
    @NotNull
    private final PageManager pageManager;
    @NotNull
    private final PermissionManager permissionManager;
    @NotNull
    private final UserAccessor userAccessor;
    @NotNull
    private final UserManager userManager;
    @NotNull
    private final TransactionalExecutorFactory transactionalExecutorFactory;
    @NotNull
    private final DarkFeatureManager darkFeatureManager;
    @NotNull
    private final UrlBuilder urlBuilder;
    @NotNull
    public static final String defaultSpaceLogoDarkFeature = "confluence.analytics.space.logo.forcedefault";

    @Autowired
    public SpaceServiceServerImpl(@ComponentImport @NotNull SpaceManager spaceManager, @ComponentImport @NotNull PageManager pageManager, @ComponentImport @NotNull PermissionManager permissionManager, @ComponentImport @NotNull UserAccessor userAccessor, @ComponentImport @NotNull UserManager userManager, @ComponentImport @NotNull TransactionalExecutorFactory transactionalExecutorFactory, @ComponentImport @NotNull DarkFeatureManager darkFeatureManager, @NotNull UrlBuilder urlBuilder) {
        Intrinsics.checkNotNullParameter((Object)spaceManager, (String)"spaceManager");
        Intrinsics.checkNotNullParameter((Object)pageManager, (String)"pageManager");
        Intrinsics.checkNotNullParameter((Object)permissionManager, (String)"permissionManager");
        Intrinsics.checkNotNullParameter((Object)userAccessor, (String)"userAccessor");
        Intrinsics.checkNotNullParameter((Object)userManager, (String)"userManager");
        Intrinsics.checkNotNullParameter((Object)transactionalExecutorFactory, (String)"transactionalExecutorFactory");
        Intrinsics.checkNotNullParameter((Object)darkFeatureManager, (String)"darkFeatureManager");
        Intrinsics.checkNotNullParameter((Object)urlBuilder, (String)"urlBuilder");
        this.spaceManager = spaceManager;
        this.pageManager = pageManager;
        this.permissionManager = permissionManager;
        this.userAccessor = userAccessor;
        this.userManager = userManager;
        this.transactionalExecutorFactory = transactionalExecutorFactory;
        this.darkFeatureManager = darkFeatureManager;
        this.urlBuilder = urlBuilder;
    }

    @Override
    @NotNull
    public com.addonengine.addons.analytics.service.confluence.model.Space getByKey(@NotNull String key) {
        Intrinsics.checkNotNullParameter((Object)key, (String)"key");
        List<Space> candidates = this.getByKeysInternal(CollectionsKt.listOf((Object)key));
        if (candidates.isEmpty()) {
            throw new NoSpaceOrNoPermissionException(key);
        }
        return this.mapConfluenceSpaceToModelSpace(candidates.get(0), false);
    }

    @Override
    @Nullable
    public com.addonengine.addons.analytics.service.confluence.model.Space getByKeyOrNull(@NotNull String key, boolean includeCategories) {
        com.addonengine.addons.analytics.service.confluence.model.Space space;
        Intrinsics.checkNotNullParameter((Object)key, (String)"key");
        Space space2 = (Space)CollectionsKt.firstOrNull(this.getByKeysInternal(CollectionsKt.listOf((Object)key)));
        if (space2 != null) {
            Space it = space2;
            boolean bl = false;
            space = this.mapConfluenceSpaceToModelSpace(it, includeCategories);
        } else {
            space = null;
        }
        return space;
    }

    @Override
    @NotNull
    public List<com.addonengine.addons.analytics.service.confluence.model.Space> getSpaces(@NotNull Set<? extends SpaceType> spaceTypes) {
        Intrinsics.checkNotNullParameter(spaceTypes, (String)"spaceTypes");
        return this.getSpaces(spaceTypes, false);
    }

    @Override
    @NotNull
    public List<com.addonengine.addons.analytics.service.confluence.model.Space> getSpacesWithCategories(@NotNull Set<? extends SpaceType> spaceTypes) {
        Intrinsics.checkNotNullParameter(spaceTypes, (String)"spaceTypes");
        return this.getSpaces(spaceTypes, true);
    }

    @Override
    @NotNull
    public URL getSpacesLogoUrl(@NotNull String spaceKey, boolean redirectUrl) {
        Intrinsics.checkNotNullParameter((Object)spaceKey, (String)"spaceKey");
        Boolean bl = this.darkFeatureManager.isEnabledForAllUsers(defaultSpaceLogoDarkFeature).orElse(false);
        Intrinsics.checkNotNullExpressionValue((Object)bl, (String)"orElse(...)");
        String string = bl != false ? this.spaceManager.getLogoForGlobalcontext().getDownloadPath() : (redirectUrl ? "/rest/confanalytics/1.0/space/logo?spaceKey=" + spaceKey : this.spaceManager.getLogoForSpace(spaceKey).getDownloadPath());
        Intrinsics.checkNotNull((Object)string);
        return this.urlBuilder.buildHostCanonicalUri(string);
    }

    @Override
    @NotNull
    public URL getDefaultSpacesLogoUrl() {
        String string = this.spaceManager.getLogoForGlobalcontext().getDownloadPath();
        Intrinsics.checkNotNullExpressionValue((Object)string, (String)"getDownloadPath(...)");
        return this.urlBuilder.buildHostCanonicalUri(string);
    }

    @Override
    @NotNull
    public List<Content> getSpaceContent(@NotNull String key, @NotNull Set<? extends ContentType> contentTypes) {
        Intrinsics.checkNotNullParameter((Object)key, (String)"key");
        Intrinsics.checkNotNullParameter(contentTypes, (String)"contentTypes");
        return (List)BuildersKt.runBlocking$default(null, (Function2)((Function2)new Function2<CoroutineScope, Continuation<? super List<? extends Content>>, Object>(this, key, contentTypes, null){
            int label;
            private /* synthetic */ Object L$0;
            final /* synthetic */ SpaceServiceServerImpl this$0;
            final /* synthetic */ String $key;
            final /* synthetic */ Set<ContentType> $contentTypes;
            {
                this.this$0 = $receiver;
                this.$key = $key;
                this.$contentTypes = $contentTypes;
                super(2, $completion);
            }

            /*
             * Unable to fully structure code
             */
            @Nullable
            public final Object invokeSuspend(@NotNull Object var1_1) {
                var9_2 = IntrinsicsKt.getCOROUTINE_SUSPENDED();
                switch (this.label) {
                    case 0: {
                        ResultKt.throwOnFailure((Object)var1_1);
                        $this$runBlocking = (CoroutineScope)this.L$0;
                        space = (Space)SpaceServiceServerImpl.access$getByKeysInternal(this.this$0, CollectionsKt.listOf((Object)this.$key)).get(0);
                        modelSpace = SpaceServiceServerImpl.access$mapConfluenceSpaceToModelSpace(this.this$0, space, false);
                        currentUser = SpaceServiceServerImpl.access$getCurrentUser(this.this$0);
                        pageContentDeferred = BuildersKt.async$default((CoroutineScope)$this$runBlocking, null, null, (Function2)((Function2)new Function2<CoroutineScope, Continuation<? super List<? extends Content>>, Object>(this.this$0, this.$contentTypes, space, currentUser, modelSpace, null){
                            int label;
                            final /* synthetic */ SpaceServiceServerImpl this$0;
                            final /* synthetic */ Set<ContentType> $contentTypes;
                            final /* synthetic */ Space $space;
                            final /* synthetic */ User $currentUser;
                            final /* synthetic */ com.addonengine.addons.analytics.service.confluence.model.Space $modelSpace;
                            {
                                this.this$0 = $receiver;
                                this.$contentTypes = $contentTypes;
                                this.$space = $space;
                                this.$currentUser = $currentUser;
                                this.$modelSpace = $modelSpace;
                                super(2, $completion);
                            }

                            @Nullable
                            public final Object invokeSuspend(@NotNull Object object) {
                                IntrinsicsKt.getCOROUTINE_SUSPENDED();
                                switch (this.label) {
                                    case 0: {
                                        ResultKt.throwOnFailure((Object)object);
                                        return SpaceServiceServerImpl.access$getTransactionalExecutorFactory$p(this.this$0).createExecutor(true, true).execute(arg_0 -> getSpaceContent.pageContentDeferred.1.invokeSuspend$lambda$2(this.$contentTypes, this.this$0, this.$space, this.$currentUser, this.$modelSpace, arg_0));
                                    }
                                }
                                throw new IllegalStateException("call to 'resume' before 'invoke' with coroutine");
                            }

                            @NotNull
                            public final Continuation<Unit> create(@Nullable Object value, @NotNull Continuation<?> $completion) {
                                return (Continuation)new /* invalid duplicate definition of identical inner class */;
                            }

                            @Nullable
                            public final Object invoke(@NotNull CoroutineScope p1, @Nullable Continuation<? super List<Content>> p2) {
                                return (this.create(p1, p2)).invokeSuspend(Unit.INSTANCE);
                            }

                            /*
                             * WARNING - void declaration
                             */
                            private static final List invokeSuspend$lambda$2(Set $contentTypes, SpaceServiceServerImpl this$0, Space $space, User $currentUser, com.addonengine.addons.analytics.service.confluence.model.Space $modelSpace, Connection it) {
                                List list;
                                if ($contentTypes.contains((Object)((Object)ContentType.PAGE))) {
                                    void $this$mapTo$iv$iv;
                                    void $this$map$iv;
                                    Page it2;
                                    void $this$filterTo$iv$iv;
                                    Iterable $this$filter$iv;
                                    List list2 = SpaceServiceServerImpl.access$getPageManager$p(this$0).getPages($space, true);
                                    Intrinsics.checkNotNullExpressionValue((Object)list2, (String)"getPages(...)");
                                    Iterable iterable = list2;
                                    boolean $i$f$filter = false;
                                    void var8_8 = $this$filter$iv;
                                    Collection destination$iv$iv = new ArrayList<E>();
                                    boolean $i$f$filterTo = false;
                                    for (T element$iv$iv : $this$filterTo$iv$iv) {
                                        it2 = (Page)element$iv$iv;
                                        boolean bl = false;
                                        if (!SpaceServiceServerImpl.access$getPermissionManager$p(this$0).hasPermission($currentUser, Permission.VIEW, (Object)it2)) continue;
                                        destination$iv$iv.add(element$iv$iv);
                                    }
                                    $this$filter$iv = (List)destination$iv$iv;
                                    boolean $i$f$map = false;
                                    $this$filterTo$iv$iv = $this$map$iv;
                                    destination$iv$iv = new ArrayList<E>(CollectionsKt.collectionSizeOrDefault((Iterable)$this$map$iv, (int)10));
                                    boolean $i$f$mapTo = false;
                                    for (T item$iv$iv : $this$mapTo$iv$iv) {
                                        it2 = (Page)item$iv$iv;
                                        Collection collection = destination$iv$iv;
                                        boolean bl = false;
                                        long l = it2.getId();
                                        String string = it2.getDisplayTitle();
                                        Intrinsics.checkNotNullExpressionValue((Object)string, (String)"getDisplayTitle(...)");
                                        Date date = it2.getCreationDate();
                                        Instant instant = date != null ? date.toInstant() : null;
                                        Date date2 = it2.getLastModificationDate();
                                        Instant instant2 = date2 != null ? date2.toInstant() : null;
                                        UrlBuilder urlBuilder = SpaceServiceServerImpl.access$getUrlBuilder$p(this$0);
                                        String string2 = it2.getUrlPath();
                                        Intrinsics.checkNotNullExpressionValue((Object)string2, (String)"getUrlPath(...)");
                                        collection.add(new Content(ContentType.PAGE, l, string, instant, instant2, urlBuilder.buildHostCanonicalUri(string2), $modelSpace));
                                    }
                                    list = (List)destination$iv$iv;
                                } else {
                                    list = CollectionsKt.emptyList();
                                }
                                return list;
                            }
                        }), (int)3, null);
                        blogContentDeferred = BuildersKt.async$default((CoroutineScope)$this$runBlocking, null, null, (Function2)((Function2)new Function2<CoroutineScope, Continuation<? super List<? extends Content>>, Object>(this.this$0, this.$contentTypes, space, currentUser, modelSpace, null){
                            int label;
                            final /* synthetic */ SpaceServiceServerImpl this$0;
                            final /* synthetic */ Set<ContentType> $contentTypes;
                            final /* synthetic */ Space $space;
                            final /* synthetic */ User $currentUser;
                            final /* synthetic */ com.addonengine.addons.analytics.service.confluence.model.Space $modelSpace;
                            {
                                this.this$0 = $receiver;
                                this.$contentTypes = $contentTypes;
                                this.$space = $space;
                                this.$currentUser = $currentUser;
                                this.$modelSpace = $modelSpace;
                                super(2, $completion);
                            }

                            @Nullable
                            public final Object invokeSuspend(@NotNull Object object) {
                                IntrinsicsKt.getCOROUTINE_SUSPENDED();
                                switch (this.label) {
                                    case 0: {
                                        ResultKt.throwOnFailure((Object)object);
                                        return SpaceServiceServerImpl.access$getTransactionalExecutorFactory$p(this.this$0).createExecutor(true, true).execute(arg_0 -> getSpaceContent.blogContentDeferred.1.invokeSuspend$lambda$2(this.$contentTypes, this.this$0, this.$space, this.$currentUser, this.$modelSpace, arg_0));
                                    }
                                }
                                throw new IllegalStateException("call to 'resume' before 'invoke' with coroutine");
                            }

                            @NotNull
                            public final Continuation<Unit> create(@Nullable Object value, @NotNull Continuation<?> $completion) {
                                return (Continuation)new /* invalid duplicate definition of identical inner class */;
                            }

                            @Nullable
                            public final Object invoke(@NotNull CoroutineScope p1, @Nullable Continuation<? super List<Content>> p2) {
                                return (this.create(p1, p2)).invokeSuspend(Unit.INSTANCE);
                            }

                            /*
                             * WARNING - void declaration
                             */
                            private static final List invokeSuspend$lambda$2(Set $contentTypes, SpaceServiceServerImpl this$0, Space $space, User $currentUser, com.addonengine.addons.analytics.service.confluence.model.Space $modelSpace, Connection it) {
                                List list;
                                if ($contentTypes.contains((Object)((Object)ContentType.BLOG))) {
                                    void $this$mapTo$iv$iv;
                                    void $this$map$iv;
                                    BlogPost it2;
                                    void $this$filterTo$iv$iv;
                                    Iterable $this$filter$iv;
                                    List list2 = SpaceServiceServerImpl.access$getPageManager$p(this$0).getBlogPosts($space, true);
                                    Intrinsics.checkNotNullExpressionValue((Object)list2, (String)"getBlogPosts(...)");
                                    Iterable iterable = list2;
                                    boolean $i$f$filter = false;
                                    void var8_8 = $this$filter$iv;
                                    Collection destination$iv$iv = new ArrayList<E>();
                                    boolean $i$f$filterTo = false;
                                    for (T element$iv$iv : $this$filterTo$iv$iv) {
                                        it2 = (BlogPost)element$iv$iv;
                                        boolean bl = false;
                                        if (!SpaceServiceServerImpl.access$getPermissionManager$p(this$0).hasPermission($currentUser, Permission.VIEW, (Object)it2)) continue;
                                        destination$iv$iv.add(element$iv$iv);
                                    }
                                    $this$filter$iv = (List)destination$iv$iv;
                                    boolean $i$f$map = false;
                                    $this$filterTo$iv$iv = $this$map$iv;
                                    destination$iv$iv = new ArrayList<E>(CollectionsKt.collectionSizeOrDefault((Iterable)$this$map$iv, (int)10));
                                    boolean $i$f$mapTo = false;
                                    for (T item$iv$iv : $this$mapTo$iv$iv) {
                                        it2 = (BlogPost)item$iv$iv;
                                        Collection collection = destination$iv$iv;
                                        boolean bl = false;
                                        long l = it2.getId();
                                        String string = it2.getDisplayTitle();
                                        Intrinsics.checkNotNullExpressionValue((Object)string, (String)"getDisplayTitle(...)");
                                        Date date = it2.getCreationDate();
                                        Instant instant = date != null ? date.toInstant() : null;
                                        Date date2 = it2.getLastModificationDate();
                                        Instant instant2 = date2 != null ? date2.toInstant() : null;
                                        UrlBuilder urlBuilder = SpaceServiceServerImpl.access$getUrlBuilder$p(this$0);
                                        String string2 = it2.getUrlPath();
                                        Intrinsics.checkNotNullExpressionValue((Object)string2, (String)"getUrlPath(...)");
                                        collection.add(new Content(ContentType.BLOG, l, string, instant, instant2, urlBuilder.buildHostCanonicalUri(string2), $modelSpace));
                                    }
                                    list = (List)destination$iv$iv;
                                } else {
                                    list = CollectionsKt.emptyList();
                                }
                                return list;
                            }
                        }), (int)3, null);
                        this.L$0 = blogContentDeferred;
                        this.label = 1;
                        v0 = pageContentDeferred.await((Continuation)this);
                        if (v0 == var9_2) {
                            return var9_2;
                        }
                        ** GOTO lbl21
                    }
                    case 1: {
                        blogContentDeferred = (Deferred)this.L$0;
                        ResultKt.throwOnFailure((Object)$result);
                        v0 = $result;
lbl21:
                        // 2 sources

                        Intrinsics.checkNotNullExpressionValue((Object)v0, (String)"await(...)");
                        var8_9 = (Collection)v0;
                        this.L$0 = var8_9;
                        this.label = 2;
                        v1 = blogContentDeferred.await((Continuation)this);
                        if (v1 == var9_2) {
                            return var9_2;
                        }
                        ** GOTO lbl33
                    }
                    case 2: {
                        var8_9 = (Collection)this.L$0;
                        ResultKt.throwOnFailure((Object)$result);
                        v1 = $result;
lbl33:
                        // 2 sources

                        Intrinsics.checkNotNullExpressionValue((Object)v1, (String)"await(...)");
                        return CollectionsKt.plus((Collection)var8_9, (Iterable)((Iterable)v1));
                    }
                }
                throw new IllegalStateException("call to 'resume' before 'invoke' with coroutine");
            }

            @NotNull
            public final Continuation<Unit> create(@Nullable Object value, @NotNull Continuation<?> $completion) {
                Function2<CoroutineScope, Continuation<? super List<? extends Content>>, Object> function2 = new /* invalid duplicate definition of identical inner class */;
                function2.L$0 = value;
                return (Continuation)function2;
            }

            @Nullable
            public final Object invoke(@NotNull CoroutineScope p1, @Nullable Continuation<? super List<Content>> p2) {
                return (this.create(p1, p2)).invokeSuspend(Unit.INSTANCE);
            }
        }), (int)1, null);
    }

    /*
     * WARNING - void declaration
     */
    private final List<com.addonengine.addons.analytics.service.confluence.model.Space> getSpaces(Set<? extends SpaceType> spaceTypes, boolean includeCategories) {
        void $this$mapTo$iv$iv;
        User currentUser = this.getCurrentUser();
        SpacesQuery.Builder spacesQueryBuilder = SpacesQuery.newQuery().withSpaceStatus(SpaceStatus.CURRENT).forUser(currentUser);
        if (spaceTypes.contains((Object)SpaceType.GLOBAL) && !spaceTypes.contains((Object)SpaceType.PERSONAL)) {
            spacesQueryBuilder.withSpaceType(com.atlassian.confluence.spaces.SpaceType.GLOBAL);
        } else if (!spaceTypes.contains((Object)SpaceType.GLOBAL) && spaceTypes.contains((Object)SpaceType.PERSONAL)) {
            spacesQueryBuilder.withSpaceType(com.atlassian.confluence.spaces.SpaceType.PERSONAL);
        }
        ListBuilder listBuilder = this.spaceManager.getSpaces(spacesQueryBuilder.build());
        Intrinsics.checkNotNullExpressionValue((Object)listBuilder, (String)"getSpaces(...)");
        ListBuilder spaceListBuilder = listBuilder;
        List spaces = spaceListBuilder.getRange(0, spaceListBuilder.getAvailableSize());
        Intrinsics.checkNotNull((Object)spaces);
        Iterable $this$map$iv = spaces;
        boolean $i$f$map = false;
        Iterable iterable = $this$map$iv;
        Collection destination$iv$iv = new ArrayList(CollectionsKt.collectionSizeOrDefault((Iterable)$this$map$iv, (int)10));
        boolean $i$f$mapTo = false;
        for (Object item$iv$iv : $this$mapTo$iv$iv) {
            void it;
            Space space = (Space)item$iv$iv;
            Collection collection = destination$iv$iv;
            boolean bl = false;
            Intrinsics.checkNotNull((Object)it);
            collection.add(this.mapConfluenceSpaceToModelSpace((Space)it, includeCategories));
        }
        return (List)destination$iv$iv;
    }

    private final List<Space> getByKeysInternal(List<String> keys) {
        User currentUser = this.getCurrentUser();
        SpacesQuery spacesQuery = SpacesQuery.newQuery().withSpaceKeys((Iterable)keys).forUser(currentUser).build();
        ListBuilder listBuilder = this.spaceManager.getSpaces(spacesQuery);
        Intrinsics.checkNotNullExpressionValue((Object)listBuilder, (String)"getSpaces(...)");
        ListBuilder spaces = listBuilder;
        List list = spaces.getRange(0, spaces.getAvailableSize());
        Intrinsics.checkNotNullExpressionValue((Object)list, (String)"getRange(...)");
        return list;
    }

    /*
     * WARNING - void declaration
     */
    private final com.addonengine.addons.analytics.service.confluence.model.Space mapConfluenceSpaceToModelSpace(Space space, boolean includeCategories) {
        List list;
        long l = space.getId();
        String string = space.getKey();
        String string2 = string;
        Intrinsics.checkNotNullExpressionValue((Object)string, (String)"getKey(...)");
        SpaceType spaceType = space.isPersonal() ? SpaceType.PERSONAL : SpaceType.GLOBAL;
        String string3 = space.getName();
        String string4 = string3;
        Intrinsics.checkNotNullExpressionValue((Object)string3, (String)"getName(...)");
        Instant instant = space.getCreationDate().toInstant();
        Instant instant2 = instant;
        Intrinsics.checkNotNullExpressionValue((Object)instant, (String)"toInstant(...)");
        String string5 = space.getUrlPath();
        Intrinsics.checkNotNullExpressionValue((Object)string5, (String)"getUrlPath(...)");
        URL uRL = this.urlBuilder.buildHostCanonicalUri(string5);
        String string6 = space.getKey();
        Intrinsics.checkNotNullExpressionValue((Object)string6, (String)"getKey(...)");
        URL uRL2 = this.getSpacesLogoUrl(string6, true);
        if (includeCategories) {
            Object object = space.getDescription();
            if (object != null && (object = object.getLabels()) != null) {
                Collection<String> collection;
                void $this$mapTo$iv$iv;
                void $this$map$iv;
                Iterable iterable = (Iterable)object;
                URL uRL3 = uRL2;
                URL uRL4 = uRL;
                Instant instant3 = instant2;
                String string7 = string4;
                SpaceType spaceType2 = spaceType;
                String string8 = string2;
                long l2 = l;
                boolean $i$f$map = false;
                void var5_12 = $this$map$iv;
                Collection destination$iv$iv = new ArrayList(CollectionsKt.collectionSizeOrDefault((Iterable)$this$map$iv, (int)10));
                boolean $i$f$mapTo = false;
                for (Object item$iv$iv : $this$mapTo$iv$iv) {
                    void it;
                    Label label = (Label)item$iv$iv;
                    collection = destination$iv$iv;
                    boolean bl = false;
                    collection.add(it.getDisplayTitle());
                }
                collection = (List)destination$iv$iv;
                l = l2;
                string2 = string8;
                spaceType = spaceType2;
                string4 = string7;
                instant2 = instant3;
                uRL = uRL4;
                uRL2 = uRL3;
                list = collection;
            } else {
                list = CollectionsKt.emptyList();
            }
        } else {
            list = CollectionsKt.emptyList();
        }
        List list2 = list;
        URL uRL5 = uRL2;
        URL uRL6 = uRL;
        Instant instant4 = instant2;
        String string9 = string4;
        SpaceType spaceType3 = spaceType;
        String string10 = string2;
        long l3 = l;
        return new com.addonengine.addons.analytics.service.confluence.model.Space(l3, string10, spaceType3, string9, instant4, uRL6, uRL5, list2);
    }

    private final User getCurrentUser() {
        return (User)this.userAccessor.getUserByKey(this.userManager.getRemoteUserKey());
    }

    public static final /* synthetic */ List access$getByKeysInternal(SpaceServiceServerImpl $this, List keys) {
        return $this.getByKeysInternal(keys);
    }

    public static final /* synthetic */ com.addonengine.addons.analytics.service.confluence.model.Space access$mapConfluenceSpaceToModelSpace(SpaceServiceServerImpl $this, Space space, boolean includeCategories) {
        return $this.mapConfluenceSpaceToModelSpace(space, includeCategories);
    }

    public static final /* synthetic */ User access$getCurrentUser(SpaceServiceServerImpl $this) {
        return $this.getCurrentUser();
    }

    public static final /* synthetic */ TransactionalExecutorFactory access$getTransactionalExecutorFactory$p(SpaceServiceServerImpl $this) {
        return $this.transactionalExecutorFactory;
    }

    public static final /* synthetic */ PermissionManager access$getPermissionManager$p(SpaceServiceServerImpl $this) {
        return $this.permissionManager;
    }

    public static final /* synthetic */ UrlBuilder access$getUrlBuilder$p(SpaceServiceServerImpl $this) {
        return $this.urlBuilder;
    }

    public static final /* synthetic */ PageManager access$getPageManager$p(SpaceServiceServerImpl $this) {
        return $this.pageManager;
    }

    @Metadata(mv={1, 9, 0}, k=1, xi=48, d1={"\u0000\u0012\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0000\b\u0086\u0003\u0018\u00002\u00020\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002R\u000e\u0010\u0003\u001a\u00020\u0004X\u0086T\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u0005"}, d2={"Lcom/addonengine/addons/analytics/service/confluence/server/SpaceServiceServerImpl$Companion;", "", "()V", "defaultSpaceLogoDarkFeature", "", "analytics"})
    public static final class Companion {
        private Companion() {
        }

        public /* synthetic */ Companion(DefaultConstructorMarker $constructor_marker) {
            this();
        }
    }
}


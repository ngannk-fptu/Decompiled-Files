/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.user.ConfluenceUser
 *  com.atlassian.confluence.user.UserAccessor
 *  com.atlassian.plugin.spring.scanner.annotation.component.ConfluenceComponent
 *  com.atlassian.plugin.spring.scanner.annotation.export.ExportAsDevService
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.atlassian.sal.api.message.I18nResolver
 *  com.atlassian.sal.api.rdbms.TransactionalExecutorFactory
 *  com.atlassian.sal.api.user.UserKey
 *  com.atlassian.sal.api.user.UserManager
 *  com.atlassian.sal.api.user.UserProfile
 *  com.atlassian.user.User
 *  com.atlassian.util.profiling.UtilTimerStack
 *  kotlin.Metadata
 *  kotlin.ResultKt
 *  kotlin.Unit
 *  kotlin.collections.CollectionsKt
 *  kotlin.collections.MapsKt
 *  kotlin.collections.SetsKt
 *  kotlin.coroutines.Continuation
 *  kotlin.coroutines.intrinsics.IntrinsicsKt
 *  kotlin.jvm.functions.Function2
 *  kotlin.jvm.internal.Intrinsics
 *  kotlin.jvm.internal.Reflection
 *  kotlin.jvm.internal.SourceDebugExtension
 *  kotlin.ranges.RangesKt
 *  kotlin.reflect.KClass
 *  kotlinx.coroutines.BuildersKt
 *  kotlinx.coroutines.CoroutineScope
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.beans.factory.annotation.Autowired
 */
package com.addonengine.addons.analytics.service.confluence.server;

import com.addonengine.addons.analytics.service.SettingsService;
import com.addonengine.addons.analytics.service.confluence.UrlBuilder;
import com.addonengine.addons.analytics.service.confluence.UserService;
import com.addonengine.addons.analytics.service.confluence.model.Group;
import com.addonengine.addons.analytics.service.confluence.model.User;
import com.addonengine.addons.analytics.service.confluence.model.UserType;
import com.addonengine.addons.analytics.service.confluence.server.UserServiceServerImpl;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.confluence.user.UserAccessor;
import com.atlassian.plugin.spring.scanner.annotation.component.ConfluenceComponent;
import com.atlassian.plugin.spring.scanner.annotation.export.ExportAsDevService;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.sal.api.message.I18nResolver;
import com.atlassian.sal.api.rdbms.TransactionalExecutorFactory;
import com.atlassian.sal.api.user.UserKey;
import com.atlassian.sal.api.user.UserManager;
import com.atlassian.sal.api.user.UserProfile;
import com.atlassian.util.profiling.UtilTimerStack;
import java.net.URL;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import kotlin.Metadata;
import kotlin.ResultKt;
import kotlin.Unit;
import kotlin.collections.CollectionsKt;
import kotlin.collections.MapsKt;
import kotlin.collections.SetsKt;
import kotlin.coroutines.Continuation;
import kotlin.coroutines.intrinsics.IntrinsicsKt;
import kotlin.jvm.functions.Function2;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.Reflection;
import kotlin.jvm.internal.SourceDebugExtension;
import kotlin.ranges.RangesKt;
import kotlin.reflect.KClass;
import kotlinx.coroutines.BuildersKt;
import kotlinx.coroutines.CoroutineScope;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

@ExportAsDevService(value={UserService.class})
@ConfluenceComponent
@Metadata(mv={1, 9, 0}, k=1, xi=48, d1={"\u0000v\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0002\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u000b\n\u0000\n\u0002\u0010\"\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010$\n\u0002\b\u0002\b\u0007\u0018\u00002\u00020\u0001B?\b\u0007\u0012\b\b\u0001\u0010\u0002\u001a\u00020\u0003\u0012\b\b\u0001\u0010\u0004\u001a\u00020\u0005\u0012\b\b\u0001\u0010\u0006\u001a\u00020\u0007\u0012\b\b\u0001\u0010\b\u001a\u00020\t\u0012\u0006\u0010\n\u001a\u00020\u000b\u0012\u0006\u0010\f\u001a\u00020\r\u00a2\u0006\u0002\u0010\u000eJ\u0010\u0010\u0012\u001a\u00020\u00132\u0006\u0010\u0014\u001a\u00020\u0015H\u0002J\b\u0010\u0016\u001a\u00020\u0013H\u0016J\u0016\u0010\u0017\u001a\b\u0012\u0004\u0012\u00020\u00190\u00182\u0006\u0010\u0014\u001a\u00020\u0015H\u0016J\u0018\u0010\u001a\u001a\u00020\u00132\u0006\u0010\u0014\u001a\u00020\u00152\u0006\u0010\u001b\u001a\u00020\u001cH\u0002J\b\u0010\u001d\u001a\u00020\u0015H\u0016J\u0010\u0010\u001e\u001a\u00020\u00132\u0006\u0010\u0014\u001a\u00020\u0015H\u0016J\u0018\u0010\u001e\u001a\u00020\u00132\u0006\u0010\u0014\u001a\u00020\u00152\u0006\u0010\u001f\u001a\u00020 H\u0016J\u001c\u0010\u001e\u001a\b\u0012\u0004\u0012\u00020\u00130\u00182\f\u0010!\u001a\b\u0012\u0004\u0012\u00020\u00150\"H\u0016J$\u0010\u001e\u001a\b\u0012\u0004\u0012\u00020\u00130\u00182\f\u0010!\u001a\b\u0012\u0004\u0012\u00020\u00150\"2\u0006\u0010\u001f\u001a\u00020 H\u0016J \u0010#\u001a\u00020\u00132\u0006\u0010\u0014\u001a\u00020\u00152\u0006\u0010\u001b\u001a\u00020\u001c2\u0006\u0010\u001f\u001a\u00020 H\u0002J\u0012\u0010$\u001a\u0004\u0018\u00010\u00152\u0006\u0010%\u001a\u00020\u0015H\u0016J\u0012\u0010&\u001a\u00020'2\b\u0010\u0014\u001a\u0004\u0018\u00010\u0015H\u0016J\"\u0010(\u001a\u000e\u0012\u0004\u0012\u00020\u0015\u0012\u0004\u0012\u00020\u00130)2\f\u0010!\u001a\b\u0012\u0004\u0012\u00020\u00150\"H\u0016J\u0010\u0010*\u001a\u00020 2\u0006\u0010\u0014\u001a\u00020\u0015H\u0016R\u000e\u0010\b\u001a\u00020\tX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0016\u0010\u000f\u001a\n \u0011*\u0004\u0018\u00010\u00100\u0010X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\f\u001a\u00020\rX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0006\u001a\u00020\u0007X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\n\u001a\u00020\u000bX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0004\u001a\u00020\u0005X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004\u00a2\u0006\u0002\n\u0000\u00a8\u0006+"}, d2={"Lcom/addonengine/addons/analytics/service/confluence/server/UserServiceServerImpl;", "Lcom/addonengine/addons/analytics/service/confluence/UserService;", "userManager", "Lcom/atlassian/sal/api/user/UserManager;", "userAccessor", "Lcom/atlassian/confluence/user/UserAccessor;", "transactionalExecutorFactory", "Lcom/atlassian/sal/api/rdbms/TransactionalExecutorFactory;", "i18nResolver", "Lcom/atlassian/sal/api/message/I18nResolver;", "urlBuilder", "Lcom/addonengine/addons/analytics/service/confluence/UrlBuilder;", "settingsService", "Lcom/addonengine/addons/analytics/service/SettingsService;", "(Lcom/atlassian/sal/api/user/UserManager;Lcom/atlassian/confluence/user/UserAccessor;Lcom/atlassian/sal/api/rdbms/TransactionalExecutorFactory;Lcom/atlassian/sal/api/message/I18nResolver;Lcom/addonengine/addons/analytics/service/confluence/UrlBuilder;Lcom/addonengine/addons/analytics/service/SettingsService;)V", "log", "Lorg/slf4j/Logger;", "kotlin.jvm.PlatformType", "getAnonymisedUserDetails", "Lcom/addonengine/addons/analytics/service/confluence/model/User;", "userKey", "", "getAnonymousUserDetails", "getGroupsUserIsMemberOf", "", "Lcom/addonengine/addons/analytics/service/confluence/model/Group;", "getNonExistentUserDetails", "defaultProfilePictureUri", "Ljava/net/URL;", "getUnknownUserName", "getUserDetails", "ignoreIncreasedPrivacyMode", "", "userKeys", "", "getUserDetailsByUserKey", "getUserKeyByUsername", "username", "getUserType", "Lcom/addonengine/addons/analytics/service/confluence/model/UserType;", "getUsersDetailsMap", "", "isUserLicensed", "analytics"})
@SourceDebugExtension(value={"SMAP\nUserServiceServerImpl.kt\nKotlin\n*S Kotlin\n*F\n+ 1 UserServiceServerImpl.kt\ncom/addonengine/addons/analytics/service/confluence/server/UserServiceServerImpl\n+ 2 utils.kt\ncom/addonengine/addons/analytics/util/UtilsKt\n+ 3 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n*L\n1#1,160:1\n11#2,5:161\n17#2,5:170\n1549#3:166\n1620#3,3:167\n1194#3,2:175\n1222#3,4:177\n*S KotlinDebug\n*F\n+ 1 UserServiceServerImpl.kt\ncom/addonengine/addons/analytics/service/confluence/server/UserServiceServerImpl\n*L\n91#1:161,5\n91#1:170,5\n93#1:166\n93#1:167,3\n99#1:175,2\n99#1:177,4\n*E\n"})
public final class UserServiceServerImpl
implements UserService {
    @NotNull
    private final UserManager userManager;
    @NotNull
    private final UserAccessor userAccessor;
    @NotNull
    private final TransactionalExecutorFactory transactionalExecutorFactory;
    @NotNull
    private final I18nResolver i18nResolver;
    @NotNull
    private final UrlBuilder urlBuilder;
    @NotNull
    private final SettingsService settingsService;
    private final Logger log;

    @Autowired
    public UserServiceServerImpl(@ComponentImport @NotNull UserManager userManager, @ComponentImport @NotNull UserAccessor userAccessor, @ComponentImport @NotNull TransactionalExecutorFactory transactionalExecutorFactory, @ComponentImport @NotNull I18nResolver i18nResolver, @NotNull UrlBuilder urlBuilder, @NotNull SettingsService settingsService) {
        Intrinsics.checkNotNullParameter((Object)userManager, (String)"userManager");
        Intrinsics.checkNotNullParameter((Object)userAccessor, (String)"userAccessor");
        Intrinsics.checkNotNullParameter((Object)transactionalExecutorFactory, (String)"transactionalExecutorFactory");
        Intrinsics.checkNotNullParameter((Object)i18nResolver, (String)"i18nResolver");
        Intrinsics.checkNotNullParameter((Object)urlBuilder, (String)"urlBuilder");
        Intrinsics.checkNotNullParameter((Object)settingsService, (String)"settingsService");
        this.userManager = userManager;
        this.userAccessor = userAccessor;
        this.transactionalExecutorFactory = transactionalExecutorFactory;
        this.i18nResolver = i18nResolver;
        this.urlBuilder = urlBuilder;
        this.settingsService = settingsService;
        this.log = LoggerFactory.getLogger(this.getClass());
    }

    @Override
    @Nullable
    public String getUserKeyByUsername(@NotNull String username) {
        Intrinsics.checkNotNullParameter((Object)username, (String)"username");
        ConfluenceUser confluenceUser = this.userAccessor.getUserByName(username);
        return confluenceUser != null && (confluenceUser = confluenceUser.getKey()) != null ? confluenceUser.getStringValue() : null;
    }

    @Override
    @NotNull
    public User getUserDetails(@NotNull String userKey) {
        Intrinsics.checkNotNullParameter((Object)userKey, (String)"userKey");
        return (User)CollectionsKt.first(this.getUserDetails(SetsKt.setOf((Object)userKey), false));
    }

    @Override
    @NotNull
    public User getUserDetails(@NotNull String userKey, boolean ignoreIncreasedPrivacyMode) {
        Intrinsics.checkNotNullParameter((Object)userKey, (String)"userKey");
        return (User)CollectionsKt.first(this.getUserDetails(SetsKt.setOf((Object)userKey), ignoreIncreasedPrivacyMode));
    }

    @Override
    @NotNull
    public List<User> getUserDetails(@NotNull Set<String> userKeys) {
        Intrinsics.checkNotNullParameter(userKeys, (String)"userKeys");
        return this.getUserDetails(userKeys, false);
    }

    @Override
    @NotNull
    public List<User> getUserDetails(@NotNull Set<String> userKeys, boolean ignoreIncreasedPrivacyMode) {
        Intrinsics.checkNotNullParameter(userKeys, (String)"userKeys");
        Object object = BuildersKt.runBlocking$default(null, (Function2)((Function2)new Function2<CoroutineScope, Continuation<? super List<? extends User>>, Object>(this, userKeys, ignoreIncreasedPrivacyMode, null){
            int label;
            private /* synthetic */ Object L$0;
            final /* synthetic */ UserServiceServerImpl this$0;
            final /* synthetic */ Set<String> $userKeys;
            final /* synthetic */ boolean $ignoreIncreasedPrivacyMode;
            {
                this.this$0 = $receiver;
                this.$userKeys = $userKeys;
                this.$ignoreIncreasedPrivacyMode = $ignoreIncreasedPrivacyMode;
                super(2, $completion);
            }

            /*
             * WARNING - void declaration
             * Enabled force condition propagation
             * Lifted jumps to return sites
             */
            @Nullable
            public final Object invokeSuspend(@NotNull Object object) {
                Object object2 = IntrinsicsKt.getCOROUTINE_SUSPENDED();
                switch (this.label) {
                    case 0: {
                        ResultKt.throwOnFailure((Object)object);
                        CoroutineScope $this$runBlocking = (CoroutineScope)this.L$0;
                        URL defaultProfilePictureUri = UserServiceServerImpl.access$getUrlBuilder$p(this.this$0).buildHostCanonicalUri("/images/icons/profilepics/default.png");
                        this.label = 1;
                        Object object3 = BuildersKt.async$default((CoroutineScope)$this$runBlocking, null, null, (Function2)((Function2)new Function2<CoroutineScope, Continuation<? super List<? extends User>>, Object>(this.this$0, this.$userKeys, defaultProfilePictureUri, this.$ignoreIncreasedPrivacyMode, null){
                            int label;
                            final /* synthetic */ UserServiceServerImpl this$0;
                            final /* synthetic */ Set<String> $userKeys;
                            final /* synthetic */ URL $defaultProfilePictureUri;
                            final /* synthetic */ boolean $ignoreIncreasedPrivacyMode;
                            {
                                this.this$0 = $receiver;
                                this.$userKeys = $userKeys;
                                this.$defaultProfilePictureUri = $defaultProfilePictureUri;
                                this.$ignoreIncreasedPrivacyMode = $ignoreIncreasedPrivacyMode;
                                super(2, $completion);
                            }

                            @Nullable
                            public final Object invokeSuspend(@NotNull Object object) {
                                IntrinsicsKt.getCOROUTINE_SUSPENDED();
                                switch (this.label) {
                                    case 0: {
                                        ResultKt.throwOnFailure((Object)object);
                                        return UserServiceServerImpl.access$getTransactionalExecutorFactory$p(this.this$0).createExecutor(true, true).execute(arg_0 -> getUserDetails.1.invokeSuspend$lambda$1(this.$userKeys, this.this$0, this.$defaultProfilePictureUri, this.$ignoreIncreasedPrivacyMode, arg_0));
                                    }
                                }
                                throw new IllegalStateException("call to 'resume' before 'invoke' with coroutine");
                            }

                            @NotNull
                            public final Continuation<Unit> create(@Nullable Object value, @NotNull Continuation<?> $completion) {
                                return (Continuation)new /* invalid duplicate definition of identical inner class */;
                            }

                            @Nullable
                            public final Object invoke(@NotNull CoroutineScope p1, @Nullable Continuation<? super List<User>> p2) {
                                return (this.create(p1, p2)).invokeSuspend(Unit.INSTANCE);
                            }

                            /*
                             * WARNING - void declaration
                             */
                            private static final List invokeSuspend$lambda$1(Set $userKeys, UserServiceServerImpl this$0, URL $defaultProfilePictureUri, boolean $ignoreIncreasedPrivacyMode, Connection it) {
                                void $this$mapTo$iv$iv;
                                Iterable $this$map$iv = $userKeys;
                                boolean $i$f$map = false;
                                Iterable iterable = $this$map$iv;
                                Collection destination$iv$iv = new ArrayList<E>(CollectionsKt.collectionSizeOrDefault((Iterable)$this$map$iv, (int)10));
                                boolean $i$f$mapTo = false;
                                for (T item$iv$iv : $this$mapTo$iv$iv) {
                                    void it2;
                                    String string = (String)item$iv$iv;
                                    Collection collection = destination$iv$iv;
                                    boolean bl = false;
                                    collection.add(UserServiceServerImpl.access$getUserDetailsByUserKey(this$0, (String)it2, $defaultProfilePictureUri, $ignoreIncreasedPrivacyMode));
                                }
                                return (List)destination$iv$iv;
                            }
                        }), (int)3, null).await((Continuation)this);
                        if (object3 != object2) return object3;
                        return object2;
                    }
                    case 1: {
                        void $result;
                        ResultKt.throwOnFailure((Object)$result);
                        Object object3 = $result;
                        return object3;
                    }
                }
                throw new IllegalStateException("call to 'resume' before 'invoke' with coroutine");
            }

            @NotNull
            public final Continuation<Unit> create(@Nullable Object value, @NotNull Continuation<?> $completion) {
                Function2<CoroutineScope, Continuation<? super List<? extends User>>, Object> function2 = new /* invalid duplicate definition of identical inner class */;
                function2.L$0 = value;
                return (Continuation)function2;
            }

            @Nullable
            public final Object invoke(@NotNull CoroutineScope p1, @Nullable Continuation<? super List<User>> p2) {
                return (this.create(p1, p2)).invokeSuspend(Unit.INSTANCE);
            }
        }), (int)1, null);
        Intrinsics.checkNotNull((Object)object);
        return (List)object;
    }

    @Override
    @NotNull
    public UserType getUserType(@Nullable String userKey) {
        if (this.settingsService.getPrivacySettings().getEnabled()) {
            return UserType.ANONYMISED;
        }
        return UserType.AUTHENTICATED;
    }

    @Override
    @NotNull
    public User getAnonymousUserDetails() {
        return new User(UserType.ANONYMOUS, null, "Anonymous", null, this.urlBuilder.buildHostCanonicalUri("/images/icons/profilepics/anonymous.png"));
    }

    /*
     * WARNING - void declaration
     */
    @Override
    @NotNull
    public List<Group> getGroupsUserIsMemberOf(@NotNull String userKey) {
        void $this$mapTo$iv$iv;
        void klass$iv;
        Intrinsics.checkNotNullParameter((Object)userKey, (String)"userKey");
        KClass kClass = Reflection.getOrCreateKotlinClass(this.getClass());
        String name$iv = "getGroupsUserIsMemberOf";
        boolean $i$f$atlassianProfilingTimer = false;
        if (UtilTimerStack.isActive()) {
            UtilTimerStack.push((String)(klass$iv.getQualifiedName() + '_' + name$iv));
        }
        boolean bl = false;
        ConfluenceUser user = this.userAccessor.getUserByKey(new UserKey(userKey));
        List list = this.userAccessor.getGroupNames((com.atlassian.user.User)user);
        Intrinsics.checkNotNullExpressionValue((Object)list, (String)"getGroupNames(...)");
        Iterable $this$map$iv = list;
        boolean $i$f$map = false;
        Iterable iterable = $this$map$iv;
        Collection destination$iv$iv = new ArrayList(CollectionsKt.collectionSizeOrDefault((Iterable)$this$map$iv, (int)10));
        boolean $i$f$mapTo = false;
        for (Object item$iv$iv : $this$mapTo$iv$iv) {
            void it;
            String string = (String)item$iv$iv;
            Collection collection = destination$iv$iv;
            boolean bl2 = false;
            Intrinsics.checkNotNull((Object)it);
            collection.add(new Group((String)it));
        }
        List result$iv = (List)destination$iv$iv;
        if (UtilTimerStack.isActive()) {
            UtilTimerStack.pop((String)(klass$iv.getQualifiedName() + '_' + name$iv));
        }
        return result$iv;
    }

    /*
     * WARNING - void declaration
     */
    @Override
    @NotNull
    public Map<String, User> getUsersDetailsMap(@NotNull Set<String> userKeys) {
        void $this$associateByTo$iv$iv;
        Intrinsics.checkNotNullParameter(userKeys, (String)"userKeys");
        List<User> users = this.getUserDetails(userKeys);
        Iterable $this$associateBy$iv = users;
        boolean $i$f$associateBy = false;
        int capacity$iv = RangesKt.coerceAtLeast((int)MapsKt.mapCapacity((int)CollectionsKt.collectionSizeOrDefault((Iterable)$this$associateBy$iv, (int)10)), (int)16);
        Iterable iterable = $this$associateBy$iv;
        Map destination$iv$iv = new LinkedHashMap(capacity$iv);
        boolean $i$f$associateByTo = false;
        for (Object element$iv$iv : $this$associateByTo$iv$iv) {
            void it;
            User user = (User)element$iv$iv;
            Map map = destination$iv$iv;
            boolean bl = false;
            String string = it.getUserKey();
            Intrinsics.checkNotNull((Object)string);
            map.put(string, element$iv$iv);
        }
        return destination$iv$iv;
    }

    private final User getUserDetailsByUserKey(String userKey, URL defaultProfilePictureUri, boolean ignoreIncreasedPrivacyMode) {
        URL uRL;
        if (!ignoreIncreasedPrivacyMode && this.settingsService.getPrivacySettings().getEnabled()) {
            return this.getAnonymisedUserDetails(userKey);
        }
        UserProfile userProfile = this.userManager.getUserProfile(new UserKey(userKey));
        if (userProfile == null) {
            this.log.warn("No user exists with the user key '" + userKey + '\'');
            return this.getNonExistentUserDetails(userKey, defaultProfilePictureUri);
        }
        if (userProfile.getProfilePictureUri() == null) {
            uRL = defaultProfilePictureUri;
        } else {
            String string = userProfile.getProfilePictureUri().toString();
            Intrinsics.checkNotNullExpressionValue((Object)string, (String)"toString(...)");
            uRL = this.urlBuilder.buildHostCanonicalUri(string);
        }
        URL profilePictureUrl = uRL;
        String string = userProfile.getUserKey().getStringValue();
        String string2 = userProfile.getFullName();
        Intrinsics.checkNotNullExpressionValue((Object)string2, (String)"getFullName(...)");
        return new User(UserType.AUTHENTICATED, string, string2, userProfile.getEmail(), profilePictureUrl);
    }

    private final User getAnonymisedUserDetails(String userKey) {
        StringBuilder stringBuilder = new StringBuilder().append("User ");
        String string = userKey.substring(0, 6);
        Intrinsics.checkNotNullExpressionValue((Object)string, (String)"this as java.lang.String\u2026ing(startIndex, endIndex)");
        return new User(UserType.ANONYMISED, userKey, stringBuilder.append(string).toString(), userKey, null);
    }

    private final User getNonExistentUserDetails(String userKey, URL defaultProfilePictureUri) {
        return new User(UserType.UNKNOWN, userKey, this.getUnknownUserName(), null, defaultProfilePictureUri);
    }

    @Override
    @NotNull
    public String getUnknownUserName() {
        String string = this.i18nResolver.getText("com.addonengine.addons.analytics.unknown.user.display.name");
        Intrinsics.checkNotNullExpressionValue((Object)string, (String)"getText(...)");
        return string;
    }

    @Override
    public boolean isUserLicensed(@NotNull String userKey) {
        Intrinsics.checkNotNullParameter((Object)userKey, (String)"userKey");
        return this.userManager.isLicensed(new UserKey(userKey));
    }

    public static final /* synthetic */ UrlBuilder access$getUrlBuilder$p(UserServiceServerImpl $this) {
        return $this.urlBuilder;
    }

    public static final /* synthetic */ TransactionalExecutorFactory access$getTransactionalExecutorFactory$p(UserServiceServerImpl $this) {
        return $this.transactionalExecutorFactory;
    }

    public static final /* synthetic */ User access$getUserDetailsByUserKey(UserServiceServerImpl $this, String userKey, URL defaultProfilePictureUri, boolean ignoreIncreasedPrivacyMode) {
        return $this.getUserDetailsByUserKey(userKey, defaultProfilePictureUri, ignoreIncreasedPrivacyMode);
    }
}


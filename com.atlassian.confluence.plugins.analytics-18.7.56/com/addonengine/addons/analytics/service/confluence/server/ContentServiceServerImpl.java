/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.core.ContentEntityObject
 *  com.atlassian.confluence.core.VersionHistorySummary
 *  com.atlassian.confluence.pages.Attachment
 *  com.atlassian.confluence.pages.PageManager
 *  com.atlassian.confluence.search.service.ContentTypeEnum
 *  com.atlassian.confluence.security.Permission
 *  com.atlassian.confluence.security.PermissionManager
 *  com.atlassian.confluence.user.ConfluenceUser
 *  com.atlassian.confluence.user.UserAccessor
 *  com.atlassian.confluence.util.GeneralUtil
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.atlassian.sal.api.user.UserManager
 *  com.atlassian.user.User
 *  javax.inject.Inject
 *  javax.inject.Named
 *  kotlin.Metadata
 *  kotlin.collections.CollectionsKt
 *  kotlin.jvm.internal.Intrinsics
 *  kotlin.jvm.internal.SourceDebugExtension
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 */
package com.addonengine.addons.analytics.service.confluence.server;

import com.addonengine.addons.analytics.service.confluence.ContentService;
import com.addonengine.addons.analytics.service.confluence.NoContentOrNoPermissionException;
import com.addonengine.addons.analytics.service.confluence.SpaceService;
import com.addonengine.addons.analytics.service.confluence.UrlBuilder;
import com.addonengine.addons.analytics.service.confluence.model.Content;
import com.addonengine.addons.analytics.service.confluence.model.ContentVersion;
import com.addonengine.addons.analytics.service.confluence.model.Space;
import com.addonengine.addons.analytics.service.model.ContentType;
import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.core.VersionHistorySummary;
import com.atlassian.confluence.pages.Attachment;
import com.atlassian.confluence.pages.PageManager;
import com.atlassian.confluence.search.service.ContentTypeEnum;
import com.atlassian.confluence.security.Permission;
import com.atlassian.confluence.security.PermissionManager;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.confluence.user.UserAccessor;
import com.atlassian.confluence.util.GeneralUtil;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.sal.api.user.UserManager;
import com.atlassian.user.User;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import javax.inject.Inject;
import javax.inject.Named;
import kotlin.Metadata;
import kotlin.collections.CollectionsKt;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Named
@Metadata(mv={1, 9, 0}, k=1, xi=48, d1={"\u0000X\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\t\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\b\u0007\u0018\u00002\u00020\u0001B?\b\u0007\u0012\b\b\u0001\u0010\u0002\u001a\u00020\u0003\u0012\b\b\u0001\u0010\u0004\u001a\u00020\u0005\u0012\b\b\u0001\u0010\u0006\u001a\u00020\u0007\u0012\b\b\u0001\u0010\b\u001a\u00020\t\u0012\u0006\u0010\n\u001a\u00020\u000b\u0012\u0006\u0010\f\u001a\u00020\r\u00a2\u0006\u0002\u0010\u000eJ\u0016\u0010\u000f\u001a\b\u0012\u0004\u0012\u00020\u00110\u00102\u0006\u0010\u0012\u001a\u00020\u0013H\u0016J\u0010\u0010\u0014\u001a\u00020\u00152\u0006\u0010\u0016\u001a\u00020\u0013H\u0016J\u0012\u0010\u0017\u001a\u0004\u0018\u00010\u00152\u0006\u0010\u0016\u001a\u00020\u0013H\u0016J\u0012\u0010\u0018\u001a\u0004\u0018\u00010\u00192\u0006\u0010\u0016\u001a\u00020\u0013H\u0002J\u0010\u0010\u001a\u001a\u00020\u00192\u0006\u0010\u0016\u001a\u00020\u0013H\u0002J\u0016\u0010\u001b\u001a\b\u0012\u0004\u0012\u00020\u001c0\u00102\u0006\u0010\u0016\u001a\u00020\u0013H\u0016J\u0010\u0010\u001d\u001a\u00020\u00152\u0006\u0010\u001e\u001a\u00020\u0019H\u0002R\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0004\u001a\u00020\u0005X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\n\u001a\u00020\u000bX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\f\u001a\u00020\rX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0006\u001a\u00020\u0007X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\b\u001a\u00020\tX\u0082\u0004\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u001f"}, d2={"Lcom/addonengine/addons/analytics/service/confluence/server/ContentServiceServerImpl;", "Lcom/addonengine/addons/analytics/service/confluence/ContentService;", "pageManager", "Lcom/atlassian/confluence/pages/PageManager;", "permissionManager", "Lcom/atlassian/confluence/security/PermissionManager;", "userAccessor", "Lcom/atlassian/confluence/user/UserAccessor;", "userManager", "Lcom/atlassian/sal/api/user/UserManager;", "spaceService", "Lcom/addonengine/addons/analytics/service/confluence/SpaceService;", "urlBuilder", "Lcom/addonengine/addons/analytics/service/confluence/UrlBuilder;", "(Lcom/atlassian/confluence/pages/PageManager;Lcom/atlassian/confluence/security/PermissionManager;Lcom/atlassian/confluence/user/UserAccessor;Lcom/atlassian/sal/api/user/UserManager;Lcom/addonengine/addons/analytics/service/confluence/SpaceService;Lcom/addonengine/addons/analytics/service/confluence/UrlBuilder;)V", "getAttachments", "", "Lcom/addonengine/addons/analytics/service/confluence/model/Attachment;", "containerId", "", "getById", "Lcom/addonengine/addons/analytics/service/confluence/model/Content;", "id", "getByIdOrNull", "getByIdOrNullIfRestricted", "Lcom/atlassian/confluence/core/ContentEntityObject;", "getByIdWithPermissionsChecked", "getVersions", "Lcom/addonengine/addons/analytics/service/confluence/model/ContentVersion;", "toAnalyticsObject", "content", "analytics"})
@SourceDebugExtension(value={"SMAP\nContentServiceServerImpl.kt\nKotlin\n*S Kotlin\n*F\n+ 1 ContentServiceServerImpl.kt\ncom/addonengine/addons/analytics/service/confluence/server/ContentServiceServerImpl\n+ 2 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n*L\n1#1,99:1\n1549#2:100\n1620#2,3:101\n1549#2:104\n1620#2,3:105\n*S KotlinDebug\n*F\n+ 1 ContentServiceServerImpl.kt\ncom/addonengine/addons/analytics/service/confluence/server/ContentServiceServerImpl\n*L\n68#1:100\n68#1:101,3\n80#1:104\n80#1:105,3\n*E\n"})
public final class ContentServiceServerImpl
implements ContentService {
    @NotNull
    private final PageManager pageManager;
    @NotNull
    private final PermissionManager permissionManager;
    @NotNull
    private final UserAccessor userAccessor;
    @NotNull
    private final UserManager userManager;
    @NotNull
    private final SpaceService spaceService;
    @NotNull
    private final UrlBuilder urlBuilder;

    @Inject
    public ContentServiceServerImpl(@ComponentImport @NotNull PageManager pageManager, @ComponentImport @NotNull PermissionManager permissionManager, @ComponentImport @NotNull UserAccessor userAccessor, @ComponentImport @NotNull UserManager userManager, @NotNull SpaceService spaceService, @NotNull UrlBuilder urlBuilder) {
        Intrinsics.checkNotNullParameter((Object)pageManager, (String)"pageManager");
        Intrinsics.checkNotNullParameter((Object)permissionManager, (String)"permissionManager");
        Intrinsics.checkNotNullParameter((Object)userAccessor, (String)"userAccessor");
        Intrinsics.checkNotNullParameter((Object)userManager, (String)"userManager");
        Intrinsics.checkNotNullParameter((Object)spaceService, (String)"spaceService");
        Intrinsics.checkNotNullParameter((Object)urlBuilder, (String)"urlBuilder");
        this.pageManager = pageManager;
        this.permissionManager = permissionManager;
        this.userAccessor = userAccessor;
        this.userManager = userManager;
        this.spaceService = spaceService;
        this.urlBuilder = urlBuilder;
    }

    @Override
    @Nullable
    public Content getByIdOrNull(long id) {
        ContentEntityObject contentEntityObject = this.getByIdOrNullIfRestricted(id);
        if (contentEntityObject == null) {
            return null;
        }
        ContentEntityObject content = contentEntityObject;
        return this.toAnalyticsObject(content);
    }

    @Override
    @NotNull
    public Content getById(long id) {
        ContentEntityObject content = this.getByIdWithPermissionsChecked(id);
        return this.toAnalyticsObject(content);
    }

    private final Content toAnalyticsObject(ContentEntityObject content) {
        ContentType contentType;
        ContentTypeEnum contentTypeEnum = content.getTypeEnum();
        switch (contentTypeEnum == null ? -1 : WhenMappings.$EnumSwitchMapping$0[contentTypeEnum.ordinal()]) {
            case 1: {
                contentType = ContentType.PAGE;
                break;
            }
            case 2: {
                contentType = ContentType.BLOG;
                break;
            }
            default: {
                throw new UnsupportedOperationException("The " + content.getTypeEnum() + " content type isn't support yet.");
            }
        }
        ContentType contentType2 = contentType;
        String spaceKey = content.toPageContext().getSpaceKey();
        Intrinsics.checkNotNull((Object)spaceKey);
        Space space = this.spaceService.getByKey(spaceKey);
        long l = content.getId();
        String string = content.getTitle();
        Intrinsics.checkNotNullExpressionValue((Object)string, (String)"getTitle(...)");
        Date date = content.getCreationDate();
        Instant instant = date != null ? date.toInstant() : null;
        Date date2 = content.getLastModificationDate();
        Instant instant2 = date2 != null ? date2.toInstant() : null;
        String string2 = content.getUrlPath();
        Intrinsics.checkNotNullExpressionValue((Object)string2, (String)"getUrlPath(...)");
        return new Content(contentType2, l, string, instant, instant2, this.urlBuilder.buildHostCanonicalUri(string2), space);
    }

    /*
     * WARNING - void declaration
     */
    @Override
    @NotNull
    public List<ContentVersion> getVersions(long id) {
        void $this$mapTo$iv$iv;
        ContentEntityObject ceo = this.getByIdWithPermissionsChecked(id);
        List list = this.pageManager.getVersionHistorySummaries(ceo);
        Intrinsics.checkNotNullExpressionValue((Object)list, (String)"getVersionHistorySummaries(...)");
        List versions = list;
        Iterable $this$map$iv = versions;
        boolean $i$f$map = false;
        Iterable iterable = $this$map$iv;
        Collection destination$iv$iv = new ArrayList(CollectionsKt.collectionSizeOrDefault((Iterable)$this$map$iv, (int)10));
        boolean $i$f$mapTo = false;
        for (Object item$iv$iv : $this$mapTo$iv$iv) {
            void it;
            VersionHistorySummary versionHistorySummary = (VersionHistorySummary)item$iv$iv;
            Collection collection = destination$iv$iv;
            boolean bl = false;
            int n = it.getVersion();
            Date date = it.getLastModificationDate();
            Instant instant = date != null ? date.toInstant() : null;
            String string = GeneralUtil.getPageUrl((VersionHistorySummary)it);
            Intrinsics.checkNotNullExpressionValue((Object)string, (String)"getPageUrl(...)");
            collection.add(new ContentVersion(n, instant, this.urlBuilder.buildHostCanonicalUri(string)));
        }
        return (List)destination$iv$iv;
    }

    /*
     * WARNING - void declaration
     */
    @Override
    @NotNull
    public List<com.addonengine.addons.analytics.service.confluence.model.Attachment> getAttachments(long containerId) {
        void $this$mapTo$iv$iv;
        ContentEntityObject ceo = this.getByIdWithPermissionsChecked(containerId);
        List list = ceo.getLatestVersionsOfAttachments();
        Intrinsics.checkNotNullExpressionValue((Object)list, (String)"getLatestVersionsOfAttachments(...)");
        Iterable $this$map$iv = list;
        boolean $i$f$map = false;
        Iterable iterable = $this$map$iv;
        Collection destination$iv$iv = new ArrayList(CollectionsKt.collectionSizeOrDefault((Iterable)$this$map$iv, (int)10));
        boolean $i$f$mapTo = false;
        for (Object item$iv$iv : $this$mapTo$iv$iv) {
            void it;
            Attachment attachment = (Attachment)item$iv$iv;
            Collection collection = destination$iv$iv;
            boolean bl = false;
            long l = it.getId();
            String string = it.getFileName();
            Intrinsics.checkNotNullExpressionValue((Object)string, (String)"getFileName(...)");
            String string2 = it.getDownloadPath();
            Intrinsics.checkNotNullExpressionValue((Object)string2, (String)"getDownloadPath(...)");
            collection.add(new com.addonengine.addons.analytics.service.confluence.model.Attachment(l, string, this.urlBuilder.buildHostCanonicalUri(string2)));
        }
        return (List)destination$iv$iv;
    }

    private final ContentEntityObject getByIdWithPermissionsChecked(long id) {
        ContentEntityObject contentEntityObject = this.getByIdOrNullIfRestricted(id);
        if (contentEntityObject == null) {
            throw new NoContentOrNoPermissionException(id);
        }
        return contentEntityObject;
    }

    private final ContentEntityObject getByIdOrNullIfRestricted(long id) {
        ContentEntityObject contentEntityObject = this.pageManager.getById(id);
        if (contentEntityObject == null) {
            return null;
        }
        ContentEntityObject content = contentEntityObject;
        ConfluenceUser currentUser = this.userAccessor.getUserByKey(this.userManager.getRemoteUserKey());
        if (!this.permissionManager.hasPermission((User)currentUser, Permission.VIEW, (Object)content)) {
            return null;
        }
        return content;
    }

    @Metadata(mv={1, 9, 0}, k=3, xi=48)
    public final class WhenMappings {
        public static final /* synthetic */ int[] $EnumSwitchMapping$0;

        static {
            int[] nArray = new int[ContentTypeEnum.values().length];
            try {
                nArray[ContentTypeEnum.PAGE.ordinal()] = 1;
            }
            catch (NoSuchFieldError noSuchFieldError) {
                // empty catch block
            }
            try {
                nArray[ContentTypeEnum.BLOG.ordinal()] = 2;
            }
            catch (NoSuchFieldError noSuchFieldError) {
                // empty catch block
            }
            $EnumSwitchMapping$0 = nArray;
        }
    }
}


/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.model.Expansions
 *  com.atlassian.confluence.mail.notification.NotificationManager
 *  com.atlassian.confluence.pages.Page
 *  com.atlassian.confluence.security.PermissionManager
 *  com.atlassian.confluence.spaces.Space
 *  com.atlassian.confluence.spaces.SpaceLogoManager
 *  com.atlassian.confluence.user.AuthenticatedUserThreadLocal
 *  com.atlassian.confluence.user.ConfluenceUser
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.atlassian.user.User
 *  javax.annotation.Nullable
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.stereotype.Component
 */
package com.atlassian.confluence.plugins.mobile.service.converter;

import com.atlassian.confluence.api.model.Expansions;
import com.atlassian.confluence.mail.notification.NotificationManager;
import com.atlassian.confluence.pages.Page;
import com.atlassian.confluence.plugins.mobile.dto.AbstractPageDto;
import com.atlassian.confluence.plugins.mobile.dto.SpaceDto;
import com.atlassian.confluence.plugins.mobile.dto.metadata.CurrentUserMetadataDto;
import com.atlassian.confluence.plugins.mobile.service.converter.MobileConverter;
import com.atlassian.confluence.security.PermissionManager;
import com.atlassian.confluence.spaces.Space;
import com.atlassian.confluence.spaces.SpaceLogoManager;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.user.User;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class MobileSpaceConverter
implements MobileConverter<SpaceDto, Space> {
    private static final String LOGO_PATH_FIELD = "logoPath";
    private static final String HOME_PAGE_FIELD = "homePage";
    private static final String WATCHED = "watched";
    private final SpaceLogoManager spaceLogoManager;
    private final NotificationManager notificationManager;
    private final PermissionManager permissionManager;

    @Autowired
    public MobileSpaceConverter(@ComponentImport SpaceLogoManager spaceLogoManager, @ComponentImport NotificationManager notificationManager, @ComponentImport PermissionManager permissionManager) {
        this.spaceLogoManager = spaceLogoManager;
        this.notificationManager = notificationManager;
        this.permissionManager = permissionManager;
    }

    @Override
    public SpaceDto to(Space source) {
        return this.to(source, Expansions.EMPTY);
    }

    @Override
    public SpaceDto to(Space source, Expansions expansions) {
        return this.to(source, null, expansions);
    }

    public SpaceDto to(Space source, @Nullable SpaceDto.ResultType resultType, Expansions expansions) {
        Page homePage;
        SpaceDto.Builder builder = SpaceDto.builder();
        builder.id(source.getId());
        builder.key(source.getKey());
        builder.name(source.getName());
        builder.type(source.getSpaceType().toString());
        ConfluenceUser loginUser = AuthenticatedUserThreadLocal.get();
        builder.addPermission("addPage", () -> this.permissionManager.hasCreatePermission((User)loginUser, (Object)source, Page.class));
        if (resultType != null) {
            builder.resultType(resultType.getValue());
        }
        if (expansions.canExpand(LOGO_PATH_FIELD)) {
            builder.logoPath(this.spaceLogoManager.getLogoUriReference(source, (User)AuthenticatedUserThreadLocal.get()));
        }
        if (expansions.canExpand(HOME_PAGE_FIELD) && (homePage = source.getHomePage()) != null) {
            builder.homePage(AbstractPageDto.builder().id(homePage.getId()).title(homePage.getTitle()).build());
        }
        if (expansions.canExpand(WATCHED)) {
            ConfluenceUser currentUser = AuthenticatedUserThreadLocal.get();
            boolean spaceWatched = this.notificationManager.getNotificationByUserAndSpace((User)currentUser, source) != null;
            builder.currentUser(CurrentUserMetadataDto.builder().watched(spaceWatched).build());
        }
        return builder.build();
    }

    @Override
    public List<SpaceDto> to(List<Space> sources) {
        if (sources == null || sources.isEmpty()) {
            return Collections.emptyList();
        }
        return sources.stream().map(this::to).collect(Collectors.toList());
    }
}


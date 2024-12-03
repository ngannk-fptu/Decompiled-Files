/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.model.Expansions
 *  com.atlassian.confluence.content.render.xhtml.ConversionContext
 *  com.atlassian.confluence.content.render.xhtml.DefaultConversionContext
 *  com.atlassian.confluence.content.render.xhtml.Renderer
 *  com.atlassian.confluence.core.ContentEntityObject
 *  com.atlassian.confluence.mail.notification.NotificationManager
 *  com.atlassian.confluence.spaces.Space
 *  com.atlassian.confluence.spaces.Spaced
 *  com.atlassian.confluence.user.AuthenticatedUserThreadLocal
 *  com.atlassian.confluence.user.ConfluenceUser
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.atlassian.renderer.RenderContext
 *  com.atlassian.user.User
 *  javax.annotation.Nullable
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.beans.factory.annotation.Qualifier
 *  org.springframework.stereotype.Component
 */
package com.atlassian.confluence.plugins.mobile.service.converter;

import com.atlassian.confluence.api.model.Expansions;
import com.atlassian.confluence.content.render.xhtml.ConversionContext;
import com.atlassian.confluence.content.render.xhtml.DefaultConversionContext;
import com.atlassian.confluence.content.render.xhtml.Renderer;
import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.mail.notification.NotificationManager;
import com.atlassian.confluence.plugins.mobile.dto.AbstractPageDto;
import com.atlassian.confluence.plugins.mobile.dto.SpaceDto;
import com.atlassian.confluence.plugins.mobile.dto.metadata.ContentMetadataDto;
import com.atlassian.confluence.plugins.mobile.dto.metadata.CurrentUserMetadataDto;
import com.atlassian.confluence.plugins.mobile.helper.TimeHelper;
import com.atlassian.confluence.plugins.mobile.service.converter.MobileConverter;
import com.atlassian.confluence.plugins.mobile.service.converter.MobileSpaceConverter;
import com.atlassian.confluence.plugins.mobile.service.factory.PersonFactory;
import com.atlassian.confluence.spaces.Space;
import com.atlassian.confluence.spaces.Spaced;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.renderer.RenderContext;
import com.atlassian.user.User;
import javax.annotation.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
public class MobileAbstractPageConverter
implements MobileConverter<AbstractPageDto, ContentEntityObject> {
    private static final String MOBILE_VIEW_RENDERER = "mobile-view-renderer";
    private final Renderer mobileViewRenderer;
    private final PersonFactory personFactory;
    private final TimeHelper timeHelper;
    private final MobileSpaceConverter spaceConverter;
    private final NotificationManager notificationManager;

    @Autowired
    public MobileAbstractPageConverter(@Qualifier(value="mobileViewRenderer") Renderer mobileViewRenderer, PersonFactory personFactory, TimeHelper timeHelper, MobileSpaceConverter spaceConverter, @ComponentImport NotificationManager notificationManager) {
        this.mobileViewRenderer = mobileViewRenderer;
        this.personFactory = personFactory;
        this.timeHelper = timeHelper;
        this.spaceConverter = spaceConverter;
        this.notificationManager = notificationManager;
    }

    @Override
    public AbstractPageDto to(ContentEntityObject content) {
        return this.to(content, Expansions.EMPTY);
    }

    @Override
    public AbstractPageDto to(ContentEntityObject content, Expansions expansions) {
        ContentMetadataDto metadata = null;
        if (expansions.canExpand("metadata")) {
            metadata = ContentMetadataDto.builder().build();
        }
        return this.to(content, metadata, expansions);
    }

    public AbstractPageDto to(ContentEntityObject content, @Nullable ContentMetadataDto metadataDto, Expansions expansions) {
        ContentMetadataDto.Builder metadataBuilder;
        AbstractPageDto.Builder builder = AbstractPageDto.builder().id(content.getId()).title(content.getTitle()).contentType(content.getType()).lastModifiedDate(content.getLastModificationDate());
        ContentMetadataDto.Builder builder2 = metadataBuilder = metadataDto != null ? ContentMetadataDto.builder(metadataDto) : ContentMetadataDto.builder();
        if (expansions.canExpand("body")) {
            DefaultConversionContext conversionContext = new DefaultConversionContext((RenderContext)content.toPageContext(), "mobile");
            conversionContext.setProperty(MOBILE_VIEW_RENDERER, (Object)true);
            builder.body(this.mobileViewRenderer.render(content, (ConversionContext)conversionContext));
        }
        if (expansions.canExpand("space")) {
            builder.space(this.convertToSpaceDto(content, expansions));
        }
        if (expansions.canExpand("author")) {
            builder.author(this.personFactory.forUser(content.getCreator()));
        }
        if (expansions.canExpand("timeToRead")) {
            builder.timeToRead(this.timeHelper.timeToRead(content.getBodyAsStringWithoutMarkup()));
        }
        if (expansions.canExpand("watched")) {
            ConfluenceUser user = AuthenticatedUserThreadLocal.get();
            boolean watched = this.notificationManager.isWatchingContent((User)user, content);
            metadataBuilder.currentUser(CurrentUserMetadataDto.builder().watched(watched).build());
        }
        builder.metadata(metadataBuilder.build());
        return builder.build();
    }

    private SpaceDto convertToSpaceDto(ContentEntityObject content, Expansions expansions) {
        if (content instanceof Spaced) {
            Space space = ((Spaced)content).getSpace();
            return space == null ? null : this.spaceConverter.to(space, expansions);
        }
        return null;
    }
}


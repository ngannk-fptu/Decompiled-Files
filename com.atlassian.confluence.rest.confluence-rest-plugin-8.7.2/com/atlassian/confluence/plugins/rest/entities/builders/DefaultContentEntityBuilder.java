/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.core.ContentEntityObject
 *  com.atlassian.confluence.core.SpaceContentEntityObject
 *  com.atlassian.confluence.core.persistence.hibernate.HibernateHandle
 *  com.atlassian.confluence.search.contentnames.SearchResult
 *  com.atlassian.confluence.search.service.ContentTypeEnum
 *  com.atlassian.confluence.search.v2.SearchResult
 *  com.atlassian.confluence.setup.settings.GlobalSettingsManager
 *  com.atlassian.confluence.spaces.Space
 *  com.atlassian.confluence.user.ConfluenceUser
 *  com.atlassian.plugins.rest.common.Link
 */
package com.atlassian.confluence.plugins.rest.entities.builders;

import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.core.SpaceContentEntityObject;
import com.atlassian.confluence.core.persistence.hibernate.HibernateHandle;
import com.atlassian.confluence.plugins.rest.entities.ContentEntity;
import com.atlassian.confluence.plugins.rest.entities.SearchResultEntity;
import com.atlassian.confluence.plugins.rest.entities.SpaceEntity;
import com.atlassian.confluence.plugins.rest.entities.builders.ContentEntityBuilder;
import com.atlassian.confluence.plugins.rest.entities.builders.SearchEntityBuilder;
import com.atlassian.confluence.plugins.rest.manager.DateEntityFactory;
import com.atlassian.confluence.plugins.rest.manager.RequestContext;
import com.atlassian.confluence.plugins.rest.manager.RequestContextThreadLocal;
import com.atlassian.confluence.plugins.rest.manager.UserEntityHelper;
import com.atlassian.confluence.search.contentnames.SearchResult;
import com.atlassian.confluence.search.service.ContentTypeEnum;
import com.atlassian.confluence.setup.settings.GlobalSettingsManager;
import com.atlassian.confluence.spaces.Space;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.plugins.rest.common.Link;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DefaultContentEntityBuilder<T extends ContentEntityObject>
implements SearchEntityBuilder,
ContentEntityBuilder<T> {
    protected final GlobalSettingsManager settingsManager;
    private final DateEntityFactory dateEntityFactory;
    private final UserEntityHelper userEntityHelper;

    public DefaultContentEntityBuilder(GlobalSettingsManager settingsManager, DateEntityFactory dateEntityFactory, UserEntityHelper userEntityHelper) {
        this.settingsManager = settingsManager;
        this.dateEntityFactory = dateEntityFactory;
        this.userEntityHelper = userEntityHelper;
    }

    @Override
    public SearchResultEntity build(com.atlassian.confluence.search.v2.SearchResult result) {
        long id = ((HibernateHandle)result.getHandle()).getId();
        String title = result.getDisplayTitle();
        String spaceKey = result.getSpaceKey();
        String spaceName = result.getSpaceName();
        String url = result.getUrlPath();
        Date creationDate = result.getCreationDate();
        Date lastModificationDate = result.getLastModificationDate();
        ConfluenceUser creator = result.getCreatorUser();
        ConfluenceUser lastModifier = result.getLastModifierUser();
        String type = result.getType();
        return this.createContentEntity(id, title, spaceKey, spaceName, url, creationDate, lastModificationDate, type, creator, lastModifier);
    }

    private String buildWikiLink(String type, Date creationDate, String spaceKey, String title) {
        ContentTypeEnum contentType = ContentTypeEnum.getByRepresentation((String)type);
        if (contentType == null) {
            return null;
        }
        switch (contentType) {
            case BLOG: {
                return "[" + spaceKey + ":" + this.format(creationDate) + "/" + title + "]";
            }
            case PAGE: {
                return "[" + spaceKey + ":" + title + "]";
            }
        }
        return null;
    }

    private String format(Date date) {
        return date == null ? "" : new SimpleDateFormat("/yyyy/MM/dd").format(date);
    }

    @Override
    public SearchResultEntity build(SearchResult result) {
        Long id = result.getId();
        String title = result.getName();
        String spaceKey = result.getSpaceKey();
        String spaceName = result.getSpaceName();
        String url = result.getUrl();
        Date creationDate = result.getCreatedDate();
        Date lastModificationDate = result.getLastModifiedDate();
        String type = result.getContentType();
        ConfluenceUser creator = result.getCreatorUser();
        ConfluenceUser lastModifier = result.getCreatorUser();
        return this.createContentEntity(id, title, spaceKey, spaceName, url, creationDate, lastModificationDate, type, creator, lastModifier);
    }

    private ContentEntity createContentEntity(Long id, String title, String spaceKey, String spaceName, String url, Date creationDate, Date lastModificationDate, String type) {
        ContentEntity contentEntity = new ContentEntity();
        contentEntity.setTitle(title);
        if (spaceKey != null) {
            contentEntity.setSpace(DefaultContentEntityBuilder.createSpaceEntity(spaceKey, spaceName));
        }
        contentEntity.setId(String.valueOf(id));
        try {
            contentEntity.addLink(Link.link((URI)new URI(this.settingsManager.getGlobalSettings().getBaseUrl() + url), (String)"alternate", (String)"text/html"));
            contentEntity.addLink(Link.link((URI)new URI(this.settingsManager.getGlobalSettings().getBaseUrl() + "/spaces/flyingpdf/pdfpageexport.action?pageId=" + id), (String)"alternate", (String)"application/pdf"));
        }
        catch (URISyntaxException uRISyntaxException) {
            // empty catch block
        }
        contentEntity.addLink(Link.self((URI)RequestContextThreadLocal.get().getUriBuilder("content").build(new Object[]{id})));
        contentEntity.setCreatedDate(this.dateEntityFactory.buildDateEntity(creationDate));
        contentEntity.setLastModifiedDate(this.dateEntityFactory.buildDateEntity(lastModificationDate));
        contentEntity.setType(type);
        contentEntity.setWikiLink(this.buildWikiLink(type, creationDate, spaceKey, title));
        return contentEntity;
    }

    private ContentEntity createContentEntity(Long id, String title, String spaceKey, String spaceName, String url, Date creationDate, Date lastModificationDate, String type, ConfluenceUser creator, ConfluenceUser lastModifier) {
        ContentEntity entity = this.createContentEntity(id, title, spaceKey, spaceName, url, creationDate, lastModificationDate, type);
        entity.setCreator(this.userEntityHelper.buildEntityForUser(creator));
        entity.setLastModifier(this.userEntityHelper.buildEntityForUser(lastModifier));
        return entity;
    }

    @Override
    public ContentEntity build(T object) {
        Space space;
        Long id = object.getId();
        String title = object.getTitle();
        String url = object.getUrlPath();
        Date creationDate = object.getCreationDate();
        Date lastModificationDate = object.getLastModificationDate();
        ConfluenceUser creator = object.getCreator();
        ConfluenceUser lastModifier = object.getLastModifier();
        String type = object.getType();
        String spaceKey = null;
        String spaceName = null;
        if (object instanceof SpaceContentEntityObject && (space = ((SpaceContentEntityObject)object).getSpace()) != null) {
            spaceKey = space.getKey();
            spaceName = space.getName();
        }
        return this.createContentEntity(id, title, spaceKey, spaceName, url, creationDate, lastModificationDate, type, creator, lastModifier);
    }

    public static SpaceEntity createSpaceEntity(Space space) {
        return space != null ? DefaultContentEntityBuilder.createSpaceEntity(space.getKey(), space.getName()) : null;
    }

    static SpaceEntity createSpaceEntity(String spaceKey, String spaceName) {
        RequestContext requestContext = RequestContextThreadLocal.get();
        SpaceEntity spaceEntity = new SpaceEntity();
        spaceEntity.setKey(spaceKey);
        spaceEntity.setName(spaceName);
        spaceEntity.addLink(Link.self((URI)requestContext.getUriBuilder("space").build(new Object[]{spaceKey})));
        return spaceEntity;
    }
}


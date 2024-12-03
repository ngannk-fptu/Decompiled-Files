/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.bonnie.Handle
 *  com.atlassian.confluence.core.persistence.AnyTypeDao
 *  com.atlassian.confluence.core.persistence.hibernate.HibernateHandle
 *  com.atlassian.confluence.search.contentnames.SearchResult
 *  com.atlassian.confluence.search.v2.SearchResult
 *  com.atlassian.confluence.setup.settings.SettingsManager
 *  com.atlassian.confluence.spaces.Space
 *  com.atlassian.confluence.spaces.SpaceDescription
 *  com.atlassian.plugins.rest.common.Link
 */
package com.atlassian.confluence.plugins.rest.entities.builders;

import com.atlassian.bonnie.Handle;
import com.atlassian.confluence.core.persistence.AnyTypeDao;
import com.atlassian.confluence.core.persistence.hibernate.HibernateHandle;
import com.atlassian.confluence.plugins.rest.entities.SearchResultEntity;
import com.atlassian.confluence.plugins.rest.entities.SpaceEntity;
import com.atlassian.confluence.plugins.rest.entities.builders.SearchEntityBuilder;
import com.atlassian.confluence.plugins.rest.manager.DateEntityFactory;
import com.atlassian.confluence.plugins.rest.manager.RequestContextThreadLocal;
import com.atlassian.confluence.search.contentnames.SearchResult;
import com.atlassian.confluence.setup.settings.SettingsManager;
import com.atlassian.confluence.spaces.Space;
import com.atlassian.confluence.spaces.SpaceDescription;
import com.atlassian.plugins.rest.common.Link;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Date;

public class SpaceEntityBuilder
implements SearchEntityBuilder {
    private DateEntityFactory dateEntityFactory;
    private SettingsManager settingsManager;
    private AnyTypeDao anyTypeDao;

    public SpaceEntityBuilder(DateEntityFactory dateEntityFactory, SettingsManager settingsManager, AnyTypeDao anyTypeDao) {
        this.dateEntityFactory = dateEntityFactory;
        this.settingsManager = settingsManager;
        this.anyTypeDao = anyTypeDao;
    }

    @Override
    public SearchResultEntity build(com.atlassian.confluence.search.v2.SearchResult result) {
        SpaceEntity entity = new SpaceEntity();
        String spaceName = result.getSpaceName();
        String spaceKey = result.getSpaceKey();
        Date creationDate = result.getCreationDate();
        Date lastModificationDate = result.getLastModificationDate();
        String url = result.getUrlPath();
        Handle handle = result.getHandle();
        long spaceDescriptionId = -1L;
        if (handle instanceof HibernateHandle) {
            spaceDescriptionId = ((HibernateHandle)handle).getId();
        }
        this.setSpaceProperties(entity, spaceName, spaceKey, url, creationDate, lastModificationDate, this.getSpaceIdFromSpaceDescriptionId(spaceDescriptionId));
        return entity;
    }

    @Override
    public SearchResultEntity build(SearchResult result) {
        SpaceEntity entity = new SpaceEntity();
        String spaceName = result.getName();
        String spaceKey = result.getSpaceKey();
        Date creationDate = result.getCreatedDate();
        Date lastModificationDate = result.getLastModifiedDate();
        String url = result.getUrl();
        long spaceDescriptionId = result.getId() != null ? result.getId() : -1L;
        this.setSpaceProperties(entity, spaceName, spaceKey, url, creationDate, lastModificationDate, this.getSpaceIdFromSpaceDescriptionId(spaceDescriptionId));
        return entity;
    }

    private long getSpaceIdFromSpaceDescriptionId(long spaceDescriptionId) {
        Space space;
        long result = -1L;
        if (spaceDescriptionId > 0L && (space = ((SpaceDescription)this.anyTypeDao.getByIdAndType(spaceDescriptionId, SpaceDescription.class)).getSpace()) != null) {
            result = space.getId();
        }
        return result;
    }

    private void setSpaceProperties(SpaceEntity entity, String spaceName, String spaceKey, String url, Date creationDate, Date lastModificationDate, long spaceId) {
        entity.setName(spaceName);
        entity.setKey(spaceKey);
        entity.addLink(Link.self((URI)RequestContextThreadLocal.get().getUriBuilder("space").build(new Object[]{spaceKey})));
        entity.setCreatedDate(this.dateEntityFactory.buildDateEntity(creationDate));
        entity.setLastModifiedDate(this.dateEntityFactory.buildDateEntity(lastModificationDate));
        entity.setWikiLink("[" + spaceKey + ":]");
        if (spaceId > 0L) {
            entity.setId(String.valueOf(spaceId));
        }
        try {
            entity.addLink(Link.link((URI)new URI(this.settingsManager.getGlobalSettings().getBaseUrl() + url), (String)"alternate", (String)"text/html"));
        }
        catch (URISyntaxException uRISyntaxException) {
            // empty catch block
        }
    }

    public SpaceEntity build(Space space) {
        SpaceEntity entity = new SpaceEntity();
        String spaceName = space.getName();
        String spaceKey = space.getKey();
        Date creationDate = space.getCreationDate();
        Date lastModificationDate = space.getLastModificationDate();
        String url = space.getUrlPath();
        this.setSpaceProperties(entity, spaceName, spaceKey, url, creationDate, lastModificationDate, space.getId());
        return entity;
    }
}


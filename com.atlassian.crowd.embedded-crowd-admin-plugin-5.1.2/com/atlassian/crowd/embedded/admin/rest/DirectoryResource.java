/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.embedded.api.CrowdDirectoryService
 *  com.atlassian.crowd.embedded.api.Directory
 *  com.atlassian.crowd.embedded.api.DirectorySynchronisationInformation
 *  com.atlassian.crowd.embedded.api.DirectorySynchronisationRoundInformation
 *  com.atlassian.plugins.rest.common.Link
 *  com.atlassian.plugins.rest.common.security.jersey.SysadminOnlyResourceFilter
 *  com.atlassian.sal.api.message.I18nResolver
 *  com.atlassian.sal.api.message.LocaleResolver
 *  com.atlassian.sal.api.message.Message
 *  com.atlassian.sal.api.timezone.TimeZoneManager
 *  com.sun.jersey.spi.container.ResourceFilters
 *  javax.ws.rs.GET
 *  javax.ws.rs.Path
 *  javax.ws.rs.PathParam
 *  javax.ws.rs.Produces
 *  javax.ws.rs.core.Context
 *  javax.ws.rs.core.Response
 *  javax.ws.rs.core.Response$Status
 *  javax.ws.rs.core.UriBuilder
 *  javax.ws.rs.core.UriInfo
 */
package com.atlassian.crowd.embedded.admin.rest;

import com.atlassian.crowd.embedded.admin.rest.entities.DirectoryEntity;
import com.atlassian.crowd.embedded.admin.rest.entities.DirectoryList;
import com.atlassian.crowd.embedded.admin.rest.entities.DirectorySynchronisationInformationEntity;
import com.atlassian.crowd.embedded.admin.util.SimpleMessage;
import com.atlassian.crowd.embedded.api.CrowdDirectoryService;
import com.atlassian.crowd.embedded.api.Directory;
import com.atlassian.crowd.embedded.api.DirectorySynchronisationInformation;
import com.atlassian.crowd.embedded.api.DirectorySynchronisationRoundInformation;
import com.atlassian.plugins.rest.common.Link;
import com.atlassian.plugins.rest.common.security.jersey.SysadminOnlyResourceFilter;
import com.atlassian.sal.api.message.I18nResolver;
import com.atlassian.sal.api.message.LocaleResolver;
import com.atlassian.sal.api.message.Message;
import com.atlassian.sal.api.timezone.TimeZoneManager;
import com.sun.jersey.spi.container.ResourceFilters;
import java.io.Serializable;
import java.net.URI;
import java.text.DateFormat;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;

@Path(value="/directory")
@Produces(value={"application/xml", "application/json"})
@ResourceFilters(value={SysadminOnlyResourceFilter.class})
public class DirectoryResource {
    @Context
    private UriInfo uriInfo;
    private final CrowdDirectoryService crowdDirectoryService;
    private final I18nResolver i18nResolver;
    private final LocaleResolver localeResolver;
    private final TimeZoneManager timeZoneManager;

    public DirectoryResource(CrowdDirectoryService crowdDirectoryService, I18nResolver i18nResolver, LocaleResolver localeResolver, TimeZoneManager timeZoneManager) {
        this.crowdDirectoryService = crowdDirectoryService;
        this.i18nResolver = i18nResolver;
        this.localeResolver = localeResolver;
        this.timeZoneManager = timeZoneManager;
    }

    @GET
    public Response get() {
        List directories = this.crowdDirectoryService.findAllDirectories();
        Locale locale = this.localeResolver.getLocale();
        TimeZone timeZone = this.timeZoneManager.getUserTimeZone();
        DirectoryList list = new DirectoryList();
        for (Directory directory : directories) {
            list.getDirectories().add(this.buildDirectoryEntity(directory, locale, timeZone));
        }
        return Response.ok((Object)list).build();
    }

    @GET
    @Path(value="/{id}")
    public Response getDirectory(@PathParam(value="id") Long id) {
        Directory directory = this.crowdDirectoryService.findDirectoryById(id.longValue());
        if (directory != null) {
            return Response.ok((Object)this.buildDirectoryEntity(directory, this.localeResolver.getLocale(), this.timeZoneManager.getUserTimeZone())).build();
        }
        return Response.status((Response.Status)Response.Status.NOT_FOUND).build();
    }

    private DirectoryEntity buildDirectoryEntity(Directory directory, Locale locale, TimeZone timeZone) {
        DirectoryEntity entity = new DirectoryEntity();
        entity.setName(directory.getName());
        entity.getLinks().add(Link.self((URI)this.getDirectoryUriBuilder().build(new Object[]{directory.getId()})));
        DirectorySynchronisationInformation syncInformation = this.crowdDirectoryService.getDirectorySynchronisationInformation(directory.getId().longValue());
        if (syncInformation != null) {
            DirectorySynchronisationInformationEntity syncEntity = new DirectorySynchronisationInformationEntity();
            if (syncInformation.getLastRound() != null) {
                syncEntity.setLastSyncDurationInSeconds(syncInformation.getLastRound().getDurationMs() / 1000L);
                DateFormat dateFormat = DateFormat.getDateTimeInstance(3, 3, locale);
                dateFormat.setTimeZone(timeZone);
                syncEntity.setLastSyncStartTime(dateFormat.format(syncInformation.getLastRound().getStartTime()));
            }
            if (syncInformation.getActiveRound() != null) {
                syncEntity.setCurrentSyncStartTime(syncInformation.getActiveRound().getStartTime());
                syncEntity.setCurrentDurationInSeconds((System.currentTimeMillis() - syncInformation.getActiveRound().getStartTime()) / 1000L);
            } else {
                syncEntity.setCurrentDurationInSeconds(0L);
            }
            Message syncStatusMessage = this.getSyncStatusMessage(syncInformation);
            if (syncStatusMessage != null) {
                syncEntity.setSyncStatus(this.i18nResolver.getText(syncStatusMessage));
            }
            entity.setSync(syncEntity);
        }
        return entity;
    }

    protected UriBuilder getDirectoryUriBuilder() {
        return this.uriInfo.getBaseUriBuilder().path("directory").path("{id}");
    }

    private Message getSyncStatusMessage(DirectorySynchronisationInformation syncInfo) {
        DirectorySynchronisationRoundInformation syncRound;
        DirectorySynchronisationRoundInformation directorySynchronisationRoundInformation = syncRound = syncInfo.isSynchronising() ? syncInfo.getActiveRound() : syncInfo.getLastRound();
        if (syncRound == null) {
            return null;
        }
        String statusKey = syncRound.getStatusKey();
        if (statusKey == null) {
            return null;
        }
        Serializable[] params = syncRound.getStatusParameters().toArray(new Serializable[0]);
        return SimpleMessage.instance("embedded.crowd." + statusKey, params);
    }
}


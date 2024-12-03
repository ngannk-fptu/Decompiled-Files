/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.gadgets.dashboard.PermissionException
 *  com.atlassian.gadgets.directory.spi.SubscribedGadgetFeed
 *  com.atlassian.gadgets.directory.spi.SubscribedGadgetFeedStore
 *  com.atlassian.gadgets.feed.GadgetFeedReaderFactory
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.atlassian.sal.api.message.I18nResolver
 *  com.google.common.base.Preconditions
 *  javax.servlet.http.HttpServletRequest
 *  javax.ws.rs.DELETE
 *  javax.ws.rs.GET
 *  javax.ws.rs.Path
 *  javax.ws.rs.PathParam
 *  javax.ws.rs.Produces
 *  javax.ws.rs.WebApplicationException
 *  javax.ws.rs.core.Context
 *  javax.ws.rs.core.Response
 *  javax.ws.rs.core.Response$Status
 */
package com.atlassian.gadgets.directory.internal.rest;

import com.atlassian.gadgets.dashboard.PermissionException;
import com.atlassian.gadgets.directory.internal.DirectoryConfigurationPermissionChecker;
import com.atlassian.gadgets.directory.internal.DirectoryUrlBuilder;
import com.atlassian.gadgets.directory.internal.jaxb.SubscribedGadgetFeedRepresentation;
import com.atlassian.gadgets.directory.spi.SubscribedGadgetFeed;
import com.atlassian.gadgets.directory.spi.SubscribedGadgetFeedStore;
import com.atlassian.gadgets.feed.GadgetFeedReaderFactory;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.sal.api.message.I18nResolver;
import com.google.common.base.Preconditions;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;

@Path(value="/directory/subscribed-gadget-feeds/{id}")
public class SubscribedGadgetFeedResource {
    private final SubscribedGadgetFeedStore store;
    private final I18nResolver i18n;
    private final DirectoryConfigurationPermissionChecker gadgetUrlChecker;
    private final DirectoryUrlBuilder urlBuilder;
    private final GadgetFeedReaderFactory readerFactory;

    public SubscribedGadgetFeedResource(@ComponentImport SubscribedGadgetFeedStore store, @ComponentImport I18nResolver i18n, DirectoryConfigurationPermissionChecker gadgetUrlChecker, DirectoryUrlBuilder urlBuilder, GadgetFeedReaderFactory readerFactory) {
        this.readerFactory = readerFactory;
        this.store = (SubscribedGadgetFeedStore)Preconditions.checkNotNull((Object)store, (Object)"feedsProvider");
        this.i18n = (I18nResolver)Preconditions.checkNotNull((Object)i18n, (Object)"i18n");
        this.gadgetUrlChecker = (DirectoryConfigurationPermissionChecker)Preconditions.checkNotNull((Object)gadgetUrlChecker, (Object)"gadgetUrlChecker");
        this.urlBuilder = (DirectoryUrlBuilder)Preconditions.checkNotNull((Object)urlBuilder, (Object)"urlBuilder");
    }

    @GET
    @Produces(value={"application/json", "application/xml"})
    public SubscribedGadgetFeedRepresentation get(@PathParam(value="id") String feedId) {
        this.checkFeedId(feedId);
        SubscribedGadgetFeed feed = this.store.getFeed(feedId);
        return new SubscribedGadgetFeedRepresentation(feed, this.urlBuilder, this.readerFactory);
    }

    @DELETE
    public Response remove(@PathParam(value="id") String feedId, @Context HttpServletRequest request) {
        this.checkForPermission(request);
        this.checkFeedId(feedId);
        this.store.removeFeed(feedId);
        return Response.ok().build();
    }

    private void checkFeedId(String feedId) {
        if (!this.store.containsFeed(feedId)) {
            throw new WebApplicationException(Response.Status.NOT_FOUND);
        }
    }

    private void checkForPermission(HttpServletRequest request) {
        try {
            this.gadgetUrlChecker.checkForPermissionToConfigureDirectory(request);
        }
        catch (PermissionException e) {
            throw new WebApplicationException(Response.status((Response.Status)Response.Status.UNAUTHORIZED).entity((Object)this.i18n.getText("directoryResource.no.write.permission")).type("text/plain").build());
        }
    }
}


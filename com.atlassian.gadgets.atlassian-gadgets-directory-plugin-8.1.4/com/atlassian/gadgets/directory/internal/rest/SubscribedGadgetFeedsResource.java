/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.gadgets.dashboard.PermissionException
 *  com.atlassian.gadgets.directory.spi.SubscribedGadgetFeed
 *  com.atlassian.gadgets.directory.spi.SubscribedGadgetFeedStore
 *  com.atlassian.gadgets.feed.GadgetFeedParsingException
 *  com.atlassian.gadgets.feed.GadgetFeedReaderFactory
 *  com.atlassian.gadgets.feed.NonAtomGadgetSpecFeedException
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.atlassian.plugins.rest.common.security.UnlicensedSiteAccess
 *  com.atlassian.sal.api.message.I18nResolver
 *  com.google.common.base.Preconditions
 *  javax.servlet.http.HttpServletRequest
 *  javax.ws.rs.Consumes
 *  javax.ws.rs.GET
 *  javax.ws.rs.POST
 *  javax.ws.rs.Path
 *  javax.ws.rs.Produces
 *  javax.ws.rs.WebApplicationException
 *  javax.ws.rs.core.Context
 *  javax.ws.rs.core.Response
 *  javax.ws.rs.core.Response$Status
 *  org.apache.commons.io.IOUtils
 *  org.apache.commons.lang3.StringUtils
 *  org.json.JSONException
 *  org.json.JSONObject
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.gadgets.directory.internal.rest;

import com.atlassian.gadgets.dashboard.PermissionException;
import com.atlassian.gadgets.directory.internal.DirectoryConfigurationPermissionChecker;
import com.atlassian.gadgets.directory.internal.DirectoryUrlBuilder;
import com.atlassian.gadgets.directory.internal.jaxb.SubscribedGadgetFeedRepresentation;
import com.atlassian.gadgets.directory.internal.jaxb.SubscribedGadgetFeedsRepresentation;
import com.atlassian.gadgets.directory.spi.SubscribedGadgetFeed;
import com.atlassian.gadgets.directory.spi.SubscribedGadgetFeedStore;
import com.atlassian.gadgets.feed.GadgetFeedParsingException;
import com.atlassian.gadgets.feed.GadgetFeedReaderFactory;
import com.atlassian.gadgets.feed.NonAtomGadgetSpecFeedException;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.plugins.rest.common.security.UnlicensedSiteAccess;
import com.atlassian.sal.api.message.I18nResolver;
import com.google.common.base.Preconditions;
import java.io.IOException;
import java.io.Reader;
import java.io.Serializable;
import java.net.URI;
import java.net.URISyntaxException;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Path(value="/directory/subscribed-gadget-feeds")
@UnlicensedSiteAccess
public class SubscribedGadgetFeedsResource {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final SubscribedGadgetFeedStore store;
    private final DirectoryConfigurationPermissionChecker gadgetUrlChecker;
    private final I18nResolver i18n;
    private final DirectoryUrlBuilder urlBuilder;
    private GadgetFeedReaderFactory readerFactory;

    public SubscribedGadgetFeedsResource(@ComponentImport SubscribedGadgetFeedStore store, DirectoryConfigurationPermissionChecker gadgetUrlChecker, @ComponentImport I18nResolver i18n, DirectoryUrlBuilder urlBuilder, GadgetFeedReaderFactory readerFactory) {
        this.readerFactory = readerFactory;
        this.store = (SubscribedGadgetFeedStore)Preconditions.checkNotNull((Object)store, (Object)"store");
        this.gadgetUrlChecker = (DirectoryConfigurationPermissionChecker)Preconditions.checkNotNull((Object)gadgetUrlChecker, (Object)"gadgetUrlChecker");
        this.i18n = (I18nResolver)Preconditions.checkNotNull((Object)i18n, (Object)"i18n");
        this.urlBuilder = (DirectoryUrlBuilder)Preconditions.checkNotNull((Object)urlBuilder, (Object)"urlBuilder");
    }

    @GET
    @Produces(value={"application/xml", "application/json"})
    public Response get() {
        return Response.ok((Object)new SubscribedGadgetFeedsRepresentation(this.store.getAllFeeds(), this.urlBuilder, this.readerFactory)).build();
    }

    @POST
    @Consumes(value={"application/json"})
    @Produces(value={"application/xml", "application/json"})
    public Response add(@Context HttpServletRequest request, Reader jsonContent) {
        URI feedUri = this.getFeedUri(jsonContent);
        if (!feedUri.isAbsolute()) {
            this.logger.error("Subscribed Gadget Feeds Resource: POST rejected due to invalid 'url' parameter: url must be absolute");
            return Response.status((Response.Status)Response.Status.BAD_REQUEST).entity((Object)this.i18n.getText("subscribed.gadget.feeds.invalid.url.parameter")).type("text/plain").build();
        }
        try {
            this.logger.debug("Subscribed Gadget Feeds Resource: POST received: url=" + feedUri);
            this.gadgetUrlChecker.checkForPermissionToConfigureDirectory(request);
            this.readerFactory.getFeedReader(feedUri);
            SubscribedGadgetFeed feed = this.store.addFeed(feedUri);
            this.logger.debug("Subscribed Gadget Feeds Resource: POST complete");
            return Response.created((URI)URI.create(this.urlBuilder.buildSubscribedGadgetFeedUrl(feed.getId()))).entity((Object)new SubscribedGadgetFeedRepresentation(feed, this.urlBuilder, this.readerFactory)).build();
        }
        catch (NonAtomGadgetSpecFeedException e) {
            this.logger.debug("Subscribed Gadget Feeds Resource: POST rejected: " + feedUri + " is not an Atom feed", (Throwable)e);
            return Response.status((Response.Status)Response.Status.BAD_REQUEST).entity((Object)this.i18n.getText("subscribed.gadget.feeds.non.atom.gadget.feed", new Serializable[]{feedUri})).type("text/plain").build();
        }
        catch (GadgetFeedParsingException e) {
            this.logger.debug("Subscribed Gadget Feeds Resource: POST rejected: " + feedUri + " could not be parsed");
            return Response.status((Response.Status)Response.Status.BAD_REQUEST).entity((Object)this.i18n.getText("subscribed.gadget.feeds.non.parsable.gadget.feed", new Serializable[]{feedUri})).type("text/plain").build();
        }
        catch (PermissionException e) {
            this.logger.warn("Subscribed Gadget Feeds Resource: POST rejected: current user not allowed to write to directory", (Throwable)e);
            return Response.status((Response.Status)Response.Status.UNAUTHORIZED).entity((Object)this.i18n.getText("directoryResource.no.write.permission")).type("text/plain").build();
        }
    }

    private URI getFeedUri(Reader jsonContent) {
        String feedUrl = this.getFeedUrl(jsonContent);
        if (StringUtils.isEmpty((CharSequence)feedUrl)) {
            this.logger.error("Subscribed Gadget Feeds Resource: POST rejected due to missing 'url' parameter");
            throw new WebApplicationException(Response.status((Response.Status)Response.Status.BAD_REQUEST).entity((Object)this.i18n.getText("subscribed.gadget.feeds.missing.url.parameter")).type("text/plain").build());
        }
        try {
            return new URI(feedUrl).normalize();
        }
        catch (URISyntaxException e) {
            this.logger.debug("Subscribed Gadget Feeds Resource: POST rejected due to invalid 'url' parameter " + feedUrl, (Throwable)e);
            throw new WebApplicationException(Response.status((Response.Status)Response.Status.BAD_REQUEST).entity((Object)this.i18n.getText("subscribed.gadget.feeds.invalid.url.parameter", new Serializable[]{feedUrl})).type("text/plain").build());
        }
    }

    private String getFeedUrl(Reader jsonContent) {
        try {
            return this.parseJsonObject(jsonContent).getString("url").trim();
        }
        catch (JSONException e) {
            this.logger.debug("Subscribed Gadget Feeds Resource: POST rejected due to missing 'url' parameter");
            throw new WebApplicationException(Response.status((Response.Status)Response.Status.BAD_REQUEST).entity((Object)this.i18n.getText("subscribed.gadget.feeds.missing.url.parameter")).type("text/plain").build());
        }
    }

    private JSONObject parseJsonObject(Reader jsonContent) {
        try {
            return new JSONObject(IOUtils.toString((Reader)jsonContent));
        }
        catch (JSONException e) {
            this.logger.debug("Subscribed Gadget Feeds Resource: POST rejected due invalid JSON data");
            throw new WebApplicationException(Response.status((Response.Status)Response.Status.BAD_REQUEST).entity((Object)this.i18n.getText("subscribed.gadget.feeds.invalid.json")).type("text/plain").build());
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}


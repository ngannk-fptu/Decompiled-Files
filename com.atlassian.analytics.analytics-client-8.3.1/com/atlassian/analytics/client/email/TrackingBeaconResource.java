/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.event.api.EventPublisher
 *  com.atlassian.plugins.rest.common.security.AnonymousAllowed
 *  javax.ws.rs.GET
 *  javax.ws.rs.Path
 *  javax.ws.rs.Produces
 *  javax.ws.rs.QueryParam
 *  javax.ws.rs.core.CacheControl
 *  javax.ws.rs.core.Response
 *  javax.ws.rs.core.Response$ResponseBuilder
 *  org.apache.commons.lang3.StringUtils
 */
package com.atlassian.analytics.client.email;

import com.atlassian.analytics.client.configuration.AnalyticsConfig;
import com.atlassian.analytics.client.email.TrackingBeaconEvent;
import com.atlassian.analytics.client.properties.AnalyticsPropertyService;
import com.atlassian.event.api.EventPublisher;
import com.atlassian.plugins.rest.common.security.AnonymousAllowed;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.CacheControl;
import javax.ws.rs.core.Response;
import org.apache.commons.lang3.StringUtils;

@Path(value="/email/trackOpen")
@Produces(value={"image/gif"})
public class TrackingBeaconResource {
    private static final Response CACHED_TRACKING_RESPONSE = TrackingBeaconResource.buildTrackingResponse();
    private static final byte[] TRACKING_GIF = new byte[]{71, 73, 70, 56, 57, 97, 1, 0, 1, 0, -128, 0, 0, -1, -1, -1, 0, 0, 0, 44, 0, 0, 0, 0, 1, 0, 1, 0, 0, 2, 2, 68, 1, 0, 59};
    private final EventPublisher eventPublisher;
    private final AnalyticsConfig analyticsConfig;
    private final String lowercaseApplicationDisplayName;

    public TrackingBeaconResource(EventPublisher eventPublisher, AnalyticsPropertyService analyticsPropertyService, AnalyticsConfig analyticsConfig) {
        this.eventPublisher = eventPublisher;
        this.analyticsConfig = analyticsConfig;
        this.lowercaseApplicationDisplayName = analyticsPropertyService.getDisplayName().toLowerCase();
    }

    @GET
    @AnonymousAllowed
    public Response trackOpen(@QueryParam(value="key") String eventName, @QueryParam(value="product") String product) {
        String productIdentifier;
        if (StringUtils.isEmpty((CharSequence)eventName)) {
            return Response.status((int)400).build();
        }
        String string = productIdentifier = StringUtils.isBlank((CharSequence)product) ? this.lowercaseApplicationDisplayName : product;
        if (this.analyticsConfig.canCollectAnalytics()) {
            this.eventPublisher.publish((Object)new TrackingBeaconEvent(eventName, productIdentifier));
        }
        return CACHED_TRACKING_RESPONSE;
    }

    private static Response buildTrackingResponse() {
        Response.ResponseBuilder responseBuilder = Response.ok((Object)TRACKING_GIF);
        CacheControl cacheControl = new CacheControl();
        cacheControl.setNoCache(true);
        responseBuilder.header("Pragma", (Object)"no-cache");
        responseBuilder.cacheControl(cacheControl);
        return responseBuilder.build();
    }
}


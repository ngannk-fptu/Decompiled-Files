/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugins.rest.common.security.AnonymousAllowed
 *  com.atlassian.sal.api.user.UserManager
 *  com.google.common.annotations.VisibleForTesting
 *  javax.ws.rs.Consumes
 *  javax.ws.rs.DELETE
 *  javax.ws.rs.GET
 *  javax.ws.rs.PUT
 *  javax.ws.rs.Path
 *  javax.ws.rs.Produces
 *  javax.ws.rs.QueryParam
 *  javax.ws.rs.core.Response
 *  javax.ws.rs.core.Response$Status
 *  org.codehaus.jackson.annotate.JsonIgnoreProperties
 *  org.codehaus.jackson.annotate.JsonProperty
 */
package com.atlassian.analytics.client.report;

import com.atlassian.analytics.client.eventfilter.whitelist.Whitelist;
import com.atlassian.analytics.client.eventfilter.whitelist.WhitelistFilter;
import com.atlassian.analytics.client.eventfilter.whitelist.WhitelistSearcher;
import com.atlassian.analytics.client.report.EventReportItem;
import com.atlassian.analytics.client.report.EventReportPermissionManager;
import com.atlassian.analytics.client.report.EventReporter;
import com.atlassian.plugins.rest.common.security.AnonymousAllowed;
import com.atlassian.sal.api.user.UserManager;
import com.google.common.annotations.VisibleForTesting;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;

@Path(value="/report")
@AnonymousAllowed
@Consumes(value={"application/json"})
@Produces(value={"application/json"})
public class ReportResource {
    @VisibleForTesting
    static final int DEFAULT_WHITELIST_SEARCH_MAX_RESULTS = 50;
    private final DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private final EventReporter eventReporter;
    private final UserManager userManager;
    private final EventReportPermissionManager eventReportPermissionManager;
    private final WhitelistFilter whitelistFilter;
    private final WhitelistSearcher whitelistSearcher;

    public ReportResource(EventReporter eventReporter, UserManager userManager, EventReportPermissionManager eventReportPermissionManager, WhitelistFilter whitelistFilter, WhitelistSearcher whitelistSearcher) {
        this.eventReporter = eventReporter;
        this.userManager = userManager;
        this.eventReportPermissionManager = eventReportPermissionManager;
        this.whitelistFilter = whitelistFilter;
        this.whitelistSearcher = whitelistSearcher;
    }

    @GET
    public Response getReport(@QueryParam(value="mode") String modeId) {
        if (!this.hasPermission()) {
            return Response.status((Response.Status)Response.Status.FORBIDDEN).build();
        }
        Mode mode = Mode.fromString(modeId);
        if (mode == null) {
            return Response.status((Response.Status)Response.Status.BAD_REQUEST).build();
        }
        List<EventBean> beans = this.toEventBeans(mode.getEvents(this.eventReporter));
        ReportBean report = new ReportBean(this.eventReporter.isCapturing(), beans);
        return Response.ok((Object)report).build();
    }

    @Path(value="/whitelist")
    @GET
    public Response getActiveWhitelists() {
        if (!this.hasPermission()) {
            return Response.status((Response.Status)Response.Status.FORBIDDEN).build();
        }
        List<Whitelist.WhitelistBean> beans = this.whitelistFilter.toWhitelistBeans();
        return Response.ok(beans).build();
    }

    @Path(value="/whitelist/search")
    @GET
    public Response searchWhitelists(@QueryParam(value="query") String query, @QueryParam(value="whitelistId") String whitelistId, @QueryParam(value="maxResults") Integer maxResults) {
        if (!this.hasPermission()) {
            return Response.status((Response.Status)Response.Status.FORBIDDEN).build();
        }
        if (maxResults == null) {
            maxResults = 50;
        }
        List<WhitelistSearcher.SearchResultItem> result = this.whitelistSearcher.search(query, whitelistId, maxResults);
        return Response.ok(result).build();
    }

    @PUT
    public Response startOrStopCapturing(ConfigBean config) {
        if (!this.hasPermission()) {
            return Response.status((Response.Status)Response.Status.FORBIDDEN).build();
        }
        boolean capturing = Boolean.TRUE.equals(config.capturing);
        this.eventReporter.setCapturing(capturing);
        return Response.ok().build();
    }

    @DELETE
    public Response clearEvents() {
        if (!this.hasPermission()) {
            return Response.status((Response.Status)Response.Status.FORBIDDEN).build();
        }
        this.eventReporter.clear();
        return Response.ok().build();
    }

    private boolean hasPermission() {
        return this.eventReportPermissionManager.hasPermission(this.userManager.getRemoteUserKey());
    }

    private List<EventBean> toEventBeans(Collection<EventReportItem> events) {
        return events.stream().filter(Objects::nonNull).map(this::toEventBean).collect(Collectors.toList());
    }

    private EventBean toEventBean(EventReportItem event) {
        return new EventBean(event.hashCode(), event.getName(), this.dateFormat.format(event.getTime()), event.getUser(), event.getRequestCorrelationId(), event.getProperties(), event.isRemoved());
    }

    @JsonIgnoreProperties(ignoreUnknown=true)
    public static class ConfigBean {
        @JsonProperty
        Boolean capturing;

        public ConfigBean() {
        }

        public ConfigBean(Boolean capturing) {
            this.capturing = capturing;
        }
    }

    @JsonIgnoreProperties(ignoreUnknown=true)
    public static class EventBean {
        @JsonProperty
        int id;
        @JsonProperty
        String name;
        @JsonProperty
        String time;
        @JsonProperty
        String user;
        @JsonProperty
        String requestCorrelationId;
        @JsonProperty
        Map<String, Object> properties;
        @JsonProperty
        boolean removed;

        public EventBean() {
        }

        public EventBean(int id, String name, String time, String user, String requestCorrelationId, Map<String, Object> properties, boolean removed) {
            this.id = id;
            this.name = name;
            this.time = time;
            this.user = user;
            this.requestCorrelationId = requestCorrelationId;
            this.properties = properties;
            this.removed = removed;
        }
    }

    @JsonIgnoreProperties(ignoreUnknown=true)
    public static class ReportBean {
        @JsonProperty
        Boolean capturing;
        @JsonProperty
        List<EventBean> events;

        public ReportBean(Boolean capturing, List<EventBean> events) {
            this.capturing = capturing;
            this.events = events;
        }
    }

    private static enum Mode {
        UNPROCESSED("unprocessed"){

            @Override
            Collection<EventReportItem> getEvents(EventReporter eventReporter) {
                return eventReporter.getRawEvents();
            }
        }
        ,
        BTF_PROCESSED("btf_processed"){

            @Override
            Collection<EventReportItem> getEvents(EventReporter eventReporter) {
                return eventReporter.getBtfProcessedEvents();
            }
        };

        private final String id;

        abstract Collection<EventReportItem> getEvents(EventReporter var1);

        private Mode(String id) {
            this.id = id;
        }

        private static Mode fromString(String modeId) {
            for (Mode mode : Mode.values()) {
                if (!mode.id.equals(modeId)) continue;
                return mode;
            }
            return null;
        }
    }
}


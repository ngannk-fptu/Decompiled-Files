/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.sal.api.websudo.WebSudoNotRequired
 *  javax.ws.rs.Consumes
 *  javax.ws.rs.POST
 *  javax.ws.rs.Path
 *  javax.ws.rs.Produces
 *  javax.ws.rs.core.Response
 *  javax.ws.rs.core.Response$Status
 *  org.codehaus.jackson.annotate.JsonCreator
 *  org.codehaus.jackson.annotate.JsonProperty
 */
package com.atlassian.upm.analytics.rest.resources;

import com.atlassian.sal.api.websudo.WebSudoNotRequired;
import com.atlassian.upm.analytics.event.UpmUiAnalyticsEvent;
import com.atlassian.upm.core.analytics.AnalyticsLogger;
import com.atlassian.upm.core.analytics.event.DefaultAnalyticsEvent;
import com.atlassian.upm.core.permission.Permission;
import com.atlassian.upm.core.rest.resources.permission.PermissionEnforcer;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;
import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonProperty;

@Path(value="/analytics")
@WebSudoNotRequired
public class AnalyticsResource {
    private static final Set<String> MARKETPLACE_EVENT_TYPES = Collections.unmodifiableSet(new HashSet<String>(Arrays.asList("page-source", "notification-badge-click", "connect-install-warning-accept", "connect-install-warning-cancel", "postinstall", "postupdate", "buy", "try", "upgrade", "renew", "retrieve-license")));
    private final AnalyticsLogger analytics;
    private final PermissionEnforcer permissionEnforcer;

    public AnalyticsResource(AnalyticsLogger analytics, PermissionEnforcer permissionEnforcer) {
        this.analytics = Objects.requireNonNull(analytics, "analytics");
        this.permissionEnforcer = Objects.requireNonNull(permissionEnforcer, "permissionEnforcer");
    }

    @POST
    @Consumes(value={"application/vnd.atl.plugins+json"})
    @Produces(value={"application/vnd.atl.plugins+json"})
    public Response addEvent(AnalyticsDataRepresentation data) {
        this.permissionEnforcer.enforcePermission(Permission.ADD_ANALYTICS_ACTIVITY);
        DefaultAnalyticsEvent event = MARKETPLACE_EVENT_TYPES.contains(data.getType()) ? new UpmUiAnalyticsEvent(data.getType(), data.getData()) : new DefaultAnalyticsEvent(data.getType(), data.getData());
        this.analytics.log(event);
        return Response.status((Response.Status)Response.Status.ACCEPTED).build();
    }

    public static final class AnalyticsDataRepresentation {
        @JsonProperty
        private Map<String, String> data;
        private String type;

        @JsonCreator
        public AnalyticsDataRepresentation(@JsonProperty(value="data") Map<String, String> data) {
            this.type = data.get("type");
            this.data = Collections.unmodifiableMap(data.entrySet().stream().filter(e -> !((String)e.getKey()).equals("type")).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue)));
        }

        public String getType() {
            return this.type;
        }

        public Map<String, String> getData() {
            return this.data;
        }
    }
}


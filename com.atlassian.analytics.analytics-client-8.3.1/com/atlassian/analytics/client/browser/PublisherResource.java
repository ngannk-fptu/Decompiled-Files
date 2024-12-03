/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.event.api.EventPublisher
 *  com.atlassian.plugins.rest.common.security.AnonymousSiteAccess
 *  javax.ws.rs.Consumes
 *  javax.ws.rs.POST
 *  javax.ws.rs.Path
 *  javax.ws.rs.Produces
 *  javax.ws.rs.core.Response
 *  org.codehaus.jackson.annotate.JsonIgnoreProperties
 *  org.codehaus.jackson.annotate.JsonProperty
 */
package com.atlassian.analytics.client.browser;

import com.atlassian.analytics.client.TimeKeeper;
import com.atlassian.analytics.client.api.browser.BrowserEvent;
import com.atlassian.event.api.EventPublisher;
import com.atlassian.plugins.rest.common.security.AnonymousSiteAccess;
import java.util.List;
import java.util.Map;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;

@Path(value="/publish")
@AnonymousSiteAccess
public class PublisherResource {
    private final EventPublisher eventPublisher;
    private final TimeKeeper timeKeeper;

    public PublisherResource(EventPublisher eventPublisher, TimeKeeper timeKeeper) {
        this.eventPublisher = eventPublisher;
        this.timeKeeper = timeKeeper;
    }

    @POST
    @Consumes(value={"application/json"})
    @Produces(value={"application/json"})
    public Response publishSingleEvent(BrowserEventBean bean) {
        BrowserEvent event = this.toBrowserEvent(bean);
        this.eventPublisher.publish((Object)event);
        return Response.ok().build();
    }

    @Path(value="/bulk")
    @POST
    @Consumes(value={"application/json"})
    @Produces(value={"application/json"})
    public Response publishBulkEvents(List<BrowserEventBean> beans) {
        for (BrowserEventBean bean : beans) {
            BrowserEvent event = this.toBrowserEvent(bean);
            this.eventPublisher.publish((Object)event);
        }
        return Response.ok().build();
    }

    private BrowserEvent toBrowserEvent(BrowserEventBean bean) {
        return new BrowserEvent(bean.name, bean.properties, this.timeKeeper.currentTimeMillis() + bean.timeDelta);
    }

    @JsonIgnoreProperties(ignoreUnknown=true)
    static class BrowserEventBean {
        @JsonProperty
        String name;
        @JsonProperty
        Map<String, Object> properties;
        @JsonProperty
        long timeDelta;

        BrowserEventBean() {
        }

        BrowserEventBean(String name, Map<String, Object> properties, long timeDelta) {
            this.name = name;
            this.properties = properties;
            this.timeDelta = timeDelta;
        }
    }
}


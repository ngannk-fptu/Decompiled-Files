/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugins.rest.common.security.jersey.SysadminOnlyResourceFilter
 *  com.sun.jersey.api.client.Statuses
 *  com.sun.jersey.spi.container.ResourceFilters
 *  javax.ws.rs.Consumes
 *  javax.ws.rs.DELETE
 *  javax.ws.rs.DefaultValue
 *  javax.ws.rs.GET
 *  javax.ws.rs.PUT
 *  javax.ws.rs.Path
 *  javax.ws.rs.Produces
 *  javax.ws.rs.QueryParam
 *  javax.ws.rs.core.Response
 *  javax.ws.rs.core.Response$Status
 *  javax.ws.rs.core.Response$StatusType
 *  org.apache.commons.lang3.StringUtils
 */
package com.atlassian.plugins.impl.rest;

import com.atlassian.plugins.impl.CdnStrategyProviderImpl;
import com.atlassian.plugins.impl.rest.CdnConfigurationEntity;
import com.atlassian.plugins.impl.rest.filter.DataCenterOnlyResourceFilter;
import com.atlassian.plugins.rest.common.security.jersey.SysadminOnlyResourceFilter;
import com.sun.jersey.api.client.Statuses;
import com.sun.jersey.spi.container.ResourceFilters;
import java.net.MalformedURLException;
import java.net.URL;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;
import org.apache.commons.lang3.StringUtils;

@Path(value="/configuration")
@Consumes(value={"application/json"})
@Produces(value={"application/json"})
@ResourceFilters(value={SysadminOnlyResourceFilter.class, DataCenterOnlyResourceFilter.class})
public class CdnConfigurationResource {
    private CdnStrategyProviderImpl cdnStrategyProviderImpl;

    public CdnConfigurationResource(CdnStrategyProviderImpl cdnStrategyProviderImpl) {
        this.cdnStrategyProviderImpl = cdnStrategyProviderImpl;
    }

    @DELETE
    public Response delete() {
        this.cdnStrategyProviderImpl.setConfiguration(new CdnConfigurationEntity(false, null));
        return Response.noContent().build();
    }

    @GET
    public Response get() {
        return Response.ok((Object)this.cdnStrategyProviderImpl.getConfiguration()).build();
    }

    @PUT
    public Response put(CdnConfigurationEntity entity) {
        CdnConfigurationEntity processedEntity = this.processEntity(entity);
        if (processedEntity == null) {
            return Response.status((Response.StatusType)Statuses.from((int)Response.Status.BAD_REQUEST.getStatusCode(), (String)"Invalid configuration provided. Address is null or a malformed URL.")).build();
        }
        this.cdnStrategyProviderImpl.setConfiguration(processedEntity);
        return this.get();
    }

    @GET
    @Path(value="/test")
    @Produces(value={"text/plain"})
    public Response test(@QueryParam(value="path") @DefaultValue(value="") String path) {
        String transformedPath = this.cdnStrategyProviderImpl.getCdnStrategy().map(strategy -> strategy.transformRelativeUrl(path)).orElse(path);
        return Response.ok((Object)transformedPath).build();
    }

    private CdnConfigurationEntity processEntity(CdnConfigurationEntity entity) {
        String url = StringUtils.removeEnd((String)entity.getUrl(), (String)"/");
        if (entity.isEnabled()) {
            if (StringUtils.isBlank((CharSequence)url)) {
                return null;
            }
            try {
                new URL(url);
            }
            catch (MalformedURLException e) {
                return null;
            }
        }
        return new CdnConfigurationEntity(entity.isEnabled(), url);
    }
}


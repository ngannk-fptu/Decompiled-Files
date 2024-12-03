/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.sal.api.websudo.WebSudoNotRequired
 *  javax.ws.rs.DefaultValue
 *  javax.ws.rs.GET
 *  javax.ws.rs.Path
 *  javax.ws.rs.QueryParam
 *  javax.ws.rs.core.Response
 *  org.apache.commons.lang3.StringUtils
 */
package com.atlassian.upm.rest.resources;

import com.atlassian.sal.api.websudo.WebSudoNotRequired;
import com.atlassian.upm.api.util.Option;
import com.atlassian.upm.rest.UpmMarketplaceFilter;
import com.atlassian.upm.rest.UpmUriBuilder;
import java.net.URI;
import java.util.Objects;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;
import org.apache.commons.lang3.StringUtils;

@Path(value="/available")
@WebSudoNotRequired
public class AvailablePluginCollectionRootResource {
    private final UpmUriBuilder uriBuilder;

    public AvailablePluginCollectionRootResource(UpmUriBuilder upmUriBuilder) {
        this.uriBuilder = Objects.requireNonNull(upmUriBuilder);
    }

    @GET
    public Response seeOther(@QueryParam(value="q") String q, @QueryParam(value="category") String category, @QueryParam(value="offset") @DefaultValue(value="0") int offset) {
        if (StringUtils.isNotBlank((CharSequence)category)) {
            return Response.seeOther((URI)this.uriBuilder.makeAbsolute(this.uriBuilder.buildAvailablePluginCollectionUriWithCategory(UpmMarketplaceFilter.RECENTLY_UPDATED, Option.some(category), offset))).build();
        }
        return Response.seeOther((URI)this.uriBuilder.makeAbsolute(this.uriBuilder.buildAvailablePluginCollectionUri(UpmMarketplaceFilter.RECENTLY_UPDATED, Option.option(StringUtils.trimToNull((String)q)), offset))).build();
    }
}


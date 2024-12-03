/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugins.rest.common.sal.websudo.WebSudoRequiredException
 *  javax.ws.rs.core.Response
 *  javax.ws.rs.core.Response$Status
 *  javax.ws.rs.ext.ExceptionMapper
 */
package com.atlassian.upm.core.rest;

import com.atlassian.plugins.rest.common.sal.websudo.WebSudoRequiredException;
import com.atlassian.upm.core.rest.representations.BaseRepresentationFactory;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;

public abstract class AbstractWebSudoRequiredExceptionMapper
implements ExceptionMapper<WebSudoRequiredException> {
    private final BaseRepresentationFactory representationFactory;

    public AbstractWebSudoRequiredExceptionMapper(BaseRepresentationFactory representationFactory) {
        this.representationFactory = representationFactory;
    }

    public Response toResponse(WebSudoRequiredException exception) {
        return Response.status((Response.Status)Response.Status.UNAUTHORIZED).entity((Object)this.representationFactory.createI18nErrorRepresentation(this.getI18nErrorKey())).type("application/vnd.atl.plugins.error+json").build();
    }

    protected abstract String getI18nErrorKey();
}


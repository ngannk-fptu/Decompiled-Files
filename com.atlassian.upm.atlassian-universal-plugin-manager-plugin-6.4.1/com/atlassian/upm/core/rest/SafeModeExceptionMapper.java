/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.sal.api.message.I18nResolver
 *  javax.ws.rs.core.Response
 *  javax.ws.rs.core.Response$Status
 *  javax.ws.rs.ext.ExceptionMapper
 *  javax.ws.rs.ext.Provider
 */
package com.atlassian.upm.core.rest;

import com.atlassian.sal.api.message.I18nResolver;
import com.atlassian.upm.core.SafeModeException;
import com.atlassian.upm.core.rest.representations.BaseRepresentationFactory;
import java.util.Objects;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class SafeModeExceptionMapper
implements ExceptionMapper<SafeModeException> {
    private final BaseRepresentationFactory representationFactory;
    private final I18nResolver i18nResolver;

    public SafeModeExceptionMapper(BaseRepresentationFactory representationFactory, I18nResolver i18nResolver) {
        this.representationFactory = Objects.requireNonNull(representationFactory, "representationFactory");
        this.i18nResolver = Objects.requireNonNull(i18nResolver, "i18nResolver");
    }

    public Response toResponse(SafeModeException exception) {
        return Response.status((Response.Status)Response.Status.CONFLICT).entity((Object)this.representationFactory.createErrorRepresentation(this.i18nResolver.getText("upm.pluginInstall.error.safe.mode"))).type("application/vnd.atl.plugins.task.error+json").build();
    }
}


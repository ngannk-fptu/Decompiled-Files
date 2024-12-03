/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugins.rest.common.security.AnonymousAllowed
 *  com.atlassian.sal.api.message.I18nResolver
 *  com.atlassian.streams.spi.StreamsLocaleProvider
 *  com.google.common.base.Preconditions
 *  com.google.common.collect.Iterables
 *  javax.ws.rs.GET
 *  javax.ws.rs.Path
 *  javax.ws.rs.PathParam
 *  javax.ws.rs.Produces
 *  javax.ws.rs.QueryParam
 *  javax.ws.rs.core.Response
 *  org.springframework.beans.factory.annotation.Qualifier
 */
package com.atlassian.streams.internal.rest.resources;

import com.atlassian.plugins.rest.common.security.AnonymousAllowed;
import com.atlassian.sal.api.message.I18nResolver;
import com.atlassian.streams.internal.rest.representations.I18nTranslations;
import com.atlassian.streams.spi.StreamsLocaleProvider;
import com.google.common.base.Preconditions;
import com.google.common.collect.Iterables;
import java.io.Serializable;
import java.util.List;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;
import org.springframework.beans.factory.annotation.Qualifier;

@Path(value="/i18n")
@AnonymousAllowed
public class I18nResource {
    private final I18nResolver i18nResolver;
    private final StreamsLocaleProvider localeProvider;

    public I18nResource(@Qualifier(value="streamsI18nResolver") I18nResolver i18nResolver, StreamsLocaleProvider localeProvider) {
        this.i18nResolver = (I18nResolver)Preconditions.checkNotNull((Object)i18nResolver, (Object)"i18nResolver");
        this.localeProvider = (StreamsLocaleProvider)Preconditions.checkNotNull((Object)localeProvider, (Object)"localeProvider");
    }

    @GET
    @Path(value="key/{key}")
    @Produces(value={"text/plain"})
    public Response getTranslation(@PathParam(value="key") String key, @QueryParam(value="parameters") List<String> parameters) {
        String text;
        if (parameters != null) {
            Serializable[] serializables = (Serializable[])Iterables.toArray(parameters, Serializable.class);
            text = this.i18nResolver.getText(key, serializables);
        } else {
            text = this.i18nResolver.getText(key);
        }
        return Response.ok((Object)text).type("text/plain").build();
    }

    @GET
    @Path(value="prefix/{prefix}")
    @Produces(value={"application/json"})
    public Response getTranslations(@PathParam(value="prefix") String prefix) {
        I18nTranslations translations = new I18nTranslations(this.i18nResolver.getAllTranslationsForPrefix(prefix, this.localeProvider.getUserLocale()));
        return Response.ok((Object)translations).type("application/json").build();
    }
}


/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.applinks.api.CredentialsRequiredException
 *  com.atlassian.cache.CacheException
 *  com.atlassian.confluence.languages.LocaleManager
 *  com.atlassian.confluence.rest.api.model.ExceptionConverter$Server
 *  com.atlassian.confluence.rest.api.model.RestError
 *  com.atlassian.confluence.search.v2.InvalidSearchException
 *  com.atlassian.confluence.user.AuthenticatedUserThreadLocal
 *  com.atlassian.confluence.util.i18n.I18NBean
 *  com.atlassian.confluence.util.i18n.I18NBeanFactory
 *  com.atlassian.sal.api.net.ResponseTransportException
 *  com.atlassian.user.User
 *  javax.ws.rs.core.MediaType
 *  javax.ws.rs.core.Response
 *  javax.ws.rs.core.Response$Status
 *  javax.ws.rs.ext.ExceptionMapper
 *  javax.ws.rs.ext.Provider
 *  org.json.JSONArray
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.extra.calendar3.rest.ExceptionMappers;

import com.atlassian.applinks.api.CredentialsRequiredException;
import com.atlassian.cache.CacheException;
import com.atlassian.confluence.extra.calendar3.exception.CalendarException;
import com.atlassian.confluence.extra.calendar3.exception.RuntimeCredentialsRequiredException;
import com.atlassian.confluence.extra.calendar3.rest.OAuthRequiredEntity;
import com.atlassian.confluence.extra.calendar3.util.JSONUtil;
import com.atlassian.confluence.languages.LocaleManager;
import com.atlassian.confluence.rest.api.model.ExceptionConverter;
import com.atlassian.confluence.rest.api.model.RestError;
import com.atlassian.confluence.search.v2.InvalidSearchException;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.confluence.util.i18n.I18NBean;
import com.atlassian.confluence.util.i18n.I18NBeanFactory;
import com.atlassian.sal.api.net.ResponseTransportException;
import com.atlassian.user.User;
import java.util.List;
import java.util.Locale;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.json.JSONArray;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Provider
public class GeneralExceptionMapper
implements ExceptionMapper<Exception> {
    protected static final Logger LOG = LoggerFactory.getLogger(GeneralExceptionMapper.class);
    private final I18NBeanFactory i18NBeanFactory;
    private final LocaleManager localeManager;

    public GeneralExceptionMapper(I18NBeanFactory i18NBeanFactory, LocaleManager localeManager) {
        this.i18NBeanFactory = i18NBeanFactory;
        this.localeManager = localeManager;
    }

    public Response toResponse(Exception e) {
        CacheException cacheException;
        Throwable innerException;
        if (e instanceof CredentialsRequiredException) {
            return this.getResponseErrorForCredentialsRequiredException((CredentialsRequiredException)((Object)e));
        }
        if (e instanceof CacheException && (innerException = (cacheException = (CacheException)e).getCause()) instanceof RuntimeCredentialsRequiredException) {
            RuntimeCredentialsRequiredException runtimeCredentialsRequiredException = (RuntimeCredentialsRequiredException)innerException;
            return this.getResponseErrorForCredentialsRequiredException(runtimeCredentialsRequiredException.getCredentialsRequiredException());
        }
        if (e.getClass().getName().equals("com.atlassian.confluence.api.service.exceptions.ReadOnlyException")) {
            return this.getResponseError(e);
        }
        if (e instanceof ResponseTransportException) {
            return this.getResponseError(e, "Transport exception return from Jira");
        }
        if (e instanceof CalendarException) {
            CalendarException calendarException = (CalendarException)e;
            return this.getResponseError(calendarException, "Calendar exception", this.getText(calendarException.getErrorMessageKey(), calendarException.getErrorMessageSubstitutions()));
        }
        if (e instanceof InvalidSearchException || e instanceof IllegalArgumentException) {
            return Response.noContent().build();
        }
        return this.getResponseError(e, "General exception happen on calendar resources");
    }

    private Response getResponseErrorForCredentialsRequiredException(CredentialsRequiredException credentialsRequiredException) {
        LOG.info("Unable to retrieve JIRA sub-calendar events. User is probably not authenticated. Log at DEBUG level for more details.");
        String oAuthUriString = credentialsRequiredException.getAuthorisationURI().toString();
        return Response.status((Response.Status)Response.Status.UNAUTHORIZED).header("WWW-Authenticate", (Object)String.format("OAuth realm=\"%s\"", oAuthUriString)).entity((Object)new OAuthRequiredEntity(oAuthUriString)).build();
    }

    private Response getResponseError(Exception exception) {
        RestError errorBean = ExceptionConverter.Server.convertServiceException((Exception)exception);
        LOG.error(exception.getMessage());
        return Response.status((int)errorBean.getStatusCode()).type(MediaType.APPLICATION_JSON_TYPE).entity((Object)errorBean).build();
    }

    private Response getResponseError(CalendarException e, String loggedMessage, String ... errorMessages) {
        LOG.debug(loggedMessage, (Throwable)e);
        if (e.isCustomError() && errorMessages.length == 1) {
            return Response.status((Response.Status)Response.Status.INTERNAL_SERVER_ERROR).header("Content-Type", (Object)"application/json").entity((Object)this.toJsonArray(e.getStatus() == CalendarException.StatusError.JQL_WRONG.getStatusNum() ? "error-jql-wrong" : "html-error-type", errorMessages[0]).toString()).build();
        }
        return Response.status((Response.Status)Response.Status.INTERNAL_SERVER_ERROR).header("Content-Type", (Object)"application/json").entity((Object)this.toJsonArray(errorMessages).toString()).build();
    }

    private Response getResponseError(Exception e, String message) {
        Response response = Response.status((Response.Status)Response.Status.INTERNAL_SERVER_ERROR).header("Content-Type", (Object)"application/json").entity((Object)this.toJsonArray(StringUtils.isBlank(e.getMessage()) ? ExceptionUtils.getStackTrace(e) : e.getMessage()).toString()).build();
        LOG.error(message, (Throwable)e);
        return response;
    }

    protected JSONArray toJsonArray(String ... errorMessages) {
        return JSONUtil.toJsonArray(errorMessages);
    }

    protected String getText(String s) {
        return this.getI18nBean().getText(s);
    }

    protected String getText(String i18nKey, List substitutions) {
        return this.getI18nBean().getText(i18nKey, substitutions);
    }

    private I18NBean getI18nBean() {
        return this.i18NBeanFactory.getI18NBean(this.getUserLocale());
    }

    private Locale getUserLocale() {
        return this.localeManager.getLocale((User)AuthenticatedUserThreadLocal.get());
    }
}


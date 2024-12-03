/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.applinks.api.ApplicationId
 *  com.atlassian.applinks.api.ApplicationLinkRequestFactory
 *  com.atlassian.applinks.api.CredentialsRequiredException
 *  com.atlassian.applinks.api.ReadOnlyApplicationLink
 *  com.atlassian.confluence.util.i18n.I18NBean
 *  com.atlassian.confluence.util.i18n.I18NBeanFactory
 *  com.atlassian.sal.api.net.ResponseException
 *  com.atlassian.sal.api.net.ResponseStatusException
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.plugins.metadata.jira.helper;

import com.atlassian.applinks.api.ApplicationId;
import com.atlassian.applinks.api.ApplicationLinkRequestFactory;
import com.atlassian.applinks.api.CredentialsRequiredException;
import com.atlassian.applinks.api.ReadOnlyApplicationLink;
import com.atlassian.confluence.plugins.metadata.jira.exception.JiraMetadataException;
import com.atlassian.confluence.plugins.metadata.jira.model.JiraMetadataError;
import com.atlassian.confluence.plugins.metadata.jira.model.JiraUnauthorisedAppLink;
import com.atlassian.confluence.util.i18n.I18NBean;
import com.atlassian.confluence.util.i18n.I18NBeanFactory;
import com.atlassian.sal.api.net.ResponseException;
import com.atlassian.sal.api.net.ResponseStatusException;
import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.net.URI;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.RejectedExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JiraMetadataErrorHelper {
    private final Map<Status, JiraMetadataError> errors = new HashMap<Status, JiraMetadataError>();
    private final Map<ApplicationId, JiraUnauthorisedAppLink> unauthorisedAppLinks = new HashMap<ApplicationId, JiraUnauthorisedAppLink>();
    private final I18NBean i18NBean;
    private final Set<String> loggedMessages = new HashSet<String>();
    private static final Logger log = LoggerFactory.getLogger(JiraMetadataErrorHelper.class);

    public JiraMetadataErrorHelper(I18NBeanFactory i18NBeanFactory) {
        this.i18NBean = i18NBeanFactory.getI18NBean();
    }

    public void handleException(Throwable e) {
        this.handleException(e, null, null);
    }

    public void handleException(Throwable e, ReadOnlyApplicationLink applink) {
        this.handleException(e, applink, null);
    }

    public void handleException(Throwable e, ReadOnlyApplicationLink applink, ApplicationLinkRequestFactory requestFactory) {
        if (e instanceof CredentialsRequiredException) {
            this.handleUnauthorisedUser(e, applink, requestFactory);
        } else if (e instanceof ExecutionException) {
            this.handleException(e.getCause(), applink, requestFactory);
        } else if (e instanceof RejectedExecutionException) {
            this.logError(Status.EXECUTION_INTERRUPTED, e, applink);
        } else if (e instanceof InterruptedException) {
            this.logError(Status.EXECUTION_INTERRUPTED, e, applink);
            Thread.currentThread().interrupt();
        } else if (e instanceof ResponseException) {
            if (e instanceof ResponseStatusException) {
                this.handleResponseStatusException((ResponseStatusException)e, applink, requestFactory);
            } else {
                this.handleException(e.getCause(), applink, requestFactory);
            }
        } else if (e instanceof ConnectException || e instanceof SocketTimeoutException || e instanceof CancellationException) {
            this.logError(Status.SERVER_UNREACHABLE, e, applink);
        } else if (e instanceof JiraMetadataException) {
            this.logError(((JiraMetadataException)e).getStatus(), e, applink);
        } else {
            this.logError(Status.OTHER, e, applink);
        }
    }

    public void handleUnauthorisedUser(Throwable e, ReadOnlyApplicationLink applink, ApplicationLinkRequestFactory requestFactory) {
        URI authURI;
        if (requestFactory != null && (authURI = requestFactory.getAuthorisationURI()) != null) {
            this.unauthorisedAppLinks.put(applink.getId(), new JiraUnauthorisedAppLink(applink, requestFactory.getAuthorisationURI().toString()));
            return;
        }
        this.logError(Status.APPLINK_MISCONFIGURED, e, applink);
    }

    private void handleResponseStatusException(ResponseStatusException e, ReadOnlyApplicationLink applink, ApplicationLinkRequestFactory requestFactory) {
        if (e.getResponse().getStatusCode() == 401 || e.getResponse().getStatusCode() == 403) {
            this.handleUnauthorisedUser((Throwable)e, applink, requestFactory);
        } else if (e.getResponse().getStatusCode() == 404) {
            this.logError(Status.SERVER_UNREACHABLE, (Throwable)e, applink);
        } else {
            this.logError(Status.OTHER, (Throwable)e, applink);
        }
    }

    private void logError(Status status, Throwable e, ReadOnlyApplicationLink applink) {
        if (status == Status.SERVER_UNREACHABLE && this.errors.containsKey((Object)Status.EXECUTION_INTERRUPTED)) {
            return;
        }
        if (!this.errors.containsKey((Object)status)) {
            this.errors.put(status, new JiraMetadataError(this.getFriendlyMessage(status)));
        }
        if (applink != null) {
            this.errors.get((Object)status).addErrorApplink(applink.getName());
        }
        this.printError(status, e, applink);
    }

    private String getFriendlyMessage(Status status) {
        return this.i18NBean.getText(status.key);
    }

    private String getLogMessage(Status status, ReadOnlyApplicationLink applink) {
        if (applink == null) {
            return this.getFriendlyMessage(status);
        }
        switch (status) {
            case APPLINK_MISCONFIGURED: {
                return "Application link '" + applink.getName() + "' appears to be misconfigured";
            }
            case SERVER_UNREACHABLE: {
                return "The server '" + applink.getName() + "' cannot be reached";
            }
            case RESPONSE_UNPARSABLE: {
                return "Response from server '" + applink.getName() + "' could not be parsed";
            }
        }
        return this.getFriendlyMessage(status);
    }

    private void printError(Status status, Throwable e, ReadOnlyApplicationLink applink) {
        String message = this.getLogMessage(status, applink);
        if (!this.loggedMessages.contains(message)) {
            if (status == Status.SERVER_UNREACHABLE || status == Status.APPLINK_MISCONFIGURED) {
                log.warn(message, e);
            } else {
                log.error(message, e);
            }
            this.loggedMessages.add(message);
        }
    }

    public Map<Status, JiraMetadataError> getErrors() {
        return this.errors;
    }

    public Map<ApplicationId, JiraUnauthorisedAppLink> getUnauthorisedAppLinks() {
        return this.unauthorisedAppLinks;
    }

    public static enum Status {
        APPLINK_MISCONFIGURED("content.metadata.jira.error.applink.misconfigured"),
        SERVER_UNREACHABLE("content.metadata.jira.error.server.unreachable"),
        EXECUTION_INTERRUPTED("content.metadata.jira.error.execution.interrupted"),
        RESPONSE_UNPARSABLE("content.metadata.jira.error.response.unparsable"),
        OTHER("content.metadata.jira.error.other");

        final String key;

        private Status(String key) {
            this.key = key;
        }
    }
}


/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.ws.rs.core.Response
 *  javax.ws.rs.ext.ExceptionMapper
 *  javax.ws.rs.ext.Provider
 */
package com.atlassian.internal.integration.jira.rest;

import com.atlassian.integration.jira.ApplicationNameAwareJiraException;
import com.atlassian.integration.jira.JiraAuthenticationRequiredException;
import com.atlassian.integration.jira.JiraCommunicationException;
import com.atlassian.integration.jira.JiraMultipleAuthenticationException;
import com.atlassian.integration.jira.JiraMultipleCommunicationException;
import com.atlassian.integration.jira.JiraValidationException;
import com.atlassian.integration.jira.JiraVersionIncompatibleException;
import com.atlassian.internal.integration.jira.rest.RestErrorMessage;
import com.atlassian.internal.integration.jira.rest.RestErrors;
import com.atlassian.internal.integration.jira.rest.RestUtils;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class RestExceptionMapper
implements ExceptionMapper<Exception> {
    private static final String APPLICATION_NAME = "applicationName";
    private static final String APPLICATION_URL = "applicationUrl";
    private static final String AUTHENTICATION_URI = "authenticationUri";

    public Response toResponse(Exception exception) {
        if (exception instanceof JiraValidationException) {
            JiraValidationException jiraValidationException = (JiraValidationException)exception;
            return RestUtils.serverError(new RestErrors(jiraValidationException.getErrors())).type("application/json;charset=UTF-8").build();
        }
        if (exception instanceof JiraVersionIncompatibleException) {
            RestErrorMessage errorMessage = new RestErrorMessage(exception);
            return RestUtils.serverError(errorMessage).type("application/json;charset=UTF-8").build();
        }
        if (exception instanceof JiraCommunicationException) {
            JiraCommunicationException jiraCommunicationException = (JiraCommunicationException)exception;
            RestErrorMessage errorMessage = this.buildRestErrorMessageForJiraCommunicationException(jiraCommunicationException);
            return RestUtils.serverError(new RestErrors(errorMessage)).type("application/json;charset=UTF-8").build();
        }
        if (exception instanceof JiraAuthenticationRequiredException) {
            JiraAuthenticationRequiredException jiraAuthenticationRequiredException = (JiraAuthenticationRequiredException)exception;
            RestErrorMessage errorMessage = this.buildRestErrorMessageForJiraAuthenticationRequiredException(jiraAuthenticationRequiredException);
            return RestUtils.ok(new RestErrors(errorMessage)).type("application/json;charset=UTF-8").build();
        }
        if (exception instanceof JiraMultipleCommunicationException) {
            JiraMultipleCommunicationException multiException = (JiraMultipleCommunicationException)exception;
            RestErrors errors = new RestErrors();
            for (JiraCommunicationException jiraCommunicationException : multiException.getExceptions()) {
                errors.addError(this.buildRestErrorMessageForJiraCommunicationException(jiraCommunicationException));
            }
            return RestUtils.serverError(errors).type("application/json;charset=UTF-8").build();
        }
        if (exception instanceof JiraMultipleAuthenticationException) {
            RestErrors errors = new RestErrors();
            JiraMultipleAuthenticationException multiException = (JiraMultipleAuthenticationException)exception;
            for (JiraAuthenticationRequiredException jiraAuthenticationRequiredException : multiException) {
                errors.addError(this.buildRestErrorMessageForJiraAuthenticationRequiredException(jiraAuthenticationRequiredException));
            }
            return RestUtils.ok(errors).type("application/json;charset=UTF-8").build();
        }
        if ("com.atlassian.confluence.api.service.exceptions.ReadOnlyException".equals(exception.getClass().getName())) {
            return RestUtils.readOnlyError(exception).build();
        }
        return RestUtils.serverError(exception.getMessage()).type("application/json;charset=UTF-8").build();
    }

    private RestErrorMessage buildRestErrorMessageForJiraCommunicationException(JiraCommunicationException exception) {
        RestErrorMessage errorMessage = this.buildCommonErrorMessage(exception);
        errorMessage.put(APPLICATION_URL, exception.getApplicationUrl().toString());
        return errorMessage;
    }

    private RestErrorMessage buildRestErrorMessageForJiraAuthenticationRequiredException(JiraAuthenticationRequiredException exception) {
        RestErrorMessage errorMessage = this.buildCommonErrorMessage(exception);
        errorMessage.put(AUTHENTICATION_URI, exception.getAuthenticationUri().toString());
        return errorMessage;
    }

    private RestErrorMessage buildCommonErrorMessage(ApplicationNameAwareJiraException exception) {
        RestErrorMessage errorMessage = new RestErrorMessage(exception);
        errorMessage.put(APPLICATION_NAME, exception.getApplicationName());
        return errorMessage;
    }
}


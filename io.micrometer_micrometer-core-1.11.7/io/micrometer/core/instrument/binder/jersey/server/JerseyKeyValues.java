/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.micrometer.common.KeyValue
 *  io.micrometer.common.util.StringUtils
 *  org.glassfish.jersey.server.ContainerRequest
 *  org.glassfish.jersey.server.ContainerResponse
 *  org.glassfish.jersey.server.monitoring.RequestEvent
 */
package io.micrometer.core.instrument.binder.jersey.server;

import io.micrometer.common.KeyValue;
import io.micrometer.common.util.StringUtils;
import io.micrometer.core.instrument.binder.http.Outcome;
import io.micrometer.core.instrument.binder.jersey.server.JerseyObservationDocumentation;
import io.micrometer.core.instrument.binder.jersey.server.JerseyTags;
import org.glassfish.jersey.server.ContainerRequest;
import org.glassfish.jersey.server.ContainerResponse;
import org.glassfish.jersey.server.monitoring.RequestEvent;

class JerseyKeyValues {
    private static final KeyValue URI_NOT_FOUND = JerseyObservationDocumentation.JerseyLegacyLowCardinalityTags.URI.withValue("NOT_FOUND");
    private static final KeyValue URI_REDIRECTION = JerseyObservationDocumentation.JerseyLegacyLowCardinalityTags.URI.withValue("REDIRECTION");
    private static final KeyValue URI_ROOT = JerseyObservationDocumentation.JerseyLegacyLowCardinalityTags.URI.withValue("root");
    private static final KeyValue EXCEPTION_NONE = JerseyObservationDocumentation.JerseyLegacyLowCardinalityTags.EXCEPTION.withValue("None");
    private static final KeyValue STATUS_SERVER_ERROR = JerseyObservationDocumentation.JerseyLegacyLowCardinalityTags.STATUS.withValue("500");
    private static final KeyValue METHOD_UNKNOWN = JerseyObservationDocumentation.JerseyLegacyLowCardinalityTags.METHOD.withValue("UNKNOWN");

    private JerseyKeyValues() {
    }

    static KeyValue method(ContainerRequest request) {
        return request != null ? JerseyObservationDocumentation.JerseyLegacyLowCardinalityTags.METHOD.withValue(request.getMethod()) : METHOD_UNKNOWN;
    }

    static KeyValue status(ContainerResponse response) {
        return response != null ? JerseyObservationDocumentation.JerseyLegacyLowCardinalityTags.STATUS.withValue(Integer.toString(response.getStatus())) : STATUS_SERVER_ERROR;
    }

    static KeyValue uri(RequestEvent event) {
        String matchingPattern;
        ContainerResponse response = event.getContainerResponse();
        if (response != null) {
            int status = response.getStatus();
            if (JerseyTags.isRedirection(status) && event.getUriInfo().getMatchedResourceMethod() == null) {
                return URI_REDIRECTION;
            }
            if (status == 404 && event.getUriInfo().getMatchedResourceMethod() == null) {
                return URI_NOT_FOUND;
            }
        }
        if ((matchingPattern = JerseyTags.getMatchingPattern(event)).equals("/")) {
            return URI_ROOT;
        }
        return JerseyObservationDocumentation.JerseyLegacyLowCardinalityTags.URI.withValue(matchingPattern);
    }

    static KeyValue exception(RequestEvent event) {
        String simpleName;
        int status;
        Throwable exception = event.getException();
        if (exception == null) {
            return EXCEPTION_NONE;
        }
        ContainerResponse response = event.getContainerResponse();
        if (response != null && ((status = response.getStatus()) == 404 || JerseyTags.isRedirection(status))) {
            return EXCEPTION_NONE;
        }
        if (exception.getCause() != null) {
            exception = exception.getCause();
        }
        return JerseyObservationDocumentation.JerseyLegacyLowCardinalityTags.EXCEPTION.withValue(StringUtils.isNotEmpty((String)(simpleName = exception.getClass().getSimpleName())) ? simpleName : exception.getClass().getName());
    }

    static KeyValue outcome(ContainerResponse response) {
        if (response != null) {
            Outcome outcome = Outcome.forStatus(response.getStatus());
            return JerseyObservationDocumentation.JerseyLegacyLowCardinalityTags.OUTCOME.withValue(outcome.name());
        }
        return JerseyObservationDocumentation.JerseyLegacyLowCardinalityTags.OUTCOME.withValue(Outcome.SERVER_ERROR.name());
    }
}

